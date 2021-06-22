package com.topceo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.google.android.gms.ads.MobileAds;
import com.smartapp.collage.CollageAdapterUrls;
import com.smartapp.collage.MediaLocal;
import com.smartapp.collage.OnItemClickListener;
import com.topceo.R;
import com.topceo.autoplayvideo.CameraAnimation;
import com.topceo.chat.ChatUtils;
import com.topceo.config.MyApplication;
import com.topceo.config.VideoListConfig;
import com.topceo.db.TinyDB;
import com.topceo.eventbus.EventImageComment;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.mediaplayer.masterplayer.MasterExoPlayer;
import com.topceo.mediaplayer.pip.DetailBinding;
import com.topceo.mediaplayer.pip.presenter.VideoListItemOpsKt;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.Item;
import com.topceo.objects.image.LinkPreview;
import com.topceo.objects.image.MyItemData;
import com.topceo.objects.other.User;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.utils.MyUtils;
import com.topceo.viewholders.HolderUtils;
import com.topceo.views.ShowMoreTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MH02_PhotoDetailActivity extends AppCompatActivity{
    private Activity context = this;
    private int avatarSize = 0;
    private int widthImage = 0, heightImage = 0;
    private boolean isMyPost = false;
    private Realm realm;
    private ImageItem item;
    private User user;
    private DetailBinding binding;
    private BroadcastReceiver receiver;

    private void initUI() {
        realm = Realm.getDefaultInstance();
        widthImage = MyUtils.getScreenWidth(context);// - roundCorner * 2;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_small);
        img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));

        TinyDB db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }

        txtViewAllComment.setVisibility(View.GONE);
        linearCommentPreview.setVisibility(View.GONE);

        item = MyApplication.imgItem;
        if (item != null) {

            if (user != null && item.getOwner() != null) {
                isMyPost = user.getUserId() == item.getOwner().getUserId();
            }

            heightImage = item.getNeedHeightImage(widthImage);
            img2.setLayoutParams(new FrameLayout.LayoutParams(widthImage, heightImage));

            if (user!=null && item.getOwner()!=null && user.getUserId() == item.getOwner().getUserId()) {
                btnFollow.setVisibility(View.GONE);
            } else {
                btnFollow.setVisibility(View.VISIBLE);
            }

            binding = new DetailBinding(context,
                    user,
                    item,
                    realm,
                    isMyPost,
                    txt3,
                    imgLike,
                    imgSave,
                    txtInput,
                    rv,
                    linearReply,
                    scrollView
                    );

            setTitleBar();
            setUI();
        }
    }

    ///////////////////////////////////////////////////
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgShop)
    ImageView imgShop;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;

    private void setTitleBar() {
        binding.setTitleBar(imgBack, imgShop, relativeChat, txtNumber, context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        //thong bao tat ca media tu Fragment_1_Home_User deu tat toan bo
        sendBroadcast(new Intent(Fragment_1_Home_User.ACTION_SET_MUTE_ALL));
        initViewQuangCao();


        initUI();
        initComment();
        registerReceiver();


    }

    void initViewQuangCao() {
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
    }


    // each data imageItem is just a string in this case
    @BindView(R.id.textView1)
    TextView txt1;
    @BindView(R.id.textView2)
    TextView txt2;
    @BindView(R.id.textView3)
    TextView txt3;
    @BindView(R.id.textView5)
    ShowMoreTextView txt5;
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

    ///////////////////////////////////////////////////////////////////////////

    //VIDEO
    @BindView(R.id.frameLayoutImage)
    FrameLayout frameLayoutImage;
    @BindView(R.id.frameLayoutVideo)
    FrameLayout frameLayoutVideo;
    @BindView(R.id.vvInfo)
    MasterExoPlayer vvInfo;
    @BindView(R.id.ivInfo)
    ImageView ivInfo;
    @BindView(R.id.imgSound)
    ImageView imgSound;
    @BindView(R.id.ivCameraAnimation)
    CameraAnimation ivCameraAnimation;

    private boolean isVideo = false;

    public void setUI() {
        if (item != null) {
            binding.setUserVip(avatarSize, img1, imgVip);

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

            //neu like,comment,share deu = 0 thi an
            binding.setLikeShareComment();
            binding.setContent(txt1, txt2, txt3, txt6, imgMenu, linearLike, imgLike, linearSave, img2, linearComment, linearShare, img1, ivLike);
            /////////////////////////////////////////////////////////////////////////
        }

        binding.setButtonUI(btnFollow);
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


    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_UPDATE_ITEM = "ACTION_UPDATE_ITEM_" + MH02_PhotoDetailActivity.class.getSimpleName();
    public static final String ACTION_UPDATE_STATE_FOLLOW = "ACTION_UPDATE_STATE_FOLLOW_" + MH02_PhotoDetailActivity.class.getSimpleName();
    public static final String ACTION_POST_DELETED = "ACTION_POST_DELETED_";
    public static final String ACTION_COMMENT_DELETED = "ACTION_COMMENT_DELETED_";

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_ITEM)) {
                    ImageItem image = MyApplication.itemReturn;
                    if (image != null) {
                        item = image;
                        setUI();
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_STATE_FOLLOW)) {
                    binding.setButtonUI(btnFollow);
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
                    binding.updateCommentCount();
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
        }
        if (realm != null) {
            realm.close();
        }

        MyApplication.imgItem = null;
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
                binding.updateLikeState();

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



////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST, binding.getmAdapter().getAllItem());
        outState.putParcelable(ImageItem.IMAGE_ITEM, item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            binding.getComments(item.getImageItemId(), 0, 0);

        } else {

            MyUtils.initCookie(context);
            item = savedInstanceState.getParcelable(ImageItem.IMAGE_ITEM);
            ArrayList<ImageComment> list = savedInstanceState.getParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST);
            if (list != null && list.size() > 0 && binding.getmAdapter()!=null) {
                binding.getmAdapter().clear();
                binding.getmAdapter().addListItems(list);
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

    private void initReplyLayout(ImageComment comment) {
        binding.initReplyLayout(comment, linearReply, imgReplyClose, txtReply1, txtReply2);
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //multi image like facebook

    @BindView(R.id.frameLayoutFacebook)
    FrameLayout frameLayoutFacebook;
    @BindView(R.id.linearDescription)
    LinearLayout linearDescription;
    @BindView(R.id.txtDescription)
    ShowMoreTextView txtDescription;
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
            MyItemData data = item.getItemData();
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
//        vvInfo.setVideo(new Video(item.getImageLarge(), 0));
        vvInfo.setUrl(item.getImageLarge());
        VideoListItemOpsKt.playVideo(context, item.getImageLarge());


        //play
        ivInfo.setVisibility(View.VISIBLE);
        VideoListConfig.Companion.configListener(vvInfo, imgSound, ivCameraAnimation, ivInfo);

        imgSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vvInfo.setMute(!vvInfo.isMute());
                VideoListConfig.Companion.setMute(vvInfo, imgSound);
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


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMMENT
    @BindView(R.id.recyclerView1)
    RecyclerView rv;
    @BindView(R.id.editText1)
    SocialAutoCompleteTextView txtInput;
    @BindView(R.id.ripple1)
    LinearLayout rippleSend;

    private void initComment() {
        initAdapter();

        if (scrollView != null) {
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    MyUtils.hideKeyboard(context);
                }
            });
        }

    }
    private void initAdapter() {
        if (binding != null) {
            rv.setFocusable(false);
            ViewCompat.setNestedScrollingEnabled(rv, false);

            binding.initAdapter(rippleSend);
            binding.initHastag();
        }
    }

}



