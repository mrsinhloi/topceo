package com.workchat.core.event;


import com.workchat.core.models.chat.RoomLog;

/**
 * Created by Igor Havrylyuk on 16.03.2017.
 */

public class RoomLogEvent_IsViewed {

    private RoomLog message;

    public RoomLogEvent_IsViewed(RoomLog message) {
        this.message = message;
    }

    public RoomLog getMessage() {
        return message;
    }
}
