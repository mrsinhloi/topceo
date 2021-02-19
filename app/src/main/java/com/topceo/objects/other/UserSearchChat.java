package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

import com.workchat.core.mbn.models.UserChatCore;

public class UserSearchChat implements Parcelable {

    private String UserId;// "9",
    private String UserName;// "mrsinhloi",
    private String FullName;// "Quoc Luu",
    private boolean IsVip;// true,
    private String ChatUserId;// "5eb4d33178f4f600424db72f",
    private boolean ChatActive;// true,
    private int VipLevelId;// 0,
    private String VipLevel;// "SKY",
    private String Avatar;//

    public UserChatCore getUserMBN(){
        UserChatCore user = new UserChatCore();
        user.set_id(getChatUserId());
        user.setName(getUserName());
        user.setNameMBN(getFullName());
        user.setAvatar(getAvatar());
        return user;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

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

    public boolean isVip() {
        return IsVip;
    }

    public void setVip(boolean vip) {
        IsVip = vip;
    }

    public String getChatUserId() {
        return ChatUserId;
    }

    public void setChatUserId(String chatUserId) {
        ChatUserId = chatUserId;
    }

    public boolean isChatActive() {
        return ChatActive;
    }

    public void setChatActive(boolean chatActive) {
        ChatActive = chatActive;
    }

    public int getVipLevelId() {
        return VipLevelId;
    }

    public void setVipLevelId(int vipLevelId) {
        VipLevelId = vipLevelId;
    }

    public String getVipLevel() {
        return VipLevel;
    }

    public void setVipLevel(String vipLevel) {
        VipLevel = vipLevel;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UserId);
        dest.writeString(this.UserName);
        dest.writeString(this.FullName);
        dest.writeByte(this.IsVip ? (byte) 1 : (byte) 0);
        dest.writeString(this.ChatUserId);
        dest.writeByte(this.ChatActive ? (byte) 1 : (byte) 0);
        dest.writeInt(this.VipLevelId);
        dest.writeString(this.VipLevel);
        dest.writeString(this.Avatar);
    }

    public UserSearchChat() {
    }

    protected UserSearchChat(Parcel in) {
        this.UserId = in.readString();
        this.UserName = in.readString();
        this.FullName = in.readString();
        this.IsVip = in.readByte() != 0;
        this.ChatUserId = in.readString();
        this.ChatActive = in.readByte() != 0;
        this.VipLevelId = in.readInt();
        this.VipLevel = in.readString();
        this.Avatar = in.readString();
    }

    public static final Parcelable.Creator<UserSearchChat> CREATOR = new Parcelable.Creator<UserSearchChat>() {
        @Override
        public UserSearchChat createFromParcel(Parcel source) {
            return new UserSearchChat(source);
        }

        @Override
        public UserSearchChat[] newArray(int size) {
            return new UserSearchChat[size];
        }
    };
}
