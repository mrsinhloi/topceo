package com.workchat.core.channel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;
import com.workchat.core.chat.ChatActivity;
import com.workchat.core.chat.ChatGroupDetailActivity;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.Permission;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.widgets.CircleTransform;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class MH04_AdminDetailActivity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    /////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.txt1)
    AppCompatTextView txt1;
    @BindView(R2.id.txt2)
    AppCompatTextView txt2;
    @BindView(R2.id.img1)
    ImageView img1;
    @BindView(R2.id.cb1)
    CheckBox cb1;
    @BindView(R2.id.cb2)
    CheckBox cb2;
    @BindView(R2.id.cb3)
    CheckBox cb3;
    @BindView(R2.id.cb4)
    CheckBox cb4;
    /////////////////////////////////////////////////////////////////////////////////////


    private TinyDB db;
    private Member member;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_04_admin_detail);
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
        int width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);
        ////////////////////////////////////////////////////////
        Bundle b = getIntent().getExtras();
        if (b != null) {

            member = b.getParcelable(Member.MEMBER);
            roomId = b.getString(Room.ROOM_ID, "");
            boolean isAddNewAdmin = b.getBoolean(Member.IS_ADD_NEW_ADMIN, false);

            //set image
            if (member != null) {
                if (member.getUserInfo() != null) {
                    if (!TextUtils.isEmpty(member.getUserInfo().getAvatar())) {
                        Picasso.get()
                                .load(member.getUserInfo().getAvatar())
                                .centerCrop()
                                .resize(width, width)
                                .transform(new CircleTransform(context))
                                .placeholder(R.drawable.icon_no_image)
                                .into(img1);
                    }
                    txt1.setText(member.getUserInfo().getName());

                }

                if(isAddNewAdmin){

                }else {
                    Permission p = member.getPermissions();
                    if (p != null) {
                        cb1.setChecked(p.isEditInfo());
                        cb2.setChecked(p.isPostMessage());
                        cb3.setChecked(p.isAddUsers());
                        cb4.setChecked(p.isAddNewAdmins());
                    }
                }

                if (member.isOwner()) {
                    txt2.setText(R.string.owner);
                }
            }
        }
        ////////////////////////////////////////////////////////
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_detail, menu);
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
                //cap quyen cho user này
                setAdminUser();
            } else {
                MyUtils.showThongBao(context);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////
    private Socket socket;

    @Override
    protected void onResume() {
        super.onResume();
        //get data
        socket = ChatApplication.Companion.getSocket();

    }

    private void setAdminUser() {
        if (member != null) {
            if (socket != null && socket.connected()) {
                JSONObject obj = new JSONObject();
                try {

                    //update member de chut tra nguoc ve truoc
                    member.getPermissions().setEditInfo(cb1.isChecked() + "");
                    member.getPermissions().setPostMessage(cb2.isChecked() + "");
                    member.getPermissions().setAddUsers(cb3.isChecked() + "");
                    member.getPermissions().setAddNewAdmins(cb4.isChecked() + "");
                    member.setAdmin(true);


                    JSONObject p = new JSONObject();
                    p.put("editInfo", cb1.isChecked());
                    p.put("postMessage", cb2.isChecked());
                    p.put("addUsers", cb3.isChecked());
                    p.put("addNewAdmins", cb4.isChecked());
                    p.put("editMessageOfOthers", false);
                    p.put("deleteMessageOfOthers", false);


                    obj.put("roomId", roomId);
                    obj.put("userId", member.getUserId());
                    obj.put("permissions", p);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("setChannelAdmin", obj, new Ack() {
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

                                        //LANG NGHE THONG TIN permissionChange
                                        //Chi co user bi thay doi quyen moi Lắng nghe sự kiện permissionChange update lại màn hình Danh sách admin và Danh sách chọn user làm admin, man hinh group detail co ds thanh vien

                                        //update man hinh Chon UserChat MH02_SelectUserActivity - adapter admin
                                        Intent intent = new Intent(MH02_SelectUserActivity.ACTION_UPDATE_USER_CHANGE_PERMISSION);
                                        intent.putExtra(Member.MEMBER, member);
                                        intent.putExtra(Room.ROOM_ID, roomId);
                                        sendBroadcast(intent);

                                        //update man hinh admin, them user nay vao man hinh MH03_AdminUserActivity
                                        intent = new Intent(MH03_AdminUserActivity.ACTION_UPDATE_USER_CHANGE_PERMISSION);
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

                                        finish();

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
