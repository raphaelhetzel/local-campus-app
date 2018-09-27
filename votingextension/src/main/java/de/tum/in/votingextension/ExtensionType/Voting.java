package de.tum.in.votingextension.ExtensionType;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Voting {

    public static final String ATTR_DATA = "vote";

    private float tempValue;
    private String creator;


    public static Voting getValidVote(String data, String creator) {
        try {
            JSONObject obj = new JSONObject(data);
            float value = (float) obj.getDouble(ATTR_DATA);
            return new Voting(value, creator);
        } catch (JSONException e) {
            return null;
        }
    }

    private Voting(float data, String creator) {
        this.tempValue = data;
        this.creator = creator;
    }


    public static String makeJsonCommentOutput(float textInput) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_DATA, Float.toString(textInput));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }


    public float getTempValue() {
        return tempValue;
    }

    public String getCreator(){
        return creator;
    }

}