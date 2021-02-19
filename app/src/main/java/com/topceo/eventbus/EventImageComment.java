package com.topceo.eventbus;

import com.topceo.objects.image.ImageComment;

public class EventImageComment {
    private ImageComment comment;
    public EventImageComment(ImageComment comment){
        this.comment = comment;
    }

    public ImageComment getComment() {
        return comment;
    }

    public void setComment(ImageComment comment) {
        this.comment = comment;
    }
}
