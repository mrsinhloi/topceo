package com.topceo.retrofit;

import com.topceo.selections.hashtags.HashtagShort;

import java.util.ArrayList;

public class PostHashtag {

    long CategoryId;
    ArrayList<HashtagShort> Hashtags;

    public long getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(long categoryId) {
        CategoryId = categoryId;
    }

    public ArrayList<HashtagShort> getHashtags() {
        return Hashtags;
    }

    public void setHashtags(ArrayList<HashtagShort> hashtags) {
        Hashtags = hashtags;
    }
}
