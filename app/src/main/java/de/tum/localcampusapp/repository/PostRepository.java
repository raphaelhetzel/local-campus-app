package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;

public interface PostRepository {
    public LiveData<Post> getPost(long id)  throws DatabaseException;
    public void addPost(Post post) throws DatabaseException;
    public void updatePost(Post post) throws DatabaseException;
    public LiveData<List<Post>> getPostsforTopic(long topicId)  throws DatabaseException;
}
