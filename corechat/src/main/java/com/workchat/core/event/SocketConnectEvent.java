package com.workchat.core.event;

/**
 * Created by MrPhuong on 2017-08-30.
 */

public class SocketConnectEvent {
    private boolean isConnected;

    public SocketConnectEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
