package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "votes", foreignKeys = @ForeignKey(entity = Post.class,
        parentColumns = "id",
        childColumns = "post_id",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION),  indices = {@Index(value = "uuid", unique = true)})

public class Vote {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String uuid;

    @ColumnInfo(name = "post_id")
    private long postId;

    @ColumnInfo(name = "creator_id")
    private String creatorId;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "score_influence")
    private long scoreInfluence;

    public Vote(String uuid, long postId, String creatorId, Date createdAt, long scoreInfluence) {
        this.uuid = uuid;
        this.postId = postId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.scoreInfluence = scoreInfluence;
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

    public long getScoreInfluence() {
        return scoreInfluence;
    }

    public void setScoreInfluence(long scoreInfluence) {
        this.scoreInfluence = scoreInfluence;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return getId() == vote.getId() &&
                getPostId() == vote.getPostId() &&
                getScoreInfluence() == vote.getScoreInfluence() &&
                Objects.equals(getUuid(), vote.getUuid()) &&
                Objects.equals(getCreatorId(), vote.getCreatorId()) &&
                Objects.equals(getCreatedAt(), vote.getCreatedAt());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getUuid(), getPostId(), getCreatorId(), getCreatedAt(), getScoreInfluence());
    }
}
