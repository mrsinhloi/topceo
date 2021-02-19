package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class ChatView implements Parcelable {
    private String userId;
    private boolean isViewed;
    private boolean showAvatar;
//    private boolean showAvatar;//web xài, mobile khi có trả về tức là cần hiện

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public boolean isShowAvatar() {
        return showAvatar;
    }

    public void setShowAvatar(boolean showAvatar) {
        this.showAvatar = showAvatar;
    }


    public ChatView() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeByte(this.isViewed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showAvatar ? (byte) 1 : (byte) 0);
    }

    protected ChatView(Parcel in) {
        this.userId = in.readString();
        this.isViewed = in.readByte() != 0;
        this.showAvatar = in.readByte() != 0;
    }

    public static final Creator<ChatView> CREATOR = new Creator<ChatView>() {
        @Override
        public ChatView createFromParcel(Parcel source) {
            return new ChatView(source);
        }

        @Override
        public ChatView[] newArray(int size) {
            return new ChatView[size];
        }
    };
}
