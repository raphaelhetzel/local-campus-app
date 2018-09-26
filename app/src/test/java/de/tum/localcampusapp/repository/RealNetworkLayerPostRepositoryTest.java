package de.tum.localcampusapp.repository;
import android.database.sqlite.SQLiteConstraintException;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.database.PostExtensionDao;
import de.tum.localcampusapp.database.VoteDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RealNetworkLayerPostRepositoryTest {

    PostDao mPostDao;
    VoteDao mVoteDao;
    PostExtensionDao mPostExtensionDao;
    SQLiteConstraintException mSqLiteConstraintException;

    TopicRepository mTopicRepository;

    RealNetworkLayerPostRepository realNetworkLayerPostRepository;

    @Before
    public void initialize_mocks() {

        mPostDao = mock(PostDao.class);
        mVoteDao = mock(VoteDao.class);
        mPostExtensionDao = mock(PostExtensionDao.class);

        mTopicRepository = mock(TopicRepository.class);
        mSqLiteConstraintException = mock(SQLiteConstraintException.class);

        realNetworkLayerPostRepository = new RealNetworkLayerPostRepository(
                mTopicRepository,
                mPostDao,
                mVoteDao,
                mPostExtensionDao
        );
    }


    @Test
    public void insertPost() throws DatabaseException, MissingRelatedDataException {
        Topic topic = new Topic(1, "/tum");
        Post post2 = new Post();
        post2.setTopicName("/tum");
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);

        realNetworkLayerPostRepository.insertPost(post2);
        verify(mPostDao).insert(any(Post.class));
    }


    @Test(expected = DatabaseException.class)
    public void insertPostWithDuplicateId() throws DatabaseException, MissingRelatedDataException {
        Topic topic = new Topic(1, "/tum");
        Post post2 = new Post();
        post2.setTopicName("/tum");
        when(mSqLiteConstraintException.getMessage()).thenReturn("primary key");
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);
        doThrow(mSqLiteConstraintException).when(mPostDao).insert(post2);

        realNetworkLayerPostRepository.insertPost(post2);
    }

    @Test(expected = MissingRelatedDataException.class)
    public void insertPostWithMissingTopic() throws DatabaseException, MissingRelatedDataException {
        Post post2 = new Post();
        post2.setTopicName("/tum");

        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(null);

        realNetworkLayerPostRepository.insertPost(post2);

        verify(mPostDao, never()).insert(any(Post.class));
    }

    @Test
    public void insertDuplicatePost() throws DatabaseException, MissingRelatedDataException {
        Topic topic = new Topic(1, "/tum");
        Post post2 = new Post();
        post2.setTopicName("/tum");
        when(mSqLiteConstraintException.getMessage()).thenReturn("posts.uuid");
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);
        doThrow(mSqLiteConstraintException).when(mPostDao).insert(post2);

        realNetworkLayerPostRepository.insertPost(post2);

        verify(mPostDao).insert(post2);
    }

    @Test
    public void insertVoteWithDuplicate() throws DatabaseException {

        Vote vote = new Vote();
        vote.setUuid("UUID");
        vote.setScoreInfluence(-1);
        Vote duplicateVote = new Vote();
        duplicateVote.setUuid("UUID");
        duplicateVote.setScoreInfluence(1);
        doThrow(mSqLiteConstraintException).when(mVoteDao).insert(duplicateVote);
        when(mSqLiteConstraintException.getMessage()).thenReturn("votes.uuid");

        realNetworkLayerPostRepository.insertVote(vote);
        realNetworkLayerPostRepository.insertVote(duplicateVote);

        verify(mVoteDao).insert(vote);
        verify(mVoteDao).insert(duplicateVote);
    }

    @Test
    public void insertVoteWithoutExistingPost() throws DatabaseException {

        Vote vote = new Vote();
        vote.setUuid("UUID");
        vote.setPostUuid("UUID2");
        vote.setScoreInfluence(-1);

        when(mPostDao.getFinalPostByUUID("UUID2")).thenReturn(null);

        realNetworkLayerPostRepository.insertVote(vote);

        assertEquals(0L, vote.getPostId());
        verify(mVoteDao).insert(vote);
    }

    @Test
    public void insertVoteWithExistingPost() throws DatabaseException {

        Vote vote = new Vote();
        vote.setUuid("UUID");
        vote.setPostUuid("UUID2");
        vote.setScoreInfluence(-1);

        Post post = new Post();
        post.setUuid("UUID2");
        post.setId(1);

        when(mPostDao.getFinalPostByUUID("UUID2")).thenReturn(post);

        realNetworkLayerPostRepository.insertVote(vote);
        assertEquals(1L, vote.getPostId());
        verify(mVoteDao).insert(vote);
    }

    @Test
    public void insertExistingUserVote() throws DatabaseException {

        Vote vote = new Vote();
        vote.setUuid("UUID");
        vote.setPostUuid("UUID2");
        vote.setCreatorId("UUID3");
        vote.setScoreInfluence(-1);

        when(mVoteDao.getUserVoteByUUID("UUID2", "UUID3")).thenReturn(new Vote());

        realNetworkLayerPostRepository.insertVote(vote);
        assertEquals(0L, vote.getPostId());
        verify(mVoteDao).getUserVoteByUUID("UUID2", "UUID3");
        verify(mVoteDao, never()).insert(vote);
    }

    @Test
    public void updatesVotesOnPostInsert() throws MissingRelatedDataException, DatabaseException {
        Topic topic = new Topic(1, "/foo");

        Vote vote = mock(Vote.class);

        List<Vote> votes = new ArrayList<>();
        votes.add(vote);
        when(mVoteDao.getVotesByPostUUID("UUID2")).thenReturn(votes);
        when(mPostDao.insert(any(Post.class))).thenReturn(1L);
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);

        Post post = new Post();
        post.setId(1);
        post.setUuid("UUID2");
        post.setTopicName("/tum");


        realNetworkLayerPostRepository.insertPost(post);

        verify(mVoteDao).getVotesByPostUUID("UUID2");
        verify(vote).setPostId(1);
    }

    @Test
    public void insertPostExtensionRelatedPost() throws DatabaseException {

        Post post = new Post();
        post.setId(1);
        post.setUuid("UUID-Post");

        PostExtension testPostExtension = new PostExtension("UUID", "UUID-Post", "Creator", new Date(), "Data");

        when(mPostDao.getFinalPostByUUID("UUID-Post")).thenReturn(post);

        realNetworkLayerPostRepository.insertPostExtension(testPostExtension);


        verify(mPostDao).getFinalPostByUUID("UUID-Post");
        verify(mPostExtensionDao).insert(argThat(argument -> {
            return argument.getClass() == PostExtension.class &&
                    argument.getPostId() == 1 &&
                    argument.getUuid().equals("UUID");
        }));

    }

    @Test
    public void insertPostExtensionNoRelatedPost() throws DatabaseException {

        Date date = new Date();

        PostExtension testPostExtension = new PostExtension("UUID", "UUID-Post", "Creator", new Date(), "Data");

        when(mPostDao.getFinalPostByUUID("UUID-Post")).thenReturn(null);

        realNetworkLayerPostRepository.insertPostExtension(testPostExtension);

        verify(mPostDao).getFinalPostByUUID("UUID-Post");
        verify(mPostExtensionDao).insert(argThat(argument -> {
            return argument.getClass() == PostExtension.class &&
                    argument.getPostId() == 0 &&
                    argument.getUuid().equals("UUID");
        }));

    }

    @Test
    public void insertDuplicatePostExtensionIgnored() throws DatabaseException {

        Date date = new Date();

        PostExtension testPostExtension = new PostExtension("UUID", "UUID-Post", "Creator", new Date(), "Data");

        when(mPostDao.getFinalPostByUUID("UUID-Post")).thenReturn(null);

        when(mSqLiteConstraintException.getMessage()).thenReturn("post_extensions.uuid");
        doThrow(mSqLiteConstraintException).when(mPostExtensionDao).insert(any(PostExtension.class));

        realNetworkLayerPostRepository.insertPostExtension(testPostExtension);

        verify(mPostDao).getFinalPostByUUID("UUID-Post");
        verify(mPostExtensionDao).insert(any());

    }
}
