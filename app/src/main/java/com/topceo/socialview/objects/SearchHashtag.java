package com.topceo.socialview.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchHashtag implements Parcelable {
    private String Hashtag;
    private int PostCount;

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Hashtag = hashtag;
    }

    public int getPostCount() {
        return PostCount;
    }

    public void setPostCount(int postCount) {
        PostCount = postCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Hashtag);
        dest.writeInt(this.PostCount);
    }

    public SearchHashtag() {
    }

    protected SearchHashtag(Parcel in) {
        this.Hashtag = in.readString();
        this.PostCount = in.readInt();
    }

    public static final Parcelable.Creator<SearchHashtag> CREATOR = new Parcelable.Creator<SearchHashtag>() {
        @Override
        public SearchHashtag createFromParcel(Parcel source) {
            return new SearchHashtag(source);
        }

        @Override
        public SearchHashtag[] newArray(int size) {
            return new SearchHashtag[size];
        }
    };
}
