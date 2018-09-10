package de.tum.localcampusapp.service;

import android.content.Context;
import android.util.Log;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingTopicException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.TopicRepository;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiPostSerializer.TOPIC_FIELD;

public class TopicHandler implements MessageReceivedCallback {

    public static final String TAG = DiscoveryHandler.class.getSimpleName();

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    public TopicHandler(TopicRepository topicRepository, PostRepository postRepository) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String s) {
        try {
            Post newPost = ScampiPostSerializer.postFromMessage(scampiMessage, this.topicRepository);
            Post existingPost = postRepository.getFinalPostByUUID(scampiMessage.getAppTag());

            if(existingPost != null) {
                Log.d(TAG, "Post exists");
                if(existingPost.equalsWitoutId(newPost)) {
                    Log.d(TAG, "Post is already up to date, ignoring");
                }
                else {
                    Log.d(TAG, "Post needs to be merged");
                    // TODO: mergePosts, add new post if merged post is newer than post from the network
                    // should be written in a way that allows the system to self stabelize -> no hard deletes
                }
            } else {
                postRepository.insertPost(newPost);
            }
        } catch (MissingFieldsException e) {
            Log.d(TAG, "Invalid Post message ignored");
        } catch (MissingTopicException e) {
            Log.d(TAG, "Message ignored as the device does not know about the topic");
        } catch (DatabaseException e) {
            e.printStackTrace();
        } finally {
            scampiMessage.close();
        }
    }

}
