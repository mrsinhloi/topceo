package com.topceo.profile;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.adapter.NotifyAdapter;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.NotifySetting;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MH21_NotifySettingActivity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R.id.recyclerView)
    RecyclerView rv;


    private TinyDB db;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_notify);
        ButterKnife.bind(this);
        db = new TinyDB(this);
        //user
        if (db != null) {
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
            }
        }

        setTitleBar();


        //layout
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);


        //tao data
        String[] arrNotify = getResources().getStringArray(R.array.arr_notify_setting);

        if (arrNotify != null) {
            for (int i = 0; i < arrNotify.length; i++) {
                String item = arrNotify[i];

                NotifySetting setting = new NotifySetting();
                setting.setNotifyString(item);
                setting.setChecked(false);

                list.add(setting);
            }
        }

        //set adapter
        adapter = new NotifyAdapter(context, list);
        rv.setAdapter(adapter);


        //lay va set lai thong tin
        getUserNotifySetting();


    }

    private NotifyAdapter adapter;
    ArrayList<NotifySetting> list = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUserNotifySetting() {
        MyApplication.apiManager.getUserNotifySetting(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {

                            ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);

                            //{"ErrorCode":0,"Message":"","Data":{"UserId":7,
                            // "NewComment":true,"NewLike":true,"NewFollower":true,"FollowAccepted":true,"FollowUploadImage":true,
                            // "MentionInComment":true,"CommentHasReply":true,"CommentHasLike":true,"FollowingAction":true,
                            // "Chat":true,"LastModifyDate":"2019-05-27 04:54:14","LastModifyOS":"Android"}}
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    JsonObject json = (JsonObject) result.getData();
                                    if (list.size() > 0) {
                                        boolean b1 = json.get("NewComment").getAsBoolean();
                                        boolean b2 = json.get("NewLike").getAsBoolean();
                                        boolean b3 = json.get("NewFollower").getAsBoolean();
                                        boolean b4 = json.get("FollowAccepted").getAsBoolean();
                                        boolean b5 = json.get("FollowUploadImage").getAsBoolean();

                                        list.get(0).setChecked(b1);
                                        list.get(1).setChecked(b2);
                                        list.get(2).setChecked(b3);
                                        list.get(3).setChecked(b4);
                                        list.get(4).setChecked(b5);

                                        adapter.notifyDataSetChanged();

                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

    }


    private void setUserNotifySetting(boolean b1, boolean b2, boolean b3, boolean b4, boolean b5) {

        if (adapter != null && adapter.isModified()) {

            MyApplication.apiManager.setUserNotifySetting(
                    b1, b2, b3, b4, b5,
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {

                                ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);

                                //{"ErrorCode":0,"Message":"","Data":{"Success":true}}
                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                        JsonObject json = (JsonObject) result.getData();


                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (MyUtils.checkInternetConnection(context)) {
            ArrayList<NotifySetting> list = adapter.getList();
            setUserNotifySetting(
                    list.get(0).isChecked(),
                    list.get(1).isChecked(),
                    list.get(2).isChecked(),
                    list.get(3).isChecked(),
                    list.get(4).isChecked()

            );
        }
    }


    //#region SETUP TOOLBAR////////////////////////////////////////////////////////////////////////
    public @BindView(R.id.toolbar)
    Toolbar toolbar;
    public @BindView(R.id.imgBack)
    ImageView imgBack;
    public @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    public @BindView(R.id.txtNumber)
    TextView txtNumber;
    public @BindView(R.id.title)TextView title;
    public @BindView(R.id.imgShop)ImageView imgShop;

    public void setTitleBar() {
        title.setText(getText(R.string.notification_config));
        //hide icon navigation
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MH01_MainActivity.isExist) {
                    startActivity(new Intent(context, MH01_MainActivity.class));
                }
                finish();
            }
        });
        relativeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainChatActivity.class);
                startActivity(intent);
            }
        });
        imgShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ShoppingActivity.class));
            }
        });
        ChatUtils.setChatUnreadNumber(txtNumber);
        registerReceiver();
    }

    private BroadcastReceiver receiver;
    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);

        registerReceiver(receiver, filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }
    //#endregion///////////////////////////////////////////////////////////////////////////////////
}
