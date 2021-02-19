package com.workchat.core.channel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.realm.Room;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;

public class MH02_SelectUserActivity extends AppCompatActivity {
    public static final String IS_SELECT_USER_ADMIN = "IS_SELECT_USER_ADMIN";
    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    /////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.recyclerView)
    RecyclerView rv;

    /////////////////////////////////////////////////////////////////////////////////////


    private TinyDB db;
    private UserChatCore user;
    private Socket socket;
    private Room room;
    private boolean isSelectUserAdmin = false;
    MH02_SelectUserAdminAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_02_select_user);
        ButterKnife.bind(this);
        db = new TinyDB(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //////////////////////
        /////////////////////////////////////////////////////////////////////
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);


        user = ChatApplication.Companion.getUser();
        ////////////////////////////////////////////////////////
        Bundle b = getIntent().getExtras();
        if (b != null) {
            isSelectUserAdmin = b.getBoolean(IS_SELECT_USER_ADMIN, false);
            room = b.getParcelable(Room.ROOM);

            if (isSelectUserAdmin) {
                adapter1 = new MH02_SelectUserAdminAdapter(room.getMembers(), context, room.get_id());
                rv.setAdapter(adapter1);
                title.setText(R.string.add_administrator);
            } else {
                MH02_SelectUserBanAdapter adapter2 = new MH02_SelectUserBanAdapter(room.getMembers(), context);
                rv.setAdapter(adapter2);
                title.setText(R.string.ban_user);
            }
        }
        ////////////////////////////////////////////////////////
        registerReceiver();
    }

    /////////////////////////////////////////////////////////////////////////////////
    ProgressDialog progressDialog;

    private void showDialog(Context context, String message) {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    /////////////////////////////////////////////////////////////////////////////////


    /*@Override
    protected void onResume() {
        super.onResume();
        //get data
        if (socket == null) {
            socket = ChatApplication.getSocketInitIfNull();
            //socket da khoi tao listener o MainActivity, neu chua ket noi thi bao ket noi lai
            if (socket.connected() == false) {
                ChatApplication.openSocket();
            }
        } else {
            //socket da khoi tao listener o MainActivity, neu chua ket noi thi bao ket noi lai
            if (socket.connected() == false) {
                ChatApplication.openSocket();
            }
        }

    }*/


    /////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_UPDATE_USER_CHANGE_PERMISSION = "ACTION_UPDATE_USER_CHANGE_PERMISSION_" + MH02_SelectUserActivity.class.getSimpleName();
    BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_USER_CHANGE_PERMISSION)) {
                    if (b != null) {
                        //cap nhat quyen admin
                        if (isSelectUserAdmin) {
                            Member member =  b.getParcelable(Member.MEMBER);
                            String roomId = b.getString(Room.ROOM_ID, "");
                            if (adapter1 != null) {
                                adapter1.replaceItem(member);
                            }
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_USER_CHANGE_PERMISSION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////

}
