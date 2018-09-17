package de.tum.localcampusapp.Activities;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.tum.localcampusapp.database.TopicDao;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryPostRepository;
import de.tum.localcampusapp.repository.InMemoryTopicRepository;
import de.tum.localcampusapp.repository.RealTopicRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.repository.TopicRepository;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;

public class TopicsViewModel extends ViewModel{

    private LiveData<List<Topic>> liveDataTopics;
    TopicRepository topicRepository;

    public TopicsViewModel(Context applicationContext) throws DatabaseException{
        // FakeDataGenerator.getInstance().insertSeveralTopics("Fake Topic", 8);

        topicRepository = RepositoryLocator.getTopicRepository();
        liveDataTopics = topicRepository.getTopics();
    }

    public LiveData<List<Topic>> getLiveDataTopics() {
        return liveDataTopics;
    }

}
