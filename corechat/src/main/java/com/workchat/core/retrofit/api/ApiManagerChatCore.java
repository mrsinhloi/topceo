package com.workchat.core.retrofit.api;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.ssl.UnsafeOkHttpClient;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManagerChatCore {

    //init
    public static WebserviceApi services;
    private static ApiManagerChatCore apiManager;

    private ApiManagerChatCore() {
        String url = ChatApplication.Companion.getURL_SERVER_CHAT();
        if(!TextUtils.isEmpty(url)) {
            OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            services = retrofit.create(WebserviceApi.class);
        }
    }

    public static ApiManagerChatCore getIntance() {
        if (apiManager == null) {
            apiManager = new ApiManagerChatCore();
        }
        return apiManager;
    }

    /**
     * Khi user dang nhap lai
     * @return
     */
    public static ApiManagerChatCore getIntanceForce() {
        return apiManager = new ApiManagerChatCore();
    }



    public void updateOneSignalUserId(String authorization,
                                      String oneSignalUserId,
                                      String oneSignalAppId,
                                      String deviceId,
                                      String os, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.updateOneSignalUserId(authorization, oneSignalUserId, oneSignalAppId, deviceId, os);
        call.enqueue(callback);
    }

    public void removeOneSignalUserId(String authorization,
                                      String oneSignalUserId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.removeOneSignalUserId(authorization, oneSignalUserId);
        call.enqueue(callback);
    }


}
