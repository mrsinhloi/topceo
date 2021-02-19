package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2016-09-15.
 *
 + Hashtag có Image là chuỗi rỗng, bên app tự thay bằng hình #, Title chính là HashTag app tự thêm dấu # vào trước, Description là số lượng Post, app tự thay bằng câu hiển thị tương ứng với language, TypeId = 0
 + User có Image là Avatar, UserName là Title, FullName là Description, TypeId = 1
 To enable screen reader support, press shortcut Ctrl+Alt+Z. To learn about keyboard shortcuts, press shortcut Ctrl+slash.

 */
public class SearchObject implements Parcelable {
    public static final int TYPE_HASH_TAG=0;
    public static final int TYPE_USER=1;
    public static final java.lang.String KEY_WORD = "KEY_WORD";

    private String Image;
    private String Title;
    private String Description;
    private int TypeId;

    public SearchObject(){}
    public SearchObject(String Image, String Title, String Description, int TypeId){
        this.Image=Image;
        this.Title=Title;
        this.Description=Description;
        this.TypeId=TypeId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getTypeId() {
        return TypeId;
    }

    public void setTypeId(int typeId) {
        TypeId = typeId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Image);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeInt(this.TypeId);
    }

    protected SearchObject(Parcel in) {
        this.Image = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
        this.TypeId = in.readInt();
    }

    public static final Parcelable.Creator<SearchObject> CREATOR = new Parcelable.Creator<SearchObject>() {
        @Override
        public SearchObject createFromParcel(Parcel source) {
            return new SearchObject(source);
        }

        @Override
        public SearchObject[] newArray(int size) {
            return new SearchObject[size];
        }
    };
}
