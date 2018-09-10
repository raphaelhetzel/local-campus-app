package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;

@Dao
public interface TopicDao {

    @Insert
    void insert(Topic topic);

    @Query("SELECT * FROM topics WHERE id = :id")
    LiveData<Topic> getTopic(long id);

    @Query("SELECT * FROM topics")
    LiveData<List<Topic>> getTopics();

    @Query("SELECT * FROM topics WHERE topic_name LIKE :name")
    LiveData<Topic> getByName(String name);

    @Query("SELECT * FROM topics WHERE topic_name LIKE :name")
    Topic getFinalByName(String name);

}
