package com.topceo.objects.promotion;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.config.MyApplication;
import com.topceo.activity.MH08_SuggestActivity;
import com.topceo.fragments.FragmentPosition;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.Media;
import com.topceo.shopping.PaymentActivity;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionScreen {

    //"Màn hình show Promotion, theo từ khoá sau:
    public static final String STARTAPP = "STARTAPP";//sau khi start app
    public static final String HOME = "HOME";//màn hình chính
    public static final String MTP = "MTP";//màn hình feature MTP
    public static final String SHOP = "SHOP";//màn hình shop
    public static final String PROFILE = "PROFILE";//màn hình account
    public static final String DISCOVER = "DISCOVER";//màn hình discover"
    public static final String MEDIA = "MEDIA";//Man hinh chi tiet media
    public static final String PAY = "PAY";//skysocial://promotion?screen=pay&type=vip

    public static void navigationScreen(Context context, String currentScreen) {
        ArrayList<Promotion> list = MyApplication.getPromotions();
        if (list != null && list.size() > 0 && context != null && !TextUtils.isEmpty(currentScreen)) {
            for (int i = 0; i < list.size(); i++) {
                Promotion p = list.get(i);
                String screen = p.getScreen();
                if (screen.equalsIgnoreCase(currentScreen)) {
                    showDialog(p, context);
                    //show roi thi bo ra
                    list.remove(i);
                    break;
                }
            }

        }
    }


    public static void showDialog(Promotion p, Context context) {
        if (p != null && context != null) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.dialog_promotion);

            ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);
            ImageView img = (ImageView) dialog.findViewById(R.id.img1);
            TextView txt1 = (TextView) dialog.findViewById(R.id.txt1);
//            TextView txt2 = (TextView) dialog.findViewById(R.id.txt2);
            LinearLayout linear1 = (LinearLayout) dialog.findViewById(R.id.linear1);
            LinearLayout linear2 = (LinearLayout) dialog.findViewById(R.id.linear2);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            if (p.isAutoApply()) {
                linear2.setVisibility(View.GONE);
            } else {//hoi y kien nguoi dung

                AppCompatButton btn1 = (AppCompatButton) dialog.findViewById(R.id.btn1);
                AppCompatButton btn2 = (AppCompatButton) dialog.findViewById(R.id.btn2);

                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dong y
                        dialog.dismiss();
                        setPromotion(p.getPromotionId(), context);

                        switch (p.getActionType()) {
                            case PromotionAction.ACCEPT:
                                //ko lam gi
                                break;
                            case PromotionAction.DEEPLINK:
                                //den man hinh mong muon
//                                PromotionScreen.navigationScreen(p.getActionLink());
                                if (!TextUtils.isEmpty(p.getActionLink())) {
                                    Uri data = Uri.parse(p.getActionLink());
                                    if (data != null && data.isHierarchical()) {
                                        String screen = data.getQueryParameter("screen");
                                        if (!TextUtils.isEmpty(screen)) {

                                            //dieu huong vao tung man hinh
                                            if (screen.equalsIgnoreCase(HOME.toLowerCase())) {
                                                Intent intent = new Intent(MH01_MainActivity.ACTION_CHANGE_FRAGMENT);
                                                intent.putExtra(FragmentPosition.FRAGMENT_POSITION, FragmentPosition.HOME);
                                                context.sendBroadcast(intent);

                                            } else if (screen.equalsIgnoreCase(MTP.toLowerCase())) {
                                                Intent intent = new Intent(MH01_MainActivity.ACTION_CHANGE_FRAGMENT);
                                                intent.putExtra(FragmentPosition.FRAGMENT_POSITION, FragmentPosition.MTP);
                                                context.sendBroadcast(intent);

                                            } else if (screen.equalsIgnoreCase(SHOP.toLowerCase())) {
                                                context.startActivity(new Intent(context, ShoppingActivity.class));

                                            } else if (screen.equalsIgnoreCase(PROFILE.toLowerCase())) {
                                                Intent intent = new Intent(MH01_MainActivity.ACTION_CHANGE_FRAGMENT);
                                                intent.putExtra(FragmentPosition.FRAGMENT_POSITION, FragmentPosition.PROFILE);
                                                context.sendBroadcast(intent);

                                            } else if (screen.equalsIgnoreCase(DISCOVER.toLowerCase())) {
                                                context.startActivity(new Intent(context, MH08_SuggestActivity.class));

                                            } else if (screen.equalsIgnoreCase(MEDIA.toLowerCase())) {
                                                String id = data.getQueryParameter("id");
                                                if (!TextUtils.isEmpty(id)) {
                                                    try {
                                                        long mediaId = Long.parseLong(id);
                                                        //play media id
                                                        getMedia(mediaId, context);

                                                    } catch (NumberFormatException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } else if (screen.equalsIgnoreCase(PAY.toLowerCase())) {
                                                String type = data.getQueryParameter("type");
                                                if (!TextUtils.isEmpty(type)) {
                                                    if (type.equalsIgnoreCase("vip")) {
                                                        Intent intent = new Intent(context, PaymentActivity.class);
                                                        //intent.putExtra(Media.MEDIA_ID, mediaId);//ko truyen la mua vip
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }

                                break;
                            case PromotionAction.LINK:
                                //mo link web
                                String url = p.getActionLink();
                                if (!TextUtils.isEmpty(url)) {
                                    MyUtils.openWebPage(url, context);
                                }
                                break;
                        }
                    }
                });

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //tu choi
                        dialog.dismiss();
                        refusePromotion(p.getPromotionId(), context);
                    }
                });


            }


            /*img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });*/


            //neu chi co hinh
            if (!TextUtils.isEmpty(p.getBanner()) &&
                    TextUtils.isEmpty(p.getTitle()) &&
                    TextUtils.isEmpty(p.getDescription())) {

                linear1.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));

                if (!TextUtils.isEmpty(p.getBanner())) {
                    int width = MyUtils.getScreenWidth(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
                    img.setLayoutParams(params);

                    Glide.with(context)
                            .load(p.getBanner())
                            .override(width, width)
                            .into(img);
                }

                //set background transparent
                txt1.setVisibility(View.GONE);
//                txt2.setVisibility(View.GONE);


            } else {

//                linear1.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

                //image
                if (!TextUtils.isEmpty(p.getBanner())) {
                    int width = MyUtils.getScreenWidth(context);
                    Glide.with(context)
                            .load(p.getBanner())
                            .override(width, width)
                            .into(img);
                } else {
                    img.setVisibility(View.GONE);
                }

                //title
                if (!TextUtils.isEmpty(p.getTitle())) {
                    txt1.setText(p.getTitle());
                } else {
                    txt1.setVisibility(View.GONE);
                }


                //description
                /*if (!TextUtils.isEmpty(p.getDescription())) {
                    txt2.setText(p.getDescription());
                } else {
                    txt2.setVisibility(View.GONE);
                }*/

            }


            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();
            lWindowParams.copyFrom(dialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT; // this is where the magic happens
            lWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lWindowParams);
            dialog.getWindow().getDecorView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


            dialog.show();


            //
            //refresh lai trang thai user
            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getResult() != null) {
                        //refresh man hinh ME
                        context.sendBroadcast(new Intent(Fragment_Profile_Owner.ACTION_REFRESH));
                    }
                    return null;
                }
            });


        }
    }

    private static void setPromotion(long promotionId, Context context) {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.setPromotion(
                    promotionId,
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                /*ReturnResult result = Webservices.parseJson(data.toString(), null, false);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                        MyUtils.showToastDebug(context, "OK");
                                    }
                                }*/

                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            MyUtils.log("error");
                        }
                    });
        }

    }

    private static void refusePromotion(long promotionId, Context context) {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.refusePromotion(
                    promotionId,
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                /*ReturnResult result = Webservices.parseJson(data.toString(), null, false);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                        MyUtils.showToastDebug(context, "OK");
                                    }
                                }*/
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            MyUtils.log("error");
                        }
                    });
        }

    }

    private static void getMedia(long mediaId, Context context) {
        if(context!=null){
            if (MyUtils.checkInternetConnection(context)) {
                MyApplication.apiManager.getMedia(
                        mediaId,
                        true,
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject data = response.body();
                                if (data != null) {
                                    ReturnResult result = Webservices.parseJson(data.toString(), Media.class, false);

                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                            Media media = (Media) result.getData();
                                            if (media != null) {
                                                if(ShoppingActivity.isExist){
                                                    Intent intent = new Intent(ShoppingActivity.ACTION_PLAY_ALBUM);
                                                    intent.putExtra(Media.MEDIA, media);
                                                    context.sendBroadcast(intent);
                                                }else{
                                                    context.startActivity(new Intent(context, ShoppingActivity.class));
                                                    SystemClock.sleep(500);
                                                    Intent intent = new Intent(ShoppingActivity.ACTION_PLAY_ALBUM);
                                                    intent.putExtra(Media.MEDIA, media);
                                                    context.sendBroadcast(intent);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                MyUtils.log("error");
                            }
                        });
            } else {
                MyUtils.showThongBao(context);
            }
        }

    }

}
