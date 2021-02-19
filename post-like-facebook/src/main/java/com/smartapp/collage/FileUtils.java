package com.smartapp.collage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static Uri parseUriImage(Uri picUri, Context context) {
        try {
            if (picUri.toString().contains("content://com.google.android.apps.photos.contentprovider")) {
                //Intent { dat=content://com.google.android.apps.photos.contentprovider/0/1/content://media/external/images/media/3476/ORIGINAL/NONE/598088098 flg=0x1 launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 } clip={text/uri-list U:content://com.google.android.apps.photos.contentprovider/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F3476/ORIGINAL/NONE/598088098} }
                picUri = getImageUrlWithAuthority(context, picUri);
                //return //content://media/external/images/media/74275
            }

            if (picUri.toString().contains("content://com.miui.gallery.open")) {
                //Intent { dat=content://com.google.android.apps.photos.contentprovider/0/1/content://media/external/images/media/3476/ORIGINAL/NONE/598088098 flg=0x1 launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 } clip={text/uri-list U:content://com.google.android.apps.photos.contentprovider/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F3476/ORIGINAL/NONE/598088098} }
                picUri = getImageUrlWithAuthority(context, picUri);
                //return //content://media/external/images/media/74275
            }

            //content://com.mi.android.globalFileexplorer.myprovider/external_files/DCIM/Screenshots/Screenshot_2018-04-29-13-26-01-752_com.android.contacts.png
            if (picUri.toString().contains("content://com.mi.android.globalFileexplorer.myprovider")) {
                //Intent { dat=content://com.google.android.apps.photos.contentprovider/0/1/content://media/external/images/media/3476/ORIGINAL/NONE/598088098 flg=0x1 launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 } clip={text/uri-list U:content://com.google.android.apps.photos.contentprovider/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F3476/ORIGINAL/NONE/598088098} }
                picUri = getImageUrlWithAuthority(context, picUri);
                //return //content://media/external/images/media/74275
            }


            //content://media/external/images/media/74275
            if (picUri.toString().contains("content://media/")) {
                String url = getRealPathFromUri(context, picUri);
                if (!TextUtils.isEmpty(url)) {
                    File f = new File(url);
                    picUri = Uri.fromFile(f);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picUri;
    }

    //google photos
    private static Uri getImageUrlWithAuthority(Context context, Uri uri) {

        /*final int takeFlags = getIntent().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ContentResolver resolver = context.getContentResolver();
        resolver.takePersistableUriPermission(uri, takeFlags);*/

       /* try {
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Get the URI of the selected image from {getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data, Context context) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //replace by
            if (data != null && data.getData() != null) {
                return data.getData();
            } else {
                return getCaptureImageOutputUri(context);
            }
        } else {
            return isCamera ? getCaptureImageOutputUri(context) : data.getData();
        }
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
