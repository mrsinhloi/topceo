package com.topceo.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.topceo.R;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class ProgressUtils {

    public static ACProgressFlower dialog;

    public static void show(Context context) {
        if (context != null) {
            hide();
            dialog = new ACProgressFlower.Builder(context)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
//                    .text("Loading...")
                    .fadeColor(Color.TRANSPARENT).build();//DKGRAY
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    public static void show(Context context, String message) {
        if (context != null && !TextUtils.isEmpty(message)) {
            hide();
            int size = context.getResources().getDimensionPixelSize(R.dimen.dialog_text_size);
            dialog = new ACProgressFlower.Builder(context)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text(message)
                    .textSize(size)
                    .fadeColor(Color.TRANSPARENT).build();//DKGRAY
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    public static void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
