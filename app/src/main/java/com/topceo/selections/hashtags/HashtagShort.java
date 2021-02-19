package com.topceo.selections.hashtags;

import android.os.Parcel;
import android.os.Parcelable;

public class HashtagShort implements Parcelable {

    private long HashtagId;
    private String Hashtag;

    public long getHashtagId() {
        return HashtagId;
    }

    public void setHashtagId(long hashtagId) {
        HashtagId = hashtagId;
    }

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Hashtag = hashtag;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.HashtagId);
        dest.writeString(this.Hashtag);
    }

    public HashtagShort() {
    }

    protected HashtagShort(Parcel in) {
        this.HashtagId = in.readLong();
        this.Hashtag = in.readString();
    }

    public static final Creator<HashtagShort> CREATOR = new Creator<HashtagShort>() {
        @Override
        public HashtagShort createFromParcel(Parcel source) {
            return new HashtagShort(source);
        }

        @Override
        public HashtagShort[] newArray(int size) {
            return new HashtagShort[size];
        }
    };
}
