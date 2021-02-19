package com.topceo.shopping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_MyStore extends Fragment {

    private static final String ARG_TAG = "ARG_TAG";
    private String tag;

    public Fragment_MyStore() {
        // Required empty public constructor
    }

    public static Fragment_MyStore newInstance(String tag) {
        Fragment_MyStore fragment = new Fragment_MyStore();
        Bundle args = new Bundle();
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tag = getArguments().getString(ARG_TAG);
        }

    }

    private User user;
    private Context context;
    @BindView(R.id.rv)
    RecyclerView rv;


    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
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
        }

        context = getActivity();
        initAdapter();
        registerReceiver();

        txtEmpty.setText(R.string.my_store_empty);


        return view;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    private Fragment_Shop_Adapter mAdapter;

    private void initAdapter() {

       /* ArrayList<String> mDataset = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            mDataset.add("Item value " + i);
        }*/
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        rv.setHasFixedSize(true);

        // use a linear layout manager
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rv.setLayoutManager(layoutManager);
//        rv.addItemDecoration(new EqualSpaceItemDecoration(1));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.margin_8dp);
        rv.addItemDecoration(itemDecoration);

//        tv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                customLoadMoreDataFromApi(page);
                if (isLoadMore) {
                    if (mAdapter != null && mAdapter.getItemCount() > 0) {
                        Media lastItem = mAdapter.getLastItem();
                        if(lastItem!=null){
                            lastDate = lastItem.getCreateDate();
                            if(lastDate>0){
                                getData();
                            }
                        }

                    }
                }

            }
        });

        // specify an adapter (see also next example)
        mAdapter = new Fragment_Shop_Adapter(getContext(), new ArrayList<Media>());
        rv.setAdapter(mAdapter);

        getData();

    }

    private int PAGE_ITEM_COUNT = 15;
    private boolean isLoadMore = true;
    private long lastDate = 0;

    private void getData() {

        MyApplication.apiManager.getLibraryList(
                -1,
                -1,
                (lastDate == 0) ? null : String.valueOf(lastDate),
                PAGE_ITEM_COUNT,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<Media>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<Media> list = (ArrayList<Media>) result.getData();
                                    if (list.size() > 0) {
                                        if (lastDate == 0) {//dau tien
                                            mAdapter.clear();
                                            mAdapter.addMore(list);
                                            txtEmpty.setVisibility(View.GONE);
                                        } else {//load tiep
                                            mAdapter.addMore(list);
                                        }
                                    }

                                    //danh sach nho hon page thi dung load more
                                    if (list.size() < PAGE_ITEM_COUNT) {
                                        isLoadMore = false;
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


    ///////////////////////////////////////////////////////////////////////////////////////////
    private BroadcastReceiver receiver;
    public static final String REFRESH_LIBRARY = "REFRESH_LIBRARY_Fragment_MyStore";

    private void registerReceiver() {
        if (getActivity() == null) return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle b = intent.getExtras();
                switch (intent.getAction()) {
                    case Fragment_Shop.ACTION_UPDATE_MEDIA:
                        Media media = b.getParcelable(Media.MEDIA);
                        if (media != null) {
                            if (mAdapter != null) {
                                mAdapter.replaceItem(media);
                            }
                        }
                        break;
                    case REFRESH_LIBRARY:
                        lastDate = 0;
                        isLoadMore = true;
                        getData();
                        break;
                }

            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(Fragment_Shop.ACTION_UPDATE_MEDIA));
        getActivity().registerReceiver(receiver, new IntentFilter(REFRESH_LIBRARY));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null && getActivity() != null) getActivity().unregisterReceiver(receiver);
    }


}
