package com.workchat.core.models.realm;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * transient de ko gay loi Serializable
 * Created by MrPhuong on 2017-08-29.
 */

public class LastLog extends RealmObject implements Parcelable {

    private String type;//text, image, file, link, action
    @Ignore
    private JsonObject content;//json se bi lỗi khi truyền giữa các activity, set null trươc khi truyen
    private String contentString;
    private String userIdAuthor;
    @PrimaryKey
    private String chatLogId;

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    private long createDate;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserIdAuthor() {
        return userIdAuthor;
    }

    public void setUserIdAuthor(String userIdAuthor) {
        this.userIdAuthor = userIdAuthor;
    }

    public String getChatLogId() {
        return chatLogId;
    }

    public void setChatLogId(String chatLogId) {
        this.chatLogId = chatLogId;
    }


    public JsonObject getContent() {
        if(content==null){
            content = getContentString();
        }
        return content;
    }

    public void setContent(JsonObject content) {
        this.content = content;
        //set string
        setContentString(content);
    }

    public JsonObject getContentString() {
        JsonObject json = null;
        if(!TextUtils.isEmpty(contentString)){
            try {
                json = new Gson().fromJson(contentString, JsonObject.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public void setContentString(JsonObject content) {
        if(content!=null){
            this.contentString = content.toString();
        }
    }


    public LastLog() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
//        dest.writeValue(this.content);
        dest.writeString(this.contentString);
        dest.writeString(this.chatLogId);
        dest.writeString(this.userIdAuthor);
        dest.writeLong(this.createDate);

    }

    protected LastLog(Parcel in) {
        this.type = in.readString();
//        this.content = (JsonObject) in.readValue(JsonObject.class.getClassLoader());
        this.contentString = in.readString();
        this.userIdAuthor = in.readString();
        this.createDate = in.readLong();
        this.chatLogId = in.readString();
    }

    public static final Creator<LastLog> CREATOR = new Creator<LastLog>() {
        @Override
        public LastLog createFromParcel(Parcel source) {
            return new LastLog(source);
        }

        @Override
        public LastLog[] newArray(int size) {
            return new LastLog[size];
        }
    };
}


