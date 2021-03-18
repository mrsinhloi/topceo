package com.topceo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.adapter.UserProfile_Grid_Adapter;
import com.topceo.db.TinyDB;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.search.SearchActivity;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.views.EqualSpaceItemDecoration;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Fragment_2_Explorer extends Fragment {

    public Fragment_2_Explorer() {
        // Required empty public constructor
    }

    private User user;
    private Context context;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.linearSearch)LinearLayout linearSearch;

    @BindView(R.id.swipeContainer)SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        ButterKnife.bind(this, view);

        //lay user dang login
        TinyDB db = new TinyDB(getActivity());
        try {
            Object obj=db.getObject(User.USER, User.class);
            if(obj!=null) {
                user = (User)obj;
            }
        } catch (Exception e) {
            e.printStackTrace();

            //logout y/c dang nhap lai, fragment 1 control roi
//            db.putBoolean(TinyDB.IS_LOGINED, false);
//            getActivity().startActivity(new Intent(getActivity(), MH15_SigninActivity.class));
//            getActivity().finish();
        }

        context = getActivity();
        initAdapter();

        registerReceiver();

        linearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, SearchActivity.class);
                startActivity(intent);
            }
        });


        if(savedInstanceState==null){
            //lay page 0
            getListImage(0,0);
        }else{
            //tao lai cookie
            MyUtils.initCookie(context);

            ArrayList<ImageItem> list=savedInstanceState.getParcelableArrayList(ImageItem.IMAGE_ARRAY_LIST);
            if(list!=null && list.size()>0){
                mAdapter.mDataset.clear();
                mAdapter.addItems(list);
            }
        }

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
                if(MyUtils.checkInternetConnection(context)){
                    //lay page 0
                    initAdapter();
                    setRefresh(true);
                    getListImage(0,0);
                }else{
                    MyUtils.showThongBao(getActivity());
                }
            }
        });


        return view;
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
                if(mAdapter!=null && mAdapter.getItemCount()>0){
                    ImageItem item=mAdapter.getItem(mAdapter.getItemCount()-1);
                    if(item!=null){
                        getListImage(item.getImageItemId(), item.getCreateDate());
                    }
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new UserProfile_Grid_Adapter(getContext());
        rv.setAdapter(mAdapter);


    }



    private void setRefresh(boolean isRefresh){
        if(isRefresh){//on
            if (swipeContainer!=null && !swipeContainer.isRefreshing())swipeContainer.setRefreshing(isRefresh);
        }else{//off
            if (swipeContainer!=null && swipeContainer.isRefreshing())swipeContainer.setRefreshing(isRefresh);
        }

    }
    private void getListImage(final long lastImageItemId, final long lastDate) {

        Webservices.getListImageItemExplorer(lastImageItemId, lastDate)
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        if(task.getError()==null){//ko co exception
                            ArrayList<ImageItem> list=(ArrayList<ImageItem>)task.getResult();
                            if (list.size() > 0) {
                                if(lastImageItemId==0){//dau tien
                                    mAdapter.clear();
                                    mAdapter.setItems(list);
                                }else{//load tiep
                                    mAdapter.addAll(list);
                                }
                            }
                            MyUtils.log("Fragment_2_Home - getListImageItemExplorer() - List size = "+list.size());
//                            if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                        }else{//co exception
                            boolean isLostCookie= MyApplication.controlException((ANError) task.getError());
                            MyUtils.log("Fragment_2_Home - getListImageItemExplorer() - Exception = "+((ANError) task.getError()).getErrorCode());

                            if(isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq!=null) {
                                                getListImage(lastImageItemId, lastDate);
                                            }
                                        }
//                                        if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                                        return null;
                                    }
                                });
                            }else{
                                if(!TextUtils.isEmpty(task.getError().getMessage())) {
                                    MyUtils.showToast(context, task.getError().getMessage());
                                }
                            }
                        }

                        setRefresh(false);

                        return null;
                    }
                });


    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_AFTER_DELETE_POST="ACTION_AFTER_DELETE_POST_" + Fragment_2_Explorer.class.getSimpleName();
    public static final String ACTION_UPDATE_ITEM="ACTION_UPDATE_ITEM" + Fragment_2_Explorer.class.getSimpleName();

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        if(getActivity()==null)return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equalsIgnoreCase(ACTION_AFTER_DELETE_POST)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        //item nay chi chua id, va description
                        long imageItemId = b.getLong(ImageItem.IMAGE_ITEM_ID);
                        if (imageItemId>0) {
                            //find position and delete it
                            mAdapter.removeItem(imageItemId);
                        }
                    }
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_ITEM)) {
                    /*Bundle b = intent.getExtras();
                    if (b != null) {
                        ImageItem image=b.getParcelable(ImageItem.IMAGE_ITEM);
                        if(image!=null){
                            mAdapter.replaceItem(image);
                        }
                    }*/
                    ImageItem image = MyApplication.itemReturn;
                    if (image != null) {
                        mAdapter.replaceItem(image);
                    }
                }

            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_AFTER_DELETE_POST));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_ITEM));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null&&getActivity()!=null)getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ImageItem.IMAGE_ARRAY_LIST, mAdapter.mDataset);
    }
}
