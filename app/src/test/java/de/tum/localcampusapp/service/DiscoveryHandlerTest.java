package de.tum.localcampusapp.service;
import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RealTopicRepository;
import de.tum.localcampusapp.repository.TopicRepository;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class DiscoveryHandlerTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();


    private TopicRepository mTopicRepository;
    private AppLib mAppLib;

    @Before
    public void initializeMocks() {
        this.mAppLib = mock(AppLib.class);
        this.mTopicRepository = mock(RealTopicRepository.class);
    }

    @Test
    public void insertsTopicToRepository() throws DatabaseException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mTopicRepository, "1");
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        scampiMessage.putString("topicName", "/tum");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mTopicRepository).insertTopic(anyString(), anyString());
    }

    @Test
    public void subscribesToTopic() throws InterruptedException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mTopicRepository, "1");
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        scampiMessage.putString("topicName", "/tum");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mAppLib).subscribe(eq("/tum"), any(TopicHandler.class));
    }

    @Test
    public void ignoresMessagesWithMissingFields() throws InterruptedException, DatabaseException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mTopicRepository, "1");
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mAppLib, never()).subscribe(any(), any());
        verify(mTopicRepository, never()).insertTopic(anyString(), anyString());
    }

    @Test
    public void doesntSubscribeTwice() throws InterruptedException, DatabaseException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mAppLib, mTopicRepository, "1");

        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        scampiMessage.putString("topicName", "/tum");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");

        SCAMPIMessage scampiMessage2 = SCAMPIMessage.builder().build();
        scampiMessage2.putString("deviceId", "1");
        scampiMessage2.putString("topicName", "/tum");
        discoveryHandler.messageReceived(scampiMessage2, "/discovery");

        verify(mAppLib, times(1)).subscribe(any(), any());
        verify(mTopicRepository, times(2)).insertTopic(anyString(), anyString());
    }
}
