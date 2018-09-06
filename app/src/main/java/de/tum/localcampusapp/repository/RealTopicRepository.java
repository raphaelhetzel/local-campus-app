package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

// TODO: Should be Singleton
public class RealTopicRepository implements TopicRepository {

    //TODO: Should probably be injected
    private final TopicDao topicDao;

    public RealTopicRepository(TopicDao topicDao) {
        this.topicDao = topicDao;
    }

    @Override
    public LiveData<List<Topic>> getTopics() throws DatabaseException {
        return topicDao.getTopics();
    }

    @Override
    public LiveData<Topic> getTopic(long id) throws DatabaseException {
        return topicDao.getTopic(id);
    }

    @Override
    public LiveData<Topic> getTopicByName(String topicName) throws DatabaseException {
        return topicDao.getByName(topicName);
    }

    @Override
    public void insertTopic(Topic topic) throws DatabaseException {
        try {
            topicDao.insert(topic);
        }
        // Catches both the case where the topic does not exist and the case where the key would be duplicate
        catch (android.database.sqlite.SQLiteConstraintException e) {
            throw new DatabaseException();
        }
    }
}
