package com.topceo.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.activity.MH02_PhotoDetailActivity;
import com.topceo.activity.MH07_MyCardActivity;
import com.topceo.activity.MH09_FollowersActivity;
import com.topceo.activity.MH10_FollowingsActivity;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.group.AllGroupActivity;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserMedium;
import com.topceo.objects.promotion.PromotionScreen;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.PaymentActivity;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Profile_Owner extends Fragment {
    public static final String IS_OWNER_SCREEN = "IS_OWNER_SCREEN";

    public static Fragment_Profile_Owner newInstance(UserMedium user, boolean isOwnerScreen) {
        Fragment_Profile_Owner f = new Fragment_Profile_Owner();
        Bundle b = new Bundle();
        b.putParcelable(UserMedium.USER, user);
        b.putBoolean(IS_OWNER_SCREEN, isOwnerScreen);
        f.setArguments(b);
        return f;
    }

    UserMedium guestUser;
    boolean isOwnerScreen;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            guestUser = getArguments().getParcelable(UserMedium.USER);
            isOwnerScreen = getArguments().getBoolean(IS_OWNER_SCREEN);
        }
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private TinyDB db;
    private User user, owner;//user co the la owner hoac khong

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
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

    @BindView(R.id.textView4)
    TextView txtFullName;
    @BindView(R.id.txtDescription)
    TextView txtDescription;


    @BindView(R.id.linearFollower)
    LinearLayout linearFollower;
    @BindView(R.id.linearFollowing)
    LinearLayout linearFollowing;
    @BindView(R.id.linearUserGroup)
    LinearLayout linearUserGroup;
    @BindView(R.id.btnGroup)
    TextView btnGroup;


    private int avatarSize = 0;
    private boolean isOwner = true;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @BindView(R.id.linearProfile)
    FrameLayout linearProfile;
    @BindView(R.id.btnEditProfile)
    TextView btnEditProfile;
    @BindView(R.id.btnCard)
    TextView btnCard;
    @BindView(R.id.imgSetting)
    ImageView imgSetting;


    @BindView(R.id.imgVipLevel)
    ImageView imgVipLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_owner_profile, container, false);
        ButterKnife.bind(this, view);

        registerReceiver();
        /////////////////////////////////
        db = new TinyDB(getContext());
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
            owner = (User) obj;
        }
        avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        //////////////////////////////////////////////////////////////
        //neu @chinh minh thi xem nhu la owner
        if (user.getUserId() == guestUser.getUserId()) {
            isOwner = true;
        } else {//@ mot nguoi khac
            user = guestUser.getUser();
            isOwner = false;
        }

        //set user//////////////////////////////////////////////////////
        if (isOwner) {
            initOwner();
            //lay thong tin so luong follower moi nhat
            getInfoFollowOfMe();
            PromotionScreen.navigationScreen(context, PromotionScreen.PROFILE);
        } else {
            //ko xai
            initUser();
        }

        ////////////////////////////////////////////////////////////////
        setupTabs();
        initButtons();
        initToolbar();


        return view;
    }


    private void setVip(User user) {
        /*if (user.isVip() || user.getUserId() == User.ADMIN_ROLE_ID) {
//            linearVip.setVisibility(View.VISIBLE);
            if (user.getUserId() == User.ADMIN_ROLE_ID) {//boss son tung
                imgVip.setImageResource(R.drawable.ic_boss);
                txtVip.setText(R.string.boss);
            }else{
                imgVip.setImageResource(R.drawable.ic_vip_fan);
//                txtVip.setText(R.string.vip_fan);
                txtVip.setText(user.getVipLevel());
            }
        } else {
//            linearVip.setVisibility(View.GONE);
//            txtUpdateVip.setVisibility(View.VISIBLE);
        }*/

        if (user != null) {
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

            String name = user.getUserName();
            if (!TextUtils.isEmpty(user.getFullName())) {
                name += " - " + user.getFullName();
            }
            MyUtils.setText(name, txtFullName);
            MyUtils.setText(user.getFavorite(), txtDescription);

            //set social
            getSocialInfo(user.getUserId());
        }
    }

    private void gotoPayment() {
        Intent intent = new Intent(context, PaymentActivity.class);
//        intent.putExtra(Media.MEDIA_ID, mediaId);//ko truyen la mua vip
        context.startActivity(intent);
    }


    private void initOwner() {
        user = (User) db.getObject(User.USER, User.class);
        if (user != null) {
            Glide.with(this)
                    .load(user.getAvatarMedium())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(avatar);
            txt1.setText(user.getImageCount() + "");
            txt2.setText(user.getFollowerCount() + "");
            txt3.setText(user.getFollowingCount() + "");
            txt6.setText(user.getGroupCount() + "");

            //vip
            setVip(user);

        }

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private void setupTabs() {
        viewPager.setOffscreenPageLimit(tabIcons.length);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        if(!isOwner){
            tabLayout.setVisibility(View.GONE);
        }

    }

    private int[] tabIcons = {
//            R.drawable.ic_grid,
            R.drawable.ic_user_post,
            R.drawable.ic_svg_22_36dp
//            R.drawable.ic_place_white_48dp,
//            R.drawable.ic_label_white_48dp
    };

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        if (isOwner) {
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        }
        /*tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);*/

//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    ViewPagerAdapter adapter;
    Fragment_1_Home_User f6;

    private void setupViewPager(ViewPager viewPager) {

        Bundle b = new Bundle();
        b.putParcelable(User.USER, user);
        if (isOwner) {
            b.putBoolean(Fragment_1_Home_User.IS_FROM_PROFILE, true);
            b.putBoolean(Fragment_1_Home_User.IS_CAN_POST, true);
        }


        //grid
//        Fragment_5_User_Profile_Grid f5 = new Fragment_5_User_Profile_Grid();
//        f5.setArguments(b);


        //list
        f6 = new Fragment_1_Home_User();
        f6.setArguments(b);


        adapter = new ViewPagerAdapter(getChildFragmentManager());
//        adapter.addFrag(f5, "One");
        adapter.addFrag(f6, "Two");

        //owner thi moi co tab da luu
        if (isOwner) {
            //load danh sach hinh da luu
            b = new Bundle();
            b.putParcelable(User.USER, user);
            b.putBoolean(Fragment_1_Home_User.IS_FROM_PROFILE, true);
            b.putBoolean(User.IS_LOAD_FAVORITE, true);
            Fragment_5_User_Profile_Grid f7 = new Fragment_5_User_Profile_Grid();
            f7.setArguments(b);
            adapter.addFrag(f7, "Three");
        } else {
            tabLayout.setVisibility(View.GONE);
        }

        viewPager.setAdapter(adapter);

        /*Bundle b = new Bundle();
        b.putParcelable(User.USER, user);
        b.putBoolean(Fragment_1_Home_User.IS_FROM_PROFILE, true);


        //grid
        Fragment_5_User_Profile_Grid f5 = new Fragment_5_User_Profile_Grid();
        f5.setArguments(b);


        //list
        f6 = new Fragment_1_Home_User();
        f6.setArguments(b);


        //load danh sach hinh da luu
        b = new Bundle();
        b.putParcelable(User.USER, user);
        b.putBoolean(Fragment_1_Home_User.IS_FROM_PROFILE, true);
        b.putBoolean(User.IS_LOAD_FAVORITE, true);
        Fragment_5_User_Profile_Grid f7 = new Fragment_5_User_Profile_Grid();
        f7.setArguments(b);

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(f5, "One");
        adapter.addFrag(f6, "Two");
        adapter.addFrag(f7, "Three");

        viewPager.setAdapter(adapter);*/
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
    public static final String ACTION_REFRESH = "ACTION_REFRESH_" + Fragment_Profile_Owner.class.getSimpleName();
    public static final String ACTION_UPDATE_STATE_FOLLOW = "ACTION_UPDATE_STATE_FOLLOW_" + Fragment_Profile_Owner.class.getSimpleName();
    public static final String ACTION_WHEN_FOLLOW_OR_UNFOLLOW = "ACTION_WHEN_FOLLOW_OR_UNFOLLOW_" + Fragment_Profile_Owner.class.getSimpleName();
    public static final String ACTION_RELOAD_USER = "ACTION_RELOAD_USER";
    public static final String ACTION_SCROLL_TO_TOP = "ACTION_SCROLL_TO_TOP" + Fragment_Profile_Owner.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH)) {
                    if (isOwner) {
                        initOwner();
                    } else {
                        initUser();
                    }
                    setupTabs();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_WHEN_FOLLOW_OR_UNFOLLOW)) {
                    getInfoFollowOfMe();
                } else if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_STATE_FOLLOW)) {
                    //thay doi o trang user, trang owner thi van la edit profile
                    if (!isOwner) {
                        isFollowing = MyUtils.isFollowing(user.getUserId());
                        setButtonUI(isFollowing);
                    }
                } else if (ACTION_RELOAD_USER.equalsIgnoreCase(intent.getAction())) {
                    initOwner();
                } else if (ACTION_SCROLL_TO_TOP.equalsIgnoreCase(intent.getAction())) {
                    if (f6 != null) {
                        f6.imgGotoTop.performClick();
                        appBarLayout.setActivated(true);
                        appBarLayout.setExpanded(true, true);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REFRESH);
        filter.addAction(ACTION_UPDATE_STATE_FOLLOW);
        filter.addAction(ACTION_WHEN_FOLLOW_OR_UNFOLLOW);
        filter.addAction(ACTION_RELOAD_USER);
        filter.addAction(ACTION_SCROLL_TO_TOP);


        context.registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (context != null && receiver != null) context.unregisterReceiver(receiver);
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


            //vip
            setVip(user);
        }


        //init text follow/unfollow
        //lay danh sach nguoi dang following
        isFollowing = MyUtils.isFollowing(user.getUserId());
        setButtonUI(isFollowing);

        //click control
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
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
                                        getContext().sendBroadcast(intent);

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
                                        getContext().sendBroadcast(intent);
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

        btnCard.setVisibility(View.VISIBLE);
        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.chatWithUser(context, user.getUserChatCore());
            }
        });


    }


    private void setButtonUI(boolean isFollowing) {//owner thi ko co button nay
        //set text Follow
        if (isFollowing) {
            btnEditProfile.setText(R.string.following_title);
            btnEditProfile.setBackgroundResource(R.drawable.bg_sky_rectangle_border);
            btnEditProfile.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnEditProfile.setText(R.string.follow_title);
            btnEditProfile.setBackgroundResource(R.drawable.bg_sky_radian);
            btnEditProfile.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void initButtons() {

        //commons
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
                    intent.putExtra(User.USER_ID, user.getUserId());
                    startActivity(intent);
                } else {
                    MyUtils.showThongBao(context);
                }

            }
        });

        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, MH15_GroupUserActivity.class);
                intent.putExtra(User.USER_ID, user.getUserId());
                startActivity(intent);*/
                AllGroupActivity.Companion.openActivity(context, user.getUserId());
            }
        });

        //different

        if (isOwner) {
            //edit profile
            linearProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context, MH20_UserEditProfileActivity.class));
                }
            });

            //my store
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, MH22_SettingActivity.class));
                }
            });

            //my card
            btnCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, MH07_MyCardActivity.class));
                }
            });
        } else {
            //follow/unfollow
            isFollowing = MyUtils.isFollowing(user.getUserId());
            setButtonUI(isFollowing);
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                            getContext().sendBroadcast(intent);

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
                                            getContext().sendBroadcast(intent);
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

            //my card
            btnCard.setText(R.string.chat);
            btnCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyUtils.chatWithUser(context, user.getUserChatCore());
                }
            });
        }
    }

    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.imgShop)
    ImageView imgShop;
    @BindView(R.id.imgMenu)
    ImageView imgMenu;

    private void initToolbar() {
        if (isOwnerScreen) {//fragment owner
            toolbar.setVisibility(View.GONE);
        } else {//man hinh profile cua user, co the la owner
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setNavigationIcon(R.drawable.ic_svg_16_36dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });

            //neu la owner thi hien icon chat va shop
            if (isOwner) {
                relativeChat.setVisibility(View.VISIBLE);
                imgShop.setVisibility(View.VISIBLE);
                imgMenu.setVisibility(View.GONE);

                relativeChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MainChatActivity.class);
                        startActivity(intent);
                    }
                });

                imgShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(context, ShoppingActivity.class));
                    }
                });

            } else {//cua user thi hien menu report
                relativeChat.setVisibility(View.GONE);
                imgShop.setVisibility(View.GONE);
                imgMenu.setVisibility(View.VISIBLE);
                //HIEN THI MENU REPORT
                imgMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initPopupMenu();
                    }
                });
                getFollowingPushNotifySetting();
            }

        }
    }

    //menu//
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void initPopupMenu() {
        PopupMenu popup = new PopupMenu(context, imgMenu);
        MenuInflater inflater = popup.getMenuInflater();

        Menu menu = popup.getMenu();
        inflater.inflate(R.menu.menu_config_notify, menu);

        MenuItem item = menu.getItem(0);
        item.setChecked(isChecked);
//        com.workchat.core.utils.MyUtils.setForceShowIcon(popup);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_1) {//pin
                    // Set the text color to red
                    item.setChecked(!item.isChecked());
                    isChecked = item.isChecked();
                    updateFollowingPushNotifySetting(isChecked);
                    return true;
                } else if (itemId == R.id.action_2) {//unpin
                    MyUtils.showDialogReportUser(context, user.getUserId());
                    return true;
                }
                return false;
            }
        });

        popup.show();

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    boolean isChecked = false;

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
                                    isChecked = json.get("NewPost").getAsBoolean();
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

    @BindView(R.id.linearSocials)
    LinearLayout linearSocials;

    private void getSocialInfo(long userId) {
        String query = Webservices.GET_USER_SOCIAL(userId);
        //sau khi upload thanh cong
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        MyUtils.log("ok");
                        //{"data":{"User":{"UserId":7,"UserName":"phuongphammtp","SocialInfo":[{"NameCode":"Web","Link":"myweb.com"},{"NameCode":"Facebook","Link":""},{"NameCode":"Twitter","Link":""},{"NameCode":"Instagram","Link":""},{"NameCode":"Youtube","Link":""}]}}}
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject user = data.getJSONObject("User");
                                JSONArray socials = user.getJSONArray("SocialInfo");

                                ArrayList<SocialItem> list = new ArrayList<>();
                                for (int i = 0; i < socials.length(); i++) {
                                    JSONObject item = socials.getJSONObject(i);
                                    String name = item.getString("NameCode");
                                    String link = item.getString("Link");

                                    SocialItem social = new SocialItem();
                                    social.setNameCode(name);
                                    social.setLink(link);
                                    list.add(social);
                                }

                                if (list.size() > 0) {
                                    ProfileUtils.setSocialText(getContext(), getLayoutInflater(), linearSocials, list);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                    }
                });
    }


}
