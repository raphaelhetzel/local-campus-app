package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.testhelper.LiveDataHelper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PostDaoTest {
    private PostDao postDao;
    private TopicDao topicDao;
    private VoteDao voteDao;
    private AppDatabase testDatabase;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        postDao = testDatabase.getPostDao();
        topicDao = testDatabase.getTopicDao();
        voteDao = testDatabase.getVoteDao();
        topicDao.insert(new Topic(1, "/tum"));
    }

    @After
    public void closeDb() throws IOException {
        // TODO: currently disabled as this interferes with other tests
        //testDatabase.close();
    }

    @Test
    public void insertWithoutID_getByUUID_getPost() throws InterruptedException {

        String uuid = UUID.randomUUID().toString();
        Post post = new Post();
        post.setUuid(uuid);
        post.setData("\"Post\"");
        post.setTopicId(1);

        long row_id = postDao.insert(post);

        LiveData<Post> uuid_result = postDao.getPostByUUID(uuid);
        assertEquals(LiveDataHelper.getValue(uuid_result).getData(), post.getData());

        long id = LiveDataHelper.getValue(uuid_result).getId();
        LiveData<Post> id_result = postDao.getPost(id);
        assertEquals(LiveDataHelper.getValue(id_result).getData(), post.getData());

        assertEquals(row_id, id);

    }

    @Test
    public void insertWithoutID_getFinalByUUID() throws InterruptedException {

        String uuid = UUID.randomUUID().toString();
        Post post = new Post();
        post.setUuid(uuid);
        post.setData("\"Post\"");
        post.setTopicId(1);

        postDao.insert(post);

        Post null_result = postDao.getFinalPostByUUID("Foo");
        assertEquals(null, null_result);

        Post uuid_result = postDao.getFinalPostByUUID(uuid);
        assertEquals(uuid_result.getData(), post.getData());

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

    @Test
    public void postsContainScore() throws InterruptedException {

        Post post1 = new Post();
        post1.setUuid("UUID");
        post1.setData("Post1");
        post1.setTopicId(1);
        postDao.insert(post1);
        Post databasePost = postDao.getFinalPostByUUID("UUID");
        Date date = new Date();

        Vote vote1 = new Vote("UUID1", "PostUUID1", "User1", new Date(), +10);
        vote1.setPostId(1);
        Vote vote2 = new Vote("UUID2", "PostUUID1", "User2", new Date(), +10);
        vote1.setPostId(1);
        Vote vote3 = new Vote("UUID3", "PostUUID1", "User3", new Date(), -10);
        vote1.setPostId(1);

        voteDao.insert(vote1);
        voteDao.insert(vote2);
        voteDao.insert(vote3);


        LiveData<List<Post>> result = postDao.getPostsforTopic(1);
        Post topicsQueryPost = LiveDataHelper.getValue(result).get(0);
        assertEquals("Post1", topicsQueryPost.getData());
        ;
        assertEquals(10, topicsQueryPost.getScore());

    }

    @Test
    public void postsContainTopicName() throws InterruptedException {

        String uuid = UUID.randomUUID().toString();
        Post post = new Post();
        post.setUuid(uuid);
        post.setData("Data");
        post.setTopicId(1);

        postDao.insert(post);

        Post uuid_result = postDao.getFinalPostByUUID(uuid);
        assertEquals("/tum", uuid_result.getTopicName());

    }

    // Ensure the error message contains the field to allow inserting duplicate messages in the repository
    @Test()
    public void insertDuplicateRightErrorMessage() {
        boolean thrown = false;
        Post post1 = new Post();
        Post post2 = new Post();
        post1.setTopicId(1);
        post2.setTopicId(1);
        post1.setUuid("UUID");
        post2.setUuid("UUID");

        postDao.insert(post1);
        try {
            postDao.insert(post2);
        } catch (SQLiteConstraintException e) {
            if (e.getMessage().contains("posts.uuid")) {
                thrown = true;
            } else {
                throw e;
            }
        }
        assertEquals(thrown, true);
    }
}