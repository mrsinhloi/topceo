package com.workchat.core.channel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Picasso;
import com.workchat.core.chat.ChatActivity;
import com.workchat.core.chat.ChatGroupDetailActivity;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.widgets.CircleTransform;
import com.workchat.core.widgets.MyEditText;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class MH03_ChannelActivity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.img1)
    ImageView img1;
    @BindView(R2.id.img2)
    ImageView img2;
    @BindView(R2.id.txt1)
    MyEditText txt1;
    @BindView(R2.id.txt2)
    MyEditText txt2;
    @BindView(R2.id.txt3)
    MyEditText txt3;

    @BindView(R2.id.txtLink)
    TextView txtLink;
    @BindView(R2.id.txtLinkDes)
    TextView txtLinkDes;
    @BindView(R2.id.group1)
    RadioGroup group1;
    @BindView(R2.id.radioDes1)
    TextView radioDes1;
    @BindView(R2.id.radioDes2)
    TextView radioDes2;
    @BindView(R2.id.cb1)
    CheckBox cb1;
    @BindView(R2.id.cb2)
    CheckBox cb2;


    @BindView(R2.id.btn1)
    ImageButton btn1;
    @BindView(R2.id.btn2)
    Button btn2;
    @BindView(R2.id.btn3)
    Button btn3;
    @BindView(R2.id.scrollView)
    ScrollView scrollView;
    @BindView(R2.id.linearCopyLink)
    LinearLayout linearCopyLink;


    private int avatarSize = 100;

    String ownerId;
    private UserChatCore user;
    private Socket socket;

    private Room room;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_03_channel);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //get data
        socket = ChatApplication.Companion.getSocket();
        //////////////////////
        title.setText(R.string.new_channel);

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img1.performClick();
            }
        });
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogChoseImage();
            }
        });

        avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size_larger);


        group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setCheck(i);
            }
        });



        radioDes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheck(R.id.radio1);
            }
        });
        radioDes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheck(R.id.radio2);
            }
        });
        setCheck(R.id.radio2);

        ////////////////////////////////////////////
        user = ChatApplication.Companion.getUser();
        try {
            ownerId = user.get_id();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setClipboard(context, txt3.getText().toString().trim());
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check link public
                String link = txt3.getText().toString().trim();
                if (!TextUtils.isEmpty(link)) {
                    link = link.replaceAll("\\s", "-");
                    checkLink(link);
                }

                MyUtils.hideKeyboard(context);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check link public
                revokeLink();
                MyUtils.hideKeyboard(context);
            }
        });


        Bundle b = getIntent().getExtras();
        if (b != null) {
            room = b.getParcelable(Room.ROOM);
            if (room != null) {
                isEdit = true;
                title.setText(R.string.update_channel);


                txt1.setText(room.getRoomName());
                txt2.setText(room.getDescription());

                if (room.isPrivate()) {
                    privateLink = room.getJoinLink();
                    setCheck(R.id.radio2);
                } else {
                    publicLink = room.getJoinLink();
                    setCheck(R.id.radio1);
                }
                cb1.setChecked(room.isSignMessage());
                cb2.setChecked(room.isEnableChatWithAdmin());
                txt3.setText(room.getJoinLink());

            }
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        //get data
        socket = ChatApplication.Companion.getSocket();


    }

    private String publicLink = "";
    private String privateLink = "";

    private void setCheck(int id) {
        group1.check(id);
        if (id == R.id.radio1) {//public
            linearCopyLink.setVisibility(View.VISIBLE);

            txtLink.setText(R.string.invite_link);
            txt3.setEnabled(true);
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.VISIBLE);
            btn3.setVisibility(View.GONE);
            txtLinkDes.setVisibility(View.VISIBLE);
            txt3.setText(publicLink);
        } else if (id == R.id.radio2) {//private
            linearCopyLink.setVisibility(View.GONE);

            //luu lai
            String link = txt3.getText().toString().trim();
            if (!privateLink.equalsIgnoreCase(link)) {
                publicLink = link;
            }

            //private
            txtLink.setText(R.string.invite_link);
            txt3.setEnabled(false);//read only
            btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.GONE);
            txtLinkDes.setVisibility(View.INVISIBLE);

            //goi tao link
            setLink();

            if (room != null && room.isPrivate()) {
                btn3.setVisibility(View.VISIBLE);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    ProgressDialog progressDialog;

    private void showDialog(Context context, String message) {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
    /////////////////////////////////////////////////////////////////////////////////

    private static final int TAKE_PICTURE = 12;

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(this, ChatApplication.Companion.getApplicaitonId() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                takePictureIntent.putExtra("return-data", true);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }

    String mCurrentPhotoPath = "";

    private File createImageFile() throws IOException {
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

    private static final int REQUEST_CODE_PICKER = 133;
    private static final int IMAGES_LIMIT = 1;
    private Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {
                uri = Uri.fromFile(new File(mCurrentPhotoPath));
                setImage();
            }

            if (requestCode == REQUEST_CODE_PICKER) {
                ArrayList<Image> images =
                        intent.getParcelableArrayListExtra(com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);

                String path = images.get(0).getPath();
                uri = Uri.fromFile(new File(path));
                setImage();
            }

        }
    }

    private boolean isCapture = false;

    private void dialogChoseImage() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        String[] items = getResources().getStringArray(R.array.get_images_from);
        final ArrayAdapter<String> arraylist = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);

        builder.setSingleChoiceItems(arraylist, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://capture
                        isCapture = true;
                        takePictureIntent();
                        break;
                    case 1:
                        isCapture = false;
                        ImagePicker.create(context)
                                .multi() // single mode
                                .imageTitle(getText(R.string.tap_to_select_image).toString())
                                .limit(IMAGES_LIMIT) // max imagesSelected can be selected
                                .showCamera(false) // show camera or not (true by default)
                                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
                        break;
                }
                dialog.dismiss();
            }

        });

        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void setImage() {
        if (uri != null)
            Picasso.get()
                    .load(uri)
                    .transform(new CircleTransform(context))
                    .resize(avatarSize, avatarSize)
                    .centerCrop()
                    .into(img1);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_1) {//                MyUtils.showToast(getApplicationContext(), "Posting...");

            String title = txt1.getText().toString().trim();
            String description = txt2.getText().toString().trim();
            boolean isPrivate = group1.getCheckedRadioButtonId() == R.id.radio2;
            String link = txt3.getText().toString().trim();

            if (!TextUtils.isEmpty(title)) {
                if (!TextUtils.isEmpty(link)) {
                    if (isEdit) {
                        updateChannel(title, description, isPrivate, link, cb1.isChecked(), cb2.isChecked());
                    } else {
                        createChannel(title, description, isPrivate, link, cb1.isChecked(), cb2.isChecked());
                    }
                } else {
                    MyUtils.showToast(context, R.string.channel_link_empty);
                    txt3.requestFocus();
                }
            } else {
                MyUtils.showToast(context, R.string.channel_title_empty);
                txt1.requestFocus();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    private void setLink() {
        if (!TextUtils.isEmpty(privateLink)) {
            txt3.setText(privateLink);
        } else {
            if (socket != null && socket.connected()) {
                JSONObject obj = new JSONObject();
                socket.emit("generatePrivateJoinLink", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
//                            MyUtils.log(args.toString());
                                try {
                                    JSONObject obj = new JSONObject(args[0].toString());
                                    int code = obj.getInt("errorCode");
                                    if (code == 0) {
                                        privateLink = obj.getString("data");
                                        txt3.setText(privateLink);
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

    //////////////////////////////////////////////////////////////////////////////////////////
    private void checkLink(String link) {
        if (socket != null && socket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomId", "");
                obj.put("joinLink", link);
            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit("checkJoinLinkAvaiable", obj, new Ack() {
                @Override
                public void call(final Object... args) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
//                            MyUtils.log(args.toString());
                            try {
                                JSONObject obj = new JSONObject(args[0].toString());
                                int code = obj.getInt("errorCode");
                                if (code == 0) {
                                    boolean b = obj.getBoolean("data");
                                    if (b) {
                                        txtLinkDes.setText(R.string.channel_link_available);
                                        txtLinkDes.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryChat));
                                    } else {
                                        txtLinkDes.setText(R.string.channel_link_unavailable);
                                        txtLinkDes.setTextColor(ContextCompat.getColor(context, R.color.red_500));
                                    }
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

    //////////////////////////////////////////////////////////////////////////////////////////
    private void createChannel(
            String roomName,
            String description,
            boolean isPrivate,
            String joinLink,
            boolean signMessage,
            boolean enableChatWithAdmin
            ) {
        if (socket != null && socket.connected()) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("roomName", roomName);
                obj.put("description", description);
                obj.put("isPrivate", isPrivate);
                obj.put("joinLink", joinLink);
                obj.put("signMessage", signMessage);
                obj.put("enableChatWithAdmin", enableChatWithAdmin);

            } catch (Exception e) {
                e.printStackTrace();
            }

            socket.emit("createChannel", obj, new Ack() {
                @Override
                public void call(final Object... args) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject obj = new JSONObject(args[0].toString());
                                int code = obj.getInt("errorCode");
                                if (code == 0) {
                                    MyUtils.showToast(context, R.string.channel_created);

                                    Room room = Room.parseRoom(context, args);
                                    if (room != null) {
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        intent.putExtra(Room.ROOM_ID, room.get_id());
                                        context.startActivity(intent);
                                        finish();
                                    }
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


    //////////////////////////////////////////////////////////////////////////////////////////
    private void updateChannel(
            String roomName,
            String description,
            boolean isPrivate,
            String joinLink,
            boolean signMessage,
            boolean enableChatWithAdmin
            ) {
        if (room != null) {
            if (socket != null && socket.connected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                    obj.put("roomName", roomName);
                    obj.put("description", description);
                    obj.put("isPrivate", isPrivate);
                    obj.put("joinLink", joinLink);
                    obj.put("signMessage", signMessage);
                    obj.put("enableChatWithAdmin", enableChatWithAdmin);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("updateChannel", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(args[0].toString());
                                    int code = obj.getInt("errorCode");
                                    if (code == 0) {
                                        MyUtils.showToast(context, R.string.update_success);

                                        Room room = Room.parseRoom(context, args);
                                        if (room != null) {

                                            //json se bi lỗi khi truyền giữa các activity, set null trươc khi truyen
                                            room.setLastLog(null);

                                            //update room man hinh Chatgroupdetail va man hinh chat
                                            Intent intent = new Intent(ChatGroupDetailActivity.ACTION_UPDATE_ROOM_MODEL);
                                            intent.putExtra(Room.ROOM, room);
                                            sendBroadcast(intent);

                                            intent = new Intent(ChatActivity.ACTION_UPDATE_ROOM_MODEL);
                                            intent.putExtra(Room.ROOM, room);
                                            sendBroadcast(intent);

                                            finish();
                                        }
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


    private void revokeLink() {
        if (room != null) {
            if (socket != null && socket.connected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", room.get_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                socket.emit("revokePrivateJoinLink", obj, new Ack() {
                    @Override
                    public void call(final Object... args) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(args[0].toString());
                                    int code = obj.getInt("errorCode");
                                    if (code == 0) {
                                        MyUtils.showToast(context, R.string.revoke_success_and_new_link_created);
                                        privateLink = obj.getString("data");
                                        txt3.setText(privateLink);

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
