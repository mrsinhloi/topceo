package com.workchat.core.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.workchat.core.channel.MH06_PinMessageActivity;
import com.workchat.core.chathead.events.CloseRoomEvent;
import com.workchat.core.chathead.events.RefreshRecentEvent;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.contacts.Contact_Fragment_1_Chat;
import com.workchat.core.contacts.Contact_Fragment_2_Online;
import com.workchat.core.database.TinyDB;
import com.workchat.core.imagezoom.ImageZoom;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.PhoneUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by MrPhuong on 11/30/2017.
 */

public class ChatUserDetailActivity extends AppCompatActivity {

    private Context context = this;

    @BindView(R2.id.chatUserDetail_toolbar)
    Toolbar chatUserDetail_toolbar;

    @BindView(R2.id.chatUserDetail_avatar)
    ImageZoom chatUserDetail_avatar;
    @BindView(R2.id.chatUserDetail_call)
    ImageView chatUserDetail_call;

    @BindView(R2.id.chatUserDetail_name)
    AppCompatTextView chatUserDetail_name;
    @BindView(R2.id.chatUserDetail_phone)
    AppCompatTextView chatUserDetail_phone;

    @BindView(R2.id.chatUserDetail_removeOrAddContact)
    AppCompatTextView chatUserDetail_removeOrAddContact;
    @BindView(R2.id.chatUserDetail_removeOrAddContactImg)
    ImageView chatUserDetail_removeOrAddContactImg;

    @BindView(R2.id.chatUserDetail_removeOrAddContactContainer)
    LinearLayout chatUserDetail_removeOrAddContactContainer;
    @BindView(R2.id.chatUserDetail_chatContainer)
    LinearLayout chatUserDetail_chatContainer;
    @BindView(R2.id.chatUserDetail_addToRoomContainer)
    LinearLayout chatUserDetail_addToRoomContainer;
    @BindView(R2.id.chatUserDetail_pinMessageContainer)
    LinearLayout chatUserDetail_pinMessageContainer;
    @BindView(R2.id.linearMedia)
    LinearLayout linearMedia;
    @BindView(R2.id.linearLeaveGroup)
    LinearLayout linearLeaveGroup;
    @BindView(R2.id.linearProfile)
    LinearLayout linearProfile;

    @BindView(R2.id.linear0)
    LinearLayout linearNotify;

    @BindView(R2.id.linearMyXteam)
    LinearLayout linearMyXteam;

    @BindView(R2.id.linearPinContact)
    LinearLayout linearPinContact;
    @BindView(R2.id.txtPinContact)
    AppCompatTextView txtPinContact;
    @BindView(R2.id.chatUserDetail_email)
    AppCompatTextView chatUserDetail_email;


    @BindView(R2.id.chatUserDetail_notifyOption)
    AppCompatCheckBox chatUserDetail_notifyOption;

    private String ownerId;
    private UserInfo userGuest;

    public static final String IS_FROM_CHAT = "IS_FROM_CHAT";
    private boolean isFromChat = false;

    //da add vao danh ba chua
    private boolean isInMyContactChat = false;
    private Realm realm;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (realm != null && !realm.isClosed()) {
            realm.close();
        }*/
    }

    private Room room;
    private TinyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initSocket();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_detail);
        ButterKnife.bind(this);
        db = new TinyDB(this);

        ownerId = ChatApplication.Companion.getUser().get_id();
        realm = ChatApplication.Companion.getRealmChat();

        setSupportActionBar(chatUserDetail_toolbar);
        Drawable d = ChatApplication.Companion.getIconBackCustom();
        if (d != null) {
            chatUserDetail_toolbar.setNavigationIcon(d);
        }else {
            chatUserDetail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        }
        chatUserDetail_toolbar.setNavigationOnClickListener(view -> finish());


        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (getIntent().hasExtra(UserInfo.USER_INFO)) {
                userGuest = b.getParcelable(UserInfo.USER_INFO);

                //LAY TEN LOCAL/////////////////////////////////////////////////////////////////
                realm.beginTransaction();
                UserInfo item = realm.where(UserInfo.class).equalTo("_id", userGuest.get_id()).findFirst();
                if (item != null) {
                    userGuest = realm.copyFromRealm(item);
                }
                realm.commitTransaction();
                //LAY TEN LOCAL/////////////////////////////////////////////////////////////////


            } else {
                realm.beginTransaction();
                UserInfo item = realm.where(UserInfo.class).equalTo("_id", getIntent().getStringExtra("")).findFirst();
                if (item != null) {
                    userGuest = realm.copyFromRealm(item);
                }
                realm.commitTransaction();
            }

            //Logger.d(userGuest.lastUpdate);

            if (userGuest != null) {
                //load avatar
                loadAvatar(userGuest);
                //set ten, sdt
                chatUserDetail_name.setText(userGuest.getName());

                if (!TextUtils.isEmpty(userGuest.getPhone())) {
                    chatUserDetail_phone.setText(userGuest.getPhone());
                } else {
                    chatUserDetail_phone.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(userGuest.getEmail())) {
                    chatUserDetail_email.setText(userGuest.getEmail());
                } else {
                    chatUserDetail_email.setVisibility(View.GONE);
                }
                chatUserDetail_phone.setOnClickListener(v -> MyUtils.copy(context, userGuest.getPhone()));

                setUIContact();
                //thong tin de cap nhat cau hinh ntf
                //phai lay lai thong tin cho room
                initRoomChatWithUser(userGuest.get_id());
            }
            isFromChat = b.getBoolean(IS_FROM_CHAT, false);
            room = b.getParcelable(Room.ROOM);
            if (room != null) {
                roomId = room.get_id();
            } else {
                //neu ko co room thi ko cho set nhan thong bao
                linearNotify.setVisibility(View.GONE);
            }
        }

        //call phone
        chatUserDetail_call.setVisibility(View.INVISIBLE);
        if(userGuest != null) {
            String phone = userGuest.getPhone();
            if (!TextUtils.isEmpty(phone)) {
                chatUserDetail_call.setVisibility(View.VISIBLE);
                chatUserDetail_call.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);

                });
            }
        }

        chatUserDetail_removeOrAddContactContainer.setOnClickListener(v -> {
            if (isInMyContactChat) {
                //confirm truoc khi xoa
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(R.string.confirm_remove_contact);
                alertDialogBuilder.setTitle(R.string.notification);
                alertDialogBuilder.setPositiveButton(R.string.delete, (arg0, arg1) -> {
                    removeContact(userGuest.get_id());
                    arg0.dismiss();
                });
                alertDialogBuilder.setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } else {
                //confirm truoc khi xoa
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(R.string.confirm_add_contact);
                alertDialogBuilder.setTitle(R.string.notification);
                alertDialogBuilder.setPositiveButton(R.string.ok, (arg0, arg1) -> {
                    addContact(userGuest.get_id());
                    arg0.dismiss();
                });
                alertDialogBuilder.setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
        chatUserDetail_chatContainer.setOnClickListener(v -> {
            if (isFromChat) {
                finish();
            } else {
                //chat nhanh
                MyUtils.chatWithUser(context, userGuest);
            }
        });

        chatUserDetail_addToRoomContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchUserChatFromRestApiActivity.class);
            startActivity(intent);
        });


        chatUserDetail_pinMessageContainer.setOnClickListener(v -> {
            if (MyUtils.checkInternetConnection(context)) {
                if (room != null) {
                    db.putObject(Room.ROOM, room);
                    Intent intent = new Intent(context, MH06_PinMessageActivity.class);
                    startActivity(intent);
                } else {
                    MyUtils.showToast(context, R.string.please_check_internet);
                }
            } else {
                MyUtils.showThongBao(context);
            }
        });
        chatUserDetail_notifyOption.setOnCheckedChangeListener((buttonView, isChecked) -> setMuteNotify(!isChecked));

        linearMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (room != null) {
                    Intent intent = new Intent(context, MH30_Media_Activity.class);
                    intent.putExtra(Room.ROOM_ID, room.get_id());
                    startActivity(intent);
                }
            }
        });
        linearLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (room != null) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                    dialog.setMessage(R.string.hide_this_room_chat_ques);
                    dialog.setTitle(R.string.notification);
                    dialog.setPositiveButton(R.string.ok, (arg0, arg1) -> {
                        arg0.dismiss();
                        leaveGroup();
                    });
                    dialog.setNegativeButton(R.string.no, (arg0, arg1) -> {
                        arg0.dismiss();
                    });

                    android.app.AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
            }
        });


        linearMyXteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open timeline of user in myxteam
                //myxteam://view/timeline?id=284&name=phuong&avatar=https://www....
                if (userGuest != null) {
                    Uri deeplink = Deeplink.getDeeplinkTimelineUser(userGuest.get_id(), userGuest.getName(), userGuest.getAvatar());
                    Deeplink.openMyxteam(context, deeplink);
                }
            }
        });

        linearProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userGuest != null) {
                    ChatApplication.Companion.openProfile(userGuest.getName());
                }
            }
        });

    }

    private void setUIContact() {
        //kiem tra xem user da co trong danh ba chua
        if (!isFinishing()) {
            /*realm.executeTransaction(realm -> {
                UserInfo user = realm
                        .where(UserInfo.class)
                        .equalTo("phone", userGuest.getPhone())
                        .or()
                        .equalTo("email", userGuest.getEmail())
                        .findFirst();

            });*/

            if (userGuest != null) {

                linearPinContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPinContact(!userGuest.isPin(), userGuest.get_id());
                    }
                });


                if (userGuest.isInChatContact()) {
                    chatUserDetail_removeOrAddContact.setText(R.string.remove_from_contacts);
                    chatUserDetail_removeOrAddContactImg.setImageResource(R.drawable.ic_contacts);
                    chatUserDetail_removeOrAddContact.setTextColor(ContextCompat.getColor(context, R.color.red_500));
                    isInMyContactChat = true;
                    linearPinContact.setVisibility(View.VISIBLE);
                    txtPinContact.setText(userGuest.isPin() ? R.string.unpin_contact : R.string.pin_contact);

                } else {
                    chatUserDetail_removeOrAddContact.setText(R.string.add_to_contacts);
                    chatUserDetail_removeOrAddContactImg.setImageResource(R.drawable.ic_contacts);
                    chatUserDetail_removeOrAddContact.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                    isInMyContactChat = false;
                    linearPinContact.setVisibility(View.GONE);
                }

            } else {
                chatUserDetail_removeOrAddContact.setText(R.string.add_to_contacts);
                chatUserDetail_removeOrAddContactImg.setImageResource(R.drawable.ic_contacts);
                chatUserDetail_removeOrAddContact.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                isInMyContactChat = false;
                linearPinContact.setVisibility(View.GONE);
            }
        }
    }

    private void loadAvatar(UserInfo user) {
        if (!TextUtils.isEmpty(user.getAvatar())) {
            //load photo
            int size = MyUtils.getScreenWidth(context);//getResources().getDimensionPixelSize(R.dimen.photo_profile) * 5;
            /*Picasso.get()
                    .load(user.getAvatar())
                    .resize(size, size)
                    .placeholder(R.drawable.icon_no_image)
                    .transform(new CenterCrop(), new CropCircleTransformation())
                    .into(chatUserDetail_avatar);*/
            if (context != null && !isFinishing()) {
                Glide.with(context)
                        .load(user.getAvatar())
                        .override(size, size)
                        .placeholder(R.drawable.icon_no_image)
                        .transform(new CenterCrop(), new RoundedCorners(size / 2))
                        .into(chatUserDetail_avatar);
            }
        } else {
            if (user != null && !TextUtils.isEmpty(user.getName())) {
                if (user.getName()!=null && user.getName().length() > 0) {
                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    int color = generator.getColor(user.getName());

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(user.getName().substring(0, 1), color);
                    chatUserDetail_avatar.setImageDrawable(drawable);
                }
            }

        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    private Socket socket;

    private void initSocket() {
        //get data
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSocket();
    }

    private void removeContact(final String userId) {
        if (socket != null && socket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(userId);

                String listString = new Gson().toJson(
                        ids,
                        new TypeToken<ArrayList<Long>>() {
                        }.getType());
                JSONArray array = new JSONArray(listString);

                obj.put("contacts", array);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //luc nao cung tra ve nguoi add, phai goi lai ham get contact list
            socket.emit("removeContact", obj, (Ack) args -> runOnUiThread(() -> {
                try {
                    JSONObject obj1 = new JSONObject(args[0].toString());
                    int code = obj1.getInt("errorCode");
                    if (code == 0) {
                        //xoa thanh cong
//                        MyUtils.showToast(context, R.string.delete_success);

                        //xoa khoi tab online va chatnhanh
                        Intent intent = new Intent(Contact_Fragment_2_Online.ACTION_REMOVE_CONTACT_TAB_ONLINE);
                        intent.putExtra(UserInfo.USER_INFO, userGuest);
                        sendBroadcast(intent);

                        intent = new Intent(Contact_Fragment_1_Chat.ACTION_REMOVE_CONTACT_TAB_CHATNHANH);
                        intent.putExtra(UserInfo.USER_INFO, userGuest);
                        sendBroadcast(intent);


                        //xu ly local
                        realm.beginTransaction();
                        UserInfo user = realm.where(UserInfo.class).equalTo("_id", userId).findFirst();
                        if (user != null) {
                            /*user.setInChatContact(false);
                            user.setPin(false);*/

                            //local
                            userGuest = realm.copyFromRealm(user);
                            userGuest.setInChatContact(false);

                            //xoá
                            user.deleteFromRealm();
                        }
                        realm.commitTransaction();

                        //load lai danh sach
//                        sendBroadcast(new Intent(Contact_Fragment_3_All.ACTION_GET_CONTACT_LIST_MBN));

                        //co thi moi pin/unpin
                        setUIContact();

                    } else {
                        String message = obj1.getString("error");
                        MyUtils.showAlertDialog(context, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }));
        }
    }

    private void addContact(final String userId) {
        if (socket != null && socket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(userId);

                String listString = new Gson().toJson(
                        ids,
                        new TypeToken<ArrayList<Long>>() {
                        }.getType());
                JSONArray array = new JSONArray(listString);

                obj.put("contacts", array);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //luc nao cung tra ve nguoi add, phai goi lai ham get contact list
            socket.emit("addContact", obj, (Ack) args -> runOnUiThread(() -> {
                try {
                    JSONObject obj1 = new JSONObject(args[0].toString());
                    int code = obj1.getInt("errorCode");
                    if (code == 0) {
                        //xoa thanh cong
//                        MyUtils.showToast(context, R.string.add_success);


                        whenAddContact();

                        //set lai giao dien
                        setUIContact();

                        //load lai danh sach
//                        sendBroadcast(new Intent(Contact_Fragment_3_All.ACTION_GET_CONTACT_LIST_MBN));

                        //co thi moi pin/unpin
//                        linearPinContact.setVisibility(View.VISIBLE);


                    } else {
                        String message = obj1.getString("error");
                        MyUtils.showAlertDialog(context, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }));
        }
    }

    private void whenAddContact() {
        //add vao db, hoac update la trong contact, roi load lai danh sach theo section, pin+contact
        realm.beginTransaction();
        userGuest.setInChatContact(true);
        PhoneUtils.setPhoneForUser(userGuest);
        MyUtils.syncOneContact(realm, userGuest);
        realm.commitTransaction();

        //load lai danh sach contact chat
        context.sendBroadcast(new Intent(Contact_Fragment_1_Chat.ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT));
        //load lai danh sach online
        context.sendBroadcast(new Intent(Contact_Fragment_2_Online.ACTION_LOAD_CONTACT_ONLINE));
    }

    private String roomId = "";

    private void setMuteNotify(final boolean isMuted) {
        if (socket != null && socket.connected() && !TextUtils.isEmpty(roomId)) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", roomId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit(isMuted ? "muteChatRoom" : "unmuteChatRoom", obj, (Ack) args -> {
                // socket return sasUrl, sasThumbUrl (nếu là image)
                runOnUiThread(() -> {
                    MyUtils.showToast(context, R.string.update_success);
                    //update man hinh chat
                    try {
                        Intent intent = new Intent(ChatActivity.ACTION_UPDATE_MEMBER_RECEIVE_NOTIFICATION);
                        intent.putExtra(Member.MEMBER_ID, ownerId);
                        intent.putExtra(Member.IS_MUTED, isMuted);
                        sendBroadcast(intent);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    private void initRoomChatWithUser(final String userId) {
        if (socket != null && socket.connected()) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("userId", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (obj.length() > 0) {
                socket.emit("getPrivateRoom", obj, (Ack) args -> runOnUiThread(() -> {
                    Room room = Room.parseRoom(context, args);
                    if (room != null) {
                        roomId = room.get_id();

                        //set cau hinh ntf
                        try {
                            Member me = getMember(ownerId, room);
                            if (me != null) {
                                chatUserDetail_notifyOption.setChecked(!me.isMuted());
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        }
    }

    private Member getMember(String userId, Room room) {
        Member member = null;
        if (room != null && room.getMembers() != null && room.getMembers().size() > 0) {
            for (int i = 0; i < room.getMembers().size(); i++) {
                Member m = room.getMembers().get(i);
                if (m.getUserId().equals(userId)) {
                    member = m;
                    break;
                }
            }
        }
        return member;
    }


    private void setPinContact(final boolean isPin, String userId) {
        if (socket != null && socket.connected() && !TextUtils.isEmpty(roomId)) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("contactId", userId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit(isPin ? "pinContact" : "unpinContact", obj, (Ack) args -> {
                // socket return sasUrl, sasThumbUrl (nếu là image)
                runOnUiThread(() -> {
                    realm.beginTransaction();
                    UserInfo item = realm.where(UserInfo.class).equalTo("_id", userGuest.get_id()).findFirst();
                    if (item != null) {
                        item.setPin(isPin);
                        userGuest.setPin(isPin);

                    }
                    realm.commitTransaction();

                    //load lai danh sach contact chat
                    context.sendBroadcast(new Intent(Contact_Fragment_1_Chat.ACTION_LOAD_CONTACT_HAVE_MBN_ACCOUNT));

//                    MyUtils.log("ok" + args[0].toString());
                    setUIContact();
                });
            });
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void leaveGroup() {
        if (isSocketConnected() && room != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //todo
            socket.emit("hideRoom", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    boolean isSuccess = RoomLog.isSuccess(context, args);
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyUtils.showToast(context, R.string.success);

                                //refresh man hinh recent chat room
                                Intent intent = new Intent(RecentChat_Fragment.REFRESH_RECENT_CHAT_ROOM);
                                sendBroadcast(intent);

                                //dong man hinh chat
                                intent = new Intent(ChatActivity.FINISH_ACTIVITY);
                                sendBroadcast(intent);

                                //refresh widget recent
                                EventBus.getDefault().post(new RefreshRecentEvent());
                                //close room widget
                                EventBus.getDefault().post(new CloseRoomEvent(room.get_id()));

                                finish();
                            }
                        });
                    }

                }
            });
        }
    }

    private boolean isSocketConnected() {
        socket = ChatApplication.Companion.getSocket();
        if (socket != null) {
            return socket.connected();
        }
        return false;
    }

}