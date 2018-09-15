package de.tum.localcampusapp.serializer;

import org.junit.Test;

import java.util.Date;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.WrongParserException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST_EXTENSION;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_VOTE;
import static de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer.CREATED_AT_FIELD;
import static de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer.CREATOR_FIELD;
import static de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer.DATA_FIELD;
import static de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer.POST_UUID_FIELD;
import static de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer.UUID_FIELD;
import static org.junit.Assert.assertEquals;

public class ScampiPostExtensionSerializerTest {

    private static long testTimestamp = 1537000654;
    private static Date testDate = new Date(testTimestamp);

    @Test
    public void messageToPostExtension() throws WrongParserException, MissingFieldsException {

        ScampiPostExtensionSerializer scampiPostExtensionSerializer = new ScampiPostExtensionSerializer();

        SCAMPIMessage testMessage = buildDefaultTestMessage();

        PostExtension result = scampiPostExtensionSerializer.messageToPostExtension(testMessage);

        assertEquals("UUID", result.getUuid());
        assertEquals("Post-UUID", result.getPostUuid());
        assertEquals("Creator", result.getCreatorId());
        assertEquals(testDate, result.getCreatedAt());
        assertEquals("Data", result.getData());

    }

    @Test(expected = WrongParserException.class)
    public void wrongParser() throws WrongParserException, MissingFieldsException {
        ScampiPostExtensionSerializer scampiPostExtensionSerializer = new ScampiPostExtensionSerializer();


        SCAMPIMessage testMessage = buildDefaultTestMessage();
        testMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_VOTE);

        PostExtension result = scampiPostExtensionSerializer.messageToPostExtension(testMessage);
    }

    @Test(expected = MissingFieldsException.class)
    public void missingFieldFromMessage() throws WrongParserException, MissingFieldsException {
        ScampiPostExtensionSerializer scampiPostExtensionSerializer = new ScampiPostExtensionSerializer();


        SCAMPIMessage testMessage = buildDefaultTestMessage();
        testMessage.removeContent(POST_UUID_FIELD);

        PostExtension result = scampiPostExtensionSerializer.messageToPostExtension(testMessage);
    }

    @Test
    public void voteToMessage() throws MissingFieldsException {

        ScampiPostExtensionSerializer scampiPostExtensionSerializer = new ScampiPostExtensionSerializer();
        Date date = new Date();

        PostExtension postExtension = new PostExtension("UUID", "UUID2", "Creator", testDate, "Data");

        SCAMPIMessage result = scampiPostExtensionSerializer.postExtensionToMessage(postExtension);
        assertEquals("UUID", result.getString(UUID_FIELD));
        assertEquals("UUID2", result.getString(POST_UUID_FIELD));
        assertEquals("Creator", result.getString(CREATOR_FIELD));
        assertEquals(testTimestamp, result.getInteger(CREATED_AT_FIELD));
        assertEquals("Data", result.getString(DATA_FIELD));
    }

    @Test(expected = MissingFieldsException.class)
    public void missingFieldFromVote() throws MissingFieldsException {
        ScampiPostExtensionSerializer scampiPostExtensionSerializer = new ScampiPostExtensionSerializer();
        Date date = new Date();

        PostExtension postExtension = new PostExtension("UUID", "UUID2", "Creator", testDate, "Data");
        postExtension.setUuid("");

        SCAMPIMessage result = scampiPostExtensionSerializer.postExtensionToMessage(postExtension);

    }

    private SCAMPIMessage buildDefaultTestMessage() {
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().appTag("UUID").build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST_EXTENSION);
        scampiMessage.putString(UUID_FIELD, "UUID");
        scampiMessage.putString(POST_UUID_FIELD, "Post-UUID");
        scampiMessage.putString(CREATOR_FIELD, "Creator");
        scampiMessage.putInteger(CREATED_AT_FIELD, testTimestamp);
        scampiMessage.putString(DATA_FIELD, "Data");
        return scampiMessage;
    }
}
