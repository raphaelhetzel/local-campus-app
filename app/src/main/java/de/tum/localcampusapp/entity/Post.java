package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "posts", foreignKeys = @ForeignKey(entity = Topic.class,
        parentColumns = "id",
        childColumns = "topic_id",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION))
public class Post {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String uuid;
    @ColumnInfo(name = "type_id")
    private long typeId;
    @ColumnInfo(name = "topic_id")
    private long topicId;
    private String creator;
    @ColumnInfo(name = "created_at")
    private Date createdAt;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    private String data;
    private int score;

    public Post() {
    }

    public Post(long id, String uuid, long typeId, long topicId, String creator, Date createdAt, Date updatedAt, String data, int score) {
        this.id = id;
        this.uuid = uuid;
        this.typeId = typeId;
        this.topicId = topicId;
        this.creator = creator;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.data = data;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id &&
                typeId == post.typeId &&
                topicId == post.topicId &&
                score == post.score &&
                Objects.equals(uuid, post.uuid) &&
                Objects.equals(creator, post.creator) &&
                Objects.equals(createdAt, post.createdAt) &&
                Objects.equals(updatedAt, post.updatedAt) &&
                Objects.equals(data, post.data);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, uuid, typeId, topicId, creator, createdAt, updatedAt, data, score);
    }
}
