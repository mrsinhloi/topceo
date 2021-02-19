package com.workchat.core.models.realm;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.utils.MyUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by MrPhuong on 3/12/2018.
 */

public class UserInfo extends RealmObject implements Parcelable {

    public static final String USER_INFO = "USER_INFO";
    public static final String IS_SAVE_LIST_USER_INFO = "IS_SAVE_LIST_USER_INFO";
    public static final String KEY_SEARCH = "KEY_SEARCH";

    @PrimaryKey
    private String localId = UUID.randomUUID().toString();
    private String _id;
    private String name;
    private String nameMBN;
    private String phone;//server tra ve, hoac local
    private String phoneE164;//+84938xxxxxx  //bo sung de giam thoi gian parse
    private String phoneNational;//0938xxxxxx
    private boolean isPin;
    private boolean isSynContactSuccess;//bo sung de biet da syn chua


    private String email;
    private String avatar;
    private String url;
    private String token;
    public String lastUpdate;
    private String createDate;
    private String createOS;


    private boolean isLocalContact;//danh ba tren dt
    private boolean isHaveMBNAccount;//co tai khoan MBN
    private boolean isInChatContact;//co tai khoan MBN va da add vao danh ba


    //bo sung header local
    private boolean isHeader;
    private String headerString;
    private int headerIcon;


    public String getNameMBN() {
        if (nameMBN == null) nameMBN = "";
        return nameMBN;
    }

    public void setNameMBN(String nameMBN) {
        if (nameMBN == null) nameMBN = "";
        this.nameMBN = nameMBN;
    }


    public UserInfo() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getName() {
        return !TextUtils.isEmpty(name) ? name : getNameMBN();
    }

    public void setName(String name) {
        if (name == null) name = "#";
        this.name = name;
    }

    public String getPhone() {
        return (phone == null) ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return (email == null) ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static ArrayList<UserInfo> parse(Context context, Object... args) {
        ArrayList<UserInfo> list = new ArrayList<>();
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        Type type = new TypeToken<List<UserInfo>>() {
                        }.getType();
                        list = new Gson().fromJson(data, type);
                    }
                } else {
                    String errMessage = json.getString("error");
                    MyUtils.log("parseUserInfo" + json.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<UserInfo> parse(Context context, JSONObject json) {
        ArrayList<UserInfo> list = new ArrayList<>();
        try {
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        Type type = new TypeToken<List<UserInfo>>() {
                        }.getType();
                        list = new Gson().fromJson(data, type);
                    }
                } else {
                    String errMessage = json.getString("error");
                    MyUtils.log("parseUserInfo" + json.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isLocalContact() {
        return isLocalContact;
    }

    public void setLocalContact(boolean localContact) {
        isLocalContact = localContact;
    }

    public boolean isHaveMBNAccount() {
        return isHaveMBNAccount;
    }

    public void setHaveMBNAccount(boolean haveMBNAccount) {
        isHaveMBNAccount = haveMBNAccount;
    }

    public boolean isInChatContact() {
        return isInChatContact;
    }

    public void setInChatContact(boolean inChatContact) {
        isInChatContact = inChatContact;
    }

    public static UserInfo createUserInfo(UserChat userChat) {
        UserInfo user = null;
        if (userChat != null) {
            try {
                user = new UserInfo();
                user.set_id(userChat.getUserId());
                user.setName(userChat.getName());
                user.setPhone(userChat.getPhone());
                user.setAvatar(userChat.getAvatar());
                user.setEmail(userChat.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    public static UserInfo createUserInfo(UserChatCore userFrom) {
        UserInfo user = null;
        if (userFrom != null) {
            try {
                user = new UserInfo();
                user.set_id(userFrom.get_id());
                user.setName(userFrom.getName());
                user.setPhone(userFrom.getPhone());
                user.setAvatar(userFrom.getAvatar());
                user.setEmail(userFrom.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    public String getLocalId() {
        if (TextUtils.isEmpty(localId)) {
            localId = UUID.randomUUID().toString();
        }
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateOS() {
        return createOS;
    }

    public void setCreateOS(String createOS) {
        this.createOS = createOS;
    }

    public String getPhoneE164() {
        return phoneE164;
    }

    public void setPhoneE164(String phoneE164) {
        this.phoneE164 = phoneE164;
    }

    public boolean isPin() {
        return isPin;
    }

    public void setPin(boolean pin) {
        isPin = pin;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getHeaderString() {
        return headerString;
    }

    public void setHeaderString(String headerString) {
        this.headerString = headerString;
    }

    public int getHeaderIcon() {
        return headerIcon;
    }

    public void setHeaderIcon(int headerIcon) {
        this.headerIcon = headerIcon;
    }

    public String getPhoneNational() {
        return phoneNational;
    }

    public void setPhoneNational(String phoneNational) {
        this.phoneNational = phoneNational;
    }

    public boolean isSynContactSuccess() {
        return isSynContactSuccess;
    }

    public void setSynContactSuccess(boolean synContactSuccess) {
        isSynContactSuccess = synContactSuccess;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.localId);
        dest.writeString(this._id);
        dest.writeString(this.name);
        dest.writeString(this.nameMBN);
        dest.writeString(this.phone);
        dest.writeString(this.phoneE164);
        dest.writeString(this.phoneNational);
        dest.writeByte(this.isPin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSynContactSuccess ? (byte) 1 : (byte) 0);
        dest.writeString(this.email);
        dest.writeString(this.avatar);
        dest.writeString(this.url);
        dest.writeString(this.token);
        dest.writeString(this.lastUpdate);
        dest.writeString(this.createDate);
        dest.writeString(this.createOS);
        dest.writeByte(this.isLocalContact ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHaveMBNAccount ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isInChatContact ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isHeader ? (byte) 1 : (byte) 0);
        dest.writeString(this.headerString);
        dest.writeInt(this.headerIcon);
    }

    protected UserInfo(Parcel in) {
        this.localId = in.readString();
        this._id = in.readString();
        this.name = in.readString();
        this.nameMBN = in.readString();
        this.phone = in.readString();
        this.phoneE164 = in.readString();
        this.phoneNational = in.readString();
        this.isPin = in.readByte() != 0;
        this.isSynContactSuccess = in.readByte() != 0;
        this.email = in.readString();
        this.avatar = in.readString();
        this.url = in.readString();
        this.token = in.readString();
        this.lastUpdate = in.readString();
        this.createDate = in.readString();
        this.createOS = in.readString();
        this.isLocalContact = in.readByte() != 0;
        this.isHaveMBNAccount = in.readByte() != 0;
        this.isInChatContact = in.readByte() != 0;
        this.isHeader = in.readByte() != 0;
        this.headerString = in.readString();
        this.headerIcon = in.readInt();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
