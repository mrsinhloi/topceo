package com.topceo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.topceo.R;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH13_InsightActivity extends AppCompatActivity {
    public static final String IS_INSIGHT = "IS_INSIGHT";
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView txt;

    private boolean isInsight = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);
        setTitleBar();

        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        Bundle b = getIntent().getExtras();
        isInsight = b.getBoolean(IS_INSIGHT, false);



        WebSettings settings = txt.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);


        //SET ZOOM
//        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

//        txt.setInitialScale(100);



        txt.setWebViewClient(new WebViewClient());
//        txt.loadUrl("file:///android_asset/html/terms_of_service.html");


        //headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", MyApplication.getToken());

        //load url
        if(isInsight){
            title.setText(getText(R.string.insight));
            txt.loadUrl(Webservices.URL_INSIGHT, headers);
        }else{//thong ke hoat dong
            title.setText(getText(R.string.thong_ke_hoat_dong));
            txt.loadUrl(Webservices.URL_INSIGHT+"/activity", headers);
        }



    }

    private class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }


    //#region SETUP TOOLBAR////////////////////////////////////////////////////////////////////////
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;
    @BindView(R.id.title)TextView title;
    public @BindView(R.id.imgShop)ImageView imgShop;

    private void setTitleBar() {
        title.setText(getText(R.string.insight));
        //hide icon navigation
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MH01_MainActivity.isExist) {
                    startActivity(new Intent(context, MH01_MainActivity.class));
                }
                finish();
            }
        });
        relativeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainChatActivity.class);
                startActivity(intent);
            }
        });
        imgShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ShoppingActivity.class));
            }
        });
        ChatUtils.setChatUnreadNumber(txtNumber);
        registerReceiver();
    }

    private BroadcastReceiver receiver;
    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);

        registerReceiver(receiver, filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }
    //#endregion///////////////////////////////////////////////////////////////////////////////////

}
