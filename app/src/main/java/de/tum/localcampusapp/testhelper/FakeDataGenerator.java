package de.tum.localcampusapp.testhelper;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.postTypes.Comment;
import de.tum.localcampusapp.postTypes.CommentHelper;
import de.tum.localcampusapp.repository.PostRepository;
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
                topicRepository.insertTopic(new Topic(id, getNameWithId(elementsName, id)));
            }
            else{
                topicRepository.insertTopic(new Topic(id, getNameWithId(elementsName, id)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createSeveralFakeComments(int count, CommentHelper commentHelper, long postId, long commentId){
        for(int i=0; i<count; i++){
            createNewFakeComment(commentHelper, postId, commentId++);
        }
    }

    public void createSeveralFakePosts(int count, long id) throws DatabaseException {
        for(int i=0; i<count; i++){
            createNewFakePost(id);
        }
    }


    public void createNewFakeComment(CommentHelper commentHelper, long postId, long commentId){
        Comment comment = new Comment(postId, commentId, "Sample Comment - PostId: "+postId+", CommentId: "+commentId, new Date(1992, 8, 23));
        commentHelper.insertComment(comment);
    }

    public void createNewFakePost(long id) throws DatabaseException {
        long currPostId = getPostId();
        Post post = new Post(currPostId, "hello", 121221, id, "Alex", new Date(1992, 8, 23)
                , new Date(2018, 6, 22), "sample Post - postId: "+ currPostId, 6);
        postRepository.insertPost(post);
    }

}
