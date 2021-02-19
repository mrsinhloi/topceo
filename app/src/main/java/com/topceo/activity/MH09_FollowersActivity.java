package com.topceo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.adapter.FollowAdapter;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MH09_FollowersActivity extends AppCompatActivity {
    private Activity context = this;
    @BindView(R.id.recyclerView1)
    RecyclerView rv;
    @BindView(R.id.list_empty)
    TextView list_empty;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private void setRefresh(boolean isRefresh) {
        if (isRefresh) {//on
            if (swipeContainer != null && !swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        } else {//off
            if (swipeContainer != null && swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        }

    }

    private long userId = 0;
    private User owner;
    private TinyDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        ButterKnife.bind(this);
        setTitleBar();

        db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            owner = (User) obj;
            userId = owner.getUserId();
        }


        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/


        initAdapter();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            long id = b.getLong(User.USER_ID, 0);
            if (id > 0) {
                userId = id;
            }
        }

        //lay data
        getUserFollowers(userId, 0, 0);


        //////////////////////////////////////////////////////////////////////////////////
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (MyUtils.checkInternetConnection(context)) {
                    //lay page 0
                    setRefresh(true);
                    getUserFollowers(userId, 0, 0);
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<User> listComments = new ArrayList<>();

    private void initAdapter() {
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(context);
        rv.setLayoutManager(mLayoutManager);

//        tv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                User follower = mAdapter.getLastestItem();
                if (follower != null) {
                    getUserFollowers(userId, follower.getUserId(), follower.getCreateDate());
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new FollowAdapter(listComments, context, owner.getUserId());
        rv.setAdapter(mAdapter);
    }


    private FollowAdapter mAdapter;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param userId
     * @param lastItemId
     */
    private void getUserFollowers(final long userId, final long lastItemId, final long lastCreateDate) {
        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getListUserFollower(userId, lastItemId, lastCreateDate, Webservices.FOLLOW_COUNT_ITEM).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<User> users = (ArrayList<User>) task.getResult();
                            MyUtils.log(users.size() + "");
                            if (users != null && users.size() > 0) {

                                if (lastItemId == 0) {//page 1
                                    mAdapter.clear();
                                    mAdapter.addAll(users);
                                } else {//load more
                                    mAdapter.addAll(users);
                                }

                            }

                            //if first load
                            if (lastItemId == 0) {
                                if (mAdapter.getItemCount() > 0) {
                                    list_empty.setVisibility(View.GONE);
                                } else {
                                    list_empty.setVisibility(View.VISIBLE);
                                }
                            }

                            users.clear();

                        } else {
//                            MyUtils.showToast(context, R.string.not_found);
                        }
                    } else {
//                    MyUtils.showToast(context, task.getError().getMessage());
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());

                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            getUserFollowers(userId, lastItemId, lastCreateDate);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }
                        }

                    }

                    setRefresh(false);
                    return null;
                }
            });
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //#region SETUP TOOLBAR////////////////////////////////////////////////////////////////////////
    public @BindView(R.id.toolbar)
    Toolbar toolbar;
    public @BindView(R.id.imgBack)
    ImageView imgBack;
    public @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    public @BindView(R.id.txtNumber)
    TextView txtNumber;
    public @BindView(R.id.title)TextView title;
    public @BindView(R.id.imgShop)ImageView imgShop;

    public void setTitleBar() {
        title.setText(getText(R.string.follower_title));
        //hide icon navigation
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MH01_MainActivity.isExist) {
                    startActivity(new Intent(context, MH01_MainActivity.class));
                }
                finish();
            }
        });
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
        ChatUtils.setChatUnreadNumber(txtNumber);
        registerReceiver();
    }

    private BroadcastReceiver receiver;
    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);

        registerReceiver(receiver, filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }
    //#endregion///////////////////////////////////////////////////////////////////////////////////


}
