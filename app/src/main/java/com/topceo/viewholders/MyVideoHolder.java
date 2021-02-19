package com.topceo.viewholders;

import android.view.View;

public abstract class MyVideoHolder extends ViewHolderBasic {
    public MyVideoHolder(View v, int avatarSize) {
        super(v, avatarSize);
    }

    public abstract View getVideoLayout();

    public abstract void playVideo();

    public abstract void stopVideo();
}
