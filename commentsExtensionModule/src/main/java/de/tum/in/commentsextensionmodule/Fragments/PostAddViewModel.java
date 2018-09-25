package de.tum.in.commentsextensionmodule.Fragments;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import de.tum.in.commentsextensionmodule.Generator.ColorGenerator;
import de.tum.localcampuslib.AddPostDataProvider;

public class PostAddViewModel {

    private static final String ATTR_DATA = "text";
    private static final String ATTR_COLOR = "color";

    private AddPostDataProvider addPostDataProvider;
    private Context context;


    public PostAddViewModel(AddPostDataProvider addPostDataProvider, Context context) {
        this.addPostDataProvider = addPostDataProvider;
        this.context = context;
    }


    public void addPost(String data) {
        String jsonData = makeJsonPostOutput(data, context);
        this.addPostDataProvider.addPost(jsonData);
    }

    private String makeJsonPostOutput(String textInput, Context context) {
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
