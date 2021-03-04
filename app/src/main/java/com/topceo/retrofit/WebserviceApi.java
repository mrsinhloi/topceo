package com.topceo.retrofit;

import com.topceo.group.models.SetNotifySettings;
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

    //
    @POST("image/addImageItem")
    Call<JsonObject> addImageItem(@Body PostImageParam postImage);

    @POST("image/comment/like")
    Call<JsonObject> commentLike(@Field("CommentId") long commentId);

    @POST("image/comment/unlike")
    Call<JsonObject> commentUnLike(@Field("CommentId") long commentId);

    //hashtag, mention
    @POST("image/searchHashtag")
    Call<JsonObject> searchHashtag(
            @Query("Keyword") String keyword
    );

    @POST("image/searchUserForMention")
    Call<JsonObject> searchUserForMention(
            @Query("Keyword") String keyword
    );


    //MEDIAIMAGE
    //Lay nhieu loai thi truyen Tags[] - Audio , Tags[] - Video
    @FormUrlEncoded
    @POST("media/list")
    Call<JsonObject> getListMedia(
            @Field("Tags[]") List<String> tag,
            @Field("IsFree") int isFree,
            @Field("PageIndex") int pageIndex,
            @Field("ItemCount") int itemCount,
            @Field("GetPriceInfo") boolean isGetPriceInfo

    );

    @FormUrlEncoded
    @POST("media/id")
    Call<JsonObject> getMedia(
            @Field("MediaId") long mediaId,
            @Field("GetPriceInfo") boolean isGetPriceInfo

    );

    @FormUrlEncoded
    @POST("media/item/list")
    Call<JsonObject> getListItemOfMedia(
            @Field("MediaId") long mediaId
    );

    @FormUrlEncoded
    @POST("media/audio/all")
    Call<JsonObject> getListAudioAll(
            @Field("PageIndex") int pageIndex,
            @Field("ItemCount") int itemCount
    );

    @FormUrlEncoded
    @POST("media/video/all")
    Call<JsonObject> getListVideoAll(
            @Field("PageIndex") int pageIndex,
            @Field("ItemCount") int itemCount
    );

    @FormUrlEncoded
    @POST("/user/media/checkCanPlay")
    Call<JsonObject> checkCanPlay(
            @Field("MediaId") long mediaId
    );


    //LIKE/UNLIKE
    @FormUrlEncoded
    @POST("media/getLikeList")
    Call<JsonObject> getLikeList(
            @Field("MediaId") long mediaId,
            @Field("LastDate") long lastDate,//ban dau truyen null
            @Field("ItemCount") int itemCount

    );

    @FormUrlEncoded
    @POST("/media/like")
    Call<JsonObject> mediaLike(
            @Field("MediaId") long mediaId
    );

    @FormUrlEncoded
    @POST("/media/unlike")
    Call<JsonObject> mediaUnlike(
            @Field("MediaId") long mediaId
    );


    //comment media
    @FormUrlEncoded
    @POST("media/comment/list")
    Call<JsonObject> mediaCommentList(
            @Field("MediaId") long mediaId,
            @Field("LastId") String lastId,
            @Field("ItemCount") int itemCount

    );

    @FormUrlEncoded
    @POST("media/comment/id")
    Call<JsonObject> mediaCommentDetail(
            @Field("ItemId") long itemId
    );

    @FormUrlEncoded
    @POST("media/comment/replyList")
    Call<JsonObject> mediaCommentReplyList(
            @Field("MediaId") long mediaId,
            @Field("ReplyToId") long replyToId,
            @Field("LastId") String lastId,
            @Field("ItemCount") int itemCount
    );

    @FormUrlEncoded
    @POST("media/comment/add")
    Call<JsonObject> mediaCommentAdd(
            @Field("MediaId") long mediaId,
            @Field("ReplyToId") String replyToId,
            @Field("Comment") String comment
    );

    @FormUrlEncoded
    @POST("media/comment/update")
    Call<JsonObject> mediaCommentUpdate(
            @Field("ItemId") long itemId,
            @Field("Comment") String comment
    );

    @FormUrlEncoded
    @POST("media/comment/delete")
    Call<JsonObject> mediaCommentDelete(
            @Field("ItemId") long itemId
    );


    //like comment cua media
    @FormUrlEncoded
    @POST("media/comment/like")
    Call<JsonObject> mediaCommentLike(
            @Field("CommentId") long commentId
    );

    @FormUrlEncoded
    @POST("media/comment/unlike")
    Call<JsonObject> mediaCommentUnlike(
            @Field("CommentId") long commentId
    );

    @FormUrlEncoded
    @POST("media/comment/getLikeList")
    Call<JsonObject> mediaCommentLikeList(
            @Field("CommentId") long commentId,
            @Field("LastId") String lastId,
            @Field("ItemCount") int itemCount
    );


    //LIBRARY
    @FormUrlEncoded
    @POST("user/library/list")
    Call<JsonObject> getLibraryList(
            @Field("IsBuy") int isBuy,
            @Field("IsFree") int isFree,
            @Field("LastDate") String lastDate,//ban dau truyen null
            @Field("ItemCount") int itemCount

    );


    //PROMOTION
    @POST("user/getPromotion")
    Call<JsonObject> getPromotion();

    @FormUrlEncoded
    @POST("user/setPromotion")
    Call<JsonObject> setPromotion(
            @Field("PromotionId") long promotionId
    );

    @FormUrlEncoded
    @POST("user/refusePromotion")
    Call<JsonObject> refusePromotion(
            @Field("PromotionId") long promotionId
    );


    //SETTING NOTIFICATION
    //TONG
    @POST("user/getUserNotifySetting")
    Call<JsonObject> getUserNotifySetting();


    @POST("user/setUserNotifySetting")
    Call<JsonObject> setUserNotifySetting(
            @Query("NewComment") boolean NewComment,
            @Query("NewLike") boolean NewLike,
            @Query("NewFollower") boolean NewFollower,
            @Query("FollowAccepted") boolean FollowAccepted,
            @Query("FollowUploadImage") boolean FollowUploadImage,
            @Query("Chat") boolean Chat,
            @Query("MentionInComment") boolean MentionInComment,
            @Query("FollowingAction") boolean FollowingAction,
            @Query("CommentHasReply") boolean CommentHasReply,
            @Query("CommentHasLike") boolean CommentHasLike
    );

    //CA NHAN
    @POST("user/getFollowingPushNotifySetting")
    Call<JsonObject> getFollowingPushNotifySetting(
            @Query("UserId") long UserId
    );

    @POST("user/updateFollowingPushNotifySetting")
    Call<JsonObject> updateFollowingPushNotifySetting(
            @Query("UserId") long UserId,
            @Query("NewPost") boolean NewPost,
            @Query("NewActivity") boolean NewActivity
    );


    //save post
    @FormUrlEncoded
    @POST("user/saved/add")
    Call<JsonObject> savePost(
            @Field("ImageItemId") long imageItemId,
            @Field("CollectionIds") String collectionIds
    );

    @FormUrlEncoded
    @POST("user/saved/remove")
    Call<JsonObject> unSavePost(
            @Field("ImageItemId") long imageItemId
    );


    //MENU SHOP SETTING
    @POST("/menu/list")
    Call<JsonObject> getListMenu();

    //THONG KE LUOT VIEW
    /*@Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("insight/addEngagements")
    Call<JsonObject> addEngagements(
            @Body JSONArray Engagements
    );*/

    @FormUrlEncoded
    @POST("insight/addEngagements")
    Call<JsonObject> addEngagements(
            @Field("Engagements[]") List<JsonObject> engagements
    );


    //select favorites
    @POST("user/suggest/getHashtagCategory")
    Call<JsonObject> getHashtagCategory();

    @FormUrlEncoded
    @POST("user/suggest/getHashtags")
    Call<JsonObject> getHashtags(
            @Field("CategoryId") long categoryId
    );

    //    @FormUrlEncoded
    @POST("user/suggest/setHashtags")
    Call<JsonObject> setHashtags(
            /*@Field("CategoryId") long categoryId,
            @Field("Hashtags") ArrayList<HashtagShort> hashtags*/
            @Body PostHashtag postHashtag
    );

    /**
     * Sau khi update tat ca man hinh thi goi ham nay de xac nhan da hoan thanh viec chon topic
     *
     * @return
     */
    @POST("user/suggest/hashtagSelected")
    Call<JsonObject> hashtagSelected();


    @POST("user/updateUserNotifyIsView")
    Call<JsonObject> updateUserNotifyIsView(
            @Query("NotifyId") long notifyId
    );

    @POST("user/updateAllUserNotifyIsView")
    Call<JsonObject> updateAllUserNotifyIsView();


    //offline
    @FormUrlEncoded
    @POST("sync/group/getNewPostCount")
    Call<JsonObject> getNewPostCount(
            @Field("LastSyncDate") long lastSyncDate
    );

    @FormUrlEncoded
    @POST("sync/group/getDeletedPostId")
    Call<JsonObject> getDeletedPostId(
            @Field("LastSyncDate") long lastSyncDate
    );


    //group user
    @FormUrlEncoded
    @POST("user/group/getMembers")
    Call<JsonObject> getMembers(
            @Field("UserId") long UserId,
            @Field("HashtagId") long HashtagId,
            @Field("LastJoinDate") long LastJoinDate,
            @Field("Count") int Count
    );

    //GROUP API
    @FormUrlEncoded
    @POST("group/list")
    Call<JsonObject> groupList(
            @Field("Keyword") String Keyword,
            @Field("PageIndex") int PageIndex,
            @Field("PageSize") int PageSize
    );

    //group chung cua 2 user
    @FormUrlEncoded
    @POST("group/mutualList")
    Call<JsonObject> groupMutualList(
            @Field("UserId") long userId,
            @Field("Keyword") String Keyword,
            @Field("PageIndex") int PageIndex,
            @Field("PageSize") int PageSize
    );

    @FormUrlEncoded
    @POST("group/searchToJoin")
    Call<JsonObject> searchToJoin(
            @Field("Keyword") String Keyword,
            @Field("PageIndex") int PageIndex,
            @Field("PageSize") int PageSize
    );

    @FormUrlEncoded
    @POST("group/info")
    Call<JsonObject> groupInfo(
            @Field("GroupId") long groupId
    );

    @POST("group/total")
    Call<JsonObject> groupTotal();

    //admin, member
    @FormUrlEncoded
    @POST("group/admin/list")
    Call<JsonObject> getAdminsGroup(
            @Field("GroupId") long GroupId
    );

    @FormUrlEncoded
    @POST("group/member/list")
    Call<JsonObject> getMembersGroup(
            @Field("GroupId") long GroupId,
            @Field("PageIndex") int PageIndex,
            @Field("PageSize") int PageSize
    );

    @FormUrlEncoded
    @POST("group/member/searchToInvite")
    Call<JsonObject> searchToInvite(
            @Field("GroupId") long GroupId,
            @Field("Keyword") String Keyword,
            @Field("PageIndex") int PageIndex,
            @Field("PageSize") int PageSize
    );

    //invite
    @FormUrlEncoded
    @POST("group/member/add")
    Call<JsonObject> groupMemberInvite(
            @Field("GroupId") long groupId,
            @Field("UserId") long userId
    );

    //ds group dang dc moi
    @POST("user/groupPending")
    Call<JsonObject> groupPending();

    //tham gia, tu choi vao group
    @FormUrlEncoded
    @POST("group/member/accept")
    Call<JsonObject> groupMemberAccept(
            @Field("GroupId") long groupId
    );

    @FormUrlEncoded
    @POST("group/member/refuse")
    Call<JsonObject> groupMemberRefuse(
            @Field("GroupId") long groupId
    );

    //goi yc thamgia/huy yc tham gia group
    //Gửi yêu cầu tham gia group
    @FormUrlEncoded
    @POST("group/memberRequest/add")
    Call<JsonObject> groupRequestJoin(
            @Field("GroupId") long groupId
    );

    //Huỷ yêu cầu tham gia group
    @FormUrlEncoded
    @POST("group/memberRequest/cancel")
    Call<JsonObject> groupCancleJoin(
            @Field("GroupId") long groupId
    );

    //roi nhom
    @FormUrlEncoded
    @POST("group/member/remove")
    Call<JsonObject> groupRemoveMember(
            @Field("GroupId") long groupId,
            @Field("UserId") long userId

    );

    //xoa khoi nhom
    @FormUrlEncoded
    @POST("group/member/leave")
    Call<JsonObject> groupLeave(
            @Field("GroupId") long groupId
    );

    //xoa nhom
    @FormUrlEncoded
    @POST("group/delete")
    Call<JsonObject> groupDelete(
            @Field("GroupId") long groupId,
            @Field("Password") String password
    );



    //edit banner
    //{ Original: { SAS, Link }, Small: { SAS, Link }}
    @FormUrlEncoded
    @POST("group/getUploadSAS")
    Call<JsonObject> getUploadSASCover(
            @Field("GroupGUID") String groupGUID,
            @Field("Extension") String extension

    );

    @FormUrlEncoded
    @POST("group/updateCover")
    Call<JsonObject> updateCover(
            @Field("GroupId") long groupId,
            @Field("CoverUrl") String coverUrl,
            @Field("CoverSmallUrl") String coverSmallUrl

    );

    //notify group setting
    @FormUrlEncoded
    @POST("group/notifySettings/get")
    Call<JsonObject> groupSettingGet(
            @Field("GroupId") long groupId
    );

//    @FormUrlEncoded
    @POST("group/notifySettings/set")
    Call<JsonObject> groupSettingSet(
            @Body SetNotifySettings settings
    );

    //tao group
    @FormUrlEncoded
    @POST("group/create")
    Call<JsonObject> groupCreate(
            @Field("GroupGUID") String groupGUID,
            @Field("GroupName") String groupName,
            @Field("Description") String description,
            @Field("IsPrivate") boolean isPrivate,
            @Field("IsHide") boolean isHide,
            @Field("CoverUrl") String coverUrl,
            @Field("CoverSmallUrl") String coverSmallUrl

    );

    @FormUrlEncoded
    @POST("group/updateInfo")
    Call<JsonObject> updateInfo(
            @Field("GroupId") long groupId,
            @Field("GroupName") String groupName,
            @Field("Description") String description

    );

    @FormUrlEncoded
    @POST("group/updatePrivacy")
    Call<JsonObject> updatePrivacy(
            @Field("GroupId") long groupId,
            @Field("IsPrivate") boolean isPrivate,
            @Field("IsHide") boolean isHide

    );


    //group config
    @FormUrlEncoded
    @POST("group/updateSettings")
    Call<JsonObject> groupUpdateSettings(
            @Field("GroupId") long groupId,
            @Field("MemberApprove") int memberApprove,
            @Field("MemberCanPost") boolean memberCanPost,
            @Field("PostNeedApprove") boolean postNeedApprove
    );

    //duyet user tham gia
    @FormUrlEncoded
    @POST("group/memberRequest/list")
    Call<JsonObject> groupMemberRequest(
            @Field("GroupId") long GroupId,
            @Field("PageIndex") int PageIndex,
            @Field("PageSize") int PageSize
    );
    //dong y
    @FormUrlEncoded
    @POST("group/memberRequest/accept")
    Call<JsonObject> groupMemberRequestAccept(
            @Field("GroupId") long groupId,
            @Field("UserId") long userId
    );
    //tu choi
    @FormUrlEncoded
    @POST("group/memberRequest/refuse")
    Call<JsonObject> groupMemberRequestRefuse(
            @Field("GroupId") long groupId,
            @Field("UserId") long userId
    );

    //tin cho duyet
    @FormUrlEncoded
    @POST("group/pendingPost/list")
    Call<JsonObject> groupPendingPost(
            @Field("GroupId") long GroupId,
            @Field("Status") int Status,
            @Field("PageIndex") int PageIndex,
            @Field("PageSize") int PageSize,
            @Field("SortOrder") boolean SortOrder

    );
    //dong y
    @FormUrlEncoded
    @POST("group/pendingPost/approve")
    Call<JsonObject> groupPendingPostAccept(
            @Field("ItemId") long itemId,
            @Field("Status") int status,
            @Field("RejectReason") String rejectReason

    );
    //tu choi, user tu huy ko muon post nua
    /*@FormUrlEncoded
    @POST("group/pendingPost/cancel")
    Call<JsonObject> groupPendingPostCancel(
            @Field("ItemId") long itemId
    );*/

    //GROUP API

    //login by firebase
    @FormUrlEncoded
    @POST("user/loginByFirebase")
    Call<JsonObject> loginByFirebase(
            @Field("Code") String code,
            @Header("os") String os
    );

    //"Hoàn tất việc đăng kí.
    //Cần truyền Bearer Token lấy đc ở hàm loginByFirebase vào Header Authorization"
    @FormUrlEncoded
    @POST("user/signup/complete")
    Call<JsonObject> signupComplete(
            @Header("Authorization") String Authorization,
            @Header("os") String os,
            @Field("UserName") String UserName,
            @Field("Password") String Password,
            @Field("Email") String Email,
            @Field("Gender") int Gender,
            @Field("CountryId") String CountryId,
            @Field("CountryName") String CountryName,
            @Field("FullName") String FullName

    );

    @POST("user/getReferralLink")
    Call<JsonObject> getReferralLink();

}
