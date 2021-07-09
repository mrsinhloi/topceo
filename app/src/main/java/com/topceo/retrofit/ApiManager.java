package com.topceo.retrofit;

import com.topceo.analytics.Engagement;
import com.topceo.config.MyApplication;
import com.topceo.group.models.NotifySettings;
import com.topceo.group.models.SetNotifySettings;
import com.topceo.selections.hashtags.HashtagShort;
import com.topceo.services.Webservices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    public static WebserviceApi services;
    private static ApiManager apiManager;

    private ApiManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(MyApplication.getClient())
                .baseUrl(Webservices.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        services = retrofit.create(WebserviceApi.class);
    }

    public static ApiManager getIntance() {
        if (apiManager == null) {
            apiManager = new ApiManager();
        }
        return apiManager;
    }

    public static ApiManager getIntanceNew() {
        return apiManager = new ApiManager();
    }


    public void addImageItem(PostImageParam postImage, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.addImageItem(postImage);
        call.enqueue(callback);
    }

    /*public void commentLike(long commentId, Callback<JsonObject> callback){
        Call<JsonObject> call = services.commentLike(commentId);
        call.enqueue(callback);
    }
    public void commentUnLike(long commentId, Callback<JsonObject> callback){
        Call<JsonObject> call = services.commentUnLike(commentId);
        call.enqueue(callback);
    }*/


    public void getListMedia(List<String> tag,
                             int isFree,
                             int pageIndex,
                             int itemCount,
                             boolean isGetPriceInfo, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getListMedia(tag, isFree, pageIndex, itemCount, isGetPriceInfo);
        call.enqueue(callback);
    }

    public void getMedia(long mediaId,
                         boolean isGetPriceInfo, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getMedia(mediaId, isGetPriceInfo);
        call.enqueue(callback);
    }

    public void getListItemOfMedia(long mediaId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getListItemOfMedia(mediaId);
        call.enqueue(callback);
    }

    public void getListAudioAll(int pageIndex, int itemCount, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getListAudioAll(pageIndex, itemCount);
        call.enqueue(callback);
    }

    public void getListVideoAll(int pageIndex, int itemCount, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getListVideoAll(pageIndex, itemCount);
        call.enqueue(callback);
    }

    public void checkCanPlay(long mediaId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.checkCanPlay(mediaId);
        call.enqueue(callback);
    }

    public void getLikeList(long mediaId, long lastDate, int itemCount, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getLikeList(mediaId, lastDate, itemCount);
        call.enqueue(callback);
    }

    public void mediaLike(long mediaId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaLike(mediaId);
        call.enqueue(callback);
    }

    public void mediaUnlike(long mediaId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaUnlike(mediaId);
        call.enqueue(callback);
    }


    //comment media
    public void mediaCommentList(long mediaId, String lastId, int itemCount, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentList(mediaId, lastId, itemCount);
        call.enqueue(callback);
    }

    public void mediaCommentDetail(long itemId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentDetail(itemId);
        call.enqueue(callback);
    }

    public void mediaCommentReplyList(long mediaId,
                                      long replyToId,
                                      String lastId,
                                      int itemCount, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentReplyList(mediaId, replyToId, lastId, itemCount);
        call.enqueue(callback);
    }

    public void mediaCommentAdd(long mediaId,
                                String replyToId,
                                String comment, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentAdd(mediaId, replyToId, comment);
        call.enqueue(callback);
    }

    public void mediaCommentUpdate(long mediaId, String comment, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentUpdate(mediaId, comment);
        call.enqueue(callback);
    }

    public void mediaCommentDelete(long itemId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentDelete(itemId);
        call.enqueue(callback);
    }

    //like comment cua media

    public void mediaCommentLike(long commentId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentLike(commentId);
        call.enqueue(callback);
    }

    public void mediaCommentUnlike(long commentId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentUnlike(commentId);
        call.enqueue(callback);
    }

    public void mediaCommentLikeList(long commentId,
                                     String lastId,
                                     int itemCount, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.mediaCommentLikeList(commentId, lastId, itemCount);
        call.enqueue(callback);
    }


    public void getLibraryList(int isBuy,
                               int isFree,
                               String lastDate,//ban dau truyen null
                               int itemCount, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getLibraryList(isBuy, isFree, lastDate, itemCount);
        call.enqueue(callback);
    }


    public void searchHashtag(String keyword, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.searchHashtag(keyword);
        call.enqueue(callback);
    }

    public void searchUserForMention(String keyword, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.searchUserForMention(keyword);
        call.enqueue(callback);
    }


    //PROMOTION
    public void getPromotion(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getPromotion();
        call.enqueue(callback);
    }

    public void setPromotion(long promotionId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.setPromotion(promotionId);
        call.enqueue(callback);
    }

    public void refusePromotion(long promotionId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.refusePromotion(promotionId);
        call.enqueue(callback);
    }


    //
    //NOTIFICATION SETTING
    public void getUserNotifySetting(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getUserNotifySetting();
        call.enqueue(callback);
    }

    public void getFollowingPushNotifySetting(long userId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getFollowingPushNotifySetting(userId);
        call.enqueue(callback);
    }

    public void setUserNotifySetting(

            boolean NewComment,
            boolean NewLike,
            boolean NewFollower,
            boolean FollowAccepted,
            boolean FollowUploadImage,

            //set mac dinh la false cho 5 cai sau, chua xai
            /*boolean Chat,
            boolean MentionInComment,
            boolean FollowingAction,
            boolean CommentHasReply,
            boolean CommentHasLike,*/
            Callback<JsonObject> callback) {
        Call<JsonObject> call = services.setUserNotifySetting(

                NewComment,
                NewLike,
                NewFollower,
                FollowAccepted,
                FollowUploadImage,

                false,
                false,
                false,
                false,
                false
        );
        call.enqueue(callback);
    }

    public void updateFollowingPushNotifySetting(
            long UserId,
            boolean NewPost,
            Callback<JsonObject> callback) {
        Call<JsonObject> call = services.updateFollowingPushNotifySetting(UserId, NewPost, false);
        call.enqueue(callback);
    }


    public void savePost(long imageItemId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.savePost(imageItemId, "");
        call.enqueue(callback);
    }

    public void unSavePost(long imageItemId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.unSavePost(imageItemId);
        call.enqueue(callback);
    }


    public void getListMenu(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getListMenu();
        call.enqueue(callback);
    }


    public void addEngagements(ArrayList<Engagement> engagements,
                               Callback<JsonObject> callback) {
        /*JsonArray array = new JsonArray();
        for (int i = 0; i < engagements.size(); i++) {
            try {
                Engagement item = engagements.get(i);
                JsonObject obj = new JsonObject();
                obj.addProperty("PostId", item.getPostId());
                obj.addProperty("EngageType", item.getEngageType());
                obj.addProperty("CreateDate", item.getCreateDate());

                array.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
//        JsonArray array = (JsonArray) new Gson().toJsonTree(engagements);

        List<JsonObject> array = new ArrayList<>();
        for (int i = 0; i < engagements.size(); i++) {
            try {
                Engagement item = engagements.get(i);
                JsonObject obj = new JsonObject();
                obj.addProperty("PostId", item.getPostId());
                obj.addProperty("EngageType", item.getEngageType());
                obj.addProperty("CreateDate", item.getCreateDate());

                array.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        JSONArray array1 = new JSONArray(engagements);

        Call<JsonObject> call = services.addEngagements(array);
        call.enqueue(callback);
    }


    //select favorite
    public void getHashtagCategory(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getHashtagCategory();
        call.enqueue(callback);
    }

    public void getHashtags(long categoryId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getHashtags(categoryId);
        call.enqueue(callback);
    }

    public void setHashtags(long categoryId, ArrayList<HashtagShort> hashtags, Callback<JsonObject> callback) {
        PostHashtag param = new PostHashtag();
        param.setCategoryId(categoryId);
        param.setHashtags(hashtags);
        Call<JsonObject> call = services.setHashtags(param);
        call.enqueue(callback);
    }

    public void hashtagSelected(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.hashtagSelected();
        call.enqueue(callback);
    }

    public void updateUserNotifyIsView(long notifyId, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.updateUserNotifyIsView(notifyId);
        call.enqueue(callback);
    }

    public void updateAllUserNotifyIsView(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.updateAllUserNotifyIsView();
        call.enqueue(callback);
    }


    public void getNewPostCount(long lastSyncDate, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getNewPostCount(lastSyncDate / 1000);
        call.enqueue(callback);
    }

    public void getDeletedPostId(long lastSyncDate, Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getDeletedPostId(lastSyncDate / 1000);
        call.enqueue(callback);
    }

    public void getMembers(long UserId, long HashtagId,
                           long LastJoinDate,
                           int Count,
                           Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getMembers(UserId, HashtagId, LastJoinDate, Count);
        call.enqueue(callback);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //GROUP API
    public void groupList(String keyword,
                          int pageIndex,
                          int pageSize,
                          Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupList(keyword, pageIndex, pageSize);
        call.enqueue(callback);
    }

    public void groupMutualList(long userId,
                                String keyword,
                                int pageIndex,
                                int pageSize,
                                Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupMutualList(userId, keyword, pageIndex, pageSize);
        call.enqueue(callback);
    }

    public void searchToJoin(String keyword,
                             int pageIndex,
                             int pageSize,
                             Callback<JsonObject> callback) {
        Call<JsonObject> call = services.searchToJoin(keyword, pageIndex, pageSize);
        call.enqueue(callback);
    }

    public void groupInfo(long groupId,
                          Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupInfo(groupId);
        call.enqueue(callback);
    }

    public void groupTotal(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupTotal();
        call.enqueue(callback);
    }

    //admin, member
    public void getAdminsGroup(long groupId,
                               Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getAdminsGroup(groupId);
        call.enqueue(callback);
    }

    public void getMembersGroup(long groupId,
                                int pageIndex,
                                int pageSize,
                                Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getMembersGroup(groupId, pageIndex, pageSize);
        call.enqueue(callback);
    }

    public void searchToInvite(long groupId,
                               String keyword,
                               int pageIndex,
                               int pageSize,
                               Callback<JsonObject> callback) {
        Call<JsonObject> call = services.searchToInvite(groupId, keyword, pageIndex, pageSize);
        call.enqueue(callback);
    }

    public void groupMemberInvite(long groupId,
                                  long userId,
                                  Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupMemberInvite(groupId, userId);
        call.enqueue(callback);
    }

    public void groupPending(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupPending();
        call.enqueue(callback);
    }

    //tham gia, tu choi vao nhom
    public void groupMemberAccept(long groupId,
                                  Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupMemberAccept(groupId);
        call.enqueue(callback);
    }

    public void groupMemberRefuse(long groupId,
                                  Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupMemberRefuse(groupId);
        call.enqueue(callback);
    }

    public void groupRequestJoin(long groupId,
                                 Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupRequestJoin(groupId);
        call.enqueue(callback);
    }

    public void groupCancleJoin(long groupId,
                                Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupCancleJoin(groupId);
        call.enqueue(callback);
    }

    public void groupRemoveMember(long groupId, long userId,
                                  Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupRemoveMember(groupId, userId);
        call.enqueue(callback);
    }

    public void groupLeave(long groupId,
                           Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupLeave(groupId);
        call.enqueue(callback);
    }

    public void groupDelete(long groupId,
                            String password,
                            Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupDelete(groupId, password);
        call.enqueue(callback);
    }

    //update cover
    public void getUploadSASCover(String groupGUID,
                                  String extension,
                                  Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getUploadSASCover(groupGUID, extension);
        call.enqueue(callback);
    }

    public void updateCover(long groupId,
                            String coverUrl,
                            String coverSmallUrl,
                            Callback<JsonObject> callback) {
        Call<JsonObject> call = services.updateCover(groupId, coverUrl, coverSmallUrl);
        call.enqueue(callback);
    }

    //group setting
    public void groupSettingGet(long groupId,
                                Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupSettingGet(groupId);
        call.enqueue(callback);
    }

    public void groupSettingSet(long groupId, NotifySettings settings,
                                Callback<JsonObject> callback) {
        String s = new Gson().toJson(settings, NotifySettings.class);
        JsonObject json = new Gson().fromJson(s, JsonObject.class).getAsJsonObject();
        SetNotifySettings param = new SetNotifySettings(groupId, json);
        Call<JsonObject> call = services.groupSettingSet(param);
        call.enqueue(callback);

    }

    public void groupCreate(String groupGUID,
                            String groupName,
                            String description,
                            boolean isPrivate,
                            boolean isHide,
                            String coverUrl,
                            String coverSmallUrl,
                            Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupCreate(groupGUID, groupName, description, isPrivate, isHide, coverUrl, coverSmallUrl);
        call.enqueue(callback);
    }

    public void updateInfo(long groupId,
                           String groupName,
                           String description,
                           Callback<JsonObject> callback) {
        Call<JsonObject> call = services.updateInfo(groupId, groupName, description);
        call.enqueue(callback);
    }

    public void updatePrivacy(long groupId,
                              boolean isPrivate,
                              boolean isHide,
                              Callback<JsonObject> callback) {
        Call<JsonObject> call = services.updatePrivacy(groupId, isPrivate, isHide);
        call.enqueue(callback);
    }

    public void groupUpdateSettings(long groupId,
                                    int memberApprove,
                                    boolean memberCanPost,
                                    boolean postNeedApprove,
                                    Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupUpdateSettings(groupId, memberApprove, memberCanPost, postNeedApprove);
        call.enqueue(callback);
    }

    //pending user
    public void groupMemberRequest(long groupId,
                                   int pageIndex,
                                   int pageSize,
                                   Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupMemberRequest(groupId, pageIndex, pageSize);
        call.enqueue(callback);
    }

    public void groupMemberRequestAccept(long groupId,
                                         long userId,
                                         Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupMemberRequestAccept(groupId, userId);
        call.enqueue(callback);
    }

    public void groupMemberRequestRefuse(long groupId,
                                         long userId,
                                         Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupMemberRequestRefuse(groupId, userId);
        call.enqueue(callback);
    }


    ///pending post
    public void groupPendingPost(long groupId,
                                 int pageIndex,
                                 int pageSize,
                                 Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupPendingPost(groupId, 0, pageIndex, pageSize, true);
        call.enqueue(callback);
    }

    public void groupPendingPostAccept(long itemId, boolean isApprove,
                                       Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupPendingPostAccept(itemId, isApprove ? 1 : 2, "");
        call.enqueue(callback);
    }

    /*public void groupPendingPostCancel(long itemId,
                                         Callback<JsonObject> callback) {
        Call<JsonObject> call = services.groupPendingPostCancel(itemId);
        call.enqueue(callback);
    }*/
    //GROUP API
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //login by firebase
    public void loginByFirebase(String code,
                                Callback<JsonObject> callback) {
        Call<JsonObject> call = services.loginByFirebase(code, Webservices.OS);
        call.enqueue(callback);
    }

    public void checkPhoneExists(String phone,
                                 Callback<JsonObject> callback) {
        Call<JsonObject> call = services.checkPhoneExists(phone);
        call.enqueue(callback);
    }

    public void signupComplete(
            String Authorization,
            String UserName,
            String Password,
            int Gender,
            String FullName,
            String Phone,
            Callback<JsonObject> callback) {
        Call<JsonObject> call = services.signupComplete(
                Authorization,
                Webservices.OS,
                UserName,
                Password,
                "",
                Gender,
                Webservices.COUNTRY_ID,
                Webservices.COUNTRY_NAME,
                FullName,
                Phone
        );
        call.enqueue(callback);
    }

    public void getReferralLink(Callback<JsonObject> callback) {
        Call<JsonObject> call = services.getReferralLink();
        call.enqueue(callback);
    }

    public void sendVerifyEmail(String email,
                                Callback<JsonObject> callback) {
        Call<JsonObject> call = services.sendVerifyEmail(Webservices.OS, email);
        call.enqueue(callback);
    }

    public void verifyEmailToken(String code,
                                 Callback<JsonObject> callback) {
        Call<JsonObject> call = services.verifyEmailToken(Webservices.OS, code);
        call.enqueue(callback);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////


}
