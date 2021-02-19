package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class ItemData extends RealmObject implements Parcelable {
    private LinkPreview LinkPreview;

    public com.topceo.objects.image.LinkPreview getLinkPreview() {
        return LinkPreview;
    }

    public void setLinkPreview(com.topceo.objects.image.LinkPreview linkPreview) {
        LinkPreview = linkPreview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.LinkPreview, flags);
    }

    public ItemData() {
    }

    protected ItemData(Parcel in) {
        this.LinkPreview = in.readParcelable(com.topceo.objects.image.LinkPreview.class.getClassLoader());
    }

    public static final Parcelable.Creator<ItemData> CREATOR = new Parcelable.Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel source) {
            return new ItemData(source);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };
}
