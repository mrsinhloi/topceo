package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.db.UserShortDB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MrPhuong on 2016-07-27.
 */
public class ImageShare extends RealmObject implements Parcelable {
    public static final String ARRAY_LIST = "ARRAY_LIST";

    @PrimaryKey
    private long ItemId;
    private long ImageItemId;
    private int ShareTo;//Share đến đâu (Wall, Facebook, ...)
    private long CreateDate;
    private UserShortDB User;//nguoi da share

    public long getItemId() {
        return ItemId;
    }

    public void setItemId(long itemId) {
        ItemId = itemId;
    }

    public long getImageItemId() {
        return ImageItemId;
    }

    public void setImageItemId(long imageItemId) {
        ImageItemId = imageItemId;
    }

    public int getShareTo() {
        return ShareTo;
    }

    public void setShareTo(int shareTo) {
        ShareTo = shareTo;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public UserShortDB getUser() {
        return User;
    }

    public void setUser(UserShortDB user) {
        User = user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ItemId);
        dest.writeLong(this.ImageItemId);
        dest.writeInt(this.ShareTo);
        dest.writeLong(this.CreateDate);
        dest.writeParcelable(this.User, flags);
    }

    public ImageShare() {
    }

    protected ImageShare(Parcel in) {
        this.ItemId = in.readLong();
        this.ImageItemId = in.readLong();
        this.ShareTo = in.readInt();
        this.CreateDate = in.readLong();
        this.User = in.readParcelable(UserShortDB.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImageShare> CREATOR = new Parcelable.Creator<ImageShare>() {
        @Override
        public ImageShare createFromParcel(Parcel source) {
            return new ImageShare(source);
        }

        @Override
        public ImageShare[] newArray(int size) {
            return new ImageShare[size];
        }
    };
}
