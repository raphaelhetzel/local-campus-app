package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryTopicRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    Handler mockHandler = HandlerInstantRun.getMockHandler();

    private TopicRepository repository;
    private LocationRepository mLocationRepository;
    private MutableLiveData<String> mLocationId;
    @Before
    public void initialize() {
        this.mLocationRepository = mock(LocationRepository.class);
        this.mLocationId = new MutableLiveData<>();
        this.mLocationId.setValue("loc1");
        when(mLocationRepository.getCurrentLocation()).thenReturn(mLocationId);
        when(mLocationRepository.getFinalCurrentLocation()).thenReturn("loc1");
        this.repository = new InMemoryTopicRepository(mockHandler, mLocationRepository);
    }

    @Test
    public void insert_getByName() throws DatabaseException, InterruptedException {
        repository.insertTopic("/tum", "loc1");
        repository.insertTopic("/tum/garching", "loc1");

        assertEquals("/tum/garching", LiveDataHelper.getValue(repository.getTopicByName("/tum/garching")).getTopicName());
        assertEquals(null, LiveDataHelper.getValue(repository.getTopicByName("/tum/foo")));
    }

    @Test
    public void insert_getFinalByName() throws DatabaseException {
        repository.insertTopic("/tum", "loc1");
        repository.insertTopic("/tum/garching", "loc1");

        assertEquals("/tum/garching", repository.getFinalTopicByName("/tum/garching").getTopicName());
        assertEquals(null, repository.getFinalTopicByName("/tum/foo"));
    }

    @Test
    public void insert_getFinalByName_getById() throws DatabaseException, InterruptedException {
        repository.insertTopic("/tum", "loc1");
        repository.insertTopic("/tum/garching", "loc1");

        Topic byNameResult = repository.getFinalTopicByName("/tum/garching");

        assertEquals("/tum/garching", LiveDataHelper.getValue(repository.getTopic(byNameResult.getId())).getTopicName());
        assertEquals(null, LiveDataHelper.getValue(repository.getTopic(1337)));
    }

    @Test
    public void insert_getFinalByName_getFinal() throws DatabaseException {
        repository.insertTopic("/tum", "loc1");
        repository.insertTopic("/tum/garching", "loc1");

        Topic byNameResult = repository.getFinalTopicByName("/tum/garching");

        assertEquals("/tum/garching", repository.getFinalTopic(byNameResult.getId()).getTopicName());
        assertEquals(null, repository.getFinalTopic(1338));
    }

    @Test
    public void insert_getTopics() throws DatabaseException, InterruptedException {
        repository.insertTopic("/tum", "loc1");
        repository.insertTopic("/tum/garching", "loc2");

        LiveData<List<Topic>> topic_result = repository.getTopics();
        assertArrayEquals(LiveDataHelper.getValue(topic_result).stream().map(topic -> topic.getTopicName()).toArray(), new String[]{"/tum", "/tum/garching"});
    }

    @Test
    public void insert_getTopicsForCurrentLocation() throws DatabaseException, InterruptedException {
        repository.insertTopic("/tum", "loc1");
        repository.insertTopic("/tum/garching", "loc2");

        LiveData<List<Topic>> topic_result = repository.getTopicsForCurrentLocation();
        assertArrayEquals(LiveDataHelper.getValue(topic_result).stream().map(topic -> topic.getTopicName()).toArray(), new String[]{"/tum"});

        repository.insertTopic("/tum/garching", "loc1");
        assertArrayEquals(LiveDataHelper.getValue(topic_result).stream().map(topic -> topic.getTopicName()).toArray(), new String[]{"/tum", "/tum/garching"});

        repository.insertTopic("/tum/room1", "loc1");
        assertArrayEquals(LiveDataHelper.getValue(topic_result).stream().map(topic -> topic.getTopicName()).toArray(), new String[]{"/tum", "/tum/garching", "/tum/room1"});
    }

    @Test
    public void insert_getFinalTopicsForCurrentLocation() throws DatabaseException {
        repository.insertTopic("/tum", "loc1");
        repository.insertTopic("/tum/garching", "loc2");

        assertArrayEquals(repository.getFinalTopicsForCurrentLocation().stream().map(topic -> topic.getTopicName()).toArray(), new String[]{"/tum"});

        repository.insertTopic("/tum/garching", "loc1");
        assertArrayEquals(repository.getFinalTopicsForCurrentLocation().stream().map(topic -> topic.getTopicName()).toArray(), new String[]{"/tum", "/tum/garching"});

        repository.insertTopic("/tum/room1", "loc1");
        assertArrayEquals(repository.getFinalTopicsForCurrentLocation().stream().map(topic -> topic.getTopicName()).toArray(), new String[]{"/tum", "/tum/garching", "/tum/room1"});
    }
}
