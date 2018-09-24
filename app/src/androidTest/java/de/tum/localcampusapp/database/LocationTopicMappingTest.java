package de.tum.localcampusapp.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.LocationTopicMapping;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LocationTopicMappingTest {

    private AppDatabase testDatabase;
    private TopicDao topicDao;
    private LocationTopicMappingDao locationTopicMappingDao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        topicDao = testDatabase.getTopicDao();
        locationTopicMappingDao = testDatabase.getLocationTopicMappingDao();
    }

    @After
    public void closeDb() throws IOException {
        // TODO: currently disabled as this interferes with other tests
        //testDatabase.close();
    }

    @Test
    public void insert_GetFinalTopicsForLocation() {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/room1");
        Topic topic3 = new Topic(3, "/tum/room2");
        String room1Location = "room1";
        String room2Location = "room2";

        topicDao.insert(topic);
        topicDao.insert(topic2);
        topicDao.insert(topic3);

        locationTopicMappingDao.insert(new LocationTopicMapping(1, room1Location));
        locationTopicMappingDao.insert(new LocationTopicMapping(2, room1Location));

        locationTopicMappingDao.insert(new LocationTopicMapping(1, room2Location));
        locationTopicMappingDao.insert(new LocationTopicMapping(3, room2Location));

        assertArrayEquals(topicDao.getFinalTopicsForLocation("room1").stream().map(t -> t.getTopicName()).collect(Collectors.toList()).toArray(), new String[]{"/tum", "/tum/room1"});
        assertArrayEquals(topicDao.getFinalTopicsForLocation("room2").stream().map(t -> t.getTopicName()).collect(Collectors.toList()).toArray(), new String[]{"/tum", "/tum/room2"});
    }

    @Test
    public void insertDuplicate() {
        boolean thrown = false;
        Topic topic = new Topic(1, "/tum");
        topicDao.insert(topic);
        locationTopicMappingDao.insert(new LocationTopicMapping(1L, "2"));
        try {
            locationTopicMappingDao.insert(new LocationTopicMapping(1L, "2"));
        } catch (SQLiteConstraintException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                thrown = true;
            } else {
                throw e;
            }
        }
        assertEquals(thrown, true);
    }

    @Test
    public void insert_GetTopicsForLocation() throws InterruptedException {
        Topic topic = new Topic(1, "/tum");
        Topic topic2 = new Topic(2, "/tum/room1");
        Topic topic3 = new Topic(3, "/tum/room2");
        String room1Location = "room1";
        String room2Location = "room2";

        topicDao.insert(topic);
        topicDao.insert(topic2);
        topicDao.insert(topic3);

        locationTopicMappingDao.insert(new LocationTopicMapping(1, room1Location));
        locationTopicMappingDao.insert(new LocationTopicMapping(2, room1Location));

        locationTopicMappingDao.insert(new LocationTopicMapping(1, room2Location));
        locationTopicMappingDao.insert(new LocationTopicMapping(3, room2Location));

        assertArrayEquals(LiveDataHelper.getValue(topicDao.getTopicsForLocation("room1")).stream().map(t -> t.getTopicName()).collect(Collectors.toList()).toArray(), new String[]{"/tum", "/tum/room1"});
        assertArrayEquals(LiveDataHelper.getValue(topicDao.getTopicsForLocation("room2")).stream().map(t -> t.getTopicName()).collect(Collectors.toList()).toArray(), new String[]{"/tum", "/tum/room2"});
    }
}
