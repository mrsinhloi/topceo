package com.workchat.core.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.workchat.core.utils.MyUtils;

public class Deeplink {

    public static final String PACKAGE_NAME = "epapersmart.teamwork";
//    public static final String ACTIVITY_DEEPLINK = "epapersmart.myxteam.activities.MH01_Loading_Activity";
    public static final String ACTIVITY_DEEPLINK = "epapersmart.myxteam.activities.DeeplinkActivity";



    public static Uri getDeeplinkTimelineUser(String id, String name, String avatar) {
        String url = "myxteam://view/timeline?id=" + id + "&name=" + name + "&avatar=" + avatar;
        return Uri.parse(url);
    }
    public static Uri getDeeplinkProject(long projectId) {
        String url = "myxteam://view/project?id=" + projectId;
        return Uri.parse(url);
    }
    public static Uri getDeeplinkTask(long taskId) {
        String url = "myxteam://view/task?id=" + taskId;
        return Uri.parse(url);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void openMyxteam(Context context, Uri uri) {
        if (context != null) {
            if (MyUtils.isAppInstalled(context, PACKAGE_NAME)) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName(PACKAGE_NAME, ACTIVITY_DEEPLINK));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(uri!=null){
                    //uri la deeplink project, task, timeline
                    intent.setData(uri);
                }
                context.startActivity(intent);

            } else {
//                MyUtils.showToast(context, R.string.request_install_myxteam);
                MyUtils.openApplicationInStore(context, PACKAGE_NAME);
            }
        }
    }


}
