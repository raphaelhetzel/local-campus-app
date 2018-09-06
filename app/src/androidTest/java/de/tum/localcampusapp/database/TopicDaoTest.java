package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
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
        testDatabase.close();
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
}
