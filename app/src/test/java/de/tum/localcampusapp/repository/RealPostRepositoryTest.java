package de.tum.localcampusapp.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class RealPostRepositoryTest {
    PostDao mPostDao;

    @Before
    public void initialize_mocks() {
        mPostDao = mock(PostDao.class);
    }

    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void insertWithDuplicateIdOrNonExistantTopic() throws DatabaseException {
        RealPostRepository realPostRepository = new RealPostRepository(mPostDao);
        Post post2 = new Post();
        doThrow(new android.database.sqlite.SQLiteConstraintException()).when(mPostDao).insert(post2);
        realPostRepository.insertPost(post2);
    }


    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void updateWithDuplicateIdOrNonExistantTopic() throws DatabaseException {
        RealPostRepository realPostRepository = new RealPostRepository(mPostDao);
        Post post2 = new Post();
        doThrow(new android.database.sqlite.SQLiteConstraintException()).when(mPostDao).update(post2);
        realPostRepository.updatePost(post2);
    }

}
