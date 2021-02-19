package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class AlbumItem implements Parcelable {


    private String name;
    private String extension;
    private String link;
    private String thumbLink;
    private String width;
    private String height;
    private String size;
    private String thumbWidth;
    private String thumbHeight;
    private String thumbSize;
    private boolean isVideo;
    private int videoLength;


    //local
    private boolean isUploading;
    private String localPath;
    private int percent = 0;


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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(String thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public String getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(String thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public String getThumbSize() {
        return thumbSize;
    }

    public void setThumbSize(String thumbSize) {
        this.thumbSize = thumbSize;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
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
        dest.writeString(this.width);
        dest.writeString(this.height);
        dest.writeString(this.size);
        dest.writeString(this.thumbWidth);
        dest.writeString(this.thumbHeight);
        dest.writeString(this.thumbSize);
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
        dest.writeInt(this.videoLength);
    }

    public AlbumItem() {
    }

    protected AlbumItem(Parcel in) {
        this.name = in.readString();
        this.extension = in.readString();
        this.link = in.readString();
        this.thumbLink = in.readString();
        this.width = in.readString();
        this.height = in.readString();
        this.size = in.readString();
        this.thumbWidth = in.readString();
        this.thumbHeight = in.readString();
        this.thumbSize = in.readString();
        this.isVideo = in.readByte() != 0;
        this.videoLength = in.readInt();
    }

    public static final Parcelable.Creator<AlbumItem> CREATOR = new Parcelable.Creator<AlbumItem>() {
        @Override
        public AlbumItem createFromParcel(Parcel source) {
            return new AlbumItem(source);
        }

        @Override
        public AlbumItem[] newArray(int size) {
            return new AlbumItem[size];
        }
    };

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
