package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostsViewModel extends ViewModel {

    private LiveData<List<Post>> liveDataPosts;
    private long topicId;
    private PostRepository postRepository;


    public PostsViewModel(long topicId, Context context) throws DatabaseException {
        this.topicId = topicId;

        //FakeDataGenerator.getInstance().createSeveralFakePosts(4, topicId);

        postRepository = RepositoryLocator.getPostRepository();
        liveDataPosts = postRepository.getPostsforTopic(topicId);
    }

    public void addPost(String dataText) throws DatabaseException {
        String typeId = "1";
        String data = dataText;

        postRepository.addPost(new Post(topicId, typeId, data));
    }

    public LiveData<List<Post>> getLiveDataPosts() {
        return liveDataPosts;
    }

}
