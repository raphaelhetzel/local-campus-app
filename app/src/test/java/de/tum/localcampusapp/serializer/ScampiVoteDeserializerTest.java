package de.tum.localcampusapp.serializer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.PostRepository;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiPostSerializer.MESSAGE_TYPE_POST;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.CREATED_AT_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.CREATOR_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.MESSAGE_TYPE_VOTE;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.POST_UUID_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.SCORE_INFLUENCE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.UUID_FIELD;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScampiVoteDeserializerTest {
    private PostRepository mPostRepository;

    @Before
    public void initializeMocks() {
        this.mPostRepository = mock(PostRepository.class);
    }

    @Test
    public void messageToVote() throws DatabaseException, MissingRelatedDataException, WrongParserException, MissingFieldsException {
        ScampiVoteDeserializer scampiVoteSerializer = new ScampiVoteDeserializer(mPostRepository);
        Date date = new Date();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);
        scampiMessage.putString(UUID_FIELD, "UUID");
        scampiMessage.putString(POST_UUID_FIELD, "UUID2");
        scampiMessage.putString(CREATOR_FIELD, "Creator");
        scampiMessage.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(date));
        scampiMessage.putInteger(SCORE_INFLUENCE_FIELD, 1);

        Post post = new Post();
        post.setUuid("UUID2");
        post.setId(1);
        when(mPostRepository.getFinalPostByUUID("UUID2")).thenReturn(post);

        Vote result = scampiVoteSerializer.messageToVote(scampiMessage);

        assertEquals("UUID", result.getUuid());
        assertEquals(1, result.getPostId());
        assertEquals("Creator", result.getCreatorId());
        assertEquals(date, result.getCreatedAt());
        assertEquals(1, result.getScoreInfluence());

    }

    @Test
    public void voteToMessage() throws MissingFieldsException {
        ScampiVoteSerializer scampiVoteSerializer = new ScampiVoteSerializer();
        Date date = new Date();

        Vote vote = new Vote();
        vote.setId(1);
        vote.setUuid("UUID");
        vote.setPostId(1);
        vote.setCreatedAt(date);
        vote.setCreatorId("Creator");
        vote.setScoreInfluence(1);

        Post post = new Post();
        post.setId(1);
        post.setUuid("UUID2");


        SCAMPIMessage scampiMessage = scampiVoteSerializer.voteToMessage(vote, post);
        assertEquals("UUID", scampiMessage.getString(UUID_FIELD));
        assertEquals("UUID2", scampiMessage.getString(POST_UUID_FIELD));
        assertEquals("Creator", scampiMessage.getString(CREATOR_FIELD));
        assertEquals(Converters.dateToTimestamp(date), new Long(scampiMessage.getInteger(CREATED_AT_FIELD)));
        assertEquals(1L, scampiMessage.getInteger(SCORE_INFLUENCE_FIELD));
    }

    @Test(expected = WrongParserException.class)
    public void wrongParser() throws DatabaseException, MissingRelatedDataException, WrongParserException, MissingFieldsException {
        ScampiVoteDeserializer scampiVoteDeSerializer = new ScampiVoteDeserializer(mPostRepository);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST);

        scampiVoteDeSerializer.messageToVote(scampiMessage);
    }

    @Test(expected = MissingFieldsException.class)
    public void missingFieldsFromMessage() throws DatabaseException, MissingRelatedDataException, WrongParserException, MissingFieldsException {
        ScampiVoteDeserializer scampiVoteDeSerializer = new ScampiVoteDeserializer(mPostRepository);
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);
        scampiMessage.putString(UUID_FIELD, "UUID");

        scampiVoteDeSerializer.messageToVote(scampiMessage);

    }

    @Test(expected = MissingRelatedDataException.class)
    public void missingRelatedData() throws DatabaseException, MissingRelatedDataException, WrongParserException, MissingFieldsException {
        ScampiVoteDeserializer scampiVoteDeSerializer = new ScampiVoteDeserializer(mPostRepository);
        Date date = new Date();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);
        scampiMessage.putString(UUID_FIELD, "UUID");
        scampiMessage.putString(POST_UUID_FIELD, "UUID2");
        scampiMessage.putString(CREATOR_FIELD, "Creator");
        scampiMessage.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(date));
        scampiMessage.putInteger(SCORE_INFLUENCE_FIELD, 1);

        when(mPostRepository.getFinalPostByUUID("UUID2")).thenReturn(null);

        scampiVoteDeSerializer.messageToVote(scampiMessage);

    }

    @Test(expected = MissingFieldsException.class)
    public void missingDataFromVote() throws MissingFieldsException {
        ScampiVoteSerializer scampiVoteSerializer = new ScampiVoteSerializer();
        Date date = new Date();

        Vote vote = new Vote();
        vote.setId(1);

        Post post = new Post();
        post.setId(1);
        post.setUuid("UUID2");


        scampiVoteSerializer.voteToMessage(vote, post);
    }


}
