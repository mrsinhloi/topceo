package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.db.MediaObjectDB;

import java.io.File;


public class MediaObject implements Parcelable {



//    @PrimaryKey
//    private String id = UUID.randomUUID().toString();
    private String Link = "";
    private int Width = 0;
    private int Height = 0;
    private int SizeInKb = 0;


    public MediaObjectDB copy(){
        MediaObjectDB item = new MediaObjectDB();
        item.setLink(Link);
        item.setWidth(Width);
        item.setHeight(Height);
        item.setSizeInKb(SizeInKb);
        return item;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public long getSizeInKb() {
        return SizeInKb;
    }

    public void setSizeInKb(int sizeInKb) {
        SizeInKb = sizeInKb;
    }

    public void setSizeInKb(long sizeInKb) {
        SizeInKb = (int) sizeInKb;
    }

    public MediaObject() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Link);
        dest.writeInt(this.Width);
        dest.writeInt(this.Height);
        dest.writeInt(this.SizeInKb);
    }

    protected MediaObject(Parcel in) {
        this.Link = in.readString();
        this.Width = in.readInt();
        this.Height = in.readInt();
        this.SizeInKb = in.readInt();
    }

    public static final Creator<MediaObject> CREATOR = new Creator<MediaObject>() {
        @Override
        public MediaObject createFromParcel(Parcel source) {
            return new MediaObject(source);
        }

        @Override
        public MediaObject[] newArray(int size) {
            return new MediaObject[size];
        }
    };

    //local
    private File fileTemp;
    public File getFileTemp() {
        return fileTemp;
    }

    public void setFileTemp(File fileTemp) {
        this.fileTemp = fileTemp;
    }
}
