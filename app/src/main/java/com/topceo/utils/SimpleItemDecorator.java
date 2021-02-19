package com.topceo.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by MrPhuong on 2017-02-02.
 */

public class SimpleItemDecorator extends RecyclerView.ItemDecoration {

    private int spacing;

    public SimpleItemDecorator(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = spacing;
    }
}
