package de.tum.localcampusapp.testhelper;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CommentHelper {

    private MutableLiveData<List<Comment>> liveComments;


    public CommentHelper(){
        this.liveComments = new MutableLiveData<>();
        this.liveComments.setValue(new ArrayList<>());
    }


    public LiveData<Comment> getLiveComment(long commentId){
        return Transformations.map(liveComments, liveComments ->  {
            List<Comment> items = liveComments.stream()
                    .filter(p -> p.getCommentId() == commentId).collect(Collectors.toList());
            if (items.size() == 1) {
                return items.get(0);
            }
            return null;
        });
    }


    public LiveData<List<Comment>> getCommentsforPost(long postId) {
        return Transformations.switchMap(liveComments, liveComments -> {
            MutableLiveData<List<Comment>> comment_posts = new MutableLiveData<>();
            List<Comment> items = liveComments.stream().filter(p -> p.getPostId() == postId).collect(Collectors.toList());
            comment_posts.setValue(items);
            return comment_posts;
        });
    }

    public void updateComment(Comment comment){
        ArrayList<Comment> comments = new ArrayList<>(this.liveComments.getValue());
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).getCommentId() == comment.getCommentId()) {
                comments.set(i, comment);
                return;
            }
        }
    }

    public void insertComment(Comment comment){
        List<Comment> temp = new ArrayList<>(liveComments.getValue());
        temp.add(comment);
        liveComments.setValue(temp);
    }

}
