package com.topceo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.objects.promotion.PromotionScreen;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MH08_SuggestActivity extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.recyclerView1)
    RecyclerView rv;
    @BindView(R.id.list_empty)
    TextView list_empty;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private void setRefresh(boolean isRefresh) {
        if (isRefresh) {//on
            if (swipeContainer != null && !swipeContainer.isRefreshing()) {
                swipeContainer.setRefreshing(isRefresh);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRefresh(false);
                    }
                }, 2500);
            }
        } else {//off
            if (swipeContainer != null && swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        }

    }

    private ImageItem item;
    private User user;
    private TinyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        ButterKnife.bind(this);

        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        setTitleBar();

        init();

        PromotionScreen.navigationScreen(context, PromotionScreen.DISCOVER);
    }

    private void init() {
        db = new TinyDB(this);
        user = (User) db.getObject(User.USER, User.class);

        initAdapter();
        getSuggestFollow(false);

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
                    getSuggestFollow(false);
                } else {
                    setRefresh(false);
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
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                customLoadMoreDataFromApi(page);
                /*User comment=mAdapter.getLastestItem();
                if(comment!=null){
                    getComments(item.getImageItemId(), comment.getItemId(), comment.getCreateDate());
                }*/
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new FollowAdapter(listComments, context, user.getUserId());
        rv.setAdapter(mAdapter);
    }


    private FollowAdapter mAdapter;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    private int suggestItems = Webservices.SUGGEST_ITEMS;
    private void getSuggestFollow(boolean isRelative) {
        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getSuggestFollow(isRelative).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<User> list = (ArrayList<User>) task.getResult();
                            if (list != null && list.size() > 0) {

                                //đầu tiên suggest theo sở thích và người có cùng đặc điểm, sở thích
                                //nếu số lượng < 15(suggestItems/2) thì lấy thêm người bình thuờng (có thể trùng nên lọc sau khi lấy về)
                                if (isRelative) {
                                    mAdapter.clear();
                                    mAdapter.addAll(list);
                                    if (list.size() < suggestItems / 2) {
                                        getSuggestFollow(false);
                                    }
                                } else {
                                    //nếu số lượng < 15(suggestItems/2) thì lấy thêm người bình thuờng (có thể trùng nên lọc sau khi lấy về)
                                    mAdapter.addAllCheckDuplicate(list);
                                }
                            }

                            if (mAdapter.getItemCount() > 0) {
                                if (list_empty.getVisibility() == View.VISIBLE) {
                                    list_empty.setVisibility(View.GONE);
                                }
                            } else {
                                if (list_empty.getVisibility() == View.GONE) {
                                    list_empty.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                getSuggestFollow(isRelative);
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
                    }

                    setRefresh(false);
                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    /* private Realm realm;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    ////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

   @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(User.ARRAY_LIST, mAdapter.mDataset);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            init();
        } else {
            realm = Realm.getDefaultInstance();
            db = new TinyDB(this);
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
            }
            ArrayList<User> list = savedInstanceState.getParcelableArrayList(User.ARRAY_LIST);
            if (list != null && list.size() > 0) {
                mAdapter.clear();
                mAdapter.addAll(list);
                list_empty.setVisibility(View.GONE);
            } else {
                list_empty.setVisibility(View.VISIBLE);
            }

            //tao lai cookie
            MyUtils.initCookie(context);
        }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;
    @BindView(R.id.title)TextView title;
    @BindView(R.id.imgShop)ImageView imgShop;

    private void setTitleBar() {
        title.setText(R.string.setting_search);
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
    ///////////////////////////////////////////////////
}
