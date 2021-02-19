package com.topceo.mediaplayer.audio;

import androidx.annotation.NonNull;

import com.topceo.shopping.Media;
import com.topceo.shopping.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

//To ease interaction between mediaplayer, service , fragments/activities,
// I have used an interface player adapter whose functions are pretty easy to understand.
public interface PlayerAdapter {
    void initMediaPlayer();

    void release();

    boolean isMediaPlayer();

    boolean isPlaying();

    void resumeOrPause();

    void pause();

    void reset();

    boolean isReset();

    void instantReset();

    void skip(final boolean isNext);

    void seekTo(final int position);

    void setPlaybackInfoListener(final PlaybackInfoListener playbackInfoListener);

    MediaItem getCurrentSong();
    int getCurrentPosition();
    Media getAlbum();
    void setAlbum(@NonNull Media album);

    @PlaybackInfoListener.State
    int getState();

    long getPlayerPosition();
    long getPlayerDuration();

    void registerNotificationActionsReceiver(final boolean isRegister);


    void setCurrentSong(@NonNull final MediaItem song, @NonNull final ArrayList<MediaItem> songs);
    void setCurrentSong(@NonNull final long mediaId);

    SimpleExoPlayer getMediaPlayer();

    void onPauseActivity();

    void onResumeActivity();
}
