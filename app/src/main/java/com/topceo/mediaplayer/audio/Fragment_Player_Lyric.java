package com.topceo.mediaplayer.audio;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.topceo.R;
import com.topceo.shopping.MediaItem;
import com.topceo.utils.MyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Player_Lyric extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "lyric";
    private String lyric = "";

    public static Fragment_Player_Lyric newInstance(String lyric) {
        Fragment_Player_Lyric fragment = new Fragment_Player_Lyric();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, lyric);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lyric = getArguments().getString(ARG_TITLE);
        }

    }

    public Fragment_Player_Lyric() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @BindView(R.id.txt1)
    LyricView txt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.media_fragment_lyric, container, false);
        ButterKnife.bind(this, v);

        setLyric(lyric);

        registerReceiver();
        return v;
    }

    String oldLyric = "";
    private void setLyric(String lyric) {
        if (TextUtils.isEmpty(lyric) || lyric.length() < 5) {
            lyric = getString(R.string.no_lyric);
        }

//        txt.setText(lyric);
        /*File file = saveToFile(lyric);
        if(file!=null) {
            txt.setLyricFile(file, "UTF-8");
        }*/

        if(!lyric.equalsIgnoreCase(oldLyric) && !lyric.equalsIgnoreCase(getString(R.string.no_lyric))){
            txt.reset();
            txt.setupLyricString(lyric);
            oldLyric = lyric;
        }else{
            txt.reset();
            txt.setupLyricString(lyric);
            oldLyric = lyric;
        }
    }

    public static final String ACTION_CHANGE_LYRIC = "ACTION_CHANGE_LYRIC";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                switch (intent.getAction()) {
                    case ACTION_CHANGE_LYRIC:
                        lyric = b.getString(MediaItem.LYRIC, "");
                        setLyric(lyric);
                        break;
                    default:
                        break;
                }
            }
        };
        if (getContext() != null) {
            getContext().registerReceiver(receiver, new IntentFilter(ACTION_CHANGE_LYRIC));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null && getContext() != null) {
            getContext().unregisterReceiver(receiver);
        }
    }


    private File saveToFile(String text) {
        if (!TextUtils.isEmpty(text)) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        File sdcard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdcard.getAbsolutePath() + "/text/");
                        dir.mkdir();
                        File file = new File(dir, "lyric.txt");
                        FileOutputStream os = null;
                        try {
                            os = new FileOutputStream(file);
                            os.write(text.getBytes());
                            os.close();

                            return file;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {
                        requestPermission(); // Code for permission
                    }
                } else {
                    File sdcard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdcard.getAbsolutePath() + "/text/");
                    dir.mkdir();
                    File file = new File(dir, "lyric.txt");
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(text.getBytes());
                        os.close();

                        return file;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return null;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private static final int PERMISSION_REQUEST_CODE = 100;

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private PlayerService mMusicService;
    private boolean isBound;
    private PlayerAdapter mPlayerAdapter;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            MyUtils.log("ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            PlayerService.MyBinder binder = (PlayerService.MyBinder) iBinder;
            mMusicService = binder.getService();
            mPlayerAdapter = mMusicService.getMediaPlayerHolder();

            if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {
                setLyricPosition();
            }

            isBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            MyUtils.log("ServiceConnection: disconnected from service.");
            isBound = false;
        }
    };


    private void bindService() {
        Intent intent = new Intent(getContext(), PlayerService.class);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            getContext().unbindService(serviceConnection);
            isBound = false;
        }

    }
    @Override
    public void onResume() {
        super.onResume();

        bindService();
        setLyricPosition();
    }

    private void setLyricPosition(){

        if(getActivity()!=null && !getActivity().isFinishing() && !TextUtils.isEmpty(lyric)){
            if (mPlayerAdapter != null && mPlayerAdapter.getCurrentSong()!=null) {

                //set position
                //setup handler that will keep seekBar and playTime in sync
                final Handler handler = new Handler();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (txt!=null && mPlayerAdapter != null & mPlayerAdapter.isPlaying()) {
                            txt.setCurrentTimeMillis(mPlayerAdapter.getPlayerPosition());
                            handler.postDelayed(this, 1000);
                        }
                    }
                });
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
