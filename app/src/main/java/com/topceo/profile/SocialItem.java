package com.topceo.profile;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialItem implements Parcelable {
    private String NameCode;
    private String Link;

    public String getNameCode() {
        return NameCode;
    }

    public void setNameCode(String nameCode) {
        NameCode = nameCode;
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
        dest.writeString(this.NameCode);
        dest.writeString(this.Link);
    }

    public SocialItem() {
    }

    protected SocialItem(Parcel in) {
        this.NameCode = in.readString();
        this.Link = in.readString();
    }

    public static final Parcelable.Creator<SocialItem> CREATOR = new Parcelable.Creator<SocialItem>() {
        @Override
        public SocialItem createFromParcel(Parcel source) {
            return new SocialItem(source);
        }

        @Override
        public SocialItem[] newArray(int size) {
            return new SocialItem[size];
        }
    };
}
