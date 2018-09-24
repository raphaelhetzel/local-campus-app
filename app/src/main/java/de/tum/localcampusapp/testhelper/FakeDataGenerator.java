package de.tum.localcampusapp.testhelper;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.generator.JSONParser;
import de.tum.localcampusapp.postTypes.Comment;
import de.tum.localcampusapp.postTypes.CommentHelper;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.repository.TopicRepository;

public class FakeDataGenerator {

    private static final int TOPIC_ID_MAX = 100000;
    private static final int POST_ID_MAX = 10000000;
    private static final int TYPE_ID_MAX = 1000;

    private TopicRepository topicRepository;
    private PostRepository postRepository;

    private ArrayList<Long> topicsIdList =new ArrayList<>();
    private ArrayList<Long> postsIdList = new ArrayList<>();
    private ArrayList<Long> typeIdList = new ArrayList<>();
    private ArrayList<Long> commentIdList = new ArrayList<>();

    private static FakeDataGenerator instance = new FakeDataGenerator();

    private FakeDataGenerator() {
        this.topicRepository = RepositoryLocator.getTopicRepository();
        this.postRepository = RepositoryLocator.getPostRepository();
    }

    public static FakeDataGenerator getInstance(){
        return instance;
    }

    public long getTypeId(){
        long num = (long) (Math.random()* TYPE_ID_MAX) +1;
        while (typeIdList.contains(num)){
            num = (long) (Math.random()* TYPE_ID_MAX) +1;
        }
        typeIdList.add(num);
        return num;
    }

    public long getPostId(){
        long num = (long) (Math.random()* POST_ID_MAX) +1;
        while (postsIdList.contains(num)){
            num = (long) (Math.random()* POST_ID_MAX) +1;
        }
        postsIdList.add(num);
        return num;
    }

    public long getTopicId(){
        long num = (long) (Math.random()* TOPIC_ID_MAX) +1;
        while(topicsIdList.contains(num)){
            num = (long) (Math.random()* TOPIC_ID_MAX) +1;
        }
        topicsIdList.add(num);
        return num;
    }

    public long getCommentId(){
        long num = (long) (Math.random()* POST_ID_MAX) +1;
        while (commentIdList.contains(num)){
            num = (long) (Math.random()* POST_ID_MAX) +1;
        }
        commentIdList.add(num);
        return num;
    }

    public String getNameWithId(String elementsName, long id){
        return elementsName + Long.toString(id);
    }

    public void insertSeveralTopics(String elementsName, int fakeDataCount){
        for(int i=0; i<fakeDataCount; i++){
            insertNewTopic(elementsName);
        }
    }

    public void setTopicsRepo(TopicRepository topicRepository){
        this.topicRepository = topicRepository;
    }

    public void setPostRepo(PostRepository postRepo){
        this.postRepository = postRepo;
    }

    public void insertNewTopic(String elementsName) {
        long id = getTopicId();
        try {
            Log.d("FakeDataGenerator", "insert: "+ getNameWithId(elementsName, id));
            if(topicRepository!=null){
                topicRepository.insertTopic(getNameWithId(elementsName, id), "no_location");
            }
            else{
                topicRepository.insertTopic(getNameWithId(elementsName, id), "no_location");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createSeveralFakeComments(int count, CommentHelper commentHelper, long postId, long commentId){
        for(int i=0; i<count; i++){
            createNewFakeComment(postId, commentId++);
        }
    }

    public void createSeveralFakePosts(int count, long id, Context context) throws DatabaseException {
        for(int i=0; i<count; i++){
            createNewFakePost(id, context);
        }
    }


    public void createNewFakeComment(long postId, long commentId) {
        // Comment Helper needs to be deleted as it serves no purpose
        postRepository.addPostExtension(new PostExtension(postId, "Sample Comment - PostId: "+postId+", CommentId: "));
    }

    public void createNewFakePost(long id, Context context) throws DatabaseException {
        long currPostId = getPostId();
        Post post = new Post(id,"1", "sample Post - postId: "+ currPostId);
        postRepository.addPost(post);

    }

}