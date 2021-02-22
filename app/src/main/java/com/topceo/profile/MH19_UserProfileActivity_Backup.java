package com.topceo.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.activity.MH02_PhotoDetailActivity;
import com.topceo.activity.MH09_FollowersActivity;
import com.topceo.activity.MH10_FollowingsActivity;
import com.topceo.activity.MH15_GroupUserActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserMedium;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MH19_UserProfileActivity_Backup extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private TinyDB db;
    private User user, owner;//user co the la owner hoac khong

    @BindView(R.id.title)
    TextView txtTitle;
    @BindView(R.id.imageView1)
    ImageView avatar;
    @BindView(R.id.textView1)
    TextView txt1;
    @BindView(R.id.textView2)
    TextView txt2;
    @BindView(R.id.textView3)
    TextView txt3;


    @BindView(R.id.textView6)
    TextView txt6;


    @BindView(R.id.button1)
    TextView btnEdit;

    @BindView(R.id.button2)
    TextView btnChat;

    @BindView(R.id.button3)
    TextView btnGroup;


    @BindView(R.id.linearFollower)
    LinearLayout linearFollower;
    @BindView(R.id.linearFollowing)
    LinearLayout linearFollowing;
    @BindView(R.id.linearUserGroup)
    LinearLayout linearUserGroup;


    @BindView(R.id.txtFollowing)
    TextView txtFollowing;
    @BindView(R.id.txtFollower)
    TextView txtFollower;


    @BindView(R.id.imgVipLevel)
    ImageView imgVipLevel;

    @BindView(R.id.txtFullName)
    TextView txtFullName;
    @BindView(R.id.txtDescription)
    TextView txtDescription;

    private void setVip(User user) {
        if (user != null) {
            /*if (user.isVip() || user.getUserId() == User.ADMIN_ROLE_ID) {
                linearVip.setVisibility(View.VISIBLE);
                if (user.getUserId() == User.ADMIN_ROLE_ID) {//boss son tung
                    imgVip.setImageResource(R.drawable.ic_boss);
                    txtVip.setText(R.string.boss);
                } else {
                    imgVip.setImageResource(R.drawable.ic_vip_fan);
//                txtVip.setText(R.string.vip_fan);
                    txtVip.setText(user.getVipLevel());
                }
            } else {
                linearVip.setVisibility(View.GONE);
            }*/

            //king sticker
            switch (user.getVipLevelId()) {
                case User.VIP_LEVEL_SKY:
                    imgVipLevel.setVisibility(View.GONE);
                    break;
                case User.VIP_LEVEL_VIP:
                    imgVipLevel.setVisibility(View.VISIBLE);
                    imgVipLevel.setImageResource(R.drawable.ic_svg_26_1_star);
                    break;
                case User.VIP_LEVEL_VVIP:
                    imgVipLevel.setVisibility(View.VISIBLE);
                    imgVipLevel.setImageResource(R.drawable.ic_svg_28_3_star);
                    break;
            }

            MyUtils.setText(user.getUserName() + " - " + user.getFullName(), txtFullName);
            MyUtils.setText(user.getFavorite(), txtDescription);
        }
    }

    private int avatarSize = 0;
    private boolean isOwner = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        registerReceiver();

        /*int p = 1234;
        String Int = "1234";
        String Int2 = "1234";

        String string = String.valueOf(p);
        boolean b1 = Int.equals(p);//false
        boolean b2 = Int.equals(string);//true
        boolean b3 = Int == Int2;//true
*/

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        updateStatusBarColor();

        /////////////////////////////////
        db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
            owner = (User) obj;
        }
        avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        //////////////////////////////////////////////////////////////
        Bundle b = getIntent().getExtras();
        if (b != null) {
            UserMedium u = (UserMedium) b.getParcelable(User.USER);
            if (u != null) {
//                long number = MyUtils.gettNumberFromString(u.getChatUserId());
                //neu @chinh minh thi xem nhu la owner
                if (user.getUserId() == u.getUserId()) {
                    isOwner = true;
                } else {//@ mot nguoi khac
                    user = u.getUser();
                    isOwner = false;
                }
            }
        }

        //set user//////////////////////////////////////////////////////
        if (isOwner) {
            initOwner();
            //lay thong tin so luong follower moi nhat
            getInfoFollowOfMe();
        } else {
            initUser();

        }

        ////////////////////////////////////////////////////////////////
        setupTabs();


        //click
        linearFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.checkInternetConnection(context)) {
                    Intent intent = new Intent(context, MH09_FollowersActivity.class);
                    intent.putExtra(User.USER_ID, user.getUserId());
                    startActivity(intent);
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

        linearFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.checkInternetConnection(context)) {
                    Intent intent = new Intent(context, MH10_FollowingsActivity.class);
                    startActivity(intent);
                } else {
                    MyUtils.showThongBao(context);
                }

            }
        });

        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && user.getGroupCount() > 0) {
                    Intent intent = new Intent(context, MH15_GroupUserActivity.class);
                    intent.putExtra(User.USER_ID, user.getUserId());
                    startActivity(intent);
                } else {
                    MyUtils.showAlertDialog(context, R.string.not_have_group);
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkChat));
        }
    }

    private void initOwner() {
        user = (User) db.getObject(User.USER, User.class);
        if (user != null) {
            Glide.with(this)
                    .load(user.getAvatarMedium())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(this))
                    .into(avatar);
            txt1.setText(user.getImageCount() + "");
            txt2.setText(user.getFollowerCount() + "");
            txt3.setText(user.getFollowingCount() + "");
            txt6.setText(user.getGroupCount() + "");
            txtTitle.setText(user.getUserName());


            //vip
            setVip(user);
        }

        btnChat.setVisibility(View.GONE);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MH19_UserProfileActivity.this, MH20_UserEditProfileActivity.class));
                startActivity(new Intent(context, MH22_SettingActivity.class));
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEdit.performClick();
            }
        });
    }

    private boolean isFollowing = false;

    private void initUser() {

        if (user != null) {
            Glide.with(context)
                    .load(user.getAvatarMedium())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(avatar);
            txt1.setText(user.getImageCount() + "");
            txt2.setText(user.getFollowerCount() + "");
            txt3.setText(user.getFollowingCount() + "");
            txt6.setText(user.getGroupCount() + "");
            txtTitle.setText(user.getUserName());


            //vip
            setVip(user);
        }


        //init text follow/unfollow
        //lay danh sach nguoi dang following
        isFollowing = MyUtils.isFollowing(user.getUserId());
        setButtonUI(isFollowing);

        //click control
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lay danh sach nguoi dang following
                isFollowing = MyUtils.isFollowing(user.getUserId());

                if (isFollowing) {
                    //bo follow
                    Webservices.updateUserFollowingState(user.getUserId(), !isFollowing).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if (task.getError() == null) {
                                if (task.getResult() != null) {
                                    boolean isOK = (boolean) task.getResult();
                                    if (isOK) {
//                                        MyUtils.showToast(context, R.string.update_success);

                                        isFollowing = !isFollowing;

                                        //set text Follow
                                        setButtonUI(isFollowing);

                                        //cap nhat man hinh PhotoDetail
                                        Intent intent = new Intent(MH02_PhotoDetailActivity.ACTION_UPDATE_STATE_FOLLOW);
                                        sendBroadcast(intent);

                                    }
                                }
                            } else {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }

                            return null;
                        }
                    });
                } else {
                    //set follow
                    Webservices.updateUserFollowingState(user.getUserId(), !isFollowing).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if (task.getError() == null) {
                                if (task.getResult() != null) {
                                    boolean isOK = (boolean) task.getResult();
                                    if (isOK) {
//                                        MyUtils.showToast(context, R.string.update_success);

                                        isFollowing = !isFollowing;

                                        //set text Follow
                                        setButtonUI(isFollowing);

                                        //cap nhat man hinh PhotoDetail
                                        Intent intent = new Intent(MH02_PhotoDetailActivity.ACTION_UPDATE_STATE_FOLLOW);
                                        sendBroadcast(intent);
                                    }
                                }
                            } else {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }

                            return null;
                        }
                    });
                }

            }
        });

        btnChat.setVisibility(View.VISIBLE);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.chatWithUser(context, user.getUserChatCore());
            }
        });


    }


    private void setButtonUI(boolean isFollowing) {//owner thi ko co button nay
        //set text Follow
        if (isFollowing) {
            btnEdit.setText(R.string.following_title);
            btnEdit.setBackgroundResource(R.drawable.bg_sky_rectangle_border);
            btnEdit.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnEdit.setText(R.string.follow_title);
            btnEdit.setBackgroundResource(R.drawable.bg_sky_radian);
            btnEdit.setTextColor(getResources().getColor(R.color.white));
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private void setupTabs() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    private int[] tabIcons = {
            R.drawable.ic_grid,
            R.drawable.ic_user_post,
            R.drawable.ic_svg_22
//            R.drawable.ic_label_white_48dp
    };

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    ViewPagerAdapter adapter;

    private void setupViewPager(ViewPager viewPager) {

        Bundle b = new Bundle();
        b.putParcelable(User.USER, user);

        Fragment_5_User_Profile_Grid f5 = new Fragment_5_User_Profile_Grid();
        f5.setArguments(b);

//        Fragment_6_User_Profile_List f6 = new Fragment_6_User_Profile_List();
//        f6.setArguments(b);
        Fragment_1_Home_User f6 = new Fragment_1_Home_User();
        f6.setArguments(b);


        b = new Bundle();
        b.putParcelable(User.USER, user);
        b.putBoolean(User.IS_LOAD_FAVORITE, true);
        Fragment_5_User_Profile_Grid f7 = new Fragment_5_User_Profile_Grid();
        f7.setArguments(b);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(f5, "One");
        adapter.addFrag(f6, "Two");
        adapter.addFrag(f7, "Three");


//        adapter.addFrag(new Fragment_2_Explorer(), "Three");
//        adapter.addFrag(new Fragment_2_Explorer(), "Four");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }


    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_REFRESH = "ACTION_REFRESH_" + MH19_UserProfileActivity_Backup.class.getSimpleName();
    public static final String ACTION_UPDATE_STATE_FOLLOW = "ACTION_UPDATE_STATE_FOLLOW_" + MH19_UserProfileActivity_Backup.class.getSimpleName();
    public static final String ACTION_WHEN_FOLLOW_OR_UNFOLLOW = "ACTION_WHEN_FOLLOW_OR_UNFOLLOW_" + MH19_UserProfileActivity_Backup.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH)) {
                    initOwner();
                    setupTabs();
                }
                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_STATE_FOLLOW)) {
                    isFollowing = MyUtils.isFollowing(user.getUserId());
                    setButtonUI(isFollowing);
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_WHEN_FOLLOW_OR_UNFOLLOW)) {
                    getInfoFollowOfMe();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_REFRESH));
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_STATE_FOLLOW));
        registerReceiver(receiver, new IntentFilter(ACTION_WHEN_FOLLOW_OR_UNFOLLOW));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void getInfoFollowOfMe() {
        if (isOwner) {
            Webservices.getUserInfoFollow(owner.getUserId()).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            if (task.getResult() != null) {
                                User u = (User) task.getResult();
                                if (u != null) {

                                    //update model
                                    user.setFollowerCount(u.getFollowerCount());
                                    user.setFollowingCount(u.getFollowingCount());

                                    //update db truoc roi moi doc len sau
                                    db.putObject(User.USER, user);

                                    //update giao dien, doc tu db
                                    initOwner();


                                }
                            }
                        }
                    } else {
                        MyUtils.showToast(context, task.getError().getMessage());
                    }

                    return null;
                }
            });
        }
    }


    private Menu mMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isOwner) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_config_notify, menu);
            mMenu = menu;

            getFollowingPushNotifySetting();

            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_1:
                // Set the text color to red
                item.setChecked(!item.isChecked());

                boolean isChecked = item.isChecked();
                updateFollowingPushNotifySetting(isChecked);

                return true;
            case R.id.action_2:
                MyUtils.showDialogReportUser(context, user.getUserId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getFollowingPushNotifySetting() {
        MyApplication.apiManager.getFollowingPushNotifySetting(
                user.getUserId(),
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {

                            ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);

                            //{"ErrorCode":0,"Message":"","Data":{"NewPost":false,"NewActivity":false}}
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    JsonObject json = (JsonObject) result.getData();
                                    boolean isChecked = json.get("NewPost").getAsBoolean();
                                    MenuItem item = mMenu.getItem(0);
                                    item.setChecked(isChecked);

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

    }

    private void updateFollowingPushNotifySetting(boolean isChecked) {
        MyApplication.apiManager.updateFollowingPushNotifySetting(
                user.getUserId(),
                isChecked,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {

                            ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);

                            //{"ErrorCode":0,"Message":"","Data":{"NewPost":false,"NewActivity":false}}
                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    try {
                                        JsonObject json = (JsonObject) result.getData();
                                        boolean success = json.get("Success").getAsBoolean();
//                                        MenuItem item = mMenu.getItem(0);
//                                        item.setChecked(isChecked);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

    }

}
