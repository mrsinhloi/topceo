package com.desmond.squarecamera;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.desmond.squarecamera.myproject.APIConstants;
import com.desmond.squarecamera.myproject.ChangeCamera;
import com.desmond.squarecamera.myproject.ImageSize;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Audio;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.size.AspectRatio;
import com.otaliastudios.cameraview.size.SizeSelector;
import com.otaliastudios.cameraview.size.SizeSelectors;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class VideoFragment extends Fragment implements ChangeCamera {

    public static final String TAG = VideoFragment.class.getSimpleName();
    public static final String CAMERA_ID_KEY = "camera_id";
    public static final String CAMERA_FLASH_KEY = "flash_mode";
    public static final String IMAGE_INFO = "image_info";

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private String mFlashMode;
    private CameraView camera;
    private ProgressBar progressBar;

    private boolean mIsSafeToTakePhoto = false;

    static VideoFragment fragment;

    public static VideoFragment newInstance() {
        if (fragment == null) {
            fragment = new VideoFragment();
        }
        return fragment;
    }

    public VideoFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore your state here because a double rotation with this fragment
        // in the backstack will cause improper state restoration
        // onCreate() -> onSavedInstanceState() instead of going through onCreateView()
        if (savedInstanceState == null) {
            mFlashMode = CameraSettingPreferences.getCameraFlashMode(getActivity());
        } else {
            mFlashMode = savedInstanceState.getString(CAMERA_FLASH_KEY);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.squarecamera__fragment_video, container, false);

    }


    public static int getScreenWidth(Context context) {
        int widthScreen = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            widthScreen = size.x;
        } else {
            widthScreen = display.getWidth();  // Deprecated
        }
        return widthScreen;
    }

    public static int getScreenHeight(Context context) {
        int heightScreen = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT > 12) {
            Point size = new Point();
            display.getSize(size);
            heightScreen = size.y;
        } else {
            heightScreen = display.getHeight();
        }
        return heightScreen;
    }

    @Override
    public void onResume() {
        super.onResume();
        camera.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }

    private TinyDB db;
    private ImageView imgSound;
    private boolean isHaveSound = true;

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        progressBar.getProgressDrawable().setColorFilter(
                Color.parseColor("#ffeb3b"), android.graphics.PorterDuff.Mode.SRC_IN);


        final ImageView swapCameraBtn = (ImageView) view.findViewById(R.id.change_camera);
        swapCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCamera();

            }
        });

        final View changeCameraFlashModeBtn = view.findViewById(R.id.flash);
        changeCameraFlashModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
                    mFlashMode = Camera.Parameters.FLASH_MODE_ON;
                    camera.setFlash(Flash.ON);
                } else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
                    mFlashMode = Camera.Parameters.FLASH_MODE_OFF;
                    camera.setFlash(Flash.OFF);
                } else if (mFlashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
                    mFlashMode = Camera.Parameters.FLASH_MODE_AUTO;
                    camera.setFlash(Flash.AUTO);
                }

//                setTextTimeDuration();

            }
        });
//        setTextTimeDuration();

        takePhotoBtn = (ImageView) view.findViewById(R.id.capture_image_button);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureVideo();
            }
        });

        createCamera(savedInstanceState);


        //sound
        db = new TinyDB(getContext());
        isHaveSound = db.getBoolean(TinyDB.IS_HAVE_SOUND, true);
        imgSound = (ImageView) view.findViewById(R.id.imgSound);
        changeImageSound(false);
        imgSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (camera == null) return;
                if (camera.isTakingPicture() || camera.isTakingVideo()) return;

                isHaveSound = !isHaveSound;
                changeImageSound(true);
                db.putBoolean(TinyDB.IS_HAVE_SOUND, isHaveSound);

            }
        });

    }

    private void changeImageSound(boolean isShowToast) {
        if (isHaveSound) {
            imgSound.setImageResource(R.drawable.ic_volume_down_white_24dp);
            camera.setAudio(Audio.ON);
            if (isShowToast) {
                Toast.makeText(getContext(), R.string.squarecamera__unmute, Toast.LENGTH_SHORT).show();
            }
        } else {
            imgSound.setImageResource(R.drawable.ic_volume_mute_white_24dp);
            camera.setAudio(Audio.OFF);
            if (isShowToast) {
                Toast.makeText(getContext(), R.string.squarecamera__mute, Toast.LENGTH_SHORT).show();
            }
        }
    }

    View view;

    private void createCamera(Bundle savedInstanceState) {
        camera = (CameraView) view.findViewById(R.id.camera_preview_view);
        camera.setLifecycleOwner(getViewLifecycleOwner());
        camera.setMode(Mode.VIDEO);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                super.onVideoTaken(result);
                // Video was taken!
                // Use result.getFile() to access a file holding
                // the recorded video.
//                com.otaliastudios.cameraview.Size size = camera.getVideoSize();
//                Toast.makeText(getContext(), "Have video", Toast.LENGTH_SHORT).show();
                isTakingVideo = false;

                /*VideoPreviewActivity.setVideoResult(result);
                Intent intent = new Intent(getContext(), VideoPreviewActivity.class);
                startActivity(intent);*/


                //tro ve man hinh chinh
                Intent data = new Intent();
                data.putExtra(APIConstants.VIDEO_URL, result.getFile().getAbsolutePath());
                if (getActivity().getParent() == null) {
                    getActivity().setResult(getActivity().RESULT_OK, data);
                } else {
                    getActivity().getParent().setResult(getActivity().RESULT_OK, data);
                }
                getActivity().finish();

            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);

                /*int errorReason = exception.getReason();
                String exceptionAsString = exception.toString();
                String exceptionAsMessage = exception.getMessage();

                Log.i("error", exceptionAsMessage);

                try {
                    camera.close();
                    camera.stopVideo();
                    camera.open();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }


        });

        int screenWidth = getScreenWidth(getContext());
        if (screenWidth > ImageSize.ORIGINAL_WIDTH) {
            screenWidth = ImageSize.ORIGINAL_WIDTH;
        }
        camera.getLayoutParams().width = screenWidth;
        camera.getLayoutParams().height = screenWidth;
        camera.setLayoutParams(camera.getLayoutParams());

        int size = getContext().getResources().getDimensionPixelSize(R.dimen.squarecamera__video_size);
        SizeSelector width = SizeSelectors.minWidth(size);
        SizeSelector height = SizeSelectors.minHeight(size);
        SizeSelector dimensions = SizeSelectors.and(width, height); // Matches sizes bigger than 1000x2000.
        SizeSelector ratio = SizeSelectors.aspectRatio(AspectRatio.of(1, 1), 0); // Matches 1:1 sizes.

        SizeSelector result = SizeSelectors.or(
                SizeSelectors.and(ratio, dimensions), // Try to match both constraints
                ratio, // If none is found, at least try to match the aspect ratio
                SizeSelectors.biggest() // If none is found, take the biggest
        );
        camera.setVideoSize(result);

        camera.setVideoBitRate(APIConstants.VIDEO_BITRATE);
        camera.setSnapshotMaxWidth(size);
        camera.setSnapshotMaxHeight(size);
//        camera.setVideoBitRate(size*size*APIConstants.BIT_RATE);


        /*if (savedInstanceState == null) {
            ViewTreeObserver observer = camera.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        camera.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        camera.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        } else {
        }*/
    }

    private ImageView takePhotoBtn;

    private void toggleCamera() {
        if (camera == null) return;
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        switch (camera.toggleFacing()) {
            case BACK:
//                message("Switched to back camera!", false);
                isBackCamera = true;

                break;

            case FRONT:
//                message("Switched to front camera!", false);
                isBackCamera = false;
                break;
        }
        CameraFragment.newInstance().changeCamera(isBackCamera);
    }

    private boolean isTakingVideo = false;

    private void captureVideo() {
        if (!isTakingVideo) {
            if (camera.getMode() == Mode.PICTURE) {
//            message("Can't record HQ videos while in PICTURE mode.", false);
                return;
            }
            if (camera.isTakingPicture() || camera.isTakingVideo()) return;
//        message("Recording for 5 seconds...", true);

            camera.takeVideoSnapshot(createVideoFile(), TIME_RECORD);
            isTakingVideo = true;
            beginRecord();
            setTextTimeDuration();
            takePhotoBtn.setImageResource(R.drawable.squarecamera__camera_recording);
        } else {
            camera.stopVideo();
            isTakingVideo = false;
            takePhotoBtn.setImageResource(R.drawable.squarecamera__capture_photo_button);

        }
    }

    private void captureVideoSnapshot() {
        if (camera.isTakingVideo()) {
//            message("Already taking video.", false);
            return;
        }
//        message("Recording snapshot for 5 seconds...", true);
        camera.takeVideoSnapshot(createVideoFile(), APIConstants.TIME_VIDEO_RECORD_SECOND);
    }


    private File createVideoFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "VIDEO_" + timeStamp + ".mp4";

        Uri outputFileUri = null;
        File getImage = getContext().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), imageFileName));
        }
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        String path = storageDir + "/" + imageFileName + ".jpg";
        File image = null;
        try {
            image = new File(outputFileUri.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


    private int position = 0;
    private int step = 1000;

    private void setTextTimeDuration() {
        View view = getView();
        if (view == null) return;

        final TextView txtTime = (TextView) view.findViewById(R.id.auto_flash_icon);
        txtTime.setText("00:00");
        position = 0;

        //set position
        //setup handler that will keep seekBar and playTime in sync
        final Handler handler = new Handler();
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (position < TIME_RECORD) {
                        //position
                        try {
                            position += step;
                            int posMns = (position / 60000) % 60000;
                            int posScs = position % 60000 / 1000;
                            String songPos = String.format("%02d:%02d", posMns, posScs);
                            txtTime.setText(songPos);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        handler.postDelayed(this, step);
                    }
                }
            });
        }

        /*if (Camera.Parameters.FLASH_MODE_AUTO.equalsIgnoreCase(mFlashMode)) {
            txtTime.setText("Auto");
        } else if (Camera.Parameters.FLASH_MODE_ON.equalsIgnoreCase(mFlashMode)) {
            txtTime.setText("On");
        } else if (Camera.Parameters.FLASH_MODE_OFF.equalsIgnoreCase(mFlashMode)) {
            txtTime.setText("Off");
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        Log.d(TAG, "onSaveInstanceState");
        outState.putString(CAMERA_FLASH_KEY, mFlashMode);
        super.onSaveInstanceState(outState);
    }


    private void setSafeToTakePhoto(final boolean isSafeToTakePhoto) {
        mIsSafeToTakePhoto = isSafeToTakePhoto;
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
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return CameraInfo.CAMERA_FACING_FRONT;
        }

        return getBackCameraID();
    }

    private int getBackCameraID() {
        return CameraInfo.CAMERA_FACING_BACK;
    }


    @Override
    public void onStop() {

        CameraSettingPreferences.saveCameraFlashMode(getActivity(), mFlashMode);

        super.onStop();
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


    public void onPictureTaken(final byte[] data, Camera camera) {
        final int rotation = 0;//getPhotoRotation();


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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //CHECK PERMISSION
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int PERMISSION_REQUEST_CODE = 200;
    String[] arrPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean checkPermission() {

        boolean result = true;
        for (int i = 0; i < arrPermissions.length; i++) {
            int grant = ContextCompat.checkSelfPermission(getContext(), arrPermissions[i]);
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }

        return result;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            R.string.deny_permission_notify
            boolean check = checkPermission();
            if (check == false) {
//                MyUtils.showToast(context, R.string.deny_permission_notify);
                requestPermissions(arrPermissions, PERMISSION_REQUEST_CODE);
            } else {
                if (camera != null) {
                    camera.open();
                }
            }

        } else {
            if (camera != null) {
                camera.open();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                requestPermission();
                break;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //END CHECK PERMISSION
    ////////////////////////////////////////////////////////////////////////////////////////
    public static final int TIME_RECORD = APIConstants.TIME_VIDEO_RECORD_SECOND * 1000;
    public static final int PERCENT = 1000;
    private int progressStatus = 0;

    private void beginRecord() {

        progressBar.setProgress(0);
        progressBar.setMax(PERCENT);

        // Start the lengthy operation in a background thread
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < PERCENT) {
                    // Update the progress status
                    progressStatus += 1;

                    // Try to sleep the thread for 20 milliseconds
                    try {
                        Thread.sleep(TIME_RECORD / PERCENT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                progressBar.setProgress(progressStatus);
                            }
                        }
                    });
                }
            }
        }).start(); // Start the operation
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
            Toast.makeText(getContext(), "resume " + isVisibleToUser, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    camera.invalidate();
                    requestPermission();
                }
            }, 500);


            /*FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();*/

        } else if (isVisibleToUser) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
//            Toast.makeText(getContext(), "onCreated "+isVisibleToUser, Toast.LENGTH_SHORT).show();
        } else if (!isVisibleToUser && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
            Toast.makeText(getContext(), "go out " + isVisibleToUser, Toast.LENGTH_SHORT).show();


            if (camera.isTakingPicture() || camera.isTakingVideo()) {
                camera.stopVideo();
            }
            //khi qua tab khac, neu camera dang la phia truoc thi reset ve sau
            if (!isBackCamera) {
                camera.toggleFacing();
            }
        }

    }

    private boolean isBackCamera = true;

    @Override
    public void changeCamera(boolean isBackCamera) {
        this.isBackCamera = isBackCamera;

    }
}
