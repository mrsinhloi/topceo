package com.workchat.core.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.Action;
import com.workchat.core.models.chat.ContentType;
import com.workchat.core.models.chat.Item;
import com.workchat.core.models.chat.Link;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.RoomAction;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.chat.Text;
import com.workchat.core.models.realm.LastLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.search.MH09_SearchActivity;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.Utils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.Realm;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by MrPhuong on 2017-09-21.
 */

public class RecentChat_Adapter extends RecyclerView.Adapter<RecentChat_Adapter.ItemHolder> {

    private Context context;
    private int avatarSize = 80, avatarSizeSmall = 40;

    public ArrayList<Room> getData() {
        return data;
    }

    private ArrayList<Room> data = new ArrayList<Room>();
    private boolean isCanLongClick = true;

    public void setCanLongClick(boolean canLongClick) {
        isCanLongClick = canLongClick;
    }

    private boolean isForward = false;
    public void setIsForward(boolean isForward) {
        this.isForward = isForward;
    }

    private boolean isSharing = false;
    public void setSharing(boolean sharing) {
        isSharing = sharing;
    }

    private boolean isFromWidget = false;

    public void setFromWidget(boolean fromWidget) {
        isFromWidget = fromWidget;
    }

    private int chatType = RecentChatType.ALL;
    private UserChatCore owner;
    private Realm realm;

    public RecentChat_Adapter(/*Context context, */List<Room> list, int chatType) {
        this.realm = ChatApplication.Companion.getRealmChat();
        this.context = ChatApplication.instance.getApplicationContext();
        this.data = new ArrayList<>(list);
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        avatarSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_smaller);
        this.chatType = chatType;
        owner = ChatApplication.Companion.getUser();
        this.realm = realm;
    }

    public RecentChat_Adapter(Context context, List<Room> list, int chatType) {
        this.realm = ChatApplication.Companion.getRealmChat();
        this.context = context;
        this.data = new ArrayList<>(list);
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        avatarSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_smaller);
        this.chatType = chatType;
        owner = ChatApplication.Companion.getUser();
        this.realm = realm;
    }


    synchronized public void sort() {
        data = sortRecentList(data);
        notifyDataSetChanged();
    }


    /**
     * Dua vao 1 list tra ve 1 list da sort
     *
     * @param chats
     * @return
     */
    synchronized public ArrayList<Room> sortRecentList(ArrayList<Room> chats) {
        ArrayList<Room> root = new ArrayList<Room>();
        ArrayList<Room> listPin = new ArrayList<Room>();
        ArrayList<Room> listUnPin = new ArrayList<Room>();

        //root dung xu ly trong qua trinh sap xep
        root.addAll(new ArrayList<Room>(chats));

        int size = root.size();
        for (int i = 0; i < size; i++) {
            Room r = root.get(i);
            if (r.isPin()) {
                listPin.add(r);
                root.remove(i);
                i -= 1;
                size -= 1;
            }
        }

        //sort theo lastLogDate
        Collections.sort(root, new SortByDate());

        //sort list pin va add vao dau danh sach
        if (listPin.size() > 0) {
            Collections.sort(listPin, new SortByDate());
            root.addAll(0, listPin);
        }
        return root;
    }



    class SortByDate implements Comparator<Room> {
        @Override
        public int compare(Room a, Room b) {
            if (a.getLastLogDateLong() == b.getLastLogDateLong()) {
                return 0;
            } else {
                return a.getLastLogDateLong() < b.getLastLogDateLong() ? 1 : -1;
            }
        }
    }


    @Override
    public RecentChat_Adapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_1_recent_chat_room_row, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final Room m = data.get(position);

        if (m.isViewed()) {
            holder.txt1.setTypeface(holder.tp1);
            holder.txt2.setTypeface(holder.tp2);

            holder.txt3.setTypeface(holder.tp3);
            holder.txt3.setTextColor(ContextCompat.getColor(context, R.color.grey_500));
            holder.txt4.setTypeface(holder.tp3);
            holder.txt4.setTextColor(ContextCompat.getColor(context, R.color.grey_500));
        } else {
            holder.txt1.setTypeface(holder.txt1.getTypeface(), Typeface.BOLD);
            holder.txt2.setTypeface(holder.txt2.getTypeface(), Typeface.BOLD);

            holder.txt3.setTypeface(holder.txt4.getTypeface(), Typeface.BOLD);
            holder.txt3.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.txt4.setTypeface(holder.txt4.getTypeface(), Typeface.BOLD);
            holder.txt4.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        //chat 1-1
        /*if (Room.ROOM_TYPE_PRIVATE.equalsIgnoreCase(m.getType())) {
            //LAY TEN LOCAL/////////////////////////////////////////////////////////////////
            realm.beginTransaction();
            UserInfo item = realm.where(UserInfo.class).equalTo("nameMBN", m.getRoomName()).findFirst();
            if (item != null) {
                m.setRoomName(item.getName());
            }
            realm.commitTransaction();
            //LAY TEN LOCAL/////////////////////////////////////////////////////////////////
        }*/


        //set noi dung
        if (m.getUserId1()!=null && m.getUserId2()!=null && m.getUserId1().equals(m.getUserId2()) && Room.ROOM_TYPE_PRIVATE.equalsIgnoreCase(m.getType())) {
            holder.txt1.setText(R.string.saved_messages);
            //doi icon
            holder.img1.setImageResource(R.drawable.ic_bookmark);
//            holder.txt3.setText(R.string.you);
        } else {
            holder.txt1.setText(m.getRoomName());
            //load image
            if (!TextUtils.isEmpty(m.getRoomAvatar())) {
                Picasso.get()
                        .load(m.getRoomAvatar())
                        .resize(avatarSize, avatarSize)
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .into(holder.img1);
            } else {
                //load theo name
                if(!TextUtils.isEmpty(m.getRoomName()) && m.getRoomName().length()>1){
                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    int color = generator.getColor(m.getRoomName());

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(m.getRoomName().substring(0,1), color);
                    holder.img1.setImageDrawable(drawable);
                }
            }

        }

        //ai goi tin nay
        if (m.getLastLogAuthor() != null && !TextUtils.isEmpty(m.getLastLogAuthor().getName())) {
            holder.txt3.setText((m.getLastLogAuthor().getName()+": "));
        }

        if (m.isPin()) {
            holder.img3.setVisibility(View.VISIBLE);
        } else {
            holder.img3.setVisibility(View.GONE);
        }

        //set ngay
        holder.txt2.setText(MyUtils.getDateChat(m.getLastLogDate()));

        holder.linearRoot.setOnClickListener(view -> {
            if (context != null) {
                if (MyUtils.checkInternetConnection(context)) {
                    if(isSharing){
                        isSharing = false;
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                        dialog.setMessage(context.getString(R.string.send_to_room_x, holder.txt1.getText()));
                        dialog.setTitle(R.string.app_name);
                        dialog.setNegativeButton(R.string.cancel, null);
                        dialog.setPositiveButton(R.string.ok, (arg0, arg1) -> {
                            arg0.dismiss();
                            MyUtils.chatWithRoom(context, m, isForward);

                            //set room da view
                            if(position>=0 && position<data.size()) {
                                data.get(position).setViewed(true);
                                notifyItemChanged(position);
                            }

                            //dong man hinh search
                            if (isForward) {
                                context.sendBroadcast(new Intent(MH09_SearchActivity.ACTION_FINISH));
                            }
                        });
                        android.app.AlertDialog alertDialog = dialog.create();
                        alertDialog.show();

                    }else if(isFromWidget){
//                        AddRoomEvent event = new AddRoomEvent(m.get_id(), m.getRoomAvatar(), true);
//                        EventBus.getDefault().post(event);
                        MyUtils.openRoomChatWidget(m.get_id(), m.getRoomAvatar(), true);
                    }
                    else{
                        MyUtils.chatWithRoom(context, m, isForward);

                        //set room da view
                        if(position>=0 && position<data.size()) {
                            data.get(position).setViewed(true);
                            notifyItemChanged(position);
                        }

                        //dong man hinh search
                        if (isForward) {
                            context.sendBroadcast(new Intent(MH09_SearchActivity.ACTION_FINISH));
                        }
                    }

                } else {
                    if(isFromWidget){
                        MyUtils.showToast(context, R.string.khongCoInternet);
                    }else {
                        MyUtils.showThongBao(context);
                    }
                }
            }
        });


        LastLog log = m.getLastLog();
        holder.txt4.setText("");
        if (log != null) {
            switch (log.getType()) {
                case ContentType.ACTION:
                    holder.txt3.setText("");
//                    holder.txt4.setTextColor(Color.GRAY);
                    try {
                        JsonObject json = log.getContent();
                        if (json != null) {
                            Action action = new Action();
                            JsonElement element = json.get("actionType");
                            if (element != null && !element.isJsonNull()) {
                                action.setActionType(json.get("actionType").getAsString());
                                action.setData(json.getAsJsonObject("data").toString());
                                if (action != null) {
                                    holder.txt4.setText(Html.fromHtml(action.getActionMessage(context)));
                                }
                            }

                        }

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }




                    break;
                case ContentType.TEXT:
                    if (log.getContent() != null) {
                        Text t = null;
                        try {
                            t = new Gson().fromJson(log.getContent().toString(), Text.class);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        if (t != null) {
                            String content = Utils.getMentionUserName(t.getContent());
                            holder.txt4.setText(content);
                        }
                    }

                    break;
                case ContentType.IMAGE:
                            /*if(log.getContent()!=null) {
                                Image i = new Gson().fromJson(log.getContent().toString(), Image.class);
                                holder.txt4.setText(i.getLink());
                            }*/
                    holder.txt4.setText(R.string.sent_photo);

                    break;
                case ContentType.VIDEO:
                    holder.txt4.setText(R.string.sent_video);

                    break;
                case ContentType.ALBUM:
                    holder.txt4.setText(R.string.sent_album);

                    break;
                case ContentType.FILE:
                    holder.txt4.setText(R.string.sent_file);

                    break;
                case ContentType.VOICE:
                    holder.txt4.setText(R.string.sent_voice);

                    break;
                case ContentType.CONTACT:
                    holder.txt4.setText(R.string.sent_contact);

                    break;
                case ContentType.LINK:
                    if (log.getContent() != null) {
                        Link link = new Gson().fromJson(log.getContent().toString(), Link.class);
                        String text = link.getText();
                        text = Utils.replaceAtMentionsWithLinksNew(text);
                        holder.txt4.setText(Html.fromHtml(text));
                    }

                    break;
                case ContentType.LOCATION:
                    // Location location = new Gson().fromJson(log.getContent().toString(), Location.class);
                    holder.txt4.setText(R.string.sent_location);

                    break;
                case ContentType.ITEM:
                    if (log.getContent() != null) {
                        Item item = new Gson().fromJson(log.getContent().toString(), Item.class);
                        String text = item.getItemName();
                        holder.txt4.setText(text);
                    }

                    break;
                case ContentType.PLAN:
                    holder.txt4.setText(R.string.sent_plan);
                    break;
                    case ContentType.MEETING:
                    holder.txt4.setText(R.string.sent_zoom_meeting);
                    break;

            }
        }


        holder.linearRoot.setOnLongClickListener(view -> {
            if (isCanLongClick) {
                Room r = m.duplicate();
                if (r != null) {
                    r.setMembers(null);
                    r.setLastLog(null);
                    r.setLastLogAuthor(null);

                    initPopupMenu(r, holder.txt1);
                    return true;
                }
            }
            return false;
        });
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    private void initPopupMenu(final Room room, TextView v) {

        if (room != null) {
            if (v != null) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();

                Menu menu = popup.getMenu();
                inflater.inflate(R.menu.menu_action_recent_chat_pin_room, menu);
                if (room.isPin()) {
                    menu.findItem(R.id.action_1).setVisible(false);
                    menu.findItem(R.id.action_2).setVisible(true);
                } else {
                    menu.findItem(R.id.action_1).setVisible(true);
                    menu.findItem(R.id.action_2).setVisible(false);
                }

                //room tu tao hoac channel thi moi cho roi khoi nhom
                if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL) ||
                        room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CUSTOM)) {
                    menu.findItem(R.id.action_3).setVisible(true);
                } else {
                    menu.findItem(R.id.action_3).setVisible(false);
                }

                MyUtils.setForceShowIcon(popup);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.action_1) {//pin
                            Intent intent = new Intent(RecentChat_Fragment.ACTION_LONG_CLICK_ROOM);
                            intent.putExtra(Room.ROOM, room);
                            intent.putExtra(Room.CHAT_TYPE, chatType);
                            intent.putExtra(RoomAction.ROOM_ACTION, RoomAction.ACTION_PIN);
                            context.sendBroadcast(intent);
                            return true;
                        } else if (itemId == R.id.action_2) {//unpin
                            Intent intent;
                            intent = new Intent(RecentChat_Fragment.ACTION_LONG_CLICK_ROOM);
                            intent.putExtra(Room.ROOM, room);
                            intent.putExtra(Room.CHAT_TYPE, chatType);
                            intent.putExtra(RoomAction.ROOM_ACTION, RoomAction.ACTION_UNPIN);
                            context.sendBroadcast(intent);
                            return true;
                        } else if (itemId == R.id.action_3) {//leave group
                            Intent intent;
                            intent = new Intent(RecentChat_Fragment.ACTION_LONG_CLICK_ROOM);
                            intent.putExtra(Room.ROOM, room);
                            intent.putExtra(Room.CHAT_TYPE, chatType);
                            intent.putExtra(RoomAction.ROOM_ACTION, RoomAction.ACTION_LEAVE_GROUP);
                            context.sendBroadcast(intent);
                            return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }


        }
    }


    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }

    private int isRoomExist(Room room) {
        int position = -1;

        if (room != null && data != null) {
            for (int i = 0; i < data.size(); i++) {
                Room item = data.get(i);
                if (item.get_id().equals(room.get_id())) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }

    public void replaceListItem(List<Room> list) {
        if (list != null && list.size() > 0) {
            data.clear();
            data.addAll(list);
            sort();
        }
    }

    public void addMore(List<Room> list) {
        if (list != null && list.size() > 0) {
            int size = data.size();
            data.addAll(list);
            notifyItemRangeInserted(size, list.size());
//            sort();
        }
    }


    public void addMore(List<Room> list, int position) {
        if (list != null && list.size() > 0) {
            data.addAll(position, list);
            notifyItemRangeInserted(position, list.size());
//            sort();
        }
    }

    public long getLastLogDate() {
        long kq = 0;
        if (data != null && data.size() > 0) {
            kq = data.get(data.size() - 1).getLastLogDate();
        }
        return kq;
    }

    public void updateMessageRoom(RoomLog log, boolean isViewed) {
        if (data != null && log != null) {
            int positionRoom = findPosition(log.getRoomId());
            if (positionRoom > -1) {

                LastLog ll = new LastLog();
                ll.setType(log.getType());
                ll.setUserIdAuthor(log.getUserIdAuthor());
                ll.setChatLogId(log.get_id());
                ll.setContent(log.getContent());
                ll.setUserIdAuthor(log.getUserIdAuthor());

                //ROOM DA TON TAI THI UPDATE LAST_LOG
                data.get(positionRoom).setViewed(isViewed);
                data.get(positionRoom).setLastLog(ll);
                data.get(positionRoom).setLastLogDate(log.getCreateDate());
                data.get(positionRoom).setLastLogAuthor(log.getAuthorInfo());
                notifyItemChanged(positionRoom);


                //NEU LA RENAME HOAC DOI AVATAR THI CAP NHAT LAI GROUP
                try {
                    if (ll.getType().equals(ContentType.ACTION)) {

                        String name = "";
                        String avatar = "";

                        JsonObject json = log.getContent();
                        String type = json.get("actionType").getAsString();
                        JsonObject jsonData = json.getAsJsonObject("data");

                        switch (type) {
                            case Action.REMOVE_MEMBER:
                                JsonObject memberJson = jsonData.getAsJsonObject("memberInfo");
                                long userId = memberJson.get("userId").getAsLong();


                                //UPDATE MAN HINH CHAT
                                Intent intent = new Intent(ChatActivity.ACTION_REMOVE_MEMBER_1);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(UserChat.USER_ID, userId);
                                context.sendBroadcast(intent);

                                //UPDATE MAN HINH chi tiet nhom
                                intent = new Intent(ChatGroupDetailActivity.ACTION_REMOVE_MEMBER_2);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(UserChat.USER_ID, userId);
                                context.sendBroadcast(intent);

                                break;

                            case Action.ADD_MEMBER:
                                memberJson = jsonData.getAsJsonObject("memberInfo");

                                Member member = new Member();
                                member.setUserId(memberJson.get("userId").getAsString());

                                UserChat user = new UserChat();
                                user.setUserId(memberJson.get("userId").getAsString());
                                user.setName(memberJson.get("name").getAsString());
                                user.setPhone(memberJson.get("phone").getAsString());
                                user.setEmail(memberJson.get("email").getAsString());
                                user.setAvatar(memberJson.get("avatar").getAsString());
                                member.setUserInfo(user);

                                //UPDATE MAN HINH CHAT
                                intent = new Intent(ChatActivity.ACTION_ADD_MEMBER_1);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(Member.MEMBER, member);
                                context.sendBroadcast(intent);

                                //UPDATE MAN HINH chi tiet nhom
                                intent = new Intent(ChatGroupDetailActivity.ACTION_ADD_MEMBER_2);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(Member.MEMBER, member);
                                context.sendBroadcast(intent);

                                break;

                            case Action.RENAME_ROOM:
                            case Action.RENAME_CHANNEL:
                                name = jsonData.get("roomName").getAsString();
                                data.get(positionRoom).setRoomName(name);

                                //UPDATE MAN HINH CHAT
                                intent = new Intent(ChatActivity.ACTION_UPDATE_ROOM_NAME_1);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(Room.ROOM_NAME, name);
                                context.sendBroadcast(intent);

                                //UPDATE MAN HINH chi tiet nhom
                                intent = new Intent(ChatGroupDetailActivity.ACTION_UPDATE_ROOM_NAME_2);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(Room.ROOM_NAME, name);
                                context.sendBroadcast(intent);

                                break;
                            case Action.CHANGE_AVATAR_ROOM:
                            case Action.CHANGE_CHANNEL_AVATAR:
                                avatar = jsonData.get("roomAvatar").getAsString();
                                data.get(positionRoom).setRoomAvatar(avatar);
                                notifyItemChanged(positionRoom);

                                //UPDATE MAN HINH CHAT
                                intent = new Intent(ChatActivity.ACTION_UPDATE_ROOM_AVATAR_1);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(Room.ROOM_AVATAR, avatar);
                                context.sendBroadcast(intent);

                                //UPDATE MAN HINH chi tiet nhom
                                intent = new Intent(ChatGroupDetailActivity.ACTION_UPDATE_ROOM_AVATAR_2);
                                intent.putExtra(Room.ROOM_ID, data.get(positionRoom).get_id());
                                intent.putExtra(Room.ROOM_AVATAR, avatar);
                                context.sendBroadcast(intent);

                                break;
                            case Action.CREATE_CHANNEL:

                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //////////////////////////////////////////////////////////////////////////////
                sort();

            } else {
                //Room mới, tìm vị trí pin và add vào bên dưới
                /*LastLog ll = new LastLog();
                ll.setType(log.getType());
                ll.setUserIdAuthor(log.getUserIdAuthor());
                ll.setChatLogId(log.get_id());
                ll.setContent(log.getContent());

                Room r = log.getRoomInfo();
                r.setViewed(false);
                r.setLastLog(ll);
                r.setLastLogDate(log.getCreateDate());
                r.setLastLogAuthor(log.getAuthorInfo());

                //tìm vị trí pin và add vào bên dưới
                int positionFind = findPositionInsertNewMessage();
                data.add(positionFind, r);
                notifyDataSetChanged();*/

                //refesh man hinh chat gan day de lay ve room moi tao
                Intent intent = new Intent(RecentChat_Fragment.REFRESH_RECENT_CHAT_ROOM);
                context.sendBroadcast(intent);
            }


        }
    }
    /*private void updateNotifyNumber(){
        int numberUnread = 0;
        if(data!=null && data.size()>0){
            for (int i = 0; i < data.size(); i++) {
                Room item = data.get(i);
                if(item.isViewed()==false){
                    MyUtils.log("room "+item.getRoomName());
                    numberUnread ++;
                }
            }
            MyUtils.setNumberChatUnread(context, numberUnread);
        }
    }*/

    public void updateRoomViewed(RoomLog log, boolean isViewed) {
        if (data != null && data.size() > 0 && log != null) {
            int position = findPosition(log.getRoomId());
            if (position > -1) {
                /*LastLog ll = new LastLog();
                ll.setType(log.getType());
                ll.setUserIdAuthor(log.getUserIdAuthor());
                ll.setChatLogId(log.get_id());
                ll.setContent(log.getContent());
                ll.setUserIdAuthor(log.getUserIdAuthor());

                //ROOM DA TON TAI THI UPDATE LAST_LOG
                data.get(position).setViewed(isViewed);
                data.get(position).setLastLog(ll);
                data.get(position).setLastLogDate(log.getCreateDate());
                data.get(position).setLastLogAuthor(log.getAuthorInfo());
                notifyItemChanged(position);*/
                data.get(position).setViewed(isViewed);
                notifyItemChanged(position);
            }
            //click vao view thi sort lai
//            sort();
        }
    }

    synchronized public int findPosition(String roomId) {
        int position = -1;
        if (!TextUtils.isEmpty(roomId)) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).get_id().equalsIgnoreCase(roomId)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * Tìm từ trên xuống nếu không gặp pin thì thoát
     * Mặc định danh sách pin sẽ nằm trên
     *
     * @return
     */
    synchronized public int findPositionInsertNewMessage() {
        int position = 0;
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).isPin()) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * Set item len vi tri 0
     *
     * @param roomId
     */
    public void setPinRoom(String roomId) {
        int position = findPosition(roomId);
        if (position >= 0) {
            data.get(position).setPin(true);
            //move to top
            //xoa ptu tai vi tri pos
            /*Room item = data.get(position);
            data.remove(position);
            notifyItemRemoved(position);
            //them phan tu vao vi tri 0
            data.add(0, item);
            notifyItemInserted(0);*/

            //sau khi pin sort
            sort();
        }
    }

    public void setUnPinRoom(String roomId) {
        if (!TextUtils.isEmpty(roomId)) {
            int position = findPosition(roomId);
            if (position >= 0) {
                data.get(position).setPin(false);
                //move to top
                /*Room item = data.get(position);
                if (item != null) {
                    //xoa khoi vi tri pin
                    data.remove(position);
                    notifyItemRemoved(position);

                    //di chuyen xuong vi tri cuoi cung cua pin
                    int pos = -1;
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).isPin() == false) {
                            pos = i;
                            break;
                        }
                    }
                    if (pos > -1 && pos < data.size()) {
                        data.add(pos, item);
                        notifyItemInserted(pos);
                    }

                }*/

                //sort lai theo thu tu
                sort();
            }
        }
    }

    public void removeRoom(String roomId) {
        if (!TextUtils.isEmpty(roomId)) {
            int position = findPosition(roomId);
            if (position >= 0) {
                data.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public void clear() {
        if (data != null && data.size() > 0) {
            data.clear();
            notifyDataSetChanged();
        }
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.linearRoot)
        LinearLayout linearRoot;
        @BindView(R2.id.imageView1)
        ImageView img1;
        /*@BindView(R2.id.imageView2)
        ImageView img2;*/
        @BindView(R2.id.imageView3)
        ImageView img3;
        @BindView(R2.id.textView1)
        TextView txt1;
        @BindView(R2.id.textView2)
        TextView txt2;
        @BindView(R2.id.textView3)
        TextView txt3;

        @BindView(R2.id.textView4)
        EmojiconTextView txt4;

        Typeface tp1, tp2, tp3;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //reset style
            tp1 = txt1.getTypeface();
            tp2 = txt2.getTypeface();
            tp3 = txt4.getTypeface();

        }

    }

    /*public void onNewData(ArrayList<Room> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new Room_DiffUtilCallback(newData, data));
        diffResult.dispatchUpdatesTo(this);
        this.data.clear();
        this.data.addAll(newData);
    }*/

    /*public void setData(ArrayList<Room> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallBack(newData, data));
        diffResult.dispatchUpdatesTo(this);
        data.clear();
        this.data.addAll(newData);
    }*/

}
