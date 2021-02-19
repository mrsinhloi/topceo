package com.workchat.core.channel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class MH01_BanUserActivity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    /////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.recyclerView)
    RecyclerView rv;

    /////////////////////////////////////////////////////////////////////////////////////


    private Socket socket;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_01_ban_user);
        ButterKnife.bind(this);

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


        ////////////////////////////////////////////////////////
        Bundle b = getIntent().getExtras();
        if (b != null) {
            room = b.getParcelable(Room.ROOM);
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


    @Override
    protected void onResume() {
        super.onResume();
        //get data
        socket = ChatApplication.Companion.getSocket();

        getRoomBannedUser();
    }

    private MH01_BanUserAdapter adapter;

    private void getRoomBannedUser() {
        if (room != null) {
            if (socket != null && socket.connected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("getRoomBannedUser", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(args[0].toString());
                                    int code = obj.getInt("errorCode");
                                    if (code == 0) {
                                        ArrayList<ChannelUser> list = ChannelUser.parse(context, args);
                                        adapter = new MH01_BanUserAdapter(list, context);
                                        rv.setAdapter(adapter);
                                    } else {
                                        String message = obj.getString("error");
                                        MyUtils.showAlertDialog(context, message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }
    }


    private boolean isSocketConnected() {
        socket = ChatApplication.Companion.getSocket();
        if (socket != null) {
            return socket.connected();
        }
        return false;
    }
    private void setBanUser(final boolean isBanned, final String userId) {
        if (room != null) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                    obj.put("userId", userId);
                    obj.put("isBanned", isBanned);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("setBanUser", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(args[0].toString());
                                    int code = obj.getInt("errorCode");
                                    if (code == 0) {
                                        MyUtils.showToast(context, R.string.success);
                                        if (isBanned == false) {
                                            //unlock ban, remove user khỏi list
                                            adapter.removeUser(userId);
                                        } else {
                                            //ban user
                                            //load lai danh sach
                                            getRoomBannedUser();

                                            //xoa user trong danh sach member
                                            if (room.getMembers() != null && room.getMembers().size() > 0) {
                                                for (int i = 0; i < room.getMembers().size(); i++) {
                                                    if (room.getMembers().get(i).getUserId().equals(userId)) {
                                                        room.getMembers().remove(i);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        String message = obj.getString("error");
                                        MyUtils.showToast(context, message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ban_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_1) {
            if (MyUtils.checkInternetConnection(context)) {
                if (room != null) {
                    Intent intent = new Intent(context, MH02_SelectUserActivity.class);
                    intent.putExtra(Room.ROOM, room);
                    startActivity(intent);
                }
            } else {
                MyUtils.showThongBao(context);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_SET_BAN_USER = "ACTION_SET_BAN_USER";
    BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(ACTION_SET_BAN_USER)) {
                    if (b != null) {
                        boolean isBan = b.getBoolean(UserChat.IS_BANNED_USER, false);
                        String userId = b.getString(UserChat.USER_ID, "");
                        setBanUser(isBan, userId);
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_SET_BAN_USER));
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
