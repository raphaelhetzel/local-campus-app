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

    @Query("SELECT filtered_posts.id as id, filtered_posts.uuid as uuid, filtered_posts.type_id as type_id, filtered_posts.topic_id as topic_id," +
            " filtered_posts.creator as creator, filtered_posts.created_at as created_at, filtered_posts.data as data, SUM(votes.score_influence) as score, topics.topic_name as topic_name " +
            "FROM (SELECT * FROM posts WHERE posts.topic_id = :topicId) as filtered_posts " +
            "INNER JOIN topics ON topics.id = filtered_posts.topic_id " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    LiveData<List<Post>> getPostsforTopic(long topicId);

    @Query("SELECT filtered_posts.id as id, filtered_posts.uuid as uuid, filtered_posts.type_id as type_id, filtered_posts.topic_id as topic_id," +
            " filtered_posts.creator as creator, filtered_posts.created_at as created_at, filtered_posts.data as data, SUM(votes.score_influence) as score, topics.topic_name as topic_name " +
            "FROM (SELECT * FROM posts WHERE posts.id = :id) as filtered_posts " +
            "INNER JOIN topics ON topics.id = filtered_posts.topic_id " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    LiveData<Post> getPost(long id);

    @Query("SELECT filtered_posts.id as id, filtered_posts.uuid as uuid, filtered_posts.type_id as type_id, filtered_posts.topic_id as topic_id," +
            " filtered_posts.creator as creator, filtered_posts.created_at as created_at, filtered_posts.data as data, SUM(votes.score_influence) as score, topics.topic_name as topic_name " +
            "FROM (SELECT * FROM posts WHERE posts.id = :id) as filtered_posts " +
            "INNER JOIN topics ON topics.id = filtered_posts.topic_id " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    Post getFinalPost(long id);

    @Query("SELECT filtered_posts.id as id, filtered_posts.uuid as uuid, filtered_posts.type_id as type_id, filtered_posts.topic_id as topic_id," +
            " filtered_posts.creator as creator, filtered_posts.created_at as created_at, filtered_posts.data as data, SUM(votes.score_influence) as score, topics.topic_name as topic_name " +
            "FROM (SELECT * FROM posts WHERE posts.uuid LIKE :uuid) as filtered_posts " +
            "INNER JOIN topics ON topics.id = filtered_posts.topic_id " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    LiveData<Post> getPostByUUID(String uuid);

    @Query("SELECT filtered_posts.id as id, filtered_posts.uuid as uuid, filtered_posts.type_id as type_id, filtered_posts.topic_id as topic_id," +
            " filtered_posts.creator as creator, filtered_posts.created_at as created_at, filtered_posts.data as data, SUM(votes.score_influence) as score, topics.topic_name as topic_name " +
            "FROM (SELECT * FROM posts WHERE posts.uuid LIKE :uuid) as filtered_posts " +
            "INNER JOIN topics ON topics.id = filtered_posts.topic_id " +
            "LEFT JOIN votes ON filtered_posts.id = votes.post_id GROUP BY filtered_posts.id")
    Post getFinalPostByUUID(String uuid);

}
