package com.topceo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated
public class MH09_FollowersActivity_Backup extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
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

    private long userId = 0;
    private User owner;
    private TinyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        ButterKnife.bind(this);
        title.setText(getText(R.string.follower_title));

        db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            owner = (User) obj;
            userId = owner.getUserId();
        }


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        initAdapter();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            long id = b.getLong(User.USER_ID, 0);
            if (id > 0) {
                userId = id;
            }
        }

        //lay data
        getUserFollowers(userId, 0, 0);


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
                    getUserFollowers(userId, 0, 0);
                } else {
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
                User follower = mAdapter.getLastestItem();
                if (follower != null) {
                    getUserFollowers(userId, follower.getUserId(), follower.getCreateDate());
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(listComments);
        rv.setAdapter(mAdapter);
    }


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
        private ArrayList<User> mDataset = new ArrayList<>();

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txt1, txt2;
            public ImageView img1;
            public Button btn;
            public LinearLayout linear1;

            public ViewHolder(View v) {
                super(v);
                txt1 = (TextView) v.findViewById(R.id.textView1);
                txt2 = (TextView) v.findViewById(R.id.textView2);
                btn = (Button) v.findViewById(R.id.button1);

                img1 = (ImageView) v.findViewById(R.id.imageView1);
                linear1 = (LinearLayout)v.findViewById(R.id.linear1);

                img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));

                // set interpolators for both expanding and collapsing animations
//                txt5.setInterpolator(new OvershootInterpolator());


            }
        }


        public void add(int position, User item) {
            mDataset.add(position, item);
            notifyItemInserted(position);
            notifyDataSetChanged();
        }

        public void remove(User item) {
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

        public MyAdapter(ArrayList<User> myDataset) {
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
            final User item = mDataset.get(position);


            Glide.with(context)
                    .load(item.getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);


            holder.txt1.setText(item.getUserName());

            if (!TextUtils.isEmpty(item.getFullName())) {
                holder.txt2.setText(item.getFullName());
                holder.txt2.setVisibility(View.VISIBLE);
            } else {
                holder.txt2.setVisibility(View.GONE);
            }

            holder.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.gotoProfile(item.getUserName(), context);
                }
            });
            holder.linear1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.img1.performClick();
                }
            });


            //thong tin user da like
            final User uLike = item;
            setStateButton(uLike, holder.btn);
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
            });

        }

        /**
         * So voi owner la co dang theo doi nguoi nay hay khong
         *
         * @param uLike
         * @param btn
         */
        private void setStateButton(User uLike, Button btn) {
            //neu user like = owner thi an button
            if (uLike.getUserId() == owner.getUserId()) {
                btn.setVisibility(View.INVISIBLE);
            } else {
                btn.setVisibility(View.VISIBLE);
                if (uLike != null) {
                    if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
                        btn.setText(getText(R.string.following_title));
                        btn.setBackgroundResource(R.drawable.bg_rectangle_2_fill);
                        btn.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        btn.setText(getText(R.string.follow_title));
                        btn.setBackgroundResource(R.drawable.bg_rectangle_1_fill);
                        btn.setTextColor(getResources().getColor(R.color.light_blue_500));
                    }
                }
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
        public void addAll(ArrayList<User> list) {
            mDataset.addAll(list);
            notifyDataSetChanged();
        }

        public User getLastestItem() {
            User item = null;
            if (getItemCount() > 0) {
                item = mDataset.get(getItemCount() - 1);
            }
            return item;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param userId
     * @param lastItemId
     */
    private void getUserFollowers(final long userId, final long lastItemId, final long lastCreateDate) {
        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getListUserFollower(userId, lastItemId, lastCreateDate, Webservices.FOLLOW_COUNT_ITEM).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<User> users = (ArrayList<User>) task.getResult();
                            MyUtils.log(users.size() + "");
                            if (users != null && users.size() > 0) {

                                if (lastItemId == 0) {//page 1
                                    mAdapter.clear();
                                    mAdapter.addAll(users);
                                } else {//load more
                                    mAdapter.addAll(users);
                                }

                            }

                            //if first load
                            if (lastItemId == 0) {
                                if (mAdapter.getItemCount() > 0) {
                                    list_empty.setVisibility(View.GONE);
                                } else {
                                    list_empty.setVisibility(View.VISIBLE);
                                }
                            }

                            users.clear();

                        } else {
//                            MyUtils.showToast(context, R.string.not_found);
                        }
                    } else {
//                    MyUtils.showToast(context, task.getError().getMessage());
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());

                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            getUserFollowers(userId, lastItemId, lastCreateDate);
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
                    return null;
                }
            });
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////

}
