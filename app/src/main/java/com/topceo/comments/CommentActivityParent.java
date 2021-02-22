package com.topceo.comments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.error.ANError;
import com.topceo.R;
import com.topceo.activity.MH02_PhotoDetailActivity;
import com.topceo.config.MyApplication;
import com.topceo.fragments.Fragment_1_Home_Admin;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.Fragment_2_Explorer;
import com.topceo.hashtag.HashTagActivity;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.services.Webservices;
import com.topceo.utils.EndlessRecyclerOnScrollListener;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

@Deprecated //Da chuyen qua MH02_PhotoDetailActivity
public class CommentActivityParent extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView1)
    RecyclerView rv;
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
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_36dp);
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
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        rv.setNestedScrollingEnabled(false);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                MyUtils.showToastDebug(context, "Load more...");
                ImageComment comment = mAdapter.getLastestItem();
                if (comment != null) {
                    getComments(item.getImageItemId(), comment.getItemId(), comment.getCreateDate());
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new CommentAdapterSectionParent_ImageComment(listComments, context, false);
        mAdapter.shouldShowHeadersForEmptySections(true);
        rv.setAdapter(mAdapter);
    }

    private CommentAdapterSectionParent_ImageComment mAdapter;


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
                                    mAdapter.addListItems(comments);
                                } else {//load more
                                    mAdapter.addListItems(comments);
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
            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.sending));
            progressDialog.show();*/
            ProgressUtils.show(context);

            long replyToId = 0;
            if(replyToComment!=null){
                replyToId = replyToComment.getItemId();
            }
            Webservices.sendComment(item.getImageItemId(), comment, replyToId).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {

                            initReplyLayout(null);

                            ImageComment img = (ImageComment) task.getResult();
                            if (img != null) {
                                list_empty.setVisibility(View.GONE);

                                //add vao trong list comment
                                mAdapter.add(img);//mAdapter.getItemCount()
                                //neu add vao cha thi scroll len dau
                                if(img.getReplyToId()==0){
                                    rv.scrollToPosition(0);//mAdapter.getItemCount()-1
                                }

                                txt.setText("");

                                //set lai so item, sau do goi nguoc ve cac man hinh
                                item.setCommentCount(mAdapter.getItemCount());

                                //update home list
                                Intent intent = new Intent(Fragment_1_Home_User.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                intent = new Intent(Fragment_1_Home_Admin.ACTION_UPDATE_ITEM);
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

                    ProgressUtils.hide();
                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////



    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST, mAdapter.getAllItem());
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
                mAdapter.clear();
                mAdapter.addListItems(list);
                list_empty.setVisibility(View.GONE);
            } else {
                list_empty.setVisibility(View.VISIBLE);
            }

        }
    }

    //EVENT BUS/////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyUtils.hideKeyboard(context, txt);
        EventBus.getDefault().unregister(this);
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventImageComment event) {
        if(event!=null && event.getComment()!=null){
            initReplyLayout(event.getComment());
        }
    }*/
    //REPLY/////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.linearReply)
    LinearLayout linearReply;
    @BindView(R.id.txtReply1)
    TextView txtReply1;
    @BindView(R.id.txtReply2)
    TextView txtReply2;
    @BindView(R.id.imgReplyClose)
    ImageView imgReplyClose;

    private ImageComment replyToComment;
    private void initReplyLayout(ImageComment comment) {
        replyToComment = comment;
        if (comment != null) {
            linearReply.setVisibility(View.VISIBLE);
            imgReplyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearReply.setVisibility(View.GONE);
                    replyToComment=null;
                }
            });

            //todo set hinh, ten, description
            if (comment.getUser() != null) {
                String name = comment.getUser().getUserName();
                txtReply1.setText(name);
            }

            txtReply2.setText(comment.getComment());


        } else {
            linearReply.setVisibility(View.GONE);
        }
    }
}
