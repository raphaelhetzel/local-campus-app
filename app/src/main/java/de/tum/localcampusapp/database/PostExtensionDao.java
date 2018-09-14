package de.tum.localcampusapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import de.tum.localcampusapp.entity.PostExtension;

@Dao
public interface PostExtensionDao {

    @Insert
    public void insert(PostExtension postExtension);

    @Update
    public void update(PostExtension postExtension);

    @Query("SELECT * FROM post_extensions WHERE post_id = :postId")
    public LiveData<List<PostExtension>> getPostExtensionsByPostId(long postId);

    @Query("SELECT * FROM post_extensions WHERE post_uuid LIKE :postUUID")
    public List<PostExtension> getFinalPostExtensionsByPostUUID(String postUUID);
}
