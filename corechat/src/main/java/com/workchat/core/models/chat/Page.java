package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class Page implements Parcelable {
    private int pageId;
    private String pageName;
    private String pageLink;
    private String pageImage;

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    public String getPageImage() {
        return pageImage;
    }

    public void setPageImage(String pageImage) {
        this.pageImage = pageImage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageId);
        dest.writeString(this.pageName);
        dest.writeString(this.pageLink);
        dest.writeString(this.pageImage);
    }

    public Page() {
    }

    protected Page(Parcel in) {
        this.pageId = in.readInt();
        this.pageName = in.readString();
        this.pageLink = in.readString();
        this.pageImage = in.readString();
    }

    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {
        @Override
        public Page createFromParcel(Parcel source) {
            return new Page(source);
        }

        @Override
        public Page[] newArray(int size) {
            return new Page[size];
        }
    };
}
