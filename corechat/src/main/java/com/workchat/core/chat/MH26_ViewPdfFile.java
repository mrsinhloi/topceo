package com.workchat.core.chat;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by Phuong Pham on 8/28/2015.
 */
public class MH26_ViewPdfFile extends Activity {
    public static final String FILE_NAME = "FILE_NAME";
    @BindView(R2.id.webView)
    WebView wv;
    @BindView(R2.id.loading_progress)
    SmoothProgressBar loading_process;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh26_view_pdf_file);
        ButterKnife.bind(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String filePath = b.getString(MH26_ViewPdfFile.FILE_NAME, "");
            if (TextUtils.isEmpty(filePath) == false) {
                try {

                    String urlEncoded = URLEncoder.encode(filePath, "UTF-8");
//                    filePath = "http://docs.google.com/viewer?url=" + urlEncoded;
                    filePath = "http://docs.google.com/gview?embedded=true&url=" + urlEncoded;
                    wv.getSettings().setJavaScriptEnabled(true);
//                    wv.getSettings().setSaveFormData(true);
//                    wv.getSettings().setBuiltInZoomControls(true);
//                    wv.getSettings().setDisplayZoomControls(true);
                    wv.getSettings().setAllowFileAccess(true);
//                    wv.getSettings().setUseWideViewPort(true);
                    wv.setWebViewClient(new AppWebViewClients());
                    wv.loadUrl(filePath);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public class AppWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            loading_process.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            loading_process.setVisibility(View.GONE);
            super.onPageFinished(view, url);

        }
    }


}
