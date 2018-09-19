package de.tum.testextension;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import de.tum.localcampuslib.AddPostDataProvider;
import de.tum.localcampuslib.ShowPostDataProvider;

public class TestAddViewModel {
    private AddPostDataProvider addPostDataProvider;
    public TestAddViewModel(AddPostDataProvider addPostDataProvider) {
        this.addPostDataProvider = addPostDataProvider;
    }

    public void addEmptyPost() {
        this.addPostDataProvider.addPost("{\"text\":\"sampleText\",\"color\":\"5144962\"");
    }
}
