package de.tum.in.postcreater.Fragments;

import android.os.Bundle;

import de.tum.localcampuslib.AddPostFragment;

public class CommentsAddFragment extends AddPostFragment {
    private CommentsAddViewModel commentsAddViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentsAddViewModel = new CommentsAddViewModel(getAddPostDataProvider());
        commentsAddViewModel.addPost();
    }
}
