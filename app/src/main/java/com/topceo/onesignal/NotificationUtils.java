package com.topceo.onesignal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.topceo.BuildConfig;


public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".notify.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = "EhubStar Channel";


    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationUtils(Context base) {
        super(base);
//        startForeground(base);
        createChannels();
    }

    /*private void startForeground(Context context) {
        Notification notification = new NotificationCompat.Builder(context, NotificationUtils.ANDROID_CHANNEL_ID)
                                    .setOngoing(true)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                                    .setCategory(Notification.CATEGORY_SERVICE)
                                    .build();
        getManager().notify(1010,notification);
    }*/


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {

        String groupId = "group_id_101";
        CharSequence groupName = "SkySocial";

        getManager().createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));


        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        androidChannel.setShowBadge(true);

        getManager().createNotificationChannel(androidChannel);

        // create ios channel
        /*NotificationChannel iosChannel = new NotificationChannel(IOS_CHANNEL_ID,
                IOS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        iosChannel.enableLights(true);
        iosChannel.enableVibration(true);
        iosChannel.setLightColor(Color.GRAY);
        iosChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(iosChannel);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(Context context, String title, String body) {
        return new Notification.Builder(context, ANDROID_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }

    /*@RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getIosChannelNotification(String title, String body) {
        return new Notification.Builder(getApplicationContext(), IOS_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }*/

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteNotificationChannel(String channelId) {

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.deleteNotificationChannel(channelId);
    }
}
