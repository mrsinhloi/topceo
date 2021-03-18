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


    protected UserShort(Parcel in) {
        UserId = in.readLong();
        UserName = in.readString();
        AvatarSmall = in.readString();
        IsVip = in.readByte() != 0;
        FullName = in.readString();
        GroupCount = in.readInt();
        ChatUserId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(UserId);
        dest.writeString(UserName);
        dest.writeString(AvatarSmall);
        dest.writeByte((byte) (IsVip ? 1 : 0));
        dest.writeString(FullName);
        dest.writeInt(GroupCount);
        dest.writeString(ChatUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserShort> CREATOR = new Creator<UserShort>() {
        @Override
        public UserShort createFromParcel(Parcel in) {
            return new UserShort(in);
        }

        @Override
        public UserShort[] newArray(int size) {
            return new UserShort[size];
        }
    };

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


    public String getChatUserId() {
        return ChatUserId;
    }

    public void setChatUserId(String chatUserId) {
        ChatUserId = chatUserId;
    }
}
