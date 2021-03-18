package com.topceo.objects.image;


import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.db.ItemDB;
import com.smartapp.collage.MediaLocal;

import java.util.ArrayList;


/*ItemType (String): IMAGE / VIDEO
Large: MediaObject
Medium: MediaObject
Small: MediaObject*/
public class Item implements Parcelable {

    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_VIDEO = "VIDEO";


    private String ItemType;
    private MediaObject Large;
    private MediaObject Medium;
    private MediaObject Small;
    private Info Info;


    protected Item(Parcel in) {
        ItemType = in.readString();
        Large = in.readParcelable(MediaObject.class.getClassLoader());
        Medium = in.readParcelable(MediaObject.class.getClassLoader());
        Small = in.readParcelable(MediaObject.class.getClassLoader());
        Info = in.readParcelable(com.topceo.objects.image.Info.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ItemType);
        dest.writeParcelable(Large, flags);
        dest.writeParcelable(Medium, flags);
        dest.writeParcelable(Small, flags);
        dest.writeParcelable(Info, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public static ArrayList<MediaLocal> getMediaLocal(ArrayList<Item> itemContent, boolean isVideo) {
        ArrayList<MediaLocal> list = new ArrayList<>();
        if (itemContent != null && itemContent.size() > 0) {
            for (int i = 0; i < itemContent.size(); i++) {
                Item item = itemContent.get(i);

                MediaObject medium = item.getMedium();
                if (medium != null) {
                    int width = medium.getWidth();
                    int height = medium.getHeight();
                    MediaLocal media = new MediaLocal(medium.getLink(), width, height, isVideo, null);
                    list.add(media);
                }
            }
        }
        return list;
    }

    public ItemDB copy() {
        ItemDB item = new ItemDB();
        item.setItemType(ItemType);
        item.setLarge(Large.copy());
        item.setMedium(Medium.copy());
        item.setSmall(Small.copy());
        if (Info != null) {
            item.setInfo(Info.copy());
        }

        return item;
    }

    public com.topceo.objects.image.Info getInfo() {
        return Info;
    }

    public void setInfo(com.topceo.objects.image.Info info) {
        Info = info;
    }


    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public MediaObject getLarge() {
        return Large;
    }

    public void setLarge(MediaObject large) {
        Large = large;
    }

    public MediaObject getMedium() {
        return Medium;
    }

    public void setMedium(MediaObject medium) {
        Medium = medium;
    }

    public MediaObject getSmall() {
        return Small;
    }

    public void setSmall(MediaObject small) {
        Small = small;
    }


    public Item() {
    }



}
