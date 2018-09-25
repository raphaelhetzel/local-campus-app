package de.tum.in.votingextension.Fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.votingextension.ExtensionType.Voting;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampuslib.entity.IPostExtension;

public class VotingShowViewModel {

    private ShowPostDataProvider showPostDataProvider;
    private float tempValue;
    private float tempChangeFactor;
    private float currTempVote;
    private float tempAvg;

    private LiveData<List<IPostExtension>> livePostExtension;

    public VotingShowViewModel(ShowPostDataProvider showPostDataProvider){
        this.showPostDataProvider = showPostDataProvider;
        this.livePostExtension = (LiveData<List<IPostExtension>>) showPostDataProvider.getPostExtensions();
    }

    public LiveData<List<IPostExtension>> getExtension(){
        return livePostExtension;
    }

    public LiveData<List<Voting>> getLiveVotes() {
        return Transformations.map(livePostExtension, (List<IPostExtension> livePostExtension) -> {
            List<Voting> validVotes = new ArrayList<>();
            for (IPostExtension iPostExtension : livePostExtension){
                Voting voting = Voting.getValidVote(iPostExtension.getPostId(), iPostExtension.getId(), iPostExtension.getData(), iPostExtension.getCreatorId());
                if(voting!=null){
                    for(Voting vot : validVotes){
                        if(vot.getCreator().equals(voting.getCreator())){
                            validVotes.remove(vot);   //TODO:comment in for multiple users, comment out for testing!
                        }
                    }
                    validVotes.add(voting);
                }
            }
            return validVotes;
        });
    }


    public float getAvgTemp(List<Voting> votes){
        float totalValue = 0;
        if(votes.size()==0) return Voting.TEMP_INIT;
        for(Voting voting : votes){
                totalValue = totalValue + voting.getTempValue();
        }
        tempAvg = totalValue / votes.size();
        return roundFloat(tempAvg);
    }


    public void addVote(float currTempVote){
        String jsonFormattedText = Voting.makeJsonCommentOutput(currTempVote);
        showPostDataProvider.addPostExtension(jsonFormattedText);
    }

    public float getDownVoteValue(){
        float voteNumber = tempAvg - Voting.TEMP_CHANGE;
        return roundFloat(voteNumber);
    }

    public float getUpVoteValue(){
        float voteNumber = tempAvg + Voting.TEMP_CHANGE;
        return roundFloat(voteNumber);
    }

    public boolean vote(float voteNumber){
        if(voteNumber > Voting.TEMP_MIN && voteNumber < Voting.TEMP_MAX){
            currTempVote = roundFloat(voteNumber);
            addVote(currTempVote);
            return true;
        }
        return false;
    }

    public float roundFloat(float number){
        return (float) Math.round(number * 10f) / 10f;
    }

}