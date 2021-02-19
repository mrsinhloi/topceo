package com.workchat.core.chat.socketio;

import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Ack;

public abstract class AckWithTimeOut implements Ack {
    public static final int NO_ACK_ERROR_CODE = 1234;
    public static final String NO_ACK = "NO_ACK";
    public static final long TIME_OUT = 5000;
    private Timer timer;
    private long timeOut = 15000;//mac dinh 5s
    private boolean called = false;

    public AckWithTimeOut() {
    }

    public AckWithTimeOut(long timeout_after) {
        if (timeout_after <= 0)
            return;
        this.timeOut = timeout_after;
        startTimer();
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                callback(NO_ACK);
            }
        }, timeOut);
    }

    public void resetTimer() {
        if (timer != null) {
            timer.cancel();
            startTimer();
        }
    }

    public void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }

    void callback(Object... args) {
        if (called) return;
        called = true;
        cancelTimer();
        call(args);
    }

    @Override
    public void call(Object... args) {
        cancelTimer();
    }
}
