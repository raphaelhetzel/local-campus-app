package de.tum.localcampusapp.repository;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import de.tum.localcampusapp.ServiceTestActivity;
import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.serializer.ScampiPostSerializer;
import de.tum.localcampusapp.service.AppLibService;
import de.tum.localcampusapp.service.TopicHandler;
import fi.tkk.netlab.dtn.scampi.applib.SCAMPIMessage;

public class RealPostRepository implements PostRepository {

    static final String TAG = ServiceTestActivity.class.getSimpleName();

    private final PostDao postDao;

    private final TopicRepository topicRepository;

    private AppLibService.ScampiBinder scampiBinder;
    private Boolean serviceBound = false;
    private Context applicationContext;
    private ExecutorService executor;

    public RealPostRepository(Context applicationContext) {
        this(applicationContext,
                RepositoryLocator.getAppDatabase(applicationContext).getPostDao(),
                RepositoryLocator.getTopicRepository(applicationContext));
    }

    public RealPostRepository(Context applicationContext, PostDao postDao, TopicRepository topicRepository) {
        this.postDao = postDao;
        this.applicationContext = applicationContext;
        this.topicRepository = topicRepository;
        this.executor = Executors.newSingleThreadExecutor();

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

    private class AddPostRunner implements Runnable {
        private Post post;
        public AddPostRunner(Post post) {
            this.post = post;
        }

        @Override
        public void run() {
            if (serviceBound) {
                Log.d(TAG, "addPost while service Bound");
                if(post.getId() == 0) {
                    Log.d(TAG, "Cannot add post as it has no id");
                    return;
                }
                try {
                    Topic topic = topicRepository.getFinalTopic(post.getId());
                    SCAMPIMessage message = ScampiPostSerializer.messageFromPost(post, topic, "CREATOR");
                    scampiBinder.publish(message, topic.getTopicName());
                } catch (InterruptedException | DatabaseException e) {
                    e.printStackTrace(); // TODO: Remove DatabaseException as you can't catch it from other threads
                }
            }
        }
    }

    @Override
    public void updatePost(Post post) throws DatabaseException {
        try {
            postDao.update(post);
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            throw new DatabaseException();
        }
    }

    @Override
    public LiveData<List<Post>> getPostsforTopic(long topicId) throws DatabaseException {
        return postDao.getPostsforTopic(topicId);
    }

    @Override
    public void insertPost(Post post) throws DatabaseException {
        try {
            postDao.insert(post);
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            throw new DatabaseException();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Service connected");
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
