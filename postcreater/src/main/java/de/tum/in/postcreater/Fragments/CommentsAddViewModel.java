package de.tum.in.postcreater.Fragments;

import de.tum.localcampuslib.AddPostDataProvider;

public class CommentsAddViewModel {

    private AddPostDataProvider addPostDataProvider;

    public CommentsAddViewModel(AddPostDataProvider addPostDataProvider) {
        this.addPostDataProvider = addPostDataProvider;
    }

    public void addPost() {
        this.addPostDataProvider.addPost("{\"text\":\"Post with Comments extension\",\"color\":\"5144962\"");
    }
}
