package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2017-08-30.
 */

public class Text implements Parcelable {
    private String contentType;
    private String content;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contentType);
        dest.writeString(this.content);
    }

    public Text() {
    }

    protected Text(Parcel in) {
        this.contentType = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Text> CREATOR = new Parcelable.Creator<Text>() {
        @Override
        public Text createFromParcel(Parcel source) {
            return new Text(source);
        }

        @Override
        public Text[] newArray(int size) {
            return new Text[size];
        }
    };
}
