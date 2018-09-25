package de.tum.in.votingextension.Registry;

import de.tum.in.votingextension.Fragments.VotingPostAddFragment;
import de.tum.in.votingextension.Fragments.VotingShowFragment;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ShowPostFragment;

public class Registry {
    public static Class<? extends ShowPostFragment> showPostFragmentClass = VotingShowFragment.class;
    public static Class<? extends AddPostFragment> addPostFragmentClass = VotingPostAddFragment.class;
    public static String typeId = "ab6acf96-24bd-4d7d-b9d0-0784e821090b";
    public static String typeDescription = "Voting Extension";
}
