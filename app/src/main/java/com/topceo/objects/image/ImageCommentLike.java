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

    protected ImageCommentLike(Parcel in) {
        ItemId = in.readLong();
        CommentId = in.readLong();
        CreateDate = in.readLong();
        UserId = in.readLong();
        UserName = in.readString();
        AvatarSmall = in.readString();
        FullName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ItemId);
        dest.writeLong(CommentId);
        dest.writeLong(CreateDate);
        dest.writeLong(UserId);
        dest.writeString(UserName);
        dest.writeString(AvatarSmall);
        dest.writeString(FullName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageCommentLike> CREATOR = new Creator<ImageCommentLike>() {
        @Override
        public ImageCommentLike createFromParcel(Parcel in) {
            return new ImageCommentLike(in);
        }

        @Override
        public ImageCommentLike[] newArray(int size) {
            return new ImageCommentLike[size];
        }
    };

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

    public ImageCommentLike() {
    }


}
