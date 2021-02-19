package com.topceo.onesignal;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

/**
 * Created by MrPhuong on 2017-09-25.
 */

public class OnesignalNotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        //
        /*OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Green on Android 5.0+ devices.
                return builder.setColor(new BigInteger("FF00FF00", 16).intValue());
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);*/

        // Read properties from result.
//        JSONObject json = notification.payload.additionalData;
//        MyUtils.log(json.toString());

//        MyUtils.showToastDebug(getApplicationContext(), "notification 3. OnesignalNotificationExtender");
        /*MyUtils.log("notification 3. OnesignalNotificationExtender");

        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                if (!notification.restoring) {
                    // Only set custom channel if notification is not being restored
                    // Note: this would override any channels set through the OneSignal dashboard
                    return builder.setChannelId("skysocial");
                }else{
                    return builder.setColor(new BigInteger("FF00FF00", 16).intValue());
                }
            }
        };

        // Do something with notification payload
        String title = notification.payload.title;
        String body  = notification.payload.body;
        JSONObject additionalData = notification.payload.additionalData;


        //ko hien notify hinh cai chuong
        return true;*/

        //ko hien notify hinh cai chuong
        return true;

    }
}
