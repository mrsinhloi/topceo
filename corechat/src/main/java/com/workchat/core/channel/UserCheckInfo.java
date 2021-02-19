package com.workchat.core.channel;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserCheckInfo implements Parcelable {

    private String phone;
    private String userId;
    private String name;
    private String avatar;
    private String email;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param context
     * @param args
     * @return danh sach tra ve la da co tai khoan MBN
     */
    public static ArrayList<UserCheckInfo> parse(Context context, Object... args) {
        ArrayList<UserCheckInfo> list = new ArrayList<>();
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        Type type = new TypeToken<List<UserCheckInfo>>() {
                        }.getType();
                        list = new Gson().fromJson(data, type);

                        /*JSONObject data = json.getJSONObject("data");
                        Iterator<String> keys = data.keys();
                        while (keys.hasNext()){
                            String key = keys.next();
                            //parse item
                            try {
                                JSONObject child = data.getJSONObject(key);
                                if(child.has("id") && !child.isNull("id")){//ko co id la ko co tai khoan mbn

                                    long id = child.getLong("id");
                                    String name = child.getString("name");
                                    String avatar = child.getString("avatar_url");
                                    String phone = key;

                                    UserCheckInfo info = new UserCheckInfo();
                                    info.setUserId(id);
                                    info.setName(name);
                                    info.setAvatar(avatar);
                                    info.setPhone(phone);

                                    //luu vao list
                                    list.add(info);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/

                    }
                } else {
                    String errMessage = json.getString("error");
//                    MyUtils.log("parseRoom"+json.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public UserCheckInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeString(this.email);
    }

    protected UserCheckInfo(Parcel in) {
        this.phone = in.readString();
        this.userId = in.readString();
        this.name = in.readString();
        this.avatar = in.readString();
        this.email = in.readString();
    }

    public static final Creator<UserCheckInfo> CREATOR = new Creator<UserCheckInfo>() {
        @Override
        public UserCheckInfo createFromParcel(Parcel source) {
            return new UserCheckInfo(source);
        }

        @Override
        public UserCheckInfo[] newArray(int size) {
            return new UserCheckInfo[size];
        }
    };
}
