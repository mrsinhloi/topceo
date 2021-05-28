package com.topceo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.adapter.FollowAdapter;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.language.LocalizationUtil;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserGroup;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MH15_GroupUserActivity extends AppCompatActivity {
    private Activity context = this;

    @BindView(R.id.recyclerView2)
    RecyclerView rv2;
    @BindView(R.id.recyclerView1)
    RecyclerView rv1;

    @BindView(R.id.list_empty)
    TextView list_empty;

    @BindView(R.id.txtGroup)
    TextView txtGroup;

    /*@BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private void setRefresh(boolean isRefresh) {
        if (isRefresh) {//on
            if (swipeContainer != null && !swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        } else {//off
            if (swipeContainer != null && swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        }

    }*/

    TinyDB db;
    private long userId = 0;
    private User owner;
    private boolean isEnglish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_group_user);
        ButterKnife.bind(this);
        setTitleBar();

        db = new TinyDB(context);
        isEnglish = !LocalizationUtil.INSTANCE.isVietnamese(context);

        ////
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


        //////////////////////////////////////////////////////////////////////////////////
        // Configure the refreshing colors
        /*swipeContainer.setColorSchemeResources(
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
                    getUserFollowings(userId, 0, 0);
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });*/

        ////////////////////////////////////////////////////////////////////////////
        initAdapter();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            long id = b.getLong(User.USER_ID, 0);
            if (id > 0) {
                userId = id;
            }
        }

        //lay data
        getGroups(userId);


    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<User> listComments = new ArrayList<>();

    private void initAdapter() {

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv1.setLayoutManager(mLayoutManager1);
        rv1.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(context);
        rv2.setLayoutManager(mLayoutManager2);

//        tv.setItemAnimator(new DefaultItemAnimator());
        rv2.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager2) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                User user = mAdapter.getLastestItem();
                if (user != null) {
                    getGroupMember(userId, groupSelected.getHashtagId(), user.getJoinDate());
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new FollowAdapter(listComments, context, owner.getUserId());
        rv2.setAdapter(mAdapter);
    }


    private FollowAdapter mAdapter;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    private UserGroup groupSelected;

    private void setGroupText() {
        if (groupSelected != null) {

            String txt = isEnglish ? groupSelected.getNameEN() : groupSelected.getName() +
                    ": " + groupSelected.getMemberCount() +
                    " " + context.getText(R.string._members);
            txtGroup.setText(txt);

        }
    }

    private void getGroups(long userId) {
        if (MyUtils.checkInternetConnection(context)) {
            AndroidNetworking.post(Webservices.API_URL + "user/group/getList")
                    .addBodyParameter("UserId", String.valueOf(userId))
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response.getInt("ErrorCode") == com.topceo.ads.ReturnResult.ERROR_CODE_THANH_CONG) {
                                    if (response.has("Data")) {
                                        JSONArray array = response.getJSONArray("Data");
                                        Type type = new TypeToken<ArrayList<UserGroup>>() {
                                        }.getType();
                                        ArrayList<UserGroup> list = new Gson().fromJson(array.toString(), type);
                                        if (list != null && list.size() > 0) {
                                            groupSelected = list.get(0);
                                            adapterGroup = new AdapterGroup(list);
                                            rv1.setAdapter(adapterGroup);

                                            //LOAD GROUP 1
                                            setGroupText();
                                            getGroupMember(userId, groupSelected.getHashtagId(), 0);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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


    private void getGroupMember(long userId, long HashtagId, long LastJoinDate) {
        if (MyUtils.checkInternetConnection(context)) {
            /*AndroidNetworking.post(Webservices.URL + "user/group/getMembers")
                    .addBodyParameter("HashtagId", String.valueOf(HashtagId))
                    .addBodyParameter("LastJoinDate", String.valueOf(LastJoinDate))
                    .addBodyParameter("Count", String.valueOf(20))
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if (response.getInt("ErrorCode") == com.ehubstar.ads.ReturnResult.ERROR_CODE_THANH_CONG) {
                                    if (response.has("Data")) {
                                        JSONArray array = response.getJSONArray("Data");
                                        Type type = new TypeToken<ArrayList<User>>() {
                                        }.getType();
                                        ArrayList<User> users = new Gson().fromJson(array.toString(), type);
                                        if (users != null && users.size() > 0) {
                                                if (LastJoinDate == 0) {//page 1
                                                    mAdapter.clear();
                                                    mAdapter.addAll(users);
                                                } else {//load more
                                                    mAdapter.addAll(users);
                                                }
                                        }

                                        //if first load
                                        if (LastJoinDate == 0) {
                                            if (mAdapter.getItemCount() > 0) {
                                                list_empty.setVisibility(View.GONE);
                                            } else {
                                                list_empty.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onError(ANError ANError) {

                            MyUtils.log(ANError.getMessage());
                            if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                            }
                        }
                    });*/

            MyApplication.apiManager.getMembers(
                    userId,
                    HashtagId,
                    LastJoinDate,
                    20,
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                Type collectionType = new TypeToken<ArrayList<User>>() {
                                }.getType();
                                ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                        ArrayList<User> users = (ArrayList<User>) result.getData();
                                        if (users != null && users.size() > 0) {
                                            if (LastJoinDate == 0) {//page 1
                                                mAdapter.clear();
                                                mAdapter.addAll(users);
                                            } else {//load more
                                                mAdapter.addAll(users);
                                            }
                                        }

                                        //if first load
                                        if (LastJoinDate == 0) {
                                            if (mAdapter.getItemCount() > 0) {
                                                list_empty.setVisibility(View.GONE);
                                            } else {
                                                list_empty.setVisibility(View.VISIBLE);
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

    ////////////////////////////////////////////////////////////////////////////////////////////
    private AdapterGroup adapterGroup;

    class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.ViewHolder> {
        private ArrayList<UserGroup> mDataset = new ArrayList<>();


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            @BindView(R.id.txt1)
            TextView txt1;
            @BindView(R.id.img1)
            ImageView img1;
            @BindView(R.id.relative1)
            RelativeLayout relative1;

            Typeface tp1;
            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);

                tp1 = txt1.getTypeface();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(avatarSize, avatarSize);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                img1.setLayoutParams(params);
            }
        }


        public void add(int position, UserGroup item) {
            mDataset.add(position, item);
            notifyItemInserted(position);
            notifyDataSetChanged();
        }

        public void remove(UserGroup item) {
            int position = -1;//= mDataset.indexOf(item);
            for (int i = 0; i < mDataset.size(); i++) {
                if (item.getUserId() == mDataset.get(i).getUserId()) {
                    position = i;
                    break;
                }
            }
            if (position >= 0) {
                mDataset.remove(position);
                notifyItemRemoved(position);
            }
        }

        public void remove(int position) {
            if (position >= 0) {
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        private int avatarSize = 0;
        private int widthScreen = 0;

        public AdapterGroup(ArrayList<UserGroup> myDataset) {
            mDataset = myDataset;
            widthScreen = MyUtils.getScreenWidth(context);
            avatarSize = widthScreen / 6;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public AdapterGroup.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_group_user_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        private TextView txtSelected;

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final UserGroup item = mDataset.get(position);

            Glide.with(context)
                    .load(item.getImageUrl())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);

            holder.txt1.setText(isEnglish ? item.getNameEN() : item.getName());

            holder.relative1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupSelected = item;
                    setGroupText();
                    getGroupMember(userId,groupSelected.getHashtagId(), 0);

                    //reset
                    if(txtSelected!=null){
                        txtSelected.setTypeface(holder.tp1);
                    }
                    //add new
                    holder.txt1.setTypeface(holder.txt1.getTypeface(), Typeface.BOLD);
                    txtSelected = holder.txt1;
                }
            });

            if(groupSelected!=null && groupSelected.getHashtagId()==item.getHashtagId()){
                holder.txt1.setTypeface(holder.txt1.getTypeface(), Typeface.BOLD);
                txtSelected = holder.txt1;
            }else{
                holder.txt1.setTypeface(holder.tp1);
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        //add by mr.pham
        //clear all items
        public void clear() {
            mDataset.clear();
            notifyDataSetChanged();
        }

        //add list items
        public void addAll(ArrayList<UserGroup> list) {
            mDataset.addAll(list);
            notifyDataSetChanged();
        }


    }
    ////////////////////////////////////////////////////////////////////////////////////////////

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
    public @BindView(R.id.imgShop)ImageView imgShop;

    public void setTitleBar() {
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
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////


}
