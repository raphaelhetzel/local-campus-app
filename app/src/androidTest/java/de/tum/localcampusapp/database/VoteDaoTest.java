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
import java.util.Date;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class VoteDaoTest {

    private TopicDao topicDao;
    private PostDao postDao;
    private VoteDao voteDao;
    private AppDatabase testDatabase;

    @Before
    public void init() {
        Context context = InstrumentationRegistry.getTargetContext();
        testDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        this.postDao = testDatabase.getPostDao();
        this.topicDao = testDatabase.getTopicDao();
        voteDao = testDatabase.getVoteDao();

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
    public void insert_getScore() {
        Vote vote1 = new Vote("UUID1", "PostUUID1", "User1", new Date(), +10);
        vote1.setPostId(1);
        Vote vote2 = new Vote("UUID2", "PostUUID1", "User2", new Date(), +10);
        vote1.setPostId(1);
        Vote vote3 = new Vote("UUID3", "PostUUID1", "User3", new Date(), -10);
        vote1.setPostId(1);

        voteDao.insert(vote1);
        voteDao.insert(vote2);
        voteDao.insert(vote3);

        assertEquals(10, voteDao.getScore(1));
        assertEquals(0, voteDao.getScore(2));
    }

    @Test
    public void insert_getUserVote() {

        Vote vote1 = new Vote("UUID1", "PostUUID1", "User1", new Date(), +10);
        vote1.setPostId(1);

        voteDao.insert(vote1);

        assertEquals("UUID1", voteDao.getUserVote(1, "User1").getUuid());
        assertEquals(null, voteDao.getUserVote(2, "User2"));
    }

    @Test
    public void inset_getUserVoteByUUID() {

        Vote vote1 = new Vote("UUID1", "PostUUID1", "User1", new Date(), +10);
        vote1.setPostId(1);

        voteDao.insert(vote1);

        assertEquals("UUID1", voteDao.getUserVoteByUUID("PostUUID1", "User1").getUuid());
        assertEquals(null, voteDao.getUserVoteByUUID("PostUUID2", "User2"));
    }

    @Test
    public void insertEmptyPostId_GetPostsByUUID() {

        Vote vote1 = new Vote("UUID1", "PostUUID1", "User1", new Date(), +10);
        voteDao.insert(vote1);


        assertEquals("UUID1", voteDao.getVotesByPostUUID("PostUUID1").get(0).getUuid());
        assertEquals(0, voteDao.getVotesByPostUUID("PostUUID1").get(0).getPostId());
        assertEquals(0, voteDao.getVotesByPostUUID("PostUUID2").size());
    }

    // Ensure the error message contains the field to allow inserting duplicate messages in the repository
    @Test()
    public void insertDuplicateRightErrorMessage() {
        boolean thrown = false;
        Vote vote1 = new Vote("UUID1", "PostUUID1", "User1", new Date(), +10);
        Vote vote2 = new Vote("UUID1", "PostUUID1", "User1", new Date(), -10);

        voteDao.insert(vote1);
        try {
            voteDao.insert(vote2);
        } catch (SQLiteConstraintException e) {
            if (e.getMessage().contains("votes.uuid")) {
                thrown = true;
            } else {
                throw e;
            }
        }
        assertEquals(thrown, true);
    }

}