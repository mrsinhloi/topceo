package com.topceo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.retrofit.ParserWc;
import com.topceo.retrofit.ReturnResultWc;
import com.topceo.utils.MyUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.JsonObject;
import com.workchat.core.retrofit.workchat.ReturnResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MH16_ReferalActivity extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.linear1)
    LinearLayout linear1;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.linear3)
    LinearLayout linear3;
    @BindView(R.id.imgShop)ImageView imgShop;


    private TinyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referal);
        ButterKnife.bind(this);
        db = new TinyDB(this);

        setTitleBar();
        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intro = getString(R.string.intro_link);
                String link = txtLink.getText().toString();
//                String text = intro + "\n" + link;

                shareFacebook(intro, link);

            }
        });
        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intro = getString(R.string.intro_link);
                String link = txtLink.getText().toString();
                String text = intro + "\n" + link;
                MyUtils.sendSms(context, text);
            }
        });
        linear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = txtLink.getText().toString();
                MyUtils.share(context, link);
            }
        });

        initFacebookShare();
    }




    /////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.txtLink)
    TextView txtLink;
    @BindView(R.id.btnCopy)
    AppCompatButton btnCopy;


    private void setTitleBar() {
        title.setText(R.string.invite_your_friend);
        //hide icon navigation
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //chuyen thanh link ref
        relativeChat.setVisibility(View.GONE);
        imgShop.setVisibility(View.INVISIBLE);

        //khoi tao link ban dau
        copyInviteLink(true);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyInviteLink(false);
            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void copyInviteLink(boolean isInit) {
        String refLink = db.getString(User.REFERAL_LINK, "");
        if (!TextUtils.isEmpty(refLink)) {
            if (isInit) {
                txtLink.setText(refLink);
            } else {
                MyUtils.copy(context, refLink);
                MyUtils.showToast(context, R.string.copied);
            }
        } else {
            if (MyUtils.checkInternetConnection(context)) {
                MyApplication.apiManager.getReferralLink(
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject obj = response.body();
                                if (obj != null) {
//                                    {"ErrorCode":0,"Message":"","Data":"https://book.ehubstar.com/ref/FgzB"}

                                    ReturnResultWc result = ParserWc.parseJson(obj.toString(), String.class, false);
                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                            //{"ErrorCode":0,"Message":"","Data":"https://weteam.ehubstar.com/ref/xEYL"}
                                            if (result.getData() != null) {
                                                String link = String.valueOf(result.getData());
                                                //luu lai
                                                if (!TextUtils.isEmpty(link)) {
                                                    db.putString(User.REFERAL_LINK, link);
                                                    if (isInit) {
                                                        txtLink.setText(link);
                                                    } else {
                                                        MyUtils.copy(context, link);
                                                        MyUtils.showToast(context, R.string.copied);
                                                    }
                                                }
                                            }
                                        } else {
                                            MyUtils.showToast(context, result.getMessage());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                MyUtils.log(t.getMessage());
                            }
                        }
                );
            } else {
                MyUtils.showThongBao(context);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private void initFacebookShare() {

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void shareFacebook(String intro, String link) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(link))
                .setQuote(intro)
                .build();
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


}
