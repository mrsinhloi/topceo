package com.workchat.core.plan;

import android.os.Parcel;
import android.os.Parcelable;

public class Vote implements Parcelable {
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String MAYBE = "maybe";


    private String userId;
    private String status;//yes,no,maybe

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Vote() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.status);
    }

    protected Vote(Parcel in) {
        this.userId = in.readString();
        this.status = in.readString();
    }

    public static final Creator<Vote> CREATOR = new Creator<Vote>() {
        @Override
        public Vote createFromParcel(Parcel source) {
            return new Vote(source);
        }

        @Override
        public Vote[] newArray(int size) {
            return new Vote[size];
        }
    };
}
