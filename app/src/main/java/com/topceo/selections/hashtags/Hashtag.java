package com.topceo.selections.hashtags;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Hashtag implements Parcelable {

    private long ItemId;
    private long HashtagId;
    private String Hashtag;
    private String Name;
    private String NameEN;
    private String ImageUrl;
    private int Order;
    private long CategoryId;
    private boolean IsSelected;

    public static ArrayList<HashtagShort> convert(ArrayList<com.topceo.selections.hashtags.Hashtag> tags) {
        ArrayList<HashtagShort> list = new ArrayList<>();
        if(tags!=null && tags.size()>0){
            for (int i = 0; i < tags.size(); i++) {
                com.topceo.selections.hashtags.Hashtag item = tags.get(i);
                HashtagShort h = new HashtagShort();
                h.setHashtagId(item.getHashtagId());
                h.setHashtag(item.getHashtag());
                list.add(h);
            }
        }
        return list;
    }


    public long getItemId() {
        return ItemId;
    }

    public void setItemId(long itemId) {
        ItemId = itemId;
    }

    public long getHashtagId() {
        return HashtagId;
    }

    public void setHashtagId(long hashtagId) {
        HashtagId = hashtagId;
    }

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Hashtag = hashtag;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNameEN() {
        return NameEN;
    }

    public void setNameEN(String nameEN) {
        NameEN = nameEN;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public long getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(long categoryId) {
        CategoryId = categoryId;
    }

    public boolean isSelected() {
        return IsSelected;
    }

    public void setSelected(boolean selected) {
        IsSelected = selected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ItemId);
        dest.writeLong(this.HashtagId);
        dest.writeString(this.Hashtag);
        dest.writeString(this.Name);
        dest.writeString(this.NameEN);
        dest.writeString(this.ImageUrl);
        dest.writeInt(this.Order);
        dest.writeLong(this.CategoryId);
        dest.writeByte(this.IsSelected ? (byte) 1 : (byte) 0);
    }

    public Hashtag() {
    }

    protected Hashtag(Parcel in) {
        this.ItemId = in.readLong();
        this.HashtagId = in.readLong();
        this.Hashtag = in.readString();
        this.Name = in.readString();
        this.NameEN = in.readString();
        this.ImageUrl = in.readString();
        this.Order = in.readInt();
        this.CategoryId = in.readLong();
        this.IsSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Hashtag> CREATOR = new Parcelable.Creator<Hashtag>() {
        @Override
        public Hashtag createFromParcel(Parcel source) {
            return new Hashtag(source);
        }

        @Override
        public Hashtag[] newArray(int size) {
            return new Hashtag[size];
        }
    };
}
