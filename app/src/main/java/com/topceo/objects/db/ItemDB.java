package com.topceo.objects.db;


import com.topceo.objects.image.Item;

import io.realm.RealmObject;


/*ItemType (String): IMAGE / VIDEO
Large: MediaObject
Medium: MediaObject
Small: MediaObject*/
public class ItemDB extends RealmObject {

    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_VIDEO = "VIDEO";



//    @PrimaryKey
//    private String id = UUID.randomUUID().toString();
    private String ItemType;
    private MediaObjectDB Large;
    private MediaObjectDB Medium;
    private MediaObjectDB Small;
    private InfoDB Info;

    public Item copy(){
        Item item = new Item();
        item.setItemType(ItemType);
        item.setLarge(Large.copy());
        item.setMedium(Medium.copy());
        item.setSmall(Small.copy());
        if(Info!=null){
            item.setInfo(Info.copy());
        }

        return item;
    }


    public InfoDB getInfo() {
        return Info;
    }

    public void setInfo(InfoDB info) {
        Info = info;
    }



    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public MediaObjectDB getLarge() {
        return Large;
    }

    public void setLarge(MediaObjectDB large) {
        Large = large;
    }

    public MediaObjectDB getMedium() {
        return Medium;
    }

    public void setMedium(MediaObjectDB medium) {
        Medium = medium;
    }

    public MediaObjectDB getSmall() {
        return Small;
    }

    public void setSmall(MediaObjectDB small) {
        Small = small;
    }


    public ItemDB() {
    }


}
