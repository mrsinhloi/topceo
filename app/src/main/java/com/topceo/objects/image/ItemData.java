package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class ItemData extends RealmObject implements Parcelable {
    private LinkPreview LinkPreview;

    protected ItemData(Parcel in) {
        LinkPreview = in.readParcelable(com.topceo.objects.image.LinkPreview.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(LinkPreview, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemData> CREATOR = new Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel in) {
            return new ItemData(in);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };

    public com.topceo.objects.image.LinkPreview getLinkPreview() {
        return LinkPreview;
    }

    public void setLinkPreview(com.topceo.objects.image.LinkPreview linkPreview) {
        LinkPreview = linkPreview;
    }
    public ItemData() {
    }

}
