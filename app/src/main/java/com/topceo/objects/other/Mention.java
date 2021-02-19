package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2016-08-08.
 */
public class Mention implements Parcelable {

    private String UserName;
    private String AvatarSmall;
    private String FullName;

    public Mention(String UserName, String AvatarSmall,String FullName){
        this.UserName=UserName;
        this.AvatarSmall=AvatarSmall;
        this.FullName=FullName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAvatarSmall() {
        return AvatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        AvatarSmall = avatarSmall;
    }
    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }


    public Mention(){}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UserName);
        dest.writeString(this.AvatarSmall);
        dest.writeString(this.FullName);
    }

    protected Mention(Parcel in) {
        this.UserName = in.readString();
        this.AvatarSmall = in.readString();
        this.FullName = in.readString();
    }

    public static final Parcelable.Creator<Mention> CREATOR = new Parcelable.Creator<Mention>() {
        @Override
        public Mention createFromParcel(Parcel source) {
            return new Mention(source);
        }

        @Override
        public Mention[] newArray(int size) {
            return new Mention[size];
        }
    };
}
