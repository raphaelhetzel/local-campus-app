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
import de.tum.localcampusapp.serializer.ScampiVoteDeserializer;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class TopicHandler implements MessageReceivedCallback {

    public static final String TAG = TopicHandler.class.getSimpleName();

    private final PostRepository postRepository;
    private final ScampiPostSerializer scampiPostSerializer;
    private final ScampiVoteDeserializer scampiVoteDeserializer;

    public TopicHandler() {
        this(RepositoryLocator.getPostRepository(),
                RepositoryLocator.getScampiPostSerializer(),
                RepositoryLocator.getScampiVoteDeserializer());
    }


    public TopicHandler(PostRepository postRepository, ScampiPostSerializer scampiPostSerializer, ScampiVoteDeserializer scampiVoteDeserializer) {
        this.postRepository = postRepository;
        this.scampiPostSerializer = scampiPostSerializer;
        this.scampiVoteDeserializer = scampiVoteDeserializer;
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
            } else if(ScampiVoteDeserializer.messageIsVote(scampiMessage)){
                Vote vote = scampiVoteDeserializer.messageToVote(scampiMessage);
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
