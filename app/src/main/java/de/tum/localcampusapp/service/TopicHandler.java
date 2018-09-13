package de.tum.localcampusapp.service;

import android.util.Log;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.serializer.ScampiVoteSerializer;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class TopicHandler implements MessageReceivedCallback {

    public static final String TAG = TopicHandler.class.getSimpleName();

    private final PostRepository postRepository;
    private final ScampiPostSerializer scampiPostSerializer;
    private final ScampiVoteSerializer scampiVoteSerializer;

    public TopicHandler() {
        this(RepositoryLocator.getPostRepository(),
                RepositoryLocator.getScampiPostSerializer(),
                new ScampiVoteSerializer());
    }


    public TopicHandler(PostRepository postRepository, ScampiPostSerializer scampiPostSerializer, ScampiVoteSerializer scampiVoteSerializer) {
        this.postRepository = postRepository;
        this.scampiPostSerializer = scampiPostSerializer;
        this.scampiVoteSerializer = scampiVoteSerializer;
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String s) {
        try {
            if (ScampiPostSerializer.messageIsPost(scampiMessage)) {
                Post newPost = scampiPostSerializer.postFromMessage(scampiMessage);
                Post existingPost = postRepository.getFinalPostByUUID(scampiMessage.getAppTag());

                if (existingPost == null) {
                    postRepository.insertPost(newPost);
                }
            } else if (ScampiVoteSerializer.messageIsVote(scampiMessage)) {
                Vote vote = scampiVoteSerializer.messageToVote(scampiMessage);
                postRepository.insertVote(vote);
            }

        } catch (MissingFieldsException e) {
            Log.d(TAG, "Invalid Post message ignored");
        } catch (WrongParserException e) {
            Log.d(TAG, "Called the wrong parser!");
        } catch (MissingRelatedDataException e) {
            Log.d(TAG, "Message ignored as the device does not know about the topic");
        } catch (DatabaseException e) {
            e.printStackTrace();
        } finally {
            scampiMessage.close();
        }
    }

}
