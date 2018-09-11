package de.tum.localcampusapp.service;

import android.content.Context;
import android.util.Log;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingTopicException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.TopicRepository;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiPostSerializer.TOPIC_FIELD;

public class TopicHandler implements MessageReceivedCallback {

    public static final String TAG = DiscoveryHandler.class.getSimpleName();

    private final PostRepository postRepository;
    private final ScampiPostSerializer scampiPostSerializer;

    // TODO add constructor to get variables from the repository


    public TopicHandler(PostRepository postRepository, ScampiPostSerializer scampiPostSerializer) {
        this.postRepository = postRepository;
        this.scampiPostSerializer = scampiPostSerializer;
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String s) {
        if (ScampiPostSerializer.messageIsPost(scampiMessage)) {
            try {

                Post newPost = scampiPostSerializer.postFromMessage(scampiMessage);
                Post existingPost = postRepository.getFinalPostByUUID(scampiMessage.getAppTag());

                if (existingPost == null) {
                    postRepository.insertPost(newPost);
                }

            } catch (MissingFieldsException e) {
                Log.d(TAG, "Invalid Post message ignored");
            } catch (WrongParserException e) {
                Log.d(TAG, "Called the wrong parser!");
            } catch (MissingTopicException e) {
                Log.d(TAG, "Message ignored as the device does not know about the topic");
            } catch (DatabaseException e) {
                e.printStackTrace();
            } finally {
                scampiMessage.close();
            }
        }
    }

}
