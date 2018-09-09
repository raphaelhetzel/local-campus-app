package de.tum.localcampusapp;

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
import de.tum.localcampusapp.repository.InMemoryTopicRepository;
import de.tum.localcampusapp.repository.RealTopicRepository;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;

public class TopicsAdapterViewModel extends AndroidViewModel {

    private long topicId;
    private String topicName;
    private LiveData<List<Topic>> liveDataTopics;

    FakeDataGenerator fakeDataGenerator;


    public TopicsAdapterViewModel(Application application) throws DatabaseException{
        super(application);
        //TODO: Replace fakeData with Database
        fakeDataGenerator = new FakeDataGenerator("FakeTopic", 8);
        fakeDataGenerator.insertSeveralTopics();
        liveDataTopics = fakeDataGenerator.getLiveData();
    }

    public void createNewDataset() throws DatabaseException {
        //Helper method for creating more fake data
        fakeDataGenerator.insertNewTopic();
    }

    public LiveData<List<Topic>> getLiveDataTopics() {
        return liveDataTopics;
        }

    }
