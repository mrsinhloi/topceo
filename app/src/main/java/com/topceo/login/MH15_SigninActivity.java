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

import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.language.LocalizationUtil;
import com.topceo.login.workchat.ui.InputPhoneActivityWc;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.MyValidator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class MH15_SigninActivity extends AppCompatActivity {
    public static final int REQUEST_SIGNUP = 10;
    public static final int ACTION_PHONE_VALIDATE = 12;
    public static final int ACTION_COMPLETE_SIGNUP = 13;


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey(IS_OPEN_SIGN_UP)) {
            boolean isSignup = b.getBoolean(IS_OPEN_SIGN_UP, false);
            if (isSignup) {
                signUp();
            }
        }

        db = new TinyDB(this);
        initUI();


        registerReceiver();

        if (BuildConfig.DEBUG) {
            _emailText.setText("phuongpham");
            _passwordText.setText("123456");
        }

        ///////////////////////////////////////////////////////////////////////////////////////


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
                    startActivity(new Intent(context, MH17_ForgetPasswordActivity.class));
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

        Intent intent = new Intent(context, InputPhoneActivityWc.class);
        intent.putExtra(InputPhoneActivityWc.VALIDATE_IN_LOCAL, false);
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
                case ACTION_PHONE_VALIDATE:
                    Bundle b = data.getExtras();
                    String code = b.getString(User.AUTHORIZATION_CODE);
                    loginByFirebase(code);
                    break;
                case ACTION_COMPLETE_SIGNUP:
                    b = data.getExtras();
                    User user = b.getParcelable(User.USER);
                    whenHaveUser(user);
                    break;
            }

        }
    }


    @Override
    public void onBackPressed() {
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

        //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
        MyApplication.whenLoginSuccess();

        _loginButton.setEnabled(true);

        //vao main de load cache truoc
        gotoMain();

        //dong man hinh welcome
        sendBroadcast(new Intent(WelcomeActivity.ACTION_FINISH));
        finish();

    }

    private void gotoMain() {
        startActivity(new Intent(MH15_SigninActivity.this, MH01_MainActivity.class));
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

    private void loginByFirebase(String code) {

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
                                        whenHaveUser(user);
                                    }

                                } else if (result.getErrorCode() == ReturnResult.ERROR_CODE_COMPLETE_SIGNUP) {
                                    if (result.getData() != null) {
                                        User user = (User) result.getData();
                                        Intent intent = new Intent(context, MH16_SignupActivity.class);
                                        intent.putExtra(User.USER, user);
                                        startActivityForResult(intent, ACTION_COMPLETE_SIGNUP);
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

    private void whenHaveUser(User user) {
        if (user != null) {
            String token = user.getCoreChatCustomToken();
            MyApplication.saveTokenChat(token);
            db.putBoolean(TinyDB.IS_LOGINED, true);
            db.putObject(User.USER, user);
            onLoginSuccess();
        }
    }
}

