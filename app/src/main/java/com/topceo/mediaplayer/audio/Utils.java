package com.topceo.mediaplayer.audio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaMetadataRetriever;

import com.topceo.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

//https://revosleap.com/2019/02/14/making-simple-music-player/
public class Utils {

    public static Bitmap songArt(String path, Context context) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        InputStream inputStream;
        retriever.setDataSource(path);
        if (retriever.getEmbeddedPicture() != null) {
            inputStream = new ByteArrayInputStream(retriever.getEmbeddedPicture());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            retriever.release();
            return bitmap;
        } else {
            return getLargeIcon(context);
        }
    }

    public static Bitmap getLargeIcon(Context context) {

        final VectorDrawable vectorDrawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            vectorDrawable = (VectorDrawable)context.getDrawable(R.drawable.music_notification);
            final int largeIconSize = context.getResources().getDimensionPixelSize(R.dimen.notification_large_dim);
            final Bitmap bitmap = Bitmap.createBitmap(largeIconSize, largeIconSize, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);

            if (vectorDrawable != null) {
                vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                vectorDrawable.setAlpha(100);
                vectorDrawable.draw(canvas);
            }
            return bitmap;
        }else{
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
            return icon;
        }

//        Drawable myDrawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
//        Bitmap anImage = ((BitmapDrawable) myDrawable).getBitmap();

    }
}
