package de.tum.localcampusapp.service;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.LocationRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.repository.TopicRepository;
import de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.serializer.ScampiVoteSerializer;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.MessageReceivedCallback;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static java.lang.Thread.sleep;

public class DiscoveryHandler implements MessageReceivedCallback {

    public static final String TAG = DiscoveryHandler.class.getSimpleName();

    private final TopicRepository topicRepository;
    private final AppLib appLib;
    private final Set<String> subscriptions;

    private String currentLocationId;

    private final Object lock = new Object();

    public DiscoveryHandler(LifecycleOwner serviceLifecylce, AppLib appLib) {
        this(serviceLifecylce, appLib, RepositoryLocator.getTopicRepository(), RepositoryLocator.getLocationRepository());
    }

    public DiscoveryHandler(LifecycleOwner serviceLifecycle, AppLib appLib, TopicRepository topicRepository, LocationRepository locationRepository) {
        this.topicRepository = topicRepository;
        this.appLib = appLib;
        this.subscriptions = new HashSet<>();
        locationRepository.getCurrentLocation().observeForever(locationId -> {
            synchronized (lock) {
                System.out.println("called");
                this.currentLocationId = locationId;
                locationChanged(locationId);
            }
        });
    }

    @Override
    public void messageReceived(SCAMPIMessage scampiMessage, String service) {
        if (scampiMessage.hasString("topicName") && scampiMessage.hasString("deviceId")) {
            Topic topic = new Topic();
            topic.setTopicName(scampiMessage.getString("topicName"));
            try {
                topicRepository.insertTopic(topic);
                // insert locationToTopicMapping
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                String topicLocationId = scampiMessage.getString("deviceId");
                System.out.println(currentLocationId);
                if(topicLocationId.equals(currentLocationId)) {
                    subscribeToTopic(topic.getTopicName());
                }
            }
        }
        scampiMessage.close();
    }

    private void locationChanged(String locationId) {
        // query location to topic mappings
        // Change subscriptions to the topics available at the new location
    }

    private void subscribeToTopic(String topicName) {
        try {
            synchronized (subscriptions) {
                if(!this.subscriptions.contains(topicName)) {
                    // TODO: change back to the short constructor
                    appLib.subscribe(topicName, new TopicHandler(null,
                            new ScampiPostSerializer(),
                            new ScampiVoteSerializer(),
                            new ScampiPostExtensionSerializer()));
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
                if(!this.subscriptions.contains(topicName)) {
                    appLib.unsubscribe(topicName);
                    this.subscriptions.remove(topicName);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
