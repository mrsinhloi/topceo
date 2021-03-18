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

    protected ImageShare(Parcel in) {
        ItemId = in.readLong();
        ImageItemId = in.readLong();
        ShareTo = in.readInt();
        CreateDate = in.readLong();
        User = in.readParcelable(UserShortDB.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ItemId);
        dest.writeLong(ImageItemId);
        dest.writeInt(ShareTo);
        dest.writeLong(CreateDate);
        dest.writeParcelable(User, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageShare> CREATOR = new Creator<ImageShare>() {
        @Override
        public ImageShare createFromParcel(Parcel in) {
            return new ImageShare(in);
        }

        @Override
        public ImageShare[] newArray(int size) {
            return new ImageShare[size];
        }
    };

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



    public ImageShare() {
    }


}
