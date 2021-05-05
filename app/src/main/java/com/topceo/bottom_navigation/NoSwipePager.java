package com.topceo.bottom_navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class NoSwipePager extends ViewPager {
    public NoSwipePager(@NonNull Context context) {
        super(context);
        this.enabled = true;
    }

    public NoSwipePager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    private boolean enabled;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
