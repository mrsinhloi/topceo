package com.desmond.squarecamera.myproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MrPhuong on 2016-07-29.
 */
public class ImageUtils {
    public static final int IMAGE_QUALITY=80;
    ////////////////////////////////////////////////////////////////////
    private static Pattern pattern;
    private static Matcher matcher;
    public static boolean isImageUrl(String image) {
        if (TextUtils.isEmpty(image)) return false;
        String regex = "([^\\s]+(\\.(?i)(/bmp|jpg|gif|png|jpeg))$)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(image);
        return matcher.matches();
    }
    ////////////////////////////////////////////////////////////////////
    /**
     * Kiem tra hinh da rotation bao nhieu do
     *
     * @param path
     * @return
     */
    public static int getRotationForImage(String path) {
        int rotation = 0;

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rotation;
    }
    ////////////////////////////////////////////////////////////////////
    /**
     * @param mp
     * @return image path save
     */
    public static String saveBitmap(Bitmap mp, String picturePath) {
        Bitmap bitmap;
        OutputStream output;

        bitmap = mp;
        try {

            output = new FileOutputStream(picturePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, ImageUtils.IMAGE_QUALITY, output);//70:size 167Kb, 100:size 1.54Mb
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picturePath;
    }
    ////////////////////////////////////////////////////////////////////
    public static Bitmap handleCameraPhotoCamera(String path, int rotationToRoot, int targetWidth, int targetHeight) {
        Bitmap scaledBitmap = null;
        scaledBitmap = decodeFile(path, targetWidth, targetHeight);

        try {
            File f = new File(path);
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //neu hinh bi xoay thi xoay lai
        if (rotationToRoot != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationToRoot);
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        }

        return scaledBitmap;
    }
    public static Bitmap decodeFile(String path, int targetWidth, int targetHeight) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(path), null, o);
            //Find the correct scale value. It should be the power of 2.

            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = calculateInSampleSize(o, targetWidth, targetHeight);
            /*
            final int REQUIRED_SIZE=480;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }*/

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }


    ////////////////////////////////////////////////////////////////////
    public static void saveBitmapTemp(Bitmap bmtmp, int rotation){
        try {
            //neu hinh bi xoay thi xoay lai
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                bmtmp = Bitmap.createBitmap(bmtmp, 0, 0, bmtmp.getWidth(), bmtmp.getHeight(), matrix, false);
            }

            //luu xuong
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bmtmp.compress(APIConstants.mOutputFormat, ImageUtils.IMAGE_QUALITY, bytes);

            File f = new File(APIConstants.SELECTED_IMAGE.getPath());
            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            // remember close de FileOutput
            fo.flush();
            fo.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            if(bmtmp!=null){
                bmtmp.recycle();
                bmtmp = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////
}
