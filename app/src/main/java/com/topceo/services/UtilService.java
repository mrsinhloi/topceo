package com.topceo.services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.utils.MyUtils;

import org.json.JSONObject;

public class UtilService {

    public static void updateLikeState(Context context, long imageItemId, boolean isLiked) {
        if(context!=null){
            AndroidNetworking.post(Webservices.URL_GRAPHQL)
                    .addQueryParameter("query", Webservices.UPDATE_IMAGE_ITEM_LIKED(imageItemId, isLiked))
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONObject objP = response.getJSONObject("data");
                                if(!objP.isNull("UpdateImageItemLiked")) {
                                    JSONObject obj = objP.getJSONObject("UpdateImageItemLiked");

                                /*double imageItemId = obj.getDouble("ImageItemId");
                                int likeCount = obj.getInt("LikeCount");

                                if (imageItemId > 0) {
//                                MyUtils.showToast(context, R.string.update_success);
                                    //update lai item dang chon
                                    mAdapter.updateItemLikeCount(likeCount, position);
                                }*/
                                }else{

                                    //update ui cac man hinh home, profile
                                    MyUtils.afterDeletePost(context, imageItemId);
                                    //null tuc la hinh da bi xoa, xoa trong list
                                    new MaterialDialog.Builder(context)
                                            .content(R.string.post_is_deleted)
                                            .positiveText(R.string.ok)
                                            .cancelable(false)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();

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
                        }
                    })
            ;
        }
    }


    public static void setLikeComment(long commentId, boolean isLiked) {
        String service = isLiked ? "image/comment/like" : "image/comment/unlike";
        AndroidNetworking.post(Webservices.API_URL + service)
                .addQueryParameter("CommentId", String.valueOf(commentId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, Boolean.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao

                            } else {

                            }
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                    }
                });
    }



}
