package com.topceo.profile;

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

import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.shopping.ShoppingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH22_SettingActivity extends AppCompatActivity {
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registerReceiver();*/
        setTitleBar();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    //#region SETUP TOOLBAR////////////////////////////////////////////////////////////////////////
    public @BindView(R.id.toolbar)
    Toolbar toolbar;
    public @BindView(R.id.imgBack)
    ImageView imgBack;
    public @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    public @BindView(R.id.txtNumber)
    TextView txtNumber;
    public @BindView(R.id.title)
    TextView title;
    public @BindView(R.id.imgShop)
    ImageView imgShop;

    public void setTitleBar() {
        title.setText(getText(R.string.options));
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
    public static final String ACTION_FINISH = "ACTION_FINISH_SettingActivity";
    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                }else if(intent.getAction().equalsIgnoreCase(ACTION_FINISH)){
                    finish();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);
        filter.addAction(ACTION_FINISH);

        registerReceiver(receiver, filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
    //#endregion///////////////////////////////////////////////////////////////////////////////////
}
