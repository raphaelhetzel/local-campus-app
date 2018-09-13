package de.tum.localcampusapp.postTypes;

public class CommentLocater {

    private CommentHelper commentHelper=null;

    private static CommentLocater instance = new CommentLocater();

    private CommentLocater() {
    }

    public static CommentLocater getInstance(){
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