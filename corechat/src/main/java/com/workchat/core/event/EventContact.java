package com.workchat.core.event;

import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.search.Search_Fragment_All;

import java.util.ArrayList;

public class EventContact {
    private ArrayList<UserInfo> list = new ArrayList<>();
    private int number;

    public ArrayList<UserInfo> getList() {
        return list;
    }

    public void setList(ArrayList<UserInfo> list) {
        this.list = list;
    }

    public EventContact(ArrayList<UserInfo> list, int number){
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
