package com.desmond.squarecamera;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.squarecamera.myproject.ChangeCamera;
import com.desmond.squarecamera.myproject.ImageSize;

import java.io.IOException;
import java.util.List;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback, Camera.PictureCallback, ChangeCamera {

    public static final String TAG = CameraFragment.class.getSimpleName();
    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_FLASH_KEY = "flash_mode";
    public static final String IMAGE_INFO = "image_info";

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private int mCameraID;
    private String mFlashMode;
    private Camera mCamera;
    private SquareCameraPreview mPreviewView;
    private SurfaceHolder mSurfaceHolder;

    private boolean mIsSafeToTakePhoto = false;

    private ImageParameters mImageParameters;

    private CameraOrientationListener mOrientationListener;

    private static CameraFragment fragment;
    public static CameraFragment newInstance() {
        if(fragment == null){
            fragment = new CameraFragment();
        }
        return fragment;
    }

    public CameraFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOrientationListener = new CameraOrientationListener(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore your state here because a double rotation with this fragment
        // in the backstack will cause improper state restoration
        // onCreate() -> onSavedInstanceState() instead of going through onCreateView()
        if (savedInstanceState == null) {
            mCameraID = getBackCameraID();
            mFlashMode = CameraSettingPreferences.getCameraFlashMode(getActivity());
            mImageParameters = new ImageParameters();
        } else {
            mCameraID = savedInstanceState.getInt(CAMERA_ID_KEY);
            mFlashMode = savedInstanceState.getString(CAMERA_FLASH_KEY);
            mImageParameters = savedInstanceState.getParcelable(IMAGE_INFO);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.squarecamera__fragment_camera, container, false);

    }

    private ImageView swapCameraBtn;
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOrientationListener.enable();
        mPreviewView = (SquareCameraPreview) view.findViewById(R.id.camera_preview_view);
        mPreviewView.getHolder().addCallback(CameraFragment.this);

        final View topCoverView = view.findViewById(R.id.cover_top_view);
        final View btnCoverView = view.findViewById(R.id.cover_bottom_view);

        mImageParameters.mIsPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mPreviewView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageParameters.mPreviewWidth = mPreviewView.getWidth();
                    mImageParameters.mPreviewHeight = mPreviewView.getHeight();

                    mImageParameters.mCoverWidth = mImageParameters.mCoverHeight
                            = mImageParameters.calculateCoverWidthHeight();

//                    Log.d(TAG, "parameters: " + mImageParameters.getStringValues());
//                    Log.d(TAG, "cover height " + topCoverView.getHeight());
                    resizeTopAndBtmCover(topCoverView, btnCoverView);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mPreviewView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        } else {
            if (mImageParameters.isPortrait()) {
                topCoverView.getLayoutParams().height = mImageParameters.mCoverHeight;
                btnCoverView.getLayoutParams().height = mImageParameters.mCoverHeight;
            } else {
                topCoverView.getLayoutParams().width = mImageParameters.mCoverWidth;
                btnCoverView.getLayoutParams().width = mImageParameters.mCoverWidth;
            }
        }

        swapCameraBtn = (ImageView) view.findViewById(R.id.change_camera);
        swapCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraID == CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraID = getBackCameraID();
                    isBackCamera = true;
                } else {
                    mCameraID = getFrontCameraID();
                    isBackCamera = false;
                }
                restartPreview();

                VideoFragment.newInstance().changeCamera(isBackCamera);
            }
        });

        final View changeCameraFlashModeBtn = view.findViewById(R.id.flash);
        changeCameraFlashModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
                    mFlashMode = Camera.Parameters.FLASH_MODE_ON;
                } else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
                    mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
                } else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
                    mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
                }

                setupFlashMode();
                setupCamera();
            }
        });
        setupFlashMode();

        final ImageView takePhotoBtn = (ImageView) view.findViewById(R.id.capture_image_button);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

    }


    private void setupFlashMode() {
        View view = getView();
        if (view == null) return;

        final TextView autoFlashIcon = (TextView) view.findViewById(R.id.auto_flash_icon);
        if (Camera.Parameters.FLASH_MODE_AUTO.equalsIgnoreCase(mFlashMode)) {
            autoFlashIcon.setText("Auto");
        } else if (Camera.Parameters.FLASH_MODE_ON.equalsIgnoreCase(mFlashMode)) {
            autoFlashIcon.setText("On");
        } else if (Camera.Parameters.FLASH_MODE_OFF.equalsIgnoreCase(mFlashMode)) {
            autoFlashIcon.setText("Off");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(CAMERA_ID_KEY, mCameraID);
        outState.putString(CAMERA_FLASH_KEY, mFlashMode);
        outState.putParcelable(IMAGE_INFO, mImageParameters);
        super.onSaveInstanceState(outState);
    }

    private void resizeTopAndBtmCover(final View topCover, final View bottomCover) {
        ResizeAnimation resizeTopAnimation
                = new ResizeAnimation(topCover, mImageParameters);
        resizeTopAnimation.setDuration(800);
        resizeTopAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        topCover.startAnimation(resizeTopAnimation);

        ResizeAnimation resizeBtmAnimation
                = new ResizeAnimation(bottomCover, mImageParameters);
        resizeBtmAnimation.setDuration(800);
        resizeBtmAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        bottomCover.startAnimation(resizeBtmAnimation);
    }

    private void releaseCameraAndPreview() {
        mPreviewView.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @SuppressWarnings("deprecation")
    private void getCamera(int cameraID) {
        try {
            releaseCameraAndPreview();
            if (cameraID == 0) {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
            } else {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
            }

//            mCamera = Camera.open(cameraID);
            mPreviewView.setCamera(mCamera);
        } catch (Exception e) {
            Log.d(TAG, "Can't open camera with id " + cameraID);
            e.printStackTrace();
        }
    }

    /**
     * Restart the camera preview
     */
    private void restartPreview() {
        if (mCamera != null) {
            stopCameraPreview();
            mCamera.release();
            mCamera = null;
        }

        getCamera(mCameraID);
        startCameraPreview();
    }

    /**
     * Start the camera preview
     */
    private void startCameraPreview() {

        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        determineDisplayOrientation();
        setupCamera();

        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();

                setSafeToTakePhoto(true);
                setCameraFocusReady(true);
            } catch (IOException e) {
                Log.d(TAG, "Can't start camera preview due to IOException " + e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop the camera preview
     */
    private void stopCameraPreview() {
        setSafeToTakePhoto(false);
        setCameraFocusReady(false);

        // Nulls out callbacks, stops face detection
        if(mCamera!=null) {
            mCamera.stopPreview();
            mPreviewView.setCamera(null);
        }
    }

    private void setSafeToTakePhoto(final boolean isSafeToTakePhoto) {
        mIsSafeToTakePhoto = isSafeToTakePhoto;
    }

    private void setCameraFocusReady(final boolean isFocusReady) {
        if (this.mPreviewView != null) {
            mPreviewView.setIsFocusReady(isFocusReady);
        }
    }

    /**
     * Determine the current display orientation and rotate the camera preview
     * accordingly
     */
    private void determineDisplayOrientation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCameraID, cameraInfo);

        // Clockwise rotation needed to align the window display to the natural position
        int degrees = 0;
        if(getActivity()!=null){
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

            switch (rotation) {
                case Surface.ROTATION_0: {
                    degrees = 0;
                    break;
                }
                case Surface.ROTATION_90: {
                    degrees = 90;
                    break;
                }
                case Surface.ROTATION_180: {
                    degrees = 180;
                    break;
                }
                case Surface.ROTATION_270: {
                    degrees = 270;
                    break;
                }
            }
        }

        int displayOrientation;

        // CameraInfo.Orientation is the angle relative to the natural position of the device
        // in clockwise rotation (angle that is rotated clockwise from the natural position)
        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            // Orientation is angle of rotation when facing the camera for
            // the camera image to match the natural orientation of the device
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        if(mImageParameters!=null) {
            mImageParameters.mDisplayOrientation = displayOrientation;
            mImageParameters.mLayoutOrientation = degrees;

            try {
                mCamera.setDisplayOrientation(mImageParameters.mDisplayOrientation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Setup the camera parameters
     */
    private void setupCamera() {
        try {

            if (mCamera != null && mCamera.getParameters() != null) {
                // Never keep a global parameters
                Camera.Parameters parameters = mCamera.getParameters();

                Size bestPreviewSize = determineBestPreviewSize(parameters);
                Size bestPictureSize = determineBestPictureSize(parameters);

                parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
                parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);


                // Set continuous picture focus, if it's supported
                if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }

                final View changeCameraFlashModeBtn = getView().findViewById(R.id.flash);
                List<String> flashModes = parameters.getSupportedFlashModes();
                if (flashModes != null && flashModes.contains(mFlashMode)) {
                    parameters.setFlashMode(mFlashMode);
                    changeCameraFlashModeBtn.setVisibility(View.VISIBLE);
                } else {
                    changeCameraFlashModeBtn.setVisibility(View.INVISIBLE);
                }

                // Lock in the changes
                mCamera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Size determineBestPreviewSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPreviewSizes(), PREVIEW_SIZE_MAX_WIDTH);
    }

    private Size determineBestPictureSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPictureSizes(), PICTURE_SIZE_MAX_WIDTH);
    }

    private Size determineBestSize(List<Size> sizes, int widthThreshold) {
        Size bestSize = null;
        Size size;
        int numOfSizes = sizes.size();
        for (int i = 0; i < numOfSizes; i++) {
            size = sizes.get(i);
            boolean isDesireRatio = (size.width / 4) == (size.height / 3);
            boolean isBetterSize = (bestSize == null) || size.width > bestSize.width;

            if (isDesireRatio && isBetterSize) {
                bestSize = size;
            }
        }

        if (bestSize == null) {
            Log.d(TAG, "cannot find the best camera size");
            return sizes.get(sizes.size() - 1);
        }

        return bestSize;
    }

    private int getFrontCameraID() {
        if(getActivity()!=null){
            PackageManager pm = getActivity().getPackageManager();
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                return CameraInfo.CAMERA_FACING_FRONT;
            }
        }

        return getBackCameraID();
    }

    private int getBackCameraID() {
        return CameraInfo.CAMERA_FACING_BACK;
    }

    /**
     * Take a picture
     */
    private void takePicture() {

        if (mIsSafeToTakePhoto) {
            setSafeToTakePhoto(false);

            mOrientationListener.rememberOrientation();

            // Shutter callback occurs after the image is captured. This can
            // be used to trigger a sound to let the user know that image is taken
            Camera.ShutterCallback shutterCallback = null;

            // Raw callback occurs when the raw image data is available
            Camera.PictureCallback raw = null;

            // postView callback occurs when a scaled, fully processed
            // postView image is available.
            Camera.PictureCallback postView = null;

            // jpeg callback occurs when the compressed image is available
            try {
                mCamera.takePicture(shutterCallback, raw, postView, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStop() {
        mOrientationListener.disable();

        // stop the preview
        if (mCamera != null) {
            stopCameraPreview();
            mCamera.release();
            mCamera = null;
        }

        CameraSettingPreferences.saveCameraFlashMode(getActivity(), mFlashMode);

        super.onStop();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCamera(mCameraID);
//                startCameraPreview();
            }
        }, 1000);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // The surface is destroyed with the visibility of the SurfaceView is set to View.Invisible
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case 1:
                Uri imageUri = data.getData();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * A picture has been taken
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        final int rotation = getPhotoRotation();


//        Log.d(TAG, "normal orientation: " + orientation);
//        Log.d(TAG, "Rotate Picture by: " + rotation);
        /*getFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        EditSavePhotoFragment.newInstance(data, rotation, mImageParameters.createCopy()),
                        EditSavePhotoFragment.TAG)
                .addToBackStack(null)
                .commit();*/
        /*Intent intent=new Intent(getActivity(), PreviewImageActivity.class);
        ImageUtility.data=data;
        ImageUtility.rotation=rotation;
        ImageUtility.parameters=mImageParameters.createCopy();
        getActivity().startActivity(intent);*/

        setSafeToTakePhoto(true);

        //////////////////////////////////////////////////////
        if(getActivity()!=null && !getActivity().isFinishing()){
            new AsyncTask<Void, Void, Void>() {
                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(getActivity(), R.style.squarecamera__Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getText(R.string.squarecamera__processing));
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    //luu anh xuong bo nho
                    ImageUtility.decodeSampledBitmapFromByte(getActivity(), data, rotation, ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_WIDTH);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    progressDialog.dismiss();
                    //tro ve man hinh chinh
                    if (getActivity().getParent() == null) {
                        getActivity().setResult(getActivity().RESULT_OK);
                    } else {
                        getActivity().getParent().setResult(getActivity().RESULT_OK);
                    }
                    getActivity().finish();
                }
            }.execute();
        }


    }

    private int getPhotoRotation() {
        int rotation;
        int orientation = mOrientationListener.getRememberedNormalOrientation();
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(mCameraID, info);

        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {
            rotation = (info.orientation + orientation) % 360;
        }

        return rotation;
    }


    /**
     * When orientation changes, onOrientationChanged(int) of the listener will be called
     */
    private static class CameraOrientationListener extends OrientationEventListener {

        private int mCurrentNormalizedOrientation;
        private int mRememberedNormalOrientation;

        public CameraOrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != ORIENTATION_UNKNOWN) {
                mCurrentNormalizedOrientation = normalize(orientation);
            }
        }

        /**
         * @param degrees Amount of clockwise rotation from the device's natural position
         * @return Normalized degrees to just 0, 90, 180, 270
         */
        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;
            }

            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        public void rememberOrientation() {
            mRememberedNormalOrientation = mCurrentNormalizedOrientation;
        }

        public int getRememberedNormalOrientation() {
            rememberOrientation();
            return mRememberedNormalOrientation;
        }
    }




    //https://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
//            Toast.makeText(getContext(), "resume "+isVisibleToUser, Toast.LENGTH_SHORT).show();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startCameraPreview();
                    if (mCamera == null) {
                        restartPreview();
                    }
                }
            }, 500);




        } else if (isVisibleToUser) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
//            Toast.makeText(getContext(), "onCreated "+isVisibleToUser, Toast.LENGTH_SHORT).show();
        } else if (!isVisibleToUser && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
//            Toast.makeText(getContext(), "go out "+isVisibleToUser, Toast.LENGTH_SHORT).show();

            //khi qua tab khac, neu camera dang la phia truoc thi reset ve sau
            if (mCameraID == CameraInfo.CAMERA_FACING_FRONT) {
                mCameraID = getBackCameraID();
                isBackCamera = true;
                restartPreview();
            }

        }

    }

    private boolean isBackCamera = true;

    @Override
    public void changeCamera(boolean isBackCamera) {
        this.isBackCamera = isBackCamera;
        if(isBackCamera){
            mCameraID = getBackCameraID();
        }else{
            mCameraID= getFrontCameraID();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCameraPreview();
    }
}
