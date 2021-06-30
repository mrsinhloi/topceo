package com.topceo.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.desmond.squarecamera.myproject.APIConstants;
import com.desmond.squarecamera.myproject.ImageUtils;
import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.bottom_navigation.NoSwipePager;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.crop.CropImageActivity;
import com.topceo.crop.ImageActivity;
import com.topceo.crop.bitmap.BitmapLoader;
import com.topceo.crop.utils.AppAnalyze;
import com.topceo.crop.utils.FileUtils;
import com.topceo.db.TinyDB;
import com.topceo.dynamic_link.DynamicData;
import com.topceo.fragments.FragmentPosition;
import com.topceo.fragments.Fragment_1_Home_Admin;
import com.topceo.fragments.Fragment_1_Home_SonTung;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.gallery.ParcelableImageModel;
import com.topceo.gallery.PickImageActivity;
import com.topceo.group.GroupDetailActivity;
import com.topceo.group.members.ApproveMemberActivity;
import com.topceo.login.MH15_SigninActivity;
import com.topceo.login.WelcomeActivity;
import com.topceo.mediaplayer.audio.PlayerService;
import com.topceo.mediaplayer.pip.presenter.VideoListItemOpsKt;
import com.topceo.mediaplayer.preview.VideoSelectThumbnailActivity;
import com.topceo.notify.Fragment_3_Notify;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.ImageSize;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserSearchChat;
import com.topceo.objects.promotion.Promotion;
import com.topceo.objects.promotion.PromotionScreen;
import com.topceo.objects.update.UpdateVersion;
import com.topceo.onesignal.NotifyObject;
import com.topceo.onesignal.NotifyType;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.search.SearchActivity;
import com.topceo.selections.SelectFavoritesActivity;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyPermission;
import com.topceo.utils.MyUtils;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.topceo.utils.PermissionUtils;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.event.EventSearchContactChat_Receive;
import com.workchat.core.event.EventSearchContactChat_Request;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.PermissionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class MH01_MainActivity extends AppCompatActivity {
    private static final int REQUEST_SELECT_FAVORITE = 12;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.noSwipePager)
    NoSwipePager viewPager;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    private Activity context = this;
    public static boolean isExist = false;
    private NotifyObject notify;
    @BindView(R.id.imgAddUser)
    ImageView imgAddUser;
    @BindView(R.id.imgShop)
    ImageView imgShop;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgViewAll)
    ImageView imgViewAll;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    Bundle bundle;
    private TinyDB db;
    private User user;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.imgLogo)
    ImageView imgLogo;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        progressDeeplink(intent);
    }

    private int cutoutHeight = 0;

    private void showSystemUI() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {  // For full screen with cutout
            // Allow the activity to layout under notches if the fill-screen option
            // was turned on by the user

            View decorView = getWindow().getDecorView();

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

        }*/

        if (isCallShowCase) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (getWindow().getDecorView().getRootWindowInsets() != null) {
                    DisplayCutout displayCutout = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                    if (displayCutout != null) {
                        cutoutHeight = displayCutout.getSafeInsetTop();
                        initShowcaseView();
//                    MyUtils.log(cutoutHeight + "");
                    }
                }
            } else {
                initShowcaseView();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        showSystemUI();
    }

    public static MH01_MainActivity mh01_mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mh01_mainActivity = this;
        isExist = true;
        db = new TinyDB(context);

        super.onCreate(savedInstanceState);
        if (!MyUtils.checkInternetConnection(this)) {
            MyUtils.showToast(this, R.string.khongCoInternet);
            finish();
            return;
        }


        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //hide icon navigation
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);

        //tat man hinh sigin
        sendBroadcast(new Intent(MH15_SigninActivity.FINISH_ACTIVITY));

        //Giu nguyen icon goc, ko can tint color
        navigation.setItemIconTintList(null);
        navigation.setSelectedItemId(R.id.navigation_2);

        //init
        imgAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MH08_SuggestActivity.class);
                startActivity(intent);
            }
        });
        imgShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.checkPermission()) {
                    startActivity(new Intent(context, ShoppingActivity.class));
                } else {
                    PermissionUtils.requestPermission(MH01_MainActivity.this, MyPermission.MY_PERMISSIONS_REQUEST_STORAGE);
                }
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchActivity.class);
                startActivity(intent);
            }
        });

        imgViewAll.setVisibility(View.GONE);
        imgViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(Fragment_3_Notify.ACTION_VIEW_ALL));
            }
        });

        relativeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainChatActivity.class);
                startActivity(intent);
            }
        });
        ///////////////////////////////

        final boolean isLogined = db.getBoolean(TinyDB.IS_LOGINED);
        if (isLogined) {
            //doc lai thong tin user, neu chua suggest thi moi hien
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
                if (user.isBanned()) {
                    MyUtils.showAlertDialog(MH01_MainActivity.this, R.string.account_is_banned, true);
                } else {
                    if (user.getReleaseDate() == 0) {
                        initUI();
                        initListUserFollower(user.getUserId());
                    } else {
                        if (user.isHashtagSuggested()) {//da suggest roi
                            finish();
                            Intent intent = new Intent(context, MH12_CountDownActivity.class);
                            intent.putExtra(MH12_CountDownActivity.NUMBER, user.getReleaseDate());
                            intent.putExtra(MH12_CountDownActivity.MESSAGE, user.getReleaseMessage());
                            startActivity(intent);
                        } else {
                            initUI();
                        }

                    }
                }

            }

        } else {
            gotoLogin();
        }

        test();

    }

    private void initUI() {
        //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
//        MyApplication.whenLoginSuccess();
        init();
        getNotifyNumber();
        ChatUtils.setChatUnreadNumber(txtNumber);
    }

    private void init() {
        /*if (initClass != null && initClass.getStatus() != AsyncTask.Status.FINISHED) {
            initClass.cancel(true);
        }
        initClass = new InitClass();
        initClass.execute();*/


        //check
        //neu vao tu notify thi bao lai cho MessageReceivingService biet de no lam tiep
        if (user != null) {

            //build tabs load data truoc
            buildFragments();
            if (user.isHashtagSuggested()) {//da suggest roi
                //neu da suggest thi moi hien promotion
                initData();
            } else {
                //vao man hinh suggest
                Intent intent = new Intent(context, SelectFavoritesActivity.class);
                startActivityForResult(intent, REQUEST_SELECT_FAVORITE);
            }


        } else {
            gotoLogin();
        }

    }

    private boolean isCallShowCase = false;

    private void initData() {

        Bundle b = getIntent().getExtras();
        if (b != null) {
            notify = b.getParcelable(NotifyObject.NOTIFY_OBJECT);
            if (notify != null) {
                navigateNotify(notify);
            }

            //neu mo room chat
            String roomId = b.getString(Room.ROOM_ID, "");
            if (!TextUtils.isEmpty(roomId)) {
                com.workchat.core.utils.MyUtils.openChatRoom(getApplicationContext(), roomId, "");
            }
        }


        //promotion
        getPromotion();
        checkVersion();

        //data
        saveListUserFollowing();
        registerToken();
        registerReceiver();

        //start media player service
        startMediaPlayer();

        //deeplink
        progressDeeplink(getIntent());

//        //showcase
//        isCallShowCase = true;
    }

    private void gotoLogin() {
        startActivity(new Intent(context, WelcomeActivity.class));
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_1:
                    switchFragment(FragmentPosition.HOME);
                    return true;
                case R.id.navigation_2:
                    switchFragment(FragmentPosition.MTP);
                    return true;
                case R.id.navigation_3://them anh moi
//                    startActivity(new Intent(context, MH03_PostActivity.class));
//                    pickImage();
//                    startActivity(new Intent(context, PostLikeFacebookActivity.class));
                    imgShop.performClick();
                    return false;
                case R.id.navigation_4:
                    switchFragment(FragmentPosition.NOTIFY);
                    return true;
                case R.id.navigation_5:
                    switchFragment(FragmentPosition.PROFILE);
                    return true;
            }
            return false;
        }

    };
    private List<Fragment> fragments = new ArrayList<>(4);

    private void buildFragments() {
        //listener
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //custom icon tab user
        Menu menu = navigation.getMenu();
        MenuItem item = menu.findItem(R.id.navigation_5);

//        BottomNavigationViewHelper.removeShiftMode(navigation);//disable BottomNavigationView shift mode de them dc 5 tab


        //load array
        fragments.clear();

        //Tab Home
        if (user != null) {

            //fragment of user
            Bundle b = new Bundle();
            b.putBoolean(Fragment_1_Home_User.IS_CAN_POST, true);
            Fragment_1_Home_User f1 = new Fragment_1_Home_User();
            f1.setArguments(b);

            if (user.getRoleId() == User.ADMIN_ROLE_ID) {
                boolean isChecked = db.getBoolean(User.IS_SHOW_ADMIN_PAGE, false);
                if (isChecked) {
                    fragments.add(new Fragment_1_Home_Admin());
                } else {
                    fragments.add(f1);
                }
            } else {
                fragments.add(f1);
            }

            //set icon
            setIconUser(item);
        }

        //Tab sontung
        User sonTung = new User();
        sonTung.setUserId(1);
        Bundle b = new Bundle();
        b.putParcelable(User.USER, sonTung);
        Fragment_1_Home_SonTung f1 = new Fragment_1_Home_SonTung();
        f1.setArguments(b);
        fragments.add(f1);


        //Tab notify
        fragments.add(new Fragment_3_Notify());
        //Tab me
//        fragments.add(MH22_Fragment_Setting.newInstance("me"));
        Fragment_Profile_Owner f2 = Fragment_Profile_Owner.newInstance(user.getUserMedium(), true);
        fragments.add(f2);

        //first lauch
        adapter = new MyFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(fragments.size());

        //Khoi tao load tab dau tien position 1
        viewPager.setCurrentItem(1);


    }

    private void setIconUser(MenuItem item) {
        if (user != null) {
            int height = getResources().getDimensionPixelSize(R.dimen.tool_bar_height);
            int avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size_medium_smaller);
            RequestOptions options = RequestOptions
                    .centerCropTransform()
                    .override(avatarSize, avatarSize)
                    .circleCropTransform()
                    .placeholder(R.drawable.ic_no_avatar);
            Glide.with(this).asBitmap()
                    .load(user.getAvatarSmall())
                    .apply(options)
                    .into(new CustomTarget<Bitmap>(avatarSize, avatarSize) {
                              @Override
                              public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                  int padding = height / 2;
                                  resource = pad(resource, padding, padding);
                                  Drawable drawable = new BitmapDrawable(getResources(), resource);
                                  item.setIcon(drawable);
                              }

                              @Override
                              public void onLoadCleared(@Nullable Drawable placeholder) {

                              }
                          }
                    );
        }
    }

    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x, Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawColor(Color.TRANSPARENT);
        can.drawBitmap(Src, padding_x / 2, padding_y / 2, null);
        return outputimage;
    }

    public static final int POSITION_NOTIFY = 2;

    private void switchFragment(int position) {
        if (position == FragmentPosition.HOME || position == FragmentPosition.MTP || position == FragmentPosition.NOTIFY) {
            //tat video man hinh profile
            sendBroadcast(new Intent(Fragment_1_Home_User.ACTION_SET_FRAGMENT_IN_PROFILE_MUTE));
        }


        if (position == POSITION_NOTIFY) {
            imgViewAll.setVisibility(View.VISIBLE);
            relativeChat.setVisibility(View.VISIBLE);
            imgShop.setVisibility(View.GONE);
        } else {
            imgViewAll.setVisibility(View.GONE);
            relativeChat.setVisibility(View.VISIBLE);
//            imgShop.setVisibility(View.VISIBLE);
        }

        //neu click cung vi tri thi scroll ve dau
        int oldPost = viewPager.getCurrentItem();
        if (oldPost == position) {
            switch (position) {
                case FragmentPosition.HOME://home
                    Intent intent = new Intent(Fragment_1_Home_User.ACTION_SCROLL_TO_TOP);
                    intent.putExtra(Fragment_1_Home_User.IS_HOME, true);
                    sendBroadcast(intent);
                    break;
                case FragmentPosition.MTP://sontung
                    intent = new Intent(Fragment_1_Home_User.ACTION_SCROLL_TO_TOP);
                    intent.putExtra(Fragment_1_Home_User.IS_HOME, false);
                    sendBroadcast(intent);
                    break;
                case FragmentPosition.NOTIFY://me
                    intent = new Intent(Fragment_3_Notify.ACTION_SCROLL_TO_TOP);
                    sendBroadcast(intent);
                    break;
                case FragmentPosition.PROFILE://me
                    intent = new Intent(Fragment_Profile_Owner.ACTION_SCROLL_TO_TOP);
                    sendBroadcast(intent);
                    break;
            }
        }
        viewPager.setCurrentItem(position, false);

        //vi tri nao dc chon thi dc phep phat video
        /*if(position == 0 || position == 1){
            Intent intent = new Intent(Fragment_1_Home_User.ACTION_CAN_PLAY_VIDEO);
            intent.putExtra(Fragment_1_Home_User.IS_HOME, position == 0);
            intent.putExtra(Fragment_1_Home_User.IS_STOP, false);//chon tab nao hat tab do
            sendBroadcast(intent);
        }else{
            Intent intent = new Intent(Fragment_1_Home_User.ACTION_CAN_PLAY_VIDEO);
            intent.putExtra(Fragment_1_Home_User.IS_STOP, true);//chon tab nao hat tab do
            sendBroadcast(intent);
        }*/

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //setBadget(1, count);
    private void setBadget(int position, int number) {
        if (navigation != null) {
            BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);
            View v = bottomNavigationMenuView.getChildAt(position);
            BottomNavigationItemView itemView = (BottomNavigationItemView) v;

            //Reset current badge
            if (itemView.getChildCount() > 2) {
                itemView.removeView(itemView.getChildAt(2));
            }
            if (number > 0) {
                View badge = LayoutInflater.from(this).inflate(R.layout.notification_badge, bottomNavigationMenuView, false);
                TextView txtBadge = (TextView) badge.findViewById(R.id.txtBadge);
                txtBadge.setText(String.valueOf(number));
                itemView.addView(badge);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private MyFragmentAdapter adapter;


    private class MyFragmentAdapter extends FragmentStatePagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        /*@Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }*/
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void navigateNotify(NotifyObject obj) {
        if (obj != null) {
//            MyUtils.showToastDebug(context, "Notify đã vào màn hình chính");
            switch (obj.getNotifyTypeId()) {

                //MỞ APP
                case NotifyType.TYPE_0_SYSTEM:
//                case NotifyType.TYPE_6_NEW_CHAT_MESSAGE:
//                case NotifyType.TYPE_7_NEW_CHAT_FILE:

                    break;


                //MỞ PROFILE
                case NotifyType.TYPE_1_FOLLOWING:
                case NotifyType.TYPE_2_ACCEPT_FOLLOWING:
                case NotifyType.TYPE_11_FOLLOWING_SHARE:
                    MyUtils.gotoProfile(obj.getActionUserName(), context);
                    break;


                //MỞ CHI TIẾT HÌNH ẢNH
                case NotifyType.TYPE_3_UPLOAD_NEW_PHOTO:
                case NotifyType.TYPE_4_NEW_LIKE:
                case NotifyType.TYPE_10_FOLLOWING_LIKE:
                case NotifyType.TYPE_5_NEW_COMMENT:
                case NotifyType.TYPE_8_MENTION_IN_COMMENT:
                case NotifyType.TYPE_9_FOLLOWING_COMMENT:
                case NotifyType.TYPE_15_MENTION_IN_IMAGE:
                case NotifyType.TYPE_13_COMMENT_HAS_REPLY:
                    gotoPhotoDetail(obj);
                    break;

                //mo link ben ngoai
                case NotifyType.TYPE_OPEN_EXTERNAL_LINK:
                    String link = obj.getExternalLink();
                    if (!TextUtils.isEmpty(link)) {
                        MyUtils.openWebPage(link, context);
                    }
                    break;


                //CHUYỂN ĐẾN MEDIA
                case NotifyType.TYPE_12_NEW_MEDIA:
                    break;

                //VAO GROUP
                case NotifyType.TYPE_17_GROUP_INVITE:
                    if (obj.getGroupId() > 0) {
                        GroupDetailActivity.Companion.openActivity(context, obj.getGroupId(), false);
                    }
                    break;
                case NotifyType.TYPE_16_GROUP_MEMBER_REQUEST:
                    if (obj.getGroupId() > 0) {
                        ApproveMemberActivity.Companion.openActivity(context, obj.getGroupId());
                    }
                    break;


            }

            //refresh lai man hinh notify,
            Intent intent = new Intent(Fragment_3_Notify.ACTION_REFRESH_NOTIFY);
            sendBroadcast(intent);
        }
    }

    private void gotoPhotoDetail(final NotifyObject notify) {
        //get ImageComment vao man hinh MH02_PhotoDetailActivity
        if (notify.getImageItemId() > 0) {
            Webservices.getImageItem(notify.getImageItemId()).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ImageItem image = (ImageItem) task.getResult();
                            if (image != null) {
                                image.setNotifyObject(notify);
                                MyUtils.gotoDetailImage(context, image);
                            }
                        } else {
                            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                            MyUtils.log("Fragment_1_Home_User - getNewFeed() - Exception = " + ((ANError) task.getError()).getErrorCode());

                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                gotoPhotoDetail(notify);
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
                    } else {

                    }
                    return null;
                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_PICK_IMAGE = "ACTION_PICK_IMAGE";
    public static final String ACTION_GO_TO_NOTIFY = "ACTION_GO_TO_NOTIFY";
    public static final String ACTION_RESTART_APP = "ACTION_RESTART_APP";
    public static final String ACTION_FINISH = "ACTION_FINISH_MAIN_ACTIVITY";
    public static final String ACTION_SHOW_PROMOTION = "ACTION_SHOW_PROMOTION";
    public static final String ACTION_CHANGE_FRAGMENT = "ACTION_CHANGE_FRAGMENT";
    public static final String ACTION_OPEN_PROFILE = "ACTION_OPEN_PROFILE";

    public static final String ACTION_GET_NUMBER_NOTIFY = "ACTION_GET_NUMBER_NOTIFY";
    public static final String ACTION_SET_NUMBER_CHAT_UNREAD = "ACTION_SET_NUMBER_CHAT_UNREAD";
    public static final String ACTION_CHANGE_ICON = "ACTION_CHANGE_ICON_topceo";

    public static final String ACTION_CHECK_PERMISSION = "ACTION_CHECK_PERMISSION";

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(ACTION_PICK_IMAGE)) {
                    pickImage();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_GO_TO_NOTIFY)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        notify = b.getParcelable(NotifyObject.NOTIFY_OBJECT);
                        if (notify != null) {
                            navigateNotify(notify);
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_RESTART_APP)) {
                    restartApp();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_FINISH)) {
                    finish();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_SHOW_PROMOTION)) {
                    getPromotion();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_CHANGE_FRAGMENT)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        int position = b.getInt(FragmentPosition.FRAGMENT_POSITION, -1);
                        if (position >= 0) {
                            switchFragment(position);
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_OPEN_PROFILE)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        String username = b.getString(User.USER_NAME);
                        if (!TextUtils.isEmpty(username)) {
                            MyUtils.gotoProfile(username, MH01_MainActivity.this);
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_GET_NUMBER_NOTIFY)) {
                    getNotifyNumber();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                } else if (intent.getAction().equalsIgnoreCase(ACTION_CHANGE_ICON)) {
                    user = (User) db.getObject(User.USER, User.class);
                    if (user != null) {
                        Menu menu = navigation.getMenu();
                        MenuItem item = menu.findItem(R.id.navigation_5);
                        setIconUser(item);
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_CHECK_PERMISSION)) {
                    if (PermissionUtils.checkPermission()) {

                    } else {
                        PermissionUtils.requestPermission(MH01_MainActivity.this, REQUEST_STORAGE_READ_VIDEO);
                    }
                }

            }
        };

        IntentFilter intent = new IntentFilter();
        intent.addAction(ACTION_GO_TO_NOTIFY);
        intent.addAction(ACTION_PICK_IMAGE);
        intent.addAction(ACTION_RESTART_APP);
        intent.addAction(ACTION_FINISH);
        intent.addAction(ACTION_GET_NUMBER_NOTIFY);
        intent.addAction(ACTION_SHOW_PROMOTION);
        intent.addAction(ACTION_CHANGE_FRAGMENT);
        intent.addAction(ACTION_OPEN_PROFILE);
        intent.addAction(ACTION_SET_NUMBER_CHAT_UNREAD);
        intent.addAction(ACTION_CHANGE_ICON);
        intent.addAction(ACTION_CHECK_PERMISSION);



        registerReceiver(receiver, intent);

    }


    private void restartApp() {
        /*Intent intent = getIntent();
        finish();
        startActivity(intent);*/

        startActivity(new Intent(context, MH00_LoadingActivity.class));
        finish();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        isExist = false;

        MyApplication.getInstance().isFirst = true;
        ChatApplication.Companion.whenAppDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);


        if (readTask != null && readTask.getStatus() != AsyncTask.Status.FINISHED) {
            readTask.cancel(true);
        }


        //Huy toan bo request
        try {
            AndroidNetworking.forceCancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //////////////////////////////////////////////////////////////////////////////
    //start thi start alarm, cung pendingIntent thi no tu replace alarm
    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    //////////////////////////////////////////////////////////////////////////////

    /**
     * Su dung o man hinh MainActvity va SiginActivity
     * //luu danh sach user following de sung dung trong man hinh UserProfile, set trang thai co dang following khong
     */
    private void saveListUserFollowing() {
        final long start = System.currentTimeMillis();
        if (user != null) {
            //lay danh sach follower, following
            Webservices.getListUserFollowing().continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {

                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            com.topceo.ads.ReturnResult result = (com.topceo.ads.ReturnResult) task.getResult();
                            if (result != null) {
                                ArrayList<Double> list = (ArrayList<Double>) result.getData();
                                //luu vao database
                                MyUtils.writeListUserFollowingToFile(list);
                                MyUtils.howLong(start, "saveListUserFollowing");
                            }
                        }
                    }
                    return null;
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_sky, menu);
        //you can add some logic (hide it if the count == 0)

        /*if (badgeCount > 0) {
            ActionItemBadge.update(this, menu.findItem(R.id.action_chat),
                    getResources().getDrawable(R.drawable.ic_chat_white_24dp), ActionItemBadge.BadgeStyles.RED, badgeCount);//FontAwesome.Icon.faw_android
        } else {
            ActionItemBadge.hide(menu.findItem(R.id.action_chat));
        }*/


        return true;
    }


    private int badgeCount = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_chat:
                return true;
            case R.id.action_add_user:
                Intent intent = new Intent(context, MH08_SuggestActivity.class);
                startActivity(intent);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*private void checkVersionUpdate() {
        if (MyUtils.checkInternetConnection(context)) {
            if (task == null) {
                task = new CheckVersionTask();
                task.execute();
            } else {
                if (task.getStatus() == AsyncTask.Status.FINISHED) {
                    task = new CheckVersionTask();
                    task.execute();
                }
            }
        }
    }

    private CheckVersionTask task;

    private class CheckVersionTask extends AsyncTask<Void, Void, Boolean> {
        String store_version = BuildConfig.VERSION_NAME;

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean isNeedUpdate = false;
            try {
                store_version = MarketVersionChecker.getMarketVersion(getPackageName());
                String device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

                if (!TextUtils.isEmpty(store_version) && !TextUtils.isEmpty(device_version)) {
                    *//*if (store_version.compareTo(device_version) > 0) {
                        // need update
                        isNeedUpdate = true;
                    }*//*
                    isNeedUpdate = new Version(store_version).isHigherThan(device_version);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isNeedUpdate;
        }

        @Override
        protected void onPostExecute(Boolean isNeedUpdate) {
            super.onPostExecute(isNeedUpdate);
            if (isNeedUpdate && !isFinishing()) {
                new MaterialDialog.Builder(context)
                        .title(R.string.update_notify)
                        .content(R.string.update_have_new_version)
                        .positiveText(R.string.update)
                        .autoDismiss(false)
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                final Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                                startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .show();
            }

        }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void pickImage() {

        if (PermissionUtils.checkPermission()) {
            Intent intent = new Intent(this, PickImageActivity.class);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            PermissionUtils.requestPermission(this, MyPermission.MY_PERMISSIONS_REQUEST_CAMERA);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {
                    case MyPermission.MY_PERMISSIONS_REQUEST_CAMERA:
                        pickImage();
                        break;
                    case MyPermission.MY_PERMISSIONS_REQUEST_READ_LOCATION:
//                        gpsFunction();
                        break;
                    case MyPermission.MY_PERMISSIONS_REQUEST_STORAGE:
                        imgShop.performClick();
                        break;
                    case REQUEST_STORAGE_READ_VIDEO:
                        ImageItem item = MyApplication.imgItem;
                        if (item != null) {
                            VideoListItemOpsKt.playVideoImageItem(context, item.getImageLarge());
                        }
                        break;

                }

            }

        }
    }

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    public static final int CROP_FILE = 3;

    private void pickImgfrmCam() {
        try {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, APIConstants.SELECTED_IMAGE);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void pickImgfrmGal() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            final Intent chooserIntent = Intent.createChooser(intent, "Select Source");
            startActivityForResult(chooserIntent, PICK_FROM_FILE);
        } catch (ActivityNotFoundException e) {
        }
    }


    public static final int REQUEST_STORAGE_READ_VIDEO = 56;
    public static final int REQUEST_CAMERA = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    if (data != null) {//VIDEO
                        String videoUrl = data.getStringExtra(APIConstants.VIDEO_URL);
                        if (videoUrl != null) {
                            //tu quay la da gioi han 1 phut roi, ko can trim, chi can chon cover la post len
                            Intent intent = new Intent(context, VideoSelectThumbnailActivity.class);
                            intent.putExtra(APIConstants.VIDEO_URL, videoUrl);
                            startActivity(intent);

                        /*Intent intent = new Intent(context, VideoPreviewActivity.class);
                        intent.putExtra(APIConstants.VIDEO_URL, videoUrl);
                        startActivity(intent);*/
                        }
                    } else {//IMAGE
                        processImage();
                    }
                    break;

                case PICK_FROM_CAMERA: {
                    Intent intent = new Intent(this, CropImageActivity.class);
                    startActivityForResult(intent, CROP_FILE);
                }
                break;
                case PICK_FROM_FILE: {
                    Uri selectedUri = data.getData();
                    String filePath = FileUtils.getPath(this, selectedUri);

                    Bitmap bmtmp = BitmapLoader.load(this, new int[]{ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT}, filePath);

                    try {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bmtmp.compress(APIConstants.mOutputFormat, ImageUtils.IMAGE_QUALITY, bytes);

                        File f = new File(APIConstants.SELECTED_IMAGE.getPath());
                        f.createNewFile();
                        //write the bytes in file
                        FileOutputStream fo = new FileOutputStream(f);
                        fo.write(bytes.toByteArray());

                        // remember close de FileOutput
                        fo.flush();
                        fo.close();
                    } catch (Exception e) {
                    }
                    try {
                        bmtmp.recycle();
                        bmtmp = null;
                    } catch (Exception e) {
                    }

                    Intent intent = new Intent(this, CropImageActivity.class);
                    startActivityForResult(intent, CROP_FILE);
                }
                break;
                case CROP_FILE: {
                    AppAnalyze appAnalyze = AppAnalyze.getInstance();
                    appAnalyze.setImageUri(APIConstants.SELECTED_IMAGE);
                    Intent intent = new Intent(context, ImageActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }

        if (requestCode == REQUEST_SELECT_FAVORITE) {
            if (resultCode == RESULT_OK) {
                if (user != null) {
                    if (user.getReleaseDate() == 0) {
                        initData();
                        //sau khi chon xong refresh lai data man hinh user
                        Intent intent = new Intent(Fragment_1_Home_User.ACTION_REFRESH);
                        sendBroadcast(intent);
                    } else {
                        finish();
                        Intent intent = new Intent(context, MH12_CountDownActivity.class);
                        intent.putExtra(MH12_CountDownActivity.NUMBER, user.getReleaseDate());
                        intent.putExtra(MH12_CountDownActivity.MESSAGE, user.getReleaseMessage());
                        startActivity(intent);

                    }
                }
            } else {
                //chua chon xong thi thoat
                finish();
            }
        }

    }

    private void processImage() {
        //phan tich hinh anh
//        MyUtils.analyMemory(APIConstants.SELECTED_IMAGE.getPath());

        ////
        AppAnalyze appAnalyze = AppAnalyze.getInstance();
        appAnalyze.setImageUri(APIConstants.SELECTED_IMAGE);
        Intent intent = new Intent(context, ImageActivity.class);
        startActivity(intent);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            MyUtils.log("ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            PlayerService.MyBinder binder = (PlayerService.MyBinder) iBinder;
            mService = binder.getService();
            isBound = true;

            getRandomNumberFromService(); // return a random number from the service
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            MyUtils.log("ServiceConnection: disconnected from service.");
            isBound = false;
        }
    };

    private void getRandomNumberFromService() {
        MyUtils.log("getRandomNumberFromService: Random number from service: " + mService.getRandomNumber());
    }

    private void bindService() {
        Intent intent = new Intent(context, PlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private PlayerService mService;
    private boolean isBound;

    private void startMediaPlayer() {
//        Intent intent = new Intent(context, PlayerService.class);
//        startService(intent);
        //neu dang co service chay thi hien thi player
        if (MyUtils.isMyServiceRunning(context, PlayerService.class)) {
            //sau khi start service thi moi duoc bind service de lay thong tin
            bindService();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        startMediaPlayer();

        //load san danh sach gallery
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveGallery();
            }
        }, 2000);*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////
    //DOC SAN GALLERY
    private void saveGallery() {
        if(!isFinishing()){
            if (PermissionUtils.isAllowReadSdCard()) {
                getGalleryPhotoAndVideo();
            }
        }
    }

    /*private ArrayList<ParcelableImageModel> getGalleryPhotos() {
        ArrayList<ParcelableImageModel> galleryList = new ArrayList<ParcelableImageModel>();
        long start = SystemClock.elapsedRealtime();
        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    ParcelableImageModel item = new ParcelableImageModel();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.setUrl(imagecursor.getString(dataColumnIndex));

                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        MyUtils.howLong(start, "get gallery photo");
        return galleryList;
    }*/

    private ReadImageTask readTask;

    private void getGalleryPhotoAndVideo() {
        if (readTask != null && readTask.getStatus() != AsyncTask.Status.FINISHED) {
            readTask.cancel(true);
        }

        readTask = new ReadImageTask();
        readTask.execute();
    }

    private class ReadImageTask extends AsyncTask<Void, Void, Void> {

        ArrayList<ParcelableImageModel> galleryList = new ArrayList<ParcelableImageModel>();
        private Cursor cursor;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*try {

                // Get relevant columns for use later.
                String[] projection = {
//                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.DATE_ADDED
//                        MediaStore.Files.FileColumns.MEDIA_TYPE,
//                        MediaStore.Files.FileColumns.MIME_TYPE,
//                        MediaStore.Files.FileColumns.TITLE
                };

                // Return only video and image metadata.
                String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                        *//*+ " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;*//*

                Uri queryUri = MediaStore.Files.getContentUri("external");

                CursorLoader cursorLoader = new CursorLoader(
                        context,
                        queryUri,
                        projection,
                        selection,
                        null, // Selection args (none).
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
                );

                cursor = cursorLoader.loadInBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            try {

                Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.MediaColumns.DATA};
                // Return only video and image metadata.
                String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                cursor = getContentResolver().query(
                        uri, projection, null, null,
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (cursor != null && cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        ParcelableImageModel item = new ParcelableImageModel();

                        int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                        String url = cursor.getString(dataColumnIndex);

                        item.setUrl(url);
                        item.setVideo(false);
                        galleryList.add(item);

                    }

                    //luu xuong bo nho
                    if (galleryList != null && galleryList.size() > 0) {
                        db.putListImageGallery(ParcelableImageModel.LIST_IMAGE_MODEL, galleryList);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void registerToken() {

        Webservices.addUserEndpoint(context).continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {
                if (task.getError() == null) {
                    if (task.getResult() != null) {
                        boolean isSuccess = (boolean) task.getResult();
                        if (isSuccess) {
                            MyUtils.showToastDebug(context, "Đăng ký token thành công");
                        }
                    }
                }
                return null;
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void getNotifyNumber() {
        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getNotifyNumber(context).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ReturnResult result = (ReturnResult) task.getResult();
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    try {
                                        String s = (String) result.getData();
                                        JSONObject json = new JSONObject(s);
                                        int count = json.getInt("Count");
                                        setBadget(3, count);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    String message = result.getErrorMessage();
                                    if (!TextUtils.isEmpty(message)) {
                                        MyUtils.showAlertDialog(context, message);
                                    }
                                }
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
                                            getNotifyNumber();
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
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void progressDeeplink(Intent intent) {
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null && data.isHierarchical()) {
                String id = data.getQueryParameter("id");
                if (!TextUtils.isEmpty(id)) {
                    //mo hinh co id nay
                    try {
                        long imageItemId = Long.parseLong(id);
                        MyUtils.gotoDetailImage(context, imageItemId);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            //from dynamic link
            long imageId = db.getLong(DynamicData.IMAGE_ID, 0);
            if (imageId > 0) {
                MyUtils.gotoDetailImage(context, imageId);
                db.putLong(DynamicData.IMAGE_ID, 0);
            }
            //profile
            long profileId = db.getLong(DynamicData.PROFILE_ID, 0);
            if (profileId > 0) {
                MyUtils.gotoProfile(profileId, context);
                db.putLong(DynamicData.PROFILE_ID, 0);
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void getPromotion() {
        MyApplication.apiManager.getPromotion(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {

                            Type collectionType = new TypeToken<List<Promotion>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<Promotion> list = (ArrayList<Promotion>) result.getData();
                                    if (list != null && list.size() > 0) {
//                                        MyUtils.showAlertDialog(context, "HAVE PROMOTION");
                                        MyApplication.setPromotionList(list);

                                        //kiem tra co promotion cho man hinh MTP thi hien len
                                        PromotionScreen.navigationScreen(context, PromotionScreen.STARTAPP);
                                        PromotionScreen.navigationScreen(context, PromotionScreen.MTP);

                                    } else {

                                        //gia lap
                                        /*Promotion p = new Promotion();
                                        p.setTitle("Miễn phí VIP MEMBER 2 tháng");
                                        p.setDescription("Miễn phí gói VIP 2 tháng cho Sky mới đăng kí trải nghiệm");
                                        p.setBanner("https://ehubstar.blob.core.windows.net/promo/Viptrial.png");
                                        p.setAutoApply(false);
                                        p.setPromotionId(1);
                                        showDialog(p);*/
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
    private void test() {
//        String deeplink = "skysocial://promotion?screen=home";
        /*String deeplink = "skysocial://promotion?screen=media&id=120";

        Uri data = Uri.parse(deeplink);
        if (data != null && data.isHierarchical()) {
            String screen = data.getQueryParameter("screen");
            if (!TextUtils.isEmpty(screen)) {
                //mo hinh co id nay
                if(screen.equalsIgnoreCase("media")){
                    String id = data.getQueryParameter("id");
                    MyUtils.log(id);
                }

            }
        }*/
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkVersion() {
        AndroidNetworking.post(Webservices.API_URL + "system/getVersionInfo")
                .setOkHttpClient(MyApplication.getClient())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, UpdateVersion.class, false);


                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                if (result.getData() != null) {
                                    UpdateVersion version = (UpdateVersion) result.getData();
                                    if (version != null) {
                                        showDialogUpdate(version);
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        if (ANError.getMessage() != null)
//                            MyUtils.showToast(context, ANError.getMessage());
                    }
                });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////
    private void showDialogUpdate(UpdateVersion version) {
        if (version.getVersion() > 0 && version.getVersion() > BuildConfig.VERSION_CODE) {
            //thông báo cập nhật, xử lý xem có bắt buộc phải cập nhật không
            boolean isForceUpdate = version.isForceUpdate();
            //neu version hiện tại <= version yc update thì bắt buộc update
            if (version.getForceUpdateVersion() > 0 && BuildConfig.VERSION_CODE <= version.getForceUpdateVersion()) {
                isForceUpdate = true;
            }

            //UPDATE
            if (context != null && !isFinishing()) {
                //Neu la phien ban debug thi yeu cau go app, cai lai
                if (BuildConfig.DEBUG) {
                    String notify = getString(R.string.confirm_update_notify_debug);
                    new MaterialDialog.Builder(context)
                            .title(R.string.new_update)
                            .titleGravity(GravityEnum.CENTER)
                            .content(notify)
                            .contentGravity(GravityEnum.CENTER)
                            .cancelable(true)
                            .autoDismiss(true)
                            .negativeText(R.string.close)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .positiveText(R.string.uninstall)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                    Uri packageURI = Uri.parse("package:" + getPackageName());
                                    Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .show();
                } else {
                    String versionName = BuildConfig.VERSION_NAME;
                    versionName = versionName.replaceAll("\\d+$", String.valueOf(version.getVersion()));
                    String notify = getString(R.string.confirm_update_notify, versionName);

                    MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                            .title(R.string.new_update)
                            .titleGravity(GravityEnum.CENTER)
                            .content(notify)
                            .contentGravity(GravityEnum.CENTER)
                            .cancelable(!isForceUpdate)
                            .autoDismiss(!isForceUpdate)
                            .positiveText(R.string.update)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    final Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                                    startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
                                    dialog.dismiss();
                                    finish();
                                }
                            });

                    //neu ko ep cap nhat thi cho them option dong lai
                    if (!isForceUpdate) {
                        builder.negativeText(R.string.close);
                        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        });
                    }
                    builder.show();


                }
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////

    View view1;
    View view2;
    View view3;
    View view4;
    View view5;
    View view6;

    //show case view
    private GuideView mGuideView;
    private GuideView.Builder builder;

    private void initShowcaseView() {

        boolean isShowcase = db.getBoolean(TinyDB.IS_SHOWCASE, true);
        if (isShowcase) {

            //UPDATE LAI LAN SAU KO HIEN NUA
            db.putBoolean(TinyDB.IS_SHOWCASE, false);


            BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);
            view1 = bottomNavigationMenuView.getChildAt(2);
            view2 = bottomNavigationMenuView.getChildAt(1);
            view3 = bottomNavigationMenuView.getChildAt(0);
            view4 = imgShop;
            view5 = imgAddUser;
            view6 = bottomNavigationMenuView.getChildAt(4);


            String[] arrShowcase = getResources().getStringArray(R.array.arr_titles_showcase);
            builder = new GuideView.Builder(this, cutoutHeight)
                    .setTitle(arrShowcase[0])
//                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                    .setGravity(Gravity.center)
                    .setDismissType(DismissType.anywhere)
                    .setTargetView(view1)
                    .setGuideListener(new GuideListener() {
                        @Override
                        public void onDismiss(View view) {
                            if (view.getId() == view1.getId()) {
                                builder.setTargetView(view2).setTitle(arrShowcase[1]);
                            } else if (view.getId() == view2.getId()) {
                                builder.setTargetView(view3).setTitle(arrShowcase[2]);
                            } else if (view.getId() == view3.getId()) {
                                builder.setTargetView(view4).setTitle(arrShowcase[3]);
                            } else if (view.getId() == view4.getId()) {
                                builder.setTargetView(view5).setTitle(arrShowcase[4]);
                            } else if (view.getId() == view5.getId()) {
                                builder.setTargetView(view6).setTitle(arrShowcase[5]);
                            } else if (view.getId() == view6.getId()) {
                                return;
                            }

                            mGuideView = builder.build();
                            mGuideView.show();
                        }
                    });

            mGuideView = builder.build();
            mGuideView.show();


        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //lay tam danh sach user follower de load tam man hinh danh sach user chat
    private void initListUserFollower(long userId) {
        getUserFollowers(userId, 0, 0);
    }

    private void getUserFollowers(final long userId, final long lastItemId, final long lastCreateDate) {
        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getListUserFollower(userId, lastItemId, lastCreateDate, 50).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<User> users = (ArrayList<User>) task.getResult();
                            MyUtils.log(users.size() + "");
                            if (users != null && users.size() > 0) {

                                if (lastItemId == 0) {//page 1
//                                    mAdapter.clear();
//                                    mAdapter.addAll(users);
                                    ArrayList<UserChatCore> followers = new ArrayList<>();
                                    for (int i = 0; i < users.size(); i++) {
                                        UserChatCore u = users.get(i).getUserChatCore();
                                        followers.add(u);
                                    }

                                    if (followers.size() > 0) {
                                        ChatApplication.Companion.setFollowers(followers);
                                    }


                                } else {//load more
//                                    mAdapter.addAll(users);
                                }

                            }

                            //if first load
                            if (lastItemId == 0) {
                                /*if (mAdapter.getItemCount() > 0) {
                                    list_empty.setVisibility(View.GONE);
                                } else {
                                    list_empty.setVisibility(View.VISIBLE);
                                }*/
                            }

//                            users.clear();

                        } else {
//                            MyUtils.showToast(context, R.string.not_found);
                        }
                    } else {
//                    MyUtils.showToast(context, task.getError().getMessage());
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());

                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            getUserFollowers(userId, lastItemId, lastCreateDate);
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
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventSearchContactChat_Request event) {
        String keyword = event.getKeyword();
        searchUserChat(keyword);
    }

    private void searchUserChat(final String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            Webservices.searchChatContact(keyword, context).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ReturnResult result = (ReturnResult) task.getResult();
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    if (result.getData() != null) {
                                        ArrayList<UserSearchChat> users = (ArrayList<UserSearchChat>) result.getData();
                                        ArrayList<UserChatCore> list = new ArrayList<>();

                                        if (users.size() > 0) {
                                            for (int i = 0; i < users.size(); i++) {
                                                UserChatCore item = users.get(i).getUserMBN();
                                                list.add(item);
                                            }

                                        }

                                        //goi ve chat
                                        EventBus.getDefault().post(new EventSearchContactChat_Receive(list));
                                    }
                                } else {

                                    String message = result.getErrorMessage();
                                    if (!TextUtils.isEmpty(message)) {
                                        MyUtils.showAlertDialog(context, message);
                                    } else {
                                        //tự thông báo
                                        if (result.getErrorCode() == ReturnResult.ERROR_CODE_PARAMS) {
                                            MyUtils.showAlertDialog(context, R.string.user_name_not_exist);
                                        } else if (result.getErrorCode() == ReturnResult.ERROR_CODE_DATA) {
                                            MyUtils.showAlertDialog(context, R.string.server_data_error);
                                        }
                                    }
                                }
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
                                            searchUserChat(keyword);
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

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


}
