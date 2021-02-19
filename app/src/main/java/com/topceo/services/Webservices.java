package com.topceo.services;

import android.content.Context;
import android.text.TextUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.ItemData;
import com.topceo.objects.other.MyNotify;
import com.topceo.objects.other.SearchObject;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserSearchChat;
import com.topceo.objects.other.UserShort;
import com.topceo.selections.hashtags.Hashtag;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by MrPhuong on 2016-07-25.
 */
public class Webservices {

    private static String languageId = Locale.getDefault().getLanguage().toUpperCase();//"VI", "EN";
    private static String languageName = Locale.getDefault().getDisplayName();//"English(United State)"

    public static final String OS = "Android";
    public static final String COUNTRY_ID = Locale.getDefault().getCountry();//VN
    public static final String COUNTRY_NAME = Locale.getDefault().getDisplayCountry();//Viet Nam


    public static final String URL = "https://abook-api.ehubstar.com/";
//    public static final String URL = "http://10.10.9.76:3002/";

    public static final String URL_GRAPHQL = URL + "data";
    public static final String URL_CORE_CHAT = "https://apichat.app/";
    public static final String URL_PAYMENT = "http://abook-pay.ehubstar.com";
    public static final String URL_INSIGHT = "http://abook-insight.ehubstar.com";


    /**
     * Neu la page 0: thi truyen len "null" hoac khong truyen
     *
     * @param lastItemDate
     * @return
     */
    public static String getLastItemDate(long lastItemDate) {
        return ((lastItemDate == 0) ? "null" : String.valueOf(lastItemDate));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
//    private static final String feedItem = "{ ImageItemId, ImageOriginal, ImageLarge, ImageMedium, ImageSmall, Description, IsOwner, IsLiked, IsShared, IsOwner,LikeCount, CommentCount,ShareCount, CreateDate, Location, Owner{UserId, UserName, AvatarSmall}, ImageLargeWidth, ImageLargeHeight, WebLink}";
//    private static final String feedItem = "{ImageItemId,ItemContent{ItemType, Large{Link, Width, Height, SizeInKb},Medium{Link, Width, Height, SizeInKb}, Small{Link, Width, Height, SizeInKb}, Info{Icon, Text, Link, Target}},Description,CreateDate,Location,LikeCount,CommentCount, ShareCount,IsLiked,IsShared,IsSaved,SavedDate,Owner{UserId,UserName,AvatarSmall,IsVip},WebLink}";
    public static final String commentPreview = "Comments(Count:2){User{UserName, AvatarSmall}, Comment, ItemId, IsLiked, ReplyToId, CreateDate}";
    private static final String feedItem = "{ImageItemId,ItemContent{ItemType, Large{Link, Width, Height, SizeInKb},Medium{Link, Width, Height, SizeInKb}, Small{Link, Width, Height, SizeInKb}, Info{Icon, Text, Link, Target}},Description,CreateDate,Location,LikeCount,CommentCount,IsLiked,IsShared,IsSaved,SavedDate,Owner{UserId,UserName,AvatarSmall,IsVip},WebLink," + commentPreview + ",ItemType,ItemData{LinkPreview{Link,Title,Caption,Image,SiteName}}}";

    private static final String feedItemShort = "{ImageItemId,Description,LikeCount,CommentCount}";//ShareCount

    public static String GET_NEWSFEED_ADMIN() {
        return "{MostInteractivePosts" + feedItem + "}";
    }

    public static String GET_NEWSFEED() {
        return "{Newsfeed(Count:15)" + feedItem + "}";
    }

    public static String GET_NEWSFEED_MORE(long imageItemId, long lastItemDate) {
        return "{Newsfeed(Count:15, LastItemId:" + imageItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + feedItem + "}";
    }

    //NEU LA TAB SON TUNG THI LAY Y HET, CHI DOI TEN
    public static String GET_NEWSFEED_SONTUNG() {
        return "{GeneralGroupFeeds(Count:15)" + feedItem + "}";
    }

    public static String GET_NEWSFEED_MORE_SONTUNG(long imageItemId, long lastItemDate) {
        return "{GeneralGroupFeeds(Count:15, LastItemId:" + imageItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + feedItem + "}";
    }


    public static String GET_NEWSFEED_SONTUNG_NEW_POST(long imageItemId) {
        return "{GeneralGroupFeeds(GetNewPost:true, LastItemId:" + imageItemId + ")" + feedItem + "}";
    }

    public static String GET_NEWSFEED_SONTUNG_UPDATE_FIRST() {
        return "{GeneralGroupFeeds(Count:15)" + feedItemShort + "}";
    }

    public static String GET_NEWSFEED_SONTUNG_UPDATE_MORE(long imageItemId, int count) {
        return "{GeneralGroupFeeds(Count:" + count + ",LastItemId:" + imageItemId + ")" + feedItemShort + "}";
    }


    //lay posts theo group
    public static String GET_FEED_OF_GROUP(long groupId, long lastItemId) {
        return "{GroupFeeds(Count:15, LastItemId:" + lastItemId + ", GroupId:" + groupId + ")" + feedItem + "}";
    }

    /**
     * Sau khi upload image len azure thi goi tiep ham nay
     * mutation{CreateImageItem(ItemGUID:"0a4e0fe9-d08d-4efe-a422-9474cee9f56f",ImageExtension:".jpg",IsPrivate:false,Description:"hohoho",Hashtags:["abc","xyz"]){ImageItemId, ItemGUID}}
     * mutation{AddImageItem(ItemGUID:"f2d7a90d-62e4-45dd-ad2a-8a37c83368d4",ImageExtension:".jpg",IsPrivate:false,Description:"#myxteam",Hashtags:["myxteam"],UserTags:[],Location:"9A Đinh Tiên Hoàng, Đa Kao, Quận 1, Hồ Chí Minh",Lat:"10.7871296",Long:"106.7004713"){ ImageItemId, ImageOriginal, Description, IsOwner, IsLiked, IsShared, IsOwner,LikeCount, CommentCount,ShareCount, CreateDate, Location, Owner{UserName, AvatarSmall}}}
     */
    public static String GET_AFTER_UPLOAD_IMAGE(String GUID, String extension, int imgWidth, int imgHeight, String description, String location, String latitude, String longitude) {
        String hashtag = "";
        List<String> tags = MyUtils.getListTag(description);
        if (tags.size() > 0) {
            for (int i = 0; i < tags.size(); i++) {
                if (i == tags.size() - 1) {
                    hashtag += "\"" + tags.get(i) + "\"";
                } else {
                    hashtag += "\"" + tags.get(i) + "\",";
                }
            }
        }
        hashtag = ",Hashtags:[" + hashtag + "]";

        ///mention
        String mention = "";
        List<String> mentions = MyUtils.getListMention(description);
        if (mentions.size() > 0) {
            for (int i = 0; i < mentions.size(); i++) {
                if (i == mentions.size() - 1) {
                    mention += "\"" + mentions.get(i) + "\"";
                } else {
                    mention += "\"" + mentions.get(i) + "\",";
                }
            }
        }
        mention = ",UserTags:[" + mention + "]";

        //replace \n by <br>
        description = MyUtils.replaceDescriptionForServer(description);
        return "mutation{AddImageItem(ItemGUID:\"" + GUID + "\",ImageExtension:\"." + extension + "\",IsPrivate:false,ImageWidth:" + imgWidth + ",ImageHeight:" + imgHeight + ",Description:\"" + description + "\"" + hashtag + mention + ",Location:\"" + location + "\",Lat:\"" + latitude + "\",Long:\"" + longitude + "\")" + feedItem + "}";
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param imageItemId
     * @param isLiked
     * @return float ImageItemId
     */
    public static String UPDATE_IMAGE_ITEM_LIKED(long imageItemId, boolean isLiked) {
        return "mutation{UpdateImageItemLiked(ImageItemId:" + imageItemId + ", IsLiked:" + isLiked + "){ImageItemId}}";//LikeCount
    }

    public static String UPDATE_IMAGE_ITEM_DESCRIPTION(float imageItemId, /*String description,*/ List<String> tags, List<String> mentions, ItemData itemData,
                                                       String address,
                                                       double lat,
                                                       double lon) {


        String hashtag = "";

        if (tags != null && tags.size() > 0) {
            for (int i = 0; i < tags.size(); i++) {
                if (i == tags.size() - 1) {
                    hashtag += "\"" + tags.get(i) + "\"";
                } else {
                    hashtag += "\"" + tags.get(i) + "\",";
                }
            }
        }
        hashtag = ",Hashtags:[" + hashtag + "]";

        ///mention
        String mention = "";
        if (mentions != null && mentions.size() > 0) {
            for (int i = 0; i < mentions.size(); i++) {
                if (i == mentions.size() - 1) {
                    mention += "\"" + mentions.get(i) + "\"";
                } else {
                    mention += "\"" + mentions.get(i) + "\",";
                }
            }
        }
        mention = ",UserTags:[" + mention + "]";

        //link parse neu co
        String linkParse = "";
        if (itemData != null && itemData.getLinkPreview() != null) {
            try {
                String json = new Gson().toJson(itemData);
                json = JSONObject.quote(json);
                linkParse = ",ItemData:" + json;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //replace \n by <br>
//        description = MyUtils.replaceDescriptionForServer(description);

        String location = "";
        if (address != null) {//dia chi co the trong khi cap nhat lai
            location = ", Location:\"" + address + "\", Lat:\"" + lat + "\", Long:\"" + lon + "\"";
        }

        if (!TextUtils.isEmpty(linkParse)) {
            return "mutation{UpdateImageItem(ImageItemId:" + imageItemId + /*", Description:\"" + description + "\"" +*/ hashtag + mention + linkParse + location + "){ImageItemId}}";
        } else {
            return "mutation{UpdateImageItem(ImageItemId:" + imageItemId + /*", Description:\"" + description + "\"" +*/ hashtag + mention + location + "){ImageItemId}}";
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * data?query={ImageComment(Id:100){Comments(Count:30, LastItemId:5000){User{UserName, AvatarSmall}, Comment, CreateDate}}
     * {ImageComment(Id:100){ImageLarge, Description, CommentCount, CommentLike, Comments(Count:30, LastItemId:5000){ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}}
     *
     * @param imageItemId
     * @param lastItemId  phan tu dau tien lastItemId=null, truyen vao 0, vao ham nay chuyen lai
     * @return
     */
    /*public static String GET_COMMENTS(long imageItemId, long lastItemId, long lastItemDate) {
        if (lastItemId == 0) {
            return "{ImageComment(Id:" + imageItemId + "){Comments(Count:30){ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}}}";
        } else {
            return "{ImageComment(Id:" + imageItemId + "){Comments(Count:30, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + "){ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}}}";
        }

    }*/

//    private static final String itemCommentParent = "ItemId,ImageItemId,Comment,CreateDate,CreateOS,LastModifyDate,LastModifyOS,ReplyToId,Edited,LikeCount,ReplyCount";
    private static final String itemCommentParent = "ItemId,ImageItemId,Comment,CreateDate,ReplyToId,LikeCount,ReplyCount,IsLiked";
    private static final String itemCommentShort = "ItemId,ImageItemId,ReplyToId";

    public static String GET_COMMENTS(long imageItemId, long lastItemId, long lastItemDate) {
        if (lastItemId == 0) {
//          return "{ImageComment(Id:" + imageItemId + "){Comments(Count:30){ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}}}";
            return "{Comments(ImageItemId:" + imageItemId + ",Count:30){User{UserId, UserName, AvatarSmall}, " + itemCommentParent + "}}";
        } else {
//            return "{ImageComment(Id:" + imageItemId + "){Comments(Count:30, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + "){ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}}}";
            return "{Comments(ImageItemId:" + imageItemId + ",Count:30, LastItemDate: " + getLastItemDate(lastItemDate) + ", LastItemId:" + lastItemId + "){User{UserId, UserName, AvatarSmall}, " + itemCommentParent + "}}";
        }

    }

    public static String GET_COMMENTS_CHILD(long imageItemId, long replyToId, long lastItemId, long lastItemDate) {
        if (lastItemId == 0) {
//          return "{ImageComment(Id:" + imageItemId + "){Comments(Count:30){ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}}}";
            return "{Comments(ImageItemId:" + imageItemId + ",ReplyToId:" + replyToId + ",Count:30){User{UserId, UserName, AvatarSmall}, " + itemCommentParent + "}}";
        } else {
//            return "{ImageComment(Id:" + imageItemId + "){Comments(Count:30, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + "){ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}}}";
            return "{Comments(ImageItemId:" + imageItemId + ",ReplyToId:" + replyToId + ",Count:30, LastItemDate: " + getLastItemDate(lastItemDate) + ", LastItemId:" + lastItemId + "){User{UserId, UserName, AvatarSmall}, " + itemCommentParent + "}}";
        }

    }

    public static String DELETE_COMMENT(long commentId) {
        return "mutation{DeleteImageComment(CommentId:" + commentId + "){ImageItemId}}";

    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * AddImageComment
     *
     * @param imageItemId
     * @param comment
     * @return ImageComment
     */
    public static String SEND_COMMENT(long imageItemId, String comment, long replyToId) {
        if (replyToId > 0) {
            return "mutation{AddImageComment(ImageItemId:" + imageItemId + ", ReplyToId:" + replyToId + ", Comment:\"" + comment + "\"){ItemId, User{UserId, UserName, AvatarSmall}, " + itemCommentShort + "}}";
        } else {
            return "mutation{AddImageComment(ImageItemId:" + imageItemId + ", Comment:\"" + comment + "\"){ItemId, User{UserId, UserName, AvatarSmall}, " + itemCommentShort + "}}";
        }


    }


    //PARSE JSON////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param obj
     * @param type Type collectionType = new TypeToken<List<WorkspaceMemberModel>>(){}.getType();
     * @return
     */
    public static ReturnResult parseJson(JSONObject obj, Type type, boolean isArray) {
        ReturnResult result = null;

        try {
            result = new ReturnResult();

            if (!obj.isNull("ErrorCode")) {
                int errorCode = obj.getInt("ErrorCode");
                result.setErrorCode(errorCode);
            }

            if (!obj.isNull("Message")) {
                String errorMessage = obj.getString("Message");
                result.setErrorMessage(errorMessage);
            }

            if (type == String.class) {
                if (!obj.isNull("Data")) {
                    String data = obj.getString("Data");
                    result.setData(data);
                }
            } else if (type == Boolean.class) {
                if (!obj.isNull("Data")) {
                    String data = obj.getString("Data");
                    try {
                        result.setData(Boolean.parseBoolean(data));
                    } catch (Exception e) {
                        result.setData(null);
                    }
                }
            } else if (type == Integer.class) {
                if (!obj.isNull("Data")) {
                    String data = obj.getString("Data");
                    try {
                        result.setData(Integer.parseInt(data));
                    } catch (Exception e) {
                        result.setData(null);
                    }
                }
            } else {
                if (!obj.isNull("Data") && type != null) {
                    if (isArray) {
//                        JSONArray data = obj.getJSONArray("Data");
//                        long start = System.currentTimeMillis();
                        String data = obj.getString("Data");
                        Object object = new Gson().fromJson(data, type);
//                        MyUtils.howLong(start, "Gson().fromJson Data");
                        result.setData(object);
                    } else {//object
//                        JSONObject data = obj.getJSONObject("Data");
//                        long start = System.currentTimeMillis();
                        String data = obj.getString("Data");
                        Object object = new Gson().fromJson(data, type);
//                        MyUtils.howLong(start, "Gson().fromJson Data");
                        result.setData(object);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static ReturnResult parseJsonCoreChat(JSONObject obj, Type type, boolean isArray) {
        ReturnResult result = null;

        try {
            result = new ReturnResult();

            if (!obj.isNull("errorCode")) {
                int errorCode = obj.getInt("errorCode");
                result.setErrorCode(errorCode);
            }

            if (!obj.isNull("error")) {
                String errorMessage = obj.getString("error");
                result.setErrorMessage(errorMessage);
            }

            if (type == String.class) {
                if (!obj.isNull("data")) {
                    String data = obj.getString("data");
                    result.setData(data);
                }
            } else if (type == Boolean.class) {
                if (!obj.isNull("data")) {
                    String data = obj.getString("data");
                    try {
                        result.setData(Boolean.parseBoolean(data));
                    } catch (Exception e) {
                        result.setData(null);
                    }
                }
            } else if (type == Integer.class) {
                if (!obj.isNull("data")) {
                    String data = obj.getString("data");
                    try {
                        result.setData(Integer.parseInt(data));
                    } catch (Exception e) {
                        result.setData(null);
                    }
                }
            } else {
                if (!obj.isNull("data") && type != null) {
                    if (isArray) {
//                        JSONArray data = obj.getJSONArray("Data");
//                        long start = System.currentTimeMillis();
                        String data = obj.getString("data");
                        Object object = new Gson().fromJson(data, type);
//                        MyUtils.howLong(start, "Gson().fromJson Data");
                        result.setData(object);
                    } else {//object
//                        JSONObject data = obj.getJSONObject("Data");
//                        long start = System.currentTimeMillis();
                        String data = obj.getString("data");
                        Object object = new Gson().fromJson(data, type);
//                        MyUtils.howLong(start, "Gson().fromJson Data");
                        result.setData(object);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static ReturnResult parseJson(String response, Type type, boolean isArray) {
        ReturnResult result = null;

        try {
            result = new ReturnResult();
            JSONObject obj = new JSONObject(response);
            result = parseJson(obj, type, isArray);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * @param obj
     * @param type Type collectionType = new TypeToken<List<WorkspaceMemberModel>>(){}.getType();
     * @return
     */
    public static ReturnResult parseJsonGraphQL(JSONObject obj, Type type) {
        ReturnResult result = null;

        try {
            result = new ReturnResult();


            if (!obj.isNull("data")) {
                JSONObject child = obj.getJSONObject("data");
                if (!child.isNull("Newsfeed") && type != null) {
                    JSONArray data = child.getJSONArray("Newsfeed");
                    long start = System.currentTimeMillis();
                    Object object = new Gson().fromJson(data.toString(), type);
                    MyUtils.howLong(start, "Gson().fromJson Newsfeed");
                    result.setData(object);
                    result.setErrorCode(ReturnResult.SUCCESS);
                    result.setErrorMessage("");

                } else

                    //SON TUNG
                    if (!child.isNull("GeneralGroupFeeds") && type != null) {
                        JSONArray data = child.getJSONArray("GeneralGroupFeeds");
                        long start = System.currentTimeMillis();
                        Object object = new Gson().fromJson(data.toString(), type);
                        MyUtils.howLong(start, "Gson().fromJson GeneralGroupFeeds");
                        result.setData(object);
                        result.setErrorCode(ReturnResult.SUCCESS);
                        result.setErrorMessage("");

                    } else if (!child.isNull("GroupFeeds") && type != null) {
                        JSONArray data = child.getJSONArray("GroupFeeds");
                        long start = System.currentTimeMillis();
                        Object object = new Gson().fromJson(data.toString(), type);
                        MyUtils.howLong(start, "Gson().fromJson GroupFeeds");
                        result.setData(object);
                        result.setErrorCode(ReturnResult.SUCCESS);
                        result.setErrorMessage("");

                    } else if (!child.isNull("ImageItemList") && type != null) {//image list of user
                            JSONArray data = child.getJSONArray("ImageItemList");
                            long start = System.currentTimeMillis();
                            Object object = new Gson().fromJson(data.toString(), type);
                            MyUtils.howLong(start, "Gson().fromJson ImageItemList");
                            result.setData(object);
                            result.setErrorCode(ReturnResult.SUCCESS);
                            result.setErrorMessage("");

                        } else

                            //image list of user favorite
                            if (!child.isNull("SavedImages") && type != null) {
                                JSONArray data = child.getJSONArray("SavedImages");
                                long start = System.currentTimeMillis();
                                Object object = new Gson().fromJson(data.toString(), type);
                                MyUtils.howLong(start, "Gson().fromJson SavedImages");
                                result.setData(object);
                                result.setErrorCode(ReturnResult.SUCCESS);
                                result.setErrorMessage("");

                            } else if (!child.isNull("AddImageItem") && type != null) {
                                JSONObject data = child.getJSONObject("AddImageItem");
                                long start = System.currentTimeMillis();
                                Object object = new Gson().fromJson(data.toString(), type);
                                MyUtils.howLong(start, "Gson().fromJson Newsfeed");
                                result.setData(object);
                                result.setErrorCode(ReturnResult.SUCCESS);
                                result.setErrorMessage("");

                            } else

                                //Explores
                                if (!child.isNull("Explores") && type != null) {
                                    JSONArray data = child.getJSONArray("Explores");
                                    long start = System.currentTimeMillis();
                                    Object object = new Gson().fromJson(data.toString(), type);
                                    MyUtils.howLong(start, "Gson().fromJson Explores");
                                    result.setData(object);
                                    result.setErrorCode(ReturnResult.SUCCESS);
                                    result.setErrorMessage("");

                                } else

                                    //ImageItemListByHashtag
                                    if (!child.isNull("ImageItemListByHashtag") && type != null) {
                                        JSONArray data = child.getJSONArray("ImageItemListByHashtag");
                                        long start = System.currentTimeMillis();
                                        Object object = new Gson().fromJson(data.toString(), type);
                                        MyUtils.howLong(start, "Gson().fromJson Explores");
                                        result.setData(object);
                                        result.setErrorCode(ReturnResult.SUCCESS);
                                        result.setErrorMessage("");

                                    } else

                                        //Suggest Follow SuggestFollow
                                        if (!child.isNull("SuggestFollow") && type != null) {
                                            JSONArray data = child.getJSONArray("SuggestFollow");
                                            long start = System.currentTimeMillis();
                                            Object object = new Gson().fromJson(data.toString(), type);
                                            MyUtils.howLong(start, "Gson().fromJson Explores");
                                            result.setData(object);
                                            result.setErrorCode(ReturnResult.SUCCESS);
                                            result.setErrorMessage("");

                                        } else

                                            //admin
                                            if (!child.isNull("MostInteractivePosts") && type != null) {
                                                JSONArray data = child.getJSONArray("MostInteractivePosts");
                                                long start = System.currentTimeMillis();
                                                Object object = new Gson().fromJson(data.toString(), type);
                                                MyUtils.howLong(start, "Gson().fromJson Explores");
                                                result.setData(object);
                                                result.setErrorCode(ReturnResult.SUCCESS);
                                                result.setErrorMessage("");

                                            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }
    //PARSE JSON END////////////////////////////////////////////////////////////////////////////////


    //NEWSFEED////////////////////////////////////////////////////////////////////////////////////////////
    //TAB SONTUNG HOAC TAB HOME
    public static Task<Object> getNewsFeedPageFirst(boolean isSonTung) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String query = "";
        if (isSonTung) {
            query = Webservices.GET_NEWSFEED_SONTUNG();
        } else {
            query = Webservices.GET_NEWSFEED();
        }
        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .setPriority(Priority.HIGH)
                .setTag("GET_NEWSFEED")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyUtils.howLong(start, "getNewsFeedPageFirst()");
//                        MyUtils.log("tag1 " + response.toString());

                        ArrayList<ImageItem> list = convertListImage(response);
                        if (list != null && list.size() > 0) {
                            successful.setResult(list);
                        } else {
                            successful.setResult(null);
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        successful.setError(ANError);
                    }
                });

        return successful.getTask();
    }

    public static Task<Object> getHomePageAdmin() {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String query = Webservices.GET_NEWSFEED_ADMIN();
//        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .setPriority(Priority.HIGH)
                .setTag("GET_NEWSFEED_ADMIN")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        MyUtils.howLong(start, "GET_NEWSFEED_ADMIN()");
//                        MyUtils.log("tag2 " + response.toString());

                        ArrayList<ImageItem> list = convertListImage(response);
                        if (list != null && list.size() > 0) {
                            successful.setResult(list);
                        } else {
                            successful.setResult(null);
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        successful.setError(ANError);
                    }
                });

        return successful.getTask();
    }

    private static ArrayList<ImageItem> convertListImage(JSONObject response) {
        Type collectionType = new TypeToken<List<ImageItem>>() {
        }.getType();
        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
        if (result != null) {
            if (result.getData() != null) {
                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                if (list != null && list.size() > 0) {
                    return list;
                } else {
                    return null;
                }
            }
        }
        return null;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    public static String RESHARE_IMAGE_ITEM(long imageItemId, String shareTo) {
        return "mutation{ReshareImageItem(ImageItemId:" + imageItemId + ", ShareTo:\"" + shareTo + "\"){ImageItemId}}";
    }

    public static Task<Object> ReshareImageItem(long imageItemId, String shareTo, int shareCount) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.RESHARE_IMAGE_ITEM(imageItemId, shareTo);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //{"data":{"ReshareImageItem":null}}
                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("ReshareImageItem")) {
                                JSONObject obj = objP.getJSONObject("ReshareImageItem");
                                long imageItemIdReturn = obj.getLong("ImageItemId");
                                if (imageItemIdReturn > 0) {
                                    successful.setResult(shareCount + 1);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(-1);//hinh da bi xoa
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        successful.setError(ANError);

                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_LIST_IMAGE_ITEM_OF_USER(long userId, long lastImageItemId, long lastItemDate) {
        if (lastImageItemId == 0) {//page dau tien
            return "{ImageItemList(UserId:" + userId + ",Count:20)" + feedItem + "}";
        } else {
            return "{ImageItemList(UserId:" + userId + ",Count:20, LastItemId:" + lastImageItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + feedItem + "}";
        }

    }

    public static Task<Object> getListImageItemOfUser(long userId, long lastImageItemId, long lastItemDate) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_LIST_IMAGE_ITEM_OF_USER(userId, lastImageItemId, lastItemDate);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successful.setResult(list);
                                } else {
                                    successful.setResult(null);
                                }
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //mutation{UpdateUserAvatar(ItemGUID:"0a4e0fe9-d08d-4efe-a422-9474cee9f56f",ImageExtension:"jpg"){UserId}}
    public static String UPDATE_USER_AVATAR(String guiId, String extension) {
        return "mutation{UpdateUserAvatar(ItemGUID:\"" + guiId + "\",ImageExtension:\"" + extension + "\"){UserId}}";
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> checkUserName(String userName, final Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking Account...");
        progressDialog.show();*/
        ProgressUtils.show(context);
        AndroidNetworking.post(Webservices.URL + "user/checkUserNameExists")
                .addQueryParameter("UserName", userName)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ReturnResult result = Webservices.parseJson(response, Boolean.class, false);
                        ProgressUtils.hide();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                boolean isExist = (boolean) result.getData();
                                if (isExist) {
//                                    MyUtils.showToast(context, R.string.user_name_exist);
                                    successful.setResult(true);
                                } else {
                                    successful.setResult(false);
                                }

                            } else {
                                successful.setResult(null);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        ProgressUtils.hide();
                        successful.setError(ANError);
                    }
                });

        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> checkPhoneExists(String phone, final Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking Account...");
        progressDialog.show();*/
        ProgressUtils.show(context);
        AndroidNetworking.post(Webservices.URL + "user/checkPhoneExists")
                .addQueryParameter("Phone", phone)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ReturnResult result = Webservices.parseJson(response, Boolean.class, false);
                        ProgressUtils.hide();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                boolean isExist = (boolean) result.getData();
                                if (isExist) {
//                                    MyUtils.showToast(context, R.string.phone_exists);
                                    successful.setResult(true);
                                } else {
                                    successful.setResult(false);
                                }

                            } else {
                                successful.setResult(null);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        ProgressUtils.hide();
                        successful.setError(ANError);
                    }
                });

        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> checkEmailExist(String email, final Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking Account...");
        progressDialog.show();*/
        ProgressUtils.show(context);
        AndroidNetworking.post(Webservices.URL + "user/checkEmailExists")
                .addQueryParameter("Email", email)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ReturnResult result = Webservices.parseJson(response, Boolean.class, false);
                        ProgressUtils.hide();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                boolean isExist = (boolean) result.getData();
                                if (isExist) {
                                    MyUtils.showToast(context, R.string.user_email_exist);
                                    successful.setResult(true);
                                } else {
                                    successful.setResult(false);
                                }

                            } else {
                                successful.setResult(null);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        ProgressUtils.hide();
                        successful.setError(ANError);
                    }
                });

        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String UPDATE_USER_PROFILE(String fullName, String userName, String email, String phone, int gender, String social, String bios) {
        //cai nao ko co thi ko truyen
        String value = "";
        if (!TextUtils.isEmpty(userName)) {
            value += ",UserName:\"" + userName + "\"";
        }

        if (!TextUtils.isEmpty(email)) {
            value += ",Email:\"" + email + "\"";
        }

        if (!TextUtils.isEmpty(phone)) {
            value += ",Phone:\"" + phone + "\"";
        }

        value += ",Gender:" + gender;

        value += "," + social;

        if (!TextUtils.isEmpty(bios)) {
            //thay the \n thanh <br>
            if (bios.contains("\n")) {
                bios = MyUtils.replaceDescriptionForServer(bios);
            }
            value += ",Favorite:\"" + bios + "\"";
        }

        return "mutation{UpdateUserProfile(FullName:\"" + fullName + "\"" + value + "){UserId}}";
    }

    public static Task<Object> updateUserProfile(String fullName, String userName, String email, String phone, int gender, String social, String bios) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.UPDATE_USER_PROFILE(fullName, userName, email, phone, gender, social, bios);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("UpdateUserProfile")) {
                                JSONObject obj = objP.getJSONObject("UpdateUserProfile");

                                long UserId = obj.getLong("UserId");
                                if (UserId > 0) {
                                    successful.setResult(true);
                                } else {
                                    successful.setResult(false);
                                }
                            } else {

                                if (!response.isNull("errors")) {
                                    JSONArray array = response.getJSONArray("errors");
                                    if (array != null && array.length() > 0) {
                                        JSONObject itemFirst = array.getJSONObject(0);
                                        String message = itemFirst.getString("message");
                                        successful.setError(new ANError(message));
                                    }
                                } else {
                                    successful.setResult(false);
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setError(e);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }


    public static String UPDATE_USER_PROFILE2(String birthday, int gender, String maritalStatus) {
        //cai nao ko co thi ko truyen
        String value = "";
        if (!TextUtils.isEmpty(birthday)) {
            value += "Birthday:\"" + birthday + "\"";
        }

        value += ",Gender:" + gender;

        value += ",MaritalStatus:\"" + maritalStatus + "\"";

        return "mutation{UpdateUserProfile(" + value + "){UserId}}";
    }

    public static Task<Object> updateUserProfile2(String birthday, int gender, String maritalStatus) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.UPDATE_USER_PROFILE2(birthday, gender, maritalStatus);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject objP = response.getJSONObject("data");
                            JSONObject obj = objP.getJSONObject("UpdateUserProfile");

                            long UserId = obj.getLong("UserId");
                            if (UserId > 0) {
                                successful.setResult(true);
                            } else {
                                successful.setResult(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setError(e);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    private static final String userItem = "{ UserId, ChatUserId, Email, FullName, UserName, IsVip, AvatarSmall, AvatarMedium, AvatarOriginal, ImageCount, FollowingCount, FollowerCount, VipLevelId, VipLevel, GroupCount, Favorite}";
    private static final String userItemInfoFollow = "{UserId, FollowingCount, FollowerCount, GroupCount}";

    public static String GET_USER(String userName) {
        return "{User(UserName:\"" + userName + "\")" + userItem + "}";
    }

    public static Task<Object> getUser(String userName) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_USER(userName);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("User")) {
                                JSONObject obj = objP.getJSONObject("User");

                                Object object = new Gson().fromJson(obj.toString(), User.class);
                                long UserId = obj.getLong("UserId");
                                if (UserId > 0) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_USER(long userId) {
        return "{User(Id:" + userId + ")" + userItem + "}";
    }

    public static Task<Object> getUser(long userId) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_USER(userId);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("User")) {
                                JSONObject obj = objP.getJSONObject("User");

                                Object object = new Gson().fromJson(obj.toString(), User.class);
                                long UserId = obj.getLong("UserId");
                                if (UserId > 0) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_USER_INFO_FOLLOW(long userId) {
        return "{User(Id:" + userId + ")" + userItemInfoFollow + "}";
    }

    //Chi lay thong tin so nguoi theo doi, va nguoi dang theo doi ban
    public static Task<Object> getUserInfoFollow(long userId) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_USER_INFO_FOLLOW(userId);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("User")) {
                                JSONObject obj = objP.getJSONObject("User");

                                Object object = new Gson().fromJson(obj.toString(), User.class);
                                long UserId = obj.getLong("UserId");
                                if (UserId > 0) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private static String listUserLike = "{ItemId, User{UserName, AvatarSmall}, Comment, CreateDate}";

    public static String GET_LIST_USER_LIKE(long imageItemId, long lastItemId, long lastItemDate) {

        if (lastItemId == 0) {
            return "{ImageComment(Id:" + imageItemId + "){Likes(Count:30)" + listUserLike + "}}";
        } else {
            return "{ImageComment(Id:" + imageItemId + "){Likes(Count:30, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + listUserLike + "}}";
        }

    }


    /**
     * Tuong tu lay so luong nguoi comment va share
     */
    public static Task<Object> getListUserLike(long imageItemId, long lastItemId, long lastItemDate) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_LIST_USER_LIKE(imageItemId, lastItemId, lastItemDate);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("User")) {
                                JSONObject obj = objP.getJSONObject("User");

                                Object object = null;
                                if (!obj.isNull("Followings")) {
                                    JSONArray array = obj.getJSONArray("Followings");

                                    Type type = new TypeToken<ArrayList<User>>() {
                                    }.getType();
                                    object = new Gson().fromJson(array.toString(), type);
                                }

                                if (object != null) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String UPDATE_USER_FOLLOWING_STATUS(long userIdFollowing, boolean isFollow) {
        if (isFollow) {
            return "mutation{FollowUser(UserId:" + userIdFollowing + "){UserId}}";
        } else {
            return "mutation{UnfollowerUser(UserId:" + userIdFollowing + "){UserId}}";
        }

    }

    public static Task<Object> updateUserFollowingState(long userIdFollowing, boolean isFollow) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.UPDATE_USER_FOLLOWING_STATUS(userIdFollowing, isFollow);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");

                            JSONObject obj = null;

                            if (!objP.isNull("FollowUser")) {
                                obj = objP.getJSONObject("FollowUser");
                            } else if (!objP.isNull("UnfollowerUser")) {
                                obj = objP.getJSONObject("UnfollowerUser");
                            }


                            //tra ve chinh minh
                            long UserId = obj.getLong("UserId");
                            if (UserId > 0) {

                                //luu hoac xoa khoi danh sach following
                                if (isFollow) {
                                    MyUtils.saveUserFollowing(userIdFollowing);
                                } else {
                                    MyUtils.deleteUserFollowing(userIdFollowing);
                                }

                                //cap nhat man hinh UserProfile
                                MyApplication app = MyApplication.getInstance();
                                if (app != null) {
                                    app.sendBroadcastRereshNumberFollow();
                                }

                                successful.setResult(true);
                            } else {
                                successful.setResult(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(false);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_LIKES(long imageItemId, long lastItemId, long lastItemDate) {
        if (lastItemId == 0) {
            return "{ImageItem(Id:" + imageItemId + "){Likes(Count:30){ItemId, User{UserId, UserName, FullName, AvatarSmall}, CreateDate}}}";
        } else {
            return "{ImageItem(Id:" + imageItemId + "){Likes(Count:30, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + "){ItemId, User{UserId, UserName, FullName, AvatarSmall}, CreateDate}}}";
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_SHARES(long imageItemId, long lastItemId, long lastItemDate) {
        if (lastItemId == 0) {
            return "{ImageComment(Id:" + imageItemId + "){Shares(Count:30){ItemId, User{UserId, UserName, FullName, AvatarSmall}, CreateDate}}}";
        } else {
            return "{ImageComment(Id:" + imageItemId + "){Shares(Count:30, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + "){ItemId, User{UserId, UserName, FullName, AvatarSmall}, CreateDate}}}";
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_LIST_IMAGE_ITEM_EXPLORER(long lastImageItemId, long lastItemDate) {
        if (lastImageItemId == 0) {//page dau tien
            return "{Explores(Count:25)" + feedItem + "}";
        } else {
            return "{Explores(Count:25, LastItemId:" + lastImageItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + feedItem + "}";
        }

    }

    public static Task<Object> getListImageItemExplorer(long lastImageItemId, long lastItemDate) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_LIST_IMAGE_ITEM_EXPLORER(lastImageItemId, lastItemDate);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successful.setResult(list);
                                } else {
                                    successful.setResult(null);
                                }
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private static String ROW_COUNT = "30";

    public static Task<Object> searchTop(String keyword, Context context, String methodName) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.searching));
        progressDialog.show();*/

        AndroidNetworking.post(Webservices.URL + methodName)
                .addQueryParameter("Keyword", keyword)
                .addQueryParameter("RowCount", ROW_COUNT)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Type collectionType = new TypeToken<List<SearchObject>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJson(response, collectionType, true);
//                        progressDialog.dismiss();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);

                            } else {
                                successful.setResult(null);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        progressDialog.dismiss();
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_LIST_IMAGE_ITEM_BY_HASHTAG(String hashtag, long lastImageItemId, long lastItemDate) {
        if (lastImageItemId == 0) {//page dau tien
            return "{ImageItemListByHashtag(Hashtag:\"" + hashtag + "\",Count:20)" + feedItem + "}";
        } else {
            return "{ImageItemListByHashtag(Hashtag:\"" + hashtag + "\",Count:20, LastItemId:" + lastImageItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + feedItem + "}";
        }

    }

    public static Task<Object> getListImageItemByHashTag(String hashtag, long lastImageItemId, long lastItemDate) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_LIST_IMAGE_ITEM_BY_HASHTAG(hashtag, lastImageItemId, lastItemDate);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successful.setResult(list);
                                } else {
                                    successful.setResult(null);
                                }
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> getUserNotify(int isViewed, long lastId) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.searching));
        progressDialog.show();*/

        AndroidNetworking.post(Webservices.URL + "user/getUserNotify")
                .addQueryParameter("IsViewed", String.valueOf(isViewed))
                .addQueryParameter("LastId", String.valueOf(lastId))
                .setOkHttpClient(MyApplication.getClient())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        MyUtils.log("Fragment_3_Notify: getUserNotify JSONObject=" + response.toString());
                        Type collectionType = new TypeToken<List<MyNotify>>() {}.getType();
                        ReturnResult result = Webservices.parseJson(response, collectionType, true);
//                        progressDialog.dismiss();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);

                            } else {
                                successful.setResult(null);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log("Fragment_3_Notify: getUserNotify ErrorCode=" + ANError.getErrorCode() + ",ErrorMessage=" + ANError.getMessage());
//                        progressDialog.dismiss();
                        MyUtils.log("Fragment_3_Notify: onError setError");
                        successful.setError(ANError);//RETURN
//                        MyUtils.log("Fragment_3_Notify: onError setResult");
//                        successful.setResult(null);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_IMAGE_ITEM(long imageItemId) {
        return "{ImageItem(Id:" + imageItemId + ")" + feedItem + "}";
    }


    public static Task<Object> getImageItem(long imageItemId) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_IMAGE_ITEM(imageItemId);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

//                        MyUtils.log("getImageItem " + response.toString());
                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("ImageItem")) {
                                JSONObject obj = objP.getJSONObject("ImageItem");

                                Object object = new Gson().fromJson(obj.toString(), ImageItem.class);
                                long id = obj.getLong("ImageItemId");
                                if (id > 0) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log("getImageItem " + ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {

                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    //{"ErrorCode":0,"Message":"","Data":{"Success":true}}
    public static Task<Object> addUserEndpoint(Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();
        if (context != null) {
            AndroidNetworking.post(Webservices.URL + "user/addUserEndpoint")
                    .addQueryParameter("DeviceId", MyUtils.getDeviceId(context))
                    .addQueryParameter("OnesignalId", MyApplication.getInstance().getOneSignalId())
                    .addQueryParameter("OnesignalAppId", context.getString(R.string.one_signal_app_id))
                    .addQueryParameter("LanguageCode", languageId)
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject obj) {
                            try {

                                ReturnResult result = new ReturnResult();
                                if (!obj.isNull("ErrorCode")) {
                                    int errorCode = obj.getInt("ErrorCode");
                                    result.setErrorCode(errorCode);
                                }

                                if (!obj.isNull("Message")) {
                                    String errorMessage = obj.getString("Message");
                                    result.setErrorMessage(errorMessage);
                                }
                                if (!obj.isNull("Data")) {
                                    JSONObject objData = obj.getJSONObject("Data");
                                    boolean data = objData.getBoolean("Success");
                                    result.setData(data);
                                }

                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    successful.setResult(true);
                                } else {
                                    successful.setResult(null);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                successful.setResult(null);
                            }
                        }

                        @Override
                        public void onError(ANError ANError) {
//                        progressDialog.dismiss();
                            successful.setError(ANError);
                        }
                    });
        }
        return successful.getTask();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    //{"ErrorCode":0,"Message":"","Data":{"Success":true}}
    public static Task<Object> deleteUserEndpoint(Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();
        if (context != null) {
            AndroidNetworking.post(Webservices.URL + "user/deleteUserEndpoint")
                    .addQueryParameter("DeviceId", MyUtils.getDeviceId(context))
                    .addQueryParameter("OnesignalId", MyApplication.getInstance().getOneSignalId())
                    .addQueryParameter("OnesignalAppId", context.getString(R.string.one_signal_app_id))
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject obj) {
                            try {

                                ReturnResult result = new ReturnResult();
                                if (!obj.isNull("ErrorCode")) {
                                    int errorCode = obj.getInt("ErrorCode");
                                    result.setErrorCode(errorCode);
                                }

                                if (!obj.isNull("Message")) {
                                    String errorMessage = obj.getString("Message");
                                    result.setErrorMessage(errorMessage);
                                }
                                if (!obj.isNull("Data")) {
                                    JSONObject objData = obj.getJSONObject("Data");
                                    boolean data = objData.getBoolean("Success");
                                    result.setData(data);
                                }

                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    successful.setResult(true);
                                } else {
                                    successful.setResult(null);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                successful.setResult(null);
                            }
                        }

                        @Override
                        public void onError(ANError ANError) {
//                        progressDialog.dismiss();
                            successful.setError(ANError);
                        }
                    });
        }
        return successful.getTask();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static final int SUGGEST_ITEMS = 30;

    public static String GET_SUGGEST_FOLLOW(boolean isRelative) {
        return "{SuggestFollow(Count:" + SUGGEST_ITEMS + ", IsFacebook:false, IsRelative:" + isRelative + ")" + USER_SUGGEST_HAVE_IMAGES + "}";
    }

    /**
     * @param isRelative:true (người có cùng sở thích), false: người bình thường
     * @return
     */
    public static Task<Object> getSuggestFollow(boolean isRelative) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_SUGGEST_FOLLOW(isRelative);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Type collectionType = new TypeToken<List<User>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<User> list = (ArrayList<User>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successful.setResult(list);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> getNewFeedMore(long imageItemId, long createDate, boolean isSonTung) {
        final TaskCompletionSource<Object> successfull = new TaskCompletionSource<>();

        final long start = System.currentTimeMillis();
        String query = GET_NEWSFEED_MORE(imageItemId, createDate);
        if (isSonTung) {
            query = GET_NEWSFEED_MORE_SONTUNG(imageItemId, createDate);
        }
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyUtils.howLong(start, "GET_NEWSFEED_MORE");

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successfull.setResult(list);
                                } else {
                                    successfull.setResult(null);
                                }
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successfull.setError(ANError);
                    }
                });

        return successfull.getTask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> getNewFeedSonTungNewPost(long imageItemId) {
        final TaskCompletionSource<Object> successfull = new TaskCompletionSource<>();

        final long start = System.currentTimeMillis();
        String query = GET_NEWSFEED_SONTUNG_NEW_POST(imageItemId);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyUtils.howLong(start, "GET_NEWSFEED_SONTUNG_NEW_POST");

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successfull.setResult(list);
                                } else {
                                    successfull.setResult(null);
                                }
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successfull.setError(ANError);
                    }
                });

        return successfull.getTask();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    private static String listUserFollowing = "{ UserId, Followings(Count:10000){UserId, UserName, AvatarSmall}}";

    public static String GET_LIST_USER_FOLLOWING(long userId) {
        return "{User(Id:" + userId + ")" + listUserFollowing + "}";
    }

    /**
     * Chay khi login thanh cong (SiginActivity), va man hinh LoadingActivity
     * return ArrayList<User> following, chi lay UserId, UserName, AvatarSmall cho nhanh
     *
     * @return
     */
    /*public static Task<Object> getListUserFollowing(long userId) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_LIST_USER_FOLLOWING(userId);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("User")) {
                                JSONObject obj = objP.getJSONObject("User");

                                Object object = null;
                                if (!obj.isNull("Followings")) {
                                    JSONArray array = obj.getJSONArray("Followings");

                                    Type type = new TypeToken<ArrayList<User>>() {
                                    }.getType();
                                    object = new Gson().fromJson(array.toString(), type);
                                }

                                if (object != null) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }*/
    public static Task<Object> getListUserFollowing() {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        AndroidNetworking.post(Webservices.URL + "user/following/getIdList")
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            com.topceo.ads.ReturnResult result = new Gson().fromJson(response.toString(), com.topceo.ads.ReturnResult.class);
                            successful.setResult(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static final int FOLLOW_COUNT_ITEM = 20;

    public static String GET_USER_FOLLOWER(long userId, long lastItemId, long lastItemDate, int count) {
        if (lastItemId == 0) {
            return "{User(Id:" + userId + "){Followers(Count:20)" + USER_SUGGEST + "}}";
        } else {
            return "{User(Id:" + userId + "){Followers(Count:20, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + USER_SUGGEST + "}}";
        }

    }

    public static Task<Object> getListUserFollower(long userId, long lastItemId, long lastItemDate, int count) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_USER_FOLLOWER(userId, lastItemId, lastItemDate, count);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("User")) {
                                JSONObject obj = objP.getJSONObject("User");

                                Object object = null;
                                if (!obj.isNull("Followers")) {
                                    JSONArray array = obj.getJSONArray("Followers");

                                    Type type = new TypeToken<ArrayList<User>>() {
                                    }.getType();
                                    object = new Gson().fromJson(array.toString(), type);
                                }

                                if (object != null) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static final String USER_SUGGEST = "{UserId, UserName, FullName, AvatarSmall,CreateDate, ChatUserId}";
    public static final String USER_SUGGEST_HAVE_IMAGES = "{UserId, UserName, FullName, AvatarSmall,CreateDate, ChatUserId, ImageItems(Count:3){ImageItemId, ItemContent{ItemType, Small{Link}}}}";


    public static String GET_USER_FOLLOWING(long userId, long lastItemId, long lastItemDate) {
        if (lastItemId == 0) {
            return "{User(Id:" + userId + "){Followings(Count:20)" + USER_SUGGEST + "}}";
        } else {
            return "{User(Id:" + userId + "){Followings(Count:20, LastItemId:" + lastItemId + ", LastItemDate:" + getLastItemDate(lastItemDate) + ")" + USER_SUGGEST + "}}";
        }

    }

    /**
     * Ham nay de load day du, ham kia de luu xet co dang follow hay khong
     * //todo Tuong lai can doi ve ham nay va luu danh sach following vao realm thi moi day du, hiem tai chi lay 1000 user count
     *
     * @param userId
     * @param lastItemId
     * @param lastItemDate
     * @return
     */
    public static Task<Object> getListUserFollowing(long userId, long lastItemId, long lastItemDate) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_USER_FOLLOWING(userId, lastItemId, lastItemDate);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("User")) {
                                JSONObject obj = objP.getJSONObject("User");

                                Object object = null;
                                if (!obj.isNull("Followings")) {
                                    JSONArray array = obj.getJSONArray("Followings");

                                    Type type = new TypeToken<ArrayList<User>>() {
                                    }.getType();
                                    object = new Gson().fromJson(array.toString(), type);
                                }

                                if (object != null) {
                                    successful.setResult(object);
                                } else {
                                    successful.setResult(null);
                                }
                            } else {
                                successful.setResult(null);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            successful.setResult(null);
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> forgetPassword(String email, Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.pleasewait));
        progressDialog.show();*/
        ProgressUtils.show(context);

        AndroidNetworking.post(Webservices.URL + "user/forgetPassword")
                .addQueryParameter("Email", String.valueOf(email))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressUtils.hide();
                        ReturnResult result = Webservices.parseJson(response, ReturnResult.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);

                            } else {
                                successful.setResult(result);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        MyUtils.log("Fragment_3_Notify: getUserNotify ErrorCode=" + ANError.getErrorCode() + ",ErrorMessage=" + ANError.getMessage());
                        ProgressUtils.hide();
//                        MyUtils.log("Fragment_3_Notify: onError setError");
                        successful.setError(ANError);//RETURN
//                        MyUtils.log("Fragment_3_Notify: onError setResult");
//                        successful.setResult(null);
                    }
                });
        return successful.getTask();
    }


    @Deprecated
    public static Task<Object> setPassword(Context context, String phone, String newPassword) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.pleasewait));
        progressDialog.show();*/
        ProgressUtils.show(context);

        AndroidNetworking.post(Webservices.URL + "user/setPassword")
                .addBodyParameter("Phone", String.valueOf(phone))
                .addBodyParameter("Password", String.valueOf(newPassword))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressUtils.hide();
                        ReturnResult result = Webservices.parseJson(response, ReturnResult.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);
                            } else {
                                successful.setResult(result);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log("Fragment_3_Notify: getUserNotify ErrorCode=" + ANError.getErrorCode() + ",ErrorMessage=" + ANError.getMessage());
                        ProgressUtils.hide();
                        MyUtils.log("Fragment_3_Notify: onError setError");
                        successful.setError(ANError);//RETURN
//                        MyUtils.log("Fragment_3_Notify: onError setResult");
//                        successful.setResult(null);
                    }
                });
        return successful.getTask();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> sendComment(long imageItemId, String comment, long replyToId) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", Webservices.SEND_COMMENT(imageItemId, comment, replyToId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //mutation{AddImageComment(ImageItemId:x, Comment: "xxx"){ImageItemId, ItemId, ReplyToId}
                        try {
                            JSONObject objP = response.getJSONObject("data");
                            JSONObject obj = objP.getJSONObject("AddImageComment");

                            //chi tra ve thong tin co ban {ImageItemId, ItemId, ReplyToId}
                            ImageComment image = new Gson().fromJson(obj.toString(), ImageComment.class);

                            if (image != null) {

                                //#1 bo sung comment
                                image.setComment(comment);
                                image.setCreateDate(System.currentTimeMillis() / 1000);
                                //#2 bo sung thong tin user truoc khi tra ve
                                UserShort userShort = MyApplication.getUserShort();
                                if (userShort != null && userShort.getUserId() > 0) {
                                    image.setUser(userShort);
                                    successful.setResult(image);
                                } else {
                                    successful.setResult(null);
                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {

                        }
                        successful.setError(ANError);
                    }
                });

        return successful.getTask();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> getComments(final long imageItemId, final long replyToId, final long lastItemId, final long lastCreateDate) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String query = "";
        if (replyToId == 0) {//parent
            query = Webservices.GET_COMMENTS(imageItemId, lastItemId, lastCreateDate);
        } else {
            query = Webservices.GET_COMMENTS_CHILD(imageItemId, replyToId, lastItemId, lastCreateDate);
        }
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject obj = response.getJSONObject("data");
                            JSONArray arr = obj.getJSONArray("Comments");

                            Type type = new TypeToken<List<ImageComment>>() {
                            }.getType();
                            ArrayList<ImageComment> comments = new Gson().fromJson(arr.toString(), type);

                            if (comments.size() > 0) {
                                successful.setResult(comments);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {

                        }
                        successful.setError(ANError);
                    }
                })
        ;

        return successful.getTask();
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> deleteComment(final long commentId) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String query = Webservices.DELETE_COMMENT(commentId);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //{"data":{"DeleteImageComment":{"ImageItemId":391}}}
                            JSONObject obj = response.getJSONObject("data");
                            if (obj != null) {
                                successful.setResult(true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {

                        }
                        successful.setError(ANError);
                    }
                })
        ;

        return successful.getTask();
    }
    /////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String UPDATE_USER_PASSWORD(String newPassword) {
        return "mutation{FollowUser(Password:" + newPassword + "){UserId}}";

    }

    public static Task<Object> updateUserPassword(String newPassword) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.UPDATE_USER_PASSWORD(newPassword);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");

                            JSONObject obj = null;

                            if (!objP.isNull("FollowUser")) {
                                obj = objP.getJSONObject("FollowUser");
                            } else if (!objP.isNull("UnfollowerUser")) {
                                obj = objP.getJSONObject("UnfollowerUser");
                            }


                            long UserId = obj.getLong("UserId");
                            if (UserId > 0) {
                                successful.setResult(true);
                            } else {
                                successful.setResult(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }


    public static String GET_USER_SOCIAL(long UserId) {
        return "{User(Id:" + UserId + "){UserId, UserName, SocialInfo{NameCode, Link}}}";

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static String GET_SAVED_IMAGES(long userId, long savedDate) {
        //USERID neu ko truyen thi la user hien tai
        String userIdParam = "";
        if (userId > 0) {
            userIdParam = "UserId:" + userId + ",";
        } else {
            userIdParam = "";
        }

        if (savedDate == 0) {//page dau tien
            return "{SavedImages(" + userIdParam + "Count:20, CollectionId:0)" + feedItem + "}";
        } else {
            return "{SavedImages(" + userIdParam + "Count:20, LastItemDate:" + getLastItemDate(savedDate) + ", CollectionId:0)" + feedItem + "}";
        }

    }

    public static Task<Object> getSavedImages(long userId, long savedDate) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.GET_SAVED_IMAGES(userId, savedDate);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                successful.setResult(list);
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String HASHTAG_FOLLOW(ArrayList<Hashtag> tags, boolean isFollow) {
        String hashtags = "";
        if (isFollow) {
            return "mutation{FollowHashtags(Hashtags:" + hashtags + "){UserId}}";
        } else {
            return "mutation{UnfollowHashtags(Hashtags:" + hashtags + "){UserId}}";
        }

    }

    public static Task<Object> hashtagFollow(ArrayList<Hashtag> tags, boolean isFollow) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        String queryString = Webservices.HASHTAG_FOLLOW(tags, isFollow);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", queryString)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONObject objP = response.getJSONObject("data");

                            JSONObject obj = null;

                            if (!objP.isNull("FollowUser")) {
                                obj = objP.getJSONObject("FollowUser");
                            } else if (!objP.isNull("UnfollowerUser")) {
                                obj = objP.getJSONObject("UnfollowerUser");
                            }


                            long UserId = obj.getLong("UserId");
                            if (UserId > 0) {
                                successful.setResult(true);
                            } else {
                                successful.setResult(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {

                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successful.setError(ANError);
                    }
                });
        return successful.getTask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> resetPassword(String username, Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.pleasewait));
        progressDialog.show();*/
        ProgressUtils.show(context);

        AndroidNetworking.post(Webservices.URL + "user/sendResetPassword")
                .addBodyParameter("UserName", username)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressUtils.hide();
                        ReturnResult result = Webservices.parseJson(response, ReturnResult.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);

                            } else {
                                successful.setResult(result);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        MyUtils.log("Fragment_3_Notify: getUserNotify ErrorCode=" + ANError.getErrorCode() + ",ErrorMessage=" + ANError.getMessage());
                        ProgressUtils.hide();
//                        MyUtils.log("Fragment_3_Notify: onError setError");
                        successful.setError(ANError);//RETURN
//                        MyUtils.log("Fragment_3_Notify: onError setResult");
//                        successful.setResult(null);
                    }
                });
        return successful.getTask();
    }

    public static Task<Object> verifyEmail(String email, Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.pleasewait));
        progressDialog.show();*/
        ProgressUtils.show(context);

        AndroidNetworking.post(Webservices.URL + "user/verification/sendEmail")
//                .addBodyParameter("Email", email)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressUtils.hide();
                        ReturnResult result = Webservices.parseJson(response, ReturnResult.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);

                            } else {
                                successful.setResult(result);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        MyUtils.log("Fragment_3_Notify: getUserNotify ErrorCode=" + ANError.getErrorCode() + ",ErrorMessage=" + ANError.getMessage());
                        ProgressUtils.hide();
//                        MyUtils.log("Fragment_3_Notify: onError setError");
                        successful.setError(ANError);//RETURN
//                        MyUtils.log("Fragment_3_Notify: onError setResult");
//                        successful.setResult(null);
                    }
                });
        return successful.getTask();
    }

    public static Task<Object> getNotifyNumber(Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.pleasewait));
        progressDialog.show();*/

        AndroidNetworking.post(Webservices.URL + "user/getUserNotifyCount")
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        progressDialog.dismiss();
                        ReturnResult result = Webservices.parseJson(response, String.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);

                            } else {
                                successful.setResult(result);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        MyUtils.log("Fragment_3_Notify: getUserNotify ErrorCode=" + ANError.getErrorCode() + ",ErrorMessage=" + ANError.getMessage());
//                        progressDialog.dismiss();
//                        MyUtils.log("Fragment_3_Notify: onError setError");
                        successful.setError(ANError);//RETURN
//                        MyUtils.log("Fragment_3_Notify: onError setResult");
//                        successful.setResult(null);
                    }
                });
        return successful.getTask();
    }


    public static Task<Object> verifyPhone(String token, final Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.show();*/
        ProgressUtils.show(context);
        AndroidNetworking.post(Webservices.URL + "user/verification/verifyPhone")
                .addBodyParameter("AuthToken", token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ReturnResult result = Webservices.parseJson(response, Boolean.class, false);
                        ProgressUtils.hide();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                successful.setResult(true);
                            } else {
                                successful.setResult(false);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        ProgressUtils.hide();
                        successful.setError(ANError);
                    }
                });

        return successful.getTask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> getNewFeedSonTungUpdateFirst() {
        final TaskCompletionSource<Object> successfull = new TaskCompletionSource<>();
        final long start = System.currentTimeMillis();
        String query = GET_NEWSFEED_SONTUNG_UPDATE_FIRST();
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        MyUtils.howLong(start, "GET_NEWSFEED_SONTUNG_NEW_POST");

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successfull.setResult(list);
                                } else {
                                    successfull.setResult(null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successfull.setError(ANError);
                    }
                });

        return successfull.getTask();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> getNewFeedSonTungUpdateMore(long imageItemId, int count) {
        final TaskCompletionSource<Object> successfull = new TaskCompletionSource<>();

        final long start = System.currentTimeMillis();
        String query = GET_NEWSFEED_SONTUNG_UPDATE_MORE(imageItemId, count);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        MyUtils.howLong(start, "GET_NEWSFEED_SONTUNG_NEW_POST");

                        Type collectionType = new TypeToken<List<ImageItem>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successfull.setResult(list);
                                } else {
                                    successfull.setResult(null);
                                }
                            }
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successfull.setError(ANError);
                    }
                });

        return successfull.getTask();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> searchChatContact(String keyword, Context context) {
        final TaskCompletionSource<Object> successful = new TaskCompletionSource<>();
        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.pleasewait));
        progressDialog.show();*/
        AndroidNetworking.post(Webservices.URL + "search/chatContact")
                .addBodyParameter("Keyword", keyword)
                .addBodyParameter("RowCount", String.valueOf(10))
                .addBodyParameter("ByFollow", String.valueOf(0))
                .addBodyParameter("ByGroup", String.valueOf(0))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        progressDialog.dismiss();
                        Type type = new TypeToken<ArrayList<UserSearchChat>>() {
                        }.getType();
                        ReturnResult result = Webservices.parseJson(response, type, true);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                successful.setResult(result);

                            } else {
                                successful.setResult(result);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        MyUtils.log("Fragment_3_Notify: getUserNotify ErrorCode=" + ANError.getErrorCode() + ",ErrorMessage=" + ANError.getMessage());
//                        progressDialog.dismiss();
//                        MyUtils.log("Fragment_3_Notify: onError setError");
                        successful.setError(ANError);//RETURN
//                        MyUtils.log("Fragment_3_Notify: onError setResult");
//                        successful.setResult(null);
                    }
                });
        return successful.getTask();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
    public static Task<Object> getFeedGroup(long groupId, long lastItemId) {
        final TaskCompletionSource<Object> successfull = new TaskCompletionSource<>();

        final long start = System.currentTimeMillis();
        String query = GET_FEED_OF_GROUP(groupId, lastItemId);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyUtils.howLong(start, "GET_NEWSFEED_MORE");

                        Type collectionType = new TypeToken<List<ImageItem>>() {}.getType();
                        ReturnResult result = Webservices.parseJsonGraphQL(response, collectionType);
                        if (result != null) {
                            if (result.getData() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) result.getData();
                                if (list != null && list.size() > 0) {
                                    successfull.setResult(list);
                                } else {
                                    successfull.setResult(null);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                        successfull.setError(ANError);
                    }
                });

        return successfull.getTask();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
