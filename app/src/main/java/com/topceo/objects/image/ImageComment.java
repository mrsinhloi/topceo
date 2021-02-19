package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.other.UserShort;

import io.realm.RealmList;

/**
 * Created by MrPhuong on 2016-07-27.
 */
public class ImageComment implements Parcelable {
    public static final String IMAGE_COMMENT_ARRAY_LIST = "IMAGE_COMMENT_ARRAY_LIST";
    public static final String COMMENT_OBJECT="COMMENT_OBJECT";
    public static final String COMMENT_ID = "COMMENT_ID";

    private long ItemId;
    private long ImageItemId;
    private long ReplyToId;
    private String Comment;
    private long CreateDate;
    private String CreateOS;
    private long LastModifyDate;
    private String LastModifyOS;
    private boolean Edited;
    private int LikeCount;
    private int ReplyCount;


    private UserShort User;
    private ImageItem ImageItem;
    private ImageComment ReplyTo;//comment cha
    private boolean IsLiked;

    //local
    private boolean isShowCommentChild=false;


    //bo sung khi load phan tu con
    private RealmList<ImageComment> listChild=new RealmList<>();
    public RealmList<ImageComment> getListChild() {
        return listChild;
    }

    public void setListChild(RealmList<ImageComment> listChild) {
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

    public UserShort getUser() {
        return User;
    }

    public void setUser(UserShort user) {
        User = user;
    }

    public com.topceo.objects.image.ImageItem getImageItem() {
        return ImageItem;
    }

    public void setImageItem(com.topceo.objects.image.ImageItem imageItem) {
        ImageItem = imageItem;
    }

    public ImageComment getReplyTo() {
        return ReplyTo;
    }

    public void setReplyTo(ImageComment replyTo) {
        ReplyTo = replyTo;
    }


    public ImageComment() {
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
        dest.writeParcelable(this.User, flags);
        dest.writeParcelable(this.ImageItem, flags);
        dest.writeParcelable(this.ReplyTo, flags);
        dest.writeByte(this.IsLiked ? (byte) 1 : (byte) 0);
        dest.writeList(this.listChild);
    }

    protected ImageComment(Parcel in) {
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
        this.User = in.readParcelable(UserShort.class.getClassLoader());
        this.ImageItem = in.readParcelable(com.topceo.objects.image.ImageItem.class.getClassLoader());
        this.ReplyTo = in.readParcelable(ImageComment.class.getClassLoader());
        this.IsLiked = in.readByte() != 0;


        this.listChild = new RealmList<>();
        in.readList(this.listChild, ImageComment.class.getClassLoader());
//        this.listChild = in.createTypedArrayList(ImageComment.CREATOR);
    }

    public static final Creator<ImageComment> CREATOR = new Creator<ImageComment>() {
        @Override
        public ImageComment createFromParcel(Parcel source) {
            return new ImageComment(source);
        }

        @Override
        public ImageComment[] newArray(int size) {
            return new ImageComment[size];
        }
    };

    public boolean isShowCommentChild() {
        return isShowCommentChild;
    }

    public void setShowCommentChild(boolean showCommentChild) {
        isShowCommentChild = showCommentChild;
    }
}
