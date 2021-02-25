package com.topceo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.ads.AdConfigModel;
import com.topceo.ads.AdUtils;
import com.topceo.ads.AdsAppModel;
import com.topceo.autoplayvideo.CameraAnimation;
import com.topceo.autoplayvideo.Video;
import com.topceo.autoplayvideo.VideoView;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.comments.CommentAdapterSectionParent_ImageComment;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.eventbus.EventImageComment;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.Fragment_2_Explorer;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.hashtag.HashTagActivity;
import com.topceo.objects.db.ImageItemDB;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.Item;
import com.topceo.objects.image.ItemData;
import com.topceo.objects.image.LinkPreview;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserShort;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.services.ReturnResult;
import com.topceo.services.UtilService;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.socialview.commons.socialview.Hashtag;
import com.topceo.socialview.commons.socialview.Mention;
import com.topceo.socialview.commons.widget.HashtagArrayAdapter;
import com.topceo.socialview.commons.widget.MentionArrayAdapter;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.socialview.objects.SearchHashtag;
import com.topceo.socialview.objects.SearchMention;
import com.topceo.utils.DateFormat;
import com.topceo.utils.EndlessRecyclerOnScrollListener;
import com.topceo.utils.MyUtils;
import com.topceo.viewholders.HolderUtils;
import com.topceo.views.ExpandableTextView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.smartapp.collage.CollageAdapterUrls;
import com.smartapp.collage.MediaLocal;
import com.smartapp.collage.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MH02_PhotoDetailActivity extends AppCompatActivity {
    private Activity context = this;

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgShop)
    ImageView imgShop;
    ///////////////////////////////////////////////////
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;

    private void setTitleBar() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imgShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ShoppingActivity.class));
            }
        });
        relativeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainChatActivity.class);
                startActivity(intent);
            }
        });
        ChatUtils.setChatUnreadNumber(txtNumber);
    }


    private ImageItem item;
    private User user;


    // each data imageItem is just a string in this case
    @BindView(R.id.textView1)
    TextView txt1;
    @BindView(R.id.textView2)
    TextView txt2;
    @BindView(R.id.textView3)
    TextView txt3;
    @BindView(R.id.textView5)
    ExpandableTextView txt5;
    @BindView(R.id.textView6)
    TextView txt6;

    @BindView(R.id.imageView1)
    ImageView img1;
    @BindView(R.id.imageView2)
    ImageView img2;
    @BindView(R.id.imgLike)
    CheckBox imgLike;
    @BindView(R.id.imgMenu2)
    ImageView imgMenu;


    @BindView(R.id.linearLayout3)
    LinearLayout linear3;

    @BindView(R.id.vBgLike)
    View vBgLike;
    @BindView(R.id.ivLike)
    ImageView ivLike;

    @BindView(R.id.linearLike)
    LinearLayout linearLike;
    @BindView(R.id.linearComment)
    LinearLayout linearComment;
    @BindView(R.id.linearShare)
    LinearLayout linearShare;

    public @BindView(R.id.linearSave)
    LinearLayout linearSave;
    public @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.button1)
    AppCompatButton btnFollow;

    @BindView(R.id.scrollViewDetail)
    NestedScrollView scrollView;
    @BindView(R.id.imgOwner)
    ImageView imgOwner;

    //comment preview
    @BindView(R.id.txtViewAllComment)
    TextView txtViewAllComment;
    @BindView(R.id.linearCommentPreview)
    LinearLayout linearCommentPreview;

    @BindView(R.id.imgVip)
    ImageView imgVip;

    private void setVip(UserShort user) {
        if (user.isVip() || user.getUserId() == 1) {
            imgVip.setVisibility(View.VISIBLE);
            /*if (user.getUserId() == 1) {//boss son tung
                imgVip.setImageResource(R.drawable.ic_boss);
            } else {
                imgVip.setImageResource(R.drawable.ic_king);
            }*/
            imgVip.setImageResource(R.drawable.ic_svg_24);
        } else {
            imgVip.setVisibility(View.GONE);
        }
    }


    private int avatarSize = 0;
    private int widthImage = 0, heightImage = 0;
//    private int roundCorner = 10;


    private void sumCheckingsystem() {
        int number1 = 10;
        int number2 = 12;
        int sum = number1 + number2;

    }

    private boolean isMyPost = false;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        //thong bao tat ca media tu Fragment_1_Home_User deu tat toan bo
        sendBroadcast(new Intent(Fragment_1_Home_User.ACTION_SET_MUTE_ALL));

        realm = Realm.getDefaultInstance();
//        roundCorner = context.getResources().getDimensionPixelOffset(R.dimen.margin_image);
        widthImage = MyUtils.getScreenWidth(context);// - roundCorner * 2;

        TinyDB db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
            /*if (user != null) {
                int width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_small);
                //set avatar
                Glide.with(context)
                        .load(user.getAvatarSmall())
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(width, width)
                        .transform(new CenterCrop(), new GlideCircleTransform(context))
                        .into(imgOwner);
            }*/
        }

        txtViewAllComment.setVisibility(View.GONE);
        linearCommentPreview.setVisibility(View.GONE);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            item = (ImageItem) getIntent().getParcelableExtra(ImageItem.IMAGE_ITEM);
            if (item != null) {

                if (user != null && item.getOwner() != null) {
                    isMyPost = user.getUserId() == item.getOwner().getUserId();
                }

                heightImage = item.getNeedHeightImage(widthImage);
                img2.setLayoutParams(new FrameLayout.LayoutParams(widthImage, heightImage));

                setUI();

                if (user.getUserId() == item.getOwner().getUserId()) {
                    btnFollow.setVisibility(View.GONE);
                } else {
                    btnFollow.setVisibility(View.VISIBLE);
                }
            }
        }


        setTitleBar();


//        widthImage = MyUtils.getScreenWidth(context);

        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_small);
        img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));


        //setup quang cao
        adContainer = (LinearLayout) findViewById(R.id.adContainer);
        linearRoot = (LinearLayout) findViewById(R.id.fLinearRoot);
        native_ad_icon = (AdIconView) findViewById(R.id.native_ad_icon);
        fTxt1 = (TextView) findViewById(R.id.fTextView1);
        fTxt2 = (TextView) findViewById(R.id.fTextView2);
        fTxt3 = (TextView) findViewById(R.id.fTextView3);
        fTxt4 = (TextView) findViewById(R.id.fTextView4);
        fMediaView1 = (MediaView) findViewById(R.id.fMediaView1);
        fLinearAds = (LinearLayout) findViewById(R.id.fLinearLayout1);
        fBtn1 = (Button) findViewById(R.id.fButton1);

//        setUpQuangCao(context);

        init();
        registerReceiver();


    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    LinearLayout adContainer;
    LinearLayout linearRoot;
    AdIconView native_ad_icon;
    TextView fTxt1;
    TextView fTxt2;
    TextView fTxt3;
    TextView fTxt4;
    MediaView fMediaView1;
    LinearLayout fLinearAds;
    Button fBtn1;

    //////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isHaveAds = false;
    private boolean isEpapersmart = false;
    private boolean isAdmob = true;//admob or facebook
    private String native_admob_id = "";
    private String native_facebook_id = "";

    private void setUpQuangCao(Context c) {

        isHaveAds = false;
        linearRoot.setVisibility(View.GONE);
        adContainer.setVisibility(View.GONE);

        //fullscreen
        Object obj = AdUtils.loadListFromFile(c, AdUtils.OBJECT_ADS);
        if (obj != null) {
            AdsAppModel adsProviderModel = (AdsAppModel) obj;
            if (adsProviderModel != null) {

                //native
                AdConfigModel adNative = adsProviderModel.getNative();
                if (adNative != null) {
                    //admob
                    if (adNative.getAdNetworkId() == AdConfigModel.AD_NETWORK_ADMOB) {
                        String native_ad_unit_id = adNative.getExternalAd().getAdUnitId();
                        if (!TextUtils.isEmpty(native_ad_unit_id)) {
                            isHaveAds = true;
                            isAdmob = true;
                            native_admob_id = native_ad_unit_id;
                            if (BuildConfig.DEBUG) {
                                native_admob_id = "ca-app-pub-3940256099942544/6300978111";
                            }
                            if (!TextUtils.isEmpty(native_admob_id)) {

//                                NativeExpressAdView adView = new NativeExpressAdView(context);
                                AdView adView = new AdView(context);
                                adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
                                adView.setAdUnitId(native_admob_id);


                                // Create an ad request.
                                AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

                                if (BuildConfig.DEBUG) {
                                    // Optionally populate the ad request builder.
                                    adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                                }

                                adView.loadAd(adRequestBuilder.build());
                                adContainer.addView(adView);

                                adView.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                        adContainer.setVisibility(View.VISIBLE);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                scrollView.scrollTo(0, 0);
                                            }
                                        });
                                    }
                                });


                            }
                        }
                    }

                    //facebook
                    /*if (adNative.getAdNetworkId() == AdConfigModel.AD_NETWORK_FACEBOOK) {
                        String native_ad_unit_id = adNative.getExternalAd().getAdUnitId();
                        if (!TextUtils.isEmpty(native_ad_unit_id)) {
                            isHaveAds = true;
                            isAdmob = false;
                            native_facebook_id = native_ad_unit_id;
                            initNativeAd(native_facebook_id);
                        }
                    }*/


                }
            }
        }


    }

    ///////////////////////////////////////////////////////////////////////////
    private NativeAd ads;

    private void initNativeAd(String unitId) {

        ads = new NativeAd(context, unitId);
        ads.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
//                MyUtils.showToastDebug(context,"load native facebook");
                linearRoot.setVisibility(View.VISIBLE);
                //set ui
                fTxt1.setText(ads.getAdvertiserName());
                fTxt2.setText("Sponsored");
                fTxt3.setText(ads.getAdBodyText());
                fTxt4.setText(ads.getAdSocialContext());

                fBtn1.setText(ads.getAdCallToAction());

                int height = ads.getAdCoverImage().getHeight();
                fMediaView1.getLayoutParams().height = height;

//                fMediaView1.setNativeAd(ads);//// Download and display the cover image.


                // Download and display the ad icon.
//                NativeAd.Image adIcon = ads.getAdIcon();
//                NativeAd.downloadAndDisplayImage(adIcon, native_ad_icon);

                //ad choice
                fLinearAds.removeAllViews();
                AdChoicesView adChoicesView = new AdChoicesView(context, ads, true);
                fLinearAds.addView(adChoicesView, 0);

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(fTxt1);
                clickableViews.add(fBtn1);
                ads.registerViewForInteraction(fLinearAds, fMediaView1, native_ad_icon, clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });


        // Request an ad
        ads.loadAd();
    }
    ///////////////////////////////////////////////////////////////////////////

    //VIDEO
    @BindView(R.id.frameLayoutImage)
    FrameLayout frameLayoutImage;
    @BindView(R.id.frameLayoutVideo)
    FrameLayout frameLayoutVideo;
    @BindView(R.id.vvInfo)
    VideoView vvInfo;
    @BindView(R.id.ivInfo)
    ImageView ivInfo;
    @BindView(R.id.imgSound)
    ImageView imgSound;
    @BindView(R.id.ivCameraAnimation)
    CameraAnimation ivCameraAnimation;

    private boolean isMuted = true;
    private boolean isVideo = false;

    private void setUI() {
        if (item != null) {
            UserShort owner = item.getOwner();
            if (owner != null) {
                Glide.with(context)
                        .load(owner.getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(avatarSize, avatarSize)
                        .transform(new GlideCircleTransform(context))
                        .into(img1);
                setVip(owner);
            }


            if (ImageItem.ITEM_TYPE_FACEBOOK.equals(item.getItemType())) {
                //neu chi co 1 video thi hien thi theo instagram video
                if (item.getItemContent().size() == 1 && item.isVideo()) {
                    item.setItemType(ImageItem.ITEM_TYPE_INSTAGRAM);
                    initTelegramPost();
                } else {
                    initFacebookPost();
                }

            } else {//instagram
                initTelegramPost();
            }


            txt1.setText(item.getOwner().getUserName());
            if (!TextUtils.isEmpty(item.getLocation())) {
                txt2.setText(item.getLocation());
                txt2.setVisibility(View.VISIBLE);
            } else {
                txt2.setVisibility(View.GONE);
            }

            txt6.setText(MyUtils.formatDate(item.getCreateDate(), DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM, context));


            //neu like,comment,share deu = 0 thi an
            setLikeShareComment();
            imgMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(imgMenu, item);
                }
            });

            ////
            linearLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //update local
                    item.setLiked(!item.isLiked());
                    whenLike();
                }
            });

            imgLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setLiked(isChecked);
                    whenLike();
                }
            });

            linearSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePost(item);
                }
            });

            img2.setOnTouchListener(new View.OnTouchListener() {
                long t1 = 0, t2 = 0;
                int count = 0;

                @Override
                public boolean onTouch(View view, MotionEvent e) {

                    int action = e.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:

                            if (count == 0) t1 = System.currentTimeMillis();
                            if (count == 1) t2 = System.currentTimeMillis();
                            count++;
                            if (count > 1) count = 0;
                            if (Math.abs(t2 - t1) < 300) {
                                t1 = 0;
                                t2 = 0;
                                count = 0;
                                //On double tap here. Do stuff
//                                MyUtils.showToast(context, "double tap");
                                if (!item.isLiked()) {
                                    linearLike.performClick();
                                }

                                animateHeart();
                            }

                            break;

                    }

                    return true;
                }

            });
            ////
            linearComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent intent = new Intent(context, CommentActivityParent.class);
                    intent.putExtra(ImageItem.IMAGE_ITEM, imageItem);
                    startActivity(intent);*/
                    txtInput.requestFocus();
                    MyUtils.showKeyboard(context);
                }
            });
            ////
            linearShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.share(context, item);
                }
            });

            //vao profile
            txt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.gotoProfile(item.getOwner().getUserName(), context);
                    finish();
                }
            });
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txt1.performClick();
                }
            });
            txt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txt1.performClick();
                }
            });

            /////////////////////////////////////////////////////////////////////////


            //click vao so luong like thi mo danh sach nguoi da like
            txt3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    MyUtils.showToastDebug(context, "Number likes");
                    Intent intent = new Intent(context, MH11_LikeActivity.class);
                    intent.putExtra(ImageItem.IMAGE_ITEM, item);
                    startActivity(intent);

                }
            });

            /////////////////////////////////////////////////////////////////////////
        }

        setButtonUI();
    }

    private void initImagePost() {
        frameLayoutImage.setVisibility(View.VISIBLE);
        frameLayoutVideo.setVisibility(View.GONE);
        frameLayoutFacebook.setVisibility(View.GONE);

        //IMAGE
        //imageview 2
        heightImage = item.getNeedHeightImage(widthImage);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthImage, heightImage);
//                    params.setMargins(roundCorner, 0, roundCorner, roundCorner);
        img2.setLayoutParams(params);


        RequestOptions options = new RequestOptions()
//                            .transform(new RoundedCorners(roundCorner))
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        String img = item.getImageLarge();
        if (!TextUtils.isEmpty(img)) {
            Glide.with(context)
                    .load(img)//images[position%images.length]
                    .thumbnail(
                            Glide.with(context)
                                    .load(item.getImageSmall())
                                    .apply(options)
                    )
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .override(widthImage, heightImage)
                    .into(img2);
        } else {
            Glide.with(context)
                    .load(R.drawable.no_media)//images[position%images.length]
                    .override(widthImage, widthImage)
                    .apply(options)
                    .into(img2)
            ;
        }
    }

    private void initVideoPost() {
        isVideo = true;

        frameLayoutImage.setVisibility(View.GONE);
        frameLayoutVideo.setVisibility(View.VISIBLE);
        frameLayoutFacebook.setVisibility(View.GONE);

        int heightImage = item.getNeedHeightImage(widthImage);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthImage, heightImage);
//                    params.setMargins(roundCorner, 0, roundCorner, roundCorner);
        vvInfo.setLayoutParams(params);

        //neu la video thi: large chua link video, (medium+small) chua cover
        vvInfo.setVideo(new Video(item.getImageLarge(), 0));

        setMute();
        imgSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuted = !isMuted;

                setMute();
            }
        });


        //set hinh
        heightImage = item.getNeedHeightImage(widthImage);
        params = new FrameLayout.LayoutParams(widthImage, heightImage);
//                    params.setMargins(roundCorner, 0, roundCorner, roundCorner);
        ivInfo.setLayoutParams(params);


        String img = item.getImageMedium();
        if (!TextUtils.isEmpty(img)) {
//                        loading.setVisibility(View.GONE);
            Glide.with(context)
                    .load(img)//images[position%images.length]
//                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(roundCorner)))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH)
                    .override(widthImage, heightImage)
                    .into(ivInfo);
        } else {
            Glide.with(context)
                    .load(R.drawable.no_media)//images[position%images.length]
                    .override(widthImage, widthImage)
                    .into(ivInfo)
            ;
        }


        //play
        ivInfo.setVisibility(View.VISIBLE);
        ivCameraAnimation.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vvInfo.play(new VideoView.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        ivInfo.setVisibility(View.GONE);
                        ivCameraAnimation.stop();
                        setMute();

                    }
                });
            }
        }, 3000);
    }

    private void whenLike() {

        //set lai giao dien: nut like va so luong like----------------------
        int like = item.getLikeCount();
        if (item.isLiked()) {
            like += 1;
        } else {
            like -= 1;
        }
        item.setLikeCount(like);

        //like
        setLikeShareComment();

        //update server
        UtilService.updateLikeState(context, item.getImageItemId(), item.isLiked());

        //update db
        updateLiked(item.getImageItemId(), item.isLiked(), item.getLikeCount());

        MyUtils.updateImageItem(context, item);
    }

    private void setMute() {
        if (vvInfo.getMediaPlayer() != null) {
            //refresh giao dien
            if (isMuted) {
                imgSound.setImageResource(R.drawable.ic_volume_off_white_24dp);
                vvInfo.getMediaPlayer().setVolume(0, 0);
            } else {
                imgSound.setImageResource(R.drawable.ic_volume_up_white_24dp);
                vvInfo.getMediaPlayer().setVolume(1, 1);
            }
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Showing popup menu when tapping on 3 dots
     */
    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view, final ImageItem item) {
        // inflate menu
        final PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        if (item.getOwner().getUserName().equals(user.getUserName())) {
            inflater.inflate(R.menu.menu_item_newsfeed_owner, popup.getMenu());
        } else {
            Menu menu = null;
            inflater.inflate(R.menu.menu_item_newsfeed, menu = popup.getMenu());
            MenuItem menuSave = menu.findItem(R.id.action_save);
            if (item.isSaved()) {
                menuSave.setTitle(R.string.not_save);
                menuSave.setIcon(R.drawable.ic_svg_23_24dp);
            } else {
                menuSave.setTitle(R.string.save_post);
                menuSave.setIcon(R.drawable.ic_svg_22_24dp);
            }
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_save:
                        savePost(item);
                        break;
                    case R.id.action_report:
//                            Toast.makeText(context, "Report", Toast.LENGTH_SHORT).show();
                        MyUtils.showDialogReportPost(context, item.getImageItemId());
                        return true;
                    case R.id.action_edit:
                        //Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                        MyUtils.editPost(item, context);

                        return true;
                    case R.id.action_delete:
//                            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                        //hoi truoc khi xoa
                        new MaterialDialog.Builder(context)
//                                    .title(R.string.delete_image)
                                .content(R.string.do_you_delete_image)
                                .positiveText(R.string.delete)
                                .negativeText(R.string.no)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        deletePost(item.getImageItemId());
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                        return true;
                    default:
                }
                return false;
            }
        });

//        popup.show();

        @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void deletePost(long imageItemId) {
        AndroidNetworking.post(Webservices.URL + "image/delete")
                .addQueryParameter("ImageItemId", String.valueOf(imageItemId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, String.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                MyUtils.showToast(context, R.string.delete_success);

                                //update ui cac man hinh home, profile
                                MyUtils.afterDeletePost(context, item.getImageItemId());
                                //xoa va refresh lai danh sach
                                finish();


                            } else {
                                MyUtils.showToast(context, R.string.delete_not_success);

                            }
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateLikeState(long imageItemId, boolean isLiked) {
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", Webservices.UPDATE_IMAGE_ITEM_LIKED(imageItemId, isLiked))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //{"data":{"UpdateImageItemLiked":null}}
                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("UpdateImageItemLiked")) {
                                JSONObject obj = objP.getJSONObject("UpdateImageItemLiked");

                                double imageItemId = obj.getDouble("ImageItemId");
//                                int likeCount = obj.getInt("LikeCount");

                                if (imageItemId > 0) {
//                                    MyUtils.showToast(context, R.string.update_success);
                                    /*//update lai imageItem dang chon
                                    int count = imageItem.getLikeCount();
                                    if(isLiked){
                                        count+=1;
                                    }else{
                                        count -=1;
                                    }
                                    imageItem.setLikeCount(count);*/

                                    //update grid user
                                    refreshItem();

                                }
                            } else {

                                //update ui cac man hinh home, profile
                                MyUtils.afterDeletePost(context, item.getImageItemId());
                                //null tuc la hinh da bi xoa, xoa trong list
                                new MaterialDialog.Builder(context)
                                        .content(R.string.post_is_deleted)
                                        .positiveText(R.string.ok)
                                        .cancelable(false)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                finish();
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

    private void refreshItem() {
        //update grid user
        Intent intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_UPDATE_ITEM);
        intent.putExtra(ImageItem.IMAGE_ITEM, item);
        sendBroadcast(intent);

        //update grid image by hashtag
        intent = new Intent(HashTagActivity.ACTION_UPDATE_ITEM);
        intent.putExtra(ImageItem.IMAGE_ITEM, item);
        sendBroadcast(intent);

        //update grid explorer
        intent = new Intent(Fragment_2_Explorer.ACTION_UPDATE_ITEM);
        intent.putExtra(ImageItem.IMAGE_ITEM, item);
        sendBroadcast(intent);

        //update home list
        intent = new Intent(Fragment_1_Home_User.ACTION_UPDATE_ITEM);
        intent.putExtra(ImageItem.IMAGE_ITEM, item);
        sendBroadcast(intent);


    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setButtonUI() {
        //todo //kiem tra owner cua hinh co nam trong danh sach minh dang theo doi khong
        final UserShort uLike = item.getOwner();
        setStateButton(uLike, btnFollow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
                    //set khong quan tam
                    //bo follow
                    Webservices.updateUserFollowingState(uLike.getUserId(), false).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if (task.getError() == null) {
                                if (task.getResult() != null) {
                                    boolean isOK = (boolean) task.getResult();
                                    if (isOK) {
//                                        MyUtils.showToast(context, R.string.update_success);

                                        //refresh giao dien
                                        setButtonUI();

                                        //refresh giao dien MH19_UserProfileActivity
                                        Intent intent = new Intent(Fragment_Profile_Owner.ACTION_UPDATE_STATE_FOLLOW);
                                        sendBroadcast(intent);
                                    }
                                }
                            } else {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }

                            return null;
                        }
                    });
                } else {//set da quan tam
                    //set follow
                    Webservices.updateUserFollowingState(uLike.getUserId(), true).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if (task.getError() == null) {
                                if (task.getResult() != null) {
                                    boolean isOK = (boolean) task.getResult();
                                    if (isOK) {
//                                        MyUtils.showToast(context, R.string.update_success);

                                        //refresh giao dien
                                        setButtonUI();

                                        //refresh giao dien MH19_UserProfileActivity
                                        Intent intent = new Intent(Fragment_Profile_Owner.ACTION_UPDATE_STATE_FOLLOW);
                                        sendBroadcast(intent);
                                    }
                                }
                            } else {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }

                            return null;
                        }
                    });
                }
            }
        });
    }

    private void setStateButton(UserShort uLike, AppCompatButton btn) {
        if (uLike != null) {
            if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
                btn.setText(getText(R.string.following_title));
                btn.setBackgroundResource(R.drawable.bg_sky_rectangle_border);
                btn.setTextColor(getResources().getColor(R.color.black));
            } else {
                btn.setText(getText(R.string.follow_title));
                btn.setBackgroundResource(R.drawable.bg_sky_radian);
                btn.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_UPDATE_ITEM = "ACTION_UPDATE_ITEM_" + MH02_PhotoDetailActivity.class.getSimpleName();
    public static final String ACTION_UPDATE_STATE_FOLLOW = "ACTION_UPDATE_STATE_FOLLOW_" + MH02_PhotoDetailActivity.class.getSimpleName();
    public static final String ACTION_POST_DELETED = "ACTION_POST_DELETED";
    public static final String ACTION_COMMENT_DELETED = "ACTION_COMMENT_DELETED";


    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_ITEM)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        ImageItem image = b.getParcelable(ImageItem.IMAGE_ITEM);
                        if (image != null) {
                            item = image;
                            setUI();
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_STATE_FOLLOW)) {
                    setButtonUI();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_POST_DELETED)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        long imageItemId = b.getLong(ImageItem.IMAGE_ITEM_ID);
                        if (item != null && imageItemId > 0) {
                            if (item.getImageItemId() == imageItemId) {
                                finish();
                            }
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_COMMENT_DELETED)) {
                    updateCommentCount();
                } else if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                }


            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);
        filter.addAction(ACTION_UPDATE_ITEM);
        filter.addAction(ACTION_UPDATE_STATE_FOLLOW);
        filter.addAction(ACTION_POST_DELETED);
        filter.addAction(ACTION_COMMENT_DELETED);

        registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
        MyUtils.hideKeyboard(context);

        if (isVideo) {
            ivCameraAnimation.stop();
            vvInfo.stop();
        }

        if (realm != null) {
            realm.close();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    Map<View, AnimatorSet> likeAnimationsMap = new HashMap<>();
    Map<View, AnimatorSet> heartAnimationsMap = new HashMap<>();

    private void animatePhotoLike(final ImageItem item) {
        vBgLike.setVisibility(View.VISIBLE);
        ivLike.setVisibility(View.VISIBLE);

        vBgLike.setScaleY(0.1f);
        vBgLike.setScaleX(0.1f);
        vBgLike.setAlpha(1f);
        ivLike.setScaleY(0.1f);
        ivLike.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(vBgLike, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(vBgLike, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(vBgLike, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //animation xong thi thuc hien
                //update server
                updateLikeState(item.getImageItemId(), item.isLiked());

                likeAnimationsMap.remove(vBgLike);
                resetLikeAnimationState();
                dispatchChangeFinishedIfAllAnimationsEnded(vBgLike);


            }
        });
        animatorSet.start();

        likeAnimationsMap.put(vBgLike, animatorSet);
    }

    private void dispatchChangeFinishedIfAllAnimationsEnded(View vBgLike) {
        if (likeAnimationsMap.containsKey(vBgLike) || heartAnimationsMap.containsKey(vBgLike)) {
            return;
        }

//        rv2.dispatchAnimationFinished(holder);
    }

    private void resetLikeAnimationState() {
        vBgLike.setVisibility(View.INVISIBLE);
        ivLike.setVisibility(View.INVISIBLE);
    }
    /////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<ImageComment> listComments = new ArrayList<>();

    private void initAdapter() {
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        rv.setNestedScrollingEnabled(false);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                MyUtils.showToastDebug(context, "Load more...");
                ImageComment comment = mAdapter.getLastestItem();
                if (comment != null) {
                    getComments(item.getImageItemId(), comment.getItemId(), comment.getCreateDate());
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new CommentAdapterSectionParent_ImageComment(listComments, context, isMyPost);
        mAdapter.shouldShowHeadersForEmptySections(true);
        rv.setAdapter(mAdapter);
    }

    private CommentAdapterSectionParent_ImageComment mAdapter;


////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {"data":{"ImageComment":{"Comments":
     * [{"ItemId":1062,"User":{"UserName":"jessicahua","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/87b2fef5-3b6a-4a05-ae09-90bcc8396ad4\/bbbf19b1-8ccb-4145-983a-cb6bee1ddf96.JPG"},"Comment":"Dep qua ah @ChupHinhDep","CreateDate":"2016-06-25 00:40:40"}
     * ,{"ItemId":1063,"User":{"UserName":"nhattien667","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/10973213-85c8-4195-9fd4-ab4f931d3331\/54074402-b36e-4635-b90b-414c167db5bc.JPG"},"Comment":"Bạn dùng app gi vậy","CreateDate":"2016-06-24 19:09:09"},
     * {"ItemId":1064,"User":{"UserName":"hieukeodeo2882","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/7627e20c-604d-4145-805c-944eae61a3df\/c955e242-0fc9-4ead-b867-64390541e841.JPG"},"Comment":"Hay nhỉ��","CreateDate":"2016-06-24 18:43:18"}]
     * }}}
     *
     * @param imageItemId
     * @param lastItemId
     */
    private void getComments(final long imageItemId, final long lastItemId, final long lastCreateDate) {

        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getComments(imageItemId, 0, lastItemId, lastCreateDate).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<ImageComment> comments = (ArrayList<ImageComment>) task.getResult();
                            if (comments.size() > 0) {

                                if (lastItemId == 0) {//page 1
                                    mAdapter.clear();
                                    mAdapter.addListItems(comments);
                                } else {//load more
                                    mAdapter.addListItems(comments);
                                }

                            }

                            //if first load
                            /*if (lastItemId == 0) {
                                if (comments.size() > 0) {
                                    list_empty.setVisibility(View.GONE);
                                } else {
                                    list_empty.setVisibility(View.VISIBLE);
                                }
                            }*/

                            comments.clear();
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            getComments(imageItemId, lastItemId, lastCreateDate);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }
                        }
                    }

//                    setRefresh(false);
                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }
    }


    private void sendComment(final String comment) {
        if (MyUtils.checkInternetConnection(context)) {
            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.sending));
            progressDialog.show();*/

            long replyToId = 0;
            if (replyToComment != null) {
                replyToId = replyToComment.getItemId();
            }
            Webservices.sendComment(item.getImageItemId(), comment, replyToId).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
//                    progressDialog.dismiss();
                    if (task.getError() == null) {
                        if (task.getResult() != null) {

                            initReplyLayout(null);

                            ImageComment img = (ImageComment) task.getResult();
                            if (img != null) {
//                                list_empty.setVisibility(View.GONE);

                                //add vao trong list comment
                                mAdapter.add(img);//mAdapter.getItemCount()
                                //neu add vao cha thi scroll len dau
                                if (img.getReplyToId() == 0) {
                                    rv.scrollToPosition(0);//mAdapter.getItemCount()-1
                                }

                                txtInput.setText("");

                                updateCommentCount();

                            }
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());

                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            sendComment(comment);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }
                        }
                    }

                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }
    }

    private void updateCommentCount() {
        if (mAdapter != null && item != null && context != null) {
            //set lai so imageItem, sau do goi nguoc ve cac man hinh
            item.setCommentCount(mAdapter.getParentAndChildCount());
            MyUtils.updateImageItem(context, item);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST, mAdapter.getAllItem());
        outState.putParcelable(ImageItem.IMAGE_ITEM, item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            getComments(item.getImageItemId(), 0, 0);

        } else {

            MyUtils.initCookie(context);

            item = savedInstanceState.getParcelable(ImageItem.IMAGE_ITEM);
            ArrayList<ImageComment> list = savedInstanceState.getParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST);
            if (list != null && list.size() > 0) {
                mAdapter.clear();
                mAdapter.addListItems(list);
//                list_empty.setVisibility(View.GONE);
            } else {
//                list_empty.setVisibility(View.VISIBLE);
            }

        }
    }

    //EVENT BUS/////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyUtils.hideKeyboard(context, txtInput);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventImageComment event) {
        if (event != null && event.getComment() != null) {
            initReplyLayout(event.getComment());
        }
    }

    //REPLY/////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.linearReply)
    LinearLayout linearReply;
    @BindView(R.id.txtReply1)
    TextView txtReply1;
    @BindView(R.id.txtReply2)
    TextView txtReply2;
    @BindView(R.id.imgReplyClose)
    ImageView imgReplyClose;

    private ImageComment replyToComment;

    private void initReplyLayout(ImageComment comment) {
        replyToComment = comment;
        if (comment != null) {
            linearReply.setVisibility(View.VISIBLE);
            imgReplyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearReply.setVisibility(View.GONE);
                    replyToComment = null;
                }
            });

            //todo set hinh, ten, description
            if (comment.getUser() != null) {
                String name = comment.getUser().getUserName();
                txtReply1.setText(name);
            }

            txtReply2.setText(comment.getComment());


        } else {
            linearReply.setVisibility(View.GONE);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMMENT
    @BindView(R.id.recyclerView1)
    RecyclerView rv;
    @BindView(R.id.editText1)
    SocialAutoCompleteTextView txtInput;
    @BindView(R.id.ripple1)
    LinearLayout rippleSend;

    private void init() {
        initAdapter();

        if (item != null) {
            getComments(item.getImageItemId(), 0, 0);
        }


        rippleSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });


        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                /*if (scrollY > oldScrollY) {

                } else {

                }*/

                MyUtils.hideKeyboard(context);
            }
        });


        txtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    send();
                    if (TextUtils.isEmpty(txtInput.getText())) {
                        MyUtils.hideKeyboard(context, txtInput);
                    }
                    return true;
                }
                return false;
            }
        });

        //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    String string = txtInput.getText().toString();
                    if (!TextUtils.isEmpty(string)) {
                        String[] words = string.split(" ");
                        if (words.length > 0) {
                            String lastWord = words[words.length - 1];

                            //#h, @t
                            if (lastWord.length() >= 2) {
                                if (lastWord.charAt(0) == '#') {
                                    String keyword = lastWord.substring(1);
                                    searchHashtag(keyword);
                                } else if (lastWord.charAt(0) == '@') {
                                    String keyword = lastWord.substring(1);
                                    searchMetion(keyword);


                                }
                            }
                        }
                    }
                }
                return false;
            }
        });
        //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////

    }

    private void send() {
        String text = txtInput.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            MyUtils.hideKeyboard(context);
            sendComment(text);
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchMetion(String keyword) {
        MyApplication.apiManager.searchUserForMention(
                keyword,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<SearchMention>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<SearchMention> list = (ArrayList<SearchMention>) result.getData();
                                    if (list.size() > 0) {
                                        setAdapterMention(list);
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchHashtag(String keyword) {
        MyApplication.apiManager.searchHashtag(
                keyword,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<SearchHashtag>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<SearchHashtag> list = (ArrayList<SearchHashtag>) result.getData();
                                    if (list.size() > 0) {
                                        setAdapterHashtag(list);
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapterHashtag(ArrayList<SearchHashtag> list) {
        if (list != null && list.size() > 0) {
            ArrayList<Hashtag> data = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SearchHashtag item = list.get(i);
                data.add(new Hashtag(item.getHashtag(), item.getPostCount()));
            }
            ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<Hashtag>(context);
            hashtagAdapter.addAll(data);
            txtInput.setHashtagAdapter(hashtagAdapter);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapterMention(ArrayList<SearchMention> list) {
        if (list != null && list.size() > 0) {

            MentionArrayAdapter<Mention> mentionAdapter = new MentionArrayAdapter<Mention>(context);
            for (int i = 0; i < list.size(); i++) {
                SearchMention item = list.get(i);
                mentionAdapter.add(new Mention(item.getUserName(), item.getFullName(), item.getAvatarSmall()));
            }

            txtInput.setMentionAdapter(mentionAdapter);
        }
    }


    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void savePost(ImageItem item) {
        if (item != null) {

            if (item.isSaved()) {
                imgSave.setImageResource(R.drawable.ic_svg_22);
                MyApplication.apiManager.unSavePost(
                        item.getImageItemId(),
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject data = response.body();
                                if (data != null) {

                                    ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);
                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
//                                            MyUtils.showToast(context, R.string.update_success);
                                            MH02_PhotoDetailActivity.this.item.setSaved(!MH02_PhotoDetailActivity.this.item.isSaved());
                                            //reload item
                                            refreshItem();
                                            //grid cua user
                                            Intent intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST_SAVED);
                                            sendBroadcast(intent);


                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
            } else {
                imgSave.setImageResource(R.drawable.ic_svg_23);
                MyApplication.apiManager.savePost(
                        item.getImageItemId(),
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject data = response.body();
                                if (data != null) {

                                    ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);
                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
//                                            MyUtils.showToast(context, R.string.save_success);
                                            MH02_PhotoDetailActivity.this.item.setSaved(!MH02_PhotoDetailActivity.this.item.isSaved());
                                            //reload item
                                            refreshItem();
                                            //grid cua user
                                            Intent intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST_SAVED);
                                            sendBroadcast(intent);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
            }


        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setLikeShareComment() {
        int like = item.getLikeCount();
//        int comment = item.getCommentCount();

        //cai nao ko co thi an
        //likes
        if (like > 0) {
            String s = String.format(context.getString(R.string.number_likes), like);
            txt3.setText(s);
            txt3.setVisibility(View.VISIBLE);
        } else {
            txt3.setVisibility(View.GONE);
            txt3.setText("");
        }


        //control image button Thich
        /*if (item.isLiked()) {
            imgLike.setBackgroundResource(R.drawable.ic_liked_ig);
        } else {
            imgLike.setBackgroundResource(R.drawable.ic_like_ig);
        }*/
        imgLike.setChecked(item.isLiked());

        if (item.isSaved()) {
            imgSave.setImageResource(R.drawable.ic_svg_23);
        } else {
            imgSave.setImageResource(R.drawable.ic_svg_22);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void animateHeart() {
        /*ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 0.3f, 0.0f, 0.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(700);
        animation.setFillAfter(true);

        ivLike.startAnimation(animation);*/
        ivLike.setVisibility(View.VISIBLE);
        Animatable animatable = (Animatable) ivLike.getDrawable();
        if (animatable.isRunning())
            animatable.stop();
        else
            animatable.start();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Tab SonTung luu offline, cac tab khac thi ko dung
    private void updateLiked(long imageItemId, boolean isLiked, int likeCount) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ImageItemDB item = realm.where(ImageItemDB.class)
                        .equalTo("ImageItemId", imageItemId).findFirst();
                if (item != null) {
                    item.setLiked(isLiked);
                    item.setLikeCount(likeCount);
                }
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //multi image like facebook

    @BindView(R.id.frameLayoutFacebook)
    FrameLayout frameLayoutFacebook;
    @BindView(R.id.linearDescription)
    LinearLayout linearDescription;
    @BindView(R.id.txtDescription)
    ExpandableTextView txtDescription;
    @BindView(R.id.rvCollage)
    RecyclerView rvCollage;

    //link preview
    @BindView(R.id.layoutPreviewLink)
    RelativeLayout layoutPreviewLink;
    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.imgClosePreview)
    ImageView imgClosePreview;
    @BindView(R.id.txt1Preview)
    TextView txt1Preview;
    @BindView(R.id.txt2Preview)
    TextView txt2Preview;

    private void initFacebookPost() {
        //close preview ko hien
        imgClosePreview.setVisibility(View.GONE);

        //an image/video
        frameLayoutImage.setVisibility(View.GONE);
        frameLayoutVideo.setVisibility(View.GONE);
        frameLayoutFacebook.setVisibility(View.VISIBLE);

        //bind data
        //show facebook post
        if (item.getItemContent() != null && item.getItemContent().size() > 0) {

            layoutPreviewLink.setVisibility(View.GONE);
            ArrayList<MediaLocal> list = Item.getMediaLocal(item.getItemContent(), item.isVideo());
            CollageAdapterUrls collageAdapter = new CollageAdapterUrls(context, list);
            rvCollage.setAdapter(collageAdapter);
            collageAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NotNull View view, int position) {
                    //open detail
//                    MyUtils.showToast(context, "Go to gallery");
                    MyUtils.openGallery(context, position, list);
                }
            });
            rvCollage.setVisibility(View.VISIBLE);

            //neu co hinh thi text se o duoi
            //an description bottom
            txtDescription.setVisibility(View.GONE);
            linear3.setVisibility(View.VISIBLE);
            HolderUtils.setDescription(item.getDescription(), txt5, context);
            txt5.setTextIsSelectable(true);

        } else {

            //an description bottom
            linear3.setVisibility(View.GONE);
            ivLike.setVisibility(View.GONE);
            txtDescription.setVisibility(View.VISIBLE);
            HolderUtils.setDescription(item.getDescription(), txtDescription, context);
            txtDescription.setTextIsSelectable(true);

            rvCollage.setVisibility(View.GONE);
            //neu co link preview
            ItemData data = item.getItemData();
            if (data != null) {
                LinkPreview link = data.getLinkPreview();
                if (link != null) {
                    String img = link.getImage();
                    String sitename = link.getSiteName();
                    String title = link.getTitle();
                    String url = link.getLink();

                    txt2Preview.setText(link.getCaption());
                    txt1Preview.setText(link.getTitle());

                    Glide.with(context)
                            .load(img)
                            .override(MyApplication.getInstance().screenWidth, MyApplication.getInstance().heightLinkPreview)
                            .centerCrop()
                            .into(imgPreview);

                    layoutPreviewLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyUtils.openWebPage(url, context);
                        }
                    });

                    layoutPreviewLink.setVisibility(View.VISIBLE);
                } else {
                    layoutPreviewLink.setVisibility(View.GONE);
                }
            } else {
                layoutPreviewLink.setVisibility(View.GONE);
            }
        }

    }

    private void initTelegramPost() {
        //neu description
        linear3.setVisibility(View.VISIBLE);
        HolderUtils.setDescription(item.getDescription(), txt5, context);


        if (item.isVideo()) {
            initVideoPost();
        } else {
            initImagePost();
        }
    }


}



