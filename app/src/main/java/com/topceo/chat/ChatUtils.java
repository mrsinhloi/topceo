package com.topceo.chat;

import android.view.View;
import android.widget.TextView;

import com.topceo.config.MyApplication;

public class ChatUtils {
    public static void setChatUnreadNumber(TextView txtNumber) {
        int number = MyApplication.numberChatUnread;
        if (number > 0) {
            txtNumber.setVisibility(View.VISIBLE);
            txtNumber.setText(String.valueOf(number));
        } else {
            txtNumber.setVisibility(View.GONE);
            txtNumber.setText("0");
        }
    }
}
