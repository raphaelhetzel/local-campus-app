package de.tum.localcampusapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import de.tum.localcampusapp.entity.Vote;

@Dao
public interface VoteDao {

    @Insert
    public void insert(Vote vote);

    @Query("SELECT SUM(score_influence) FROM votes WHERE post_id = :postId")
    public int getScore(long postId);


    @Query("SELECT * FROM votes WHERE post_id = :postId AND creator_id LIKE :userId")
    public Vote getUserVote(long postId, String userId);
}
