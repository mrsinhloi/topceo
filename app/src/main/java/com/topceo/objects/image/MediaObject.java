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


    protected MediaObject(Parcel in) {
        Link = in.readString();
        Width = in.readInt();
        Height = in.readInt();
        SizeInKb = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Link);
        dest.writeInt(Width);
        dest.writeInt(Height);
        dest.writeInt(SizeInKb);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaObject> CREATOR = new Creator<MediaObject>() {
        @Override
        public MediaObject createFromParcel(Parcel in) {
            return new MediaObject(in);
        }

        @Override
        public MediaObject[] newArray(int size) {
            return new MediaObject[size];
        }
    };

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




    //local
    private File fileTemp;
    public File getFileTemp() {
        return fileTemp;
    }

    public void setFileTemp(File fileTemp) {
        this.fileTemp = fileTemp;
    }
}
