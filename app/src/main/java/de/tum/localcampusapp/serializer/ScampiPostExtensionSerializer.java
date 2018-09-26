package de.tum.localcampusapp.serializer;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.WrongParserException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST_EXTENSION;

/**
    Serializer and Deserializer for PostExtensions.
 */
public class ScampiPostExtensionSerializer {

    public static final String UUID_FIELD = "uuid";
    public static final String POST_UUID_FIELD = "post_uuid";
    public static final String CREATOR_FIELD = "creator";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String DATA_FIELD = "data";

    /**
        Serialize a PostExtension into a Scampi Message. Raises a {@link MissingFieldsException}
        if the postExtension is missing fields.
     */
    public SCAMPIMessage postExtensionToMessage(PostExtension postExtension) throws MissingFieldsException {
        if (!postExtensionHasRequiredFields(postExtension)) throw new MissingFieldsException();

        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().appTag(postExtension.getUuid()).build();
        scampiMessage.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST_EXTENSION);
        scampiMessage.putString(UUID_FIELD, postExtension.getUuid());
        scampiMessage.putString(POST_UUID_FIELD, postExtension.getPostUuid());
        scampiMessage.putString(CREATOR_FIELD, postExtension.getCreatorId());
        scampiMessage.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(postExtension.getCreatedAt()));
        scampiMessage.putString(DATA_FIELD, postExtension.getData());
        return scampiMessage;
    }

    /**
        Deserialize a PostExtension from a Scampi Message. Raises a {@link MissingFieldsException}
        if the message is missing important fields and raises a {@link WrongParserException} if the methods was called with
        a message that does not contain a PostExtension (identified by the <code>MESSAGE_TYPE_FIELD</code>).
     */
    public PostExtension messageToPostExtension(SCAMPIMessage scampiMessage) throws WrongParserException, MissingFieldsException {
        if (!messageIsPostExtension(scampiMessage)) throw new WrongParserException();
        if (!messageHasRequiredFields(scampiMessage)) throw new MissingFieldsException();

        PostExtension postExtension = new PostExtension();
        postExtension.setUuid(scampiMessage.getString(UUID_FIELD));
        postExtension.setPostUuid(scampiMessage.getString(POST_UUID_FIELD));
        postExtension.setCreatorId(scampiMessage.getString(CREATOR_FIELD));
        postExtension.setData(scampiMessage.getString(DATA_FIELD));
        postExtension.setCreatedAt(Converters.fromTimestamp(scampiMessage.getInteger(CREATED_AT_FIELD)));

        return postExtension;
    }

    public static boolean messageIsPostExtension(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(MESSAGE_TYPE_FIELD) && scampiMessage.getString(MESSAGE_TYPE_FIELD).equals(MESSAGE_TYPE_POST_EXTENSION))
            return true;
        return false;
    }

    private boolean postExtensionHasRequiredFields(PostExtension postExtension) {
        if (!(postExtension.getUuid() == null ||
                postExtension.getUuid().isEmpty() ||
                postExtension.getPostUuid() == null ||
                postExtension.getPostUuid().isEmpty() ||
                postExtension.getCreatedAt() == null ||
                postExtension.getCreatorId() == null ||
                postExtension.getCreatorId().isEmpty() ||
                postExtension.getData() == null) ||
                postExtension.getData().isEmpty()) return true;
        return false;
    }

    private boolean messageHasRequiredFields(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(POST_UUID_FIELD) &&
                scampiMessage.hasString(UUID_FIELD) &&
                scampiMessage.hasString(CREATOR_FIELD) &&
                scampiMessage.hasInteger(CREATED_AT_FIELD) &&
                scampiMessage.hasString(DATA_FIELD)) return true;
        return false;
    }
}
