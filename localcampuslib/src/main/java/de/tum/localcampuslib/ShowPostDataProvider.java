package de.tum.localcampuslib;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampuslib.entity.IPost;
import de.tum.localcampuslib.entity.IPostExtension;

/**
 This is the only interface a ShowPostFragment is expected to use for interacting with data.
 */
public interface ShowPostDataProvider {

    public LiveData<? extends IPost> getPost();
    public LiveData<? extends List<? extends IPostExtension>> getPostExtensions();

    public void addPostExtension(String data);

    public String getCurrentUser();

    public void upVote();
    public void downVote();
}
