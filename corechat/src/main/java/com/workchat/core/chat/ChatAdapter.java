package com.workchat.core.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.workchat.core.autolink.AutoLinkMode;
import com.workchat.core.autolink.CustomTextViewLink;
import com.workchat.core.channel.MH06_PinMessageActivity;
import com.workchat.core.chat.link_preview_2.LinkUtils;
import com.workchat.core.chathead.events.MenuEvent;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.event.RoomLogEvent_Reply;
import com.workchat.core.models.chat.Action;
import com.workchat.core.models.chat.AlbumItem;
import com.workchat.core.models.chat.ChatView;
import com.workchat.core.models.chat.Contact;
import com.workchat.core.models.chat.ContentType;
import com.workchat.core.models.chat.ContentTypeLayout;
import com.workchat.core.models.chat.File;
import com.workchat.core.models.chat.Image;
import com.workchat.core.models.chat.Item;
import com.workchat.core.models.chat.Link;
import com.workchat.core.models.chat.LocaleHelper;
import com.workchat.core.models.chat.Location;
import com.workchat.core.models.chat.Meeting;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.OriginMessage;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.chat.Text;
import com.workchat.core.models.chat.Video;
import com.workchat.core.models.chat.Voice;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.plan.Comment;
import com.workchat.core.plan.CustomSeekBar;
import com.workchat.core.plan.MH03_Plan_Detail_Activity;
import com.workchat.core.plan.MH04_Comment_Activity;
import com.workchat.core.plan.PlanModel;
import com.workchat.core.plan.ProgressItem;
import com.workchat.core.plan.Vote;
import com.workchat.core.search.MH09_SearchActivity;
import com.workchat.core.utils.DateFormat;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.Utils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Sau khi set logs item thi set Room de lay danh sach member
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isFromWidget;
    private boolean isHaveError;

    /*public boolean isHaveError() {
        return isHaveError;
    }

    public void setHaveError(boolean haveError) {
        isHaveError = haveError;
        //lay lai context
//        context = ChatApplication.instance.getApplicationContext();
    }*/

    public void setFromWidget(boolean isFromWidget) {
        this.isFromWidget = isFromWidget;
    }

    private static Context context;
    private int imageSize = 160;
    private int avatarSize = 60;
    private int avatarSizeMedium = 60;
    private int radius = 10;

    private int emojiFontSizeDefault = 20;
    private int emojiFontSizeLarge = 20;
    private int imgPreviewSize = 100;


    private int padding2 = 2;
    private int padding7 = 7;
    private int padding10 = 10;
    private int padding15 = 15;

    private int mapWidth = 300;
    private int mapHeight = 200;

    private boolean isPinAdapter = false;

    private Realm realm;
    private String map_api_key = "";
    private TinyDB db;
    String languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET;

    public ChatAdapter(Context context, String id, boolean isPinAdapter, Realm realm) {
        this.context = context;
        db = new TinyDB(context);
        languageSelected = db.getString(LocaleHelper.SELECTED_LANGUAGE, LocaleHelper.LANGUAGE_TIENG_VIET);

        imageSize = context.getResources().getDimensionPixelSize(R.dimen.image_size);

        //lay 1/3 man hinh
        int screen = MyUtils.getScreenWidth(context);
        screen = screen / 3;
        if (screen < imageSize) {
            imageSize = screen;
        }

        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium_smaller);
        avatarSizeMedium = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        radius = context.getResources().getDimensionPixelSize(R.dimen.radius_image);

        emojiFontSizeDefault = context.getResources().getDimensionPixelSize(R.dimen.icon_emoji_size);
        emojiFontSizeLarge = context.getResources().getDimensionPixelSize(R.dimen.icon_emoji_size_large);
        padding2 = context.getResources().getDimensionPixelOffset(R.dimen.margin_2dp);
        padding7 = context.getResources().getDimensionPixelOffset(R.dimen.margin_7dp);
        padding10 = context.getResources().getDimensionPixelOffset(R.dimen.margin_10dp);
        padding15 = context.getResources().getDimensionPixelOffset(R.dimen.margin_15dp);

        this.ownerId = id;
        imgPreviewSize = context.getResources().getDimensionPixelSize(R.dimen.image_preview_size);
        this.isPinAdapter = isPinAdapter;
        this.realm = realm;

        mapWidth = context.getResources().getDimensionPixelSize(R.dimen.map_width);
        mapHeight = context.getResources().getDimensionPixelSize(R.dimen.map_height);
        map_api_key = ChatApplication.Companion.getGOOGLE_MAPS_ANDROID_API_KEY();
    }

    private Room room;
    public String ownerId;
    private boolean isPrivateRoom = false;
    private boolean isChannel = false;
    private List<Member> members = new ArrayList<>();

    public void setRoom(Room room) {
        if (room != null) {
            this.room = room;
            List<Member> list = room.getMembers();
            //Doi ten tat ca thanh vien theo local name
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {

                    Member item = list.get(i);
                    UserChat chat = item.getUserInfo();
                    if (chat != null) {

                        //NAME la NAME_SERVER, NameLocal

                        //LAY TEN LOCAL/////////////////////////////////////////////////////////////////
                        /*if (realm != null) {
                            realm.beginTransaction();
                            UserInfo user = realm.where(UserInfo.class).equalTo("nameMBN", chat.getName()).findFirst();
                            realm.commitTransaction();
                            if (user != null) {
                                //name local in chat
                                if (!TextUtils.isEmpty(user.getName()) && !user.getName().equalsIgnoreCase(chat.getName())) {
                                    list.get(i).getUserInfo().setNameLocal(user.getName());
                                    room.getMembers().get(i).getUserInfo().setNameLocal(user.getName());
                                }
                            }
                        }*/
                        //LAY TEN LOCAL/////////////////////////////////////////////////////////////////
                    }
                }
            }


            if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_PRIVATE)) {
                isPrivateRoom = true;//chat 1:1
            } else {
                isPrivateRoom = false;
            }

            if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL)) {
                isChannel = true;
            } else {
                isChannel = false;
            }

            //update giao dien
            members.clear();
            members.addAll(list);
            notifyDataSetChanged();
        }


    }

    private Member getMember(String userId) {
        Member member = null;
        if (members != null && members.size() > 0) {
            for (int i = 0; i < members.size(); i++) {
                Member m = members.get(i);
                if (m.getUserId().equals(userId)) {
                    member = m;
                    break;
                }
            }
        }
        return member;
    }

    private Member getMemberByUsername(String username) {
        Member member = null;
        if (members != null && members.size() > 0) {
            for (int i = 0; i < members.size(); i++) {
                Member m = members.get(i);
                if (m.getUserInfo()!=null && m.getUserInfo().getName().equals(username)) {
                    member = m;
                    break;
                }
            }
        }
        return member;
    }

    private ArrayList<RoomLog> logs = new ArrayList<>();


    //////////////////////////////////////////////////////////////////////////////////////////////
    private HashMap<String, List<ChatView>> mapViewed = new HashMap<>();

    private boolean isExistInMapViewed(ChatView v) {

        boolean isExist = false;
        if (mapViewed != null) {
            for (String key : mapViewed.keySet()) {
                List<ChatView> views = mapViewed.get(key);
                if (views != null) {

                    for (int i = 0; i < views.size(); i++) {
                        if (v.getUserId().equals(views.get(i).getUserId())) {
                            isExist = true;
                            break;
                        }
                    }

                    if (isExist) {
                        break;
                    }

                }
            }
        }


        return isExist;
    }

    private void initMapViewed() {
        mapViewed.clear();
        if (logs != null && logs.size() > 0) {
            //quet tu duoi len tren
            int numberMember = 0;
            for (int i = logs.size() - 1; i >= 0; i--) {

                RoomLog item = logs.get(i);
                List<ChatView> views = item.getViews();
                if (views != null && views.size() > 0) {

                    //ai chua co thi add vao logs row of mapView
                    List<ChatView> list = new ArrayList<>();
                    for (int j = 0; j < views.size(); j++) {
                        ChatView v = views.get(j);
                        //co can hien thi avatar ko va ko phai owner
                        if (v.isShowAvatar() && !v.getUserId().equals(ownerId)) {
                            boolean isExist = isExistInMapViewed(v);
                            if (isExist == false) {
                                list.add(v);
                                numberMember += 1;
                            }
                        }
                    }

                    //add 1 row in mapView
                    if (list.size() > 0) {
                        mapViewed.put(item.get_id(), list);
                    }

                    //dung neu du so luong
                    if (members != null && numberMember == members.size()) {
                        break;
                    }

                }


            }

        }
    }


    private void animationShow(final View v) {
        if (v != null) {
            //start voi animation
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", .3f, 1f);
            fadeIn.setDuration(300);
            final AnimatorSet mAnimationSet = new AnimatorSet();
            mAnimationSet.play(fadeIn);
            mAnimationSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (v != null) {
                        v.setVisibility(View.VISIBLE);
                    }
                }
            });
            mAnimationSet.start();
        }
    }

    private void animationHide(final View v) {
        if (v != null) {
            //start voi animation
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 1f, .3f);
            fadeIn.setDuration(300);
            final AnimatorSet mAnimationSet = new AnimatorSet();
            mAnimationSet.play(fadeIn);
            mAnimationSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (v != null) {
                        v.setVisibility(View.GONE);
                    }
                }
            });
            mAnimationSet.start();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////


    private boolean isFinished(PlanModel plan) {
        boolean finished = false;
        if (plan != null) {
            long time = plan.getTimeStamp();
            String result = MyUtils.getTimeDifferent(time, context);
            String finishedString = context.getString(R.string.finished);
            if (result.equals(finishedString)) {
                finished = true;
            }
        }
        return finished;
    }


    private void setVisibleAvatar(int position, ImageView img) {
        //mot user chat nhieu dong thi chi hien thi avatar o dong dau tien
        int pre = position + 1;
        if (pre >= 0 && pre < logs.size() && position < logs.size()) {
            RoomLog item2 = logs.get(pre);
            RoomLog m = logs.get(position);

            //khac nhau thi hien, hoac tin1 la action thi tin 2 phai hien ra
            boolean condition1 = false;
            if (item2 != null && !TextUtils.isEmpty(item2.getUserIdAuthor()) && m != null && !TextUtils.isEmpty(m.getUserIdAuthor())) {
                condition1 = !m.getUserIdAuthor().equals(item2.getUserIdAuthor());
            }
            //tin1 la action va tin 2 ko phai la action thi moi hien ra avatar
            boolean condition2 = (getItemViewType(pre) == ContentTypeLayout.ACTION) && (getItemViewType(position) != ContentTypeLayout.ACTION);
            if (condition1 || condition2) {//2nguoi
                img.setVisibility(View.VISIBLE);
            } else {
                img.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return logs == null ? 0 : logs.size();
    }

    private boolean isBeginLoading = false;

    public void setBeginLoading(boolean isBeginLoading) {
        this.isBeginLoading = isBeginLoading;
    }

    /**
     * Do danh sach da dao nguoc, vi tri 0 la vi tri cuoi cung
     *
     * @return
     */
    public String getLastLogId_Top() {
        String id = "";
        if (isBeginLoading) {
            id = "";
            isBeginLoading = false;
        } else {
            if (logs != null && logs.size() > 0) {

                int pos = logs.size() - 1;
                //kiem tra lay phan tu cuoi la item group date
                if (!logs.get(pos).isGroupDate()) {
                    id = logs.get(pos).get_id();
                } else {
                    if (logs.size() > 1) {
                        id = logs.get(pos - 1).get_id();
                    }
                }
            }
        }
        return id;
    }


    public String getLastLogId_Bottom() {
        String id = "";
        if (logs != null && logs.size() > 0) {
            RoomLog log = logs.get(0);
            id = log.get_id();
        }
        return id;
    }

    public long getLastPinDate_Bottom() {
        long last = 0;
        if (logs != null && logs.size() > 0) {
            RoomLog log = logs.get(0);
            last = log.getPinDate();
        }
        return last;
    }


    public RoomLog getLastLog() {
        RoomLog log = null;
        if (logs != null && logs.size() > 0) {
            log = logs.get(0);
        }
        return log;
    }


    public void clearData() {
        if (logs != null && logs.size() > 0) {
            logs.clear();
            notifyDataSetChanged();
        }
    }


    public void append(RoomLog event) {

        boolean isHolder = event.isUploading();
        if (isHolder) {//add vao giu cho
            logs.add(0, event);
            notifyItemInserted(0);
        } else {//item that su, neu co thi thay the

            if (event.isDeleted()) {//da xoa
                int pos = findPosById(event.get_id());
                if (pos != -1) {
                    logs.remove(pos);
                    notifyItemRemoved(pos);
                }
            } else if (event.isUpdated()) {

                int pos = findPosById(event.get_id());
                if (pos != -1) {
                    logs.set(pos, event);
                    notifyItemChanged(pos);
                }

            } else {//them moi

                int pos = findPosByUUID(event.getItemGUID());
                if (pos != -1) {
                    logs.set(pos, event);
                    notifyItemChanged(pos);
                } else {//ko co thi them moi
                    logs.add(0, event);
                    notifyItemInserted(0);
                    initMapViewed();
                }
            }
        }
    }

    public int findPosByUUID(String uuid) {
        int pos = -1;

        if (!TextUtils.isEmpty(uuid)) {
            if (logs != null && logs.size() > 0) {
                for (int i = 0; i < logs.size(); i++) {
                    RoomLog log = logs.get(i);
                    if (!TextUtils.isEmpty(log.getItemGUID())) {
                        if (log.getItemGUID().equalsIgnoreCase(uuid)) {
                            pos = i;
                            break;
                        }
                    }

                }
            }
        }

        return pos;
    }

    public int findPosById(String id) {
        int pos = -1;

        if (!TextUtils.isEmpty(id)) {
            if (logs != null && logs.size() > 0) {
                for (int i = 0; i < logs.size(); i++) {
                    RoomLog log = logs.get(i);
                    if (!TextUtils.isEmpty(log.get_id())) {
                        if (log.get_id().equalsIgnoreCase(id)) {
                            pos = i;
                            break;
                        }
                    }

                }
            }
        }

        return pos;
    }

    public void append(ArrayList<RoomLog> data) {

        try {
            if (data != null && data.size() > 0) {

                if (isPinAdapter) {
                    //ko can group ngay
                } else {
                    //chi tao ngay cho data vua lay ve, ko can tao ngay cho toan bo logs se ton nhieu thoi gian
                    int positionDateBefore = findFirstGroupDate(false);
                    data = createGroupDateForRoomLogLoadMoreTop(data, positionDateBefore);

                }

                int size = logs.size();
                logs.addAll(data);
                notifyItemRangeInserted(size, data.size());

                //tao logs de hien thi viewed
                initMapViewed();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void append(ArrayList<RoomLog> data, int position) {
        try {
            if (data != null && data.size() > 0 && position >= 0 && position <= logs.size()) {


                if (isPinAdapter) {
                    //ko can group ngay
                } else {
                    int positionDateBefore = -1;
                    if (position == 0) {//load next log on bottom so voi ben tren
                        positionDateBefore = findFirstGroupDate(true);
                        data = createGroupDateForRoomLogLoadNextBottom(data, positionDateBefore);
                    } else {//load more on top so voi ben duoi
                        positionDateBefore = findFirstGroupDate(false);
                        data = createGroupDateForRoomLogLoadMoreTop(data, positionDateBefore);
                    }

                }

                //danh sach da xoa 1 dong date lam giam so luong xuong 1 hoac N dong Date group
                if (position > 0 && logs.size() < position) {
                    position = logs.size();
                }
                logs.addAll(position, data);
                notifyItemRangeInserted(position, data.size());
                //tao logs de hien thi viewed
                initMapViewed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int position) {
        logs.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Xoa cac group date khoi logs, truoc khi tao lai group date
     *
     * @param chatLogs
     * @return
     */
    private ArrayList<RoomLog> deleteGroupDateInList(List<RoomLog> chatLogs) {
        ArrayList<RoomLog> messages = new ArrayList<RoomLog>();
        for (int i = 0; i < chatLogs.size(); i++) {
            RoomLog log = chatLogs.get(i);
            if (!log.isGroupDate()) {
                messages.add(log);
            }
        }

        return messages;
    }

    private int numberGroupDate = 0;

    public int getNumberGroupDate() {
        return numberGroupDate;
    }

    /**
     * Bo sung cac dau cach ngay tren dau
     *
     * @param chatLogs
     * @return
     */
    @Deprecated //thay bang createGroupDateForRoomLogLoadMoreTop ben duoi
    private ArrayList<RoomLog> createGroupDateForRoomLog_Old(List<RoomLog> chatLogs) {
        ArrayList<RoomLog> messages = new ArrayList<RoomLog>();

        if (chatLogs != null && chatLogs.size() > 0) {
            //loai bo cac group date truoc khi tao lai
            chatLogs = deleteGroupDateInList(chatLogs);

            numberGroupDate = 0;
            //add vao man hinh
            int size = chatLogs.size();
            for (int i = size - 1; i >= 0; i--) {
                RoomLog log = chatLogs.get(i);
                String date1 = MyUtils.getDateChatFull(log.getCreateDate());

                int x = i - 1;
                if (x >= 0 && x < size) {
                    RoomLog log2 = chatLogs.get(x);
                    String date2 = MyUtils.getDateChatFull(log2.getCreateDate());

                    //tin thi van add
                    messages.add(0, log);

                    if (date1.equals(date2) == false) {//khong bang thi them moi mot slipt date
                        RoomLog m = new RoomLog();
                        m.set_id("");
                        m.setGroupDate(true);
                        m.setCreateDate(log.getCreateDate());

                        messages.add(0, m);
                        numberGroupDate++;
                    }

                } else {// phan tu cuoi cung

                    //them message
                    messages.add(0, log);

                    //roi them ngay tren dau
                    RoomLog m = new RoomLog();
                    m.setGroupDate(true);
                    m.setCreateDate(log.getCreateDate());
                    messages.add(0, m);
                    numberGroupDate++;
                }
            }
        }

        return messages;

    }

    /**
     * Bo sung cac dau cach ngay tren dau
     *
     * @param list
     * @return
     */
    private ArrayList<RoomLog> createGroupDateForRoomLogLoadMoreTop(ArrayList<RoomLog> list, int positionDateBefore) {
        if (list != null && list.size() > 0) {

            //step 1 neu group date da ton tai thi xoa
            boolean isRemoved = false;
            String dateBefore = null;
            if (positionDateBefore != -1) {
                RoomLog log = logs.get(positionDateBefore);
                dateBefore = MyUtils.getDateChatFull(log.getCreateDate());
            }

            //step 2 tao log date
            String tempDate = null;
            int positionTempDate = -1;

            numberGroupDate = 0;
            //add vao man hinh
            //logs[1,2,3,4,5,6]=>[1,d,2,3,d,4,5,d,6,d]
            for (int i = 0; i <= list.size() - 1; i++) {
                RoomLog log = list.get(i);
                if (!log.isGroupDate()) {
                    String date1 = MyUtils.getDateChatFull(log.getCreateDate());

                    int x = i + 1;
                    if (x >= 0 && x < list.size()) {
                        RoomLog log2 = list.get(x);
                        if (!log2.isGroupDate()) {
                            String date2 = MyUtils.getDateChatFull(log2.getCreateDate());

                            if (!date1.equals(date2)) {//khong bang thi them moi mot slipt date
                                RoomLog m = createLogDate(log.getCreateDate());
                                list.add(x, m);
                                i = i + 1;
                                numberGroupDate++;

                                tempDate = date1;
                                positionTempDate = x;


                                //neu co roi thi xoa
                                if (date1.equals(dateBefore)) {
                                    if (positionDateBefore < logs.size()) {
                                        logs.remove(positionDateBefore);
                                        notifyItemRemoved(positionDateBefore);
                                        isRemoved = true;
                                    }
                                }


                            }
                        }
                    } else {// phan tu cuoi cung

                        //roi them ngay tren dau
                        RoomLog m = createLogDate(log.getCreateDate());
                        list.add(m);
                        numberGroupDate++;

                        //xoa trong list local neu da co ngay roi
                        if (date1.equals(tempDate)) {
                            list.remove(positionTempDate);
                        }


                        //neu chua xoa thi moi xoa
                        if (!isRemoved) {
                            if (date1.equals(dateBefore)) {
                                if (positionDateBefore < logs.size()) {
                                    logs.remove(positionDateBefore);
                                    notifyItemRemoved(positionDateBefore);
                                }
                            }
                        }

                    }
                }
            }


        }

        return list;

    }

    /**
     * Bo sung cac dau cach ngay tren dau
     *
     * @param list
     * @return
     */
    private ArrayList<RoomLog> createGroupDateForRoomLogLoadNextBottom(ArrayList<RoomLog> list, int positionDateBefore) {
        if (list != null && list.size() > 0) {

            //step 1 neu group date da ton tai thi xoa
            boolean isRemoved = false;
            String dateBefore = null;
            if (positionDateBefore != -1) {
                RoomLog log = logs.get(positionDateBefore);
                dateBefore = MyUtils.getDateChatFull(log.getCreateDate());

            }


            //step 2 tao log date
            String tempDate = null;
            int positionTempDate = -1;

            numberGroupDate = 0;
            //add vao man hinh
            //logs[6,5,4,3,2,1]=>[d,6,d,5,4,d,3,2,d,1]
            for (int i = list.size() - 1; i > 0; i--) {
                RoomLog log = list.get(i);
                if (!log.isGroupDate()) {
                    String date1 = MyUtils.getDateChatFull(log.getCreateDate());


                    //phan tu on top, neu chua co ngay thi them ngay
                    if (i == list.size() - 1) {
                        if (!date1.equals(dateBefore)) {
                            RoomLog m = createLogDate(log.getCreateDate());
                            list.add(list.size(), m);

                            tempDate = date1;
                            positionTempDate = list.size();

                        }
                    }


                    //so sanh 2 tin ke tiep nhau
                    //5,4,3,2,1
                    int x = i - 1;
                    if (x >= 0) {
                        RoomLog log2 = list.get(x);
                        if (!log2.isGroupDate()) {
                            String date2 = MyUtils.getDateChatFull(log2.getCreateDate());
//                            MyUtils.log("datebefore "+dateBefore+" - date1 "+ date1 + " - date2 "+date2 + " - datetemp "+tempDate);

                            if (!date1.equals(date2)) {//khong bang thi them moi mot slipt date

                                //neu temp chua co thi van so sanh voi truoc do
                                if (tempDate != null) {
                                    //kiem tra tren dau da them chua
                                    if (!date2.equals(tempDate)) {
                                        RoomLog m = createLogDate(log2.getCreateDate());
                                        list.add(i, m);
                                        numberGroupDate++;
                                    }
                                } else {
                                    //co tempdate thi so sanh voi tempdate
                                    if (!date2.equals(dateBefore)) {
                                        RoomLog m = createLogDate(log2.getCreateDate());
                                        list.add(i, m);
                                        numberGroupDate++;
                                    }
                                }


                            }
                        }
                    }


                }
            }


        }

        return list;

    }

    private RoomLog createLogDate(long createDate) {
        RoomLog m = new RoomLog();
        m.set_id("");
        m.setGroupDate(true);
        m.setCreateDate(createDate);
        return m;
    }

    /**
     * @param isFromTop true: 0->N, false: N->0
     * @return
     */
    public int findFirstGroupDate(boolean isFromTop) {
        int pos = -1;

        if (logs != null && logs.size() > 0) {
            if (isFromTop) {
                for (int i = 0; i < logs.size(); i++) {
                    RoomLog log = logs.get(i);
                    if (log.isGroupDate()) {
                        pos = i;
                        break;
                    }
                }
            } else {
                for (int i = logs.size() - 1; i >= 0; i--) {
                    RoomLog log = logs.get(i);
                    if (log.isGroupDate()) {
                        pos = i;
                        break;
                    }
                }
            }
        }

        return pos;
    }

    //TIMEOUT 30S FUNCTION///////////////////////////////////////////////////////////////////////////////////////////////
    /*private ArrayList<String> timeoutIds = new ArrayList<>();

    private void initTimeoutList() {
        timeoutIds.clear();
    }

    private void deleteTimeout(final String chatLogId) {
        if (timeoutIds.contains(chatLogId)) {
            timeoutIds.remove(chatLogId);
            notifyDataSetChanged();
        }
    }

    private Handler handler = new Handler();

    private void addTimeout(final String chatLogId) {
        if (!timeoutIds.contains(chatLogId)) {//ko co thi moi add
            timeoutIds.add(chatLogId);
            //start timeout
            final Timer timer = new Timer("timer" + chatLogId);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("timer", "finish_" + chatLogId);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            MyUtils.showToast(context,"Finish timeout");
                            deleteTimeout(chatLogId);
                            timer.cancel();
                        }
                    });
                }
            }, 30000, 10000);//30s la cancel, ko lap lai
        }
    }*/


    public void updateRoomViewed(RoomLog log) {

        //cung room
        if (room != null) {
            if (logs != null && logs.size() > 0 && log.getRoomId().equals(room.get_id())) {

                int pos = -1;
                for (int i = logs.size() - 1; i >= 0; i--) {
                    RoomLog room = logs.get(i);
                    if (room.get_id() != null && room.get_id().equals(log.get_id())) {//tim duoc vi tri
                        pos = i;
                        break;
                    }
                }

                if (pos > -1) {
                    logs.get(pos).setViews(log.getViews());
                    initMapViewed();
                    notifyDataSetChanged();
                }
            }
        }
    }

    private void deleteMessage(String roomId, String chatLogId) {
        Socket mSocket = ChatApplication.Companion.getSocket();
        // Sending an object
        if (mSocket != null && mSocket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", roomId);
                obj.put("chatLogId", chatLogId);

                mSocket.emit("deleteMessage", obj, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.d("test", args[0].toString());
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MyUtils.showToast(context, R.string.socket_not_connected);
        }
    }

    public void setPinMessage(String logId, boolean isPin) {
        if (!TextUtils.isEmpty(logId)) {
            if (logs != null && logs.size() > 0) {
                for (int i = 0; i < logs.size(); i++) {
                    if (logs.get(i).get_id().equalsIgnoreCase(logId)) {
                        logs.get(i).setPin(isPin);
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        }
    }

    /**
     * Chi dung cho MH06_PinMessageActivity
     *
     * @param logId
     */
    public void setUnpinMessageForPinAdapter(String logId) {
        if (isPinAdapter) {
            if (!TextUtils.isEmpty(logId)) {
                if (logs != null && logs.size() > 0) {
                    for (int i = 0; i < logs.size(); i++) {
                        if (logs.get(i).get_id().equalsIgnoreCase(logId)) {
                            logs.remove(i);
                            break;
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }

    public int getPostionOfLog(String logId) {
        int position = -1;
        if (!TextUtils.isEmpty(logId)) {
            if (logs != null && logs.size() > 0) {
                for (int i = 0; i < logs.size(); i++) {
                    if (logs.get(i).get_id().equalsIgnoreCase(logId)) {
                        position = i;
                        break;
                    }
                }
            }
        }
        return position;
    }



    /*public void planAddComment(Comment comment) {
        try {
            if (comment != null) {
                int position = getPostionOfLog(comment.getChatLogId());
                if (position >= 0 && position < getItemCount()) {
                    RoomLog item = logs.get(position);
                    PlanModel plan = new Gson().fromJson(item.getContent().toString(), PlanModel.class);
                    if(plan!=null){

                        //them vao logs
                        plan.getComments().add(comment);

                        //chuyen comments ve json,
//                        Type type = new TypeToken<ArrayList<Comment>>(){}.getType();
                        String jsonString = new Gson().toJSON(plan, PlanModel.class);

                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(jsonString).getAsJsonObject();


                        //set lai cho content cua message;
                        item.setContent(json);

                        //refresh
                        notifyItemChanged(position);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    //TIMEOUT 30S FUNCTION///////////////////////////////////////////////////////////////////////////////////////////////


    private void showActionMenu(View view, final RoomLog m, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        if (isFromWidget) {
            if (position >= getItemCount() - 4) {
                popup = new PopupMenu(context, view, Gravity.TOP);
            }
        }
        //app:showAsAction="ifRoom|withText" NOT WORKING
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_action_chat_row_pin_message, menu);


        //neu la pin adapter thi khong co copy va forward tin
        if (isPinAdapter) {
            menu.findItem(R.id.action_1).setVisible(false);//pin
            menu.findItem(R.id.action_2).setVisible(true);//unpin
            menu.findItem(R.id.action_3).setVisible(true);//copy
        } else {
            if (m.isPin()) {
                menu.findItem(R.id.action_1).setVisible(false);//pin
                menu.findItem(R.id.action_2).setVisible(true);//unpin
                menu.findItem(R.id.action_3).setVisible(true);//copy
            } else {
                menu.findItem(R.id.action_1).setVisible(true);//pin
                menu.findItem(R.id.action_2).setVisible(false);//unpin
                menu.findItem(R.id.action_3).setVisible(true);//copy
            }
        }

        //neu la owner thi moi duoc xoa, nguoc lai thi an menu xoa
        if (m.getUserIdAuthor().equals(ownerId)) {
            //NEU KHONG PHAI LA ACTION, MEETING THI MOI CHO XOA
            if (
                    !m.getType().equals(ContentType.ACTION) &&
                            !m.getType().equals(ContentType.MEETING)

            ) {
                menu.findItem(R.id.action_6).setVisible(true);
            } else {
                menu.findItem(R.id.action_6).setVisible(false);
            }
        } else {
            menu.findItem(R.id.action_6).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_1 || itemId == R.id.action_2) {
                    if (isPinAdapter) {//MH06_PinMessageActivity
                        Intent intent = new Intent(MH06_PinMessageActivity.ACTION_PIN_MESSAGE_CHAT);
                        intent.putExtra(RoomLog.ROOM_LOG_ID, m.get_id());
                        intent.putExtra(RoomLog.IS_PIN, !m.isPin());
                        context.sendBroadcast(intent);
                    } else {//chat adapter //MH ChatActivity
                        //pin message
                        Intent intent = new Intent(ChatActivity.ACTION_PIN_MESSAGE_CHAT);
                        intent.putExtra(RoomLog.ROOM_LOG_ID, m.get_id());
                        intent.putExtra(RoomLog.IS_PIN, !m.isPin());
                        context.sendBroadcast(intent);
                    }
                } else if (itemId == R.id.action_3) {
                    String text = getContent(m);
                    if (!TextUtils.isEmpty(text)) {
                        MyUtils.copy(context, text);

                    }
                } else if (itemId == R.id.action_4) {//reply
                    EventBus.getDefault().post(new RoomLogEvent_Reply(m));
                } else if (itemId == R.id.action_5) {//forward
                    //luu item tren cache
                    ChatApplication.Companion.setLogForward(m);

                    //chon room
                    Intent intent = new Intent(context, MH09_SearchActivity.class);
                    intent.putExtra(RoomLog.IS_FORWARD, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (itemId == R.id.action_6) {//xoa mot item
                        /*new MaterialDialog.Builder(context)
                                .content(R.string.delete_this_message)
                                .positiveText(R.string.delete)
                                .negativeText(R.string.no)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        deleteMessage(room.get_id(), m.get_id());
                                        dialog.dismiss();
                                    }
                                })
                                .show();*/

                    if (isFromWidget) {
                        if (room != null) {
                            deleteMessage(room.get_id(), m.get_id());
                        }
                    } else {
                        if (context != null) {
                            try {
                                new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                                        .setMessage(R.string.delete_this_message)
                                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (room != null) {
                                                    deleteMessage(room.get_id(), m.get_id());
                                                }
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return true;
            }
        });
        popup.show();
    }

    public String getContent(RoomLog m) {
        String text = "";
        try {
            if (m != null) {
                switch (m.getType()) {
                    case ContentType.TEXT:
                        Text t = new Gson().fromJson(m.getContent().toString(), Text.class);
                        text = Utils.copyMentionUserName(t.getContent());
                        break;
                    case ContentType.IMAGE:
                        Image i = new Gson().fromJson(m.getContent().toString(), Image.class);
                        text = i.getLink();
                        break;
                    case ContentType.FILE:
                        File f = new Gson().fromJson(m.getContent().toString(), File.class);
                        text = f.getLink();
                        break;
                    case ContentType.VIDEO:
                        Video v = new Gson().fromJson(m.getContent().toString(), Video.class);
                        text = v.getLink();
                        break;
                    case ContentType.LINK:
                        Link link = new Gson().fromJson(m.getContent().toString(), Link.class);
                        text = link.getLink();
                        break;
                    case ContentType.LOCATION:
                        Location location = new Gson().fromJson(m.getContent().toString(), Location.class);
                        text = location.getAddress();
                        //neu ko co thi lay o cache
                        if (TextUtils.isEmpty(text)) {
                            text = m.getAddressSave();
                        }
                        //neu van khong co address thi lay lat,long
                        if (TextUtils.isEmpty(text)) {
                            text = location.getLat() + "," + location.getLng();
                        }
                        break;
                    case ContentType.ITEM:
                        Item item = new Gson().fromJson(m.getContent().toString(), Item.class);
                        text = item.getItemLink();
                        break;
                    case ContentType.ALBUM:
                        /*JsonObject json = m.getContent();
                        JsonArray items = json.getAsJsonArray("items");
                        java.lang.reflect.Type type = new TypeToken<List<AlbumItem>>() {}.getType();
                        final ArrayList<AlbumItem> album = new Gson().fromJson(items.toString(), type);
                        if (album!=null && album.size()>0){
                            for (int j = 0; j < album.size(); j++) {
                                String url = album.get(j).getThumbLink();
                                if(j==0){
                                    text = url;
                                }else{
                                    text = text +", "+url;
                                }

                            }
                        }*/
                        text = context.getString(R.string.album);
                        break;
                    case ContentType.VOICE:
                        Voice voice = new Gson().fromJson(m.getContent().toString(), Voice.class);
                        text = voice.getLink();
                        break;
                    case ContentType.CONTACT:
                        Contact contact = new Gson().fromJson(m.getContent().toString(), Contact.class);
                        text = contact.getName() + " : " + contact.getMobile();
                        break;
                    case ContentType.PLAN:
                        PlanModel plan = new Gson().fromJson(m.getContent().toString(), PlanModel.class);
                        text = plan.getTitle();
                        break;
                    case ContentType.MEETING:
                        Meeting meeting = new Gson().fromJson(m.getContent().toString(), Meeting.class);
                        text = meeting.getAgenda();
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public String getContentReplyForward(String type, JsonObject obj) {
        String text = "";
        try {
            if (obj != null) {
                String json = obj.toString();
                if (!TextUtils.isEmpty(json)) {
                    switch (type) {
                        case ContentType.TEXT:
                            Text t = new Gson().fromJson(json, Text.class);
                            text = Utils.copyMentionUserName(t.getContent());
                            break;
                        case ContentType.IMAGE:
//                            Image i = new Gson().fromJson(json, Image.class);
//                            text = i.getLink();
                            text = context.getString(R.string.image);
                            break;
                        case ContentType.FILE:
//                            File f = new Gson().fromJson(json, File.class);
//                            text = f.getLink();
                            text = context.getString(R.string.file);
                            break;
                        case ContentType.VIDEO:
//                            Video v = new Gson().fromJson(json, Video.class);
//                            text = v.getLink();
                            text = context.getString(R.string.video);
                            break;
                        case ContentType.LINK:
                            Link link = new Gson().fromJson(json, Link.class);
                            text = link.getLink();
                            break;
                        case ContentType.LOCATION:
                            Location location = new Gson().fromJson(json, Location.class);
                            text = location.getAddress();
                            //neu ko co thi lay o cache
//                            if (TextUtils.isEmpty(text)) {
//                                text = m.getAddressSave();
//                            }
                            //neu van khong co address thi lay lat,long
                            if (TextUtils.isEmpty(text)) {
                                text = location.getLat() + "," + location.getLng();
                            }
                            break;
                        case ContentType.ITEM:
                            Item item = new Gson().fromJson(json, Item.class);
                            text = item.getItemLink();
                            break;
                        case ContentType.ALBUM:
                        /*JsonObject json = m.getContent();
                        JsonArray items = json.getAsJsonArray("items");
                        java.lang.reflect.Type type = new TypeToken<List<AlbumItem>>() {}.getType();
                        final ArrayList<AlbumItem> album = new Gson().fromJson(items.toString(), type);
                        if (album!=null && album.size()>0){
                            for (int j = 0; j < album.size(); j++) {
                                String url = album.get(j).getThumbLink();
                                if(j==0){
                                    text = url;
                                }else{
                                    text = text +", "+url;
                                }

                            }
                        }*/
                            text = context.getString(R.string.album);
                            break;
                        case ContentType.VOICE:
//                            Voice voice = new Gson().fromJson(json, Voice.class);
//                            text = voice.getLink();
                            text = context.getString(R.string.voice);
                            break;
                        case ContentType.CONTACT:
                            Contact contact = new Gson().fromJson(json, Contact.class);
                            text = contact.getName() + " : " + contact.getMobile();
                            break;
                        case ContentType.PLAN:
                            PlanModel plan = new Gson().fromJson(json, PlanModel.class);
                            text = context.getString(R.string.schedule_a_meeting) + " - " + plan.getTitle();
                            break;
                        case ContentType.NEW_PAYMENT:
                            Logger.d("getContentReplyForward");
                            break;
                        case ContentType.MEETING:
                            Meeting meeting = new Gson().fromJson(json, Meeting.class);
                            text = "Meeting " + meeting.getAgenda();
                            break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }


    //media player/////////////////////////////////////////////////////////////////////////////////
    private String linkPlaying = "";
    private MediaPlayer mediaPlayer;
    private Timer timer;
    private int positionPlaying = -1;

    private void play(String url) {
        //neu co file dang phat thi release
        releaseMediaPlayer();

        //tao moi media player
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            linkPlaying = url;

            //khi hat xong thi reset lai giao dien
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                    notifyDataSetChanged();
                }
            });

            //timer duration
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        if (txtDuration != null) {
                            txtDuration.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                        int currentDuration = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition();
                                        updatePlayer(currentDuration);
                                        if (txtDuration != null) {
                                            txtDuration.postDelayed(this, 1000);
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        timer.cancel();
                        timer.purge();
                    }
                }
            }, 0, 1000);

        } catch (Exception e) {
            // make something
        }
    }

    public void releaseMediaPlayer() {

        linkPlaying = "";
        txtDuration = null;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private TextView txtDuration;

    private void updatePlayer(int currentDuration) {
        if (txtDuration != null) {
            txtDuration.setText("" + milliSecondsToTimer((long) currentDuration));
        }
    }

    /**
     * Function to convert milliseconds time to Timer Format
     * Hours:Minutes:Seconds
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void setOnClickMention(String content, CustomTextViewLink txt2) {
        if (txt2 != null && !TextUtils.isEmpty(content)) {
            content = Utils.replaceAtMentionsWithLinksNew(content);


            //Muốn nhận dạng phone/email thi tạo tương tự như Mention
//            AutoLinkMode mention = AutoLinkMode.MENTION;
            txt2.addAutoLinkMode(new AutoLinkMode[]{
                    AutoLinkMode.EMAIL,
                    AutoLinkMode.PHONE,
                    AutoLinkMode.URL,
                    AutoLinkMode.MENTION,
                    AutoLinkMode.TASK,
                    AutoLinkMode.PROJECT
            });
            txt2.setAutoLinkText(content.replace("\n", "<br/>"));
            txt2.disableUnderLine();
            txt2.setAutoLinkOnClickListener(new CustomTextViewLink.AutoLinkOnClickListener() {
                @Override
                public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
                    //matchedText = <a class='mention-user' href='/' onclick='return false;' data-userid='2'>@Bạn</a>
                    //tim userid
//                    String userId = Utils.getMentionUserIdFromTagA(matchedText);
                    String username = matchedText.substring(matchedText.indexOf("@") + 1);
//                    UserInfo user = getUser(fullName);
                    if (!TextUtils.isEmpty(username)) {
                        collapseBubble();
                        if (username.equals(ChatApplication.Companion.getBanOrYou())) {
                            //vao tin da luu
                            MyUtils.chatWithUser(context, ChatApplication.Companion.getUser(), false);
                        } else {
                            Member member = getMemberByUsername(username);
                            if (member != null) {
                                if(!room.getRoomName().equals(member.getUserInfo().getName())) {
                                    MyUtils.chatWithUser(context, member);
                                }
                            }

                        }

                    } else {
                        collapseBubble();
                        //check task hoac project
                        String url = matchedText;
                        if (MyUtils.checkLinkToTask(url)) {//link task
                            long taskId = MyUtils.getIdFromLinkTaskOrProject(url);
                            Uri uri = Deeplink.getDeeplinkTask(taskId);
                            Deeplink.openMyxteam(context, uri);
                        } else if (MyUtils.checkLinkToProject(url)) {//link project
                            long projectId = MyUtils.getIdFromLinkTaskOrProject(url);
                            Uri uri = Deeplink.getDeeplinkProject(projectId);
                            Deeplink.openMyxteam(context, uri);
                        } else {
                            //nguoc lai
                            if (MyUtils.checkUrl(matchedText)) {
                                Intent i = new Intent(Intent.ACTION_VIEW);

                                Uri uri = Uri.parse(url);

                                //mongodb://localhost:27017/db/
                                if (!url.contains("://")) {
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        uri = Uri.parse("http://" + url);
                                    }
                                }
                                i.setData(uri);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            } else if (MyUtils.isMobilePhone(matchedText)) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + matchedText));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } else if (MyUtils.isEmailValid(matchedText)) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                intent.putExtra(Intent.EXTRA_EMAIL, matchedText);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                                if (intent.resolveActivity(context.getPackageManager()) != null) {
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        }


                    }

                }
            });


        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*private UserInfo getUser(String fullName) {
        if (!TextUtils.isEmpty(fullName)) {
            *//*realm.beginTransaction();
            UserInfo user = realm.where(UserInfo.class)
                    .equalTo("nameMBN", fullName)
                    .or()
                    .equalTo("name", fullName)
                    .findFirst();
            realm.commitTransaction();*//*

            *//*if (user != null) {
                MyUtils.chatWithUser(context, user);
            }*//*

            return user;
        }
        return null;
    }*/

    ///////////////////////////////////////////////////////////////////////////////////////////////
    int positionNeedAlertBackground = -1;

    public void changeBackgroundFound(int position) {
        if (position < getItemCount()) {
            positionNeedAlertBackground = position;
            notifyItemChanged(position);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void setReplyUI(RoomLog m, LinearLayout linearReply,
                            ImageView imgReplyClose,
                            TextView txtReply1,
                            TextView txtReply2,
                            ImageView imgReply
    ) {

        linearReply.setVisibility(View.GONE);
        imgReplyClose.setVisibility(View.GONE);
        if (m == null || m.isDeleted()) {//khong co reply, an giao dien
            linearReply.setVisibility(View.GONE);
        } else {
            if (RoomLog.REPLY.equals(m.getReplyOrForward()) && m.getOriginMessage() != null) {
                //SET GIAO DIEN REPLY TIN GOC
                JsonElement originMessage = m.getOriginMessage();
                if (originMessage != null) {
                    OriginMessage message = new Gson().fromJson(originMessage.toString(), OriginMessage.class);
                    if (message != null) {
                        //todo set hinh, ten, description
                        if (message.getAuthorinfo() != null) {
                            String name = message.getAuthorinfo().getName();
                            txtReply1.setText(name);
                        }

                        //get description
                        String desc = getContentReplyForward(message.getType(), message.getContent());
                        txtReply2.setText(desc);


                        //set hinh
                        imgReply.setVisibility(View.GONE);
                        String link = "";
                        int resId = 0;

                        if (message.getContent() != null) {
                            try {
                                String json = message.getContent().toString();
                                switch (message.getType()) {
                                    case ContentType.TEXT:
//                        Text t = new Gson().fromJson(json, Text.class);
//                        text = Utils.copyMentionUserName(t.getContent());
                                        break;
                                    case ContentType.IMAGE:
                                        Image i = new Gson().fromJson(json, Image.class);
                                        link = i.getLink();
                                        break;
                                    case ContentType.FILE:
//                        File f = new Gson().fromJson(json, File.class);
//                        text = f.getLink();
                                        resId = R.drawable.icon_file_doc;
                                        break;
                                    case ContentType.VIDEO:
//                        Video v = new Gson().fromJson(json, Video.class);
//                        text = v.getLink();
                                        resId = R.drawable.ic_record_video;
                                        break;
                                    case ContentType.LINK:
//                        Link link = new Gson().fromJson(json, Link.class);
//                        text = link.getLink();
                                        resId = R.drawable.ic_link_light_blue_500_48dp;
                                        break;
                                    case ContentType.LOCATION:
                                /*Location location = new Gson().fromJson(json, Location.class);
                                text = location.getAddress();
                                //neu ko co thi lay o cache
                                if (TextUtils.isEmpty(text)) {
                                    text = m.getAddressSave();
                                }
                                //neu van khong co address thi lay lat,long
                                if(TextUtils.isEmpty(text)){
                                    text = location.getLat()+","+location.getLng();
                                }*/
                                        resId = R.drawable.img_location;
                                        break;
                                    case ContentType.ITEM:
//                        Item item = new Gson().fromJson(json, Item.class);
//                        text = item.getItemLink();
                                        break;
                                    case ContentType.ALBUM:
                                        JsonObject json2 = message.getContent();
                                        JsonArray items = json2.getAsJsonArray("items");
                                        java.lang.reflect.Type type = new TypeToken<List<AlbumItem>>() {
                                        }.getType();
                                        final ArrayList<AlbumItem> album = new Gson().fromJson(items.toString(), type);
                                        if (album != null && album.size() > 0) {
                                            link = album.get(0).getThumbLink();
                                        }

                                        break;
                                    case ContentType.VOICE:
//                            Voice voice = new Gson().fromJson(json, Voice.class);
//                            text = voice.getLink();
                                        resId = R.drawable.ic_voice_120;
                                        break;
                                    case ContentType.CONTACT:
                                        Contact contact = new Gson().fromJson(json, Contact.class);
                                        link = contact.getAvatar();
                                        break;
                                    case ContentType.PLAN:
//                            PlanModel plan = new Gson().fromJson(json, PlanModel.class);
//                            text = plan.getTitle();
                                        resId = R.drawable.ic_meet_card_2;
                                        break;
                                    case ContentType.MEETING:
                                        resId = R.drawable.ic_zoom;
                                        break;
                                }

                                if (!TextUtils.isEmpty(link)) {
                                    int avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_64);
                                    int radius = context.getResources().getDimensionPixelSize(R.dimen.radius_image);
                                    try {
                                        Glide.with(context)
                                                .load(link)
                                                .override(avatarSize, avatarSize)
                                                .placeholder(R.drawable.no_media_small)
                                                .transform(new CenterCrop(), new RoundedCorners(radius))
                                                .into(imgReply);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        isHaveError = true;
                                    }
                                    imgReply.setVisibility(View.VISIBLE);
                                } else if (resId > 0) {
                                    imgReply.setImageResource(resId);
                                    imgReply.setVisibility(View.VISIBLE);
                                }

                                linearReply.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }


        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void setForwardUI(RoomLog m, _0_BaseHolder holder) {
        holder.linearForward.setVisibility(View.GONE);

        if (m == null || m.isDeleted()) {//khong co reply, an giao dien
            holder.linearForward.setVisibility(View.GONE);
        } else {

            if (RoomLog.FORWARD.equals(m.getReplyOrForward()) && m.getOriginMessage() != null) {
                //SET GIAO DIEN
                JsonElement originMessage = m.getOriginMessage();
                if (originMessage != null) {
                    OriginMessage message = new Gson().fromJson(originMessage.toString(), OriginMessage.class);
                    if (message != null) {
                        //todo set ten nguoi goi tin goc
                        if (message.getAuthorinfo() != null) {
                            String name = message.getAuthorinfo().getName();
                            holder.txtForwardName.setText(name);
                        }

                        holder.linearForward.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void setPin(boolean isPin, ImageView imgPin) {
        if (isPinAdapter) {
            imgPin.setVisibility(View.GONE);
        } else {
            if (isPin) {
                imgPin.setVisibility(View.VISIBLE);
            } else {
                imgPin.setVisibility(View.GONE);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void openRoomChatFromMessageReplyOrForward(RoomLog m) {
        if (m != null && !m.isDeleted()) {
            JsonElement originMessage = m.getOriginMessage();
            if (originMessage != null) {
                OriginMessage message = new Gson().fromJson(originMessage.toString(), OriginMessage.class);
                if (message != null) {
//                    context.finish();
                    MyUtils.openChatRoom(context, message.getRoomid(), message.getId());
                    collapseBubble();
                }
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.frameLayout)
        FrameLayout frameLayout;
        @BindView(R2.id.txtGroupDate)
        TextView txtGroupDate;


        @BindView(R2.id.linear1)
        LinearLayout linear1;
        @BindView(R2.id.linear2)
        LinearLayout linear2;
        @BindView(R2.id.img1)
        ImageView img1;


        @BindView(R2.id.txt1)
        TextView txt1;
        @BindView(R2.id.txt3)
        TextView txt3;
        @BindView(R2.id.txt33)
        TextView txt33;
        @BindView(R2.id.imgPin)
        ImageView imgPin;

        @BindView(R2.id.txt2)
        CustomTextViewLink txt2;

        //Image
        @BindView(R2.id.linear3)
        RelativeLayout linear3;
        @BindView(R2.id.img2)
        ImageView img2;
        @BindView(R2.id.pb)
        ProgressBar pb;

        //Video
        @BindView(R2.id.imgPlay)
        ImageView imgPlay;

        @BindView(R2.id.map)
        MapView map;

        @BindView(R2.id.recyclerView)
        RecyclerView rv;
        @BindView(R2.id.linearRoot)
        LinearLayout linearRoot;
        @BindView(R2.id.linearIcons)
        LinearLayout linearIcons;

        //link preview
        @BindView(R2.id.linearPreview)
        LinearLayout linearPreview;
        @BindView(R2.id.linearBox)
        LinearLayout linearBox;
        @BindView(R2.id.imgPre)
        ImageView imgPre;
        @BindView(R2.id.txtPre1)
        TextView txtPre1;
        @BindView(R2.id.txtPre2)
        TextView txtPre2;
        @BindView(R2.id.txtPre3)
        TextView txtPre3;
        @BindView(R2.id.pbPre)
        ProgressBar pbPre;
        @BindView(R2.id.txtLink)
        TextView txtLink;


        @BindView(R2.id.linearItemPreview)
        LinearLayout linearItemPreview;
        @BindView(R2.id.txtItem1)
        TextView txtItem1;
        @BindView(R2.id.txtItem2)
        TextView txtItem2;
        @BindView(R2.id.txtItem3)
        TextView txtItem3;
        @BindView(R2.id.imgItem)
        ImageView imgItem;
        @BindView(R2.id.btnItem)
        AppCompatButton btnItem;

        @BindView(R2.id.linearItemPriceSaleOff)
        LinearLayout linearItemPriceSaleOff;
        @BindView(R2.id.txtItem4)
        TextView txtItem4;

        //album
        @BindView(R2.id.rvAlbum)
        RecyclerView rvAlbum;

        //voice
        @BindView(R2.id.linearVoice)
        LinearLayout linearVoice;
        @BindView(R2.id.imgVoicePlay)
        ImageView imgVoicePlay;
        @BindView(R2.id.txtVoiceDuration)
        TextView txtVoiceDuration;
        @BindView(R2.id.txtVoiceSize)
        TextView txtVoiceSize;

        //contact
        @BindView(R2.id.linearContact)
        LinearLayout linearContact;
        @BindView(R2.id.imgContactAvatar)
        ImageView imgContactAvatar;
        @BindView(R2.id.txtContactName)
        TextView txtContactName;
        @BindView(R2.id.txtContactPhone)
        TextView txtContactPhone;
        @BindView(R2.id.btnContactMessage)
        Button btnContactMessage;
        @BindView(R2.id.btnContactInfo)
        Button btnContactInfo;

        //plan
        @BindView(R2.id.linearPlan)
        LinearLayout linearPlan;
        @BindView(R2.id.txt1Plan)
        TextView txt1Plan;
        @BindView(R2.id.txt2Plan)
        TextView txt2Plan;
        @BindView(R2.id.txt3Plan)
        TextView txt3Plan;
        @BindView(R2.id.txt4Plan)
        TextView txt4Plan;
        @BindView(R2.id.pbPlan)
        CustomSeekBar pbPlan;
        @BindView(R2.id.btnComment)
        Button btnComment;


        //REPLY MESSAGE
        @BindView(R2.id.linearReply)
        LinearLayout linearReply;
        @BindView(R2.id.relativeRely)
        RelativeLayout relativeRely;
        @BindView(R2.id.imgReply)
        ImageView imgReply;
        @BindView(R2.id.imgReplyClose)
        ImageView imgReplyClose;
        @BindView(R2.id.txtReply1)
        TextView txtReply1;
        @BindView(R2.id.txtReply2)
        TextView txtReply2;


        //FORWARD MESSAGE
        @BindView(R2.id.linearForward)
        LinearLayout linearForward;
        @BindView(R2.id.txtForwardName)
        TextView txtForwardName;

        private static final int DEFAULT_COLUMN = 3;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            rvAlbum.setLayoutManager(new GridLayoutManager(itemView.getContext(), DEFAULT_COLUMN));
            rvAlbum.setHasFixedSize(true);

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _1_DateHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.frameLayout)
        LinearLayout frameLayout;
        @BindView(R2.id.txtGroupDate)
        TextView txtGroupDate;

        public _1_DateHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _0_EmptyHolder extends RecyclerView.ViewHolder {

        //CONTENT
        //contact
        @BindView(R2.id.view)
        View view;

        public _0_EmptyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class _0_BaseHolder extends RecyclerView.ViewHolder {
        //Layout main and avatar
        @BindView(R2.id.linearRoot)
        LinearLayout linearRoot;
        @BindView(R2.id.relativeContainer)
        RelativeLayout relativeContainer;
        @BindView(R2.id.linear1)
        LinearLayout linear1;
        @BindView(R2.id.linear2)
        LinearLayout linear2;
        @BindView(R2.id.img1)
        ImageView img1;

        //name
        @Nullable
        @BindView(R2.id.txt33)
        TextView txt33;

        //Top : forward
        //FORWARD MESSAGE
        @BindView(R2.id.linearForward)
        LinearLayout linearForward;
        @BindView(R2.id.txtForwardName)
        TextView txtForwardName;


        //Bottom: date and pin icon
        @BindView(R2.id.txt3)
        TextView txt3;
        @BindView(R2.id.imgPin)
        ImageView imgPin;


        //BOTTOM: ICON USER VIEWED AND NAME
        @BindView(R2.id.linearIcons)
        LinearLayout linearIcons;
        @BindView(R2.id.recyclerView)
        RecyclerView rv;
        @BindView(R2.id.txt1)
        TextView txt1;


        //CONTENT tuy vao tung type


        public _0_BaseHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class _2_TextHolder extends _0_BaseHolder {


        //Top : forward, reply
        //REPLY MESSAGE
        @BindView(R2.id.linearReply)
        LinearLayout linearReply;
        @BindView(R2.id.relativeRely)
        RelativeLayout relativeRely;
        @BindView(R2.id.imgReply)
        ImageView imgReply;
        @BindView(R2.id.imgReplyClose)
        ImageView imgReplyClose;
        @BindView(R2.id.txtReply1)
        TextView txtReply1;
        @BindView(R2.id.txtReply2)
        TextView txtReply2;


        //CONTENT
        @BindView(R2.id.txt2)
        CustomTextViewLink txt2;


        public _2_TextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Linkify.addLinks(txt2, Linkify.ALL);
            txt2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            txt2.setEmojiconSize(emojiFontSizeDefault);

        }

        public void bindView(RoomLog m) {
            if (m != null) {
                txt2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txt2.setEmojiconSize(emojiFontSizeDefault);

                //reply
                setReplyUI(m, linearReply, imgReplyClose, txtReply1, txtReply2, imgReply);

                if (m != null && m.getContent() != null) {
                    Text t = new Gson().fromJson(m.getContent().toString(), Text.class);

                    if (t != null) {
                        boolean isOnlyEmoji = MyUtils.isOnlyEmojiCharacter(t.getContent());
                        if (isOnlyEmoji) {
                            txt2.setPadding(padding7, padding7, padding7, padding7);
                            txt2.setText(t.getContent());
                            int lines = txt2.getText().toString().length();
                            if (lines > 8) {
                                txt2.setEmojiconSize(emojiFontSizeDefault);
                            } else {
                                txt2.setEmojiconSize(emojiFontSizeLarge);
                            }

                        } else {
                            //Control mention
                            txt2.setPadding(padding7, padding2, padding7, 0);
                            setOnClickMention(t.getContent(), txt2);
                        }


                    }

                    txt2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linear2.performClick();
                        }
                    });
                    txt2.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            linear2.performLongClick();
                            return true;
                        }
                    });
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private _3_ImageHolder uploadingImageHolder;

    public class _3_ImageHolder extends _0_BaseHolder {

        //CONTENT
        //Image
        @BindView(R2.id.linear3)
        RelativeLayout linear3;
        @BindView(R2.id.img2)
        ImageView img2;
        @BindView(R2.id.pb)
        ProgressBar pb;


        public _3_ImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //uploading holder
        @BindView(R2.id.relativeCir)
        RelativeLayout relativeCir;
        @BindView(R2.id.progressCir)
        ProgressBar progressCir;
        @BindView(R2.id.txtCir)
        TextView txtCir;
        @BindView(R2.id.imgCir)
        ImageView imgCir;

        private void setPercent(int percent) {
            if (percent == 100) {
                if (txtCir.getVisibility() != View.GONE) {
                    txtCir.setVisibility(View.GONE);
                }
                if (imgCir.getVisibility() != View.VISIBLE) {
                    imgCir.setVisibility(View.VISIBLE);
                }
            } else {
                if (txtCir.getVisibility() != View.VISIBLE) {
                    txtCir.setVisibility(View.VISIBLE);
                }
                if (imgCir.getVisibility() != View.GONE) {
                    imgCir.setVisibility(View.GONE);
                }
                txtCir.setText(percent + "%");
            }
            progressCir.setProgress(percent);
        }

        private boolean isUploading;

        private void setState(RoomLog m) {
            //HINH VA BAN DO THI KO CAN HIEN BACKGROUND
            linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);

            //set image theo trang thai
            this.isUploading = m.isUploading();
            if (isUploading) {

                uploadingImageHolder = this;
                if (relativeCir.getVisibility() != View.VISIBLE) {
                    relativeCir.setVisibility(View.VISIBLE);
                }
                //set hinh cache tam tu local path
                if (m != null) {

                    //percent
                    int percent = getPercent(m.getImgThumbnailLocal());
                    setPercent(percent);

                    //local image
                    if (m.getThumbWidth() > 0 && m.getThumbHeight() > 0) {
                        int height = MyUtils.getHeightImage(m.getThumbWidth(), m.getThumbHeight(), imageSize);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, height);
                        img2.setLayoutParams(params);
                        relativeCir.setLayoutParams(params);
                    }

                    if (TextUtils.isEmpty(m.getImgThumbnailLocal())) {
                        img2.setVisibility(View.GONE);
                    } else {
                        img2.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(context)
                                    .load(m.getImgThumbnailLocal())
                                    .transform(new RoundedCorners(radius))
                                    .error(R.drawable.no_media_small)
                                    .into(img2);
                        } catch (Exception e) {
                            e.printStackTrace();
                            isHaveError = true;
                        }
                    }

                }
            } else {

                if (relativeCir.getVisibility() != View.GONE) {
                    relativeCir.setVisibility(View.GONE);
                }
                try {
                    final Image i = new Gson().fromJson(m.getContent().toString(), Image.class);
                    img2.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (isPinAdapter) {
                                linear2.performClick();
                            } else {
                                try {
                                    String fullImage = i.getLink();//thumnail.replace("tn-", "");
                                    //                                            MyUtils.openFile(context, fullImage);
                                    ArrayList<String> list = MyUtils.getAllImageLink(ChatAdapter.this.logs);
                                    int position = list.indexOf(fullImage);

                                    MyUtils.openFileImages(context, list, position);

                                    collapseBubble();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

                    img2.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            linear2.performLongClick();
                            return true;
                        }
                    });


                    if (!TextUtils.isEmpty(i.getThumbLink())) {
                        boolean isImageValid = MyUtils.isImageUrl(i.getThumbLink());
                        if (isImageValid) {
                            //ratio = height/width; => height = ratio*width
                            int height = MyUtils.getHeightImage((int) i.getThumbWidth(), (int) i.getThumbHeight(), imageSize);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, height);
                            img2.setLayoutParams(params);


                            //NEU LA ANH GIF THI LAY HINH FULL HIEN THI
                            String url = i.getThumbLink();
                            boolean isGif = MyUtils.isImageGifUrl(i.getLink());
                            if (isGif) {
                                url = i.getLink();
                            }

                            //load hinh
                            try {
                                Glide.with(context)
                                        .load(url)
                                        .transform(new RoundedCorners(radius))
                                        .error(R.drawable.no_media_small)
                                        .into(img2);
                            } catch (Exception e) {
                                e.printStackTrace();
                                isHaveError = true;
                            }

                        } else {
                            //hien link
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _4_FileHolder extends _0_BaseHolder {

        //CONTENT
        //Image
        @BindView(R2.id.linear3)
        RelativeLayout linear3;
        @BindView(R2.id.img2)
        ImageView img2;
        @BindView(R2.id.pb)
        ProgressBar pb;

        //text file name
        @BindView(R2.id.txt2)
        CustomTextViewLink txt2;


        public _4_FileHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            img2.setImageResource(R.drawable.img_upload_150);


        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _5_LinkHolder extends _0_BaseHolder {

        //Top : forward, reply
        //REPLY MESSAGE
        @BindView(R2.id.linearReply)
        LinearLayout linearReply;
        @BindView(R2.id.relativeRely)
        RelativeLayout relativeRely;
        @BindView(R2.id.imgReply)
        ImageView imgReply;
        @BindView(R2.id.imgReplyClose)
        ImageView imgReplyClose;
        @BindView(R2.id.txtReply1)
        TextView txtReply1;
        @BindView(R2.id.txtReply2)
        TextView txtReply2;


        //CONTENT
        //link preview
        @BindView(R2.id.linearPreview)
        LinearLayout linearPreview;
        @BindView(R2.id.linearBox)
        LinearLayout linearBox;
        @BindView(R2.id.imgPre)
        ImageView imgPre;
        @BindView(R2.id.txtPre1)
        TextView txtPre1;
        @BindView(R2.id.txtPre2)
        TextView txtPre2;
        @BindView(R2.id.txtPre3)
        TextView txtPre3;
        @BindView(R2.id.pbPre)
        ProgressBar pbPre;
        @BindView(R2.id.txtLink)
        CustomTextViewLink txtLink;


        public _5_LinkHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _6_ActionHolder extends RecyclerView.ViewHolder {

        //text action
        @BindView(R2.id.txt2)
        CustomTextViewLink txt2;

        public _6_ActionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _7_LocationHolder extends _0_BaseHolder implements OnMapReadyCallback {

        //CONTENT: mapView and address
        @BindView(R2.id.map)
        MapView mapView;
        @BindView(R2.id.txt2)
        CustomTextViewLink txt2;
        @BindView(R2.id.txtCheckin)
        TextView txtCheckin;
        View layout;

        public _7_LocationHolder(View itemView) {
            super(itemView);
            layout = itemView;
            ButterKnife.bind(this, itemView);
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the mapView ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            setMapLocation();

            if (location != null) {
                if (!TextUtils.isEmpty(location.getLat()) && !TextUtils.isEmpty(location.getLng())) {
                    /*String urlMap = MyUtils.getMapImageUrl(location.getLat(), location.getLng(), mapWidth, mapHeight, map_api_key);
                    Glide.with(context)
                            .load(urlMap)
                            .override(mapWidth, mapHeight)
                            .transform(new CenterCrop(), new RoundedCorners(radius))
                            .into(mapView);*/

                    if (map != null) {
                        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                String address = txt2.getText().toString();
                                MyUtils.openMap(context, location.getLat(), location.getLng(), address);
                                collapseBubble();
                            }
                        });
                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                String address = txt2.getText().toString();
                                MyUtils.openMap(context, location.getLat(), location.getLng(), address);
                                collapseBubble();
                                return true;
                            }
                        });
                    }
                    txt2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout.performClick();
                        }
                    });

                }

                if (map != null) {
                    map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            linear2.performLongClick();
                        }
                    });

                }


                txt2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        linear2.performLongClick();
                        return true;
                    }
                });
            }
        }

        /**
         * {@link com.google.android.gms.maps.GoogleMap}.
         * Adds a marker and centers the camera on the NamedLocation with the normal map type.
         */
        private void setMapLocation() {
            if (map == null) return;

            Location data = (Location) mapView.getTag();
            if (data == null) return;

            // Add a marker for this item and set the camera
            LatLng latLng = data.getLatLng();
            if (latLng != null) {
                // Set the map type back to normal
//                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.clear();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));
                map.addMarker(new MarkerOptions().position(latLng));


            }
        }

        private Location location;

        public void bindView(RoomLog m, int position) {
            if (m != null) {
                //HINH VA BAN DO THI KO CAN HIEN BACKGROUND
                linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);

                location = new Gson().fromJson(m.getContent().toString(), Location.class);
                if (location.isCheckin()) {
                    txtCheckin.setVisibility(View.VISIBLE);
                } else {
                    txtCheckin.setVisibility(View.GONE);
                }

                //map
                layout.setTag(this);
                mapView.setTag(location);
                setMapLocation();

                //UI
                txt2.setText("");
                if (!TextUtils.isEmpty(location.getAddress())) {
                    txt2.setText(location.getAddress());
                } else if (!TextUtils.isEmpty(m.getAddressSave())) {
                    txt2.setText(m.getAddressSave());
                } else {
                    new AsyncTask<Integer, Void, String>() {
                        int index = -1;

                        @Override
                        protected String doInBackground(Integer... voids) {
                            index = voids[0];
                            return MyUtils.getAddress(context, location.getLat(), location.getLng());
                        }

                        @Override
                        protected void onPostExecute(final String address) {
                            super.onPostExecute(address);
                            if (index == position) {
                                try {
                                    txt2.setText(address);
                                    //luu lai vao logs de lan sau khong lay lai
                                    m.setAddressSave(address);
                                    logs.get(index).setAddressSave(address);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }.execute(position);
                }


                    /*final LatLng sydney = new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLng()));
                    mapView.onCreate(null);
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mapView) {
                            // Since the mapView is re-used, need to remove pre-existing mapView features.
                            mapView.clear();
                            mapView.addMarker(new MarkerOptions().position(sydney));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 16f);
                            mapView.moveCamera(cameraUpdate);

                        }
                    });*/


            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _8_9_PaymentHolder extends _0_BaseHolder {

        //CONTENT: text
        @BindView(R2.id.txt2)
        CustomTextViewLink txt2;


        public _8_9_PaymentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _10_ItemHolder extends _0_BaseHolder {

        //CONTENT: Item
        @BindView(R2.id.linearItemPreview)
        LinearLayout linearItemPreview;
        @BindView(R2.id.txtItem1)
        TextView txtItem1;
        @BindView(R2.id.txtItem2)
        TextView txtItem2;
        @BindView(R2.id.txtItem3)
        TextView txtItem3;
        @BindView(R2.id.imgItem)
        ImageView imgItem;
        @BindView(R2.id.btnItem)
        AppCompatButton btnItem;

        @BindView(R2.id.linearItemPriceSaleOff)
        LinearLayout linearItemPriceSaleOff;
        @BindView(R2.id.txtItem4)
        TextView txtItem4;


        public _10_ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private _11_AlbumHolder uploadingAlbumHolder;

    public class _11_AlbumHolder extends _0_BaseHolder {

        //CONTENT
        //Image
       /* @BindView(R2.id.linear3)
        RelativeLayout linear3;
        @BindView(R2.id.img2)
        ImageView img2;
        @BindView(R2.id.pb)
        ProgressBar pb;*/
        //album
        @BindView(R2.id.rvAlbum)
        RecyclerView rvAlbum;

        private static final int DEFAULT_COLUMN = 3;

        public _11_AlbumHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rvAlbum.setLayoutManager(new GridLayoutManager(itemView.getContext(), DEFAULT_COLUMN));
            rvAlbum.setHasFixedSize(true);
//            img2.setImageResource(R.drawable.no_media_small);
        }

        ArrayList<AlbumItem> album;
        private ChatAlbumAdapter adapter;

        public void setState(RoomLog m) {
            //HINH VA BAN DO THI KO CAN HIEN BACKGROUND
            linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);

            if (m != null) {

                album = new ArrayList<AlbumItem>();
                if (m.isUploading()) {
                    uploadingAlbumHolder = this;
                    if (m.getPaths() != null && m.getPaths().size() > 0) {
                        for (int i = 0; i < m.getPaths().size(); i++) {
                            String path = m.getPaths().get(i);
                            AlbumItem item = new AlbumItem();
                            item.setLocalPath(path);
                            item.setUploading(true);
                            item.setPercent(getPercent(path));
                            album.add(item);
                        }
                    }

                } else {
                    JsonObject json = m.getContent();
                    JsonArray items = json.getAsJsonArray("items");
                    java.lang.reflect.Type type = new TypeToken<List<AlbumItem>>() {
                    }.getType();
                    ArrayList<AlbumItem> list = new Gson().fromJson(items.toString(), type);
                    if (list != null && list.size() > 0) {
                        album.addAll(list);
                    }

                }


                //thumnail
                adapter = new ChatAlbumAdapter(album, context, DEFAULT_COLUMN);
                adapter.setItemClickListener(new ChatAlbumAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //mo xem album: gom hinh vao video
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < album.size(); i++) {
                            AlbumItem item = album.get(i);
                            if (!TextUtils.isEmpty(item.getLink())) {
                                list.add(item.getLink());
                            }
                        }
                        if (list.size() > 0) {
                            MyUtils.openFileImages(context, list, position);
                            collapseBubble();
                        }
                    }
                });
                adapter.setItemLongClickListener(new ChatAlbumAdapter.ItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position) {
                        linear2.performLongClick();
                        return true;
                    }
                });

                /*adapter.setItemPercentChangeListener(new ChatAlbumAdapter.ItemPercentChangeListener() {
                    @Override
                    public boolean onPercentChange(String path, int percent) {
                        addPercent(path, percent);
                        return true;
                    }
                });*/

                //set so cot va adapter
                int column = DEFAULT_COLUMN;
                if (album != null && album.size() < column) {
                    column = album.size();
                }

                GridLayoutManager layout = (GridLayoutManager) rvAlbum.getLayoutManager();
                layout.setSpanCount(column);
                rvAlbum.setAdapter(adapter);
                rvAlbum.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        linear2.performLongClick();
                        return true;
                    }
                });
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private _12_VideoHolder uploadingVideoHolder = null;

    public class _12_VideoHolder extends _0_BaseHolder {

        //CONTENT
        //Image
        @BindView(R2.id.linear3)
        RelativeLayout linear3;
        @BindView(R2.id.img2)
        ImageView img2;
        //        @BindView(R2.id.pb)
//        ProgressBar pb;
        //icon play
        @BindView(R2.id.imgPlay)
        ImageView imgPlay;


        public _12_VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //uploading holder
        /*@BindView(R2.id.relativeCir)
        RelativeLayout relativeCir;
        @BindView(R2.id.progressCir)
        ProgressBar progressCir;
        @BindView(R2.id.txtCir)
        TextView txtCir;
        @BindView(R2.id.imgCir)
        ImageView imgCir;

        private void setPercent(int percent) {
            if (percent == 100) {
                if (txtCir.getVisibility() != View.GONE) {
                    txtCir.setVisibility(View.GONE);
                }
                if (imgCir.getVisibility() != View.VISIBLE) {
                    imgCir.setVisibility(View.VISIBLE);
                }
            } else {
                if (txtCir.getVisibility() != View.VISIBLE) {
                    txtCir.setVisibility(View.VISIBLE);
                }
                if (imgCir.getVisibility() != View.GONE) {
                    imgCir.setVisibility(View.GONE);
                }
                txtCir.setText(percent + "%");
            }
            progressCir.setProgress(percent);
        }*/

        private boolean isUploading;

        private void setState(RoomLog m) {
            //HINH VA BAN DO THI KO CAN HIEN BACKGROUND
            linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);

            //set image theo trang thai
            this.isUploading = m.isUploading();
            if (isUploading) {

                uploadingVideoHolder = this;
//                if (relativeCir.getVisibility() != View.VISIBLE) {
//                    relativeCir.setVisibility(View.VISIBLE);
//                }
                //set hinh cache tam tu local path
                if (m != null) {

                    //percent
//                    int percent = getPercent(m.getImgThumbnailLocal());
//                    setPercent(percent);

                    //local image
                    if (m.getThumbWidth() > 0 && m.getThumbHeight() > 0) {
                        int height = MyUtils.getHeightImage(m.getThumbWidth(), m.getThumbHeight(), imageSize);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, height);
                        img2.setLayoutParams(params);
//                        relativeCir.setLayoutParams(params);
                    }
                    try {
                        Glide.with(context)
                                .load(m.getImgThumbnailLocal())
                                .transform(new RoundedCorners(radius))
                                .error(R.drawable.no_media_small)
                                .into(img2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isHaveError = true;
                    }
                }
            } else {
                uploadingVideoHolder = null;
//                if (relativeCir.getVisibility() != View.GONE) {
//                    relativeCir.setVisibility(View.GONE);
//                }

                img2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        linear2.performLongClick();
                        return true;
                    }
                });

                try {
                    final Video video = new Gson().fromJson(m.getContent().toString(), Video.class);

                    //thumnail
                    int imgHeight = MyUtils.getHeightImage(video.getThumbWidth(), video.getThumbHeight(), imageSize);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, imgHeight);
                    img2.setLayoutParams(params);

                    try {
                        Glide.with(context)
                                .load(video.getThumbLink())
                                .transform(new RoundedCorners(radius))
                                .error(R.drawable.no_media_small)
                                .into(img2);
                    } catch (Exception e) {
                        e.printStackTrace();
                        isHaveError = true;
                    }

                    img2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imgPlay.performClick();
                        }
                    });
                    imgPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (MyUtils.checkInternetConnection(context)) {
                                MyUtils.openVideo(context, video.getLink());
                                collapseBubble();
                            } else {
                                MyUtils.showThongBao(context);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _13_VoiceHolder extends _0_BaseHolder {

        //CONTENT
        //voice
        @BindView(R2.id.linearVoice)
        LinearLayout linearVoice;
        @BindView(R2.id.imgVoicePlay)
        ImageView imgVoicePlay;
        @BindView(R2.id.txtVoiceDuration)
        TextView txtVoiceDuration;
        @BindView(R2.id.txtVoiceSize)
        TextView txtVoiceSize;


        public _13_VoiceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _14_ContactHolder extends _0_BaseHolder {

        //CONTENT
        //contact
        @BindView(R2.id.linearContact)
        LinearLayout linearContact;
        @BindView(R2.id.imgContactAvatar)
        ImageView imgContactAvatar;
        @BindView(R2.id.txtContactName)
        TextView txtContactName;
        @BindView(R2.id.txtContactPhone)
        TextView txtContactPhone;
        @BindView(R2.id.btnContactMessage)
        Button btnContactMessage;
        @BindView(R2.id.btnContactInfo)
        Button btnContactInfo;


        public _14_ContactHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _15_PlanHolder extends _0_BaseHolder {

        //CONTENT
        //plan
        @BindView(R2.id.linearPlan)
        LinearLayout linearPlan;
        @BindView(R2.id.txt1Plan)
        TextView txt1Plan;
        @BindView(R2.id.txt2Plan)
        TextView txt2Plan;
        @BindView(R2.id.txt3Plan)
        TextView txt3Plan;
        @BindView(R2.id.txt4Plan)
        TextView txt4Plan;
        @BindView(R2.id.pbPlan)
        CustomSeekBar pbPlan;
        @BindView(R2.id.btnComment)
        Button btnComment;


        public _15_PlanHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class _16_MeetingHolder extends _0_BaseHolder {

        //CONTENT
        //meeting
        @BindView(R2.id.txtMeeting1)
        TextView txtMeeting1;
        @BindView(R2.id.txtMeeting2)
        TextView txtMeeting2;
        @BindView(R2.id.txtMeeting3)
        TextView txtMeeting3;
        @BindView(R2.id.txtMeeting4)
        TextView txtMeeting4;
        @BindView(R2.id.btnMeeting)
        Button btnMeeting;


        public _16_MeetingHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Meeting meeting) {
            if (meeting != null) {
                txtMeeting1.setText(meeting.getTopic());
                txtMeeting2.setText(meeting.getAgenda());
                txtMeeting3.setText(String.valueOf(meeting.getId()));
                txtMeeting4.setText(meeting.getStatus());


                //neu ket thuc thi ko cho nhan nut tham gia
                if (meeting.getStatus().equalsIgnoreCase("ended")) {
                    btnMeeting.setOnClickListener(null);
                    btnMeeting.setTextColor(Color.BLACK);
                    changeColor(btnMeeting, R.color.grey_400);

                } else {
                    btnMeeting.setTextColor(Color.WHITE);
                    changeColor(btnMeeting, R.color.light_blue_500);
                    btnMeeting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(meeting.getJoin_url())) {
                                MyUtils.goToWeb(context, meeting.getJoin_url());
                                collapseBubble();
                            }
                        }
                    });
                }

            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void changeColor(Button button, int color) {
        Drawable buttonDrawable = button.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        //the color is a direct color int and not a color resource
        DrawableCompat.setTint(buttonDrawable, ContextCompat.getColor(context, color));
        button.setBackground(buttonDrawable);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);

        RoomLog m = logs.get(position);
        switch (m.getType()) {
            case ContentType.DATE:
                type = ContentTypeLayout.DATE;
                break;
            case ContentType.TEXT:
                type = ContentTypeLayout.TEXT;
                break;
            case ContentType.IMAGE:
                type = ContentTypeLayout.IMAGE;
                break;
            case ContentType.FILE:
                type = ContentTypeLayout.FILE;
                break;
            case ContentType.LINK:
                type = ContentTypeLayout.LINK;
                break;

            case ContentType.ACTION:
                type = ContentTypeLayout.ACTION;
                break;
            case ContentType.LOCATION:
                type = ContentTypeLayout.LOCATION;
                break;
            case ContentType.NEW_PAYMENT:
                type = ContentTypeLayout.NEW_PAYMENT;
                break;
            case ContentType.PAYMENT_STATUS:
                type = ContentTypeLayout.PAYMENT_STATUS;
                break;
            case ContentType.ITEM:
                type = ContentTypeLayout.ITEM;
                break;

            case ContentType.ALBUM:
                type = ContentTypeLayout.ALBUM;
                break;
            case ContentType.VIDEO:
                type = ContentTypeLayout.VIDEO;
                break;
            case ContentType.VOICE:
                type = ContentTypeLayout.VOICE;
                break;
            case ContentType.CONTACT:
                type = ContentTypeLayout.CONTACT;
                break;
            case ContentType.PLAN:
                type = ContentTypeLayout.PLAN;
                break;
            case ContentType.MEETING:
                type = ContentTypeLayout.MEETING;
                break;

        }
        return type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        switch (viewType) {
            case ContentTypeLayout.DATE:
                View v = li.inflate(R.layout.activity_chat_row_1_date, parent, false);
                return new _1_DateHolder(v);
            case ContentTypeLayout.TEXT:
                v = li.inflate(R.layout.activity_chat_row_2_text, parent, false);
                return new _2_TextHolder(v);
            case ContentTypeLayout.IMAGE:
                v = li.inflate(R.layout.activity_chat_row_3_image, parent, false);
                return new _3_ImageHolder(v);
            case ContentTypeLayout.FILE:
                v = li.inflate(R.layout.activity_chat_row_4_file, parent, false);
                return new _4_FileHolder(v);
            case ContentTypeLayout.LINK:
                v = li.inflate(R.layout.activity_chat_row_5_link, parent, false);
                return new _5_LinkHolder(v);

            case ContentTypeLayout.ACTION:
                v = li.inflate(R.layout.activity_chat_row_6_action, parent, false);
                return new _6_ActionHolder(v);
            case ContentTypeLayout.LOCATION:
                v = li.inflate(R.layout.activity_chat_row_7_location, parent, false);
                return new _7_LocationHolder(v);

            case ContentTypeLayout.NEW_PAYMENT:
            case ContentTypeLayout.PAYMENT_STATUS:
                v = li.inflate(R.layout.activity_chat_row_8_9_payment, parent, false);
                return new _8_9_PaymentHolder(v);

            case ContentTypeLayout.ITEM:
                v = li.inflate(R.layout.activity_chat_row_10_item, parent, false);
                return new _10_ItemHolder(v);

            case ContentTypeLayout.ALBUM:
                v = li.inflate(R.layout.activity_chat_row_11_album, parent, false);
                return new _11_AlbumHolder(v);
            case ContentTypeLayout.VIDEO:
                v = li.inflate(R.layout.activity_chat_row_12_video, parent, false);
                return new _12_VideoHolder(v);
            case ContentTypeLayout.VOICE:
                v = li.inflate(R.layout.activity_chat_row_13_voice, parent, false);
                return new _13_VoiceHolder(v);
            case ContentTypeLayout.CONTACT:
                v = li.inflate(R.layout.activity_chat_row_14_contact, parent, false);
                return new _14_ContactHolder(v);
            case ContentTypeLayout.PLAN:
                v = li.inflate(R.layout.activity_chat_row_15_plan, parent, false);
                return new _15_PlanHolder(v);
            case ContentTypeLayout.MEETING:
                v = li.inflate(R.layout.activity_chat_row_16_meeting, parent, false);
                return new _16_MeetingHolder(v);

            default:
                v = li.inflate(R.layout.activity_chat_row_0_empty, parent, false);
                return new _0_EmptyHolder(v);

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isDestroyed(Context context) {
        if (null == context) {
            return true;
        }

        /*if (context instanceof Activity) {
            Activity activity = (Activity) context;

            if (activity.isFinishing()) {
                return true;
            }

            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed();
        }*/

        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //NEU LA TYPE MOI THI KO HIEN RA
        if (holder instanceof _0_EmptyHolder) {
            ((_0_EmptyHolder) holder).view.setVisibility(View.GONE);
            return;
        }

        //NEU LA TYPE DA DINH NGHIA TRUOC THI XU LY
        if (position < logs.size()) {

            final RoomLog m = logs.get(position);


            ///////////////////////////////////////////////////////////////////////////////////////
            //SET DEFAULT: AVATAR, PIN ICON, TIME, NAME
            if (holder instanceof _0_BaseHolder) {
                _0_BaseHolder baseHolder = (_0_BaseHolder) holder;

                //time
                ///dang upload
                if (m.isUploading()) {
                    String time = MyUtils.getCurrentDate(DateFormat.DATE_FORMAT_HH_MM_AA);
                    baseHolder.txt3.setText(time);

                } else {
                    String time = MyUtils.getDateChatHHmm(m.getCreateDate());
                    baseHolder.txt3.setText(time);
                }

                //mac dinh an nguoi xem
                baseHolder.txt1.setVisibility(View.GONE);

                //pin icon
                setPin(m.isPin(), baseHolder.imgPin);

                //forward
                setForwardUI(m, baseHolder);

                //user view
                //co trong ds mapView thi hien thi, va ko phai la action
                if (mapViewed.containsKey(m.get_id()) && !m.getType().equalsIgnoreCase(ContentType.ACTION)) {
                    baseHolder.rv.setVisibility(View.INVISIBLE);
                    baseHolder.rv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
                    ListAvatarAdapter adapter = new ListAvatarAdapter(context, mapViewed.get(m.get_id()), room);
                    baseHolder.rv.setAdapter(adapter);

                    animationShow(baseHolder.rv);

                } else {
                    baseHolder.rv.setVisibility(View.GONE);
                    baseHolder.rv.setAdapter(null);
                }

                //set layout owner, guest
                if (m.getUserIdAuthor().equals(ownerId)) {//me
                    baseHolder.img1.setVisibility(View.INVISIBLE);
                    baseHolder.linear2.setBackgroundResource(R.drawable.shape_outcoming_message);

                    baseHolder.linear1.setGravity(Gravity.RIGHT);
                    baseHolder.linearRoot.setGravity(Gravity.RIGHT);
                    baseHolder.linearIcons.setGravity(Gravity.RIGHT);

                    //KHONG HIEN TEN MINH
                    baseHolder.txt33.setVisibility(View.GONE);


                } else {//USER ĐỐI DIỆN

                    setVisibleAvatar(position, baseHolder.img1);
                    baseHolder.linear2.setBackgroundResource(R.drawable.shape_incoming_message);

                    baseHolder.linear1.setGravity(Gravity.LEFT);
                    baseHolder.linearRoot.setGravity(Gravity.LEFT);
                    baseHolder.linearIcons.setGravity(Gravity.LEFT);


                    //neu avatar ko hien thi ko can load
                    if (baseHolder.img1.getVisibility() == View.VISIBLE) {

//                        final Member member = getMember(m.getUserIdAuthor());
                        //lay ten local
                        UserInfo item = realm.where(UserInfo.class).equalTo("_id", m.getUserIdAuthor()).findFirst();
                        if (item != null) {
                            //co local thi lay local
                        } else {
                            //ko co local thi lay theo member
                            Member member = getMember(m.getUserIdAuthor());
                            if (member != null && member.getUserInfo() != null) {
                                item = member.getUserInfo().convertToUserInfo();
                            }

                        }


                        if (item != null) {
                            //neu chat 1:1 thi ko hien ten
                            if (!isPrivateRoom) {
                                baseHolder.txt33.setVisibility(View.VISIBLE);
                                baseHolder.txt33.setText(item.getName());
                                if(!TextUtils.isEmpty(item.getName())) {
                                    baseHolder.txt33.setMinWidth(item.getName().length());
                                }

                            } else {
                                baseHolder.txt33.setVisibility(View.GONE);
                            }

                            if (!TextUtils.isEmpty(item.getAvatar())) {
                                if (!isDestroyed(context)) {
                                    try {
                                        Glide.with(context)
                                                .load(item.getAvatar())
                                                .override(avatarSize, avatarSize)
                                                .placeholder(R.drawable.ic_user_2)
                                                .transform(new CenterCrop(), new RoundedCorners(avatarSize / 2))
                                                .into(baseHolder.img1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        isHaveError = true;

                                    }
                                }
                            } else {
                                if (!TextUtils.isEmpty(item.getName())) {
                                    ColorGenerator generator = ColorGenerator.MATERIAL;
                                    int color = generator.getColor(item.getName());

                                    TextDrawable drawable = TextDrawable.builder()
                                            .buildRound(item.getName().substring(0, 1), color);
                                    baseHolder.img1.setImageDrawable(drawable);
                                }
                            }


                            final UserInfo info = item;
                            baseHolder.img1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (MyUtils.checkInternetConnection(context)) {
                                        if (room != null) {
                                            Intent intent = new Intent(context, ChatUserDetailActivity.class);
                                            intent.putExtra(UserInfo.USER_INFO, info);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        }
                                    } else {
                                        MyUtils.showThongBao(context);
                                    }
                                }
                            });
                        }


                    } else {

                        if (isChannel) {

                            if (room != null) {
                                //kiem tra quyen tren channel, an hien ten admin
                                if (room.isSignMessage()) {//hien ten admin
                                    final Member member = getMember(m.getUserIdAuthor());
                                    if (member != null && member.getUserInfo() != null) {
                                        baseHolder.txt33.setVisibility(View.VISIBLE);
                                        baseHolder.txt33.setText(room.getRoomName() + " - " + member.getUserInfo().getName());
                                        baseHolder.txt33.setMinWidth(baseHolder.txt33.getText().length());
                                    }

                                } else {//chi hien ten kenh
                                    baseHolder.txt33.setVisibility(View.VISIBLE);
                                    baseHolder.txt33.setText(room.getRoomName());
                                    baseHolder.txt33.setMinWidth(baseHolder.txt33.getText().length());
                                }

                                //avatar channel
                                baseHolder.img1.setVisibility(View.VISIBLE);
                                try {
                                    Glide.with(context)
                                            .load(room.getRoomAvatar())
                                            .override(avatarSize, avatarSize)
                                            .placeholder(R.drawable.ic_channel)
                                            .transform(new CenterCrop(), new RoundedCorners(avatarSize / 2))
                                            .into(baseHolder.img1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    isHaveError = true;
                                }
                            }

                        } else {
                            baseHolder.txt33.setVisibility(View.GONE);
                        }
                    }
                }
                /////////////////////////////////////////////////////////////////////


                baseHolder.linear2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //CHAT: CLICK VAO HIEN THI NGUOI DA XEM TIN
                        //PIN: CLICK VAO THI LINK DEN TIN TRONG CHAT
                        if (isPinAdapter) {
                            //TIN PIN : NHAN VAO 1 TIN DA PIN THI CLEAR ADAPTER, LAY LAI TU DAU, TIN SẼ KẸP GIỮA 30 TIN TRÊN, 30 TIN DƯỚI,
                            /*SAU ĐÓ LOAD LÊN LẤY THÊM, LOAD XUỐNG THÌ LẤY THÊM ĐẾN KHI =0 THÌ ĐÃ VỀ TIN 1 (ẨN NÚT VỀ ĐẦU - LOAD LẠI NHƯ MỚI VÀO MÀN HÌNH)
                            Lấy ds logs trước và sau 1 Log	getRoomNearbyLogs	roomId, logId, itemCount
                            Lấy ds logs trước 1 Log	getRoomPreviousLogs	roomId, logId, itemCount
                            Lấy ds logs sau 1 Log	getRoomNextLogs	roomId, logId, itemCount*/

                            Intent intent = new Intent(ChatActivity.ACTION_OPEN_PIN_MESSAGE_CHAT);
                            intent.putExtra(RoomLog.ROOM_LOG_ID, m.get_id());
                            context.sendBroadcast(intent);

                            //dong man hinh pin message
                            intent = new Intent(MH06_PinMessageActivity.ACTION_FINISH);
                            context.sendBroadcast(intent);

                        } else {

                            if (baseHolder.txt1.getVisibility() == View.VISIBLE) {
                                animationHide(baseHolder.txt1);
                            } else {
                                //forward hoac reply
                                if (RoomLog.REPLY.equals(m.getReplyOrForward()) && m.getOriginMessage() != null) {
                                    //dieu huong
                                    openRoomChatFromMessageReplyOrForward(m);
                                } else if (RoomLog.FORWARD.equals(m.getReplyOrForward()) && m.getOriginMessage() != null) {
                                    //dieu huong
                                    openRoomChatFromMessageReplyOrForward(m);

                                } else {
                                    //xem ai da xem
                                    try {
                                        List<ChatView> views = m.getViews();
                                        if (views != null && views.size() > 1) {

                                            String title = context.getString(R.string.seen_by) + " ";
                                            String message = "";
                                            for (int i = 0; i < views.size(); i++) {
                                                ChatView v = views.get(i);
                                                if (v.isViewed()) {
                                                    Member member = getMember(v.getUserId());
                                                    if (member != null && member.getUserInfo() != null) {
                                                        if (i == views.size() - 1) {
                                                            message += member.getUserInfo().getName();
                                                        } else {
                                                            message += member.getUserInfo().getName() + ", ";
                                                        }
                                                    }
                                                }
                                            }

                                            if (!TextUtils.isEmpty(message) && message.length() > 2) {
                                                message = title + message;
                                                String end = message.substring(message.length() - 2, message.length());
                                                if (end.equalsIgnoreCase(", ")) {
                                                    message = message.substring(0, message.length() - 2);
                                                }

                                                baseHolder.txt1.setText(message);
                                                animationShow(baseHolder.txt1);
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            }

                        }

                    }
                });

                baseHolder.linear2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        //Khong hanh dong cho action(moi user, xoa user)
                        if (!m.getType().equalsIgnoreCase(ContentType.ACTION)) {
                            showActionMenu(baseHolder.linear2, m, position);
                        }

                        return true;
                    }
                });


                //alert position background
                if (position == positionNeedAlertBackground) {

                    //hien roi thi thoi
                    positionNeedAlertBackground = -1;

                    int colorFrom = ContextCompat.getColor(context, R.color.light_blue_500);
                    int colorTo = ContextCompat.getColor(context, R.color.blue_grey_50);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(3000); // milliseconds
                    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animator) {
                            baseHolder.linearRoot.setBackgroundColor((int) animator.getAnimatedValue());
                        }


                    });
                    colorAnimation.start();

                } else {
                    Animation animation = baseHolder.linearRoot.getAnimation();
                    if (animation != null && !animation.hasEnded()) {
                        baseHolder.linearRoot.clearAnimation();
                    }
                }

            }

            ///////////////////////////////////////////////////////////////////////////////////////


            //type
            switch (getItemViewType(position)) {
                case ContentTypeLayout.DATE:
                    _1_DateHolder h1 = (_1_DateHolder) holder;
                    h1.txtGroupDate.setText(MyUtils.getDateChatFull(m.getCreateDate()));
                    break;
                case ContentTypeLayout.TEXT:
                    _2_TextHolder h2 = (_2_TextHolder) holder;
                    h2.bindView(m);

                    break;
                case ContentTypeLayout.IMAGE:
                    _3_ImageHolder h3 = (_3_ImageHolder) holder;
                    h3.setState(m);

                    break;
                case ContentTypeLayout.FILE:
                    _4_FileHolder h4 = (_4_FileHolder) holder;

                    //HINH VA BAN DO THI KO CAN HIEN BACKGROUND
                    h4.linear2.setBackgroundResource(0);

                    h4.linear3.setVisibility(View.GONE);

                    //Drawable d = ContextCompat.getDrawable(context, R.drawable.ic_insert_drive_file_grey_500_48dp);
                    h4.txt2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_download_black_24dp, 0, 0, 0);
                    //https://stackoverflow.com/questions/43349822/how-to-make-a-textview-text-link-clickable

                    try {
                        final File f = new Gson().fromJson(m.getContent().toString(), File.class);
                        String fileName = MyUtils.getFileNameFromUrl(f.getLink());
                        String html = "<a href='" + f.getLink() + "'>" + fileName + "</a>";

                        h4.txt2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (f != null && f.getLink() != null) {
                                            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(f.getLink()));
                                            context.startActivity(browserIntent);*/
                                    //neu tu widget thi tai luon
                                    if (isFromWidget) {
                                        MyUtils.downloadFile(context, f.getLink());
                                    } else {
                                        try {
                                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(context);
                                            alertDialogBuilder.setMessage(R.string.confirm_download);
                                            alertDialogBuilder.setPositiveButton(context.getString(R.string.download), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    arg0.dismiss();
                                                    MyUtils.downloadFile(context, f.getLink());
                                                }
                                            });
                                            alertDialogBuilder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });

                                            androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }


                                }
                            }
                        });
                        h4.txt2.setText(Html.fromHtml(html));
                        Linkify.addLinks(h4.txt2, Linkify.WEB_URLS);

                        h4.txt2.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                h4.linear2.performLongClick();
                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case ContentTypeLayout.LINK:
                    _5_LinkHolder h5 = (_5_LinkHolder) holder;
                    //reply
                    setReplyUI(m, h5.linearReply, h5.imgReplyClose, h5.txtReply1, h5.txtReply2, h5.imgReply);

                    final Link link = new Gson().fromJson(m.getContent().toString(), Link.class);

//                            holder.txt2.setText(link.getLink());
                    //HIEN THI LINK PREVIEW
                    final String url = link.getLink();
                    h5.linearPreview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (context != null) {
                                if (url != null) {

                                    if (MyUtils.checkLinkToTask(url)) {//link task
                                        long taskId = MyUtils.getIdFromLinkTaskOrProject(url);
                                        Uri uri = Deeplink.getDeeplinkTask(taskId);
                                        Deeplink.openMyxteam(context, uri);
                                    } else if (MyUtils.checkLinkToProject(url)) {//link project
                                        long projectId = MyUtils.getIdFromLinkTaskOrProject(url);
                                        Uri uri = Deeplink.getDeeplinkProject(projectId);
                                        Deeplink.openMyxteam(context, uri);
                                    } else {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
                                            context.startActivity(browserIntent);
                                        } else {
                                            MyUtils.showToast(context, "You don't have any browser to open web page");
                                        }

                                    }

                                }
                            }
                        }
                    });


                    h5.linearBox.setVisibility(View.GONE);
                    //hien thi text
//                    h5.txtLink.setText(link.getText());
                    setOnClickMention(link.getText(), h5.txtLink);
                    h5.txtLink.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            h5.linear2.performLongClick();
                            return true;
                        }
                    });


                    //neu link da load truoc do thi ko load lai
                    final boolean isImage = MyUtils.isImageUrl(url);
                    if (link.isParse() || isImage) {//da parse hoac la link image
                        //Code for the UiThread
                        h5.pbPre.setVisibility(View.GONE);
                        h5.linearBox.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(link.getTitle())) {
                            h5.txtPre1.setVisibility(View.VISIBLE);
                            h5.txtPre1.setText(link.getTitle());
                        } else {
                            h5.txtPre1.setVisibility(View.GONE);
                        }

                        if (!TextUtils.isEmpty(link.getDescription())) {
                            h5.txtPre2.setVisibility(View.VISIBLE);
                            h5.txtPre2.setText(link.getDescription());
                        } else {
                            h5.txtPre2.setVisibility(View.GONE);
                        }

                        //phai co hoac title hoac description thi moi hien host
                        if (TextUtils.isEmpty(link.getTitle()) && TextUtils.isEmpty(link.getDescription())) {
                            h5.txtPre3.setVisibility(View.GONE);
                        } else {
                            String host = LinkUtils.getHostName(url);
                            if (!TextUtils.isEmpty(host)) {
                                h5.txtPre3.setVisibility(View.VISIBLE);
                                h5.txtPre3.setText(host);
                            } else {
                                h5.txtPre3.setVisibility(View.GONE);
                            }
                        }

                        if (context != null) {
                            try {
                                if (!TextUtils.isEmpty(link.getImageLink())) {
                                    //load hinh
                                    Glide.with(context)
                                            .load(link.getImageLink())
                                            .transform(new RoundedCorners(radius))
                                            .placeholder(R.drawable.no_media_small)
                                            .into(h5.imgPre);
                                } else {
                                    if (isImage) {
                                        Glide.with(context)
                                                .load(url)
                                                .transform(new RoundedCorners(radius))
                                                .placeholder(R.drawable.no_media_small)
                                                .into(h5.imgPre);
                                    } else {
                                        h5.imgPre.setVisibility(View.GONE);
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                isHaveError = true;
                            }
                        }
                    } else {

                        //chua parse thi chi hien thi link
                        h5.linearBox.setVisibility(View.GONE);

                    }

                    h5.linearPreview.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            h5.linear2.performLongClick();
                            return true;
                        }
                    });

                    break;

                case ContentTypeLayout.ACTION:
                    _6_ActionHolder h6 = (_6_ActionHolder) holder;

                    try {
                        JsonObject json = m.getContent();
                        if (json != null) {
                            Action action = new Action();

                            JsonElement item = json.get("actionType");
                            if (!item.isJsonNull()) {
                                action.setActionType(json.get("actionType").getAsString());
                                action.setData(json.getAsJsonObject("data").toString());
                                if (action != null) {

                                    //set canh trai hay canh phai neu la deletemessage
                                    /*if (action.getActionType().equals(Action.DELETE_MESSAGE)) {
                                        if(m.getUserIdAuthor().equalsIgnoreCase(ownerId)){
                                            //la minh thi canh phai
                                            h6.txt2.setGravity(Gravity.RIGHT);
                                        }else{
                                            //canh trai
                                            h6.txt2.setGravity(Gravity.LEFT);
                                        }
                                    }*/

                                    h6.txt2.setTextColor(ContextCompat.getColor(context, R.color.grey_500));
//                                    h6.txt2.setText(Html.fromHtml());
                                    String message = action.getActionMessage(context);
                                    setOnClickMention(message, h6.txt2);

                                    //action binh thuong thi ko click
                                    //neu la action pinmessage thi click vao dieu huong den tin
                                    switch (action.getActionType()) {

                                        //NEU LA REMINDER THI TAO LINK VA DIEU HUONG KHI CLICK VAO
                                        //neu la action pinmessage thi click vao dieu huong den tin
                                        case Action.PIN_MESSAGE:
                                        case Action.UNPIN_MESSAGE:
                                            final JSONObject data = new JSONObject(action.getData());
                                            if (data != null) {
                                                h6.txt2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {

                                                            if (data.has("message")) {

                                                                JSONObject message = data.getJSONObject("message");
                                                                String roomId = message.getString("roomId");
                                                                String chatLogId = message.getString("_id");


                                                                //tim vi tri chat log
                                                                int position = getPostionOfLog(chatLogId);
                                                                //neu ton tai thi scroll den
                                                                if (room != null && room.get_id().equalsIgnoreCase(roomId) && position >= 0) {
                                                                    Intent intent = new Intent(ChatActivity.ACTION_SCROLL_TO_POSITION_LOG);
                                                                    intent.putExtra(RoomLog.ROOM_LOG_ID, chatLogId);
                                                                    context.sendBroadcast(intent);
                                                                } else {
//                                                                    context.finish();
                                                                    //nguoc lai thi mo room chat
                                                                    MyUtils.openChatRoom(context, roomId, chatLogId);
                                                                    collapseBubble();
                                                                }
                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                            }
                                            break;
                                        case Action.PLAN_REMINDER:
                                            h6.txt2.setTextColor(ContextCompat.getColor(context, R.color.light_blue_500));
                                            final JSONObject j = new JSONObject(action.getData());
                                            if (j != null) {
                                                h6.txt2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {
                                                            String roomId = j.getString("roomId");
                                                            String chatLogId = j.getString("chatLogId");

                                                            //tim vi tri chat log
                                                            int position = getPostionOfLog(chatLogId);
                                                            //neu ton tai thi scroll den
                                                            if (room != null && room.get_id().equalsIgnoreCase(roomId) && position >= 0) {
                                                                Intent intent = new Intent(ChatActivity.ACTION_SCROLL_TO_POSITION_LOG);
                                                                intent.putExtra(RoomLog.ROOM_LOG_ID, chatLogId);
                                                                context.sendBroadcast(intent);
                                                            } else {
//                                                                context.finish();
                                                                //nguoc lai thi mo room chat
                                                                MyUtils.openChatRoom(context, roomId, chatLogId);
                                                                collapseBubble();
                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                            }
                                            break;


                                    }

                                }
                            } else {
                                logs.remove(position);
                                notifyItemRemoved(position);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case ContentTypeLayout.LOCATION:
                    _7_LocationHolder h7 = (_7_LocationHolder) holder;
                    h7.bindView(m, position);
                    break;
                /*case ContentTypeLayout.NEW_PAYMENT:
                    _8_9_PaymentHolder h8 = (_8_9_PaymentHolder) holder;

                    JSONObject data = null;
                    try {
                        data = new JSONObject(m.getContent().getAsJsonObject("data").toString());
                        String amount = data.optString("amount");
                        String senderUserName = data.optString("senderUserName");
//                            holder.txt2.setText("Nhận số tiền " + amount + " từ " + senderUserName + " thành công");ừ
                        h8.txt2.setText(context.getString(R.string.receive_money_x_to_y_success, amount, senderUserName));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
                case ContentTypeLayout.PAYMENT_STATUS:
                    _8_9_PaymentHolder h9 = (_8_9_PaymentHolder) holder;

                    JSONObject data2 = null;
                    try {
                        data2 = new JSONObject(m.getContent().getAsJsonObject("data").toString());
                        String amount2 = data2.optString("amount");
                        String destUserName = data2.optString("destUserName");
//                            holder.txt2.setText("Chuyển số tiền " + amount2 + " đến " + destUserName + " thành công");
                        h9.txt2.setText(context.getString(R.string.send_money_x_to_y_success, amount2, destUserName));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;*/
                case ContentTypeLayout.ITEM:
                    _10_ItemHolder h10 = (_10_ItemHolder) holder;

                    //HINH VA BAN DO THI KO CAN HIEN BACKGROUND
                    h10.linear2.setBackgroundResource(0);
                    final Item item = new Gson().fromJson(m.getContent().toString(), Item.class);
                    if (item != null) {
                        h10.txtItem1.setText(item.getItemName().trim());


                        //GIA GỐC
                        if (TextUtils.isEmpty(item.getItemOriginPrice())) {
                            h10.linearItemPriceSaleOff.setVisibility(View.GONE);
                        } else {
                            SpannableStringBuilder spannableStringBuilder = MyUtils.getStrikeText(
                                    MyUtils.getMoneyFormat(item.getItemOriginPrice()));
                            if (spannableStringBuilder != null) {
                                h10.txtItem4.setText(spannableStringBuilder);
                                h10.linearItemPriceSaleOff.setVisibility(View.VISIBLE);
                            } else {
                                h10.linearItemPriceSaleOff.setVisibility(View.GONE);
                            }
                        }

                        //GIA BAN
                        h10.txtItem2.setText(MyUtils.getMoneyFormat(item.getItemPrice()));
                        if (TextUtils.isEmpty(item.getItemPrice())) {//gia da giam
                            h10.txtItem2.setText(R.string.lienhe);
                            h10.txtItem3.setVisibility(View.GONE);
                        } else {//set gia
                            h10.txtItem2.setText(MyUtils.getMoneyFormat(item.getItemPrice()));
                            h10.txtItem3.setVisibility(View.VISIBLE);
                        }

                        int width = context.getResources().getDimensionPixelSize(R.dimen.dimen_120dp);
                        int height = context.getResources().getDimensionPixelSize(R.dimen.dimen_90dp);

                        String img = item.getItemImage();
                        if (!TextUtils.isEmpty(img)) {
                            try {
                                Glide.with(context)
                                        .load(img)
                                        .centerInside()
                                        .override(width, height)
                                        .into(h10.imgItem);
                            } catch (Exception e) {
                                e.printStackTrace();
                                isHaveError = true;
                            }
                        }


                    }

                    h10.linearItemPreview.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            h10.linear2.performLongClick();
                            return true;
                        }
                    });

                    break;

                case ContentTypeLayout.ALBUM:
                    _11_AlbumHolder h11 = (_11_AlbumHolder) holder;
                    h11.setState(m);
                    break;
                case ContentTypeLayout.VIDEO:
                    _12_VideoHolder h12 = (_12_VideoHolder) holder;
                    h12.setState(m);
                    break;
                case ContentTypeLayout.VOICE:
                    _13_VoiceHolder h13 = (_13_VoiceHolder) holder;

                    //KO CAN HIEN BACKGROUND
                    h13.linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);
                    final Voice voice = new Gson().fromJson(m.getContent().toString(), Voice.class);
                    if (voice != null) {

                        //Hien thi thong tin voice
                        //hien thi duration
                        int length = voice.getVoiceLength();
                        int minute = length / 60;
                        int second = length % 60;
                        String duration = MyUtils.getDurationString(minute, second);
                        h13.txtVoiceDuration.setText(duration);

                        //hien thi size
                        String size = MyUtils.getFileSizeString(voice.getSize());
                        h13.txtVoiceSize.setText(size);

                        //Neu voice dang phat thi hien thi UI
                        if (!TextUtils.isEmpty(linkPlaying) && voice.getLink().equals(linkPlaying)) {
                            txtDuration = h13.txtVoiceDuration;
                            //Dang playing
                            h13.imgVoicePlay.setImageResource(R.drawable.ic_pause_circle_filled_white_36dp);
                            h13.imgVoicePlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    positionPlaying = -1;
                                    releaseMediaPlayer();
                                    notifyItemChanged(position);
                                }
                            });
                        } else {
                            //Khong playing
                            h13.imgVoicePlay.setImageResource(R.drawable.ic_play_circle_filled_white_white_36dp);
                            h13.imgVoicePlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    linkPlaying = voice.getLink();
                                    txtDuration = h13.txtVoiceDuration;
                                    play(linkPlaying);
                                    notifyItemChanged(position);
                                    //vi tri cu neu dang play thi reset lai giao dien
                                    if (positionPlaying >= 0) {
                                        notifyItemChanged(positionPlaying);
                                    }
                                    positionPlaying = position;
                                }
                            });
                        }

                    }


                    break;
                case ContentTypeLayout.CONTACT:
                    _14_ContactHolder h14 = (_14_ContactHolder) holder;

                    //KO CAN HIEN BACKGROUND
                    h14.linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);
                    final Contact contact = new Gson().fromJson(m.getContent().toString(), Contact.class);
                    if (contact != null) {
                        //avatar
                        if (!TextUtils.isEmpty(contact.getAvatar())) {
                            try {
                                Glide.with(context)
                                        .load(contact.getAvatar())
                                        .override(avatarSizeMedium, avatarSizeMedium)
                                        .placeholder(R.drawable.ic_user_2)
                                        .error(R.drawable.ic_user_2)
                                        .transform(new CenterCrop(), new RoundedCorners(avatarSizeMedium / 2))
                                        .into(h14.imgContactAvatar);
                            } catch (Exception e) {
                                e.printStackTrace();
                                isHaveError = true;
                            }
                        } else {

                            //load theo name
                            if (!TextUtils.isEmpty(contact.getName())) {
                                ColorGenerator generator = ColorGenerator.MATERIAL;
                                int color = generator.getColor(contact.getName());

                                TextDrawable drawable = TextDrawable.builder()
                                        .buildRound(contact.getName().substring(0, 1), color);
                                h14.imgContactAvatar.setImageDrawable(drawable);
                            }
                        }


                        //ten, phone
                        h14.txtContactName.setText(contact.getName());
                        h14.txtContactPhone.setText(contact.getMobile());

                        //button message
                        h14.btnContactMessage.setOnClickListener(v -> {
                            //chat voi nguoi da co tai khoan mbn
                            if (MyUtils.checkInternetConnection(context)) {
                                MyUtils.chatWithUser(context, contact);
                            } else {
                                MyUtils.showThongBao(context);
                            }
                        });
                        h14.btnContactInfo.setOnClickListener(v -> {
                            if (MyUtils.checkInternetConnection(context)) {
                                UserInfo item1 = contact.convertUserInfo();
                                if (item1 != null) {
                                    //vào màn hình chi tiết user
                                    Intent intent = new Intent(context, ChatUserDetailActivity.class);
                                    intent.putExtra(UserInfo.USER_INFO, item1);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            } else {
                                MyUtils.showThongBao(context);
                            }
                        });
                    }


                    break;
                case ContentTypeLayout.PLAN:
                    _15_PlanHolder h15 = (_15_PlanHolder) holder;

                    //KO CAN HIEN BACKGROUND
                    h15.linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);
                    h15.btnComment.setText(R.string.comment);


                    //parse
                    PlanModel plan = new Gson().fromJson(m.getContent().toString(), PlanModel.class);
                    if (plan != null) {


                        //tieu de
                        h15.txt2Plan.setText(plan.getTitle());

                        //thoi gian
                        long time = plan.getTimeStamp();
                        h15.txt3Plan.setText(MyUtils.getTimePlan(time, languageSelected));

                        //thoi gian con lai
                        String diff = MyUtils.getTimeDifferent(time, context);
                        h15.txt1Plan.setText(diff);
                        //kiem tra da ket thuc chua
                        boolean finished = isFinished(plan);
                        if (finished) {
                            h15.txt1Plan.setBackgroundResource(R.drawable.bg_rectangle_blue_corner_disable);
                        } else {
                            h15.txt1Plan.setBackgroundResource(R.drawable.bg_rectangle_blue_corner);
                        }


                        final PlanModel mPlan = plan;
                        h15.linearPlan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (room != null) {
                                    //nguoi tao plan
                                    final Member member = getMember(m.getUserIdAuthor());
                                    mPlan.setOwner(member);
                                    mPlan.setMemberCount(room.getMemberCount());
                                    mPlan.setRoomId(room.get_id());
                                    mPlan.setChatLogId(m.get_id());


                                    //vao man hinh plan
                                    Intent intent = new Intent(context, MH03_Plan_Detail_Activity.class);
                                    intent.putExtra(PlanModel.PLAN_MODEL, mPlan);
                                    room.setLastLog(null);
                                    intent.putExtra(Room.ROOM, room);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        });
                        h15.linearPlan.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                h15.linear2.performLongClick();
                                return true;
                            }
                        });

                        //set progress bar multi color
                        ArrayList<ProgressItem> items = new ArrayList<>();
                        int numberVoted = plan.getResult().size();
                        int numberSum = 1;
                        if (room != null) {
                            numberSum = room.getMemberCount();
                            //text so luong yes, no, maybe
                            int yes = plan.getNumberVote(Vote.YES);
                            int no = plan.getNumberVote(Vote.NO);
                            int maybe = plan.getNumberVote(Vote.MAYBE);

                            String present = "";
                            if (yes > 0) {
                                present += context.getString(R.string.yes_x, yes + "");

                                ProgressItem part = new ProgressItem();
                                part.progressItemPercentage = yes * 100 / numberSum;
                                part.color = R.color.yes;
                                items.add(part);
                            }
                            if (no > 0) {
                                if (present.length() > 0) present += ", ";
                                present += context.getString(R.string.no_x, no + "");

                                ProgressItem part = new ProgressItem();
                                part.progressItemPercentage = no * 100 / numberSum;
                                part.color = R.color.no;
                                items.add(part);
                            }
                            if (maybe > 0) {
                                if (present.length() > 0) present += ", ";
                                present += context.getString(R.string.maybe_x, maybe + "");

                                ProgressItem part = new ProgressItem();
                                part.progressItemPercentage = maybe * 100 / numberSum;
                                part.color = R.color.maybe;
                                items.add(part);
                            }
                            h15.txt4Plan.setText(present);
                        }


                        //neu con du la mau grey
                        int nonVoted = numberSum - numberVoted;
                        if (nonVoted > 0) {
                            ProgressItem part = new ProgressItem();
                            part.progressItemPercentage = nonVoted * 100 / numberSum;
                            part.color = R.color.grey_300;
                            items.add(part);
                        }
                        h15.pbPlan.initData(items);
                        h15.pbPlan.invalidate();

                        //comment
                        ArrayList<Comment> list = plan.getComments();
                        if (list != null && list.size() > 0) {
                            h15.btnComment.setText(list.size() + "");
                        }
                        h15.btnComment.setOnClickListener(v -> {
                            if (room != null) {
                                //nguoi tao plan
                                final Member member = getMember(m.getUserIdAuthor());
                                mPlan.setOwner(member);
                                mPlan.setMemberCount(room.getMemberCount());
                                mPlan.setRoomId(room.get_id());
                                mPlan.setChatLogId(m.get_id());

                                Intent intent = new Intent(context, MH04_Comment_Activity.class);
                                intent.putExtra(PlanModel.PLAN_MODEL, mPlan);
                                room.setLastLog(null);
                                intent.putExtra(Room.ROOM, room);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });

                    }


                    //alert position background
                    if (position == positionNeedAlertBackground) {
                        //neu day la item lịch hẹn thì mở lịch hẹn
                        if (m.getType().equals(ContentType.PLAN)) {
                            h15.linearPlan.performClick();
                        }
                    }


                    break;

                case ContentTypeLayout.MEETING:
                    _16_MeetingHolder h16 = (_16_MeetingHolder) holder;

                    //KO CAN HIEN BACKGROUND
//                    h16.linear2.setBackgroundResource(R.drawable.bg_rectangle_grey_corner);

                    //parse
                    Meeting meeting = new Gson().fromJson(m.getContent().toString(), Meeting.class);
                    if (meeting != null) {
                        h16.bind(meeting);
                    }
                    break;

                case ContentTypeLayout.POLL:

                    break;
                default:
                    break;

            }


        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setPercentImageUploading(String path, int percent) {
        if (!TextUtils.isEmpty(path)) {
            addPercent(path, percent);
            if (uploadingImageHolder != null) {
                uploadingImageHolder.setPercent(percent);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void setImageHolderAlbumUploading(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (uploadingAlbumHolder != null &&
                    uploadingAlbumHolder.adapter != null
            ) {
                uploadingAlbumHolder.adapter.setPathUploading(path);
            }
        }
    }

    public void setPercentAlbumUploading(String path, int percent) {
        if (!TextUtils.isEmpty(path)) {
            addPercent(path, percent);
            if (uploadingAlbumHolder != null &&
                    uploadingAlbumHolder.adapter != null &&
                    uploadingAlbumHolder.adapter.holderUploading != null
            ) {
                uploadingAlbumHolder.adapter.holderUploading.setPercent(path, percent);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////
    private Map<String, Integer> mapPercent = new HashMap<String, Integer>();

    public void initMapPercent() {
        if (mapPercent != null) {
            mapPercent.clear();
        } else {
            mapPercent = new HashMap<String, Integer>();
        }
    }

    private void addPercent(String path, int percent) {
        mapPercent.put(path, percent);
    }

    private int getPercent(String path) {
        if (mapPercent.containsKey(path)) {
            return mapPercent.get(path);
        }
        return 0;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void collapseBubble() {
        if (isFromWidget) {
            EventBus.getDefault().post(new MenuEvent(true));
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


}