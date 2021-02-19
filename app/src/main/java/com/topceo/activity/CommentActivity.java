/*
package com.ehubstar.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.bumptech.glide.Glide;
import com.ehubstar.R;
import com.ehubstar.fragments.EndlessRecyclerViewScrollListener;
import com.ehubstar.fragments.Fragment_1_Home_User;
import com.ehubstar.fragments.Fragment_2_Explorer;
import com.ehubstar.fragments.GlideCircleTransform;
import com.ehubstar.hashtag.HashTagActivity;
import com.ehubstar.objects.image.ImageComment;
import com.ehubstar.objects.image.ImageItem;
import com.ehubstar.objects.other.User;
import com.ehubstar.profile.Fragment_5_User_Profile_Grid;
import com.ehubstar.services.Webservices;
import com.ehubstar.utils.DateFormat;
import com.ehubstar.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated
public class CommentActivity extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView1)
    RecyclerView rv2;
    @BindView(R.id.editText1)
    EditText txt;
    //    @BindView(R.id.imageView1)ImageView imgSend;
    @BindView(R.id.ripple1)
    LinearLayout rippleSend;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.hideKeyboard(context, txt);
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            item = b.getParcelable(ImageItem.IMAGE_ITEM);
        }

        init();


    }

    private void init() {
        initAdapter();

        if (item != null) {
            getComments(item.getImageItemId(), 0, 0);
        }

        rippleSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    send();
                    if (TextUtils.isEmpty(txt.getText())) {
                        MyUtils.hideKeyboard(context, txt);
                    }
                    return true;
                }
                return false;
            }
        });


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
                    if (item != null) {
                        //lay page 0
                        setRefresh(true);
                        getComments(item.getImageItemId(), 0, 0);
                    } else {
                        setRefresh(false);
                    }
                } else {
                    setRefresh(false);
                    MyUtils.showThongBao(context);
                }
            }
        });
    }

    private void send() {
        String text = txt.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sendComment(text);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<ImageComment> listComments = new ArrayList<>();

    private void initAdapter() {
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(context);
        rv2.setLayoutManager(mLayoutManager);

//        tv.setItemAnimator(new DefaultItemAnimator());
        rv2.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                customLoadMoreDataFromApi(page);
                ImageComment comment = mAdapter.getLastestItem();
                if (comment != null) {
                    getComments(item.getImageItemId(), comment.getItemId(), comment.getCreateDate());
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(listComments);
        rv2.setAdapter(mAdapter);
    }


    private MyAdapter mAdapter;

    */
/**
     * https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
     * https://github.com/wasabeef/recyclerview-animators
     * <p/>
     * notifyItemChanged(int pos)	Notify that item at position has changed.
     * notifyItemInserted(int pos)	Notify that item reflected at position has been newly inserted.
     * notifyItemRemoved(int pos)	Notify that items previously located at position has been removed from the data set.
     * notifyDataSetChanged()	    Notify that the dataset has changed. Use only as last resort.
     * rvContacts.scrollToPosition(0);   // index 0 position
     *//*

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<ImageComment> mDataset = new ArrayList<>();

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView txt1, txt2;
            public ImageView img1;

            public ViewHolder(View v) {
                super(v);
                txt1 = (TextView) v.findViewById(R.id.textView1);
                txt2 = (TextView) v.findViewById(R.id.textView2);

                img1 = (ImageView) v.findViewById(R.id.imageView1);

                img1.setLayoutParams(new RelativeLayout.LayoutParams(avatarSize, avatarSize));

                // set interpolators for both expanding and collapsing animations
//                txt5.setInterpolator(new OvershootInterpolator());


            }
        }


        public void add(int position, ImageComment item) {
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

        public MyAdapter(ArrayList<ImageComment> myDataset) {
            mDataset = myDataset;
            widthScreen = MyUtils.getScreenWidth(context);
            avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_comment_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final ImageComment item = mDataset.get(position);


            Glide.with(context)
                    .load(mDataset.get(position).getUser().getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);


            String s = "<span><b>" + mDataset.get(position).getUser().getUserName() + "</b> " + mDataset.get(position).getComment() + "</span>";
            holder.txt1.setText(Html.fromHtml(s));
            holder.txt2.setText(MyUtils.formatDate(mDataset.get(position).getCreateDate(), DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM, context));

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
        public void addAll(ArrayList<ImageComment> list) {
            mDataset.addAll(list);
            notifyDataSetChanged();
        }

        public ImageComment getLastestItem() {
            ImageComment item = null;
            if (getItemCount() > 0) {
                item = mDataset.get(getItemCount() - 1);
            }
            return item;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////////////////

    */
/**
     * {"data":{"ImageComment":{"Comments":
     * [{"ItemId":1062,"User":{"UserName":"jessicahua","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/87b2fef5-3b6a-4a05-ae09-90bcc8396ad4\/bbbf19b1-8ccb-4145-983a-cb6bee1ddf96.JPG"},"Comment":"Dep qua ah @ChupHinhDep","CreateDate":"2016-06-25 00:40:40"}
     * ,{"ItemId":1063,"User":{"UserName":"nhattien667","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/10973213-85c8-4195-9fd4-ab4f931d3331\/54074402-b36e-4635-b90b-414c167db5bc.JPG"},"Comment":"Bạn dùng app gi vậy","CreateDate":"2016-06-24 19:09:09"},
     * {"ItemId":1064,"User":{"UserName":"hieukeodeo2882","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/7627e20c-604d-4145-805c-944eae61a3df\/c955e242-0fc9-4ead-b867-64390541e841.JPG"},"Comment":"Hay nhỉ��","CreateDate":"2016-06-24 18:43:18"}]
     * }}}
     *
     * @param imageItemId
     * @param lastItemId
     *//*

    private void getComments(final long imageItemId, final long lastItemId, final long lastCreateDate) {

        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getComments(imageItemId, 0, lastItemId, lastCreateDate).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<ImageComment> comments = (ArrayList<ImageComment>) task.getResult();
                            if (comments.size() > 0) {

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
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            getComments(imageItemId, lastItemId, lastCreateDate);
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
        } else {
            MyUtils.showThongBao(context);
        }
    }

    private void sendComment(final String comment) {
        if (MyUtils.checkInternetConnection(context)) {
            final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.sending));
            progressDialog.show();
            Webservices.sendComment(item.getImageItemId(), comment, 0).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ImageComment img = (ImageComment) task.getResult();
                            if (img != null) {
                                list_empty.setVisibility(View.GONE);

                                //add vao trong list comment
                                mAdapter.add(0, img);//mAdapter.getItemCount()
                                mAdapter.notifyItemInserted(0);
                                rv2.scrollToPosition(0);//mAdapter.getItemCount()-1

                                txt.setText("");

                                //set lai so item, sau do goi nguoc ve cac man hinh
                                item.setCommentCount(mAdapter.getItemCount());

                                //update home list
                                Intent intent = new Intent(Fragment_1_Home_User.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);


                                //update man hinh chi tiet MH02_PhotoDetailActivity
                                intent = new Intent(MH02_PhotoDetailActivity.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                //update grid user
                                intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                //update grid image by hashtag
                                intent = new Intent(HashTagActivity.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                //update grid explorer
                                intent = new Intent(Fragment_2_Explorer.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);
                            }
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());

                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            sendComment(comment);
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

                    progressDialog.dismiss();
                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStop() {
        super.onStop();
        MyUtils.hideKeyboard(context, txt);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST, mAdapter.mDataset);
        outState.putParcelable(ImageItem.IMAGE_ITEM, item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            getComments(item.getImageItemId(), 0, 0);

        } else {

            MyUtils.initCookie(context);

            item = savedInstanceState.getParcelable(ImageItem.IMAGE_ITEM);
            ArrayList<ImageComment> list = savedInstanceState.getParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST);
            if (list != null && list.size() > 0) {
                mAdapter.mDataset.clear();
                mAdapter.addAll(list);
                list_empty.setVisibility(View.GONE);
            } else {
                list_empty.setVisibility(View.VISIBLE);
            }

        }
    }
}
*/
