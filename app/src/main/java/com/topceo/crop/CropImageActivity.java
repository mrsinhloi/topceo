package com.topceo.crop;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.desmond.squarecamera.myproject.APIConstants;
import com.desmond.squarecamera.myproject.ImageUtils;
import com.edmodo.cropper.CropImageView;
import com.topceo.R;
import com.topceo.crop.bitmap.BitmapLoader;
import com.topceo.crop.utils.Util;
import com.topceo.objects.image.ImageSize;

import java.io.IOException;
import java.io.OutputStream;


/**
 * The activity can crop specific region of interest from an image.
 */

public class CropImageActivity extends MonitoredActivity {

    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    private final Handler mHandler = new Handler();
    
    private CropImageView   mImageView;
    private Bitmap mBitmap;
    
    private ContentResolver mContentResolver;

    @Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
    	setContentView(R.layout.layout_cropimage);
    	mContentResolver = getContentResolver();
        mImageView = (CropImageView) findViewById(R.id.CropImageView);


        mBitmap = BitmapLoader.load(CropImageActivity.this, new int[] {
                ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT}, APIConstants.SELECTED_IMAGE.getPath());
        
        /*if (mBitmap.getWidth() > APIConstants.SIZEIMGUPLOAD || mBitmap.getHeight() > APIConstants.SIZEIMGUPLOAD) {
			if (mBitmap.getWidth() > mBitmap.getHeight()) {
				mBitmap = Util.resizeImage(mBitmap, APIConstants.SIZEIMGUPLOAD, APIConstants.SIZEIMGUPLOAD * mBitmap.getHeight() / mBitmap.getWidth(), false);
			} else {
				mBitmap = Util.resizeImage(mBitmap, APIConstants.SIZEIMGUPLOAD  * mBitmap.getWidth() / mBitmap.getHeight(), APIConstants.SIZEIMGUPLOAD, false);
			}
		}*/


        mImageView.setAspectRatio(ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT);

        if (mBitmap == null) {
            finish();
            return;
        }
        mImageView.setFixedAspectRatio(true);
        mImageView.setImageBitmap(mBitmap);


        findViewById(R.id.discard).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

        findViewById(R.id.save).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        try {
                            onSaveClicked();
                        } catch (Exception e) {
                            finish();
                        }
                    }
                });
        findViewById(R.id.rotateLeft).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                    	mImageView.rotateImage(-90);
                    }
                });

        findViewById(R.id.rotateRight).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                    	mImageView.rotateImage(90);
                    }
                });
    }

	private void onSaveClicked() throws Exception {

        final Bitmap b = mImageView.getCroppedImage();
        Util.startBackgroundJob(this, null, getString(R.string.saving_image),
                new Runnable() {
                    public void run() {

                        saveOutput(b);
                    }
                }, mHandler);
    }
    
    private void saveOutput(Bitmap croppedImage) {
    	croppedImage = Util.resizeImage(croppedImage, ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT, croppedImage.getConfig(), false);
    	
        OutputStream outputStream = null;
        try {
            outputStream = mContentResolver.openOutputStream(APIConstants.SELECTED_IMAGE);
            if (outputStream != null) {
                croppedImage.compress(mOutputFormat, ImageUtils.IMAGE_QUALITY, outputStream);
            }
        } catch (IOException ex) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        } finally {
        	if (outputStream != null) {
	        	try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
        }

        
        croppedImage.recycle();
        croppedImage = null;
        
        mBitmap.recycle();
        mBitmap = null;

        setResult(RESULT_OK);
        finish();
    }
    
    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

}


