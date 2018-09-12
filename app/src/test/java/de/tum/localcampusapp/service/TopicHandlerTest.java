package de.tum.localcampusapp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.serializer.ScampiVoteDeserializer;
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
    private ScampiVoteDeserializer mScampiVoteDeserializer;

    @Before
    public void initializeMocks() {
        this.mPostRepository = mock(PostRepository.class);
        this.mScampiPostSerializer = mock(ScampiPostSerializer.class);
        this.mScampiVoteDeserializer = mock(ScampiVoteDeserializer.class);
    }

    @Test
    public void receivesPost() throws MissingRelatedDataException, MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer, mScampiVoteDeserializer);
        Post post = new Post();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, ScampiPostSerializer.MESSAGE_TYPE_POST);
        when(mScampiPostSerializer.postFromMessage(scampiMessage)).thenReturn(post);

        topicHandler.messageReceived(scampiMessage, "Topic");

        verify(mScampiPostSerializer).postFromMessage(scampiMessage);
        verify(mPostRepository).insertPost(post);
    }

    @Test
    public void receivesVote() throws MissingRelatedDataException, MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer, mScampiVoteDeserializer);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        Vote vote = new Vote();
        scampiMessage.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, "vote");
        when(mScampiVoteDeserializer.messageToVote(scampiMessage)).thenReturn(vote);

        topicHandler.messageReceived(scampiMessage, "Topic");

        verify(mScampiVoteDeserializer).messageToVote(scampiMessage);
        verify(mPostRepository).insertVote(vote);
    }

    @Test
    public void receivesPostExtension() throws MissingRelatedDataException, MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer, mScampiVoteDeserializer);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, "post_extension");

        topicHandler.messageReceived(scampiMessage, "Topic");

        verify(mScampiPostSerializer, times(0)).postFromMessage(scampiMessage);
        verify(mPostRepository, times(0)).insertPost(any());
    }
}
