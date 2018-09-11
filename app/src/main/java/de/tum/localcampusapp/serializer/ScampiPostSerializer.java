package de.tum.localcampusapp.serializer;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingTopicException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.TopicRepository;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class ScampiPostSerializer {

    public static final String TYPE_ID_FIELD = "type_id";
    public static final String UUID_FIELD = "uuid";
    public static final String CREATOR_FIELD = "creator";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String UPDATED_AT_FIELD = "updated_at";
    public static final String DATA_FIELD = "data";
    public static final String SCORE_FIELD = "score";
    public static final String TOPIC_FIELD = "topic";
    public static final String MESSAGE_TYPE_FIELD = "message_type";
    public static final String MESSAGE_TYPE_POST = "post";

    private final TopicRepository topicRepository;

    public ScampiPostSerializer(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Post postFromMessage(SCAMPIMessage scampiMessage) throws MissingFieldsException, MissingTopicException, DatabaseException, WrongParserException {
        if(!messageIsPost(scampiMessage)) throw new WrongParserException();
        if (messageIsMissingFields(scampiMessage)) throw new MissingFieldsException();
        Topic topic = topicRepository.getFinalTopicByName(scampiMessage.getString(TOPIC_FIELD));
        if (topic == null) throw new MissingTopicException();

        Post post = new Post();
        post.setUuid(scampiMessage.getString(UUID_FIELD));
        post.setTypeId(scampiMessage.getString(TYPE_ID_FIELD));
        post.setCreator(scampiMessage.getString(CREATOR_FIELD));
        post.setCreatedAt(Converters.fromTimestamp(scampiMessage.getInteger(CREATED_AT_FIELD)));
        post.setUpdatedAt(Converters.fromTimestamp(scampiMessage.getInteger(UPDATED_AT_FIELD)));
        post.setData(scampiMessage.getString(DATA_FIELD));
        post.setScore(scampiMessage.getInteger(SCORE_FIELD));
        post.setTopicId(topic.getId());
        return post;
    }

    public SCAMPIMessage messageFromPost(Post post, Topic topic, String creator) {

        SCAMPIMessage message = SCAMPIMessage.builder()
                .appTag(post.getUuid())
                .build();
        message.putString(MESSAGE_TYPE_FIELD, MESSAGE_TYPE_POST);
        message.putString(TYPE_ID_FIELD, post.getTypeId());
        message.putString(UUID_FIELD, post.getUuid());
        message.putInteger(CREATED_AT_FIELD, Converters.dateToTimestamp(post.getCreatedAt()));
        message.putInteger(UPDATED_AT_FIELD, Converters.dateToTimestamp(post.getCreatedAt()));
        message.putString(DATA_FIELD, post.getData());
        message.putInteger(SCORE_FIELD, post.getScore());
        message.putString(TOPIC_FIELD, topic.getTopicName());
        return message;
    }

    private boolean messageIsMissingFields(SCAMPIMessage scampiMessage) {
        if (scampiMessage.hasString(TYPE_ID_FIELD) &&
                scampiMessage.hasString(UUID_FIELD) &&
                scampiMessage.hasString(CREATOR_FIELD) &&
                scampiMessage.hasInteger(CREATED_AT_FIELD) &&
                scampiMessage.hasInteger(UPDATED_AT_FIELD) &&
                scampiMessage.hasString(DATA_FIELD) &&
                scampiMessage.hasInteger(SCORE_FIELD) &&
                scampiMessage.hasString(TOPIC_FIELD)) return false;
        return true;
    }

    public static boolean messageIsPost(SCAMPIMessage scampiMessage) {
        if ( scampiMessage.hasString(MESSAGE_TYPE_FIELD) && scampiMessage.getString(MESSAGE_TYPE_FIELD).equals(MESSAGE_TYPE_POST))
            return true;
        return false;
    }
}
