package com.topceo.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.androidnetworking.error.ANError;
import com.google.android.material.textfield.TextInputEditText;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private TinyDB db;


    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.pass1)
    TextInputEditText edit1;
    @BindView(R.id.pass2)
    TextInputEditText edit2;
    @BindView(R.id.btnUpdate)
    AppCompatButton btnUpdate;

    @BindView(R.id.scrollView)
    ScrollView sv;
    @BindView(R.id.linearRoot)
    LinearLayout linearRoot;


    private String phone = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        db = new TinyDB(this);
        MyUtils.transparentStatusBar(getWindow());

        setSupportActionBar(toolbar);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Kiem tra password
                String pass1 = edit1.getText().toString().trim();
                if (TextUtils.isEmpty(pass1)) {
                    MyUtils.showToast(context, R.string.please_input_password);
                    edit2.requestFocus();
                    return;
                }

                String pass2 = edit2.getText().toString().trim();
                if (TextUtils.isEmpty(pass2)) {
                    MyUtils.showToast(context, R.string.please_input_password);
                    edit2.requestFocus();
                    return;
                }

                if (!pass1.equals(pass2)) {
                    MyUtils.showToast(context, R.string.password_not_mach);
                    edit2.requestFocus();
                    return;
                }


                //goi ham set password
                setPassword(phone, pass1);


            }
        });


        /*linearRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = linearRoot.getRootView().getHeight() - linearRoot.getHeight();

                if (heightDiff > 100) {
                    Log.d("MyActivity", "keyboard opened");
                    sv.fullScroll(View.FOCUS_DOWN);
                } else {
                    Log.d("MyActivity", "keyboard closed");
                }
            }
        });*/

        Bundle b = getIntent().getExtras();
        if (b != null) {
            phone = b.getString(User.PHONE, "");
            /*if (!TextUtils.isEmpty(phone)) {

            } else {
                finish();
            }*/
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setPassword(final String phone, final String password) {
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(phone)) {
            Webservices.updatePassword(context, password).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ReturnResult result = (ReturnResult) task.getResult();
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    MyUtils.showToast(context, R.string.changepasswordsuccess);

                                    //dang nhap va vao app
                                    Intent intent = new Intent(MH15_SigninActivity.AUTO_LOGIN_BY_PHONE_AND_PASSWORD);
                                    intent.putExtra(User.PHONE, phone);
                                    intent.putExtra(User.PASSWORD, password);
                                    sendBroadcast(intent);

                                    finish();
                                } else {
                                    if (result.getErrorMessage() != null) {
                                        MyUtils.showAlertDialog(context, result.getErrorMessage());
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
                                            setPassword(phone, password);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (task.getError()!=null && !TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showAlertDialog(context, task.getError().getMessage());
                            }
                        }
                    }
                    return null;
                }
            });

        } else {
            MyUtils.showToast(context, R.string.please_input_password);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


}
