package com.workchat.core.chathead.events;

public class StopService {
    private boolean isStop;
    public StopService(boolean isStop){
        this.isStop = isStop;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
