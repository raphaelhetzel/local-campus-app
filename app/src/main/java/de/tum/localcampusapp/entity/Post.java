package de.tum.localcampusapp.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class Post {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long topicId;
    private String creator;
    private Date createdAt;
    private Date updatedAt;
    private String data;

    public Post(long id, long topicId, String creator, Date createdAt, Date updatedAt, String data) {
        this.id = id;
        this.topicId = topicId;
        this.creator = creator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topic_id) {
        this.topicId = topic_id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreated_at() {
        return createdAt;
    }

    public void setCreatedAt(Date created_at) {
        this.createdAt = created_at;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updated_at) {
        this.updatedAt = updated_at;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
