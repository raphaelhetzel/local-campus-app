package de.tum.in.localcampuscommentsextension.Registry.Generator;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import de.tum.localcampuslib.ShowPostDataProvider;

public class PostMapper {
    private String text;
    private int color;
    private float internalRating;
    private String date;

    private static final String ATTR_COLOR = "color";
    private static final String ATTR_DATA = "text";

    private ShowPostDataProvider showPostDataProvider;

    public static PostMapper getWorkingPostMapper(ShowPostDataProvider showPostDataProvider) {
        try {
            JSONObject obj = new JSONObject(showPostDataProvider.getPost().getValue().getData());
            String text = obj.getString(ATTR_DATA);
            int color = obj.getInt(ATTR_COLOR);
            return new PostMapper(showPostDataProvider, text, color);
        } catch (JSONException e) {
            return null;
        }
    }

    private PostMapper(ShowPostDataProvider showPostDataProvider, String text, int color) {
        this.showPostDataProvider = showPostDataProvider;
        this.text = text;
        this.color = color;


    }

    /*
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
*/

    public static String makeJsonPostOutput(String textInput, Context context) {
      //  int color = ColorGenerator.getColor(context);
        //TODO: Figure out what to do with colors as we cannot reach apps resources
        int color = 10;
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