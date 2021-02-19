package com.workchat.core.chat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cipolat.superstateview.SuperStateView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.workchat.core.channel.MH06_PinMessageActivity;
import com.workchat.core.chat.link_preview_2.LinkPreviewCallback;
import com.workchat.core.chat.link_preview_2.LinkUtils;
import com.workchat.core.chat.link_preview_2.SourceContent;
import com.workchat.core.chat.link_preview_2.TextCrawler;
import com.workchat.core.chat.locations.MyLocation;
import com.workchat.core.chat.locations.SearchLocationActivity;
import com.workchat.core.chat.socketio.SocketState;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.event.RoomLogEvent_Chat;
import com.workchat.core.event.RoomLogEvent_IsViewed;
import com.workchat.core.event.RoomLogEvent_Reply;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.Action;
import com.workchat.core.models.chat.AlbumItem;
import com.workchat.core.models.chat.Contact;
import com.workchat.core.models.chat.ContentType;
import com.workchat.core.models.chat.Image;
import com.workchat.core.models.chat.Item;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.OriginMessage;
import com.workchat.core.models.chat.Permission;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.chat.SasModel;
import com.workchat.core.models.realm.Project;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.plan.CalendarManager;
import com.workchat.core.plan.MH01_Create_Plan;
import com.workchat.core.plan.PlanModelLocal;
import com.workchat.core.utils.FileUtil;
import com.workchat.core.utils.FileUtils;
import com.workchat.core.utils.ListenableInputStream;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.BuildConfig;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;
import io.github.memfis19.annca.internal.utils.Utils;
import io.realm.Realm;
import io.socket.client.Ack;
import io.socket.client.Socket;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ChatFragment extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static boolean isExists = false;
    private Context context = this;
    private Activity activity = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title; //set ten doi phuong, dua vao list member, ko quan tam roomName

    @BindView(R2.id.linearParent)
    LinearLayout linearParent;
    @BindView(R2.id.imgEmoji)
    ImageView imgEmoji;
    @BindView(R2.id.editText1)
    EmojiconEditText txt;
    @BindView(R2.id.linearSend)
    LinearLayout linearSend;
    @BindView(R2.id.linearLike)
    LinearLayout linearLike;


    private EmojIconActions emojIcon;
    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    @BindView(R2.id.txtInternetState)
    TextView txtInternetState;
    @BindView(R2.id.txtSocketState)
    TextView txtSocketState;
    @BindView(R2.id.linearCamera)
    LinearLayout linearCamera;
    @BindView(R2.id.linearPicture)
    LinearLayout linearPicture;
    @BindView(R2.id.linearAdd)
    LinearLayout linearAdd;
    @BindView(R2.id.imgAdd)
    ImageView imgAdd;


    @Nullable
    @BindView(R2.id.emptyView)
    SuperStateView emptyView;
    @BindView(R2.id.progressWheel)
    ProgressBar progressWheel;
    @BindView(R2.id.progressWheelCenter)
    ProgressBar progressWheelCenter;
    private String oldContent = "";

    private void loadMore() {
        if (isFindPinMessage) {//load more tu tin Pin
            getRoomPreviousLogs(adapter.getLastLogId_Top());
        } else {//load danh sach chat
            loadMore = true;
            loadHistory();
        }
    }

    private boolean isKeyboarShow = false;

    private void initEmoji() {
        emojIcon = new EmojIconActions(context, linearParent, txt, imgEmoji);
        emojIcon.setIconsIds(R.drawable.ic_keyboard_grey_500_48dp, R.drawable.ic_tag_faces_grey_500_48dp);
        emojIcon.ShowEmojIcon();

        //mo keyboard thi an cac nut, an keyboard thi hien cac nut
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                linearCamera.setVisibility(View.GONE);
                linearPicture.setVisibility(View.GONE);
                isKeyboarShow = true;
                //an button scroll
                if (btnScroll != null) {
                    btnScroll.setVisibility(View.GONE);
                }
                //scroll ve cuoi
                btnScroll.performClick();
            }

            @Override
            public void onKeyboardClose() {
                linearCamera.setVisibility(View.VISIBLE);
                linearPicture.setVisibility(View.VISIBLE);
                isKeyboarShow = false;
            }
        });
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processChatRoom(intent);
    }*/

    ///////////////////////////////////////////////////////////////////////
    private UserChatCore owner;
    private UserChatCore guest;
    private TinyDB db;
    public static String roomId = "";
    private String jsonCreateRoom = "";
    private String channelId = "";
    private String chatLogId = "";
    private Project project;

    public static final String LIST_PARAMS_DEEP_LINKING = "LIST_PARAMS_DEEP_LINKING";
    private ArrayList<String> params = new ArrayList<>();

    private void resetLayout(){
        /*title.setText("       ");
        adapter = new ChatAdapter(this, owner.get_id(), false, realm);
        rv.setAdapter(adapter);*/

    }

    private void processChatRoom(Intent intent) {

        if (intent != null) {

            Bundle b = intent.getExtras();
            if (b != null) {

                resetLayout();


                //Chat with support
                isSupport = b.getBoolean(UserChatCore.IS_CHAT_WITH_SUPPORT, false);

                //CHAT WITH USER
                guestId = b.getString(UserChatCore.USER_ID, "");


                //forward, xu ly sau khi room da load xong
                isForward = b.getBoolean(RoomLog.IS_FORWARD, false);


                //kiem tra vao tu deep link
                params = b.getStringArrayList(LIST_PARAMS_DEEP_LINKING);
                if (params != null && params.size() > 0) {

                } else {
                    //ARTICLE
                    Parcelable obj = null;

                    //USER
                    obj = b.getParcelable(UserChatCore.USER_MODEL);
                    if (obj != null) {
                        guest = (UserChatCore) obj;
                        if (guest != null) {
                            guestId = guest.get_id();
                        }
                    }

                    //LOAD LAI GROUP ID
                    roomId = b.getString(Room.ROOM_ID, "");


                    chatLogId = b.getString(RoomLog.ROOM_LOG_ID, "");

                    //TAO MOI GROUP CHAT
                    jsonCreateRoom = b.getString(UserChatCore.JSON_CREATE_ROOM, "");
                    if (!TextUtils.isEmpty(jsonCreateRoom)) {

                    }
                    //chuyen thanh ham nay
                    channelId = b.getString(Room.CHANNEL_ID, "");

                    //vao tu chat gan day
                    room = b.getParcelable(Room.ROOM);
                    if (room != null) {
                        roomId = room.get_id();
                    }

                    //project
                    project = b.getParcelable(Project.PROJECT_MODEL);


                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////
    private Socket socket;

    /**
     * Khi mo truc tiep tu app MBN qua thi phai enable chuc nang lang nghe cua socket
     */
    /*private void initSocketListener() {
        //socket
        if (!ChatApplication.Companion.isSetupSocket() || ChatApplication.Companion.getSocket() == null) {
            UserMBN user = ChatApplication.Companion.getUser();
            if (user != null) {
                ChatApplication.Companion.setupSocket(user);
            }
        }
    }*/


    /**
     * Khi resume thi kiem tra bien nay, neu true: thi ko goi ham initChatRoom, nguoc lai thi goi
     */
    private boolean isSelectImageOrFile = false;

    private void initSocketOnly() {
        //link den socket trong application
        socket = ChatApplication.Companion.getSocket();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatApplication.Companion.getSocketInitIfNull();
            }
        }, 1000);
    }

    private void initSocketConnection() {
        //link den socket trong application
        socket = ChatApplication.Companion.getSocket();
        if (socket == null) {
            ChatApplication.Companion.getSocketInitIfNull();
        }


        //lay lai danh sach
        initChatRoom();

    }

    private boolean isSocketConnected() {
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && socket.connected()) {
            MyUtils.log("socket connected true");
            return true;
        } else {
            MyUtils.log("socket connected false");
            return false;
        }
    }

    TextCrawler textCrawler;

    @BindView(R2.id.imgPin)
    ImageView imgPin;
    @BindView(R2.id.fab)
    ImageView btnScroll;
    @BindView(R2.id.linearChat)
    LinearLayout linearChat;


    @BindView(R2.id.btnChatWithAdmin)
    Button btnChatWithAdmin;
    @BindView(R2.id.linearChannelInfo)
    LinearLayout linearChannelInfo;
    @BindView(R2.id.imgChannelAvatar)
    ImageView imgChannelAvatar;
    @BindView(R2.id.imgChannelClose)
    ImageView getImgChannelClose;
    @BindView(R2.id.txtChannelName)
    TextView txtChannelName;

    private boolean isForward = false;
    private Realm realm;

    private long startGlobal = SystemClock.elapsedRealtime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        isExists = true;
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        //state
        if (!MyUtils.checkInternetConnection(context)) {
            if (emptyView != null) {
                emptyView.setImageState(R.drawable.ic_wifi);
                emptyView.setTitleText(getString(R.string.khongCoInternet));
            }
        } else {
            if (emptyView != null) {
                emptyView.setImageState(R.drawable.ic_chat_square);
                emptyView.setTitleText(getString(R.string.empty_list_chat_notify));
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////

        realm = ChatApplication.Companion.getRealmChat();
        db = new TinyDB(this);
        owner = ChatApplication.Companion.getUser();
        if (owner == null || TextUtils.isEmpty(owner.get_id())) {
            finish();
        }


        setupRecyclerView();

        /////////////////////////////////////////////////////////////////////////////////////
        processChatRoom(getIntent());
        //khoi tao socket
        initSocketConnection();
        //hien thi trang thai ban dau
        if (socket == null || (socket != null && !socket.connected())) {
            stateOffline();
        }
        /////////////////////////////////////////////////////////////////////////////////////
        if (BuildConfig.DEBUG) {
            txtSocketState.setVisibility(View.GONE);
        } else {
            txtSocketState.setVisibility(View.GONE);
        }


        imgPin.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dong keyboard
                MyUtils.hideKeyboard(activity);
                finish();
            }
        });


        textCrawler = new TextCrawler();
        //KEYBOARD SHOW/HIDE
        linearParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = linearParent.getRootView().getHeight() - linearParent.getHeight();
                if (heightDiff > MyUtils.dpToPx(context, 200)) { // if more than 200 dp, it's probably a keyboard...
                    // ... do something here
                    //Make changes for keyboard visible
//                            MyUtils.showToast(context, "Keyboard visible");
                    if (!isNeedExpand) {
                        imgAdd.setRotation(0);
                        linearExpand.setVisibility(View.GONE);
                        linearRecorder.setVisibility(View.GONE);
                    }
                } else {
                    //Make changes for Keyboard not visible
//                            MyUtils.showToast(context, "Keyboard closed");
                    //dong neu van con text thi phai hien nut send
//                    imgAdd.setRotation(0);
//                    linearExpand.setVisibility(View.GONE);
                }
            }
        });

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    linearSend.setVisibility(View.VISIBLE);
                    linearLike.setVisibility(View.GONE);
                } else {
                    linearSend.setVisibility(View.GONE);
                    linearLike.setVisibility(View.VISIBLE);
                }
            }
        });


        txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isNeedExpand = false;
            }

        });


        linearSend.setOnClickListener(view -> {
            String message = txt.getText().toString().trim();
            sendMessage(message);
        });
        linearLike.setOnClickListener(v -> {
            String txtLike = "\uD83D\uDC4D";
            sendMessage(txtLike);
        });


        linearCamera.setOnClickListener(view -> {
            //camera
            if (checkPermission()) {
                if (MyUtils.checkInternetConnection(context)) {
                    isSelectImageOrFile = true;
//                    startActivityForResult(getPickImageChooserIntent(), 200);
//                    takePictureIntent();
                    takeOrRecordVideo2();
                } else {
                    MyUtils.showThongBao(context);
                }
            } else {
                requestPermission(PERMISSION_REQUEST_CAMERA_AND_STORAGE_1);
            }
        });

        //select image
        linearPicture.setOnClickListener(v -> {
            if (checkPermission()) {
                if (MyUtils.checkInternetConnection(context)) {
                    isSelectImageOrFile = true;
//                    Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
//                    startActivityForResult(i, REQUEST_CODE_PICK_PICTURE);
                    FilePickerBuilder.getInstance()
                            .setMaxCount(20)
//                            .setSelectedFiles(photoPaths)
//                            .setActivityTheme(R.style.FilePickerTheme)
                            .setActivityTitle("Select media")
                            .enableVideoPicker(true)
                            .enableCameraSupport(true)
                            .showGifs(false)
                            .showFolderView(true)
                            .enableSelectAll(true)
                            .enableImagePicker(true)
//                            .setCameraPlaceholder(R.drawable.custom_camera)
                            .withOrientation(Orientation.PORTRAIT_ONLY)
                            .pickPhoto(activity, REQUEST_CODE_PICK_PICTURE);
                } else {
                    MyUtils.showThongBao(context);
                }
            } else {
                requestPermission(PERMISSION_REQUEST_CAMERA_AND_STORAGE_2);
            }
        });


        imgPin.setOnClickListener(v -> {
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

        ////////////////////////////////////////////////////////////////////////////////////////////
        ((View) btnScroll).setVisibility(View.GONE);
        btnScroll.setOnClickListener(v -> {
            btnScroll.setVisibility(View.GONE);

            if (adapter != null && adapter.getItemCount() > 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /*int position = adapter.getItemCount() - 1;
                        if (position >= 0) {
                            rv.scrollToPosition(position);
                        }*/
                        rv.scrollToPosition(0);

                    }
                }, 200);
            }

        });

        ////////////////////////////////////////////////////////////////////////////////////////////


        initEmoji();

        registerReceiver();

        ///menu bottom
        intExpandItem();

        //recoder
        initRecorder();

        //reply ui
        setReplyUI(null);

        //Tao connect google play service
        requestPermissionLocation();

        progressWheel.setVisibility(View.GONE);
        progressWheelCenter.setVisibility(View.GONE);

        MyUtils.howLong(startGlobal, "oncreate finish");
        requestStorage();
        checkDrawOverlay();

    }

    private void requestStorage() {
        boolean isOK = MyUtils.haveStoragePermission(context);
        if (!isOK) {
            ActivityCompat.requestPermissions(ChatFragment.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
    }


    private void loadPinData() {
        if (isSocketConnected() && room != null) {

            //Load tu tren xuong duoi, moi nhat da sap xep tren dau
            final String id = "";
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
                obj.put("lastLogId", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("getPinLogs", obj, (Ack) args -> runOnUiThread(() -> {
                final ArrayList<RoomLog> list = RoomLog.parseListRoomLog(context, args);
                if (list != null && list.size() > 0) {
                    imgPin.setVisibility(View.VISIBLE);
                } else {
                    imgPin.setVisibility(View.INVISIBLE);
                }
            }));

        }
    }


    /**
     * RecycleListener that completely clears the {@link GoogleMap}
     * attached to a row in the RecyclerView.
     * Sets the map type to {@link GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    private RecyclerView.RecyclerListener mRecycleListener = new RecyclerView.RecyclerListener() {

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder instanceof ChatAdapter._7_LocationHolder) {
                ChatAdapter._7_LocationHolder mapHolder = (ChatAdapter._7_LocationHolder) holder;
                if (mapHolder != null && mapHolder.map != null) {
                    // Clear the map and free up resources by changing the map type to none.
                    // Also reset the map when it gets reattached to layout, so the previous map would
                    // not be displayed.
                    mapHolder.map.clear();
                    mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
                }
            }

        }
    };

    private LinearLayoutManager linearLayout;
    private void setupRecyclerView() {
        if (owner != null) {
            adapter = new ChatAdapter(this, owner.get_id(), false, realm);
            if (rv != null) {
                linearLayout = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
//                linearLayout.setStackFromEnd(true);
                rv.setLayoutManager(linearLayout);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(adapter);
//                rv.setRecyclerListener(mRecycleListener);

                rv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (isKeyboarShow) {
                            if (emojIcon != null) {
//                            emojIcon.closeEmojIcon();//an grid icon
                                MyUtils.hideKeyboard(activity);
                            }
                        }

                        //neu expand dang mo thi an
                        if (linearRecorder.getVisibility() == View.VISIBLE) {
                            linearRecorder.setVisibility(View.GONE);
                            imgAdd.setRotation(0);
                        }
                        if (linearExpand.getVisibility() == View.VISIBLE) {
                            isNeedExpand = false;
                            linearExpand.setVisibility(View.GONE);
                            //add
                            imgAdd.setRotation(0);
                        }


                        return false;
                    }

                });

                registerScrollRecyclerview();

            }
        }
    }

    private void registerScrollRecyclerview() {
        /*if (isFindPinMessage) {
            rv.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore() {
                    if (isFindPinMessage) {//load xuong
                        getRoomNextLogsBottom(adapter.getLastLogId_Bottom());
                    }

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && btnScroll.isShown()) {
                        btnScroll.hide();
                    } else if (dy < -10 && !btnScroll.isShown()) {
                        btnScroll.show();
                    }


                }
            });

        } else {*/

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isScrolling = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        isScrolling = false;
//                        MyUtils.log("The RecyclerView is not scrolling");
                        MyUtils.hideKeyboard(activity);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        isScrolling = true;
//                        MyUtils.log("Scrolling now");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        isScrolling = true;
//                        MyUtils.log("Scroll Settling");
                        break;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //LOAD MORE khi len TOP
                int pastVisibleItems = linearLayout.findLastCompletelyVisibleItemPosition();
                int size = adapter.getItemCount();
                //size > 10 khi list <10 se bi load duplicate
                if (!isLoading && size >= PAGE_ITEM && size - pastVisibleItems <= PAGE_ITEM * 2) {//pastVisibleItems  == 0
                    //tu dong lay them
                    loadMore();
                }

                //LOAD MORE KHI DI XUONG BOTTOM
                if (isFindPinMessage) {
//                    int visibleItemCount = linearLayout.getChildCount();
                    int firstVisibleItems = linearLayout.findFirstCompletelyVisibleItemPosition();
                    if (!isLoading && size >= PAGE_ITEM && firstVisibleItems <= PAGE_ITEM) {//pastVisibleItems  == 0
                        //tu dong lay them
                        getRoomNextLogsBottom(adapter.getLastLogId_Bottom());
                    }

                        /*if (!isLoading && !rv.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                            //tu dong lay them
                            getRoomNextLogsBottom(adapter.getLastLogId_Bottom());
                        }*/

                }

                //BTN SCROLL DOWN
                if (dy > 0 && btnScroll.isShown()) {
                    btnScroll.setVisibility(View.GONE);
                } else if (dy < -10 && !btnScroll.isShown()) {
                    btnScroll.setVisibility(View.VISIBLE);
                }
            }
        });
//        }
    }

    //CHAT////////////////////////////////////////////////////
    private ChatAdapter adapter;

    @Override
    protected void onDestroy() {

        reset();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (receiver != null) unregisterReceiver(receiver);

        /*if (realm != null && !realm.isClosed()) {
            realm.close();
        }*/

        //neu man hinh main chua mo thi mo lai
        ChatApplication.Companion.reopenMainActivity();

        isExists = false;
        super.onDestroy();
    }

    private void reset() {
        //reset -1
        if (db != null) db.putInt(TinyDB.CHAT_ADAPTER_POSITION, lastFirstVisiblePosition = -1);

        //cancel handler
        stop();

        //neu co file ghi am dang phat thi release
        if (adapter != null) {
            adapter.releaseMediaPlayer();
            adapter.clearData();
            adapter = null;
            rv.setAdapter(null);
        }
        roomId = "";
        room = null;
        guest = null;
        //article = null;
        jsonCreateRoom = "";
        channelId = "";


    }

    //EVENT////////////////////////////////////////////////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoomLogEvent_Chat(RoomLogEvent_Chat event) {
        MyUtils.showToastDebug(context, "have new message");
        //Neu cung room thi moi nhan
        RoomLog log = event.getMessage();
        whenHaveNewMessage(log);
    }

    private void whenHaveNewMessage(RoomLog log) {
        if ((room != null && room.get_id().equals(log.getRoomId())) ||
                roomId.equals(log.getRoomId())) {
            if (adapter != null) {
                adapter.append(log);

                //them moi thi moi scroll ve cuoi, (update+xoa+pin)thi giu nguyen vi tri
                boolean typePin = false;
                if (ContentType.ACTION.equalsIgnoreCase(log.getType())) {
                    JsonObject content = log.getContent();
                    String actionType = Action.getActionType(content);
                    if (Action.PIN_MESSAGE.equalsIgnoreCase(actionType) || Action.UNPIN_MESSAGE.equalsIgnoreCase(actionType)) {
                        typePin = true;
                    }
                }
                if (log.isUpdated() || log.isDeleted() || typePin) {
                    return;
                } else {
                    /*if (rv != null) {
                        rv.scrollToPosition(adapter.getItemCount() - 1);
                    }*/
                }


                //an button scroll
                if (btnScroll != null) {
                    btnScroll.performClick();
                }


                setLogIsView(log.get_id());
                hideEmptyView();
            }
        }
    }

    private void hideEmptyView() {
        if (emptyView != null && emptyView.getVisibility() == View.VISIBLE) {
            emptyView.setVisibility(View.GONE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRoomViewed(final RoomLogEvent_IsViewed event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RoomLog log = event.getMessage();
                if (log != null) {
                    if (adapter != null) {
                        //kiem tra minh da xem chua
                /*boolean isViewed = false;
                List<com.muabannhanh.chat.model.ChatView> views = log.getViews();
                if(views!=null && views.size()>0){
                    for (int i = 0; i < views.size(); i++) {
                        com.muabannhanh.chat.model.ChatView v = views.get(i);
                        if(v.getUserId()==ownerId && v.isViewed()){
                            isViewed=true;
                            break;
                        }
                    }
                }*/

                        adapter.updateRoomViewed(log);

                    }
                }
            }
        });
    }

    //GOI TIN CHAT--------------------------------------
    private boolean isSending = false;

    private void sendMessage(String message) {

        if (TextUtils.isEmpty(message)) {
            txt.requestFocus();
            return;
        }

        //mention @
        //replace @mention to structure json
        ArrayList<String> list = MyUtils.findUserMention(message);
        if (list.size() > 0) {
            if (room != null && mentionAdapter != null) {
                ArrayList<Member> mentionUsers = mentionAdapter.getMembers();//room.getMembers();
                if (mentionUsers != null) {
                    for (String name : list) {
                        for (int i = 0; i < mentionUsers.size(); i++) {
                            Member user = mentionUsers.get(i);
                            if (user != null && user.getUserInfo() != null && user.getUserInfo().getName().equals(name)) {
                                String replace = MyUtils.getMention(name, user.getUserId());
                                String needReplace = "[" + name + "]";
                                message = message.replace(needReplace, replace);
                            }
                        }
                    }
                }
            }
        }

        //neu co url thi goi ham sendlink, nguoc lai thi goi ham new message
        List<String> links = LinkUtils.extractUrls(message);
        if (links.size() > 0) {
            sendSocketLink(message, links.get(0));
        } else {
            sendSocketMessage(message);
        }

        //hide keyboard
        /*if (isKeyboarShow) {
            if (emojIcon != null) {
                MyUtils.hideKeyboard(activity);
            }
        }*/

    }

    private void timeoutSend() {

    }

    private void sendSocketMessage(String message) {
        if (MyUtils.checkInternetConnection(context)) {
            if (isSocketConnected() && room != null) {
                isSending = true;
                progressWheelCenter.setVisibility(View.VISIBLE);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                    obj.put("text", message);
                    obj.put("itemGUID", MyUtils.getGUID());
                    if (originMessage != null) {
                        obj.put("originMessage", originMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                socket.emit("sendText", obj, new Ack() {
                    @Override
                    public void call(Object... args) {

                        boolean isSuccess = RoomLog.isSuccess(context, args);
                        if (isSuccess) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                MyUtils.showToast(context, R.string.send_success);
                                    txt.setText("");
                                    progressWheelCenter.setVisibility(View.GONE);
                                    setReplyUI(null);
                                    btnScroll.performClick();
                                }
                            });

                        }

                        isSending = false;


                    }
                });
            }
        } else {
            MyUtils.showThongBao(context);
        }
    }

    private boolean isSendOneTime = true;

    ///////////////////////////////////////////////////////////////////////////////////////////
    //SEND LINK SAU DO PARSE UPDATE LINK
    private String guid = "";//luu lai de luc goi updatelink thi goi len

    private void sendSocketLink(final String message, final String link) {
        if (MyUtils.checkInternetConnection(context)) {
            if (isSocketConnected() && room != null) {
                isSending = true;
                progressWheelCenter.setVisibility(View.VISIBLE);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                    obj.put("text", message);
                    obj.put("link", link);
                    obj.put("itemGUID", guid = MyUtils.getGUID());
                    if (originMessage != null) {
                        obj.put("originMessage", originMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                socket.emit("sendLink", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {
                        boolean isSuccess = RoomLog.isSuccess(context, args);
                        if (isSuccess) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                MyUtils.showToast(context, R.string.send_success);
                                    txt.setText("");

                                    //sau do moi parse vaf update ve server
                                    RoomLog log = RoomLog.parseRoomLog(context, args);
                                    if (log != null) {
                                        log.setItemGUID(guid);
                                        parseLinkAndUpdate(message, link, log);
                                    }

                                    progressWheelCenter.setVisibility(View.GONE);
                                    setReplyUI(null);

                                    btnScroll.performClick();

                                }
                            });

                        }
                        isSending = false;

                    }
                });
            }
        } else {
            MyUtils.showThongBao(context);
        }
    }

    private void parseLinkAndUpdate(final String message, String link, final RoomLog m) {
        if (!TextUtils.isEmpty(link)) {
            textCrawler.makePreview(new LinkPreviewCallback() {
                @Override
                public void onPre() {

                }

                @Override
                public void onPos(SourceContent sourceContent, boolean isNull) {
                    if (isNull || sourceContent.getFinalUrl().equals("")) {
                        //ko parse dc

                    } else {
                        //set ChatLog 1 guid truoc, chut co socket updatemessage tra ve thi so sanh de update
                        setPreviewLink(sourceContent, m, message);
                    }
                }
            }, link);
        }
    }

    private void setPreviewLink(SourceContent content, RoomLog m, String text) {
        if (content != null && m != null) {

            //Code for the UiThread
            String url = "";
            if (content.getImages() != null && content.getImages().size() > 0) {
                url = content.getImages().get(0);
            }

            //update socket
            updateLinkParse(m.getRoomId(), m.get_id(), text, content.getUrl(), content.getTitle(), url, content.getDescription(), m.getItemGUID());
        }
    }

    private void updateLinkParse(String roomId, String chatLogId,
                                 String text, String link, String title, String imageLink, String description, String itemGUID) {
        Socket mSocket = ChatApplication.Companion.getSocket();
        // Sending an object
        if (mSocket != null && mSocket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", roomId);
                obj.put("chatLogId", chatLogId);
                obj.put("text", text);
                obj.put("link", link);
                obj.put("title", title);
                obj.put("imageLink", imageLink);
                obj.put("description", description);
                obj.put("itemGUID", itemGUID);

                mSocket.emit("updateLink", obj, new Ack() {
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
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void setLogIsView(String logId) {
        if (isSocketConnected() && room != null && !TextUtils.isEmpty(logId)) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
                obj.put("chatLogId", logId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("setLogIsView", obj, new Ack() {
                @Override
                public void call(Object... args) {
//                    boolean isSuccess = RoomLog.isSuccess(context, args);
//                    if (isSuccess) {
//                        //ham on se nhan 1 ket qua tra ve, va cap nhat lai cho recent adapter
//                    }
                }
            });
        }
    }


    //KHOI TAO GROUP CHAT--------------------------------
    private Room room;

    private void setTitle() {

        linearChannelInfo.setVisibility(View.GONE);

        if (room != null) {
            //set title
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black_24dp, 0);
            String name = room.getRoomName();
            title.setText(name);

            if (Room.ROOM_TYPE_PAGE.equalsIgnoreCase(room.getType())) {
                title.setText(room.getRoomName());
            } else if (Room.ROOM_TYPE_ITEM.equalsIgnoreCase(room.getType())) {
                //Hiển thị Item: màn hình chat gần đây hiển thị tên sản phẩm, bên trong hiển thị tên người bán
                List<Member> list = room.getMembers();
                if (list != null && list.size() > 0) {
                    //lay ten nguoi doi dien
                    String roomName = room.getRoomName();
                    for (int i = 0; i < list.size(); i++) {
                        Member item = list.get(i);
                        if (!TextUtils.isEmpty(item.getUserId()) && !owner.get_id().equals(item.getUserId())) {
                            if (item.getUserInfo() != null) {
                                roomName = item.getUserInfo().getName();
                                break;
                            }
                        }
                    }

                    title.setText(roomName);
                }
            } else if (Room.ROOM_TYPE_CHANNEL_ADMIN.equalsIgnoreCase(room.getType())) {

                //duoc chat
                linearChat.setVisibility(View.VISIBLE);
                btnChatWithAdmin.setVisibility(View.GONE);

                linearChannelInfo.setVisibility(View.VISIBLE);
                txtChannelName.setText(room.getChannelName());
                getImgChannelClose.setOnClickListener(view -> linearChannelInfo.setVisibility(View.GONE));

                int width = getResources().getDimensionPixelSize(R.dimen.dimen_80dp);
                int height = getResources().getDimensionPixelSize(R.dimen.dimen_60dp);

                String url = room.getChannelAvatar();
                if (!TextUtils.isEmpty(url)) {
                    Picasso.get()
                            .load(url)
                            .resize(width, height)
                            .centerInside()
                            .into(imgChannelAvatar);
                }


                imgChannelAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        MyUtils.openChatRoom(context, room.getChannelId());

                    }
                });
                linearChannelInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgChannelAvatar.performClick();
                    }
                });


            } else if (Room.ROOM_TYPE_CHANNEL.equalsIgnoreCase(room.getType())) {////neu la channel thi kiem tra co quyen chat hay khong

                //neu la member trong channel thi vao luon, nguoc lai hoi xac nhan
                Member me = null;
                List<Member> members = room.getMembers();

                if (members != null && members.size() > 0) {
                    for (int i = 0; i < members.size(); i++) {
                        Member item = members.get(i);
                        if (!TextUtils.isEmpty(item.getUserId()) && item.getUserId().equals(owner.get_id())) {
                            me = item;
                            break;
                        }
                    }
                }

                if (me != null) {
                    Permission permission = me.getPermissions();
                    if (permission != null) {
                        if (permission.isPostMessage()) {
                            //duoc chat
                            linearChat.setVisibility(View.VISIBLE);
                            btnChatWithAdmin.setVisibility(View.GONE);
                        } else {
                            //ko dc chat
                            linearChat.setVisibility(View.GONE);

                            //chat voi admin
                            btnChatWithAdmin.setVisibility(View.VISIBLE);
                            btnChatWithAdmin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    //18092018 doi thanh https://docs.google.com/spreadsheets/d/1nI695YutuT96QEdQFT20xAkdJyZ8FBS8UlmlO9FNExM/edit?pli=1#gid=0&range=C140
                                    finish();

                                    //mo man hinh chat voi admin, dong thoi forward tin cuoi cung
                                    //luu item tren cache
                                    RoomLog log = adapter.getLastLog();
                                    if (log != null) {
                                        ChatApplication.Companion.setLogForward(log);
                                        Intent intent = new Intent(context, ChatFragment.class);
                                        intent.putExtra(Room.CHANNEL_ID, room.get_id());
                                        intent.putExtra(RoomLog.IS_FORWARD, true);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(context, ChatFragment.class);
                                        intent.putExtra(Room.CHANNEL_ID, room.get_id());
                                        startActivity(intent);
                                    }

                                }
                            });
                        }
                    }
                }


            } else {//1-1

                name = room.getRoomName();
                //LAY TEN LOCAL/////////////////////////////////////////////////////////////////
                try {
                    realm.beginTransaction();
                    UserInfo item = realm.where(UserInfo.class).equalTo("nameMBN", name).findFirst();
                    if (item != null) {
                        name = item.getName();
                        room.setRoomName(name);
                    }
                    realm.commitTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //LAY TEN LOCAL/////////////////////////////////////////////////////////////////
                title.setText(room.getRoomName());

                if (room.getUserId1() != null && room.getUserId2() != null && room.getUserId1().equals(room.getUserId2()) && Room.ROOM_TYPE_PRIVATE.equalsIgnoreCase(room.getType())) {
                    title.setText(R.string.saved_messages);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

            }


            //show hide icon pin
            loadPinData();

        }

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (room != null && !TextUtils.isEmpty(room.getType())) {
                    switch (room.getType()) {
                        case Room.ROOM_TYPE_PAGE:
                        case Room.ROOM_TYPE_CUSTOM:
                        case Room.ROOM_TYPE_SUPPORT:
                        case Room.ROOM_TYPE_CHANNEL:
                        case Room.ROOM_TYPE_CHANNEL_ADMIN:
                        case Room.ROOM_TYPE_PROJECT:
                            if (MyUtils.checkInternetConnection(context)) {

                                Intent intent = new Intent(context, ChatGroupDetailActivity.class);
                                room.setLastLog(null);
                                room.getProjectLink();
                                intent.putExtra(Room.ROOM, room);
                                startActivity(intent);

                            } else {
                                MyUtils.showThongBao(context);
                            }
                            break;
                        default:
                            //vao profile, lay phone tu danh sach member trong GROUP
                            String phone = "";
                            UserChat userChat = null;
                            if (room != null) {
                                List<Member> list = room.getMembers();
                                if (list != null && list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++) {
                                        Member m = list.get(i);

                                        //di vao profile cua doi phuong
                                        String id = owner.get_id();
                                        if (!TextUtils.isEmpty(m.getUserId()) && !m.getUserId().equals(id)) {
                                            UserChat u = m.getUserInfo();
                                            if (u != null) {
                                                phone = u.getPhone();
                                                userChat = u;
                                                userChat.setUserId(m.getUserId());
                                                userChat.setMuted(m.isMuted());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (userChat != null) {
                                UserInfo userInfo = UserInfo.createUserInfo(userChat);
                                if (userInfo != null) {
                                    Intent intent = new Intent(context, ChatUserDetailActivity.class);
                                    intent.putExtra(UserInfo.USER_INFO, userInfo);
                                    intent.putExtra(ChatUserDetailActivity.IS_FROM_CHAT, true);
                                    if (room != null) {
                                        room.setLastLog(null);
                                        intent.putExtra(Room.ROOM, room);
                                    }
                                    context.startActivity(intent);
                                }
                            }
                            break;
                    }

                }


            }
        });
    }

    private void initChatRoom() {
        Logger.d("initChatroom");
        //khoi tao room
        if (params != null && params.size() > 0) {
            int size = params.size();
            switch (size) {
                case 2:
                    //mbnappchat://chat/room?type=join&link=sdsds
                    //2 param giong nhau, chi de phan biet voi room private
                    String link = params.get(0);
                    //Hoi co dong y vao room
                    initRoomChannel(link);

                    break;
                case 1://private
                    initRoomChatWithUser();
                    break;
                case 5://page
                    initRoomChatWithPage();
                    break;
                case 8://item
                    initRoomChatWithItem();
                    break;
            }
        } else {
            //Logger.d("getChat_page() == null");
            if (room != null) {
                //room nay chi co thong tin co ban de load truoc history, can lay lai room day du
                whenHaveRoom();
                //lay lai room day du
                initRoomChatWithRoomIdAndSetAdapter(room.get_id());

            } else if (!TextUtils.isEmpty(roomId)) {
//                initRoomChatWithRoomId(roomId);
                //room nay chi co thong tin co ban de load truoc history, can lay lai room day du
                whenHaveRoom();
                //lay lai room day du
                initRoomChatWithRoomIdAndSetAdapter(roomId);
            } else if (project != null) {
                title.setText(project.getProjectName());
                initRoomChatProject(project.getProjectId());
            } else if (!TextUtils.isEmpty(guestId)) {
                initRoomChatWithUser();
            } else if (!TextUtils.isEmpty(jsonCreateRoom)) {
                createChatRoom(jsonCreateRoom);
            } else if (!TextUtils.isEmpty(channelId)) {
                getChannelAdminRoom(channelId);
            } else if (isSupport) {
                Object obj = db.getObject(Room.ROOM_SUPPORT_CACHE, Room.class);
                if (obj != null) {
                    room = (Room) obj;

                    //room nay chi co thong tin co ban de load truoc history, can lay lai room day du
                    whenHaveRoom();
                    //lay lai room day du
                    initRoomChatWithRoomIdAndSetAdapter(room.get_id());
                } else {
                    //tao tu dau
                    initRoomChatWithSupport();
                }
            }
        }
    }

    String guestId;

    private void initRoomChatWithUser() {
        if (room == null) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    if (params != null && params.size() > 0) {
                        obj.put("userId", Long.parseLong(params.get(0)));
                    } else {
                        if (!TextUtils.isEmpty(guestId)) {
                            obj.put("userId", guestId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (obj.length() > 0) {
                    socket.emit("getPrivateRoom", obj, (Ack) args -> runOnUiThread(() -> {
                        room = Room.parseRoom(context, args);
                        whenHaveRoom();
                    }));
                }

            }
        } else {
            if (adapter != null) adapter.setRoom(room);
            beginLoadHistory();
        }
    }


    private void initRoomChatProject(long projectId) {
        if (room == null) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("projectId", projectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (obj.length() > 0) {
                    socket.emit("getProjectRoom", obj, (Ack) args -> runOnUiThread(() -> {
                        room = Room.parseRoom(context, args);
                        whenHaveRoom();
                    }));
                }

            }
        } else {
            if (adapter != null) adapter.setRoom(room);
            beginLoadHistory();
        }
    }

    /////////////////////////////////////////////////////////////
    private boolean isSupport = false;

    private void initRoomChatWithSupport() {
        if (room == null) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("supportGroupId", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (obj.length() > 0) {
                    /*socket.emit("getSupportRoom", obj, (Ack) args -> runOnUiThread(() -> {
                        room = Room.parseRoom(context, args);
                        whenHaveRoom();

                        //luu lai de lan 2 load len
                        if (room != null) {
                            db.putObject(Room.ROOM_SUPPORT_CACHE, room);
                        }
                    }));*/
                    socket.emit("getSupportRoom", obj, new Ack() {
                        @Override
                        public void call(Object... args) {
                            room = Room.parseRoom(context, args);
                            whenHaveRoom();

                            //luu lai de lan 2 load len
                            if (room != null) {
                                db.putObject(Room.ROOM_SUPPORT_CACHE, room);
                            }
                        }
                    });
                }

            }
        } else {
            if (adapter != null) adapter.setRoom(room);
            beginLoadHistory();
        }
    }

    private void whenHaveRoom() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beginLoadHistory();

                if (room != null) {
                    setTitle();
                    if (adapter != null) adapter.setRoom(room);
                }
            }
        });

    }

    private void initRoomChatWithItem() {
        if (room == null) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    if (params != null && params.size() > 0) {
                        obj.put("userIdOwner", params.get(0));
                        obj.put("userIdGuest", params.get(1));
                        obj.put("itemId", params.get(2));
                        obj.put("itemName", params.get(3));
                        obj.put("itemImage", params.get(4));
                        obj.put("itemLink", params.get(5));
                        obj.put("itemPrice", params.get(6));
                        // bổ sung giá gốc
                        obj.put("itemOriginPrice", params.get(7));
                    } else {
                        /*if (article != null) {
                            obj.put("userIdOwner", article.getUser().get_id());
                            obj.put("userIdGuest", owner.get_id());
                            obj.put("itemId", article.get_id());
                            obj.put("itemName", article.getName());
                            obj.put("itemImage", article.getDefault_image_url());
                            obj.put("itemLink", article.getUrl());
                            obj.put("itemPrice", article.getPrice());
                            obj.put("itemOriginPrice", article.getPrice_origin());
                        }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("getItemRoom", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                room = Room.parseRoom(context, args);
                                if (room != null) {
                                    whenHaveRoom();

                                    //set info item
                                    setTopInfo(room.getItem());
                                }
                            }
                        });
                    }
                });
            }
        } else {
            adapter.setRoom(room);
            beginLoadHistory();
        }
    }


    private void initRoomChatWithRoomId(final String roomId) {

        /*for (int i = 0; i < 10; i++) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, i * 1000);
        }*/

        MyUtils.howLong(startGlobal,
                " have room " + roomId +
                        " socket " + (socket == null) +
                        " isconnected " + isSocketConnected()
        );

        if (room == null) {
            final long start = SystemClock.elapsedRealtime();
            if (isSocketConnected()) {
                if (!TextUtils.isEmpty(roomId)) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("roomId", roomId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("getChatRoom", obj, new Ack() {
                        @Override
                        public void call(final Object... args) {
                            boolean isSuccess = Room.isSuccess(context, args);
                            if (isSuccess) {
                                MyUtils.howLong(start, "load chat 1 return json");
                                room = Room.parseRoom(context, args);
                                ChatFragment.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (room != null) {
                                            MyUtils.howLong(start, "load chat 1");
                                            whenHaveRoom();

                                        /*itemId = 729803
                                        itemImage = "https://cdn.muabannhanh.com/asset/frontend/img/gallery/2017/03/27/58d8ced5794d6_1490603733.jpg"
                                        itemLink = "https://muabannhanh.com/ban-ban-phim-laptop-id-cb220b00"
                                        itemName = "Bán bàn phím laptop"*/
                                            //neu room la loai ITEM thi set vao article
                                            if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_ITEM)) {
                                                setTopInfo(room.getItem());
                                            } else if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL)) {

                                            } else if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_PRIVATE)) {//user
                                           /* List<Member> members = room.getMembers();
                                            if (members != null && members.size() > 0) {
                                                String roomName = "";
                                                for (int i = 0; i < members.size(); i++) {
                                                    Member m = members.get(i);
                                                    if(!owner.get_id().equals(String.valueOf(m.getUserId()))){
                                                        if(m.getUserInfo()!=null) {
                                                            roomName = m.getUserInfo().getName();
                                                            title.setText(roomName);
                                                        }
                                                    }
                                                }
                                            }*/
                                            }
                                        }

                                        MyUtils.howLong(startGlobal, "when have room");

                                    }
                                });
                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int code = Room.getErrorCode(context, args);
                                        if (code == Room.NOT_INT_ROOM_CODE) {
                                            String message = getString(R.string.not_in_room);
                                            MyUtils.showAlertDialog(ChatFragment.this, message, true);
                                        } else {
                                            String message = Room.getErrorMessage(context, args);
                                            MyUtils.showAlertDialog(ChatFragment.this, message, true);
                                        }

                                    }
                                });
                            }

                        }
                    });
                }
            }
        } else {
            if (adapter != null) {
                adapter.setRoom(room);
            }
            beginLoadHistory();
        }
    }

    private void initRoomChatWithRoomIdAndSetAdapter(final String roomId) {
        if (!TextUtils.isEmpty(roomId)) {
            final long start = SystemClock.elapsedRealtime();
            if (isSocketConnected()) {
                if (!TextUtils.isEmpty(roomId)) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("roomId", roomId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("getChatRoom", obj, new Ack() {
                        @Override
                        public void call(final Object... args) {


                            runOnUiThread(() -> {

                                room = Room.parseRoom(context, args);
                                if (room != null) {
                                    setTitle();
                                    MyUtils.howLong(start, "load chat initRoomChatWithRoomIdAndSetAdapter");
//                                        whenHaveRoom();
                                    //chi set lai adapter room
                                    if (adapter != null) adapter.setRoom(room);

                                    /*itemId = 729803
                                    itemImage = "https://cdn.muabannhanh.com/asset/frontend/img/gallery/2017/03/27/58d8ced5794d6_1490603733.jpg"
                                    itemLink = "https://muabannhanh.com/ban-ban-phim-laptop-id-cb220b00"
                                    itemName = "Bán bàn phím laptop"*/
                                    //neu room la loai ITEM thi set vao article
                                    if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_ITEM)) {
                                        setTopInfo(room.getItem());
                                    } else if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL)) {

                                    } else if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_PRIVATE)) {//user

                                    }

                                    setupMentionAdapter();
                                } else {
                                    int code = Room.getErrorCode(context, args);
                                    if (code == Room.NOT_INT_ROOM_CODE) {
                                        String message = getString(R.string.not_in_room);
                                        MyUtils.showAlertDialog(ChatFragment.this, message, true);
                                    } else {
                                        String message = Room.getErrorMessage(context, args);
                                        MyUtils.showAlertDialog(ChatFragment.this, message, true);
                                    }
                                }


                            });
                        }
                    });
                }
            }
        }
    }

    private void initRoomChatWithPage() {
        if (room == null) {

            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();

                /*List<Integer> member = new ArrayList<>();
                List<String> list = page.getAdmins();
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        try {
                            int number = Integer.parseInt(list.get(i));
                            member.add(number);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }*/

                try {
                    if (params != null && params.size() > 0) {
                        obj.put("userIdGuest", owner.get_id());//params.get(0)
                        obj.put("pageId", params.get(1));
                        obj.put("pageName", params.get(2));
                        obj.put("pageLink", params.get(3));
                        obj.put("pageImage", params.get(4));
                    } else {
                        /*if (page != null) {
                            obj.put("userIdGuest", owner.get_id());
                            obj.put("pageId", page.getPage_id());
                            obj.put("pageName", page.getName());
                            obj.put("pageLink", page.getUrl());
                            obj.put("pageImage", page.getAvatar_url());
//                          obj.put("pageMembers", new JSONArray(member));//update 8/1/2018
                        }*/
                    }

//                        MyUtils.log(obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("getPageRoom", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                room = Room.parseRoom(context, args);
                                if (room != null) {
                                    whenHaveRoom();

                                }
                            }
                        });
                    }
                });
            }
        } else {
            adapter.setRoom(room);
            beginLoadHistory();
        }
    }

    private void initRoomChannel(String linkChannel) {
        if (isSocketConnected()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("joinLink", linkChannel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit("getRoomFromJoinLink", obj, new Ack() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            room = Room.parseRoom(context, args);
                            if (room != null) {

                                boolean isMember = false;
                                //neu la member trong channel thi vao luon, nguoc lai hoi xac nhan
                                List<Member> members = room.getMembers();
                                if (members != null && members.size() > 0) {
                                    for (int i = 0; i < members.size(); i++) {
                                        Member item = members.get(i);
                                        if (item.getUserId().equals(owner.get_id())) {
                                            isMember = true;
                                            break;
                                        }
                                    }
                                }


                                if (isMember) {
                                    //vào room
                                    if (room != null) {

                                        ////do room nay moi tao, thieu quyen, nen phai goi lay lai
                                        roomId = room.get_id();
                                        room = null;
                                        initRoomChatWithRoomId(roomId);
                                    }
                                } else {
                                    //goi qua man hinh hoi co muon vao room nay khong
                                    showPopupChannelInfo(room);
                                }
                            }
                        }


                    });
                }
            });
        }
    }


    private void createChatRoom(String jsonCreateRoom) {
        if (room == null) {

            if (isSocketConnected()) {
                if (!TextUtils.isEmpty(jsonCreateRoom)) {

                    JSONObject obj = new JSONObject();
                    try {
                        JSONArray array = new JSONArray(jsonCreateRoom);
                        obj.put("members", array);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    socket.emit("createChatRoom", obj, new Ack() {
                        @Override
                        public void call(final Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    room = Room.parseRoom(context, args);
                                    if (room != null) {
                                        whenHaveRoom();


                                        //refesh man hinh chat gan day de lay ve room moi tao
                                        Intent intent = new Intent(RecentChat_Fragment.REFRESH_RECENT_CHAT_ROOM);
                                        sendBroadcast(intent);

                                    }
                                }
                            });
                        }
                    });

                }
            }
        } else {
            adapter.setRoom(room);
            beginLoadHistory();
        }
    }

    /**
     * Lấy thông tin room của nhóm
     *
     * @param channelId
     */
    private void getChannelAdminRoom(String channelId) {
        if (room == null) {

            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("userIdGuest", owner.get_id());
                    obj.put("channelId", channelId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("getChannelAdminRoom", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                room = Room.parseRoom(context, args);
                                if (room != null) {
                                    whenHaveRoom();


                                    //refesh man hinh chat gan day de lay ve room moi tao
                                    Intent intent = new Intent(RecentChat_Fragment.REFRESH_RECENT_CHAT_ROOM);
                                    sendBroadcast(intent);

                                }
                            }
                        });
                    }
                });
            }
        } else {
            adapter.setRoom(room);
            beginLoadHistory();
        }
    }


    private void beginLoadHistory() {

        //co chat log thi load theo dang tim den 1 log
        if (!TextUtils.isEmpty(chatLogId)) {
            getRoomNearbyLogs(chatLogId);
        } else {//load tu dau
            //load lai lich su
            loadMore = true;
            loadHistory();
        }

        //dang ky scroll theo loai nao pin/ko pin
        registerScrollRecyclerview();

        ////////////////////////////////
        setupMentionAdapter();

    }

    MentionAdapter mentionAdapter;

    private void setupMentionAdapter() {
        if (room != null) {

            ArrayList<Member> list = room.getMembers();
            mentionAdapter = new MentionAdapter(activity, R.layout.mention_user_row_name_and_phone, list);
            txt.setThreshold(1);//co chua @
            txt.setAdapter(mentionAdapter);
            txt.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                String oldValue = "";
                boolean isFirst = true;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    if (isFirst) {
                        oldValue = s.toString();
                        isFirst = false;
                    }
                }

                @Override
                public void afterTextChanged(Editable text) {
                    if (mentionAdapter != null) {
                        String s = text.toString();

                        //co 2 cai @@ thi bo 1 cai
                        if (s.toString().contains("@@")) {
                            //tu xoa
                            txt.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        }

                        if (s.toString().contains("@")) {

                            int star = s.lastIndexOf("@");
                            int end = s.lastIndexOf(" ");
                            if (end < star) end = s.length();
                            s = s.substring(star, end);

                            oldContent = text.toString();

                            mentionAdapter.getFilter().filter(s);
//                        Log.e("test", "test1: " + text);
                        }


                    }
                }
            });


            txt.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    String s = oldContent;
//                    Log.e("test", "test2: " + s);

                    String replace = "";
                    if (s.contains("@")) {
                        int star = s.indexOf("@");
                        int end = s.indexOf(" ");
                        if (end < star) end = s.length();
                        replace = s.substring(star, end);
                    }

                    Member user = mentionAdapter.getItem(arg2);
                    if (!TextUtils.isEmpty(replace) && user != null && user.getUserInfo() != null) {
                        s = s.replace(replace, "[" + user.getUserInfo().getName() + "] ");
                    }
                    txt.setText(s);
                    oldContent = txt.getText().toString();
                    txt.setSelection(txt.length());
                }
            });


        }
    }

    private boolean isLoading = false;
    private boolean loadMore = true;
    public static final int PAGE_ITEM = 15;

    private void loadHistory() {
        final long start = SystemClock.elapsedRealtime();
        isFindPinMessage = false;
        if (!isLoading && loadMore) {
            if (isSocketConnected() && adapter != null) {

                final String id = adapter.getLastLogId_Top();
                JSONObject obj = new JSONObject();
                try {

                    if (TextUtils.isEmpty(roomId)) {
                        if (room != null) {
                            roomId = room.get_id();
                        }
                    }
                    if (TextUtils.isEmpty(roomId)) {
                        return;
                    }

                    obj.put("roomId", roomId);
                    obj.put("lastLogId", id);
                    obj.put("itemCount", PAGE_ITEM);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MyUtils.log("load more " + id);
                isLoading = true;
                socket.emit("getRoomLogs", obj, new Ack() {
                    @Override
                    public void call(Object... args) {

                        final ArrayList<RoomLog> list = RoomLog.parseListRoomLog(context, args);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (list != null && list.size() > 0) {

                                    //dao vi tri lai
//                                    long start1 = SystemClock.elapsedRealtime();
//                                    Collections.reverse(list);
//                                    MyUtils.howLong(start1, "load chat reverse time");

                                    if (adapter != null) {
                                        if (!TextUtils.isEmpty(id)) {//load more
                                            if(!isFinishing()) {
                                                adapter.append(list);
                                            }
                                            MyUtils.howLong(start, "load chat 3 - [page N");
                                        } else {//first

//                                            MyUtils.log("load more first" + list.size());
                                            if(!isFinishing()) {
                                                adapter.clearData();
                                                adapter.append(list);
                                            }


                                            MyUtils.howLong(start, "load chat 2 - page 1");

//                                            SystemClock.sleep(100);

                                        }


                                        //lan dau tien, scroll ve cuoi
                                        /*if (TextUtils.isEmpty(id) && adapter.getItemCount() > 1) {
                                            btnScroll.performClick();
                                        }*/
                                    }


                                    loadMore = true;
                                    isLoading = false;
                                } else {
                                    loadMore = false;
                                    isLoading = true;
                                }


                                /*List<Movie> list1 = new ArrayList<>();
                                for (int i = 0; i < 100; i++) {
                                    Movie item = new Movie("title "+i, "gen "+i, "year "+i);
                                    list1.add(item);
                                }
                                MoviesAdapter adapter = new MoviesAdapter(list1);
                                rv.setAdapter(adapter);*/


                                //cap nhat da xem room
                                updateLogViewed();


                                //Sau khi load lich su xong, neu la tin can forward thi goi
                                if (isForward) {
                                    isForward = false;

                                    //forward 1 log
                                    RoomLog log = ChatApplication.Companion.getLogForward();
                                    if (log != null) {
                                        //goi tin
                                        forwardMessage(log);
                                    } else {
                                        //#1 share text
                                        String sharedText = ChatApplication.Companion.getSharedText();
                                        if (sharedText != null) {
                                            //co the la 1 text, hoac link
                                            sendMessage(sharedText);
                                        } else {
                                            //#2 share an image or a video
                                            Uri uri = ChatApplication.Companion.getSharedImageOrVideo();
                                            if (uri != null) {
                                                String mimeType = getContentResolver().getType(uri);
                                                if (mimeType.contains("video")) {
                                                    //nang cap sau
                                                } else if (mimeType.contains("image")) {
                                                    String path = FileUtil.getFilePathByUri(context, uri);
                                                    if (path != null) {
                                                        isCapture = true;
                                                        if (MyUtils.checkInternetConnection(context)) {
                                                            uploadImage(mCurrentPhotoPath = path);
                                                        } else {
                                                            MyUtils.showThongBao(context);
                                                        }
                                                    }

                                                }
                                            } else {
                                                //#3 share multiple images, multiple video
                                                ArrayList<Uri> listImages = ChatApplication.Companion.getSharedMultiImages();
                                                if (listImages != null && listImages.size() > 0) {

                                                    isCapture = false;
                                                    //tao moi lai
                                                    paths = new ArrayList<>();

                                                    //tim hinh
                                                    for (int i = 0; i < listImages.size(); i++) {
                                                        Uri item = listImages.get(i);
                                                        String mimeType = getContentResolver().getType(item);
                                                        if (mimeType.contains("image")) {
                                                            String path = FileUtil.getFilePathByUri(context, item);
                                                            if (!TextUtils.isEmpty(path)) {
                                                                paths.add(path);
                                                            }
                                                        }
                                                    }

                                                    //send images
                                                    if (paths.size() > 0) {
                                                        if (paths.size() == 1) {
                                                            isSendAlbum = false;
                                                            processUploadMedia(paths.get(0));//1 hình hoặc 1 video
                                                        } else {//>1
                                                            pathsAlbumCache = new ArrayList<>(paths);
                                                            isSendAlbum = true;
                                                            uuidAlbum = MyUtils.getGUID();
                                                            isAddHolderAlbum = false;
                                                            albums = new JSONArray();
                                                            processUploadMedia(paths.get(0));//hình hoặc nhiều video
                                                        }
                                                    }

                                                }else{
                                                    //#4 share an document pdf, docx, ...
                                                    uri = ChatApplication.Companion.getSharedDocument();
                                                    if (uri != null) {
                                                        String mimeType = getContentResolver().getType(uri);
                                                        if (mimeType.contains("application")) {
                                                            String path = FileUtil.getFilePathByUri(context, uri);
                                                            if (path != null) {
                                                                isCapture = true;
                                                                if (MyUtils.checkInternetConnection(context)) {
                                                                    uploadFile(path);
                                                                } else {
                                                                    MyUtils.showThongBao(context);
                                                                }
                                                            }

                                                        }
                                                    } else {

                                                    }
                                                }

                                            }
                                        }
                                    }

                                    //reset
                                    ChatApplication.Companion.resetLogForwardAndDataShared();
                                }


                                //hide empty view, khi load page dau tien
                                if(adapter!=null && adapter.getItemCount()>0){
                                    hideEmptyView();
                                }
                                MyUtils.howLong(startGlobal, "when get page 1");

                            }
                        });


                    }
                });

            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    public static final String FINISH_ACTIVITY = "FINISH_ACTIVITY_ChatActivity_001";

    public static final String ACTION_UPDATE_ROOM_NAME_1 = "ACTION_UPDATE_ROOM_NAME_1";
    public static final String ACTION_UPDATE_ROOM_AVATAR_1 = "ACTION_UPDATE_ROOM_AVATAR_1";

    public static final String ACTION_ADD_MEMBER_1 = "ACTION_ADD_MEMBER_1";
    public static final String ACTION_REMOVE_MEMBER_1 = "ACTION_REMOVE_MEMBER_1";

    public static final String ACTION_OPEN_ARTICLE = "ACTION_OPEN_ARTICLE";
    public static final String ACTION_BUY_ARTICLE = "ACTION_BUY_ARTICLE";

    public static final String ACTION_UPDATE_ROOM_MODEL = "ACTION_UPDATE_ROOM_MODEL_2";

    public static final String ACTION_UPDATE_USER_CHANGE_PERMISSION = "ACTION_UPDATE_USER_CHANGE_PERMISSION_ChatActivity";
    public static final String ACTION_PIN_MESSAGE_CHAT = "ACTION_PIN_MESSAGE_CHAT_ChatActivity";
    public static final String ACTION_UNPIN_MESSAGE_CHAT = "ACTION_UNPIN_MESSAGE_CHAT_ChatActivity";
    public static final String ACTION_OPEN_PIN_MESSAGE_CHAT = "ACTION_OPEN_PIN_MESSAGE_CHAT_ChatActivity";
    public static final String ACTION_UPDATE_MEMBER_RECEIVE_NOTIFICATION = "ACTION_UPDATE_MEMBER_RECEIVE_NOTIFICATION";
    public static final String ACTION_SEND_PLAN = "ACTION_SEND_PLAN";
    public static final String ACTION_SCROLL_TO_POSITION_LOG = "ACTION_SCROLL_TO_POSITION_LOG";
    public static final String ACTION_REQUEST_STORAGE = "ACTION_REQUEST_STORAGE";

    //hien thi trang thai socket
    public static final String ACTION_STATE_SOCKET = "ACTION_STATE_SOCKET";
    public static final String STRING_STATE_SOCKET = "STRING_STATE_SOCKET";


    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();

                switch (intent.getAction()) {

                    case ACTION_STATE_SOCKET:
                        String state = b.getString(STRING_STATE_SOCKET, "");
                        txtSocketState.setText(state);

                        //control state
                        if (SocketState.CONNECTED.equalsIgnoreCase(state)) {

                            //reset state loading
                            loadMore = true;
                            isLoading = false;


                            stateOnline();

                            //lay lai thong so truoc khi load room
                            processChatRoom(getIntent());

                            //load lai tu dau
                            if (adapter != null) adapter.setBeginLoading(true);
                            initSocketConnection();

                        } else if (SocketState.DISCONNECT.equalsIgnoreCase(state)) {
                            stateOffline();
                        } else {
                            //reconnect, connecting, error, v.v.
                            stateOffline();
                        }
                        break;


                    case ACTION_REQUEST_STORAGE:
                        requestStorage();
                        break;

                    case FINISH_ACTIVITY:
                        finish();
                        break;

                    case ACTION_UPDATE_ROOM_NAME_1://khi doi ten hoac avatar nhom, phai cung room moi update
                        if (room != null) {
                            if (b != null) {
                                String id = b.getString(Room.ROOM_ID, "");
                                if (id.equals(room.get_id())) {
                                    String name = b.getString(Room.ROOM_NAME, "");
                                    if (!TextUtils.isEmpty(name)) {
                                        room.setRoomName(name);
                                        title.setText(name);
                                    }
                                }
                            }
                        }
                        break;

                    case ACTION_UPDATE_ROOM_AVATAR_1:
                        if (room != null) {
                            if (b != null) {
                                String id = b.getString(Room.ROOM_ID, "");
                                if (id.equals(room.get_id())) {
                                    String name = b.getString(Room.ROOM_AVATAR, "");
                                    if (!TextUtils.isEmpty(name)) {
                                        room.setRoomAvatar(name);
                                    }
                                }
                            }
                        }
                        break;

                    case ACTION_REMOVE_MEMBER_1:
                        if (room != null) {
                            if (b != null) {
                                String id = b.getString(Room.ROOM_ID, "");
                                if (id.equals(room.get_id())) {
                                    String userId = b.getString(UserChat.USER_ID, "");
                                    if (!TextUtils.isEmpty(userId)) {

                                        if (room.getMembers() != null && room.getMembers().size() > 0) {
                                            //tim va xoa
                                            for (int i = 0; i < room.getMembers().size(); i++) {
                                                if (userId.equals(room.getMembers().get(i).getUserId())) {
                                                    room.getMembers().remove(i);
                                                    break;
                                                }
                                            }
//                                        adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }
                        break;

                    case ACTION_ADD_MEMBER_1:
                        if (room != null) {
                            if (b != null) {
                                String id = b.getString(Room.ROOM_ID, "");
                                if (id.equals(room.get_id())) {
                                    Member member = b.getParcelable(Member.MEMBER);
                                    if (member != null) {

                                        //kiem tra ko co thi moi them vao
                                        boolean isExists = false;
                                        for (int i = 0; i < room.getMembers().size(); i++) {
                                            if (member.getUserId().equals(room.getMembers().get(i).getUserId())) {
                                                isExists = true;
                                                break;
                                            }
                                        }
                                        if (isExists == false) {
                                            room.getMembers().add(member);
                                        }
                                    }
                                }
                            }
                        }
                        break;

                    case ACTION_UPDATE_ROOM_MODEL:
                        if (room != null) {
                            if (b != null) {
                                Room r = b.getParcelable(Room.ROOM);
                                if (r != null) {
                                    if (room.get_id().equalsIgnoreCase(r.get_id())) {
                                        room = r;
                                        //da co su kien socket set lai title roi
                                        //thong tin ko doi gi nen ko can set lai UI
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                setTitle();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                        break;

                    case ACTION_UPDATE_USER_CHANGE_PERMISSION:
                        if (b != null) {
                            //cap nhat quyen admin
                            Member member = b.getParcelable(Member.MEMBER);
                            String roomId = b.getString(Room.ROOM_ID, "");

                            //room channel
                            if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CHANNEL) && room.get_id().equalsIgnoreCase(roomId)) {
                                List<Member> list = room.getMembers();
                                for (int i = 0; i < list.size(); i++) {
                                    Member item = list.get(i);
                                    if (item.getUserId().equals(member.getUserId())) {
                                        room.getMembers().set(i, member);
                                        break;
                                    }
                                }
                            }
                        }
                        break;

                    case ACTION_PIN_MESSAGE_CHAT:
                        if (b != null) {
                            String id = b.getString(RoomLog.ROOM_LOG_ID, "");
                            boolean isPin = b.getBoolean(RoomLog.IS_PIN, false);
                            if (!TextUtils.isEmpty(id)) {
                                setPinMessage(room.get_id(), id, isPin);

                                //khi pin hoac unpin thi kiem tra xem co con danh sach pin hay khong
                                loadPinData();
                            }
                        }
                        break;

                    case ACTION_UNPIN_MESSAGE_CHAT:
                        if (b != null) {
                            String id = b.getString(RoomLog.ROOM_LOG_ID, "");
                            boolean isPin = b.getBoolean(RoomLog.IS_PIN, false);
                            if (!TextUtils.isEmpty(id)) {
                                adapter.setPinMessage(id, isPin);

                                //khi pin hoac unpin thi kiem tra xem co con danh sach pin hay khong
                                loadPinData();
                            }
                        }
                        break;

                    case ACTION_OPEN_PIN_MESSAGE_CHAT:
                        if (b != null) {
                            String id = b.getString(RoomLog.ROOM_LOG_ID, "");
                            if (!TextUtils.isEmpty(id)) {
                                getRoomNearbyLogs(id);
                                registerScrollRecyclerview();
                            }
                        }
                        break;

                    case ACTION_UPDATE_MEMBER_RECEIVE_NOTIFICATION:
                        if (b != null) {
                            String id = b.getString(Member.MEMBER_ID, "");
                            boolean isMuted = b.getBoolean(Member.IS_MUTED, false);
                            if (!TextUtils.isEmpty(id) && room != null) {

                                List<Member> list = room.getMembers();
                                for (int i = 0; i < list.size(); i++) {
                                    Member item = list.get(i);
                                    if (item.getUserId().equals(id)) {
                                        room.getMembers().get(i).setMuted(isMuted);
                                        break;
                                    }
                                }
                            }


                        }
                        break;

                    case ACTION_SEND_PLAN:
                        if (b != null) {
                            PlanModelLocal plan = b.getParcelable(PlanModelLocal.PLAN_MODEL);
                            sendPlan(plan);
                        }
                        break;


                    case ACTION_SCROLL_TO_POSITION_LOG:
                        if (b != null) {
                            String logId = b.getString(RoomLog.ROOM_LOG_ID);
                            scrollToLog(logId);
                        }
                        break;

                }


            }
        };


        IntentFilter filters = new IntentFilter();

        filters.addAction(FINISH_ACTIVITY);
        filters.addAction(ACTION_UPDATE_ROOM_NAME_1);
        filters.addAction(ACTION_UPDATE_ROOM_AVATAR_1);

        filters.addAction(ACTION_ADD_MEMBER_1);
        filters.addAction(ACTION_REMOVE_MEMBER_1);

        filters.addAction(ACTION_OPEN_ARTICLE);
        filters.addAction(ACTION_BUY_ARTICLE);

        filters.addAction(ACTION_UPDATE_ROOM_MODEL);
        filters.addAction(ACTION_UPDATE_USER_CHANGE_PERMISSION);
        filters.addAction(ACTION_PIN_MESSAGE_CHAT);
        filters.addAction(ACTION_UNPIN_MESSAGE_CHAT);
        filters.addAction(ACTION_OPEN_PIN_MESSAGE_CHAT);
        filters.addAction(ACTION_UPDATE_MEMBER_RECEIVE_NOTIFICATION);
        filters.addAction(ACTION_SEND_PLAN);
        filters.addAction(ACTION_REQUEST_STORAGE);
        filters.addAction(ACTION_SCROLL_TO_POSITION_LOG);
        filters.addAction(ACTION_STATE_SOCKET);

        registerReceiver(receiver, filters);


    }

    AnimatorSet animationOnline;

    private void stateOnline() {
        if (animationOffline != null && animationOffline.isRunning()) {
            animationOffline.cancel();
        }
        txtInternetState.setText(getText(R.string.online_mode));
        txtInternetState.setBackgroundColor(getResources().getColor(R.color.light_blue_500));
        txtInternetState.setVisibility(View.VISIBLE);
        //an voi animation
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(txtInternetState, "alpha", 1f, .3f);
        fadeOut.setDuration(2500);
        animationOnline = new AnimatorSet();
        animationOnline.play(fadeOut);
        animationOnline.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                txtInternetState.setVisibility(View.GONE);
            }
        });
        animationOnline.start();
    }

    AnimatorSet animationOffline;

    private void stateOffline() {
        if (animationOnline != null && animationOnline.isRunning()) {
            animationOnline.cancel();
        }
        txtInternetState.setText(getText(R.string.offline_mode));
        txtInternetState.setBackgroundColor(getResources().getColor(R.color.orange_500));
        txtInternetState.setVisibility(View.GONE);

        //start voi animation
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(txtInternetState, "alpha", .3f, 1f);
        fadeIn.setDuration(2500);
        animationOffline = new AnimatorSet();
        animationOffline.play(fadeIn);
        animationOffline.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                txtInternetState.setVisibility(View.VISIBLE);
            }
        });
        animationOffline.start();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //CHECK PERMISSION
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 200;
    String[] arrPermissionsLocation = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private boolean checkPermissionLocation() {

        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < arrPermissionsLocation.length; i++) {
                int grant = ContextCompat.checkSelfPermission(getApplicationContext(), arrPermissionsLocation[i]);
                if (grant == PackageManager.PERMISSION_DENIED) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    private void requestPermissionLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            R.string.deny_permission_notify
            boolean check = checkPermissionLocation();
            if (check == false) {
//                MyUtils.showToast(context, R.string.deny_permission_notify);
                requestPermissions(arrPermissionsLocation, PERMISSION_REQUEST_CODE_LOCATION);
            } else {
                initGoogleApiClient();
            }

        } else {
            initGoogleApiClient();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //END CHECK PERMISSION
    ////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //CHECK PERMISSION
    /////////////////////////////////////////////////////////////////////////////////////////////////
    Bitmap myBitmap;
    Uri picUri;

    private final static int PERMISSION_REQUEST_CAMERA_AND_STORAGE_1 = 108;
    private final static int PERMISSION_REQUEST_CAMERA_AND_STORAGE_2 = 109;
    private final static int PERMISSION_REQUEST_CAMERA_AND_STORAGE_3 = 110;
    private final static int PERMISSION_REQUEST_STORAGE = 111;

    private final static int PERMISSION_REQUEST_RECORD = 112;
    String[] arrPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private boolean checkPermission() {

        boolean result = true;
        for (int i = 0; i < arrPermissions.length; i++) {
            int grant = ContextCompat.checkSelfPermission(getApplicationContext(), arrPermissions[i]);
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }

        return result;
    }

    private ArrayList<String> listPermissionNeedRequest() {
        ArrayList<String> list = new ArrayList<>();
        for (String item :
                arrPermissions) {
            if (ContextCompat.checkSelfPermission(this, item) != PackageManager.PERMISSION_GRANTED) {
                list.add(item);
            }
        }
        return list;
    }

    private void requestPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> list = listPermissionNeedRequest();
            if (list.size() > 0) {
                //lay item 0, neu bi tu choi thi bao ra
                String first = list.get(0);
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, first)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(R.string.notification);
                    dialog.setMessage(R.string.deny_permission_notify);
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MyUtils.goToSettings(context);
                        }
                    });
                    dialog.setNegativeButton(R.string.no, null);
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                } else {
                    requestPermissions(list.toArray(new String[list.size()]), requestCode);
                }

            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //END CHECK PERMISSION
    ////////////////////////////////////////////////////////////////////////////////////////

    //CAMERA
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted
            switch (requestCode) {
                case PERMISSION_REQUEST_CAMERA_AND_STORAGE_1:
                    linearCamera.performClick();
                    break;
                case PERMISSION_REQUEST_CAMERA_AND_STORAGE_2:
                    linearPicture.performClick();
                    break;
                case PERMISSION_REQUEST_CAMERA_AND_STORAGE_3:
                    if (linears != null && linears.size() > 1) {
                        linears.get(1).performClick();
                    }
                    break;

                case PERMISSION_REQUEST_RECORD:
                    if (btnRecord != null) btnRecord.performClick();
                    break;

                case PERMISSION_REQUEST_CODE_LOCATION:
                    requestPermissionLocation();
                    break;
                case PERMISSION_REQUEST_STORAGE:
                    //OK
                    break;
            }
        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<String> pathsAlbumCache = new ArrayList<>();


    private void uploadImage(String path) {
        if (!TextUtils.isEmpty(path)) {

            //init value
            resetPath();

            //xoa 1 ptu ra khoi queue
            if (paths.size() > 0 && paths.contains(path)) {
                paths.remove(path);
            }

            //upload
            picUri = Uri.parse(path);
            parseUriAndUploadImage();
        }
    }


    private static final int ACTION_SELECT_LOCATION = 123;
    private static final int REQUEST_CODE_PICK_PICTURE = 333;
    public static final int FIND_MEMBER_CODE = 2018;
    private boolean isCapture = false;
    private boolean isSendAlbum = false;
    private String uuidAlbum = "";
    private boolean isAddHolderAlbum = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case ACTION_SELECT_LOCATION:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        MyLocation location = b.getParcelable(MyLocation.MY_LOCATION);
                        if (location != null && location.getLat() > 0 && location.getLon() > 0) {
                            lat = location.getLat();
                            lon = location.getLon();
                            address = location.getAddress();
                            isCheckin = location.isCurrentLocation();

                            sendLocation();
                        }
                    }

                    break;

                case REQUEST_CODE_TAKE_PICTURE:
                    isCapture = true;
                    if (MyUtils.checkInternetConnection(context)) {
                        uploadImage(mCurrentPhotoPath);
                    } else {
                        MyUtils.showThongBao(context);
                    }

                    break;
                case REQUEST_CODE_PICK_PICTURE://chọn nhiều hình hoặc nhiều video
                    isCapture = false;
                    //tao moi lai
                    paths = new ArrayList<>();
                    if (MyUtils.checkInternetConnection(context)) {
                        if (data != null) {
                            paths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                            if (paths.size() > 0) {
                                if (paths.size() == 1) {
                                    isSendAlbum = false;
                                    processUploadMedia(paths.get(0));//1 hình hoặc 1 video
                                } else {//>1
                                    pathsAlbumCache = new ArrayList<>(paths);
                                    isSendAlbum = true;
                                    uuidAlbum = MyUtils.getGUID();
                                    isAddHolderAlbum = false;
                                    albums = new JSONArray();
                                    processUploadMedia(paths.get(0));//hình hoặc nhiều video
                                }
                            }
                        }
                    } else {
                        MyUtils.showThongBao(context);
                    }
                    break;
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        ArrayList<Object> photoPaths = new ArrayList<>();
                        ArrayList<String> l = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                        if (l != null && l.size() > 0) {
                            photoPaths.addAll(l);
                        }
                    }
                    break;
                case FilePickerConst.REQUEST_CODE_DOC:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        ArrayList<Object> docPaths = new ArrayList<>();
                        docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        MyUtils.log(docPaths.size() + "");
                        //0 = "/storage/emulated/0/ebooks/Khac biet de but pha - Jason Fried.pdf"
                        //1 = "/storage/emulated/0/ebooks/family_playbook_v2a_en.pdf"

                        if (docPaths.size() > 0) {
                            uploadFile(docPaths.get(0).toString());
                        }
                    }
                    break;

                //quay phim hoac chup anh
                case CAPTURE_MEDIA:
                    String path = data.getStringExtra(AnncaConfiguration.Arguments.FILE_PATH);
//                    Toast.makeText(this, "Media captured. " + path, Toast.LENGTH_SHORT).show();
                    processUploadMedia(path);

                    break;

                case FIND_MEMBER_CODE:
                    if (data != null) {
                        b = data.getExtras();
                        if (b != null) {
                            final ArrayList<UserChatCore> users = b.getParcelableArrayList(UserChatCore.USER_MODEL);
                            if (users != null) {
                                for (int i = 0; i < users.size(); i++) {
                                    UserChatCore user = users.get(i);

                                    String uuid = MyUtils.getGUID();
                                    sendContact(
                                            user.get_id(),
                                            user.getName(),
                                            user.getAvatar(),
                                            user.getPhone(),
                                            user.getEmail(), uuid);
                                }
                            }
                        }
                    }
                    break;
            }


            //location
            switch (requestCode) {

                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case RESULT_OK:
                            if (isSendLocation) {
//                                sendLocation();
                                selectLocation();
                            } else {
                                //tao plan
                                createPlan();
                            }
                            break;
                        case RESULT_CANCELED:
                            MyUtils.showToast(context, R.string.gps_not_turn_on);
                            break;
                    }
                    break;
            }

        }


    }

    private void uploadFile(String path){
        if(!TextUtils.isEmpty(path)){
            //tao sas
            filePath = path;
            fileName = MyUtils.getFileNameAndExtension(filePath);


            //file size
            File f = new File(filePath);
            fileSize = MyUtils.getSizeBytes(f.length());

            generateUploadSAS(fileName, ContentTypeInChat.FILE);
            //upload azure
            //goi ham send file
        }
    }

    //true: sendLocation, false: tao Plan
    private boolean isSendLocation = true;

    //image
    private String imgPathFull = "", imgPathThumb = "";
    private int widthFull = 0, heightFull = 0, widthThumb = 0, heightThumb = 0;
    private float sizeFull = 0.0f, sizeThumb = 0.0f;
    private String fileImageName = "";//ten hinh full

    //video xai chung filePath, fileSize, fileName
    private int videoWidth = 0, videoHeight = 0;

    //file
    private String filePath = "";//rar, doc, pdf, ppt
    private float fileSize = 0.0f;
    private String fileName = "";

    private void resetPath() {
        imgPathFull = "";
        imgPathThumb = "";
        widthFull = 0;
        heightFull = 0;
        widthThumb = 0;
        heightThumb = 0;
        sizeFull = 0.0f;
        sizeThumb = 0.0f;
        fileImageName = "";

        videoWidth = 0;
        videoHeight = 0;

        filePath = "";
        fileName = "";
        fileSize = 0.0f;
    }


    private Uri parseUriImage(Uri picUri) {
        try {
            if (picUri.toString().contains("content://com.google.android.apps.photos.contentprovider")) {
                //Intent { dat=content://com.google.android.apps.photos.contentprovider/0/1/content://media/external/images/media/3476/ORIGINAL/NONE/598088098 flg=0x1 launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 } clip={text/uri-list U:content://com.google.android.apps.photos.contentprovider/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F3476/ORIGINAL/NONE/598088098} }
                picUri = getImageUrlWithAuthority(context, picUri);
                //return //content://media/external/images/media/74275
            }

            if (picUri.toString().contains("content://com.miui.gallery.open")) {
                //Intent { dat=content://com.google.android.apps.photos.contentprovider/0/1/content://media/external/images/media/3476/ORIGINAL/NONE/598088098 flg=0x1 launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 } clip={text/uri-list U:content://com.google.android.apps.photos.contentprovider/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F3476/ORIGINAL/NONE/598088098} }
                picUri = getImageUrlWithAuthority(context, picUri);
                //return //content://media/external/images/media/74275
            }

            //content://com.mi.android.globalFileexplorer.myprovider/external_files/DCIM/Screenshots/Screenshot_2018-04-29-13-26-01-752_com.android.contacts.png
            if (picUri.toString().contains("content://com.mi.android.globalFileexplorer.myprovider")) {
                //Intent { dat=content://com.google.android.apps.photos.contentprovider/0/1/content://media/external/images/media/3476/ORIGINAL/NONE/598088098 flg=0x1 launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 } clip={text/uri-list U:content://com.google.android.apps.photos.contentprovider/0/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F3476/ORIGINAL/NONE/598088098} }
                picUri = getImageUrlWithAuthority(context, picUri);
                //return //content://media/external/images/media/74275
            }


            //content://media/external/images/media/74275
            if (picUri.toString().contains("content://media/")) {
                String url = getRealPathFromUri(context, picUri);
                if (!TextUtils.isEmpty(url)) {
                    File f = new File(url);
                    picUri = Uri.fromFile(f);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return picUri;
    }

    private void parseUriAndUploadImage() {
        if (picUri != null) {
            try {

                picUri = parseUriImage(picUri);
                String path = picUri.getPath();

                //anh gif
                if(MyUtils.isImageGifUrl(path)){
                    //doc len va resize
                    myBitmap = BitmapFactory.decodeFile(path);
                    if (myBitmap != null) {

                        widthFull = myBitmap.getWidth();
                        heightFull = myBitmap.getHeight();
                        widthThumb = myBitmap.getWidth();
                        heightThumb = myBitmap.getHeight();


                        //file thumb va full
                        imgPathThumb = path;
                        imgPathFull = path;

                        //doc thong tin size
                        File f = new File(imgPathFull);
                        sizeFull = MyUtils.getSizeBytes(f.length());

                        f = new File(imgPathThumb);
                        sizeThumb = MyUtils.getSizeBytes(f.length());

                        //tao sas
                        fileImageName = MyUtils.getFileNameAndExtension(imgPathFull);
                        generateUploadSAS(fileImageName, ContentTypeInChat.IMAGE);
                        //upload azure
                        //goi ham send image
                    }
                }else{//anh jpg
                    //doc len va resize
                    myBitmap = BitmapFactory.decodeFile(path);
                    if (myBitmap != null) {
                        myBitmap = MyUtils.rotateImageIfRequired(myBitmap, picUri);
                        Bitmap thumb = MyUtils.getResizedBitmap(myBitmap, getResources().getDimensionPixelSize(R.dimen.img_size_thumb));
                        Bitmap full = MyUtils.getResizedBitmap(myBitmap, getResources().getDimensionPixelSize(R.dimen.img_size_full));

                        widthFull = full.getWidth();
                        heightFull = full.getHeight();
                        widthThumb = thumb.getWidth();
                        heightThumb = thumb.getHeight();


                        //luu xuong, gan vao 2 path full va thumb
                        imgPathThumb = MyUtils.createImageFile(context).getAbsolutePath();
                        MyUtils.saveBitmapThumb(thumb, imgPathThumb);
                        imgPathFull = MyUtils.createImageFile(context).getAbsolutePath();
                        MyUtils.saveBitmap(full, imgPathFull);

                        //doc thong tin size
                        File f = new File(imgPathFull);
                        sizeFull = MyUtils.getSizeBytes(f.length());

                        f = new File(imgPathThumb);
                        sizeThumb = MyUtils.getSizeBytes(f.length());

                        //tao sas
                        fileImageName = MyUtils.getFileNameAndExtension(imgPathFull);
                        generateUploadSAS(fileImageName, ContentTypeInChat.IMAGE);
                        //upload azure
                        //goi ham send image
                    }
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //google photos
    private Uri getImageUrlWithAuthority(Context context, Uri uri) {

        /*final int takeFlags = getIntent().getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ContentResolver resolver = context.getContentResolver();
        resolver.takePersistableUriPermission(uri, takeFlags);*/

       /* try {
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //replace by
            if (data != null && data.getData() != null) {
                return data.getData();
            } else {
                return getCaptureImageOutputUri();
            }
        } else {
            return isCamera ? getCaptureImageOutputUri() : data.getData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }


    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param fileName
     * @param contentType {@link ContentTypeInChat}
     */
    private void generateUploadSAS(final String fileName, final int contentType) {
        if (isSocketConnected() && room != null) {

            final String uuid = MyUtils.getGUID();

            switch (contentType) {
                case ContentTypeInChat.IMAGE:
                case ContentTypeInChat.VIDEO:
                    if (isSendAlbum) {
                        if (!isAddHolderAlbum) {
                            addImageHolder(uuidAlbum, ContentType.ALBUM, pathsAlbumCache);
                            isAddHolderAlbum = true;
                        }
                    } else {//1 hinh
                        ArrayList list = new ArrayList<String>();
                        list.add(imgPathThumb);
                        addImageHolder(uuid, ContentType.IMAGE, list);
                    }

                    break;

                case ContentTypeInChat.VOICE:
                case ContentTypeInChat.FILE:
                    /*String image = MyUtils.getURLForResource(R.drawable.icon_file_doc);
                    ArrayList list = new ArrayList<String>();
                    list.add(image);*/

                    addImageHolder(uuid, ContentType.IMAGE, new ArrayList<String>());
                    break;
            }


            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
                obj.put("fileName", fileName);
                obj.put("itemGUID", uuid);
            } catch (Exception e) {
                e.printStackTrace();
            }


            socket.emit("generateUploadSAS", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
//                    MyUtils.log(args.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final SasModel model = SasModel.parseSasModel(context, args);
                            //upload azure
                            if (model != null) {

                                model.setFileName(fileName);

                                switch (contentType) {
                                    case ContentTypeInChat.IMAGE:
                                        startTaskUploadImage(model, contentType, uuid);
                                        break;
                                    case ContentTypeInChat.VIDEO:
                                        startTaskUploadVideo(model, contentType, uuid);
                                        break;
                                    case ContentTypeInChat.VOICE:
                                    case ContentTypeInChat.FILE:
                                        startTaskUploadFile(model, contentType, uuid, filePath);

                                        break;
                                }

                            }
                        }
                    });


                }
            });
        }
    }


    public static int WRITE_SIZE = 256 * 1024 / 3;//256KB

    private boolean uploadFile(String sasLink,
                               String imgPath,
                               int contentType,
                               final UploadItemTask task
    ) {
        if (!TextUtils.isEmpty(sasLink)) {
            try {

                //neu la file thi tang size len cho nhanh
                if (contentType != ContentTypeInChat.IMAGE) {
                    WRITE_SIZE = 256 * 1024;
                }

                //Return a reference to the blob using the SAS URI.
                CloudBlockBlob blob = new CloudBlockBlob(new StorageUri(URI.create(sasLink)));
                blob.setStreamWriteSizeInBytes(WRITE_SIZE);//256 k


                OutputStream oStream = blob.openOutputStream();
                File f = new File(imgPath);
//                FileInputStream inputStream = new FileInputStream(f);
//                blob.upload(inputStream, f.length());


                FileInputStream inputStream = new FileInputStream(f);
                final long length = f.length();
//                MyUtils.log("write total = " + length);
                final long lengthSum = task.lengthSum;
                ListenableInputStream lis = new ListenableInputStream(inputStream, new ListenableInputStream.ReadListener() {
                    @Override
                    public void onRead(long bytes) {
                        task.totalBytes += bytes;
//                        MyUtils.log("write = " + totalBytes);
                        int percent = (int) (task.totalBytes * 100 / lengthSum);
                        task.onProgressUpdate(new Integer[]{percent});
                    }
                }, WRITE_SIZE, length);
                blob.upload(lis, length);


                //upload properties
                String properties = "image/jpeg";
                switch (contentType) {
                    case ContentTypeInChat.IMAGE:
                        if(MyUtils.isImageGifUrl(imgPath)){
                            properties = "image/gif";
                        }else {
                            properties = "image/jpeg";
                        }
                        break;
                    case ContentTypeInChat.VIDEO:
                        properties = "video/mp4";
                        break;
                    case ContentTypeInChat.VOICE:
                        properties = "audio/mp3";
                        break;
                    default:
                        properties = "application/octet-stream";
                        break;
                }
                blob.getProperties().setContentType(properties);


                blob.uploadProperties();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }


    private void sendImage(SasModel sas, String fileName, String uuid) {
        if (isSocketConnected() && room != null) {
            JSONObject obj = new JSONObject();
            try {
                //roomId, fileImageName, link, thumbLink, height, width, size, thumbHeight, thumbWidth, thumbSize, itemGUID
                obj.put("roomId", room.get_id());
                obj.put("fileName", fileName);
                obj.put("link", sas.getLink());
                obj.put("thumbLink", sas.getThumbLink());
                obj.put("height", heightFull);
                obj.put("width", widthFull);
                obj.put("size", sizeFull);
                obj.put("thumbHeight", heightThumb);
                obj.put("thumbWidth", widthThumb);
                obj.put("thumbSize", sizeThumb);
                obj.put("itemGUID", uuid);
                if (originMessage != null) {
                    obj.put("originMessage", originMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("sendImage", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    //MyUtils.log(args.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean isSuccess = RoomLog.isSuccess(context, args);
                            if (isSuccess) {
                                setReplyUI(null);

                                //sau 3s vẫn chưa thấy tín hiệu của new message gởi về thì tự add vào
                                MyUtils.showToastDebug(context, R.string.upload_success);

                            }
                        }
                    });

                    isSelectImageOrFile = false;
                }
            });
        } else {
            isSelectImageOrFile = false;
        }
    }

    private void sendVideo(SasModel sas, String fileName, String uuid) {
        if (isSocketConnected() && room != null) {
            JSONObject obj = new JSONObject();
            try {
                //roomId, fileImageName, link, thumbLink, height, width, size, thumbHeight, thumbWidth, thumbSize, itemGUID
                obj.put("roomId", room.get_id());
                obj.put("fileName", fileName);
                obj.put("link", sas.getLink());
                obj.put("thumbLink", sas.getThumbLink());
                obj.put("height", videoHeight);//size of video
                obj.put("width", videoWidth);
                obj.put("size", fileSize);
                obj.put("thumbHeight", heightThumb);
                obj.put("thumbWidth", widthThumb);
                obj.put("thumbSize", sizeThumb);
                obj.put("itemGUID", uuid);
                if (originMessage != null) {
                    obj.put("originMessage", originMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("sendVideo", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    //MyUtils.log(args.toString());
                    boolean isSuccess = RoomLog.isSuccess(context, args);
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                MyUtils.showToast(context, R.string.upload_success);
                                setReplyUI(null);
                            }
                        });

                    }
                    isSelectImageOrFile = false;
                }
            });
        } else {
            isSelectImageOrFile = false;
        }
    }

    /**
     * @param sas
     * @param voiceLength tính theo giây
     * @param size        tính theo byte
     * @param uuid
     */
    private void sendVoice(SasModel sas, int voiceLength, float size, String uuid) {
        if (isSocketConnected() && room != null) {
            JSONObject obj = new JSONObject();
            try {
                //roomId, fileImageName, link, thumbLink, height, width, size, thumbHeight, thumbWidth, thumbSize, itemGUID
                obj.put("roomId", room.get_id());
                obj.put("link", sas.getLink());
                obj.put("voiceLength", voiceLength);
                obj.put("size", size);
                obj.put("itemGUID", uuid);
                if (originMessage != null) {
                    obj.put("originMessage", originMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("sendVoice", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    //MyUtils.log(args.toString());
                    boolean isSuccess = RoomLog.isSuccess(context, args);
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                MyUtils.showToast(context, R.string.upload_success);
                                setReplyUI(null);
                            }
                        });

                    }
                    isSelectImageOrFile = false;
                }
            });
        } else {
            isSelectImageOrFile = false;
        }
    }

    private void sendContact(String contactId,
                             String contactName,
                             String contactAvatar,
                             String contactMobile,
                             String contactEmail,
                             String uuid) {
        if (isSocketConnected() && room != null) {
            JSONObject obj = new JSONObject();
            try {
                //roomId, fileImageName, link, thumbLink, height, width, size, thumbHeight, thumbWidth, thumbSize, itemGUID
                obj.put("roomId", room.get_id());
                obj.put("contactId", contactId);
                obj.put("contactName", contactName);
                obj.put("contactAvatar", contactAvatar);
                obj.put("contactMobile", contactMobile);
                obj.put("contactEmail", contactEmail);
                obj.put("itemGUID", uuid);
                if (originMessage != null) {
                    obj.put("originMessage", originMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("sendContact", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    //MyUtils.log(args.toString());
                    boolean isSuccess = RoomLog.isSuccess(context, args);
                    if (isSuccess) {

                    }
                    isSelectImageOrFile = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setReplyUI(null);
                        }
                    });
                }
            });
        } else {
            isSelectImageOrFile = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private JSONArray albums = null;

    private void sendAlbum(String uuid) {
        if (isSocketConnected() && room != null && albums != null) {
            JSONObject obj = new JSONObject();
            try {
                //roomId, fileImageName, link, thumbLink, height, width, size, thumbHeight, thumbWidth, thumbSize, itemGUID
                obj.put("roomId", room.get_id());
                obj.put("itemGUID", uuid);
                obj.put("count", albums.length());
                obj.put("items", albums);
                if (originMessage != null) {
                    obj.put("originMessage", originMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("sendAlbum", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    //MyUtils.log(args.toString());
                    boolean isSuccess = RoomLog.isSuccess(context, args);
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                MyUtils.showToast(context, R.string.upload_success);
                                setReplyUI(null);
                            }
                        });
                    }
                    isSelectImageOrFile = false;

                }
            });
        } else {
            isSelectImageOrFile = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private void sendFile(SasModel sas, String fileName, String uuid) {
        if (isSocketConnected() && room != null) {
            JSONObject obj = new JSONObject();
            try {
                //roomId, fileImageName, link, thumbLink, height, width, size, thumbHeight, thumbWidth, thumbSize, itemGUID
                obj.put("roomId", room.get_id());
                obj.put("fileName", fileName);
                obj.put("link", sas.getLink());
                obj.put("size", fileSize);
                obj.put("itemGUID", uuid);
                if (originMessage != null) {
                    obj.put("originMessage", originMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("sendFile", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    //MyUtils.log(args.toString());
                    boolean isSuccess = RoomLog.isSuccess(context, args);
                    if (isSuccess) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                MyUtils.showToast(context, R.string.upload_success);
                                setReplyUI(null);
                            }
                        });
                    }
                    isSelectImageOrFile = false;
                }
            });
        } else {
            isSelectImageOrFile = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    //LOCATION/////////////////////////////////////////////////////////////////////////////
    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static LocationSettingsRequest.Builder builder;

    private void initGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (db != null) {
            lat = db.getDouble(TinyDB.LATITUDE, 0);
            lon = db.getDouble(TinyDB.LONGITUDE, 0);
        }

        if (mGoogleApiClient == null) {
            //communication with Google Play Services
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            //////////////////////////////////////////////////////////////
            // Create the location request
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(30 * 1000)//bao lau thi update
                    .setFastestInterval(2 * 1000);
            //***cho phan setting o cuoi
            builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);
            //////////////////////////////////////////////////////////////
            mGoogleApiClient.connect();
        } else {
            if (mGoogleApiClient != null) mGoogleApiClient.connect();
        }

        //kiem tra gps da duoc bat chua,
        checkLocationSettings();
    }

    @Override
    protected void onStart() {
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            db.putDouble(TinyDB.LATITUDE, lat);
            db.putDouble(TinyDB.LONGITUDE, lon);


            stopLocationUpdates();
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Location mLastLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        getMyLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("tag", "onConnectionSuspended");
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            Log.d("tag", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isResume = false;
    int lastFirstVisiblePosition = -1;
    private boolean mRequestingLocationUpdates = false;

    @Override
    public void onResume() {//start lai location update
        super.onResume();

        isResume = true;
        initSocketOnly();

        //socket
        if (owner == null) {
            owner = ChatApplication.Companion.getUser();
        }


        //RESTORE
        try {
            if (linearLayout != null) {
                lastFirstVisiblePosition = db.getInt(TinyDB.CHAT_ADAPTER_POSITION, lastFirstVisiblePosition);
//                linearLayout.scrollToPosition(lastFirstVisiblePosition);
//                rv.scrollToPosition(lastFirstVisiblePosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //LOCATION
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
        //SAVE
        try {
            if (linearLayout != null) {
                int lastFirstVisiblePosition = linearLayout.findFirstCompletelyVisibleItemPosition();
                db.putInt(TinyDB.CHAT_ADAPTER_POSITION, lastFirstVisiblePosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Them doan nay do getLastLocation() return null
     */
    protected void startLocationUpdates() {

        // Request location updates
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mRequestingLocationUpdates = true;
        }


    }

    protected void stopLocationUpdates() {
        try {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mRequestingLocationUpdates = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocation(location);
        //lay duoc toa do thi dung cap nhat
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
//        sendLocation();
    }

    double lat = 0.0, lon = 0.0;
    private String address = "";

    private void setLocation(Location location) {
        if (location != null) {
            mLastLocation = location;
            lat = location.getLatitude();
            lon = location.getLongitude();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    address = MyUtils.getAddress(context, String.valueOf(lat), String.valueOf(lon));
                    return null;
                }
            }.execute();
        }
    }

    private void selectLocation() {
        Intent intent = new Intent(context, SearchLocationActivity.class);
        startActivityForResult(intent, ACTION_SELECT_LOCATION);
    }

    boolean isCheckin = true;

    private void sendLocation() {
        if (lat > 0 && lon > 0) {
            if (isSocketConnected() && room != null) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                    obj.put("lat", String.valueOf(lat));
                    obj.put("lng", String.valueOf(lon));
                    obj.put("address", address);
                    obj.put("checkin", isCheckin);
                    obj.put("itemGUID", MyUtils.getGUID());
                    if (originMessage != null) {
                        obj.put("originMessage", originMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                socket.emit("sendLocation", obj, new Ack() {
                    @Override
                    public void call(Object... args) {
                        boolean isSuccess = RoomLog.isSuccess(context, args);
                        if (isSuccess) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    MyUtils.showToast(context, R.string.send_success);
                                    txt.setText("");
                                    setReplyUI(null);
                                    btnScroll.performClick();
                                }
                            });

                        }
                    }
                });
            }
        } else {
            getMyLocation();
        }
    }

    /////////////////////////////////////////////////////////////////////

    private static PendingResult<LocationSettingsResult> result;
    public static boolean isLocationON = false;

    private Status status;

    private void checkLocationSettings() {
        if (builder != null && mGoogleApiClient != null) {
            //dam bao builder da tao truoc do cung voi mGoogleApiClient
            result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            isLocationON = true;

                            break;

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            isLocationON = false;

                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            isLocationON = false;
                            // Location settings are unavailable so not possible to show any dialog now
                        /*if (mLocationSettingsListener != null) {
                            mLocationSettingsListener.onLocationError();
                        }*/
                            MyUtils.showToast(context, "Location is unavailable.");
                            break;
                    }
                }
            });
        }
    }

    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private void getMyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        //bao location update
        startLocationUpdates();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            setLocation(mLastLocation);
        }

    }

    //////////////////////////////////////
    //////////////////////////////////////////
    @BindView(R2.id.imageView1)
    ImageView img1;
    @BindView(R2.id.textView1)
    TextView txt1;
    @BindView(R2.id.textView2)
    TextView txt2;
    @BindView(R2.id.textView3)
    TextView txt3;
    @BindView(R2.id.textView4)
    TextView txt4;
    @BindView(R2.id.linearProfile)
    LinearLayout linearProfile;
    @BindView(R2.id.linearRoot)
    LinearLayout linearRoot;


    /**
     * Vao bang roomId, Item nam trong GROUP, type = "item"
     *
     * @param item
     */
    private void setTopInfo(final Item item) {
        if (item != null) {
            txt1.setText(item.getItemName());
            txt2.setVisibility(View.GONE);

            String price = item.getItemPrice();
            if (price.equalsIgnoreCase("0")) {
                txt3.setText(R.string.lienhe);
            } else {
                txt3.setText(MyUtils.getMoneyFormat(item.getItemPrice()));
            }


            int width = getResources().getDimensionPixelSize(R.dimen.dimen_80dp);
            int height = getResources().getDimensionPixelSize(R.dimen.dimen_60dp);

            String url = item.getItemImage();
            if (!TextUtils.isEmpty(url)) {
                Picasso.get()
                        .load(url)
                        .resize(width, height)
//                        .crossFade()
                        .into(img1);
            }

            linearProfile.setVisibility(View.GONE);

            linearRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item != null) {
                        long id = item.getItemId();
                        if (id > 0) {
                            //todo MH04_ArticleDetailActivity
//                            MyUtils.openMbnApp(context, item.getItemId() + "");



                            /*if (MH04_ArticleDetailActivity.isExists) {
                                //neu la san pham nay thi chi can dong man hinh
                                if (MH04_ArticleDetailActivity.article != null && MH04_ArticleDetailActivity.article.get_id().equalsIgnoreCase(item.getItemId() + "")) {
                                    finish();
                                } else {//dong man hinh do lai
                                    Intent intent = new Intent(MH04_ArticleDetailActivity.ACTION_FINISH);
                                    sendBroadcast(intent);

                                    //mo san pham len
                                    if (MyUtils.checkInternetConnection(context)) {
                                        if (item != null && item.getItemId() > 0) {
                                            String articleId = item.getItemId() + "";
                                            if (!TextUtils.isEmpty(articleId)) {
                                                //lay chi tiet
                                                getArticleDetailNormal(articleId);
                                            }
                                        }
                                    } else {
                                        MyUtils.showThongBao(context);
                                    }

                                }
                            } else {//ko ton tai thi lay article roi moi vao man hinh chi tiet

                                //mo san pham len
                                if (MyUtils.checkInternetConnection(context)) {
                                    if (item != null && item.getItemId() > 0) {
                                        String articleId = item.getItemId() + "";
                                        if (!TextUtils.isEmpty(articleId)) {
                                            //lay chi tiet
                                            getArticleDetailNormal(articleId);
                                        }
                                    }
                                } else {
                                    MyUtils.showThongBao(context);
                                }
                            }*/
                        } else {
                            //mo trang web
                            MyUtils.goToWeb(context, item.getItemLink());
                        }
                    }
                }
            });

            linearRoot.setVisibility(View.VISIBLE);

        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    private ProgressDialog dialog;


    private void dissmissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    ///////////////////////////////////////////
    private void addImageHolder(String uuid, String type, ArrayList<String> paths) {
        if (adapter != null && room != null) {


            RoomLog log = new RoomLog();
            log.setGroupDate(false);
            log.setRoomId(room.get_id());
            log.setContent(null);
            log.setUploading(true);
            if (owner != null) {
                log.setUserIdAuthor(owner.get_id());
            }
            log.setType(type);
            log.setDeleted(false);
            log.setItemGUID(uuid);
            log.set_id("0");
            log.setPaths(paths);//albums

            if (type == ContentType.IMAGE) {
                if (paths != null && paths.size() > 0) {
                    log.setImgThumbnailLocal(paths.get(0));//imgPathThumb
                }
                log.setThumbWidth(widthThumb);
                log.setThumbHeight(heightThumb);
            }

            adapter.initMapPercent();
            adapter.append(log);
            hideEmptyView();
            btnScroll.performClick();

        }
    }

    private void updateLogViewed() {
        if (adapter != null) {
            //cap nhat la da doc logid
            setLogIsView(adapter.getLastLogId_Bottom());
        }
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

                    if (!isFinishing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(args[0].toString());
                                    int code = obj.getInt("errorCode");
                                    if (code == 0) {
                                        adapter.setPinMessage(logId, isPin);
                                        MyUtils.showToast(context, R.string.success);
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
                }
            });
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isFindPinMessage = false;

    /**
     * Lấy danh sách mới hoàn toàn, lấy ds trước và sau 1 log với số lượng 30
     *
     * @param logId
     */
    private void getRoomNearbyLogs(final String logId) {
        if (isSocketConnected() && adapter != null && !TextUtils.isEmpty(logId)) {

            isFindPinMessage = true;

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", roomId);
                obj.put("logId", logId);
                obj.put("itemCount", PAGE_ITEM);//PAGE_ITEM(15) * 2 + 1 = 31 items
            } catch (JSONException e) {
                e.printStackTrace();
            }

            isLoading = true;
            socket.emit("getRoomNearbyLogs", obj, new Ack() {
                @Override
                public void call(Object... args) {

                    if (!isFinishing()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final ArrayList<RoomLog> list = RoomLog.parseListRoomLog(context, args);
                                if (list != null && list.size() > 0) {
                                    if (adapter != null) {

                                        adapter.clearData();
                                        Collections.reverse(list);
                                        adapter.append(list);


                                        scrollToLog(logId);


                                        //danh dau tin nay da xem
                                        setLogIsView(logId);

                                        hideEmptyView();
                                    }

                                }

//                                MyUtils.howLong(startGlobal, "when get history from id");


                            }
                        });
                    }

                    isLoading = false;
                }
            });

        }
    }

    private void scrollToLog(String chatLogId) {
        if (!TextUtils.isEmpty(chatLogId) && adapter != null) {
            //cho cho view tao xong thi hien hieu ung highlight dong
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //scroll den vi tri vua lay ve
                    int position = adapter.getPostionOfLog(chatLogId);
                    if (position >= 0 && position < adapter.getItemCount()) {
                        rv.scrollToPosition(position);
                        if (adapter != null)
                            adapter.changeBackgroundFound(position);

                    }
                }
            }, 1000);

        }
    }

    /**
     * Add vao vi tri 0
     *
     * @param logId
     */
    private void getRoomPreviousLogs(final String logId) {
        if (isSocketConnected() && room != null && adapter != null && !TextUtils.isEmpty(logId)) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
                obj.put("logId", logId);
                obj.put("itemCount", PAGE_ITEM);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MyUtils.log("load message getRoomPreviousLogs on top");
            isLoading = true;
            socket.emit("getRoomPreviousLogs", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    final ArrayList<RoomLog> list = RoomLog.parseListRoomLog(context, args);
                    if (list != null && list.size() > 0) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //* Add vao vi tri top
                                Collections.reverse(list);
                                adapter.append(list, adapter.getItemCount());


                                //scroll den vi tri vua lay ve
                                /*if (!TextUtils.isEmpty(logId)) {
                                    int position = adapter.getPostionOfLog(logId);
//                                    if (position - 2 > 0) position = position - 2;
                                    if (position >= 0 && position < adapter.getItemCount()) {
                                        rv.scrollToPosition(position);
                                    }
                                }*/

                                hideEmptyView();

                            }
                        });
                    }
                    isLoading = false;
                }
            });

        }
    }

    /**
     * Add vao vi tri 0
     *
     * @param logId
     */
    private void getRoomNextLogsBottom(final String logId) {
        if (isSocketConnected() && room != null && adapter != null && !TextUtils.isEmpty(logId)) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
                obj.put("logId", logId);
                obj.put("itemCount", PAGE_ITEM);
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            MyUtils.log("load message getRoomNextLogsBottom on bottom");
//            progressWheel.setVisibility(View.VISIBLE);
            isLoading = true;
            socket.emit("getRoomNextLogs", obj, new Ack() {
                @Override
                public void call(final Object... args) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ArrayList<RoomLog> list = RoomLog.parseListRoomLog(context, args);
                            if (list != null && list.size() > 0) {

                                //* Add vao vi tri bottom - 0 do la recyclerview reverse
                                Collections.reverse(list);
                                adapter.append(list, 0);


                                //scroll den vi tri vua lay ve
                                /*if (!TextUtils.isEmpty(logId)) {
                                    int position = adapter.getPostionOfLog(logId);
//                                    if (position + 2 < adapter.getItemCount()) position = position + 2;
                                    if (position >= 0 && position < adapter.getItemCount()) {
                                        rv.scrollToPosition(position);
                                    }
                                }*/

                                hideEmptyView();

                            }
                            isLoading = false;
//                            progressWheel.setVisibility(View.GONE);

                        }
                    });


                }
            });

        }
    }


    private AlertDialog alertDialog = null;

    private void showPopupChannelInfo(Room room) {

        if (room != null) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

            // Create a alert dialog builder.
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // Set icon value.
            builder.setIcon(R.mipmap.ic_launcher);
            // Set title value.
//            builder.setTitle("Custom ChatView Alert Dialog");

            // Get custom login form view.
            final View v = getLayoutInflater().inflate(R.layout.activity_08_confirm_join_channel, null);
            // Set above view in alert dialog.
            builder.setView(v);

            // Register button click listener.
            ImageView img1 = (ImageView) v.findViewById(R.id.img1);
            ImageView img2 = (ImageView) v.findViewById(R.id.img2);
            TextView txt1 = (TextView) v.findViewById(R.id.txt1);
            TextView txt2 = (TextView) v.findViewById(R.id.txt2);
            TextView txt3 = (TextView) v.findViewById(R.id.txt3);
            Button btn1 = (Button) v.findViewById(R.id.btn1);
            final Button btn2 = (Button) v.findViewById(R.id.btn2);

            //set UI

            txt1.setText(room.getRoomName());
            txt2.setText(getString(R.string.number_members, room.getMembers().size()));
            txt3.setText(room.getDescription());

            int avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size_large);
            if (!TextUtils.isEmpty(room.getRoomAvatar())) {
                Picasso.get()
                        .load(room.getRoomAvatar())
                        .resize(avatarSize, avatarSize)
                        .centerCrop()
                        .placeholder(R.drawable.ic_channel)
                        .transform(new CropCircleTransformation())
                        .into(img1);
            }


            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //chap nhan vao, goi add member vao channel
                    addRoomMember();

                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    finish();
                }
            });
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn2.performClick();
                }
            });


            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();

        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addRoomMember() {
        if (isSocketConnected() && room != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());

                JSONObject child = new JSONObject();

                child.put("userId", owner.get_id());
                child.put("name", owner.getName());
                child.put("phone", owner.getPhone());
                child.put("email", owner.getEmail());
                child.put("avatar", owner.getAvatar());
                child.put("url", owner.getUrl());

                obj.put("memberInfo", child);
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit("addRoomMember", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
//                    MyUtils.log(args.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyUtils.showToast(context, R.string.add_member_success);
                            if (alertDialog != null && alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }

                            //refresh list recent chat room
                            Intent intent = new Intent(RecentChat_Fragment.REFRESH_RECENT_CHAT_ROOM);
                            sendBroadcast(intent);

                            //vào room
                            if (room != null) {
                                ////do room nay moi tao, thieu quyen, nen phai goi lay lai
                                roomId = room.get_id();
                                room = null;
                                initRoomChatWithRoomId(roomId);
                            }
                        }
                    });

                }
            });
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_CODE_TAKE_PICTURE = 555;

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(this, BuildConfig.LIBRARY_PACKAGE_NAME + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = storageDir + "/" + imageFileName + ".jpg";
        File image = null;
        try {
            image = new File(path);
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


    private static final int CAPTURE_MEDIA = 666;

    private void takeOrRecordVideo() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        AnncaConfiguration.Builder builder = new AnncaConfiguration.Builder(activity, CAPTURE_MEDIA);
        builder.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
        builder.setMediaResultBehaviour(AnncaConfiguration.PREVIEW);
//        builder.setMinimumVideoDuration(1000);
        builder.setVideoDuration(1 * 60 * 1000);
        new Annca(builder.build()).launchCamera();
    }

    private void takeOrRecordVideo2() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        AnncaConfiguration.Builder videoLimited = new AnncaConfiguration.Builder(activity, CAPTURE_MEDIA);
//        videoLimited.setMediaAction(AnncaConfiguration.MEDIA_ACTION_VIDEO);
        videoLimited.setMediaQuality(AnncaConfiguration.MEDIA_QUALITY_MEDIUM);
        videoLimited.setVideoFileSize(20 * 1024 * 1024);
        videoLimited.setMinimumVideoDuration(1 * 60 * 1000);
        new Annca(videoLimited.build()).launchCamera();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private String videoPath = "";

    private void processUploadMedia(final String path) {
        if (!TextUtils.isEmpty(path)) {
//            String mimeType = Utils.getMimeType(path);
            String mimeType = Utils.getMimeType(path, context);

            if (mimeType != null) {
                if (mimeType.contains("video")) {
//                        displayVideo(savedInstanceState);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            //neu co thi xoa khoi array list
                            resetPath();
                            //xoa 1 ptu ra khoi queue
                            if (paths != null && paths.size() > 0 && paths.contains(path)) {
                                paths.remove(path);
                            }

                            String inputSize = FileUtils.getFileSize(path);
                            try {
                                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//                            videoPath = SiliCompressor.with(context).compressVideo(path, storageDir.getAbsolutePath());//,720,1280,1000
                                String outputSize = FileUtils.getFileSize(path);

                                //file
                                filePath = path;
                                fileName = MyUtils.getFileNameAndExtension(filePath);


                                //file size
                                File f = new File(filePath);
                                fileSize = MyUtils.getSizeBytes(f.length());

                                //extract bitmap from video
                                myBitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);

                                //thumbnail
                                Bitmap thumb = MyUtils.getResizedBitmap(myBitmap, getResources().getDimensionPixelSize(R.dimen.img_size_thumb));
                                widthThumb = thumb.getWidth();
                                heightThumb = thumb.getHeight();
                                //luu xuong, gan vao 2 path full va thumb
                                imgPathThumb = MyUtils.createImageFile(context).getAbsolutePath();
                                MyUtils.saveBitmapThumb(thumb, imgPathThumb);


                                f = new File(imgPathThumb);
                                sizeThumb = MyUtils.getSizeBytes(f.length());


                                //video width/height
                                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                mediaMetadataRetriever.setDataSource(filePath);
                                String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                                String videoW = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                                String videoH = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

                                videoWidth = Integer.parseInt(videoW);
                                videoHeight = Integer.parseInt(videoH);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            //tao sas
                            generateUploadSAS(fileName, ContentTypeInChat.VIDEO);
                        }
                    }.execute();
                } else if (mimeType.contains("image")) {
                    isCapture = true;
                    if (MyUtils.checkInternetConnection(context)) {
                        uploadImage(mCurrentPhotoPath = path);
                    } else {
                        MyUtils.showThongBao(context);
                    }
                }
            }
        }
    }


    @BindView(R2.id.linearExpand)
    LinearLayout linearExpand;
    @BindViews({R2.id.linear1, R2.id.linear2, R2.id.linear3, R2.id.linear4, R2.id.linear5})
    List<LinearLayout> linears;
    @BindViews({R2.id.img1, R2.id.img2, R2.id.img3, R2.id.img4, R2.id.img5})
    List<ImageView> imgs;
    private int[] imgRes = {
            R.drawable.ic_location_120,
            R.drawable.ic_file_120,
            R.drawable.ic_voice_120,
            R.drawable.ic_contacts_120,
            R.drawable.ic_schedule_120
    };

    private void intExpandItem() {
        //mac dinh an
        linearExpand.setVisibility(View.GONE);
        linearRecorder.setVisibility(View.GONE);


        //set size image fit screen
        int screenWidth = MyUtils.getScreenWidth(context);
        int size = screenWidth / 10;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        for (int i = 0; i < imgs.size(); i++) {
            imgs.get(i).setLayoutParams(params);
            Picasso.get()
                    .load(imgRes[i])
                    .resize(size, size)
                    .into(imgs.get(i));
        }

        linearAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //luon an
                if (linearRecorder.getVisibility() != View.GONE) {
                    linearRecorder.setVisibility(View.GONE);
                    imgAdd.setRotation(0);
                } else {
                    //expand
                    if (linearExpand.getVisibility() == View.GONE) {
                        isNeedExpand = true;
                        MyUtils.hideKeyboard(activity);
                        linearExpand.setVisibility(View.VISIBLE);
                        //close
                        imgAdd.setRotation(45);
                    } else {
                        isNeedExpand = false;
                        linearExpand.setVisibility(View.GONE);
                        //add
                        imgAdd.setRotation(0);
                    }
                }

            }
        });

        //vi tri
        linears.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = checkPermissionLocation();
                if (!check) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ChatFragment.this.requestPermissions(arrPermissionsLocation, PERMISSION_REQUEST_CODE_LOCATION);
                    }
                } else {
                    if (MyUtils.checkInternetConnection(context)) {
                        if (!isLocationON) {//GPS CHUA BAT THI BAO BAT
                            try {

                                //Doan code nay se hien thi popup y/c bat gps, lan sau vao gps se tu dong bat do da duoc nguoi dung OK
                                // This line will check the result and prompt a dialog if the device location settings is not enabled
                                if (status != null) {
                                    isSendLocation = true;
                                    status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                                }

                            } catch (IntentSender.SendIntentException e) {
                            }
                        } else {
                            //neu da bat gps
//                            sendLocation();
                            selectLocation();
                        }
                    } else {
                        MyUtils.showThongBao(context);
                    }
                }


                //restore ve trang thai ban dau
                restoreStateInit();

            }
        });

        //tập tin
        linears.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera
                if (checkPermission()) {
                    if (MyUtils.checkInternetConnection(context)) {
                        isSelectImageOrFile = true;
                        FilePickerBuilder.getInstance().setMaxCount(10).pickFile(activity);
                    } else {
                        MyUtils.showThongBao(context);
                    }

                } else {
                    requestPermission(PERMISSION_REQUEST_CAMERA_AND_STORAGE_3);
                }

                //restore ve trang thai ban dau
                restoreStateInit();

            }
        });

        //record
        linears.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.checkInternetConnection(context)) {
                    if (linearRecorder.getVisibility() == View.GONE) {//mo ghi am, dong menu
                        isNeedExpand = true;
                        MyUtils.hideKeyboard(activity);
                        linearRecorder.setVisibility(View.VISIBLE);
                        //close
                        imgAdd.setRotation(45);
                        linearExpand.setVisibility(View.GONE);
                    } else {
                        isNeedExpand = false;
                        linearRecorder.setVisibility(View.GONE);
                        //add
                        imgAdd.setRotation(0);
                    }
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

        //goi lien he
        linears.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MO CHON USER
                //add thanh vien
                if (MyUtils.checkInternetConnection(context)) {
                    Intent intent = new Intent(context, SearchUserChatFromRestApiActivity.class);
                    intent.putExtra(SearchUserChat2Activity.IS_ADD_MEMBER, true);
                    startActivityForResult(intent, FIND_MEMBER_CODE);

                    //restore ve trang thai ban dau
                    restoreStateInit();

                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

        //lich hen
        linears.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MO CHON USER
                //add thanh vien
                if (MyUtils.checkInternetConnection(context)) {

                    if (!isLocationON) {//GPS CHUA BAT THI BAO BAT
                        try {
                            //Doan code nay se hien thi popup y/c bat gps, lan sau vao gps se tu dong bat do da duoc nguoi dung OK
                            // This line will check the result and prompt a dialog if the device location settings is not enabled
                            if (status != null) {
                                isSendLocation = false;
                                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            }

                        } catch (IntentSender.SendIntentException e) {
                        }
                    } else {
                        createPlan();
                    }


                    //restore ve trang thai ban dau
                    restoreStateInit();

                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });


    }

    private void createPlan() {
        //neu da bat gps
        Intent intent = new Intent(context, MH01_Create_Plan.class);
        startActivity(intent);
    }

    private void restoreStateInit() {
        //restore ve trang thai ban dau
        imgAdd.setRotation(0);
        linearExpand.setVisibility(View.GONE);
        linearRecorder.setVisibility(View.GONE);
    }

    private boolean isNeedExpand = false;
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    MediaRecorder myAudioRecorder;
    String voicePath;
    private int voiceDuration = 0;

    private void initRecorder() {
        //init views
        initRecordViews();

    }

    private void start() {
        try {
            //init recorder
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            voicePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + timeStamp + ".mp3";
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setOutputFile(voicePath);

            //bat dau ghi am
            myAudioRecorder.prepare();
            myAudioRecorder.start();

            //dem thoi gian
            startTime();
        } catch (IllegalStateException ise) {
            // make something ...
        } catch (IOException ioe) {
            // make something
        }
    }

    private void stop() {
        if (myAudioRecorder != null) {
            myAudioRecorder.stop();
            myAudioRecorder.reset();
            myAudioRecorder.release();
            myAudioRecorder = null;

            //dung thoi gian
            stopTime();
        }
    }

    private void play(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.linearRecorder)
    LinearLayout linearRecorder;
    @BindView(R2.id.txtTime)
    TextView txtTime;
    @BindView(R2.id.imgClose)
    ImageView imgClose;
    @BindView(R2.id.btnRecord)
    Button btnRecord;
    @BindView(R2.id.imgSend)
    ImageView imgSend;
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isRecording = false;

    private void initRecordViews() {
        //set view mac dinh ban dau
        setView(isRecording);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //co quyen ghi am
                if (checkPermission()) {
                    isRecording = !isRecording;
                    setView(isRecording);
                } else {
                    requestPermission(PERMISSION_REQUEST_RECORD);
                }

            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop ghi am, reset trang thai ve dau
                stop();
                imgClose.setVisibility(View.INVISIBLE);
                isRecording = false;
                btnRecord.setText(R.string.record);
                imgClose.setVisibility(View.INVISIBLE);
                imgSend.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setView(boolean isRecording) {
        if (isRecording) {
            btnRecord.setText(R.string.send);
            imgClose.setVisibility(View.VISIBLE);
            imgSend.setVisibility(View.INVISIBLE);

            //Bat dau ghi am
            start();

        } else {
            btnRecord.setText(R.string.record);
            imgClose.setVisibility(View.INVISIBLE);
            imgSend.setVisibility(View.INVISIBLE);

            //Dung ghi am
            stop();

            //play thu
//            play(voicePath);


            if (!TextUtils.isEmpty(voicePath)) {
                //lay thong tin upload len server
                //tao sas
                filePath = voicePath;
                fileName = MyUtils.getFileNameAndExtension(filePath);
                //file size
                File f = new File(filePath);
                fileSize = MyUtils.getSizeBytes(f.length());
                voiceDuration = MyUtils.getAudioDuration(f);
                generateUploadSAS(fileName, ContentTypeInChat.VOICE);
                //upload azure
                //goi ham send file
            }


        }
    }

    private void startTime() {
        //reset
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;

        //bat dau
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

    }

    private void stopTime() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        txtTime.setText("00:00");
    }

    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
//            int milliseconds = (int) (updatedTime % 1000);
            txtTime.setText("" + mins + ":"
                    + String.format("%02d", secs) /*+ ":"
                    + String.format("%03d", milliseconds)*/);
            customHandler.postDelayed(this, 0);
        }

    };
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendPlan(final PlanModelLocal plan) {
        if (isSocketConnected() && room != null && plan != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
                obj.put("title", plan.getTitle());
                obj.put("timeStamp", plan.getTimestamp());
                obj.put("duration", plan.getDuration());
                if (plan.getPlace() != null) {
                    obj.put("place", plan.getPlace().getPlaceAsJsonObject());
                }
                obj.put("note", plan.getNote());
                obj.put("itemGUID", MyUtils.getGUID());
                if (originMessage != null) {
                    obj.put("originMessage", originMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("sendPlan", obj, new Ack() {
                @Override
                public void call(final Object... args) {
//                    MyUtils.log("OK");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //parse chat log
                            RoomLog log = RoomLog.parseRoomLog(context, args);
                            CalendarManager manager = new CalendarManager(context);
                            manager.createEvent(log.get_id(), plan);

                            setReplyUI(null);
                        }
                    });
                }
            });
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RoomLogEvent_Reply event) {
        if (event != null) {
            RoomLog log = event.getMessage();
            if (log != null) {
                setReplyUI(log);
                txt.requestFocus();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
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

    private JSONObject originMessage = null;

    private void setReplyUI(RoomLog m) {


        if (m == null || m.isDeleted()) {//khong co reply, an giao dien
            relativeRely.setVisibility(View.GONE);
            originMessage = null;
        } else {

            Logger.d(m.getType());

            relativeRely.setVisibility(View.VISIBLE);
            imgReplyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    relativeRely.setVisibility(View.GONE);
                    originMessage = null;
                }
            });
            //todo set hinh, ten, description
            if (m.getAuthorInfo() != null) {
                String name = m.getAuthorInfo().getName();
                txtReply1.setText(name);
            }

            if (adapter != null) {
                //get description
                String desc = adapter.getContentReplyForward(m.getType(), m.getContent());
                txtReply2.setText(desc);
            }


            //set hinh
            imgReply.setVisibility(View.GONE);
            String link = "";
            int resId = 0;
            try {
                if (m != null) {
                    switch (m.getType()) {
                        case ContentType.TEXT:
//                        Text t = new Gson().fromJson(m.getContent().toString(), Text.class);
//                        text = Utils.copyMentionUserName(t.getContent());
                            break;
                        case ContentType.IMAGE:
                            Image i = new Gson().fromJson(m.getContent().toString(), Image.class);
                            link = i.getLink();
                            break;
                        case ContentType.FILE:
//                        File f = new Gson().fromJson(m.getContent().toString(), File.class);
//                        text = f.getLink();
                            resId = R.drawable.icon_file_doc;
                            break;
                        case ContentType.VIDEO:
//                        Video v = new Gson().fromJson(m.getContent().toString(), Video.class);
//                        text = v.getLink();
                            resId = R.drawable.ic_record_video;
                            break;
                        case ContentType.LINK:
//                        Link link = new Gson().fromJson(m.getContent().toString(), Link.class);
//                        text = link.getLink();
                            resId = R.drawable.ic_link_light_blue_500_48dp;
                            break;
                        case ContentType.LOCATION:
                        /*Location location = new Gson().fromJson(m.getContent().toString(), Location.class);
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
//                        Item item = new Gson().fromJson(m.getContent().toString(), Item.class);
//                        text = item.getItemLink();
                            break;
                        case ContentType.ALBUM:
                            JsonObject json = m.getContent();
                            JsonArray items = json.getAsJsonArray("items");
                            java.lang.reflect.Type type = new TypeToken<List<AlbumItem>>() {
                            }.getType();
                            final ArrayList<AlbumItem> album = new Gson().fromJson(items.toString(), type);
                            if (album != null && album.size() > 0) {
                                link = album.get(0).getThumbLink();
                            }
                            break;
                        case ContentType.VOICE:
//                            Voice voice = new Gson().fromJson(m.getContent().toString(), Voice.class);
//                            text = voice.getLink();
                            resId = R.drawable.ic_voice_120;
                            break;
                        case ContentType.CONTACT:
                            Contact contact = new Gson().fromJson(m.getContent().toString(), Contact.class);
                            link = contact.getAvatar();
                            break;
                        case ContentType.PLAN:
//                            PlanModel plan = new Gson().fromJson(m.getContent().toString(), PlanModel.class);
//                            text = plan.getTitle();
                            resId = R.drawable.ic_meet_card_2;
                            break;
                        case ContentType.NEW_PAYMENT:
                            Logger.d(m.getType());
                            break;
                    }

                    if (!TextUtils.isEmpty(link)) {
                        int avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size_64);
                        int radius = getResources().getDimensionPixelSize(R.dimen.radius_image);
                        Picasso.get()
                                .load(link)
                                .resize(avatarSize, avatarSize)
                                .centerCrop()
                                .placeholder(R.drawable.no_media_small)
                                .transform(new RoundedCornersTransformation(radius, 0, RoundedCornersTransformation.CornerType.ALL))
                                .into(imgReply);
                        imgReply.setVisibility(View.VISIBLE);
                    } else if (resId > 0) {
                        imgReply.setImageResource(resId);
                        imgReply.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //tao originMessage
            originMessage = createOriginMessage(m);


        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private JSONObject createOriginMessage(RoomLog m) {
        JSONObject origin = null;
        if (m != null) {
            //tao originMessage
            OriginMessage log = new OriginMessage();
            log.setRoomid(m.getRoomId());
            log.setId(m.get_id());
            log.setType(m.getType());
            log.setContent(m.getContent());
            log.setAuthorinfo(m.getAuthorInfo());
            String json = new Gson().toJson(log, OriginMessage.class);
            try {
                origin = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return origin;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private void forwardMessage(RoomLog log) {
        if (isSocketConnected() && room != null && log != null) {

            //tao originMessage local khong dinh den originMessage ben ngoai
            JSONObject json = createOriginMessage(log);
            if (json != null) {
                //goi
                isSending = true;
                progressWheelCenter.setVisibility(View.VISIBLE);
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                    obj.put("itemGUID", MyUtils.getGUID());
                    if (json != null) {
                        obj.put("originMessage", json);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("forwardMessage", obj, new Ack() {
                    @Override
                    public void call(Object... args) {

                        boolean isSuccess = RoomLog.isSuccess(context, args);
                        if (isSuccess) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressWheelCenter.setVisibility(View.GONE);
                                }
                            });

                        }

                        isSending = false;

                    }
                });
            }


        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void startTaskUploadImage(SasModel model, int contentType, String uuid) {
        uploadImageTask = new UploadImageTask(model, contentType, uuid);
        uploadImageTask.execute();
    }

    private UploadImageTask uploadImageTask;
    class UploadImageTask extends UploadItemTask {
        SasModel model;
        int contentType;
        String uuid;
        String fileName;

        public UploadImageTask(SasModel model, int contentType, String uuid) {
            this.model = model;
            this.contentType = contentType;
            this.uuid = uuid;
            this.fileName = model.getFileName();
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            if (model != null) {

                //tong size upload, reset
                lengthSum = 0;
                totalBytes = 0;
                pathUploading = picUri.getPath();

                //length sum of thumb and full image
                File f = new File(imgPathThumb);
                lengthSum += f.length();
                f = new File(imgPathFull);
                lengthSum += f.length();

                //upload
                boolean isSuccess = uploadFile(model.getSasThumbUrl(), imgPathThumb, contentType, this);
                if (isSuccess) {
                    isSuccess = uploadFile(model.getSasUrl(), imgPathFull, contentType, this);
                    if (isSuccess) {
                        if (isSendAlbum) {
                            JSONObject json = new JSONObject();
                            try {
                                json.put("name", fileName);
                                json.put("extension", fileName.substring(fileName.lastIndexOf('.') + 1));
                                json.put("link", model.getLink());
                                json.put("thumbLink", model.getThumbLink());
                                json.put("width", widthFull);
                                json.put("height", heightFull);
                                json.put("size", sizeFull);
                                json.put("thumbWidth", widthThumb);
                                json.put("thumbHeight", heightThumb);
                                json.put("thumbSize", sizeThumb);
                                json.put("isVideo", false);
                                json.put("videoLength", 0);//durations

                                albums.put(json);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {

                if (!isSendAlbum) {//chi 1 hinh
                    //goi ham send image
                    sendImage(model, fileName, uuid);
                } else {
                    //send album
                    //thanh cong thi kiem tra trong queue neu con thi goi tiep
                    if (paths.size() > 0) {
                        String path = paths.get(0);
                        //album upload hinh tiep theo
                        if (adapter != null) {
                            adapter.setImageHolderAlbumUploading(path);
                        }
                        processUploadMedia(path);
                    } else {
                        //da goi het hinh
                        sendAlbum(uuidAlbum);
                    }
                }


            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void startTaskUploadVideo(SasModel model, int contentType, String uuid) {
        uploadVideoTask = new UploadVideoTask(model, contentType, uuid);
        uploadVideoTask.execute();
    }

    private UploadVideoTask uploadVideoTask;

    private class UploadVideoTask extends UploadItemTask {

        SasModel model;
        int contentType;
        String uuid;
        String fileName;

        public UploadVideoTask(SasModel model, int contentType, String uuid) {
            this.model = model;
            this.contentType = contentType;
            this.uuid = uuid;
            this.fileName = model.getFileName();
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            //tong size upload, reset
            lengthSum = 0;
            totalBytes = 0;
            pathUploading = imgPathThumb;

            //length sum of thumb and full image
            File f = new File(imgPathThumb);
            lengthSum += f.length();
            f = new File(filePath);
            lengthSum += f.length();

            //upload
            boolean isSuccess = uploadFile(model.getSasThumbUrl(), imgPathThumb, ContentTypeInChat.IMAGE, this);
            if (isSuccess) {//upload video
                isSuccess = uploadFile(model.getSasUrl(), filePath, contentType, this);
                if (isSuccess) {

                    if (isSendAlbum) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("name", fileName);
                            json.put("extension", fileName.substring(fileName.lastIndexOf('.') + 1));
                            json.put("link", model.getLink());
                            json.put("thumbLink", model.getThumbLink());
                            json.put("width", videoWidth);
                            json.put("height", videoHeight);
                            json.put("size", fileSize);
                            json.put("thumbWidth", widthThumb);
                            json.put("thumbHeight", heightThumb);
                            json.put("thumbSize", sizeThumb);
                            json.put("isVideo", true);
                            json.put("videoLength", 0);//durations

                            albums.put(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {

                if (isSendAlbum == false) {//chi 1 hinh
                    //goi ham send video
                    sendVideo(model, fileName, uuid);
                } else {
                    //send album
                    //thanh cong thi kiem tra trong queue neu con thi goi tiep
                    if (paths.size() > 0) {
//                                                uploadImage(paths.get(0));
                        processUploadMedia(paths.get(0));
                    } else {
                        //da goi het video
                        sendAlbum(uuidAlbum);
                    }
                }


            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //PARENT TASK
    private class UploadItemTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values != null && values.length > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int percent = values[0];
                        //            MyUtils.showToastDebug(context, "percent = " + percent);
//                        MyUtils.log("percent = " + percent);
                        if (adapter != null) {
                            if (isSendAlbum) {
                                adapter.setPercentAlbumUploading(pathUploading, percent);
                            } else {
                                adapter.setPercentImageUploading(pathUploading, percent);
                            }

                        }
                    }
                });
            }
        }

        public long lengthSum = 0;
        public long totalBytes = 0;
        public String pathUploading = "";

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void startTaskUploadFile(SasModel model, int contentType, String uuid, String filePath) {
        uploadFileTask = new UploadFileTask(model, contentType, uuid, filePath);
        uploadFileTask.execute();
    }

    private UploadFileTask uploadFileTask;

    //goi file
    private class UploadFileTask extends UploadItemTask {

        SasModel model;
        int contentType;
        String uuid;
        String fileName;
        String filePath;

        public UploadFileTask(SasModel model, int contentType, String uuid, String filePath) {
            this.model = model;
            this.contentType = contentType;
            this.uuid = uuid;
            this.fileName = model.getFileName();
            this.filePath = filePath;

            //restore ve trang thai ban dau
            restoreStateInit();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            //tong size upload, reset
            lengthSum = 0;
            totalBytes = 0;
            pathUploading = filePath;

            //length sum of thumb and full image
            File f = new File(filePath);
            lengthSum += f.length();

            boolean isSuccess = uploadFile(model.getSasUrl(), filePath, contentType, this);
            if (isSuccess) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                switch (contentType) {
                    case ContentTypeInChat.VOICE:
                        sendVoice(model, voiceDuration, fileSize, uuid);
                        break;
                    case ContentTypeInChat.FILE:
                        //goi ham send FILE
                        sendFile(model, fileName, uuid);
                        break;
                }

            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //CHECK DRAW OVERLAY AND NOTIFICATION BLOCK/////////////////////////////////////////////////
    public final static int REQUEST_CODE_OVERLAY = 10101;
    //kiem tra quyen draw overlay
    private void checkDrawOverlay() {
        if (MyUtils.isCanOverlay(context)) {

        } else {

            //yeu cau cap quyen
            // If not, form up an Intent to launch the permission request
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(R.string.allow_bubble_chat_feature);
                dialog.setTitle(R.string.notification);
                dialog.setCancelable(false);
                dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_CODE_OVERLAY);
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////


}