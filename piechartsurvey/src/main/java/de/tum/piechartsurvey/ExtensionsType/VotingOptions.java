package de.tum.piechartsurvey.ExtensionsType;

import java.util.ArrayList;

public class VotingOptions {

    //This class contains all VoteOptions defines
    //and the title of the survey

    public static final String ATTR_DATA = "text";
    public static final String ATTR_VOTE_OPTIONS = "vote_options";

    private String title;
    private ArrayList<VoteOption> options;


    public static VotingOptions getValidVotingOptions(String text, ArrayList<VoteOption> options) {
        if (options.size() > Colors.colorsInternal.length || options.size() < 1) {
            return null;
        }
        return new VotingOptions(text, options);
    }

    private VotingOptions(String title, ArrayList<VoteOption> options) {
        this.title = title;
        this.options = options;
    }

    public ArrayList<VoteOption> getOptions() {
        return options;
    }

    public String getTitle() {
        return title;
    }


}
