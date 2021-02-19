package com.workchat.core.chathead.events;

public class MenuEvent {
    private boolean isCollapse;
    public MenuEvent(boolean isCollapse){
        this.isCollapse = isCollapse;
    }

    public boolean isCollapse() {
        return isCollapse;
    }

    public void setCollapse(boolean collapse) {
        isCollapse = collapse;
    }
}
