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

    protected ImageComment(Parcel in) {
        ItemId = in.readLong();
        ImageItemId = in.readLong();
        ReplyToId = in.readLong();
        Comment = in.readString();
        CreateDate = in.readLong();
        CreateOS = in.readString();
        LastModifyDate = in.readLong();
        LastModifyOS = in.readString();
        Edited = in.readByte() != 0;
        LikeCount = in.readInt();
        ReplyCount = in.readInt();
        User = in.readParcelable(UserShort.class.getClassLoader());
        ImageItem = in.readParcelable(com.topceo.objects.image.ImageItem.class.getClassLoader());
        ReplyTo = in.readParcelable(ImageComment.class.getClassLoader());
        IsLiked = in.readByte() != 0;
        isShowCommentChild = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ItemId);
        dest.writeLong(ImageItemId);
        dest.writeLong(ReplyToId);
        dest.writeString(Comment);
        dest.writeLong(CreateDate);
        dest.writeString(CreateOS);
        dest.writeLong(LastModifyDate);
        dest.writeString(LastModifyOS);
        dest.writeByte((byte) (Edited ? 1 : 0));
        dest.writeInt(LikeCount);
        dest.writeInt(ReplyCount);
        dest.writeParcelable(User, flags);
        dest.writeParcelable(ImageItem, flags);
        dest.writeParcelable(ReplyTo, flags);
        dest.writeByte((byte) (IsLiked ? 1 : 0));
        dest.writeByte((byte) (isShowCommentChild ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageComment> CREATOR = new Creator<ImageComment>() {
        @Override
        public ImageComment createFromParcel(Parcel in) {
            return new ImageComment(in);
        }

        @Override
        public ImageComment[] newArray(int size) {
            return new ImageComment[size];
        }
    };

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


    public boolean isShowCommentChild() {
        return isShowCommentChild;
    }

    public void setShowCommentChild(boolean showCommentChild) {
        isShowCommentChild = showCommentChild;
    }
}
