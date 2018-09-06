package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import de.tum.localcampusapp.entity.Post;

@Dao
public interface PostDao {

    @Insert
    void insert(Post post);

    @Update
    void update(Post post);

    @Query("SELECT * FROM posts WHERE topic_id = :topicId")
    LiveData<List<Post>> getPostsforTopic(long topicId);

    @Query("SELECT * FROM posts WHERE id = :id")
    LiveData<Post> getPost(long id);

    @Query("SELECT * FROM posts WHERE uuid LIKE :uuid")
    LiveData<Post> getPostByUUID(String uuid);

}
