package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;

public interface PostRepository {
    LiveData<Post> getPost(long id) throws DatabaseException;

    LiveData<Post> getPostByUUID(String uuid) throws DatabaseException;

    void addPost(Post post) throws DatabaseException;

    void updatePost(Post post) throws DatabaseException;

    LiveData<List<Post>> getPostsforTopic(long topicId) throws DatabaseException;

    /*
        Should only be called from the scampi side of the application.
        Possibly refactor the structure to better separate this.
    */
    void insertPost(Post post) throws DatabaseException;
}
