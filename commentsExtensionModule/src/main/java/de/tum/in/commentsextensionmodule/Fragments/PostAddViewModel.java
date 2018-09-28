package de.tum.in.commentsextensionmodule.Fragments;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import de.tum.in.commentsextensionmodule.Generator.ColorGenerator;
import de.tum.in.commentsextensionmodule.Generator.DateTransformer;
import de.tum.in.commentsextensionmodule.Registry.Registry;
import de.tum.localcampuslib.AddPostDataProvider;

public class PostAddViewModel {

    private final String ATTR_DATA = "text";
    private final String ATTR_COLOR = "color";

    private final String TEXT_EMPTY = "Please fill in your title!";

    private String errorMessage;

    private AddPostDataProvider addPostDataProvider;
    private Context context;
    private int color;


    public PostAddViewModel(AddPostDataProvider addPostDataProvider, Context context) {
        this.addPostDataProvider = addPostDataProvider;
        this.context = context;
        color = ColorGenerator.getColor(context);
    }

    public int getColor(){
        return color;
    }

    public String getTypePost(){
        return Registry.typeDescription;
    }

    public String getDate(){
        return DateTransformer.getTimeDate(new Date());
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public boolean addPost(String data) {
        if(noError(data)){
            String jsonData = makeJsonPostOutput(data, context);
            this.addPostDataProvider.addPost(jsonData);
            return true;
        }
        return false;
    }

    private boolean noError(String data){
        if(data.isEmpty()){
            errorMessage = TEXT_EMPTY;
            return false;
        }
        return true;
    }

    private String makeJsonPostOutput(String textInput, Context context) {

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
