package com.topceo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.topceo.R;
import com.topceo.config.MyApplication;

import java.util.ArrayList;

public class PermissionUtils {
    static String[] arrPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static boolean checkPermission() {
        boolean result = true;
        for (int i = 0; i < arrPermissions.length; i++) {
            int grant = ContextCompat.checkSelfPermission(MyApplication.context, arrPermissions[i]);
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }

        return result;
    }


    public static ArrayList<String> listPermissionNeedRequest() {
        ArrayList<String> list = new ArrayList<>();
        for (String item :
                arrPermissions) {
            if (ContextCompat.checkSelfPermission(MyApplication.context, item) != PackageManager.PERMISSION_GRANTED) {
                list.add(item);
            }
        }
        return list;
    }

    public static void requestPermission(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> list = listPermissionNeedRequest();
            if (list.size() > 0) {
                //lay item 0, neu bi tu choi thi bao ra
                String first = list.get(0);
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, first)) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity);
                    dialog.setTitle(R.string.notification);
                    dialog.setMessage(R.string.deny_permission_notify);
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MyUtils.goToSettings(MyApplication.context);
                        }
                    });
                    dialog.setNegativeButton(R.string.no, null);
                    android.app.AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                } else {
                    activity.requestPermissions(list.toArray(new String[list.size()]), requestCode);
                }

            }
        }
    }

    public static boolean isAllowReadSdCard() {

        boolean result = true;

        String[] arrPermissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE};
        for (int i = 0; i < arrPermissions.length; i++) {
            int grant = ContextCompat.checkSelfPermission(MyApplication.context, arrPermissions[i]);
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }

        return result;
    }
}
