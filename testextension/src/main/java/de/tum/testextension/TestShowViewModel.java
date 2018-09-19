package de.tum.testextension;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import de.tum.localcampuslib.ShowPostDataProvider;

public class TestShowViewModel {

    private ShowPostDataProvider showPostDataProvider;

    public TestShowViewModel(ShowPostDataProvider showPostDataProvider) {
        this.showPostDataProvider = showPostDataProvider;
    }

    public LiveData<String> getText() {
       return Transformations.map(this.showPostDataProvider.getPost(), post -> {
           if(post != null) {
               return post.getData();
           }
           return "No Post";
        });
    }
}
