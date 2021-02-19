package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class Meeting implements Parcelable {

    private String userId;// 2,
    private String uuid;// "T7FzKjWAR4yWfEi2wect1w==",
    private long id;// 183153272,
    private String host_id;// "Vgn3QarjRRK_jK8PQNAs2Q",
    private String topic;// "456",
    private int type;// 2,
    private String status;// "waiting",
    private String agenda;// "Meeting được tạo bởi Phương Phạm.",
    private String start_url;// "https://zoom.us/s/183153272?zak=eyJ6bV9za20iOiJ6bV9vMm0iLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJjbGllbnQiLCJ1aWQiOiJWZ24zUWFyalJSS19qSzhQUU5BczJRIiwiaXNzIjoid2ViIiwic3R5IjoxMDAsIndjZCI6ImF3MSIsImNsdCI6MCwic3RrIjoiZkg4YjVycUxlN2FzOGljbkJEaGJ4bDJWZXd3SEpqczNKenRDTUZvRUZyby5CZ1VZUkdjelpEUjZlVXRsV1V4VFUyOXViMk56VTFrclVUMDlRREl3TnpNM05XSmlPRFk0WlRKaU9URXpPREZsTmpVeU4yVmpOak0wTURoak56UTFZMlprTWpBNFpXTTRPRGRpWVRReU1EUTNZell3T0Rkall6SXlZMklBRERORFFrRjFiMmxaVXpOelBRQURZWGN4IiwiZXhwIjoxNTg2NDI1OTQxLCJpYXQiOjE1ODY0MTg3NDEsImFpZCI6IlJudGZvMTQ2VElDX1ZLWTFVVmJjOFEiLCJjaWQiOiIifQ.ia8uLhJrPGkZ8GnrEjAC0DfPprqK4XLk4ZXp-xitPcs",
    private String join_url;// "https://zoom.us/j/183153272?pwd=WmJwOG0zMTk2aSt4bnd1UFJTYnR6Zz09",
    private String password;// null,
    private String encrypted_password;// null

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getStart_url() {
        return start_url;
    }

    public void setStart_url(String start_url) {
        this.start_url = start_url;
    }

    public String getJoin_url() {
        return join_url;
    }

    public void setJoin_url(String join_url) {
        this.join_url = join_url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncrypted_password() {
        return encrypted_password;
    }

    public void setEncrypted_password(String encrypted_password) {
        this.encrypted_password = encrypted_password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.uuid);
        dest.writeLong(this.id);
        dest.writeString(this.host_id);
        dest.writeString(this.topic);
        dest.writeInt(this.type);
        dest.writeString(this.status);
        dest.writeString(this.agenda);
        dest.writeString(this.start_url);
        dest.writeString(this.join_url);
        dest.writeString(this.password);
        dest.writeString(this.encrypted_password);
    }

    public Meeting() {
    }

    protected Meeting(Parcel in) {
        this.userId = in.readString();
        this.uuid = in.readString();
        this.id = in.readLong();
        this.host_id = in.readString();
        this.topic = in.readString();
        this.type = in.readInt();
        this.status = in.readString();
        this.agenda = in.readString();
        this.start_url = in.readString();
        this.join_url = in.readString();
        this.password = in.readString();
        this.encrypted_password = in.readString();
    }

    public static final Parcelable.Creator<Meeting> CREATOR = new Parcelable.Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel source) {
            return new Meeting(source);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };
}
