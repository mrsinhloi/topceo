package com.workchat.core.chat;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.workchat.core.channel.EndlessRecyclerOnScrollListener;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by Phuong Pham on 7/31/2015.
 */
public class MH29_Link_Send_In_Chat extends Fragment {


    @BindView(R2.id.loading_progress)
    SmoothProgressBar loading_progress;

    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    @BindView(R2.id.txtEmpty)TextView txtEmpty;


    //SOCKET////////////////////////////////////////////////////////////////////////////////////////////
    private static GsonBuilder gsonb;
    private static Gson gson;
    private static JsonParser parser;
    private LayoutInflater inflate;
    private UserChatCore user;
    private Socket mSocket;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Context context = getContext();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private String chatRoomId = "";
    private static final String ARG_PARAM1 = "param1";

    public static MH29_Link_Send_In_Chat newInstance(String param1) {
        MH29_Link_Send_In_Chat fragment = new MH29_Link_Send_In_Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatRoomId = getArguments().getString(ARG_PARAM1);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.mh28_file_send_in_chat, container, false);
        ButterKnife.bind(this, v);

        inflate = getLayoutInflater();
        user = ChatApplication.Companion.getUser();

        gsonb = new GsonBuilder();
        gson = gsonb.create();
        parser = new JsonParser();

        //socket
        mSocket = ChatApplication.Companion.getSocket();


        LinearLayoutManager linearLayout = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(linearLayout);
        DividerItemDecoration divider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.line_horizontal)));
        rv.addItemDecoration(divider);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                String lastLogId = adapter.getLastLogId();
                getRoomFiles(chatRoomId, lastLogId);
            }
        });


        if (MyUtils.checkInternetConnection(context)) {
            getRoomFiles(chatRoomId, "");
        }

        return v;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    private MH29_Link_Send_In_Chat_Adapter adapter;
    private void getRoomFiles(String roomId, String lastLogId) {
        loading_progress.setVisibility(View.VISIBLE);
        // Sending an object
        JSONObject obj = new JSONObject();
        try {
            obj.put("roomId", roomId);
            obj.put("lastLogId", lastLogId);
            mSocket.emit("getRoomLinks", obj, new Ack() {

                @Override
                public void call(final Object... args) {
                    if(getActivity()!=null && !getActivity().isFinishing()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<RoomLog> list = RoomLog.parseListRoomLog(context, args);
                                if (list != null && list.size() > 0) {
                                    if (TextUtils.isEmpty(lastLogId)) {
                                        //page dau tien
                                        adapter = new MH29_Link_Send_In_Chat_Adapter(context, list);
                                        rv.setAdapter(adapter);
                                        txtEmpty.setVisibility(View.GONE);
                                    } else {
                                        adapter.addMore(list);
                                    }
                                }

                                loading_progress.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            loading_progress.setVisibility(View.GONE);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
