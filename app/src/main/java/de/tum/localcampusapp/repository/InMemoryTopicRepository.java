package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

// This is just a Mock and should not be used in Production!

public class InMemoryTopicRepository implements TopicRepository {

    private final LocationRepository locationRepository;
    private final Handler handler;
    private MutableLiveData<List<TopicLocationObject>> storage;

    private volatile long topicId = 1L;


    private static class TopicLocationObject {
        public Topic topic;
        public Set<String> locations;

        public TopicLocationObject(Topic topic, Set<String> locations) {
            this.topic = topic;
            this.locations = locations;
        }
    }

    // Should be started from main thread (onCreate() of an activity/service)
    public InMemoryTopicRepository(LocationRepository locationRepository) {
        this(new Handler(), locationRepository);
    }


    public InMemoryTopicRepository(Handler mainThreadHandler, LocationRepository locationRepository) {
        this.handler = mainThreadHandler;
        this.storage = new MutableLiveData<>();
        this.storage.setValue(new ArrayList<>());
        this.locationRepository = locationRepository;
    }

    @Override
    public LiveData<List<Topic>> getTopics() {
        return Transformations.map(storage, topicLocationObjects -> {
            return topicLocationObjects.stream()
                    .map(topicLocationObject -> topicLocationObject.topic)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public LiveData<List<Topic>> getTopicsForCurrentLocation() {
        MediatorLiveData<List<Topic>> liveData = new MediatorLiveData<>();
        liveData.addSource(storage, topicLocationObjects -> {
            List<Topic> output = topicLocationObjects.stream()
                    .filter(topicLocationObject -> topicLocationObject.locations.contains(locationRepository.getFinalCurrentLocation()))
                    .map(topicLocationObject -> topicLocationObject.topic)
                    .collect(Collectors.toList());
            liveData.setValue(output);
        });
        liveData.addSource(locationRepository.getCurrentLocation(), currentLocation -> {
            List<Topic> output = storage.getValue().stream()
                    .filter(topicLocationObject -> topicLocationObject.locations.contains(currentLocation))
                    .map(topicLocationObject -> topicLocationObject.topic)
                    .collect(Collectors.toList());
            liveData.setValue(output);
        });
        return liveData;
    }

    @Override
    public LiveData<Topic> getTopic(long id) {
        return Transformations.map(storage, topicLocationObjects -> {
            return topicLocationObjects.stream()
                    .map(topicLocationObject -> topicLocationObject.topic)
                    .filter(p -> p.getId() == id)
                    .reduce(null, (concat, topic) -> topic);
        });
    }

    @Override
    public LiveData<Topic> getTopicByName(String topicName) {
        return Transformations.map(storage, topicLocationObjects -> {
            return topicLocationObjects.stream()
                    .map(topicLocationObject -> topicLocationObject.topic)
                    .filter(p -> p.getTopicName().equals(topicName))
                    .reduce(null, (concat, topic) -> topic);
        });
    }

    @Override
    public List<Topic> getFinalTopicsForCurrentLocation() {
        return storage.getValue().stream()
                .filter(topicLocationObject -> topicLocationObject.locations.contains(locationRepository.getFinalCurrentLocation()))
                .map(topicLocationObject -> topicLocationObject.topic)
                .collect(Collectors.toList());
    }

    @Override
    public Topic getFinalTopicByName(String topicName) {
        return storage.getValue().stream()
                .map(topicLocationObject -> topicLocationObject.topic)
                .filter(topic -> topic.getTopicName().equals(topicName))
                .reduce(null, (concat, topic) -> topic);
    }

    @Override
    public Topic getFinalTopic(long id) {
        return storage.getValue().stream()
                .map(topicLocationObject -> topicLocationObject.topic)
                .filter(topic -> topic.getId() == id)
                .reduce(null, (concat, topic) -> topic);
    }


    @Override
    public void insertTopic(String topicName, String locationId) {
        handler.post(new InsertTask(topicName, locationId));
    }

    // Helper to update LiveData from main Thread
    private class InsertTask implements Runnable {
        String topicName;
        String locationId;

        public InsertTask(String topicName, String locationId) {
            this.topicName = topicName;
            this.locationId = locationId;
        }

        @Override
        public void run() {
            synchronized (storage) {
                List<TopicLocationObject> tempTopicLocationObjects = new ArrayList<>(storage.getValue());
                Optional<TopicLocationObject> existingTopicLocationObject = tempTopicLocationObjects.stream().filter(t -> t.topic.getTopicName().equals(topicName)).findFirst();
                if(existingTopicLocationObject.isPresent()) {
                    tempTopicLocationObjects.remove(existingTopicLocationObject.get());
                    existingTopicLocationObject.get().locations.add(locationId);
                    tempTopicLocationObjects.add(existingTopicLocationObject.get());
                } else {
                    TopicLocationObject newTopicLocationObject = new TopicLocationObject(new Topic(topicId++, topicName), new HashSet<>());
                    newTopicLocationObject.locations.add(locationId);
                    tempTopicLocationObjects.add(newTopicLocationObject);
                }
                storage.setValue(tempTopicLocationObjects);
            }
        }
    }
}
