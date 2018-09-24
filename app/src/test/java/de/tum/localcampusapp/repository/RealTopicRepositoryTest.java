package de.tum.localcampusapp.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.database.sqlite.SQLiteConstraintException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.database.LocationTopicMappingDao;
import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.entity.LocationTopicMapping;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RealTopicRepositoryTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private TopicDao mTopicDao;
    private LocationTopicMappingDao mLocationTopicMappingDao;
    private LocationRepository mLocationRepository;

    @Before
    public void initialize_mocks() {
        mTopicDao = mock(TopicDao.class);
        mLocationTopicMappingDao = mock(LocationTopicMappingDao.class);
        mLocationRepository = mock(LocationRepository.class);
    }

    @Test
    public void insertNewTopic() {
        RealTopicRepository realTopicRepository = new RealTopicRepository(mLocationRepository, mTopicDao, mLocationTopicMappingDao);

        when(mTopicDao.getFinalByName("/topic1")).thenReturn(null);
        when(mTopicDao.insert(any(Topic.class))).thenReturn(1L);

        realTopicRepository.insertTopic("/topic1", "location");

        verify(mTopicDao).getFinalByName("/topic1");
        verify(mTopicDao).insert(any(Topic.class));
        verify(mLocationTopicMappingDao).insert(new LocationTopicMapping(1, "location"));
    }

    @Test
    public void insertExistingTopicNewLocation() {
        RealTopicRepository realTopicRepository = new RealTopicRepository(mLocationRepository, mTopicDao, mLocationTopicMappingDao);

        when(mTopicDao.getFinalByName("/topic1")).thenReturn(new Topic(1, "/topic1"));

        realTopicRepository.insertTopic("/topic1", "location");

        verify(mTopicDao).getFinalByName("/topic1");
        verify(mTopicDao, never()).insert(any(Topic.class));
        verify(mLocationTopicMappingDao).insert(new LocationTopicMapping(1, "location"));
    }

    @Test
    public void insertExistingTopicAndLocation() {
        SQLiteConstraintException mSqLiteConstraintException = mock(SQLiteConstraintException.class);
        when(mSqLiteConstraintException.getMessage()).thenReturn("UNIQUE constraint failed");
        RealTopicRepository realTopicRepository = new RealTopicRepository(mLocationRepository, mTopicDao, mLocationTopicMappingDao);

        when(mTopicDao.getFinalByName("/topic1")).thenReturn(new Topic(1, "/topic1"));
        doThrow(mSqLiteConstraintException).when(mLocationTopicMappingDao).insert(new LocationTopicMapping(1, "location"));

        realTopicRepository.insertTopic("/topic1", "location");

        verify(mTopicDao).getFinalByName("/topic1");
        verify(mTopicDao, never()).insert(any(Topic.class));
        verify(mLocationTopicMappingDao).insert(new LocationTopicMapping(1, "location"));
    }

    @Test
    public void getTopicsForCurrentLocation() throws InterruptedException {
        RealTopicRepository realTopicRepository = new RealTopicRepository(mLocationRepository, mTopicDao, mLocationTopicMappingDao);

        List<Topic> location1Topics = new ArrayList<>();
        List<Topic> location2Topics = new ArrayList<>();
        MutableLiveData<List<Topic>> location1LiveTopics = new MutableLiveData<>();
        location1LiveTopics.setValue(location1Topics);
        MutableLiveData<List<Topic>> location2LiveTopics = new MutableLiveData<>();
        location2LiveTopics.setValue(location2Topics);
        MutableLiveData<String> location = new MutableLiveData<>();

        when(mLocationRepository.getCurrentLocation()).thenReturn(location);
        location.setValue("location1");

        when(mTopicDao.getTopicsForLocation("location1")).thenReturn(location1LiveTopics);
        when(mTopicDao.getTopicsForLocation("location2")).thenReturn(location2LiveTopics);

        LiveData<List<Topic>> data = realTopicRepository.getTopicsForCurrentLocation();

        verify(mLocationRepository).getCurrentLocation();
        assertEquals(LiveDataHelper.getValue(data), location1Topics);
        verify(mTopicDao).getTopicsForLocation("location1");

        location.setValue("location2");
        assertEquals(LiveDataHelper.getValue(data), location2Topics);
        verify(mTopicDao).getTopicsForLocation("location2");
    }




}
