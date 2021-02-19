package com.workchat.core.utils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.workchat.core.models.realm.Room;

import java.util.ArrayList;

public class MyDiffUtilCallBack extends DiffUtil.Callback {
    ArrayList<Room> newList;
    ArrayList<Room> oldList;

    public MyDiffUtilCallBack(ArrayList<Room> newList, ArrayList<Room> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition).get_id().equalsIgnoreCase(oldList.get(oldItemPosition).get_id());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = newList.get(newItemPosition).compareTo(oldList.get(oldItemPosition));
        return result == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Room newModel = newList.get(newItemPosition);
        Room oldModel = oldList.get(oldItemPosition);

        Bundle diff = new Bundle();

        if (!newModel.getLastLog().getChatLogId().equals(oldModel.getLastLog().getChatLogId())) {
            diff.putParcelable(Room.ROOM, newModel);
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
//        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
