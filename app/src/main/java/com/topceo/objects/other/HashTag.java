package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2016-08-08.
 */
public class HashTag implements Parcelable {
    public static final String HASH_TAG="HASH_TAG";

    private String Hashtag;
    private long PostCount;

    public HashTag(String Hashtag, long PostCount){
        this.Hashtag=Hashtag;
        this.PostCount=PostCount;
    }

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Hashtag = hashtag;
    }

    public long getPostCount() {
        return PostCount;
    }

    public void setPostCount(long postCount) {
        PostCount = postCount;
    }


    public HashTag(){}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Hashtag);
        dest.writeLong(this.PostCount);
    }

    protected HashTag(Parcel in) {
        this.Hashtag = in.readString();
        this.PostCount = in.readLong();
    }

    public static final Parcelable.Creator<HashTag> CREATOR = new Parcelable.Creator<HashTag>() {
        @Override
        public HashTag createFromParcel(Parcel source) {
            return new HashTag(source);
        }

        @Override
        public HashTag[] newArray(int size) {
            return new HashTag[size];
        }
    };
}
