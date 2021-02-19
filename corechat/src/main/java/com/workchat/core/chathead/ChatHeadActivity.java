package com.workchat.core.chathead;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.utils.MyUtils;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;

public class ChatHeadActivity extends AppCompatActivity {
    private Context context = this;
    public static final String IS_SELECT_IMAGE = "IS_SELECT_IMAGE";
    public static final String IS_TAKE_PICTURE = "IS_SELECT_IMAGE";
    public static final String IS_TAKE_FILE = "IS_SELECT_IMAGE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        boolean takePicture = b.getBoolean(IS_TAKE_PICTURE);
        if(takePicture){
            pickPicture();
        }
    }

    private static final int CAPTURE_MEDIA = 666;
    private void takeOrRecordVideo2() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        AnncaConfiguration.Builder videoLimited = new AnncaConfiguration.Builder(this, CAPTURE_MEDIA);
//        videoLimited.setMediaAction(AnncaConfiguration.MEDIA_ACTION_VIDEO);
        videoLimited.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
        videoLimited.setVideoFileSize(20 * 1024 * 1024);
        videoLimited.setMinimumVideoDuration(1 * 60 * 1000);
        new Annca(videoLimited.build()).launchCamera();
    }

    String mCurrentPhotoPath;
    private static final int REQUEST_CODE_TAKE_PICTURE = 555;
    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = MyUtils.createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                Uri uri = FileProvider.getUriForFile(this, ChatApplication.Companion.getApplicaitonId() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
            }
        }
    }

    private static final int REQUEST_CODE_PICK_PICTURE = 333;
    private void pickPicture(){
        FilePickerBuilder.getInstance()
                            .setMaxCount(20)
//                            .setSelectedFiles(photoPaths)
//                            .setActivityTheme(R.style.FilePickerTheme)
                            .setActivityTitle("Select media")
                            .enableVideoPicker(true)
                            .enableCameraSupport(true)
                            .showGifs(false)
                            .showFolderView(true)
                            .enableSelectAll(true)
                            .enableImagePicker(true)
//                            .setCameraPlaceholder(R.drawable.custom_camera)
                            .withOrientation(Orientation.PORTRAIT_ONLY)
                            .pickPhoto(this, REQUEST_CODE_PICK_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                /*case ACTION_SELECT_LOCATION:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        MyLocation location = b.getParcelable(MyLocation.MY_LOCATION);
                        if (location != null && location.getLat() > 0 && location.getLon() > 0) {
                            lat = location.getLat();
                            lon = location.getLon();
                            address = location.getAddress();
                            isCheckin = location.isCurrentLocation();

                            sendLocation();
                        }
                    }

                    break;*/

                case REQUEST_CODE_TAKE_PICTURE:
                    /*isCapture = true;
                    if (MyUtils.checkInternetConnection(context)) {
                        uploadImage(mCurrentPhotoPath);
                    } else {
                        MyUtils.showThongBao(context);
                    }*/

                    break;
                case REQUEST_CODE_PICK_PICTURE://chọn nhiều hình hoặc nhiều video
                    /*isCapture = false;
                    //tao moi lai
                    paths = new ArrayList<>();
                    if (MyUtils.checkInternetConnection(context)) {
                        if (data != null) {
                            paths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                            if (paths.size() > 0) {
                                if (paths.size() == 1) {
                                    isSendAlbum = false;
                                    processUploadMedia(paths.get(0));//1 hình hoặc 1 video
                                } else {//>1
                                    pathsAlbumCache = new ArrayList<>(paths);
                                    isSendAlbum = true;
                                    uuidAlbum = MyUtils.getGUID();
                                    isAddHolderAlbum = false;
                                    albums = new JSONArray();
                                    processUploadMedia(paths.get(0));//hình hoặc nhiều video
                                }
                            }
                        }
                    } else {
                        MyUtils.showThongBao(context);
                    }*/
                    break;
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        ArrayList<Object> photoPaths = new ArrayList<>();
                        ArrayList<String> l = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                        if (l != null && l.size() > 0) {
                            photoPaths.addAll(l);
                        }
                    }
                    break;
                case FilePickerConst.REQUEST_CODE_DOC:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        ArrayList<Object> docPaths = new ArrayList<>();
                        docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        MyUtils.log(docPaths.size() + "");
                        //0 = "/storage/emulated/0/ebooks/Khac biet de but pha - Jason Fried.pdf"
                        //1 = "/storage/emulated/0/ebooks/family_playbook_v2a_en.pdf"

                        /*if (docPaths.size() > 0) {
                            uploadFile(docPaths.get(0).toString());
                        }*/
                    }
                    break;

                //quay phim hoac chup anh
                case CAPTURE_MEDIA:
                    String path = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
//                    Toast.makeText(this, "Media captured. " + path, Toast.LENGTH_SHORT).show();
//                    processUploadMedia(path);

                    break;

                /*case FIND_MEMBER_CODE:
                    if (data != null) {
                        b = data.getExtras();
                        if (b != null) {
                            final ArrayList<UserMBN> users = b.getParcelableArrayList(UserMBN.USER_MODEL);
                            if (users != null) {
                                for (int i = 0; i < users.size(); i++) {
                                    UserMBN user = users.get(i);

                                    String uuid = MyUtils.getGUID();
                                    sendContact(
                                            user.get_id(),
                                            user.getName(),
                                            user.getAvatar(),
                                            user.getPhone(),
                                            user.getEmail(), uuid);
                                }
                            }
                        }
                    }
                    break;*/
            }


            //location
            /*switch (requestCode) {

                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case RESULT_OK:
                            if (isSendLocation) {
//                                sendLocation();
                                selectLocation();
                            } else {
                                //tao plan
                                createPlan();
                            }
                            break;
                        case RESULT_CANCELED:
                            MyUtils.showToast(context, R.string.gps_not_turn_on);
                            break;
                    }
                    break;
            }*/

        }


    }
}
