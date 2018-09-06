package de.tum.localcampusapp.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class RealTopicRepositoryTest {
    TopicDao mTopicDao;

    @Before
    public void initialize_mocks() {
        mTopicDao = mock(TopicDao.class);
    }

    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void insertWithDuplicateId() throws DatabaseException {
        RealTopicRepository realTopicRepository = new RealTopicRepository(mTopicDao);
        Topic topic2 = new Topic(1, "/tum/garching");
        doThrow(new android.database.sqlite.SQLiteConstraintException()).when(mTopicDao).insert(topic2);
        realTopicRepository.insertTopic(topic2);
    }
}
