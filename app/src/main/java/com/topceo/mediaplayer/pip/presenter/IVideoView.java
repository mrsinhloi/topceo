/*
 * Created on 2020-12-10 10:06:03 AM.
 * Copyright © 2020 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip.presenter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.mediaplayer.pip.bean.Video;


/**
 * @author 刘振林
 */
public interface IVideoView extends IView<IVideoPresenter> {

    void setResult(int resultCode, Intent data);

    void setVideoToPlay(@NonNull Video video);

    void seekPositionOnVideoStarted(int position);

    int getPlayingVideoProgress();

    void notifyPlaylistSelectionChanged(int oldPosition, int position, boolean checkNewItemVisibility);

    @NonNull
    PlaylistViewHolder newPlaylistViewHolder(@NonNull ViewGroup parent);

    abstract class PlaylistViewHolder extends RecyclerView.ViewHolder {
        protected PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void loadVideoThumb(@NonNull Video video);
        public abstract void cancelLoadingVideoThumb();
        public abstract void setVideoTitle(@Nullable String text);
        public abstract void setVideoProgressAndDurationText(@Nullable String text);
        public abstract void highlightItemIfSelected(boolean selected);
    }
}
