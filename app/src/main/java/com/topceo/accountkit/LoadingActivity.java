package com.topceo.accountkit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.ads.AdUtils;
import com.topceo.ads.AdsAppModel;
import com.topceo.ads.ReturnResult;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.onesignal.NotifyObject;
import com.topceo.utils.MyUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
public class LoadingActivity extends AppCompatActivity {
    private Activity context = this;

    private NotifyObject notify;
    TinyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.initLanguage(this);
        if (!MyUtils.checkInternetConnection(this)) {
            MyUtils.showToast(this, R.string.khongCoInternet);
            finish();
            return;
        }
        db = new TinyDB(context);
        gotoApp();

    }


    private void gotoApp() {

        final boolean isLogined = db.getBoolean(TinyDB.IS_LOGINED);
        if (isLogined) {
            //tao cookie truoc khi su dung
            /*MyApplication.initCookie(getApplicationContext()).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getResult() != null) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //kiem tra neu bi banned
                                if (task.getResult() instanceof Integer) {
                                    MyUtils.showAlertDialog(context, R.string.account_is_banned, true);
                                } else {
                                    //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
                                    MyApplication.whenLoginSuccess();

                                    //doc lai thong tin user, neu chua suggest thi moi hien
                                    Object obj = db.getObject(User.USER, User.class);
                                    if (obj != null) {
                                        User user = (User) obj;
                                        if(user.isHashtagSuggested()){//da suggest roi
                                            gotoMain();
                                        }else{
                                            //vao man hinh suggest
                                            startActivity(new Intent(context, SelectFavoritesActivity.class));

                                        }
                                    }else{
                                        gotoMain();
                                    }

                                    finish();
                                }

                            }
                        });

                    }else{

                    }
                    return null;
                }
            });*/
            gotoMain();

        } else {
//            startActivity(new Intent(context, WelcomeActivity.class));
//            finish();
        }
        /////////////////////////////////////////

    }

    private void gotoMain() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            notify = b.getParcelable(NotifyObject.NOTIFY_OBJECT);
        }
        Intent intent = new Intent(context, MH01_MainActivity.class);
        intent.putExtra(NotifyObject.NOTIFY_OBJECT, notify);
        startActivity(intent);
    }

    ///////////////////////////
    private void replaceWord(String tagMention) {
        //dua vao cursorPosition de biet dang edit o word nao
        int cursorPosition = 0;
        String text = "12 @345 67890";
        String gold = MyUtils.getStringEditting(text, cursorPosition);

        //replace word dang edit bang tagMention
        text = text.replace(gold, tagMention);
        MyUtils.log(text);

    }

    /////////////////////////////////////////////////////
    private void checkPermissionReadPhoneState() {
        if (MyUtils.isAndroid6()) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // We do not have this permission. Let's ask the user
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 100);
            } else {
                gotoApp();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                    gotoApp();
                } else {
                    // permission denied
                    MyUtils.showToast(getApplicationContext(), R.string.app_not_rung_without_permission);
                    finish();
                }
                return;
            }

        }
    }

    public static String ACCESS_KEY_ADS = "fb123Android!@#";

    /**
     * LAY THONG TIN QUANG CAO
     * Password = AccessKey xac dinh 1 app, tren trang admin solomoads
     */
    private void getAdAppModel(int appId) {
        AndroidNetworking.post("http://apiv2.solomoads.com/api/Ads/GetAdConfig")
                .addHeaders("UserName", "Android")
                .addHeaders("Password", ACCESS_KEY_ADS)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addBodyParameter("AppId", String.valueOf(appId))
                .addBodyParameter("DeviceId", MyUtils.getDeviceId(context))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("test", response.toString());

                        try {
                            if (response.getInt("ErrorCode") == ReturnResult.ERROR_CODE_THANH_CONG) {
                                if (response.has("Data")) {
                                    JSONObject data = response.getJSONObject("Data");
                                    AdsAppModel model = new Gson().fromJson(data.toString(), AdsAppModel.class);
                                    if (model != null) {
                                        AdUtils.writeListToFile(context, model, AdUtils.OBJECT_ADS);
                                    } else {
                                        AdUtils.writeListToFile(context, null, AdUtils.OBJECT_ADS);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //co hay ko thi cung vao app
                        gotoApp();

                    }

                    @Override
                    public void onError(ANError error) {
                        gotoApp();
                    }
                });
    }


}
