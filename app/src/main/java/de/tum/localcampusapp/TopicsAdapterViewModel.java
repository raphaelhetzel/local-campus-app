package de.tum.localcampusapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import de.tum.localcampusapp.entity.Topic;

public class TopicsAdapterViewModel extends AndroidViewModel {

    private long topicId;
    private String topicName;
    private LiveData<List<Topic>> topics;
    //private List<Topic> topicList;

    public TopicsAdapterViewModel(Application application){
        super(application);
        //TODO: topics = LiveData<List<Topic>>
    }

    public void init(long topicId, String topicName) {
        this.topicId = topicId;
        this.topicName = topicName;
    }

    public LiveData<List<Topic>> getTopics() {
        return topics;
    }


}
