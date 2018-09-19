package de.tum.localcampusapp.extensioninterface;

import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampuslib.AddPostDataProvider;
import de.tum.localcampusapp.entity.Post;

public class RealAddPostDataProvider implements AddPostDataProvider {

    private long topicId;
    private String postType;

    public RealAddPostDataProvider(long topicId, String postType) {
        this.topicId = topicId;
        this.postType = postType;
    }

    @Override
    public void addPost(String data) {
        Post newPost = new Post(topicId, postType, data);
        RepositoryLocator.getPostRepository().addPost(newPost);
    }
}
