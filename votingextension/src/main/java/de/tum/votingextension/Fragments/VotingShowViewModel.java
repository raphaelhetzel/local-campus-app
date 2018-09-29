package de.tum.votingextension.Fragments;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.tum.votingextension.ExtensionType.Voting;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampuslib.entity.IPost;
import de.tum.localcampuslib.entity.IPostExtension;

public class VotingShowViewModel {

    private ShowPostDataProvider showPostDataProvider;

    private float currTempVote;
    private float tempAvg;

    public static final String ATTR_DESCRIPTION = "text";
    public static final String ATTR_COLOR = "color";
    public static final String ATTR_DEVICE_ID = "device_id";
    public static final String ATTR_TEMP_MIN = "temp_min";
    public static final String ATTR_TEMP_MAX = "temp_max";
    public static final String ATTR_TEMP_DEFAULT = "temp_default";
    public static final String ATTR_TEMP_CHANGE = "temp_change";


    private String description;
    private String deviceId;
    private float tempMin;
    private float tempMax;
    private float tempDefault;
    private float tempChange;

    private LiveData<List<IPostExtension>> livePostExtensions;
    private LiveData<IPost> post;

    private boolean databaseVoteExists = false;
    private boolean inMemoryVoteExists = false;

    public VotingShowViewModel(ShowPostDataProvider showPostDataProvider, LifecycleOwner lifecycleOwner) {
        this.showPostDataProvider = showPostDataProvider;
        this.post = (LiveData<IPost>) showPostDataProvider.getPost();
        this.livePostExtensions = (LiveData<List<IPostExtension>>) showPostDataProvider.getPostExtensions();

        showPostDataProvider.getPostExtensions().observe(lifecycleOwner, postExtensions -> {
            databaseVoteExists = postExtensions.stream()
                    .anyMatch(postExtension -> postExtension.getCreatorId().equals(showPostDataProvider.getCurrentUser()));
        });
    }

    public void setPostVariables(String data){
            try {
                JSONObject obj = new JSONObject(data);
                description = obj.getString(VotingPostViewModel.ATTR_DESCRIPTION);
                deviceId = obj.getString(VotingPostViewModel.ATTR_DEVICE_ID);
                tempMin = Float.parseFloat(obj.getString(VotingPostViewModel.ATTR_TEMP_MIN));
                tempMax = Float.parseFloat(obj.getString(VotingPostViewModel.ATTR_TEMP_MAX));
                tempDefault = Float.parseFloat(obj.getString(VotingPostViewModel.ATTR_TEMP_DEFAULT));
                tempChange = Float.parseFloat(obj.getString(VotingPostViewModel.ATTR_TEMP_CHANGE));
            } catch (JSONException e) {
               // return null
        }
    }

    public LiveData<IPost> getPost(){
        return post;
    }

    public LiveData<List<Voting>> getLiveVotes() {
        return Transformations.map(livePostExtensions, (List<IPostExtension> livePostExtension) -> {
            List<Voting> validVotes = new ArrayList<>();
            for (IPostExtension iPostExtension : livePostExtension){
                Log.d("live", "getLiveVotes: "+ iPostExtension.getData());
                Voting voting = Voting.getValidVote(iPostExtension.getData(), iPostExtension.getCreatorId());
                if(voting!=null){
                    for(Voting vot : validVotes){
                        if(vot.getCreator().equals(voting.getCreator())){
                            validVotes.remove(vot);
                        }
                    }
                    validVotes.add(voting);
                }
            }
            return validVotes;
        });
    }


    private float getAvgTemp(List<Voting> votes){

        if(votes.size()==0){
            tempAvg = tempDefault;
            return tempDefault;
        }

        float totalValue = 0;
        for(Voting voting : votes){
                totalValue = totalValue + voting.getTempValue();
        }

        tempAvg = roundFloat(totalValue / votes.size());
        return tempAvg;
    }

    public String getAvgTempString(List<Voting> votes){
        return Float.toString(getAvgTemp(votes));
    }


    public void addVote(float currTempVote){
        String jsonFormattedText = Voting.makeJsonCommentOutput(currTempVote);
        showPostDataProvider.addPostExtension(jsonFormattedText);
    }

    public float getDownVoteValue(){
        float voteNumber = tempAvg - tempChange;
        return roundFloat(voteNumber);
    }

    public float getUpVoteValue(){
        float voteNumber = tempAvg + tempChange;
        return roundFloat(voteNumber);
    }

    public boolean vote(float voteNumber){
        if(inMemoryVoteExists || databaseVoteExists) return false;
        if(voteNumber > tempMin && voteNumber < tempMax){
            currTempVote = roundFloat(voteNumber);
            addVote(currTempVote);
            inMemoryVoteExists = true;
            return true;
        }
        return false;
    }

    public float roundFloat(float number){
        return (float) Math.round(number * 10f) / 10f;
    }


    public String getTempMin() {
        return Float.toString(tempMin);
    }

    public String getTempMax() {
        return Float.toString(tempMax);
    }


    public String getTempChange() {
        return Float.toString(tempChange);
    }


    public String getDescription() {
        return description;
    }

    public String getDeviceId() {
        return deviceId;
    }


}