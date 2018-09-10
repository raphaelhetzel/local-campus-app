package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

public class InMemoryTopicRepository implements TopicRepository {
    private final Handler handler;
    private MutableLiveData<List<Topic>> topics;


    // Should be started from main thread (onCreate() of an activity/service)
    public InMemoryTopicRepository() {
        this(new Handler());
    }


    public InMemoryTopicRepository(Handler mainThreadHandler) {
        this.handler = mainThreadHandler;
        this.topics = new MutableLiveData<>();
        this.topics.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<List<Topic>> getTopics() throws DatabaseException {
        return topics;
    }

    @Override
    public LiveData<Topic> getTopic(long id) throws DatabaseException {
        return Transformations.map(topics, topics -> {
            List<Topic> items = topics.stream().filter(p -> p.getId() == id).collect(Collectors.toList());
            if (items.size() == 1) {
                return items.get(0);
            }
            return null;
        });
    }

    @Override
    public LiveData<Topic> getTopicByName(String topicName) throws DatabaseException {
        return Transformations.map(topics, topics -> {
            List<Topic> items = topics.stream().filter(p -> p.getTopicName().equals(topicName)).collect(Collectors.toList());
            if (items.size() == 1) {
                return items.get(0);
            }
            return null;
        });
    }

    @Override
    public Topic getFinalTopicByName(String topicName) throws DatabaseException {
        List<Topic> all_topics = new ArrayList<>(topics.getValue());
        return all_topics.stream().filter(p -> p.getTopicName().equals(topicName)).reduce(null, (concat, topic) -> topic);
    }

    @Override
    public Topic getFinalTopic(long id) throws DatabaseException {
        List<Topic> all_topics = new ArrayList<>(topics.getValue());
        return all_topics.stream().filter(p -> p.getId() == id).reduce(null, (concat, topic) -> topic);
    }


    @Override
    public void insertTopic(Topic topic) throws DatabaseException {
        handler.post(new InsertTask(topic));
    }

    // Helper to update LiveData from main Thread
    private class InsertTask implements Runnable {
        Topic topic;

        public InsertTask(Topic topic) {
            this.topic = topic;
        }

        @Override
        public void run() {
            synchronized (InMemoryTopicRepository.this) {
                List<Topic> temp = new ArrayList<>(topics.getValue());
                if (temp.stream().anyMatch(t -> t.getTopicName().equals(topic.getTopicName()))) {
                    return;
                }
                temp.add(topic);
                topics.setValue(temp);
            }
        }
    }
}
