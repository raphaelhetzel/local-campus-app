package de.tum.in.commentsextensionmodule.ExtensionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Comment {
    private long postId;
    private long commentId;
    private String data;
    private Date updatedComment;

    private static final String ATTR_DATA = "text";

    public static Comment getWorkingComment(long postId, long commentId, String data, Date updatedComment) {
        try {
            JSONObject obj = new JSONObject(data);
            String text = obj.getString(ATTR_DATA);
            return new Comment(postId, commentId, text, updatedComment);
        } catch (JSONException e) {
            return null;
        }
    }

    private Comment(long postId, long commentId, String data, Date updatedComment) {
        this.postId = postId;
        this.commentId = commentId;
        this.data = data;
        this.updatedComment = updatedComment;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long postId) {
        this.commentId = commentId;
    }

    public String getData() {
        return data;
    }

    public Date getUpdatedComment() {
        return updatedComment;
    }

    public void setUpdatedComment(Date updatedComment) {
        this.updatedComment = updatedComment;
    }


}