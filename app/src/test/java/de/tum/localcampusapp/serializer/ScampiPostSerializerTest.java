package de.tum.localcampusapp.serializer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingTopicException;
import de.tum.localcampusapp.exception.WrongParserException;
import de.tum.localcampusapp.repository.TopicRepository;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScampiPostSerializerTest {

    private TopicRepository mTopicRepository;

    @Before
    public void initializeMocks() {
        this.mTopicRepository = mock(TopicRepository.class);
    }

    @Test
    public void postFromMessage() throws DatabaseException, MissingFieldsException, WrongParserException, MissingTopicException {
        ScampiPostSerializer scampiPostSerializer = new  ScampiPostSerializer(mTopicRepository);
        Date currentTime = new Date();
        SCAMPIMessage message = SCAMPIMessage.builder().build();
        message.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, ScampiPostSerializer.MESSAGE_TYPE_POST);
        message.putString(ScampiPostSerializer.TOPIC_FIELD, "Topic");
        message.putInteger(ScampiPostSerializer.CREATED_AT_FIELD, Converters.dateToTimestamp(currentTime));
        message.putString(ScampiPostSerializer.TYPE_ID_FIELD, "Type");
        message.putString(ScampiPostSerializer.CREATOR_FIELD ,"Creator");
        message.putString(ScampiPostSerializer.DATA_FIELD,"Data");
        message.putString(ScampiPostSerializer.UUID_FIELD,"UUID");

        message.putInteger(ScampiPostSerializer.SCORE_FIELD, 1);
        message.putInteger(ScampiPostSerializer.UPDATED_AT_FIELD, Converters.dateToTimestamp(currentTime));

        when(mTopicRepository.getFinalTopicByName("Topic")).thenReturn(new Topic(1, "Topic"));

        Post post  = scampiPostSerializer.postFromMessage(message);
        assertEquals(post.getId(), 0); // Doesn't set id
        assertEquals(post.getTypeId(), "Type");
        assertEquals(post.getData(), "Data");
        assertEquals(post.getCreatedAt(), currentTime);
        assertEquals(post.getCreator(), "Creator");
        assertEquals(post.getTopicId(), 1);
        assertEquals(post.getUuid(), "UUID");

        assertEquals(post.getUpdatedAt(), currentTime);
        assertEquals(post.getScore(), 1);

    }

    @Test
    public void MessageFromPost() {
        ScampiPostSerializer scampiPostSerializer = new  ScampiPostSerializer(mTopicRepository);
        Date currentTime = new Date();

        Post post = new Post(
                1,
                "UUID",
                "Type",
                1,
                "Creator",
                currentTime,
                currentTime,
                "Data",
                1
        );
        Topic topic = new Topic(1, "Topic");

        SCAMPIMessage message = scampiPostSerializer.messageFromPost(post, topic, "Creator");
        assertEquals(message.getString(ScampiPostSerializer.MESSAGE_TYPE_FIELD), ScampiPostSerializer.MESSAGE_TYPE_POST);
        assertEquals(message.getString(ScampiPostSerializer.TOPIC_FIELD), "Topic");
        assertEquals(message.getString(ScampiPostSerializer.UUID_FIELD), "UUID");
        assertEquals(message.getString(ScampiPostSerializer.DATA_FIELD), "Data");
        assertEquals(message.getString(ScampiPostSerializer.TYPE_ID_FIELD), "Type");
        assertEquals(new Long(message.getInteger(ScampiPostSerializer.CREATED_AT_FIELD)), Converters.dateToTimestamp(currentTime));

        assertEquals(new  Long(message.getInteger(ScampiPostSerializer.UPDATED_AT_FIELD)), Converters.dateToTimestamp(currentTime));
        assertEquals(message.getInteger(ScampiPostSerializer.SCORE_FIELD), 1);
    }

    @Test(expected = MissingFieldsException.class)
    public void postFromMessage_MissingFields() throws DatabaseException, MissingFieldsException, WrongParserException, MissingTopicException {
        ScampiPostSerializer scampiPostSerializer = new  ScampiPostSerializer(mTopicRepository);
        SCAMPIMessage message = SCAMPIMessage.builder().build();
        message.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, ScampiPostSerializer.MESSAGE_TYPE_POST);
        message.putString(ScampiPostSerializer.TOPIC_FIELD, "Topic");

        Post post  = scampiPostSerializer.postFromMessage(message);
    }

    @Test(expected = MissingTopicException.class)
    public void postFromMessage_MissingTopic() throws DatabaseException, MissingFieldsException, WrongParserException, MissingTopicException {
        ScampiPostSerializer scampiPostSerializer = new  ScampiPostSerializer(mTopicRepository);
        Date currentTime = new Date();
        SCAMPIMessage message = SCAMPIMessage.builder().build();
        message.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, ScampiPostSerializer.MESSAGE_TYPE_POST);
        message.putString(ScampiPostSerializer.TOPIC_FIELD, "Topic");
        message.putInteger(ScampiPostSerializer.CREATED_AT_FIELD, Converters.dateToTimestamp(currentTime));
        message.putString(ScampiPostSerializer.TYPE_ID_FIELD, "Type");
        message.putString(ScampiPostSerializer.CREATOR_FIELD ,"Creator");
        message.putString(ScampiPostSerializer.DATA_FIELD,"Data");
        message.putString(ScampiPostSerializer.UUID_FIELD,"UUID");

        message.putInteger(ScampiPostSerializer.SCORE_FIELD, 1);
        message.putInteger(ScampiPostSerializer.UPDATED_AT_FIELD, Converters.dateToTimestamp(currentTime));

        when(mTopicRepository.getFinalTopicByName("Topic")).thenReturn(null);

        Post post  = scampiPostSerializer.postFromMessage(message);
    }

    @Test(expected = WrongParserException.class)
    public void postFromMessage_NotAPost() throws DatabaseException, MissingFieldsException, WrongParserException, MissingTopicException {
        ScampiPostSerializer scampiPostSerializer = new  ScampiPostSerializer(mTopicRepository);
        Date currentTime = new Date();
        SCAMPIMessage message = SCAMPIMessage.builder().build();
        message.putString(ScampiPostSerializer.MESSAGE_TYPE_FIELD, "post_extension");

        Post post  = scampiPostSerializer.postFromMessage(message);
    }
}

