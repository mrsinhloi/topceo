package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Ai da like
 */
public class ImageCommentLike implements Parcelable {


    private long ItemId;
    private long CommentId;
    private long CreateDate;
    private long UserId;
    private String UserName;
    private String AvatarSmall;
    private String FullName;

    public long getItemId() {
        return ItemId;
    }

    public void setItemId(long itemId) {
        ItemId = itemId;
    }

    public long getCommentId() {
        return CommentId;
    }

    public void setCommentId(long commentId) {
        CommentId = commentId;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAvatarSmall() {
        return AvatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        AvatarSmall = avatarSmall;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ItemId);
        dest.writeLong(this.CommentId);
        dest.writeLong(this.CreateDate);
        dest.writeLong(this.UserId);
        dest.writeString(this.UserName);
        dest.writeString(this.AvatarSmall);
        dest.writeString(this.FullName);
    }

    public ImageCommentLike() {
    }

    protected ImageCommentLike(Parcel in) {
        this.ItemId = in.readLong();
        this.CommentId = in.readLong();
        this.CreateDate = in.readLong();
        this.UserId = in.readLong();
        this.UserName = in.readString();
        this.AvatarSmall = in.readString();
        this.FullName = in.readString();
    }

    public static final Parcelable.Creator<ImageCommentLike> CREATOR = new Parcelable.Creator<ImageCommentLike>() {
        @Override
        public ImageCommentLike createFromParcel(Parcel source) {
            return new ImageCommentLike(source);
        }

        @Override
        public ImageCommentLike[] newArray(int size) {
            return new ImageCommentLike[size];
        }
    };
}
