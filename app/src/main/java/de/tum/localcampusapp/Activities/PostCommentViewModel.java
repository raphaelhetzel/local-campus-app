package de.tum.localcampusapp.Activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.entity.PostExtension;
import de.tum.localcampusapp.exception.DatabaseException;
import de.tum.localcampusapp.postTypes.CommentLocater;
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
//        commentHelper = CommentLocater.getInstance().getCommentHelper();
//        FakeDataGenerator.getInstance().createSeveralFakeComments(3, commentHelper, postId, 1);
//        liveDataComments = commentHelper.getCommentsforPost(postId);
        liveDataComments = Transformations.map(postRepository.getPostExtensionsForPost(postId), postExtensions -> {
           return postExtensions.stream().map(postExtension -> {
                return new Comment(postId,
                        1,
                        postExtension.getData(),
                        postExtension.getCreatedAt());
           }).collect(Collectors.toList());
        });
    }

    public void addComment(String commentData) throws DatabaseException {
            postRepository.addPostExtension(new PostExtension(postId, commentData));
    }

    public LiveData<Post> getLiveDataPost() {
        return liveDataPost;
    }

    public LiveData<List<Comment>> getLiveDataComments() {
        return liveDataComments;
    }

}


