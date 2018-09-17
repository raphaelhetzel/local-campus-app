package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.Date;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.generator.JSONParser;
import de.tum.localcampusapp.postTypes.PostMapper;
import de.tum.localcampusapp.postTypes.PostMapperHelper;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostsViewModel extends ViewModel {

    private LiveData<List<Post>> liveDataPosts;
    private long topicId;
    private PostRepository postRepository;

    private LiveData<List<PostMapper>> liveDataMapped;
    private PostMapperHelper postMapperHelper;

    public PostsViewModel(long topicId, Context context) throws DatabaseException{
        this.topicId = topicId;

        //TODO: delete after DB is up
        //FakeDataGenerator.getInstance().createSeveralFakePosts(4, topicId, context);

        postRepository = RepositoryLocator.getPostRepository();
        liveDataPosts = postRepository.getPostsforTopic(topicId);
        postMapperHelper = new PostMapperHelper(postRepository.getPostsforTopic(topicId));
        liveDataMapped = postMapperHelper.transformPosts();

    }

    public void addPost(String dataText, Context context) throws DatabaseException {
        String typeId = "1";

        String parsedJsonData = JSONParser.makeJsonPostOutput(dataText, context);

        postRepository.addPost(new Post(topicId, typeId, parsedJsonData));

    }

    public LiveData<List<PostMapper>> getLiveDataMapped(){
        return liveDataMapped;
    }

    public LiveData<List<Post>> getLiveDataPosts() {
        return liveDataPosts;
    }

}
