package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.workchat.core.utils.MyUtils;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class Link implements Parcelable {

    private String text;
    private String link;
    private boolean isParse;//true: thi app parse va update len server
    private String title;
    private String imageLink;
    private String description;

    public String getLink() {
        if (TextUtils.isEmpty(link)) link = "";
        link = MyUtils.endcodeURL(link);
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isParse() {
        return isParse;
    }

    public void setParse(boolean parse) {
        isParse = parse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeString(this.link);
        dest.writeByte(this.isParse ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeString(this.imageLink);
        dest.writeString(this.description);
    }

    public Link() {
    }

    protected Link(Parcel in) {
        this.text = in.readString();
        this.link = in.readString();
        this.isParse = in.readByte() != 0;
        this.title = in.readString();
        this.imageLink = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Link> CREATOR = new Parcelable.Creator<Link>() {
        @Override
        public Link createFromParcel(Parcel source) {
            return new Link(source);
        }

        @Override
        public Link[] newArray(int size) {
            return new Link[size];
        }
    };
}
