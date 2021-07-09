package com.topceo.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.androidnetworking.error.ANError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.JsonObject;
import com.myxteam.phone_verification.MH01_Input_Phone;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.retrofit.ParserWc;
import com.topceo.retrofit.ReturnResultWc;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.topceo.utils.ProgressUtils;

import org.jetbrains.annotations.NotNull;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import cc.cloudist.acplibrary.ACProgressFlower;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class MH18_EmailVerifyActivity extends AppCompatActivity {
    public static final String EMAIL_NEED_VERIFY = "EMAIL_NEED_VERIFY";
    public static final String EMAIL_VERIFY_LINK = "EMAIL_VERIFY_LINK";
    public static void start(Activity context, String phoneNumber, boolean isForgetPassword) {
        Intent intent = new Intent(context, MH18_EmailVerifyActivity.class);
        intent.putExtra(MH01_Input_Phone.IS_FORGET_PASSWORD, isForgetPassword);
        intent.putExtra(MH01_Input_Phone.PHONE_NUMBER, phoneNumber);
        context.startActivity(intent);
    }
    public static void startEmailVerifyLink(Activity context, String emailVerifyLink) {
        //co 2 truong hop: dang mo, hoac load moi
        Intent intent = new Intent(context, MH18_EmailVerifyActivity.class);
        intent.putExtra(EMAIL_VERIFY_LINK, emailVerifyLink);
        context.startActivity(intent);
    }


    String phone = "";
    boolean isForgetPassword = false;
    String emailVerifyLink = "";
    private void init(Intent intent) {
        if(intent!=null){
            Bundle b = intent.getExtras();
            if (b != null) {

                //neu phone empty thi moi doc thong tin
                if(TextUtils.isEmpty(phone)) {
                    phone = b.getString(MH01_Input_Phone.PHONE_NUMBER, "");
                    isForgetPassword = b.getBoolean(MH01_Input_Phone.IS_FORGET_PASSWORD, false);
                }

                //khi click tu link verify trong email, khi dang ky tai khoan moi
                emailVerifyLink = b.getString(EMAIL_VERIFY_LINK, "");
                if(!TextUtils.isEmpty(emailVerifyLink)){
                    verifyEmailLink(emailVerifyLink);
                }
            }
        }
    }

    private static final String TAG = "MH18_EmailVerify";
    private Activity context = this;

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txt1)
    TextInputEditText txt1;


    @BindView(R.id.btn1)
    AppCompatButton btn1;

    private TinyDB db;
    private User user;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_email);
        ButterKnife.bind(this);
        db = new TinyDB(this);
//        MyUtils.transparentStatusBar(getWindow());

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.checkInternetConnection(context)) {

                    String email = txt1.getText().toString().trim();
                    if (!MyUtils.isEmailValid(email)) {
                        txt1.setError(getText(R.string.invalid_email));
                        txt1.requestFocus();
                        return;
                    }

                    ////
                    sendVerifyEmail(email);

                } else {
                    MyUtils.showThongBao(context);
                }

            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }
        if (user != null) {
            txt1.setText(user.getEmail());
        }*/
        txt1.requestFocus();
        init(getIntent());

    }

    @Deprecated
    private void verifyEmailOld(final String email) {
        if (!TextUtils.isEmpty(email)) {
            Webservices.verifyEmail(email, context).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ReturnResult result = (ReturnResult) task.getResult();
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    db.putBoolean(User.IS_SEND_VERIFY_EMAIL, true);
                                    MyUtils.showAlertDialog(context, R.string.send_request_verify_email_success, true);
                                } else {

                                    String message = result.getErrorMessage();
                                    if (!TextUtils.isEmpty(message)) {
                                        MyUtils.showAlertDialog(context, message);
                                    } else {
                                        //tự thông báo
                                        if (result.getErrorCode() == ReturnResult.ERROR_CODE_PARAMS) {
                                            MyUtils.showAlertDialog(context, R.string.email_not_exist);
                                        } else if (result.getErrorCode() == ReturnResult.ERROR_CODE_DATA) {
                                            MyUtils.showAlertDialog(context, R.string.server_data_error);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            verifyEmailOld(email);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }
                        }
                    }
                    return null;
                }
            });

        }
    }


    private void verifyEmailLink(String emailLink) {
        if (!TextUtils.isEmpty(emailLink)) {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            // Confirm the link is a sign-in with email link.
            if (auth.isSignInWithEmailLink(emailLink)) {
                // Retrieve this from wherever you stored it
                String email = db.getString(EMAIL_NEED_VERIFY, "");
                if(!TextUtils.isEmpty(email)){
                    // The client SDK will parse the code from the link for you.
                    ProgressUtils.show(context);
                    auth.signInWithEmailLink(email, emailLink)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        MyUtils.log("Successfully signed in with email link!");
                                        AuthResult result = task.getResult();
                                        // You can access the new user via result.getUser()
                                        // Additional user info profile *not* available via:
                                        // result.getAdditionalUserInfo().getProfile() == null
                                        // You can check if the user is new or existing:
                                        // result.getAdditionalUserInfo().isNewUser()
                                        if (result.getUser() != null) {
                                            result.getUser().getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull com.google.android.gms.tasks.Task<GetTokenResult> task) {
                                                    String idToken = task.getResult().getToken();
                                                    MyUtils.log("OK token");
                                                    verifyEmailToken(idToken);
                                                }
                                            });
                                        }else{
                                            ProgressUtils.hide();
                                        }
                                    } else {
                                        MyUtils.log("Error signing in with email link" + task.getException());
                                        MyUtils.showAlertDialog(context, R.string.link_auth_expired_des);
                                        ProgressUtils.hide();
                                    }

                                }


                            });
                }else{
//                    MyUtils.showAlertDialog(context, R.string.not_found_email);
                }


            }
        }
    }


    private void sendVerifyEmail(final String email) {
        if (!TextUtils.isEmpty(email)) {
            ACProgressFlower dialog = ProgressUtils.show(context);
            dialog.setCancelable(false);
            MyApplication.apiManager.sendVerifyEmail(email, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null) {
                        //MyUtils.log(obj.toString());
                        ReturnResultWc result = ParserWc.parseJson(
                                obj.toString(),
                                String.class,
                                false
                        );
                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                //chua ton tai, SERVER goi link xac thuc qua email, click link trong email tra ve dynamic link vao day
                                db.putString(EMAIL_NEED_VERIFY, email);
                                MyUtils.showAlertDialog(context, R.string.verification_link_sent_to_your_email, true);
                                MyUtils.hideKeyboard(context);
                            } else {

                                //da ton tai email tren he thong, chi dung cho truong hop quen mat khau
                                if (isForgetPassword) {

                                } else {//dang ky thi thong bao da ton tai
                                    MyUtils.showAlertDialog(context, result.getMessage());
                                }
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
        }
    }

    private void verifyEmailToken(final String idToken) {
        if (!TextUtils.isEmpty(idToken)) {
            MyApplication.apiManager.verifyEmailToken(idToken, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null) {
                        //MyUtils.log(obj.toString());
                        ReturnResultWc result = ParserWc.parseJson(
                                obj.toString(),
                                User.class,
                                false
                        );
                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                //chua ton tai, SERVER goi link xac thuc qua email, click link trong email tra ve dynamic link vao day
                                if (result.getData() != null) {
                                    User user = (User) result.getData();
                                    //neu da co username thi co tai khoan, vao main
                                    /*if (user.getUserId() > 0) {
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
                                            whenHaveUser(user);
                                        }
                                    } else {
                                        //nguoc lai thi vao hoan tat dang ky
                                        completeSignUp(user);
                                    }*/
                                    completeSignUp(user);
                                }
                            } else {
                                MyUtils.showAlertDialog(context, result.getMessage());
                            }
                        }
                    }
                    ProgressUtils.hide();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    MyUtils.log("have error");
                }
            });
        }
    }

    public static final int ACTION_COMPLETE_SIGNUP = 123;
    private void completeSignUp(User user) {
        if (user != null) {
            Intent intent = new Intent(context, MH16_SignupActivity.class);
            intent.putExtra(User.EMAIL, user.getEmail());
            intent.putExtra(User.TOKEN, user.getToken());
            startActivityForResult(intent, ACTION_COMPLETE_SIGNUP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case ACTION_COMPLETE_SIGNUP:
                    Bundle b = data.getExtras();
                    User user = b.getParcelable(User.USER);
                    if(user!=null){
                        MyUtils.whenHaveUser(user);
                        onLoginSuccess();
                    }
                    break;
            }
        }

    }

    public void onLoginSuccess() {
        //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
        MyApplication.whenLoginSuccess();
        //dong man hinh welcome
        sendBroadcast(new Intent(WelcomeActivity.ACTION_FINISH));
        //vao main de load cache truoc
        MyUtils.gotoMain(context);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressUtils.hide();
    }
}

