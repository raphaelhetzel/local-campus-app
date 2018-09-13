package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

import de.tum.localcampusapp.ServiceTestActivity;
import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.database.VoteDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.exception.MissingFieldsException;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.serializer.ScampiVoteSerializer;
import de.tum.localcampusapp.service.AppLibService;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class RealPostRepository implements PostRepository {

    static final String TAG = ServiceTestActivity.class.getSimpleName();

    private final PostDao postDao;
    private final VoteDao voteDao;

    private final TopicRepository topicRepository;

    private AppLibService.ScampiBinder scampiBinder;
    private Boolean serviceBound = false;
    private Executor executor;
    private ScampiPostSerializer scampiPostSerializer;
    private ScampiVoteSerializer scampiVoteSerializer;

    private final Object insertLock = new Object();
    private final Object voteLock = new Object();

    private final Set<String> voteBuffer;

    public RealPostRepository(Context applicationContext, PostDao postDao, TopicRepository topicRepository, Executor executor, ScampiPostSerializer scampiPostSerializer, VoteDao voteDao) {
        this(applicationContext, postDao, topicRepository, executor, scampiPostSerializer, voteDao, new ScampiVoteSerializer());
    }

    public RealPostRepository(Context applicationContext, PostDao postDao, TopicRepository topicRepository, Executor executor, ScampiPostSerializer scampiPostSerializer, VoteDao voteDao, ScampiVoteSerializer scampiVoteSerializer) {
        this.postDao = postDao;
        this.topicRepository = topicRepository;
        this.executor = executor;
        this.scampiPostSerializer = scampiPostSerializer;
        this.voteDao = voteDao;
        this.scampiVoteSerializer = scampiVoteSerializer;

        this.voteBuffer = Collections.synchronizedSet(new HashSet<String>());

        Intent intent = new Intent(applicationContext.getApplicationContext(), AppLibService.class);
        applicationContext.bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
    }

    @Override
    public LiveData<Post> getPost(long id) throws DatabaseException {
        return postDao.getPost(id);
    }

    @Override
    public LiveData<Post> getPostByUUID(String uuid) throws DatabaseException {
        return postDao.getPostByUUID(uuid);
    }

    @Override
    public Post getFinalPostByUUID(String uuid) throws DatabaseException {
        return postDao.getFinalPostByUUID(uuid);
    }

    @Override
    public void addPost(Post post) throws DatabaseException {
        executor.execute(new AddPostRunner(post));
    }

    @Override
    public LiveData<List<Post>> getPostsforTopic(long topicId) throws DatabaseException {
        return postDao.getPostsforTopic(topicId);
    }

    @Override
    public void upVote(long postId) {
        vote(postId, 1);
    }

    @Override
    public void downVote(long postId) {
        vote(postId, -1);
    }

    private boolean vote(long postId, long scoreInfluce) {
        executor.execute(new VoteRunner(postId, "TODOCREATOR", scoreInfluce));
        return true;

    }

    @Override
    public void insertPost(Post post) throws DatabaseException {
        try {
            post.setScore(0);
            long postId = postDao.insert(post);
            synchronized (insertLock) {
                List<Vote> unasignedVotes = voteDao.getVotesByPostUUID(post.getUuid());
                for (Vote vote : unasignedVotes) {
                    vote.setPostId(postId);
                    voteDao.update(vote);
                }
            }

        } catch (android.database.sqlite.SQLiteConstraintException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public void insertVote(Vote vote) throws DatabaseException {
        try {
            synchronized (insertLock) {

                Vote existing_user_vote = voteDao.getUserVoteByUUID(vote.getPostUuid(), "TODOCREATOR");
                if (existing_user_vote != null) return;

                Post post = postDao.getFinalPostByUUID(vote.getPostUuid());
                if (post == null) vote.setPostId(0L);
                else vote.setPostId(post.getId());
                voteDao.insert(vote);
            }
        }
        // Catches both the case where the topic id and the uuid are duplicate
        catch (android.database.sqlite.SQLiteConstraintException e) {
            /*
                Ignore duplicate votes, while matching a String isn't ideal
                this should be fine as the string is verified by a test
             */
            if (e.getMessage().contains("votes.uuid")) {
                return;
            }
            throw new DatabaseException();
        }
    }

    private class VoteRunner implements Runnable {

        private long postId;
        private String userId;
        private long scoreInfluence;

        public VoteRunner(long postId, String userId, long scoreInfluence) {
            this.postId = postId;
            this.userId = userId;
            this.scoreInfluence = scoreInfluence;
        }

        @Override
        public void run() {
            if (serviceBound) {
                try {
                    synchronized (voteLock) {
                        Post post = postDao.getFinalPost(postId);

                        if (post == null) return;
                        if (voteBuffer.contains(post.getUuid())) return;
                        if (voteDao.getUserVote(postId, userId) != null) return;

                        Topic topic = topicRepository.getFinalTopic(post.getTopicId());

                        Vote vote = new Vote();
                        vote.setUuid(UUID.randomUUID().toString());
                        vote.setPostUuid(post.getUuid());
                        vote.setCreatedAt(new Date());
                        vote.setCreatorId("TODOCREATOR");
                        vote.setScoreInfluence(scoreInfluence);

                        SCAMPIMessage scampiMessage = scampiVoteSerializer.voteToMessage(vote);
                        scampiBinder.publish(scampiMessage, topic.getTopicName());

                        voteBuffer.add(post.getUuid());
                    }
                } catch (MissingFieldsException | InterruptedException | DatabaseException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "Service went down, message currently ignored");
            }
        }
    }

    private class AddPostRunner implements Runnable {
        private Post post;

        public AddPostRunner(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            if (serviceBound) {
                if (post.getId() == 0) {
                    return;
                }
                try {
                    Topic topic = topicRepository.getFinalTopic(post.getTopicId());
                    SCAMPIMessage message = scampiPostSerializer.messageFromPost(post, topic, "TODOCREATOR");
                    scampiBinder.publish(message, topic.getTopicName());
                } catch (InterruptedException | DatabaseException e) {
                    e.printStackTrace(); // TODO: Remove DatabaseException as you can't catch it from other threads
                }
            } else {
                Log.d(TAG, "Service went down, message currently ignored");
            }
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AppLibService.ScampiBinder scampi = (AppLibService.ScampiBinder) service;
            scampiBinder = scampi;
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };
}
