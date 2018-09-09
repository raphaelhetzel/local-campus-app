package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "topics", indices = {@Index(value = "topic_name", unique = true)})
public class Topic {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "topic_name")
    private String topicName;

    public Topic() {
    }

    public Topic(long id, String topicName) {
        this.id = id;
        this.topicName = topicName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
