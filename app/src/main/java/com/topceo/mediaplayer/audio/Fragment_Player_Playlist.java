package com.topceo.mediaplayer.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.shopping.MediaItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Player_Playlist extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PLAYLIST = "playlist";
    private static final String ARG_POSTION = "position";

    private ArrayList<MediaItem> items = new ArrayList<>();
    private int positionSelected =0;

    public static Fragment_Player_Playlist newInstance(ArrayList<MediaItem> items, int position) {
        Fragment_Player_Playlist fragment = new Fragment_Player_Playlist();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PLAYLIST, items);
        args.putInt(ARG_POSTION, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            items = getArguments().getParcelableArrayList(ARG_PLAYLIST);
            positionSelected = getArguments().getInt(ARG_POSTION);
        }

    }

    public Fragment_Player_Playlist(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @BindView(R.id.recyclerView)
    RecyclerView rv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.media_fragment_playlist, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    Fragment_Player_Playlist_Adapter adapter;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        //adapter
        adapter = new Fragment_Player_Playlist_Adapter(getContext(), items, positionSelected);
        rv.setAdapter(adapter);

        registerReceiver();
    }


    public static final String ACTION_CHANGE_POSITION = "ACTION_CHANGE_POSITION";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                switch (intent.getAction()) {
                    case ACTION_CHANGE_POSITION:
                        int position = b.getInt(MediaItem.POSITION, 0);
                        if(adapter!=null){
                            positionSelected=position;
                            adapter.setPosition(position);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        getContext().registerReceiver(receiver, new IntentFilter(ACTION_CHANGE_POSITION));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getContext().unregisterReceiver(receiver);
        }
    }
}
