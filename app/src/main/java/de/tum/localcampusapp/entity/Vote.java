package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity(tableName = "votes",  indices = {@Index(value = "uuid", unique = true),
        @Index(value = "post_uuid"),
        @Index(value = {"post_id", "creator_id"}),
        @Index(value = {"post_uuid", "creator_id"})})
//TODO reduce indicies
public class Vote {

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

    @ColumnInfo(name = "score_influence")
    private long scoreInfluence;

    public Vote() {
    }

    public Vote(String uuid, String postUuid, String creatorId, Date createdAt, long scoreInfluence) {
        this.uuid = uuid;
        this.postUuid = postUuid;
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

    public long getScoreInfluence() {
        return scoreInfluence;
    }

    public void setScoreInfluence(long scoreInfluence) {
        this.scoreInfluence = scoreInfluence;
    }
}
