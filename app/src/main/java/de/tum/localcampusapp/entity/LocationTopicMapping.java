package de.tum.localcampusapp.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import java.util.Objects;

@Entity(tableName = "location_topic_mapping",
        primaryKeys = {"topic_id", "location_id"},
        indices = {@Index(value = "location_id")},
        foreignKeys = @ForeignKey(entity = Topic.class,
                parentColumns = "id",
                childColumns = "topic_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.NO_ACTION))

public class LocationTopicMapping {

    @ColumnInfo(name = "topic_id")
    private long topicId;

    /**
        This is currently just the locationId (a String) as we don't store other information about
        the location.
        In the future this could be replaced with a reference to a location entity (e.g. if we need
        to store more data on a location, like coordinates).
     */
    @NonNull @ColumnInfo(name = "location_id")
    private String locationId;

    public LocationTopicMapping(long topicId, String locationId) {
        this.topicId = topicId;
        this.locationId = locationId;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationTopicMapping that = (LocationTopicMapping) o;
        return getTopicId() == that.getTopicId() &&
                Objects.equals(getLocationId(), that.getLocationId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getTopicId(), getLocationId());
    }
}
