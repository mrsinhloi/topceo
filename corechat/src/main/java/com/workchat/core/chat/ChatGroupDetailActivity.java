package com.workchat.core.chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.StorageUri;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.squareup.picasso.Picasso;
import com.workchat.core.channel.MH01_BanUserActivity;
import com.workchat.core.channel.MH03_AdminUserActivity;
import com.workchat.core.channel.MH03_ChannelActivity;
import com.workchat.core.channel.MH06_PinMessageActivity;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.event.SocketConnectEvent;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.chat.Permission;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.chat.SasModel;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.widgets.NonScrollListView;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerConst;
import io.socket.client.Ack;
import io.socket.client.Socket;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by MrPhuong on 11/30/2017.
 */

public class ChatGroupDetailActivity extends AppCompatActivity {

    private boolean isChannel = false;
    private boolean isChannelOwner = false;
    private boolean isPage = false;
    private Context context = this;

//    @BindView(R2.id.toolbar)
//    Toolbar toolbar;
//    @BindView(R2.id.title)
//    TextView title;

    @BindView(R2.id.imgBack)
    ImageView imgBack;
    @BindView(R2.id.imageView1)
    ImageView img1;
    @BindView(R2.id.imageView2)
    ImageView img2;
    @BindView(R2.id.imageView3)
    ImageView img3;

    @BindView(R2.id.textView1)
    AppCompatTextView txt1;
    @BindView(R2.id.textView2)
    AppCompatTextView txt2;

    @BindView(R2.id.listView1)
    NonScrollListView lv;
    @BindView(R2.id.scrollView2)
    ScrollView sv;
    @BindView(R2.id.cbNotify)
    AppCompatCheckBox cbNotify;
    private boolean isMutedNotify = false;

    private Room room;
    public static final int ADD_MEMBER_CODE = 2017;

    private String ownerId;

    private TinyDB db;
    private boolean isSupport = false;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_detail);
        ButterKnife.bind(this);

        ownerId = ChatApplication.Companion.getUser().get_id();
        db = new TinyDB(this);
        outputFileUri = getCaptureImageOutputUri();

        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        Drawable d = ChatApplication.Companion.getIconBackCustom();
        if (d != null) {
            imgBack.setImageDrawable(d);
        }else {
            imgBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ko phai support thi moi cho doi avatar
                if (!isSupport) {
                    //doi avatar
                    if (MyUtils.checkInternetConnection(context)) {
                        startActivityForResult(getPickImageChooserIntent(), REQUEST_PICK_IMAGE);
                    } else {
                        MyUtils.showThongBao(context);
                    }
                }
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add thanh vien
                if (MyUtils.checkInternetConnection(context)) {
                    /*Intent intent = new Intent(context, SearchUserChat2Activity.class);
                    intent.putExtra(SearchUserChat2Activity.IS_ADD_MEMBER, true);
                    startActivityForResult(intent, ADD_MEMBER_CODE);*/

                    Intent intent = new Intent(context, SearchUserChatFromRestApiActivity.class);
                    intent.putExtra(SearchUserChatFromRestApiActivity.IS_ADD_MEMBER, true);
                    startActivityForResult(intent, ADD_MEMBER_CODE);


                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add thanh vien
                img2.performClick();
            }
        });

        //edit name chat
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeRoomName();
            }


        });


        ///////////////////////////////////////////////////////////////////////////////////////
        //load room detail
        Bundle b = getIntent().getExtras();
        if (b != null) {
            room = b.getParcelable(Room.ROOM);
            if (room != null) {
                txt1.setText(room.getRoomName());
                String avatar = room.getRoomAvatar();
                loadAvatar(avatar);


                //type
                switch (room.getType()) {
                    case Room.ROOM_TYPE_SUPPORT:
                        //ko them/xoa member
                        isSupport = true;
                        initChannel(false);
                        img3.setVisibility(View.GONE);

                        //an danh sach thanh vien
                        linearMembers.setVisibility(View.GONE);

                        break;
                    case Room.ROOM_TYPE_CHANNEL:
                        isChannel = true;
                        initChannel(true);
                        //neu la owner thi Rời kênh -> Xoá kênh
                        if (room != null) {
                            String ownerChannel = ownerId;
                            List<Member> list = room.getMembers();
                            for (int i = 0; i < list.size(); i++) {
                                Member item = list.get(i);
                                if (item.isOwner()) {
                                    ownerChannel = item.getUserId();
                                    break;
                                }
                            }
                            if (ownerChannel == ownerId) {
                                isChannelOwner = true;
                                txtLeftGroup.setText(R.string.remove_channel);
                            }
                        }
                        break;
                    case Room.ROOM_TYPE_CHANNEL_ADMIN://as group
                        isChannel = true;
                        initChannel(true);
                        break;
                    case Room.ROOM_TYPE_PAGE:
                        //khong cho edit va khong xoa member list
                        isPage = true;
                        initChannel(false);
                        img3.setVisibility(View.GONE);
                        break;
                    case Room.ROOM_TYPE_PROJECT:
                        //ko them/xoa member
                        isSupport = true;
                        initChannel(false);
                        img3.setVisibility(View.GONE);
                        break;

                    default:
                        initChannel(false);
                        break;
                }


                //load danh sach thanh vien, so luong >1(minh) thi xem nhu da load lai room
                members = room.getMembers();
                if (members != null && members.size() > 1) {
                    loadMember();
                } else {
                    initRoomChatWithRoomId();
                }

            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////
        //camera
        initCamera();
        ///////////////////////////////////////////////////////////////////////////////////////
        registerReceiver();
        ///////////////////////////////////////////////////////////////////////////////////////
        cbNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMuteNotify(!isChecked);
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////
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

        ///////////////////////////////////////////////////////////////////////////////////////

    }

    private void loadMember() {
        if (room != null) {
            //load danh sach thanh vien
            members = room.getMembers();
            if (members != null && members.size() > 0) {
                adapter = new UserAdapter();
                lv.setAdapter(adapter);
                scrollToTop();

                //tim chinh minh va xem thuoc tinh isMuted
                for (int i = 0; i < members.size(); i++) {
                    Member item = members.get(i);
                    if (ownerId.equals(item.getUserId())) {
                        isMutedNotify = item.isMuted();
                        //minh co phai la admin ko
                        isAdmin = item.isAdmin();
                        break;
                    }
                }
                cbNotify.setChecked(!isMutedNotify);
            }
        }
    }

    private void scrollToTop() {
        // Wait until my scrollView is ready
        sv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ready, move up
                sv.fullScroll(View.FOCUS_UP);
            }
        });
    }

    private void loadAvatar(String avatar) {
        if (!TextUtils.isEmpty(avatar)) {
            //load photo
            int size = getResources().getDimensionPixelSize(R.dimen.photo_profile);
            //tang kich thuoc de hinh ro hon: 0.75, 1, 1.5, 2, 3, 4
            double density = getResources().getDisplayMetrics().density;
            size = (int) (size * density);

            if (img1 != null && context != null) {
                Picasso.get()
                        .load(avatar)
                        .resize(size, size)
                        .centerCrop()
                        .placeholder(R.drawable.ic_account_circle_grey)
                        .transform(new CropCircleTransformation())
                        .into(img1);
            }
        }
    }

    private List<Member> members = new ArrayList<>();
    private UserAdapter adapter;

    private class UserAdapter extends BaseAdapter {
        int size = getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);

        String you = "You";

        public UserAdapter() {
            you = context.getString(R.string.you);
        }

        @Override
        public int getCount() {
            return members.size();
        }

        @Override
        public Member getItem(int arg0) {
            return members.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        class UserHolder {
            LinearLayout linearRoot;
            ImageView img1, img2;
            AppCompatTextView txt1, txt2, txt3;
        }

        UserHolder holder = null;

        @Override
        public View getView(final int arg0, View v, ViewGroup arg2) {

            if (v == null) {
                holder = new UserHolder();
                v = getLayoutInflater().inflate(R.layout.activity_chat_group_detail_row, null);
                holder.img1 = (ImageView) v.findViewById(R.id.imageView1);
                holder.img2 = (ImageView) v.findViewById(R.id.imageView2);
                holder.txt1 = (AppCompatTextView) v.findViewById(R.id.textView1);
                holder.txt2 = (AppCompatTextView) v.findViewById(R.id.textView2);
                holder.txt3 = (AppCompatTextView) v.findViewById(R.id.textView3);
                holder.linearRoot = (LinearLayout) v.findViewById(R.id.linearRoot);

                v.setTag(holder);
            } else {
                holder = (UserHolder) v.getTag();
            }


            final Member member = getItem(arg0);
            if (member != null) {

                if (member.getUserInfo() != null) {
                    String ownerOrAdmin = (member.isOwner()) ? " (Owner) " : "";
                    if (TextUtils.isEmpty(ownerOrAdmin)) {
                        ownerOrAdmin = (member.isAdmin()) ? " (Admin) " : "";
                    }


                    String youString = (member.getUserId().equals(ownerId)) ? " - " + you + " " : "";
                    holder.txt1.setText((member.getUserInfo().getName() + youString));
                    holder.txt2.setText(ownerOrAdmin);

                    String phone = member.getUserInfo().getPhone();
                    if(!TextUtils.isEmpty(phone)) {
                        holder.txt3.setVisibility(View.VISIBLE);
                        holder.txt3.setText(phone);
                    }else{
                        holder.txt3.setVisibility(View.GONE);
                    }


                    String avatar = member.getUserInfo().getAvatar();
                    if (!TextUtils.isEmpty(avatar)) {
                        Picasso.get()
                                .load(avatar)
                                .resize(size, size)
                                .placeholder(R.drawable.ic_user_2)
                                .error(R.drawable.ic_user_2)
                                .transform(new CropCircleTransformation())
                                .into(holder.img1);
                    } else {
                        holder.img1.setImageResource(R.drawable.ic_user_2);
                    }
                }

                if (isPage || isSupport) {
                    holder.img2.setVisibility(View.INVISIBLE);
                } else {
                    //neu la minh, hoac owner thi an
                    if (ownerId.equals(member.getUserId()) || member.isOwner()) {
                        holder.img2.setVisibility(View.INVISIBLE);
                    } else {
                        //neu minh la admin thi co quyen xoa, nguoc lai thi an
                        if (isAdmin) {
                            holder.img2.setVisibility(View.VISIBLE);
                        } else {
                            holder.img2.setVisibility(View.INVISIBLE);
                        }
                    }

                    holder.img2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //xoa khoi nhom
                            confirmRemoveRoomMember(members.get(arg0));
                        }
                    });
                }


                holder.linearRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyUtils.chatWithUser(context, members.get(arg0));
                    }
                });


                //color
                if (member.isOwner()) {
                    holder.txt2.setTextColor(ContextCompat.getColor(context, R.color.red_500));
                } else {
                    holder.txt2.setTextColor(ContextCompat.getColor(context, R.color.orange_500));
                }

            }

            return v;
        }


    }


    /////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        initSocket();

//        sv.post(new Runnable() {
//            public void run() {
//                sv.fullScroll(sv.FOCUS_UP);
//            }
//        });
    }

    private Socket socket;

    private void initSocket() {
        //get data
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketConnectEvent connection) {
        if (connection.isConnected()) {
            initSocket();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //CAMERA
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    Bitmap myBitmap;
    Uri picUri;


    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 108;

    private void initCamera() {
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest != null && permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            MyUtils.showMessageOKCancel(context, getString(R.string.app_need_camera_permission),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int REQUEST_PICK_IMAGE = 200;
    Uri outputFileUri;


    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {
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
//                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

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
        File getImage = MyUtils.createImageFile(context);
        if (getImage != null) {
            outputFileUri = Uri.fromFile(getImage);
        }
        return outputFileUri;

        /*Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    //reset 2 path
                    resetPath();

                    Uri uri = data.getData();//getPickImageResultUri(data);
                    if (uri != null) {
                        picUri = uri;
                        parseUri();
                    } else {
                        Bitmap b = (Bitmap) data.getExtras().get("data");
                        if (b != null) {
                            if (b.getWidth() > b.getHeight()) {
                                b = MyUtils.rotateImage(b, 90);
                            }
                            String path = MyUtils.saveBitmap(b, outputFileUri.getPath());
                            picUri = Uri.fromFile(new File(path));
                            parseUri();
                        }
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

                case ADD_MEMBER_CODE:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Bundle b = data.getExtras();
                        if (b != null) {
                            final ArrayList<UserChatCore> users = b.getParcelableArrayList(UserChatCore.USER_MODEL);
                            if (users != null) {
                                for (int i = 0; i < users.size(); i++) {
                                    final int pos = i;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            addRoomMember(users.get(pos));
                                        }
                                    }, (i == 0) ? 0 : 500);
                                }
                            }
                        }
                    }
                    break;
            }

        }


    }

    //image
    private String imgPathFull = "", imgPathThumb = "";
    private int widthFull = 0, heightFull = 0, widthThumb = 0, heightThumb = 0;
    private float sizeFull = 0.0f, sizeThumb = 0.0f;
    private String fileImageName = "";//ten hinh full

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

        filePath = "";
        fileName = "";
        fileSize = 0.0f;
    }

    private void parseUri() {
        if (picUri != null) {
            try {

                if (picUri.toString().contains("content://com.google.android.apps.photos.contentprovider")) {
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
                //doc len va resize
                myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                myBitmap = MyUtils.rotateImageIfRequired(myBitmap, picUri);
                Bitmap thumb = MyUtils.getResizedBitmap(myBitmap, getResources().getDimensionPixelSize(R.dimen.img_size_thumb));
                Bitmap full = MyUtils.getResizedBitmap(myBitmap, getResources().getDimensionPixelSize(R.dimen.img_size_thumb));//img_size_full

                //doc thong tin width,height
                MyUtils.log("width=" + thumb.getWidth() + ", height=" + thumb.getHeight());
                MyUtils.log("widht=" + full.getWidth() + ", height=" + full.getHeight());
//                imgPreview.setImageBitmap(full);

                widthFull = full.getWidth();
                heightFull = full.getHeight();
                widthThumb = thumb.getWidth();
                heightThumb = thumb.getHeight();


                //luu xuong, gan vao 2 path full va thumb
                imgPathThumb = MyUtils.createImageFile(context).getAbsolutePath();
                imgPathFull = MyUtils.createImageFile(context).getAbsolutePath();
                MyUtils.saveBitmapThumb(thumb, imgPathThumb);
                MyUtils.saveBitmap(full, imgPathFull);

                //doc thong tin size
                File f = new File(imgPathFull);
                sizeFull = MyUtils.getSizeBytes(f.length());

                f = new File(imgPathThumb);
                sizeThumb = MyUtils.getSizeBytes(f.length());


                //tao sas
                fileImageName = MyUtils.getFileNameAndExtension(imgPathFull);


                generateUploadSAS(fileImageName);
                //upload azure
                //goi ham send image


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
    public static Uri getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
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

        return isCamera ? getCaptureImageOutputUri() : data.getData();
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

    private boolean isSocketConnected() {
        socket = ChatApplication.Companion.getSocket();
        if (socket != null) {
            return socket.connected();
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    private void generateUploadSAS(final String fileName) {
        if (isSocketConnected() && room != null) {

            final String uuid = MyUtils.getGUID();

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
                    MyUtils.log(args.toString());
                    final SasModel model = SasModel.parseSasModel(context, args);
                    //upload azure
                    if (model != null) {
                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                //chi up hinh thumnail vao sasURL, 300px
                                boolean isSuccess = uploadFile(model.getSasUrl(), imgPathThumb, true);
                                if (isSuccess) {
                                    return true;
                                }
                                return false;
                            }

                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                super.onPostExecute(aBoolean);
                                if (aBoolean) {
                                    //goi ham send image
                                    sendImage(model, fileName, uuid);
                                }
                            }
                        }.execute();
                    }


                }
            });
        }
    }

    private boolean uploadFile(String sasLink, String imgPath, boolean isImage) {
        if (TextUtils.isEmpty(sasLink) == false) {

            try {
                //Return a reference to the blob using the SAS URI.
                CloudBlockBlob blob = new CloudBlockBlob(new StorageUri(URI.create(sasLink)));
                //blob.setStreamWriteSizeInBytes(256 * 1024);//256 k


                OutputStream oStream = blob.openOutputStream();
                File f = new File(imgPath);
                FileInputStream inputStream = new FileInputStream(f);
                blob.upload(inputStream, f.length());

                //upload properties
                if (isImage) {
                    blob.getProperties().setContentType("image/jpeg");
                    blob.uploadProperties();
                } else {
                    blob.getProperties().setContentType("application/octet-stream");
                    blob.uploadProperties();
                }
                return true;
            } catch (StorageException e) {
                e.printStackTrace();
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
                obj.put("roomAvatar", sas.getLink());
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit(isChannel ? "changeChannelAvatar" : "changeAvatarChatRoom", obj, new Ack() {
                @Override
                public void call(final Object... args) {
                    //return sasUrl, sasThumbUrl (nếu là image)
                    //MyUtils.log(args.toString());
                    final boolean isSuccess = RoomLog.isSuccess(context, args);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isSuccess) {
                                MyUtils.showToast(context, R.string.changed_room_avatar_success);
                                //socket gởi về set lai image
                            }
                        }
                    });

                }
            });
        } else {
            MyUtils.showToast(context, R.string.not_found_socket_connection);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private EditText txt;

    private void changeRoomName() {
        if (room != null) {
            /*new MaterialDialog.Builder(this)
                    .title(isChannel ? R.string.change_channel_name : R.string.change_room_name)
                    .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    .input(getString(R.string.input_room_name), room.getRoomName(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            // Do something
                            if (!TextUtils.isEmpty(input)) {
                                if (MyUtils.checkInternetConnection(context)) {
                                    renameChatRoom(input.toString());
                                    dialog.dismiss();
                                } else {
                                    MyUtils.showThongBao(context);
                                }
                            }
                        }
                    }).show();*/

            ////
            View v = getLayoutInflater().inflate(R.layout.dialog_edittext, null);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle(isChannel ? R.string.change_channel_name : R.string.change_room_name)
                    .setView(v)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (txt != null) {
                                String input = txt.getText().toString().trim();
                                if (!TextUtils.isEmpty(input)) {
                                    if (MyUtils.checkInternetConnection(context)) {
                                        renameChatRoom(input.toString());
                                        dialog.dismiss();
                                    } else {
                                        MyUtils.showThongBao(context);
                                    }
                                }
                            }

                        }
                    });


            AlertDialog dialog = builder.create();
            dialog.show();

            txt = v.findViewById(R.id.editText);
            txt.setHint(R.string.input_room_name);
            txt.setText(room.getRoomName());
            txt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);


        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void renameChatRoom(String roomName) {
        if (isSocketConnected() && room != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
                obj.put("roomName", roomName);
            } catch (Exception e) {
                e.printStackTrace();
            }


            socket.emit(isChannel ? "renameChannel" : "renameChatRoom", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    // socket return sasUrl, sasThumbUrl (nếu là image)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            MyUtils.log(args.toString());
                            MyUtils.showToast(context, R.string.rename_room_success);
                        }
                    });

                }
            });
        }
    }


    private void confirmRemoveRoomMember(final Member member) {
        if (MyUtils.checkInternetConnection(context)) {
            /*new MaterialDialog.Builder(context)
                    .content(R.string.confirm_remove_member_chat)
                    .positiveText(R.string.agree)
                    .negativeText(R.string.huy)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (MyUtils.checkInternetConnection(context)) {
                                removeRoomMember(member);
                            } else {
                                MyUtils.showThongBao(context);
                            }
                        }
                    })
                    .show();*/

            new MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.confirm_remove_member_chat)
                    .setNegativeButton(R.string.huy, null)
                    .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (MyUtils.checkInternetConnection(context)) {
                                removeRoomMember(member);
                            } else {
                                MyUtils.showThongBao(context);
                            }

                        }
                    })
                    .show();


        } else {
            MyUtils.showThongBao(context);
        }
    }

    private void removeRoomMember(Member member) {
        if (isSocketConnected() && room != null && member.getUserInfo() != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());

                JSONObject child = new JSONObject();
                child.put("userId", member.getUserId());
                child.put("name", member.getUserInfo().getName());
                child.put("avatar", member.getUserInfo().getAvatar());

                obj.put("memberInfo", child);
            } catch (Exception e) {
                e.printStackTrace();
            }


            socket.emit("removeRoomMember", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    // socket return sasUrl, sasThumbUrl (nếu là image)
//                    MyUtils.log(args.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyUtils.showToast(context, R.string.removed_member_success);
                        }
                    });

                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addRoomMember(final UserChatCore user) {
        if (isSocketConnected() && room != null && user != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());

                JSONObject child = new JSONObject();
                String userId = user.get_id();

                child.put("userId", userId);
                child.put("name", user.getName());
                child.put("phone", user.getPhone());
                child.put("email", user.getEmail());
                child.put("avatar", user.getAvatar());
                child.put("url", user.getUrl());

                obj.put("memberInfo", child);
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit("addRoomMember", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    // socket return sasUrl, sasThumbUrl (nếu là image)
//                    MyUtils.log(args.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyUtils.showToast(context, R.string.add_member_success);
                            //{"errorCode":101,"error":"Member đã có trong Group"}
                            //UPDATE MAN HINH chi tiet nhom
                            /*Intent intent = new Intent(ChatGroupDetailActivity.ACTION_ADD_MEMBER_2);
                            intent.putExtra(Room.ROOM_ID, room.get_id());
                            intent.putExtra(Member.MEMBER, user.getM);
                            context.sendBroadcast(intent);*/

                        }
                    });

                }
            });
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_UPDATE_ROOM_NAME_2 = "ACTION_UPDATE_ROOM_NAME_2";
    public static final String ACTION_UPDATE_ROOM_AVATAR_2 = "ACTION_UPDATE_ROOM_AVATAR_2";
    public static final String ACTION_ADD_MEMBER_2 = "ACTION_ADD_MEMBER_2";
    public static final String ACTION_REMOVE_MEMBER_2 = "ACTION_REMOVE_MEMBER_2";

    public static final String ACTION_UPDATE_ROOM_MODEL = "ACTION_UPDATE_ROOM_MODEL_1";
    public static final String ACTION_UPDATE_USER_CHANGE_PERMISSION = "ACTION_UPDATE_USER_CHANGE_PERMISSION_" + ChatGroupDetailActivity.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();

                //khi doi ten hoac avatar nhom, phai cung room moi update
                if (intent.getAction().equals(ACTION_UPDATE_ROOM_NAME_2)) {
                    if (room != null) {
                        if (b != null) {
                            String id = b.getString(Room.ROOM_ID, "");
                            if (id.equals(room.get_id())) {
                                String name = b.getString(Room.ROOM_NAME, "");
                                if (!TextUtils.isEmpty(name)) {
                                    room.setRoomName(name);
                                    txt1.setText(name);
                                }
                            }
                        }
                    }
                }
                if (intent.getAction().equals(ACTION_UPDATE_ROOM_AVATAR_2)) {
                    if (room != null) {
                        if (b != null) {
                            String id = b.getString(Room.ROOM_ID, "");
                            if (id.equals(room.get_id())) {
                                String avatar = b.getString(Room.ROOM_AVATAR, "");
                                if (!TextUtils.isEmpty(avatar)) {
                                    room.setRoomAvatar(avatar);
                                    loadAvatar(avatar);
                                }
                            }
                        }
                    }
                }

                if (intent.getAction().equals(ACTION_REMOVE_MEMBER_2)) {
                    if (room != null) {
                        if (b != null) {
                            String id = b.getString(Room.ROOM_ID, "");
                            if (id.equals(room.get_id())) {
                                String userId = b.getString(UserChat.USER_ID, "");
                                if (!TextUtils.isEmpty(userId)) {
                                    if (members != null && members.size() > 0) {
                                        //tim va xoa
                                        for (int i = 0; i < members.size(); i++) {
                                            if (userId.equals(members.get(i).getUserId())) {
                                                members.remove(i);
//                                                room.getMembers().remove(i);
                                                break;
                                            }
                                        }

                                        adapter = new UserAdapter();
                                        lv.setAdapter(adapter);
                                        sv.post(new Runnable() {
                                            public void run() {
                                                sv.fullScroll(sv.FOCUS_UP);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }

                if (intent.getAction().equals(ACTION_ADD_MEMBER_2)) {
                    if (room != null) {
                        if (b != null) {
                            String id = b.getString(Room.ROOM_ID, "");
                            if (id.equals(room.get_id())) {
                                Member member = b.getParcelable(Member.MEMBER);
                                if (member != null) {

                                    //kiem tra ko co thi moi them vao
                                    boolean isExists = false;
                                    for (int i = 0; i < members.size(); i++) {
                                        if (member.getUserId().equals(members.get(i).getUserId())) {
                                            isExists = true;
                                            break;
                                        }
                                    }
                                    if (!isExists) {
                                        members.add(member);
//                                        room.getMembers().add(member);


                                        adapter = new UserAdapter();
                                        lv.setAdapter(adapter);
                                        sv.post(new Runnable() {
                                            public void run() {
                                                sv.fullScroll(sv.FOCUS_UP);
                                            }
                                        });
                                    }


                                }
                            }
                        }
                    }
                }


                if (intent.getAction().equals(ACTION_UPDATE_ROOM_MODEL)) {
                    if (room != null) {
                        if (b != null) {
                            Room r = b.getParcelable(Room.ROOM);
                            if (r != null) {
                                if (room.get_id().equalsIgnoreCase(r.get_id())) {
                                    room = r;
                                    //da co su kien socket set lai title roi
                                    //set lai thong tin UI
                                    initChannel(true);
                                }
                            }
                        }
                    }
                }


                if (intent.getAction().equals(ACTION_UPDATE_USER_CHANGE_PERMISSION)) {
                    if (b != null) {
                        //cap nhat quyen admin
                        Member member = b.getParcelable(Member.MEMBER);
                        String roomId = b.getString(Room.ROOM_ID, "");

                        //update room
                        if (room != null) {
                            List<Member> list = room.getMembers();
                            for (int i = 0; i < list.size(); i++) {
                                Member item = list.get(i);
                                if (item.getUserId().equals(member.getUserId())) {
                                    room.getMembers().set(i, member);
                                    break;
                                }
                            }
                        }
                        //update adapter
                        if (adapter != null) {
                            if (members != null && members.size() > 0) {
                                for (int i = 0; i < members.size(); i++) {
                                    Member item = members.get(i);
                                    if (item.getUserId().equals(member.getUserId())) {
                                        members.set(i, member);
                                        break;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }
                }


            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_ROOM_NAME_2));
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_ROOM_AVATAR_2));

        registerReceiver(receiver, new IntentFilter(ACTION_ADD_MEMBER_2));
        registerReceiver(receiver, new IntentFilter(ACTION_REMOVE_MEMBER_2));

        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_ROOM_MODEL));
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_USER_CHANGE_PERMISSION));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    //group va channel
    @BindView(R2.id.linearAddmember)
    LinearLayout linearAddmember;
    @BindView(R2.id.linearMembers)
    LinearLayout linearMembers;
    //CHANNEL
    @BindView(R2.id.linearInfo)
    LinearLayout linearInfo;
    @BindView(R2.id.linearBanUser)
    LinearLayout linearBanUser;
    @BindView(R2.id.linearAdmin)
    LinearLayout linearAdmin;
    @BindView(R2.id.linearLeftGroup)
    LinearLayout linearLeftGroup;
    @BindView(R2.id.txtLeftGroup)
    AppCompatTextView txtLeftGroup;
    @BindView(R2.id.linearInviteLink)
    LinearLayout linearInviteLink;
    @BindView(R2.id.linearDescription)
    LinearLayout linearDescription;
    @BindView(R2.id.txtInviteLink)
    TextView txtInviteLink;
    @BindView(R2.id.txtDescription)
    TextView txtDescription;
    @BindView(R2.id.linearPinMessage)
    LinearLayout linearPinMessage;
    @BindView(R2.id.linearProject)
    LinearLayout linearProject;

    //sent file + links
    @BindView(R2.id.linearMedia)
    LinearLayout linearMedia;

    private Member findMe() {
        Member me = null;
        if (members != null && members.size() > 0) {
            for (int i = 0; i < members.size(); i++) {
                Member member = members.get(i);
                if (member.getUserId().equals(ownerId)) {
                    me = member;
                    break;
                }
            }
        }
        return me;
    }

    private void initChannel(final boolean isChannel) {
        if (isChannel) {
            linearInfo.setVisibility(View.VISIBLE);
            linearBanUser.setVisibility(View.VISIBLE);
            linearAdmin.setVisibility(View.VISIBLE);
            linearLeftGroup.setVisibility(View.VISIBLE);


            if (room != null) {

                txt1.setText(room.getRoomName());
                String link = room.getJoinLink();

                //PRIVATE THI KO SHARE LINK
                if (!TextUtils.isEmpty(link) && room.isPrivate() == false) {
                    linearInviteLink.setVisibility(View.VISIBLE);
                    txtInviteLink.setText(link);
                    linearInviteLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyUtils.setClipboard(context, txtInviteLink.getText().toString().trim());
                        }
                    });
                } else {
                    linearInviteLink.setVisibility(View.GONE);
                }

                String des = room.getDescription();
                if (!TextUtils.isEmpty(des)) {
                    linearDescription.setVisibility(View.VISIBLE);
                    txtDescription.setText(des);
                } else {
                    linearDescription.setVisibility(View.GONE);
                }


            }

            linearInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (room != null) {
                        Intent intent = new Intent(context, MH03_ChannelActivity.class);
                        intent.putExtra(Room.ROOM, room);
                        startActivity(intent);
                    }
                }
            });
            linearBanUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (room != null) {
                        Intent intent = new Intent(context, MH01_BanUserActivity.class);
                        intent.putExtra(Room.ROOM, room);
                        startActivity(intent);
                    }
                }
            });
            linearAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (room != null) {
                        Intent intent = new Intent(context, MH03_AdminUserActivity.class);
                        intent.putExtra(Room.ROOM, room);
                        startActivity(intent);
                    }
                }
            });


            //Kiem tra quyen admin cho user này
            members = room.getMembers();
            if (members != null && members.size() > 0) {
                Member me = findMe();
                if (me != null) {

                    Permission p = me.getPermissions();
                    if (p != null) {

                        //EDIT THONG TIN
                        if (p.isEditInfo()) {
                            linearInfo.setVisibility(View.VISIBLE);
                            img3.setVisibility(View.VISIBLE);
                            img1.setEnabled(true);
                        } else {
                            linearInfo.setVisibility(View.GONE);
                            img3.setVisibility(View.GONE);
                            img1.setEnabled(false);
                        }

                        //SET ADMIN thi cung co quyen add user
                        if (p.isAddNewAdmins()) {
                            linearAdmin.setVisibility(View.VISIBLE);
                            linearAddmember.setVisibility(View.VISIBLE);
                            linearMembers.setVisibility(View.VISIBLE);
                        } else {
                            linearAdmin.setVisibility(View.GONE);
                            linearAddmember.setVisibility(View.GONE);
                            linearMembers.setVisibility(View.GONE);
                        }

                        //BAN USER - add
                        if (p.isAddUsers()) {
                            linearBanUser.setVisibility(View.VISIBLE);
                            linearAddmember.setVisibility(View.VISIBLE);
                            linearMembers.setVisibility(View.VISIBLE);
                        } else {
                            linearBanUser.setVisibility(View.GONE);
                            linearAddmember.setVisibility(View.GONE);
                            linearMembers.setVisibility(View.GONE);
                        }

                    } else {

                        //an cac chuc nang
                        linearInfo.setVisibility(View.GONE);
                        linearAdmin.setVisibility(View.GONE);
                        linearBanUser.setVisibility(View.GONE);

                        //khong cho xem danh sach thanh vien
                        linearAddmember.setVisibility(View.GONE);
                        linearMembers.setVisibility(View.GONE);

                        //ko cho edit
                        img3.setVisibility(View.GONE);
                        img1.setEnabled(false);
                    }
                }
            }

        } else {

            //GROUP
            linearInfo.setVisibility(View.GONE);
            linearBanUser.setVisibility(View.GONE);
            linearAdmin.setVisibility(View.GONE);
            linearInviteLink.setVisibility(View.GONE);
            linearDescription.setVisibility(View.GONE);

            linearLeftGroup.setVisibility(View.VISIBLE);

            if (isSupport) {
                linearAddmember.setVisibility(View.GONE);
            } else {
                linearAddmember.setVisibility(View.VISIBLE);
            }


            //neu la admin hoac owner thi dc thay doi info, (neu la owner thi cung la admin) => chi can ktra admin
            Member me = findMe();
            if (me != null) {
                if (me.isAdmin()) {
                    // cho edit
                    img3.setVisibility(View.VISIBLE);
                    img1.setEnabled(true);
                } else {
                    //ko cho edit
                    img3.setVisibility(View.GONE);
                    img1.setEnabled(false);
                }
            }

        }

        if (!TextUtils.isEmpty(room.getProjectLink())) {
            linearProject.setVisibility(View.VISIBLE);
            linearProject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        /*Project project = room.getProject();
                        if (project != null) {
                            long id = MyUtils.getIdFromLinkTaskOrProject(room.getProjectLink());
                            Uri uri = Deeplink.getDeeplinkProject(id);
                            Deeplink.openMyxteam(context, uri);
                        }*/

                        long id = MyUtils.getIdFromLinkTaskOrProject(room.getProjectLink());
                        Uri uri = Deeplink.getDeeplinkProject(id);
                        Deeplink.openMyxteam(context, uri);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            linearProject.setVisibility(View.GONE);
        }

        //chi group tu tao moi duoc phep roi khoi
        if (room != null) {
            //room tu tao va channel thi moi cho phep member roi khoi
            if (room.getType().equalsIgnoreCase(Room.ROOM_TYPE_CUSTOM) || isChannel || isPage) {
                linearLeftGroup.setVisibility(View.VISIBLE);
            } else {
                linearLeftGroup.setVisibility(View.GONE);
            }

        }

        linearLeftGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChannel) {
                    if (isChannelOwner) {
                        //xoa kenh
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage(R.string.confirm_remove_channel);
                        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                removeChannel();
                            }
                        });

                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        //roi kenh
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage(R.string.confirm_left_channel);
                        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                leaveChannel();
                            }
                        });

                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                } else {//room group
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.confirm_left_group);
                    alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            leaveRoom();
                        }
                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        linearPinMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.checkInternetConnection(context)) {
                    if (room != null) {
                        db.putObject(Room.ROOM, room);
                        Intent intent = new Intent(context, MH06_PinMessageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        MyUtils.showToast(context, R.string.please_check_internet);
                    }
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void removeChannel() {
        if (isSocketConnected() && room != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //todo
            socket.emit("deleteChatRoom", obj, new Ack() {
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

                                finish();
                            }
                        });
                    }

                }
            });
        }
    }

    private void leaveChannel() {
        if (isSocketConnected() && room != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
            } catch (Exception e) {
                e.printStackTrace();
            }


            socket.emit("leaveChannel", obj, new Ack() {
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

                                finish();
                            }
                        });
                    }

                }
            });
        }
    }

    private void leaveRoom() {
        if (isSocketConnected() && room != null) {

            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
            } catch (Exception e) {
                e.printStackTrace();
            }


            socket.emit("leaveRoom", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    // socket return sasUrl, sasThumbUrl (nếu là image)
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

                                finish();
                            }
                        });
                    }

                }
            });
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void setMuteNotify(final boolean isMuted) {
        if (isSocketConnected() && room != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", room.get_id());
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit(isMuted ? "muteChatRoom" : "unmuteChatRoom", obj, new Ack() {
                @Override
                public void call(Object... args) {
                    // socket return sasUrl, sasThumbUrl (nếu là image)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                        }
                    });

                }
            });
        }
    }

    /////////////
    private void initRoomChatWithRoomId() {
        if (room != null) {
            final long start = SystemClock.elapsedRealtime();
            if (isSocketConnected()) {
                if (!TextUtils.isEmpty(room.get_id())) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("roomId", room.get_id());
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
                                ChatGroupDetailActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (room != null) {
                                            loadMember();
                                        }

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int code = Room.getErrorCode(context, args);
                                        if (code == Room.NOT_INT_ROOM_CODE) {
                                            String message = getString(R.string.not_in_room);
                                            MyUtils.showAlertDialog(ChatGroupDetailActivity.this, message, true);
                                        } else {
                                            String message = Room.getErrorMessage(context, args);
                                            MyUtils.showAlertDialog(ChatGroupDetailActivity.this, message, true);
                                        }

                                    }
                                });
                            }

                        }
                    });
                }
            }
        }
    }

}
