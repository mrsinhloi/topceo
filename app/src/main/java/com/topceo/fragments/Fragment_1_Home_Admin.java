package com.topceo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.activity.MH08_SuggestActivity;
import com.topceo.config.MasterExoPlayerHelper;
import com.topceo.config.MyApplication;
import com.topceo.analytics.Engagement;
import com.topceo.config.MyExtentionsKt;
import com.topceo.config.VideoListConfig;
import com.topceo.db.TinyDB;
import com.topceo.login.MH15_SigninActivity;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.utils.EndlessRecyclerOnScrollListener;
import com.topceo.utils.MyUtils;
import com.topceo.adapter.FeedAdapter;
import com.topceo.views.AutoPlayVideoRecyclerView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_1_Home_Admin extends Fragment {

    public static final String IS_FROM_PROFILE = "IS_FROM_PROFILE";

    public Fragment_1_Home_Admin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtils.log("Fragment_1_Home_User - onCreate");
    }


    private Context context;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.list_empty)
    TextView empty;
    @BindView(R.id.imgEmptyView)
    ImageView imgEmptyView;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
        if (isRefresh) {//on
            if (swipeContainer != null && !swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        } else {//off
            if (swipeContainer != null && swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        }

    }

    private User user;
    private User guest;
    private User owner;//de so sanh dt voi hinh cua nguoi nay
    private boolean isOwnerProfile = true;

    @BindView(R.id.imgGotoTop)
    ImageView imgGotoTop;

    private boolean isRefresh = false;
    private boolean isFromProfile = false;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        MyUtils.log("Fragment_1_Home_User - onCreateView");
        context = getContext();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        ButterKnife.bind(this, view);

        MobileAds.initialize(getContext(), getString(R.string.admob_app_id));

        registerReceiver();
        TinyDB db = new TinyDB(getActivity());
        try {
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
                owner = (User) obj;
            }

        } catch (Exception e) {
            e.printStackTrace();

            //logout y/c dang nhap lai
            db.putBoolean(TinyDB.IS_LOGINED, false);
            getActivity().startActivity(new Intent(getActivity(), MH15_SigninActivity.class));
            getActivity().finish();
        }

        imgEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MH08_SuggestActivity.class);
                startActivity(intent);
            }
        });


        //lay tham so user goi qua, nguoi dc goi qua du la owner hay user thi cung nhu nhau
        Bundle b = getArguments();
        if (b != null) {//profile cua mot @username
            isFromProfile = b.getBoolean(IS_FROM_PROFILE, false);
            guest = b.getParcelable(User.USER);
            if (guest != null) {
                user = guest;
                //neu xem profile thi ko hien quang cao
                isHaveAds = false;

                //kiem tra co phai la tab SonTung
                if (user.getUserId() == 1) {
                    //load nhu binh thuong, chi thay doi ten ham
                    isOwnerProfile = true;
                    isSonTung = true;
                } else {
                    isOwnerProfile = false;
                }
            }
        }

        ////////////
        context = getActivity();
        initAdapter();

        ////////////////


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
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
//                fetchTimelineAsync(0);
                //read newsfeed
                if (MyUtils.checkInternetConnection(context)) {
                    setRefresh(true);
                    whenRefresh();
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });

        if (savedInstanceState == null) {

            if (isOwnerProfile) {//owner va sontung
                //read newsfeed
                setRefresh(true);
                getNewFeedFirst();
            } else {//newfeed home cua tung nguoi
                getListImage(0, 0);
            }


        } else {

            //tao lai cookie
            MyUtils.initCookie(context);

            ArrayList<ImageItem> list = savedInstanceState.getParcelableArrayList(ImageItem.IMAGE_ARRAY_LIST);
            if (list != null && list.size() > 0) {
                mAdapter.mDataset.clear();
                mAdapter.addAll(list);
            }


        }


        //go to top
        imgGotoTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.scrollToPosition(0);
            }
        });


        //setup quang cao
//        setUpQuangCao(context);

        return view;
    }

    private void whenRefresh() {
        if (isOwnerProfile) {
            //read newsfeed
            getNewFeedFirst();
        } else {
            getListImage(0, 0);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isHaveAds = false;

    private void initAdapter() {
        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
//        rv.setNestedScrollingEnabled(false);
//        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMore();
                MyUtils.showToastDebug(context, "Load more...");
            }

            private int oldPosition = -1;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visiblePosition = mLayoutManager.findFirstVisibleItemPosition();
                if (visiblePosition > -1 && visiblePosition != oldPosition) {

                    //khi scroll xuong thi moi ghi nhan, scroll len thi ko ghi nhan
                    if (visiblePosition > oldPosition) {
                        MyUtils.log("item visible = " + visiblePosition);
                        if (!listPosition.contains(visiblePosition)) {
                            listPosition.add(visiblePosition);
                            MyApplication.getInstance().insertMap(mAdapter.getItemId(visiblePosition), Engagement.TYPE_VIEW);
                        }
                    }

                    oldPosition = visiblePosition;
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new FeedAdapter(context, isOwnerProfile, owner);
        rv.setAdapter(mAdapter);
        helper = VideoListConfig.Companion.configVideoAutoPlaying(context, this, rv);

    }
    MasterExoPlayerHelper helper;

    //List position de kiem tra xem item nay da xem thi ko ghi nhan nua
    private ArrayList<Integer> listPosition = new ArrayList<>();

    private void loadMore() {
        if (isOwnerProfile) {
            customLoadMoreDataFromApi();
        } else {
            if (mAdapter != null && mAdapter.getItemCount() > 0) {
                ImageItem item = mAdapter.getLastestItem();
                if (item != null) {
                    getListImage(item.getImageItemId(), item.getCreateDate());
                }
            }
        }
    }


    private FeedAdapter mAdapter;
    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private boolean isNeedLoadMore = true;

    public void customLoadMoreDataFromApi() {

        if (isNeedLoadMore) {
            ImageItem lastestItem = mAdapter.getLastestItem();
            if (lastestItem != null) {

                Webservices.getNewFeedMore(lastestItem.getImageItemId(), lastestItem.getCreateDate(), isSonTung).continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {
                        if (task.getError() == null) {
                            if (task.getResult() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) task.getResult();
                                if (list != null && list.size() > 0) {
                                    mAdapter.addAll(list);
                                }
                            }
                        } else {
                            ANError error = (ANError) task.getError();
                            //recall service
                            boolean isLostCookie = MyApplication.controlException(error);
                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                customLoadMoreDataFromApi();
                                            }
                                        }
//                                        if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                                        return null;
                                    }
                                });
                            } else {
                                if (!TextUtils.isEmpty(error.getMessage())) {
                                    MyUtils.showToastDebug(context, error.getMessage());
                                }
                            }
                        }

                        return null;
                    }
                });

            }
        }
    }

    /////////////////////////////////////////////////////////
//    public static final String ACTION_FINISH ="ACTION_FINISH_"+Fragment_1_Home_User.class.getSimpleName();
    public static final String ACTION_REFRESH = "ACTION_REFRESH_" + Fragment_1_Home_Admin.class.getSimpleName();
    public static final String ACTION_UPDATE_ITEM = Fragment_1_Home_User.ACTION_UPDATE_ITEM;//"ACTION_UPDATE_ITEM_" + Fragment_1_Home_Admin.class.getSimpleName();
    //    public static final String ACTION_UPDATE_NUMBER_COMMENT = "ACTION_UPDATE_NUMBER_COMMENT_" + Fragment_1_Home_User.class.getSimpleName();
    public static final String ACTION_AFTER_DELETE_POST = "ACTION_AFTER_DELETE_POST_" + Fragment_1_Home_Admin.class.getSimpleName();
    public static final String ACTION_SCROLL_TO_TOP = "ACTION_SCROLL_TO_TOP";

    public static final String ACTION_SET_FRAGMENT_IN_PROFILE_MUTE = "ACTION_SET_FRAGMENT_IN_PROFILE_MUTE";
    public static final String ACTION_SET_MUTE_ALL = "ACTION_SET_MUTE_ALL";


    public static final String IS_HOME = "IS_HOME";


    private BroadcastReceiver receiver;

    private void registerReceiver() {
        if (getActivity() == null) return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH)) {
                    refresh();

                }
                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_ITEM)) {
                    /*Bundle b = intent.getExtras();
                    if (b != null) {

                        ImageItem item = b.getParcelable(ImageItem.IMAGE_ITEM);
                        if (item != null) {
//                            mAdapter.updateItemDescription(item);
                            mAdapter.replaceItem(item);
                        }
                    }*/
                    ImageItem item = MyApplication.itemReturn;
                    if (item != null) {
                        mAdapter.replaceItem(item);
                    }
                }

                /*if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_NUMBER_COMMENT)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        int count = b.getInt(ImageComment.IMAGE_ITEM_NUMBER_COMMENT, 0);
                        long imageItemId = b.getLong(ImageComment.IMAGE_ITEM_ID, 0);
                        if (imageItemId > 0) {
                            mAdapter.updateNumberComment(imageItemId, count);
                        }
                    }
                }*/

                if (intent.getAction().equalsIgnoreCase(ACTION_AFTER_DELETE_POST)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        //item nay chi chua id, va description
                        long imageItemId = b.getLong(ImageItem.IMAGE_ITEM_ID);
                        if (imageItemId > 0) {
                            //find position and delete it
                            mAdapter.remove(imageItemId);
                        }
                    }
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_SCROLL_TO_TOP)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        boolean isHome = b.getBoolean(IS_HOME, false);
                        if (isHome) {
                            if (guest == null) {//dung tab home
                                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                                    if (rv != null) {
                                        rv.smoothScrollToPosition(0);
                                    }
                                }
                            }
                        } else {
                            if (guest != null) {//dung tab home
                                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                                    if (rv != null) {
                                        rv.smoothScrollToPosition(0);
                                    }
                                }
                            }
                        }


                    }
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_SET_FRAGMENT_IN_PROFILE_MUTE)) {
                    if (isFromProfile) {
//                        requestStopVideo(true);
                    }
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_SET_MUTE_ALL)) {
//                    requestStopVideo(true);
                }

            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_REFRESH));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_ITEM));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_AFTER_DELETE_POST));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_SCROLL_TO_TOP));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_SET_FRAGMENT_IN_PROFILE_MUTE));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_SET_MUTE_ALL));


    }

    private void refresh() {
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                whenRefresh();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) getActivity().unregisterReceiver(receiver);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    boolean isSonTung = false;

    private void getNewFeedFirst() {
        if (MyUtils.checkInternetConnection(context)) {
            if (user.getRoleId() == User.ADMIN_ROLE_ID) {
                getHomePageAdmin();
            } else {
                getHomePageUser();
            }
        } else {
            MyUtils.showThongBao(context);
        }

    }

    private void getHomePageUser() {
        empty.setVisibility(View.VISIBLE);
        Webservices.getNewsFeedPageFirst(isSonTung)
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {
                        setRefresh(false);
                        setResult(task);

                        return null;
                    }
                });
    }

    private void getHomePageAdmin() {
        //ko can loadmore
        isNeedLoadMore = false;
        empty.setVisibility(View.VISIBLE);
        Webservices.getHomePageAdmin()
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {
                        setRefresh(false);
                        setResult(task);

                        return null;
                    }
                });
    }

    private void setResult(Task<Object> task) {
        empty.setVisibility(View.GONE);
        if (task.getError() == null) {//ko co exception
            ArrayList<ImageItem> list = (ArrayList<ImageItem>) task.getResult();
            if (list != null && list.size() > 0) {

                mAdapter.replaceAll(list);

                //up hinh
                if (!isRefresh) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemChanged(0);
                        }
                    }, 2000);
                }


            } else {
                //hien empty image
                imgEmptyView.setVisibility(View.VISIBLE);
            }


        } else {//co exception
            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
            if (isLostCookie) {
                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {
                        if (task.getResult() != null) {
                            //kiem tra neu bi banned
                            if (task.getResult() instanceof Integer) {
                                //chi hien thong bao 1 lan o tab sontung
                                if (isSonTung) {
                                    if (getActivity() != null && !getActivity().isFinishing()) {
                                        MyUtils.showAlertDialog(getActivity(), R.string.account_is_banned, true);
                                    }
                                }
                            } else {
                                User kq = (User) task.getResult();
                                if (kq != null) {
                                    //Khi login thanh cong thi co cookie moi nen goi khoi tao retrofit
                                    MyApplication.whenLoginSuccess();
                                    getNewFeedFirst();
                                }
                            }

                        }
                        return null;
                    }
                });
            } else {
                if (!TextUtils.isEmpty(task.getError().getMessage())) {
                    MyUtils.showToastDebug(context, task.getError().getMessage());
                }
            }

            //hien empty image
            imgEmptyView.setVisibility(View.VISIBLE);
        }
    }


    /////////////////////////////////////////////////////////////////////////////


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ImageItem.IMAGE_ARRAY_LIST, mAdapter.mDataset);
    }


    //GUEST///////////////////////////////////////////////////////////////////////////
    private void getListImage(final long lastImageItemId, final long lastDate) {
        Webservices.getListImageItemOfUser(user.getUserId(), lastImageItemId, lastDate)
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        if (task.getError() == null) {//ko co exception
                            ArrayList<ImageItem> list = (ArrayList<ImageItem>) task.getResult();
                            if (list != null && list.size() > 0) {
                                if (lastImageItemId == 0) {//dau tien
                                    mAdapter.clear();
                                    mAdapter.addAll(list);
                                } else {//load tiep
                                    mAdapter.addAll(list);
                                }
                            } else {
                                //hien empty image
                                imgEmptyView.setVisibility(View.VISIBLE);
                            }
                            MyUtils.log("Fragment_1_Home_User - getNewFeedFirst() - List size = " + list.size());
                        } else {//co exception
                            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                            MyUtils.log("Fragment_1_Home_User - getNewFeedFirst() - Exception = " + ((ANError) task.getError()).getErrorCode());

                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                getListImage(lastImageItemId, lastDate);
                                            }
                                        }
                                        return null;
                                    }
                                });
                            } else {
                                if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                    MyUtils.showToastDebug(context, task.getError().getMessage());
                                }
                            }
                        }

                        setRefresh(false);
                        return null;
                    }
                });


    }


    @Override
    public void onPause() {
        super.onPause();
        //goi thong tin ve server
        MyApplication.getInstance().sendAnalyticToServer();
    }



    //https://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
//            requestStopVideo(false);

            /*if (rv != null && rv.getHandingVideoHolder() != null) {
                rv.getHandingVideoHolder().playVideo();
            }*/
            if(helper!=null) {
                helper.getExoPlayerHelper().play();
            }

//            MyUtils.showToastDebug(getContext(), "resume " + isVisibleToUser);
        } else if (isVisibleToUser) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
//            MyUtils.showToastDebug(getContext(), "onCreated " + isVisibleToUser);
        } else if (!isVisibleToUser && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
//            MyUtils.showToastDebug(getContext(), "go out " + isVisibleToUser);
//            requestStopVideo(true);

            //todo stop video
            /*if (rv != null && rv.getHandingVideoHolder() != null) {
                rv.getHandingVideoHolder().stopVideo();
            }*/
            if(helper!=null) {
                helper.getExoPlayerHelper().pause();
            }
        }

    }


}
