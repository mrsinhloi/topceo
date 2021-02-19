package com.topceo.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;

public class ViewHolder4 extends RecyclerView.ViewHolder {

    public TextView txt1, txt2, txt3, txt4;
    public AdIconView native_ad_icon;
    public LinearLayout linear1, linearRoot;
    public MediaView mediaView1;
    public Button btn1;

    public ViewHolder4(View v) {
        super(v);

        txt1 = (TextView) v.findViewById(R.id.fTextView1);
        txt2 = (TextView) v.findViewById(R.id.fTextView2);
        txt3 = (TextView) v.findViewById(R.id.fTextView3);
        txt4 = (TextView) v.findViewById(R.id.fTextView4);

        native_ad_icon = (AdIconView) v.findViewById(R.id.native_ad_icon);
        linear1 = (LinearLayout) v.findViewById(R.id.fLinearLayout1);
        linearRoot = (LinearLayout) v.findViewById(R.id.fLinearRoot);
        mediaView1 = (MediaView) v.findViewById(R.id.fMediaView1);
        btn1 = (Button) v.findViewById(R.id.fButton1);

    }
}
