package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.Context;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RepositoryLocatorTest {
    private Context mContext;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void initializeMocks() {
        mContext = mock(Context.class);
    }

    @Before
    public void resetLocator() {
        RepositoryLocator.reset();
    }

    @Test
    public void returnsSigletonRealPostRepositoryByDefault() {

        PostRepository postRepository = RepositoryLocator.getPostRepository(mContext);
        assertEquals(postRepository.getClass(), RealPostRepository.class);

        PostRepository postRepository2 = RepositoryLocator.getPostRepository(mContext);
        assertEquals(postRepository, postRepository2);
    }

    @Test
    public void letsOverwritePostRepository() {

        PostRepository postRepository = RepositoryLocator.getPostRepository(mContext);
        assertEquals(postRepository.getClass(), RealPostRepository.class);

        RepositoryLocator.setCustomPostRepository(new InMemoryPostRepository());

        PostRepository postRepository2 = RepositoryLocator.getPostRepository(mContext);
        assertEquals(postRepository2.getClass(), InMemoryPostRepository.class);
    }

    @Test
    public void returnsSigletonRealTopicRepositoryByDefault() {

        TopicRepository topicRepository = RepositoryLocator.getTopicRepository(mContext);
        assertEquals(topicRepository.getClass(), RealTopicRepository.class);

        TopicRepository topicRepository2 = RepositoryLocator.getTopicRepository(mContext);
        assertEquals(topicRepository, topicRepository2);
    }

    @Test
    public void letsOverwriteTopicRepository() {

        TopicRepository topicRepository = RepositoryLocator.getTopicRepository(mContext);
        assertEquals(topicRepository.getClass(), RealTopicRepository.class);

        RepositoryLocator.setCustomTopicRepository(new InMemoryTopicRepository());

        TopicRepository topicRepository2 = RepositoryLocator.getTopicRepository(mContext);
        assertEquals(topicRepository2.getClass(), InMemoryTopicRepository.class);
    }
}
