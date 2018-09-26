package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;

/**
    Repository to interact with Topics. Used by both the Application Side and the Network Layer.

    Possibly refactor the repository into a Application and Network Layer repository. (Not as relevant as the
    Post Repository as the Topics repository has no dependency on the Networking service.
 */
public interface TopicRepository {

    /**
        Returns all Topics, regardless of the Locations they are available at.
        Deprecated in favor of the location aware alternative <code>getTopicsForCurrentLocation();</code>
     */
    @Deprecated
    LiveData<List<Topic>> getTopics();

    LiveData<List<Topic>> getTopicsForCurrentLocation();

    LiveData<Topic> getTopicByName(String topicName);

    LiveData<Topic> getTopic(long id);

    /**
        Directly return a list of all Topics available at the current Location.
        As this is most likely blocking, it MUST not be called from the UI thread!
     */
    List<Topic> getFinalTopicsForCurrentLocation();

    /**
        Directly return a Topic.
        As this is most likely blocking, it MUST not be called from the UI thread!
     */
    Topic getFinalTopic(long id);

    /**
        Directly return a Topic by it's name.
        As this is most likely blocking, it MUST not be called from the UI thread!
     */
    Topic getFinalTopicByName(String topicName);

    /**
        Insert a Topic into the Database.

        Allows & expects duplicate inserts (both the topic itself
        and location links to the topic).

        Should only be called from the Networking Layer.
    */
    void insertTopic(String topicName, String locationId);
}
