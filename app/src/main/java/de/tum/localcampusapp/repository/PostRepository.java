package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Vote;
import de.tum.localcampusapp.exception.DatabaseException;

public interface PostRepository {

    // As posts and votes are tightly coupled for performance reasons,
    // they share a repository

    LiveData<Post> getPost(long id) throws DatabaseException;

    LiveData<Post> getPostByUUID(String uuid) throws DatabaseException;

    Post getFinalPostByUUID(String uuid) throws DatabaseException;

    void addPost(Post post) throws DatabaseException;

    LiveData<List<Post>> getPostsforTopic(long topicId) throws DatabaseException;


    boolean upVote(long postId);

    boolean downVote(long postId);


    /*
        The insert methods should only be called from the scampi side of the application.
        Possibly refactor the structure to better separate this.
    */

    void insertPost(Post post) throws DatabaseException;

    void insertVote(Vote vote) throws DatabaseException;
}
