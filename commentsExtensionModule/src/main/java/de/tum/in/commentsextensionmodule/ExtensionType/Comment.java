package de.tum.in.commentsextensionmodule.ExtensionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Comment {

    private String data;
    private Date dateCreated;

    public static final String ATTR_DATA = "text";
    //comment only needs one attribute: the textinput

    public static Comment getValidComment(String data, Date dateCreated) {
        //checks if the JSON from the backend can be parsed properly
        try {
            JSONObject obj = new JSONObject(data);
            String text = obj.getString(ATTR_DATA);
            return new Comment(text, dateCreated);
        } catch (JSONException e) {
            return null;
        }
    }

    private Comment(String data, Date dateCreated) {
        this.data = data;
        this.dateCreated = dateCreated;
    }


    public String getData() {
        return data;
    }

    public Date getCreateDate() {
        return dateCreated;
    }

}