package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.workchat.core.utils.MyUtils;

/**
 * Created by MrPhuong on 1/31/2018.
 */

public class Video implements Parcelable {

    private String name;
    private String extension;
    private String link;
    private String thumbLink;
    private int width;
    private int height;
    private float size;
    private int thumbWidth;
    private int thumbHeight;
    private float thumbSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getLink() {
        if (TextUtils.isEmpty(link)) link = "";
        link = MyUtils.endcodeURL(link);
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbLink() {
        return thumbLink;
    }

    public void setThumbLink(String thumbLink) {
        this.thumbLink = thumbLink;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public float getThumbSize() {
        return thumbSize;
    }

    public void setThumbSize(float thumbSize) {
        this.thumbSize = thumbSize;
    }

    public Video() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.extension);
        dest.writeString(this.link);
        dest.writeString(this.thumbLink);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeFloat(this.size);
        dest.writeInt(this.thumbWidth);
        dest.writeInt(this.thumbHeight);
        dest.writeFloat(this.thumbSize);
    }

    protected Video(Parcel in) {
        this.name = in.readString();
        this.extension = in.readString();
        this.link = in.readString();
        this.thumbLink = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.size = in.readFloat();
        this.thumbWidth = in.readInt();
        this.thumbHeight = in.readInt();
        this.thumbSize = in.readFloat();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
