package com.topceo.viewholders;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;

/**
 * Admob
 */
public class ViewHolder3 extends RecyclerView.ViewHolder {
    // each data item is just a string in this case

    public LinearLayout adContainer;
//            NativeExpressAdView adView;

    public ViewHolder3(View v) {
        super(v);
        adContainer = (LinearLayout) v.findViewById(R.id.adContainer);
//                adView=(NativeExpressAdView)v.findViewById(adView);
    }
}
