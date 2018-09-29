package de.tum.in.piechartsurvey.ExtensionsType;

import org.json.JSONException;
import org.json.JSONObject;

public class Vote {

    public static final String ATTR_ID = "id";

    private int id;
    private String creator;

    private int clicksSum = 0;
    private float scoreInPerctentage = 0;


    public static Vote getValidVote(String creator, String data) {
        try {
            JSONObject obj = new JSONObject(data);
            int id = obj.getInt(ATTR_ID);
            return new Vote(creator, id);
        } catch (JSONException e) {
            return null;
        }
    }


    private Vote(String creator, int id) {
        this.creator = creator;
        this.id = id;
    }

    public Vote(int id, int value) {
        this.id = id;
        clicksSum = value;
    }

    public void setScoreInPerctentage(float score) {
        if (score >= 0 && score <= 100) {
            this.scoreInPerctentage = score;
        }
    }

    public float getScoreInPerctentage() {
        return scoreInPerctentage;
    }

    public String makeJsonCommentOutput() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public static String makeJsonCommentOutput(int id) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(ATTR_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

    public int getId() {
        return id;
    }

    public void increaseClickSum() {
        clicksSum++;
    }

    public int getClicksSum() {
        return clicksSum;
    }

    public int getColor() {
        return Colors.colorsInternal[id];
    }

    public String getCreator() {
        return creator;
    }

}
