package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.remote.EspressoRemoteMessage;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLClientInfoException;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.testhelper.LiveDataHelper;


@RunWith(AndroidJUnit4.class)
public class TopicDaoTest {
    private TopicDao topicDao;
    private AppDatabase testDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        topicDao = testDatabase.getTopicDao();
    }

    @After
    public void closeDb() throws IOException {
        // TODO: currently disabled as this interferes with other tests
        //testDatabase.close();
    }

    @Test
    public void insertWithoutID_getByName() throws InterruptedException {
        Topic topic = new Topic();
        topic.setTopicName("Test");
        topicDao.insert(topic);

        LiveData<Topic> null_topic = topicDao.getByName("Foo");
        assertEquals(LiveDataHelper.getValue(null_topic), null);

        LiveData<Topic> result_topic = topicDao.getByName("Test");
        assertEquals(LiveDataHelper.getValue(result_topic).getTopicName(), topic.getTopicName());
    }

    @Test
    public void insertWithoutID_getFinalByName() throws InterruptedException {
        Topic topic = new Topic();
        topic.setTopicName("Test");
        topicDao.insert(topic);

        Topic null_topic = topicDao.getFinalByName("Foo");
        assertEquals(null_topic, null);

        Topic result_topic = topicDao.getFinalByName("Test");
        assertEquals(result_topic.getTopicName(), topic.getTopicName());
    }

    @Test
    public void insertWithoutID_getTopics() throws InterruptedException {
        Topic topic = new Topic();
        topic.setTopicName("/tum");
        topicDao.insert(topic);

        Topic topic2 = new Topic();
        topic.setTopicName("/tum/garching");
        topicDao.insert(topic);

        LiveData<List<Topic>> topics = topicDao.getTopics();
        assertArrayEquals(LiveDataHelper.getValue(topics).stream().map(t -> t.getTopicName()).toArray(String[]::new), new String[]{"/tum", "/tum/garching"});
    }

    @Test
    public void insertWithNonExistingID() throws InterruptedException {
        Topic topic = new Topic();
        topic.setTopicName("Test");
        topic.setId(1);
        topicDao.insert(topic);

        LiveData<Topic> result_topic = topicDao.getTopic(1);
        assertEquals(LiveDataHelper.getValue(result_topic).getId(), 1);
    }

    @Test(expected = android.database.sqlite.SQLiteConstraintException.class)
    public void insertWithExistingID() throws InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(1, "/tum/garching");

        topicDao.insert(topic);
        topicDao.insert(topic2);

        // Should Not Run mainly for documentation
        LiveData<Topic> result_topic = topicDao.getTopic(1);
        assertEquals(LiveDataHelper.getValue(result_topic).getTopicName(), "/tum");
    }

    // Ensure the error message contains the field to allow inserting duplicate messages
    @Test
    public void insertDuplicate() {
        Topic topic = new Topic();
        topic.setTopicName("/tum");
        Topic topic2 = new Topic();
        topic2.setTopicName("/tum");
        boolean thrown = false;

        topicDao.insert(topic);
        try {
            topicDao.insert(topic2);
        } catch (SQLiteConstraintException e) {
            if (e.getMessage().contains("topics.topic_name")) {
                thrown = true;
            } else {
                throw e;
            }
        }
        assertEquals(thrown, true);
    }

    @Test
    public void insert_getByName_getById() throws InterruptedException {
        Topic topic = new Topic();
        topic.setTopicName("/tum");

        topicDao.insert(topic);

        Topic uuid_topic = topicDao.getFinalByName("/tum");
        long id = uuid_topic.getId();

        LiveData<Topic> id_topic = topicDao.getTopic(id);
        assertEquals(LiveDataHelper.getValue(id_topic).getTopicName(), topic.getTopicName());

    }

    @Test
    public void insert_getByName_getFinalById() {
        Topic topic = new Topic();
        topic.setTopicName("/tum");

        topicDao.insert(topic);

        Topic uuid_topic = topicDao.getFinalByName("/tum");
        long id = uuid_topic.getId();


        Topic id_topic = topicDao.getFinalTopic(id);
        assertEquals(id_topic.getTopicName(), topic.getTopicName());

    }
}
