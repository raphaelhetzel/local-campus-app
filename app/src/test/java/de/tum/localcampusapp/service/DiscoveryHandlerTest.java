package de.tum.localcampusapp.service;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.junit.MockitoJUnitRunner;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RealPostRepository;
import de.tum.localcampusapp.repository.RealTopicRepository;
import de.tum.localcampusapp.repository.TopicRepository;
import fi.tkk.netlab.dtn.scampi.applib.AppLib;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DiscoveryHandlerTest {

    private TopicRepository mTopicRepository;
    private PostRepository mPostRepository;
    private AppLib mAppLib;

    @Before
    public void initializeMocks() {
        this.mAppLib = mock(AppLib.class);
        this.mPostRepository = mock(RealPostRepository.class);
        this.mTopicRepository = mock(RealTopicRepository.class);
    }

    @Test
    public void insertsTopicToRepository() throws DatabaseException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mTopicRepository, mPostRepository, mAppLib);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        scampiMessage.putString("topicName", "/tum");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mTopicRepository).insertTopic(any(Topic.class));
    }

    @Test
    public void subscribesToTopic() throws InterruptedException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mTopicRepository, mPostRepository, mAppLib);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        scampiMessage.putString("topicName", "/tum");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mAppLib).subscribe(eq("/tum"), any(TopicHandler.class));
    }

    @Test
    public void ignoresMessagesWithMissingFields() throws InterruptedException, DatabaseException {
        DiscoveryHandler discoveryHandler = new DiscoveryHandler(mTopicRepository, mPostRepository, mAppLib);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString("deviceId", "1");
        discoveryHandler.messageReceived(scampiMessage, "/discovery");
        verify(mAppLib, never()).subscribe(any(), any());
        verify(mTopicRepository, never()).insertTopic(any());
    }
}
