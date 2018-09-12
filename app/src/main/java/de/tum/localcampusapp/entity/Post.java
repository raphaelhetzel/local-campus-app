package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
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
    private String typeId;
    @ColumnInfo(name = "topic_id")
    private long topicId;
    private String creator;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    private String data;

    // Unfortunately, there is no way of allowing the fetch from multiple
    // tables while still preventing a column to be created for it
    // we can only get rid of this field by not using room anymore
    private long score;

    public Post() {
    }

    public Post(long id, String uuid, String typeId, long topicId, String creator, Date createdAt, String data) {
        this.id = id;
        this.uuid = uuid;
        this.typeId = typeId;
        this.topicId = topicId;
        this.creator = creator;
        this.createdAt = createdAt;
        this.data = data;
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getScore() {
        return score;
    }

    // this should never be used!
    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id &&
                typeId.equals(post.typeId) &&
                topicId == post.topicId &&
                getScore() == post.getScore() &&
                Objects.equals(uuid, post.uuid) &&
                Objects.equals(creator, post.creator) &&
                Objects.equals(createdAt, post.createdAt) &&
                Objects.equals(data, post.data);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, uuid, typeId, topicId, creator, createdAt, data, score);
    }
}
