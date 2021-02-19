package com.workchat.core.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.utils.MyUtils;

import io.socket.client.Socket;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(ACTION_CONNECTIVITY_CHANGE)){
            if(!isInitialStickyBroadcast()){
                if(MyUtils.checkInternetConnection(context)){
                    initSocket();
                }
            }

        }
    }

    private void initSocket(){
        Socket socket = ChatApplication.Companion.getSocket();
        if(socket != null && !socket.connected()){
            socket.connect();
        }
    }

}
