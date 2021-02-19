package com.workchat.core.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.workchat.core.autolink.AutoLinkMode;
import com.workchat.core.chat.ChatActivity;
import com.workchat.core.chat.MH24_ShowImageAndVideoActivity;
import com.workchat.core.chat.MH25_ShowVideoActivity;
import com.workchat.core.chat.MH26_ViewPdfFile;
import com.workchat.core.chathead.events.AddRoomEvent;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.api.ParseJson;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.Contact;
import com.workchat.core.models.chat.ContentType;
import com.workchat.core.models.chat.Image;
import com.workchat.core.models.chat.LocaleHelper;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Project;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.corechat.BuildConfig;
import com.workchat.corechat.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class MyUtils {

    public static boolean checkInternetConnection(Context context) {
        boolean b = false;
        if (context != null) {
            ConnectivityManager conMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr != null) {
                NetworkInfo nw = conMgr.getActiveNetworkInfo();
                if (nw != null && nw.isConnectedOrConnecting()) {
                    b = true;
                }
            }
        }
        return b;
    }

    public static void showThongBao(Context context) {
        if (context != null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                View view = inflater.inflate(R.layout.layout_no_internet, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);

                final AlertDialog dialog = builder.create();
//                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();

                Button btn = (Button) view.findViewById(R.id.btnOK);
                btn.setOnClickListener(v -> {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                });


                // Get screen width and height in pixels
                // The absolute width of the available display size in pixels.
                int displayWidth = getScreenWidth(context);
                // The absolute height of the available display size in pixels.
                int displayHeight = getScreenHeight(context);

                // Initialize a new window manager layout parameters
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

                // Copy the alert dialog window attributes to new layout parameter instance
                layoutParams.copyFrom(dialog.getWindow().getAttributes());

                // Set the alert dialog window width and height
                // Set alert dialog width equal to screen width 90%
                // int dialogWindowWidth = (int) (displayWidth * 0.9f);
                // Set alert dialog height equal to screen height 90%
                // int dialogWindowHeight = (int) (displayHeight * 0.9f);

                // Set alert dialog width equal to screen width 70%
                int dialogWindowWidth = (int) (displayWidth * 0.7f);
                // Set alert dialog height equal to screen height 70%
//                int dialogWindowHeight = (int) (displayHeight * 0.7f);

                // Set the width and height for the layout parameters
                // This will bet the width and height of alert dialog
                layoutParams.width = dialogWindowWidth;
//                layoutParams.height = dialogWindowHeight;

                // Apply the newly created layout parameters to the alert dialog window
                dialog.getWindow().setAttributes(layoutParams);

            }
        }
    }

    public static void showThongBao(final Activity context, final boolean isFinish) {
        if (context != null) {
            LayoutInflater inflater = context.getLayoutInflater();
            if (inflater != null) {
                View view = inflater.inflate(R.layout.layout_no_internet, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);

                final AlertDialog dialog = builder.create();
                dialog.show();

                Button btn = (Button) view.findViewById(R.id.btnOK);
                btn.setOnClickListener(v -> {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (isFinish) {
                        context.finish();
                    }
                });


            }
        }
    }

    public static void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToastDebug(Context context, String message) {
        if (BuildConfig.DEBUG) {
            if (context != null && !TextUtils.isEmpty(message)) {
//                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void showToastDebugInThread(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MyUtils.showToastDebug(context, message);
            }
        });
    }

    public static void showToast(Context context, int message) {
        if (context != null) {
            Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToastDebug(Context context, int message) {
        if (BuildConfig.DEBUG) {
            if (context != null) {
                Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void showToast(Activity context, int message) {
        if (context != null) {
            Toast.makeText(context, context.getResources().getString(message), Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToast(Activity context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }


    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    public static String getCurrentDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    /*public static String getIMEI(Context context) {
        String kq = "";
        try {
            if (context != null) {
                kq = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return kq;
                }
                if (mTelephony.getDeviceId() != null)
                    kq = mTelephony.getDeviceId(); //*** use for mobiles
                else
                    kq = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); //*** use for tablets
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kq.toUpperCase();
    }*/

    public static String getIMEI(Context context) {
        /*String kq = "";
        if (context != null) {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null)
                kq = mTelephony.getDeviceId(); /*//*** use for mobiles
         else
         kq = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); /*//*** use for tablets
         }

         return kq.toUpperCase();*/
        String strUserDeviceID = UUID.randomUUID().toString();
        try {
            strUserDeviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strUserDeviceID.toUpperCase();
    }

    public static String getModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static int getAppVersionCode(Context context) {
        int code = -1;
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            code = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getAppVersionName(Context context) {
        String name = "";
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            name = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getUnsignedString(String s) {
        StringBuffer unsignedString = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            unsignedString.append(getUnsignedChar(s.charAt(i)));
        }

        return unsignedString.toString();
    }

    public static char getUnsignedChar(char c) {

        if (c == '\u00E1' || c == '\u00E0' || c == '\u1EA3' || c == '\u00E3'
                || c == '\u1EA1' || c == '\u0103' || c == '\u1EAF'
                || c == '\u1EB1' || c == '\u1EB3' || c == '\u1EB5'
                || c == '\u1EB7' || c == '\u00E2' || c == '\u1EA5'
                || c == '\u1EA7' || c == '\u1EA9' || c == '\u1EAB'
                || c == '\u1EAD') {
            return 'a';
        } else if (c == '\u00C1' || c == '\u00C0' || c == '\u1EA2'
                || c == '\u00C3' || c == '\u1EA0' || c == '\u0102'
                || c == '\u1EAE' || c == '\u1EB0' || c == '\u1EB2'
                || c == '\u1EB4' || c == '\u1EB6' || c == '\u00C2'
                || c == '\u1EA4' || c == '\u1EA6' || c == '\u1EA8'
                || c == '\u1EAA' || c == '\u1EAC') {
            return 'A';
        } else if (c == '\u00E9' || c == '\u00E8' || c == '\u1EBB'
                || c == '\u1EBD' || c == '\u1EB9' || c == '\u00EA'
                || c == '\u1EBF' || c == '\u1EC1' || c == '\u1EC3'
                || c == '\u1EC5' || c == '\u1EC7') {
            return 'e';
        } else if (c == '\u00C9' || c == '\u00C8' || c == '\u1EBA'
                || c == '\u1EBC' || c == '\u1EB8' || c == '\u00CA'
                || c == '\u1EBE' || c == '\u1EC0' || c == '\u1EC2'
                || c == '\u1EC4' || c == '\u1EC6') {
            return 'E';
        } else if (c == '\u00ED' || c == '\u00EC' || c == '\u1EC9'
                || c == '\u0129' || c == '\u1ECB') {
            return 'i';
        } else if (c == '\u00CD' || c == '\u00CC' || c == '\u1EC8'
                || c == '\u0128' || c == '\u1ECA') {
            return 'I';
        } else if (c == '\u00F3' || c == '\u00F2' || c == '\u1ECF'
                || c == '\u00F5' | c == '\u1ECD' || c == '\u00F4'
                || c == '\u1ED1' || c == '\u1ED3' || c == '\u1ED5'
                || c == '\u1ED7' || c == '\u1ED9' || c == '\u01A1'
                || c == '\u1EDB' || c == '\u1EDD' || c == '\u1EDF'
                || c == '\u1EE1' || c == '\u1EE3') {
            return 'o';
        } else if (c == '\u00D3' || c == '\u00D2' || c == '\u1ECE'
                || c == '\u00D5' | c == '\u1ECC' || c == '\u00D4'
                || c == '\u1ED0' || c == '\u1ED2' || c == '\u1ED4'
                || c == '\u1ED6' || c == '\u1ED8' || c == '\u01A0'
                || c == '\u1EDA' || c == '\u1EDC' || c == '\u1EDE'
                || c == '\u1EE0' || c == '\u1EE2') {
            return 'O';
        } else if (c == '\u00FA' || c == '\u00F9' || c == '\u1EE7'
                || c == '\u0169' | c == '\u1EE5' || c == '\u01B0'
                || c == '\u1EE9' || c == '\u1EEB' || c == '\u1EED'
                || c == '\u1EEF' || c == '\u1EF1') {
            return 'u';
        } else if (c == '\u00DA' || c == '\u00D9' || c == '\u1EE6'
                || c == '\u0168' | c == '\u1EE4' || c == '\u01AF'
                || c == '\u1EE8' || c == '\u1EEA' || c == '\u1EEC'
                || c == '\u1EEE' || c == '\u1EF0') {
            return 'U';
        } else if (c == '\u00FD' || c == '\u1EF3' || c == '\u1EF7'
                || c == '\u1EF9' || c == '\u1EF5') {
            return 'y';
        } else if (c == '\u00DD' || c == '\u1EF2' || c == '\u1EF6'
                || c == '\u1EF8' || c == '\u1EF4') {
            return 'Y';
        } else if (c == '\u0111') {
            return 'd';
        } else if (c == '\u0110') {
            return 'D';
        }
        return c;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * SERVER ĐỔI SANG DÙNG UNIT_TIME(LONG) - SECOND
     * Hom nay: "HH:mm"
     * Cac ngay khac: "dd/MM/yy"
     * Server tra ve ngay 0, phai cong them mui gio cua thiet bi
     *
     * @param dateString
     * @return
     */
    public static String getDateChat(String dateString) {
        String time = "";
        try {
            SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
            java.util.Date date = new Date(Long.parseLong(dateString) * 1000);

            String day = getDateChatFull(dateString);
            if (getCurrentDate().equals(day)) {//cung ngay
                newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
            } else {//neu la cac ngay khac
                newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_SHORT_MMM, Locale.getDefault());
            }
            time = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDateChat(long dateString) {
        String time = "";
        try {
            SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
            java.util.Date date = new Date(dateString * 1000);

            String day = getDateChatFull(dateString);
            if (getCurrentDate().equals(day)) {//cung ngay
                newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
            } else {//neu la cac ngay khac
                newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_SHORT_MMM, Locale.getDefault());
            }
            time = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long getLastLogDateToLong(String dateString) {
        long time = 0;
        try {
            time = Long.parseLong(dateString) * 1000;//second to miliseconds

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * Hien thi HH:mm
     *
     * @param dateString
     * @return
     */
    public static String getDateChatHHmm(String dateString) {
        String time = "";
        try {
            SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM_AA, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
            java.util.Date date = new Date(Long.parseLong(dateString) * 1000);
            time = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDateChatHHmm(long dateString) {
        String time = "";
        try {
            SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM_AA, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
            java.util.Date date = new Date(dateString * 1000);
            time = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDateChatFull(String dateString) {
        String time = "";
        if (!TextUtils.isEmpty(dateString)) {
            try {
                SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
                java.util.Date date = new Date(Long.parseLong(dateString) * 1000);
                time = newFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    public static String getDateChatFull(long dateString) {
        String time = "";
        if (dateString > 0) {
            try {
                SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
                java.util.Date date = new Date(dateString * 1000);
                time = newFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    public static boolean isOver40minutes(long dateLong) {
        boolean isOver = false;
        if (dateLong > 0) {
            //your UTC time var
            long time = dateLong * 1000;

            //convert it
            /*Time timeFormat = new Time();
            long offset = TimeZone.getDefault().getOffset(time);
            timeFormat.set(time + offset);

            //use the value
            long localTime = timeFormat.toMillis(true);*/

            long currentTime = new java.util.Date().getTime();
            /*long currentTime3 = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            long currentTime = cal.getTimeInMillis();*/
            long distance = (currentTime - time) / (1000 * 60);
            isOver = distance > 40;
        }
        return isOver;
    }

    public static String getDate(long dateString, String format) {
        String time = "";
        if (dateString > 0) {
            try {
                SimpleDateFormat newFormat = new SimpleDateFormat(format, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
                java.util.Date date = new Date(dateString * 1000);
                time = newFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    public static String getDateNotify(String dateString) {
        String time = "";
        try {
            SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM_AA_AND_DATE, Locale.getDefault());//de chieu dai bang voi ngay, nhin de hon
            java.util.Date date = new Date(Long.parseLong(dateString) * 1000);
            time = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    private static Pattern pattern;
    private static Matcher matcher;

    public static boolean isImageUrl(String image) {
        if (TextUtils.isEmpty(image)) return false;
        String regex = "([^\\s]+(\\.(?i)(/bmp|jpg|gif|png|jpeg))$)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(image);
        return matcher.matches();
    }

    public static boolean isImageGifUrl(String image) {
        if (TextUtils.isEmpty(image)) return false;
        String regex = "([^\\s]+(\\.(?i)(gif))$)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(image);
        return matcher.matches();
    }

    public static boolean isDocumentUrl(String document) {
        /*if (document == null) return false;
        String regex = "([^\\s]+(\\.(?i)(/doc|docx|xls|xlsx|pdf|ppt|pptx))$)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(document);
        return matcher.matches();*/
        if (document.endsWith(".doc") ||
                document.endsWith(".docx") ||
                document.endsWith(".xls") ||
                document.endsWith(".xlsx") ||
                document.endsWith(".ppt") ||
                document.endsWith(".pptx") ||
                document.endsWith(".pdf")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function will take an URL as input and return the file name.
     * <p>Examples :</p>
     * <ul>
     * <li>http://example.com/a/b/c/test.txt -> test.txt</li>
     * <li>http://example.com/ -> an empty string </li>
     * <li>http://example.com/test.txt?param=value -> test.txt</li>
     * <li>http://example.com/test.txt#anchor -> test.txt</li>
     * </ul>
     *
     * @param urlString
     * @return
     */
    public static String getFileNameFromUrl(String urlString) {
        urlString = urlString.replace("\\", "/");
        String name = urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
        if (name.contains("%20")) {
            name = name.replace("%20", " ");
        }
        return name;
    }


    public static String getMention(String userName, String userId) {
        return "@[" + userName + "](userid:" + userId + ")";
    }

    public static ArrayList<String> findUserMention(String text) {
        ArrayList<String> list = new ArrayList<String>();
        String patternString1 = "\\[([\\s\\S\\d _][^\\]]+)\\]";

        Pattern pattern = Pattern.compile(patternString1);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("TEST", "my_log: " + message);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getAddress(Context context, String lat, String lon) {
        String ret = "";
        if (context != null) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
                if (addresses != null && addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    if (returnedAddress != null) {
                        StringBuilder strReturnedAddress = new StringBuilder();

                        int number = returnedAddress.getMaxAddressLineIndex();
                        if (number > 0) {
                            for (int i = 0; i < number; i++) {
                                if (i == returnedAddress.getMaxAddressLineIndex() - 1) {
                                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                                } else {
                                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                                }
                            }
                        } else {
                            strReturnedAddress.append(returnedAddress.getAddressLine(0));
                        }
                        ret = strReturnedAddress.toString();
                    }

                } else {
                    ret = "No Address returned!";
                }
            } catch (IOException e) {
                e.printStackTrace();
                ret = "No Address!";
            }
        }
        return ret;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize2(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static void copy(Context context, String text) {
        if (context != null) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            MyUtils.showToast(context, R.string.copied);
        }
    }

    private static final String MONEY_FORMAT = "###,###,###.###";

    public static String getMoneyFormat(String money) {
        String result = "";

        if (!TextUtils.isEmpty(money)) {
            result = money.trim();
            if (!TextUtils.isEmpty(result)) {
                //replace het cac dau cham, dau phay, khoang trang
                result = result.replaceAll("\\s+", "");
                result = result.replace(".", "");
                result = result.replace(",", "");

                double value = 0;
                try {
                    value = Double.parseDouble(result);
                    result = new DecimalFormat(MONEY_FORMAT).format(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public static void howLong(long start, String message) {
        long time = SystemClock.elapsedRealtime() - start;
        Log.d("test", "how long = " + time + " - " + message);
    }

    public static int getScreenWidth(Context context) {
        int widthScreen = 0;
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Display display = wm.getDefaultDisplay();
                if (display != null) {
                    Point size = new Point();
                    display.getSize(size);
                    widthScreen = size.x;
                }
            }
        }
        return widthScreen;
    }

    public static int getScreenHeight(Context context) {
        int heightScreen = 0;
        if (context != null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                Display display = wm.getDefaultDisplay();
                if (display != null) {
                    Point size = new Point();
                    display.getSize(size);
                    heightScreen = size.y;
                }
            }
        }
        return heightScreen;
    }

    public static void showKeyboard(Activity context) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void hideKeyboardForce(Context context) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }


    public static void hideKeyboard(Activity context) {
        if (context != null) {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (context.getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_id_root);
     * setHideKeyboard(this, layout);
     *
     * @param context
     * @param view
     */
    public static void hideKeyboard(final Context context, View view) {
        if (context != null && view != null) {
            try {
                //Set up touch listener for non-text box views to hide keyboard.
                if (!(view instanceof EditText || view instanceof ScrollView)) {
                    view.setOnTouchListener((v, event) -> {
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(v.getWindowToken(), 0);//InputMethodManager.HIDE_NOT_ALWAYS
                        return false;
                    });
                }
                //If a layout container, iterate over children and seed recursion.
                if (view instanceof ViewGroup) {
                    for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                        View innerView = ((ViewGroup) view).getChildAt(i);
                        hideKeyboard(context, innerView);
                    }
                }

                if (view instanceof EditText) {
                    InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(view.getWindowToken(), 0);//InputMethodManager.HIDE_NOT_ALWAYS
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void showAlertDialog(Context context, String title, String message) {
        if (context != null && !TextUtils.isEmpty(message)) {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
            dialog.setMessage(message);
            dialog.setTitle(title);
            dialog.setPositiveButton(R.string.ok, (arg0, arg1) -> {

            });
            android.app.AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }
    }

    public static void showAlertDialog(Context context, String message) {

        if (context != null && !TextUtils.isEmpty(message)) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton(R.string.ok, (arg0, arg1) -> {

            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public static void showAlertDialog(Activity context, String message, boolean isFinish) {

        if (context != null && !TextUtils.isEmpty(message)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.notification);
            builder.setMessage(message);
            builder.setPositiveButton(R.string.close, (arg0, arg1) -> {
                if (isFinish) {
                    context.finish();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (isFinish) {
                        context.finish();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            if (context != null && !context.isFinishing()) {
                alertDialog.show();
            }
        }
    }

    public static void showAlertDialog(Context context, int message) {
        if (context != null && message > 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setPositiveButton(R.string.ok, (arg0, arg1) -> {

            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public static androidx.appcompat.app.AlertDialog createDialogLoading(AppCompatActivity compatActivity, int title) {
        if (compatActivity != null && !compatActivity.isFinishing() && title > 0) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(compatActivity)
                    .setTitle(title)
                    .setView(R.layout.dialog_loading)
                    .setCancelable(true);
            return builder.create();
        }
        return null;
    }


    public static void goToWeb(Context c, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        c.startActivity(i);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    /**
     * Tao text strike through text
     *
     * @param text
     * @return
     */
    public static SpannableStringBuilder getStrikeText(String text) {
        SpannableStringBuilder spannableStringBuilder = null;
        if (!TextUtils.isEmpty(text)) {
            spannableStringBuilder = new SpannableStringBuilder(text);
            StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
            spannableStringBuilder.setSpan(strikethroughSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }


    public static void openApplicationInStore(Context context, String packageName) {
        if (context != null && !TextUtils.isEmpty(packageName)) {
            try {
                String url = "market://details?id=" + packageName;
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (ActivityNotFoundException anfe) {
                String url = "https://play.google.com/store/apps/details?id=" + packageName;
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isEmailValid(String email) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String endcodeURL(String link) {
        if (!TextUtils.isEmpty(link)) {
            if (link.contains(" ")) {
                try {
                    link = link.replace(" ", "%20");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return link;
    }

    public static void inviteSms(Context context, String phone) {
        if (!TextUtils.isEmpty(phone)) {
            try {
                //goi SMS moi dung ung dung MBN
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("address", phone);
                smsIntent.putExtra("sms_body", "");
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
            } catch (Exception e) {
                e.printStackTrace();
                MyUtils.showToast(context, R.string.not_found_sim);
            }
        } else {
            MyUtils.showToast(context, R.string.phone_empty);
        }
    }

    public static void inviteSms(Context context, String phone, String message) {
        if (!TextUtils.isEmpty(phone)) {
            try {
                //goi SMS moi dung ung dung MBN
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("address", phone);
                smsIntent.putExtra("sms_body", message);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
            } catch (Exception e) {
                e.printStackTrace();
                MyUtils.showToast(context, R.string.not_found_sim);
            }
        } else {
            MyUtils.showToast(context, R.string.phone_empty);
        }
    }

    public static void chatWithUser(Context context, Member member) {
        if (member != null) {
            //chat voi user
            if (checkInternetConnection(context)) {
                if (ChatActivity.isExists) {
                    context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
                }
                UserChat user = member.getUserInfo();
                if (user != null) {
                    UserChatCore data = new UserChatCore();
                    data.set_id(member.getUserId());
                    data.setName(user.getName());
                    data.setEmail(user.getEmail());
                    data.setAvatar(user.getAvatar());

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(UserChatCore.USER_MODEL, data);
                    context.startActivity(intent);
                }
            } else {
                MyUtils.showThongBao(context);
            }
        }
    }

    public static void chatWithUser(Context context, UserInfo member) {
        if (member != null) {
            //chat voi user
            if (checkInternetConnection(context)) {
                if (ChatActivity.isExists) {
                    context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
                }
                UserChatCore data = new UserChatCore();
                data.set_id(member.get_id());
                data.setName(member.getName());
                data.setEmail(member.getEmail());
                data.setAvatar(member.getAvatar());

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(UserChatCore.USER_MODEL, data);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                MyUtils.showThongBao(context);
            }
        }
    }

    public static void chatWithUser(Context context, UserChatCore member, boolean isForward) {
        if (member != null) {
            //chat voi user
            if (checkInternetConnection(context)) {
                if (ChatActivity.isExists) {
                    context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
                }
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(UserChatCore.USER_MODEL, member);
                intent.putExtra(RoomLog.IS_FORWARD, isForward);
                context.startActivity(intent);
            } else {
                MyUtils.showThongBao(context);
            }
        }
    }

    public static void chatWithSupport(Context context) {
        if (checkInternetConnection(context)) {
            if (ChatActivity.isExists) {
                context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
            }
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(UserChatCore.IS_CHAT_WITH_SUPPORT, true);
            context.startActivity(intent);
        } else {
            MyUtils.showThongBao(context);
        }
    }

    public static void chatWithUser(Context context, UserInfo member, boolean isForward) {
        if (member != null) {
            //chat voi user
            if (checkInternetConnection(context)) {
                if (ChatActivity.isExists) {
                    context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
                }
                UserChatCore data = new UserChatCore();
                data.set_id(member.get_id());
                data.setName(member.getName());
                data.setEmail(member.getEmail());
                data.setAvatar(member.getAvatar());

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(UserChatCore.USER_MODEL, data);
                intent.putExtra(RoomLog.IS_FORWARD, isForward);
                context.startActivity(intent);
            } else {
                MyUtils.showThongBao(context);
            }
        }
    }

    public static void chatWithUser(Context context, Contact contact) {
        if (contact != null) {
            //chat voi user
            if (checkInternetConnection(context)) {
                if (ChatActivity.isExists) {
                    context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
                }
                UserChatCore data = new UserChatCore();
                data.set_id(contact.getContactId());
                data.setName(contact.getName());
                data.setEmail(contact.getEmail());
                data.setAvatar(contact.getAvatar());

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(UserChatCore.USER_MODEL, data);
                context.startActivity(intent);
            } else {
                MyUtils.showThongBao(context);
            }
        }
    }


    public static void chatWithRoom(Context context, String roomId, boolean isForward) {
        if (roomId != null) {
            if (ChatActivity.isExists) {
                context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
            }

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Room.ROOM_ID, roomId);
            intent.putExtra(RoomLog.IS_FORWARD, isForward);
            context.startActivity(intent);
        }
    }

    public static String getPhoneNumberOnly(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            //thay the khoang trong 0901 102 123
            if (phoneNumber.contains(" ")) {
                phoneNumber = phoneNumber.replace(" ", "");
            }
            //thay the dau gach ngang 0901-102-123
            if (phoneNumber.contains("-")) {
                phoneNumber = phoneNumber.replace("-", "");
            }
            //+84 thanh 0
//            phoneNumber = phoneNumber.replace("+84", "0");
        }
        return phoneNumber;
    }

    public static void setClipboard(Context context, String text) {
        if (context != null && !TextUtils.isEmpty(text)) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
                clipboard.setPrimaryClip(clip);
            }
            MyUtils.showToast(context, R.string.link_copied_to_clipboard);
        }
    }


    /**
     * Tat ca cac ky tu chi la emoji thi true, nguoc lai false
     *
     * @param s
     * @return
     */
    public static boolean isOnlyEmojiCharacter(String s) {
        boolean isOnlyEmoji = false;
        if (!TextUtils.isEmpty(s)) {
            for (int i = 0; i < s.length(); i++) {
                int type = Character.getType(s.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    isOnlyEmoji = true;
                } else {
                    isOnlyEmoji = false;
                    break;
                }
            }
        }
        return isOnlyEmoji;
    }

    public static void openFileImages(Context context, ArrayList<String> urls, int positionSelected) {
        try {
            Intent intent = new Intent(context, MH24_ShowImageAndVideoActivity.class);
            intent.putExtra(MH24_ShowImageAndVideoActivity.IMAGE_URLS, urls);
            intent.putExtra(MH24_ShowImageAndVideoActivity.IMAGE_URLS_POSITION_SELECTED, positionSelected);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openVideo(Context context, String url) {
        try {
            Intent intent = new Intent(context, MH25_ShowVideoActivity.class);
            intent.putExtra(MH25_ShowVideoActivity.VIDEO_URL, url);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * width, height ratio
     *
     * @param thumbWidth
     * @param thumbHeight
     * @param requestWidth
     * @return
     */
    public static int getHeightImage(int thumbWidth, int thumbHeight, int requestWidth) {
        int requestHeight = requestWidth;
        if (thumbWidth > 0 && thumbHeight > 0) {
            try {
                float width = thumbWidth;
                float height = thumbHeight;
                float ratio = height / width;
                requestHeight = (int) (ratio * requestWidth);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return requestHeight;
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static String getGUID() {
        return UUID.randomUUID().toString();
    }

    public static String getFileNameAndExtension(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            String name = filePath.substring(filePath.lastIndexOf('/') + 1);

            //loai bo dau tieng viet
            /*name = MyUtils.getUnsignedString(name);

            //thay the khoang trong, loai bo dau trong file
            name = name.replace(" ", "_");
            name = name.replace("-", "_");*/

            return name;
        }
        return "";
    }

    public static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    ///////////////////////////////////////////////////////////////////
    public static File createImageFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), imageFileName));
        }
        File image = null;
        try {
            image = new File(outputFileUri.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int requestWidth) {
        int width = image.getWidth();
        int height = image.getHeight();

        //resize theo chieu cua image
        /*float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            //neu hinh lon hon size yeu cau thi moi resize, ko thi giu nguyen hinh nho
            if (width > requestWidth) {
                width = requestWidth;
            }

            height = (int) (width / bitmapRatio);
        } else {
            //neu hinh lon hon size yeu cau thi moi resize, ko thi giu nguyen hinh nho
            if (height > requestWidth) {
                height = requestWidth;
            }
            width = (int) (height * bitmapRatio);
        }*/

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            //width>height
            //neu hinh lon hon size yeu cau thi moi resize, ko thi giu nguyen hinh nho
            if (height > requestWidth) {
                height = requestWidth;
            }

            width = (int) (height * bitmapRatio);
        } else {
            //height>width
            //neu hinh lon hon size yeu cau thi moi resize, ko thi giu nguyen hinh nho
            if (width > requestWidth) {
                width = requestWidth;
            }
            height = (int) (width / bitmapRatio);
        }


        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * 75: CHO CHAT LUONG MEDIUM
     *
     * @param mp
     * @return image path save
     */
    public static String saveBitmap(Bitmap mp, String picturePath) {
        Bitmap bitmap;
        OutputStream output;

        bitmap = mp;
        try {
            output = new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, output);//70:size 167Kb, 100:size 1.54Mb
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return picturePath;
    }

    public static String saveBitmapThumb(Bitmap mp, String picturePath) {
        Bitmap bitmap;
        OutputStream output;

        bitmap = mp;
        try {
            output = new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output);//70:size 167Kb, 100:size 1.54Mb
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return picturePath;
    }

    public static float getSizeBytes(long length) {
        // Get length of file in bytes
        float fileSizeInBytes = length;
        return fileSizeInBytes;
    }

    /**
     * Tra ve so giay cua mp3
     *
     * @param file
     * @return
     */
    public static int getAudioDuration(File file) {

        int seconds = 0;

        try {
            // load data file
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(file.getAbsolutePath());

            String out = "";
            // get mp3 info

            // convert duration to minute:seconds
            String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.v("time", duration);
            long dur = Long.parseLong(duration);
            seconds = (int) TimeUnit.MILLISECONDS.toSeconds(dur);

            // close object
            metaRetriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;
    }

    public static void setNumberChatUnread(int count) {
        //SET NUMBER UNREAD
        ChatApplication.Companion.setNumberChatUnread(count);
    }

    public static void setNumberNotifyUnread(int count) {
        //SET NUMBER UNREAD
        ChatApplication.Companion.setNumberNotifyUnread(count);
    }

    public static void openChatRoom(Context context, String roomId) {
        if (context != null && !TextUtils.isEmpty(roomId)) {
//            if (ChatActivity.isExists) {
            context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
//            }
            // Open chat room
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Room.ROOM_ID, roomId);
            context.startActivity(intent);
        }
    }


    public static void openChatRoom(Context context, Project project) {
        if (context != null && project != null) {
//            if (ChatActivity.isExists) {
            context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
//            }
            // Open chat room
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Project.PROJECT_MODEL, project);
            context.startActivity(intent);
        }
    }


    public static void chatWithRoom(Context context, Room room, boolean isForward) {
        if (room != null && context != null) {
            context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));

            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(RoomLog.IS_FORWARD, isForward);
            Room room2 = room.duplicate();
            room2.setLastLog(null);
            intent.putExtra(Room.ROOM, room2);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            context.startActivity(intent);
        }
    }

    /**
     * Mở room và lấy chat log, mở lịch
     *
     * @param context
     * @param roomId
     * @param chatLogId
     */
    public static void openChatRoom(Context context, String roomId, String chatLogId) {

        if (context != null && !TextUtils.isEmpty(roomId)) {
//            if (ChatActivity.isExists) {
            context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
//            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Open chat room
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Room.ROOM_ID, roomId);
                    intent.putExtra(RoomLog.ROOM_LOG_ID, chatLogId);
                    context.startActivity(intent);
                }
            }, 500);
        }

        /*if (context != null && !TextUtils.isEmpty(roomId)) {
            if (ChatActivity.isExists) {
                if(roomId.equals(ChatActivity.roomId)){
                    // Open chat room
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    return;
                }else{
                    context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
                }
            }


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Open chat room
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Room.ROOM_ID, roomId);
                    intent.putExtra(RoomLog.ROOM_LOG_ID, chatLogId);
                    context.startActivity(intent);
                }
            }, 300);
        }*/

        /*if (context != null && !TextUtils.isEmpty(roomId)) {
            // Open chat room
            Intent intent = new Intent(context, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Room.ROOM_ID, roomId);
            intent.putExtra(RoomLog.ROOM_LOG_ID, chatLogId);
            context.startActivity(intent);
        }*/


    }

    /**
     * Kiem tra app mbn da duoc cai dat chua?
     * true: mo app
     * false: de nghi download
     *
     * @param context
     */
    public static void openMbnApp(final Context context, String articleId) {
        if (!TextUtils.isEmpty(articleId)) {
            final String mbnPackage = "com.muabannhanh";
            boolean isExist = MyUtils.isAppInstalled(context, mbnPackage);
            if (!isExist) {
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(context);
                dialog.setMessage(R.string.notify_install_mbn_app);
                dialog.setCancelable(false);
                dialog.setPositiveButton(R.string.install, (arg0, arg1) -> {
                    arg0.dismiss();
                    openApplicationInStore(context, mbnPackage);
                });
                dialog.setNegativeButton(R.string.close, (dialog1, which) -> dialog1.dismiss());

                androidx.appcompat.app.AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            } else {
                String deeplink = "mbnapp://chat/room?type=openArticle&articleId=" + articleId;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(deeplink));
                context.startActivity(intent);
            }
        }
    }

    public static void goToSettings(Context context) {
        if (context != null) {
            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myAppSettings);
        }
    }

    public static boolean haveStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error", "You already have the permission");
            return true;
        }
    }

    public static void downloadFile(Context context, String file) {
        if (context != null) {
            if (URLUtil.isValidUrl(file)) {
                if (haveStoragePermission(context)) {
                    String nameAndType = getFileNameFromUrl(file);
                    Uri Download_Uri = Uri.parse(file);
                    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setAllowedOverRoaming(false);
                    request.allowScanningByMediaScanner();
                    request.setTitle("Downloading " + nameAndType);
                    request.setDescription("Downloading... ");
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + context.getString(R.string.app_name) + "/" + nameAndType);
                    //notify after download completed
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //set type
                    String path = "file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + context.getString(R.string.app_name) + "/" + nameAndType;
                    File newFile = new File(path);
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String ext = newFile.getName().substring(newFile.getName().lastIndexOf(".") + 1);
                    String type = mime.getMimeTypeFromExtension(ext);
                    request.setMimeType(type);

                    //run
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    long refid = downloadManager.enqueue(request);
//                    MyApplication.Companion.setIdDownloading(refid);
                    MyUtils.showToast(context, R.string.download_file);
                } else {
                    //request permission
                    context.sendBroadcast(new Intent(ChatActivity.ACTION_REQUEST_STORAGE));
                }

            }
        }
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDurationString(int minute, int second) {
        String duration = "";
        String s1 = minute > 9 ? String.valueOf(minute) : ("0" + minute);
        String s2 = second > 9 ? String.valueOf(second) : ("0" + second);
        duration = s1 + ":" + s2;
        return duration;
    }

    public static String getFileSizeString(long length) {
        String fileSize = "0 MB";

        try {
            double fileSizeKb = (double) length / 1024;
            double fileSizeMb = (double) fileSizeKb / 1024;

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            // JUnitReports wants points here, regardless of the locale
            symbols.setDecimalSeparator('.');
            DecimalFormat format = new DecimalFormat("#.##", symbols);
            format.setMinimumFractionDigits(2);

            String f = format.format(fileSizeMb);
            fileSizeMb = Double.parseDouble(f);
            if (fileSizeMb > 0) {
                fileSize = fileSizeMb + " MB";
            } else {
                f = format.format(fileSizeKb);
                fileSizeKb = Double.parseDouble(f);
                fileSize = fileSizeKb + " KB";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileSize;
    }

    public static double getFileSizeMB(long length) {
        double fileSize = 0;

        try {
            double fileSizeKb = (double) length / 1024;
            double fileSizeMb = fileSizeKb / 1024;
            fileSize = fileSizeMb;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileSize;
    }

    public static String getTimePlan(long timestamp, String languageTag) {
        String time = "";
        try {
            if (TextUtils.isEmpty(languageTag)) {
                languageTag = LocaleHelper.LANGUAGE_TIENG_VIET;
            }
            //set time UI
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_PLAN, Locale.forLanguageTag(languageTag));
            time = sdf.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getTimePlan(Calendar cal, String languageTag) {
        String time = "";
        try {
            if (TextUtils.isEmpty(languageTag)) {
                languageTag = LocaleHelper.LANGUAGE_TIENG_VIET;
            }
            //set time UI
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_PLAN, Locale.forLanguageTag(languageTag));
            time = sdf.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getTimeDifferent(long timestamp, Context context) {
        //set time UI
        Calendar cal2 = Calendar.getInstance(Locale.getDefault());
        cal2.setTimeInMillis(timestamp * 1000);

        Calendar cal1 = Calendar.getInstance(Locale.getDefault());
        long diff = cal2.getTimeInMillis() - cal1.getTimeInMillis();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long days = diff / daysInMilli;
        diff = diff % daysInMilli;

        long hours = diff / hoursInMilli;
        diff = diff % hoursInMilli;

        long minutes = diff / minutesInMilli;
        diff = diff % minutesInMilli;

        long seconds = diff / secondsInMilli;

        String time = "";
        if (days <= 0 && hours <= 0 && minutes <= 0) {
            time = context.getString(R.string.finished);
        } else {
            time = "Còn ";
            if (days > 0) {
                time += days + context.getString(R.string._day_);
            }
            if (hours > 0) {
                time += hours + context.getString(R.string._hour_);
            }
            if (minutes > 0) {
                time += minutes + context.getString(R.string._minute_);
            }
        }

        return time;
    }


    //TODO CHECK PHONE NUMBER
    public static void createUserTempAndGoToChat(final Activity context, String phone, final String phoneOwner, final boolean isForward) {
        if (context != null && !TextUtils.isEmpty(phone)) {

            if (!TextUtils.isEmpty(phone)) {

                phone = MyUtils.getPhoneNumberOnly(phone);

                String countryCode = PhoneUtils.getDefaultCountryNameCode();
                boolean isValid = PhoneUtils.isValidNumber(phone, countryCode);
                if (isValid) {
                    String phoneValided = PhoneUtils.getE164FormattedMobileNumber(phone, countryCode);
                    if (phoneValided != null) {

                        Socket socket = ChatApplication.Companion.getSocket();
                        if (socket != null && socket.connected()) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("phone", phoneValided);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            socket.emit("getOrCreateAccountByPhone", obj, new Ack() {
                                @Override
                                public void call(Object... args) {
                                    final UserChatCore user = ParseJson.ParseUserLuva(args);
                                    if (user != null && !TextUtils.isEmpty(user.get_id())) {
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (user.getPhone().equalsIgnoreCase(phoneOwner)) {
                                                    MyUtils.showAlertDialog(context, R.string.phone_of_owner);
                                                } else {
                                                    //mo chat
                                                    MyUtils.chatWithUser(context, user, isForward);

                                                }


                                            }
                                        });
                                    }

                                }
                            });
                        }

                    } else {
                        MyUtils.showToast(context, R.string.number_phone_incorrect);
                    }

                } else {
                    MyUtils.showToast(context, R.string.number_phone_incorrect);
                }
            } else {
                MyUtils.showToast(context, R.string.please_input_phone);
            }
        }
    }


    /**
     * Kiem tra co phai la so di dong hay khong
     *
     * @param phone
     * @return
     */
    public static boolean isMobilePhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        String regex = "(08){1}([0-9]){8}|(09){1}([0-9]){8}|(07){1}([0-9]){8}|(03){1}([0-9]){8}|(05){1}([0-9]){8}|(09){1}([0-9]){8}|(849){1}([0-9]){8}|(847){1}([0-9]){8}|(845){1}([0-9]){8}|(843){1}([0-9]){8}|(848){1}([0-9]){8}|(\\+849){1}([0-9]){8}|(\\+848){1}([0-9]){8}|(\\+847){1}([0-9]){8}|(\\+845){1}([0-9]){8}|(\\+843){1}([0-9]){8}";
        pattern = Pattern.compile(regex, Pattern.MULTILINE);
        matcher = pattern.matcher(phone);
        return matcher.matches();
    }


    public static boolean comparePhoneNumber(String phone1, String phone2) {
        boolean equal = false;

        if (!TextUtils.isEmpty(phone1) && !TextUtils.isEmpty(phone2)) {
            try {
                //quy ve chuan quoc te
                phone1 = PhoneUtils.getE164FormattedMobileNumber(phone1, PhoneUtils.DEFAULT_ISO_COUNTRY);
                phone2 = PhoneUtils.getE164FormattedMobileNumber(phone2, PhoneUtils.DEFAULT_ISO_COUNTRY);

                if (!TextUtils.isEmpty(phone1) && !TextUtils.isEmpty(phone2)) {
                    equal = phone1.equalsIgnoreCase(phone2);
                    //                MyUtils.log("phone " + phone1 + " - " + phone2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return equal;
    }


    //MAP-----------------------------------------------------------------------------------------
    public static String getMapImageUrl(String lat, String lon, int width, int height, String map_api_key) {
        final String coordPair = lat + "," + lon;
        return "https://maps.googleapis.com/maps/api/staticmap?"
                + "&zoom=18"
                + "&size=" + width + "x" + height
                + "&maptype=roadmap&sensor=true"
                + "&center=" + coordPair
                + "&markers=color:red|" + coordPair
                + "&key=" + map_api_key;
    }


    public static void openMap(Context context, String lat, String lng, String address) {
        if (context != null) {
            String mapPackage = "com.google.android.apps.maps";
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + address);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage(mapPackage);
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                MyUtils.showToast(context, R.string.install_google_map);
                //open store
                final Uri marketUri = Uri.parse("market://details?id=" + mapPackage);
                context.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
            }
        }
    }

    public static ArrayList<String> getAllImageLink(List<RoomLog> list) {
        ArrayList<String> links = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {

                try {
                    RoomLog item = list.get(i);
                    if (item.getType().equalsIgnoreCase(ContentType.IMAGE)) {
                        JsonObject json = item.getContent();
                        if (json != null) {
                            Image image = new Gson().fromJson(json.toString(), Image.class);
                            String link = image.getLink();
                            links.add(link);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return links;
    }

    /**
     * Mo mot file bat ky, imgView, pdf, doc, excel, v.v..
     *
     * @param context
     * @param filePath
     */
    public static void openFile(Context context, String filePath) {

        try {
            if (context != null && TextUtils.isEmpty(filePath) == false) {
                //IMAGE
                if (MyUtils.isImageUrl(filePath)) {
                    if (context != null && TextUtils.isEmpty(filePath) == false) {
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(filePath);
                        Intent intent = new Intent(context, MH24_ShowImageAndVideoActivity.class);
                        intent.putExtra(MH24_ShowImageAndVideoActivity.IMAGE_URLS, list);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    //DOCUMENTS
                } else if (MyUtils.isDocumentUrl(filePath)) {
                    Intent intent = new Intent(context, MH26_ViewPdfFile.class);
                    intent.putExtra(MH26_ViewPdfFile.FILE_NAME, filePath);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {//ALL FILE
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(filePath)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //MAP-----------------------------------------------------------------------------------------

    public static void sortList(ArrayList<UserInfo> items) {
        //Sort the list if necessary
        if (null != items && items.size() > 1) {
            ArrayList<UserInfo> list = new ArrayList<>(items);
            Collections.sort(list, new Comparator<UserInfo>() {
                @Override
                public int compare(UserInfo c1, UserInfo c2) {
                    if (c1 != null && c2 != null) {
//                        return String.valueOf(c1.getName().charAt(0)).compareTo(String.valueOf(c2.getName().charAt(0)));
                        char char1 = (!TextUtils.isEmpty(c1.getName())) ? c1.getName().charAt(0) : c1.getNameMBN().charAt(0);
                        char1 = MyUtils.getUnsignedChar(char1);
                        char1 = Character.toUpperCase(char1);

                        char char2 = (!TextUtils.isEmpty(c2.getName())) ? c2.getName().charAt(0) : c2.getNameMBN().charAt(0);
                        char2 = MyUtils.getUnsignedChar(char2);
                        char2 = Character.toUpperCase(char2);

                        return String.valueOf(char1).compareToIgnoreCase(String.valueOf(char2));
                    } else {
                        return 0;
                    }
                }
            });
            //sort xong thi gan lai
            items = list;
        }
    }

    public static void sortListUserMBN(ArrayList<UserChatCore> items) {
        //Sort the list if necessary
        if (null != items && items.size() > 1) {
//            ArrayList<UserMBN> list = new ArrayList<>(items);
            //sort xong thi gan lai
//            items = list;
            Collections.sort(items, new Comparator<UserChatCore>() {
                @Override
                public int compare(UserChatCore c1, UserChatCore c2) {
                    if (c1 != null && c2 != null) {
                        MyUtils.log("compare " + c1.getName() + " - " + c2.getName());
//                        return String.valueOf(c1.getName().charAt(0)).compareTo(String.valueOf(c2.getName().charAt(0)));
                        char char1 = (!TextUtils.isEmpty(c1.getName())) ? c1.getName().charAt(0) : c1.getNameMBN().charAt(0);
                        char1 = MyUtils.getUnsignedChar(char1);
                        char1 = Character.toUpperCase(char1);

                        char char2 = (!TextUtils.isEmpty(c2.getName())) ? c2.getName().charAt(0) : c2.getNameMBN().charAt(0);
                        char2 = MyUtils.getUnsignedChar(char2);
                        char2 = Character.toUpperCase(char2);

                        return String.valueOf(char1).compareToIgnoreCase(String.valueOf(char2));
                    } else {
                        return 0;
                    }
                }
            });

        }
    }


    /*public static void syncContactWithDatabase(Realm realm, ArrayList<UserInfo> listServer) {
        if (realm != null && listServer != null && listServer.size() > 0) {
            long start = SystemClock.elapsedRealtime();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < listServer.size(); i++) {
                        UserInfo user = listServer.get(i);
                        syncOneContact(realm, user);
                    }


                    //hoan thanh step 1
                    MyUtils.howLong(start, "sync contact step 0 - " + listServer.size());

                }
            });
        }
    }*/

    public static void syncOneContact(Realm realm, UserInfo user) {
        if (realm != null && user != null) {
            //neu tim bang email ko co thi tim tiep bang so dient thoai
            UserInfo db = null;
            String email = user.getEmail();
            if (!TextUtils.isEmpty(email)) {
                db = realm.where(UserInfo.class).equalTo("email", email).findFirst();
            }

            if (db == null) {

                //check 1 sdt thuong
                if (!TextUtils.isEmpty(user.getPhone())) {
                    //So voi db, neu ton tai thi update, nguoc lai thi them moi
                    db = realm.where(UserInfo.class)
                            .equalTo("phone", user.getPhone())
                            .findFirst();
                }

                //neu van null thi check 2 sdt quoc te
                if (db == null) {
                    if (!TextUtils.isEmpty(user.getPhoneE164())) {
                        //So voi db, neu ton tai thi update, nguoc lai thi them moi
                        db = realm.where(UserInfo.class)
                                .equalTo("phone", user.getPhoneE164())
                                .findFirst();
                    }
                }
            }

            //neu co thi update
            if (db != null) {
                db.set_id(user.get_id());
                db.setNameMBN(user.getName());
                db.setEmail(user.getEmail());
                db.setAvatar(user.getAvatar());
                db.setPin(user.isPin());
                db.setHaveMBNAccount(true);
                db.setInChatContact(true);//danh ba chat
                db.setSynContactSuccess(true);
            } else {//ko co thi them moi
                user.setNameMBN(user.getName());
                user.setLocalContact(false);
                user.setHaveMBNAccount(true);
                user.setInChatContact(true);//danh ba chat
                realm.insert(user);
            }
        }
    }


    public static boolean checkUrl(String link) {
        Matcher matcher = AutoLinkMode.patternURL.matcher(link);
        while (matcher.find()) {
            return true;
        }
        return false;
    }


    public static String regexTask = "https?:\\/\\/app.myxteam.com\\/(task)\\/\\?id=([0-9]*)";
    public static Pattern patternTask = Pattern.compile(regexTask, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public static boolean checkLinkToTask(String link) {
        return patternTask.matches(regexTask, link.toLowerCase());
    }


    public static String regexProject = "https?:\\/\\/app.myxteam.com\\/(project)\\/\\?id=([0-9]*)";
    public static Pattern patternProject = Pattern.compile(regexProject, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    public static boolean checkLinkToProject(String link) {
        return patternProject.matches(regexProject, link.toLowerCase());
    }

    public static long getIdFromLinkTaskOrProject(String link) {
        long id = 0;
        String s = link.substring(link.indexOf("=") + 1);
        try {
            if (!TextUtils.isEmpty(s)) {
                id = Long.parseLong(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

   /* public static boolean isContainTaskLink(String text) {
        if (TextUtils.isEmpty(text)) return false;
        return text.toLowerCase().contains("https://app.myxteam.com/task/");
    }

    public static boolean isContainProjectLink(String text) {
        if (TextUtils.isEmpty(text)) return false;
        return text.toLowerCase().contains("https://app.myxteam.com/project/");
    }*/

    /**
     * String date="1900-01-04T00:00:00";
     *
     * @param date
     * @return
     */
    public static String convertDate(String date, String fromFormat, String toFormat) {
        if (!TextUtils.isEmpty(date)) {

            SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
            java.util.Date d = null;
            try {
                d = dateFormat.parse(date);

                Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
                SimpleDateFormat sdf = new SimpleDateFormat(toFormat);
                sdf.setCalendar(cal);
                cal.setTime(d);
                return sdf.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return "";
    }

    /**
     * Kiem tra hinh da rotation bao nhieu do
     *
     * @param path
     * @return
     */
    public static int getRotationForImage(String path) {
        int rotation = 0;

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rotation;
    }

    public static Bitmap handleCameraPhotoCamera(String path, int rotationToRoot, int targetWidth, int targetHeight, boolean isCapture) {
        Bitmap scaledBitmap = null;

        //todo
//        targetWidth = 1080;
//        targetHeight = 1920;

        scaledBitmap = MyUtils.decodeFile(path, targetWidth, targetHeight);

        if (scaledBitmap != null) {
            //neu hinh bi xoay thi xoay lai,
//            if (rotationToRoot != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationToRoot);
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//            }

            //neu hinh chup thi xoa, hinh chon tu gallery thi giu lai
            if (isCapture) {
                try {
                    File f = new File(path);
                    f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return scaledBitmap;
    }

    public static Bitmap decodeFile(String path, int targetWidth, int targetHeight) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(path), null, o);
            //Find the correct scale value. It should be the power of 2.

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = calculateInSampleSize(o, targetWidth, targetHeight);
            /*
            final int REQUIRED_SIZE=480;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }*/

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * @param mp
     * @return image path save
     *//*
    public static String saveBitmap(Bitmap mp, String picturePath) {
        Bitmap bitmap;
        OutputStream output;

        bitmap = mp;
        try {

            output = new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);//70:size 167Kb, 100:size 1.54Mb
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }*/

    /**
     * @param projectId
     * @param taskId
     * @param fileName  (file or image)
     * @return
     */
    public static String azureCreateAttachLink(long projectId, long taskId, String fileName) {
        //block name attach: ProjectId/TaskId/yyyyMMddHHmmss/filename.type
        String result = projectId + "/" + taskId + "/";
        result += azureGetCurrentDateMilisecond() + "/";
        result += fileName;

        return result;
    }

    public static String azureCreateAttachLinkAvatar(String fileName) {
        //block name attach: yyyyMMddHHmmss/filename.type
        String result = "";
        result += azureGetCurrentDateMilisecond() + "/";
        result += fileName;

        return result;
    }

    /**
     * Tao chuoi thoi gian duy nhat
     *
     * @return
     */
    public static String azureGetCurrentDateMilisecond() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_MILISECOND_AZURE);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime());//"yyyyMMddHHmmss";//
    }

    public static String getURLForResource(int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }


    public static boolean isCanOverlay(Context context) {
        boolean canDrawOverlay = true;
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                canDrawOverlay = Settings.canDrawOverlays(context);
            }
        }

        return canDrawOverlay;
    }

    public static File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = storageDir + "/" + imageFileName + ".jpg";
        File image = null;
        try {
            image = new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static void openRoomChatWidget(String id, String roomAvatar, boolean isFromRecent) {
        AddRoomEvent event = new AddRoomEvent(id, roomAvatar, true);
        EventBus.getDefault().post(event);
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        if (context != null && serviceClass != null) {
            try {
                ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (serviceClass.getName().equals(service.service.getClassName())) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
