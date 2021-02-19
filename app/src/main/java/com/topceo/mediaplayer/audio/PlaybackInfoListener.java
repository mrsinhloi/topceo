package com.topceo.mediaplayer.audio;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//I have included the playback listener class to listen to position changes while song is playing.
// The class is important for the Seekbar to work and in updating UI whether a song is playing or paused.
public abstract class PlaybackInfoListener {
    public void onPositionChanged(final long position) {
    }

    public void onStateChanged(final @State int state) {
    }

    public void onPlaybackCompleted() {
    }

    @IntDef({State.INVALID, State.PLAYING, State.PAUSED, State.COMPLETED, State.RESUMED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int INVALID = -1;
        int PLAYING = 0;
        int PAUSED = 1;
        int COMPLETED = 2;
        int RESUMED = 3;
    }
}
