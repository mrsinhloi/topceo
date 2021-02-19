package com.workchat.core.chat.link_preview_2;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;
import com.workchat.corechat.R;

/**
 * Created by MrPhuong on 1/31/2018.
 */

public class ResizeTransform implements Transformation {
    Context context;
    int imgPreviewSize = 100;

    public ResizeTransform(Context context) {
        this.context = context;
        imgPreviewSize = context.getResources().getDimensionPixelSize(R.dimen.image_preview_size);
    }

    @Override
    public Bitmap transform(Bitmap source) {

        float ratio = (float) source.getHeight() / (float) source.getWidth();
        float heightFloat = ((float) imgPreviewSize) * ratio;
        Bitmap result = Bitmap.createScaledBitmap(source, imgPreviewSize, (int) heightFloat, false);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "square()";
    }
}
