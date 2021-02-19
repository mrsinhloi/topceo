package com.workchat.core.retrofit.api;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebserviceApi {

    //group
    /*@FormUrlEncoded
    @POST("user/group/getMembers")
    Call<JsonObject> getMembers(
            @Field("UserId") long UserId,
            @Field("HashtagId") long HashtagId,
            @Field("LastJoinDate") long LastJoinDate,
            @Field("Count") int Count
    );*/

    //OneSignal
    @FormUrlEncoded
    @POST("/user/updateOneSignalUserId")
    Call<JsonObject> updateOneSignalUserId(
            @Header("Authorization") String authorization,
            @Field("oneSignalUserId") String oneSignalUserId,
            @Field("oneSignalAppId") String oneSignalAppId,
            @Field("deviceId") String deviceId,
            @Field("os") String os

    );

    @FormUrlEncoded
    @POST("/user/removeOneSignalUserId")
    Call<JsonObject> removeOneSignalUserId(
            @Header("Authorization") String authorization,
            @Field("oneSignalUserId") String oneSignalUserId
    );

}
