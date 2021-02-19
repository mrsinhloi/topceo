package com.workchat.core.channel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cipolat.superstateview.SuperStateView;
import com.workchat.core.chat.ChatActivity;
import com.workchat.core.chat.ChatAdapter;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * //TIN PIN : NHAN VAO 1 TIN DA PIN THI CLEAR ADAPTER, LAY LAI TU DAU, TIN SẼ KẸP GIỮA 30 TIN TRÊN, 30 TIN DƯỚI,
 * SAU ĐÓ LOAD LÊN LẤY THÊM, LOAD XUỐNG THÌ LẤY THÊM ĐẾN KHI =0 THÌ ĐÃ VỀ TIN 1 (ẨN NÚT VỀ ĐẦU - LOAD LẠI NHƯ MỚI VÀO MÀN HÌNH)
 * Lấy ds logs trước và sau 1 Log	getRoomNearbyLogs	roomId, logId, itemCount
 * Lấy ds logs trước 1 Log	getRoomPreviousLogs	roomId, logId, itemCount
 * Lấy ds logs sau 1 Log	getRoomNextLogs	roomId, logId, itemCount
 */
public class MH06_PinMessageActivity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    /////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    @BindView(R2.id.emptyView)
    SuperStateView emptyView;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    /////////////////////////////////////////////////////////////////////////////////////


    private TinyDB db;
    private UserChatCore user;
    private Socket socket;
    private Room room;

    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_06_pin_message);
        ButterKnife.bind(this);
        db = new TinyDB(this);
        realm = ChatApplication.Companion.getRealmChat();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //////////////////////
        user = ChatApplication.Companion.getUser();
        /////////////////////////////////////////////////////////////////////
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int mypage, int totalItemsCount) {
//                String lastLogDate = adapter.getLastLogId_Top();
                loadHistory();
            }

            @Override
            public void onDetectScroll(boolean isScrollUp) {

            }
        });


        //ChatActivity(row 2903) da luu Room truoc khi goi qua day
        room = (Room) db.getObject(Room.ROOM, Room.class);
        adapter = new ChatAdapter(this,user.get_id(), true, realm);
        if (room != null) {
            adapter.setRoom(room);
        }
        rv.setAdapter(adapter);

        ////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////
        registerReceiver();


    }

    /////////////////////////////////////////////////////////////////////////////////
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void closeProgress() {
        progressBar.setVisibility(View.GONE);
    }

    /////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onResume() {
        super.onResume();
        //get data
        socket = ChatApplication.Companion.getSocket();

        loadHistory();

    }

    private boolean isSocketConnected() {
//        MyUtils.log("isSocketConnected() socket null = " + (socket == null));
        boolean connected = false;
        if (socket != null) {
            connected = socket.connected();
        }

        return connected;
    }


    private ChatAdapter adapter;
    private boolean isLoading = false;
    private boolean loadMore = true;
    public static final int PAGE_ITEM = 20;
    private void loadHistory() {
        showProgress();
        if (loadMore) {
            if (isSocketConnected() && room != null) {

                //Load tu tren xuong duoi, moi nhat da sap xep tren dau
                final long lastPinDate = adapter.getLastPinDate_Bottom();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                    obj.put("itemCount", PAGE_ITEM);
                    if (lastPinDate > 0) {
                        obj.put("lastPinDate", lastPinDate);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MyUtils.log("pin date "+lastPinDate);
                isLoading = true;
                socket.emit("getPinLogs", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ArrayList<RoomLog> list = RoomLog.parseListRoomLog(context, args);
                                if (list != null && list.size() > 0) {

                                    if (lastPinDate > 0) {//load more
                                        adapter.append(list, adapter.getItemCount()-1);
                                    } else {//first
                                        adapter.clearData();
                                        adapter.append(list);
                                    }

                                    if (emptyView.getVisibility() == View.VISIBLE) {
                                        emptyView.setVisibility(View.GONE);
                                    }

                                    if(list.size()<PAGE_ITEM){
                                        loadMore = false;
                                    }else {
                                        loadMore = true;
                                    }
                                } else {
                                    loadMore = false;
                                }
                                isLoading = false;

                                closeProgress();

                            }
                        });


                    }

                });

            } else {
                closeProgress();
            }
        } else {
            closeProgress();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////

    public static final String ACTION_PIN_MESSAGE_CHAT = "ACTION_PIN_MESSAGE_CHAT_" + MH06_PinMessageActivity.class.getSimpleName();
    public static final String ACTION_FINISH = "ACTION_FINISH_" + MH06_PinMessageActivity.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                if (intent.getAction().equalsIgnoreCase(ACTION_PIN_MESSAGE_CHAT)) {
                    if (b != null) {
                        String id = b.getString(RoomLog.ROOM_LOG_ID, "");
                        boolean isPin = b.getBoolean(RoomLog.IS_PIN, false);
                        if (!TextUtils.isEmpty(id) && room != null) {
                            setPinMessage(room.get_id(), id, isPin);
                        }
                    }
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_FINISH)) {
                    finish();
                }


            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_PIN_MESSAGE_CHAT));
        registerReceiver(receiver, new IntentFilter(ACTION_FINISH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
        /*if (realm != null) {
            realm.close();
            realm = null;
        }*/
    }

    private void setPinMessage(String roomId, final String logId, final boolean isPin) {
        if (socket != null && socket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", roomId);
                obj.put("logId", logId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String method = "";
            if (isPin) {
                method = "pinMessage";
            } else {
                method = "unpinMessage";
            }
            socket.emit(method, obj, new Ack() {
                @Override
                public void call(final Object... args) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(args[0].toString());
                                int code = obj.getInt("errorCode");
                                if (code == 0) {
                                    MyUtils.showToast(context, R.string.success);

                                    if (isPin == false) {//xoa khoi danh sach pin
                                        adapter.setUnpinMessageForPinAdapter(logId);

                                        //refresh adapter phia truoc
                                        Intent intent = new Intent(ChatActivity.ACTION_UNPIN_MESSAGE_CHAT);
                                        intent.putExtra(RoomLog.ROOM_LOG_ID, logId);
                                        intent.putExtra(RoomLog.IS_PIN, isPin);
                                        context.sendBroadcast(intent);
                                    }

                                } else {
                                    String message = obj.getString("error");
                                    MyUtils.showToast(context, message);
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
