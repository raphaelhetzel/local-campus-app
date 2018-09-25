package de.tum.in.votingextension.ExtensionType;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Voting {

    public static final String ATTR_DATA = "vote";
    public static final float TEMP_INIT = 20;
    public static final float TEMP_MAX = 40;
    public static final float TEMP_MIN = -10;
    public static final float TEMP_CHANGE = (float) 0.5;

    private long postId;
    private long voteId;
    private float tempValue;

    private String creator;

    public static Voting getValidVote(long postId, long voteId, String data, String creator) {
        try {
            JSONObject obj = new JSONObject(data);
            float value = (float) obj.getDouble(ATTR_DATA);
            Log.d("getValidData", "data "+data+" gotValue: "+value);
            return new Voting(postId, voteId, value, creator);
        } catch (JSONException e) {
            return null;
        }
    }

    private Voting(long postId, long voteId, float data, String creator) {
        this.postId = postId;
        this.voteId = voteId;
        this.tempValue = data;
        this.creator = creator;
    }


    public static String makeJsonCommentOutput(float textInput) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_DATA, Float.toString(textInput));
            Log.d("makeJsonCommentOutput", "makeJsonCommentOutput: "+Float.toString(textInput));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public static boolean checkIfValidValue(float value){
        if(value > TEMP_MIN && value < TEMP_MAX ){
            return true;
        }
        return false;
    }

    public float getTempValue() {
        return tempValue;
    }

    public String getCreator(){
        return creator;
    }

}