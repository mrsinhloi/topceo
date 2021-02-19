package com.workchat.core.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workchat.core.channel.MH03_ChannelActivity;
import com.workchat.core.chat.receiver.NetworkChangeReceiver;
import com.workchat.core.chat.socketio.AckWithTimeOut;
import com.workchat.core.config.App;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.contacts.Contact_Fragment_1_Chat;
import com.workchat.core.database.TinyDB;
import com.workchat.core.event.EventChatRecent;
import com.workchat.core.event.RoomLogEvent_Have_Message;
import com.workchat.core.event.RoomLogEvent_IsViewed;
import com.workchat.core.event.SocketConnectEvent;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.ChatView;
import com.workchat.core.models.chat.RoomAction;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.search.MH09_SearchActivity;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.PhoneUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecentChat_Fragment_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentChat_Fragment_2 extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecentChat_Fragment.
     */
    public static RecentChat_Fragment_2 newInstance(int chatType, boolean isForward, boolean isSharing) {
        RecentChat_Fragment_2 fragment = new RecentChat_Fragment_2();
        Bundle args = new Bundle();
        args.putInt(CHAT_TYPE, chatType);
        args.putBoolean(RoomLog.IS_FORWARD, isForward);
        args.putBoolean(RoomLog.IS_SHARING, isSharing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        if (getArguments() != null) {
            chatType = getArguments().getInt(CHAT_TYPE, RecentChatType.ALL);
            isForward = getArguments().getBoolean(RoomLog.IS_FORWARD);
            isSharing = getArguments().getBoolean(RoomLog.IS_SHARING);
        }

        initSocket();
    }


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHAT_TYPE = "CHAT_TYPE";
    private int chatType = RecentChatType.ALL;
    boolean isForward = false;
    boolean isSharing = false;
    private boolean isCanSearch = false;
    public void setCanSearch(boolean canSearch) {
        isCanSearch = canSearch;
    }
    private boolean isCanLongClick = true;
    public void setCanLongClick(boolean canLongClick) {
        isCanLongClick = canLongClick;
        if (adapter != null) {
            adapter.setCanLongClick(isCanLongClick);
        }
    }

    private Socket socket;
    private void initSocket() {
        //get data
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && !socket.connected()) {
            socket.connected();
        }
    }

    private boolean isSocketConnected() {
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && socket.connected()) {
            return true;
        } else {
            ChatApplication.Companion.getSocketInitIfNull();
            return false;
        }
    }

    @BindView(R2.id.linearFooter)
    LinearLayout linearFooter;
    private void showFooter() {
        linearFooter.setVisibility(View.VISIBLE);
    }
    private void hideFooter() {
        linearFooter.setVisibility(View.INVISIBLE);
    }

    @BindView(R2.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    @BindView(R2.id.loading_progress)
    SmoothProgressBar progress_loading;

    private void closeRefresh() {
        hideFooter();
        isLoading1 = false;
        isLoading2 = false;
        isLoading3 = false;

    }

    private boolean isKeyboardShow = false;
    private void registerHideKeyboard() {
        nestedScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = nestedScrollView.getRootView().getHeight() - nestedScrollView.getHeight();

                if (heightDiff > 100) {
//                    Log.e("MyActivity", "keyboard opened");
                    isKeyboardShow = true;
                } else {
//                    Log.e("MyActivity", "keyboard closed");
                    isKeyboardShow = false;

                }
            }
        });
    }

    String ownerId;
    private UserChatCore user;
    private TinyDB db;
    LinearLayoutManager mLayoutManager;


    @BindView(R2.id.linearCreateChannel)
    LinearLayout linearCreateChannel;
    @BindView(R2.id.linearCreateGroup)
    LinearLayout linearCreateGroup;

    private void initNewForTab() {

        switch (chatType) {
            case RecentChatType.ALL:
                linearCreateGroup.setVisibility(View.GONE);
                linearCreateChannel.setVisibility(View.GONE);
                break;
            case RecentChatType.GROUP://tru private va channel
                linearCreateGroup.setVisibility(View.VISIBLE);
                linearCreateChannel.setVisibility(View.GONE);
                break;
            case RecentChatType.CHANNEL://type channel
                linearCreateGroup.setVisibility(View.GONE);
                linearCreateChannel.setVisibility(View.VISIBLE);
                break;

        }

        linearCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchUserChatFromRestApiActivity.class);
                startActivity(intent);
            }
        });
        linearCreateChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MH03_ChannelActivity.class);
                startActivity(intent);
            }
        });

    }


    private int screenHeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_1_recent_chat_room, container, false);
        ButterKnife.bind(this, v);

        realm = ChatApplication.Companion.getRealmChat();

        db = new TinyDB(getContext());
        user = ChatApplication.Companion.getUser();
        if (user != null) {
            try {
                ownerId = user.get_id();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }


        screenHeight = MyUtils.getScreenHeight(getContext());
        isInternetConnected = MyUtils.checkInternetConnection(getContext());
        registerReceiver();
        registerHideKeyboard();
        /////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////
        nestedScrollView.setSmoothScrollingEnabled(true);
        //
        mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        //add devider
//        rv.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));
        rv.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(rv, false);

//        final int screenHeight = MyUtils.getScreenHeight(getContext());
        hideFooter();
        //https://medium.com/@mujtahidah/load-more-recyclerview-inside-nested-scroll-view-and-coordinator-layout-4f179dc01fd

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //hide keyboard when scroll
                if (v.getChildAt(v.getChildCount() - 1) != null) {

                    if (scrollY > oldScrollY) {
//                        Log.i(TAG, "Scroll DOWN");
                        //hide keyboard
                        if (isKeyboardShow) {
                            MyUtils.hideKeyboard(getActivity());
                        }

                    }
                    if (scrollY < oldScrollY) {
//                        Log.i(TAG, "Scroll UP");
                        if (isKeyboardShow) {
                            MyUtils.hideKeyboard(getActivity());
                        }
                    }

                    if (scrollY == 0) {
//                        Log.i(TAG, "TOP SCROLL");
                        if (isKeyboardShow) {
                            MyUtils.hideKeyboard(getActivity());
                        }
                    }
//
                }

                if (!nestedScrollView.canScrollVertically(1)) {
                    loadMore();
                }

            }
        });

        //init adapter
        task1 = new LoadDataTask();
        task1.execute();


        initNewForTab();
        //lay danh sach contact and syn
        syncContactList();

        return v;
    }

    private void loadMore() {
        MyUtils.log("load more " + chatType);
        //Load Your Data
        if (TextUtils.isEmpty(keyword)) {

            boolean isLoad = true;
            boolean isLoading = false;
            //ko load more
            switch (chatType) {
                case RecentChatType.ALL:
                    isLoad = isLoadMore1;
                    isLoading = isLoading1;
                    break;
                case RecentChatType.GROUP://tru private va channel
                    isLoad = isLoadMore2;
                    isLoading = isLoading2;
                    break;
                case RecentChatType.CHANNEL://type channel
                    isLoad = isLoadMore3;
                    isLoading = isLoading3;
                    break;

            }


            if (isLoad && !isLoading) {
                showFooter();
                long lastLogDate = adapter.getLastLogDate();
                getData(lastLogDate);
            }


        }
    }

    private LoadDataTask task1;

    private class LoadDataTask extends AsyncTask<Void, Void, ArrayList<Room>> {
        @Override
        protected ArrayList<Room> doInBackground(Void... voids) {
            return getListRoomFromDB();
        }

        @Override
        protected void onPostExecute(ArrayList<Room> rooms) {
            super.onPostExecute(rooms);
            adapter = new RecentChat_Adapter(/*getContext(),*/ rooms, chatType);
            adapter.setCanLongClick(isCanLongClick);
            adapter.setIsForward(isForward);
            adapter.setSharing(isSharing);
            rv.setAdapter(adapter);

            getListRoomPin("", 1);
        }
    }


    private boolean isScrollDown = false;
    private ArrayList<UserInfo> listUsers;


    private RecentChat_Adapter adapter;
    private String keyword = "";

    private boolean isLoadMore1 = true;
    private boolean isLoadMore2 = true;
    private boolean isLoadMore3 = true;
    int position = 0;

    private boolean isLoading1 = false;
    private boolean isLoading2 = false;
    private boolean isLoading3 = false;


    private void getData(final long lastLogDate) {
        final long start = SystemClock.elapsedRealtime();
        boolean isLoading = false;
        if (isSocketConnected() && getActivity() != null && !getActivity().isFinishing()) {
            //ko load more
            switch (chatType) {
                case RecentChatType.ALL:
                    isLoading = isLoading1;
                    break;
                case RecentChatType.GROUP://tru private va channel
                    isLoading = isLoading2;
                    break;
                case RecentChatType.CHANNEL://type channel
                    isLoading = isLoading3;
                    break;

            }

            if (!isLoading) {


                JSONObject obj = new JSONObject();
                try {
                    obj.put("lastLogDate", lastLogDate);
                    obj.put("keyword", MyUtils.getUnsignedString(keyword));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                position = 0;
                if (mLayoutManager != null) {
                    position = mLayoutManager.findFirstVisibleItemPosition();
                }


                //all
                String event = "getRecentChatRoom";
                switch (chatType) {
                    case RecentChatType.ALL:
                        event = "getRecentChatRoom";
                        isLoading1 = true;
                        break;
                    case RecentChatType.GROUP://tru private va channel
                        event = "getRecentGroupRoom";
                        isLoading2 = true;
                        break;
                    case RecentChatType.CHANNEL://type channel
                        event = "getRecentChannel";
                        isLoading3 = true;
                        break;

                }
                MyUtils.log("add more " + event + " key = " + lastLogDate);
                final String name = event;


                socket.emit(event, obj, new AckWithTimeOut(AckWithTimeOut.TIME_OUT) {
                    @Override
                    public void call(final Object... args) {

                        switch (chatType) {
                            case RecentChatType.ALL:
                                isLoading1 = false;
                                break;
                            case RecentChatType.GROUP://tru private va channel
                                isLoading1 = false;
                                break;
                            case RecentChatType.CHANNEL://type channel
                                isLoading1 = false;
                                break;

                        }

                        if (args != null) {
                            if (args[0].toString().equalsIgnoreCase(AckWithTimeOut.NO_ACK)) {
                                //timeout

                            } else {
                                MyUtils.howLong(start, name);
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (getContext() != null && adapter != null) {


                                                ArrayList<Room> list = Room.parseListRoom(getContext(), args);
                                                if (list != null && list.size() > 0) {


                                                    //neu co keyword thi chi tao adapter moi
                                                    if (!TextUtils.isEmpty(keyword)) {
                                                        adapter.clear();
                                                        adapter.addMore(list);

                                                        switch (chatType) {
                                                            case RecentChatType.ALL:
                                                                //BAO CHO FRAGMENT SEARCH ALL
                                                                EventBus.getDefault().post(new EventChatRecent(list, list.size()));
                                                                //SET TITLE
                                                                Intent intent = new Intent(MH09_SearchActivity.ACTION_SET_TAB_TITLE_SEARCH);
                                                                intent.putExtra(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH, 2);
                                                                int size = list.size();
                                                                if (TextUtils.isEmpty(keyword)) {//ko co tu khoa thi ko set so luong
                                                                    size = 0;
                                                                }
                                                                intent.putExtra(MH09_SearchActivity.SEARCH_NUMBER_FOUND, size);
                                                                getContext().sendBroadcast(intent);
                                                                break;
                                                        }

                                                    } else {
                                                        //neu lan dau tien thi them moi
                                                        if (lastLogDate == 0) {//tab0 da luu ds pin, gio lay page0, clear roi add vao adapter
                                                            listTab0.addAll(list);

                                                            if (adapter != null)
                                                                adapter.replaceListItem(listTab0);

                                                            listTab0.clear();

                                                            switch (chatType) {
                                                                case RecentChatType.ALL:
                                                                    //BAO CHO FRAGMENT SEARCH ALL
                                                                    EventBus.getDefault().post(new EventChatRecent(list, list.size()));
                                                                    //SET TITLE
                                                                    Intent intent = new Intent(MH09_SearchActivity.ACTION_SET_TAB_TITLE_SEARCH);
                                                                    intent.putExtra(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH, 2);
                                                                    int size = 0;
                                                                    intent.putExtra(MH09_SearchActivity.SEARCH_NUMBER_FOUND, size);
                                                                    getContext().sendBroadcast(intent);
                                                                    break;
                                                            }


                                                            //lan dau tien neu danh sach <15 item thi goi load more
                                                            //==>Khac phuc loi ko scroll khi co qua it phan tu
                                                    /*if (adapter.getItemCount() < 20) {
                                                        isLoading = false;
                                                        loadMore();
                                                    }*/

                                                            //moi khi resume thi goi lay lai so luong tin chua doc
                                                            ChatApplication.Companion.getUnreadRoomCount();

                                                        } else {
                                                            adapter.addMore(list);
                                                        }

                                                    }


                                                } else {


                                                    //chi co list pin, ko co page
                                                    if (listTab0 != null) {//tab0 da luu ds pin, gio lay page0, clear roi add vao adapter
                                                        adapter.replaceListItem(listTab0);
                                                        listTab0.clear();

                                                        switch (chatType) {
                                                            case RecentChatType.ALL:
                                                                //BAO CHO FRAGMENT SEARCH ALL
                                                                EventBus.getDefault().post(new EventChatRecent(adapter.getData(), adapter.getItemCount()));
                                                                //SET TITLE
                                                                Intent intent = new Intent(MH09_SearchActivity.ACTION_SET_TAB_TITLE_SEARCH);
                                                                intent.putExtra(MH09_SearchActivity.ACTION_GO_TO_POSITION_SEARCH, 2);
                                                                int size = 0;
                                                                intent.putExtra(MH09_SearchActivity.SEARCH_NUMBER_FOUND, size);
                                                                getContext().sendBroadcast(intent);
                                                                break;
                                                        }


                                                    }

                                                    if (!TextUtils.isEmpty(keyword)) {
                                                        adapter.clear();
                                                    }

                                                    if (list == null || list.size() == 0) {
                                                        //ko load more
                                                        switch (chatType) {
                                                            case RecentChatType.ALL:
                                                                isLoadMore1 = false;
                                                                break;
                                                            case RecentChatType.GROUP://tru private va channel
                                                                isLoadMore2 = false;
                                                                break;
                                                            case RecentChatType.CHANNEL://type channel
                                                                isLoadMore3 = false;
                                                                break;

                                                        }

                                                        //page dau tien
                                                        if (lastLogDate == 0) {
                                                            adapter.clear();
                                                        }
                                                    }


                                                }


                                                if (adapter != null && adapter.getItemCount() > 0) {
                                                    rv.setVisibility(View.VISIBLE);
                                                } else {
                                                    rv.setVisibility(View.GONE);
                                                }

                                            }

                                            closeRefresh();

                                        }


                                    });
                                }
                            }
                        }


                    }
                });
            }
        } else {
            closeRefresh();
        }

    }

    //LOAD DANH SACH GROUP PIN
    private boolean isLoadingPinRoom = false;
    private ArrayList<Room> listTab0 = new ArrayList<>();

    private void getListRoomPin(String lastLogDate, int from) {

        final long start = SystemClock.elapsedRealtime();
        if (isSocketConnected() && getActivity() != null && !getActivity().isFinishing()) {

            //khoi dau la cho load more
            isLoadMore1 = true;
            isLoadMore2 = true;
            isLoadMore3 = true;
            isLoadingPinRoom = true;

            isLoading1 = false;
            isLoading2 = false;
            isLoading3 = false;

            JSONObject obj = new JSONObject();
            try {
                obj.put("lastLogDate", lastLogDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            MyUtils.log("add more pin from " + from);
            socket.emit("getRecentPinChatRoom", obj, new Ack() {
                @Override
                public void call(final Object... args) {

                    MyUtils.howLong(start, "getRecentPinChatRoom");
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        ArrayList<Room> list = Room.parseListRoom(getContext(), args);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getContext() != null) {

                                    if (list != null && list.size() > 0) {

                                        //set trang thai la pin
                                        for (int i = 0; i < list.size(); i++) {
                                            list.get(i).setPin(true);
                                        }

                                        //loc theo man hinh Room.TYPE
                                        List<Room> listOfType = new ArrayList<>();
                                        switch (chatType) {
                                            case RecentChatType.ALL:
                                                listTab0 = new ArrayList<>();
                                                listTab0.addAll(list);

                                                isLoadingPinRoom = false;
                                                //lay danh sach cac room
                                                getData(0);
                                                break;
                                            case RecentChatType.GROUP://tru private va channel
                                                if (adapter != null) {
                                                    adapter.clear();
                                                }
                                                for (int i = 0; i < list.size(); i++) {
                                                    Room r = list.get(i);
                                                    if (!r.getType().equalsIgnoreCase(Room.ROOM_TYPE_PRIVATE) &&
                                                            !r.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL)) {
                                                        listOfType.add(r);
                                                    }
                                                }
                                                //ADD VAO ADAPTER
                                                if (adapter != null && listOfType.size() > 0) {
                                                    adapter.clear();
                                                    adapter.addMore(listOfType, 0);
                                                }

                                                isLoadingPinRoom = false;
                                                //lay danh sach cac room
                                                getData(0);
                                                break;
                                            case RecentChatType.CHANNEL://type channel
                                                if (adapter != null) {
                                                    adapter.clear();
                                                }
                                                for (int i = 0; i < list.size(); i++) {
                                                    Room r = list.get(i);
                                                    if (r.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL)) {
                                                        listOfType.add(r);
                                                    }
                                                }
                                                //ADD VAO ADAPTER
                                                if (adapter != null && listOfType.size() > 0) {
                                                    adapter.clear();
                                                    adapter.addMore(listOfType, 0);
                                                }

                                                isLoadingPinRoom = false;
                                                //lay danh sach cac room
                                                getData(0);
                                                break;

                                        }


                                    } else {
                                        //ko co list pin thi van vao lay chat room
                                        isLoadingPinRoom = false;
                                        //lay danh sach cac room
                                        getData(0);
                                    }
                                }


                            }
                        });
                    }
                }
            });
        } else {
            closeRefresh();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this) == false) {
            EventBus.getDefault().register(this);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this) == true) {
            EventBus.getDefault().unregister(this);
        }

        if (task1 != null && task1.getStatus() != AsyncTask.Status.FINISHED) {
            task1.cancel(true);
        }


        try {
            if (receiver != null && getContext() != null && isRegister) {
                getContext().unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveCache();
        if(adapter!=null){
            adapter.clear();
        }
    }

    private void saveCache() {
//        MyUtils.showToast(getContext(), "save cache");
        if (TextUtils.isEmpty(keyword)) {
            ArrayList<Room> items = new ArrayList<>();
            if (adapter != null && adapter.getItemCount() > 0) {
                int number = 15;
                ArrayList<Room> list = adapter.getData();
                if (list.size() > number) {
                    items = new ArrayList<>(list.subList(0, number - 1));
                } else {
                    items.addAll(list);
                }
                db.putListRoom(Room.ROOM_LIST_SAVE + chatType, items);//
//                adapter.clear();
            } else {
                db.putListRoom(Room.ROOM_LIST_SAVE + chatType, items);
            }
        }
    }



    private ArrayList<Room> getListRoomFromDB() {
        ArrayList<Room> list = new ArrayList<>();
        //chi lam tab 0
//        if (chatType == RecentChatType.ALL) {
        try {
            ArrayList<Room> items = db.getListRoom(Room.ROOM_LIST_SAVE + chatType);
            if (items != null && items.size() > 0) {
                list.addAll(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        return list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRoomViewed(RoomLogEvent_IsViewed event) {
        RoomLog log = event.getMessage();
        if (log != null) {
            if (adapter != null) {
                //kiem tra minh da xem chua
                boolean isViewed = false;
                List<ChatView> views = log.getViews();
                if (views != null && views.size() > 0) {
                    for (int i = 0; i < views.size(); i++) {
                        ChatView v = views.get(i);
                        if (v.getUserId().equals(ownerId) && v.isViewed()) {
                            isViewed = true;
                            break;
                        }
                    }
                }

//                adapter.updateRoomViewed(log, isViewed);
                adapter.updateMessageRoom(log, isViewed);

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRoomHaveMessage(RoomLogEvent_Have_Message event) {
        RoomLog log = event.getMessage();
        if (log != null) {

            //neu cung loai thi moi nhan vao
            Room room = log.getRoomInfo();
            if (room != null) {

                //neu la tin cua minh thi xem nhu da viewed
                if(log.getAuthorInfo().getUserId().equals(user.get_id())){
                    log.getRoomInfo().setViewed(true);
                }

                //channel
                if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL) &&
                        chatType == RecentChatType.CHANNEL) {
                    addRoomLog(log);

                    //group ko phai channel va private
                } else if (!room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL) &&
                        !room.getType().equalsIgnoreCase(Room.ROOM_TYPE_PRIVATE) &&
                        chatType == RecentChatType.GROUP) {

                    addRoomLog(log);

                } else if (chatType == RecentChatType.ALL) {
                    addRoomLog(log);
                }
            }


        }
    }

    private void addRoomLog(RoomLog log) {
        if (log != null) {
            if (adapter != null) {
                //neu man hinh dang mo thi viewed=true
                if (ChatActivity.isExists && ChatActivity.roomId.equalsIgnoreCase(log.getRoomId())) {
                    adapter.updateMessageRoom(log, true);
                } else {//ko phai room dang mo
                    adapter.updateMessageRoom(log, log.getRoomInfo().isViewed());
                }

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketConnectEvent connection) {
        if (connection.isConnected()) {
//            MyUtils.showToastDebug(context, "socket connected");
            getListRoomPin("", 2);
        } else {

        }
    }


    public static final String SEARCH_TEXT = "SEARCH_TEXT";
    /////////////////////////////////////////////////////////////////////////////////////////////
    public static final String REFRESH_RECENT_CHAT_ROOM = "REFRESH_RECENT_CHAT_ROOM";
    public static final String ACTION_LONG_CLICK_ROOM = "ACTION_LONG_CLICK_ROOM";
    public static final String ACTION_LEAVE_GROUP = "ACTION_LEAVE_GROUP";//CUSTOM HOAC CHANNEL
    public static final String ACTION_SEARCH_TEXT = "ACTION_SEARCH_TEXT";
    public static final String ACTION_GO_TO_TOP_CHAT = "ACTION_GO_TO_TOP_CHAT";

    private boolean isInternetConnected = true;
    private BroadcastReceiver receiver;
    private boolean isRegister = false;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (getActivity() != null && !getActivity().isFinishing()) {
                    Bundle b = intent.getExtras();

                    //Khi mang thay doi thi kiem tra neu online lai thi lay lai danh sach
                    if (intent.getAction().equals(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE)) {
                        if (!isInitialStickyBroadcast()) {
                            //tu offline sang online thi moi goi, cai nay mac dinh lan dau chay thi ko cho
                            boolean isConnected = MyUtils.checkInternetConnection(context);
                            if (isConnected && !isInternetConnected) {
                                getListRoomPin("", 3);
                            }
                            isInternetConnected = isConnected;
                        }
                    } else if (intent.getAction().equals(REFRESH_RECENT_CHAT_ROOM)) {
                        getListRoomPin("", 4);
                    } else if (intent.getAction().equals(ACTION_LONG_CLICK_ROOM)) {
                        if (b != null) {

                            //chi hien cho tab dang chon
                            int type = b.getInt(Room.CHAT_TYPE, -1);
                            if (type == chatType) {//dung man hinh
                                //chi co 2 thuoc tinh isPin va id
                                Room r = b.getParcelable(Room.ROOM);
                                int action = b.getInt(RoomAction.ROOM_ACTION, -1);
                                if (r != null && action >= 0) {
                                    switch (action) {
                                        case RoomAction.ACTION_PIN:
                                            pinRoom(r.get_id());
                                            break;
                                        case RoomAction.ACTION_UNPIN:
                                            unpinRoom(r.get_id());
                                            break;
                                        case RoomAction.ACTION_LEAVE_GROUP:
                                            leaveGroupConfirm(r);
                                            break;
                                    }
                                }
                            }

                        }
                    } else if (intent.getAction().equals(ACTION_LEAVE_GROUP)) {
                        if (b != null) {
                            String roomId = b.getString(Room.ROOM_ID, "");
                            if (!TextUtils.isEmpty(roomId)) {
                                adapter.removeRoom(roomId);
                            }
                        }
                    } else if (intent.getAction().equals(ACTION_SEARCH_TEXT)) {
                        if (isCanSearch) {
                            if (b != null) {
                                int type = b.getInt(Room.CHAT_TYPE, -1);
                                keyword = b.getString(SEARCH_TEXT, "");
                                //ko co thi lay tu dau, co thi search(chi lay 1 page 50 ptu)

                                if (!TextUtils.isEmpty(keyword)) {//co keyword thi search theo tung man hinh
                                    if (type == chatType) {//dung man hinh
                                        getData(0);
                                    }
                                } else {//khi keyword rong thi search tat ca, phuc hoi tu dau cho ca 3 tab
                                    getListRoomPin("", 5);
                                }

                            }
                        }
                    } else if (intent.getAction().equals(ACTION_GO_TO_TOP_CHAT)) {
                        if (b != null) {
                            int type = b.getInt(Room.CHAT_TYPE, -1);
                            if (type == chatType) {
                                gotoTop();
                            }
                        }

                    }else if(intent.getAction().equalsIgnoreCase(App.Companion.getWHEN_APP_MOVE_TO_BACKGROUND())){
                        saveCache();
                    }
                }

            }
        };
        if (getContext() != null) {
            getContext().registerReceiver(receiver, new IntentFilter(REFRESH_RECENT_CHAT_ROOM));
            getContext().registerReceiver(receiver, new IntentFilter(ACTION_LONG_CLICK_ROOM));
            getContext().registerReceiver(receiver, new IntentFilter(ACTION_LEAVE_GROUP));
            getContext().registerReceiver(receiver, new IntentFilter(ACTION_SEARCH_TEXT));
            getContext().registerReceiver(receiver, new IntentFilter(ACTION_GO_TO_TOP_CHAT));
            getContext().registerReceiver(receiver, new IntentFilter(App.Companion.getWHEN_APP_MOVE_TO_BACKGROUND()));

            getContext().registerReceiver(receiver, new IntentFilter(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE));
            isRegister = true;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    private void pinRoom(final String roomId) {
        if (!TextUtils.isEmpty(roomId)) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", roomId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("pinRoom", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {
                        if (getActivity() != null && !getActivity().isFinishing())
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject obj = new JSONObject(args[0].toString());
                                        int code = obj.getInt("errorCode");
                                        if (code == 0) {
//                                        MyUtils.showToast(getContext(), R.string.success);
                                            //update room pin len dau
                                            if (adapter != null) {
                                                adapter.setPinRoom(roomId);
                                            }

                                        } else {
                                            String message = obj.getString("error");
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

    private void unpinRoom(final String roomId) {
        if (!TextUtils.isEmpty(roomId)) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", roomId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("unpinRoom", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {
                        if (getActivity() != null && !getActivity().isFinishing())
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject obj = new JSONObject(args[0].toString());
                                        int code = obj.getInt("errorCode");
                                        if (code == 0) {
//                                        MyUtils.showToast(getContext(), R.string.success);
                                            //update unpin
                                            if (adapter != null) {
                                                adapter.setUnPinRoom(roomId);
                                            }

                                        } else {
                                            String message = obj.getString("error");
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

    private void leaveRoom(final Room room) {
        if (room != null) {
            //chi roi khoi room custom va channel
            if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL) ||
                    room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CUSTOM)) {

                if (isSocketConnected()) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("roomId", room.get_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String event = "leaveRoom";
                    if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL)) {
                        event = "leaveChannel";
                    }

                    socket.emit(event, obj, new Ack() {
                        @Override
                        public void call(final Object... args) {
                            if (getActivity() != null && !getActivity().isFinishing())
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject obj = new JSONObject(args[0].toString());
                                            int code = obj.getInt("errorCode");
                                            if (code == 0) {
                                                MyUtils.showToast(getContext(), R.string.success);
                                                //update adapter
                                                //phai goi broadcast vi fragment o ca 3 man hinh
                                                Intent intent = new Intent(ACTION_LEAVE_GROUP);
                                                intent.putExtra(Room.ROOM_ID, room.get_id());
                                                getContext().sendBroadcast(intent);

                                            } else {
                                                String message = obj.getString("error");
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


    //////////////////////////////////////////////////////////////////////////////////////////////

    private void leaveGroupConfirm(final Room room) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(R.string.left_group_confirm);
//        alertDialogBuilder.setIcon(R.drawable.fail);
        alertDialogBuilder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                leaveRoom(room);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void gotoTop() {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_UP);
                nestedScrollView.smoothScrollTo(0, 0);
                getContext().sendBroadcast(new Intent(ChatNhanh_Fragment_Bottom.ACTION_EXPAND_TOOLBAR));
            }
        }, 200);*/

        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.scrollTo(0, 0);
            }
        });
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onResume() {
        super.onResume();

        //moi khi resume thi goi lay lai so luong tin chua doc
        ChatApplication.Companion.getUnreadRoomCount();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private Realm realm;

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }*/

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void syncContactList() {
        if (chatType == RecentChatType.ALL) {
            long start = SystemClock.elapsedRealtime();
            if (isSocketConnected() && getActivity() != null && !getActivity().isFinishing()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("pageIndex", 0);
                    obj.put("itemPerPage", 0);//lay het

                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("getContactList", obj, (Ack) args -> {
                    if (getActivity() != null && !getActivity().isFinishing())
                        getActivity().runOnUiThread(() -> {
                            try {
                                JSONObject obj1 = new JSONObject(args[0].toString());
                                int code = obj1.getInt("errorCode");
                                if (code == 0) {//thanh cong
                                    ArrayList<UserInfo> list = UserInfo.parse(getContext(), args);
                                    if (list != null && list.size() > 0) {
                                        //Dong bo voi db
                                        PhoneUtils.setPhoneForList(list);
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                for (int i = 0; i < list.size(); i++) {
                                                    UserInfo user = list.get(i);
                                                    MyUtils.syncOneContact(realm, user);
                                                }


                                                //hoan thanh step 1
//                                                MyUtils.howLong(start, "sync contact step 0 - " + list.size());

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (getContext() != null)
                                                            getContext().sendBroadcast(new Intent(Contact_Fragment_1_Chat.ACTION_LOAD_CONTACT_IF_ADAPTER_NULL));
                                                    }
                                                }, 500);


                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        });
                });
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


}
