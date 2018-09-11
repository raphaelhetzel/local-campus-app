package de.tum.localcampusapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryPostRepository;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;

public class PostsViewModel extends ViewModel{

    private LiveData<List<Post>> liveDataPosts;
    private long topicId;
    private PostRepository postRepository;


    public PostsViewModel(long topicId, Context context) throws DatabaseException{
        this.topicId = topicId;

        FakeDataGenerator.getInstance().createSeveralFakePosts(4, 1, topicId);

        postRepository = RepositoryLocator.getPostRepository(context);
        liveDataPosts = postRepository.getPostsforTopic(topicId);
    }

    public LiveData<List<Post>> getLiveDataPosts() {
        return liveDataPosts;
    }

}
