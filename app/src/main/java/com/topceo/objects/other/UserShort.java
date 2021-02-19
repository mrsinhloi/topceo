package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.db.UserShortDB;

public class UserShort implements Parcelable {

//    private String id = UUID.randomUUID().toString();
    private long UserId;//": 1,
    private String UserName;//": "sontungmtp",
    private String AvatarSmall;//": "https://cdn.ehubstar.com/avatar/baf0e3e1-3895-46a5-9039-811b2209c838/a10fd31c-77b1-47b0-b6ec-a8a5b6bbb2d3/s.jpg",
    private boolean IsVip;//": true

    //them cho phan group
    private String FullName;
    private int GroupCount;
    private String ChatUserId;


    public UserShortDB copy(){
        UserShortDB item = new UserShortDB();
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


    public UserShort() {
    }


    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public int getGroupCount() {
        return GroupCount;
    }

    public void setGroupCount(int groupCount) {
        GroupCount = groupCount;
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
        dest.writeInt(this.GroupCount);
        dest.writeString(this.ChatUserId);
    }

    protected UserShort(Parcel in) {
        this.UserId = in.readLong();
        this.UserName = in.readString();
        this.AvatarSmall = in.readString();
        this.IsVip = in.readByte() != 0;
        this.FullName = in.readString();
        this.GroupCount = in.readInt();
        this.ChatUserId = in.readString();
    }

    public static final Creator<UserShort> CREATOR = new Creator<UserShort>() {
        @Override
        public UserShort createFromParcel(Parcel source) {
            return new UserShort(source);
        }

        @Override
        public UserShort[] newArray(int size) {
            return new UserShort[size];
        }
    };

    public String getChatUserId() {
        return ChatUserId;
    }

    public void setChatUserId(String chatUserId) {
        ChatUserId = chatUserId;
    }
}
