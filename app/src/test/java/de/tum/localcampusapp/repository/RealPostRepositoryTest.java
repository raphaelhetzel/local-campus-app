package de.tum.localcampusapp.repository;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteConstraintException;
import android.os.IBinder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executor;

import de.tum.localcampusapp.database.Converters;
import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.database.VoteDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.localcampusapp.testhelper.ExecutorInstantRun;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RealPostRepositoryTest {
    PostDao mPostDao;
    VoteDao mVoteDao;
    Context mContext;
    TopicRepository mTopicRepository;
    AppLibService.ScampiBinder mScampiBinder;
    ComponentName mComponentName;
    Executor mExecutor;
    ScampiPostSerializer mScampiPostSerializer;
    SQLiteConstraintException mSqLiteConstraintException;

    @Before
    public void initialize_mocks() {
        mPostDao = mock(PostDao.class);
        mVoteDao = mock(VoteDao.class);
        mContext = mock(Context.class);
        mTopicRepository = mock(TopicRepository.class);
        mScampiBinder = mock(AppLibService.ScampiBinder.class);
        mComponentName = mock(ComponentName.class);
        mExecutor = ExecutorInstantRun.getMockExecutor();
        mScampiPostSerializer = mock(ScampiPostSerializer.class);
        mSqLiteConstraintException = mock(SQLiteConstraintException.class);
    }

    @Test(expected = de.tum.localcampusapp.exception.DatabaseException.class)
    public void insertWithDuplicateIdOrNonExistantTopic() throws DatabaseException {
        RealPostRepository realPostRepository = new RealPostRepository(mContext, mPostDao, mTopicRepository, mExecutor, mScampiPostSerializer, mVoteDao);
        Post post2 = new Post();
        doThrow(new android.database.sqlite.SQLiteConstraintException()).when(mPostDao).insert(post2);
        realPostRepository.insertPost(post2);
    }

    @Test
    public void add() throws DatabaseException, InterruptedException {
        Post post = new Post(
                1,
                "UUID",
                "1",
                1,
                "Test",
                new Date(),
                "DATA"
        );

        Topic topic = new Topic(1, "/tum");

        SCAMPIMessage scampiMessage = SCAMPIMessage.builder().build();

        when(mContext.bindService(any(), any(), anyInt())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ServiceConnection serviceConnection = invocation.getArgument(1);
                serviceConnection.onServiceConnected(mComponentName, mScampiBinder);
                return null;
            }
        });
        when(mTopicRepository.getFinalTopic(1)).thenReturn(topic);
        when(mScampiPostSerializer.messageFromPost(post, topic, "TODOCREATOR")).thenReturn(scampiMessage);

        RealPostRepository realPostRepository = new RealPostRepository(mContext, mPostDao, mTopicRepository, mExecutor, mScampiPostSerializer, mVoteDao);

        realPostRepository.addPost(post);
        verify(mScampiPostSerializer).messageFromPost(post, topic, "TODOCREATOR");
        verify(mScampiBinder).publish(eq(scampiMessage), eq("/tum"));
        verify(mTopicRepository).getFinalTopic(1);
    }

    @Test
    public void insertVoteWithDuplicate() throws DatabaseException {

        RealPostRepository realPostRepository = new RealPostRepository(mContext, mPostDao, mTopicRepository, mExecutor, mScampiPostSerializer, mVoteDao);
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

}
