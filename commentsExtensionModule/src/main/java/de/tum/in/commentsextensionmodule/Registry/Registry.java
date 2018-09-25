package de.tum.in.commentsextensionmodule.Registry;

import de.tum.in.commentsextensionmodule.Fragments.CommentShowFragment;
import de.tum.in.commentsextensionmodule.Fragments.PostAddFragment;
import de.tum.localcampuslib.AddPostFragment;
import de.tum.localcampuslib.ShowPostFragment;

public class Registry {
    public static Class<? extends ShowPostFragment> showPostFragmentClass = CommentShowFragment.class;
    public static Class<? extends AddPostFragment> addPostFragmentClass = PostAddFragment.class;
    public static String typeId = "6ed88f3a-5895-4cac-b096-d260ecc9b71d";
    public static String typeDescription = "Comments Extension";
}
