package com.topceo.onesignal;

import android.os.Parcel;
import android.os.Parcelable;

public class NotifyObject implements Parcelable {
    public static final String NOTIFY_OBJECT = "NOTIFY_OBJECT";

    private String Message;
    private long UserId;//UserId của người nhận notify (có thể mở rộng app đăng nhập đồng thời bằng nhiều tài khoản và có thể switch user như Instagram),
    private long NotifyId;//Id lưu trong db, dùng để update trạng thái đã xem,
    private int NotifyTypeId;//Id của loại notify (tham khảo bảng mô tả notify trên link Excel google),
    private String NotifyType;//từ khóa cụ thể của loại notify,
    private long ActionUserId;//UserId của User thực hiện hành động dẫn tới notfiy
    private String ActionUserName;//Username của User thực hiện hành động dẫn tới notfiy (dùng để show ra khi notify)
    private String ActionUserAvatar;//User avatar của User thực hiện hành động dẫn tới notify (dùng để show ra khi notify)
    private long TargetUserId;//tới màn hình User profile của com.ehubstar.objects.other.User,
    private long ImageItemId;//tới màn hình Image item,
    private long CommentId;//tới màn hình comment và dòng comment,
    private long ChatRoomId;//tới room chat,
    private long ChatLogId;//tới chat message tương ứng,
    private long Date;//ngày giờ notify để show, chứ hiện tại toàn show giờ notify tới như bên myxteam
    private String ImageUrl;//đường link hình liên quan (nếu app hiển thị đc ngay trong Notify thì dùng)
    private long GroupId;


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public long getNotifyId() {
        return NotifyId;
    }

    public void setNotifyId(long notifyId) {
        NotifyId = notifyId;
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

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }


    public NotifyObject() {
    }

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {
        GroupId = groupId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Message);
        dest.writeLong(this.UserId);
        dest.writeLong(this.NotifyId);
        dest.writeInt(this.NotifyTypeId);
        dest.writeString(this.NotifyType);
        dest.writeLong(this.ActionUserId);
        dest.writeString(this.ActionUserName);
        dest.writeString(this.ActionUserAvatar);
        dest.writeLong(this.TargetUserId);
        dest.writeLong(this.ImageItemId);
        dest.writeLong(this.CommentId);
        dest.writeLong(this.ChatRoomId);
        dest.writeLong(this.ChatLogId);
        dest.writeLong(this.Date);
        dest.writeString(this.ImageUrl);
        dest.writeLong(this.GroupId);
    }

    protected NotifyObject(Parcel in) {
        this.Message = in.readString();
        this.UserId = in.readLong();
        this.NotifyId = in.readLong();
        this.NotifyTypeId = in.readInt();
        this.NotifyType = in.readString();
        this.ActionUserId = in.readLong();
        this.ActionUserName = in.readString();
        this.ActionUserAvatar = in.readString();
        this.TargetUserId = in.readLong();
        this.ImageItemId = in.readLong();
        this.CommentId = in.readLong();
        this.ChatRoomId = in.readLong();
        this.ChatLogId = in.readLong();
        this.Date = in.readLong();
        this.ImageUrl = in.readString();
        this.GroupId = in.readLong();
    }

    public static final Creator<NotifyObject> CREATOR = new Creator<NotifyObject>() {
        @Override
        public NotifyObject createFromParcel(Parcel source) {
            return new NotifyObject(source);
        }

        @Override
        public NotifyObject[] newArray(int size) {
            return new NotifyObject[size];
        }
    };
}
