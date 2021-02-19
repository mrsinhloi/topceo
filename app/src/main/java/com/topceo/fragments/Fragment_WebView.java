package com.topceo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.topceo.R;
import com.just.agentweb.AgentWeb;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_WebView extends Fragment {
    public Fragment_WebView() {
    }

    private static final String ARG_TAG = "ARG_TAG";
    private String link;

    public static Fragment_WebView newInstance(String url) {
        Fragment_WebView fragment = new Fragment_WebView();
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


    @BindView(R.id.container)
    LinearLayout linearContainer;

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
        View v = inflater.inflate(R.layout.fragment_webview_container, container, false);
        ButterKnife.bind(this, v);
        swipeContainer.setEnabled(false);

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
                    loadWebview();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRefresh(false);
                    }
                }, 3000);
            }
        });

        loadWebview();
    }

    private AgentWeb mAgentWeb;
    private void loadWebview(){
        if(!TextUtils.isEmpty(link)){
            mAgentWeb = AgentWeb.with(this)
                    .setAgentWebParent(linearContainer, new LinearLayout.LayoutParams(-1, -1))
                    .useDefaultIndicator()
                    .createAgentWeb()
                    .ready()
                    .go(link);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAgentWeb.destroy();
    }
}
