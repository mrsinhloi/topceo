package com.topceo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.db.UserShortDB;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.ImageLike;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.utils.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MH11_LikeActivity extends AppCompatActivity {
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
            if (swipeContainer != null && !swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        } else {//off
            if (swipeContainer != null && swipeContainer.isRefreshing())
                swipeContainer.setRefreshing(isRefresh);
        }

    }

    private ImageItem item;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        ButterKnife.bind(this);
        setTitleBar();

        TinyDB db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }
        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        Bundle b = getIntent().getExtras();
        if (b != null) {
            item = b.getParcelable(ImageItem.IMAGE_ITEM);
        }

        init();


    }

    private void init() {
        initAdapter();
        if (item != null) {
            getLikes(item.getImageItemId(), 0, 0);
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
                if (MyUtils.checkInternetConnection(context)) {
                    //lay page 0
                    setRefresh(true);
                    getLikes(item.getImageItemId(), 0, 0);
                } else {
                    MyUtils.showThongBao(context);
                }
            }
        });
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<ImageLike> listComments = new ArrayList<>();

    private void initAdapter() {
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv.setLayoutManager(mLayoutManager);

//        tv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                customLoadMoreDataFromApi(page);
                ImageLike comment = mAdapter.getLastestItem();
                if (comment != null) {
                    if (comment.getItemId() != lastItemOld) {
                        lastItemOld = comment.getItemId();
                        getLikes(item.getImageItemId(), comment.getItemId(), comment.getCreateDate());
                    }
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(listComments);
        rv.setAdapter(mAdapter);
    }

    private long lastItemOld = 0;

    private MyAdapter mAdapter;

    /**
     * https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
     * https://github.com/wasabeef/recyclerview-animators
     * <p/>
     * notifyItemChanged(int pos)	Notify that item at position has changed.
     * notifyItemInserted(int pos)	Notify that item reflected at position has been newly inserted.
     * notifyItemRemoved(int pos)	Notify that items previously located at position has been removed from the data set.
     * notifyDataSetChanged()	    Notify that the dataset has changed. Use only as last resort.
     * rvContacts.scrollToPosition(0);   // index 0 position
     */
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<ImageLike> mDataset = new ArrayList<>();

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txt1, txt2;
            public ImageView img1;
            public ToggleButton btn;

            public ViewHolder(View v) {
                super(v);
                txt1 = (TextView) v.findViewById(R.id.textView1);
                txt2 = (TextView) v.findViewById(R.id.textView2);
                btn = (ToggleButton) v.findViewById(R.id.button1);

                img1 = (ImageView) v.findViewById(R.id.imageView1);

                img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));

                // set interpolators for both expanding and collapsing animations
//                txt5.setInterpolator(new OvershootInterpolator());


            }
        }


        public void add(int position, ImageLike item) {
            mDataset.add(position, item);
            notifyItemInserted(position);
            notifyDataSetChanged();
        }

        public void remove(ImageComment item) {
            int position = -1;//= mDataset.indexOf(item);
            for (int i = 0; i < mDataset.size(); i++) {
                if (item.getItemId() == mDataset.get(i).getItemId()) {
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

        public MyAdapter(ArrayList<ImageLike> myDataset) {
            mDataset = myDataset;
            widthScreen = MyUtils.getScreenWidth(context);
            avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_list_item);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_like_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final ImageLike item = mDataset.get(position);


            Glide.with(context)
                    .load(item.getUser().getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);


            holder.txt1.setText(item.getUser().getUserName());

            if (!TextUtils.isEmpty(item.getUser().getFullName())) {
                holder.txt2.setText(item.getUser().getFullName());
                holder.txt2.setVisibility(View.VISIBLE);
            } else {
                holder.txt2.setVisibility(View.GONE);
            }

            holder.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.gotoProfile(item.getUser().getUserName(), context);
                }
            });
            holder.txt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.img1.performClick();
                }
            });
            holder.txt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.img1.performClick();
                }
            });

            //thong tin user da like
            final UserShortDB uLike = item.getUser();
            holder.btn.setOnCheckedChangeListener(null);
            setStateButton(uLike, holder.btn);
            holder.btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    /*if (b) {//on following
                        holder.btn.setTextColor(context.getResources().getColor(R.color.black));
                    } else {//off follow
                        holder.btn.setTextColor(context.getResources().getColor(R.color.white));
                    }*/


                    //update server
                    Webservices.updateUserFollowingState(uLike.getUserId(), b).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if (task.getError() == null) {
                                if (task.getResult() != null) {
                                    boolean isOK = (boolean) task.getResult();
                                    if (isOK) {
//                                            MyUtils.showToast(context, R.string.update_success);

                                        //refresh giao dien
//                                    notifyItemChanged(position);

                                    }
                                }
                            } else {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }

                            return null;
                        }
                    });


                }
            });
            /*setStateButton(uLike, holder.btn);
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
                        //set khong quan tam
                        //bo follow
                        Webservices.updateUserFollowingState(uLike.getUserId(), false).continueWith(new Continuation<Object, Void>() {
                            @Override
                            public Void then(Task<Object> task) throws Exception {
                                if (task.getError() == null) {
                                    if (task.getResult() != null) {
                                        boolean isOK = (boolean) task.getResult();
                                        if (isOK) {
//                                            MyUtils.showToast(context, R.string.update_success);
                                            //refresh giao dien
                                            notifyItemChanged(position);
                                        }
                                    }
                                } else {
                                    MyUtils.showToast(context, task.getError().getMessage());
                                }

                                return null;
                            }
                        });
                    } else {//set da quan tam
                        //set follow
                        Webservices.updateUserFollowingState(uLike.getUserId(), true).continueWith(new Continuation<Object, Void>() {
                            @Override
                            public Void then(Task<Object> task) throws Exception {
                                if (task.getError() == null) {
                                    if (task.getResult() != null) {
                                        boolean isOK = (boolean) task.getResult();
                                        if (isOK) {
//                                            MyUtils.showToast(context, R.string.update_success);
                                            //refresh giao dien
                                            notifyItemChanged(position);
                                        }
                                    }
                                } else {
                                    MyUtils.showToast(context, task.getError().getMessage());
                                }

                                return null;
                            }
                        });
                    }
                }
            });*/

        }

        private void setStateButton(UserShortDB uLike, ToggleButton btn) {
            //neu user like = owner thi an button
            if (uLike.getUserId() == user.getUserId()) {
                btn.setVisibility(View.INVISIBLE);
            } else {
                btn.setVisibility(View.VISIBLE);
                /*if (uLike != null) {
                    if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
                        btn.setText(getText(R.string.following_title));
                        btn.setBackgroundResource(R.drawable.bg_rectangle_2_fill);
                        btn.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btn.setText(getText(R.string.follow_title));
                        btn.setBackgroundResource(R.drawable.bg_rectangle_1_fill);
                        btn.setTextColor(getResources().getColor(R.color.light_blue_500));
                    }
                }*/
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


        private boolean isContain(long id) {
            boolean have = false;
            if (mDataset != null && mDataset.size() > 0) {
                for (int i = 0; i < mDataset.size(); i++) {
                    if (mDataset.get(i).getItemId() == id) {
                        have = true;
                        break;
                    }
                }
            }
            return have;
        }

        //add list items
        public void addAll(ArrayList<ImageLike> list) {
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (!isContain(list.get(i).getItemId())) {
                        mDataset.add(list.get(i));
                    }
                }
            }
//            mDataset.addAll(list);
            notifyDataSetChanged();
        }

        public ImageLike getLastestItem() {
            ImageLike item = null;
            if (getItemCount() > 0) {
                item = mDataset.get(getItemCount() - 1);
            }
            return item;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {"data":{"ImageComment":{"Comments":
     * [{"ItemId":1062,"User":{"UserName":"jessicahua","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/87b2fef5-3b6a-4a05-ae09-90bcc8396ad4\/bbbf19b1-8ccb-4145-983a-cb6bee1ddf96.JPG"},"Comment":"Dep qua ah @ChupHinhDep","CreateDate":"2016-06-25 00:40:40"}
     * ,{"ItemId":1063,"User":{"UserName":"nhattien667","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/10973213-85c8-4195-9fd4-ab4f931d3331\/54074402-b36e-4635-b90b-414c167db5bc.JPG"},"Comment":"Bạn dùng app gi vậy","CreateDate":"2016-06-24 19:09:09"},
     * {"ItemId":1064,"User":{"UserName":"hieukeodeo2882","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/7627e20c-604d-4145-805c-944eae61a3df\/c955e242-0fc9-4ead-b867-64390541e841.JPG"},"Comment":"Hay nhỉ��","CreateDate":"2016-06-24 18:43:18"}]
     * }}}
     *
     * @param imageItemId
     * @param lastItemId
     */
    private void getLikes(final long imageItemId, final long lastItemId, final long lastCreateDate) {

        String query = Webservices.GET_LIKES(imageItemId, lastItemId, lastCreateDate);
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", query)
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject objP = response.getJSONObject("data");

                            JSONObject obj = null;
                            if (objP.has("ImageComment")) {
                                obj = objP.getJSONObject("ImageComment");
                            } else {
                                obj = objP.getJSONObject("ImageItem");
                            }

                            JSONArray arr = obj.getJSONArray("Likes");

                            Type type = new TypeToken<List<ImageLike>>() {
                            }.getType();
                            ArrayList<ImageLike> comments = new Gson().fromJson(arr.toString(), type);
//                            Collections.reverse(comments);

                            if (comments.size() > 0) {
//                                MyUtils.showToastDebug(getApplicationContext(), ""+comments.size());
                                if (lastItemId == 0) {//page 1
                                    mAdapter.clear();
                                    mAdapter.addAll(comments);
                                } else {//load more
                                    mAdapter.addAll(comments);
                                }


                            }

                            //if first load
                            if (lastItemId == 0) {
                                if (comments.size() > 0) {
                                    list_empty.setVisibility(View.GONE);
                                } else {
                                    list_empty.setVisibility(View.VISIBLE);
                                }
                            }

                            comments.clear();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        setRefresh(false);

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {

                        }
                    }
                })
        ;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ImageLike.ARRAY_LIST, mAdapter.mDataset);
        outState.putParcelable(ImageItem.IMAGE_ITEM, item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            init();
        } else {
            //init cookie
            MyUtils.initCookie(context);

            item = savedInstanceState.getParcelable(ImageItem.IMAGE_ITEM);
            ArrayList<ImageLike> list = savedInstanceState.getParcelableArrayList(ImageLike.ARRAY_LIST);
            if (list != null && list.size() > 0) {
                mAdapter.mDataset.clear();
                mAdapter.addAll(list);
                list_empty.setVisibility(View.GONE);
            } else {
                list_empty.setVisibility(View.VISIBLE);
            }

        }
    }


    //#region SETUP TOOLBAR////////////////////////////////////////////////////////////////////////
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;
    @BindView(R.id.title)TextView title;
    public @BindView(R.id.imgShop)ImageView imgShop;

    private void setTitleBar() {
        title.setText(getText(R.string.like_title_long));
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
