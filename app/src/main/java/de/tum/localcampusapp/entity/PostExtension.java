package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

import de.tum.localcampuslib.entity.IPostExtension;

@Entity(tableName = "post_extensions",
        indices = {@Index(value = "uuid", unique = true), @Index(value = "post_uuid")})
public class PostExtension implements IPostExtension {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String uuid;

    @ColumnInfo(name = "post_id")
    private long postId;

    @ColumnInfo(name = "post_uuid")
    private String postUuid;

    @ColumnInfo(name = "creator_id")
    private String creatorId;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "data")
    private String data;

    public PostExtension() {
    }

    // App Layer Constructor
    public PostExtension(long postId, String data) {
        this.postId = postId;
        this.data = data;
    }

    // Insert Constructor
    public PostExtension(String uuid, String postUuid, String creatorId, Date createdAt, String data) {
        this.uuid = uuid;
        this.postUuid = postUuid;
        this.creatorId = creatorId;
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

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getPostUuid() {
        return postUuid;
    }

    public void setPostUuid(String postUuid) {
        this.postUuid = postUuid;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostExtension that = (PostExtension) o;
        return getId() == that.getId() &&
                getPostId() == that.getPostId() &&
                Objects.equals(getUuid(), that.getUuid()) &&
                Objects.equals(getPostUuid(), that.getPostUuid()) &&
                Objects.equals(getCreatorId(), that.getCreatorId()) &&
                Objects.equals(getCreatedAt(), that.getCreatedAt()) &&
                Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getUuid(), getPostId(), getPostUuid(), getCreatorId(), getCreatedAt(), getData());
    }
}
