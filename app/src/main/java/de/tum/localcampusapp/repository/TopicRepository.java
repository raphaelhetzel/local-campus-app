package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

public interface TopicRepository {
    public LiveData<Topic> getPost(long topicId);
    public void addTopic(Topic topic) throws DatabaseException;
}
