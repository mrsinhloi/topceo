package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MrPhuong on 3/7/2018.
 */

public class Permission implements Parcelable {
    private String editInfo;
    private String addUsers;
    private String addNewAdmins;
    private String postMessage;
    private String editMessageOfOthers;
//    private boolean deleteMessageOfOthers;

    public boolean isEditInfo() {
        boolean isTrue = false;
        if (editInfo != null)
            if (editInfo.equalsIgnoreCase("1") || editInfo.equalsIgnoreCase("true")) {
                isTrue = true;
            }
        return isTrue;
    }

    public void setEditInfo(String editInfo) {
        this.editInfo = editInfo;
    }

    public boolean isAddUsers() {
//        return addUsers;
        boolean isTrue = false;
        if (addUsers != null)
            if (addUsers.equalsIgnoreCase("1") || addUsers.equalsIgnoreCase("true")) {
                isTrue = true;
            }
        return isTrue;
    }

    public void setAddUsers(String addUsers) {
        this.addUsers = addUsers;
    }

    public boolean isAddNewAdmins() {
//        return addNewAdmins;
        boolean isTrue = false;
        if (addNewAdmins != null)
            if (addNewAdmins.equalsIgnoreCase("1") || addNewAdmins.equalsIgnoreCase("true")) {
                isTrue = true;
            }
        return isTrue;
    }

    public void setAddNewAdmins(String addNewAdmins) {
        this.addNewAdmins = addNewAdmins;
    }

    public boolean isPostMessage() {
//        return postMessage;
        boolean isTrue = false;
        if (postMessage != null)
            if (postMessage.equalsIgnoreCase("1") || postMessage.equalsIgnoreCase("true")) {
                isTrue = true;
            }
        return isTrue;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }

    public boolean isEditMessageOfOthers() {
//        return editMessageOfOthers;
        boolean isTrue = false;
        if (editMessageOfOthers != null)
            if (editMessageOfOthers.equalsIgnoreCase("1") || editMessageOfOthers.equalsIgnoreCase("true")) {
                isTrue = true;
            }
        return isTrue;
    }

    public void setEditMessageOfOthers(String editMessageOfOthers) {
        this.editMessageOfOthers = editMessageOfOthers;
    }

    public void resetPermission() {
        editInfo = "false";
        addUsers = "false";
        addNewAdmins = "false";
        postMessage = "false";
        editMessageOfOthers = "false";
//        deleteMessageOfOthers="false";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.editInfo);
        dest.writeString(this.addUsers);
        dest.writeString(this.addNewAdmins);
        dest.writeString(this.postMessage);
        dest.writeString(this.editMessageOfOthers);
    }

    public Permission() {
    }

    protected Permission(Parcel in) {
        this.editInfo = in.readString();
        this.addUsers = in.readString();
        this.addNewAdmins = in.readString();
        this.postMessage = in.readString();
        this.editMessageOfOthers = in.readString();
    }

    public static final Parcelable.Creator<Permission> CREATOR = new Parcelable.Creator<Permission>() {
        @Override
        public Permission createFromParcel(Parcel source) {
            return new Permission(source);
        }

        @Override
        public Permission[] newArray(int size) {
            return new Permission[size];
        }
    };
}
