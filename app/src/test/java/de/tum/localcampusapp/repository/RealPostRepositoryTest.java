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
    public void addPost() throws InterruptedException, MissingFieldsException {
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
    public void addPostNoTopic() throws InterruptedException {
        Post post = new Post(
                1,
                "Type",
                "DATA"
        );
        when(mTopicRepository.getFinalTopic(1)).thenReturn(null);

        // Ignore Log.d not mocked error as mocking Log.d directly would require PowerMock / Robolectric
        try {
            realPostRepository.addPost(post);
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("android.util.Log not mocked")) throw e;
        }

        verify(mTopicRepository).getFinalTopic(1);
        verify(mScampiBinder, never()).publish(any(), any());
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
    public void duplicateVoteDB() throws InterruptedException {
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
        realPostRepository.upVote(1);
        verify(mScampiBinder, times(1)).publish(any(), eq("/tum"));
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

        // Ignore Log.d not mocked error as mocking Log.d directly would require PowerMock / Robolectric
        try {
            realPostRepository.addPostExtension(testPostExtension);
        } catch (RuntimeException e) {
            if (!e.getMessage().contains("android.util.Log not mocked")) throw e;
        }

        verify(mPostDao).getFinalPost(1);
        verify(mTopicRepository, never()).getFinalTopic(1);
        verify(mScampiPostExtensionSerializer, never()).postExtensionToMessage(any());
        verify(mScampiBinder, never()).publish(any(), any());

    }

}
