package com.topceo.mediaplayer.audio;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.appcompat.widget.AppCompatImageView;

import com.topceo.R;

public class ImageViewSpinning extends AppCompatImageView {

    public ImageViewSpinning(Context context) {
        super(context);
    }

    public ImageViewSpinning(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewSpinning(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private static final int ROTATE_ANIMATION_DURATION = 800;
    private static final int IMAGE_RESOURCE_ID = R.drawable.no_media_small;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Set and scale the image
        setImageResource(IMAGE_RESOURCE_ID);

        // Start the animation
        startAnimation();
    }

    /**
     * Starts the rotate animation.
     */
    private void startAnimation() {
        clearAnimation();

        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(ROTATE_ANIMATION_DURATION);
        rotate.setRepeatCount(Animation.INFINITE);
        startAnimation(rotate);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            startAnimation();
        } else {
            clearAnimation();
        }
    }
}
