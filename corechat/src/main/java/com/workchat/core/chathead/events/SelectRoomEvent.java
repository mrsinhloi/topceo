package com.workchat.core.chathead.events;

public class SelectRoomEvent {
    private String roomId;

    public SelectRoomEvent(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


}
