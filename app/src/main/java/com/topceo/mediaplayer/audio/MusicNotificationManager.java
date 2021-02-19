package com.topceo.mediaplayer.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.topceo.R;
import com.topceo.shopping.Media;
import com.topceo.shopping.MediaItem;

//import androidx.media.app.NotificationCompat;


public class MusicNotificationManager {
    public static final int NOTIFICATION_ID = 101;
    static final String PLAY_PAUSE_ACTION = "action.PLAYPAUSE";
    static final String NEXT_ACTION = "action.NEXT";
    static final String PREV_ACTION = "action.PREV";
    private final String CHANNEL_ID = "action.CHANNEL_ID";
    private final int REQUEST_CODE = 100;
    private final NotificationManager mNotificationManager;
    private final PlayerService mMusicService;
    private NotificationCompat.Builder mNotificationBuilder;
    private MediaSessionCompat mediaSession;
    private MediaSessionManager mediaSessionManager;
    private MediaControllerCompat.TransportControls transportControls;
    private Context context;

    MusicNotificationManager(@NonNull final PlayerService musicService) {
        mMusicService = musicService;
        mNotificationManager = (NotificationManager) mMusicService.getSystemService(Context.NOTIFICATION_SERVICE);
        context = musicService.getBaseContext();
    }


    public final NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    public final NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    private PendingIntent playerAction(String action) {

        final Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(mMusicService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Notification createNotification() {

        final MediaItem song = mMusicService.getMediaPlayerHolder().getCurrentSong();
        final Media album = mMusicService.getMediaPlayerHolder().getAlbum();

        mNotificationBuilder = new NotificationCompat.Builder(mMusicService, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        final Intent openPlayerIntent = new Intent(mMusicService, MediaPlayerActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(mMusicService, REQUEST_CODE,
                openPlayerIntent, 0);

        final String artist = song.getAuthor();
        final String songTitle = song.getTitle();

        initMediaSession(song, album);



        mNotificationBuilder
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_play)
                .setLargeIcon(getBitmap(song))
                .setColor(context.getResources().getColor(R.color.colorAccentChat))
                .setContentTitle(songTitle)
                .setContentText(artist)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mNotificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2));


        int size = context.getResources().getDimensionPixelSize(R.dimen.notification_large_dim);
        Glide.with(context)
                .asBitmap()
                .override(size, size)
                .centerCrop()
                .load(!TextUtils.isEmpty(song.getThumbnailUrl())?song.getThumbnailUrl():album.getThumbnail())
                .into(new Target<Bitmap>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onDestroy() {

                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {

                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mNotificationBuilder.setLargeIcon(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void setRequest(@Nullable Request request) {

                    }

                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }
                });


        return mNotificationBuilder.build();
    }

    @NonNull
    private NotificationCompat.Action notificationAction(final String action) {

        int icon;

        switch (action) {
            default:
            case PREV_ACTION:
                icon = R.drawable.ic_skip_previous;
                break;
            case PLAY_PAUSE_ACTION:

                icon = mMusicService.getMediaPlayerHolder().getState() != PlaybackInfoListener.State.PAUSED
                        ? R.drawable.ic_pause : R.drawable.ic_play;
                break;
            case NEXT_ACTION:
                icon = R.drawable.ic_skip_next;
                break;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    @RequiresApi(26)
    private void createNotificationChannel() {

        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            final NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            mMusicService.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(mMusicService.getString(R.string.app_name));

            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void initMediaSession(MediaItem song, Media album) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = ((MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE));
        }
        mediaSession = new MediaSessionCompat(context, "AudioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        updateMetaData(song, album);
    }

    private void updateMetaData(MediaItem song, Media album) {
        int size = context.getResources().getDimensionPixelSize(R.dimen.notification_large_dim);
        Glide.with(context)
                .asBitmap()
                .override(size, size)
                .load(!TextUtils.isEmpty(song.getThumbnailUrl())?song.getThumbnailUrl():album.getThumbnail())
                .into(new Target<Bitmap>() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onDestroy() {

                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getBitmap(song))
                                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getAuthor())
                                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album.getTitle())
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                                .build());
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource)
                                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getAuthor())
                                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album.getTitle())
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                                .build());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void setRequest(@Nullable Request request) {

                    }

                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }
                });





    }

    private Bitmap getBitmap(MediaItem song){
       return Utils.getLargeIcon(context);
    }

}
