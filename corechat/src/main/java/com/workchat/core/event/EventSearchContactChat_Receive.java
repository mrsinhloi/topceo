package com.workchat.core.event;

import com.workchat.core.mbn.models.UserChatCore;

import java.util.ArrayList;

public class EventSearchContactChat_Receive {
    private ArrayList<UserChatCore> list = new ArrayList<UserChatCore>();
    private int number;

    public EventSearchContactChat_Receive(ArrayList<UserChatCore> list) {
        this.list = list;
    }

    public ArrayList<UserChatCore> getList() {
        return list;
    }

    public void setList(ArrayList<UserChatCore> list) {
        this.list = list;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
