package com.topceo.retrofit;

public class PostImageCommentLike {
    public long getCommentId() {
        return CommentId;
    }

    public void setCommentId(long commentId) {
        CommentId = commentId;
    }

    private long CommentId;
    public PostImageCommentLike(long commentId){
        this.CommentId = commentId;
    }
}
