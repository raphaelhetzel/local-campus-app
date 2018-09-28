package de.tum.in.commentsextensionmodule.Fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.commentsextensionmodule.ExtensionType.Comment;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampuslib.entity.IPost;
import de.tum.localcampuslib.entity.IPostExtension;

public class CommentShowViewModel {

    private ShowPostDataProvider showPostDataProvider;

    private LiveData<IPost> livePost;
    private LiveData<List<IPostExtension>> livePostExtension;

    private LiveData<List<Comment>> liveDataComments;
    private LiveData<List<Comment>> liveDataCommentsFiltered;


    public CommentShowViewModel(ShowPostDataProvider showPostDataProvider){
        this.showPostDataProvider = showPostDataProvider;
        livePost = (LiveData<IPost>) showPostDataProvider.getPost();
        livePostExtension = (LiveData<List<IPostExtension>>) showPostDataProvider.getPostExtensions();
    }


    public LiveData<IPost> getLivePost() {
        return livePost;
    }

    public LiveData<List<IPostExtension>> getExtension(){
        return livePostExtension;
    }


    public void addComment(String text){
        String jsonFormattedText = makeJsonCommentOutput(text);
        showPostDataProvider.addPostExtension(jsonFormattedText);
    }


    public LiveData<List<Comment>> getLiveComments() {
        return Transformations.map(livePostExtension, (List<IPostExtension> livePostExtension) -> {
            List<Comment> workingComments = new ArrayList<>();
            for (IPostExtension iPostExtension : livePostExtension){
                Comment comment = Comment.getValidComment(iPostExtension.getData(), iPostExtension.getCreatedAt());
                if(comment!=null){
                    workingComments.add(comment);
                }
            }
            return workingComments;
        });
    }


    private String makeJsonCommentOutput(String textInput) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(Comment.ATTR_DATA, textInput);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }


    public void upVote(){
        showPostDataProvider.upVote();
    }

    public void downVote(){
        showPostDataProvider.downVote();
    }

}
