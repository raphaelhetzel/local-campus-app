package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.repository.TopicRepository;

public class TopicsViewModel extends ViewModel {

    private LiveData<List<Topic>> liveDataTopics;
    TopicRepository topicRepository;

    public TopicsViewModel(Context applicationContext) throws DatabaseException {
        // FakeDataGenerator.getInstance().insertSeveralTopics("Fake Topic", 8);

        topicRepository = RepositoryLocator.getTopicRepository();
        liveDataTopics = topicRepository.getTopicsForCurrentLocation();
    }

    public LiveData<List<Topic>> getLiveDataTopics() {
        return liveDataTopics;
    }

}
