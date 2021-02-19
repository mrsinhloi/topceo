package com.topceo.group;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.group.models.GroupInfo;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.SimpleItemDecorator;
import com.topceo.viewholders.FeedAdapter;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupHorizontalAdapter_ViewHolder extends RecyclerView.ViewHolder {

    // each data item is just a string in this case
    Context context;
    @BindView(R.id.rvHorizontal)
    RecyclerView rvHorizontal;
    @BindView(R.id.linearGroupRoot)
    LinearLayout linearGroupRoot;

    GroupHorizontalAdapter groupAdapter;
    public GroupHorizontalAdapter_ViewHolder(View v, Context context) {
        super(v);
        ButterKnife.bind(this, v);
        this.context = context;


        //suggestion
        int space = context.getResources().getDimensionPixelOffset(R.dimen.margin_10dp);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvHorizontal.setLayoutManager(mLayoutManager2);
        rvHorizontal.setHasFixedSize(true);
        rvHorizontal.addItemDecoration(new SimpleItemDecorator(space));
        rvHorizontal.setItemAnimator(new DefaultItemAnimator());

        //suggest
        groupAdapter = new GroupHorizontalAdapter(new ArrayList<GroupInfo>(), context);
        rvHorizontal.setAdapter(groupAdapter);


    }

    private FeedAdapter adapter;
    private boolean isFirst = true;
    public void bind(boolean isRefresh, FeedAdapter feedAdapter) {
        adapter = feedAdapter;
        if (list.size() > 0) {
            //da co san thi ko lam gi
            if (isRefresh) {
                groupList("", 1, 8);
            }
        } else {
            //get top group
            groupList("", 1, 8);
        }

    }

    ArrayList<GroupInfo> list = new ArrayList<>();

    private void groupList(String keyword,
                           int pageIndex,
                           int pageSize) {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.groupList(
                    keyword, pageIndex, pageSize,
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                Type collectionType = new TypeToken<ArrayList<GroupInfo>>() {
                                }.getType();
                                ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                        list = (ArrayList<GroupInfo>) result.getData();
                                        if (list != null && list.size() > 0) {
                                            if (pageIndex == 1) {
                                                initGroup(list);
                                                adapter.addGroup();
                                            } else {
                                                //load more
                                            }

                                        } else {
                                            if (pageIndex == 1) {
                                                //ko co group nao thi mo all group
                                                adapter.removeGroup();
                                                if(isFirst){
                                                    isFirst = false;
                                                    AllGroupActivity.Companion.openActivity(context,0);
                                                }
                                            }
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

    }

    private void getGroupTotal() {
        MyApplication.apiManager.groupTotal(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject data = response.body();
                if (data != null) {
                    ReturnResult result = Webservices.parseJson(data.toString(), Integer.class, false);
                    if (result != null) {
                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                            int total = (int) result.getData();
                            if (total > 0) {
                                if (groupAdapter != null) {
                                    groupAdapter.showTotal(total);
                                }
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

    private void initGroup(ArrayList<GroupInfo> list) {
//        list = increaseList(list);
        GroupInfo all = new GroupInfo();
        all.setGroupId(0);
//        all.setGroupName(context.getString(R.string.all));
        all.setGroupName("");
        list.add(list.size(), all);

        groupAdapter.update(list);
    }

    private ArrayList<GroupInfo> increaseList(ArrayList<GroupInfo> list) throws CloneNotSupportedException {
        ArrayList<GroupInfo> data = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            GroupInfo item = list.get(0);
            item.setGroupId(i + 1);
            item.setGroupName("Group 00" + i);
            data.add(item);
        }

        return data;
    }
}
