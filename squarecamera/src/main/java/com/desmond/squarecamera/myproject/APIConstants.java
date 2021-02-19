package com.desmond.squarecamera.myproject;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

import java.io.File;

/**
 * Created by MrPhuong on 2016-07-21.
 */
public class APIConstants {

    public static final String VIDEO_URL = "VIDEO_URL";
    public static final String IMAGE_TMP = "tmpwj.jpg";
    public static final int TIME_VIDEO_RECORD_SECOND = 60;//SECOND
    public static final int VIDEO_BITRATE = 4000000;//700 * 1024;


    public static Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    public static Uri SELECTED_IMAGE = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), APIConstants.IMAGE_TMP));

    public static final File FOLDER_IMAGE = new File(Environment.getExternalStorageDirectory().getPath());

    public static final String LANGUAGE_US = "en_US";
    public static final String LANGUAGE_VIETNAM = "vi";
    public static final String LANGUAGE_CHINA = "zh-TW";
    public static final String LANGUAGE = "language";

    public static final String font_Roboto_Light = "fonts/Roboto-Light.ttf";
    public static final String font_Roboto_Regular = "fonts/Roboto-Regular.ttf";
    public static final String font_Roboto_Bold = "fonts/Roboto-Bold.ttf";
    public static final boolean isUseCustomeFont = true;

    public static final String USER_NEWS = "user_news";

    public static final int TIMEOUT_DIALOG = 50000;//30000
    public static final int TIMEOUT_DIALOG_LONG = 120000;
    public static void timerDelayRemoveDialog(long time, final Dialog d) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try{
                    if(d != null)
                        if(d.isShowing())
                            d.dismiss();
                }
                catch (Exception ex) {
                }
            }
        }, time);
    }

}
