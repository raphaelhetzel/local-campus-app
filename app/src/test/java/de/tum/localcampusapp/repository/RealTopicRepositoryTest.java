package de.tum.localcampusapp.repository;

import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RealTopicRepositoryTest {
    TopicDao mTopicDao;
    SQLiteConstraintException sqLiteConstraintException;

    @Before
    public void initialize_mocks() {
        mTopicDao = mock(TopicDao.class);
        sqLiteConstraintException = mock(SQLiteConstraintException.class);
    }

    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void insertWithDuplicateId() throws DatabaseException {
        RealTopicRepository realTopicRepository = new RealTopicRepository(mTopicDao);
        Topic topic2 = new Topic(1, "/tum/garching");
        when(sqLiteConstraintException.getMessage()).thenReturn("UNIQUE constraint failed: topics.id (code 1555 SQLITE_CONSTRAINT_PRIMARYKEY)");
        doThrow(sqLiteConstraintException).when(mTopicDao).insert(topic2);
        realTopicRepository.insertTopic(topic2);
    }
}
