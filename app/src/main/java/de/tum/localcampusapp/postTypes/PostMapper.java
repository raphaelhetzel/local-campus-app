package de.tum.localcampusapp.postTypes;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.tum.localcampusapp.entity.Post;
import de.tum.localcampusapp.generator.ColorGenerator;
import de.tum.localcampusapp.generator.DateTransformer;
import de.tum.localcampusapp.repository.ExtensionRepository;
import de.tum.localcampusapp.repository.RepositoryLocator;


public class PostMapper {
    private String text;
    private int color;
    private Post post;
    private float internalRating;
    private static final String ATTR_COLOR = "color";
    private static final String ATTR_DATA = "text";


    public static PostMapper getValidPostMapper(Post post) {
        try {
            JSONObject obj = new JSONObject(post.getData());
            String text = obj.getString(ATTR_DATA);
            int color = obj.getInt(ATTR_COLOR);
            return new PostMapper(post, text, color);
        } catch (JSONException e) {
            return null;
        }
    }

    private PostMapper(Post post, String text, int color) {
        this.post = post;
        this.text = text;
        this.color = color;
    }

    public String getDate() {
        return DateTransformer.getTimeDate(post.getCreatedAt());
    }

    public long getLikes() {
        return post.getScore();
    }

    public String getLikesString() {
        return Long.toString(post.getScore());
    }

    public long getId() {
        return post.getId();
    }

    public String getIdString() {
        return Long.toString(post.getId());
    }

    public String getTextComment() {
        return text;
    }

    public int getColor() {
        return color;
    }

    public String getType() {
        return RepositoryLocator.getExtensionRepository().getDescriptionFor(post.getTypeId());
    }

    public float getInternalRating() {
        this.internalRating = calculateRating(post.getCreatedAt(), post.getScore());
        return internalRating;
    }

    public float calculateRating(Date createdAt, long score){
        float downSet = new Float(0.000003);
        long diff = createdAt.getTime() - new Date().getTime();
        long diffSeconds = TimeUnit.SECONDS.convert(diff,TimeUnit.MILLISECONDS);
        float rating = score + downSet * ((float) diffSeconds) ;
        return rating;
    }

    public static String makeJsonPostOutput(String textInput, Context context) {
        int color = ColorGenerator.getColor(context);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_DATA, textInput);
            jsonObj.put(ATTR_COLOR, color);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }
}