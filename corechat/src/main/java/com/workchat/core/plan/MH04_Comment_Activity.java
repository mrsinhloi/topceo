package com.workchat.core.plan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.DateFormat;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class MH04_Comment_Activity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    private PlanModel plan;
    private Room room;
    private UserChatCore user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh04_comment);
        ButterKnife.bind(this);

        user = ChatApplication.Companion.getUser();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_green_500_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initUI();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            plan =  b.getParcelable(PlanModel.PLAN_MODEL);
            room = b.getParcelable(Room.ROOM);
            if (plan != null && room != null) {
                ArrayList<Comment> list = plan.getComments();
                adapter = new MH04_Comment_Adapter(plan.getComments(), room, context);
                rv.setAdapter(adapter);
                if(adapter.getItemCount()>1){
                    rv.scrollToPosition(adapter.getItemCount()-1);
                }
            }

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    @BindView(R2.id.editText1)
    EditText txt;
    @BindView(R2.id.imgSend)
    ImageView imgSend;
    private MH04_Comment_Adapter adapter;

    private void initUI() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(mLayoutManager);
//        DividerItemDecoration itemDecor = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
//        rv.addItemDecoration(itemDecor);

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = txt.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    commentToPlan(message);
                    MyUtils.hideKeyboard(context, txt);
                    MyUtils.hideKeyboard(context);
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        initSocketConnection();
    }

    private Socket socket;
    private void initSocketConnection() {
        //link den socket trong application
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && socket.connected() == false) {
            socket.connect();
        }
    }
    private boolean isSocketConnected() {
        boolean connected = false;
        //khi hoi ket noi socket thi lay lai
        socket = ChatApplication.Companion.getSocket();
        if (socket != null) {
            connected = socket.connected();
            if (connected == false) {
                socket.connect();
//                MyUtils.showToastDebug(context, "Lost socket connection");
            }
        }


        return connected;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void commentToPlan(final String message){
        if(plan!=null){
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", plan.getRoomId());
                    obj.put("chatLogId", plan.getChatLogId());
                    obj.put("message", message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                socket.emit("commentToPlan", obj, new Ack() {
                    @Override
                    public void call(Object... args) {
//                    MyUtils.log("OK");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText("");

                                //tao vao update nguoc lai
                                Comment comment = new Comment();
                                comment.setUserId(user.get_id());
                                comment.setMessage(message);
                                comment.setCreateDate(MyUtils.getCurrentDate(DateFormat.DATE_FORMAT_SERVER));
                                comment.setChatLogId(plan.getChatLogId());
                                comment.setRoomId(plan.getRoomId());

                                if(adapter!=null){
                                    adapter.addItem(comment);
                                }

                                //tang so comment ben man hinh 03
                                Intent intent = new Intent(MH03_Plan_Detail_Activity.ACTION_ADD_COMMENT);
                                intent.putExtra(Comment.COMMENT, comment);
                                sendBroadcast(intent);

                                //scroll ve cuoi
                                if(adapter!=null && adapter.getItemCount()>1){
                                    rv.scrollToPosition(adapter.getItemCount()-1);
                                }
                            }
                        });
                    }
                });
            }else{
                MyUtils.showToast(context, R.string.socket_not_connected);
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////



}
