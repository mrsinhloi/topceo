package com.workchat.core.notification;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.workchat.core.chathead.events.MenuEvent;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Mr.Phuong on 10/22/2015.
 * Lam trung gian de chuyen den activity phu hop
 */
public class ChatNotificationActivity extends AppCompatActivity {
    private Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////giam ntf
//        MyApplication app = MyApplication.instance;
//        app.setNumberForIcon((MyApplication.Companion.getNumberNotify() - 1));

        //tao ket noi neu chua ket noi
        boolean isSocketConnected = ChatApplication.Companion.isSocketConnected();
        if (!isSocketConnected) {
            TinyDB db = new TinyDB(this);
            UserChatCore user = (UserChatCore) db.getObject(UserChatCore.USER_MODEL, UserChatCore.class);
            if (user != null) {
                ChatApplication.Companion.setupSocket(user);
            }
        }


        ////
        final Bundle b = getIntent().getExtras();
        if (b != null) {
            try {
                String roomId = b.getString(Room.ROOM_ID, "");
                String roomLogId = b.getString(RoomLog.ROOM_LOG_ID, "");
                if (!TextUtils.isEmpty(roomId)) {
                    //neu chua mo man hinh chinh thi mo len
                    /*if (!MainActivity.Companion.isExists()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(Room.ROOM_ID, roomId);
//                        intent.putExtra(RoomLog.ROOM_LOG_ID, "");
                        startActivity(intent);
                    } else {
                        MyUtils.openChatRoom(getApplicationContext(), roomId, "");
                    }*/
                    MyUtils.openChatRoom(getApplicationContext(), roomId, "");

                    //collapse widget
                    EventBus.getDefault().post(new MenuEvent(true));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finish();
    }
}