package com.topceo.retrofit;

import com.topceo.objects.image.Item;
import com.topceo.objects.image.MyItemData;

import java.util.ArrayList;
import java.util.List;

public class PostImageParam {

    private String ItemGUID;
    private boolean IsPrivate;
    private String Description;
    private String Location;
    private double Lat;
    private double Long;
    private ArrayList<Item> ItemContent;
    private List<String> Hashtags;
    private List<String> UserTags;
    private String ItemType;
    private MyItemData ItemData;
    private long GroupId;


    public PostImageParam() { }

    public PostImageParam(
            String ItemGUID,
            boolean IsPrivate,
            String Description,
            String Location,
            double Lat,
            double Long,
            ArrayList<Item> ItemContent,
            List<String> Hashtags,
            List<String> UserTags,
            String ItemType,
            MyItemData ItemData
    ) {
        this.ItemGUID = ItemGUID;
        this.IsPrivate = IsPrivate;
        this.Description = Description;
        this.Location = Location;
        this.Lat = Lat;
        this.Long = Long;
        this.ItemContent = ItemContent;
        this.Hashtags = Hashtags;
        this.UserTags = UserTags;
        this.ItemType = ItemType;
        this.ItemData = ItemData;
    }

    public String getItemGUID() {
        return ItemGUID;
    }

    public void setItemGUID(String itemGUID) {
        ItemGUID = itemGUID;
    }

    public boolean isPrivate() {
        return IsPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        IsPrivate = aPrivate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public ArrayList<Item> getItemContent() {
        return ItemContent;
    }

    public void setItemContent(ArrayList<Item> itemContent) {
        ItemContent = itemContent;
    }

    public List<String> getHashtags() {
        return Hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        Hashtags = hashtags;
    }

    public List<String> getUserTags() {
        return UserTags;
    }

    public void setUserTags(ArrayList<String> userTags) {
        UserTags = userTags;
    }

    public void setUserTags(List<String> userTags) {
        UserTags = userTags;
    }

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public MyItemData getItemData() {
        return ItemData;
    }

    public void setItemData(MyItemData myItemData) {
        ItemData = myItemData;
    }

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {
        GroupId = groupId;
    }
}
