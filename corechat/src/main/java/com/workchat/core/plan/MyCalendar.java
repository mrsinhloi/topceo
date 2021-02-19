package com.workchat.core.plan;

public class MyCalendar {
    private String calName;
    private int calID;
    public MyCalendar(String calName,
                      int calID){
        this.calID=calID;
        this.calName=calName;
    }

    public String getCalName() {
        return calName;
    }

    public void setCalName(String calName) {
        this.calName = calName;
    }

    public int getCalID() {
        return calID;
    }

    public void setCalID(int calID) {
        this.calID = calID;
    }
}
