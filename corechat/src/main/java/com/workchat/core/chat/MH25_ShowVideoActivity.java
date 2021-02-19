package com.workchat.core.chat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;


public class MH25_ShowVideoActivity extends Activity {
    public static final String VIDEO_URL = "VIDEO_URL";

    private TextView txtTitle;
    private ImageView img1, img2, img3;
    private VideoView vv;
    private String name = "";
    private Context context = this;


    private String videoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh25_show_video_activity);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);//save
        vv = (VideoView) findViewById(R.id.player);

        img1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        Bundle b = getIntent().getExtras();
        if (b != null) {
            videoUrl = b.getString(VIDEO_URL, "");
            if (!TextUtils.isEmpty(videoUrl)) {
//                videoUrl = "https://chatnhanh.blob.core.windows.net/chat/5a546cb65c225b2c440fc0f9/158852/8ce28a92-8660-442f-85ae-6249b3c6afb8/vid_2018_07_19_15_37_22.mp4";
                vv.setVideoURI(Uri.parse(videoUrl));
                vv.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        vv.start();
                    }
                });

            }
        }

        //share
        img2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Uri imageUri = Uri.parse(videoUrl);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "Video");
                    intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(imageUri));
                    startActivity(Intent.createChooser(intent, "Share"));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        //download image
        img3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.checkInternetConnection(context)) {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.download_confirm);
                    alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();

                            String path = videoUrl;
                            if (!TextUtils.isEmpty(path)) {
                                String name = MyUtils.getFileNameFromUrl(path);
                                downloadVideo(path, name);
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //http://www.codexpedia.com/android/android-downloadmanager-example/
    public void downloadVideo(String url, String outputFileName) {

        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(outputFileName)) {
            try {
                //replace blank space
                url = url.replaceAll(" ", "%20");
                outputFileName = outputFileName.replaceAll(" ", "_");


                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Download video to device");
                request.setTitle("Download " + outputFileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                request.setDestinationInExternalPublicDir(getString(R.string.app_name), outputFileName);
                request.setVisibleInDownloadsUi(true);
                request.setMimeType("media/video");

                //Log.d("MainActivity: ", "download folder>>>>" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                MyUtils.showAlertDialog(context, R.string.file_downloading_in_app_bar);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
    ///////////////////////////////////////////////////////////////////////////////////////////

}
