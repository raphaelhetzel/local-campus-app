package de.tum.localcampusapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import de.tum.localcampusapp.entity.LocationTopicMapping;

@Dao
public abstract class LocationTopicMappingDao {

    @Insert
    public abstract void insert(LocationTopicMapping locationTopicMapping);

    // Queried in the topic DAO.
}
