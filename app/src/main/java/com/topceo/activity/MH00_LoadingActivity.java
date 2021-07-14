package com.topceo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.dynamic_link.DynamicData;
import com.topceo.login.WelcomeActivity;
import com.topceo.onesignal.NotifyObject;
import com.topceo.utils.MyUtils;

import bolts.Continuation;
import bolts.Task;

public class MH00_LoadingActivity extends AppCompatActivity {
    private Activity context = this;

    private NotifyObject notify;
    TinyDB db;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //neu co dynamic link
        progressDynamiclink(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new TinyDB(context);
        progressDynamiclink(getIntent());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_00);
//        gotoApp();

//        throw new RuntimeException("Test Crash"); // Force a crash

    }


    private void gotoApp() {

        final boolean isLogined = db.getBoolean(TinyDB.IS_LOGINED);
        if (isLogined) {
            checkCookie();

            /*Uri videoUri = getMedia(VIDEO_SAMPLE);
            vv.setVideoURI(videoUri);
            vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(2.f));
                        vv.start();
                    }
                }
            });
            vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    gotoMain();
                }
            });
            vv.start();*/


            if (!MyUtils.checkInternetConnection(this)) {
                MyUtils.showToast(this, R.string.khongCoInternet);
                finish();
                return;
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gotoMain();
                    }
                }, 3000);
            }


        } else {
            //neu vao tu email verify link
            startActivity(new Intent(context, WelcomeActivity.class));
            finish();
        }
        /////////////////////////////////////////

    }

    private void gotoMain() {
        Intent intent = new Intent(context, MH01_MainActivity.class);
        startActivity(intent);
        finish();
    }


    //VIDEO VIEW CONTROL///
    private static final String VIDEO_SAMPLE = "video_loading";

    private Uri getMedia(String mediaName) {
        return Uri.parse("android.resource://" + getPackageName() +
                "/raw/" + mediaName);
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        if(vv!=null){
            vv.stopPlayback();
        }
        db.putInt(PLAYBACK_TIME, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            vv.pause();
        }
        db.putInt(PLAYBACK_TIME, vv.getCurrentPosition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentPosition = db.getInt(PLAYBACK_TIME);
        if (mCurrentPosition > 0) {
            vv.seekTo(mCurrentPosition);
        } else {
            // Skipping to 1 shows the first frame of the video.
            vv.seekTo(1);
        }
        vv.start();
    }*/

    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";

    //VIDEO VIEW CONTROL///
    void checkCookie() {
        //tao cookie truoc khi su dung
        MyApplication.initCookie(getApplicationContext()).continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {
                if (task.getResult() != null) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //kiem tra neu bi banned
                            if (task.getResult() instanceof Integer) {
//                                    MyUtils.showAlertDialog(context, R.string.account_is_banned, true);

                            } else {

                                //Da goi trong initCookie nen ko can goi lai
//                                    MyApplication.whenLoginSuccess();

                            }

                        }
                    });

                } else {

                }
                return null;
            }
        });
    }


    //DYNAMIC LINK
    private void progressDynamiclink(Intent intent) {
        //mac dinh la ko ghi cho ai
        db.putLong(DynamicData.IMAGE_ID, 0);
        db.putLong(DynamicData.AFFILIATE_ID, 0);
        db.putLong(DynamicData.PROFILE_ID, 0);

        //neu co thi se ghi nhan
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        //co deeplink hay ko thi cung vao app
                        if (deepLink != null) {

                            //mo hinh co id nay
                            String id = deepLink.getQueryParameter("id");
                            if (!TextUtils.isEmpty(id)) {
                                try {
                                    long itemId = Long.parseLong(id);
                                    //MyUtils.gotoDetailImage(context, imageItemId);
                                    if (deepLink.toString().contains("profile?id=")) {
                                        db.putLong(DynamicData.PROFILE_ID, itemId);
                                    } else {
                                        db.putLong(DynamicData.IMAGE_ID, itemId);
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            //ghi nhan affiliate
                            String affId = deepLink.getQueryParameter("affId");
                            if (!TextUtils.isEmpty(affId)) {
                                try {
                                    long aff = Long.parseLong(affId);
                                    db.putLong(DynamicData.AFFILIATE_ID, aff);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }

                                MyUtils.showToast(context, "You have affId = " + affId);
                            }

                        }

                        //vao app
                        gotoApp();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyUtils.log("getDynamicLink:onFailure");
                        //vao app
                        gotoApp();
                    }
                });
    }


}
