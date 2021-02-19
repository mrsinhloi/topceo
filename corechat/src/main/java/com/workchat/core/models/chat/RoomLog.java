package com.workchat.core.models.chat;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.utils.MyUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RoomLog implements Parcelable {

    public static final String ROOM_LOG_ID = "ROOM_LOG_ID";
    public static final java.lang.String IS_PIN = "IS_PIN";
    public static final String IS_FORWARD = "IS_FORWARD";
    public static final String ITEM_GUID = "ITEM_GUID";
    public static final String IS_SHARING = "IS_SHARING";

    private String _id;
    private String roomId;
    private String userIdAuthor;
    private String type;//private, item, page, custom
    private JsonObject content;//json
    private long createDate;
    private String itemGUID;

    private List<ChatView> views;
    private Room roomInfo;
    private UserChat authorInfo;

    //bo sung
    private boolean isDeleted;
    private boolean isUpdated;
    private boolean isGroupDate;
    private boolean isUploading;
    private boolean isPin;
    //server bo sung
    private long pinDate;
    private PinUserInfo pinUserInfo;

    //bo sung
    private String addressSave;
    private JsonElement originMessage;
    public static final String REPLY = "REPLY";
    public static final String FORWARD = "FORWARD";
    private String replyOrForward;//null or REPLY or FORWARD

    //bo sung local
    private String imgThumbnailLocal;
    private int thumbWidth;
    private int thumbHeight;

    //album local paths
    ArrayList<String> paths;


    public String get_id() {
        if (_id == null) _id = "";
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserIdAuthor() {
        return userIdAuthor;
    }

    public void setUserIdAuthor(String userIdAuthor) {
        this.userIdAuthor = userIdAuthor;
    }

    public String getType() {
        if (isGroupDate) type = ContentType.DATE;
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public List<ChatView> getViews() {
        return views;
    }

    public void setViews(List<ChatView> views) {
        this.views = views;
    }

    public static RoomLog parseRoomLog(Context context, Object... args) {
        RoomLog room = null;
        try {
            JSONObject json = (JSONObject) args[0];
            MyUtils.log(json.toString());
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        room = new Gson().fromJson(data, RoomLog.class);
                    }
                } else {
                    String errMessage = json.getString("error");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return room;
    }

    public static ArrayList<RoomLog> parseListRoomLog(Context context, Object... args) {
        ArrayList<RoomLog> list = new ArrayList<>();
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        java.lang.reflect.Type type = new TypeToken<ArrayList<RoomLog>>() {
                        }.getType();
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        list = gson.fromJson(data, type);
                    }
                } else {
                    String errMessage = json.getString("error");
                    MyUtils.log("parseListRoomLog" + json.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isSuccess(Context context, Object... args) {
        boolean isSuccess = false;
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    isSuccess = true;
                } else {
                    String errMessage = json.getString("error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }


    public JsonObject getContent() {
        return content;
    }

    public void setContent(JsonObject content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isGroupDate() {
        return isGroupDate;
    }

    public void setGroupDate(boolean groupDate) {
        isGroupDate = groupDate;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }

    public String getItemGUID() {
        return itemGUID;
    }

    public void setItemGUID(String itemGUID) {
        this.itemGUID = itemGUID;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }


    public Room getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(Room roomInfo) {
        this.roomInfo = roomInfo;
    }

    public boolean isPin() {
        return isPin;
    }

    public void setPin(boolean pin) {
        isPin = pin;
    }

    public String getAddressSave() {
        return addressSave;
    }

    public void setAddressSave(String addressSave) {
        this.addressSave = addressSave;
    }

    public UserChat getAuthorInfo() {
        return authorInfo;
    }

    public void setAuthorInfo(UserChat authorInfo) {
        this.authorInfo = authorInfo;
    }


    public String getReplyOrForward() {
        return replyOrForward;
    }

    public void setReplyOrForward(String replyOrForward) {
        this.replyOrForward = replyOrForward;
    }

    public JsonElement getOriginMessage() {
        return originMessage;
    }

    public void setOriginMessage(JsonElement originMessage) {
        this.originMessage = originMessage;
    }

    public RoomLog() {
    }

    public String getImgThumbnailLocal() {
        return imgThumbnailLocal;
    }

    public void setImgThumbnailLocal(String imgThumbnailLocal) {
        this.imgThumbnailLocal = imgThumbnailLocal;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public long getPinDate() {
        return pinDate;
    }

    public void setPinDate(long pinDate) {
        this.pinDate = pinDate;
    }

    public PinUserInfo getPinUserInfo() {
        return pinUserInfo;
    }

    public void setPinUserInfo(PinUserInfo pinUserInfo) {
        this.pinUserInfo = pinUserInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.roomId);
        dest.writeString(this.userIdAuthor);
        dest.writeString(this.type);
//        dest.writeParcelable(this.content, flags);
        dest.writeLong(this.createDate);
        dest.writeString(this.itemGUID);
        dest.writeTypedList(this.views);
        dest.writeParcelable(this.roomInfo, flags);
        dest.writeParcelable(this.authorInfo, flags);
        dest.writeByte(this.isDeleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUpdated ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGroupDate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUploading ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPin ? (byte) 1 : (byte) 0);
        dest.writeLong(this.pinDate);
        dest.writeParcelable(this.pinUserInfo, flags);
        dest.writeString(this.addressSave);
//        dest.writeParcelable(this.originMessage, flags);
        dest.writeString(this.replyOrForward);
        dest.writeString(this.imgThumbnailLocal);
        dest.writeInt(this.thumbWidth);
        dest.writeInt(this.thumbHeight);
    }

    protected RoomLog(Parcel in) {
        this._id = in.readString();
        this.roomId = in.readString();
        this.userIdAuthor = in.readString();
        this.type = in.readString();
//        this.content = in.readParcelable(JsonObject.class.getClassLoader());
        this.createDate = in.readLong();
        this.itemGUID = in.readString();
        this.views = in.createTypedArrayList(ChatView.CREATOR);
        this.roomInfo = in.readParcelable(Room.class.getClassLoader());
        this.authorInfo = in.readParcelable(UserChat.class.getClassLoader());
        this.isDeleted = in.readByte() != 0;
        this.isUpdated = in.readByte() != 0;
        this.isGroupDate = in.readByte() != 0;
        this.isUploading = in.readByte() != 0;
        this.isPin = in.readByte() != 0;
        this.pinDate = in.readLong();
        this.pinUserInfo = in.readParcelable(PinUserInfo.class.getClassLoader());
        this.addressSave = in.readString();
//        this.originMessage = in.readParcelable(JsonElement.class.getClassLoader());
        this.replyOrForward = in.readString();
        this.imgThumbnailLocal = in.readString();
        this.thumbWidth = in.readInt();
        this.thumbHeight = in.readInt();
    }

    public static final Creator<RoomLog> CREATOR = new Creator<RoomLog>() {
        @Override
        public RoomLog createFromParcel(Parcel source) {
            return new RoomLog(source);
        }

        @Override
        public RoomLog[] newArray(int size) {
            return new RoomLog[size];
        }
    };

    public ArrayList<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }
}
