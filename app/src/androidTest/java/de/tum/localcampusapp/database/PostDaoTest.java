package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PostDaoTest {
    private PostDao postDao;
    private TopicDao topicDao;
    private AppDatabase testDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        postDao = testDatabase.getPostDao();
        topicDao = testDatabase.getTopicDao();
        topicDao.insert(new Topic(1, "/tum"));
    }

    @After
    public void closeDb() throws IOException {
        testDatabase.close();
    }

    @Test
    public void insertWithoutID_getByUUID_getPost() throws InterruptedException {

        String uuid = UUID.randomUUID().toString();
        Post post = new Post();
        post.setUuid(uuid);
        post.setData("\"Post\"");
        post.setTopicId(1);

        postDao.insert(post);

        LiveData<Post> uuid_result = postDao.getPostByUUID(uuid);
        assertEquals(LiveDataHelper.getValue(uuid_result).getData(), post.getData());

        long id = LiveDataHelper.getValue(uuid_result).getId();
        LiveData<Post> id_result = postDao.getPost(id);
        assertEquals(LiveDataHelper.getValue(uuid_result).getData(), post.getData());

    }

    @Test
    public void getPostsforTopic() throws InterruptedException {
        topicDao.insert(new Topic(2, "/tum/garching"));

        Post post1 = new Post();
        post1.setUuid(UUID.randomUUID().toString());
        post1.setData("Post1");
        post1.setTopicId(1);
        postDao.insert(post1);

        Post post2 = new Post();
        post2.setUuid(UUID.randomUUID().toString());
        post2.setData("Post2");
        post2.setTopicId(1);
        postDao.insert(post2);

        Post post3 = new Post();
        post3.setUuid(UUID.randomUUID().toString());
        post3.setData("Post3");
        post3.setTopicId(2);
        postDao.insert(post3);

        LiveData<List<Post>> result = postDao.getPostsforTopic(1);
        assertArrayEquals(LiveDataHelper.getValue(result).stream().map(p -> p.getData()).toArray(String[]::new), new String[]{"Post1", "Post2"});
    }


    @Test
    public void updatePost() throws InterruptedException {


        String uuid = UUID.randomUUID().toString();
        Post post = new Post();
        post.setUuid(uuid);
        post.setData("Post");
        post.setTopicId(1);

        postDao.insert(post);

        LiveData<Post> uuid_result = postDao.getPostByUUID(uuid);
        Post result_post = LiveDataHelper.getValue(uuid_result);
        assertEquals(result_post.getData(), "Post");

        result_post.setData("Modified");

        LiveData<Post> uuid_result2 = postDao.getPostByUUID(uuid);
        assertEquals(LiveDataHelper.getValue(uuid_result2), "Modified");
    }

    @Test
    public void insertWithNonExistingID() throws InterruptedException {
        Post post = new Post();
        post.setId(1);
        post.setTopicId(1);
        post.setData("Test");
        postDao.insert(post);


        LiveData<Post> result_post = postDao.getPost(1);
        assertEquals(LiveDataHelper.getValue(result_post).getData(), "Test");
    }

    @Test(expected = android.database.sqlite.SQLiteConstraintException.class)
    public void insertWithExistingID() throws InterruptedException {
        Post post1 = new Post();
        post1.setId(1);
        post1.setTopicId(1);
        post1.setData("Test");

        Post post2 = new Post();
        post2.setId(1);
        post2.setTopicId(1);
        post2.setData("Test2");

        postDao.insert(post1);
        postDao.insert(post2); //Fails

        //Won't run
        LiveData<Post> result_post = postDao.getPost(1);
        assertEquals(LiveDataHelper.getValue(result_post).getData(), "Test");
    }
}