package de.tum.localcampusapp.service;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RealTopicRepository;
import de.tum.localcampusapp.repository.TopicRepository;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static android.arch.lifecycle.Lifecycle.State.STARTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class DiscoveryHandlerTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();


    private TopicRepository mTopicRepository;
    private AppLib mAppLib;
    private LifecycleOwner mLifeCycleOwner;
    private MutableLiveData<List<Topic>> mTopicList;

    @Before
    public void initializeMocks() {
        this.mAppLib = mock(AppLib.class);
        this.mTopicRepository = mock(RealTopicRepository.class);

        this.mLifeCycleOwner = mock(LifecycleOwner.class);
        LifecycleRegistry lifecycle = new LifecycleRegistry(mock(LifecycleOwner.class));
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        when(mLifeCycleOwner.getLifecycle()).thenReturn(lifecycle);

        mTopicList = new MutableLiveData<>();
        mTopicList.setValue(new ArrayList<>());

        when(mTopicRepository.getTopicsForCurrentLocation()).thenReturn(mTopicList);
    }

    @Test
    public void insertsTopicToRepository() {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mLifeCycleOwner,  mTopicRepository);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        scampiMessage.putString("topicName", "/tum");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mTopicRepository).insertTopic("/tum", "1");
    }

    @Test
    public void ignoresMessagesWithMissingFields() {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mLifeCycleOwner, mTopicRepository);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mTopicRepository, never()).insertTopic(anyString(), anyString());
    }

    @Test
    public void subscribesToTopicsRelevantToTheLocation() throws InterruptedException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mLifeCycleOwner, mTopicRepository);

        Topic topic1 = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        addTestTopic(topic1);
        addTestTopic(topic2);

        verify(mAppLib).subscribe(eq("/tum"), any(TopicHandler.class));
        verify(mAppLib).subscribe(eq("/tum/garching"), any(TopicHandler.class));
    }

    @Test
    public void doesntSubscribeTwice() throws InterruptedException, DatabaseException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mLifeCycleOwner, mTopicRepository);

        Topic topic1 = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        Topic topic3 = new Topic(2, "/tum/garching/room1");
        addTestTopic(topic1);
        addTestTopic(topic2);
        addTestTopic(topic3);

        verify(mAppLib, times(1)).subscribe(eq("/tum"), any(TopicHandler.class));
        verify(mAppLib, times(1)).subscribe(eq("/tum/garching"), any(TopicHandler.class));
        verify(mAppLib, times(1)).subscribe(eq("/tum/garching/room1"), any(TopicHandler.class));
        verify(mAppLib, never()).unsubscribe(anyString());
    }

    @Test
    public void unsubscribesFromTopicsNotRelevantAnymore() throws InterruptedException, DatabaseException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mLifeCycleOwner, mTopicRepository);

        Topic topic1 = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/garching");
        Topic topic3 = new Topic(2, "/tum/city");
        addTestTopic(topic1);
        addTestTopic(topic2);
        verify(mAppLib, times(1)).subscribe(eq("/tum/garching"), any(TopicHandler.class));

        removeTestTopic(topic2);
        addTestTopic(topic3);

        verify(mAppLib, times(1)).subscribe(eq("/tum"), any(TopicHandler.class));
        verify(mAppLib, times(1)).subscribe(eq("/tum/city"), any(TopicHandler.class));
        verify(mAppLib, times(1)).unsubscribe("/tum/garching");
    }

    private void addTestTopic(Topic topic) {
        List<Topic> temp = mTopicList.getValue();
        temp.add(topic);
        mTopicList.setValue(temp);
    }

    private void removeTestTopic(Topic topic) {
        List<Topic> temp = mTopicList.getValue();
        temp.remove(topic);
        mTopicList.setValue(temp);
    }
}
