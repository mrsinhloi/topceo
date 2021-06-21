package com.topceo.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.topceo.R;
import com.topceo.accountkit.PhoneUtils;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.login.MH18_EmailVerifyActivity;
import com.topceo.objects.image.ImageSize;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.SasChild;
import com.topceo.services.SasParent;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.MyValidator;
import com.topceo.utils.ProgressUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MH20_UserEditProfileActivity extends AppCompatActivity {
    private Context context = this;
    private AppCompatActivity activity = this;

    private TinyDB db;
    private User user;

    @BindView(R.id.imageView1)
    ImageView avatar;
    @BindView(R.id.textView1)
    TextView txtChangeAvatar;
    @BindView(R.id.editText1)
    EditText txt1;
    @BindView(R.id.editText2)
    EditText txt2;
    @BindView(R.id.editText4)
    EditText txt4;

    @BindView(R.id.editText5)
    EditText txt5;
    @BindView(R.id.editText6)
    EditText txt6;
    @BindView(R.id.editText7)
    EditText txt7;
    @BindView(R.id.editText8)
    EditText txt8;
    @BindView(R.id.editText9)
    EditText txt9;
    @BindView(R.id.editText10)
    EditText txt10;
    @BindView(R.id.editText11)
    EditText txt11;

    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;

    @BindView(R.id.editBios)
    AppCompatEditText editBios;

    @BindView(R.id.txtAlertEmail)
    TextView txtAlertEmail;
    @BindView(R.id.txtAlertPhone)
    TextView txtAlertPhone;

    @BindView(R.id.spinner1)
    Spinner spinner1;

    private int avatarSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
        ButterKnife.bind(this);

        setTitleBar();

        /*toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        MyUtils.updateStatusBarColor(getWindow());

        /////////////////////////////////
        db = new TinyDB(this);
        avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        ///////////////
        //set user//////////////////////////////////////////////////////
        initUser();

        ////////////////////////////////////////////////////////////////
        txtChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change avatar
                changeAvatar();
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtChangeAvatar.performClick();
            }
        });


        //neu vừa rồi đã gởi email xác thực thì giờ load lại profile để update trạng thái hiển thị
        boolean isSendEmail = db.getBoolean(User.IS_SEND_VERIFY_EMAIL);
        if (isSendEmail) {
            if (MyUtils.checkInternetConnection(context)) {
                db.putBoolean(User.IS_SEND_VERIFY_EMAIL, false);
                initCookie();
            }
        }


        //////////////////////////////////////////////////////////////////////////////////
        /*// Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (MyUtils.checkInternetConnection(context)) {
                    //lay page 0
                    setRefresh(true);
                    initCookie();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setRefresh(false);
                        }
                    }, 3000);
                }
            }
        });*/

    }

    private void initSpinner() {
        //init data
        int rowId = R.layout.spinner_row_1;
        String[] arrGender = getResources().getStringArray(R.array.arr_gender);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, rowId, arrGender);
        arrayAdapter.setDropDownViewResource(rowId);
        spinner1.setAdapter(arrayAdapter);

        //init ui
        int[] valueInt = getResources().getIntArray(R.array.arr_gender_value);
        gender = valueInt[0];
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = valueInt[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (user != null) {
            gender = user.getGender();
            int pos = -1;
            for (int i = 0; i < valueInt.length; i++) {
                if (gender == valueInt[i]) {
                    pos = i;
                    break;
                }
            }
            if (pos >= 0) {
                spinner1.setSelection(pos);
            }
        }
    }


    private void initUser() {
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }
        if (user != null) {
//            MyUtils.showToast(context, "Welcome "+user.getUserName());

            if (context != null) {
                String url = user.getAvatarSmall();
                /*Glide.with(context)
                        .load(url)//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(avatarSize, avatarSize)
                        .transform(new GlideCircleTransform(context))
                        .into(avatar);*/

                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.ic_no_avatar)
                        .centerCrop()
                        .override(avatarSize, avatarSize)
                        .transform(new GlideCircleTransform(context))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull @NotNull Drawable resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Drawable> transition) {
                                avatar.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                            }
                        });


                txt1.setText(user.getFullName());
                txt2.setText(user.getUserName());
                txt4.setText(user.getPhone());
//                txt10.setText(user.getEmail());

                String favorite = user.getFavorite();
                if (!TextUtils.isEmpty(favorite)) {
                    //thay the <br> thanh \n
                    if (favorite.contains("<br>")) {
                        favorite = favorite.replace("<br>", "\n");
                    }
                    editBios.setText(favorite);
                }
                editBios.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // limit to 5 lines
                        if (editBios != null && editBios.getLayout() != null) {
                            if (editBios.getLayout().getLineCount() > 5)
                                editBios.getText().delete(editBios.getText().length() - 1, editBios.getText().length());
                        }
                    }
                });
                ///EMAIL/////////////////////////////////////////////////////////////////////////
                /*if (user.isEmailVerified()) {
                    img1.setImageResource(R.drawable.ic_check_circle_light_blue_500_24dp);
                    txtAlertEmail.setVisibility(View.GONE);
                } else {
                    img1.setImageResource(R.drawable.ic_info_outline_orange_500_24dp);
                    txtAlertEmail.setVisibility(View.VISIBLE);
                }*/
                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //nếu chưa verify thì mới verify
                        if (!user.isEmailVerified()) {
                            startActivity(new Intent(context, MH18_EmailVerifyActivity.class));
                        }
                    }
                });
                txtAlertEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img1.performClick();
                    }
                });
                ///EMAIL/////////////////////////////////////////////////////////////////////////


                ///PHONE/////////////////////////////////////////////////////////////////////////
//                img2.setVisibility(View.GONE);
//                txtAlertPhone.setVisibility(View.GONE);
                if (user.isPhoneVerified()) {
                    img2.setImageResource(R.drawable.ic_check_circle_light_blue_500_24dp);
                    txtAlertPhone.setVisibility(View.GONE);
                } else {
                    img2.setImageResource(R.drawable.ic_info_outline_orange_500_24dp);
                    txtAlertPhone.setVisibility(View.VISIBLE);
                }
                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //nếu chưa verify thì mới verify
                        if (!user.isPhoneVerified()) {
                            phone = txt4.getText().toString().trim();
                            phone = PhoneUtils.getE164FormattedMobileNumber(phone, PhoneUtils.getDefaultCountryNameCode());
                            String phone2 = PhoneUtils.getE164FormattedMobileNumber(user.getPhone(), PhoneUtils.getDefaultCountryNameCode());
                            if (!TextUtils.isEmpty(phone2) && !phone2.equalsIgnoreCase(phone)) {//khac thi kiem tra
                                //bao luu truoc
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle(R.string.notification);
                                alertDialogBuilder.setMessage(R.string.phone_change_confirm);
                                alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        updateProfile();
                                    }
                                });

                                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                requestVerifyPhone(phone2);
                            }

                        }
                    }
                });
                txtAlertPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img2.performClick();
                    }
                });
                ///PHONE/////////////////////////////////////////////////////////////////////////


            }

            getSocialInfo(user.getUserId());

            initSpinner();

        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /*//menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            //update profile
            updateProfile();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }*/

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private String fullName = "", userName = "", phone = "", email = "", bios = "";
    private String web = "", facebook = "", twitter = "", instagram = "", youtube = "", linkedIn = "";
    int gender = -1;

    private void updateProfile() {
        if (MyUtils.checkInternetConnection(context)) {

            //check thong tin
            fullName = txt1.getText().toString().trim();
            userName = txt2.getText().toString().trim().toLowerCase();
            phone = txt4.getText().toString().trim();
//            email = txt10.getText().toString().trim().toLowerCase();
            bios = editBios.getText().toString().trim();

            web = txt9.getText().toString().trim().toLowerCase();
            facebook = txt5.getText().toString().trim().toLowerCase();
            twitter = txt6.getText().toString().trim().toLowerCase();
            instagram = txt7.getText().toString().trim().toLowerCase();
            youtube = txt8.getText().toString().trim();
            linkedIn = txt11.getText().toString().trim();


            boolean valid = true;
            if (TextUtils.isEmpty(fullName) || fullName.length() < 3) {
                txt1.setError(getText(R.string.name_request_lenght));
                valid = false;
                txt1.requestFocus();
                return;
            } else {
                txt1.setError(null);
            }

            if (!MyValidator.validateUsername(userName)) {
                MyUtils.showToast(context, R.string.user_name_explain_2);
                txt2.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(userName) || userName.length() < 3) {
                txt2.setError(getText(R.string.name_request_lenght));
                valid = false;
                txt2.requestFocus();
                return;
            } else {
                txt2.setError(null);
            }

            /*if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                txt10.setError(getText(R.string.email_invalid));
                valid = false;
                txt10.requestFocus();
                return;
            } else {
                txt10.setError(null);
            }*/

            if (TextUtils.isEmpty(phone) || phone.length() < 10) {
                txt4.setError(getText(R.string.phone_invalid));
                valid = false;
                txt4.requestFocus();
                return;
            } else {
                txt4.setError(null);
            }

            //options, kiem tra khi co nhap gia tri
            //web
            if (!TextUtils.isEmpty(web)) {
                boolean isValid = Patterns.WEB_URL.matcher(web).find();
                if (!isValid) {
                    txt9.setError(getText(R.string.web_invalid));
                    txt9.requestFocus();
                    valid = false;
                    return;
                } else {
                    txt9.setError(null);
                }
            }

            //facebook
            if (!TextUtils.isEmpty(facebook)) {
                if (MyUtils.isFacebookUrl(facebook)) {
                    txt5.setError(null);
                } else {
                    txt5.setError(getText(R.string.facebook_invalid));
                    valid = false;
                    txt5.requestFocus();
                    return;
                }
            }

            //twitter
            if (!TextUtils.isEmpty(twitter)) {
                if (MyUtils.isUsername(twitter)) {
                    txt6.setError(null);
                } else {
                    txt6.setError(getText(R.string.twitter_invalid));
                    valid = false;
                    txt6.requestFocus();
                    return;
                }
            }
            //instagram
            if (!TextUtils.isEmpty(instagram)) {
                if (MyUtils.isUsername(instagram)) {
                    txt7.setError(null);
                } else {
                    txt7.setError(getText(R.string.instagram_invalid));
                    valid = false;
                    txt7.requestFocus();
                    return;
                }
            }

            //youtube
            if (!TextUtils.isEmpty(youtube)) {
                if (MyUtils.isYoutubeUrl(youtube)) {
                    txt8.setError(null);
                } else {
                    txt8.setError(getText(R.string.youtube_invalid));
                    valid = false;
                    txt8.requestFocus();
                    return;
                }
            }

            //linkedin
            if (!TextUtils.isEmpty(linkedIn)) {
                if (MyUtils.isLinkedInUrl(linkedIn)) {
                    txt11.setError(null);
                } else {
                    txt11.setError(getText(R.string.linkedin_invalid));
                    valid = false;
                    txt11.requestFocus();
                    return;
                }
            }


            if (valid) {
                //kiem tra user name co hop le
                if (!userName.equalsIgnoreCase(user.getUserName())) {//khac thi kiem tra
                    checkUserName();
                } else {
                    //ko doi thi set ve rong "", de lat ko truyen vao graphQL
                    userName = "";//name ko doi

                    /*if (!phone.equalsIgnoreCase(user.getPhone())) {//khac thi kiem tra
                        checkPhoneExist();
                    } else {
                        phone = "";//phone ko doi
                        //tien hanh update
                        updateUserProfile();
                    }*/

                    //tien hanh update
                    updateUserProfile();

                }
            }


        } else {
            MyUtils.showThongBao(context);
        }


    }

    private void checkUserName() {
        if (MyValidator.validateUsername(userName)) {
            Webservices.checkUserName(userName, context).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {//ko co exception
                        boolean isExist = (boolean) task.getResult();
                        if (isExist) {
                            MyUtils.showAlertDialog(context, R.string.user_name_exist);
                            txt2.requestFocus();
                            MyUtils.hideKeyboard(MH20_UserEditProfileActivity.this);
                        } else {
                            //nam thay doi, tiep tuc kiem tra so dien thoai
                            phone = PhoneUtils.getE164FormattedMobileNumber(phone, PhoneUtils.getDefaultCountryNameCode());
                            String phone2 = PhoneUtils.getE164FormattedMobileNumber(user.getPhone(), PhoneUtils.getDefaultCountryNameCode());
                            if (!phone2.equalsIgnoreCase(phone)) {//khac thi kiem tra
                                checkPhoneExist();
                            } else {
                                phone = "";//phone ko doi
                                //tien hanh update
                                updateUserProfile();
                            }
                        }
                    } else {
                        MyUtils.showToast(context, task.getError().getMessage());
                    }
                    return null;
                }
            });
        } else {
            MyUtils.showToast(context, R.string.user_name_invalid);
            txt2.setError(getText(R.string.name_request_validate));
        }
    }


    private void checkPhoneExist() {
        if (!TextUtils.isEmpty(phone)) {
            Webservices.checkPhoneExists(phone, context).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {//ko co exception
                        boolean isExist = (boolean) task.getResult();
                        if (isExist) {
                            MyUtils.showAlertDialog(context, R.string.phone_exists);
                            txt4.requestFocus();
                            MyUtils.hideKeyboard(MH20_UserEditProfileActivity.this);
                        } else {
                            requestVerifyPhone(phone);
                        }
                    } else {
                        MyUtils.showToast(context, task.getError().getMessage());
                    }
                    return null;
                }
            });
        }
    }

    public static final int ACTION_PHONE_VALIDATE = 2;
    private String phoneRequest = "";

    private void requestVerifyPhone(String phone) {
//        phone chua ton tai, co the dang ky, nhung can xac thuc so dt roi moi cho update
//        Intent intent = new Intent(context, MH01_InputPhoneNumber_Activity.class);
//        intent.putExtra(User.PHONE, phone);
//        startActivityForResult(intent, MH01_InputPhoneNumber_Activity.REQUEST_VALID_NUMBER_PHONE);

        if (!TextUtils.isEmpty(phone)) {
            phoneRequest = phone;
            AuthUI.IdpConfig idpConfig = new AuthUI.IdpConfig.PhoneBuilder()
//                .setDefaultCountryIso("vn")//if (languageSelected == "vi") "vn" else "en"
                    .setDefaultNumber(phone)
                    .build();
            ArrayList<AuthUI.IdpConfig> providers = new ArrayList<>();
            providers.add(idpConfig);

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.GreenTheme)
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    ACTION_PHONE_VALIDATE
            );
        } else {
            MyUtils.showAlertDialog(context, R.string.phone_invalid);
        }
//        Intent intent = new Intent(context, InputPhoneActivity.class);
//        intent.putExtra(InputPhoneActivity.VALIDATE_IN_LOCAL, false);
//        startActivityForResult(intent, ACTION_PHONE_VALIDATE);
    }


    private void updateUserProfile() {
        MyUtils.hideKeyboard(MH20_UserEditProfileActivity.this);

        //thong tin social
        String[] socials = getResources().getStringArray(R.array.arr_socials);
        String social = "SocialInfo:\"[" +

                "{\\\"NameCode\\\": \\\"" + socials[0] + "\\\", \\\"Link\\\": \\\"" + web + "\\\"}," +
                "{\\\"NameCode\\\": \\\"" + socials[1] + "\\\", \\\"Link\\\": \\\"" + facebook + "\\\"}," +
                "{\\\"NameCode\\\": \\\"" + socials[2] + "\\\", \\\"Link\\\": \\\"" + twitter + "\\\"}," +
                "{\\\"NameCode\\\": \\\"" + socials[3] + "\\\", \\\"Link\\\": \\\"" + instagram + "\\\"}," +
                "{\\\"NameCode\\\": \\\"" + socials[4] + "\\\", \\\"Link\\\": \\\"" + youtube + "\\\"}," +
                "{\\\"NameCode\\\": \\\"" + socials[5] + "\\\", \\\"Link\\\": \\\"" + linkedIn + "\\\"}" +

                "]\"";

        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.updating));
        progressDialog.show();*/
        ProgressUtils.show(context);
        Webservices.updateUserProfile(fullName, userName, email, phone, gender, social, bios).continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {
                if (task.getError() == null) {//ko co exception
                    boolean ok = (boolean) task.getResult();
                    if (ok) {

                        //goi lai ham login de tao lai gia tri user
                        //khi dang nhap thi dung facebookId, facebookPass OR email/pass,
                        // pass thi ko doi nen chi can cap nhat lai email

                        if (!TextUtils.isEmpty(userName)) {
                            db.putString(TinyDB.USER_NAME, userName);
                        }
                        if (!TextUtils.isEmpty(phone)) {
                            db.putString(TinyDB.USER_PHONE, phone);
                        }
                        if (!TextUtils.isEmpty(email)) {
                            db.putString(TinyDB.USER_EMAIL, email);
                        }

                        //sau do goi lai login de lay User.class
                        initCookie();

                        MyUtils.showAlertDialog(context, R.string.update_success);
                        MyUtils.hideKeyboard(MH20_UserEditProfileActivity.this);

                    }
                } else {
//                    if(!TextUtils.isEmpty(task.getError().getMessage().trim()))MyUtils.showToast(context, task.getError().getMessage());

                    ANError error = (ANError) task.getError();
                    boolean isLostCookie = MyApplication.controlException(error);
                    if (isLostCookie) {
                        MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                            @Override
                            public Void then(Task<Object> task) throws Exception {
                                if (task.getResult() != null) {
                                    User kq = (User) task.getResult();
                                    if (kq != null) {
                                        updateUserProfile();
                                    }
                                }
                                return null;
                            }
                        });
                    } else {
                        String message = error.getMessage();
                        if (!TextUtils.isEmpty(message)) {
                            if (message.toLowerCase().contains("email")) {
                                MyUtils.showAlertDialog(context, R.string.email_exist);
                            } else if (message.toLowerCase().contains("phone")) {
                                MyUtils.showAlertDialog(context, R.string.phone_exists);
                            } else {
                                MyUtils.showAlertDialog(context, message);
                            }
                        }
                    }
                }

                ProgressUtils.hide();
                return null;
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String imgPath = "";

    private void uploadImageToServer(String localPath) {
        if (!TextUtils.isEmpty(localPath)) {
            imgPath = localPath;

            //neu xoay thi xoay lai va luu vao bo nho, khac 0 thi se xoay lai
//            int rotation = MyUtils.getRotationForImage(imgPath);
//            imgPath = MyUtils.saveBitmap(MyUtils.resizeImage(imgPath, rotation, imageSize, imageSize), imgPath);

            File file = new File(imgPath);
            //chuan bi lay duong dan sas truoc khi upload anh len azure, dung lower case
            final String GUID = UUID.randomUUID().toString().toLowerCase();
            final String extension = MyUtils.getFileExtension(file);


            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.avatar_uploading));
            progressDialog.show();*/
            ProgressUtils.show(context);
            AndroidNetworking.post(Webservices.API_URL + "user/getAvatarUploadSAS")
                    .addQueryParameter("ItemGUID", GUID)
                    .addQueryParameter("ImageExtension", extension)
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ReturnResult result = Webservices.parseJson(response, SasParent.class, false);
                            ProgressUtils.hide();

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                    SasParent parent = (SasParent) result.getData();
                                    if (parent != null) {
                                        //Upload tung kich thuoc hinh len server
                                        uploadImage(parent, GUID, extension);
                                    }

                                } else {
                                    MyUtils.showToast(context, result.getErrorMessage());
                                }
                            }
                        }

                        @Override
                        public void onError(ANError ANError) {
                            ProgressUtils.hide();
                        }
                    });
        } else {
            MyUtils.showToast(context, R.string.not_found);
        }
    }

    public void uploadImage(SasParent parent, String guid, String extension) {
        if (uploadTask == null || uploadTask.getStatus() == AsyncTask.Status.FINISHED) {
            uploadTask = new UploadImageTask(parent, guid, extension);
            uploadTask.execute();
        }
    }

    private UploadImageTask uploadTask;

    private class UploadImageTask extends AsyncTask<Void, Void, Boolean> {

        String GUID = "", extension = "";
        SasParent parent;

        public UploadImageTask(SasParent p, String guid, String ext) {
            parent = p;
            GUID = guid;
            extension = ext;
        }

//        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.image_uploading));
            progressDialog.show();*/
            ProgressUtils.show(context);
        }

        @Override
        protected Boolean doInBackground(Void... strings) {
            int uploadNumber = 0;
            File temp = MyUtils.createImageFile(context);


            //upload origin
            SasChild original = parent.getOriginal();
            if (original != null) {

                /*Bitmap b = MyUtils.resizeAndRotateImage(imgPath, ImageSize.MEDIUM_WIDTH, ImageSize.MEDIUM_WIDTH);
                MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                imgPath = MyUtils.saveBitmap(b, temp.getAbsolutePath());*/

                //crop anh da resize ve kich thuoc medium, nen chi can upload len
                imgPath = mCurrentPhotoPath;

                boolean isSuccessed = uploadImageToAzure(original, imgPath);
                if (isSuccessed) {
                    uploadNumber += 1;
                }
            }

            //upload medium
            SasChild medium = parent.getMedium();
            if (medium != null) {

                /*Bitmap b = MyUtils.resizeAndRotateImage(imgPath, ImageSize.MEDIUM_WIDTH, ImageSize.MEDIUM_WIDTH);
                MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                imgPath = MyUtils.saveBitmap(b, imgPath);*/

                //crop anh da resize ve kich thuoc medium, nen chi can upload len
                imgPath = mCurrentPhotoPath;

                boolean isSuccessed = uploadImageToAzure(medium, imgPath);
                if (isSuccessed) {
                    uploadNumber += 1;
                }
            }

            //upload small
            SasChild small = parent.getSmall();
            if (small != null) {

                Bitmap b = MyUtils.resizeAndRotateImage(imgPath, ImageSize.SMALL_WIDTH, ImageSize.SMALL_WIDTH);
                MyUtils.log("image_size " + b.getWidth() + " - " + b.getHeight());
                imgPath = MyUtils.saveBitmap(b, imgPath);

                boolean isSuccessed = uploadImageToAzure(small, imgPath);
                if (isSuccessed) {
                    uploadNumber += 1;
                }
            }

            //neu == 3 thi thanh cong
            if (uploadNumber == 3) {
                return true;
            } else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {

                String query = Webservices.UPDATE_USER_AVATAR(GUID, extension);
                //sau khi upload thanh cong
                AndroidNetworking.post(Webservices.URL_GRAPHQL)
                        .addQueryParameter("query", query)
                        .setOkHttpClient(MyApplication.getClient())
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
//                                MyUtils.log("ok");
                                //upload thanh cong
                                MyUtils.showToast(context, R.string.update_success);
                                //sau do goi lai login de lay User.class
                                initCookie();

                            }

                            @Override
                            public void onError(ANError ANError) {
                                MyUtils.log(ANError.getMessage());
                                if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                                }
                            }
                        })
                ;
            } else {
                MyUtils.showToast(context, R.string.uploadfail);
            }
            ProgressUtils.hide();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean uploadImageToAzure(SasChild child, String imgPath) {
        if (child != null) {
            String sasLink = child.getSAS();
            try {
                //Return a reference to the blob using the SAS URI.
                CloudBlockBlob blob = new CloudBlockBlob(new StorageUri(URI.create(sasLink)));
                //blob.setStreamWriteSizeInBytes(256 * 1024);//256 k


                OutputStream oStream = blob.openOutputStream();
                File f = new File(imgPath);
                FileInputStream inputStream = new FileInputStream(f);
                blob.upload(inputStream, f.length());

                //upload properties
                blob.getProperties().setContentType("image/jpeg");//File : "application/octet-stream"
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


    private void getSocialInfo(long userId) {
        String query = Webservices.GET_USER_SOCIAL(userId);
        //sau khi upload thanh cong
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        MyUtils.log("ok");
                        //{"data":{"User":{"UserId":7,"UserName":"phuongphammtp","SocialInfo":[{"NameCode":"Web","Link":"myweb.com"},{"NameCode":"Facebook","Link":""},{"NameCode":"Twitter","Link":""},{"NameCode":"Instagram","Link":""},{"NameCode":"Youtube","Link":""}]}}}
                        if (response != null) {
                            try {
                                String[] arrSocial = getResources().getStringArray(R.array.arr_socials);

                                JSONObject data = response.getJSONObject("data");
                                JSONObject user = data.getJSONObject("User");
                                JSONArray socials = user.getJSONArray("SocialInfo");
                                for (int i = 0; i < socials.length(); i++) {
                                    JSONObject item = socials.getJSONObject(i);
                                    String name = item.getString("NameCode");
                                    String link = item.getString("Link");


                                    if (name.equalsIgnoreCase(arrSocial[0])) {
                                        txt9.setText(link);
                                    } else if (name.equalsIgnoreCase(arrSocial[1])) {
                                        txt5.setText(link);
                                    } else if (name.equalsIgnoreCase(arrSocial[2])) {
                                        txt6.setText(link);
                                    } else if (name.equalsIgnoreCase(arrSocial[3])) {
                                        txt7.setText(link);
                                    } else if (name.equalsIgnoreCase(arrSocial[4])) {
                                        txt8.setText(link);
                                    } else if (name.equalsIgnoreCase(arrSocial[5])) {
                                        txt11.setText(link);
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                    }
                });
    }


    private void initCookie() {
        if (MyUtils.checkInternetConnection(context)) {
            //tao cookie truoc khi su dung
            MyApplication.initCookie(getApplicationContext()).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getResult() != null) {
                        user = (User) task.getResult();


                        //cap nhat ten o man hinh user profile va menu Main, va fragment_1 newsfeed
                        Intent intent = new Intent(Fragment_Profile_Owner.ACTION_REFRESH);
                        sendBroadcast(intent);

                        intent = new Intent(MH22_Fragment_Setting.ACTION_REFRESH_USER_INFO);
                        sendBroadcast(intent);

                        //2 tab Fragment_1_Home_SonTung va Fragment_1_Home_User
                        intent = new Intent(Fragment_1_Home_User.ACTION_REFRESH);
                        sendBroadcast(intent);

                        intent = new Intent(MH01_MainActivity.ACTION_CHANGE_ICON);
                        sendBroadcast(intent);


                        initUser();


                    }
                    return null;
                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_CODE_PICKER = 13;

    private void changeAvatar() {
        ImagePicker.create(this)
                .single() // single mode
                .imageTitle(getText(R.string.tap_to_select_image).toString())
                //.limit(1) // max images can be selected
                .showCamera(true) // show camera or not (true by default)
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICKER && data != null) {
                ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                // do your logic ....
                if (images != null && images.size() > 0) {
                    MyUtils.showToastDebug(context, images.get(0).getPath());
                    mCurrentPhotoPath = images.get(0).getPath();
                    cropImage();
                }

            }

            /*if (requestCode == ACTION_PHONE_VALIDATE) {
                Bundle b = data.getExtras();
                String validPhone = b.getString(User.PHONE, "");
                String code = b.getString(User.AUTHORIZATION_CODE);

                //so xac thuc vao so nhap la trung nhau -> OK
                if (validPhone.equals(phone) || validPhone.equals("0" + phone)) {
                    //phone khong doi
                    phone = validPhone;
                    //tien hanh update
                    updateUserProfile();
                } else {
                    //So xac thuc va so nhap khac nhau
                    MyUtils.showAlertDialog(context, R.string.phone_input_and_phone_validate_not_match);
                }
            }*/
        }

        //ucrop
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            mCurrentPhotoPath = resultUri.getPath();
            //update server mCurrentPhotoPath
            uploadImageToServer(mCurrentPhotoPath);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            MyUtils.showAlertDialog(context, cropError.getMessage());
        }

        //phone
        if (requestCode == ACTION_PHONE_VALIDATE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (response == null) {
                MyUtils.showAlertDialog(context, R.string.error_verification);
                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String phone = user.getPhoneNumber();

                    //cung so dt thi moi xu ly tiep
                    if (phone.equalsIgnoreCase(phoneRequest)) {
                        androidx.appcompat.app.AlertDialog loadingDialog = MyUtils.createDialogLoading(context, R.string.reading_info);
                        loadingDialog.show();
                        user.getIdToken(true).addOnCompleteListener(activity, new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<GetTokenResult> task) {
                                loadingDialog.dismiss();
                                //verify in server
                                String token = task.getResult().getToken();
                                if (!TextUtils.isEmpty(token)) {
                                    Webservices.verifyPhone(token, context).continueWith(new Continuation<Object, Void>() {
                                        @Override
                                        public Void then(Task<Object> task) throws Exception {
                                            if (task.getError() == null) {//ko co exception
                                                boolean success = (boolean) task.getResult();
                                                if (success) {
                                                    //load lai profile
                                                    initCookie();
                                                }
                                            } else {
                                                MyUtils.showToast(context, task.getError().getMessage());
                                            }
                                            return null;
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        //khac so dt thi ko tinh
                        //So xac thuc va so nhap khac nhau
                        MyUtils.showAlertDialog(context, R.string.phone_input_and_phone_validate_not_match);
                    }

                }

            }
        }

    }

    //#region SETUP TOOLBAR////////////////////////////////////////////////////////////////////////
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;
    @BindView(R.id.title)
    TextView title;
    public @BindView(R.id.imgShop)
    ImageView imgShop;

    private void setTitleBar() {
        setSupportActionBar(toolbar);
        title.setText(getText(R.string.edit_your_profile));
        //hide icon navigation
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MH01_MainActivity.isExist) {
                    startActivity(new Intent(context, MH01_MainActivity.class));
                }
                finish();
            }
        });
        relativeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainChatActivity.class);
                startActivity(intent);
            }
        });
        imgShop.setVisibility(View.VISIBLE);
        //imgShop is save info
        imgShop.setImageResource(R.drawable.ic_svg_25);
        imgShop.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        imgShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(context, ShoppingActivity.class));
                updateProfile();
            }
        });
        ChatUtils.setChatUnreadNumber(txtNumber);
        registerReceiver();
    }

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);

        registerReceiver(receiver, filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        context = null;
    }

    //#endregion///////////////////////////////////////////////////////////////////////////////////

    //CROP IMAGE////
    private int sizeCrop = 100;
    String mCurrentPhotoPath = "";

    private void cropImage() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
            File f = new File(mCurrentPhotoPath);
            if (f.exists()) {
                Uri uri = Uri.fromFile(f);
                File target = MyUtils.createImageFile(context);
                sizeCrop = ImageSize.MEDIUM_WIDTH;//getResources().getDimensionPixelSize(R.dimen.photo_profile_larger) * 2;
                UCrop.Options opt = new UCrop.Options();
                opt.setCircleDimmedLayer(true);

                UCrop.of(uri, Uri.fromFile(target))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(sizeCrop, sizeCrop)
                        .withOptions(opt)
                        .start(MH20_UserEditProfileActivity.this);
            }
        }


    }
}
