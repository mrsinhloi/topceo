package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2016-09-30.
 */

public class AppVersion implements Parcelable {

    private String OS;
    private int Version;
    private boolean ForceUpdate;
    private String UpdateMessage;
    private String AppUrl;

    //////////////////////////////////////////////////////////////////////////////////////////////
    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public boolean isForceUpdate() {
        return ForceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        ForceUpdate = forceUpdate;
    }

    public String getUpdateMessage() {
        return UpdateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        UpdateMessage = updateMessage;
    }

    public String getAppUrl() {
        return AppUrl;
    }

    public void setAppUrl(String appUrl) {
        AppUrl = appUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.OS);
        dest.writeInt(this.Version);
        dest.writeByte(this.ForceUpdate ? (byte) 1 : (byte) 0);
        dest.writeString(this.UpdateMessage);
        dest.writeString(this.AppUrl);
    }

    public AppVersion() {
    }

    protected AppVersion(Parcel in) {
        this.OS = in.readString();
        this.Version = in.readInt();
        this.ForceUpdate = in.readByte() != 0;
        this.UpdateMessage = in.readString();
        this.AppUrl = in.readString();
    }

    public static final Parcelable.Creator<AppVersion> CREATOR = new Parcelable.Creator<AppVersion>() {
        @Override
        public AppVersion createFromParcel(Parcel source) {
            return new AppVersion(source);
        }

        @Override
        public AppVersion[] newArray(int size) {
            return new AppVersion[size];
        }
    };
}
