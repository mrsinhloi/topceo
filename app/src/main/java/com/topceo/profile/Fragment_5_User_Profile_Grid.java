package com.topceo.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.adapter.UserProfile_Grid_Adapter;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.login.MH15_SigninActivity;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.views.EqualSpaceItemDecoration;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_5_User_Profile_Grid extends Fragment {

    private User user;

    public Fragment_5_User_Profile_Grid() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtils.log("Fragment_5_User_Profile_Grid - onCreate");
    }


    private Context context;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;

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


    private boolean isOwner = true;
    private boolean isLoadFavorite = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyUtils.log("Fragment_5_User_Profile_Grid - onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_5, container, false);
        ButterKnife.bind(this, view);

        //lay user dang login
        TinyDB db = new TinyDB(getActivity());
        try {
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();

            //logout y/c dang nhap lai
            db.putBoolean(TinyDB.IS_LOGINED, false);
            getActivity().startActivity(new Intent(getActivity(), MH15_SigninActivity.class));
            getActivity().finish();
        }

        //lay tham so user goi qua, nguoi dc goi qua du la owner hay user thi cung nhu nhau
        Bundle b = getArguments();
        if (b != null) {//profile cua mot @username
            User u = b.getParcelable(User.USER);
            if (u != null) {
                isOwner = u.getUserId() == user.getUserId();
                user = u;
            }

            isLoadFavorite = b.getBoolean(User.IS_LOAD_FAVORITE, false);
        }

        ////////////
        context = getActivity();
        initAdapter();//lay luon data


        //////////////////////////////////////////////////////////////////////////////////
        registerReceiver();


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
                refresh();
            }
        });


        if (isLoadFavorite) {
            txtEmpty.setText(R.string.you_have_not_save_post);
            txtEmpty.setOnClickListener(null);
            txtEmpty.setFocusable(false);
        } else {
            /*if(isOwner) {
                txtEmpty.setText(R.string.please_post_first_photo);
                swipeContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mAdapter==null || mAdapter.getItemCount()==0) {
                            getContext().sendBroadcast(new Intent(MH01_MainActivity.ACTION_PICK_IMAGE));
                        }
                    }
                });
            }*/
        }


        return view;
    }

    private void refresh() {
        if (MyUtils.checkInternetConnection(context)) {
            //lay page 0
            setRefresh(true);
            //lay page 0
            getData();
        } else {
            MyUtils.showThongBao(getActivity());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    private UserProfile_Grid_Adapter mAdapter;

    private void initAdapter() {

       /* ArrayList<String> mDataset = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            mDataset.add("Item value " + i);
        }*/
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        rv.setHasFixedSize(true);

        // use a linear layout manager
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(new EqualSpaceItemDecoration(1));
//        tv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                customLoadMoreDataFromApi(page);
                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                    ImageItem item = mAdapter.getItem(mAdapter.getItemCount() - 1);
                    if (item != null) {
                        if (isLoadFavorite) {
                            getSavedImages(item.getSavedDate());
                        } else {
                            getListImage(item.getImageItemId(), item.getCreateDate());
                        }
                    }
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new UserProfile_Grid_Adapter(getContext());
        rv.setAdapter(mAdapter);

        getData();

    }

    private void getData() {
        //lay page 0
        if (isLoadFavorite) {
            getSavedImages(0);
        } else {
            getListImage(0, 0);
        }
    }


    private void getListImage(final long lastImageItemId, final long lastDate) {

        Webservices.getListImageItemOfUser(user.getUserId(), lastImageItemId, lastDate)
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        if (task.getError() == null) {//ko co exception
                            if (task.getResult() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) task.getResult();
                                if (list.size() > 0) {
                                    if (lastImageItemId == 0) {//dau tien
                                        mAdapter.clear();
                                        mAdapter.setItems(list);
                                    } else {//load tiep
                                        mAdapter.addAll(list);
                                    }
                                } else {

                                }
                            }
//                            if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                        } else {//co exception
                            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
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
                                    MyUtils.showToast(context, task.getError().getMessage());
                                }
                            }
                        }

                        setRefresh(false);

                        if (mAdapter != null && mAdapter.getItemCount() == 0) {
                            txtEmpty.setVisibility(View.VISIBLE);
                        } else {
                            txtEmpty.setVisibility(View.GONE);
                        }

                        return null;
                    }
                });


    }

    private void getSavedImages(final long lastSavedDate) {

        //neu la owner thi truyen 0, de ben duoi ko truyen thuoc tinh nay
        Webservices.getSavedImages((isOwner) ? 0 : user.getUserId(), lastSavedDate)
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        if (task.getError() == null) {//ko co exception
                            if (task.getResult() != null) {
                                ArrayList<ImageItem> list = (ArrayList<ImageItem>) task.getResult();
                                if (list.size() > 0) {
                                    if (lastSavedDate == 0) {//dau tien
                                        mAdapter.clear();
                                        mAdapter.setItems(list);
                                    } else {//load tiep
                                        mAdapter.addAll(list);
                                    }
                                } else {
                                    if (lastSavedDate == 0) {//dau tien
                                        mAdapter.clear();
                                        mAdapter.setItems(list);
                                    }
                                }
                            }
//                            if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                        } else {//co exception
                            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                getSavedImages(lastSavedDate);
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

                        if (mAdapter != null && mAdapter.getItemCount() == 0) {
                            txtEmpty.setVisibility(View.VISIBLE);
                        } else {
                            txtEmpty.setVisibility(View.GONE);
                        }

                        return null;
                    }
                });


    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_AFTER_DELETE_POST = "ACTION_AFTER_DELETE_POST_" + Fragment_5_User_Profile_Grid.class.getSimpleName();
    public static final String ACTION_UPDATE_ITEM = "ACTION_UPDATE_ITEM" + Fragment_5_User_Profile_Grid.class.getSimpleName();
    public static final String ACTION_REFRESH_LIST_SAVED = "ACTION_REFRESH_LIST_SAVED" + Fragment_5_User_Profile_Grid.class.getSimpleName();
    public static final String ACTION_REFRESH_LIST = "ACTION_REFRESH_LIST_" + Fragment_5_User_Profile_Grid.class.getSimpleName();

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        if (getActivity() == null) return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equalsIgnoreCase(ACTION_AFTER_DELETE_POST)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        //item nay chi chua id, va description
                        long imageItemId = b.getLong(ImageItem.IMAGE_ITEM_ID);
                        if (imageItemId > 0) {
                            //find position and delete it
                            mAdapter.removeItem(imageItemId);
                            if (mAdapter.getItemCount() == 0) {
                                txtEmpty.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_ITEM)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        ImageItem image = b.getParcelable(ImageItem.IMAGE_ITEM);
                        if (image != null) {
                            mAdapter.replaceItem(image);
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH_LIST_SAVED)) {
                    if (isLoadFavorite) {
                        refresh();
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH_LIST)) {
                    if (!isLoadFavorite) {
                        refresh();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_AFTER_DELETE_POST);
        filter.addAction(ACTION_REFRESH_LIST);
        filter.addAction(ACTION_UPDATE_ITEM);
        filter.addAction(ACTION_REFRESH_LIST_SAVED);

        if (getContext() != null)
            getContext().registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null && getActivity() != null) getActivity().unregisterReceiver(receiver);
    }


}
