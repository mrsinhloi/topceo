package com.topceo.autoplayvideo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.danikula.videocache.HttpProxyCacheServer;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.utils.MyUtils;

/**
 * Created by tuanha00 on 1/22/2018.
 */

public class VideoView extends RelativeLayout implements TextureView.SurfaceTextureListener {


    private Video video;

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private OnCompletionListener onCompletionListener;

    public VideoView(Context context) {
        super(context);
        init(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    private void init(Context context) {
        inflate(context, R.layout.fragment_1_row_video_layout, this);
        TextureView textureView = findViewById(R.id.surfaceView);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            if(!TextUtils.isEmpty(video.getUrlVideo())) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setSurface(new Surface(surfaceTexture));
                HttpProxyCacheServer proxy = MyApplication.getProxy(getContext());
                String proxyUrl = proxy.getProxyUrl(video.getUrlVideo());
                mediaPlayer.setDataSource(proxyUrl);
//               mediaPlayer.setDataSource(video.getUrlVideo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void play(final OnPreparedListener onPreparedListener) {
        if (mediaPlayer != null && video!=null) {
            if (isPlaying) return;
            isPlaying = true;
            try {
//                this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                this.mediaPlayer.prepareAsync();
                this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        if(video!=null){
                            mediaPlayer.seekTo(video.getSeekTo());
                            mediaPlayer.setLooping(true);
                            mediaPlayer.start();
                            if (onPreparedListener != null) onPreparedListener.onPrepared();
                        }
                    }
                });

                //ko co bi loi load cache
                this.mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        MyUtils.log("percent = "+ percent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (!isPlaying) return;
        isPlaying = false;
//        video.setSeekTo(mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() ? 0 : mediaPlayer.getCurrentPosition());
        mediaPlayer.pause();
        mediaPlayer.stop();
    }

    public void setVolume(int volume) {
        if(mediaPlayer!=null){
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public interface OnCompletionListener {
        void onCompletion();
    }


    public interface OnPreparedListener {
        void onPrepared();
    }

}

