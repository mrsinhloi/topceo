package com.topceo.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.myxteam.phone_verification.MH01_Input_Phone;
import com.myxteam.phone_verification.MH02_Input_Code;
import com.myxteam.phone_verification.MyExtensionKt;
import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.language.LocalizationUtil;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.MyValidator;
import com.topceo.utils.ProgressUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class MH15_SigninActivity extends AppCompatActivity {
    public static final int REQUEST_SIGNUP = 11;
    public static final int ACTION_PHONE_VALIDATE = 12;
    public static final int ACTION_COMPLETE_SIGNUP = 13;
    public static final int ACTION_PHONE_VALIDATE_AND_CHECK_EXIST = 14;
    public static final int ACTION_PHONE_VALIDATE_FOR_FORGET_PASSWORD = 15;


    public static final String IS_OPEN_SIGN_UP = "IS_OPEN_SIGN_UP";
    private Activity context = this;
    private static final String TAG = "MH15_SigninActivity";


    @BindView(R.id.input_email)
    TextInputEditText _emailText;
    @BindView(R.id.input_password)
    TextInputEditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    @BindView(R.id.forget_password)
    TextView forget_password;
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


    private void testValidate() {

        boolean isOK = false;
        String name = "";

        name = "Test1";
        isOK = MyValidator.validateUsername(name);

        name = "test1,";
        isOK = MyValidator.validateUsername(name);

        name = "test1_hello";
        isOK = MyValidator.validateUsername(name);

        name = "test_1.you";
        isOK = MyValidator.validateUsername(name);

        name = "test_1.";
        isOK = MyValidator.validateUsername(name);
    }

    private String phoneTemp = "";
    private String passTemp = "";
    private TinyDB db;


    boolean isSignup = false;

    private void initIntent(Intent intent) {
        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b != null && b.containsKey(IS_OPEN_SIGN_UP)) {
                isSignup = b.getBoolean(IS_OPEN_SIGN_UP, false);
                if (isSignup) {
                    signUp();
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initIntent(getIntent());
        db = new TinyDB(this);
        initUI();


        registerReceiver();

        if (BuildConfig.DEBUG) {
            _emailText.setText("phuongpham");
            _passwordText.setText("123456");
        }

        ///////////////////////////////////////////////////////////////////////////////////////
        _emailText.requestFocus();
        MyExtensionKt.focusAndShowKeyboard(_emailText);

    }

    boolean isVietnamese = true;

    //    @BindView(R.id.group1)
//    RadioGroup group1;
    private void initLanguage() {
        //languages
        isVietnamese = LocalizationUtil.INSTANCE.isVietnamese(context);
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

        /*group1.setOnCheckedChangeListener(null);
        if (languageSelected.equals(LocaleHelper.LANGUAGE_TIENG_VIET)) {
            group1.check(R.id.radio1);
        } else {
            group1.check(R.id.radio2);
        }
        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        if (!languageSelected.equals(LocaleHelper.LANGUAGE_TIENG_VIET)) {
                            languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET;
                            db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected);
                            LocaleHelper.setLocale(context, languageSelected);
                            initUI();
                        }
                        break;

                    case R.id.radio2:
                        if (!languageSelected.equals(LocaleHelper.LANGUAGE_ENGLISH)) {
                            languageSelected = LocaleHelper.LANGUAGE_ENGLISH;
                            db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected);
                            LocaleHelper.setLocale(context, languageSelected);
                            initUI();
                        }
                        break;
                }
            }
        });*/

        //edittext
        if (!TextUtils.isEmpty(phoneTemp)) {
            _emailText.setText(phoneTemp);
        }
        _emailText.addTextChangedListener(new TextWatcher() {
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

        if (!TextUtils.isEmpty(passTemp)) {
            _passwordText.setText(passTemp);
        }
        _passwordText.addTextChangedListener(new TextWatcher() {
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


    }

    private void initUI() {
        //set language first
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
//        MyUtils.transparentStatusBar(getWindow());

        initLanguage();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo goi email de lay lai pass
                if (MyUtils.checkInternetConnection(context)) {
                    /*Intent intent = new Intent(context, MH01_InputPhoneNumber_Activity.class);
                    intent.putExtra(User.IS_RESET_PASSWORD, true);
                    startActivityForResult(intent, MH01_InputPhoneNumber_Activity.REQUEST_VALID_NUMBER_PHONE);*/
//                    startActivity(new Intent(context, MH17_ForgetPasswordActivity.class));

                    /*Intent intent = new Intent(context, InputPhoneActivityWc.class);
                    intent.putExtra(InputPhoneActivityWc.VALIDATE_IN_LOCAL, false);
                    intent.putExtra(InputPhoneActivityWc.IS_FORGET_PASSWORD, true);
                    startActivityForResult(intent, ACTION_PHONE_VALIDATE_FOR_FORGET_PASSWORD);
                    */

                    Intent intent = new Intent(context, MH01_Input_Phone.class);
                    intent.putExtra(MH01_Input_Phone.IS_FORGET_PASSWORD, true);
                    startActivityForResult(intent, ACTION_PHONE_VALIDATE_AND_CHECK_EXIST);
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


    }

    private void login() {
        Log.d(TAG, "Login");
        if (!validate()) {
            MyUtils.showAlertDialog(context, R.string.please_input_info);
            onLoginFailed();
            return;
        }

        username = _emailText.getText().toString().toLowerCase();
        final String password = _passwordText.getText().toString();

        LoginUtils utils = new LoginUtils(this, event);
        utils.login(username, password);
    }


    private void signUp() {
        /*Intent intent = new Intent(getApplicationContext(), MH16_SignupActivity.class);//MH16_SignupActivity.class
        startActivityForResult(intent, REQUEST_SIGNUP);*/

        /*Intent intent = new Intent(getApplicationContext(), MH01_InputPhoneNumber_Activity.class);//MH16_SignupActivity.class
        intent.putExtra(User.IS_SIGN_UP, true);
        startActivity(intent);*/

        /*Intent intent = new Intent(context, InputPhoneActivityWc.class);
        intent.putExtra(InputPhoneActivityWc.VALIDATE_IN_LOCAL, false);
        startActivityForResult(intent, ACTION_PHONE_VALIDATE);*/

        Intent intent = new Intent(context, MH01_Input_Phone.class);
        intent.putExtra(MH01_Input_Phone.IS_VALIDATE_IN_LOCAL, false);
        startActivityForResult(intent, ACTION_PHONE_VALIDATE);

    }


    private boolean isLoginByEmail = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SIGNUP:
                    // TODO: Implement successful signup logic here
                    // By default we just finish the Activity and log them in automatically
                    if (data != null) {
                        isLoginByEmail = data.getBooleanExtra(MH16_SignupActivity.IS_SIGNUP_BY_EMAIL, false);
                    }

                    if (isLoginByEmail) {
                        TinyDB db = new TinyDB(MH15_SigninActivity.this);
                        String email = db.getString(TinyDB.USER_NAME);
                        String password = db.getString(TinyDB.USER_PASSWORD);

                        //fill
                        _emailText.setText(email);
                        _passwordText.setText(password);
                        login();
                    }
                    break;

                case ACTION_COMPLETE_SIGNUP:
                    Bundle b = data.getExtras();
                    User user = b.getParcelable(User.USER);
                    if(user!=null){
                        MyUtils.whenHaveUser(user);
                        onLoginSuccess();
                    }
                    break;


                case ACTION_PHONE_VALIDATE_AND_CHECK_EXIST://#step1
                    b = data.getExtras();
                    String phoneNumber = b.getString(MH01_Input_Phone.PHONE_NUMBER);
                    //kiem tra ton tai
                    checkMobileExisted(phoneNumber);
                    break;
                case ACTION_PHONE_VALIDATE:
                    b = data.getExtras();
                    boolean isRequest = isRequestVerifyEmail(b);
                    if (isRequest) {
                        requestVerifyByEmail(b);
                    } else {
                        String code = b.getString(MH01_Input_Phone.AUTHORIZATION_CODE);
                        loginByFirebase(code, false);
                    }

                    break;
                case ACTION_PHONE_VALIDATE_FOR_FORGET_PASSWORD://#step2
                    b = data.getExtras();
                    isRequest = isRequestVerifyEmail(b);
                    if (isRequest) {
                        requestVerifyByEmail(b);
                    } else {
                        b = data.getExtras();
                        String code = b.getString(MH01_Input_Phone.AUTHORIZATION_CODE);
                        loginByFirebase(code, true);
                    }
                    break;
            }

        } else {
            switch (requestCode) {
                case ACTION_PHONE_VALIDATE:
                    //vao xac thuc de dang ky, nhung back ve thi thoat khoi man hinh login nay
                    if (isSignup) {
                        finish();
                    }
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String phone = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            valid = false;
        }

        return valid;
    }


    private User user;
    private String username = "";


    //////////////////////////////////////////////////////////////////////////////////////////////
    public static final String FACEBOOK_ID = "FACEBOOK_ID";
    public static final String LOGIN_BY_FACEBOOK = "LOGIN_BY_FACEBOOK";
    public static final String FINISH_ACTIVITY = "FINISH_ACTIVITY_" + MH15_SigninActivity.class.getSimpleName();
    public static final String AUTO_LOGIN_BY_PHONE_AND_PASSWORD = "AUTO_LOGIN_BY_PHONE_AND_PASSWORD_" + MH15_SigninActivity.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(LOGIN_BY_FACEBOOK)) {
                    if (b != null) {
                        String facebookId = b.getString(FACEBOOK_ID, "");
                        LoginUtils utils = new LoginUtils(MH15_SigninActivity.this, event);
                        utils.loginFacebook(facebookId);
                    }
                }

                if (intent.getAction().equalsIgnoreCase(FINISH_ACTIVITY)) {
                    finish();
                }

                if (intent.getAction().equalsIgnoreCase(AUTO_LOGIN_BY_PHONE_AND_PASSWORD)) {
                    if (b != null) {
                        String phone = b.getString(User.PHONE, "");
                        String password = b.getString(User.PASSWORD, "");
                        if (!TextUtils.isEmpty(phone) &&
                                !TextUtils.isEmpty(password)) {
                            //dien vao form va tu dong dang nhap
                            _emailText.setText(phone);
                            _passwordText.setText(password);
                            login();
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(LOGIN_BY_FACEBOOK));
        registerReceiver(receiver, new IntentFilter(FINISH_ACTIVITY));
        registerReceiver(receiver, new IntentFilter(AUTO_LOGIN_BY_PHONE_AND_PASSWORD));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
        MyApplication.whenLoginSuccess();
        //dong man hinh welcome
        sendBroadcast(new Intent(WelcomeActivity.ACTION_FINISH));
        //vao main de load cache truoc
        MyUtils.gotoMain(context);
        finish();
    }



    private LoginEvent event = new LoginEvent() {
        @Override
        public void whenloginByIdTokenSuccess() {
            onLoginSuccess();
        }

        @Override
        public void whenLoginByIdTokenFail() {

        }
    };

    /////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onResume() {
        super.onResume();
        boolean newVietnamese = LocalizationUtil.INSTANCE.isVietnamese(this);
        if (isVietnamese != newVietnamese) {
            initUI();
        }
    }


    private void completeSignUp(User user) {
        if (user != null) {
            Intent intent = new Intent(context, MH16_SignupActivity.class);
            intent.putExtra(User.PHONE, user.getPhone());
            intent.putExtra(User.TOKEN, user.getToken());
            startActivityForResult(intent, ACTION_COMPLETE_SIGNUP);
        }
    }


    private void loginByFirebase(String code, boolean isForgetPassword) {

        if (!TextUtils.isEmpty(code)) {
            if (MyUtils.checkInternetConnection(context)) {

                AlertDialog dialog = MyUtils.createDialogLoading(this, R.string.logging_in);
                dialog.show();

                MyApplication.apiManager.loginByFirebase(code, new Callback<JsonObject>() {
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
                                        //neu da co username thi co tai khoan, vao main
                                        if (user.getUserId() > 0) {
                                            if (isForgetPassword) {
                                                //lay token config vao cookie
                                                db.putObject(User.USER, user);
                                                //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
                                                MyApplication.whenLoginSuccess();

                                                //nhap mat khau moi
                                                Intent intent = new Intent(context, ResetPasswordActivity.class);
                                                intent.putExtra(User.PHONE, user.getPhone());
                                                startActivity(intent);
                                            } else {
                                                MyUtils.whenHaveUser(user);
                                                onLoginSuccess();
                                            }
                                        } else {
                                            //nguoc lai thi vao hoan tat dang ky
                                            completeSignUp(user);
                                        }
                                    }

                                } else if (result.getErrorCode() == ReturnResult.ERROR_CODE_COMPLETE_SIGNUP) {
                                    if (result.getData() != null) {
                                        User user = (User) result.getData();
                                        completeSignUp(user);
                                    }
                                } else {
                                    //tai khoan da ton tai, thi dang nhap
                                    MyUtils.showAlertDialog(context, result.getErrorMessage());
                                }
                            }
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
            } else {
                MyUtils.showThongBao(context);
            }
        }

    }


    private void checkMobileExisted(String phone) {
        if (!TextUtils.isEmpty(phone)) {
            if (MyUtils.checkInternetConnection(this)) {
                ProgressUtils.show(this);
                MyApplication.apiManager.checkPhoneExists(phone, new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject body = response.body();
                        if (body != null) {
                            ReturnResult result = Webservices.parseJson(
                                    body.toString(),
                                    Boolean.class, false
                            );
                            if (result != null) {
                                //Thanh cong thi tra ve UserMBN -> qua man hinh nhap thong tin
                                if (result.getErrorCode() == ReturnResult.SUCCESS) { //da ton tai thi vao
                                    if (result.getData() != null) {
                                        boolean exist = (boolean) result.getData();
                                        if (exist) {
                                            /*Intent intent = new Intent(context, MH01_Input_Phone.class);
                                            intent.putExtra(MH01_Input_Phone.IS_VALIDATE_IN_LOCAL, false);
                                            startActivityForResult(intent, ACTION_PHONE_VALIDATE_FOR_FORGET_PASSWORD);*/
                                            //verify code
                                            Intent intent = new Intent(context, MH02_Input_Code.class);
                                            intent.putExtra(MH01_Input_Phone.IS_VALIDATE_IN_LOCAL, false);
                                            intent.putExtra(MH01_Input_Phone.IS_FORGET_PASSWORD, true);
                                            intent.putExtra(MH01_Input_Phone.PHONE_NUMBER, phone);
                                            startActivityForResult(intent, ACTION_PHONE_VALIDATE_FOR_FORGET_PASSWORD);
                                        } else {
                                            String text = getString(
                                                    R.string.phone_not_exists_please_chose_another_phone,
                                                    phone
                                            );
                                            MyUtils.showAlertDialog(context, text);
                                        }
                                    }
                                } else {
                                    //tai khoan da ton tai, thi dang nhap
                                    MyUtils.showAlertDialog(context,
                                            result.getErrorMessage()
                                    );
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
                MyUtils.showThongBao(this);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isRequestVerifyEmail(Bundle b) {
        return b.getBoolean(MH01_Input_Phone.IS_REQUEST_VERIFY_BY_EMAIL, false);
    }

    private void requestVerifyByEmail(Bundle b) {
        String number = b.getString(MH01_Input_Phone.PHONE_NUMBER, "");
        boolean isForgetPassword = b.getBoolean(MH01_Input_Phone.IS_FORGET_PASSWORD, false);
        if(isForgetPassword){//quen mat khau
//            MyUtils.showAlertDialog(context, "Quen mat khau");
        }else{//dang ky moi
//            MyUtils.showAlertDialog(context, "Dang ky moi");
        }

        MH18_EmailVerifyActivity1.start(context, number, isForgetPassword);
    }


}

