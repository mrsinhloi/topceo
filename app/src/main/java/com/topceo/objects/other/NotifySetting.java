package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

public class NotifySetting implements Parcelable {

    private String notifyString;
    private boolean isChecked;

    public String getNotifyString() {
        return notifyString;
    }

    public void setNotifyString(String notifyString) {
        this.notifyString = notifyString;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.notifyString);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    public NotifySetting() {
    }

    protected NotifySetting(Parcel in) {
        this.notifyString = in.readString();
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<NotifySetting> CREATOR = new Parcelable.Creator<NotifySetting>() {
        @Override
        public NotifySetting createFromParcel(Parcel source) {
            return new NotifySetting(source);
        }

        @Override
        public NotifySetting[] newArray(int size) {
            return new NotifySetting[size];
        }
    };
}
