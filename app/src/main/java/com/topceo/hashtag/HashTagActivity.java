package com.topceo.hashtag;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.adapter.UserProfile_Grid_Adapter;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.HashTag;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * http://joerichard.net/android/android-material-design-searchview-example/
 */
public class HashTagActivity extends AppCompatActivity {

    private Context context=this;
    @Nullable @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.rv)RecyclerView rv;
    @BindView(R.id.title)TextView title;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkChat));
        }
    }

    private String hashTag="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hashtag);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //lay hashtag
        Bundle b=getIntent().getExtras();
        if(b!=null){
            hashTag=b.getString(HashTag.HASH_TAG, "");
        }

        title.setText("#"+hashTag);
        initAdapter();//lay luon data


        //////////////////////////////////////////////////////////////////////////////////
        registerReceiver();


    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_AFTER_DELETE_POST="ACTION_AFTER_DELETE_POST_" + HashTagActivity.class.getSimpleName();
    public static final String ACTION_UPDATE_ITEM="ACTION_UPDATE_ITEM" + HashTagActivity.class.getSimpleName();

    private BroadcastReceiver receiver;

    private void registerReceiver() {
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
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        ImageItem image=b.getParcelable(ImageItem.IMAGE_ITEM);
                        if(image!=null){
                            mAdapter.replaceItem(image);
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_AFTER_DELETE_POST));
        registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_ITEM));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null)unregisterReceiver(receiver);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
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
        mAdapter = new UserProfile_Grid_Adapter(context);
        rv.setAdapter(mAdapter);

        //lay page 0
        getListImage(0,0);
    }


    private void getListImage(final long lastImageItemId, final long lastDate) {

        if(!TextUtils.isEmpty(hashTag)){
            Webservices.getListImageItemByHashTag(hashTag, lastImageItemId, lastDate)
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
                                MyUtils.log("Fragment_1_Home_User - getNewFeed() - List size = "+list.size());
//                            if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                            }else{//co exception
                                boolean isLostCookie= MyApplication.controlException((ANError) task.getError());
                                MyUtils.log("Fragment_1_Home_User - getNewFeed() - Exception = "+((ANError) task.getError()).getErrorCode());

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

                            return null;
                        }
                    });
        }else {
            MyUtils.showToast(context, R.string.hashtag_empty);
        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////

}
