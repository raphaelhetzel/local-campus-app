package de.tum.votingextension.Fragments;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import de.tum.votingextension.Generator.ColorGenerator;
import de.tum.localcampuslib.AddPostDataProvider;

public class VotingPostViewModel {

    public static final String ATTR_DATA = "text";

    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_COLOR = "color";
    public static final String ATTR_DEVICE_ID = "device_id";
    public static final String ATTR_TEMP_MIN = "temp_min";
    public static final String ATTR_TEMP_MAX = "temp_max";
    public static final String ATTR_TEMP_DEFAULT = "temp_default";
    public static final String ATTR_TEMP_CHANGE = "temp_change";

    private final String VARIABLES_NOT_SET = "Please fill in all input fields!";
    private final String MAX_MIN_ERROR = "Your max value has to be higher than you min value!";
    private final String TEMP_TOO_HIGH = "Your max temperature is too high!";
    private final String TEMP_TOO_LOW = "Your min temperature is too low!";
    private final String TEMP_DEFAULT_OUTBOUND = "Your default temperature is outbound";
    private final String TEMP_CHANGE_WRONG = "Your +/- temperature change value is inappropriate!";
    private final String DEVICE_ID_WRONG = "No Device found for entered Device-ID!";

    private final float TEMP_MIN = -50;
    private final float TEMP_MAX = 1000;
    private final float TEMP_CHANGE_MIN = 0;
    private final float TEMP_CHANGE_MAX = 100;


    private AddPostDataProvider addPostDataProvider;
    private Context context;

    private String postDesciption;
    private String deviceId;
    private String tempMin;
    private String tempMax;
    private String tempCurr;
    private String tempChange;

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }


    public VotingPostViewModel(AddPostDataProvider addPostDataProvider, Context context) {
        this.addPostDataProvider = addPostDataProvider;
        this.context = context;
    }

    public void setInputValues(String postDesciption,
                          String deviceId,
                          String tempMin,
                          String tempMax,
                          String tempCurr,
                          String tempChange){

        this.postDesciption = postDesciption;
        this.deviceId = deviceId;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.tempCurr = tempCurr;
        this.tempChange = tempChange;
    }


    private boolean variablesNotEmpty(){
        if(
            (postDesciption.isEmpty() ||
            deviceId.isEmpty() ||
            tempMin.isEmpty() ||
            tempMax.isEmpty() ||
            tempCurr.isEmpty() ||
            tempChange.isEmpty())
        ){
            errorMessage = VARIABLES_NOT_SET;
            return false;
        }
        return true;
    }

    private boolean maxMinError(){
        if(Float.parseFloat(tempMin)>=Float.parseFloat(tempMax)){
            errorMessage = MAX_MIN_ERROR;
            return false;
        }
        return true;
    }

    private boolean tempTooHigh(){
        if(Float.parseFloat(tempMax) > TEMP_MAX){
            errorMessage = TEMP_TOO_HIGH;
            return false;
        }
        return true;
    }

    private boolean tempTooLow(){
        if(Float.parseFloat(tempMin) < TEMP_MIN){
            errorMessage = TEMP_TOO_LOW;
            return false;
        }
        return true;
    }

    private boolean defaultTempOutbound(){
        if(Float.parseFloat(tempCurr)>TEMP_MAX || Float.parseFloat(tempCurr)<TEMP_MIN){
            errorMessage = TEMP_DEFAULT_OUTBOUND;
            return false;
        }
        return true;
    }

    private boolean tempChangeWrong(){
        if(Float.parseFloat(tempChange) < TEMP_CHANGE_MIN || Float.parseFloat(tempChange) > TEMP_CHANGE_MAX){
            errorMessage = TEMP_CHANGE_WRONG;
            return false;
        }
        return true;
    }

    private boolean deviceNotFound(){
        //Change this and call API after devices are availale
        return true;
    }


    public boolean addPost() {
        if(checkIfNoError()) {
            return false;
        }

        String jsonData = makeJsonPostOutput();
        this.addPostDataProvider.addPost(jsonData);
        return true;
    }


    private boolean checkIfNoError(){
        if(variablesNotEmpty() &&
                maxMinError() &&
                tempTooHigh() &&
                tempTooLow() &&
                tempChangeWrong() &&
                defaultTempOutbound() &&
                deviceNotFound()){
            return false;
        }
        return true;
    }

    private String makeJsonPostOutput() {
        int color = ColorGenerator.getColor();

        //All the following JSON data is needed in order to
        // set the limits for voting
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_DATA, postDesciption);
            jsonObj.put(ATTR_DESCRIPTION, postDesciption);
            jsonObj.put(ATTR_COLOR, color);

            jsonObj.put(ATTR_DEVICE_ID, deviceId);
            jsonObj.put(ATTR_TEMP_MIN, tempMin);

            jsonObj.put(ATTR_TEMP_MAX, tempMax);
            jsonObj.put(ATTR_TEMP_DEFAULT, tempCurr);

            jsonObj.put(ATTR_TEMP_CHANGE, tempChange);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

}