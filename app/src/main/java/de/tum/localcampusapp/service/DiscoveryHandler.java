package de.tum.localcampusapp.service;

import android.arch.lifecycle.LifecycleOwner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.repository.TopicRepository;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class DiscoveryHandler implements MessageReceivedCallback {

    public static final String TAG = DiscoveryHandler.class.getSimpleName();

    private final TopicRepository topicRepository;
    private final AppLib appLib;
    private final Set<String> subscriptions;

    public DiscoveryHandler(AppLib appLib, LifecycleOwner lifecycleOwner) {
        this(appLib, lifecycleOwner, RepositoryLocator.getTopicRepository());
    }


    public DiscoveryHandler(AppLib appLib, LifecycleOwner lifecycleOwner, TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
        this.appLib = appLib;
        this.subscriptions = new HashSet<>();

        topicRepository.getTopicsForCurrentLocation().observe(lifecycleOwner, topics -> locationChanged(topics));
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String service) {
        if (scampiMessage.hasString("topicName") && scampiMessage.hasString("deviceId")) {
            String topicName = scampiMessage.getString("topicName");
            String locationId = scampiMessage.getString("deviceId");
            topicRepository.insertTopic(topicName, locationId);
        }
        scampiMessage.close();
    }

    private void locationChanged(List<Topic> currentLocationTopics) {
            Set<String> oldSubscriptions = new HashSet<>(subscriptions);
            Set<String> newSubscriptions = currentLocationTopics.stream().map(topic -> topic.getTopicName()).collect(Collectors.toSet());

            oldSubscriptions.removeAll(newSubscriptions);
            newSubscriptions.removeAll(oldSubscriptions);

            for(String topic : oldSubscriptions) {
                unsubscribeFromTopic(topic);
            }

            for(String topic : newSubscriptions) {
                subscribeToTopic(topic);
            }
    }

    private void subscribeToTopic(String topicName) {
        try {
            synchronized (subscriptions) {
                if(!this.subscriptions.contains(topicName)) {
                    appLib.subscribe(topicName, new TopicHandler());
                    this.subscriptions.add(topicName);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void unsubscribeFromTopic(String topicName) {
        try {
            synchronized (subscriptions) {
                if(this.subscriptions.contains(topicName)) {
                    appLib.unsubscribe(topicName);
                    this.subscriptions.remove(topicName);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
