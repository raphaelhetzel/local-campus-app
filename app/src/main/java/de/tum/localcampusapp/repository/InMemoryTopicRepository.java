package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

public class InMemoryTopicRepository implements TopicRepository {
    private MutableLiveData<List<Topic>> topics;

    public InMemoryTopicRepository() {
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
    public void insertTopic(Topic topic) throws DatabaseException {
        Log.d("REPO", "called");
        List<Topic> temp = new ArrayList<>(topics.getValue());
        temp.add(topic);
        topics.setValue(temp);
    }
}
