package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.workchat.core.models.realm.UserInfo;

//contactId	string	(userId của contact)
//name	string
//avatar	string
//mobile	string
//email	string
public class Contact implements Parcelable {

    private String contactId;
    private String name;
    private String avatar;
    private String mobile;
    private String email;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        if (name == null) name = "";
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserInfo convertUserInfo() {
        UserInfo user = null;

        try {
            user = new UserInfo();
            user.set_id(getContactId());
            user.setAvatar(getAvatar());
            user.setPhone(getMobile());
            user.setEmail(getEmail());
            user.setName(getName());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contactId);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeString(this.mobile);
        dest.writeString(this.email);
    }

    public Contact() {
    }

    protected Contact(Parcel in) {
        this.contactId = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.mobile = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
