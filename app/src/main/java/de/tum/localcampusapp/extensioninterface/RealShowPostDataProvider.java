package de.tum.localcampusapp.extensioninterface;

import android.arch.lifecycle.LiveData;

import java.util.List;

import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;

public class RealShowPostDataProvider implements ShowPostDataProvider {

    private long postId;
    private PostRepository postRepository;


    public RealShowPostDataProvider(long postId) {
        this.postRepository = RepositoryLocator.getPostRepository();
        this.postId = postId;
    }
    @Override
    public LiveData<Post> getPost() {
        return postRepository.getPost(postId);
    }

    @Override
    public LiveData<List<PostExtension>> getPostExtensions() {
        return postRepository.getPostExtensionsForPost(postId);
    }

    @Override
    public void addPostExtension(String data) {
        PostExtension newPostExtension = new PostExtension(postId, data);
        postRepository.addPostExtension(newPostExtension);
    }

    @Override
    public String getCurrentUser() {
        return RepositoryLocator.getUserRepository().getId();
    }

    @Override
    public void upVote() {
        postRepository.upVote(postId);
    }

    @Override
    public void downVote() {
        postRepository.downVote(postId);
    }
}
