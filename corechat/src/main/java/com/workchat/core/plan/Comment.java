package com.workchat.core.plan;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    public static final String COMMENT = "COMMENT";
    public static final String NUMBER_COMMENT = "NUMBER_COMMENT";
    private String userId;
    private String message;
    private String createDate;

    //local: sau khi them comment thi goi thong tin nay de refresh giao dien man hinh chat
    private String roomId;
    private String chatLogId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        if (message == null) message = "";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getChatLogId() {
        return chatLogId;
    }

    public void setChatLogId(String chatLogId) {
        this.chatLogId = chatLogId;
    }

    public Comment() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.message);
        dest.writeString(this.createDate);
        dest.writeString(this.roomId);
        dest.writeString(this.chatLogId);
    }

    protected Comment(Parcel in) {
        this.userId = in.readString();
        this.message = in.readString();
        this.createDate = in.readString();
        this.roomId = in.readString();
        this.chatLogId = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
