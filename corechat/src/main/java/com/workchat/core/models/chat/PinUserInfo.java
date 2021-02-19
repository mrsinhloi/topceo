package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class PinUserInfo implements Parcelable {

    //    _id, name, avatar, email, phone
    private String _id;
    private String name;
    private String avatar;
    private String email;
    private String phone;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeString(this.email);
        dest.writeString(this.phone);
    }

    public PinUserInfo() {
    }

    protected PinUserInfo(Parcel in) {
        this._id = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
    }

    public static final Parcelable.Creator<PinUserInfo> CREATOR = new Parcelable.Creator<PinUserInfo>() {
        @Override
        public PinUserInfo createFromParcel(Parcel source) {
            return new PinUserInfo(source);
        }

        @Override
        public PinUserInfo[] newArray(int size) {
            return new PinUserInfo[size];
        }
    };
}
