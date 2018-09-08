package de.tum.localcampusapp.repository;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import java.util.List;

import de.tum.localcampusapp.database.PostDao;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.service.AppLibService;

public class RealPostRepository implements PostRepository {

    private final PostDao postDao;

    //private AppLibService.ScampiBinder scampi;


    public RealPostRepository(PostDao postDao) {
        this.postDao = postDao;
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
    public void addPost(Post post) throws DatabaseException {
        // TODO: Call Service
        throw new RuntimeException("Unimplemented");
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
}
