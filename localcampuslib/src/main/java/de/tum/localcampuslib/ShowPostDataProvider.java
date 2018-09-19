package de.tum.localcampuslib;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampuslib.entity.IPost;
import de.tum.localcampuslib.entity.IPostExtension;

public interface ShowPostDataProvider {

    public LiveData<? extends IPost> getPost();
    public LiveData<? extends List<? extends IPostExtension>> getPostExtensions();

    public void addPostExtension(String data);
}
