package de.tum.localcampusapp.service;

import android.content.Context;
import android.util.Log;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RepositoryLocator;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class DiscoveryHandler implements MessageReceivedCallback {

    public static final String TAG = DiscoveryHandler.class.getSimpleName();

    private final Context applicationContext;

    public DiscoveryHandler(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String service) {
        Log.d(TAG, "Message received on Service: " + service);
        if (scampiMessage.hasString("topicName") && scampiMessage.hasString("deviceId")) {
            Log.d(TAG, "Received Topic: " + scampiMessage.getString("topicName") + " from location " + scampiMessage.getString("deviceId"));
            Topic topic = new Topic();
            topic.setTopicName(scampiMessage.getString("topicName"));
            try {
                // TODO: prevent duplicated in the repository
                RepositoryLocator.getTopicRepository(applicationContext).insertTopic(topic);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
        scampiMessage.close();
    }
}
