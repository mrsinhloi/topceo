package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.workchat.core.models.realm.UserChat;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class Member implements Parcelable {

    public static final String MEMBER = "MEMBER";
    public static final String MEMBER_ID = "MEMBER_ID";
    public static final String IS_MUTED = "IS_MUTED";
    public static final String IS_ADD_NEW_ADMIN = "IS_ADD_NEW_ADMIN";

    private String userId;
    private UserChat userInfo;
    private boolean isArchived;
    private boolean isDelete;
    private boolean isMuted;
    private boolean isGuest;
    private boolean isFavorite;

    //bo sung 7/3/2018
    private boolean isAdmin;
    private boolean isOwner;
    private Permission permissions;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserChat getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserChat userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission permissions) {
        this.permissions = permissions;
    }

    @NonNull
    @Override
    public String toString() {
        String name = "";
        if (userInfo != null) name = userInfo.getName();
        return name;
    }

    public Member() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeByte(this.isArchived ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDelete ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMuted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGuest ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isOwner ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.permissions, flags);
    }

    protected Member(Parcel in) {
        this.userId = in.readString();
        this.userInfo = in.readParcelable(UserChat.class.getClassLoader());
        this.isArchived = in.readByte() != 0;
        this.isDelete = in.readByte() != 0;
        this.isMuted = in.readByte() != 0;
        this.isGuest = in.readByte() != 0;
        this.isFavorite = in.readByte() != 0;
        this.isAdmin = in.readByte() != 0;
        this.isOwner = in.readByte() != 0;
        this.permissions = in.readParcelable(Permission.class.getClassLoader());
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel source) {
            return new Member(source);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
}
