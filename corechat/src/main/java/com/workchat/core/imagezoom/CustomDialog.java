package com.workchat.core.imagezoom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Window;
import android.widget.ImageView;

import com.workchat.corechat.R;


public class CustomDialog {
    public static void show(Context context, Bitmap bitmap) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_zoom);

        ImageView imageView = dialog.findViewById(R.id.imageview);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }
}
