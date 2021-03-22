package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class MyItemData extends RealmObject implements Parcelable {
    private LinkPreview LinkPreview;

    protected MyItemData(Parcel in) {
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

    public static final Creator<MyItemData> CREATOR = new Creator<MyItemData>() {
        @Override
        public MyItemData createFromParcel(Parcel in) {
            return new MyItemData(in);
        }

        @Override
        public MyItemData[] newArray(int size) {
            return new MyItemData[size];
        }
    };

    public com.topceo.objects.image.LinkPreview getLinkPreview() {
        return LinkPreview;
    }

    public void setLinkPreview(com.topceo.objects.image.LinkPreview linkPreview) {
        LinkPreview = linkPreview;
    }
    public MyItemData() {
    }

}
