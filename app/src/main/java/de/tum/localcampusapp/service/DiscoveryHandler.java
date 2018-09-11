package de.tum.localcampusapp.service;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.repository.TopicRepository;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class DiscoveryHandler implements MessageReceivedCallback {

    public static final String TAG = DiscoveryHandler.class.getSimpleName();

    private final TopicRepository topicRepository;
    private final AppLib appLib;

    public DiscoveryHandler(AppLib appLib) {
        this(RepositoryLocator.getTopicRepository(), appLib);
    }

    public DiscoveryHandler(TopicRepository topicRepository, AppLib appLib) {
        this.topicRepository = topicRepository;
        this.appLib = appLib;
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String service) {
        if (scampiMessage.hasString("topicName") && scampiMessage.hasString("deviceId")) {
            Topic topic = new Topic();
            topic.setTopicName(scampiMessage.getString("topicName"));
            try {
                topicRepository.insertTopic(topic);
                subscribeToTopic(topic.getTopicName());
            } catch (DatabaseException e) {
                e.printStackTrace();
            }

        }
        scampiMessage.close();
    }

    private void subscribeToTopic(String topicName) {
        try {
            // TODO: replace with constructor using the repository
            appLib.subscribe(topicName, new TopicHandler());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
