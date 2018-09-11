package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.repository.InMemoryPostRepository;
import de.tum.localcampusapp.postTypes.Comment;
import de.tum.localcampusapp.postTypes.CommentHelper;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;
import de.tum.localcampusapp.testhelper.FakeDataGenerator;

public class PostCommentViewModel extends ViewModel {

    private LiveData<Post> liveDataPost;
    private LiveData<List<Comment>> liveDataComments;
    private long postId;
    PostRepository postRepository;
    CommentHelper commentHelper;


    public PostCommentViewModel(long postId, Context context) throws DatabaseException{

        this.postId = postId;

        postRepository = RepositoryLocator.getPostRepository();
        liveDataPost = postRepository.getPost(postId);

        //Fake Comments for Fake Post
        commentHelper = new CommentHelper();
        FakeDataGenerator.getInstance().createSeveralFakeComments(3, commentHelper, postId, 1);
        liveDataComments = commentHelper.getCommentsforPost(postId);
    }

    public void addComment(String commentData) throws DatabaseException {
            long id = FakeDataGenerator.getInstance().getPostId();
            long commentId = FakeDataGenerator.getInstance().getCommentId();

            commentHelper.insertComment(new Comment(postId, commentId, commentData, new Date()));
    }

    public LiveData<Post> getLiveDataPost() {
        return liveDataPost;
    }

    public LiveData<List<Comment>> getLiveDataComments() {
        return liveDataComments;
    }

}


