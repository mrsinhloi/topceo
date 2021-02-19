package com.topceo.objects.menu;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MenuShop implements Parcelable {

    private int MenuId;
    private String MenuName;
    private String MenuIcon;
    private int Position;
    private String TargetType;

    public JsonObject getTargetContent() {
        return TargetContent;
    }

    public void setTargetContent(JsonObject targetContent) {
        TargetContent = targetContent;
    }

    private JsonObject TargetContent;//json

    public int getMenuId() {
        return MenuId;
    }

    public void setMenuId(int menuId) {
        MenuId = menuId;
    }

    public String getMenuName() {
        return MenuName;
    }

    public void setMenuName(String menuName) {
        MenuName = menuName;
    }

    public String getMenuIcon() {
        return MenuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        MenuIcon = menuIcon;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public String getTargetType() {
        return TargetType;
    }

    public void setTargetType(String targetType) {
        TargetType = targetType;
    }


    public MenuShop() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.MenuId);
        dest.writeString(this.MenuName);
        dest.writeString(this.MenuIcon);
        dest.writeInt(this.Position);
        dest.writeString(this.TargetType);
        dest.writeString((this.TargetContent==null)?"":this.TargetContent.toString());
    }

    protected MenuShop(Parcel in) {
        this.MenuId = in.readInt();
        this.MenuName = in.readString();
        this.MenuIcon = in.readString();
        this.Position = in.readInt();
        this.TargetType = in.readString();
        this.TargetContent = new Gson().fromJson(in.readString(), JsonObject.class);
    }

    public static final Creator<MenuShop> CREATOR = new Creator<MenuShop>() {
        @Override
        public MenuShop createFromParcel(Parcel source) {
            return new MenuShop(source);
        }

        @Override
        public MenuShop[] newArray(int size) {
            return new MenuShop[size];
        }
    };
}
