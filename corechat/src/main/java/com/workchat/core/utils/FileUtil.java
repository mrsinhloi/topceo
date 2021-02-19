package com.workchat.core.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * http://qingmicity.cn/2019/09/05/文件Uri解析/
 */
public class FileUtil {

    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }

        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }

        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //如果是系统自带五种provider之一
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            } else if (isMiGlobalFileDocument(uri)){
                //小米国际文件管理器
                int length = "/external_files" .length();
                path = Environment.getExternalStorageDirectory() + uri.getPath().substring(length, uri.getPath().length());
                return path;
            } else if (isMiuiGallery(uri)){
                path = uri.getLastPathSegment();
                return path;
            }else if (uri.getAuthority().equals("media")){
                path = sortUri(context, uri);
                return path;
            } else if (isGooglePhotosUri(uri)){
                //Google相册图片
                Uri contentUri = null;
                List<String> segments = uri.getPathSegments();
                for (String segment : segments){
                    if (segment.contains("content")){
                        contentUri = Uri.parse(segment);
                        break;
                    }
                }
                if (contentUri != null){
                    path = sortUri(context, contentUri);
                }
                return path;
            }/* else if (isGoogleDriveUri(uri)){

            }*/else{
                try {
                    path = getFilePathFromURI(context, uri);
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String sortUri(Context context, Uri uri){
        String path = null;
        String id = uri.getLastPathSegment();
        Uri contentUri = null;
        if (uri.getPath().contains("images")){
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (uri.getPath().contains("video")){
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if (uri.getPath().contains("audio")){
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        if (contentUri != null){
            path = getDataColumn(context, contentUri, "_id = ?", new String[]{id});
        } else {
            //根据id查询文件(不限类)
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, "_id = ?", new String[]{id}, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int index = cursor.getColumnIndexOrThrow("_data");
                path = cursor.getString(index);
            }
            cursor.close();
        }
        return path;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isMiGlobalFileDocument(Uri uri){
        return "com.mi.android.globalFileexplorer.myprovider".equals(uri.getAuthority());
    }

    /**
     * com.miui.gallery.open
     * content://com.miui.gallery.open/raw/%2Fstorage%2Femulated%2F0%2FDCIM%2FCamera%2FIMG_20191112_093031.jpg
     * /storage/emulated/0/DCIM/Camera/IMG_20191112_093031.jpg
     * @param uri
     * @return
     */
    private static boolean isMiuiGallery(Uri uri){
        return "com.miui.gallery.open".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri){
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    private static boolean isGoogleDriveUri(Uri uri){
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }


    ///
    private static String getFilePathFromURI(Context context, Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }



}
