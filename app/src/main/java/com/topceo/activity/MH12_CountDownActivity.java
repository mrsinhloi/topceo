package com.topceo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.topceo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

public class MH12_CountDownActivity extends AppCompatActivity {

    public static final String NUMBER = "NUMBER";
    public static final String MESSAGE = "MESSAGE";
    private Activity context = this;

    @BindView(R.id.coundownView)
    CountdownView coundownView;
    @BindView(R.id.txt)TextView txt;
    @BindView(R.id.imgClose)
    ImageView imgClose;


    private long number = 0;
    private String message = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_count_down);
        ButterKnife.bind(this);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Bundle b = getIntent().getExtras();
        number = b.getLong(NUMBER, 0)*1000;
        message = b.getString(MESSAGE, "");

        txt.setText(message);
        coundownView.start(number);
        coundownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                finish();
                startActivity(new Intent(MH12_CountDownActivity.this, MH01_MainActivity.class));
            }
        });



    }




}
