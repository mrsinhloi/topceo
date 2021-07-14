package com.topceo.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.androidnetworking.error.ANError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.JsonObject;
import com.myxteam.phone_verification.MH01_Input_Phone;
import com.myxteam.phone_verification.MyExtensionKt;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.retrofit.ParserWc;
import com.topceo.retrofit.ReturnResultWc;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
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
public class MH19_EmailVerifyActivity2 extends AppCompatActivity {
    public static void start(Activity context, String email, boolean isForgetPassword) {
        Intent intent = new Intent(context, MH19_EmailVerifyActivity2.class);
        intent.putExtra(MH01_Input_Phone.IS_FORGET_PASSWORD, isForgetPassword);
        intent.putExtra(MH18_EmailVerifyActivity1.EMAIL, email);
        context.startActivity(intent);
    }

    String email = "";
    String originEmail = "";

    boolean isForgetPassword = false;

    private void init(Intent intent) {
        if (intent != null) {
            Bundle b = intent.getExtras();
            if (b != null) {
                email = b.getString(MH18_EmailVerifyActivity1.EMAIL, "");
                originEmail = b.getString(MH18_EmailVerifyActivity1.EMAIL, "");

                isForgetPassword = b.getBoolean(MH01_Input_Phone.IS_FORGET_PASSWORD, false);
                txt1.setText(email);

                setEmailDes(email);
            }
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
                String email = s.toString().toLowerCase();
                if (MyUtils.isEmailValid(email)) {
                    if (!email.equals(originEmail)) {
                        btn1.setVisibility(View.GONE);
                        btn2.setVisibility(View.VISIBLE);
                    } else {
                        btn1.setVisibility(View.VISIBLE);
                        btn2.setVisibility(View.GONE);
                    }
                } else {
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.GONE);
                }

            }
        });
    }

    private void setEmailDes(String email) {
        String des1 = getString(R.string.email_verify_sent_to_des, email);
        des1Tv.setText(MyUtils.fromHtml(des1));

        String des2 = getString(R.string.email_verify_update_to_des, email);
        des2Tv.setText(MyUtils.fromHtml(des2));


    }

    private static final String TAG = "MH18_EmailVerify";
    private Activity context = this;

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txt1)
    TextInputEditText txt1;


    @BindView(R.id.btn1)
    AppCompatButton btn1;
    @BindView(R.id.btn2)
    AppCompatButton btn2;

    @BindView(R.id.des1_tv)
    TextView des1Tv;
    @BindView(R.id.des2_tv)
    TextView des2Tv;

    private TinyDB db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_email_2);
        ButterKnife.bind(this);
        db = new TinyDB(this);

        btn2.setOnClickListener(new View.OnClickListener() {
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
                    if (isForgetPassword) {
                        sendResetPassword(email);
                    } else {//dang ky
                        sendVerifyEmail(email);
                    }

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


        init(getIntent());
        txt1.clearFocus();
        registerReceiver();

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
                                db.putString(MH18_EmailVerifyActivity1.EMAIL_NEED_VERIFY, email);
                                originEmail = email;
//                                MyUtils.showAlertDialog(context, R.string.verification_link_sent_to_your_email, true);
                                MyUtils.hideKeyboard(context);

                                setEmailDes(email);
                                btn1.setVisibility(View.VISIBLE);
                                btn2.setVisibility(View.GONE);
                            } else {
                                MyUtils.showAlertDialog(context, result.getMessage());
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

    //reset password
    private void sendResetPassword(final String email) {
        if (!TextUtils.isEmpty(email)) {
            ACProgressFlower dialog = ProgressUtils.show(context);
            dialog.setCancelable(false);
            MyApplication.apiManager.sendResetPassword(email, new Callback<JsonObject>() {
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
                                db.putString(MH18_EmailVerifyActivity1.EMAIL_NEED_RESET_PASSWORD, email);
//                                MyUtils.showAlertDialog(context, R.string.verification_link_sent_to_your_email, true);
                                originEmail = email;
                                MyUtils.hideKeyboard(context);

                                setEmailDes(email);
                                btn1.setVisibility(View.VISIBLE);
                                btn2.setVisibility(View.GONE);
                            } else {
                                MyUtils.showAlertDialog(context, result.getMessage());
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


    public static final String ACTION_FINISH = "ACTION_FINISH_VERIFY2_ACTIVITY";
    private BroadcastReceiver receiver = null;

    private void registerReceiver() {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equalsIgnoreCase(ACTION_FINISH)) {
                        finish();
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_FINISH);
            registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

}

