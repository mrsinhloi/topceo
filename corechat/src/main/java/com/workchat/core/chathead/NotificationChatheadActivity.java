package com.workchat.core.chathead;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.workchat.core.chathead.events.MenuEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Mr.Phuong on 10/22/2015.
 * Lam trung gian de chuyen den activity phu hop
 */
public class NotificationChatheadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //expand widget
        EventBus.getDefault().post(new MenuEvent(false));

        finish();
    }
}