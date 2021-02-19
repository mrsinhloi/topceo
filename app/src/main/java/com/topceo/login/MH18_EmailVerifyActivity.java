package com.topceo.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.google.android.material.textfield.TextInputEditText;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class MH18_EmailVerifyActivity extends AppCompatActivity {
    private static final String TAG = "MH17_ForgetPasswordActivity";
    private Activity context = this;

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txt1)
    TextInputEditText txt1;


    @BindView(R.id.btn1)
    AppCompatButton btn1;

    private String phone;
    private TinyDB db;
    private User user;

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
                    /*phone = txt1.getText().toString();
                    if (!TextUtils.isEmpty(phone) && phone.length() >= 10) {
                        Intent intent = new Intent(context, MH01_InputPhoneNumber_Activity.class);
                        intent.putExtra(User.PHONE, phone);
                        intent.putExtra(User.IS_RESET_PASSWORD, true);
                        startActivityForResult(intent, MH01_InputPhoneNumber_Activity.REQUEST_VALID_NUMBER_PHONE);
                    } else {
                        MyUtils.showToast(context, R.string.phone_invalid);
                    }*/


                    String email = txt1.getText().toString().trim();
                    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        txt1.setError(getText(R.string.invalid_email));
                        txt1.requestFocus();
                        return;
                    }

                    ////
                    verifyEmail(email);

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

        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }
        if (user != null) {
            txt1.setText(user.getEmail());
        }

    }

    private void verifyEmail(final String email) {
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
                                    if(!TextUtils.isEmpty(message)) {
                                        MyUtils.showAlertDialog(context, message);
                                    }else{
                                        //tự thông báo
                                        if(result.getErrorCode()==ReturnResult.ERROR_CODE_PARAMS){
                                            MyUtils.showAlertDialog(context, R.string.email_not_exist);
                                        }else if(result.getErrorCode()==ReturnResult.ERROR_CODE_DATA){
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
                                            verifyEmail(email);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            /*if (requestCode == MH01_InputPhoneNumber_Activity.REQUEST_VALID_NUMBER_PHONE) {
                Bundle b = data.getExtras();
                String validPhone = b.getString(User.PHONE, "");

                //so xac thuc vao so nhap la trung nhau -> OK
                Intent intent = new Intent(context, ResetPasswordActivity.class);
                intent.putExtra(User.PHONE, validPhone);
                startActivity(intent);
                finish();
            }*/
        }

    }


}

