package com.topceo.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.mianamiana.view.MianaCropView;

/**
 * Created by MrPhuong on 2016-08-03.
 */
public class CropImageBehavior extends CoordinatorLayout.Behavior<MianaCropView> {
    public CropImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, MianaCropView child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
//        return dependency instanceof LinearLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, MianaCropView child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
//        return true;
    }
}
