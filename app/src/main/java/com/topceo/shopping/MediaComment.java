package com.topceo.shopping;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MediaComment implements Parcelable {

    public static final String COMMENT_ID = "COMMENT_ID";
    public static final String IS_MEDIA_COMMENT = "IS_MEDIA_COMMENT";
    private long ItemId;//	Float
    private long ImageItemId;//	Float
    private String Comment;//	String
    private long CreateDate;//	Unix TimeStamp
    private String CreateOS;//	string
    private long LastModifyDate;//	Unix TimeStamp
    private String LastModifyOS;//	string
    private long ReplyToId;//	Float
    private boolean Edited;//	Bool
    private int LikeCount;//	int
    private int ReplyCount;//	int
    private long UserId;//	Float
    private String UserName;//	String
    private String AvatarSmall;//	string
    private String FullName;//	string
    private boolean IsLiked;//	bool


    public boolean isShowCommentChild() {
        return isShowCommentChild;
    }

    public void setShowCommentChild(boolean showCommentChild) {
        isShowCommentChild = showCommentChild;
    }

    //local
    private boolean isShowCommentChild = false;


    //bo sung khi load phan tu con
    private ArrayList<MediaComment> listChild = new ArrayList<>();

    public ArrayList<MediaComment> getListChild() {
        if (listChild == null) listChild = new ArrayList<>();
        return listChild;
    }

    public void setListChild(ArrayList<MediaComment> listChild) {
        this.listChild = listChild;
    }


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

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public String getCreateOS() {
        return CreateOS;
    }

    public void setCreateOS(String createOS) {
        CreateOS = createOS;
    }

    public long getLastModifyDate() {
        return LastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        LastModifyDate = lastModifyDate;
    }

    public String getLastModifyOS() {
        return LastModifyOS;
    }

    public void setLastModifyOS(String lastModifyOS) {
        LastModifyOS = lastModifyOS;
    }

    public long getReplyToId() {
        return ReplyToId;
    }

    public void setReplyToId(long replyToId) {
        ReplyToId = replyToId;
    }

    public boolean isEdited() {
        return Edited;
    }

    public void setEdited(boolean edited) {
        Edited = edited;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(int likeCount) {
        LikeCount = likeCount;
    }

    public int getReplyCount() {
        return ReplyCount;
    }

    public void setReplyCount(int replyCount) {
        ReplyCount = replyCount;
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

    public boolean isLiked() {
        return IsLiked;
    }

    public void setLiked(boolean liked) {
        IsLiked = liked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ItemId);
        dest.writeLong(this.ImageItemId);
        dest.writeString(this.Comment);
        dest.writeLong(this.CreateDate);
        dest.writeString(this.CreateOS);
        dest.writeLong(this.LastModifyDate);
        dest.writeString(this.LastModifyOS);
        dest.writeLong(this.ReplyToId);
        dest.writeByte(this.Edited ? (byte) 1 : (byte) 0);
        dest.writeInt(this.LikeCount);
        dest.writeInt(this.ReplyCount);
        dest.writeLong(this.UserId);
        dest.writeString(this.UserName);
        dest.writeString(this.AvatarSmall);
        dest.writeString(this.FullName);
        dest.writeByte(this.IsLiked ? (byte) 1 : (byte) 0);
    }

    public MediaComment() {
    }

    protected MediaComment(Parcel in) {
        this.ItemId = in.readLong();
        this.ImageItemId = in.readLong();
        this.Comment = in.readString();
        this.CreateDate = in.readLong();
        this.CreateOS = in.readString();
        this.LastModifyDate = in.readLong();
        this.LastModifyOS = in.readString();
        this.ReplyToId = in.readLong();
        this.Edited = in.readByte() != 0;
        this.LikeCount = in.readInt();
        this.ReplyCount = in.readInt();
        this.UserId = in.readLong();
        this.UserName = in.readString();
        this.AvatarSmall = in.readString();
        this.FullName = in.readString();
        this.IsLiked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MediaComment> CREATOR = new Parcelable.Creator<MediaComment>() {
        @Override
        public MediaComment createFromParcel(Parcel source) {
            return new MediaComment(source);
        }

        @Override
        public MediaComment[] newArray(int size) {
            return new MediaComment[size];
        }
    };
}
