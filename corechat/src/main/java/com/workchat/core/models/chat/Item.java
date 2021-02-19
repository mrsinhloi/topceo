package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.workchat.core.utils.MyUtils;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class Item implements Parcelable {

    private long itemId;
    private String itemName;
    private String itemImage;
    private String itemLink;
    private String itemPrice;
    private String itemOriginPrice;


    public long getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        if (TextUtils.isEmpty(itemImage)) itemImage = "";
        itemImage = MyUtils.endcodeURL(itemImage);
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemLink() {
        if (TextUtils.isEmpty(itemLink)) itemLink = "";
        itemLink = MyUtils.endcodeURL(itemLink);
        return itemLink;
    }

    public void setItemLink(String itemLink) {
        this.itemLink = itemLink;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemOriginPrice() {
        return itemOriginPrice;
    }

    public void setItemOriginPrice(String itemOriginPrice) {
        this.itemOriginPrice = itemOriginPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.itemId);
        dest.writeString(this.itemName);
        dest.writeString(this.itemImage);
        dest.writeString(this.itemLink);
        dest.writeString(this.itemPrice);
        dest.writeString(this.itemOriginPrice);
    }

    public Item() {
    }

    protected Item(Parcel in) {
        this.itemId = in.readLong();
        this.itemName = in.readString();
        this.itemImage = in.readString();
        this.itemLink = in.readString();
        this.itemPrice = in.readString();
        this.itemOriginPrice = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
