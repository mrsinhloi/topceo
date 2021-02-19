package com.workchat.core.share;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class SharingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onSharedIntent();

    }

    private void onSharedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {

            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/") /*|| type.startsWith("video/")*/) {
                handleSendImage(intent); // Handle single image being sent
            } else if (type.startsWith("application/")) {
                handleSendDocument(intent);
            }

        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("*/*") || type.startsWith("image/")) {//images+videos
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            //// Handle other intents, such as being started from the home screen
            MyUtils.log("onSharedIntent: nothing shared");
        }

        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //1
    void handleSendText(Intent i) {//text
        String sharedText = i.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
//            MyUtils.log(sharedText);
            ChatApplication.Companion.setSharedText(sharedText);
            selectRoom();
        }
    }

    //2
    void handleSendImage(Intent intent) {//1 image hoac 1 video
        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            // Update UI to reflect image being shared
//            MyUtils.log(imageUri.toString());
//            if(FileUtil.isGooglePhotosUri(uri)){
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
//            }
            ChatApplication.Companion.setSharedImageOrVideo(uri);
            selectRoom();
        }
    }

    //3
    void handleSendMultipleImages(Intent intent) {//nhieu hinh, nhieu video
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
//            MyUtils.log(imageUris.toString());
            Uri uri = imageUris.get(0);
//            if(FileUtil.isGooglePhotosUri(uri)){
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
//            }

            ChatApplication.Companion.setSharedMultiImages(imageUris);
            selectRoom();
        }
    }

    //4
    void handleSendDocument(Intent i) {//text
        Uri uri = (Uri) i.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            // Update UI to reflect image being shared
//            MyUtils.log(imageUri.toString());
            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            ChatApplication.Companion.setSharedDocument(uri);
            selectRoom();
        }
    }

    //goto room
    void selectRoom() {
        ChatApplication.Companion.openMainActivityAndSearchRoom();
    }

}
