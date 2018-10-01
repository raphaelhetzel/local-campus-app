package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.postTypes.PostMapper;
import de.tum.localcampusapp.postTypes.PostMapperHelper;

public class PostsAdapterModel extends ViewModel {

    private long topicId;
    private LiveData<List<PostMapper>> liveDataMapped;
    private Context context;


    public PostsAdapterModel(long topicId, Context context) {
        this.topicId = topicId;
        PostMapperHelper postMapperHelper = new PostMapperHelper(topicId);
        this.liveDataMapped = postMapperHelper.transformPosts();
        this.context = context;
    }

    public PostsAdapterModel(LiveData<List<Post>> liveDataPosts) {
        PostMapperHelper postMapperHelper = new PostMapperHelper(liveDataPosts);
        this.liveDataMapped = postMapperHelper.transformPosts();
    }

    //Sorts the list according to the internal rating which will be calculated in PostMapper
    //according to the number of likes and the age of a post
    public List<PostMapper> comparison(List<PostMapper> pm) {
        Comparator<PostMapper> pmComparator = Comparator.comparingDouble(PostMapper::getInternalRating);
        pm.sort(pmComparator);
        Collections.reverse(pm);
        return pm;
    }

    public LiveData<List<PostMapper>> getLiveData() {
        return liveDataMapped;
    }

}
