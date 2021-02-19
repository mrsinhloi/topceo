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

    public boolean isVip() {
        return IsVip;
    }

    public void setVip(boolean vip) {
        IsVip = vip;
    }


    public UserShortDB() {
    }


    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.UserId);
        dest.writeString(this.UserName);
        dest.writeString(this.AvatarSmall);
        dest.writeByte(this.IsVip ? (byte) 1 : (byte) 0);
        dest.writeString(this.FullName);
        dest.writeString(this.ChatUserId);
    }

    protected UserShortDB(Parcel in) {
        this.UserId = in.readLong();
        this.UserName = in.readString();
        this.AvatarSmall = in.readString();
        this.IsVip = in.readByte() != 0;
        this.FullName = in.readString();
        this.ChatUserId = in.readString();
    }

    public static final Creator<UserShortDB> CREATOR = new Creator<UserShortDB>() {
        @Override
        public UserShortDB createFromParcel(Parcel source) {
            return new UserShortDB(source);
        }

        @Override
        public UserShortDB[] newArray(int size) {
            return new UserShortDB[size];
        }
    };

    public String getChatUserId() {
        return ChatUserId;
    }

    public void setChatUserId(String chatUserId) {
        ChatUserId = chatUserId;
    }
}
