package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

import static org.junit.Assert.assertArrayEquals;

public class InMemoryTopicRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private TopicRepository repository;

    @Before
    public void before() {
        this.repository = new InMemoryTopicRepository();
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
    public void insert_getTopics() throws DatabaseException, InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        repository.insertTopic(topic);
        repository.insertTopic(topic2);
        LiveData<List<Topic>> topic_result = repository.getTopics();
        assertArrayEquals(LiveDataHelper.getValue(topic_result).toArray(), new Topic[]{topic, topic2});
    }
}
