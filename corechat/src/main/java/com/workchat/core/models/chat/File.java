package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.workchat.core.utils.MyUtils;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class File implements Parcelable {
    private String name;
    private String extension;
    private String link;
    private float size;

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

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public File() {
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
        dest.writeFloat(this.size);
    }

    protected File(Parcel in) {
        this.name = in.readString();
        this.extension = in.readString();
        this.link = in.readString();
        this.size = in.readFloat();
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel source) {
            return new File(source);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };
}
