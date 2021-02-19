package com.workchat.core.channel;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.MyUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrPhuong on 3/12/2018.
 */

public class ChannelUser implements Parcelable {
    private String userId;
    private UserInfo userInfo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static ArrayList<ChannelUser> parse(Context context, Object... args) {
        ArrayList<ChannelUser> list = new ArrayList<>();
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data") && !json.isNull("data")) {
                        String data = json.getString("data");
                        Type type = new TypeToken<List<ChannelUser>>() {
                        }.getType();
                        list = new Gson().fromJson(data, type);
                    }
                } else {
                    String errMessage = json.getString("error");
                    MyUtils.log("parseRoom" + json.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeParcelable(this.userInfo, flags);
    }

    public ChannelUser() {
    }

    protected ChannelUser(Parcel in) {
        this.userId = in.readString();
        this.userInfo = in.readParcelable(UserInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<ChannelUser> CREATOR = new Parcelable.Creator<ChannelUser>() {
        @Override
        public ChannelUser createFromParcel(Parcel source) {
            return new ChannelUser(source);
        }

        @Override
        public ChannelUser[] newArray(int size) {
            return new ChannelUser[size];
        }
    };
}
