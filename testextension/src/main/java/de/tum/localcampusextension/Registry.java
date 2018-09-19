package de.tum.localcampusextension;

import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ShowPostFragment;
import de.tum.testextension.TestAddFragment;
import de.tum.testextension.TestAddViewModel;
import de.tum.testextension.TestShowFragment;

public class Registry {
    public static Class<? extends ShowPostFragment> showPostFragmentClass = TestShowFragment.class;
    public static Class<? extends AddPostFragment> addPostFragmentClass = TestAddFragment.class;
    public static String typeId = "ee5afd62-6e72-4728-8404-e91d7ea2c303";
    public static String typeDescription = "Sample Extension";
}
