package com.topceo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.topceo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_WebView_2 extends Fragment {
    public Fragment_WebView_2() {
    }

    private static final String ARG_TAG = "ARG_TAG";
    private String link;

    public static Fragment_WebView_2 newInstance(String url) {
        Fragment_WebView_2 fragment = new Fragment_WebView_2();
        Bundle args = new Bundle();
        args.putString(ARG_TAG, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            link = getArguments().getString(ARG_TAG);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @BindView(R.id.webview)
    WebView wv;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private void setRefresh(boolean isRefresh) {
        if (isRefresh) {//on
            if (swipeContainer != null && !swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        } else {//off
            if (swipeContainer != null && swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_webview, container, false);
        ButterKnife.bind(this, v);


        WebSettings settings = wv.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);


        //SET ZOOM
//        settings.setBuiltInZoomControls(true);
        settings.setLoadsImagesAutomatically(true);
//        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        wv.setInitialScale(100);


        wv.setWebViewClient(new WebViewClient());
        wv.setWebChromeClient(new MyWebViewClient());


        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        return v;
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setRefresh(false);
        }
    }

    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
            if(newProgress==100){
                setRefresh(false);
            }
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!TextUtils.isEmpty(link)) {
//            link = "https://www.apple.com";
            wv.loadUrl(link);
        }


        //////////////////////////////////////////////////////////////////////////////////
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TextUtils.isEmpty(link)) {
                    wv.loadUrl(link);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRefresh(false);
                    }
                }, 3000);
            }
        });
    }


}
