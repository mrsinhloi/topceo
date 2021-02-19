package com.topceo.services;

import android.os.Parcel;
import android.os.Parcelable;

public class SasParent implements Parcelable {

    private SasChild Original;//avata
    private SasChild Large;//post image
    private SasChild Medium;
    private SasChild Small;

    public SasChild getOriginal() {
        return Original;
    }

    public void setOriginal(SasChild original) {
        Original = original;
    }

    public SasChild getMedium() {
        return Medium;
    }

    public void setMedium(SasChild medium) {
        Medium = medium;
    }

    public SasChild getSmall() {
        return Small;
    }

    public void setSmall(SasChild small) {
        Small = small;
    }


    public SasParent() {
    }

    public SasChild getLarge() {
        return Large;
    }

    public void setLarge(SasChild large) {
        Large = large;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.Original, flags);
        dest.writeParcelable(this.Large, flags);
        dest.writeParcelable(this.Medium, flags);
        dest.writeParcelable(this.Small, flags);
    }

    protected SasParent(Parcel in) {
        this.Original = in.readParcelable(SasChild.class.getClassLoader());
        this.Large = in.readParcelable(SasChild.class.getClassLoader());
        this.Medium = in.readParcelable(SasChild.class.getClassLoader());
        this.Small = in.readParcelable(SasChild.class.getClassLoader());
    }

    public static final Creator<SasParent> CREATOR = new Creator<SasParent>() {
        @Override
        public SasParent createFromParcel(Parcel source) {
            return new SasParent(source);
        }

        @Override
        public SasParent[] newArray(int size) {
            return new SasParent[size];
        }
    };
}
