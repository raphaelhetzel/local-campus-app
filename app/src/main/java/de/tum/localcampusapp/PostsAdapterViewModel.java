package de.tum.localcampusapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryPostRepository;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;

public class PostsAdapterViewModel extends AndroidViewModel {

    private LiveData<List<Post>> liveDataPosts;
    private long topicId;


    public PostsAdapterViewModel(long topicId, Application application) throws DatabaseException {
        super(application);

        this.topicId = topicId;

        //TODO: Change this
        InMemoryPostRepository inMemoryPostRepository = new InMemoryPostRepository();

        Post post1 = new Post(121, "hello" , 121221, topicId, "Alex", new Date(1992, 8, 23),
                new Date(2018, 6, 22), "sample data", 6);
        Post post2 = new Post(122, "schiptore" , 1212, topicId, "Sami", new Date(1992, 8, 23),
                new Date(2018, 6, 22), "2nd data", 2);
        Post post3 = new Post(123, "hola" , 121221, topicId, "Alex", new Date(1992, 8, 23),
                new Date(2018, 6, 22), "3rd sample data", 6);

        ArrayList<Post> posts = new ArrayList<Post>();
        posts.add(post1);
        posts.add(post2);
        posts.add(post3);

        inMemoryPostRepository.insertPost(post1);
        inMemoryPostRepository.insertPost(post2);
        inMemoryPostRepository.insertPost(post3);

        liveDataPosts = inMemoryPostRepository.getPostsforTopic(topicId);

    }

    public LiveData<List<Post>> getLiveDataPosts() {
        return liveDataPosts;
    }
}
