package de.tum.localcampusapp.generator;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser{

    private static final String ATTR_COLOR = "color";
    private static final String ATTR_DATA = "text";


    public static int getColor(String textInput) throws JSONException {
        JSONObject obj = new JSONObject(textInput);
        return obj.getInt(ATTR_COLOR);
    }

    public static String getText(String textInput) throws JSONException {
        JSONObject obj = new JSONObject(textInput);
        return obj.getString(ATTR_DATA);
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

    public static String makeJsonCommentOutput(String textInput) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_DATA, textInput);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

}