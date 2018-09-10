package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

public interface TopicRepository {
    LiveData<List<Topic>> getTopics() throws DatabaseException;

    LiveData<Topic> getTopic(long id) throws DatabaseException;

    LiveData<Topic> getTopicByName(String topicName) throws DatabaseException;

    Topic getFinalTopicByName(String topicName) throws DatabaseException;

    /*
        Should only be called from the scampi side of the application.
        Possibly refactor the structure to better separate this.
    */
    void insertTopic(Topic topic) throws DatabaseException;
}
