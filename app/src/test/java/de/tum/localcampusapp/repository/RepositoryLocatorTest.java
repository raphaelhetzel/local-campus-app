package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.content.Context;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RepositoryLocatorTest {
    private Context mContext;
    private TopicDao mTopicDao;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void initializeMocks() {
        mContext = mock(Context.class);
        mTopicDao = mock(TopicDao.class);
    }

    @Before
    public void resetLocator() {
        RepositoryLocator.reset();
    }

    @Test
    public void init() {
        RepositoryLocator.init(mContext);
        assertEquals(RepositoryLocator.getPostRepository().getClass(), RealPostRepository.class);
        assertEquals(RepositoryLocator.getTopicRepository().getClass(), RealTopicRepository.class);
        assertEquals(RepositoryLocator.getScampiPostSerializer().getClass(), ScampiPostSerializer.class);
    }

    @Test
    public void initInMemory() {
        RepositoryLocator.initInMemory(mContext);
        assertEquals(RepositoryLocator.getPostRepository().getClass(), InMemoryPostRepository.class);
        assertEquals(RepositoryLocator.getTopicRepository().getClass(), InMemoryTopicRepository.class);
        assertEquals(RepositoryLocator.getScampiPostSerializer().getClass(), ScampiPostSerializer.class);
    }

    @Test
    public void preventsImplicitReInit() throws RuntimeException {
        RepositoryLocator.init(mContext);
        RepositoryLocator.initInMemory(mContext);
        assertEquals(RepositoryLocator.getTopicRepository().getClass(), RealTopicRepository.class);
    }

    @Test
    public void reInitCustom() {
        // This would not work for the current implementation of the repositories
        RepositoryLocator.reInitCustom(new RealTopicRepository(mTopicDao),
                new InMemoryPostRepository(),
                new ScampiPostSerializer(new InMemoryTopicRepository()));
        assertEquals(RepositoryLocator.getPostRepository().getClass(), InMemoryPostRepository.class);
        assertEquals(RepositoryLocator.getTopicRepository().getClass(), RealTopicRepository.class);
        assertEquals(RepositoryLocator.getScampiPostSerializer().getClass(), ScampiPostSerializer.class);
    }
}
