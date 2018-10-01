package de.tum.piechartsurvey.Fragments;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tum.piechartsurvey.ExtensionsType.Vote;
import de.tum.piechartsurvey.ExtensionsType.VoteOption;
import de.tum.piechartsurvey.ExtensionsType.VotingOptions;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampuslib.entity.IPost;
import de.tum.localcampuslib.entity.IPostExtension;

public class PieExtensionViewModel {

    static final String TAG = PieExtensionViewModel.class.getSimpleName();

    private ShowPostDataProvider showPostDataProvider;
    private LiveData<List<IPostExtension>> livePostExtensions;
    private LiveData<IPost> livePost;

    private boolean databaseVoteExists = false;
    private boolean inMemoryVoteExists = false;

    public PieExtensionViewModel(ShowPostDataProvider showPostDataProvider, LifecycleOwner lifecycleOwner) {
        this.showPostDataProvider = showPostDataProvider;
        this.livePostExtensions = (LiveData<List<IPostExtension>>) showPostDataProvider.getPostExtensions();
        this.livePost = (LiveData<IPost>) showPostDataProvider.getPost();

        showPostDataProvider.getPostExtensions().observe(lifecycleOwner, postExtensions -> {
            databaseVoteExists = postExtensions.stream()
                    .anyMatch(postExtension -> postExtension.getCreatorId().equals(showPostDataProvider.getCurrentUser()));
        });
    }

    public VotingOptions parseJsonPost(IPost post) {

        try {
            ArrayList<VoteOption> list = new ArrayList<>();
            String text;

            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(post.getData());
            JSONObject jsonObject = (JSONObject) obj;

            text = (String) jsonObject.get(VotingOptions.ATTR_DATA);

            JSONArray msg = (JSONArray) jsonObject.get(VotingOptions.ATTR_VOTE_OPTIONS);

            Iterator<String> iterator = msg.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                String elem = iterator.next();
                VoteOption v = VoteOption.getValidOption(i++, elem);
                if (v != null) {
                    list.add(v);
                }
            }

            VotingOptions votingOptions = VotingOptions.getValidVotingOptions(text, list);
            return votingOptions;

        } catch (Exception e) {
            return null;
        }
    }


    public void addVote(int id) {
        if( inMemoryVoteExists || databaseVoteExists) return;
        String jsonString = Vote.makeJsonCommentOutput(id);
        showPostDataProvider.addPostExtension(jsonString);
        inMemoryVoteExists = true;
    }

    public VotingOptions getVotingOptions(IPost post) {
        ArrayList<String> options = new ArrayList<>();
        VotingOptions votingOptions = parseJsonPost(post);
        return votingOptions;
    }

    //data defined in the PiePostViewModel
    public LiveData<IPost> getLivePost() {
        return livePost;
    }

    //data if another user made a vote
    public LiveData<List<Vote>> getLiveVotes() {
        return Transformations.map(livePostExtensions, (List<IPostExtension> livePostExtension) -> {

            List<Vote> duplicateVotes = new ArrayList<>();      //all votes in repo - can be multiple from a user
            List<Vote> validVotes = new ArrayList<>();          //all last votes from every user
            int totalSum = 0;

            for (IPostExtension iPostExtension : livePostExtension) {
                Vote voting = Vote.getValidVote(iPostExtension.getCreatorId(), iPostExtension.getData());

                if (voting != null) {

                    for (Vote vote : duplicateVotes) {
                        if (vote.getCreator().equals(voting.getCreator())) {
                            duplicateVotes.remove(vote);   //comment in for multiple users, comment out for single user multiple votes
                        }                                  //Only the last vote of a certain user counts, therefore all prior votes
                                                           //of a certain user has to removed from the list
                    }

                    duplicateVotes.add(voting);
                }

            }

            HashMap<Integer, Integer> clickSum = new HashMap<>();       //map for Vote-ID and Vote-ClickSum

            for (Vote v : duplicateVotes) {
                int voteId = v.getId();                     //how often a certain vote possibility was clicked
                if (clickSum.containsKey(voteId)) {         //as last vote of each user
                    int clicksTotal = clickSum.get(voteId) + 1;
                    clickSum.replace(voteId, clicksTotal);
                } else {
                    clickSum.put(voteId, 1);                //if vote pops up the first time it has no attribute for number of counts
                }                                           //so the initial value has to be set up to 1
            }

            Iterator it = clickSum.entrySet().iterator();
            int totalClickSum = 0;

            while (it.hasNext()) {                          //uses the Hashmap<Integer, Integer> clickSum with the Vote-ID and its e
                Map.Entry pair = (Map.Entry) it.next();     //number of clicks to map it to the datasctructure Vote
                Vote vote = new Vote((int) pair.getKey(), (int) pair.getValue());
                totalClickSum = totalClickSum + (int) pair.getValue();
                validVotes.add(vote);
                it.remove();
            }

            for (Vote v : validVotes) {             //maps total clicks sum of each Vote into percentage
                                                    //so it can be presented in the PieChart
                v.setScoreInPerctentage(v.getClicksSum() * new Float(100) / totalClickSum);
            }

            return validVotes;
        });
    }

}