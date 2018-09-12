package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;

public class PostsViewModel extends ViewModel{

    private LiveData<List<Post>> liveDataPosts;
    private long topicId;
    private PostRepository postRepository;


    public PostsViewModel(long topicId, Context context) throws DatabaseException{
        this.topicId = topicId;

        //FakeDataGenerator.getInstance().createSeveralFakePosts(4, topicId);

        postRepository = RepositoryLocator.getPostRepository();
        liveDataPosts = postRepository.getPostsforTopic(topicId);
    }

    public void addPost(String dataText) throws DatabaseException {
        long id = FakeDataGenerator.getInstance().getPostId();
        String uuid = UUID.randomUUID().toString();
        //long typeId = FakeDataGenerator.getInstance().getTypeId();
        String typeId = "1";
        String creator = "user";
        Date createdAt = new Date();
        String data = dataText;

        postRepository.addPost(new Post(id, uuid, typeId, topicId, creator, createdAt, data));
    }

    public LiveData<List<Post>> getLiveDataPosts() {
        return liveDataPosts;
    }

}
