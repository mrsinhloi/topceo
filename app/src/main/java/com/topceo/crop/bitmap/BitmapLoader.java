package com.topceo.crop.bitmap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class BitmapLoader {

	public static Bitmap load(Context context, int[] holderDimension, String image_url) {
		// Get the dimensions of the View
		int targetW = holderDimension[0];
		int targetH = holderDimension[1];

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;

		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(image_url, bmOptions);
		return bitmap;
	}
	/*public static Bitmap load(Context context, int[] holderDimension, String image_url) {
		try {
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			if (Build.VERSION.SDK_INT < 11)
	        {
				try {
					Field field = bitmapOptions.getClass().getField("inNativeAlloc");
					field.setBoolean(bitmapOptions, true);
				} catch (Exception e) {
				}
	        }
			bitmapOptions.inJustDecodeBounds = true;
			bitmapOptions.inScaled = false;
			BitmapFactory.decodeFile(image_url, bitmapOptions);

			int inSampleSize = 1;

			final int outWidth = bitmapOptions.outWidth;
			final int outHeight = bitmapOptions.outHeight;

			final int holderWidth = holderDimension[0];
			final int holderHeight = holderDimension[1];

			// Calculation inSampleSize
			if (outHeight > holderHeight || outWidth > holderWidth) {
				final int halfWidth = outWidth / 2;
				final int halfHeight = outHeight / 2;

				while ((halfHeight / inSampleSize) > holderHeight && (halfWidth / inSampleSize) > holderWidth) {
					inSampleSize *= 2;
				}
			}

			bitmapOptions.inSampleSize = inSampleSize;

			// Decoding bitmap
			bitmapOptions.inJustDecodeBounds = false;
			return BitmapProcessing.modifyOrientation(BitmapFactory.decodeFile(image_url, bitmapOptions), image_url);
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		return null;
	}*/

	public static Bitmap loadFromAsset(Context context, int[] holderDimension, String image_url) {
		Bitmap bm = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		if (Build.VERSION.SDK_INT < 11)
        {
			try {
				Field field = options.getClass().getField("inNativeAlloc");
				field.setBoolean(options, true);
			} catch (Exception e) {
			}
        }
		options.inJustDecodeBounds = true;
		options.inScaled = false;

		InputStream istr;

		AssetManager assetManager = context.getAssets();
		try {
			istr = assetManager.open(image_url);
			BitmapFactory.decodeStream(istr, null, options);
			istr.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError ofm) {
		}
		
		int inSampleSize = 1;

		final int outWidth = options.outWidth;
		final int outHeight = options.outHeight;

		final int holderWidth = holderDimension[0];
		final int holderHeight = holderDimension[1];

		// Calculation inSampleSize
		if (outHeight > holderHeight || outWidth > holderWidth) {
			final int halfWidth = outWidth / 2;
			final int halfHeight = outHeight / 2;

			while ((halfHeight / inSampleSize) > holderHeight && (halfWidth / inSampleSize) > holderWidth) {
				inSampleSize *= 2;
			}
		}

		options.inSampleSize = inSampleSize;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		try {
			istr = assetManager.open(image_url);
			bm = BitmapFactory.decodeStream(istr, null, options);
			istr.close();
		} catch (IOException e) {
		} catch (OutOfMemoryError ofm) {
		}

		return bm;
	}

	public static Bitmap load(Context context, int[] holderDimension, Uri image_uri) throws Exception {
		String image_url = UriToUrl.get(context, image_uri);
		if (image_url != null) {
			return load(context, holderDimension, image_url);
		}
		return null;
	}

	public static Bitmap load(Context context, String image_url) throws Exception {
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		if (Build.VERSION.SDK_INT < 11)
        {
			try {
				Field field = bitmapOptions.getClass().getField("inNativeAlloc");
				field.setBoolean(bitmapOptions, true);
			} catch (Exception e) {
			}
        }

		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image_url, bitmapOptions);

		final float imageSize = (float) bitmapOptions.outWidth * (float) bitmapOptions.outHeight * 4.0f / 1024.0f / 1024.0f; // MB

		bitmapOptions.inSampleSize = (int) Math.pow(2, Math.floor(imageSize / MemoryManagement.free(context)));
		bitmapOptions.inJustDecodeBounds = false;

		return BitmapProcessing.modifyOrientation(BitmapFactory.decodeFile(image_url, bitmapOptions), image_url);
	}

}
