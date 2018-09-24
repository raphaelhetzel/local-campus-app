package de.tum.in.commentsextensionmodule.ExtensionType;

public class CommentLocator {

    private CommentHelper commentHelper=null;

    private static CommentLocator instance = new CommentLocator();

    private CommentLocator() {
    }

    public static CommentLocator getInstance(){
        return instance;
    }


    public void reset() {
        commentHelper = null;
    }

    public CommentHelper getCommentHelper() {
        if (commentHelper != null) {
            return commentHelper;
        }
        commentHelper = new CommentHelper();
        return commentHelper;
    }
}
