/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.workchat.core.chathead.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.workchat.core.chat.ChatActivity;
import com.workchat.core.chat.RecentChatType;
import com.workchat.core.chat.RecentChat_Adapter;
import com.workchat.core.chat.receiver.NetworkChangeReceiver;
import com.workchat.core.chat.socketio.AckWithTimeOut;
import com.workchat.core.chathead.events.MenuEvent;
import com.workchat.core.chathead.events.RefreshRecentEvent;
import com.workchat.core.chathead.introduction.HoverMotion;
import com.workchat.core.chathead.theming.HoverTheme;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.event.EventChatRecent;
import com.workchat.core.event.RoomLogEvent_Have_Message;
import com.workchat.core.event.RoomLogEvent_IsViewed;
import com.workchat.core.event.SocketConnectEvent;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.ChatView;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.search.MH09_SearchActivity;
import com.workchat.core.utils.MyUtils;
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
import io.mattcarroll.events.BackPressEvent;
import io.mattcarroll.hover.Content;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * {@link Content} that displays an introduction to Hover.
 */
public class RecentChatRoomContent extends FrameLayout implements Content {

    private HoverMotion mHoverMotion;

    public RecentChatRoomContent(@NonNull Context context) {
        super(context);
        init();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void init() {
        initSocket();
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_1_recent_chat_room_bubble, this, true);
        mHoverMotion = new HoverMotion();
        ButterKnife.bind(this);

        initNewForTab();
        initUI();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        //mo lai thi lay lai moi nhat neu dang o page dau tien
        isDetach = false;
    }

    private void refreshList() {
        if (isSocketConnected() && adapter != null && adapter.getItemCount() > 0) {

            //dang focus o page 1
            /*if (nestedScrollView != null) {
                float y = nestedScrollView.getScrollY();
//                    MyUtils.showToastDebugInThread(getContext(), "Load again y = " + y);
                if ((int)y <= screenHeight / 2) {
                    //load lai danh sach moi nhat
                    getListRoomPin("", 3);

                }
            }*/

            //Cu vao la load moi nhat
            //load lai danh sach moi nhat
            getListRoomPin("", 3);
        }
    }

    @Override
    public void onShown() {
//        mHoverMotion.start(mLogo);
        //forcus lai socket, neu ko co ket noi thi goi ket noi lai
        initSocket();
        refreshList();
//        MyUtils.showToastDebugInThread(getContext(), "onShow()");
    }

    @Override
    public void onHidden() {
//        mHoverMotion.stop();
//        MyUtils.showToastDebugInThread(getContext(), "onHidden()");
    }

    private boolean isDetach = false;

    @Override
    protected void onDetachedFromWindow() {
//        EventBus.getDefault().unregister(this);
//        whenDetach();
        super.onDetachedFromWindow();
        isDetach = true;

    }


    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean isFullscreen() {
        return true;
    }


    // Called in Android UI's main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull HoverTheme newTheme) {
//        mHoverTitleTextView.setTextColor(newTheme.getAccentColor());
//        mGoalsTitleTextView.setTextColor(newTheme.getAccentColor());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHAT_TYPE = "CHAT_TYPE";
    private int chatType = RecentChatType.ALL;
    boolean isForward = false;
    boolean isSharing = false;
    private boolean isCanSearch = false;

    public void setCanSearch(boolean canSearch) {
        isCanSearch = canSearch;
    }

    private boolean isCanLongClick = false;

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
        if (socket == null) {
            ChatApplication.Companion.getSocketInitIfNull();
        }
        if (socket != null && !socket.connected()) {
            socket.connected();
        }
    }

    private boolean isSocketConnected() {
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && socket.connected()) {
            return true;
        } else {
            initSocket();
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

        /*linearCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchUserChat2Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        linearCreateChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MH03_ChannelActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });*/

    }

    private int screenHeight = 0;
    private boolean isInternetConnected = true;
    //////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.imgAvatar)
    ImageView imgAvatar;

    private void initUI() {
        realm = ChatApplication.Companion.getRealmChat();
        db = new TinyDB(getContext());
        user = ChatApplication.Companion.getUser();
        if (user != null) {
            try {
                ownerId = user.get_id();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            //avatar
            //room avatar
            int avatarSize = getResources().getDimensionPixelSize(R.dimen.chathead_size);
            Glide.with(getContext())
                    .load(user.getAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_2)
                    .transform(new RoundedCorners(avatarSize / 2))
                    .override(avatarSize, avatarSize)
                    .into(imgAvatar);
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
                /*if (v.getChildAt(v.getChildCount() - 1) != null) {

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
                }*/

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
//        syncContactList();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
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
            adapter.setFromWidget(true);
            rv.setAdapter(adapter);

            getListRoomPin("", 1);
        }
    }


    private boolean isScrollDown = false;
    private ArrayList<UserInfo> listUsers;
    //////////////////////////////////////////////////////////////////////////////////////////////
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
        if (isSocketConnected() && getContext() != null) {
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
                                RecentChatRoomContent.this.post(new Runnable() {
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
        if (isSocketConnected() && getContext() != null) {

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
                    ArrayList<Room> list = Room.parseListRoom(getContext(), args);
                    RecentChatRoomContent.this.post(new Runnable() {
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
            });
        } else {
            closeRefresh();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private Realm realm;

    private void whenDetach() {
        /*if (task1 != null && task1.getStatus() != AsyncTask.Status.FINISHED) {
            task1.cancel(true);
        }

        saveCache();
        if(adapter!=null){
            adapter.clear();
        }

        if (realm != null) {
            realm.close();
            realm = null;
        }*/

        saveCache();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
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
                if (log != null && log.getAuthorInfo() != null && user != null && (log.getAuthorInfo().getUserId().equals(user.get_id()))) {
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

    //////////////////////////////////////////////////////////////////////////////////////////////
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
                        RecentChatRoomContent.this.post(new Runnable() {
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
                        RecentChatRoomContent.this.post(new Runnable() {
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
                            RecentChatRoomContent.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject obj = new JSONObject(args[0].toString());
                                        int code = obj.getInt("errorCode");
                                        if (code == 0) {
                                            MyUtils.showToast(getContext(), R.string.success);
                                            //update adapter
                                            //phai goi broadcast vi fragment o ca 3 man hinh
                                            /*Intent intent = new Intent(ACTION_LEAVE_GROUP);
                                            intent.putExtra(Room.ROOM_ID, room.get_id());
                                            getContext().sendBroadcast(intent);*/

                                            adapter.removeRoom(room.get_id());

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
    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    private void registerReceiver() {
        if (getContext() != null) {
            getContext().registerReceiver(networkChangeReceiver, new IntentFilter(NetworkChangeReceiver.ACTION_CONNECTIVITY_CHANGE));
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull BackPressEvent event) {
        if (!isDetach) {
            EventBus.getDefault().post(new MenuEvent(true));
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull RefreshRecentEvent event) {
        if (!isDetach) {
            //refresh
            getListRoomPin("", 4);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////


}
