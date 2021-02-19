package com.workchat.core.chathead.events;

public class UpdateNumberEvent {
    private String roomId;
    private int number;

    public UpdateNumberEvent(String roomId, int number) {
        this.roomId = roomId;
        this.number = number;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
