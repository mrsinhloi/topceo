package com.topceo.socialview.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchMention implements Parcelable {

    private String UserName;
    private String FullName;
    private String AvatarSmall;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getAvatarSmall() {
        return AvatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        AvatarSmall = avatarSmall;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UserName);
        dest.writeString(this.FullName);
        dest.writeString(this.AvatarSmall);
    }

    public SearchMention() {
    }

    protected SearchMention(Parcel in) {
        this.UserName = in.readString();
        this.FullName = in.readString();
        this.AvatarSmall = in.readString();
    }

    public static final Creator<SearchMention> CREATOR = new Creator<SearchMention>() {
        @Override
        public SearchMention createFromParcel(Parcel source) {
            return new SearchMention(source);
        }

        @Override
        public SearchMention[] newArray(int size) {
            return new SearchMention[size];
        }
    };
}
