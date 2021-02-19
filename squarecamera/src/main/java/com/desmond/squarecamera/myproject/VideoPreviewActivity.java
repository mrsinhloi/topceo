package com.desmond.squarecamera.myproject;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.desmond.squarecamera.R;
import com.otaliastudios.cameraview.VideoResult;

import java.lang.ref.WeakReference;


public class VideoPreviewActivity extends Activity {

    private VideoView videoView;

    private static WeakReference<VideoResult> videoResult;

    public static void setVideoResult(@Nullable VideoResult result) {
        videoResult = result != null ? new WeakReference<>(result) : null;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squarecamera_activity_video_preview);
        videoView = findViewById(R.id.video);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });


        final VideoResult result = videoResult == null ? null : videoResult.get();
        if (result == null) {
            finish();
            return;
        }

        /*actualResolution.setTitleAndMessage("Size", result.getSize() + "");
        isSnapshot.setTitleAndMessage("Snapshot", result.isSnapshot() + "");
        rotation.setTitleAndMessage("Rotation", result.getRotation() + "");
        audio.setTitleAndMessage("Audio", result.getAudio().name());
        audioBitRate.setTitleAndMessage("Audio bit rate", result.getAudioBitRate() + " bits per sec.");
        videoCodec.setTitleAndMessage("VideoCodec", result.getVideoCodec().name());
        videoBitRate.setTitleAndMessage("Video bit rate", result.getVideoBitRate() + " bits per sec.");
        videoFrameRate.setTitleAndMessage("Video frame rate", result.getVideoFrameRate() + " fps");*/


        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(Uri.fromFile(result.getFile()));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();
                float viewWidth = videoView.getWidth();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                videoView.setLayoutParams(lp);
                playVideo();

                if (result.isSnapshot()) {
                    // Log the real size for debugging reason.
                    Log.e("VideoPreview", "The video full size is " + videoWidth + "x" + videoHeight);
                }
            }
        });
    }

    void playVideo() {
        if (videoView.isPlaying()) return;
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            setVideoResult(null);
        }
    }
}
