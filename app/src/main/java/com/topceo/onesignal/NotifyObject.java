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
    private long ReplyToId;
    private long ChatRoomId;//tới room chat,
    private long ChatLogId;//tới chat message tương ứng,
    private long Date;//ngày giờ notify để show, chứ hiện tại toàn show giờ notify tới như bên myxteam
    private String ImageUrl;//đường link hình liên quan (nếu app hiển thị đc ngay trong Notify thì dùng)
    private long GroupId;

    //mo link tu notify
    private String ExternalLink;


    protected NotifyObject(Parcel in) {
        Message = in.readString();
        UserId = in.readLong();
        NotifyId = in.readLong();
        NotifyTypeId = in.readInt();
        NotifyType = in.readString();
        ActionUserId = in.readLong();
        ActionUserName = in.readString();
        ActionUserAvatar = in.readString();
        TargetUserId = in.readLong();
        ImageItemId = in.readLong();
        CommentId = in.readLong();
        ReplyToId = in.readLong();
        ChatRoomId = in.readLong();
        ChatLogId = in.readLong();
        Date = in.readLong();
        ImageUrl = in.readString();
        GroupId = in.readLong();
        ExternalLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Message);
        dest.writeLong(UserId);
        dest.writeLong(NotifyId);
        dest.writeInt(NotifyTypeId);
        dest.writeString(NotifyType);
        dest.writeLong(ActionUserId);
        dest.writeString(ActionUserName);
        dest.writeString(ActionUserAvatar);
        dest.writeLong(TargetUserId);
        dest.writeLong(ImageItemId);
        dest.writeLong(CommentId);
        dest.writeLong(ReplyToId);
        dest.writeLong(ChatRoomId);
        dest.writeLong(ChatLogId);
        dest.writeLong(Date);
        dest.writeString(ImageUrl);
        dest.writeLong(GroupId);
        dest.writeString(ExternalLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotifyObject> CREATOR = new Creator<NotifyObject>() {
        @Override
        public NotifyObject createFromParcel(Parcel in) {
            return new NotifyObject(in);
        }

        @Override
        public NotifyObject[] newArray(int size) {
            return new NotifyObject[size];
        }
    };

    public String getExternalLink() {
        return ExternalLink;
    }

    public void setExternalLink(String externalLink) {
        ExternalLink = externalLink;
    }


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

    public long getReplyToId() {
        return ReplyToId;
    }

    public void setReplyToId(long replyToId) {
        ReplyToId = replyToId;
    }


}
