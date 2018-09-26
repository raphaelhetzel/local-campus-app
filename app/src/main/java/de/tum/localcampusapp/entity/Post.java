package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

import de.tum.localcampuslib.entity.IPost;

@Entity(tableName = "posts", foreignKeys = @ForeignKey(entity = Topic.class,
        parentColumns = "id",
        childColumns = "topic_id",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION), indices = {@Index(value = "uuid", unique = true)})
public class Post implements IPost {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String uuid;

    @ColumnInfo(name = "type_id")
    private String typeId;

    @ColumnInfo(name = "topic_id")
    private long topicId;

     /**
        This field is queried from the relation and not directly persisted.
        Unfortunately, room will still create a column. This could only be solved
        by separating the Room entities for the Database Creation from our actual entities.
     */
    @ColumnInfo(name = "topic_name")
    private String topicName;

    private String creator;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    private String data;

    /**
       This field is queried from the relation and not directly persisted.
       Unfortunately, room will still create a column. This could only be solved
       by separating the Room entities for the Database Creation from our actual entities.
    */
    private long score;

    public Post() {
    }

    // Insert Constructor
    public Post(String uuid, String typeId, String topicName, String creator, Date createdAt, String data) {
        this.uuid = uuid;
        this.typeId = typeId;
        this.topicName = topicName;
        this.creator = creator;
        this.createdAt = createdAt;
        this.data = data;
    }

    // Application Level Constructor
    public Post(long topicId, String typeId, String data) {
        this.topicId = topicId;
        this.typeId = typeId;
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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    // this should never be set directly / will only be used by the database
    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return getId() == post.getId() &&
                getTopicId() == post.getTopicId() &&
                getScore() == post.getScore() &&
                Objects.equals(getUuid(), post.getUuid()) &&
                Objects.equals(getTypeId(), post.getTypeId()) &&
                Objects.equals(getTopicName(), post.getTopicName()) &&
                Objects.equals(getCreator(), post.getCreator()) &&
                Objects.equals(getCreatedAt(), post.getCreatedAt()) &&
                Objects.equals(getData(), post.getData());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getUuid(), getTypeId(), getTopicId(), getTopicName(), getCreator(), getCreatedAt(), getData(), getScore());
    }
}
