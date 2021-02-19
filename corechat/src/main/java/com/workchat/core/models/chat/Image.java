package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.workchat.core.utils.MyUtils;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class Image implements Parcelable {

    private String name;
    private String extension;//jpg, png, gif
    private String link;
    private String thumbLink;
    private float width;
    private float height;
    private float thumbWidth;
    private float thumbHeight;
    private float size;
    private float thumbSize;

    public String getName() {
        if (name == null) name = "";
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
        if (TextUtils.isEmpty(thumbLink)) thumbLink = "";
        thumbLink = MyUtils.endcodeURL(thumbLink);
        return thumbLink;
    }

    public void setThumbLink(String thumbLink) {
        this.thumbLink = thumbLink;
    }

    public int getWidth() {
        return (int) width;
    }

    public int getHeight() {
        return (int) height;
    }

    public float getSize() {
        return size;
    }

    public int getThumbWidth() {
        return (int) thumbWidth;
    }

    public int getThumbHeight() {
        return (int) thumbHeight;
    }

    public float getThumbSize() {
        return thumbSize;
    }

    public void setThumbSize(float thumbSize) {
        this.thumbSize = thumbSize;
    }

    public Image() {
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
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
        dest.writeFloat(this.width);
        dest.writeFloat(this.height);
        dest.writeFloat(this.size);
        dest.writeFloat(this.thumbWidth);
        dest.writeFloat(this.thumbHeight);
        dest.writeFloat(this.thumbSize);
    }

    protected Image(Parcel in) {
        this.name = in.readString();
        this.extension = in.readString();
        this.link = in.readString();
        this.thumbLink = in.readString();
        this.width = in.readFloat();
        this.height = in.readFloat();
        this.size = in.readFloat();
        this.thumbWidth = in.readFloat();
        this.thumbHeight = in.readFloat();
        this.thumbSize = in.readFloat();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
