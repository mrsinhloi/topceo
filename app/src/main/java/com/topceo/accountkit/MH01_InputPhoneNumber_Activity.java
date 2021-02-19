/*
package com.ehubstar.accountkit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.ehubstar.BuildConfig;
import com.ehubstar.R;
import com.ehubstar.db.TinyDB;
import com.ehubstar.login.ResetPasswordActivity;
import com.ehubstar.login.MH15_SigninActivity;
import com.ehubstar.objects.other.User;
import com.ehubstar.services.ReturnResult;
import com.ehubstar.services.Webservices;
import com.ehubstar.utils.LocaleHelper;
import com.ehubstar.utils.MyUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH01_InputPhoneNumber_Activity extends AppCompatActivity {
    public static final int REQUEST_VALID_NUMBER_PHONE = 100;

    private Activity context = this;
    private TinyDB db;

    @BindView(R.id.editPhone)
    AppCompatEditText editPhone;
    @BindView(R.id.btnStart)
    AppCompatButton btnStart;
    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.group1)
    RadioGroup group;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    //Tu ben ngoai yeu cau xac thuc so dien thoai nay
    private String phoneInit = "";
    private boolean isResetPassword = false;
    private boolean isSignUp = false;

    private String phoneTemp = "";
    String languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET;

    private void init() {
        db = new TinyDB(this);

        //language
        */
/*languageSelected = db.getString(LocaleHelper.SELECTED_LANGUAGE, Locale.getDefault().getLanguage());
        if (languageSelected.equalsIgnoreCase(LocaleHelper.LANGUAGE_TIENG_VIET)) {
            LocaleHelper.setLocale(context, languageSelected);
        } else {
            LocaleHelper.setLocale(context, languageSelected);
        }*//*



        //UI
        setContentView(R.layout.mh01_input_phone_number);
        ButterKnife.bind(this);
        MyUtils.transparentStatusBar(getWindow());

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(phone)) {
                        if (isSignUp || isResetPassword) {
                            //dang ky hay reset pass thi check sdt truoc
                            checkPhoneExistsBeforeGotoAccountKit(phone);



                        } else {
                            //thi cu vao xac thuc va tra ve ket qua
                            //Mo account kit
                            phoneLogin(phone);
                        }

                    } else {
                        MyUtils.showToast(context, R.string.please_input_phone_correct);
                    }

                } else {
                    MyUtils.showToast(context, R.string.please_input_phone);
                }
            }
        });

        if (BuildConfig.DEBUG) {
//            editPhone.setText("0938936128");
        }

        if (!TextUtils.isEmpty(phoneTemp)) {
            editPhone.setText(phoneTemp);
        }
        editPhone.addTextChangedListener(new TextWatcher() {
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


        //language
        */
/*group.setOnCheckedChangeListener(null);
        if (languageSelected.equalsIgnoreCase(LocaleHelper.LANGUAGE_TIENG_VIET)) {
            group.check(R.id.radio1);
        } else {
            group.check(R.id.radio2);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        if (languageSelected != LocaleHelper.LANGUAGE_TIENG_VIET) {
                            languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET;
                            db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected);
                            LocaleHelper.setLocale(context, languageSelected);
                            init();
                        }
                        break;
                    case R.id.radio2:
                        if (languageSelected != LocaleHelper.LANGUAGE_ENGLISH) {
                            languageSelected = LocaleHelper.LANGUAGE_ENGLISH;
                            db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected);
                            LocaleHelper.setLocale(context, languageSelected);
                            init();
                        }
                        break;
                }
            }
        });*//*


        //Ben ngoai y/c xac thuc so dien thoai
        Bundle b = getIntent().getExtras();
        if (b != null) {
            phoneInit = b.getString(User.PHONE, "");
            if (!TextUtils.isEmpty(phoneInit)) {
                editPhone.setText(phoneInit);
            }
            isResetPassword = b.getBoolean(User.IS_RESET_PASSWORD, false);
            isSignUp = b.getBoolean(User.IS_SIGN_UP, false);
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static int APP_REQUEST_CODE = 99;
    private String phoneNumber = "";

    public void phoneLogin(String phone) {
        phoneNumber = phone;
        final Intent intent = new Intent(context, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...


        PhoneNumber number = new PhoneNumber("+84", phone, "VN");
        configurationBuilder.setInitialPhoneNumber(number);

        int theme = R.style.AppLoginTheme_Custom;
        UIManager uiManager = new ThemeUIManager(theme);
        configurationBuilder.setUIManager(uiManager);

        try {
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, APP_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        showProgress();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                String error = loginResult.getError().toString();
                MyUtils.showAlertDialog(context, error);
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
                hideProgress();
            } else {
                */
/*if (loginResult.getAccessToken() != null) {
                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();
                } else {
                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0, 10));
                }*//*


                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
                //ResponseType.TOKEN
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        if (account != null) {
                            String rawPhone = account.getPhoneNumber().getRawPhoneNumber();
                            String phone = account.getPhoneNumber().getPhoneNumber();
                            String phoneE164 = account.getPhoneNumber().toString();
                            String localPhone = PhoneUtils.getLocalFormattedMobileNumber(phoneE164);
                            //kiem tra sdt nhan duoc so voi sdt da nhap truoc do co matching voi nhau khong
                            //co the khi vao account kit user da doi sdt, nen kiem tra buoc nay

                            //neu local phone khong nhan dc thi dung phone tao local phone
                            if (TextUtils.isEmpty(localPhone) && !TextUtils.isEmpty(phone)) {
                                localPhone = "0" + phone;
                            }
                            if (!TextUtils.isEmpty(localPhone)) {
                                //Qua man hinh nhap thong tin ca nhan
                                //Kiem tra so dien thoai da ton tai chua
                                if (isResetPassword) {

                                    */
/*Intent data = new Intent();
                                    data.putExtra(User.PHONE, localPhone);//de kiem tra so voi so da nhap
                                    setResult(RESULT_OK, data);
                                    finish();*//*


                                    //vao man hinh reset pass
                                    //so xac thuc vao so nhap la trung nhau -> OK
                                    Intent intent = new Intent(context, ResetPasswordActivity.class);
                                    intent.putExtra(User.PHONE, localPhone);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    checkPhoneExists(localPhone);
                                }

                            } else {
                                MyUtils.showAlertDialog(context, R.string.phone_not_detect);
                            }

                        }
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        hideProgress();
                    }
                });


            }

            // Surface the result to your user in an appropriate way.
            */
/*Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG)
                    .show();*//*

        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkPhoneExists(final String phone) {
        */
/*final ProgressDialog progressDialog = new ProgressDialog(context,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.checking));
        progressDialog.show();*//*


        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.URL + "user/checkPhoneExists")
                .addQueryParameter("Phone", phone)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ReturnResult result = Webservices.parseJson(response, Boolean.class, false);
//                        progressDialog.dismiss();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                boolean isExists = (Boolean) result.getData();
                                if (!isExists) {


                                    //Yeu cau xac thuc so dien thoai tu ben ngoai
                                    if (!TextUtils.isEmpty(phoneInit)) {
                                        Intent data = new Intent();
                                        data.putExtra(User.PHONE, phone);//de kiem tra so voi so da nhap
                                        setResult(RESULT_OK, data);
                                        finish();
                                    } else {
                                        //cap nhat thong tin ca nhan
                                        Intent intent = new Intent(context, MH02_Update_Profile_Activity.class);
                                        intent.putExtra(User.PHONE, phone);
                                        startActivity(intent);

                                        if (isSignUp) {
                                            //neu la dang ky thi goi dong man hinh SiginActivity
                                            sendBroadcast(new Intent(MH15_SigninActivity.FINISH_ACTIVITY));
                                        }

                                        finish();
                                    }


                                } else {
                                    String notify = getString(R.string.phone_exists_please_chose_another_phone, phone);
                                    MyUtils.showAlertDialog(context, notify);
                                    hideProgress();
                                }

                            } else {
                                if (!TextUtils.isEmpty(result.getErrorMessage())) {
                                    MyUtils.showToast(context, result.getErrorMessage());

                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        progressDialog.dismiss();
                        if (ANError != null) {
                            MyUtils.showAlertDialog(context, ANError.getErrorBody());
                        }
                    }
                });
    }

    private void checkPhoneExistsBeforeGotoAccountKit(final String phone) {
        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.URL + "user/checkPhoneExists")
                .addQueryParameter("Phone", phone)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ReturnResult result = Webservices.parseJson(response, Boolean.class, false);
//                        progressDialog.dismiss();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                boolean isExists = (Boolean) result.getData();

                                //chua ton tai thi mo, ton tai thi thong bao
                                if (isSignUp) {
                                    if (!isExists) {
                                        //Mo account kit
                                        phoneLogin(phone);

                                    } else {
                                        String notify = getString(R.string.phone_exists_please_chose_another_phone, phone);
                                        MyUtils.showAlertDialog(context, notify);
                                        hideProgress();
                                    }
                                } else if (isResetPassword) {
                                    //so ton tai thi moi reset, nguoc lai thong bao
                                    if (isExists) {
                                        //Mo account kit
                                        phoneLogin(phone);

                                    } else {
                                        String notify = getString(R.string.phone_not_exists_please_chose_another_phone, phone);
                                        MyUtils.showAlertDialog(context, notify);
                                        hideProgress();
                                    }
                                } else {
                                    //yeu cau xac thuc va tra ve
                                    //Mo account kit
                                    phoneLogin(phone);
                                }


                            } else {
                                if (!TextUtils.isEmpty(result.getErrorMessage())) {
                                    MyUtils.showToast(context, result.getErrorMessage());

                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
//                        progressDialog.dismiss();
                        if (ANError != null) {
                            MyUtils.showAlertDialog(context, ANError.getErrorBody());
                        }
                    }
                });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


}
*/
