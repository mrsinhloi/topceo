package com.workchat.core.mbn.api;

import com.google.gson.Gson;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.mbn.models.VersionApp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hung on 11/24/2015.
 */
public class ParseJson {

    /**
     * {
     *   "errorCode": 0,
     *   "error": null,
     *   "data": {
     *     "_id": "5c7f45d7f30039146cbe5bac",
     *     "email": "",
     *     "phone": "0938936128",
     *     "avatar": "",
     *     "name": "",
     *     "createDate": 1551844823,
     *     "lastUpdateDate": 1551844823,
     *     "lastLoginDate": 1551844823,
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YzdmNDVkN2YzMDAzOTE0NmNiZTViYWMiLCJlbWFpbCI6IiIsInBob25lIjoiMDkzODkzNjEyOCIsImF2YXRhciI6IiIsIm5hbWUiOiIiLCJjcmVhdGVEYXRlIjoxNTUxODQ0ODIzLCJsYXN0VXBkYXRlRGF0ZSI6MTU1MTg0NDgyMywibGFzdExvZ2luRGF0ZSI6MTU1MTg0NDgyMywiaWF0IjoxNTUxODU5NzY2LCJleHAiOjQ3MDU0NTk3NjZ9.B3i4jdsUXZuF69PFeGH1znKBCim3g7Nfu1ajpAzWU4Q"
     *   }
     * }
     * @param json
     * @return
     */
    public static UserChatCore ParseUserLuva(JSONObject json) {
        UserChatCore user = new UserChatCore();
        if (json.has("data") && !json.isNull("data")) {
            try {
                JSONObject data = new JSONObject();
                data = json.getJSONObject("data");
                user = new Gson().fromJson(data.toString(), UserChatCore.class);

                /*if (data.has("email") && !data.isNull("email"))
                user.setEmail(data.getString("email"));

                if (data.has("phone") && !data.isNull("phone"))
                user.setPhone(data.getString("phone"));

                if (data.has("avatar") && !data.isNull("avatar"))
                user.setAvatar(data.getString("avatar"));

                if (data.has("name") && !data.isNull("name"))
                user.setName(data.getString("name"));

                if (data.has("_id") && !data.isNull("_id"))
                user.set_id(data.getLong("_id"));

                if (data.has("token") && !data.isNull("token"))
                    user.setToken(data.getString("token"));*/

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return user;
    }
    public static UserChatCore ParseUserLuva(Object... args) {
        UserChatCore user = new UserChatCore();
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0 || code == 1) {
                    user = ParseUserLuva(json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static VersionApp ParseVersionApp(JSONObject response) {
        VersionApp versionApp = new VersionApp();
        try {

            JSONObject result = response.getJSONObject("result");
            if (result.has("vername") && result.has("vercode") && result.has("feature") && result.has("linkapp")) {
                versionApp.setVersionName(result.getString("vername"));
                versionApp.setVersionCode(result.getString("vercode"));
                versionApp.setFeature(result.getString("feature"));
                versionApp.setLink(result.getString("linkapp"));

            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
        return versionApp;

    }

}