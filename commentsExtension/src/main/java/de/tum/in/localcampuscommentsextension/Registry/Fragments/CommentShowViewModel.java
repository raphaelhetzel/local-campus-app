package de.tum.in.localcampuscommentsextension.Registry.Fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.localcampuscommentsextension.Registry.ExtensionType.Comment;
import de.tum.localcampuslib.ShowPostDataProvider;

public class CommentShowViewModel {

    private static final String ATTR_DATA = "text";

    private ShowPostDataProvider showPostDataProvider;

    private LiveData<List<Comment>> liveDataComments;
    private LiveData<List<Comment>> liveDataCommentsFiltered;


    public CommentShowViewModel(ShowPostDataProvider showPostDataProvider){
        this.showPostDataProvider = showPostDataProvider;
    }




    public LiveData<List<Comment>> getFilteredLiveComments(){
        return Transformations.map(liveDataComments, (List<Comment> listComments) -> {
            List<Comment> workingComments = new ArrayList<>();
            for (Comment comment : listComments) {
                if(comment!=null){
                    workingComments.add(comment);
                }
            }
            return workingComments;
        });
    }


    public void upVote(){
        //TODO: Call API method after it is available
    }

    public void downVote(){
        //TODO: Call API method after it is available
    }



/*
        postDate.setText(postMapper.getDate());

        int color = postMapper.getColor();
        rootLayout.setBackgroundColor(color);
        postParentLayout.setBackgroundColor(color);

        postType.setText(postMapper.getType());
        numLikes.setText(postMapper.getLikesString());

        postText.setText(postMapper.getTextComment());
 */

    public LiveData<String> getText() {
        return Transformations.map(this.showPostDataProvider.getPost(), post -> {
            if(post != null) {
                return post.getData();
            }
            return "No Post";
        });
    }


    public LiveData<Long> getScore() {
            return Transformations.map(this.showPostDataProvider.getPost(), post -> {
                if(post != null) {
                    return post.getScore();
                }
               return new Long(0);
            });
    }

}
