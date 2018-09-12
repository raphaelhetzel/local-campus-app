package de.tum.localcampusapp.database;

import android.arch.persistence.room.Room;
import android.content.Context;
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
    public void inset_getScore() {

        voteDao.insert(new Vote("UUID1", 1, "User1", new Date(), +10));
        voteDao.insert(new Vote("UUID2", 1, "User2", new Date(), +10));
        voteDao.insert(new Vote("UUID3", 1, "User3", new Date(), -10));

        assertEquals(10, voteDao.getScore(1));

        assertEquals(0, voteDao.getScore(2));
    }

    @Test
    public void inset_getUserVote() {

        voteDao.insert(new Vote("UUID1", 1, "User1", new Date(), +10));

        assertEquals("UUID1", voteDao.getUserVote(1, "User1").getUuid());

        assertEquals(null, voteDao.getUserVote(2, "User2"));
    }

}