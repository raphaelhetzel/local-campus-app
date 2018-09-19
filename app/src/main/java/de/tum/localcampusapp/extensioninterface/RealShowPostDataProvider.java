package de.tum.localcampusapp.extensioninterface;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;

public class RealShowPostDataProvider implements ShowPostDataProvider {

    private long postId;


    public RealShowPostDataProvider(long postId) {
        this.postId = postId;
    }
    @Override
    public LiveData<Post> getPost() {
        return RepositoryLocator.getPostRepository().getPost(postId);
    }

    @Override
    public LiveData<List<PostExtension>> getPostExtensions() {
        return RepositoryLocator.getPostRepository().getPostExtensionsForPost(postId);
    }

    @Override
    public void addPostExtension(String data) {
        PostExtension newPostExtension = new PostExtension(postId, data);
        RepositoryLocator.getPostRepository().addPostExtension(newPostExtension);
    }
}
