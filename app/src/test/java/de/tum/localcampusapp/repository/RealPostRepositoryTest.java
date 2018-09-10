package de.tum.localcampusapp.repository;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class RealPostRepositoryTest {
    PostDao mPostDao;
    Context mContext;
    TopicRepository mTopicRepository;

    @Before
    public void initialize_mocks() {
        mPostDao = mock(PostDao.class);
        mContext = mock(Context.class);
        mTopicRepository = mock(TopicRepository.class);
    }

    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void insertWithDuplicateIdOrNonExistantTopic() throws DatabaseException {
        RealPostRepository realPostRepository = new RealPostRepository(mContext, mPostDao, mTopicRepository);
        Post post2 = new Post();
        doThrow(new android.database.sqlite.SQLiteConstraintException()).when(mPostDao).insert(post2);
        realPostRepository.insertPost(post2);
    }


    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void updateWithDuplicateIdOrNonExistantTopic() throws DatabaseException {
        RealPostRepository realPostRepository = new RealPostRepository(mContext, mPostDao, mTopicRepository);
        Post post2 = new Post();
        doThrow(new android.database.sqlite.SQLiteConstraintException()).when(mPostDao).update(post2);
        realPostRepository.updatePost(post2);
    }

    @Test
    public void add() throws DatabaseException {
        RealPostRepository realPostRepository = new RealPostRepository(mContext, mPostDao, mTopicRepository);
        Post post2 = new Post();
        post2.setTopicId(1);
        doThrow(new android.database.sqlite.SQLiteConstraintException()).when(mPostDao).update(post2);
        realPostRepository.updatePost(post2);
    }

}
