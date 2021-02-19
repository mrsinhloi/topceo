package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.workchat.core.models.realm.UserChat;

public class OriginMessage implements Parcelable {


    @Expose
    @SerializedName("authorInfo")
    private UserChat authorinfo;
    @Expose
    @SerializedName("content")
    private JsonObject content;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("roomId")
    private String roomid;
    @Expose
    @SerializedName("_id")
    private String Id;

    public UserChat getAuthorinfo() {
        return authorinfo;
    }

    public void setAuthorinfo(UserChat authorinfo) {
        this.authorinfo = authorinfo;
    }

    public JsonObject getContent() {
        return content;
    }

    public void setContent(JsonObject content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.authorinfo, flags);
        dest.writeValue(this.content);
        dest.writeString(this.type);
        dest.writeString(this.roomid);
        dest.writeString(this.Id);
    }

    public OriginMessage() {
    }

    protected OriginMessage(Parcel in) {
        this.authorinfo = in.readParcelable(UserChat.class.getClassLoader());
        this.content = (JsonObject) in.readValue(JsonObject.class.getClassLoader());
        this.type = in.readString();
        this.roomid = in.readString();
        this.Id = in.readString();
    }

    public static final Parcelable.Creator<OriginMessage> CREATOR = new Parcelable.Creator<OriginMessage>() {
        @Override
        public OriginMessage createFromParcel(Parcel source) {
            return new OriginMessage(source);
        }

        @Override
        public OriginMessage[] newArray(int size) {
            return new OriginMessage[size];
        }
    };
}
