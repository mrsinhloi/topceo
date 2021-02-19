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

import com.workchat.core.chat.ChatActivity;
import com.workchat.core.chat.ChatGroupDetailActivity;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class MH03_AdminUserActivity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    /////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.recyclerView)
    RecyclerView rv;


    private Socket socket;
    private Room room;
    private MH03_AdminUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_03_admin_user);
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
            room =  b.getParcelable(Room.ROOM);
            adapter = new MH03_AdminUserAdapter(room.getMembers(), context, room.get_id());
            rv.setAdapter(adapter);
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_user, menu);
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
                    intent.putExtra(MH02_SelectUserActivity.IS_SELECT_USER_ADMIN, true);
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
    public static final String ACTION_UPDATE_USER_CHANGE_PERMISSION = "ACTION_UPDATE_USER_CHANGE_PERMISSION_" + MH03_AdminUserActivity.class.getSimpleName();
    public static final String ACTION_REMOVE_ADMIN = "ACTION_REMOVE_ADMIN_" + MH03_AdminUserActivity.class.getSimpleName();
    BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context mcontext, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_USER_CHANGE_PERMISSION)) {
                    if (b != null) {
                        //cap nhat quyen admin
                        Member member = b.getParcelable(Member.MEMBER);
                        String roomId = b.getString(Room.ROOM_ID, "");

                        if (room != null) {
                            //thay the phan tu va tao lai adapter
                            List<Member> list = room.getMembers();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getUserId().equals(member.getUserId())) {
                                    list.set(i, member);
                                    room.getMembers().set(i, member);
                                    break;
                                }
                            }
                            adapter = new MH03_AdminUserAdapter(room.getMembers(), context, room.get_id());
                            rv.setAdapter(adapter);
                        }
                    }
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_REMOVE_ADMIN)) {
                    if (b != null) {
                        //cap nhat quyen admin
                        Member member =  b.getParcelable(Member.MEMBER);
                        String roomId = b.getString(Room.ROOM_ID, "");
                        removeAdminUser(member, roomId);
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_USER_CHANGE_PERMISSION));
        registerReceiver(receiver, new IntentFilter(ACTION_REMOVE_ADMIN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    private void removeAdminUser(final Member member, final String roomId) {
        if (member != null) {
            if (socket != null && socket.connected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", roomId);
                    obj.put("userId", member.getUserId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("removeChannelAdmin", obj, new Ack() {
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

                                        //reset quyen member
                                        member.setAdmin(false);
                                        member.getPermissions().resetPermission();

                                        //xoa member khoi danh sach
                                        if (room != null) {

                                            //thay the phan tu va tao lai adapter
                                            List<Member> list = room.getMembers();
                                            for (int i = 0; i < list.size(); i++) {
                                                if (list.get(i).getUserId().equals(member.getUserId())){
                                                    list.set(i, member);
                                                    room.getMembers().set(i, member);
                                                    break;
                                                }
                                            }
                                            adapter = new MH03_AdminUserAdapter(room.getMembers(), context, room.get_id());
                                            rv.setAdapter(adapter);
                                        }

                                        //update man hinh Chon UserChat MH02_SelectUserActivity - adapter admin
                                        Intent intent = new Intent(MH02_SelectUserActivity.ACTION_UPDATE_USER_CHANGE_PERMISSION);
                                        intent.putExtra(Member.MEMBER, member);
                                        intent.putExtra(Room.ROOM_ID, roomId);
                                        sendBroadcast(intent);


                                        //update man hinh chi tiet channel ChatGroupDetailActivity
                                        intent = new Intent(ChatGroupDetailActivity.ACTION_UPDATE_USER_CHANGE_PERMISSION);
                                        intent.putExtra(Member.MEMBER, member);
                                        intent.putExtra(Room.ROOM_ID, roomId);
                                        sendBroadcast(intent);

                                        //update man hinh ChatActivity danh sach member update lai quyen
                                        intent = new Intent(ChatActivity.ACTION_UPDATE_USER_CHANGE_PERMISSION);
                                        intent.putExtra(Member.MEMBER, member);
                                        intent.putExtra(Room.ROOM_ID, roomId);
                                        sendBroadcast(intent);

//                                        finish();

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
}
