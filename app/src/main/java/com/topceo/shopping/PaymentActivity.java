package com.topceo.shopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;

import java.util.HashMap;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentActivity extends AppCompatActivity {

    private Context context;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progressBar)
    ProgressBar pb;

    private void showProgress() {
        pb.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        pb.setVisibility(View.INVISIBLE);
    }

    private long mediaId = 0;
    private User user;
    private TinyDB db;

    private boolean isBuyVip = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        /*toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_20_128);//
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0,0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //user
        db = new TinyDB(this);
        user = (User) db.getObject(User.USER, User.class);

        //media id
        Bundle b = getIntent().getExtras();
        if (b != null) {
            mediaId = b.getLong(Media.MEDIA_ID, 0);
            if (mediaId > 0) {
                isBuyVip = false;
                String url = Webservices.URL_PAYMENT + "/media?id=" + mediaId + "&v=" + user.isVip();
                init(url);
            }
        } else {//ko co mediaId thi  la mua vip
            isBuyVip = true;
            String url = Webservices.URL_PAYMENT + "/vip";
            init(url);
        }

    }

    private void init(String url) {

        showProgress();

        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSaveFormData(false);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                if(progress!=null && progress.isShowing()) progress.dismiss();
                hideProgress();

                MyUtils.log(url);
                //http://services.ehubstar.com/pay/PaySuccess
                url = url.toLowerCase();
                if (url.contains("/paysuccess")) {//thanh cong, vao load lai library
                    if (isBuyVip) {

                        //init lai cookie va lay lai user
                        MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                            @Override
                            public Void then(Task<Object> task) throws Exception {
                                if (task.getResult() != null) {
                                    User kq = (User) task.getResult();
                                    if (kq != null) {
                                        //initCookie da lay lai thong tin user, gio refresh lai UI  thoi
                                        sendBroadcast(new Intent(Fragment_Profile_Owner.ACTION_REFRESH));
                                    }
                                }
                                return null;
                            }
                        });

                    } else {
                        //load lai thu vien
                        sendBroadcast(new Intent(Fragment_MyStore.REFRESH_LIBRARY));
                    }
                    finish();
                } else if (url.contains("/cancel")) {//dong man hinh khi user cancel
                    finish();
                } else if (url.contains("/error")) {//de nguyen de doc loi

                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                showProgress();
                view.loadUrl(url);
                return true;
            }
        });

        //cookies
        //cookie
        /*CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        }

        CookieStore cookie = MyApplication.cookie;
        if (cookie != null) {
            List<Cookie> validCookies = cookie.getValidCookies();
            if(validCookies!=null&&validCookies.size()>0) {
                for (int i = 0; i < validCookies.size(); i++) {
                    Cookie item = validCookies.get(i);
                    String sCookie = item.toString();
                    cookieManager.setCookie(url, sCookie);
                }

            }
        }*/

        //headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", MyApplication.getToken());

        //load url
        webview.loadUrl(url, headers);
    }


}
