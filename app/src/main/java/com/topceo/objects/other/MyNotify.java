package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2016-09-19.
 */
public class MyNotify implements Parcelable {

    public static final String NOTIFY_ARRAY_LIST = "NOTIFY_ARRAY_LIST";
    public static final int IS_VIEWED_DA_XEM = 1;
    public static final int IS_VIEWED_CHUA_XEM = 0;
    public static final int IS_VIEWED_TAT_CA = -1;


    private long NotifyId;
    private long UserId;
    private int NotifyTypeId;
    private String NotifyType;
    private String Message;
    private long ActionUserId;
    private String ActionUserName;
    private String ActionUserAvatar;
    private long TargetUserId;
    private long ImageItemId;
    private long CommentId;
    private long ChatRoomId;
    private long ChatLogId;
    private long CreateDate;
    private String ViewDate;
    private String ImageUrl;
    private boolean IsViewed;

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {
        GroupId = groupId;
    }

    private long GroupId;


    public long getNotifyId() {
        return NotifyId;
    }

    public void setNotifyId(long notifyId) {
        NotifyId = notifyId;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public int getNotifyTypeId() {
        return NotifyTypeId;
    }

    public void setNotifyTypeId(int notifyTypeId) {
        NotifyTypeId = notifyTypeId;
    }

    public String getNotifyType() {
        return NotifyType;
    }

    public void setNotifyType(String notifyType) {
        NotifyType = notifyType;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public long getActionUserId() {
        return ActionUserId;
    }

    public void setActionUserId(long actionUserId) {
        ActionUserId = actionUserId;
    }

    public String getActionUserName() {
        return ActionUserName;
    }

    public void setActionUserName(String actionUserName) {
        ActionUserName = actionUserName;
    }

    public String getActionUserAvatar() {
        return ActionUserAvatar;
    }

    public void setActionUserAvatar(String actionUserAvatar) {
        ActionUserAvatar = actionUserAvatar;
    }

    public long getTargetUserId() {
        return TargetUserId;
    }

    public void setTargetUserId(long targetUserId) {
        TargetUserId = targetUserId;
    }

    public long getImageItemId() {
        return ImageItemId;
    }

    public void setImageItemId(long imageItemId) {
        ImageItemId = imageItemId;
    }

    public long getCommentId() {
        return CommentId;
    }

    public void setCommentId(long commentId) {
        CommentId = commentId;
    }

    public long getChatRoomId() {
        return ChatRoomId;
    }

    public void setChatRoomId(long chatRoomId) {
        ChatRoomId = chatRoomId;
    }

    public long getChatLogId() {
        return ChatLogId;
    }

    public void setChatLogId(long chatLogId) {
        ChatLogId = chatLogId;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public String getViewDate() {
        return ViewDate;
    }

    public void setViewDate(String viewDate) {
        ViewDate = viewDate;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    ////////////////////////
    public MyNotify() {
    }


    public boolean isViewed() {
        return IsViewed;
    }

    public void setViewed(boolean viewed) {
        IsViewed = viewed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.NotifyId);
        dest.writeLong(this.UserId);
        dest.writeInt(this.NotifyTypeId);
        dest.writeString(this.NotifyType);
        dest.writeString(this.Message);
        dest.writeLong(this.ActionUserId);
        dest.writeString(this.ActionUserName);
        dest.writeString(this.ActionUserAvatar);
        dest.writeLong(this.TargetUserId);
        dest.writeLong(this.ImageItemId);
        dest.writeLong(this.CommentId);
        dest.writeLong(this.ChatRoomId);
        dest.writeLong(this.ChatLogId);
        dest.writeLong(this.CreateDate);
        dest.writeString(this.ViewDate);
        dest.writeString(this.ImageUrl);
        dest.writeByte(this.IsViewed ? (byte) 1 : (byte) 0);
        dest.writeLong(this.GroupId);
    }

    protected MyNotify(Parcel in) {
        this.NotifyId = in.readLong();
        this.UserId = in.readLong();
        this.NotifyTypeId = in.readInt();
        this.NotifyType = in.readString();
        this.Message = in.readString();
        this.ActionUserId = in.readLong();
        this.ActionUserName = in.readString();
        this.ActionUserAvatar = in.readString();
        this.TargetUserId = in.readLong();
        this.ImageItemId = in.readLong();
        this.CommentId = in.readLong();
        this.ChatRoomId = in.readLong();
        this.ChatLogId = in.readLong();
        this.CreateDate = in.readLong();
        this.ViewDate = in.readString();
        this.ImageUrl = in.readString();
        this.IsViewed = in.readByte() != 0;
        this.GroupId = in.readLong();
    }

    public static final Creator<MyNotify> CREATOR = new Creator<MyNotify>() {
        @Override
        public MyNotify createFromParcel(Parcel source) {
            return new MyNotify(source);
        }

        @Override
        public MyNotify[] newArray(int size) {
            return new MyNotify[size];
        }
    };
}
