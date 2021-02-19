package com.workchat.core.mbn.models;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.pchmn.materialchips.model.ChipInterface;
import com.workchat.core.utils.MyUtils;

/**
 * Created by Hung on 11/24/2015.
 */
public class UserChatCore implements ChipInterface, Parcelable {

    public static final String USER_MODEL = "USER_MODEL";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String JSON_CREATE_ROOM = "JSON_CREATE_ROOM";
    public static final String USER_ID = "USER_ID";
    public static final String IS_CHAT_WITH_SUPPORT = "IS_CHAT_WITH_SUPPORT";
    public static final String PHONE = "PHONE";
    public static final String USER_MODEL_LIST = "USER_MODEL_LIST";
    public static final String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";


    private String _id;
    private long mxtUserId;
    private String phone = "";
    private String name = "";
    private String nameMBN = "";//bo sung


    private String email = "";
    private String avatar = "";
    private String token = "";
    private String position="";
    private String url = "";
    private String password = "";
    private boolean mobileVerified;
    private String mxtAccessToken;
    private String birthday;
    private String gender;


    public String getNameMBN() {
        return nameMBN;
    }

    public void setNameMBN(String nameMBN) {
        this.nameMBN = nameMBN;
    }

    public String getFirstNameCharacter(){
        String label = null;
        if(!TextUtils.isEmpty(name)){
            label = name.substring(0,1);
            label = label.toUpperCase();
        }
        return label;
    }


    public UserChatCore() {
    }


    @Override
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPhone() {
        if (!TextUtils.isEmpty(phone)) {
            phone = MyUtils.getPhoneNumberOnly(phone);
        } else phone = "";

        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        if (TextUtils.isEmpty(avatar)) avatar = "";
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //////////////////////////////////////////////////////////////////
    @Override
    public Uri getAvatarUri() {
        /*if(!TextUtils.isEmpty(avatar_url)){
            return  Uri.parse(avatar_url);
        }*/
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getInfo() {
        return phone;
    }

    @Override
    public String getAvatarUrl() {
        return getAvatar();
    }

    public long getMxtUserId() {
        return mxtUserId;
    }

    public void setMxtUserId(long mxtUserId) {
        this.mxtUserId = mxtUserId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public String getMxtAccessToken() {
        return mxtAccessToken;
    }

    public void setMxtAccessToken(String mxtAccessToken) {
        this.mxtAccessToken = mxtAccessToken;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeLong(this.mxtUserId);
        dest.writeString(this.phone);
        dest.writeString(this.name);
        dest.writeString(this.nameMBN);
        dest.writeString(this.email);
        dest.writeString(this.avatar);
        dest.writeString(this.token);
        dest.writeString(this.position);
        dest.writeString(this.url);
        dest.writeString(this.password);
        dest.writeByte(this.mobileVerified ? (byte) 1 : (byte) 0);
        dest.writeString(this.mxtAccessToken);
        dest.writeString(this.birthday);
        dest.writeString(this.gender);
    }

    protected UserChatCore(Parcel in) {
        this._id = in.readString();
        this.mxtUserId = in.readLong();
        this.phone = in.readString();
        this.name = in.readString();
        this.nameMBN = in.readString();
        this.email = in.readString();
        this.avatar = in.readString();
        this.token = in.readString();
        this.position = in.readString();
        this.url = in.readString();
        this.password = in.readString();
        this.mobileVerified = in.readByte() != 0;
        this.mxtAccessToken = in.readString();
        this.birthday = in.readString();
        this.gender = in.readString();
    }

    public static final Creator<UserChatCore> CREATOR = new Creator<UserChatCore>() {
        @Override
        public UserChatCore createFromParcel(Parcel source) {
            return new UserChatCore(source);
        }

        @Override
        public UserChatCore[] newArray(int size) {
            return new UserChatCore[size];
        }
    };
}