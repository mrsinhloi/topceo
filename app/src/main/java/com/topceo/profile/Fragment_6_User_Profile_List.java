package com.topceo.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.EndlessRecyclerViewScrollListener;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.login.MH15_SigninActivity;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.DateFormat;
import com.topceo.utils.MyUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copy from Fragment_1_Home_User
 */
@Deprecated
public class Fragment_6_User_Profile_List extends Fragment {

    public Fragment_6_User_Profile_List() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyUtils.log("Fragment_1_Home_User - onCreate");
    }


    private Context context;
    @BindView(R.id.rv)
    RecyclerView rv;
//    @BindView(R.id.swipeContainer)
//    SwipeRefreshLayout swipeContainer;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyUtils.log("Fragment_1_Home_User - onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_6, container, false);
        ButterKnife.bind(this, view);
        registerReceiver();


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
                user = u;
            }
        }

        ////////////
        context = getActivity();
        initAdapter();


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
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
//                fetchTimelineAsync(0);
                //read newsfeed
                getNewFeed();
            }
        });*/

        //read newsfeed
        getListImage(0, 0);


        return view;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private void initAdapter() {

       /* ArrayList<String> mDataset = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            mDataset.add("Item value " + i);
        }*/
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        rv.setHasFixedSize(true);

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
                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                    ImageItem item = mAdapter.getLastestItem();
                    if (item != null) {
                        getListImage(item.getImageItemId(), item.getCreateDate());
                    }
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter();
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
        private ArrayList<ImageItem> mDataset = new ArrayList<>();


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txt1, txt2, txt3, txt4, txt6, txt7;
            public ReadMoreTextView txt5;
            public ImageView img1, img2, img3, img4, imgMenu;
            public LinearLayout linear1, linear3;
            public View separator1, separator2;
//            public SpinKitView loading;//SendingProgressView

            public LinearLayout linearLike, linearComment, linearShare;


            public ViewHolder(View v) {
                super(v);
                txt1 = (TextView) v.findViewById(R.id.textView1);
                txt2 = (TextView) v.findViewById(R.id.textView2);
                txt3 = (TextView) v.findViewById(R.id.textView3);
                txt4 = (TextView) v.findViewById(R.id.textView4);
                txt5 = (ReadMoreTextView) v.findViewById(R.id.textView5);
                txt6 = (TextView) v.findViewById(R.id.textView6);
                txt7 = (TextView) v.findViewById(R.id.textView7);

                img1 = (ImageView) v.findViewById(R.id.imageView1);
                img2 = (ImageView) v.findViewById(R.id.imageView2);
                img3 = (ImageView) v.findViewById(R.id.imageView3);
                img4 = (ImageView) v.findViewById(R.id.imageView4);
                imgMenu = (ImageView) v.findViewById(R.id.imgMenu);

                img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
                img2.setLayoutParams(new FrameLayout.LayoutParams(widthScreen, widthScreen));

                linear1 = (LinearLayout) v.findViewById(R.id.linearLayout1);
                linear3 = (LinearLayout) v.findViewById(R.id.linearLayout3);

                separator1 = (View) v.findViewById(R.id.separator1);
                separator2 = (View) v.findViewById(R.id.separator2);

                linearLike = (LinearLayout) v.findViewById(R.id.linearLike);
                linearComment = (LinearLayout) v.findViewById(R.id.linearComment);
                linearShare = (LinearLayout) v.findViewById(R.id.linearShare);

//                loading=(SpinKitView) v.findViewById(R.id.spin_kit);

                // set interpolators for both expanding and collapsing animations
//                txt5.setInterpolator(new OvershootInterpolator());


            }
        }


        public void add(int position, ImageItem item) {
            mDataset.add(position, item);
            notifyItemInserted(position);
        }

        public void remove(ImageItem item) {
            int position = -1;//= mDataset.indexOf(item);
            for (int i = 0; i < mDataset.size(); i++) {
                if (item.getImageItemId() == mDataset.get(i).getImageItemId()) {
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

        public MyAdapter() {
            widthScreen = MyUtils.getScreenWidth(context);
            avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_1_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final ImageItem item = mDataset.get(position);


            Glide.with(context)
                    .load(item.getOwner().getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);


            String img = item.getImageMedium();
            if (!TextUtils.isEmpty(img)) {
//                        holder1.loading.setVisibility(View.GONE);

                Glide.with(context)
                        .load(img)//images[position%images.length]
                        .thumbnail(
                                Glide.with(context)
                                        .load(item.getImageSmall())
                        )
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .override(widthScreen, widthScreen)
                        .into(holder.img2);
            } else {
                Glide.with(context)
                        .load(R.drawable.no_media)//images[position%images.length]
                        .override(widthScreen, widthScreen)
                        .into(holder.img2)
                ;
            }

            holder.txt1.setText(item.getOwner().getFullName());
            holder.txt2.setText(item.getLocation());
            holder.txt6.setText(MyUtils.formatDate(item.getCreateDate(), DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM, context));


            //neu like,comment,share deu = 0 thi an
            final int like = item.getLikeCount();
            int comment = item.getCommentCount();
            int share = item.getShareCount();

            if (like == 0 && comment == 0 && share == 0) {
                holder.linear1.setVisibility(View.GONE);
                holder.separator1.setVisibility(View.GONE);
            } else {
                holder.linear1.setVisibility(View.VISIBLE);
                holder.separator1.setVisibility(View.VISIBLE);

                //cai nao ko co thi an
                //like
                if (like > 0) {
                    holder.txt3.setText(like + "");
                    holder.txt3.setVisibility(View.VISIBLE);
                    holder.img3.setVisibility(View.VISIBLE);

                } else {
                    holder.txt3.setVisibility(View.GONE);
                    holder.img3.setVisibility(View.GONE);
                }

                //comment
                if (comment > 0) {
                    String s = String.format(getString(R.string.number_comment), comment);
                    holder.txt4.setText(s);
                    holder.txt4.setVisibility(View.VISIBLE);
                } else {
                    holder.txt4.setVisibility(View.GONE);
                }

                //share
                if (share > 0) {
                    String s = String.format(getString(R.string.number_share), share);
                    holder.txt7.setText(s);
                    holder.txt7.setVisibility(View.VISIBLE);
                } else {
                    holder.txt7.setVisibility(View.GONE);
                }


            }

            //neu description
            if (TextUtils.isEmpty(item.getDescription())) {
                holder.linear3.setVisibility(View.GONE);
                holder.separator2.setVisibility(View.GONE);
            } else {
                holder.linear3.setVisibility(View.VISIBLE);
                holder.separator2.setVisibility(View.VISIBLE);

                holder.txt5.setText(MyUtils.fromHtml(item.getDescription()));
            }


            //control image button Thich
            if (item.isLiked()) {
                holder.img4.setImageResource(R.drawable.ic_like_fill);
            } else {
                holder.img4.setImageResource(R.drawable.ic_like_outline);
            }

            holder.imgMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.imgMenu, item, position);
                }
            });

            ////
            holder.linearLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //update local
                    boolean liked = !item.isLiked();
                    item.setLiked(liked);
                    mDataset.get(position).setLiked(liked);

                    //like
                    int likeNumber = like;
                    if (liked) {
                        likeNumber += 1;
                    } else {
                        likeNumber -= 1;
                    }

                    if (likeNumber > 0) {
                        holder.txt3.setText(likeNumber + "");
                        holder.txt3.setVisibility(View.VISIBLE);
                        holder.img3.setVisibility(View.VISIBLE);

                    } else {
                        holder.txt3.setVisibility(View.GONE);
                        holder.img3.setVisibility(View.GONE);
                    }

                    //update server
                    updateLikeState(position, item.getImageItemId(), item.isLiked());
                }
            });
            ////
            holder.linearComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.gotoDetailImage(context, item);
                }
            });
            ////
            holder.linearShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent intent = new Intent(context, MH06_FeedShareActivity.class);
                    intent.putExtra(ImageItem.IMAGE_ITEM, item);
                    startActivity(intent);*/
                    MyUtils.share(context, item);
                }
            });

        }

        /**
         * Showing popup menu when tapping on 3 dots
         */
        private void showPopupMenu(View view, final ImageItem item, final int position) {
            // inflate menu
            final PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            if (item.isOwner()) {
                inflater.inflate(R.menu.menu_item_newsfeed_owner, popup.getMenu());
            } else {
                inflater.inflate(R.menu.menu_item_newsfeed, popup.getMenu());
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_report:
                            MyUtils.showDialogReportPost(context, item.getImageItemId());
                            return true;
                        case R.id.action_edit:
                            //Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                            MyUtils.editPost(item, context);

                            return true;
                        case R.id.action_delete:
//                            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                            //hoi truoc khi xoa
                            new MaterialDialog.Builder(context)
//                                    .title(R.string.delete_image)
                                    .content(R.string.do_you_delete_image)
                                    .positiveText(R.string.delete)
                                    .negativeText(R.string.no)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            deletePost(item.getImageItemId(), position);
                                            dialog.dismiss();
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();

                            return true;
                        default:
                    }
                    return false;
                }
            });
            popup.show();
        }


        private String[] images = {
                "http://www.peterkutz.com/computergraphics/images/path_tracer/PhotonMapping.png",
                "https://cdn3.vox-cdn.com/thumbor/k2L99FQWBoPvaM6HKcBn-uQDcfQ=/cdn0.vox-cdn.com/uploads/chorus_asset/file/3847870/11376655_479893395513491_201556343_n.0.jpg",
                "http://www.sternstunden.net/demoshots/allsky/140812_rainbow_allsy_0640_1080x1080.jpg",
                "https://cdn1.vox-cdn.com/thumbor/cRQfCgfPhAfabE3WfdESO3LVOsA=/cdn0.vox-cdn.com/uploads/chorus_asset/file/3847972/11379249_1467686426858468_402561493_n.0.jpg",
                "https://scontent.cdninstagram.com/hphotos-xfa1/t51.2885-15/11370971_837216559694773_771634899_n.jpg",

                "https://cdn3.vox-cdn.com/thumbor/9-G0AxBxrhZnO_uyGN6n96JPjMo=/cdn0.vox-cdn.com/uploads/chorus_asset/file/3847968/11374490_115876812084984_810949242_n.0.jpg",
                "https://pbs.twimg.com/media/CnxqOw1WEAArkqu.jpg",
                "http://www.greenwaylawnandlanscaping.com/images/work/residential-landscaping.jpg",
                "http://flashwallpapers.com/wp-content/uploads/2015/09/Nature-Sunrise-Bright-Lake-Field-Landscape-1080x1080.jpg",
                "http://images.designtrends.com/wp-content/uploads/2015/10/06102208/Layered-Contemporary-Garden-Landscape-Design.jpg",

                "http://www.galerieproject.com/wp-content/uploads/2015/10/surreal-landscape-photo-manipulations-jati-putra-pratama-48.jpg",
                "http://images.designtrends.com/wp-content/uploads/2015/10/06102208/Stones-Contemporary-Garden-Landscape-Design.jpg",
                "http://66.media.tumblr.com/8cb21a78248ca78c0ae6da1ddc6339ca/tumblr_o94n3mq6AJ1v8t18zo1_1280.jpg",
                "http://greenwaylawnandlanscaping.com/images/work/landscape-maintenance.jpg",
                "http://65.media.tumblr.com/00adebf4e9bc521ceab009901df65a6b/tumblr_o8pzwt326H1v8t18zo1_1280.jpg"
        };

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
        public void addAll(ArrayList<ImageItem> list) {
            mDataset.addAll(list);
            notifyDataSetChanged();
        }

        public ImageItem getLastestItem() {
            ImageItem item = null;
            if (getItemCount() > 0) {
                item = mDataset.get(getItemCount() - 1);
            }
            return item;
        }

        public void updateItemLikeCount(int likeCount, int position) {
            mDataset.get(position).setLikeCount(likeCount);
            notifyItemChanged(position);
        }

        public void updateItemDescription(ImageItem item) {
            int pos = -1;
            for (int i = 0; i < mDataset.size(); i++) {
                if (mDataset.get(i).getImageItemId() == item.getImageItemId()) {
                    pos = i;
                    break;
                }
            }

            if (pos >= 0) {
                mDataset.get(pos).setDescription(item.getDescription());
                notifyItemChanged(pos);
                notifyDataSetChanged();
            }


        }

        public void updateNumberComment(long imageItemId, int numberComment) {
            int pos = -1;
            for (int i = 0; i < mDataset.size(); i++) {
                if (mDataset.get(i).getImageItemId() == imageItemId) {
                    pos = i;
                    break;
                }
            }

            if (pos >= 0) {
                mDataset.get(pos).setCommentCount(numberComment);
                notifyItemChanged(pos);
                notifyDataSetChanged();
            }


        }

    }


    private void getListImage(final long lastImageItemId, final long lastDate) {

        Webservices.getListImageItemOfUser(user.getUserId(), lastImageItemId, lastDate)
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        if (task.getError() == null) {//ko co exception
                            ArrayList<ImageItem> list = (ArrayList<ImageItem>) task.getResult();
                            if (list.size() > 0) {
                                if (lastImageItemId == 0) {//dau tien
                                    mAdapter.clear();
                                    mAdapter.addAll(list);
                                } else {//load tiep
                                    mAdapter.addAll(list);
                                }
                            }
                            MyUtils.log("Fragment_1_Home_User - getNewFeed() - List size = " + list.size());
//                            if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                        } else {//co exception
                            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                            MyUtils.log("Fragment_1_Home_User - getNewFeed() - Exception = " + ((ANError) task.getError()).getErrorCode());

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
//                                        if (swipeContainer!=null && swipeContainer.isRefreshing()) swipeContainer.setRefreshing(false);
                                        return null;
                                    }
                                });
                            } else {
                                if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                    MyUtils.showToast(context, task.getError().getMessage());
                                }
                            }
                        }

                        return null;
                    }
                });


    }

    /////////////////////////////////////////////////////////
//    public static final String ACTION_FINISH ="ACTION_FINISH_"+Fragment_1_Home_User.class.getSimpleName();
    public static final String ACTION_REFRESH = "ACTION_REFRESH_" + Fragment_6_User_Profile_List.class.getSimpleName();
    public static final String ACTION_UPDATE_ITEM = "ACTION_UPDATE_ITEM";
    public static final String ACTION_UPDATE_NUMBER_COMMENT = "ACTION_UPDATE_NUMBER_COMMENT";

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        if (getActivity() == null) return;
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH)) {

                    /*swipeContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeContainer.setRefreshing(true);
                            getNewFeed();
                        }
                    });*/
                }
                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_ITEM)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        //item nay chi chua id, va description
                        ImageItem item = b.getParcelable(ImageItem.IMAGE_ITEM);
                        if (item != null) {
                            mAdapter.updateItemDescription(item);
                        }
                    }
                }

                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_NUMBER_COMMENT)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        int count = b.getInt(ImageItem.IMAGE_ITEM_NUMBER_COMMENT, 0);
                        long imageItemId = b.getLong(ImageItem.IMAGE_ITEM_ID, 0);
                        if (imageItemId > 0) {
                            mAdapter.updateNumberComment(imageItemId, count);
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_REFRESH));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_ITEM));
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_UPDATE_NUMBER_COMMENT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) getActivity().unregisterReceiver(receiver);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateLikeState(final int position, long imageItemId, boolean isLiked) {
        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", Webservices.UPDATE_IMAGE_ITEM_LIKED(imageItemId, isLiked))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("UpdateImageItemLiked")) {
                                JSONObject obj = objP.getJSONObject("UpdateImageItemLiked");

                                /*double imageItemId = obj.getDouble("ImageItemId");
                                int likeCount = obj.getInt("LikeCount");

                                if (imageItemId > 0) {
//                                MyUtils.showToast(context, R.string.update_success);
                                    //update lai item dang chon
                                    mAdapter.updateItemLikeCount(likeCount, position);
                                }*/
                            } else {

                                //update ui cac man hinh home, profile
                                MyUtils.afterDeletePost(context, imageItemId);
                                //null tuc la hinh da bi xoa, xoa trong list
                                new MaterialDialog.Builder(context)
                                        .content(R.string.post_is_deleted)
                                        .positiveText(R.string.ok)
                                        .cancelable(false)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


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
    private void reportPost(int reportTypeId, long imageItemId) {
        AndroidNetworking.post(Webservices.URL + "image/report")
                .addQueryParameter("ReportTypeId", String.valueOf(reportTypeId))
                .addQueryParameter("ImageItemId", String.valueOf(imageItemId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, String.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                MyUtils.showToast(context, R.string.report_success);
                            } else {
                                MyUtils.showToast(context, R.string.report_not_success);

                            }
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void deletePost(long imageItemId, final int position) {
        AndroidNetworking.post(Webservices.URL + "image/delete")
                .addQueryParameter("ImageItemId", String.valueOf(imageItemId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, String.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                MyUtils.showToast(context, R.string.delete_success);

                                //xoa va refresh lai danh sach
                                mAdapter.remove(position);
                            } else {
                                MyUtils.showToast(context, R.string.delete_not_success);

                            }
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                    }
                });
    }
    /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////


}
