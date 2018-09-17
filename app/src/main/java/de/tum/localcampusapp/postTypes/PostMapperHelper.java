package de.tum.localcampusapp.postTypes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostMapperHelper {

        /*
    private PostRepository postRepository;
    private LiveData<List<Post>> livePosts;
    private LiveData<List<PostMapper>> liveDataPostMapperList;

    public PostMapperHelper(long topicId){
        postRepository = RepositoryLocator.getPostRepository();
        livePosts = postRepository.getPostsforTopic(topicId);
    }

    public LiveData<List<PostMapper>> getPostMapperList(){
        return liveDataPostMapperList;
    }

    public void tranformPost(){
        //TODO:
    }
    */

    private MutableLiveData<List<PostMapper>> livePostMapper;
    private LiveData<List<Post>> listPosts;
    long topicId;
    long postId;
    private PostRepository postRepository;
    private LiveData<Post> livePost;

    public PostMapperHelper(long topicId){
       // this.livePostMapper = new MutableLiveData<>();
       // this.livePostMapper.setValue(new ArrayList<>());
        postRepository = RepositoryLocator.getPostRepository();
        listPosts = postRepository.getPostsforTopic(topicId);
        this.topicId = topicId;
    }
    

    public PostMapperHelper(LiveData<List<Post>> listPosts){
        //this.livePostMapper = new MutableLiveData<>();
        //this.livePostMapper.setValue(new ArrayList<>());
        this.listPosts = listPosts;
    }

    public LiveData<List<PostMapper>> transformPosts(){
        List<PostMapper> postMappers= new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        return Transformations.map(listPosts, (List<Post> listPosts) -> {
            Log.d("ListPosts: ", Integer.toString(listPosts.size())+ " postMappersLisT: "+postMappers.size());

            for (Post post : listPosts) {
                PostMapper pp = new PostMapper(post);
                if(!ids.contains(pp.getId())){
                    Log.d("ListItems: ", Integer.toString(listPosts.size()));
                    postMappers.add(pp);
                    ids.add(post.getId());
                }
            }

            Log.d("ListMappers: ", Integer.toString(postMappers.size()));
            return postMappers;
        });

    }

    /*
    public LiveData<List<PostMapper>> sortAccordingLikesAndDate(){
        //TODO: fill
    }
    */

    public List<Post> transormLivePosts(){

        /*
        MediatorLiveData<List<Post>> liveData = new MediatorLiveData<>();
        liveData.addSource(posts, posts -> {
            List<Post> items = posts.stream().filter(p -> p.getTopicId() == topicId).collect(Collectors.toList());
            for (Post post : items) {
                post.setScore(calculateScore(post.getId(), votes.getValue()));
            }
            liveData.setValue(items);
         */
        List<Post> posts = new ArrayList<>(listPosts.getValue());
        Log.d("ListPost", "transormLivePosts: "+posts.size());
        return posts;

        /*

        Transformations.map(listPosts, listPosts -> {
            posts = listPosts.stream().collect(Collectors.toList());
            Log.d("ListPost", "transormLivePosts: "+posts.size());
            return posts;
        });
        Log.d("ListPost", "transormLivePosts: + NULL");
        return null;
        */
    }

    public LiveData<List<PostMapper>> getLivePostMapperList(){


        /*
        return Transformations.map(topics, topics -> {
            List<Topic> items = topics.stream().filter(p -> p.getId() == id).collect(Collectors.toList());
         */

        //List<Post> posts = listPosts.getValue();
        List<Post> posts = transormLivePosts();
        for (Post post : posts) {
        List<PostMapper> temp = new ArrayList<>(livePostMapper.getValue());
        temp.add(new PostMapper(post));
        livePostMapper.setValue(temp);
        }
        return livePostMapper;
    }

    public LiveData<PostMapper> getLivePostMapper(long id){
        return Transformations.map(livePostMapper, livePostMapper ->  {
            List<PostMapper> items = livePostMapper.stream()
                    .filter(p -> p.getId() == id).collect(Collectors.toList());
            if(items.size() == 1){
                return items.get(0);
            }
            return null;
        });
    }

    public LiveData<List<PostMapper>> getCommentsforPost() {
        return Transformations.switchMap(livePostMapper, livePostMapper -> {
            MutableLiveData<List<PostMapper>> listPostMappers = new MutableLiveData<>();
            List<PostMapper> items = livePostMapper.stream().collect(Collectors.toList());
            listPostMappers.setValue(items);
            return listPostMappers;
        });
    }

    public void updatePostMapper(PostMapper postMapper){
        ArrayList<PostMapper> postMappers = new ArrayList<>(this.livePostMapper.getValue());
        for (int i = 0; i < postMappers.size(); i++) {
            if (postMappers.get(i).getId() == postMapper.getId()) {
                postMappers.set(i, postMapper);
                this.livePostMapper.setValue(postMappers);
                return;
            }
        }
    }

    public void insertPostMapper(PostMapper postMapper){
        List<PostMapper> temp = new ArrayList<>(livePostMapper.getValue());
        temp.add(postMapper);
        livePostMapper.setValue(temp);
    }

}
