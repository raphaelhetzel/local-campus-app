package de.tum.testextension;

import de.tum.localcampuslib.AddPostDataProvider;

public class TestAddViewModel {
    private AddPostDataProvider addPostDataProvider;
    public TestAddViewModel(AddPostDataProvider addPostDataProvider) {
        this.addPostDataProvider = addPostDataProvider;
    }

    public void addEmptyPost() {
        this.addPostDataProvider.addPost("{\"text\":\"sampleText\",\"color\":\"5144962\"}");
    }
}
