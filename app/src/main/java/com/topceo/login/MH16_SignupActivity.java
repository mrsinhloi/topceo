package com.topceo.login;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.language.LocalizationUtil;
import com.topceo.objects.other.User;
import com.topceo.services.CookieStore;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class MH16_SignupActivity extends AppCompatActivity {
    private Activity context = this;
    private static final String TAG = "MH16_SignupActivity";

    @BindView(R.id.txt1)
    TextInputEditText txt1;
    @BindView(R.id.txt2)
    TextInputEditText txt2;
    @BindView(R.id.txt3)
    TextInputEditText txt3;
    @BindView(R.id.txt4)
    TextInputEditText txt4;
    @BindView(R.id.txt6)
    TextView txt6;
    @BindView(R.id.btn1)
    Button btn1;

    @BindView(R.id.spinner1)
    Spinner spinner1;
    @Nullable
    @BindView(R.id.imgClose)
    ImageView imgClose;

    @BindView(R.id.txtVietnamese)
    TextView txtVietnamese;
    @BindView(R.id.txtEnglish)
    TextView txtEnglish;

    private void changeLanguage(boolean isVietnamese) {
        if (isVietnamese) {
            txtVietnamese.setTextColor(ContextCompat.getColor(context, R.color.sky_end));
            txtEnglish.setTextColor(ContextCompat.getColor(context, R.color.grey_500));
        } else {
            txtVietnamese.setTextColor(ContextCompat.getColor(context, R.color.grey_500));
            txtEnglish.setTextColor(ContextCompat.getColor(context, R.color.sky_end));
        }
    }

    private String usernameTemp = "";
    private String passTemp = "";
    private String emailTemp = "";
    private String phoneTemp = "";
    private int positionTemp = 0;
    private TinyDB db;

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TinyDB(this);

        user = getIntent().getParcelableExtra(User.USER);
        initUI();

    }

    private void initUI() {
        //set language first
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
//        MyUtils.transparentStatusBar(getWindow());

        initLanguage();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////
        // We can logout from facebook by calling following method
//        LoginManager.getInstance().logOut();


        txt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        int[] valueInt = getResources().getIntArray(R.array.arr_gender_value);
        gender = valueInt[0];
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = valueInt[position];
                positionTemp = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (positionTemp > 0) {
            gender = valueInt[positionTemp];
            spinner1.setSelection(positionTemp);
        }


        txtFocused = txt1;
        setFocusChange(txt1);
        setFocusChange(txt2);
        setFocusChange(txt3);
        setFocusChange(txt4);

        if (user != null) {
            txt4.setText(user.getPhone());
        }

    }

    private void initLanguage() {
        //languages
        boolean isVietnamese = LocalizationUtil.INSTANCE.isVietnamese(context);
        changeLanguage(isVietnamese);
        txtVietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVietnamese) {
                    db.putString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_VI);
                    MyApplication.initLanguage(context);
                    initUI();
                }
            }
        });
        txtEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVietnamese) {
                    db.putString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_EN);
                    MyApplication.initLanguage(context);
                    initUI();
                }
            }
        });


        //username
        if (!TextUtils.isEmpty(usernameTemp)) {
            txt2.setText(usernameTemp);
        }
        txt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameTemp = s.toString();
            }
        });

        //password
        if (!TextUtils.isEmpty(passTemp)) {
            txt3.setText(passTemp);
        }
        txt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passTemp = s.toString();
            }
        });

        //email
        if (!TextUtils.isEmpty(emailTemp)) {
            txt1.setText(emailTemp);
        }
        txt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailTemp = s.toString();
            }
        });

        //phone
        if (!TextUtils.isEmpty(phoneTemp)) {
            txt4.setText(phoneTemp);
        }
        txt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneTemp = s.toString();
            }
        });

    }


    TextInputEditText txtFocused;

    private void setFocusChange(TextInputEditText txt) {
        if (txt != null) {
            txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        if (txt.getId() != txtFocused.getId()) {
                            String text = txtFocused.getText().toString().trim();
                            if (!TextUtils.isEmpty(text)) {
                                validate();
                            }
                        }
                        txtFocused = txt;
                    }
                }
            });
        }
        /*if (txt != null) {
            txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT ||
                            actionId == EditorInfo.IME_ACTION_DONE) {
                        String text = v.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)) {
                            validate();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }*/
    }

    private String emailFacebook = "";

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    String email;
    String name;
    String password;
    String phone;
    int gender = -1;

    private void readData() {
        email = txt1.getText().toString().trim();
        name = txt2.getText().toString().trim();
        password = txt3.getText().toString().trim();
        phone = txt4.getText().toString().trim();
        /*if (!TextUtils.isEmpty(phone)) {
            phone = PhoneUtils.getE164FormattedMobileNumber(phone, PhoneUtils.getDefaultCountryNameCode());
        }*/
    }

    public void signup() {

        if (!validate()) {
            return;
        }


        // TODO: Implement your own signup logic here.
        readData();
//        signupByEmail();
        loginByFirebase();


    }


    public static final String IS_SIGNUP_BY_EMAIL = "IS_SIGNUP_BY_EMAIL";
    private boolean isSignupByEmail = false;

    public void onSignupSuccess() {
        MyUtils.showToast(context, R.string.sign_up_success);

        btn1.setEnabled(true);

        //goi lai de login
        Intent data = new Intent();
        data.putExtra(IS_SIGNUP_BY_EMAIL, isSignupByEmail);
        setResult(RESULT_OK, data);

        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.please_check_input_login, Toast.LENGTH_LONG).show();
        btn1.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        readData();


        if (name.isEmpty() || name.length() < 5) {
            txt2.setError(getText(R.string.name_request_lenght));
            txt2.requestFocus();
            valid = false;
            return valid;
        } else {
            txt2.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            txt3.setError(getText(R.string.password_request_lenght));
            txt3.requestFocus();
            valid = false;
            return valid;
        } else {
            txt3.setError(null);
        }

        /*if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txt1.setError(getText(R.string.invalid_email));
            txt1.requestFocus();
            valid = false;
            return valid;
        } else {
            txt1.setError(null);
        }*/


        if (TextUtils.isEmpty(phone)) {
            txt4.setError(getText(R.string.phone_invalid));
            txt4.requestFocus();
            valid = false;
            return valid;
        } else {
            txt4.setError(null);
        }


        return valid;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    /*private void signupByEmail() {
        MyUtils.hideKeyboard(context);
        ProgressUtils.show(context);

        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.URL + "user/signup")
                .addBodyParameter("Email", email)
                .addBodyParameter("UserName", name)
                .addBodyParameter("Password", password)
                .addBodyParameter("Phone", phone)
                .addBodyParameter("Gender", String.valueOf(gender))
                .addBodyParameter("OS", Webservices.OS)
                .addBodyParameter("CountryId", Webservices.COUNTRY_ID)
                .addBodyParameter("CountryName", Webservices.COUNTRY_NAME)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyUtils.howLong(start, "signup");
                        ReturnResult result = Webservices.parseJson(response, null, false);


                        if (result != null) {
                            //{"ErrorCode":107,"Message":{"Invalid":["UserName"],"Existed":[]}}
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                //save thong tin
                                TinyDB db = new TinyDB(context);
                                db.putString(TinyDB.USER_NAME, name);
                                db.putString(TinyDB.USER_EMAIL, email);
                                db.putString(TinyDB.USER_PASSWORD, password);
//                                db.putBoolean(TinyDB.IS_LOGINED, true);

                                isSignupByEmail = true;


                                //login
                                LoginUtils utils = new LoginUtils(MH16_SignupActivity.this, new LoginEvent() {
                                    @Override
                                    public void whenloginByIdTokenSuccess() {
                                        ProgressUtils.hide();
                                        onLoginSuccess();
                                    }

                                    @Override
                                    public void whenLoginByIdTokenFail() {
                                        ProgressUtils.hide();
                                    }
                                });
                                utils.login(name, password);

                            } else {
                                ProgressUtils.hide();
                                String message = result.getErrorMessage();
                                if (result.getErrorCode() == ReturnResult.ERROR_CODE_SIGNUP) {
                                    if (!TextUtils.isEmpty(message)) {
                                        SignupError error = new Gson().fromJson(message, SignupError.class);
                                        if (error != null) {

                                            String params = error.getParams(error.getExisted());
                                            if (params != null) {
                                                String existed = getString(R.string.existed, params);
                                                MyUtils.showAlertDialog(context, existed);
                                            } else {
                                                params = error.getParams(error.getInvalid());
                                                if (params != null) {
                                                    String invalid = getString(R.string.invalid, params);
                                                    MyUtils.showAlertDialog(context, invalid);
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    if (!TextUtils.isEmpty(message)) {
                                        MyUtils.showAlertDialog(context, message);
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
    }*/


    /////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String imgPath = "";

    private void uploadImageToServer(String localPath) {
        if (!TextUtils.isEmpty(localPath)) {
            imgPath = localPath;

            //neu xoay thi xoay lai va luu vao bo nho, khac 0 thi se xoay lai
//            int rotation = MyUtils.getRotationForImage(imgPath);
//            imgPath = MyUtils.saveBitmap(MyUtils.resizeImage(imgPath, rotation, imageSize, imageSize), imgPath);

            File file = new File(imgPath);
            //chuan bi lay duong dan sas truoc khi upload anh len azure
            final String GUID = UUID.randomUUID().toString().toLowerCase();
            final String extension = MyUtils.getFileExtension(file);


            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.avatar_uploading));
            progressDialog.show();*/
            ProgressUtils.show(context);
            AndroidNetworking.post(Webservices.URL + "user/getAvatarUploadSAS")
                    .addQueryParameter("ItemGUID", GUID)
                    .addQueryParameter("ImageExtension", extension)
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ReturnResult result = Webservices.parseJson(response, String.class, false);
                            ProgressUtils.hide();

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                                    String sasLink = (String) result.getData();
                                    if (TextUtils.isEmpty(sasLink) == false) {
                                        Log.d("TAG", "SAS_LINK: " + sasLink);
                                        uploadImage(sasLink, GUID, extension);
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

    public void uploadImage(String sasLink, String guid, String extension) {
        if (uploadTask == null || uploadTask.getStatus() == AsyncTask.Status.FINISHED) {
            uploadTask = new UploadImageTask(guid, extension);
            uploadTask.execute(sasLink);
        }
    }

    private UploadImageTask uploadTask;

    private class UploadImageTask extends AsyncTask<String, Void, Boolean> {

        String GUID = "", extension = "";

        public UploadImageTask(String guid, String ext) {
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
        protected Boolean doInBackground(String... strings) {
            String sasLink = strings[0];
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
            return false;
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
                                MyUtils.log("ok");
                                //upload thanh cong
                                MyUtils.showToast(context, R.string.toast_upload_success);

                                //vao app
                                /*Intent intent = new Intent(MH15_SigninActivity.LOGIN_BY_FACEBOOK);
                                intent.putExtra(MH15_SigninActivity.FACEBOOK_ID, facebookId);
                                sendBroadcast(intent);*/
                                finish();

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Deprecated
    public void login(final String username, final String password) {
        /*final ProgressDialog progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.authenticating));
        progressDialog.show();*/

        ProgressUtils.show(context);

        MyApplication.client = new OkHttpClient.Builder()
                .cookieJar(new CookieStore())
                .build();

        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.URL + "user/login")
                .addBodyParameter("username", username)
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
                                db.putString(TinyDB.USER_NAME, username);
                                db.putString(TinyDB.USER_PASSWORD, password);
                                db.putBoolean(TinyDB.IS_LOGIN_BY_FACEBOOK, false);
                                db.putBoolean(TinyDB.IS_LOGINED, true);

                                //luu user
                                new AsyncTask<Void, Void, Void>() {
                                    User user;

                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        if (result.getData() != null) {
                                            user = (User) result.getData();
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
        sendBroadcast(new Intent(WelcomeActivity.ACTION_FINISH));
        finish();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //hoan tat dang ky
    private void loginByFirebase() {

        if (MyUtils.checkInternetConnection(context)) {

            MyUtils.hideKeyboard(context);
            ProgressUtils.show(context);

            String token = "bearer "+user.getToken();
            MyApplication.apiManager.signupComplete(
                    token,
                    name,
                    password,
                    gender,
                    new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null) {

                        ReturnResult result = Webservices.parseJson(obj.toString(), User.class, false);
                        if (result != null) {
                            //Thanh cong thi tra ve UserMBN -> qua man hinh nhap thong tin
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                if (result.getData() != null) {
                                    User user = (User) result.getData();
                                    Intent data = new Intent();
                                    data.putExtra(User.USER, user);
                                    setResult(RESULT_OK, data);
                                    finish();
                                }

                            } else {
                                //tai khoan da ton tai, thi dang nhap
                                MyUtils.showAlertDialog(context, result.getErrorMessage());
                            }
                        }
                    }
                    ProgressUtils.hide();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    ProgressUtils.hide();
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }

    }

}

