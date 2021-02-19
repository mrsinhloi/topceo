package com.topceo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.widget.RadioButton;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtil {

    public static final DiskCacheStrategy cacheType= DiskCacheStrategy.ALL;

    Context context;
    private SharedPreferences mPrefs;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    public static int SIZEIMG = 480;
    private BitmapFactory.Options options;
    private Bitmap original;
    private boolean issetprofile = false;
    private RadioButton rbmale, rbfemale;
    private Activity acti;
    public ImageUtil _instance;

    private ImageUtil() {
    }

    public ImageUtil getInstance() {
        return _instance;
    }

    public Bitmap decodeFile(File f) {
    	/*try {
    		System.runFinalization();
    	    Runtime.getRuntime().gc();
			System.gc();
		} catch (Exception e) {}*/
        Bitmap b = null;
        FileInputStream fis = null;
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inScaled = false;
			o.inPurgeable = true;
			o.inInputShareable = true;
			
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            fis = null;

            int scale = 1;
            if (o.outHeight > SIZEIMG || o.outWidth > SIZEIMG) {
                scale = (int) Math.pow(2.0,
                        (int) Math.round(Math.log(SIZEIMG / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inScaled = false;
			o2.inPurgeable = true;
			o2.inInputShareable = true;
			
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
            fis = null;
        } catch (IOException e) {
        }
        catch (OutOfMemoryError e) {
	    }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
                fis = null;
            }
        }
        return b;
    }

    public float rotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = { Images.ImageColumns.ORIENTATION };
            Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
            }
        }
        return 0f;
    }

    public float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap fitImage(Bitmap baseImage, Bitmap.Config config, boolean doRecycleBase) {
    	try {
			System.gc();
		} catch (Exception e) {}
    	try{
	        int width = SIZEIMG;
	        int height = SIZEIMG;
	        if (baseImage == null) {
	            throw new RuntimeException("baseImage is null");
	        }
	        if (config == null) {
	            config = Bitmap.Config.ARGB_8888;
	        }
	        Point resizedP = calculateFitImage(baseImage, width, height, null);
	
	        Bitmap resizedBitmap = null;
			try {
				resizedBitmap = Bitmap.createScaledBitmap(baseImage, resizedP.x, resizedP.y, true);
			} catch (OutOfMemoryError e) {
			}
	
	        if (doRecycleBase) {// to avoid memory error
	            baseImage.recycle();
	        }
	
	        Bitmap returnBitmap = Bitmap.createBitmap(width, height, config);
	        Canvas canvas = new Canvas(returnBitmap);
	        canvas.setDensity(Bitmap.DENSITY_NONE);
	        canvas.drawBitmap(resizedBitmap, (width - resizedP.x), (height - resizedP.y), null);
	        resizedBitmap.recycle();
	
	        return returnBitmap;
    	}catch(OutOfMemoryError oom){}
    	return null;
    }

    public Point calculateFitImage(Bitmap baseImage, int width, int height, Point receiver) {
        if (baseImage == null) {
            throw new RuntimeException("baseImage is null");
        }
        if (receiver == null) {
            receiver = new Point();
        }
        int dw = width;
        int dh = height;

        if (dw != 0 && dh != 0) {
            double waspect = (double) dw / baseImage.getWidth();
            double haspect = (double) dh / baseImage.getHeight();
            if (waspect > haspect) {// fit h
                dw = (int) (baseImage.getWidth() * haspect);
            } else {
                dh = (int) (baseImage.getHeight() * waspect);
            }
        }
        receiver.x = dw;
        receiver.y = dh;
        return receiver;
    }

}
