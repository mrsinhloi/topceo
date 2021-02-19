package com.topceo.amazon;

import android.os.Parcel;
import android.os.Parcelable;

@Deprecated
public class NotifyClass implements Parcelable {

    private long chatRoomId;
    private long commentId;
    private String notifyType;
    private String imageUrl;
    private long userId;
    private long targetUserId;
    private String actionUserName;
    private String date;//2016-09-21 06:26:06
    private String message;
    private int notifyTypeId;
    private long actionUserId;
    private long notifyId;
    private long chatLogId;
    private String actionUserAvatar;
    private long imageItemId;


    public NotifyClass() {
    }

    public NotifyClass(long chatRoomId,
                       long commentId,
                       String notifyType,
                       String imageUrl,
                       long userId,
                       long targetUserId,
                       String actionUserName,
                       String date,//2016-09-21 06:26:06
                       String message,
                       int notifyTypeId,
                       long actionUserId,
                       long notifyId,
                       long chatLogId,
                       String actionUserAvatar,
                       long imageItemId) {
        this.chatRoomId = chatRoomId;
        this.commentId = commentId;
        this.notifyType = notifyType;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.actionUserName = actionUserName;
        this.date = date;//2016-09-21 06:26:06
        this.message = message;
        this.notifyTypeId = notifyTypeId;
        this.actionUserId = actionUserId;
        this.notifyId = notifyId;
        this.chatLogId = chatLogId;
        this.actionUserAvatar = actionUserAvatar;
        this.imageItemId = imageItemId;

    }

    ///////////////////////

    public long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getActionUserName() {
        return actionUserName;
    }

    public void setActionUserName(String actionUserName) {
        this.actionUserName = actionUserName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNotifyTypeId() {
        return notifyTypeId;
    }

    public void setNotifyTypeId(int notifyTypeId) {
        this.notifyTypeId = notifyTypeId;
    }

    public long getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(long actionUserId) {
        this.actionUserId = actionUserId;
    }

    public long getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(long notifyId) {
        this.notifyId = notifyId;
    }

    public long getChatLogId() {
        return chatLogId;
    }

    public void setChatLogId(long chatLogId) {
        this.chatLogId = chatLogId;
    }

    public String getActionUserAvatar() {
        return actionUserAvatar;
    }

    public void setActionUserAvatar(String actionUserAvatar) {
        this.actionUserAvatar = actionUserAvatar;
    }

    public long getImageItemId() {
        return imageItemId;
    }

    public void setImageItemId(long imageItemId) {
        this.imageItemId = imageItemId;
    }

    //////////////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.chatRoomId);
        dest.writeLong(this.commentId);
        dest.writeString(this.notifyType);
        dest.writeString(this.imageUrl);
        dest.writeLong(this.userId);
        dest.writeLong(this.targetUserId);
        dest.writeString(this.actionUserName);
        dest.writeString(this.date);
        dest.writeString(this.message);
        dest.writeInt(this.notifyTypeId);
        dest.writeLong(this.actionUserId);
        dest.writeLong(this.notifyId);
        dest.writeLong(this.chatLogId);
        dest.writeString(this.actionUserAvatar);
        dest.writeLong(this.imageItemId);
    }

    protected NotifyClass(Parcel in) {
        this.chatRoomId = in.readLong();
        this.commentId = in.readLong();
        this.notifyType = in.readString();
        this.imageUrl = in.readString();
        this.userId = in.readLong();
        this.targetUserId = in.readLong();
        this.actionUserName = in.readString();
        this.date = in.readString();
        this.message = in.readString();
        this.notifyTypeId = in.readInt();
        this.actionUserId = in.readLong();
        this.notifyId = in.readLong();
        this.chatLogId = in.readLong();
        this.actionUserAvatar = in.readString();
        this.imageItemId = in.readLong();
    }

    public static final Parcelable.Creator<NotifyClass> CREATOR = new Parcelable.Creator<NotifyClass>() {
        @Override
        public NotifyClass createFromParcel(Parcel source) {
            return new NotifyClass(source);
        }

        @Override
        public NotifyClass[] newArray(int size) {
            return new NotifyClass[size];
        }
    };

    //////////////////////////////////////////////////////////////////////////////////
    /*1	Following	Có người theo dõi	Tới Profile User theo dõi (dựa vào TargeUserId)
	2	AcceptedFollowing	Đc phép theo dõi	Tới Profile User đồng ý cho phép theo dõi (dựa vào TargetUserId)
	3	UploadNewPhoto	Có photo mới	Tới màn hình chi tiết Image (dựa vào ImageItemId)
	4	NewLike	Hình có Like mới 	Tới màn hình chi tiết Image (dựa vào ImageItemId)
	5	NewComment	Hình có comment	Tới màn hình comment của 1 Image (dựa vào ImageItemId và CommentId)
	6	NewChatMessage	Có tin chat mới	Tới màn hình chat room (dựa vào ChatRoomId và ChatLogId)
	7	NewChatFile	Có file gửi qua chat	Tới màn hình chat room (dựa vào ChatRoomId và ChatLogId)
	8	MentionInComment	Có ngườ nhắc tới	Tới màn hình comment của 1 Image (dựa vào ImageItemId và CommentId)
	9	FollowingComment	User đang follow thực hiện comment	Tới màn hình comment của 1 Image (dựa vào ImageItemId và CommentId)
	10	FollowingLike	User đang follow thực hiện like	Tới màn hình chi tiết Image (dựa vào ImageItemId)
	11	FollowingShare	"User đang follow thực hiện share
	lên tường nhà"	Tới Profile User theo dõi (dựa vào TargeUserId)			*/
    public static class NotifyTypeId {
        public static final int Following=1;
        public static final int AcceptedFollowing=2;
        public static final int UploadNewPhoto=3;
        public static final int NewLike=4;
        public static final int NewComment=5;
        public static final int NewChatMessage=6;
        public static final int NewChatFile=7;
        public static final int MentionInComment=8;
        public static final int FollowingComment=9;
        public static final int FollowingLike=10;
        public static final int FollowingShare=11;
    }

}
