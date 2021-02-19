package com.workchat.core.event;

import com.workchat.core.models.realm.Room;
import com.workchat.core.search.Search_Fragment_All;

import java.util.ArrayList;

public class EventChatRecent {
    private ArrayList<Room> list = new ArrayList<Room>();
    private int number;

    public ArrayList<Room> getList() {
        return list;
    }

    public void setList(ArrayList<Room> list) {
        this.list = list;
    }

    public EventChatRecent(ArrayList<Room> list, int number){
        //lay gioi han 5 phan tu
        int limit = Search_Fragment_All.numberMore;
        if(number<=limit) {
            this.list = list;
        }else{
            this.list.clear();
            this.list.addAll(list.subList(0, limit));
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
