package com.topceo.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.group.GroupDetailActivity;
import com.topceo.group.GroupNotify;
import com.topceo.group.members.ApproveMemberActivity;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.MyNotify;
import com.topceo.objects.other.User;
import com.topceo.onesignal.NotifyType;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_3_Notify extends Fragment {

    public Fragment_3_Notify() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private Context context;
    @BindView(R.id.rv)
    RecyclerView rv;
    private User user;
    private TinyDB db;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private void setRefresh(boolean isRefresh) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (isRefresh) {//on
                if (swipeContainer != null && !swipeContainer.isRefreshing())
                    swipeContainer.setRefreshing(isRefresh);
            } else {//off
                if (swipeContainer != null && swipeContainer.isRefreshing())
                    swipeContainer.setRefreshing(isRefresh);
            }
        }

    }


    @BindView(R.id.txtEmpty)
    TextView txtEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyUtils.log("Fragment_3_Notify: onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_3, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();

        db = new TinyDB(getActivity());
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }

        registerReceiver();

        initAdapter();

        if (savedInstanceState != null) {
            //tao lai cookie
            MyUtils.initCookie(context);

            ArrayList<MyNotify> list = savedInstanceState.getParcelableArrayList(MyNotify.NOTIFY_ARRAY_LIST);
            if (list != null && list.size() > 0) {
                mDataset.clear();
                mDataset.addAll(list);
                mAdapter.notifyDataSetChanged();
            }


        } else {
            getGroupPending();
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
                refresh();
            }
        });


        return view;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////////////////////
    private void initAdapter() {
        MyUtils.log("Fragment_3_Notify: initAdapter");
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(mLayoutManager);
//        tv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                int count = mDataset.size();
                if (count > 0) {
                    MyNotify notify = mDataset.get(count - 1);
                    getUserNotify(notify.getNotifyId());
                }

            }
        });

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter();
        rv.setAdapter(mAdapter);


    }


    private ArrayList<MyNotify> mDataset = new ArrayList<>();
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


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txt1;
            public ImageView img1, img2;
            public LinearLayout linear1, linear2;


            public ViewHolder(View v) {
                super(v);
                txt1 = (TextView) v.findViewById(R.id.textView1);
                img1 = (ImageView) v.findViewById(R.id.imageView1);
                img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
                img2 = (ImageView) v.findViewById(R.id.imageView2);
                img2.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
                linear1 = (LinearLayout) v.findViewById(R.id.linearLayout1);
                linear2 = (LinearLayout) v.findViewById(R.id.linearLayout2);

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
        private int buttonHeight = 0;

        public MyAdapter() {
            widthScreen = MyUtils.getScreenWidth(context);
            avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_3_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            if (position >= 0 && position < getItemCount()) {
                final MyNotify item = mDataset.get(position);
                if (item != null) {
                    if (item.getNotifyId() > 0) {
                        bindNotifyNormal(item, holder, position);
                    } else {
                        //danh sach moi tham gia group
                        if (item instanceof GroupNotify) {
                            GroupNotify notify = (GroupNotify) item;
                            bindNotifyGroup(notify, holder, position);
                        }
                    }
                }
            }

            /////////////////////////////////////////////////////////////////////////

        }

        private void bindNotifyGroup(GroupNotify item, ViewHolder holder, int position) {
            //avatar
            Glide.with(context)
                    .load(item.getAvatarSmall())
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);

            //hinh cover
            Glide.with(context)
                    .load(item.getCoverUrl())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.no_media_small)
                    .override(avatarSize, avatarSize)
                    .into(holder.img2);
            holder.img2.setVisibility(View.VISIBLE);
            holder.linear1.setVisibility(View.GONE);
            holder.linear2.setVisibility(View.GONE);

            String message = context.getString(R.string.username_invite_groupname, item.getUserName(), item.getGroupName());
            holder.txt1.setText(message);
            if (item.isViewed()) {
                holder.txt1.setTypeface(null);
                holder.txt1.setTextColor(ContextCompat.getColor(context, R.color.grey_500));
            } else {
                holder.txt1.setTypeface(holder.txt1.getTypeface(), Typeface.BOLD);
                holder.txt1.setTextColor(ContextCompat.getColor(context, R.color.black));
            }

            holder.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //vao group detail de accept/refuse
                    GroupDetailActivity.Companion.openActivity(context, item.getGroupId(), false);
                }
            });

            holder.txt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.img1.performClick();
                }
            });
            holder.img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.img1.performClick();
                }
            });
        }


        private void bindNotifyNormal(MyNotify item, ViewHolder holder, int position) {
            if (MyUtils.isImageUrl(item.getActionUserAvatar())) {
                Glide.with(context)
                        .load(item.getActionUserAvatar())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(avatarSize, avatarSize)
                        .transform(new GlideCircleTransform(context))
                        .into(holder.img1);
            }

            if (MyUtils.isImageUrl(item.getImageUrl())) {//hien thi hinh
                Glide.with(context)
                        .load(item.getImageUrl())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                        .placeholder(R.drawable.no_media_small)
                        .override(avatarSize, avatarSize)
                        .into(holder.img2);

                holder.img2.setVisibility(View.VISIBLE);
                holder.linear1.setVisibility(View.GONE);
                holder.linear2.setVisibility(View.GONE);
            } else {//follow/Unfollow

                if (item.getActionUserId() != user.getUserId()) {//Khong follow/Unfollow chinh minh
                    holder.img2.setVisibility(View.GONE);
                    holder.linear1.setVisibility(View.VISIBLE);
                    holder.linear2.setVisibility(View.VISIBLE);

                    if (item.getActionUserId() > 0) {
                        if (MyUtils.isFollowing(item.getActionUserId())) {//da quan tam
                            holder.linear1.setVisibility(View.GONE);
                            holder.linear2.setVisibility(View.VISIBLE);
                        } else {
                            holder.linear1.setVisibility(View.VISIBLE);
                            holder.linear2.setVisibility(View.GONE);
                        }
                    }
                    holder.linear1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (MyUtils.isFollowing(item.getActionUserId())) {//da quan tam
                                //set khong quan tam
                                //bo follow
                                Webservices.updateUserFollowingState(item.getActionUserId(), false).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getError() == null) {
                                            if (task.getResult() != null) {
                                                boolean isOK = (boolean) task.getResult();
                                                if (isOK) {
//                                                    MyUtils.showToast(context, R.string.update_success);
                                                    notifyDataSetChanged();
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
                                Webservices.updateUserFollowingState(item.getActionUserId(), true).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getError() == null) {
                                            if (task.getResult() != null) {
                                                boolean isOK = (boolean) task.getResult();
                                                if (isOK) {
//                                                    MyUtils.showToast(context, R.string.update_success);

                                                    notifyDataSetChanged();
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
                    holder.linear2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.linear1.performClick();
                        }
                    });
                } else {
                    holder.img2.setVisibility(View.GONE);
                    holder.linear1.setVisibility(View.GONE);
                    holder.linear2.setVisibility(View.GONE);
                }

            }


            holder.txt1.setText(item.getMessage());
            if (item.isViewed()) {
                holder.txt1.setTypeface(null);
                holder.txt1.setTextColor(ContextCompat.getColor(context, R.color.grey_500));
            } else {
                holder.txt1.setTypeface(holder.txt1.getTypeface(), Typeface.BOLD);
                holder.txt1.setTextColor(ContextCompat.getColor(context, R.color.black));
            }


            holder.img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.gotoProfile(item.getActionUserId(), context);
                }
            });

            holder.txt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //neu la tin cho duyet
                    if (item.getNotifyType().equals(NotifyType.TYPE_14_GROUP_MEMBER_REQUEST) &&
                            item.getGroupId() > 0) {
                        //tai thoi diem nhan thi dung, sau do thi co the sai do admin co the doi quyen user nay
                        //vao man hinh approve nhom
                        ApproveMemberActivity.Companion.openActivity(context, item.getGroupId());
                    } else {
                        //get ImageComment vao man hinh MH02_PhotoDetailActivity
                        Webservices.getImageItem(item.getImageItemId()).continueWith(new Continuation<Object, Void>() {
                            @Override
                            public Void then(Task<Object> task) throws Exception {
                                if (task.getError() == null) {
                                    if (task.getResult() != null) {
                                        ImageItem image = (ImageItem) task.getResult();
                                        if (image != null) {
                                            MyUtils.gotoDetailImage(context, image);
                                        }
                                    }
                                }
                                return null;
                            }
                        });
                    }

                    updateUserNotifyIsView(item.getNotifyId());
                }
            });
            holder.img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.txt1.performClick();
                }
            });
        }


        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if (mDataset != null) {
                return mDataset.size();
            } else {
                return 0;
            }
        }

        //add by mr.pham
        //clear all items
        public void clear() {
            if (getItemCount() > 0) {
                mDataset.clear();
                notifyDataSetChanged();
            }
        }

        //add list items
        public void addAll(ArrayList<MyNotify> list) {
            if (mDataset != null) {
                int size = getItemCount();
                mDataset.addAll(list);
                notifyItemRangeInserted(size, list.size());
            }

        }

        private int findPosition(long notifyId) {
            int position = -1;
            if (mDataset != null) {
                for (int i = 0; i < mDataset.size(); i++) {
                    MyNotify item = mDataset.get(i);
                    if (item.getNotifyId() == notifyId) {
                        position = i;
                        break;
                    }
                }
            }
            return position;
        }

        public void updateStateView(long notifyId, boolean isViewed) {
            int position = findPosition(notifyId);
            if (position >= 0 && position < getItemCount()) {
                mDataset.get(position).setViewed(isViewed);
                notifyItemChanged(position);
            }
        }

        public void updateAllViewed() {
            if (mDataset != null && getItemCount() > 0) {
                for (int i = 0; i < mDataset.size(); i++) {
                    mDataset.get(i).setViewed(true);
                }
                notifyDataSetChanged();
            }
        }


    }

    /////////////////////////////////////////////////////////////////////////
    public void getUserNotify(final long lastId) {
        MyUtils.log("Fragment_3_Notify: getUserNotify lastId=" + lastId);
        MyUtils.hideKeyboard(getActivity());
        /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getText(R.string.loading));
        progressDialog.show();*/
        Webservices.getUserNotify(MyNotify.IS_VIEWED_TAT_CA, lastId).continueWith(new Continuation<Object, Void>() {
            @Override
            public Void then(Task<Object> task) throws Exception {
                setRefresh(false);
                if (task.getError() == null) {
                    if (task.getResult() != null) {
                        ReturnResult result = (ReturnResult) task.getResult();
                        if (result.getErrorCode() == ReturnResult.SUCCESS) {
                            ArrayList<MyNotify> list = (ArrayList<MyNotify>) result.getData();
                            if (list != null && list.size() > 0) {
                                if (lastId == 0) {
//                                    mDataset.clear();//da clear roi o fun groupPending (moi user vao nhom)
                                    mDataset.addAll(list);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    mDataset.addAll(list);
                                    mAdapter.notifyDataSetChanged();
                                }

                                //load lai so thong bao
                                if (getContext() != null) {
                                    getContext().sendBroadcast(new Intent(MH01_MainActivity.ACTION_GET_NUMBER_NOTIFY));
                                }

                            } else {
//                            MyUtils.showToast(context, R.string.not_found);
                            }
                        } else {
                            getUserNotify(lastId);
                        }

                    } else {
                        recallService((ANError) task.getError(), lastId);
                    }
                } else {
                    recallService((ANError) task.getError(), lastId);
                }


                if (mAdapter != null) {
                    if (mAdapter.getItemCount() == 0) {
                        txtEmpty.setVisibility(View.VISIBLE);
                    } else {
                        txtEmpty.setVisibility(View.GONE);
                    }
                }
//                progressDialog.dismiss();

                return null;
            }
        });
    }

    private void recallService(ANError ANError, final long lastId) {
        boolean isLostCookie = MyApplication.controlException(ANError);
        MyUtils.log("Fragment_1_Home_User - getListImageItemExplorer() - Exception = " + (ANError).getErrorCode());

        if (isLostCookie) {
            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getResult() != null) {
                        User kq = (User) task.getResult();
                        if (kq != null) {
                            getUserNotify(lastId);
                        }
                    }
//                                        if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                    return null;
                }
            });
        } else {
            if (!TextUtils.isEmpty(ANError.getMessage())) {
                MyUtils.showToast(context, ANError.getMessage());
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    public static final String ACTION_VIEW_ALL = "ACTION_VIEW_ALL" + Fragment_3_Notify.class.getSimpleName();
    public static final String ACTION_SCROLL_TO_TOP = "ACTION_SCROLL_TO_TOP" + Fragment_3_Notify.class.getSimpleName();
    public static final String ACTION_REFRESH_NOTIFY = "ACTION_REFRESH_NOTIFY" + Fragment_3_Notify.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_VIEW_ALL)) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                    dialog.setTitle(R.string.notification);
                    dialog.setMessage(R.string.mark_all_viewed);
                    dialog.setNegativeButton(R.string.no, null);
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            updateAllUserNotifyIsView();
                        }
                    });

                    android.app.AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

                }
                if (intent.getAction().endsWith(ACTION_SCROLL_TO_TOP)) {
                    rv.post(new Runnable() {
                        @Override
                        public void run() {
                            rv.smoothScrollToPosition(0);
                        }
                    });
                }
                if (intent.getAction().endsWith(ACTION_REFRESH_NOTIFY)) {
                    refresh();
                }
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_VIEW_ALL));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_SCROLL_TO_TOP));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_REFRESH_NOTIFY));


    }

    private void refresh() {
        if (MyUtils.checkInternetConnection(context)) {
            //lay page 0
            setRefresh(true);
            getGroupPending();
        } else {
//            MyUtils.showThongBao(getActivity());
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null && getActivity() != null) getActivity().unregisterReceiver(receiver);
        if (realm != null) realm.close();
    }
    /////////////////////////////////////////////////////////////////////////

    private Realm realm;


    /////////////////////////////////////////////////////////////////////////


    @Override
    public void onStop() {
        super.onStop();
//        MyUtils.log("Fragment_3_Notify: onStop");
        setRefresh(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        MyUtils.log("Fragment_3_Notify: onSaveInstanceState");
        outState.putParcelableArrayList(MyNotify.NOTIFY_ARRAY_LIST, mDataset);
    }


    /////////////////////////////////////////////////////////////////////////
    private void updateUserNotifyIsView(final long notifyId) {
        if (notifyId > 0) {
            MyApplication.apiManager.updateUserNotifyIsView(
                    notifyId,
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                ReturnResult result = Webservices.parseJson(data.toString(), String.class, false);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                        if (mAdapter != null) {
                                            mAdapter.updateStateView(notifyId, true);
                                            //lay lai thong tin so luong notify
                                            if (getContext() != null) {
                                                getContext().sendBroadcast(new Intent(MH01_MainActivity.ACTION_GET_NUMBER_NOTIFY));
                                            }
                                        }
                                    } else {
                                        String message = result.getErrorMessage();
                                        if (!TextUtils.isEmpty(message)) {
                                            MyUtils.showAlertDialog(context, message);
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


    private void updateAllUserNotifyIsView() {
        MyApplication.apiManager.updateAllUserNotifyIsView(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            ReturnResult result = Webservices.parseJson(data.toString(), String.class, false);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                                    if (mAdapter != null) {
                                        mAdapter.updateAllViewed();
                                        //lay lai thong tin so luong notify
                                        if (getContext() != null) {
                                            getContext().sendBroadcast(new Intent(MH01_MainActivity.ACTION_GET_NUMBER_NOTIFY));
                                        }
                                    }
                                } else {

                                    String message = result.getErrorMessage();
                                    if (!TextUtils.isEmpty(message)) {
                                        MyUtils.showAlertDialog(context, message);
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

    /**
     * Goi ham nay dau tien roi moi goi tiep
     */
    private void getGroupPending() {
        MyApplication.apiManager.groupPending(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Type collectionType = new TypeToken<ArrayList<GroupNotify>>() {
                }.getType();
                if (response != null && response.body() != null) {
                    ReturnResult result = Webservices.parseJson(response.body().toString(), collectionType, true);
                    if (result != null) {
                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//co sas thi upload len server
                            ArrayList<GroupNotify> list = (ArrayList<GroupNotify>) result.getData();

                            if (getActivity() != null && !getActivity().isFinishing()) {
                                mDataset.clear();
                                if (list != null && list.size() > 0) {
                                    rv.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDataset.addAll(list);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                                getUserNotify(0);
                            }
                        }
                    }
                }
                setRefresh(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                setRefresh(false);
            }
        });

    }


}
