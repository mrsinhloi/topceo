package com.topceo.eventbus;

import com.topceo.shopping.MediaComment;

public class EventMediaComment {
    private MediaComment comment;
    public EventMediaComment(MediaComment comment){
        this.comment = comment;
    }

    public MediaComment getComment() {
        return comment;
    }

    public void setComment(MediaComment comment) {
        this.comment = comment;
    }
}
