package de.tum.in.piechartsurvey.Fragments;

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

import de.tum.in.piechartsurvey.ExtensionsType.Vote;
import de.tum.in.piechartsurvey.ExtensionsType.VoteOption;
import de.tum.in.piechartsurvey.ExtensionsType.VotingOptions;
import de.tum.localcampuslib.ShowPostDataProvider;
import de.tum.localcampuslib.entity.IPost;
import de.tum.localcampuslib.entity.IPostExtension;

public class PieExtensionViewModel {

    static final String TAG = PieExtensionViewModel.class.getSimpleName();

    private ShowPostDataProvider showPostDataProvider;
    private LiveData<List<IPostExtension>> livePostExtensions;
    private LiveData<IPost> livePost;

    public PieExtensionViewModel(ShowPostDataProvider showPostDataProvider) {
        this.showPostDataProvider = showPostDataProvider;
        this.livePostExtensions = (LiveData<List<IPostExtension>>) showPostDataProvider.getPostExtensions();
        this.livePost = (LiveData<IPost>) showPostDataProvider.getPost();
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
        String jsonString = Vote.makeJsonCommentOutput(id);
        showPostDataProvider.addPostExtension(jsonString);
    }

    public VotingOptions getVotingOptions(IPost post) {
        ArrayList<String> options = new ArrayList<>();
        VotingOptions votingOptions = parseJsonPost(post);
        return votingOptions;
    }


    public LiveData<IPost> getLivePost() {
        return livePost;
    }


    public LiveData<List<Vote>> getLiveVotes() {
        return Transformations.map(livePostExtensions, (List<IPostExtension> livePostExtension) -> {

            List<Vote> duplicateVotes = new ArrayList<>();
            List<Vote> validVotes = new ArrayList<>();
            int totalSum = 0;

            for (IPostExtension iPostExtension : livePostExtension) {
                Vote voting = Vote.getValidVote(iPostExtension.getCreatorId(), iPostExtension.getData());

                if (voting != null) {

                    for (Vote vote : duplicateVotes) {
                        if (vote.getCreator().equals(voting.getCreator())) {
                            duplicateVotes.remove(vote);   //comment in for multiple users, comment out for single user multiple votes
                        }
                    }

                    duplicateVotes.add(voting);
                }

            }

            HashMap<Integer, Integer> clickSum = new HashMap<>();

            for (Vote v : duplicateVotes) {
                int voteId = v.getId();
                if (clickSum.containsKey(voteId)) {
                    int clicksTotal = clickSum.get(voteId) + 1;
                    clickSum.replace(voteId, clicksTotal);
                } else {
                    clickSum.put(voteId, 1);
                }
            }

            Iterator it = clickSum.entrySet().iterator();
            int totalClickSum = 0;

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Vote vote = new Vote((int) pair.getKey(), (int) pair.getValue());
                totalClickSum = totalClickSum + (int) pair.getValue();
                validVotes.add(vote);
                it.remove();
            }

            for (Vote v : validVotes) {
                v.setScoreInPerctentage(v.getClicksSum() * new Float(100) / totalClickSum);
            }

            return validVotes;
        });
    }

}