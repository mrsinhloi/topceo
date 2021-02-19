package com.topceo.mediaplayer.preview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.desmond.squarecamera.myproject.APIConstants;
import com.topceo.R;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnK4LVideoListener;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {

    private Context context = this;
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    private K4LVideoTrimmer mVideoTrimmer;
//    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();
        String path = "";

        if (extraIntent != null) {
            path = extraIntent.getStringExtra(EXTRA_VIDEO_PATH);
        }

        //setting progressbar
        /*mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));*/

        mVideoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {
            mVideoTrimmer.setMaxDuration(APIConstants.TIME_VIDEO_RECORD_SECOND);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnK4LVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        /*if (mProgressDialog != null && !mProgressDialog.isShowing() && !isFinishing())
            mProgressDialog.show();*/
        ProgressUtils.show(context);
    }

    @Override
    public void getResult(final Uri uri) {
        ProgressUtils.hide();

        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, uri.getPath()), Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
        finish();*/

        String savedPath = uri.getPath();

        //vao man hinh chon thumbnail
        Intent intent = new Intent(context, VideoSelectThumbnailActivity.class);
        intent.putExtra(APIConstants.VIDEO_URL, savedPath);
        startActivity(intent);

        finish();
    }

    @Override
    public void cancelAction() {
        ProgressUtils.hide();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        ProgressUtils.hide();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyUtils.showToast(context, message);
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyUtils.showToastDebug(context, "onVideoPrepared");
            }
        });
    }
}
