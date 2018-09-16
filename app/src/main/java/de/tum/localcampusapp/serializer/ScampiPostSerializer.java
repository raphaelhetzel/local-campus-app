package de.tum.localcampusapp.serializer;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.WrongParserException;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_FIELD;
import static de.tum.localcampusapp.serializer.ScampiMessageTypes.MESSAGE_TYPE_POST;

public class ScampiPostSerializer {

    public static final String TYPE_ID_FIELD = "type_id";
    public static final String UUID_FIELD = "uuid";
    public static final String CREATOR_FIELD = "creator";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String DATA_FIELD = "data";
    public static final String TOPIC_FIELD = "topic";

    public Post postFromMessage(SCAMPIMessage scampiMessage) throws MissingFieldsException, WrongParserException {
        if (!messageIsPost(scampiMessage)) throw new WrongParserException();
        if (messageIsMissingFields(scampiMessage)) throw new MissingFieldsException();

        Post post = new Post();
        post.setUuid(scampiMessage.getString(UUID_FIELD));
        post.setTopicName(scampiMessage.getString(TOPIC_FIELD));
        post.setTypeId(scampiMessage.getString(TYPE_ID_FIELD));
        post.setCreator(scampiMessage.getString(CREATOR_FIELD));
        post.setCreatedAt(Converters.fromTimestamp(scampiMessage.getInteger(CREATED_AT_FIELD)));
        post.setData(scampiMessage.getString(DATA_FIELD));
        return post;
    }

    public SCAMPIMessage messageFromPost(Post post) throws MissingFieldsException{
        if(!postHasRequiredFields(post)) throw new MissingFieldsException();
        SCAMPIMessage message = SCAMPIMessage.builder()
                .appTag(post.getUuid())
                .build();
        message.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST);
        message.putString(TYPE_ID_FIELD, post.getTypeId());
        message.putString(UUID_FIELD, post.getUuid());
        message.putString(CREATOR_FIELD, post.getCreator());
        message.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(post.getCreatedAt()));
        message.putString(DATA_FIELD, post.getData());
        message.putString(TOPIC_FIELD, post.getTopicName());
        return message;
    }

    private boolean messageIsMissingFields(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(TYPE_ID_FIELD) &&
                scampiMessage.hasString(UUID_FIELD) &&
                scampiMessage.hasString(CREATOR_FIELD) &&
                scampiMessage.hasInteger(CREATED_AT_FIELD) &&
                scampiMessage.hasString(DATA_FIELD) &&
                scampiMessage.hasString(TOPIC_FIELD)) return false;
        return true;
    }

    private boolean postHasRequiredFields(Post post) {
        if (!(post.getUuid() == null ||
                post.getUuid().isEmpty() ||
                post.getTopicName() == null ||
                post.getTopicName().isEmpty() ||
                post.getCreatedAt() == null ||
                post.getCreator() == null ||
                post.getCreator().isEmpty() ||
                post.getData() == null ||
                post.getData().isEmpty() ||
                post.getTypeId() == null ||
                post.getTypeId().isEmpty())) return true;
        return false;
    }

    public static boolean messageIsPost(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(MESSAGE_TYPE_FIELD) && scampiMessage.getString(MESSAGE_TYPE_FIELD).equals(MESSAGE_TYPE_POST))
            return true;
        return false;
    }
}
