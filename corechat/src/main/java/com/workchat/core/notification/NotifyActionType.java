package com.workchat.core.notification;

import com.google.gson.JsonObject;

public class NotifyActionType {

    public static final String addToContact = "addToContact";
    public static final String newToChatNhanh = "newToChatNhanh";
    public static final String loginChatNhanh = "loginChatNhanh";
    public static final String addToRoom = "addToRoom";
    public static final String removeFromRoom = "removeFromRoom";
    public static final String planReminder = "planReminder";


    public static String getActionType(JsonObject data) {
        String actionType = "";
        if (data != null) {
            try {
                if (data.has("actionType")) {
                    actionType = data.get("actionType").getAsString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return actionType;
    }

    public static String getRoomId(JsonObject data) {
        String roomId = "";
        if (data != null) {
            try {
                JsonObject actionData = data.getAsJsonObject("actionData");
                if (actionData.has("roomId")) {
                    roomId = actionData.get("roomId").getAsString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return roomId;
    }
    public static String getChatLogId(JsonObject data) {
        String chatLogId = "";
        if (data != null) {
            try {
                JsonObject actionData = data.getAsJsonObject("actionData");
                if (actionData.has("chatLogId")) {
                    chatLogId = actionData.get("chatLogId").getAsString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return chatLogId;
    }

    /*public static void navigation(Context context, BaseNotification item) {
        if (context != null && item != null) {
            String actionType = getActionType(item.getData());
            switch (actionType){

                //chat voi 1 nguoi
                case addToContact:
                case newToChatNhanh:
                case loginChatNhanh:

                    //chat nhanh
                    UserInfo userGuest = new UserInfo();
                    userGuest.set_id(item.getCreateUserId());
                    MyUtils.chatWithUser(context, userGuest);

                    break;


                case addToRoom://chat voi room
                    String roomId = getRoomId(item.getData());
                    if(!TextUtils.isEmpty(roomId)){
                        MyUtils.openChatRoom(context, roomId);
                    }
                    break;
                case removeFromRoom://ko vao room

                    break;
                case planReminder:
                    roomId = getRoomId(item.getData());
                    if(!TextUtils.isEmpty(roomId)){
                        String chatLogId = getChatLogId(item.getData());
                        MyUtils.openChatRoom(context, roomId, chatLogId);
                    }
                    break;
            }
        }
    }*/

}