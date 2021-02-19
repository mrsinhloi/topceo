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
import com.topceo.objects.menu.MenuShop;
import com.topceo.objects.other.User;
import com.topceo.objects.promotion.PromotionScreen;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Shop extends Fragment {

    private static final String ARG_TAG = "ARG_TAG";
    private MenuShop menuShop;

    public Fragment_Shop() {
        // Required empty public constructor
    }

    public static Fragment_Shop newInstance(MenuShop menuShop) {
        Fragment_Shop fragment = new Fragment_Shop();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TAG, menuShop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menuShop = getArguments().getParcelable(ARG_TAG);
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

        txtEmpty.setText(R.string.have_not_content);


        //kiem tra co promotion cho man hinh MTP thi hien len
        PromotionScreen.navigationScreen(context, PromotionScreen.SHOP);

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

        int columns = 3;
        //neu la video thi 2 cot
        if(menuShop.getMenuName().equalsIgnoreCase("Video")){
            columns = 2;
        }

        // use a linear layout manager
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
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
                if(isLoadMore){
                    if (mAdapter != null && mAdapter.getItemCount() > 0) {
                        pageIndex+=1;
                        getData();
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
    private int pageIndex = 1;
    private boolean isLoadMore = true;
    private void getData() {

        List<String> listTag = null;
        int isFree = 1;
        try {
            JSONObject obj = new JSONObject(menuShop.getTargetContent().toString());
            String tag = obj.getString("Tags");
            if(!tag.equalsIgnoreCase("[]")){
                JSONArray array = obj.getJSONArray("Tags");
                Type type = new TypeToken<List<String>>(){}.getType();
                listTag = new Gson().fromJson(array.toString(), type);
            }

            isFree = obj.getInt("IsFree");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*MyApplication.apiManager.getListMedia(
                (MediaType.ALL.equals(targetContent)) ? null : targetContent,
                (MediaType.ALL.equals(targetContent)) ? 1 : -1,
                pageIndex,
                PAGE_ITEM_COUNT,*/
        MyApplication.apiManager.getListMedia(
                listTag,
                isFree,
                pageIndex,
                PAGE_ITEM_COUNT,
                false,
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
                                        if (pageIndex == 1) {//dau tien
                                            mAdapter.clear();
                                            mAdapter.addMore(list);

                                            txtEmpty.setVisibility(View.GONE);
                                        } else {//load tiep
                                            mAdapter.addMore(list);
                                        }
                                    }

                                    //danh sach nho hon page thi dung load more
                                    if(list.size()<PAGE_ITEM_COUNT){
                                        isLoadMore = false;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        MyUtils.log("error");
                    }
                });

    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_UPDATE_MEDIA = "ACTION_UPDATE_MEDIA_Fragment_Shop";

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        if (getActivity() == null) return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle b = intent.getExtras();
                switch (intent.getAction()){
                    case ACTION_UPDATE_MEDIA:
                        Media media = b.getParcelable(Media.MEDIA);
                        if(media!=null){
                            if(mAdapter!=null){
                                mAdapter.replaceItem(media);
                            }
                        }
                        break;
                }

            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_MEDIA));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null && getActivity() != null) getActivity().unregisterReceiver(receiver);
    }


}
