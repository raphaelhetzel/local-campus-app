package de.tum.localcampusapp.repository;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteConstraintException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;


import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.database.PostExtensionDao;
import de.tum.localcampusapp.database.VoteDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.exception.MissingRelatedDataException;
import de.tum.localcampusapp.serializer.ScampiPostExtensionSerializer;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.serializer.ScampiVoteSerializer;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.localcampusapp.testhelper.ExecutorInstantRun;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RealPostRepositoryTest {

    Context mContext;
    Executor mExecutor;

    PostDao mPostDao;
    VoteDao mVoteDao;
    PostExtensionDao mPostExtensionDao;
    SQLiteConstraintException mSqLiteConstraintException;

    TopicRepository mTopicRepository;
    UserRepository mUserRepository;

    AppLibService.ScampiBinder mScampiBinder;
    ComponentName mComponentName;

    ScampiPostSerializer mScampiPostSerializer;
    ScampiVoteSerializer mScampiVoteSerializer;
    ScampiPostExtensionSerializer mScampiPostExtensionSerializer;

    RealPostRepository realPostRepository;

    @Before
    public void initialize_mocks() {
        mContext = mock(Context.class);
        mExecutor = ExecutorInstantRun.getMockExecutor();

        mPostDao = mock(PostDao.class);
        mVoteDao = mock(VoteDao.class);
        mPostExtensionDao = mock(PostExtensionDao.class);

        mTopicRepository = mock(TopicRepository.class);
        mUserRepository = mock(UserRepository.class);
        mSqLiteConstraintException = mock(SQLiteConstraintException.class);

        mScampiBinder = mock(AppLibService.ScampiBinder.class);
        mComponentName = mock(ComponentName.class);

        mScampiPostSerializer = mock(ScampiPostSerializer.class);
        mScampiVoteSerializer = mock(ScampiVoteSerializer.class);
        mScampiPostExtensionSerializer = mock(ScampiPostExtensionSerializer.class);

        when(mUserRepository.getId()).thenReturn("MOCKUSER");

        when(mContext.bindService(any(Intent.class), any(ServiceConnection.class), anyInt())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ServiceConnection serviceConnection = invocation.getArgument(1);
                serviceConnection.onServiceConnected(mComponentName, mScampiBinder);
                return null;
            }
        });

        realPostRepository = new RealPostRepository(mContext,
                mPostDao,
                mVoteDao,
                mPostExtensionDao,
                mTopicRepository,
                mUserRepository,
                mScampiPostSerializer,
                mScampiVoteSerializer,
                mScampiPostExtensionSerializer,
                mExecutor);
    }

    @Test
    public void insertPost() throws DatabaseException, MissingRelatedDataException {
        Topic topic = new Topic(1, "/tum");
        Post post2 = new Post();
        post2.setTopicName("/tum");
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);

        realPostRepository.insertPost(post2);
        verify(mPostDao).insert(any(Post.class));
    }


    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void insertPostWithDuplicateId() throws DatabaseException, MissingRelatedDataException {
        Topic topic = new Topic(1, "/tum");
        Post post2 = new Post();
        post2.setTopicName("/tum");
        when(mSqLiteConstraintException.getMessage()).thenReturn("primary key");
        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(topic);
        doThrow(mSqLiteConstraintException).when(mPostDao).insert(post2);

        realPostRepository.insertPost(post2);
    }

    @Test(expected = MissingRelatedDataException.class)
    public void insertPostWithMissingTopic() throws DatabaseException, MissingRelatedDataException {
        Post post2 = new Post();
        post2.setTopicName("/tum");

        when(mTopicRepository.getFinalTopicByName("/tum")).thenReturn(null);

        realPostRepository.insertPost(post2);

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

        realPostRepository.insertPost(post2);

        verify(mPostDao).insert(post2);
    }

    @Test
    public void addPost() throws DatabaseException, InterruptedException, MissingFieldsException {
        Post post = new Post(
                1,
                "Type",
                "DATA"
        );

        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();
        when(mScampiPostSerializer.messageFromPost(post)).thenReturn(scampiMessage);
        when(mTopicRepository.getFinalTopic(1)).thenReturn(new Topic(1, "Topic"));

        realPostRepository.addPost(post);

        verify(mTopicRepository).getFinalTopic(1);
        verify(mScampiPostSerializer).messageFromPost(argThat(argument -> {
            return argument.getClass() == Post.class &&
                    argument.getTypeId().equals("Type") &&
                    argument.getData().equals("DATA") &&
                    argument.getCreator().equals("MOCKUSER") &&
                    argument.getCreatedAt() != null &&
                    argument.getTopicName().equals("Topic");

        }));
        verify(mScampiBinder).publish(eq(scampiMessage), eq("Topic"));
    }

    @Test
    public void addPostNoTopic() throws DatabaseException, InterruptedException, MissingFieldsException {
        Post post = new Post(
                1,
                "Type",
                "DATA"
        );
        when(mTopicRepository.getFinalTopic(1)).thenReturn(null);

        realPostRepository.addPost(post);

        verify(mTopicRepository).getFinalTopic(1);
        verify(mScampiBinder, never()).publish(any(), any());
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

        realPostRepository.insertVote(vote);
        realPostRepository.insertVote(duplicateVote);

        verify(mVoteDao).insert(vote);
        verify(mVoteDao).insert(duplicateVote);
    }

    @Test
    public void upVote() throws MissingFieldsException, InterruptedException {
        Post post = new Post(
                1,
                "1",
                "DATA"
        );

        Topic topic = new Topic(1, "/tum");
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();

        when(mPostDao.getFinalPost(1)).thenReturn(post);
        when(mTopicRepository.getFinalTopic(1)).thenReturn(topic);
        when(mScampiVoteSerializer.voteToMessage(any(Vote.class))).thenReturn(scampiMessage);

        realPostRepository.upVote(1);
        verify(mScampiVoteSerializer).voteToMessage(any(Vote.class));
        verify(mScampiBinder).publish(eq(scampiMessage), eq("/tum"));
        verify(mTopicRepository).getFinalTopic(1);
        verify(mPostDao).getFinalPost(1);
    }


    @Test
    public void duplicateVoteDB() throws MissingFieldsException, InterruptedException {
        Post post = new Post(
                1,
                "1",
                "DATA"
        );

        Topic topic = new Topic(1, "/tum");
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();

        when(mPostDao.getFinalPost(1)).thenReturn(post);
        when(mVoteDao.getUserVote(1, "MOCKUSER")).thenReturn(new Vote());

        realPostRepository.upVote(1);
        verify(mScampiBinder, never()).publish(any(), eq("/tum"));
    }

    @Test
    public void duplicateVoteBuffer() throws MissingFieldsException, InterruptedException {
        Post post = new Post(
                1,
                "1",
                "DATA"
        );

        Topic topic = new Topic(1, "/tum");
        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();

        when(mPostDao.getFinalPost(1)).thenReturn(post);
        when(mTopicRepository.getFinalTopic(1)).thenReturn(topic);
        when(mScampiVoteSerializer.voteToMessage(any(Vote.class))).thenReturn(scampiMessage);

        realPostRepository.upVote(1);
        realPostRepository.upVote(2);
        verify(mScampiBinder, times(1)).publish(any(), eq("/tum"));
    }

    @Test
    public void insertVoteWithoutExistingPost() {

        Vote vote = new Vote();
        vote.setUuid("UUID");
        vote.setPostUuid("UUID2");
        vote.setScoreInfluence(-1);

        when(mPostDao.getFinalPostByUUID("UUID2")).thenReturn(null);

        realPostRepository.insertVote(vote);
        assertEquals(0L, vote.getPostId());
        verify(mVoteDao).insert(vote);
    }

    @Test
    public void insertVoteWithExistingPost() {

        Vote vote = new Vote();
        vote.setUuid("UUID");
        vote.setPostUuid("UUID2");
        vote.setScoreInfluence(-1);

        Post post = new Post();
        post.setUuid("UUID2");
        post.setId(1);

        when(mPostDao.getFinalPostByUUID("UUID2")).thenReturn(post);

        realPostRepository.insertVote(vote);
        assertEquals(1L, vote.getPostId());
        verify(mVoteDao).insert(vote);
    }

    @Test
    public void insertExistingUserVote() {

        Vote vote = new Vote();
        vote.setUuid("UUID");
        vote.setPostUuid("UUID2");
        vote.setCreatorId("UUID3");
        vote.setScoreInfluence(-1);

        when(mVoteDao.getUserVoteByUUID("UUID2", "UUID3")).thenReturn(new Vote());

        realPostRepository.insertVote(vote);
        assertEquals(0L, vote.getPostId());
        verify(mVoteDao).getUserVoteByUUID("UUID2", "UUID3");
        verify(mVoteDao, never()).insert(vote);
    }

    @Test
    public void updatesVotesOnPostInsert() throws MissingRelatedDataException {
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


        realPostRepository.insertPost(post);

        verify(mVoteDao).getVotesByPostUUID("UUID2");
        verify(vote).setPostId(1);
    }

    @Test
    public void addPostExtension() throws MissingFieldsException, InterruptedException {

        PostExtension testPostExtension = new PostExtension(1, "TestData");

        Post post = new Post();
        post.setId(1);
        post.setUuid("PostUUID");
        post.setTopicId(1);

        Topic topic = new Topic(1, "/foo");

        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();

        when(mPostDao.getFinalPost(1)).thenReturn(post);
        when(mTopicRepository.getFinalTopic(1)).thenReturn(topic);
        when(mScampiPostExtensionSerializer.postExtensionToMessage(any(PostExtension.class))).thenReturn(scampiMessage);

        realPostRepository.addPostExtension(testPostExtension);

        verify(mPostDao).getFinalPost(1);
        verify(mTopicRepository).getFinalTopic(1);
        verify(mScampiPostExtensionSerializer).postExtensionToMessage(argThat(argument -> {
            return argument.getClass() == PostExtension.class &&
                    argument.getPostUuid().equals("PostUUID") &&
                    argument.getCreatorId().equals("MOCKUSER");
        }));

        verify(mScampiBinder).publish(scampiMessage, "/foo");

    }

    @Test
    public void addPostExtensionMissingPost() throws MissingFieldsException, InterruptedException {

        PostExtension testPostExtension = new PostExtension(1, "TestData");

        when(mPostDao.getFinalPost(1)).thenReturn(null);

        realPostRepository.addPostExtension(testPostExtension);

        verify(mPostDao).getFinalPost(1);
        verify(mTopicRepository, never()).getFinalTopic(1);
        verify(mScampiPostExtensionSerializer, never()).postExtensionToMessage(any());
        verify(mScampiBinder, never()).publish(any(), any());

    }

    @Test
    public void insertPostExtensionRelatedPost() {

        Post post = new Post();
        post.setId(1);
        post.setUuid("UUID-Post");

        PostExtension testPostExtension = new PostExtension("UUID", "UUID-Post", "Creator", new Date(), "Data");

        when(mPostDao.getFinalPostByUUID("UUID-Post")).thenReturn(post);

        realPostRepository.insertPostExtension(testPostExtension);


        verify(mPostDao).getFinalPostByUUID("UUID-Post");
        verify(mPostExtensionDao).insert(argThat(argument -> {
            return argument.getClass() == PostExtension.class &&
                    argument.getPostId() == 1 &&
                    argument.getUuid().equals("UUID");
        }));

    }

    @Test
    public void insertPostExtensionNoRelatedPost() {

        Date date = new Date();

        PostExtension testPostExtension = new PostExtension("UUID", "UUID-Post", "Creator", new Date(), "Data");

        when(mPostDao.getFinalPostByUUID("UUID-Post")).thenReturn(null);

        realPostRepository.insertPostExtension(testPostExtension);

        verify(mPostDao).getFinalPostByUUID("UUID-Post");
        verify(mPostExtensionDao).insert(argThat(argument -> {
            return argument.getClass() == PostExtension.class &&
                    argument.getPostId() == 0 &&
                    argument.getUuid().equals("UUID");
        }));

    }

    @Test
    public void insertDuplicatePostExtensionIgnored() {

        Date date = new Date();

        PostExtension testPostExtension = new PostExtension("UUID", "UUID-Post", "Creator", new Date(), "Data");

        when(mPostDao.getFinalPostByUUID("UUID-Post")).thenReturn(null);

        when(mSqLiteConstraintException.getMessage()).thenReturn("post_extensions.uuid");
        doThrow(mSqLiteConstraintException).when(mPostExtensionDao).insert(any(PostExtension.class));

        realPostRepository.insertPostExtension(testPostExtension);

        verify(mPostDao).getFinalPostByUUID("UUID-Post");
        verify(mPostExtensionDao).insert(any());

    }

}
