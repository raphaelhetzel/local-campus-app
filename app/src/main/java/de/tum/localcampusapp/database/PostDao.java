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
    long insert(Post post);

    @Query("SELECT filtered_posts.id, filtered_posts.uuid, filtered_posts.type_id, filtered_posts.topic_id," +
            " filtered_posts.creator, filtered_posts.created_at, filtered_posts.data, SUM(votes.score_influence) as score " +
            "FROM (SELECT * FROM posts WHERE posts.topic_id = :topicId) as filtered_posts " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    LiveData<List<Post>> getPostsforTopic(long topicId);

    @Query("SELECT filtered_posts.id, filtered_posts.uuid, filtered_posts.type_id, filtered_posts.topic_id," +
            " filtered_posts.creator, filtered_posts.created_at, filtered_posts.data, SUM(votes.score_influence) as score " +
            "FROM (SELECT * FROM posts WHERE posts.id = :id) as filtered_posts " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    LiveData<Post> getPost(long id);

    @Query("SELECT filtered_posts.id, filtered_posts.uuid, filtered_posts.type_id, filtered_posts.topic_id," +
            " filtered_posts.creator, filtered_posts.created_at, filtered_posts.data, SUM(votes.score_influence) as score " +
            "FROM (SELECT * FROM posts WHERE posts.id = :id) as filtered_posts " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    Post getFinalPost(long id);

    @Query("SELECT filtered_posts.id, filtered_posts.uuid, filtered_posts.type_id, filtered_posts.topic_id," +
            " filtered_posts.creator, filtered_posts.created_at, filtered_posts.data, SUM(votes.score_influence) as score " +
            "FROM (SELECT * FROM posts WHERE posts.uuid LIKE :uuid) as filtered_posts " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    LiveData<Post> getPostByUUID(String uuid);

    @Query("SELECT filtered_posts.id, filtered_posts.uuid, filtered_posts.type_id, filtered_posts.topic_id," +
            " filtered_posts.creator, filtered_posts.created_at, filtered_posts.data, SUM(votes.score_influence) as score " +
            "FROM (SELECT * FROM posts WHERE posts.uuid LIKE :uuid) as filtered_posts " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    Post getFinalPostByUUID(String uuid);

}
