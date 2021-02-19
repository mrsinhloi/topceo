package com.topceo.imagezoom;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

//https://github.com/felixsoares/ImageZoom
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
    public void onClick(View view) {
        if (getDrawable() != null)
            CustomDialog.show(getContext(), ((BitmapDrawable) getDrawable()).getBitmap());
    }
}
