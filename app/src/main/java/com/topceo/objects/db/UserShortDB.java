package com.topceo.objects.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.other.UserShort;

import io.realm.RealmObject;

public class UserShortDB extends RealmObject implements Parcelable {

    //    private String id = UUID.randomUUID().toString();
    private long UserId;//": 1,
    private String UserName;//": "sontungmtp",
    private String AvatarSmall;//": "https://cdn.ehubstar.com/avatar/baf0e3e1-3895-46a5-9039-811b2209c838/a10fd31c-77b1-47b0-b6ec-a8a5b6bbb2d3/s.jpg",
    private boolean IsVip;//": true
    private String FullName;
    private String ChatUserId;

    protected UserShortDB(Parcel in) {
        UserId = in.readLong();
        UserName = in.readString();
        AvatarSmall = in.readString();
        IsVip = in.readByte() != 0;
        FullName = in.readString();
        ChatUserId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(UserId);
        dest.writeString(UserName);
        dest.writeString(AvatarSmall);
        dest.writeByte((byte) (IsVip ? 1 : 0));
        dest.writeString(FullName);
        dest.writeString(ChatUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserShortDB> CREATOR = new Creator<UserShortDB>() {
        @Override
        public UserShortDB createFromParcel(Parcel in) {
            return new UserShortDB(in);
        }

        @Override
        public UserShortDB[] newArray(int size) {
            return new UserShortDB[size];
        }
    };

    public UserShort copy() {
        UserShort item = new UserShort();
        item.setUserId(UserId);
        item.setUserName(UserName);
        item.setAvatarSmall(AvatarSmall);
        item.setVip(IsVip);
        item.setFullName(FullName);
        item.setChatUserId(ChatUserId);

        return item;
    }


    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getUserName() {
        if (UserName == null) UserName = "";
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAvatarSmall() {
        if (AvatarSmall == null) AvatarSmall = "";
        return AvatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        AvatarSmall = avatarSmall;
    }

    public boolean isVip() {
        return IsVip;
    }

    public void setVip(boolean vip) {
        IsVip = vip;
    }


    public UserShortDB() {
    }


    public String getFullName() {
        if (FullName == null) FullName = "";
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }


    public String getChatUserId() {
        return ChatUserId;
    }

    public void setChatUserId(String chatUserId) {
        ChatUserId = chatUserId;
    }
}
