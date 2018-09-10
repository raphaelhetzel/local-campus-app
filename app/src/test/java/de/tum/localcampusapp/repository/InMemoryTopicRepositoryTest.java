package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.os.Handler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.testhelper.HandlerInstantRun;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryTopicRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    Handler mockHandler = HandlerInstantRun.getMockHandler();

    private TopicRepository repository;


    @Before
    public void before() {
        this.repository = new InMemoryTopicRepository(mockHandler);
    }

    @Test
    public void insert_get() throws DatabaseException, InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        repository.insertTopic(topic);
        repository.insertTopic(topic2);
        LiveData<Topic> topic_result = repository.getTopic(2);
        assert (LiveDataHelper.getValue(topic_result).equals(topic2));
    }

    @Test
    public void insert_getByName() throws DatabaseException, InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        repository.insertTopic(topic);
        repository.insertTopic(topic2);
        LiveData<Topic> topic_result = repository.getTopicByName("/tum/garching");
        assert (LiveDataHelper.getValue(topic_result).equals(topic2));
    }

    @Test
    public void insert_getFinalByName() throws DatabaseException, InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        repository.insertTopic(topic);
        repository.insertTopic(topic2);
        assertEquals(repository.getFinalTopicByName("Foo"), null);
        assertEquals(repository.getFinalTopicByName(topic2.getTopicName()), topic2);
    }

    @Test
    public void insert_getTopics() throws DatabaseException, InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        repository.insertTopic(topic);
        repository.insertTopic(topic2);
        LiveData<List<Topic>> topic_result = repository.getTopics();
        assertArrayEquals(LiveDataHelper.getValue(topic_result).toArray(), new Topic[]{topic, topic2});
    }

    @Test
    public void insertDuplicate() throws DatabaseException, InterruptedException {
        Topic topic = new Topic();
        topic.setTopicName("/tum");
        Topic topic2 = new Topic();
        topic2.setTopicName("/tum");
        repository.insertTopic(topic);
        repository.insertTopic(topic2);
        LiveData<List<Topic>> topic_result = repository.getTopics();
        assertEquals(LiveDataHelper.getValue(topic_result).size(), 1);
    }
}
