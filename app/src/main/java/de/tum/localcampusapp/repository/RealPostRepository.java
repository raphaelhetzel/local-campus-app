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

import de.tum.localcampusapp.ServiceTestActivity;
import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.service.AppLibService;

public class RealPostRepository implements PostRepository {

    static final String TAG = ServiceTestActivity.class.getSimpleName();

    private final PostDao postDao;

    private AppLibService.ScampiBinder scampiBinder;
    private Boolean serviceBound = false;


    public RealPostRepository(Context applicationContext, PostDao postDao) {
        this.postDao = postDao;

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
        if (this.serviceBound) {
            Log.d(TAG, "addPost while service Bound");
            this.scampiBinder.publishPost(post);
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
        return null;
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
