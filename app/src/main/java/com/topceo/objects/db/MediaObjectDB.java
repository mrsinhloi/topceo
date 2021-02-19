package com.topceo.objects.db;

import com.topceo.objects.image.MediaObject;

import io.realm.RealmObject;


public class MediaObjectDB extends RealmObject{



//    @PrimaryKey
//    private String id = UUID.randomUUID().toString();
    private String Link = "";
    private int Width = 0;
    private int Height = 0;
    private int SizeInKb = 0;

    public MediaObject copy(){
        MediaObject item = new MediaObject();
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

    public MediaObjectDB() {
    }


}
