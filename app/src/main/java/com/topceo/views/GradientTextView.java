package com.topceo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.topceo.R;

public class GradientTextView extends androidx.appcompat.widget.AppCompatTextView {
    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context,
                            AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed,
                            int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getPaint().setShader(new LinearGradient(
                    0, 0, getWidth(), getHeight(),
                    ContextCompat.getColor(getContext(),
                            R.color.sky_end),
                    ContextCompat.getColor(getContext(),
                            R.color.sky_start),
                    Shader.TileMode.CLAMP));
        }
    }
}
