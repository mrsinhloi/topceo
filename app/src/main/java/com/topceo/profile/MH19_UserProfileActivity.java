package com.topceo.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.topceo.R;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserMedium;

public class MH19_UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_19_user_profile);
        registerReceiver();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            UserMedium u = (UserMedium) b.getParcelable(User.USER);
            if (u != null) {
                Fragment_Profile_Owner f = Fragment_Profile_Owner.newInstance(u, false);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.placeholder, f);
                ft.commit();
            }

        }



    }


    public static final String ACTION_FINISH = "ACTION_FINISH_MH19_UserProfileActivity";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(ACTION_FINISH)) {
                    finish();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_FINISH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
    }

}
