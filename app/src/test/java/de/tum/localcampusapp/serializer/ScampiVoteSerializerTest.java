package de.tum.localcampusapp.serializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.WrongParserException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_VOTE;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.CREATED_AT_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.CREATOR_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.POST_UUID_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.SCORE_INFLUENCE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiVoteSerializer.UUID_FIELD;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ScampiVoteSerializerTest {

    @Test
    public void messageToVote() throws WrongParserException, MissingFieldsException {
        ScampiVoteSerializer scampiVoteSerializer = new ScampiVoteSerializer();
        Date date = new Date();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);
        scampiMessage.putString(UUID_FIELD, "UUID");
        scampiMessage.putString(POST_UUID_FIELD, "UUID2");
        scampiMessage.putString(CREATOR_FIELD, "Creator");
        scampiMessage.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(date));
        scampiMessage.putInteger(SCORE_INFLUENCE_FIELD, 1);

        Vote result = scampiVoteSerializer.messageToVote(scampiMessage);

        assertEquals("UUID", result.getUuid());
        assertEquals("UUID2", result.getPostUuid());
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
        vote.setPostUuid("UUID2");
        vote.setCreatedAt(date);
        vote.setCreatorId("Creator");
        vote.setScoreInfluence(1);


        SCAMPIMessage scampiMessage = scampiVoteSerializer.voteToMessage(vote);
        assertEquals("UUID", scampiMessage.getString(UUID_FIELD));
        assertEquals("UUID2", scampiMessage.getString(POST_UUID_FIELD));
        assertEquals("Creator", scampiMessage.getString(CREATOR_FIELD));
        assertEquals(Converters.dateToTimestamp(date), new Long(scampiMessage.getInteger(CREATED_AT_FIELD)));
        assertEquals(1L, scampiMessage.getInteger(SCORE_INFLUENCE_FIELD));
    }

    @Test(expected = WrongParserException.class)
    public void wrongParser() throws WrongParserException, MissingFieldsException {
        ScampiVoteSerializer scampiVoteSerializer = new ScampiVoteSerializer();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST);

        scampiVoteSerializer.messageToVote(scampiMessage);
    }

    @Test(expected = MissingFieldsException.class)
    public void missingFieldsFromMessage() throws WrongParserException, MissingFieldsException {
        ScampiVoteSerializer scampiVoteDeSerializer = new ScampiVoteSerializer();
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);
        scampiMessage.putString(UUID_FIELD, "UUID");

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


        scampiVoteSerializer.voteToMessage(vote);
    }


}
