package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PostExtensionDaoTest {

    private AppDatabase testDatabase;
    private TopicDao topicDao;
    private PostDao postDao;
    private PostExtensionDao postExtensionDao;

    @Before
    public void init() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        this.postDao = testDatabase.getPostDao();
        this.topicDao = testDatabase.getTopicDao();
        postExtensionDao = testDatabase.getPostExtensionDao();

        topicDao.insert(new Topic(1, "/foo"));
        Post post = new Post();
        post.setId(1);
        post.setTopicId(1);
        postDao.insert(post);
    }


    @After
    public void closeDb() throws IOException {
        // TODO: currently disabled as this interferes with other tests
        //testDatabase.close();
    }

    @Test
    public void insert_getPostExtensionsByPostId() throws InterruptedException {
        PostExtension postExtension = new PostExtension("Ext-UUID", "Post-UUID", "Creator", new Date(), "Data");
        postExtension.setPostId(1);

        PostExtension postExtension2 = new PostExtension("Ext-UUID2", "Post-UUID", "Creator", new Date(), "Data");
        postExtension2.setPostId(1);

        postExtensionDao.insert(postExtension);
        postExtensionDao.insert(postExtension2);

        LiveData<List<PostExtension>> result = postExtensionDao.getPostExtensionsByPostId(1);
        assertEquals(2, LiveDataHelper.getValue(result).size());
        assertEquals("Creator", LiveDataHelper.getValue(result).get(0).getCreatorId());
        assertEquals("Data", LiveDataHelper.getValue(result).get(0).getData());
    }

    @Test
    public void insert_getPostExtensionsByPostUUID() throws InterruptedException {
        PostExtension postExtension = new PostExtension("Ext-UUID", "Post-UUID", "Creator", new Date(), "Data");

        postExtensionDao.insert(postExtension);

        List<PostExtension> result = postExtensionDao.getFinalPostExtensionsByPostUUID("Post-UUID");
        assertEquals(1, result.size());
        assertEquals("Creator", result.get(0).getCreatorId());
        assertEquals("Data", result.get(0).getData());
    }

    @Test
    public void insert_getPostExtensionsByPostId_liveUpdate() throws InterruptedException {
        PostExtension postExtension = new PostExtension("Ext-UUID", "Post-UUID", "Creator", new Date(), "Data");

        postExtensionDao.insert(postExtension);
        PostExtension insertedPostExtension = postExtensionDao.getFinalPostExtensionsByPostUUID("Post-UUID").get(0);

        LiveData<List<PostExtension>> result = postExtensionDao.getPostExtensionsByPostId(1);
        assertEquals(0, LiveDataHelper.getValue(result).size());

        insertedPostExtension.setPostId(1);
        postExtensionDao.update(insertedPostExtension);

        sleep(100);

        assertEquals(1, LiveDataHelper.getValue(result).size());

    }

    // Ensure the error message contains the field to allow inserting duplicate messages in the repository
    @Test()
    public void insertDuplicateRightErrorMessage() {
        boolean thrown = false;
        PostExtension postExtension = new PostExtension("Ext-UUID", "Post-UUID", "Creator", new Date(), "Data");
        PostExtension postExtension2 = new PostExtension("Ext-UUID", "Post-UUID", "Creator", new Date(), "Data2");

        postExtensionDao.insert(postExtension);
        try {
            postExtensionDao.insert(postExtension2);
        } catch (SQLiteConstraintException e) {
            if (e.getMessage().contains("post_extensions.uuid")) {
                thrown = true;
            } else {
                throw e;
            }
        }
        assertEquals(thrown, true);
    }
}