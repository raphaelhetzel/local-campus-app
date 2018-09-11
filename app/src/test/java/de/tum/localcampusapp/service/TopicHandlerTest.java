package de.tum.localcampusapp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingTopicException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class TopicHandlerTest {

    PostRepository mPostRepository;
    private ScampiPostSerializer mScampiPostSerializer;

    @Before
    public void initializeMocks() {
        this.mPostRepository = mock(PostRepository.class);
        this.mScampiPostSerializer = mock(ScampiPostSerializer.class);
    }

    @Test
    public void receivesPost() throws MissingTopicException, MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer);
        Post post = new Post();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, ScampiPostSerializer.MESSAGE_TYPE_POST);
        when(mScampiPostSerializer.postFromMessage(scampiMessage)).thenReturn(post);

        topicHandler.messageReceived(scampiMessage, "Topic");

        verify(mScampiPostSerializer).postFromMessage(scampiMessage);
        verify(mPostRepository).insertPost(post);
    }

    @Test
    public void receivesPostExtension() throws MissingTopicException, MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, "post_extension");

        topicHandler.messageReceived(scampiMessage, "Topic");

        verify(mScampiPostSerializer, times(0)).postFromMessage(scampiMessage);
        verify(mPostRepository, times(0)).insertPost(any());
    }
}
