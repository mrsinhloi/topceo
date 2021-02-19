/*
package com.appsaboutphoto.chuphinhdep.amazon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.ehubstar.R;
import TinyDB;
import GlideCircleTransform;
import User;
import ReturnResult;
import Webservices;
import MyUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;

public class MessageReceivingService extends Service {

    private static int FLAG_NOTIFICATION = PendingIntent.FLAG_UPDATE_CURRENT;//PendingIntent.FLAG_ONE_SHOT;
    private static int FLAG_INTENT=Intent.FLAG_ACTIVITY_CLEAR_TOP;

    public static boolean isExists = false;
    public static final String IS_FROM_NOTIFY = "IS_FROM_NOTIFY";


    private GoogleCloudMessaging gcm;
    private Webservices services;


    private static User user;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "service onStartCommand...");
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        if (intent != null) {
            checkRegisterNotify();
        }
        isExists = true;
        return START_STICKY;
    }




    private static SharedPreferences sp;

    private static Realm realm;
    private static TinyDB db;

    public void onCreate() {
        super.onCreate();
        Log.d("test", "service created...");
        isExists = true;
        realm = Realm.getDefaultInstance();
//        MyUtils.showToast(getApplicationContext(), "SERVICE START SUCCESS");
//        Log.e("TEST", "TEST: SERVICE START SUCCESS");

        sp = MyUtils.getSP(getApplicationContext());

        db=new TinyDB(this);

        Object obj=db.getObject(User.USER, User.class);
        if(obj!=null) {
            user = (User)obj;
        }
        services = new Webservices();

        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        checkRegisterNotify();
        // Let AndroidMobilePushApp know we have just initialized and there may be stored messages
//        sendToApp(new Bundle(), this);
        registerReceiver();
    }


    private static String oldMessage = "";

    public static int getNotifyNumber() {
        return notifyNumber;
    }

    public static void setNotifyNumber(int number) {
        if (number < 0) number = 0;
        notifyNumber = number;
    }

    private static int notifyNumber = 0;
    private static String date = "";
    */
/**
     * Old: Bundle[{chatRoomId=0, google.sent_time=1474439166510, commentId=0, notifyType=NewLike, imageUrl=https://chds.blob.core.windows.net/9be94326-8581-41d9-938b-002b09a0bc04/3b35f957-0c6f-4b9f-ae20-40b955a49d27/m.jpg, userId=192701, targetUserId=0, actionUserName=ChupHinhDep, date=2016-09-21 06:26:06, from=175813866741, google.message_id=0:1474439166518769%b2c165caf9fd7ecd, message=ChupHinhDep liked your photo, notifyTypeId=4, actionUserId=1, notifyId=495592, chatLogId=0, actionUserAvatar=https://chds.blob.core.windows.net/da96177c-dae8-4fc9-a121-8915ee50f1fd/85e244d2-6a43-44a2-8b0c-6543ae502cdd/s.jpg, collapse_key=do_not_collapse, imageItemId=75729}]
     * New: Bundle[{"chatRoomId":"0","google.sent_time":1475215642215,"commentId":"0","notifyType":"NewLike","imageUrl":"https:\/\/chds.blob.core.windows.net\/9be94326-8581-41d9-938b-002b09a0bc04\/5f1e9330-0f30-4453-9502-eec6db7fd966\/m.jpg","userId":"192701","targetUserId":"0","actionUserName":"ChupHinhDep","date":"2016-09-30 06:07:21","from":"175813866741","google.message_id":"0:1475215642232521%b2c165caf9fd7ecd","message":"ChupHinhDep liked your photo","notifyTypeId":"4","actionUserId":"1","notifyId":"74293","chatLogId":"0","actionUserAvatar":"https:\/\/chds.blob.core.windows.net\/da96177c-dae8-4fc9-a121-8915ee50f1fd\/85e244d2-6a43-44a2-8b0c-6543ae502cdd\/s.jpg","collapse_key":"do_not_collapse","imageItemId":"75755"}
     * @param extras
     * @param context
     *//*

    protected static void saveToLog(Bundle extras, final Context context) {

        //neu da logout thi khong nhan notify
        try {
            sp = MyUtils.getSP(context);
            final boolean isLogined=db.getBoolean(TinyDB.IS_LOGINED);
            if (isLogined==false) {
                //MyUtils.showToast(context, "khong nhan notify");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /////////////////////////////////////////////////////////////
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();

            int linesOfMessageCount = 0;

            Date when = Calendar.getInstance().getTime();
            long id = 0;
            if (extras != null) {
//                MyUtils.showToastDebug(context, "AMAZON NOTIFICATION");

                String json=getJson(extras);
                MyUtils.log(json);
                try {
                    final NotifyClass notify=new Gson().fromJson(json, NotifyClass.class);
                    if(notify!=null){
                        notifyNumber++;
                        linesOfMessageCount++;

                        String dateString = notify.getDate();
                        if (!TextUtils.isEmpty(dateString)) {
                            date = MyUtils.getDateStringNotify(dateString);
                            when = MyUtils.getDateDateNotify(dateString);
                        }

                        //tao notify, viec dieu huong se do ControlNavigationNotifyActivity dam nhan
                        int notify_id = MessageReceivingService.NotificationID.getID();
                        Intent intent=new Intent(context, ControlNavigationNotifyActivity.class);
                        intent.putExtra(NotifyClass.NOTIFY_CLASS, notify);
                        intent.setFlags(FLAG_INTENT);
                        intent.setAction(Long.toString(System.currentTimeMillis()));
                        customNotification(intent, context, notify.getMessage(), notify_id, notify.getActionUserAvatar(), date);





                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }

        }


    }





    private static void removeNotification(int id, Context context) {
        if (Context.NOTIFICATION_SERVICE != null) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
            nMgr.cancel(id);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    protected static void postNotification(Intent intentAction, Context context,
                                           String message, int notify_id, Date when) {


        if (!TextUtils.isEmpty(message)) {
            final NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            final PendingIntent pendingIntent = PendingIntent.getActivity(context, notify_id,
                    intentAction,
                    FLAG_NOTIFICATION);

//            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Uri alarmSound=Uri.parse("android.resource://"
//                    + context.getPackageName() + "/" + R.raw.candy);
            final Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(context.getText(R.string.app_name))
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setLights(Color.BLUE, 500, 500)
//                    .setSound(alarmSound)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .getNotification();

            notification.when = when.getTime();

            // notification.setLatestEventInfo(context, "MyXteam", message, pendingIntent);
            mNotificationManager.notify(notify_id, notification);//R.string.notification_number

        }
    }



    public static void customNotification(Intent intentAction, final Context context,
                               String message, int notify_id, final String avatar, String date) {

//        MyUtils.log(" notify_id=" + notify_id);
        if (!TextUtils.isEmpty(message)) {


            // Using RemoteViews to bind custom layouts into Notification
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.notification_custom);
            // Locate and set the Image into customnotificationtext.xml ImageViews
            remoteViews.setImageViewResource(R.id.imageView1, R.mipmap.ic_launcher);
//        remoteViews.setImageViewResource(R.id.textView1,R.drawable.androidhappy);

            // Locate and set the Text into customnotificationtext.xml TextViews
            remoteViews.setTextViewText(R.id.textView1, context.getString(R.string.app_name));
            remoteViews.setTextViewText(R.id.textView2, date);
            remoteViews.setTextViewText(R.id.textView3, message);

            // Set Notification Title
            String strtitle = context.getString(R.string.app_name);

            //1. Pending Intent - Open NotificationView.java Activity
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, notify_id,
                    intentAction,
                    FLAG_NOTIFICATION);


            //2. Channel va Group
            String channelId = context.getString(R.string.notification_channel_id);
            String channelName = context.getString(R.string.notification_channel_name);

            String groupId = context.getString(R.string.notification_group_id);
            String groupName = context.getString(R.string.notification_group_name);

            //3. Default ringtone
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Uri customUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.blank_sound);



            //4. NotificationCompat.Builder
            notifyNumber += 1;
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent)
                            .setColor(ContextCompat.getColor(context, R.color.primary_dark))
                            .setOnlyAlertOnce(true)
                            .setNumber(1)
                            .setBadgeIconType(Notification.BADGE_ICON_SMALL)//ko set la ko hien
                            .setCustomContentView(remoteViews);
//                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));

            Notification notification = notificationBuilder.build();


            //set hinh
            if (!TextUtils.isEmpty(avatar)) {
                Drawable d = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
                int size = d.getIntrinsicWidth();

                //java.lang.IllegalArgumentException: You must call this method on the main thread
                final NotificationTarget target = new NotificationTarget(context, remoteViews, R.id.imageView1, size, size, notification, notify_id);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(context)
                                .load(avatar)
                                .asBitmap()
                                .into(target);
                    }
                });

            }

            //5. NotificationManager
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            //6. Neu la android Oreo 8
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                int priority = NotificationManager.IMPORTANCE_DEFAULT;


                //Option
                notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));
                //Needed
                NotificationChannel channel = new NotificationChannel(channelId, channelName, priority);
                // Sets whether notifications posted to this channel should display notification lights
                channel.enableLights(true);
                // Sets whether notification posted to this channel should vibrate.

                // Sets the notification light color for notifications posted to this channel
                channel.setLightColor(Color.GREEN);
                // Sets whether notifications posted to this channel appear on the lockscreen or not
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setShowBadge(true);
                channel.setGroup(groupId);
                channel.enableVibration(true);




                notificationManager.createNotificationChannel(channel);
            }


            //7. Hien thi notify
            notificationManager.notify(notify_id, notification);

            /////////////////////////////////
            setNotifyNumber(notifyNumber);

        }
    }

    private static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_logo_notify : R.mipmap.ic_launcher;
    }
    ///////////////////////////////////////////////////////////////
    private void checkRegisterNotify() {
        if (sp != null) {
            //da dang ky token amazon chua?
            if (!sp.getBoolean(getString(R.string.is_register_amazon), false)) {
                register();
            } else {//da dang ky roi, neu chua co token thi phai dang ky lai

                String token = sp.getString(MessageReceivingService.SNS_TOKEN_DEVICE, "");
                Log.d("test", "token=" + token);
                if (TextUtils.isEmpty(token)) {
                    register();
                }



            }
        }
    }

    /////////////////////////////////////////////////////////////
    private void register() {
        if (user != null) {
            if (MyUtils.checkInternetConnection(getApplicationContext())) {
                if (registerTask == null) {
                    registerTask = new RegisterTask();
                    registerTask.execute();
                } else {
                    if (registerTask.getStatus() == AsyncTask.Status.FINISHED) {
                        registerTask = new RegisterTask();
                        registerTask.execute();
                    }
                }

            }
        }
    }

    private RegisterTask registerTask;

    private class RegisterTask extends AsyncTask<Void, Void, ReturnResult> {
        String token = "";

        @Override
        protected ReturnResult doInBackground(Void... voids) {
            ReturnResult result = null;
            try {
                if (gcm != null && user != null) {
                    token = gcm.register(getString(R.string.project_number));
                    Log.d("registrationId", "token = " + token);

                    Webservices.addUserEndpoint(token, MyUtils.getDeviceId(getApplicationContext())).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if(task.getError()==null){
                                if(task.getResult()!=null){
                                    boolean isSuccess=(boolean)task.getResult();
                                    if(isSuccess){
                                        //neu token rong thi dang ky lai
                                        boolean registered = true;
                                        if (TextUtils.isEmpty(token)) registered = false;
                                        //dang ky thanh cong
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putBoolean(getString(R.string.is_register_amazon), registered);
                                        editor.putString(MessageReceivingService.SNS_TOKEN_DEVICE, token);
                                        editor.commit();

                                        MyUtils.showToastDebug(getApplicationContext(), "Đăng ký token thành công");
                                    }
                                }
                            }
                            return null;
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ReturnResult result) {
            super.onPostExecute(result);

        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////


    public IBinder onBind(Intent arg0) {
        return null;
    }

    private static class NotificationID {
        private static final AtomicInteger c = new AtomicInteger(0);

        public static int getID() {
            return c.incrementAndGet();
        }
    }

    @Override
    public void onDestroy() {
        isExists = false;
        super.onDestroy();
        realm.close();
        Log.d("test", "service destroy...");
//        sendBroadcast(new Intent("YouNeedRestartMe"));
        if(receiver!=null)getApplicationContext().unregisterReceiver(receiver);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    private static String getJson(final Bundle bundle) {
        if (bundle == null) return null;
        JSONObject jsonObject = new JSONObject();

        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            try {
                jsonObject.put(key, wrap(bundle.get(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    public static Object wrap(Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return new JSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                return toJSONArray(o);
            }
            if (o instanceof Map) {
                return new JSONObject((Map) o);
            }
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static JSONArray toJSONArray(Object array) throws JSONException {
        JSONArray result = new JSONArray();
        if (!array.getClass().isArray()) {
            throw new JSONException("Not a primitive array: " + array.getClass());
        }
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            result.put(wrap(Array.get(array, i)));
        }
        return result;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////
    @Deprecated
    public static final String ACTION_MAIN_ACTIVITY_STARTED="ACTION_MAIN_ACTIVITY_STARTED";
    private BroadcastReceiver receiver;
    private void registerReceiver(){
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(ACTION_MAIN_ACTIVITY_STARTED)){
                    //lam tiep viec chua lam

                }
            }
        };
        getApplicationContext().registerReceiver(receiver, new IntentFilter(ACTION_MAIN_ACTIVITY_STARTED));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////

}
*/
