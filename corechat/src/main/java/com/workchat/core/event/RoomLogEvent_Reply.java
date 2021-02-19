package com.workchat.core.event;


import com.workchat.core.models.chat.RoomLog;

/**
 * Created by Igor Havrylyuk on 16.03.2017.
 */

public class RoomLogEvent_Reply {

    private RoomLog message;

    public RoomLogEvent_Reply(RoomLog message) {
        this.message = message;
    }

    public RoomLog getMessage() {
        return message;
    }
}
