package de.tum.localcampusapp.postTypes;

import android.util.Pair;

import java.util.Date;

public class Comment {
    private long postId;
    private long commentId;
    private String data;
    private Date updatedComment;


    public Comment(long postId, long commentId, String data, Date updatedComment) {
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

    public void setData(String data) {
        this.data = data;
    }

    public Date getUpdatedComment() {
        return updatedComment;
    }

    public void setUpdatedComment(Date updatedComment) {
        this.updatedComment = updatedComment;
    }

}