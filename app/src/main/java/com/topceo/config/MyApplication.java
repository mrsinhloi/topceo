package com.topceo.config;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.danikula.videocache.HttpProxyCacheServer;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liuzhenlin.common.utils.SystemBarUtils;
import com.liuzhenlin.common.utils.Utils;
import com.liuzhenlin.texturevideoview.utils.DensityUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.myxteam.toolarge.TooLargeTool;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.analytics.Engagement;
import com.topceo.db.TinyDB;
import com.topceo.group.GroupDetailActivity;
import com.topceo.language.LocalizationUtil;
import com.topceo.mediaplayer.pip.Files;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserShort;
import com.topceo.objects.promotion.Promotion;
import com.topceo.onesignal.NotificationActivity;
import com.topceo.onesignal.NotificationUtils;
import com.topceo.onesignal.NotifyObject;
import com.topceo.onesignal.OneSignalLog;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.retrofit.ApiManager;
import com.topceo.services.CookieStore;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.socialspost.facebook.FacebookApi;
import com.topceo.utils.MyUtils;
import com.topceo.video_compressor.file.FileUtils;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.config.EventControlListener;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.Project;
import com.workchat.core.models.realm.Room;
import com.workchat.core.retrofit.workchat.Parser;
import com.workchat.core.ssl.UnsafeOkHttpClient;
import com.yariksoffice.lingver.Lingver;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import bolts.Task;
import bolts.TaskCompletionSource;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by mikepenz on 27.03.15.
 */
public class MyApplication extends ChatApplication implements EventControlListener {

    public boolean isFirst = true;
    private int mStatusHeight;

    private volatile int mScreenWidth = -1;
    private volatile int mScreenHeight = -1;

    private volatile int mRealScreenWidth = -1;
    private volatile int mRealScreenHeight = -1;

    private volatile int mVideoThumbWidth = -1;

    private void init1(){
        mStatusHeight = SystemBarUtils.getStatusHeight(this);
        registerComponentCallbacks(Glide.get(this));
    }
    @NonNull
    public static File getAppExternalFilesDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), Files.EXTERNAL_FILES_FOLDER);
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    public int getStatusHeightInPortrait() {
        return mStatusHeight;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public int getScreenWidthIgnoreOrientation() {
        if (mScreenWidth == -1) {
            synchronized (this) {
                if (mScreenWidth == -1) {
                    int screenWidth = DensityUtils.getScreenWidth(this);
                    if (getResources().getConfiguration().orientation
                            != Configuration.ORIENTATION_PORTRAIT) {
                        //@formatter:off
                        int screenHeight  = DensityUtils.getScreenHeight(this);
                        if (screenWidth   > screenHeight) {
                            screenWidth  ^= screenHeight;
                            screenHeight ^= screenWidth;
                            screenWidth  ^= screenHeight;
                        }
                        //@formatter:on
                        mScreenHeight = screenHeight;
                    }
                    mScreenWidth = screenWidth;
                }
            }
        }
        return mScreenWidth;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public int getScreenHeightIgnoreOrientation() {
        if (mScreenHeight == -1) {
            synchronized (this) {
                if (mScreenHeight == -1) {
                    int screenHeight = DensityUtils.getScreenHeight(this);
                    if (getResources().getConfiguration().orientation
                            != Configuration.ORIENTATION_PORTRAIT) {
                        //@formatter:off
                        int screenWidth   = DensityUtils.getScreenWidth(this);
                        if (screenWidth   > screenHeight) {
                            screenWidth  ^= screenHeight;
                            screenHeight ^= screenWidth;
                        }
                        //@formatter:on
                        mScreenWidth = screenWidth;
                    }
                    mScreenHeight = screenHeight;
                }
            }
        }
        return mScreenHeight;
    }

    public int getRealScreenWidthIgnoreOrientation() {
        if (mRealScreenWidth == -1) {
            synchronized (this) {
                if (mRealScreenWidth == -1) {
                    int screenWidth = DensityUtils.getRealScreenWidth(getInstance());
                    if (getResources().getConfiguration().orientation
                            != Configuration.ORIENTATION_PORTRAIT) {
                        //@formatter:off
                        int screenHeight  = DensityUtils.getRealScreenHeight(this);
                        if (screenWidth   > screenHeight) {
                            screenWidth  ^= screenHeight;
                            screenHeight ^= screenWidth;
                            screenWidth  ^= screenHeight;
                        }
                        //@formatter:on
                        mRealScreenHeight = screenHeight;
                    }
                    mRealScreenWidth = screenWidth;
                }
            }
        }
        return mRealScreenWidth;
    }

    public int getRealScreenHeightIgnoreOrientation() {
        if (mRealScreenHeight == -1) {
            synchronized (this) {
                if (mRealScreenHeight == -1) {
                    int screenHeight = DensityUtils.getRealScreenHeight(this);
                    if (getResources().getConfiguration().orientation
                            != Configuration.ORIENTATION_PORTRAIT) {
                        //@formatter:off
                        int screenWidth   = DensityUtils.getRealScreenWidth(getInstance());
                        if (screenWidth   > screenHeight) {
                            screenWidth  ^= screenHeight;
                            screenHeight ^= screenWidth;
                        }
                        //@formatter:on
                        mRealScreenWidth = screenWidth;
                    }
                    mRealScreenHeight = screenHeight;
                }
            }
        }
        return mRealScreenHeight;
    }

    public int getVideoThumbWidth() {
        if (mVideoThumbWidth == -1) {
            synchronized (this) {
                if (mVideoThumbWidth == -1) {
                    mVideoThumbWidth = Utils.roundFloat(getScreenWidthIgnoreOrientation() * 0.2778f);
                }
            }
        }
        return mVideoThumbWidth;
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);

    }

    public static TinyDB db;
    public static CookieStore cookie = null;
    //retrofit
    public static ApiManager apiManager;


    private static MyApplication application;
    private static PhoneNumberUtil phoneUtil;

    public static PhoneNumberUtil getPhoneUtil() {
        return phoneUtil;
    }

    public static MyApplication getInstance() {
        if (application == null) {
            application = new MyApplication();
        }
        return application;
    }
    @NonNull
    public static MyApplication getInstance(@NonNull Context context) {
        return application == null ? (MyApplication) context.getApplicationContext() : application;
    }


    private static String oneSignalUserId = "";

    private static ArrayList<Promotion> promotions = new ArrayList<>();

    public static void setPromotionList(ArrayList<Promotion> list) {
        promotions = list;
    }

    public static ArrayList<Promotion> getPromotions() {
        return promotions;
    }


    public void sendBroadcastRereshNumberFollow() {
        if (myContext != null)
            myContext.sendBroadcast(new Intent(Fragment_Profile_Owner.ACTION_WHEN_FOLLOW_OR_UNFOLLOW));
    }

    public String getOneSignalId() {
        return oneSignalUserId;
    }

    private void initOneSignal() {
        //Test Onesignal Log
        if (BuildConfig.DEBUG) {
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        }

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .autoPromptLocation(false) // default call promptLocation later
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .init();
        /////////////////////////////#############/////////////////////////
        Drawable iconBackCustom = AppCompatResources.getDrawable(this, R.drawable.ic_svg_16_36dp);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                oneSignalUserId = userId;
                String appId = getString(R.string.one_signal_app_id);
                ChatApplication.Companion.setOneSignalUserId(userId);
                ChatApplication.Companion.setOneSignalAppId(appId);
                //resize to 36x36dp
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_round_arrow_back_ios_20);
                int iconSize = getResources().getDimensionPixelSize(R.dimen.ic_size_36);
//                Drawable iconBackCustom = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, false));

                ChatApplication.Companion.init(
                        getString(R.string.GOOGLE_MAPS_ANDROID_API_KEY),
                        MyApplication.this,
                        BuildConfig.APPLICATION_ID,
                        Webservices.URL_CORE_CHAT,
                        iconBackCustom,
                        false,
                        false
                );
            }
        });
        /////////////////////////////#############/////////////////////////


        //notify tren android 8.0
        initAndroidOreo();
    }

    private void initAndroidOreo() {
        //https://blmte.com/computer-internet-technology/271019_remoteserviceexception-context-startforegroundservice-did-not-then-call-service-startforeground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationUtils mNotificationUtils = new NotificationUtils(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void initWebservice(String authorization) {
        //networking
        if (cookie == null) {
            cookie = new CookieStore();
        }

        //#region PASS SERVER CERTIFICED///////////////////////////////////////////////////////////////
//        HttpLoggingInterceptor logg = new HttpLoggingInterceptor();
//        logg.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        //trust https
        OkHttpClient.Builder builder = UnsafeOkHttpClient.getBuilder(authorization);
//        builder.addInterceptor(logg);
        builder.cookieJar(cookie);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.protocols(Util.immutableList(Protocol.HTTP_1_1));
        client = builder.build();
        AndroidNetworking.initialize(context, client);

        //retrofit, co cookie thi moi set
        apiManager = ApiManager.getIntance();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static final int MAX_NUMBER_TRY = 10;
    private static int numberLostConnection = 0;

    public static boolean controlException(ANError e) {
        boolean isLostCookie = false;
        if (e.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED ||
                (e.getMessage() != null && e.getMessage().contains("SocketTimeoutException"))
        ) {
            isLostCookie = true;

            numberLostConnection++;
            if (numberLostConnection > MAX_NUMBER_TRY) {
                isLostCookie = false;
            }
        }

        return isLostCookie;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static OkHttpClient getClient() {
        if (client == null) {
            MyUtils.log("Fragment_1_Home_User - MyApplication - getClient = null");
        } else {
            MyUtils.log("Fragment_1_Home_User - MyApplication - getClient != null");
        }
        return client;
    }

    /////////////////////////////////////////////////////
    /**
     * Chi truy cap truc tiep tu ham Login, de gan gia tri cho no <br/>
     * Cac ham khac phai goi getClient()
     */
    public static OkHttpClient client;

    private static Context myContext;

    private static boolean isCallingInitCookie = false;

    /**
     * Khoi tao OkHttpClient client de cac service khac dung
     */
    public synchronized static Task<Object> initCookie(final Context context) {
        myContext = context;
        //ket qua tra ve
        final TaskCompletionSource<Object> task = new TaskCompletionSource<>();

        boolean isFromFacebook = db.getBoolean(TinyDB.IS_LOGIN_BY_FACEBOOK);


        if (isFromFacebook) {
            final long start = System.currentTimeMillis();
            AndroidNetworking.post(Webservices.API_URL + "user/loginFacebook")
                    .addQueryParameter("username", db.getString(TinyDB.FACEBOOK_ID))
                    .addQueryParameter("password", db.getString(TinyDB.FACEBOOK_PASSWORD))
                    .setOkHttpClient(client)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            MyUtils.howLong(start, "user/loginFacebook");

                            ReturnResult result = Webservices.parseJson(response, User.class, false);


                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    //save thong tin

                                    //chu yeu luu user
                                    if (result.getData() != null) {
                                        User user = (User) result.getData();
                                        db.putObject(User.USER, user);


//                                        db.putString(TinyDB.FACEBOOK_ID, FacebookId);//ko doi
//                                        db.putString(TinyDB.FACEBOOK_PASSWORD,Password);//ko doi
                                        db.putBoolean(TinyDB.IS_LOGIN_BY_FACEBOOK, true);
                                        db.putBoolean(TinyDB.IS_LOGINED, true);

                                        //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
                                        MyApplication.whenLoginSuccess();

                                        task.setResult(user);
                                    } else {
                                        task.setResult(null);
                                    }

                                } else {
                                    task.setResult(null);
                                    if (!TextUtils.isEmpty(result.getErrorMessage())) {
                                        MyUtils.showToast(context, result.getErrorMessage());
                                    }
                                }
                            } else {
                                task.setResult(null);
                            }


                        }

                        @Override
                        public void onError(ANError ANError) {
                            task.setError(ANError);
                        }
                    });
        } else {
            final long start = SystemClock.elapsedRealtime();
            String username = db.getString(TinyDB.USER_NAME);
            String password = db.getString(TinyDB.USER_PASSWORD);

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                isCallingInitCookie = true;
                AndroidNetworking.post(Webservices.API_URL + "user/login")
                        .addBodyParameter("username", username)
                        .addBodyParameter("password", password)
                        .setOkHttpClient(client)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                MyUtils.howLong(start, "user/login");
                                isCallingInitCookie = false;

                                ReturnResult result = Webservices.parseJson(response, User.class, false);
                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {

                                        //luu user
                                        if (result.getData() != null) {
                                            User user = (User) result.getData();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    db.putObject(User.USER, user);

                                                    db.putBoolean(TinyDB.IS_LOGIN_BY_FACEBOOK, false);
                                                    db.putBoolean(TinyDB.IS_LOGINED, true);

                                                    //tao lai cache user short
                                                    userShort = user.getUserShort();

                                                    //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
                                                    MyApplication.whenLoginSuccess();

                                                    task.setResult(user);
                                                }
                                            }).start();


                                        } else {
                                            task.setResult(null);
                                        }

                                    } else if (result.getErrorCode() == ReturnResult.ERROR_CODE_BANNED) {
                                        Object obj = db.getObject(User.USER, User.class);
                                        if (obj != null) {
                                            User user = (User) obj;
                                            user.setBanned(true);
                                            db.putObject(User.USER, user);
                                        }
                                        task.setResult(ReturnResult.ERROR_CODE_BANNED);
                                    } else {

                                        if (!TextUtils.isEmpty(result.getErrorMessage())) {
//                                        MyUtils.showToast(context, result.getErrorMessage());
                                        }

                                        task.setResult(null);
                                    }
                                } else {
                                    task.setResult(null);
                                }


                            }

                            @Override
                            public void onError(ANError ANError) {
                                isCallingInitCookie = false;
                                if (ANError.getMessage() != null)
                                    MyUtils.showToast(context, ANError.getMessage());
                                task.setError(ANError);
                            }
                        });
            }
        }

        return task.getTask();
    }


    //http://stackoverflow.com/questions/32798816/unexpected-top-level-exception-com-android-dex-dexexception-multiple-dex-files
    //ONESIGNAL///////////////////////////////////////////////////////////////////
    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
//            MyUtils.showToastDebug(context, "OOOP 1....");
            MyUtils.log("notification 1. ExampleNotificationReceivedHandler");

            //neu ko co user la da logout, ko nhan notify
            if (getUser() == null) {
                return;
            }

            if (notification != null) {
                try {
                    JSONObject data = notification.payload.additionalData;
                    String body = notification.payload.body;
                    if (data != null) {
                        if (data.has("roomId")) {//CHAT

                            String roomId = data.optString("roomId");

                            NotifyObject obj = new NotifyObject();
                            obj.setMessage(body);


                            Object activityToLaunch = NotificationActivity.class;
                            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(Room.ROOM_ID, roomId);

                            postNotification(intent, getApplicationContext(), obj);

                            //tang notify
                            setNumberForIcon(numberNotify++);

                        } else {//MTP
                            NotifyObject obj = new Gson().fromJson(data.toString(), NotifyObject.class);
                            if (obj != null) {
                                obj.setMessage(body);

                                Object activityToLaunch = NotificationActivity.class;
                                Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(NotifyObject.NOTIFY_OBJECT, obj);

                                postNotification(intent, getApplicationContext(), obj);

                                //tang notify
                                setNumberForIcon(numberNotify++);
                            }
                        }

                    } else {
                        //chi hien message
                        NotifyObject obj = new NotifyObject();
                        obj.setMessage(body);

                        Object activityToLaunch = NotificationActivity.class;
                        Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(NotifyObject.NOTIFY_OBJECT, obj);

                        postNotification(intent, getApplicationContext(), obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }


    }


    public static void whenLogout() {
        userShort = null;
        user = null;

        //clear cache
        GroupDetailActivity.Companion.getMapInvited().clear();

    }

    private static User user;

    public static User getUser() {
        if (user == null) {
            if (db != null) {
                Object obj = db.getObject(User.USER, User.class);
                if (obj != null) {
                    user = (User) obj;
                }
            }
        }
        return user;
    }

    private static UserShort userShort;

    public static UserShort getUserShort() {
        if (userShort == null) {
            getUser();
            if (user != null) {
                userShort = user.getUserShort();
            }
        }

        return userShort;
    }

    /**
     * Lam moi lai thong tin user load len tu bo nho cache
     */
    public static void initUser() {
        if (db != null) {
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
            }
        }
    }

    public static String getToken() {
        String token = "";
        User u = getUser();
        if (u != null) {
            token = "bearer " + u.getToken();
        }
        return token;
    }


    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            /*MyUtils.log("notification 2. ExampleNotificationOpenedHandler");
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl
            MyUtils.log(launchUrl);*/
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static int numberNotify = 0;

    public void setNumberForIcon(int number) {
        /*if (number >= 0) {
            try {
                me.leolin.shortcutbadger.ShortcutBadger.applyCount(getApplicationContext(), number);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    /////////////////////////////////////////////////////////////////////////////////
    private static class NotificationID {
        private static final AtomicInteger c = new AtomicInteger(0);

        public static int getID() {
            return c.incrementAndGet();
        }
    }

    private static int FLAG_NOTIFICATION = PendingIntent.FLAG_UPDATE_CURRENT;
    public static final int iconInt = R.drawable.ic_stat_onesignal_default;

    protected static void postNotification(Intent intentAction, Context context,
                                           NotifyObject notify) {

        String message = notify.getMessage();
        int notify_id = NotificationID.getID();
        Date when = Calendar.getInstance().getTime();
        if (!TextUtils.isEmpty(message)) {
            final NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            final PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    notify_id,
                    intentAction,
                    FLAG_NOTIFICATION);

//            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Uri alarmSound=Uri.parse("android.resource://"
//                    + context.getPackageName() + "/" + R.raw.candy);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {

                    Notification.Builder builder =
                            new Notification.Builder(context, NotificationUtils.ANDROID_CHANNEL_ID);
                    builder
                            .setColor(ContextCompat.getColor(context, R.color.white))
                            .setSmallIcon(iconInt)
//                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            .setOnlyAlertOnce(true)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
//                            .setContentTitle(context.getText(R.string.app_name))
                            .setContentText(message)
                            .setWhen(when.getTime());
//                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
//                    startForeground(notify_id, notificationBuilder.build());

                    String image = notify.getImageUrl();
                    if (!TextUtils.isEmpty(image) && MyUtils.isImageUrl(image)) {
                        FutureTarget<Bitmap> futureTarget = Glide.with(context).asBitmap().load(image).submit();
                        Bitmap b = futureTarget.get();

                        builder.setLargeIcon(b);
                        builder.setStyle(new Notification.BigPictureStyle().bigPicture(b));
                    } else {
                        builder.setStyle(new Notification.BigTextStyle().bigText(message));
                    }


                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //todo tao icon cho android L
                        builder.setSmallIcon(iconInt);//R.drawable.ic_notify_lollipop
                        builder.setColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                    } else {
                        builder.setSmallIcon(iconInt);
                    }

                    mNotificationManager.notify(notify_id, builder.build());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

//            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Uri alarmSound=Uri.parse("android.resource://"
//                    + context.getPackageName() + "/" + R.raw.candy);
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationUtils.ANDROID_CHANNEL_ID)
                        .setSmallIcon(iconInt)
//                        .setContentTitle(context.getText(R.string.app_name))
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);

                String image = notify.getImageUrl();
                if (!TextUtils.isEmpty(image) && MyUtils.isImageUrl(image)) {
                    FutureTarget<Bitmap> futureTarget = Glide.with(context).asBitmap().load(image).submit();
                    Bitmap b = null;
                    try {
                        b = futureTarget.get();
                        builder.setLargeIcon(b);
                        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(b).bigLargeIcon(null));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } else {
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                }

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //todo tao icon cho android L
                    builder.setSmallIcon(iconInt);//R.drawable.ic_notify_lollipop
                    builder.setColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                } else {
                    builder.setSmallIcon(iconInt);
                }

                Notification notification = builder.build();
                notification.when = when.getTime();
                mNotificationManager.notify(notify_id, notification);

            }

        }
    }


    //cache video preview
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .cacheDirectory(getExternalCacheDir())
                .maxCacheFilesCount(20)
                .build();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //CHUC NANG GOI THONG KE
    private HashMap<Long, Engagement> map = new HashMap<>();

    public void insertMap(long postId, String type) {
        if (postId > 0) {
            Engagement engagement = new Engagement();
            engagement.setPostId(postId);
            engagement.setEngageType(type);
            engagement.setCreateDate(System.currentTimeMillis() / 1000);
            map.put(engagement.getPostId(), engagement);
        }
    }

    private boolean isExistInMap(long postId) {
        return map.get(postId) != null;
    }

    public ArrayList<Engagement> getEngatements() {
        ArrayList<Engagement> list = new ArrayList<>();
        for (Long id : map.keySet()) {
            list.add(map.get(id));
        }
        return list;
    }

    //goi thong tin ve server
    public void sendAnalyticToServer() {
//        if(MyUtils.checkInternetConnection(getApplicationContext())){
        ArrayList<Engagement> list = getEngatements();
        if (list.size() > 0) {

            //xoa cai da goi
            map.clear();
            MyApplication.apiManager.addEngagements(
                    list,
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                //Chi goi len, neu co loi thi bo qua

                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            MyUtils.log("error");
                        }
                    });

        }
//        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static final String FIREBASE_SKYSOCIAL = "skysocial";
    public static final String FIREBASE_CHATCORE = "chatcore";

    /**
     * User login vao bang firebase corechat
     */
    private void initFirebaseMultiProject() {

        //SKY SOCIAL
        // Manually configure Firebase Options. The following fields are REQUIRED:
        //   - Project ID
        //   - App ID
        //   - API Key
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("sontungmtp-56bcc")
                .setApplicationId("1:745408950616:android:0c36d39959303f32c4352d")
                .setApiKey("AIzaSyCSv2KAZGBdXp_SrHfJtB4rPltqRrmHC6w")
                // setDatabaseURL(...)
                // setStorageBucket(...)
                .build();

        // Initialize with secondary app
        FirebaseApp.initializeApp(this, options, FIREBASE_SKYSOCIAL);

        // Retrieve secondary FirebaseApp
//        FirebaseApp secondary = FirebaseApp.getInstance("skysocial");


        //CHAT CORE
        FirebaseOptions options2 = new FirebaseOptions.Builder()
                .setProjectId("chatcore-7120d")
                .setApplicationId("1:286058919610:android:8dbe50ec4cba74f791f483")
                .setApiKey("AIzaSyC2LNaCDId6SSQX249UrR63e1QwFolghlo")
                // setDatabaseURL(...)
                // setStorageBucket(...)
                .build();

        // Initialize with secondary app
        FirebaseApp.initializeApp(this, options2, FIREBASE_CHATCORE);

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static String ipPublic = "";

    public static String getIpPublic() {
        if (!TextUtils.isEmpty(ipPublic)) {
            initIpPublic();
        }
        return ipPublic;
    }

    private static void initIpPublic() {

        if (MyUtils.checkInternetConnection(context)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ipPublic = MyUtils.getPublicIP();
                    return null;
                }
            }.execute();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void initLanguage(Context context) {
//        LocalizationUtil.INSTANCE.applyLanguageContext(context);
        String language = db.getString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_VI);
        Lingver.getInstance().setLocale(context, language);
    }

    public static int colorMention = Color.BLUE;
    public static int colorHashtag = Color.YELLOW;
    public static Context context;
    public int screenWidth;
    public int heightLinkPreview;

    private boolean isTrackBugs = true;
    private void initCrashCollect(){
        if(isTrackBugs){

            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    throw new RuntimeException("Boom!");
                }
            }, 5000);*/
        }
    }
    @Override
    public void onCreate() {
        TooLargeTool.startLogging(this);
        Webservices.initURLs(this);
        context = this;
        application = this;
        db = new TinyDB(this);
        phoneUtil = PhoneNumberUtil.createInstance(this);
        screenWidth = MyUtils.getScreenWidth(context);
        heightLinkPreview = context.getResources().getDimensionPixelOffset(R.dimen.imgPreviewHeight);
        colorMention = ContextCompat.getColor(context, R.color.mention);
        colorHashtag = ContextCompat.getColor(context, R.color.hashtag);

        //init language
        String language = db.getString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_VI);
        Lingver.init(this, language);

        super.onCreate();

        // OneSignal Initialization
        initOneSignal();

        initUser();
        initIpPublic();

        //firebase
        initFirebaseMultiProject();

        //facebook ads
        AudienceNetworkAds.isInAdsProcess(this);
        AdSettings.setMultiprocessSupportMode(AdSettings.MultiprocessSupportMode.MULTIPROCESS_SUPPORT_MODE_OFF);


        initWebservice(getToken());

        //video compressor
        FileUtils.createApplicationFolder();


        //font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
//                Glide.get(this).reimageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.colorPrimary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });


        // Initialize Realm (just once per application)
        Realm.init(this);
        RealmConfiguration configuration =
                new RealmConfiguration.Builder()
                        .name("topceo.realm")
                        .schemaVersion(6)
                        .deleteRealmIfMigrationNeeded()
                        .build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);


        //downloader
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        //socials
        initSocials();

        //crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);

        initCrashCollect();

        //firebase
//        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static String tokenChat = "";

    public static void saveTokenChat(String token) {
        tokenChat = token;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void whenSignUp(String tokenBearer){
        initWebservice(tokenBearer);
        apiManager = ApiManager.getIntanceNew();//tao moi lai
    }
    public static void whenLoginSuccess() {
        //load lai thong tin user
        initUser();
        //tao lai header cho client cho AndroidNetworking
        initWebservice(getToken());
        //dung chung client vua dc tao ben tren
        apiManager = ApiManager.getIntanceNew();//tao moi lai

        //tao lai socket
        if (user != null) {
            UserChatCore chatUser = user.getUserChatCore();
            if (!TextUtils.isEmpty(tokenChat)) {
                chatUser.setToken(tokenChat);
            }
            ChatApplication.Companion.whenHaveUser(chatUser);

            //dang ky onesignal token
            oneSignalSubscribIfNeed();
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //#region OneSignal/////////////////////////////////////////////////////////////////////////

    private static void updateExternalUserId() {
        if (getUser() != null) {
            OneSignal.setExternalUserId(String.valueOf(getUser().getUserId()));
        }
    }

    /**
     * Kiểm tra nếu app chưa đăng ký onesignal thì sẽ gọi đăng ký
     * MainActivity co dang ky nhan onOSSubscriptionChanged(lần đầu đăng ký hoặc subscribed -> unsubscribed -> subscribed)
     * Thì sẽ gọi đăng ký lên server local : [whenHaveOneSignalUserId]
     * <p>
     * TH2: //hoặc sau khi logout(xóa token) login lại trạng thái vẫn là subscrib nhưng đã xóa token trên server
     * // ->cần đăng ký lên lại
     */
    public static void oneSignalSubscribIfNeed() {
        boolean isSubscribed = isOneSignalSubscribed();
        //set subscription: nếu chưa subscrib
        if (!isSubscribed) {
            //Dù có đăng ký lên server local thì trạng thái subscrib vẫn ko enable, phải gọi trực tiếp như sau:
            OneSignalLog.Companion.printLog("You are not subscrib, request subscrib");
            OneSignal.setSubscription(true);
            updateExternalUserId();

            //MainActivity co dang ky nhan onOSSubscriptionChanged, no se goi dang ky lai whenHaveOneSignalUserId
            //goi dang ky lai
//            oneSignalSubscribIfNeed();
            isRegisterOneSignalLocalServer = db.getBoolean(TinyDB.IS_REGISTER_ONESIGNAL_LOCAL_SERVER);
            if (!isRegisterOneSignalLocalServer) {
                OneSignalLog.Companion.printLog("You are subscribed, call register to local server");
                updateOneSignalUserId();
            }
        } else {
            //hoặc sau khi logout(xóa token) login lại trạng thái vẫn là subscrib nhưng đã xóa token trên server
            // ->cần đăng ký lên lại
            isRegisterOneSignalLocalServer = db.getBoolean(TinyDB.IS_REGISTER_ONESIGNAL_LOCAL_SERVER);
            if (!isRegisterOneSignalLocalServer) {
                OneSignalLog.Companion.printLog("You are subscribed, call register to local server");
                updateOneSignalUserId();
                updateExternalUserId();
            }
        }
    }

    /**
     * Kiểm tra đã subscrib hay chưa
     */
    public static boolean isOneSignalSubscribed() {
        boolean isSubscribed = false;
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        if (status != null) {
            isSubscribed = status.getSubscriptionStatus().getSubscribed();
        }
        return isSubscribed;
    }

    /**
     * Đăng ký oneSignalUserId lên server local (WorkChat)
     */
    public static void whenHaveOneSignalUserId(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            oneSignalUserId = userId;
            updateOneSignalUserId();
        }
    }

    public static boolean isRegisterOneSignalLocalServer = false;
    //Để tránh onOSSubscriptionChanged gọi nhiều lần
    public static boolean isCalling = false;

    public static void updateOneSignalUserId() {
        if (!TextUtils.isEmpty(oneSignalUserId)) {
            if (MyUtils.checkInternetConnection(context)) {

                String header = ChatApplication.Companion.getHeader();
                String oneSignalAppId = context.getResources().getString(R.string.one_signal_app_id);
                String deviceId = MyUtils.getIMEI(context);

                if (!isCalling) {
                    isCalling = true;
                    ChatApplication.Companion.getApiManagerChat().updateOneSignalUserId(
                            header,
                            oneSignalUserId,
                            oneSignalAppId,
                            deviceId,
                            OS,
                            new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    JsonObject obj = response.body();
                                    if (obj != null) {
                                        com.workchat.core.retrofit.workchat.ReturnResult result = Parser.parseJson(obj.toString(), null, false);
                                        if (result != null) {
                                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                                OneSignalLog.Companion.printLog("register oneSignalUserId to local server success");
                                                isRegisterOneSignalLocalServer = true;
                                                db.putBoolean(TinyDB.IS_REGISTER_ONESIGNAL_LOCAL_SERVER, isRegisterOneSignalLocalServer);
                                            } else {
                                                MyUtils.showToast(context, result.getErrorMessage());
                                            }
                                        }
                                    }
                                    isCalling = false;
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    isCalling = false;
                                    OneSignalLog.Companion.printLog("Error " + t.toString());
                                }
                            }
                    );
                }
            }
        }
    }

    public static void removeOneSignalUserIdAndCloseSocketWhenLogout() {

        isRegisterOneSignalLocalServer = false;
        if (!TextUtils.isEmpty(oneSignalUserId)) {
            if (MyUtils.checkInternetConnection(context)) {

                String header = ChatApplication.Companion.getHeader();
                ChatApplication.Companion.getApiManagerChat().removeOneSignalUserId(
                        header,
                        oneSignalUserId,
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject obj = response.body();
                                if (obj != null) {
                                    com.workchat.core.retrofit.workchat.ReturnResult result = Parser.parseJson(obj.toString(), null, false);
                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                            OneSignalLog.Companion.printLog("When you logout");
                                        } else {
                                            MyUtils.showToast(context, result.getErrorMessage());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        }
                );
            }
        }

        //clear notification
        OneSignal.clearOneSignalNotifications();
        OneSignal.setSubscription(false);


        //co xoa dc hay ko thi cung phai cho dang xuat
        //clear cache
        db.clear();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.clearCaches();
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

        //close socket
        ChatApplication.Companion.logout();

    }

    //#endregion OneSignal/////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //todo implement functions #111
    //#region EventControlListener/////////////////////////////////////////////////////////////////
    @Override
    public void reOpenMainActivity() {

    }

    @Override
    public void openAddContactActivity() {

    }

    public static int numberChatUnread = 0;

    @Override
    public void setNumberChatUnread(int i) {
        numberChatUnread = i;

        //thong bao cho main activity doc lai gia tri numberChatUnread
        Intent intent = new Intent(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);
        sendBroadcast(intent);
    }

    @Override
    public void setNumberNotifyUnread(int i) {

    }

    @Override
    public void whenTokenInvalid() {

    }

    @Override
    public void openMainActivityAndSearchRoom() {

    }

    @Override
    public void openDeeplink(String s, String s1, Project project, ArrayList<String> arrayList) {

    }

    @Override
    public void openProfile(String username) {
        if (!TextUtils.isEmpty(username)) {
            //thieu context activity nen phai goi 1 broadcast
            Intent intent = new Intent(MH01_MainActivity.ACTION_OPEN_PROFILE);
            intent.putExtra(User.USER_NAME, username);
            context.sendBroadcast(intent);
        }
    }

    //#endregion/////////////////////////////////////////////////////////////////////////////////////////////

    public static FacebookApi facebookApi;

    private void initSocials() {
        facebookApi = new FacebookApi();

    }

    //chuyen qua lai data
    public static ImageItem imgItem;//tu danh sach sang detail
    public static ImageItem itemReturn;
    public static ImageItem imgEdit;


}
