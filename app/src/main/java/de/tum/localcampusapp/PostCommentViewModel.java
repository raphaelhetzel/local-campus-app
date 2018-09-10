package de.tum.localcampusapp;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryPostRepository;
import de.tum.localcampusapp.testhelper.Comment;
import de.tum.localcampusapp.testhelper.CommentHelper;

public class PostCommentViewModel extends AndroidViewModel {
    private LiveData<Post> liveDataPost;
    private LiveData<List<Comment>> liveDataComments;
    private long postId;
    private Context context;
    InMemoryPostRepository inMemoryPostRepository;
    CommentHelper commentHelper;


    private void createNewFakePosts(long postId, long id) throws DatabaseException {
        Post post = new Post(postId, "hello", 121221, id, "Alex", new Date(1992, 8, 23)
                , new Date(2018, 6, 22), "sample Post - postId: "+postId, 6);

        inMemoryPostRepository.insertPost(post);
    }

    private void createNewFakeComment(long postId, long commentId){
        Comment comment = new Comment(postId, commentId, "Sample Comment - PostId: "+postId+", CommentId: "+commentId, new Date(1992, 8, 23));
        commentHelper.insertComment(comment);
    }


    public PostCommentViewModel(long postId, Application application, Context context) throws DatabaseException {
        super(application);

        this.context = context;
        this.postId = postId;

        //TODO: Later when Database is up: Change to getDatabase with postId
        //Fake Post Repo
        inMemoryPostRepository = new InMemoryPostRepository();
        createNewFakePosts(postId, 2345);
        liveDataPost = inMemoryPostRepository.getPost(postId);

        //Fake Comments for Fake Post
        commentHelper = new CommentHelper();
        createNewFakeComment(postId, 1);
        createNewFakeComment(postId, 2);
        createNewFakeComment(postId, 3);
        liveDataComments = commentHelper.getCommentsforPost(postId);

    }

    public LiveData<Post> getLiveDataPost() {
        return liveDataPost;
    }

    public LiveData<List<Comment>> getLiveDataComments() {
        return liveDataComments;
    }
}


