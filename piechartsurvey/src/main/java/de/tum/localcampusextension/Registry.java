package de.tum.localcampusextension;

import de.tum.piechartsurvey.Fragments.PieExtensionFragment;
import de.tum.piechartsurvey.Fragments.PiePostFragment;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ShowPostFragment;

public class Registry {
    public static Class<? extends ShowPostFragment> showPostFragmentClass = PieExtensionFragment.class;
    public static Class<? extends AddPostFragment> addPostFragmentClass = PiePostFragment.class;
    public static String typeId = "36990489-5f2c-4ff8-aff7-84e9a881c7e8";
    public static String typeDescription = "PieChart Survey Extension";
}