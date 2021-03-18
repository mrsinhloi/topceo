package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.db.UserShortDB;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MrPhuong on 2016-07-27.
 */
public class ImageLike extends RealmObject implements Parcelable {
    public static final String ARRAY_LIST = "ARRAY_LIST";

    @PrimaryKey
    private long ItemId;
    private long ImageItemId;
    private int LikeEmotionId;//default 1
    private long CreateDate;
    private UserShortDB User;//user da like


    protected ImageLike(Parcel in) {
        ItemId = in.readLong();
        ImageItemId = in.readLong();
        LikeEmotionId = in.readInt();
        CreateDate = in.readLong();
        User = in.readParcelable(UserShortDB.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ItemId);
        dest.writeLong(ImageItemId);
        dest.writeInt(LikeEmotionId);
        dest.writeLong(CreateDate);
        dest.writeParcelable(User, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageLike> CREATOR = new Creator<ImageLike>() {
        @Override
        public ImageLike createFromParcel(Parcel in) {
            return new ImageLike(in);
        }

        @Override
        public ImageLike[] newArray(int size) {
            return new ImageLike[size];
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

    public int getLikeEmotionId() {
        return LikeEmotionId;
    }

    public void setLikeEmotionId(int likeEmotionId) {
        LikeEmotionId = likeEmotionId;
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




    public ImageLike() {
    }


}
