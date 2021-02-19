package com.topceo.objects.chatcore;

import android.os.Parcel;
import android.os.Parcelable;

public class UserChatCore implements Parcelable {

    private String _id;// "5eb3a96978f4f600424db71a",
    private String uid;// "SKY-7",
    private String name;// "phuongphammtp",
    private String providerId;// "custom",
    /*private String providerData;// {
  /*private String providerId;// "sky",
        private String name;// "phuongphammtp",
        private String email;// "vsoftphuong@gmail.com",
        private String phone;// "+84938936128"
        },*/
    private boolean disabled;// true,
    private String email;// "",
    private boolean emailVerified;// false,
    private String phone;// "",
    private boolean nameChanged;// false,
    private boolean avatarChanged;// false,
    private String birthday;// "",
    private String gender;// "",
    private String position;// "",
    private long createDate;// 1588832617,
    private String createOS;// null,
    private long lastLoginDate;// 1588844638,
    private String lastLoginOS;// "Android",
    private long lastUpdateDate;// 1588844638,
    private String lastUpdateOS;// "Android",
    private String os;// "Android",
    private String device;// "32DF23A8C9CE28EF",
    private String token;//

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isNameChanged() {
        return nameChanged;
    }

    public void setNameChanged(boolean nameChanged) {
        this.nameChanged = nameChanged;
    }

    public boolean isAvatarChanged() {
        return avatarChanged;
    }

    public void setAvatarChanged(boolean avatarChanged) {
        this.avatarChanged = avatarChanged;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getCreateOS() {
        return createOS;
    }

    public void setCreateOS(String createOS) {
        this.createOS = createOS;
    }

    public long getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(long lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastLoginOS() {
        return lastLoginOS;
    }

    public void setLastLoginOS(String lastLoginOS) {
        this.lastLoginOS = lastLoginOS;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getLastUpdateOS() {
        return lastUpdateOS;
    }

    public void setLastUpdateOS(String lastUpdateOS) {
        this.lastUpdateOS = lastUpdateOS;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.providerId);
        dest.writeByte(this.disabled ? (byte) 1 : (byte) 0);
        dest.writeString(this.email);
        dest.writeByte(this.emailVerified ? (byte) 1 : (byte) 0);
        dest.writeString(this.phone);
        dest.writeByte(this.nameChanged ? (byte) 1 : (byte) 0);
        dest.writeByte(this.avatarChanged ? (byte) 1 : (byte) 0);
        dest.writeString(this.birthday);
        dest.writeString(this.gender);
        dest.writeString(this.position);
        dest.writeLong(this.createDate);
        dest.writeString(this.createOS);
        dest.writeLong(this.lastLoginDate);
        dest.writeString(this.lastLoginOS);
        dest.writeLong(this.lastUpdateDate);
        dest.writeString(this.lastUpdateOS);
        dest.writeString(this.os);
        dest.writeString(this.device);
        dest.writeString(this.token);
    }

    public UserChatCore() {
    }

    protected UserChatCore(Parcel in) {
        this._id = in.readString();
        this.uid = in.readString();
        this.name = in.readString();
        this.providerId = in.readString();
        this.disabled = in.readByte() != 0;
        this.email = in.readString();
        this.emailVerified = in.readByte() != 0;
        this.phone = in.readString();
        this.nameChanged = in.readByte() != 0;
        this.avatarChanged = in.readByte() != 0;
        this.birthday = in.readString();
        this.gender = in.readString();
        this.position = in.readString();
        this.createDate = in.readLong();
        this.createOS = in.readString();
        this.lastLoginDate = in.readLong();
        this.lastLoginOS = in.readString();
        this.lastUpdateDate = in.readLong();
        this.lastUpdateOS = in.readString();
        this.os = in.readString();
        this.device = in.readString();
        this.token = in.readString();
    }

    public static final Parcelable.Creator<UserChatCore> CREATOR = new Parcelable.Creator<UserChatCore>() {
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
