package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

public class RealTopicRepository implements TopicRepository {

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

    /*
        TODO: make it clear in the name that this method can handle existing topics
        in a future version we might need duplicates to remove topics if a router
        gets out of range (we will then need a device id in the entity)
     */
    @Override
    public void insertTopic(Topic topic) throws DatabaseException {
        try {
            topicDao.insert(topic);
        }
        // Catches both the case where the topic id and the topic_name is duplicate
        catch (android.database.sqlite.SQLiteConstraintException e) {
            /*
                Ignore duplicate topics, while matching a String isn't ideal
                this should be fine as the string is verified by a test
             */
            if (e.getMessage().contains("topics.topic_name")) {
                return;
            }
            throw new DatabaseException();
        }
    }
}
