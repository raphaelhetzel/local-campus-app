package de.tum.localcampusapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import de.tum.localcampusapp.entity.Vote;

@Dao
public interface VoteDao {

    @Insert
    public void insert(Vote vote);

    @Update
    public void update(Vote vote);

    @Query("SELECT SUM(score_influence) FROM votes WHERE post_id = :postId")
    public int getScore(long postId);

    @Query("SELECT * FROM votes WHERE post_id = :postId AND creator_id LIKE :userId")
    public Vote getUserVote(long postId, String userId);

    @Query("SELECT * FROM votes WHERE post_uuid LIKE :postUUID AND creator_id LIKE :userId")
    public Vote getUserVoteByUUID(String postUUID, String userId);

    @Query("SELECT * FROM votes WHERE post_uuid LIKE :postUUID")
    public List<Vote> getVotesByPostUUID(String postUUID);
}
