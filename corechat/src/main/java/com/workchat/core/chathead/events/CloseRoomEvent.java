package com.workchat.core.chathead.events;

public class CloseRoomEvent {
    private String roomId;

    public CloseRoomEvent(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


}
