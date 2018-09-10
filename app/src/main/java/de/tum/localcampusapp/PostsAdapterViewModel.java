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

public class PostsAdapterViewModel extends AndroidViewModel {

    private LiveData<List<Post>> liveDataPosts;
    private long topicId;
    private PostRepository postRepository=null;
    private InMemoryPostRepository inMemoryPostRepository;


    private void createNewFakePosts(long postId, long id) throws DatabaseException {
            Post post = new Post(postId, "hello", 121221, id, "Alex", new Date(1992, 8, 23)
                    , new Date(2018, 6, 22), "sample Post - postId: "+postId, 6);

            if(postRepository != null){
                postRepository.insertPost(post);
            }
            else{
                inMemoryPostRepository.insertPost(post);

            }
            if (postRepository!=null){
                liveDataPosts = postRepository.getPostsforTopic(topicId);
                return;
            }
            liveDataPosts = inMemoryPostRepository.getPostsforTopic(topicId);
    }


    public PostsAdapterViewModel(long topicId, Application application, Context context) throws DatabaseException {
        super(application);
        this.topicId = topicId;

        //postRepository = RepositoryLocator.getPostRepository(context);
        inMemoryPostRepository = new InMemoryPostRepository();
        createNewFakePosts(1, topicId);
        createNewFakePosts(2, topicId);
        createNewFakePosts(3, topicId);
        createNewFakePosts(4, topicId);
    }


    public LiveData<List<Post>> getLiveDataPosts() {
        return liveDataPosts;
    }
}
