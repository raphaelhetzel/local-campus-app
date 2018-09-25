package de.tum.localcampusapp.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import java.util.List;

import de.tum.localcampusapp.database.LocationTopicMappingDao;
import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.entity.LocationTopicMapping;
import de.tum.localcampusapp.entity.Topic;

public class RealTopicRepository implements TopicRepository {

    private final LocationRepository locationRepository;
    private final TopicDao topicDao;
    private final LocationTopicMappingDao locationTopicMappingDao;

    public RealTopicRepository(LocationRepository locationRepository, TopicDao topicDao, LocationTopicMappingDao locationTopicMappingDao) {
        this.topicDao = topicDao;
        this.locationTopicMappingDao = locationTopicMappingDao;
        this.locationRepository = locationRepository;
    }

    @Override
    public  LiveData<List<Topic>> getTopics() {
        return topicDao.getTopics();
    }

    @Override
    public  synchronized LiveData<List<Topic>> getTopicsForCurrentLocation() {
        return Transformations.switchMap(locationRepository.getCurrentLocation(), currentLocation -> {
            return topicDao.getTopicsForLocation(currentLocation);
        });
    }

    @Override
    public  LiveData<Topic> getTopic(long id) {
        return topicDao.getTopic(id);
    }

    @Override
    public  synchronized List<Topic> getFinalTopicsForCurrentLocation() {
        return topicDao.getFinalTopicsForLocation(locationRepository.getFinalCurrentLocation());
    }

    @Override
    public LiveData<Topic> getTopicByName(String topicName) {
        return topicDao.getByName(topicName);
    }

    @Override
    public Topic getFinalTopicByName(String topicName) {
        return topicDao.getFinalByName(topicName);
    }

    @Override
    public Topic getFinalTopic(long id) {
        return topicDao.getFinalTopic(id);
    }

    @Override
    public synchronized void insertTopic(String topicName, String locationId) {

        long topicId;
        Topic existingTopic = getFinalTopicByName(topicName);
        if(existingTopic == null || existingTopic.getId() == 0) {
            Topic topic = new Topic();
            topic.setTopicName(topicName);
            topicId = topicDao.insert(topic);
        } else {
            topicId = existingTopic.getId();
        }

        try {
            locationTopicMappingDao.insert(new LocationTopicMapping(topicId, locationId));
        }
        catch (android.database.sqlite.SQLiteConstraintException e) {
            if(!e.getMessage().contains("UNIQUE constraint failed")) {
                throw e;
            }
        }
    }
}
