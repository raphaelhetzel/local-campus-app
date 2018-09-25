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
import de.tum.localcampusapp.repository.NetworkLayerPostRepository;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.serializer.ScampiVoteSerializer;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST_EXTENSION;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_VOTE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TopicHandlerTest {

    NetworkLayerPostRepository mPostRepository;
    private ScampiPostSerializer mScampiPostSerializer;
    private ScampiVoteSerializer mScampiVoteSerializer;
    private ScampiPostExtensionSerializer mScampiPostExtensionSerializer;

    @Before
    public void initializeMocks() {
        this.mPostRepository = mock(NetworkLayerPostRepository.class);
        this.mScampiPostSerializer = mock(ScampiPostSerializer.class);
        this.mScampiVoteSerializer = mock(ScampiVoteSerializer.class);
        this.mScampiPostExtensionSerializer = mock(ScampiPostExtensionSerializer.class);
    }

    @Test
    public void receivesPost() throws MissingRelatedDataException, MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer, mScampiVoteSerializer, mScampiPostExtensionSerializer);
        Post post = new Post();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST);
        when(mScampiPostSerializer.postFromMessage(scampiMessage)).thenReturn(post);

        topicHandler.messageReceived(scampiMessage, "/topic");

        verify(mScampiPostSerializer).postFromMessage(scampiMessage);
        verify(mPostRepository).insertPost(post);
    }

    @Test
    public void receivesVote() throws MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer, mScampiVoteSerializer, mScampiPostExtensionSerializer);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        Vote vote = new Vote();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);
        when(mScampiVoteSerializer.messageToVote(scampiMessage)).thenReturn(vote);

        topicHandler.messageReceived(scampiMessage, "/topic");

        verify(mScampiVoteSerializer).messageToVote(scampiMessage);
        verify(mPostRepository).insertVote(vote);
    }

    @Test
    public void receivesPostExtension() throws MissingFieldsException, WrongParserException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer, mScampiVoteSerializer, mScampiPostExtensionSerializer);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST_EXTENSION);

        topicHandler.messageReceived(scampiMessage, "Topic");

        verify(mScampiPostExtensionSerializer).messageToPostExtension(scampiMessage);
        verify(mPostRepository).insertPostExtension(any());
    }

    @Test
    public void receivedMessageWithUnknownMessage() throws MissingRelatedDataException, DatabaseException {
        TopicHandler topicHandler = new TopicHandler(mPostRepository, mScampiPostSerializer, mScampiVoteSerializer, mScampiPostExtensionSerializer);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, "UNKNOWN");

        // Ignore Log.d not mocked error as mocking Log.d directly would require PowerMock / Robolectric
        try {
            topicHandler.messageReceived(scampiMessage, "Topic");
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("android.util.Log not mocked")) throw e;
        }

        verify(mPostRepository, never()).insertPost(any());
        verify(mPostRepository, never()).insertVote(any());
        verify(mPostRepository, never()).insertPostExtension(any());
    }
}
