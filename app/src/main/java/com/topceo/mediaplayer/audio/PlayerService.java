package com.topceo.mediaplayer.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.topceo.shopping.Media;
import com.topceo.shopping.MediaItem;

import java.util.ArrayList;
import java.util.Random;

/**
 * https://codingwithmitch.com/blog/bound-services-on-android/
 */
public class PlayerService extends Service {

    //BINDER////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Class used for the client Binder. The Binder object is responsible for returning an instance
     * of "MyService" to the client.
     */
    public class MyBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    // Binder given to clients (notice class declaration below)
    private final IBinder mBinder = new MyBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    /**
     * method for clients to get a random number from 0 - 100
     */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
    //BINDER////////////////////////////////////////////////////////////////////////////////////////


    /**
     * This is how the client gets the IBinder object from the service. It's retrieve by the "ServiceConnection"
     * which you'll see later.
     **/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mMediaPlayerHolder == null) {
            mMediaPlayerHolder = new MediaPlayerHolder(this);
            mMusicNotificationManager = new MusicNotificationManager(this);
            mMediaPlayerHolder.registerNotificationActionsReceiver(true);
        }
        return mBinder;
    }



    @Override
    public void onCreate() {
        super.onCreate();
//        initForAndroidOreoAndAbove();
    }

    private void initForAndroidOreoAndAbove() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("A service is running in the background")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    //Bo sung de nhan data
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getExtras() != null) {
                media = intent.getParcelableExtra(Media.MEDIA);
                listAlbum = intent.getParcelableArrayListExtra(MediaItem.LIST);

                //tien hanh playing
                if (listAlbum != null && listAlbum.size() > 0) {
//                    beginPlaying(listAlbum.get(0).getFileUrl());

                    if (mMediaPlayerHolder == null) {
                        mMediaPlayerHolder = new MediaPlayerHolder(this);
                        mMusicNotificationManager = new MusicNotificationManager(this);
                        mMediaPlayerHolder.registerNotificationActionsReceiver(true);
                    }

                    mMediaPlayerHolder.setAlbum(media);
                    mMediaPlayerHolder.setCurrentSong(listAlbum.get(0), listAlbum);
                    mMediaPlayerHolder.initMediaPlayer();
                }


//
            }
        }
        return START_STICKY;
    }


    private Media media;
    private ArrayList<MediaItem> listAlbum = new ArrayList<>();

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public ArrayList<MediaItem> getListAlbum() {
        return listAlbum;
    }

    public void setListAlbum(ArrayList<MediaItem> listAlbum) {
        this.listAlbum = listAlbum;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //PLAYER
    /*private String currentUrlPlaying = "";
    private int currentUrlPlayingPosition = 0;

    public int getCurrentPosition() {
        int position = 0;
        if (!TextUtils.isEmpty(currentUrlPlaying) && listAlbum != null) {
            for (int i = 0; i < listAlbum.size(); i++) {
                if (currentUrlPlaying.equalsIgnoreCase(listAlbum.get(i).getFileUrl())) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    private SimpleExoPlayer exoPlayer;

    private void beginPlaying(String url) {

        if (!TextUtils.isEmpty(url)) {

            //reset
            currentUrlPlaying = url;

            TrackSelector trackSelector = new DefaultTrackSelector();

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));

            MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), renderersFactory, trackSelector, new DefaultLoadControl());

            exoPlayer.prepare(audioSource);

            exoPlayer.setPlayWhenReady(true);



        }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /*private void playingVideo(String url){
        if(!TextUtils.isEmpty(url)){
            TrackSelector trackSelector = new DefaultTrackSelector();

            SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            PlayerView simpleExoPlayerView = findViewById(R.id.player_view);

            simpleExoPlayerView.setPlayer(exoPlayer);

            exoPlayer.setPlayWhenReady(true);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "VideoPlayer"));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("https://goo.gl/PyKasq"));

            exoPlayer.prepare(videoSource);
        }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if(exoPlayer!=null){
            exoPlayer.release();
        }*/

        try {
            mMediaPlayerHolder.registerNotificationActionsReceiver(false);
            mMusicNotificationManager = null;
            mMediaPlayerHolder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private MediaPlayerHolder mMediaPlayerHolder;

    private MusicNotificationManager mMusicNotificationManager;

    private boolean sRestoredFromPause = false;

    public final boolean isRestoredFromPause() {
        return sRestoredFromPause;
    }

    public void setRestoredFromPause(boolean restore) {
        sRestoredFromPause = restore;
    }

    public final MediaPlayerHolder getMediaPlayerHolder() {
        return mMediaPlayerHolder;
    }

    public MusicNotificationManager getMusicNotificationManager() {
        return mMusicNotificationManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////


}
