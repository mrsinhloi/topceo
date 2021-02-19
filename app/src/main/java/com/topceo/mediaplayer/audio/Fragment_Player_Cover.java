package com.topceo.mediaplayer.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.topceo.R;
import com.topceo.shopping.MediaItem;
import com.topceo.utils.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Player_Cover extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "cover_url";
    private String cover;
    public static Fragment_Player_Cover newInstance(String cover) {
        Fragment_Player_Cover fragment = new Fragment_Player_Cover();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, cover);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cover = getArguments().getString(ARG_TITLE);
        }

    }

    public Fragment_Player_Cover(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @BindView(R.id.img1)
    ImageView img;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.media_fragment_cover, container, false);
        ButterKnife.bind(this, v);

        //init animation
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f ,
                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(10000);
        img.setAnimation(anim);


        if(isLive()){
            int size = (int)(MyUtils.getScreenWidth(getContext())*3.0f/5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.gravity = Gravity.CENTER;
            img.setLayoutParams(params);

            //load online
            Glide.with(getContext())
                    .load(cover)
                    .override(size, size)
                    .placeholder(R.drawable.no_media_small_circle)
                    .error(R.drawable.no_media_small_circle)
                    .transform(new CenterCrop(), new RoundedCorners(size/2))
                    .into(img);

            img.startAnimation(anim);
        }

        registerReceiver();


        return v;
    }

    public static final String ACTION_CHANGE_COVER = "ACTION_CHANGE_COVER";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                switch (intent.getAction()) {
                    case ACTION_CHANGE_COVER:
                        cover = b.getString(MediaItem.COVER, "");

                        //luu
                        /*Bundle args = new Bundle();
                        args.putString(ARG_TITLE, cover);
                        setArguments(args);*/

                        if(isLive()){
                            int size = (int)(MyUtils.getScreenWidth(getContext())*3.0f/5);
                            //load online
                            if(!TextUtils.isEmpty(cover)){
                                Glide.with(getContext())
                                        .load(cover)
                                        .override(size, size)
                                        .placeholder(R.drawable.no_media_small_circle)
                                        .error(R.drawable.no_media_small_circle)
                                        .transform(new CenterCrop(), new RoundedCorners(size/2))
                                        .into(img);
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        };
        getContext().registerReceiver(receiver, new IntentFilter(ACTION_CHANGE_COVER));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null && getContext() != null) {
            getContext().unregisterReceiver(receiver);
        }
    }

    private boolean isLive() {
        return getActivity() != null && !getActivity().isFinishing();
    }
}
