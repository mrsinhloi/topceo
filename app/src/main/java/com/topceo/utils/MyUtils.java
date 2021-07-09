package com.topceo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.Html;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.desmond.squarecamera.myproject.APIConstants;
import com.desmond.squarecamera.myproject.ImageUtils;
import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.activity.MH02_PhotoDetailActivity;
import com.topceo.activity.MH04_FeedEditActivity;
import com.topceo.config.MyApplication;
import com.topceo.crop.ImageActivity;
import com.topceo.crop.bitmap.BitmapLoader;
import com.topceo.crop.utils.AppAnalyze;
import com.topceo.db.TinyDB;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.Fragment_2_Explorer;
import com.topceo.group.GroupDetailActivity;
import com.topceo.hashtag.HashTagActivity;
import com.topceo.login.MH15_SigninActivity;
import com.topceo.login.WelcomeActivity;
import com.topceo.mediaplayer.extractor.ExtractorException;
import com.topceo.mediaplayer.extractor.YoutubeStreamExtractor;
import com.topceo.mediaplayer.extractor.model.YTMedia;
import com.topceo.mediaplayer.extractor.model.YTSubtitles;
import com.topceo.mediaplayer.extractor.model.YoutubeMeta;
import com.topceo.mediaplayer.pip.VideoActivityPipDetail;
import com.topceo.mediaplayer.pip.VideoActivityPipList;
import com.topceo.mediaplayer.pip.presenter.VideoListItemOpsKt;
import com.topceo.objects.db.UserFollowing;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.ImageSize;
import com.topceo.objects.other.HashTag;
import com.topceo.objects.other.User;
import com.topceo.post.PostLikeFacebookActivity;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.profile.MH19_UserProfileActivity;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.socialview.core.widget.SocialTextView;
import com.topceo.socialview.core.widget.SocialView;
import com.smartapp.collage.MediaLocal;
import com.smartapp.collage.UtilsKt;
import com.smartapp.gallery.ShowGalleryActivity;
import com.workchat.core.chat.ChatActivity;
import com.workchat.core.mbn.models.UserChatCore;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;


public class MyUtils {
    public static void whenHaveUser(User user) {
        if (user != null) {
            String token = user.getCoreChatCustomToken();
            MyApplication.saveTokenChat(token);
            MyApplication.db.putBoolean(TinyDB.IS_LOGINED, true);
            MyApplication.db.putObject(User.USER, user);
        }
    }

    public static void gotoMain(Context context) {
        context.startActivity(new Intent(context, MH01_MainActivity.class));
    }

    public static void setEdittextFilter(EditText txt, String filterChars) {
        if (txt != null) {
            /*txt.setFilters(new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (source != null && !filterChars.contains(("" + source))) {
                        return "";
                    }
                    return source;
                }
            }});*/


        }
    }

    public static final int NUNMBER_THUMBNAIL = 10;

    public static ArrayList<Bitmap> extractThumbnail(Context context, String videoPath) {
        ArrayList<Bitmap> list = new ArrayList<>();

        if (!TextUtils.isEmpty(videoPath)) {
            long durationSecond = MyUtils.getDurationOfVideo(context, videoPath) / 1000;
            MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
            mRetriever.setDataSource(videoPath);


            //don vi tinh bang nano second, 2s lay 1 tam
            long second = 1000 * 1000;
            long duration = durationSecond * second;//nano second
            for (long i = second; i <= duration; i += second * 2) // for incrementing 1s use 1000
            {
                if (list.size() >= NUNMBER_THUMBNAIL) {
                    break;
                }
                list.add(mRetriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
            }
        }

        return list;
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
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void login(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, MH15_SigninActivity.class);
            context.startActivity(intent);
        }
    }

    public static void signup(Context context) {
        if (context != null) {
            //vao man hinh login goi xac thuc so dien thoai truoc roi moi vao dang ky bo sung thong tin
            Intent intent = new Intent(context, MH15_SigninActivity.class);
            intent.putExtra(MH15_SigninActivity.IS_OPEN_SIGN_UP, true);
            context.startActivity(intent);
        }
    }

    public static String replaceDescriptionForServer(String description) {
        if (description.contains("\n")) {
            description = description.replace("\n", "<br>");
        }
        return description;
    }
    /*public static String replaceDescriptionHaveBR(String description) {
        if (description.contains("<br>")) {
            description = description.replace("<br>", "\n");
        }
        return description;
    }*/


    public static boolean checkInternetConnection(Context context) {
        boolean b = false;

        if (context != null) {
            ConnectivityManager conMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr != null) {
                NetworkInfo nw = conMgr.getActiveNetworkInfo();
                if (nw != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo()
                        .isConnectedOrConnecting()) {

                    b = true;

                }
            }
        }

        return b;


    }

    /**
     * http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out/27312494#27312494
     * Real internet - faster
     *
     * @return
     */
    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * CPUID tuong duong voi hostname, dung de phan biet 2 may khac nhau
     *
     * @return
     */
    public static String getCPUID(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    private static InetAddress getInetAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static void showThongBao(Context context) {
        if (context != null) {
            new MaterialDialog.Builder(context)
//                    .title(R.string.notify)
                    .content(R.string.khongCoInternet)
                    .positiveText(R.string.ok)
                    .autoDismiss(true)
                    .cancelable(true)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);

                            dialog.dismiss();

                        }
                    })
                    .show();
        }
    }

    public static final void showToast(Context context, String message) {
        if (context != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static final void showToast(Context context, int message) {
        if (context != null) {
            Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show();
        }
    }

    public static final void showToastDebug(Context context, String message) {
        if (BuildConfig.DEBUG) {
            if (context != null && !TextUtils.isEmpty(message)) {
//                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static final void showToast(Activity context, int message) {
        if (context != null) {
            Toast.makeText(context, context.getResources().getString(message), Toast.LENGTH_SHORT).show();
        }
    }

    public static final void showToast(Activity context, String message) {
        if (context != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /////////////////////////////////////////////////////////
    public static void showAlertDialog(Context context, String title, String message) {

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setTextColor(Color.BLACK);
    }

    public static void showAlertDialog(Context context, String message) {

        if (context != null && !TextUtils.isEmpty(message)) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(message);
//        alertDialogBuilder.setIcon(R.drawable.fail);
            alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setTextColor(Color.BLACK);
        }
    }

    public static void showAlertDialogHtml(Context context, String messageHtml) {

        if (context != null && !TextUtils.isEmpty(messageHtml)) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setMessage(Html.fromHtml(messageHtml));
//        alertDialogBuilder.setIcon(R.drawable.fail);
            alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setTextColor(Color.BLACK);
        }
    }


    public static void showAlertDialog(Context context, int message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
//        alertDialogBuilder.setIcon(R.drawable.fail);
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setTextColor(Color.BLACK);
    }


    public static void showAlertDialog(Activity context, int message, boolean isFinish) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        if (isFinish) {
            alertDialogBuilder.setCancelable(false);
        }
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                context.finish();
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                if (isFinish && context != null) {
                    context.finish();
                }
            }
        });


        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setTextColor(Color.BLACK);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static byte[] toUTF8ByteArray(String s) {
        /*int len = s.length();
        byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }

	    return data;*/
        return toUTF8ByteArray(s.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] string) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        char[] c = string;
        int i = 0;

        while (i < c.length) {
            char ch = c[i];

            if (ch < 0x0080) {
                bOut.write(ch);
            } else if (ch < 0x0800) {
                bOut.write(0xc0 | (ch >> 6));
                bOut.write(0x80 | (ch & 0x3f));
            }
            // surrogate pair
            else if (ch >= 0xD800 && ch <= 0xDFFF) {
                // in error - can only happen, if the Java String class has a
                // bug.
                if (i + 1 >= c.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                char W1 = ch;
                ch = c[++i];
                char W2 = ch;
                // in error - can only happen, if the Java String class has a
                // bug.
                if (W1 > 0xDBFF) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                int codePoint = (((W1 & 0x03FF) << 10) | (W2 & 0x03FF)) + 0x10000;
                bOut.write(0xf0 | (codePoint >> 18));
                bOut.write(0x80 | ((codePoint >> 12) & 0x3F));
                bOut.write(0x80 | ((codePoint >> 6) & 0x3F));
                bOut.write(0x80 | (codePoint & 0x3F));
            } else {
                bOut.write(0xe0 | (ch >> 12));
                bOut.write(0x80 | ((ch >> 6) & 0x3F));
                bOut.write(0x80 | (ch & 0x3F));
            }

            i++;
        }

        return bOut.toByteArray();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final Pattern mPattern = Pattern.compile("([1-9]{1}[0-9]{0,2}([0-9]{3})*(\\.[0-9]{0,2})?|[1-9]{1}[0-9]{0,}(\\.[0-9]{0,2})?|0(\\.[0-9]{0,2})?|(\\.[0-9]{1,2})?)");

    public static final boolean isMoneyFormat(String value) {
        Matcher matcher = mPattern.matcher(value);
        return matcher.matches();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void updateStatusBarColor(Window window) {
        if (window != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(window.getContext().getResources().getColor(R.color.colorPrimaryDarkChat));
            }
        }
    }

    public static void openGallery(Context context, int position, ArrayList<MediaLocal> list) {
        ArrayList<String> paths = MediaLocal.getListPath(list);
        if (paths.size() > 0) {
            Intent intent = new Intent(context, ShowGalleryActivity.class);
            intent.putExtra(ShowGalleryActivity.LIST_PATH, paths);
            intent.putExtra(ShowGalleryActivity.POSITION_SELECTED, position);
            context.startActivity(intent);
        }
    }


    public static void transparentStatusBar(Window window) {
        if (window != null) {
            // In Activity's onCreate() for instance
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    public static void showDialogReportPost(Context context, final long imageItemId) {
        final int[] reports = context.getResources().getIntArray(R.array.arr_reports_int_image);
        new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialogTheme))
                .setTitle(R.string.report_reason)
                .setItems(R.array.arr_reports_string_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //nguoi dung dang chon option nao
                        int reason = reports[which];
                        //goi thong tin len server
                        MyUtils.reportPost(reason, imageItemId, context);
                    }
                })
                .show();
        /*new MaterialAlertDialogBuilder(new ContextThemeWrapper(context, R.style.CustomDialogTheme))
                .setTitle(R.string.report_reason)
                .setItems(R.array.arr_reports_string_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //nguoi dung dang chon option nao
                        int reason = reports[which];
                        //goi thong tin len server
                        MyUtils.reportPost(reason, imageItemId, context);
                    }
                })
                .show();*/
    }

    public static void showDialogReportUser(Context context, final long userId) {
        final int[] reports = context.getResources().getIntArray(R.array.arr_reports_int_user);
        new MaterialDialog.Builder(context)
                .title(R.string.report_reason)
                .items(R.array.arr_reports_string_user)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        //nguoi dung dang chon option nao
                        int reason = reports[which];
                        //goi thong tin len server
                        MyUtils.reportUser(reason, userId, context);

                    }
                })
                .show();
    }

    public static void setTextFileInfo(Context context, TextView txt, String videoPath) {
        if (context != null && txt != null) {
            File f = new File(videoPath);
            long bytes = f.length();
            long durationSecond = MyUtils.getDurationOfVideo(context, videoPath) / 1000;
            String durationString = MyUtils.formatDuration(durationSecond);
            String sizeString = Formatter.formatFileSize(context, bytes);
            String info = "Size: " + sizeString + " - Duration: " + durationString + "s ";
            txt.setText(info);
        }
    }

    public static String formatDuration(long second) {
        String duration = "00:00";
        if (second > 0) {
            long minute = second / 60;
            long hour = minute / 60;
            if (hour > 0) {
                minute = minute % 60;
                second = second % 60;
                duration = String.format("%d:%02d:%02d", hour, minute, second);
            } else {
                if (minute > 0) {
                    second = second % 60;
                }
                duration = String.format("%02d:%02d", minute, second);
            }

        }
        return duration;
    }

    public static long getDurationOfVideo(Context context, String path) {
        if (context != null && !TextUtils.isEmpty(path)) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //use one of overloaded setDataSource() functions to set your data source
            retriever.setDataSource(context, Uri.fromFile(new File(path)));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);

            retriever.release();

            return timeInMillisec;
        }
        return 0;
    }

    public static void afterDeletePost(Context context, long imageItemId) {
        ///todo //refresh grid image cua man hinh home of user va grid explorer
        //home
        Intent intent = new Intent(Fragment_1_Home_User.ACTION_AFTER_DELETE_POST);
        intent.putExtra(ImageItem.IMAGE_ITEM_ID, imageItemId);
        context.sendBroadcast(intent);


        //grid cua user
        intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_AFTER_DELETE_POST);
        intent.putExtra(ImageItem.IMAGE_ITEM_ID, imageItemId);
        context.sendBroadcast(intent);

        //grid cua hashtag
        intent = new Intent(HashTagActivity.ACTION_AFTER_DELETE_POST);
        intent.putExtra(ImageItem.IMAGE_ITEM_ID, imageItemId);
        context.sendBroadcast(intent);

        //grid explorer
        intent = new Intent(Fragment_2_Explorer.ACTION_AFTER_DELETE_POST);
        intent.putExtra(ImageItem.IMAGE_ITEM_ID, imageItemId);
        context.sendBroadcast(intent);

        //dong man hinh detail neu dang mo post nay
        intent = new Intent(MH02_PhotoDetailActivity.ACTION_POST_DELETED);
        intent.putExtra(ImageItem.IMAGE_ITEM_ID, imageItemId);
        context.sendBroadcast(intent);

        //xoa trong group detail co danh sach post
        intent = new Intent(GroupDetailActivity.ACTION_DELETE_POST_IN_GROUP);
        intent.putExtra(ImageItem.IMAGE_ITEM_ID, imageItemId);
        context.sendBroadcast(intent);
    }


    public static void whenClickComment(Context context, SocialTextView txt2) {

        if (context != null) {
            txt2.setOnHashtagClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                MyUtils.showToastDebug(context, text.toString());
                    MyUtils.gotoHashtag(text.toString(), context);
                }
            });
            txt2.setOnMentionClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                MyUtils.showToastDebug(context, text.toString());
                    MyUtils.gotoProfile(text.toString(), context);
                }
            });
            txt2.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                    MyUtils.openWebPage(text.toString(), context);
                }
            });
        }
    }


    public static String createNickname(String fullname) {
        String nickname = "";
        if (!TextUtils.isEmpty(fullname)) {
            nickname = getUnsignedString(fullname.toLowerCase(Locale.ROOT));
            if (!TextUtils.isEmpty(nickname) && nickname.contains(" ")) {
                nickname = nickname.replaceAll("\\s+", "");
            }
        }

        return nickname;
    }

    public static String getYoutubeId(String url) {
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }/*from w  w  w.  j a  va  2 s .c om*/
        return null;
    }

    public static void playYoutubeShopping(Activity context, String url) {
        if (context != null && !TextUtils.isEmpty(url)) {
//            long start = SystemClock.elapsedRealtime();
            new YoutubeStreamExtractor(new YoutubeStreamExtractor.ExtractorListner() {
                @Override
                public void onExtractionDone(List<YTMedia> adativeStream, final List<YTMedia> muxedStream, List<YTSubtitles> subtitles, YoutubeMeta meta) {
//                    MyUtils.howLong(start, "parse success");
                    //url to get subtitle
                    if (muxedStream != null && muxedStream.size() > 0) {
                        String subUrl = muxedStream.get(muxedStream.size() - 1).getUrl();
                        VideoListItemOpsKt.playVideo(context, subUrl);
                    }
                }

                @Override
                public void onExtractionGoesWrong(final ExtractorException e) {
                    if (e != null) {
                        MyUtils.showToast(context, e.getMessage());
                    }
                }
            }).useDefaultLogin().Extract(url);
        }
    }

    public static void closePip(Context context) {
        Intent mh1 = new Intent(VideoActivityPipDetail.ACTION_FINISH);
        context.sendBroadcast(mh1);
        Intent mh2 = new Intent(VideoActivityPipList.ACTION_FINISH);
        context.sendBroadcast(mh2);
    }

    /////DATE/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String getNameOfDay() {
        Calendar c = Calendar.getInstance();
        java.util.Date date = c.getTime();
        CharSequence time = android.text.format.DateFormat.format("EEEE", date.getTime()); // gives like (Wednesday)
        return time.toString();
    }

    /*
     * Lay ngay hien tai
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    public static String getCurrentDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    public static String getDateStringNotify(String date) {
        if (!TextUtils.isEmpty(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_SERVER);
            java.util.Date d = new java.util.Date();
            try {
                d = sdf.parse(date);
                SimpleDateFormat sdf2 = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM_AA);
                return sdf2.format(d).toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM_AA);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    public static java.util.Date getDateDateNotify(String date) {
        if (!TextUtils.isEmpty(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_SERVER);
            java.util.Date d = new java.util.Date();
            try {
                d = sdf.parse(date);
                return d;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Calendar.getInstance().getTime();
    }

    public static String getCurrentDateLong() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_LONG);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime()).toUpperCase();//"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";//
    }

    public static long getCurrentLongDate() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }


    /**
     * Yesterday
     *
     * @return
     */
    public static String getPreviousDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return sdf.format(cal.getTime()).toUpperCase();
    }

    /**
     * @param days ex: -5
     * @return
     */
    public static String getPreviousDate(int days) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return sdf.format(cal.getTime()).toUpperCase();
    }


    /**
     * Tomorrow
     *
     * @return
     */
    public static String getNextDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return sdf.format(cal.getTime()).toUpperCase();
    }

    public static String getNextNextDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 2);
        return sdf.format(cal.getTime()).toUpperCase();
    }

    public static String convertDate(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        return sdf.format(cal.getTime()).toUpperCase();
    }

    public static String convertDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        return sdf.format(date).toUpperCase();
    }

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
     * @param date
     * @return 20160504 - yyyyMMdd
     */
    public static int convertDateReminder(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_REMINDER_FORMAT);
        return Integer.parseInt(sdf.format(date));
    }

    /*
     * Lay ngay hien tai
     */
    public static String getCurrentDateDetail() {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        Calendar cal = Calendar.getInstance();
        return sdf.format(cal.getTime()).toUpperCase();
    }

    /**
     * String date="\\/Date(1401969054401)\\/";
     *
     * @param date
     * @return
     */
    public static String getDateFromTickNumber(String date) {
        if (TextUtils.isEmpty(date) == false) {
            date = date.substring(date.indexOf("(") + 1, date.indexOf(")"));
            Date d = new Date(Long.parseLong(date));

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
            sdf.setCalendar(cal);
            cal.setTime(d);
            return sdf.format(d);
        }
        return "";
    }

    public static String getDateDetailFromTickNumber(String date) {
        if (TextUtils.isEmpty(date) == false) {
            date = date.substring(date.indexOf("(") + 1, date.indexOf(")"));
            Date d = new Date(Long.parseLong(date));

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM);
            sdf.setCalendar(cal);
            cal.setTime(d);
            return sdf.format(d);
        }
        return "";
    }
    /*public static String getDateFromTickNumber(String date){
        String dateString="";
        long d = getLongDate(date);
        if (d != 0) {
            dateString = new SimpleDateFormat(DateFormat.DATE_FORMAT).format(new Date(d));
        }
        return dateString;
    }*/

    public static Date getDateFromTickNumber2(String date) {
        Date d = null;
        if (date != null && !date.equals("")) {
            date = date.substring(date.indexOf("(") + 1, date.indexOf(")"));
            d = new Date(Long.parseLong(date));
        }
        return d;
    }

    /**
     * String date="\\/Date(1401969054401)\\/";
     *
     * @param date
     * @return
     */
    public static String getDateFromTickNumberDetail(String date) {
        if (date != null && !date.equals("")) {
            date = date.substring(date.indexOf("(") + 1, date.indexOf(")"));
            Date d = new Date(Long.parseLong(date));

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM);
            sdf.setCalendar(cal);
            cal.setTime(d);
            return sdf.format(d);
        }
        return "";
    }

    public static String createDateTickNumberCurrent() {
        long date = System.currentTimeMillis();
        return "\\/Date(" + date + ")\\/";
    }

    ///WEEK AND MONTH////////////////////////////////////////////////////////////////////

    public static String firstDayOfWeek() {

        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        return sdf.format(cal.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    public static long firstDayOfWeekLong() {

        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        return cal.getTimeInMillis();//"dd/MM/yyyy";//
    }


    public static String endDayOfWeek() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        // Add 6 days to reach the last day of the current week
        cal.add(Calendar.DAY_OF_YEAR, 6);

        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        return sdf.format(cal.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    public static long endDayOfWeekLong() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        // Add 6 days to reach the last day of the current week
        cal.add(Calendar.DAY_OF_YEAR, 6);

        return cal.getTimeInMillis();//"dd/MM/yyyy";//
    }

    public static String firstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.set(Calendar.DATE, 1);

        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        return sdf.format(calendar.getTime()).toUpperCase();//"dd/MM/yyyy";//
    }

    public static long firstDayOfMonthLong() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.set(Calendar.DATE, 1);

        return calendar.getTimeInMillis();
    }

    public static String endDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

        // int days=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
        return sdf.format(calendar.getTime()).toUpperCase();//"12DEC2013";//
    }

    public static long endDayOfMonthLong() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

        // int days=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        return calendar.getTimeInMillis();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static String getIMEI(Context context) {
        String kq = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        /*if (context != null) {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null)
                kq = mTelephony.getDeviceId(); //*** use for mobiles
            else
                kq = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); //*** use for tablets
        }*/
        return kq.toUpperCase();
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

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public static int getAppVersion(Context context) {
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

    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getText(String taskName) {
        if (taskName == null) taskName = "";
        return taskName;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param context
     * @param timeDelay milisecond
     */
    public static void setTimeoutScreen(Context context, int timeDelay) {
        //Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, timeDelay);
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*@Deprecated
    public static String getPath(Uri uri, Activity context) {
        String path="";
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }*/

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

                // TODO handle non-primary volumes
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
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
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
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static String getMimeType(String url) {
        String parts[] = url.split("\\.");
        String extension = parts[parts.length - 1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long getLongDate(String date) {
        long result = 0;
        if (date != null && !date.equals("")) {
            date = date.substring(date.indexOf("(") + 1, date.indexOf(")"));
            try {
                result = Long.parseLong(date);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Qua han
     *
     * @param due_date
     * @return
     */
    public static boolean isOver_DueDate(String due_date) {
        boolean result = false;
        long d = getLongDate(due_date);
        if (d != 0) {
            if (System.currentTimeMillis() > d) {
                result = true;
            }
        }

        return result;
    }
    /////END//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    static SimpleDateFormat dateFormatBegin = new SimpleDateFormat(DateFormat.DATE_FORMAT_LONG);

    /**
     * Hom nay: "HH:mm"
     * Cac ngay khac: "dd/MM/yy"
     *
     * @param dateString
     * @return
     */
    public static String getDateChat(String dateString) {
        String time = "";
        try {
            java.util.Date date = (java.util.Date) dateFormatBegin.parse(dateString);
            SimpleDateFormat newFormat;
            //neu la ngay hom nay
            String day = getDateChatFull(dateString);
            if (getCurrentDate().equals(day)) {
                newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM_AA);//de chieu dai bang voi ngay, nhin de hon
            } else {//neu la cac ngay khac
                newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_SHORT);
            }
            time = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
        //		return dateString;
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
            java.util.Date date = (java.util.Date) dateFormatBegin.parse(dateString);
            SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_HH_MM_AA);//de chieu dai bang voi ngay, nhin de hon
            time = newFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
        //		return dateString;
    }

    public static String getDateChatFull(String dateString) {
        String time = "";
        if (TextUtils.isEmpty(dateString) == false) {
            try {

                java.util.Date date = (java.util.Date) dateFormatBegin.parse(dateString);
                SimpleDateFormat newFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_VN_DDMMYYYY);
                time = newFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
        //		return dateString;
    }


    ////////////////////////////////////////////////////////////////////////////////////
    public static String getMessage(String userName, String message, String createDate) {
        String result = message;
        if (message != null) {
            if (message.equals("CREATEGROUPROOM")) {
                result = userName + " đã tạo nhóm - " + getDateChatFull(createDate);
            } else if (message.contains("ADDROOMMEMBER")) {
                String[] arr = message.split("&");
                result = userName + " đã thêm " + arr[1] + " vào nhóm - " + getDateChatFull(createDate);
            } else if (message.contains("REMOVEROOMMEMBER")) {
                String[] arr = message.split("&");
                result = userName + " đã mời " + arr[1] + " khỏi nhóm - " + getDateChatFull(createDate);
            } else if (message.contains("USERLEAVEROOM")) {
                String[] arr = message.split("&");
                result = userName + " đã rời khỏi nhóm - " + getDateChatFull(createDate);
            } else if (message.contains("NOTROOMMEMBER")) {
                String[] arr = message.split("&");
                result = userName + " không còn là thành viên của nhóm - " + getDateChatFull(createDate);
            }

        } else {
            result = "";
        }
        return result;
    }


    private static Pattern pattern;
    private static Matcher matcher;

    public static boolean isImageUrl(String image) {
        if (TextUtils.isEmpty(image)) return false;

        //remove bank place
        image = image.replaceAll(" ", "");
        String regex = "([^\\s]+(\\.(?i)(/bmp|jpg|gif|png|jpeg|webp))$)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(image);
        return matcher.matches();
    }

    public static boolean isJpg_Png(String path) {
        if (TextUtils.isEmpty(path)) return false;
        if (
                path.endsWith(".jpg") ||
                        path.endsWith(".pgeg") ||
                        path.endsWith(".png")
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMp4(String path) {
        if (TextUtils.isEmpty(path)) return false;
        if (
                path.endsWith(".mp4")
        ) {
            return true;
        } else {
            return false;
        }
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
//		String fileNameWithExtension = URLUtil.guessFileName(urlString, null, null);
        urlString = urlString.replace("\\", "/");
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }


    public static String getMention(String userName, long userId) {
        return "@[" + userName + "](userid:" + userId + ")";
    }

    public static ArrayList<String> findUserMention(String text) {
        ArrayList<String> list = new ArrayList<String>();
        String patternString1 = "\\[([\\s\\S\\d _][^\\]]+)\\]";

        Pattern pattern = Pattern.compile(patternString1);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
//			System.out.println("found: " + matcher.group(1));
            list.add(matcher.group(1));
        }

        return list;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static long howLongTask(long startTime) {
        long endTime = System.currentTimeMillis();
        long secondMilis = endTime - startTime;
        long second = TimeUnit.MILLISECONDS.toSeconds(secondMilis);
        Log.e("test", "howLongTask: " + secondMilis);
        return secondMilis;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////

    //AZURE BLOCK STORAGE///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param roomId
     * @param userId
     * @param fileName (file or image)
     * @return
     */
    public static String azureCreateChatLink(long roomId, long userId, String fileName) {
        //block name chat:   RoomId/UserId/yyyyMMddHHmmss/filename.type
        String result = roomId + "/" + userId + "/";
        result += azureGetCurrentDateMilisecond() + "/";
        result += fileName;


        return result;
    }

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

    public static byte[] getRandomBuffer(int size) {
        byte[] buffer = new byte[size];
        Random random = new Random();
        random.nextBytes(buffer);
        return buffer;
    }


    public static String generateRandomBlobNameWithPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        String blobName = prefix + UUID.randomUUID().toString();
        return blobName.replace("-", "");
    }

    public static void log(String message) {
        Log.i("TEST", "my log: " + message);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////
    //Read more: http://www.androidhub4you.com/2012/12/listview-into-scrollview-in-android.html#ixzz3kZiWOs8n
    public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void gotoMap(String lat, String lon, Context context) {
        if (context != null) {
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?z=17");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getAddress(Context context, String lat, String lon) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder(context.getText(R.string.address) + ": ");
                /*for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (i == returnedAddress.getMaxAddressLineIndex() - 1) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    } else {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                    }
                }*/
                strReturnedAddress.append(returnedAddress.getAddressLine(0));
                ret = strReturnedAddress.toString();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "Can't get Address!";
        }
        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Boolean isSoftKeyBoardVisible(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
//            Log.d(TAG,"Software Keyboard was shown");
            return true;
        } else {
//            Log.d(TAG,"Software Keyboard was not shown");
            return false;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public static int getExifOrientation(String src) {
        int orientation = 1;

        try {
            /**
             * if your are targeting only api level >= 5
             * ExifInterface exif = new ExifInterface(src);
             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class.forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass.getConstructor(new Class[]{String.class});
                Object exifInstance = exifConstructor.newInstance(new Object[]{src});
                Method getAttributeInt = exifClass.getMethod("getAttributeInt", new Class[]{String.class, int.class});
                Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[]{tagOrientation, 1});
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orientation;
    }


    public static Bitmap resizeAndRotateImage(String path, int targetWidth, int targetHeight) {
        int rotation = getRotationForImage(path);
        return resizeImage(path, rotation, targetWidth, targetHeight);
    }

    public static Bitmap resizeImage(String path, int rotationToRoot, int targetWidth, int targetHeight) {
        Bitmap scaledBitmap = null;
        scaledBitmap = MyUtils.decodeFile(path, targetWidth, targetHeight);

        //resize bitmap follow width, auto height
        float aspectRatio = (float) scaledBitmap.getWidth() / (float) scaledBitmap.getHeight();
        int width = targetWidth;    //your width
        if (width > scaledBitmap.getWidth()) {
            width = scaledBitmap.getWidth();
        }
        int height = Math.round(width / aspectRatio);

        //neu hinh bi xoay thi xoay lai
        if (rotationToRoot != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationToRoot);
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, width, height, matrix, true);
        } else {
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, width, height);
        }

        return scaledBitmap;
    }

    public static Bitmap decodeFile(String path, int targetWidth, int targetHeight) {
        try {
            //decode image size
            int scale = calculateInSampleSize(path, targetWidth, targetHeight);

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static Point decodeFile(String path) {
        Point point = new Point(0, 0);
        if (!TextUtils.isEmpty(path)) {
            try {
                //decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(path), null, o);
                //Find the correct scale value. It should be the power of 2.

                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                point = new Point(width_tmp, height_tmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return point;
    }


    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(String path,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        Size size = UtilsKt.getImageSize(path);
        final int width = size.getWidth();
        final int height = size.getHeight();
        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight) {

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


    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        } catch (Exception e) {
            return "jpg";
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getPhoneNumber(Context context) {
        String phone = "";
        /*try {
            TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            phone= tMgr.getLine1Number();
            if(phone==null)phone="";
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return phone;
    }


    public static void copy(Context context, String text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static void setFullscreen(AppCompatActivity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getSupportActionBar().hide();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String KEY_M_ITEM = "KEY_M_ITEM";

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean writeObjectToFile(Object obj, String key, Context context) {
        boolean isSuccess = false;
        if (context != null) {
            File myfile = context.getFileStreamPath(key);
            try {
                if (myfile.exists() || myfile.createNewFile()) {
                    FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(obj);
                    fos.close();

                    isSuccess = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    public static Object getUserModelFromFile(String key, Context context) {
        Object info = null;
        if (context != null) {
            File myfile = context.getFileStreamPath(key);
            try {
                if (myfile.exists()) {
                    FileInputStream fis = context.openFileInput(key);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    info = ois.readObject();
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return info;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //2016-07-01T12:49:00
    //date format
    public static String formatDate(String date, String format, Context context) {
        SimpleDateFormat oldFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_SERVER, Locale.ENGLISH);
        oldFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat newFormat = new SimpleDateFormat(format);
        newFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

        String currentDateTimeString = date;
        try {
            if (TextUtils.isEmpty(date) == false) {
                currentDateTimeString = newFormat.format(oldFormat.parse(date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(newFormat.parse(currentDateTimeString));
            currentDateTimeString = DateFormat.getTimeAgo(cal.getTimeInMillis(), context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentDateTimeString;
    }

    public static String formatDate(long date, String format, Context context) {
        /*SimpleDateFormat oldFormat=new SimpleDateFormat(DateFormat.DATE_FORMAT_SERVER, Locale.ENGLISH);
        oldFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat newFormat=new SimpleDateFormat(format);
        newFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));*/

        String currentDateTimeString = "";
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTimeInMillis(date * 1000);
            currentDateTimeString = DateFormat.getTimeAgo(cal.getTimeInMillis(), context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentDateTimeString;
    }

    public static final String MONEY_FORMAT = "###,###,###.###";

    public static String getMoneyFormat(double money) {
        return new DecimalFormat(MONEY_FORMAT)
                .format(money);
    }

    public static void howLong(long start, String message) {
        long time = SystemClock.elapsedRealtime() - start;
        Log.d("test", "how long = " + time + " - " + message);
    }

    private void getScreenSize(Activity context) {
        // Method 1 Obtain by WindowManager
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width1 = outMetrics.widthPixels;
        int height1 = outMetrics.heightPixels;
//        Log.i(TAG, "Method 1: height::" + height + "  width::" + width);

        //Method 2 // get through Resources
        DisplayMetrics dm1 = context.getResources().getDisplayMetrics();
        int height2 = dm1.heightPixels;
        int width2 = dm1.widthPixels;
//        Log.i(TAG, "Method 2: height::" + height1 + "  width::" + width1);

        //Method 3 // get the default screen resolution
        Display display = context.getWindowManager().getDefaultDisplay();
        int width3 = display.getWidth();
        int height3 = display.getHeight();
//        Log.i(TAG, "Method 3: height::" + height2 + "  width::" + width2);//Method 3: height::1080  width::1920
    }

    public static int getScreenWidth(Context context) {
        int widthScreen = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            widthScreen = size.x;
        } else {
            widthScreen = display.getWidth();  // Deprecated
        }
        return widthScreen;
    }

    public static int getScreenHeight(Context context) {
        int heightScreen = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            heightScreen = size.y;
        } else {
            heightScreen = display.getHeight();
        }
        return heightScreen;
    }

    /**
     * Load bitmap, resize size chuan, luu xuong, vao man hinh edit
     *
     * @param context
     * @param filePath
     */
    public static void editImage(Activity context, String filePath) {
        if (!TextUtils.isEmpty(filePath)) {

            Bitmap bmtmp = BitmapLoader.load(context, new int[]{ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT}, filePath);
            saveBitmapTemp(bmtmp);


            Intent intent = new Intent(context, ImageActivity.class);
            context.startActivity(intent);

            /*Intent intent = new Intent(context, CropImageActivity.class);
            context.startActivityForResult(intent, MainActivity.CROP_FILE);*/
        }
    }

    public static void saveBitmapTemp(Bitmap bmtmp) {
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

            //set duong dan hinh
            AppAnalyze appAnalyze = AppAnalyze.getInstance();
            appAnalyze.setImageUri(APIConstants.SELECTED_IMAGE);

        } catch (Exception e) {
        }
        try {
            bmtmp.recycle();
            bmtmp = null;
        } catch (Exception e) {
        }
    }


    public static void analyMemory(String path) {
        File f = new File(path);
        long size = f.length();
        long kb = size / 1024;
        long mb = kb / 1024;
        MyUtils.log("KB=" + kb + ",MB=" + mb);
    }

    ///////////////////////////////////////////////////////////////

    /**
     * Lay danh sach bat dau bang #
     *
     * @param text
     * @return
     */
    public static List<String> getListTag(String text) {
        List<String> tags = new ArrayList<>();
        if (!TextUtils.isEmpty(text)) {
            String[] words = text.split(" ");
            for (String word : words) {
                if (word.substring(0, 1).equals("#")) {
                    tags.add(word.substring(1));//ko lay ky tu dau
                }
            }
        }
        return tags;
    }

    /**
     * * Lay danh sach bat dau bang @
     *
     * @param text
     * @return
     */
    public static List<String> getListMention(String text) {
        List<String> tags = new ArrayList<>();
        if (!TextUtils.isEmpty(text)) {
            String[] words = text.split(" ");
            for (String word : words) {
                if (word.substring(0, 1).equals("@")) {
                    tags.add(word.substring(1));//ko lay ky tu dau
                }
            }
        }
        return tags;
    }

    /////////////////////////////////
    public static String getStringEditting(String text, int cursorPosition) {
        String gold = "";
        if (!TextUtils.isEmpty(text)) {
            //dua vao cursorPosition de biet dang edit o word nao
            //extrac word xem la # or @
            String s1 = text.substring(0, cursorPosition);//12 @34
            String s2 = text.substring(cursorPosition);//5 67890
            //can tim @345
            String gold1 = "";
            if (!TextUtils.isEmpty(s1)) {
                int pos = s1.lastIndexOf(" ");
                if (pos >= 0) {
                    gold1 = s1.substring(pos + 1);//@34
                } else {
                    gold1 = s1.substring(0);
                }

            }
            String gold2 = "";
            if (!TextUtils.isEmpty(s2)) {
                int pos = s2.indexOf(" ");
                if (pos >= 0) {
                    gold2 = s2.substring(0, pos);//5
                } else {
                    gold2 = s2;//phan tu cuoi cung
                }

            }

            gold = gold1 + gold2;//@345
        }

        return gold;

    }

    public static void showKeyboard(Activity context) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }


    public static void showKeyboard(Activity context, EditText txt) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txt, InputMethodManager.SHOW_IMPLICIT);
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

    public static void hideKeyboard(Activity context, EditText txt) {
        if (context != null) {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (context.getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Facebook - "com.facebook.katana"
     * Twitter - "com.twitter.android"
     * Instagram - "com.instagram.android"
     * Pinterest - "com.pinterest"
     **/
    public void SharingToSocialMedia(String packageName, Context context) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);

        boolean installed = appInstalledOrNot(packageName, context);
        if (installed) {
            intent.setPackage(packageName);
            context.startActivity(intent);
        } else {
            Toast.makeText(context,
                    "Installed packageName first", Toast.LENGTH_LONG).show();
        }

    }

    private boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////////



    /*public static ArrayList<UserFollowing> getListUserFollowingFromFile() {
        ArrayList<UserFollowing> list = new ArrayList<UserFollowing>();

        Realm realm = Realm.getDefaultInstance();
        if (realm != null) {
            try {
                //get all user
                RealmQuery<UserFollowing> query = realm.where(UserFollowing.class);
                //execute query
                RealmResults<UserFollowing> result = query.findAll();
                for (int i = 0; i < result.size(); i++) {
                    list.add(result.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            realm.close();
        }

        return list;
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void gotoProfile(final String username, final Context context) {
        if (MyUtils.checkInternetConnection(context)) {
            if (!TextUtils.isEmpty(username)) {
                /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(context.getText(R.string.loading));
                progressDialog.show();*/
                ProgressUtils.show(context);

                //LAY user tu username
                Webservices.getUser(username).continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        ProgressUtils.hide();
                        if (task.getError() == null) {
                            if (task.getResult() != null) {
                                User user = (User) task.getResult();
                                Intent intent = new Intent(context, MH19_UserProfileActivity.class);
                                intent.putExtra(User.USER, user.getUserMedium());
                                context.startActivity(intent);
                            } else {
                                MyUtils.showToast(context, R.string.not_found);
                            }
                        } else {
//                        MyUtils.showToast(context, task.getError().getMessage());
//                        MyUtils.showToast(context, R.string.not_found);

                            ANError error = (ANError) task.getError();
                            boolean isLostCookie = MyApplication.controlException(error);
                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                gotoProfile(username, context);
                                            }
                                        }
                                        return null;
                                    }
                                });
                            } else {
                                if (!TextUtils.isEmpty(error.getMessage())) {
                                    MyUtils.showToast(context, error.getMessage());
                                }
                            }
                        }

                        return null;
                    }
                });

            }
        } else {
            MyUtils.showThongBao(context);
        }
    }

    public static void gotoProfile(final long userId, final Context context) {
        if (MyUtils.checkInternetConnection(context)) {
            if (userId > 0) {
                /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(context.getText(R.string.loading));
                progressDialog.show();*/
                ProgressUtils.show(context);

                //LAY user tu username
                Webservices.getUser(userId).continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        ProgressUtils.hide();
                        if (task.getError() == null) {
                            if (task.getResult() != null) {
                                User user = (User) task.getResult();
                                Intent intent = new Intent(context, MH19_UserProfileActivity.class);
                                intent.putExtra(User.USER, user.getUserMedium());
                                context.startActivity(intent);
                            } else {
                                MyUtils.showToast(context, R.string.not_found);
                            }
                        } else {
//                        MyUtils.showToast(context, task.getError().getMessage());
//                        MyUtils.showToast(context, R.string.not_found);

                            ANError error = (ANError) task.getError();
                            boolean isLostCookie = MyApplication.controlException(error);
                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                gotoProfile(userId, context);
                                            }
                                        }
                                        return null;
                                    }
                                });
                            } else {
                                if (!TextUtils.isEmpty(error.getMessage())) {
                                    MyUtils.showToast(context, error.getMessage());
                                }
                            }
                        }

                        return null;
                    }
                });

            }
        } else {
            MyUtils.showThongBao(context);
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    public static void gotoHashtag(String hashtag, final Context context) {
        if (!TextUtils.isEmpty(hashtag)) {
            Intent intent = new Intent(context, HashTagActivity.class);
            intent.putExtra(HashTag.HASH_TAG, hashtag);
            context.startActivity(intent);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public static void reportPost(int reportTypeId, long imageItemId, final Context context) {
        AndroidNetworking.post(Webservices.API_URL + "image/report")
                .addQueryParameter("ReportTypeId", String.valueOf(reportTypeId))
                .addQueryParameter("ImageItemId", String.valueOf(imageItemId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, String.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                MyUtils.showAlertDialog(context, R.string.report_success);
                            } else {
                                MyUtils.showAlertDialog(context, R.string.report_not_success);

                            }
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                    }
                });
    }

    public static void reportUser(int reportTypeId, long userId, final Context context) {
        AndroidNetworking.post(Webservices.API_URL + "image/report")
                .addQueryParameter("ReportTypeId", String.valueOf(reportTypeId))
                .addQueryParameter("UserId", String.valueOf(userId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, String.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                MyUtils.showAlertDialog(context, R.string.report_success);
                            } else {
                                MyUtils.showAlertDialog(context, R.string.report_not_success);

                            }
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                    }
                });
    }

    public static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(context.getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    public static String getDeviceId(Context context) {
        String kq = UUID.randomUUID().toString();
        if (context != null) {

            /*final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null)
                kq = mTelephony.getDeviceId(); /*//*** use for mobiles
             else
             kq = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);*/ //*** use for tablets

            try {
                kq = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return kq.toUpperCase();
    }

    public static final String md5_Admod(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void share(Context context) {
        String text = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getText(R.string.app_name));
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static void share(Context context, ImageItem item) {
        if (context != null) {

            /*Intent intent = new Intent(context, MH06_FeedShareActivity.class);
            intent.putExtra(ImageItem.IMAGE_ITEM, item);
            context.startActivity(intent);*/

            if (!TextUtils.isEmpty(item.getWebLink())) {
                share(context, item.getWebLink());
            }

        }
    }

    public static void share(Context context, String message) {
        if (context != null) {

            String text = message;
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getText(R.string.app_name));
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(sendIntent, "Share"));

        }
    }

    public static void sendSms(Context context, String message) {
        if (context != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra("sms_body", message);
                intent.setData(Uri.parse("sms:"));
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException anfe) {
                Log.d("Error", "Error");
            }
        }
    }


    public static boolean isAndroid6() {
        boolean isAndroid6 = false;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            isAndroid6 = true;
        }
        return isAndroid6;
    }

    /////////////////////////////////////////////////////////
    public static boolean isEmailValid(String email) {
        /*final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();*/
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    public static void initCookie(Context context) {
        MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {
                if (task.getResult() != null) {
                    User kq = (User) task.getResult();
                    if (kq != null) {
//                            getSuggestFollow();
                    }
                }
                return null;
            }
        });
    }

    public static void goToSettings(Context context) {
        if (context != null) {
            Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
            myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myAppSettings);
        }
    }

    public static void openWebPage(String url, Context context) {
        if (context != null && !TextUtils.isEmpty(url)) {
            try {
                Uri webpage = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    public static File createImageFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        File image = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            image = new File(getImage.getPath(), imageFileName);
        } else {
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            image = new File(storageDir.getPath(), imageFileName);

        }

        return image;
    }

    public static File createVideoFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "VIDEO_COMPRESS_" + timeStamp + ".mp4";

        File image = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            image = new File(getImage.getPath(), imageFileName);
        } else {
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            image = new File(storageDir.getPath(), imageFileName);

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

    public static int getRotateImage(Bitmap img, Uri selectedImage) throws IOException {
        int rotate = 0;
        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
            default:
                rotate = 0;
        }

        return rotate;
    }

    public static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            //neu hinh lon hon size yeu cau thi moi resize, ko thi giu nguyen hinh nho
            if (width > maxSize) {
                width = maxSize;
            }

            height = (int) (width / bitmapRatio);
        } else {
            //neu hinh lon hon size yeu cau thi moi resize, ko thi giu nguyen hinh nho
            if (height > maxSize) {
                height = maxSize;
            }
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * @param mp
     * @return image path save
     */
    public static String saveBitmap(Bitmap mp, String picturePath) {
        Bitmap bitmap;
        OutputStream output;

        bitmap = mp;
        try {
            output = new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output);//70:size 167Kb, 100:size 1.54Mb
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }

    public static String saveBitmap(Bitmap mp, String picturePath, int quality) {
        Bitmap bitmap;
        OutputStream output;

        bitmap = mp;
        try {
            output = new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);//70:size 167Kb, 100:size 1.54Mb
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

    public static String getGUID() {
        return UUID.randomUUID().toString();
    }


    public static String getFileNameAndExtension(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            String name = filePath.substring(filePath.lastIndexOf('/') + 1);

            //loai bo dau tieng viet
            name = MyUtils.getUnsignedString(name);

            //thay the khoang trong, loai bo dau trong file
            name = name.replace(" ", "_");
            name = name.replace("-", "_");

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


    //////////////////////////////////////////////////////////////////////////////////////////
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(String dateString, Context context) {
        if (context != null && !TextUtils.isEmpty(dateString)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long time = 0;
            try {
                java.util.Date date = simpleDateFormat.parse(dateString);
                time = date.getTime();
            } catch (Exception e) {

            }

            if (time > 0) {
                long now = getCurrentLongDate();
                long diff = now - time;
//            if (time < 1000000000000L) {
                //if timestamp given in seconds, convert to millis time *= 1000; }
                if (time > now || time <= 0) {
                    return null;
                }
                //localize final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return context.getString(R.string.just_now);//"just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return context.getString(R.string.a_minute_ago);//"a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " " + context.getString(R.string.minutes_ago);//" minutes ago";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return context.getString(R.string.an_hour_ago);//"an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " " + context.getString(R.string.hours_ago);//" hours ago";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return context.getString(R.string.yesterday);//"yesterday";
                } else if (diff / DAY_MILLIS < 365) {
                    return diff / DAY_MILLIS + " " + context.getString(R.string.days_ago);//" days ago";
                } else {
                    return diff / (DAY_MILLIS * 365) + " " + context.getString(R.string.years_ago);
                }
//            }
            }
        }

        return "";
    }

    public static String getTimeAgo(long dateLong, Context context) {

        String dateString = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date(dateLong);

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
            sdf.setCalendar(cal);
            cal.setTime(d);

            dateString = sdf.format(d);
            dateString = getTimeAgo(dateString, context);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return dateString;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isOreoOrHigher() {
        if (Build.VERSION.SDK_INT >= 26) {
            return true;
        } else {
            return false;
        }
    }

    public static void startPlayerService(Context context, Class<?> serviceClass, Bundle bundle) {
        if (context != null && serviceClass != null) {
            Intent intent = new Intent(context, serviceClass);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            if (isOreoOrHigher()) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    public static void stopPlayerService(Context context, Class<?> serviceClass) {
        if (context != null && serviceClass != null) {
            if (isMyServiceRunning(context, serviceClass)) {
                Intent intent = new Intent(context, serviceClass);
                context.stopService(intent);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final boolean isUsername(String value) {
        String regex = "^@((?=.*[a-zA-Z])\\w{3,25})$";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static final boolean isUsernameOnly(String value) {
        String regex = "^((?=.*[a-zA-Z])\\w{3,25})$";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


    public static final String getUsername(String value) {
        String username = value;
        if (username.contains("@")) {
            String regex = "^@((?=.*[a-zA-Z])\\w{3,25})$";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                if (matcher.groupCount() > 0) {
                    username = matcher.group(1);
                    break;
                }
            }
        }
        return username;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final boolean isFacebookUrl(String value) {
        String regex = "(?:https?:\\/\\/)?(?:www\\.)?(mbasic.facebook|m\\.facebook|facebook|fb)\\.(com|me)\\/(?:(?:\\w\\.)*#!\\/)?(?:pages\\/)?(?:[\\w\\-\\.]*\\/)*([\\w\\-\\.]*)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static final boolean isTwitterUrl(String value) {
        String regex = "http(s)?:\\/\\/(.*\\.)?twitter\\.com\\/[A-z0-9_]+\\/?";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static final boolean isLinkedInUrl(String value) {
        try {
            String regex = "^(http(s)?:\\/\\/)?(www\\.)?linkedin\\.com\\/.*$";
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static final boolean isInstagramUrl(String value) {
        String regex = "https?:\\/\\/(www\\.)?instagram\\.com\\/([A-Za-z0-9_](?:(?:[A-Za-z0-9_]|(?:\\.(?!\\.))){0,28}(?:[A-Za-z0-9_]))?)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static final boolean isYoutubeUrl(String value) {
        String regex = "((http(s)?:\\/\\/)?)(www\\.)?((youtube\\.com\\/)|(youtu.be\\/))[\\S]+";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static androidx.appcompat.app.AlertDialog createDialogLoading(Context context, int title) {
        if (context != null && title > 0) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle(title)
                    .setView(R.layout.dialog_loading)
                    .setCancelable(true);
            return builder.create();
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Luu lai danh sach user followings
     *
     * @param list
     * @return
     */
    public static boolean writeListUserFollowingToFile(final ArrayList<Double> list) {
        boolean isSuccess = false;

        Realm realm = Realm.getDefaultInstance();
        if (realm != null) {
            try {
                if (list == null) {
                    realm.beginTransaction();
                    realm.delete(UserFollowing.class);
                    realm.commitTransaction();
                } else {

                    //xoa roi moi ghi lai
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(UserFollowing.class);
                            for (int i = 0; i < list.size(); i++) {
                                try {
                                    UserFollowing user = new UserFollowing();
                                    user.setUserId(list.get(i).longValue());

                                    realm.copyToRealmOrUpdate(user);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

//                            RealmResults<UserFollowing> ids = realm.where(UserFollowing.class).findAll();

                            realm.close();

                        }
                    });
                }
                isSuccess = true;
//                realm.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isSuccess;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void deleteUserFollowing(long userId) {
        if (userId > 0) {
            //remove khoi database
            Realm realm = Realm.getDefaultInstance();
            if (realm != null) {
                try {
                    //remove user khoi db
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            UserFollowing results = realm.where(UserFollowing.class).equalTo("UserId", userId).findFirst();
                            if (results != null) {
                                results.deleteFromRealm();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                realm.close();
            }
        }
    }

    public static void saveUserFollowing(long userId) {
        if (userId > 0) {
            //remove khoi database
            Realm realm = Realm.getDefaultInstance();
            if (realm != null) {
                try {
                    //remove user khoi db
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            UserFollowing user = new UserFollowing();
                            user.setUserId(userId);
                            realm.copyToRealm(user);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                realm.close();
            }
        }
    }

    public static boolean isFollowing(long userId) {
        boolean isExist = false;
        if (userId > 0) {
            //remove khoi database
            Realm realm = Realm.getDefaultInstance();
            if (realm != null) {
                try {
                    realm.beginTransaction();
                    //get all user
                    UserFollowing query = realm.where(UserFollowing.class).equalTo("UserId", userId).findFirst();
                    //execute query
                    if (query != null) {
                        isExist = true;
                    }
                    realm.commitTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                realm.close();
            }
        }
        return isExist;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getPublicIP() {
        /*try {
            //http://checkip.amazonaws.com/
            Document doc = Jsoup.connect("http://www.checkip.org").get();
            return doc.getElementById("yourip").select("h1").first().select("span").text();
        } catch (IOException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }*/
        return "0.0.0.0";
    }

    public static void chatWithUser(Context context, UserChatCore user) {
        if (user != null) {
            //chat voi user
            if (checkInternetConnection(context)) {
                if (ChatActivity.isExists) {
                    context.sendBroadcast(new Intent(ChatActivity.FINISH_ACTIVITY));
                }

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(UserChatCore.USER_MODEL, user);
                context.startActivity(intent);
            } else {
                com.workchat.core.utils.MyUtils.showThongBao(context);
            }
        }
    }

    public static long gettNumberFromString(String uuid) {
        //5ea2a3c263bc0762cc081740
        if (!TextUtils.isEmpty(uuid)) {
            String str = uuid.replaceAll("\\D+", "");
            return Long.parseLong(str);
        } else {
            return 0;
        }
    }

    public static void setText(String message, TextView txt) {
        if (!TextUtils.isEmpty(message)) {
            txt.setVisibility(View.VISIBLE);
            txt.setText(Html.fromHtml(message));
        } else {
            txt.setVisibility(View.GONE);
        }
    }

    public static void updateImageItem(Context context, ImageItem imageItem, boolean isUpdateDetail) {
        if (context != null && imageItem != null) {
            MyApplication.itemReturn = imageItem;

            //update home list
            Intent intent = new Intent(Fragment_1_Home_User.ACTION_UPDATE_ITEM);
//            intent.putExtra(ImageItem.IMAGE_ITEM, imageItem);
            context.sendBroadcast(intent);

            if (isUpdateDetail) {
                //update man hinh chi tiet MH02_PhotoDetailActivity => da update truc tiep
                intent = new Intent(MH02_PhotoDetailActivity.ACTION_UPDATE_ITEM);
//            intent.putExtra(ImageItem.IMAGE_ITEM, imageItem);
                context.sendBroadcast(intent);
            }

            //update grid user
            intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_UPDATE_ITEM);
//            intent.putExtra(ImageItem.IMAGE_ITEM, imageItem);
            context.sendBroadcast(intent);

            //update grid image by hashtag
            intent = new Intent(HashTagActivity.ACTION_UPDATE_ITEM);
//            intent.putExtra(ImageItem.IMAGE_ITEM, imageItem);
            context.sendBroadcast(intent);

            //update grid explorer
            intent = new Intent(Fragment_2_Explorer.ACTION_UPDATE_ITEM);
//            intent.putExtra(ImageItem.IMAGE_ITEM, imageItem);
            context.sendBroadcast(intent);
        }
    }

    public static void gotoDetailImage(Context context, ImageItem item, boolean isClickCommentButton) {
        MyApplication.isClickCommentButton = isClickCommentButton;
        gotoDetailImage(context, item);
    }

    public static void gotoDetailImage(Context context, ImageItem item) {
        if (context != null && item != null) {

            //luu tam vao cache va doc len lai tu cache
            MyApplication.imgItem = item;

            if (item.isVideo()) {
                if (PermissionUtils.checkPermission()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            VideoListItemOpsKt.playVideoImageItem(context, item.getImageLarge());
                        }
                    }, 500);
                } else {
                    context.sendBroadcast(new Intent(MH01_MainActivity.ACTION_CHECK_PERMISSION));
                }
            } else {
                Intent intent = new Intent(context, MH02_PhotoDetailActivity.class);
                context.startActivity(intent);
            }

        }
    }

    public static void gotoDetailImage(Context context, long imageItemId) {
        if (context != null && imageItemId > 0) {
            Webservices.getImageItem(imageItemId).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ImageItem image = (ImageItem) task.getResult();
                            if (image != null) {
                                MyUtils.gotoDetailImage(context, image);
                            }
                        }
                    }
                    return null;
                }
            });
        }
    }

    public static void editPost(ImageItem item, Context context) {
        if (context != null) {
            if (item != null) {


                if (ImageItem.ITEM_TYPE_FACEBOOK.equals(item.getItemType())) {
                    //luu tam vao cache va doc len lai tu cache
                    MyApplication.imgEdit = item;

                    Intent intent = new Intent(context, PostLikeFacebookActivity.class);
//                    intent.putExtra(ImageItem.IMAGE_ITEM, item);
                    context.startActivity(intent);
                } else {//instagram
                    Intent intent = new Intent(context, MH04_FeedEditActivity.class);
                    intent.putExtra(ImageItem.IMAGE_ITEM, item);
                    context.startActivity(intent);
                }

            }
        }
    }


    //show hide views
    public static void hide(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
    }

    public static void show(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.VISIBLE);
        }
    }
}
