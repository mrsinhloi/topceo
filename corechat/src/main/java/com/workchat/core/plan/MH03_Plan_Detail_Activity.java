package com.workchat.core.plan;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.database.TinyDB;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.LocaleHelper;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.realm.Room;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.utils.PermissionUtil;
import com.workchat.core.widgets.CircleTransform;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.socket.client.Ack;
import io.socket.client.Socket;

public class MH03_Plan_Detail_Activity extends AppCompatActivity {
    private static final String TAG = "MH03_Plan_Detail";

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    private PlanModel plan;
    private UserChatCore user;
    private Room room;

    TinyDB db;
    String languageSelected = LocaleHelper.LANGUAGE_TIENG_VIET;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh03_plan_detail);
        ButterKnife.bind(this);
        user = ChatApplication.Companion.getUser();

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_green_500_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db = new TinyDB(this);
        languageSelected = db.getString(LocaleHelper.SELECTED_LANGUAGE, LocaleHelper.LANGUAGE_TIENG_VIET);
        Bundle b = getIntent().getExtras();
        plan = b.getParcelable(PlanModel.PLAN_MODEL);
        if (plan != null) {
            init();
        } else {
            finish();
        }
        room = b.getParcelable(Room.ROOM);

        registerReceiver();

    }

    private void init() {
        //text
        initInfo();
        //set ui button
        initButtonUI();
        //mau progress bar
        initProgressBarColor();
        //set giao dien info
        setFeedbackUI();
        //set ui button lien ket google calendar
        initButtonLink();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.img1)
    ImageView img1;
    @BindView(R2.id.img2)
    ImageView img2;

    @BindView(R2.id.txt1)
    TextView txt1;
    @BindView(R2.id.txt2)
    TextView txt2;
    @BindView(R2.id.txt3)
    TextView txt3;
    @BindView(R2.id.txt4)
    TextView txt4;
    @BindView(R2.id.txt5)
    TextView txt5;
    @BindView(R2.id.txt6)
    TextView txt6;//note

    //comment
    @BindView(R2.id.relativeComment)
    RelativeLayout relativeComment;
    @BindView(R2.id.btnComment)
    Button btnComment;
    //feedback
    @BindView(R2.id.relativeFeedback)
    RelativeLayout relativeFeedback;

    private int avatarSize = 60;

    private void initInfo() {
        if (plan != null) {

            //nguoi tao
            avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium_smaller);
            Member member = plan.getOwner();
            if (member != null && member.getUserInfo() != null) {
                txt1.setText(member.getUserInfo().getName());
                Picasso.get()
                        .load(member.getUserInfo().getAvatar())
                        .resize(avatarSize, avatarSize)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_2)
                        .transform(new CircleTransform(context))
                        .into(img1);
            } else {//member null thi la chinh minh
                if (user != null) {
                    txt1.setText(user.getName());
                    Picasso.get()
                            .load(user.getAvatarUrl())
                            .resize(avatarSize, avatarSize)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_2)
                            .transform(new CircleTransform(context))
                            .into(img1);
                }
            }

            //thoi gian con lai
            long time = plan.getTimeStamp();
            txt2.setText(MyUtils.getTimeDifferent(time, context));
            //kiem tra da ket thuc chua
            boolean finished = isFinished();
            if (finished) {
                txt2.setBackgroundResource(R.drawable.bg_rectangle_blue_corner_disable);
            }

            //tieu de
            txt3.setText(plan.getTitle());
            //gio
            txt4.setText(MyUtils.getTimePlan(time, languageSelected));

            //dia diem
            final String address = plan.getAddress();
            txt5.setText(address);
            txt5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = plan.getLat();
                    String lng = plan.getLng();
                    MyUtils.openMap(context, lat, lng, address);
                }
            });
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txt5.performLongClick();
                }
            });

            //ghi chu
            txt6.setText(plan.getNote());

            //comment
            ArrayList<Comment> list = plan.getComments();
            if (list != null && list.size() > 0) {
                btnComment.setText(list.size() + "");
            }
            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MH04_Comment_Activity.class);
                    intent.putExtra(PlanModel.PLAN_MODEL, plan);
                    room.setLastLog(null);
                    intent.putExtra(Room.ROOM, room);
                    context.startActivity(intent);
                }
            });
            relativeComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnComment.performClick();
                }
            });
            relativeFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MH05_Feedback_Activity.class);
                    intent.putExtra(PlanModel.PLAN_MODEL, plan);
                    room.setLastLog(null);
                    intent.putExtra(Room.ROOM, room);
                    context.startActivity(intent);
                }
            });

        }
    }


    private int[] bgs = {
            R.drawable.button_click_yes,
            R.drawable.button_click_no,
            R.drawable.button_click_maybe
    };
    private int[] colors = {
            R.color.yes,
            R.color.no,
            R.color.maybe
    };
    @BindViews({R2.id.btn1, R2.id.btn2, R2.id.btn3})
    List<Button> btns;
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private int colorSelected = R.color.white;
    private int positionSelected = -1;

    private void setButtonSelected() {

        for (int i = 0; i < btns.size(); i++) {
            Button btn = btns.get(i);
            if (positionSelected == i) {
                btn.setTextColor(ContextCompat.getColor(context, colorSelected));
                btn.setBackgroundColor(ContextCompat.getColor(context, colors[i]));
            } else {
                btn.setBackgroundResource(bgs[i]);
                btn.setTextColor(ContextCompat.getColor(context, colors[i]));
            }
        }
    }

    private boolean isFinished() {
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

    private void initButtonUI() {

        //kiem tra da ket thuc chua
        boolean finished = isFinished();

        //khoi tao giao dien
        for (int i = 0; i < btns.size(); i++) {
            Button btn = btns.get(i);
            final int temp = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //neu khac vi tri thi moi set
                    if (temp != positionSelected) {
                        positionSelected = temp;
                        setButtonSelected();
                        //doi ket qua phan hoi
                        if (plan != null) {
                            plan = plan.changeVote(positionSelected, user.get_id());
                            setFeedbackUI();
                            confirmToPlan();
                        }
                    }
                }
            });

            //neu da ket thuc thi disable
            if (isFinished()) {
                btn.setEnabled(false);
            }
        }

        //set gia tri da chon
        if (plan != null && user != null) {
            positionSelected = plan.getPositionSelected(user.get_id());
            setButtonSelected();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //progress
    @BindView(R2.id.txt7)
    TextView txt7;//so nguoi da phan hoi
    @BindView(R2.id.txt8)
    TextView txt8;
    @BindView(R2.id.txt9)
    TextView txt9;
    @BindView(R2.id.txt10)
    TextView txt10;
    @BindView(R2.id.pb1)
    ProgressBar pb1;
    @BindView(R2.id.pb2)
    ProgressBar pb2;
    @BindView(R2.id.pb3)
    ProgressBar pb3;

    private void setFeedbackUI() {
        if (plan != null) {

            ArrayList<Vote> votes = plan.getResult();

            //da phan hoi
            int voted = votes.size();
            int sum = plan.getMemberCount();
            String s = voted + " / " + sum;
            txt7.setText(getString(R.string.x_people_responded, s));

            //co
            int yes = plan.getNumberVote(Vote.YES);
            txt8.setText(getString(R.string.x_yes, yes + ""));

            //khong
            int no = plan.getNumberVote(Vote.NO);
            txt9.setText(getString(R.string.x_no, no + ""));

            //co the
            int maybe = plan.getNumberVote(Vote.MAYBE);
            txt10.setText(getString(R.string.x_maybe, maybe + ""));


            //set progress, mac dinh 100%
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                pb1.setProgress(yes * 100 / voted, true);
                pb2.setProgress(no * 100 / voted, true);
                pb3.setProgress(maybe * 100 / voted, true);
            } else {
                pb1.setProgress(yes * 100 / voted);
                pb2.setProgress(no * 100 / voted);
                pb3.setProgress(maybe * 100 / voted);
            }

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void initProgressBarColor() {
        //progress bar
        //set mau
        setColorProgressBar(pb1, R.color.yes);
        setColorProgressBar(pb2, R.color.no);
        setColorProgressBar(pb3, R.color.maybe);
    }

    private void setColorProgressBar(ProgressBar pb, int color) {
        //step 1
        pb.getProgressDrawable().setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.MULTIPLY);
        //step 2
        LayerDrawable progressBarDrawable = (LayerDrawable) pb.getProgressDrawable();
        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
        Drawable progressDrawable = progressBarDrawable.getDrawable(1);
        backgroundDrawable.setColorFilter(ContextCompat.getColor(context, R.color.grey_500), PorterDuff.Mode.SRC_IN);//color 1
        progressDrawable.setColorFilter(ContextCompat.getColor(context, R.color.maybe), PorterDuff.Mode.SRC_OUT);//color 2
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        initSocketConnection();
    }

    private Socket socket;

    private void initSocketConnection() {
        //link den socket trong application
        socket = ChatApplication.Companion.getSocket();
        if (socket != null && socket.connected() == false) {
            socket.connect();
        }
    }

    private boolean isSocketConnected() {
        boolean connected = false;
        //khi hoi ket noi socket thi lay lai
        socket = ChatApplication.Companion.getSocket();
        if (socket != null) {
            connected = socket.connected();
            if (connected == false) {
                socket.connect();
//                MyUtils.showToastDebug(context, "Lost socket connection");
            }
        }


        return connected;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void confirmToPlan() {
        if (plan != null) {
            if (isSocketConnected()) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomId", plan.getRoomId());
                    obj.put("chatLogId", plan.getChatLogId());

                    String status = plan.getStatus(positionSelected);
                    obj.put("status", status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                socket.emit("confirmToPlan", obj, new Ack() {
                    @Override
                    public void call(Object... args) {
//                        MyUtils.log("OK");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (plan != null) {
                                    CalendarManager manager = new CalendarManager(context);
                                    switch (positionSelected) {
                                        case 0://yes
                                        case 2://maybe thi add vao calendar
                                            //neu khong ton tai thi them vao calendar
                                            if (plan != null) {
                                                manager.createEvent(plan.getChatLogId(), plan.getPlanModelLocal());
                                            }
                                            break;
                                        case 1://No thi remove
                                            if (plan != null) {
                                                //kiem tra neu co thi remove
                                                manager.deleteEvent(plan.getChatLogId());
                                            }
                                            break;

                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                MyUtils.showToast(context, R.string.socket_not_connected);
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_ADD_COMMENT = "ACTION_ADD_COMMENT";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_ADD_COMMENT)) {
                    Bundle b = intent.getExtras();
                    Comment comment = b.getParcelable(Comment.COMMENT);
                    if (comment != null) {
                        //comment
                        plan.getComments().add(comment);
                        ArrayList<Comment> list = plan.getComments();
                        if (list != null && list.size() > 0) {
                            btnComment.setText(list.size() + "");
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_ADD_COMMENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.btnLink)
    Button btnLink;
    @BindView(R2.id.linearLink)
    LinearLayout linearLink;

    private void initButtonLink() {
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify that all required contact permissions have been granted.
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Contacts permissions have not been granted.
                    Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
                    requestCalendarPermission();

                } else {

                    // Contact permissions have been granted. Show the contacts fragment.
                    Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
                    linearLink.setVisibility(View.GONE);

                }
            }
        });

        //neu da gan quyen thi hide linearLink
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {


        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
            linearLink.setVisibility(View.GONE);

        }


    }

    private static final int REQUEST_CALENDAR = 1;
    private static String[] PERMISSIONS_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };

    private void requestCalendarPermission() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CALENDAR)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CALENDAR)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(linearLink, R.string.link_with_google_calendar,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(context, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(context, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
        }
        // END_INCLUDE(contacts_permission_request)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALENDAR) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                /*Snackbar.make(linearLink, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();*/
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                /*Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();*/
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
