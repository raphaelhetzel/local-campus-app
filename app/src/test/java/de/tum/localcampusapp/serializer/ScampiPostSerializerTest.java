package de.tum.localcampusapp.serializer;

import org.junit.Test;

import java.util.Date;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.WrongParserException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST;
import static de.tum.localcampusapp.serializer.ScampiPostSerializer.TOPIC_FIELD;
import static org.junit.Assert.assertEquals;

public class ScampiPostSerializerTest {

    private static long testTimestamp = 1537000654;
    private static Date testDate = new Date(testTimestamp);

    @Test
    public void postFromMessage() throws DatabaseException, MissingFieldsException, WrongParserException {
        ScampiPostSerializer scampiPostSerializer = new ScampiPostSerializer();

        SCAMPIMessage testMessage = buildDefaultTestMessage();
        Post post = scampiPostSerializer.postFromMessage(testMessage);

        assertEquals(post.getId(), 0); // Doesn't set id
        assertEquals(post.getTypeId(), "Type");
        assertEquals(post.getData(), "Data");
        assertEquals(post.getCreatedAt(), testDate);
        assertEquals(post.getCreator(), "Creator");
        assertEquals(post.getTopicName(), "Topic");
        assertEquals(post.getUuid(), "UUID");
        assertEquals(post.getTopicId(), 0);

    }

    @Test(expected = MissingFieldsException.class)
    public void postFromMessage_MissingFields() throws DatabaseException, MissingFieldsException, WrongParserException {
        ScampiPostSerializer scampiPostSerializer = new ScampiPostSerializer();

        SCAMPIMessage testMessage = buildDefaultTestMessage();
        testMessage.removeContent(TOPIC_FIELD
        );
        scampiPostSerializer.postFromMessage(testMessage);
    }

    @Test(expected = WrongParserException.class)
    public void postFromMessage_NotAPost() throws DatabaseException, MissingFieldsException, WrongParserException {
        ScampiPostSerializer scampiPostSerializer = new ScampiPostSerializer();

        SCAMPIMessage testMessage = buildDefaultTestMessage();
        testMessage.putString(MESSAGE_TYPE_FIELD, "post_extension");

        scampiPostSerializer.postFromMessage(testMessage);
    }

    @Test
    public void MessageFromPost() throws MissingFieldsException {
        ScampiPostSerializer scampiPostSerializer = new ScampiPostSerializer();

        Post post = new Post(
                "UUID",
                "Type",
                "Topic",
                "Creator",
                testDate,
                "Data"
        );

        SCAMPIMessage message = scampiPostSerializer.messageFromPost(post);
        assertEquals(message.getString(MESSAGE_TYPE_FIELD), MESSAGE_TYPE_POST);
        assertEquals(message.getString(TOPIC_FIELD), "Topic");
        assertEquals(message.getString(ScampiPostSerializer.UUID_FIELD), "UUID");
        assertEquals(message.getString(ScampiPostSerializer.CREATOR_FIELD), "Creator");
        assertEquals(message.getString(ScampiPostSerializer.DATA_FIELD), "Data");
        assertEquals(message.getString(ScampiPostSerializer.TYPE_ID_FIELD), "Type");
        assertEquals(message.getInteger(ScampiPostSerializer.CREATED_AT_FIELD), testTimestamp);
    }

    private SCAMPIMessage buildDefaultTestMessage() {
        SCAMPIMessage message = SCAMPIMessage.builder().build();
        message.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST);
        message.putString(TOPIC_FIELD, "Topic");
        message.putInteger(ScampiPostSerializer.CREATED_AT_FIELD, testTimestamp);
        message.putString(ScampiPostSerializer.TYPE_ID_FIELD, "Type");
        message.putString(ScampiPostSerializer.CREATOR_FIELD, "Creator");
        message.putString(ScampiPostSerializer.DATA_FIELD, "Data");
        message.putString(ScampiPostSerializer.UUID_FIELD, "UUID");
        return message;
    }
}

