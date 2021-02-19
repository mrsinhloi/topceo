package com.workchat.core.models.realm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class UserChat extends RealmObject implements Parcelable {
    public static final String USER_ID = "USER_ID";
    public static final String IS_BANNED_USER = "IS_BANNED_USER";
    public static final String LIST_USER_ONLINE = "LIST_USER_ONLINE";

    @PrimaryKey
    private String _id;
    private String name;//name server
    private String nameLocal;//bo sung name local
    private String phone;
    private String email;
    private String avatar;

    //bo sung local
    private boolean isMuted;

    public String getUserId() {
        return _id;
    }

    public void setUserId(String userId) {
        this._id = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        if(phone==null)phone = "";
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        if (TextUtils.isEmpty(avatar)) avatar = "";
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }


    public UserChat() {
    }

    public UserInfo convertToUserInfo() {
        UserInfo info = null;
        if(!TextUtils.isEmpty(_id)){
            info = new UserInfo();
            info.set_id(_id);
            info.setName(name);
            info.setEmail(email);
            info.setPhone(phone);
            info.setAvatar(avatar);
        }

        return info;
    }

    public String getNameLocal() {
        return nameLocal;
    }

    public void setNameLocal(String nameLocal) {
        this.nameLocal = nameLocal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.name);
        dest.writeString(this.nameLocal);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.avatar);
        dest.writeByte(this.isMuted ? (byte) 1 : (byte) 0);
    }

    protected UserChat(Parcel in) {
        this._id = in.readString();
        this.name = in.readString();
        this.nameLocal = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.avatar = in.readString();
        this.isMuted = in.readByte() != 0;
    }

    public static final Creator<UserChat> CREATOR = new Creator<UserChat>() {
        @Override
        public UserChat createFromParcel(Parcel source) {
            return new UserChat(source);
        }

        @Override
        public UserChat[] newArray(int size) {
            return new UserChat[size];
        }
    };
}
