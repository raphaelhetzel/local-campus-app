package de.tum.localcampusapp.service;

import android.util.Log;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.NetworkLayerPostRepository;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.serializer.ScampiMessageTypes;
import de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.serializer.ScampiVoteSerializer;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST_EXTENSION;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_VOTE;

public class TopicHandler implements MessageReceivedCallback {

    public static final String TAG = TopicHandler.class.getSimpleName();

    private final NetworkLayerPostRepository postRepository;
    private final ScampiPostSerializer scampiPostSerializer;
    private final ScampiVoteSerializer scampiVoteSerializer;
    private final ScampiPostExtensionSerializer scampiPostExtensionSerializer;

    public TopicHandler() {
        this(RepositoryLocator.getNetworkLayerPostRepository(),
                new ScampiPostSerializer(),
                new ScampiVoteSerializer(),
                new ScampiPostExtensionSerializer());
    }


    public TopicHandler(NetworkLayerPostRepository postRepository, ScampiPostSerializer scampiPostSerializer, ScampiVoteSerializer scampiVoteSerializer, ScampiPostExtensionSerializer scampiPostExtensionSerializer) {
        this.postRepository = postRepository;
        this.scampiPostSerializer = scampiPostSerializer;
        this.scampiVoteSerializer = scampiVoteSerializer;
        this.scampiPostExtensionSerializer = scampiPostExtensionSerializer;
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String s) {
        try {
            switch (ScampiMessageTypes.messageTypeOf(scampiMessage)) {
                case MESSAGE_TYPE_POST:
                    Post post = scampiPostSerializer.postFromMessage(scampiMessage);
                    postRepository.insertPost(post);
                    break;
                case MESSAGE_TYPE_VOTE:
                    Vote vote = scampiVoteSerializer.messageToVote(scampiMessage);
                    postRepository.insertVote(vote);
                    break;
                case MESSAGE_TYPE_POST_EXTENSION:
                    PostExtension postExtension = scampiPostExtensionSerializer.messageToPostExtension(scampiMessage);
                    postRepository.insertPostExtension(postExtension);
                    break;
                default:
                    Log.d(TAG, "Ignored unknown Message Type");
            }
        } catch (MissingFieldsException e) {
            Log.d(TAG, "Igored Message with Missing Fields");
        } catch (MissingRelatedDataException e) {
            Log.d(TAG, "Ignored message as the device does not know about required related Information (e.g. the Topic)");
        } catch (DatabaseException | WrongParserException e) {
            // There is something wrong in the Application that needs to be fixed
            e.printStackTrace();
        } finally {
            scampiMessage.close();
        }
    }

}
