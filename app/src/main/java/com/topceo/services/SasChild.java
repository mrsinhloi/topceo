package com.topceo.services;

import android.os.Parcel;
import android.os.Parcelable;

public class SasChild implements Parcelable {
    private String SAS;
    private String Link;

    public String getSAS() {
        return SAS;
    }

    public void setSAS(String SAS) {
        this.SAS = SAS;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.SAS);
        dest.writeString(this.Link);
    }

    public SasChild() {
    }

    protected SasChild(Parcel in) {
        this.SAS = in.readString();
        this.Link = in.readString();
    }

    public static final Parcelable.Creator<SasChild> CREATOR = new Parcelable.Creator<SasChild>() {
        @Override
        public SasChild createFromParcel(Parcel source) {
            return new SasChild(source);
        }

        @Override
        public SasChild[] newArray(int size) {
            return new SasChild[size];
        }
    };
}
