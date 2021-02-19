package com.topceo.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.language.LocalizationUtil;
import com.topceo.utils.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {
    private Context context = this;

    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.btn1)
    AppCompatButton btn1;
    @BindView(R.id.btn2)
    AppCompatButton btn2;

    private TinyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    boolean oldVietnamese = true;

    private void initUI() {
        db = new TinyDB(this);
        oldVietnamese = LocalizationUtil.INSTANCE.isVietnamese(this);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        initLanguage();
        //set image
       /* int with = MyUtils.getScreenWidth(this);
        RequestOptions options = new RequestOptions()
                .override(with, with)
//                .centerCrop()
//                .transform(new RoundedCorners(with/2))
                ;
        Glide.with(this)
                .load(R.drawable.ic_sky_logo_bubble)
                .apply(options)
                .into(img1)
        ;*/

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(WelcomeActivity.this, MH15_SigninActivity.class));
                MyUtils.login(context);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(WelcomeActivity.this, MH16_SignupActivity.class));
                MyUtils.signup(context);
            }
        });

        registerReceiver();
    }

    public static final String ACTION_FINISH = "ACTION_FINISH_WELCOME_ACTIVITY";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*boolean newVietnamese = LocalizationUtil.INSTANCE.isVietnamese(this);
        if (oldVietnamese != newVietnamese) {
            initUI();
        }*/
        initUI();
    }

    //init language
    @BindView(R.id.group1)
    RadioGroup group1;

    private void initLanguage() {
        //languages
        group1.setOnCheckedChangeListener(null);
        boolean isVietnamese = LocalizationUtil.INSTANCE.isVietnamese(context);
        if (isVietnamese) {
            group1.check(R.id.radio1);
        } else {
            group1.check(R.id.radio2);
        }
        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        /*if (!languageSelected.equals(LocaleHelper.LANGUAGE_TIENG_VIET)) {
                            languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET;
                            db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected);
                            LocaleHelper.setLocale(context, languageSelected);
                            initUI();
                        }*/
                        db.putString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_VI);
                        MyApplication.initLanguage(context);
                        initUI();
                        break;

                    case R.id.radio2:
                        /*if (!languageSelected.equals(LocaleHelper.LANGUAGE_ENGLISH)) {
                            languageSelected = LocaleHelper.LANGUAGE_ENGLISH;
                            db.putString(LocaleHelper.SELECTED_LANGUAGE, languageSelected);
                            LocaleHelper.setLocale(context, languageSelected);
                            initUI();
                        }*/
                        db.putString(LocalizationUtil.SELECTED_LANGUAGE_KEY, LocalizationUtil.LANGUAGE_EN);
                        MyApplication.initLanguage(context);
                        initUI();
                        break;
                }
            }
        });
    }
}
