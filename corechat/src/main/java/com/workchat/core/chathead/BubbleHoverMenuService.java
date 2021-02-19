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
package com.workchat.core.chathead;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.workchat.core.chathead.events.AddRoomEvent;
import com.workchat.core.chathead.events.CloseRoomEvent;
import com.workchat.core.chathead.events.MenuEvent;
import com.workchat.core.chathead.events.SelectRoomEvent;
import com.workchat.core.chathead.events.StopService;
import com.workchat.core.chathead.events.UpdateNumberEvent;
import com.workchat.core.chathead.theming.HoverTheme;
import com.workchat.core.chathead.ui.ChatRoomContent;
import com.workchat.core.chathead.ui.RecentChatRoomContent;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.mattcarroll.hover.HoverMenu;
import io.mattcarroll.hover.HoverView;
import io.mattcarroll.hover.OnExitListener;
import io.mattcarroll.hover.SideDock;
import io.mattcarroll.hover.homewatcher.HomeWatcher;
import io.mattcarroll.hover.homewatcher.OnHomePressedListener;
import io.mattcarroll.hover.window.HoverMenuService;
import io.mattcarroll.hover.window.WindowViewController;

/**
 * Demo {@link HoverMenuService}.
 */
public class BubbleHoverMenuService extends Service {

    private static final String TAG = "DemoHoverMenuService";

    public static void showFloatingMenu(Context context, AddRoomEvent event) {
        boolean isCanOverlay = MyUtils.isCanOverlay(context);
//        MyUtils.showToastDebug(context, "isCanOverlay = " + isCanOverlay);


        if (context != null && isCanOverlay) {

            if(!MyUtils.isServiceRunning(BubbleHoverMenuService.class, context)){
                Intent intent = new Intent(context, BubbleHoverMenuService.class);
                intent.putExtra(AddRoomEvent.ADD_ROOM_EVENT, event);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent);
                } else {
                    context.startService(intent);
                }
            }else{
                //chi add room
                EventBus.getDefault().post(event);
            }

        }
    }

    private ReorderingSectionHoverMenu mDemoHoverMenu;

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
        initSpaceTabview();
//        initNotification();
        EventBus.getDefault().register(this);
        initHomeWatcher();
    }

    HomeWatcher mHomeWatcher;
    private void initHomeWatcher(){
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                MyUtils.hideKeyboardForce(getApplicationContext());
                // do something here...
                collapse(true);
            }
            @Override
            public void onHomeLongPressed() {
                collapse(true);
            }
        });
        mHomeWatcher.startWatch();
    }

    ///////////////////////////////////////////////////////////////////////////////
    public static final int MAX_TAB = 5;
    private int avatarSize;
    private int spaceTabview;
    private int padding;

    private void initSpaceTabview() {
        int tab = MAX_TAB + 1;

        //toi da man hinh co MAX_TAB
        int width = MyUtils.getScreenWidth(this);
        avatarSize = getResources().getDimensionPixelSize(R.dimen.hover_tab_size);

//        int spaceSum = width - (avatarSize * tab);

        //cach 15%
        spaceTabview = avatarSize + (int) (avatarSize * 0.15f);
        padding = getResources().getDimensionPixelOffset(R.dimen.chathead_border_width_icon);


    }
    ///////////////////////////////////////////////////////////////////////////////


    @Override
    public void onDestroy() {
        if (mIsRunning) {
            mHoverView.removeFromWindow();
            mIsRunning = false;
        }

        if(mHomeWatcher!=null){
            mHomeWatcher.stopWatch();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Called in Android UI's main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull MenuEvent event) {
        collapse(event.isCollapse());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull StopService event) {
        if (event.isStop()) {
//            MyUtils.showToastDebug(getApplicationContext(), "stopSelf()");
            stopSelf();
        }
    }



    HoverView hoverView;

    private void collapse(boolean isCollapse) {
        if (hoverView != null) {
            if (isCollapse) {
                hoverView.collapse();
            } else {
                hoverView.expand();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull HoverTheme newTheme) {
//        mDemoHoverMenu.setTheme(newTheme);
    }




    private HoverMenu createHoverMenu() {
        /*try {
            mDemoHoverMenu = new DemoHoverMenuFactory().createDemoMenuFromCode(getContextForHoverMenu());
//            mDemoHoverMenuAdapter = new DemoHoverMenuFactory().createDemoMenuFromFile(getContextForHoverMenu());
            return mDemoHoverMenu;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        return mDemoHoverMenu = new ReorderingSectionHoverMenu(getApplicationContext());
    }


    private class ReorderingSectionHoverMenu extends HoverMenu {
        private final Context mContext;
        private final List<Section> mSections = new ArrayList<>();


        ReorderingSectionHoverMenu(@NonNull Context context) {
            mContext = context;
            insertTab(0);
            if (currentEvent != null) {

                //room moi them vao thi chon luon
                currentEvent.setFromRecent(true);
                insertTab(currentEvent);

            }
            /*insertTab(1);
            insertTab(2);
            insertTab(3);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMutationSteps.get(mNextStep).run();
                    ++mNextStep;

                    if (mNextStep < mMutationSteps.size()) {
                        new Handler(Looper.getMainLooper()).postDelayed(this, 1000);
                    }
                }
            }, 5000);*/
        }

        private View createTabView1(String roomAvatar) {
            /*ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(R.drawable.tab_background);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return imageView;*/

            ImageView imageView = new ImageView(mContext);
//            imageView.setImageResource(R.drawable.tab_background);
//            imageView.setBackgroundResource(R.drawable.profile_decorator);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            if (!TextUtils.isEmpty(roomAvatar)) {

                //padding have circle border
                imageView.setBackgroundResource(R.drawable.bg_circle_white_border);
                imageView.setPadding(padding, padding, padding, padding);

                //load image
                Glide.with(getApplicationContext())
                        .load(roomAvatar)
                        .override(avatarSize, avatarSize)
                        .transform(new CenterCrop(), new RoundedCorners(avatarSize / 2))
                        .placeholder(R.drawable.ic_user_2)
                        .into(imageView);
                imageView.setTag(roomAvatar);

            } else {

                //recent icon
                int size = avatarSize + padding * 4;
                Glide.with(getApplicationContext())
                        .load(R.drawable.chathead_workchat)
                        .override(size, size)
                        .into(imageView);
                imageView.setTag(roomAvatar);
            }
            return imageView;
        }

        private View createTabView(String roomAvatar) {

            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_bubble_icon, null, false);
            ImageView img = view.findViewById(R.id.avatar);
            TextView txt = view.findViewById(R.id.number);


            if (!TextUtils.isEmpty(roomAvatar)) {
                //tao moi
                txt.setText("1");
                txt.setVisibility(View.VISIBLE);

                //padding have circle border
                img.setBackgroundResource(R.drawable.bg_circle_white_border);
                img.setPadding(padding, padding, padding, padding);

                //load image
                Glide.with(getApplicationContext())
                        .load(roomAvatar)
                        .override(avatarSize, avatarSize)
                        .transform(new CenterCrop(), new RoundedCorners(avatarSize / 2))
                        .placeholder(R.drawable.ic_user_2)
                        .into(img);
                img.setTag(roomAvatar);

            } else {
                txt.setVisibility(View.GONE);

                //recent icon
                int size = avatarSize + padding * 4;
                Glide.with(getApplicationContext())
                        .load(R.drawable.chathead_workchat)
                        .override(size, size)
                        .into(img);
                img.setTag(roomAvatar);
            }

            return view;
        }


        @Override
        public String getId() {
            return "reorderingmenu";
        }

        @Override
        public int getSectionCount() {
            return mSections.size();
        }

        @Nullable
        @Override
        public Section getSection(int index) {
            return mSections.get(index);
        }

        @Nullable
        @Override
        public Section getSection(@NonNull SectionId sectionId) {
            for (Section section : mSections) {
                if (section.getId().equals(sectionId)) {
                    return section;
                }
            }
            return null;
        }

        @NonNull
        @Override
        public List<Section> getSections() {
            return new ArrayList<>(mSections);
        }

        private void delay() {
//            SystemClock.sleep(300);
        }

        public void insertTab(int position) {
            if (mSections.size() == 0) {
                String id = "recentTabId";//Integer.toString(mSections.size());
                mSections.add(position, new Section(
                        new SectionId(id, spaceTabview),
                        createTabView(""),
                        new RecentChatRoomContent(getApplicationContext())
                ));
                notifyMenuChanged();
            }
        }


        public void insertTab(AddRoomEvent event) {

            boolean exists = isContainSection(event.getRoomId());
            if (!exists) {

                //neu dang dong thi ko them vao
                /*if(!hoverView.isExpanded){
                    return;
                }*/

                delay();

                //toi da 5 tab, neu vuot qua thi xoa cu nhat
                if (mSections.size() >= MAX_TAB) {
                    //remove ptu ke nut chat gan day
                    removeTab(mSections.size() - 2);
                    delay();
                }

                SectionId sectionId = new SectionId(event.getRoomId(), spaceTabview);
                Section section = null;
                mSections.add(0, section = new Section(
                        sectionId,
                        createTabView(event.getRoomAvatar()),
                        new ChatRoomContent(getApplicationContext(), event)
//                        new HoverMenuScreen(mContext, "Screen " + event.getRoomId())
                ));
                notifyMenuChanged();

                //co tin den la chon
                if (event.isFromRecent()) {
//                    SystemClock.sleep(100);
                    selectTab(section);
                }

                //update room avatar
                /*View view = section.getTabView();
                if(view instanceof ImageView){
                    ImageView imageView = (ImageView)view;
                    String roomAvatar = "https://myxteam.blob.core.windows.net/chat/5e53717745e8750a44e19af5/36/80eaee17-6fb9-4897-8907-f665ea619d07/o/albumart_%7B60c0d5d2-2a8b-4309-90d9-f434572a6f22%7D_large.jpg";
                    int padding = getResources().getDimensionPixelOffset(R.dimen.chathead_border_width_icon);
                    if (!TextUtils.isEmpty(roomAvatar)) {

                        //padding have circle border
                        imageView.setBackgroundResource(R.drawable.bg_circle_white_border);
                        imageView.setPadding(padding, padding, padding, padding);

                        //load image
                        Glide.with(getApplicationContext())
                                .load(roomAvatar)
                                .override(avatarSize, avatarSize)
                                .transform(new CenterCrop(), new RoundedCorners(avatarSize / 2))
                                .placeholder(R.drawable.ic_user_2)
                                .into(imageView);
                        imageView.setTag(roomAvatar);

                    }
                }*/

            } else {
                //select tab
                if (event.isFromRecent()) {
                    int position = getPositionInSections(event.getRoomId());
                    if (position >= 0) {
                        Section section = mSections.get(position);
                        selectTab(section);
                    }
                }
            }
        }

        private void selectTab(Section section) {
            if (hoverView != null) {
                hoverView.setmSelectedSection(section);
            }
        }

        private void removeTab(String roomId) {
            if (!TextUtils.isEmpty(roomId)) {
                //neu chi con 1 item thi xoa luon chat gan day
                /*if (mSections.size() <= 2) {
                    stopSelf();
                } else {

                }*/

                int position = getPositionInSections(roomId);
                if (position >= 0) {
                    delay();
                    removeTab(position);
                }
            }
        }

        private void removeTab2(int position) {
            if (position < mSections.size()) {
                //remove
                // If the removed section was the selected section then select a new section.
                if(hoverView!=null){
                    Section item = mSections.get(position);
                    if (item.getId().equals(hoverView.getmSelectedSectionId())) {
                        int newSelectionIndex = 0;
                        if (position - 1 < mSections.size() - 1) {
                            newSelectionIndex = position - 1;
                        } else {
                            newSelectionIndex = mSections.size() - 1;
                        }

                        //todo Mr.Phuong add this row
                        if (newSelectionIndex < 0) newSelectionIndex = 0;

                        //select tab
                        selectTab(mSections.get(newSelectionIndex));
                    }
                }

                //remove
                mSections.remove(position);
                notifyMenuChanged();


            }
        }
        private void removeTab(int position) {

            //neu dang dong thi ko xoa
            /*if(!hoverView.isExpanded){
                return;
            }*/

            if (position < mSections.size()) {
                //remove
                mSections.remove(position);
                notifyMenuChanged();

            }
        }



        private void moveTab(int startPosition, int endPosition) {
            Section section = mSections.remove(startPosition);
            mSections.add(endPosition, section);
            notifyMenuChanged();
        }

        private boolean isContainSection(String roomId) {
            boolean isExists = false;
            for (int i = 0; i < mSections.size(); i++) {
                Section item = mSections.get(i);
                if (item.getId().toString().equals(roomId)) {
                    isExists = true;
                    break;
                }
            }
            return isExists;
        }

        private int getPositionInSections(String roomId) {
            int position = -1;
            for (int i = 0; i < mSections.size(); i++) {
                Section item = mSections.get(i);
                if (item.getId().toString().equals(roomId)) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        private void updateNumber(UpdateNumberEvent event){
            if(!TextUtils.isEmpty(event.getRoomId())) {
                int numberUnread = event.getNumber();

                int position = getPositionInSections(event.getRoomId());
                if (position >= 0) {
                    Section section = mSections.get(position);
                    View v = section.getTabView();
                    TextView txt = v.findViewById(R.id.number);

                    if (numberUnread == 0) {
                        //chi hien icon
                        txt.setVisibility(View.GONE);
                    } else {
                        //hien icon va so
                        txt.setVisibility(View.VISIBLE);
                        txt.setText(String.valueOf(numberUnread));
                    }
                }


            }
        }
    }


    // Called in Android UI's main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull AddRoomEvent event) {
        if (mDemoHoverMenu != null) {
            mDemoHoverMenu.insertTab(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull CloseRoomEvent event) {
        if (mDemoHoverMenu != null) {
            mDemoHoverMenu.removeTab(event.getRoomId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull SelectRoomEvent event) {
        if (mDemoHoverMenu != null) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull UpdateNumberEvent event) {
        if (mDemoHoverMenu != null) {
            mDemoHoverMenu.updateNumber(event);
        }

    }


    private AddRoomEvent currentEvent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!mIsRunning) {
            Log.d(TAG, "onStartCommand() - showing Hover menu.");
            mIsRunning = true;
            initHoverMenu(intent);
        }


        if (intent != null) {
            MyUtils.showToastDebug(getApplicationContext(), "onStartCommand");
            Bundle b = intent.getExtras();
            AddRoomEvent event = b.getParcelable(AddRoomEvent.ADD_ROOM_EVENT);
            if (event != null) {
                currentEvent = event;
                if (mDemoHoverMenu != null) {
                    mDemoHoverMenu.insertTab(event);
                }
            }
        }
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    /**
     * Hook method for subclasses to take action when the user exits the HoverMenu. This method runs
     * just before this {@code HoverMenuService} calls {@code stopSelf()}.
     */
    protected void onHoverMenuExitingByUserRequest() {
        // Hook for subclasses.
    }
    private HoverView mHoverView;
    private boolean mIsRunning;
    private OnExitListener mOnMenuOnExitListener = new OnExitListener() {
        @Override
        public void onExit() {
            Log.d(TAG, "Menu exit requested. Exiting.");
            mHoverView.removeFromWindow();
            onHoverMenuExitingByUserRequest();
            stopSelf();
        }
    };
    private void initHoverMenu(@NonNull Intent intent) {
        mHoverView = HoverView.createForWindow(
                this,
                new WindowViewController((WindowManager) getSystemService(Context.WINDOW_SERVICE)),
                new SideDock.SidePosition(SideDock.SidePosition.RIGHT, 0.5f)
        );
        mHoverView.setOnExitListener(mOnMenuOnExitListener);
        mHoverView.addToWindow();

        onHoverMenuLaunched(intent, mHoverView);
    }

    protected Context getContextForHoverMenu() {
        return new ContextThemeWrapper(this, R.style.AppTheme);
    }
    protected void onHoverMenuLaunched(@NonNull Intent intent, @NonNull HoverView hoverView) {
        this.hoverView = hoverView;
        this.hoverView.setMenu(createHoverMenu());
        this.hoverView.collapse();

    }

    //region NOTIFICATION IN ANDROID 8 - ANDROID O
    public void initNotification() {
        Notification foregroundNotification = getForegroundNotification();
        if (null != foregroundNotification) {
            int notificationId = getForegroundNotificationId();
            startForeground(notificationId, foregroundNotification);
        }
    }

    protected int getForegroundNotificationId() {
        // Subclasses should provide their own notification ID if using a notification.
        return 1233;
    }

    public static final String NOTIFY_GROUP_BUBBLE = "com.workchat.NOTIFY_GROUP_BUBBLE";

    protected Notification getForegroundNotification() {
        // If subclass returns a non-null Notification then the Service will be run in
        // the foreground.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = getPackageName() + ".channel.bubble";
            String channelName = "Workchat Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);


            Intent notificationIntent = new Intent(this, NotificationChatheadActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(io.mattcarroll.hover.R.drawable.ic_workchat_notify)
                    .setContentTitle(getString(io.mattcarroll.hover.R.string.background_notify_chathead))
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setGroup(NOTIFY_GROUP_BUBBLE)
                    .build();
            return notification;
        } else {
            return new Notification();
        }
    }
    //endregion NOTIFICATION IN ANDROID 8 - ANDROID O
}
