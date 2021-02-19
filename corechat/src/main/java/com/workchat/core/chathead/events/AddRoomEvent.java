package com.workchat.core.chathead.events;

import android.os.Parcel;
import android.os.Parcelable;

public class AddRoomEvent implements Parcelable {
    public static final String ADD_ROOM_EVENT = "ADD_ROOM_EVENT";
    private String roomId;
    private String roomAvatar;
    private boolean isFromRecent;//tu man hinh recent chat
    public AddRoomEvent(String roomId, String roomAvatar, boolean isFromRecent){
        this.roomId = roomId;
        this.roomAvatar = roomAvatar;
        this.isFromRecent = isFromRecent;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomAvatar() {
        return roomAvatar;
    }

    public void setRoomAvatar(String roomAvatar) {
        this.roomAvatar = roomAvatar;
    }

    public boolean isFromRecent() {
        return isFromRecent;
    }

    public void setFromRecent(boolean fromRecent) {
        isFromRecent = fromRecent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomId);
        dest.writeString(this.roomAvatar);
        dest.writeByte(this.isFromRecent ? (byte) 1 : (byte) 0);
    }

    protected AddRoomEvent(Parcel in) {
        this.roomId = in.readString();
        this.roomAvatar = in.readString();
        this.isFromRecent = in.readByte() != 0;
    }

    public static final Creator<AddRoomEvent> CREATOR = new Creator<AddRoomEvent>() {
        @Override
        public AddRoomEvent createFromParcel(Parcel source) {
            return new AddRoomEvent(source);
        }

        @Override
        public AddRoomEvent[] newArray(int size) {
            return new AddRoomEvent[size];
        }
    };
}
