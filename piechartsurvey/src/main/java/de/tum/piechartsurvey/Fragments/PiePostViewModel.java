package de.tum.piechartsurvey.Fragments;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import de.tum.piechartsurvey.ExtensionsType.VoteOption;
import de.tum.piechartsurvey.ExtensionsType.VotingOptions;
import de.tum.piechartsurvey.Generator.ColorGenerator;
import de.tum.localcampuslib.AddPostDataProvider;

public class PiePostViewModel {

    public static final String ATTR_DATA = "text";
    public static final String ATTR_COLOR = "color";

    private String error = "The maximum number of possibilities is reached";
    private static final String optionsError = "You must add at least 2 options";
    private static final String titleError = "Fill out all input fields";

    private AddPostDataProvider addPostDataProvider;
    private Context context;

    private ArrayList<VoteOption> voteOptions;


    public PiePostViewModel(AddPostDataProvider addPostDataProvider, Context context) {
        this.addPostDataProvider = addPostDataProvider;
        this.context = context;
        voteOptions = new ArrayList<>();
    }

    public boolean saveSurvey(String data) {
        if (!checkIfValidSurvey(data)) return false;

        try {
            String jsonData = makeJsonPostOutput(data);
            Log.d("add Post", "addPost: " + jsonData);

            this.addPostDataProvider.addPost(jsonData);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkIfValidSurvey(String title) {

        if (voteOptions.size() < 2) {
            error = optionsError;
            return false;
        }

        if (title.isEmpty()) {
            error = titleError;
            return false;
        }
        return true;
    }


    public String getErrorMessage() {
        return error;
    }

    public void resetVoteOptions() {
        voteOptions.clear();
    }


    public VoteOption addNewOption(String text) {
        int count = voteOptions.size();
        VoteOption voteOption = VoteOption.getValidOption(count, text);
        if (voteOption != null) {
            voteOptions.add(voteOption);
            return voteOption;
        }
        return null;
    }

    private org.json.simple.JSONArray createJsonArrayList() {
        org.json.simple.JSONArray list = new org.json.simple.JSONArray();
        for (VoteOption v : voteOptions) {
            list.add(v.getText());
        }
        return list;
    }

    private String makeJsonPostOutput(String textInput) {
        org.json.simple.JSONArray list = createJsonArrayList();

        int color = ColorGenerator.getColor(context);

        org.json.simple.JSONObject jsonObj = new org.json.simple.JSONObject();
        try {
            jsonObj.put(ATTR_COLOR, Integer.toString(color));
            jsonObj.put(ATTR_DATA, textInput);
            jsonObj.put(VotingOptions.ATTR_VOTE_OPTIONS, list);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }
}