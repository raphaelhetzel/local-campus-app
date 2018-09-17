package de.tum.localcampusapp.postTypes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostMapperHelper {

    private MutableLiveData<List<PostMapper>> livePostMapper;
    private LiveData<List<Post>> listPosts;
    long topicId;
    long postId;
    private PostRepository postRepository;
    private LiveData<Post> livePost;


    public PostMapperHelper(long topicId){
        postRepository = RepositoryLocator.getPostRepository();
        listPosts = postRepository.getPostsforTopic(topicId);
        this.topicId = topicId;
    }

    public PostMapperHelper(long postId, boolean arg){
        postRepository = RepositoryLocator.getPostRepository();
        livePost = postRepository.getPost(postId);
        this.postId = postId;
    }

    public PostMapperHelper(LiveData<List<Post>> listPosts){
        this.listPosts = listPosts;
    }


    public LiveData<PostMapper> tranformPost(){
        return Transformations.map(livePost, (Post livePost) -> {
           return new PostMapper(livePost);
        });
    }


    public List<PostMapper> comparison(List<PostMapper> pm){
        Comparator<PostMapper> pmComparator = Comparator.comparingDouble(PostMapper::getInternalRating);
        pm.sort(pmComparator);
        Collections.reverse(pm);
        return pm;
    }


    public LiveData<List<PostMapper>> transformPosts(){
        ArrayList<Long> ids = new ArrayList<>();
        return Transformations.map(listPosts, (List<Post> listPosts) -> {
//            Log.d("ListPosts: ", Integer.toString(listPosts.size())+ " postMappersLisT: "+postMappers.size());
            List<PostMapper> postMappers= new ArrayList<>();
            for (Post post : listPosts) {
                postMappers.add(new PostMapper(post));
            }
//            for (Post post : listPosts) {
//                PostMapper pp = new PostMapper(post);
//                if(!ids.contains(pp.getId())){
//                    postMappers.add(pp);
//                    ids.add(post.getId());
//                }
//            }
            return comparison(postMappers);
        });

    }

}