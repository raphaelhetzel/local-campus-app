package de.tum.in.commentsextensionmodule.Generator;

import org.json.JSONException;
import org.json.JSONObject;

import de.tum.localcampuslib.entity.IPost;

public class PostMapper {
    private String text;
    private int color;
    IPost post;

    private static final String ATTR_COLOR = "color";
    private static final String ATTR_DATA = "text";


    public static PostMapper getWorkingPostMapper(IPost post) {
        try {
            JSONObject obj = new JSONObject(post.getData());
            String text = obj.getString(ATTR_DATA);
            int color = obj.getInt(ATTR_COLOR);
            return new PostMapper(post, text, color);
        } catch (JSONException e) {
            return null;
        }
    }

    private PostMapper(IPost post, String text, int color) {
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
        return post.getTypeId();
    }
}