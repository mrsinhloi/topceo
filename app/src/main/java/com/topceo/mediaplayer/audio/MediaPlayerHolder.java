package com.topceo.mediaplayer.audio;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.topceo.R;
import com.topceo.db.TinyDB;
import com.topceo.shopping.Media;
import com.topceo.shopping.MediaItem;
import com.topceo.utils.MyUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class MediaPlayerHolder implements PlayerAdapter/*,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener */ {

    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    private static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    private static final float VOLUME_NORMAL = 1.0f;
    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;
    private final Context mContext;
    private final PlayerService mMusicService;
    private final AudioManager mAudioManager;
    private SimpleExoPlayer mMediaPlayer;
    private PlaybackInfoListener mPlaybackInfoListener;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekBarPositionUpdateTask;
    private MediaItem mSelectedSong;
    private ArrayList<MediaItem> mSongs;
    private boolean isReplaySong = false;
    private boolean isShuffle = false;
    private @PlaybackInfoListener.State
    int mState;
    private NotificationReceiver mNotificationActionsReceiver;
    private MusicNotificationManager mMusicNotificationManager;
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    private boolean mPlayOnFocusGain;
    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                @Override
                public void onAudioFocusChange(int focusChange) {

                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            mPlayOnFocusGain = isMediaPlayer() && mState == PlaybackInfoListener.State.PLAYING
                                    || mState == PlaybackInfoListener.State.RESUMED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (isMediaPlayer()) {
                        // Update the player state based on the change
                        configurePlayerState();
                    }

                }
            };

    MediaPlayerHolder(@NonNull final PlayerService musicService) {
        mMusicService = musicService;
        mContext = mMusicService.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private void registerActionsReceiver() {
        mNotificationActionsReceiver = new NotificationReceiver();
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(MusicNotificationManager.PREV_ACTION);
        intentFilter.addAction(MusicNotificationManager.PLAY_PAUSE_ACTION);
        intentFilter.addAction(MusicNotificationManager.NEXT_ACTION);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        mMusicService.registerReceiver(mNotificationActionsReceiver, intentFilter);
    }

    private void unregisterActionsReceiver() {
        if (mMusicService != null && mNotificationActionsReceiver != null) {
            try {
                mMusicService.unregisterReceiver(mNotificationActionsReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void registerNotificationActionsReceiver(final boolean isReceiver) {

        if (isReceiver) {
            registerActionsReceiver();
        } else {
            unregisterActionsReceiver();
        }
    }

    @Override
    public final MediaItem getCurrentSong() {
        return mSelectedSong;
    }

    @Override
    public int getCurrentPosition() {
        if (mSelectedSong != null) {
            int position = findPosition(mSelectedSong.getItemId());
            if (position > -1) {
                return position;
            }
        }
        return 0;
    }

    @Override
    public void setCurrentSong(@NonNull final MediaItem song, @NonNull final ArrayList<MediaItem> songs) {
        mSelectedSong = song;
        mSongs = songs;
    }

    @Override
    public void setCurrentSong(@NonNull long itemId) {
        if (mSongs != null) {
            int position = findPosition(itemId);
            if (position > -1) {
                MediaItem item = mSongs.get(position);
                //neu khac bai dang hat
//                if (item.getMediaId() != mSelectedSong.getMediaId()) {
                mSelectedSong = item;
                initMediaPlayer();
//                }
            }
        }
    }

    private int findPosition(long itemId) {
        int position = -1;
        if (mSongs != null) {
            for (int i = 0; i < mSongs.size(); i++) {
                MediaItem item = mSongs.get(i);
                if (item.getItemId() == itemId) {
                    position = i;
                    break;
                }
            }

        }
        return position;
    }

    private Media album;

    @Override
    public Media getAlbum() {
        return album;
    }

    @Override
    public void setAlbum(@NonNull Media album) {
        this.album = album;
    }

    /*@Override
    public void onCompletion(@NonNull final MediaPlayer mediaPlayer) {
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
            mPlaybackInfoListener.onPlaybackCompleted();
        }

        if (isReplaySong) {
            if (isMediaPlayer()) {
                resetSong();
            }
            isReplaySong = false;
        } else {
            skip(true);
        }
    }*/

    @Override
    public void onResumeActivity() {
        startUpdatingCallbackWithPosition();
    }

    @Override
    public void onPauseActivity() {
        stopUpdatingCallbackWithPosition();
    }

    private void tryToGetAudioFocus() {

        final int result = mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    public void setPlaybackInfoListener(@NonNull final PlaybackInfoListener listener) {
        mPlaybackInfoListener = listener;
    }

    private void setStatus(final @PlaybackInfoListener.State int state) {

        mState = state;
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onStateChanged(state);
        }
    }

    private void resumeMediaPlayer() {
        if (!isPlaying() && isMediaPlayer()) {
//            mMediaPlayer.start();
            mMediaPlayer.setPlayWhenReady(true);
            setStatus(PlaybackInfoListener.State.RESUMED);
            mMusicService.startForeground(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
        }
    }

    private void pauseMediaPlayer() {
        setStatus(PlaybackInfoListener.State.PAUSED);
//        mMediaPlayer.pause();
        if (isMediaPlayer())
            mMediaPlayer.setPlayWhenReady(false);
        mMusicService.stopForeground(false);
        mMusicNotificationManager.getNotificationManager().notify(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
    }

    private void resetSong() {
        if (isMediaPlayer()) {
            mMediaPlayer.seekTo(0);
//        mMediaPlayer.start();
            mMediaPlayer.setPlayWhenReady(true);
            setStatus(PlaybackInfoListener.State.PLAYING);
        }
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }

        mExecutor.scheduleAtFixedRate(
                mSeekBarPositionUpdateTask,
                0,
                1000,
                TimeUnit.MILLISECONDS
        );
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private void stopUpdatingCallbackWithPosition() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekBarPositionUpdateTask = null;
        }
    }

    private boolean isStatePlaying() {
        return isMediaPlayer()
                && mMediaPlayer.getPlaybackState() != Player.STATE_ENDED
                && mMediaPlayer.getPlaybackState() != Player.STATE_IDLE
                && mMediaPlayer.getPlayWhenReady();
    }

    private void updateProgressCallbackTask() {
        if (isStatePlaying()) {
            long currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void instantReset() {
        if (isMediaPlayer()) {
            if (mMediaPlayer.getCurrentPosition() < 5000) {
                skip(false);
            } else {
                resetSong();
            }
        }
    }

    private TinyDB db;
    private boolean setPlayWhenReady = true;

    /**
     * Once the {@link MediaPlayer} is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the {@link } the {@link MediaPlayer} is
     * released. Then in the onStart() of the {@link } a new {@link MediaPlayer}
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    @Override
    public void initMediaPlayer() {
        db = new TinyDB(mContext);

        /*try {
            if (isMediaPlayer()) {
                mMediaPlayer.reset();
            } else {
                mMediaPlayer = new MediaPlayer();

                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
                mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
                mMusicNotificationManager = mMusicService.getMusicNotificationManager();
            }
            tryToGetAudioFocus();
            mMediaPlayer.setDataSource(mSelectedSong.getFileUrl());
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if (mSelectedSong != null && !TextUtils.isEmpty(mSelectedSong.getFileUrl())) {
            //reset
            if (isMediaPlayer()) {
                mMediaPlayer.release();
            }


            //create new
            TrackSelector trackSelector = new DefaultTrackSelector();

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, mContext.getString(R.string.app_name)));

            MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mSelectedSong.getFileUrl()));

            mMediaPlayer = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory, trackSelector, new DefaultLoadControl());

            mMediaPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playWhenReady) {
                        startUpdatingCallbackWithPosition();
                        setStatus(PlaybackInfoListener.State.PLAYING);
                    }

                    switch (playbackState) {
                        case Player.STATE_ENDED://PLAY COMPLETED
                            if (mPlaybackInfoListener != null) {
                                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                                mPlaybackInfoListener.onPlaybackCompleted();
                            }

                            isReplaySong = db.getBoolean(TinyDB.IS_REPEAT);
                            if (isReplaySong) {
                                if (isMediaPlayer()) {
                                    resetSong();
                                }
                                isReplaySong = false;
                            } else {
                                isShuffle = db.getBoolean(TinyDB.IS_SHUFFLE);
                                if (isShuffle) {
                                    //random bai hat
                                    randomSong();
                                } else {
                                    //neu nhieu hon 1 bai thi moi tiep tuc qua bai moi
                                    if (mSongs != null && mSongs.size() > 1) {
                                        skip(true);
                                    } else {
                                        //reset state lai tu dau
                                        setPlayWhenReady = false;
                                        initMediaPlayer();
                                        setPlayWhenReady = true;
                                        pauseMediaPlayer();
                                    }
                                }

                            }
                            break;
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });

            mMediaPlayer.prepare(audioSource);

            mMediaPlayer.setPlayWhenReady(setPlayWhenReady);

            //state
            tryToGetAudioFocus();

            //notify
            mMusicNotificationManager = mMusicService.getMusicNotificationManager();
            mMusicService.startForeground(MusicNotificationManager.NOTIFICATION_ID, mMusicNotificationManager.createNotification());
        } else {
            MyUtils.showToast(mContext, "Media not found");
        }


    }


    @Override
    public final SimpleExoPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    /*@Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        startUpdatingCallbackWithPosition();
        setStatus(PlaybackInfoListener.State.PLAYING);
    }*/


    @Override
    public void release() {
        if (isMediaPlayer()) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            giveUpAudioFocus();
            unregisterActionsReceiver();
        }
    }

    @Override
    public boolean isPlaying() {
        return isStatePlaying();
    }

    @Override
    public void resumeOrPause() {

        if (isPlaying()) {
            pauseMediaPlayer();
        } else {
            resumeMediaPlayer();
        }
    }

    @Override
    public void pause() {
        if (isPlaying()) {
            pauseMediaPlayer();
        }
    }

    @Override
    public final @PlaybackInfoListener.State
    int getState() {
        return mState;
    }

    @Override
    public boolean isMediaPlayer() {
        return mMediaPlayer != null;
    }

    @Override
    public void reset() {
        isReplaySong = !isReplaySong;
    }

    @Override
    public boolean isReset() {
        return isReplaySong;
    }

    @Override
    public void skip(final boolean isNext) {
        getSkipSong(isNext);
    }

    private void getSkipSong(final boolean isNext) {
        final int currentIndex = mSongs.indexOf(mSelectedSong);

        int index;

        try {
            index = isNext ? currentIndex + 1 : currentIndex - 1;
            mSelectedSong = mSongs.get(index);
        } catch (IndexOutOfBoundsException e) {
            mSelectedSong = currentIndex != 0 ? mSongs.get(0) : mSongs.get(mSongs.size() - 1);
            e.printStackTrace();
        }

        initMediaPlayer();
    }


    private int getRandomNumber(int min, int max) {
        return (new Random()).nextInt((max - min) + 1) + min;
    }

    /**
     * Random tranh bai dang hat, lap toi da 10 lan de tim index moi, ko trung bai cu
     */
    private void randomSong() {
        final int currentIndex = mSongs.indexOf(mSelectedSong);

        int index = getRandomNumber(0, mSongs.size() - 1);
        if (index == currentIndex) {
            if (mSongs != null && mSongs.size() > 1) {
                for (int i = 0; i < 10; i++) {
                    index = getRandomNumber(0, mSongs.size() - 1);
                    if (index != currentIndex) {
                        break;
                    }
                }
            }
        }

        if (index < 0) index = 0;

        try {
            mSelectedSong = mSongs.get(index);
        } catch (IndexOutOfBoundsException e) {
            mSelectedSong = currentIndex != 0 ? mSongs.get(0) : mSongs.get(mSongs.size() - 1);
            e.printStackTrace();
        }
        initMediaPlayer();
    }

    @Override
    public void seekTo(final int position) {
        if (isMediaPlayer()) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public long getPlayerPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getPlayerDuration() {
        return mMediaPlayer.getDuration();
    }

    /**
     * Get the current volume: int currentvolume = player.getVolume();
     * Mute: player.setVolume(0f);
     * Unmute: player.setVolume(currentVolume);
     * <p>
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the MediaPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private void configurePlayerState() {

        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pauseMediaPlayer();
        } else {

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mMediaPlayer.setVolume(VOLUME_DUCK);
            } else {
                mMediaPlayer.setVolume(VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                resumeMediaPlayer();
                mPlayOnFocusGain = false;
            }
        }
    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action != null) {

                switch (action) {
                    case MusicNotificationManager.PREV_ACTION:
                        instantReset();
                        break;
                    case MusicNotificationManager.PLAY_PAUSE_ACTION:
                        resumeOrPause();
                        break;
                    case MusicNotificationManager.NEXT_ACTION:
                        skip(true);
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (mSelectedSong != null) {
                            pauseMediaPlayer();
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        if (mSelectedSong != null && !isPlaying()) {
                            resumeMediaPlayer();
                        }
                        break;
                    case Intent.ACTION_HEADSET_PLUG:
                        if (mSelectedSong != null) {
                            switch (intent.getIntExtra("state", -1)) {
                                //0 means disconnected
                                case 0:
                                    pauseMediaPlayer();
                                    break;
                                //1 means connected
                                case 1:
                                    if (!isPlaying()) {
                                        resumeMediaPlayer();
                                    }
                                    break;
                            }
                        }
                        break;
                    case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                        if (isPlaying()) {
                            pauseMediaPlayer();
                        }
                        break;
                }
            }
        }
    }
}
