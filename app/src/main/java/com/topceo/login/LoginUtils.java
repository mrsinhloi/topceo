package com.topceo.login;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.topceo.R;
import com.topceo.accountkit.PhoneUtils;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.chatcore.UserChatCore;
import com.topceo.objects.other.User;
import com.topceo.services.CookieStore;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class LoginUtils {
    public static final String TAG = "LoginUtils";

    AppCompatActivity context;
    LoginEvent event;

    public LoginUtils(AppCompatActivity context, LoginEvent event) {
        this.context = context;
        this.event = event;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////
    public void loginFacebook(final String FacebookId) {
        final String Password = "FB";
        /*final ProgressDialog progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.logining));
        progressDialog.show();*/
        ProgressUtils.show(context);

        MyApplication.client = new OkHttpClient.Builder()
                .cookieJar(new CookieStore())
                .build();
        final long start = System.currentTimeMillis();
        AndroidNetworking.post(Webservices.API_URL + "user/loginFacebook")
                .addQueryParameter("username", FacebookId)
                .addQueryParameter("password", Password)
                .setOkHttpClient(MyApplication.client)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyUtils.howLong(start, "loginFacebook");
                        ReturnResult result = Webservices.parseJson(response, User.class, false);
                        ProgressUtils.hide();

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                //save thong tin
                                TinyDB db = new TinyDB(context);
                                db.putString(TinyDB.FACEBOOK_ID, FacebookId);
                                db.putString(TinyDB.FACEBOOK_PASSWORD, Password);
                                db.putBoolean(TinyDB.IS_LOGIN_BY_FACEBOOK, true);
                                db.putBoolean(TinyDB.IS_LOGINED, true);

                                //luu user
                                if (result.getData() != null) {
                                    User user = (User) result.getData();
                                    db.putObject(User.USER, user);
                                    loginByFirebase(user.getCoreChatCustomToken());
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
                        ProgressUtils.hide();
                    }
                });
    }


    public void login(String username, String password) {
        ProgressUtils.show(context);

        //nếu là sdt thì parse về chuẩn E164 trước khi gởi lên
        if (TextUtils.isDigitsOnly(username)) {
            String parsed = PhoneUtils.getE164FormattedMobileNumber(username, PhoneUtils.getDefaultCountryNameCode());
            if (!TextUtils.isEmpty(parsed)) {
                username = parsed;
            }
        }
        final String userNameOrPhone = username;

        //https://www.sitepoint.com/consuming-web-apis-in-android-with-okhttp/
        final long start = System.currentTimeMillis();
        String url = Webservices.API_URL + "user/login";
        AndroidNetworking.post(url)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .setOkHttpClient(MyApplication.client)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        MyUtils.howLong(start, "login");
                        final ReturnResult result = Webservices.parseJson(response, User.class, false);


                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                //save thong tin
                                final TinyDB db = new TinyDB(context);
                                db.putString(TinyDB.USER_NAME, userNameOrPhone);
                                db.putString(TinyDB.USER_PASSWORD, password);
                                db.putBoolean(TinyDB.IS_LOGIN_BY_FACEBOOK, false);
                                db.putBoolean(TinyDB.IS_LOGINED, true);

                                //luu user
                                new AsyncTask<Void, Void, Void>() {
                                    String customToken = "";

                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        if (result.getData() != null) {
                                            User user = (User) result.getData();
                                            db.putObject(User.USER, user);
                                            customToken = user.getCoreChatCustomToken();


                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        loginByFirebase(customToken);
                                    }
                                }.execute();


                            } else {
                                ProgressUtils.hide();
                                if (!TextUtils.isEmpty(result.getErrorMessage())) {
                                    String message = result.getErrorMessage();
                                    switch (message) {
                                        case "Password":
                                            MyUtils.showAlertDialog(context, R.string.please_check_input_login_password);
                                            break;
                                        default:
                                            MyUtils.showAlertDialog(context, R.string.please_check_input_login_username_or_phone);
                                            break;
                                    }
                                } else {
//                                    MyUtils.showAlertDialog(context, R.string.please_check_input_login);
                                    MyUtils.showAlertDialog(context, response.toString());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        ProgressUtils.hide();
                    }
                });
        MyUtils.hideKeyboard(context);
    }


    private void loginByFirebase(String customToken) {
        FirebaseApp app = FirebaseApp.getInstance(MyApplication.FIREBASE_CHATCORE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance(app);
        mAuth.signInWithCustomToken(customToken)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithCustomToken:success");
//                            onLoginSuccess();
                            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        String token = task.getResult().getToken();
                                        loginByIdToken(token);
                                    } else {
                                        String error = task.getException().toString();
                                        MyUtils.log(error);
                                        ProgressUtils.hide();
                                    }
                                }
                            });
                        } else {
                            ProgressUtils.hide();
                        }
                    }
                });
    }

    private void loginByIdToken(String idToken) {
        if (!TextUtils.isEmpty(idToken)) {
            AndroidNetworking.post(Webservices.URL_CORE_CHAT + "loginByIdToken")
                    .addBodyParameter("code", idToken)
                    .addBodyParameter("os", Webservices.OS)
                    .addBodyParameter("ip", MyApplication.getIpPublic())
                    .addBodyParameter("device", MyUtils.getDeviceId(context))
//                    .setOkHttpClient(client)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ProgressUtils.hide();
                            ReturnResult result = Webservices.parseJsonCoreChat(response, UserChatCore.class, false);
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {

                                    //luu user
                                    if (result.getData() != null) {
                                        UserChatCore user = (UserChatCore) result.getData();
                                        String token = user.getToken();
                                        MyApplication.saveTokenChat(token);
//                                        onLoginSuccess();
                                        event.whenloginByIdTokenSuccess();

                                    } else {
//                                        task.setResult(null);
                                    }

                                } else if (result.getErrorCode() == ReturnResult.ERROR_CODE_BANNED) {
//                                    task.setResult(ReturnResult.ERROR_CODE_BANNED);
                                    event.whenLoginByIdTokenFail();
                                } else {
                                    event.whenLoginByIdTokenFail();
                                    if (!TextUtils.isEmpty(result.getErrorMessage())) {
                                        MyUtils.showToast(context, result.getErrorMessage());
                                    }

//                                    task.setResult(null);
                                }
                            } else {
//                                task.setResult(null);
                            }


                        }

                        @Override
                        public void onError(ANError ANError) {
                            ProgressUtils.hide();
                            String error = ANError.getErrorCode() + " - " + ANError.getErrorDetail();
                            error += "<br/>"+ANError.getErrorBody();
                            MyUtils.showAlertDialogHtml(context, error);
                        }
                    });
        }else{
            ProgressUtils.hide();
        }
    }
}
