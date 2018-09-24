package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

public interface TopicRepository {

    // Returns all Topics, regardless of the Locations they are available at.
    @Deprecated
    LiveData<List<Topic>> getTopics();

    LiveData<List<Topic>> getTopicsForCurrentLocation();

    LiveData<Topic> getTopicByName(String topicName);

    LiveData<Topic> getTopic(long id);

    // As this is most likely blocking, it MUST not be called from the UI thread!
    List<Topic> getFinalTopicsForCurrentLocation();

    // As this is most likely blocking, it MUST not be called from the UI thread!
    Topic getFinalTopic(long id);

    // As this is most likely blocking, it MUST not be called from the UI thread!
    Topic getFinalTopicByName(String topicName);

    /*
        Allows & expects duplicate inserts (both the topic itself
        and location links to the topic).

        Should only be called from the scampi side of the application.
        Possibly refactor the repositories to better separate this.
    */
    void insertTopic(String topicName, String locationId);
}
