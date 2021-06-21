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
    private long ReplyToId;
    private String ExternalLink;

    private long ChatRoomId;
    private long ChatLogId;
    private long CreateDate;
    private String ViewDate;
    private String ImageUrl;
    private boolean IsViewed;

    protected MyNotify(Parcel in) {
        NotifyId = in.readLong();
        ReplyToId = in.readLong();
        ExternalLink = in.readString();
        UserId = in.readLong();
        NotifyTypeId = in.readInt();
        NotifyType = in.readString();
        Message = in.readString();
        ActionUserId = in.readLong();
        ActionUserName = in.readString();
        ActionUserAvatar = in.readString();
        TargetUserId = in.readLong();
        ImageItemId = in.readLong();
        CommentId = in.readLong();
        ChatRoomId = in.readLong();
        ChatLogId = in.readLong();
        CreateDate = in.readLong();
        ViewDate = in.readString();
        ImageUrl = in.readString();
        IsViewed = in.readByte() != 0;
        GroupId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(NotifyId);
        dest.writeLong(ReplyToId);
        dest.writeString(ExternalLink);
        dest.writeLong(UserId);
        dest.writeInt(NotifyTypeId);
        dest.writeString(NotifyType);
        dest.writeString(Message);
        dest.writeLong(ActionUserId);
        dest.writeString(ActionUserName);
        dest.writeString(ActionUserAvatar);
        dest.writeLong(TargetUserId);
        dest.writeLong(ImageItemId);
        dest.writeLong(CommentId);
        dest.writeLong(ChatRoomId);
        dest.writeLong(ChatLogId);
        dest.writeLong(CreateDate);
        dest.writeString(ViewDate);
        dest.writeString(ImageUrl);
        dest.writeByte((byte) (IsViewed ? 1 : 0));
        dest.writeLong(GroupId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyNotify> CREATOR = new Creator<MyNotify>() {
        @Override
        public MyNotify createFromParcel(Parcel in) {
            return new MyNotify(in);
        }

        @Override
        public MyNotify[] newArray(int size) {
            return new MyNotify[size];
        }
    };

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {
        GroupId = groupId;
    }

    private long GroupId;

    public long getReplyToId() {
        return ReplyToId;
    }

    public void setReplyToId(long replyToId) {
        ReplyToId = replyToId;
    }

    public String getExternalLink() {
        return ExternalLink;
    }

    public void setExternalLink(String externalLink) {
        ExternalLink = externalLink;
    }

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


}
