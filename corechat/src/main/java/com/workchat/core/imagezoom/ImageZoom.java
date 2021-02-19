package com.workchat.core.imagezoom;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class ImageZoom extends AppCompatImageView implements View.OnClickListener {

    public ImageZoom(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public ImageZoom(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public ImageZoom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Drawable d = getDrawable();
        if (d != null) {
            if (d instanceof BitmapDrawable) {
                CustomDialog.show(getContext(), ((BitmapDrawable) getDrawable()).getBitmap());
            }
        }
    }
}
