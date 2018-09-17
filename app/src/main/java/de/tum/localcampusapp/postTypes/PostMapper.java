package de.tum.localcampusapp.postTypes;

import android.arch.lifecycle.LiveData;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.generator.DateTransformer;
import de.tum.localcampusapp.generator.JSONParser;
import de.tum.localcampusapp.repository.PostRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;

public class PostMapper {

    private String date;
    private String textComment;
    private int color;
    private long topicId;
    private Post post;
    private float internalRating;


    public PostMapper(Post post){
        this.post = post;
    }

    public String getDate(){
        return DateTransformer.getTimeDate(post.getCreatedAt());
    }

    public long getLikes(){
        return post.getScore();
    }

    public String getLikesString(){
        return Long.toString(post.getScore());
    }

    public long getId(){
        return post.getId();
    }

    public String getIdString(){
        return Long.toString(post.getId());
    }

    public String getTextComment() throws JSONException {
        //return post.getData();
       String text = JSONParser.getText(post.getData());
       return text;
    }

    public int getColor() throws JSONException {
        int color = JSONParser.getColor(post.getData());
        return color;
    }


    public String getType(){
        return post.getTypeId();
    }

    private void setInternalRating(long likes){

        float downSet = new Float(0.1);
        Date currDate = new Date();
        long diff =  post.getCreatedAt().getDay() - currDate.getDay();
        long diffDays = (TimeUnit.DAYS.convert(diff, TimeUnit.DAYS));
        this.internalRating = likes - downSet * ((float) diffDays);
        Log.d("setScore", "likes: "+likes+ " rating: "+internalRating+ " id: "+getId());
        //return internalRating;
    }

    public float getInternalRating(){
        float downSet = new Float(0.1);
        Date currDate = new Date();
        long diff =  post.getCreatedAt().getDay() - currDate.getDay();
        long diffDays = (TimeUnit.DAYS.convert(diff, TimeUnit.DAYS));
        this.internalRating = post.getScore() - downSet * ((float) diffDays);
        return internalRating;
    }

}