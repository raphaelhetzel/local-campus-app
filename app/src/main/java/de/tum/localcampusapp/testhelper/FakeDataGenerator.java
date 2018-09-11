package de.tum.localcampusapp.testhelper;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.Topic;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.postTypes.Comment;
import de.tum.localcampusapp.postTypes.CommentHelper;
import de.tum.localcampusapp.repository.InMemoryPostRepository;
import de.tum.localcampusapp.repository.InMemoryTopicRepository;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.TopicRepository;

public class FakeDataGenerator {

    private static final int ID_MAX = 100000;

    private TopicRepository topicRepository;
    private PostRepository postRepository;

    private ArrayList<Long> idList=new ArrayList<>();
    //private TopicRepository topicRepository=null;

    private static FakeDataGenerator instance = new FakeDataGenerator();

    private FakeDataGenerator() {
    }

    public static FakeDataGenerator getInstance(){
        return instance;
    }

    public long getId(){
        long num = (long) (Math.random()*ID_MAX) +1;
        while(idList.contains(num)){
            num = (long) (Math.random()*ID_MAX) +1;
        }
        idList.add(num);
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
        long id = getId();
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

    public void createSeveralFakePosts(int count, long postId, long id) throws DatabaseException {
        for(int i=0; i<count; i++){
            createNewFakePosts(postId++, id);
        }
    }

    public void createNewFakeComment(CommentHelper commentHelper, long postId, long commentId){
        Comment comment = new Comment(postId, commentId, "Sample Comment - PostId: "+postId+", CommentId: "+commentId, new Date(1992, 8, 23));
        commentHelper.insertComment(comment);
    }

    public void createNewFakePosts(long postId, long id) throws DatabaseException {
        Post post = new Post(postId, "hello", 121221, id, "Alex", new Date(1992, 8, 23)
                , new Date(2018, 6, 22), "sample Post - postId: "+postId, 6);
        postRepository.insertPost(post);
    }

}
