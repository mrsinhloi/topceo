package com.workchat.core.event;


import com.workchat.core.models.chat.RoomLog;

/**
 * Created by Igor Havrylyuk on 16.03.2017.
 */

public class RoomLogEvent_Have_Message {

    private RoomLog message;

    public RoomLogEvent_Have_Message(RoomLog message) {
        this.message = message;
    }

    public RoomLog getMessage() {
        return message;
    }
}
