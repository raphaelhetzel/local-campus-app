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
import java.util.concurrent.Executors;

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
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class RealPostRepository implements PostRepository, NetworkLayerPostRepository {

    static final String TAG = RealPostRepository.class.getSimpleName();

    private final Context applicationContext;

    private final PostDao postDao;
    private final VoteDao voteDao;
    private final PostExtensionDao postExtensionDao;

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    private AppLibService.ScampiBinder scampiBinder;
    private Boolean serviceBound = false;
    private Executor executor;

    private ScampiPostSerializer scampiPostSerializer;
    private ScampiVoteSerializer scampiVoteSerializer;
    private ScampiPostExtensionSerializer scampiPostExtensionSerializer;

    private final Object insertLock = new Object();
    private final Object voteLock = new Object();

    private final Set<String> voteBuffer;

    public RealPostRepository(Context applicationContext,
                              PostDao postDao,
                              VoteDao voteDao,
                              PostExtensionDao postExtensionDao,
                              TopicRepository topicRepository,
                              UserRepository userRepository) {
        this(applicationContext,
                postDao,
                voteDao,
                postExtensionDao,
                topicRepository,
                userRepository,
                new ScampiPostSerializer(),
                new ScampiVoteSerializer(),
                new ScampiPostExtensionSerializer(),
                Executors.newSingleThreadExecutor());
    }

    // Constructor that allows testing
    public RealPostRepository(Context applicationContext,
                              PostDao postDao,
                              VoteDao voteDao,
                              PostExtensionDao postExtensionDao,
                              TopicRepository topicRepository,
                              UserRepository userRepository,
                              ScampiPostSerializer scampiPostSerializer,
                              ScampiVoteSerializer scampiVoteSerializer,
                              ScampiPostExtensionSerializer scampiPostExtensionSerializer,
                              Executor executor) {
        this.applicationContext = applicationContext;
        this.postDao = postDao;
        this.voteDao = voteDao;
        this.postExtensionDao = postExtensionDao;
        this.topicRepository = topicRepository;
        this.executor = executor;
        this.scampiPostSerializer = scampiPostSerializer;
        this.scampiVoteSerializer = scampiVoteSerializer;
        this.scampiPostExtensionSerializer = scampiPostExtensionSerializer;
        this.userRepository = userRepository;

        this.voteBuffer = Collections.synchronizedSet(new HashSet<String>());
    }

    public void bindService() {
        Intent intent = new Intent(applicationContext.getApplicationContext(), AppLibService.class);
        applicationContext.bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
    }

    /// Post

    @Override
    public LiveData<Post> getPost(long id) {
        return postDao.getPost(id);
    }

    @Override
    public LiveData<Post> getPostByUUID(String uuid) {
        return postDao.getPostByUUID(uuid);
    }

    @Override
    public Post getFinalPostByUUID(String uuid) {
        return postDao.getFinalPostByUUID(uuid);
    }

    @Override
    public void addPost(Post post) {
        executor.execute(new AddPostRunner(post));
    }

    @Override
    public LiveData<List<Post>> getPostsforTopic(long topicId) {
        return postDao.getPostsforTopic(topicId);
    }

    @Override
    public void insertPost(Post post) throws DatabaseException, MissingRelatedDataException {
        try {
            Topic relatedTopic = topicRepository.getFinalTopicByName(post.getTopicName());
            if (relatedTopic == null) throw new MissingRelatedDataException();

            post.setScore(0);
            post.setTopicId(relatedTopic.getId());

            synchronized (insertLock) {
                long postId = postDao.insert(post);
                post.setId(postId);
                updateRelatedVotes(post);
                updateRelatedPostExtensions(post);
            }

        } catch (android.database.sqlite.SQLiteConstraintException e) {
            // Ignore duplicate inserts
            if (e.getMessage().contains("posts.uuid")) {
                return;
            }
            throw new DatabaseException();
        }
    }

    private void updateRelatedVotes(Post post) {
        List<Vote> unassignedVotes = voteDao.getVotesByPostUUID(post.getUuid());
        for (Vote vote : unassignedVotes) {
            vote.setPostId(post.getId());
            voteDao.update(vote);
        }
    }

    private void updateRelatedPostExtensions(Post post) {
        List<PostExtension> unassignedPosts = postExtensionDao.getFinalPostExtensionsByPostUUID(post.getUuid());
        for (PostExtension postExtension : unassignedPosts) {
            postExtension.setPostId(post.getId());
            postExtensionDao.update(postExtension);
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
                try {
                    Topic topic = topicRepository.getFinalTopic(post.getTopicId());
                    if (topic == null) {
                        Log.d(TAG, "Tried to insert Post, however the related Topic was missing. This is probably an error in the app!");
                        return;
                    }
                    post.setTopicName(topic.getTopicName());
                    post.setUuid(UUID.randomUUID().toString());
                    post.setCreator(userRepository.getId());
                    post.setCreatedAt(new Date());
                    SCAMPIMessage message = scampiPostSerializer.messageFromPost(post);
                    scampiBinder.publish(message, topic.getTopicName());
                } catch (MissingFieldsException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Service went down, message currently ignored");
            }
        }
    }

    /// Vote

    @Override
    public void upVote(long postId) {
        vote(postId, 1);
    }

    @Override
    public void downVote(long postId) {
        vote(postId, -1);
    }

    private void vote(long postId, long scoreInfluce) {
        executor.execute(new VoteRunner(postId, userRepository.getId(), scoreInfluce));
    }

    @Override
    public void insertVote(Vote vote) throws DatabaseException {
        try {
            synchronized (insertLock) {

                Vote existing_user_vote = voteDao.getUserVoteByUUID(vote.getPostUuid(), vote.getCreatorId());
                if (existing_user_vote != null) return;

                Post post = postDao.getFinalPostByUUID(vote.getPostUuid());
                if (post == null) vote.setPostId(0L);
                else vote.setPostId(post.getId());
                voteDao.insert(vote);
            }
        }
        // Catches both the case where the id and the uuid are duplicate
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
                        if (post == null) {
                            Log.d(TAG, "Tried to insert Vote, however the related Post was missing. This is probably an error in the app!");
                            return;
                        }

                        if (voteBuffer.contains(post.getUuid())) return;
                        if (voteDao.getUserVote(postId, userId) != null) return;

                        Topic topic = topicRepository.getFinalTopic(post.getTopicId());

                        Vote vote = new Vote();
                        vote.setUuid(UUID.randomUUID().toString());
                        vote.setPostUuid(post.getUuid());
                        vote.setCreatedAt(new Date());
                        vote.setCreatorId(userRepository.getId());
                        vote.setScoreInfluence(scoreInfluence);

                        SCAMPIMessage scampiMessage = scampiVoteSerializer.voteToMessage(vote);
                        scampiBinder.publish(scampiMessage, topic.getTopicName());

                        voteBuffer.add(post.getUuid());
                    }
                } catch (MissingFieldsException | InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(TAG, "Service went down, message currently ignored");
            }
        }
    }


    //Post Extension

    @Override
    public void addPostExtension(PostExtension postExtension) {
        executor.execute(new AddPostExtensionRunner(postExtension));
    }

    @Override
    public LiveData<List<PostExtension>> getPostExtensionsForPost(long postId) {
        return postExtensionDao.getPostExtensionsByPostId(postId);
    }

    @Override
    public void insertPostExtension(PostExtension postExtension) throws DatabaseException {
        try {
            synchronized (insertLock) {
                Post relatedPost = postDao.getFinalPostByUUID(postExtension.getPostUuid());
                if (relatedPost == null) postExtension.setPostId(0);
                else postExtension.setPostId(relatedPost.getId());
                postExtensionDao.insert(postExtension);
            }
        }
        // Catches both the case where the id and the uuid are duplicate
        catch (android.database.sqlite.SQLiteConstraintException e) {
            // ignore duplicate inserts
            if (e.getMessage().contains("post_extensions.uuid")) {
                return;
            }
            throw new DatabaseException();
        }
    }

    private class AddPostExtensionRunner implements Runnable {
        private PostExtension postExtension;

        public AddPostExtensionRunner(PostExtension postExtension) {
            this.postExtension = postExtension;
        }

        @Override
        public void run() {
            if (serviceBound) {
                try {
                    Post relatedPost = postDao.getFinalPost(postExtension.getPostId());
                    if (relatedPost == null) {
                        Log.d(TAG, "Tried to insert PostExtension, however the related Post was missing. This is probably an error in the app!");
                        return;
                    }

                    Topic topic = topicRepository.getFinalTopic(relatedPost.getTopicId());

                    postExtension.setUuid(UUID.randomUUID().toString());
                    postExtension.setPostUuid(relatedPost.getUuid());
                    postExtension.setCreatedAt(new Date());
                    postExtension.setCreatorId(userRepository.getId());

                    SCAMPIMessage scampiMessage = scampiPostExtensionSerializer.postExtensionToMessage(postExtension);
                    scampiBinder.publish(scampiMessage, topic.getTopicName());

                } catch (MissingFieldsException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Service went down, message currently ignored");
            }
        }
    }

    /// Service Connection

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
