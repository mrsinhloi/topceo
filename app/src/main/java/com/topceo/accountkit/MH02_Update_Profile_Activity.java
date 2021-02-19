package com.topceo.accountkit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.MyValidator;
import com.topceo.utils.ProgressUtils;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MH02_Update_Profile_Activity extends AppCompatActivity {


    private Activity context = this;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    private TinyDB db;

    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;
    @BindView(R.id.imgEditAvatar)
    ImageView imgEditAvatar;
    @BindView(R.id.txtPhone)
    TextView txtPhone;
    @BindView(R.id.edit1)
    AppCompatEditText edit1;
    @BindView(R.id.edit2)
    AppCompatEditText edit2;
    @BindView(R.id.edit3)
    AppCompatEditText edit3;
    @BindView(R.id.btnNext)
    AppCompatButton btnNext;
    @BindView(R.id.scrollView)
    ScrollView sv;
    @BindView(R.id.linearRoot)
    FrameLayout linearRoot;

    private String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh02_update_profile);
        ButterKnife.bind(this);
        db = new TinyDB(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        if(b!=null){
            phone = b.getString(User.PHONE, "");
            if (!TextUtils.isEmpty(phone)) {
                txtPhone.setText(phone);
            }
        }


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Kiem tra ten dang nhap
                String username = edit1.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    MyUtils.showToast(context, R.string.username_invalid);
                    edit1.requestFocus();
                    return;
                }

                username = username.toLowerCase();
                if (!MyValidator.validateUsername(username)) {
                    MyUtils.showToast(context, R.string.username_invalid);
                    edit1.requestFocus();
                    return;
                }


                //Kiem tra password
                String pass1 = edit2.getText().toString().trim();
                if (TextUtils.isEmpty(pass1)) {
                    MyUtils.showToast(context, R.string.please_input_password);
                    edit2.requestFocus();
                    return;
                }

                String pass2 = edit3.getText().toString().trim();
                if (TextUtils.isEmpty(pass2)) {
                    MyUtils.showToast(context, R.string.please_input_password);
                    edit3.requestFocus();
                    return;
                }

                if (!pass1.equals(pass2)) {
                    MyUtils.showToast(context, R.string.password_not_mach);
                    edit3.requestFocus();
                    return;
                }



//                if (!new UsernameValidator().validate(username)) {


                //dang ky
                signupByPhone(phone, username.toLowerCase(), pass1);

//                user.setUserName(name);
//                user.setEmail(email);
//                completeSignup(user);
            }
        });

        //chon hinh
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
        imgEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgAvatar.performClick();
            }
        });
        /*edit1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    sv.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        });*/
        linearRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = linearRoot.getRootView().getHeight() - linearRoot.getHeight();

                if (heightDiff > 1000) {
                    Log.d("MyActivity", "keyboard opened");
                    sv.fullScroll(View.FOCUS_DOWN);
                } else {
                    Log.d("MyActivity", "keyboard closed");
                }
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //CHECK PERMISSION
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int PERMISSION_REQUEST_CODE = 200;
    String[] arrPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private boolean checkPermission() {
        boolean result = true;
        for (int i = 0; i < arrPermissions.length; i++) {
            int grant = ContextCompat.checkSelfPermission(context, arrPermissions[i]);
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }

        return result;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean check = checkPermission();
            if (!check) {
                numberShow++;
                if (numberShow >= 3) {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.deny_permission_notify);
                    alertDialogBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            MyUtils.goToSettings(context);
                        }
                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    requestPermissions(arrPermissions, PERMISSION_REQUEST_CODE);
                }

            } else {
                dialogChoseImage();
            }
        } else {
            dialogChoseImage();
        }
    }

    private int numberShow = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                requestPermission();
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //END CHECK PERMISSION
    ////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_CODE_PICKER = 133;
    private static final int IMAGES_LIMIT = 1;

    private void dialogChoseImage() {
        new MaterialDialog.Builder(this)
                .items(R.array.get_images_from)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0://capture
                                takePictureIntent();
                                break;
                            case 1:
                                ImagePicker.create(context)
                                        .multi() // single mode
                                        .imageTitle(getText(R.string.tap_to_select_image).toString())
                                        .limit(IMAGES_LIMIT) // max imagesSelected can be selected
                                        .showCamera(false) // show camera or not (true by default)
                                        .start(REQUEST_CODE_PICKER); // start image picker activity with request code
                                break;
                        }
                    }
                })
                .show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int TAKE_PICTURE = 12;

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }

    String mCurrentPhotoPath = "";

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = storageDir + "/" + imageFileName + ".jpg";
        File image = null;
        try {
            image = new File(path);
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {
                uploadImage(mCurrentPhotoPath);
            } else if (requestCode == REQUEST_CODE_PICKER) {
                ArrayList<Image> images =
                        intent.getParcelableArrayListExtra(
                                com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

                String path = images.get(0).getPath();
                if (!TextUtils.isEmpty(path)) {
                    uploadImage(path);
                }


            }

        }
    }

    private Uri picUri;

    private void uploadImage(String path) {
        if (!TextUtils.isEmpty(path)) {

            //init value
//            resetPath();

            //upload
            picUri = Uri.parse(path);
            parseUriAndUploadImage();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int widthThumb, heightThumb;
    private String imgPathThumb = "";
    private String fileImageName = "";
    private Bitmap myBitmap;

    private void parseUriAndUploadImage() {
        if (picUri != null) {
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
                //doc len va resize
//                myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                myBitmap = BitmapFactory.decodeFile(picUri.getPath());
                myBitmap = MyUtils.rotateImageIfRequired(myBitmap, picUri);
                Bitmap thumb = MyUtils.getResizedBitmap(myBitmap, MyConstant.IMG_WIDTH_PX_AVATAR);


                widthThumb = thumb.getWidth();
                heightThumb = thumb.getHeight();


                //luu xuong, gan vao 2 path full va thumb
                imgPathThumb = MyUtils.createImageFile(context).getAbsolutePath();
                MyUtils.saveBitmap(thumb, imgPathThumb);

                //recycle la mat hinh
                /*thumb.recycle();
                full.recycle();*/

                //doc thong tin size
                File f = new File(imgPathThumb);
                float sizeThumb = MyUtils.getSizeBytes(f.length());

                //set giao dien
                Glide.with(context)
                        .load(new File(imgPathThumb))
//                        .transform(new RoundedCorners(sizeThumb/2))
                        .into(imgAvatar);
                //an icon edit avata
                imgEditAvatar.setVisibility(View.GONE);


                //tao sas
                fileImageName = MyUtils.getFileNameAndExtension(imgPathThumb);
                generateUploadSAS(fileImageName);
                //upload azure
                //goi ham send image


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
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

    //google photos
    public static Uri getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void generateUploadSAS(final String fileName) {
        /*if (isSocketConnected()) {

            final String uuid = MyUtils.getGUID();

            JSONObject obj = new JSONObject();
            try {
                obj.put("fileName", fileName);
                obj.put("itemGUID", uuid);
            } catch (Exception e) {
                e.printStackTrace();
            }


            socket.emit("getAvatarUploadSAS", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    MyUtils.log(args.toString());
                    final SasModel model = SasModel.parseSasModel(context, args);
                    //upload azure
                    if (model != null) {

                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                boolean isSuccess = uploadFile(model.getSasUrl(), imgPathThumb, ContentTypeInChat.IMAGE);
                                return isSuccess;
                            }

                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                super.onPostExecute(aBoolean);
                                if (aBoolean) {
                                    updateUserAvatar(model);
                                }
                            }
                        }.execute();

                    }


                }
            });
        }*/
    }

    private boolean uploadFile(String sasLink, String imgPath, int contentType) {
        if (TextUtils.isEmpty(sasLink) == false) {

            try {
                //Return a reference to the blob using the SAS URI.
                CloudBlockBlob blob = new CloudBlockBlob(new StorageUri(URI.create(sasLink)));
                //blob.setStreamWriteSizeInBytes(256 * 1024);//256 k


                OutputStream oStream = blob.openOutputStream();
                File f = new File(imgPath);
                FileInputStream inputStream = new FileInputStream(f);
                blob.upload(inputStream, f.length());

                //upload properties
                switch (contentType) {
                    case ContentTypeInChat.IMAGE:
                        blob.getProperties().setContentType("image/jpeg");
                        break;
                    case ContentTypeInChat.VIDEO:
                        blob.getProperties().setContentType("video/mp4");
                        break;
                    case ContentTypeInChat.VOICE:
                        blob.getProperties().setContentType("audio/mp3");
                        break;
                    default:
                        blob.getProperties().setContentType("application/octet-stream");
                        break;
                }


                blob.uploadProperties();

                return true;
            } catch (StorageException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void signupByPhone(final String phone, final String username, final String password) {
        if (MyUtils.checkInternetConnection(context)) {
            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.creating_account));
            progressDialog.show();*/
            ProgressUtils.show(context);

            final long start = System.currentTimeMillis();
            AndroidNetworking.post(Webservices.URL + "user/signupByPhone")
                    .addQueryParameter("Phone", phone)
                    .addQueryParameter("UserName", username)
                    .addQueryParameter("Password", password)
                    .addQueryParameter("OS", Webservices.OS)
                    .addQueryParameter("CountryId", Webservices.COUNTRY_ID)
                    .addQueryParameter("CountryName", Webservices.COUNTRY_NAME)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ReturnResult result = Webservices.parseJson(response, null, false);
                            ProgressUtils.hide();

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    //save thong tin
                                    TinyDB db = new TinyDB(context);
                                    db.putString(TinyDB.USER_NAME, username);
                                    db.putString(TinyDB.USER_PHONE, phone);
                                    db.putString(TinyDB.USER_PASSWORD, password);

                                    MyUtils.showToast(context, R.string.sign_up_success);

                                    //login
                                    login(phone, password);

                                } else {
                                    if (!TextUtils.isEmpty(result.getErrorMessage())) {
                                        if (result.getErrorMessage().toLowerCase().contains("username")) {
                                            edit1.requestFocus();
                                            edit1.setSelection(edit1.getText().length());
                                            MyUtils.showAlertDialog(context, R.string.user_name_exist);
                                        } else if (result.getErrorMessage().toLowerCase().contains("phone")) {
                                            //so dien thoai da check ton tai ben ngoai man hinh
                                            MyUtils.showAlertDialog(context, R.string.phone_exists);
                                        } else {
                                            MyUtils.showAlertDialog(context, result.getErrorMessage());
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(ANError ANError) {
                            ProgressUtils.hide();
                        }
                    });
        } else {
            MyUtils.showThongBao(context);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String IS_SIGNUP_BY_EMAIL = "IS_SIGNUP_BY_EMAIL";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void login(final String phone, final String password) {
        /*final ProgressDialog progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.authenticating));
        progressDialog.show();*/
        ProgressUtils.show(context);


        /*MyApplication.client = new OkHttpClient.Builder()
                .cookieJar(new CookieStore())
                .build();*/

        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.URL + "user/login")
                .addBodyParameter("username", phone)
                .addBodyParameter("password", password)
                .setOkHttpClient(MyApplication.client)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProgressUtils.hide();
                        MyUtils.howLong(start, "login");
                        final ReturnResult result = Webservices.parseJson(response, User.class, false);


                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                //save thong tin
                                final TinyDB db = new TinyDB(context);
                                db.putString(TinyDB.USER_PHONE, phone);
                                db.putString(TinyDB.USER_PASSWORD, password);
                                db.putBoolean(TinyDB.IS_LOGIN_BY_FACEBOOK, false);
                                db.putBoolean(TinyDB.IS_LOGINED, true);

                                //luu user
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        if (result.getData() != null) {
                                            User user = (User) result.getData();
                                            db.putObject(User.USER, user);
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);

                                        onLoginSuccess();

                                    }
                                }.execute();


                            } else {
                                /*if(!TextUtils.isEmpty(result.getErrorMessage())){
                                    MyUtils.showToast(context, result.getErrorMessage());
                                }*/
                                MyUtils.showToast(context, R.string.please_check_input_login);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        ProgressUtils.hide();
                    }
                });
    }

    public void onLoginSuccess() {

        //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
        MyApplication.whenLoginSuccess();
        startActivity(new Intent(context, MH01_MainActivity.class));
        finish();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


}
