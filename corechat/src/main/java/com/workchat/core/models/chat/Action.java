package com.workchat.core.models.chat;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.plan.PlanModel;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.Utils;
import com.workchat.corechat.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MrPhuong on 2017-09-14.
 * createRoom: 'createRoom',
 * addMember: 'addMember',
 * removeMember: 'removeMember',
 * leaveRoom: 'leaveRoom',
 * deleteMessage: 'deleteMessage'
 * <p>
 * renameRoom
 * changeAvatarRoom
 */

public class Action implements Parcelable {

    public static final String CREATE_ROOM = "createRoom";
    public static final String RENAME_ROOM = "renameRoom";
    public static final String CHANGE_AVATAR_ROOM = "changeAvatarRoom";

    public static final String ADD_MEMBER = "addMember";
    public static final String REMOVE_MEMBER = "removeMember";
    public static final String LEAVE_ROOM = "leaveRoom";//=> xai chung REMOVE_MEMBER
    public static final String DELETE_MESSAGE = "deleteMessage";


    public static final String CREATE_CHANNEL = "createChannel";
    public static final String RENAME_CHANNEL = "renameChannel";
    public static final String CHANGE_CHANNEL_AVATAR = "changeChannelAvatar";
    public static final String PLAN_REMINDER = "planReminder";

    //bo sung 25-07-2019
    public static final String PIN_MESSAGE = "pinMessage";
    public static final String UNPIN_MESSAGE = "unpinMessage";
    public static final String SAVE_MESSAGE_ROOM = "savedMessageRoom";

    //bo sung 10/04/2020
    public static final String MEETING_END = "meetingEnd";



    private String actionType;
    private String data;

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private Context context;
    /**
     * createRoom	{ userId, userName }
     * renameRoom	{ userId, userName, roomName }
     * changeAvatarRoom	{ userId, userName, roomAvatar }
     * addMember	{ userId, userName, memberInfo { userId, name, phone, email, avatar, url }}
     * removeMember	{ userId, userName, memberInfo { userId, name, phone, email, avatar, url }}
     * deleteMessage	{ roomLog đã xoá, tuỳ vào MessageType }
     *
     * @return
     */
    public String getActionMessage(Context context) {
        this.context = context;
        String message = "";
        try {
            if (context != null && !TextUtils.isEmpty(actionType) && !TextUtils.isEmpty(data)) {
                String name = "";
                String room = "";
                JSONObject json = new JSONObject(data);
                switch (actionType) {
                    case CREATE_ROOM:
                        name = json.getString("userName");
                        message = String.format(context.getString(R.string.created_room), name);
                        break;
                    case ADD_MEMBER:
                        name = json.getString("userName");
                        JSONObject member = json.getJSONObject("memberInfo");
                        room = member.getString("name");
                        message = String.format(context.getString(R.string.added_member), name, room);
                        break;
                    case REMOVE_MEMBER:
                        name = json.getString("userName");
                        member = json.getJSONObject("memberInfo");
                        room = member.getString("name");
                        message = String.format(context.getString(R.string.removed_member), name, room);
                        break;
                    case DELETE_MESSAGE:
                        message = context.getString(R.string.message_deleted);
                        break;
                    case RENAME_ROOM:
                        name = json.getString("userName");
                        room = json.getString("roomName");
                        message = String.format(context.getString(R.string.renamed_room_to), name, room);
                        break;
                    case CHANGE_AVATAR_ROOM:
                        name = json.getString("userName");
                        message = String.format(context.getString(R.string.changed_room_avatar), name);
                        break;


                    case CREATE_CHANNEL:
                        name = json.getString("userName");
                        message = String.format(context.getString(R.string.created_channel), name);
                        break;
                    case RENAME_CHANNEL:
                        name = json.getString("userName");
                        room = json.getString("roomName");
                        message = String.format(context.getString(R.string.renamed_channel_to), name, room);
                        break;
                    case CHANGE_CHANNEL_AVATAR:
                        name = json.getString("userName");
                        message = String.format(context.getString(R.string.changed_room_avatar), name);
                        break;

                    case LEAVE_ROOM:
                        name = json.getString("userName");
                        message = String.format(context.getString(R.string.leave_room), name);
                        break;
                    case PLAN_REMINDER:
                        String title = json.getString("title");
                        long timeStamp = json.getLong("timeStamp");
                        String time = MyUtils.getTimePlan(timeStamp, ChatApplication.Companion.getLanguageTag());
                        message = context.getString(R.string.plan_x_due_y, title,time);
                        break;
                    case PIN_MESSAGE:
                        message = getContentMessage(json, true);
                        break;
                    case UNPIN_MESSAGE:
                        message = getContentMessage(json, false);
                        break;
                    case SAVE_MESSAGE_ROOM:
                        message = context.getString(R.string.saved_messages);
                        break;
                    case MEETING_END:
                        String meetingTopic = "";
                        if(json.has("topic")){
                             meetingTopic = json.getString("topic");
                        }
                        message = context.getString(R.string.meeting_x_ended, meetingTopic);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    private String getContentMessage(JSONObject data, boolean isPin){
        String message = "";
        if(data!=null){
            JSONObject messageObj = null;
            try {
                String name = data.getString("userName");
                messageObj = data.getJSONObject("message");
                JSONObject contentObj = messageObj.getJSONObject("content");
                String type = messageObj.getString("type");
                if(contentObj!=null){
                    switch (type){
                        case ContentType.TEXT:
                            Text t = new Gson().fromJson(contentObj.toString(), Text.class);
                                String content = Utils.getMentionUserName(t.getContent());
                                if(content.length()>50){
                                    content = content.substring(0,20) + "...";
                                }

                                if(isPin){
                                    message = context.getString(R.string.action_pin_message, name, content);
                                }else{
                                    message = context.getString(R.string.action_unpin_message, name, content);
                                }

                            break;
                        case ContentType.IMAGE:
                            Image i = new Gson().fromJson(contentObj.toString(), Image.class);
                            if(isPin){
                                message = context.getString(R.string.action_pin_image, name, i.getName());
                            }else{
                                message = context.getString(R.string.action_unpin_image, name, i.getName());
                            }

                            break;
                        case ContentType.VIDEO:
                            Video v = new Gson().fromJson(contentObj.toString(), Video.class);
                            if(isPin){
                                message = context.getString(R.string.action_pin_video, name, v.getName());
                            }else{
                                message = context.getString(R.string.action_unpin_video, name, v.getName());
                            }

                            break;
                        case ContentType.ALBUM:
                            if(isPin){
                                message = context.getString(R.string.action_pin_album, name);
                            }else{
                                message = context.getString(R.string.action_unpin_album, name);
                            }


                            break;
                        case ContentType.FILE:
                            File f = new Gson().fromJson(contentObj.toString(), File.class);
                            if(isPin){
                                message = context.getString(R.string.action_pin_file, name, f.getName());
                            }else{
                                message = context.getString(R.string.action_unpin_file, name, f.getName());
                            }

                            break;
                        case ContentType.VOICE:
                            if(isPin){
                                message = context.getString(R.string.action_pin_voice, name);
                            }else{
                                message = context.getString(R.string.action_unpin_voice, name);
                            }

                            break;
                        case ContentType.CONTACT:
                            Contact c = new Gson().fromJson(contentObj.toString(), Contact.class);
                            if(isPin){
                                message = context.getString(R.string.action_pin_contact, name, c.getName());
                            }else{
                                message = context.getString(R.string.action_unpin_contact, name, c.getName());
                            }
                            break;
                        case ContentType.LINK:
                            Link link = new Gson().fromJson(contentObj.toString(), Link.class);
                            if(isPin){
                                message = context.getString(R.string.action_pin_link, name, link.getText());
                            }else{
                                message = context.getString(R.string.action_unpin_link, name, link.getText());
                            }

                            break;
                        case ContentType.LOCATION:
                            if(isPin){
                                message = context.getString(R.string.action_pin_location, name);
                            }else{
                                message = context.getString(R.string.action_unpin_location, name);
                            }

                            break;
                        case ContentType.ITEM:
                            Item item = new Gson().fromJson(contentObj.toString(), Item.class);
                            if(isPin){
                                message = context.getString(R.string.action_pin_item, name, item.getItemName());
                            }else{
                                message = context.getString(R.string.action_unpin_item, name, item.getItemName());
                            }
                            break;
                        case ContentType.PLAN:
                            PlanModel p = new Gson().fromJson(contentObj.toString(), PlanModel.class);
                            if(isPin){
                                message = context.getString(R.string.action_pin_plan, name, p.getTitle());
                            }else{
                                message = context.getString(R.string.action_unpin_plan, name, p.getTitle());
                            }
                            break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.actionType);
        dest.writeString(this.data);
    }

    public Action() {
    }

    protected Action(Parcel in) {
        this.actionType = in.readString();
        this.data = in.readString();
    }

    public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel source) {
            return new Action(source);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    public static String getActionType(JsonObject json){
        if (json != null) {
            Action action = new Action();

            JsonElement item = json.get("actionType");
            if (!item.isJsonNull()) {
                return json.get("actionType").getAsString();
            }
        }
        return "";
    }
}
