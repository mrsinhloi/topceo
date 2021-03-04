package com.topceo.mediaplayer.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.comments.CommentAdapterSectionParent_MediaComment;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.eventbus.EventMediaComment;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.mediaplayer.video.customJzvd.JZMediaExo;
import com.topceo.mediaplayer.video.customJzvd.MyJzvdStd;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.Fragment_Shop;
import com.topceo.shopping.Media;
import com.topceo.shopping.MediaComment;
import com.topceo.shopping.MediaItem;
import com.topceo.socialview.commons.socialview.Hashtag;
import com.topceo.socialview.commons.socialview.Mention;
import com.topceo.socialview.commons.widget.HashtagArrayAdapter;
import com.topceo.socialview.commons.widget.MentionArrayAdapter;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.socialview.objects.SearchHashtag;
import com.topceo.socialview.objects.SearchMention;
import com.topceo.utils.EndlessRecyclerOnScrollListener;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.Jzvd;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    private Activity context = this;
    private ArrayList<MediaItem> list = new ArrayList<>();
    private Media media;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.btnLike)
    CheckBox btnLike;
    @BindView(R.id.imgComment)
    ImageView imgComment;

    @BindView(R.id.txtLikeCount)
    TextView txtLikeCount;
    @BindView(R.id.txtCommentCount)
    TextView txtCommentCount;

    private int screenWidth = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        screenWidth = MyUtils.getScreenWidth(context);
        initYoutubePlayer();


        Bundle b = getIntent().getExtras();
        if (b != null) {
            list = b.getParcelableArrayList(MediaItem.LIST);
            media = b.getParcelable(Media.MEDIA);

            if (list != null && list.size() > 0) {
                videoSelected = list.get(0);
                /*videoUri = videoSelected.getFileUrl();
                setUp();*/
                setUIVideo();
                initPlayer();
                setTextItem(list.size());

            }

            if (media != null) {
                btnLike.setChecked(media.isLiked());
            }
        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToView();
            }
        });

        btnLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (media != null) {
                    setLikeMedia(media.getMediaId(), b);
                }
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isHideKeyboard) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        MyUtils.hideKeyboard(context);
                        isHideKeyboard = true;
                        return true;
                    }
                }

                return false;
            }
        });

        //recyclerview
        initRecyclerView();
        registerReceiver();
        initInputComment();


        initAutoFullscreen();

    }

    private boolean isHideKeyboard = false;

    private void scrollToView() {
        runJustBeforeBeingDrawn(txtInput, new Runnable() {
            @Override
            public void run() {
                //...
                final int top = findTopRelativeToParent(scrollView, txtInput);
                scrollView.scrollTo(0, top);
                txtInput.requestFocus();
                MyUtils.showKeyboard(context);
                //...
            }
        });
    }

    private int findTopRelativeToParent(ViewGroup parent, View child) {

        int top = child.getTop();

        View childDirectParent = ((View) child.getParent());
        boolean isDirectChild = (childDirectParent.getId() == parent.getId());

        try {
            while (!isDirectChild) {
                top += childDirectParent.getTop();
                childDirectParent = ((View) childDirectParent.getParent());
                isDirectChild = (childDirectParent.getId() == parent.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return top;

    }

    public static void runJustBeforeBeingDrawn(final View view, final Runnable runnable) {
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                runnable.run();
                return true;
            }
        };
        view.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }






    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;
    @BindView(R.id.recyclerView1)
    RecyclerView rv1;
    @BindView(R.id.recyclerView2)
    RecyclerView rv2;
    Video_Adapter adapterVideo;
    CommentAdapterSectionParent_MediaComment adapterComment;

    private void initRecyclerView() {

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv1.setLayoutManager(llm);
        rv1.setNestedScrollingEnabled(false);
        rv1.setHasFixedSize(true);
        rv1.setItemAnimator(new DefaultItemAnimator());
        //rv1.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        llm2.setOrientation(RecyclerView.VERTICAL);
        rv2.setLayoutManager(llm2);
        rv2.setNestedScrollingEnabled(false);
        rv2.setHasFixedSize(false);
        rv2.setItemAnimator(new DefaultItemAnimator());

        ViewCompat.setNestedScrollingEnabled(rv1, false);
        ViewCompat.setNestedScrollingEnabled(rv2, false);


        //list video
        if (list != null && list.size() > 0) {

            /*for (int i = 0; i < 10; i++) {
                list.add(list.get(0));
            }*/

            //adapterVideo video
            adapterVideo = new Video_Adapter(this, list, 0);
            rv1.setAdapter(adapterVideo);


            //adapterVideo comment
            adapterComment = new CommentAdapterSectionParent_MediaComment(new ArrayList<MediaComment>(), context);
            adapterComment.shouldShowHeadersForEmptySections(true);
            rv2.setAdapter(adapterComment);
            getComments(videoSelected.getMediaId(), 0);
            rv2.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore() {
                    MyUtils.showToastDebug(context, "Load more...");
                    MediaComment comment = adapterComment.getLastestItem();
                    if (comment != null) {
                        getComments(videoSelected.getMediaId(), comment.getItemId());
                    }
                }
            });

        }

        //list comment

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private MediaItem videoSelected;
    @BindView(R.id.txt1)
    TextView txt1;
    @BindView(R.id.txt2)
    TextView txt2;
    @BindView(R.id.txt3)
    TextView txt3;

    private void setUIVideo() {
        if (videoSelected != null) {
            txt1.setText(videoSelected.getTitle());
            txt3.setText(MyUtils.getTimeAgo(videoSelected.getLastModifyDate() * 1000, this));
            if (!TextUtils.isEmpty(videoSelected.getAuthor())) {
                txt2.setText(videoSelected.getAuthor());
                txt2.setVisibility(View.VISIBLE);
            } else {
                txt2.setVisibility(View.GONE);
            }

        }

        //hien thi so luong like comment
        if (media != null) {
            if (media.getLikeCount() > 0) {
                txtLikeCount.setVisibility(View.VISIBLE);
                txtLikeCount.setText(String.valueOf(media.getLikeCount()));
            } else {
                txtLikeCount.setVisibility(View.GONE);
            }

            /*if (media.getCommentCount() > 0) {
                txtCommentCount.setVisibility(View.VISIBLE);
                txtCommentCount.setText(String.valueOf(media.getCommentCount()));
            } else {
                txtCommentCount.setVisibility(View.GONE);
            }*/
            txtCommentCount.setText(String.valueOf(media.getCommentCount()));
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_PLAY_VIDEO = "ACTION_PLAY_VIDEO";

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                switch (intent.getAction()) {
                    case ACTION_PLAY_VIDEO:
                        MediaItem mediaItem = b.getParcelable(MediaItem.MEDIA_ITEM);
                        if (mediaItem != null) {
                            videoSelected = mediaItem;
                            setUIVideo();
                            /*videoUri = videoSelected.getFileUrl();
                            setUp();*/
                            initPlayer();

                        }

                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.smoothScrollTo(0, 0);
                            }
                        });

                        break;

                    default:
                        break;
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_PLAY_VIDEO));

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isLoadMore = true;
    public static final int ITEM_COUNT = 20;

    private void getComments(long mediaId, long lastId) {

        MyApplication.apiManager.mediaCommentList(
                mediaId,
                (lastId == 0) ? null : String.valueOf(lastId),
                ITEM_COUNT,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<MediaComment>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<MediaComment> list = (ArrayList<MediaComment>) result.getData();
                                    if (list.size() > 0) {
                                        if (lastId == 0) {//page 1
                                            adapterComment.clear();
                                            adapterComment.addListItems(list);

                                        } else {//load more
                                            adapterComment.addListItems(list);
                                        }
                                    }

                                    //danh sach nho hon page thi dung load more
                                    if (list.size() < ITEM_COUNT) {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private User user;
    @BindView(R.id.imgOwner)
    ImageView imgOwner;
    @BindView(R.id.editText1)
    SocialAutoCompleteTextView txtInput;
    @BindView(R.id.ripple1)
    LinearLayout rippleSend;
    @BindView(R.id.txtItems)
    TextView txtItems;
    @BindView(R.id.linearInputComment)
    LinearLayout linearInputComment;

    private void setTextItem(int number) {
        if (number > 0) {
            txtItems.setText(getString(R.string.list_x_items, number));
            txtItems.setVisibility(View.VISIBLE);
        } else {
            txtItems.setVisibility(View.GONE);
        }
    }

    private int avatarSize = 0;

    private void initInputComment() {
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium_smaller);
        TinyDB db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
            if (user != null) {
                int width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);
                //set avatar
                Glide.with(context)
                        .load(user.getAvatarSmall())
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(width, width)
                        .transform(new GlideCircleTransform(context))
                        .into(imgOwner);
            }
        }

        rippleSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        txtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    send();
                    if (TextUtils.isEmpty(txtInput.getText())) {
                        MyUtils.hideKeyboard(context, txtInput);
                    }
                    return true;
                }
                return false;
            }
        });
        txtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isHideKeyboard = false;
                }
            }
        });

        //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    String string = txtInput.getText().toString();
                    if (!TextUtils.isEmpty(string)) {
                        String[] words = string.split(" ");
                        if (words.length > 0) {
                            String lastWord = words[words.length - 1];

                            //#h, @t
                            if (lastWord.length() >= 2) {
                                if (lastWord.charAt(0) == '#') {
                                    String keyword = lastWord.substring(1);
                                    searchHashtag(keyword);
                                } else if (lastWord.charAt(0) == '@') {
                                    String keyword = lastWord.substring(1);
                                    searchMetion(keyword);


                                }
                            }
                        }
                    }
                }
                return false;
            }
        });
        //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void send() {
        String text = txtInput.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            MyUtils.hideKeyboard(context);
            sendComment(text);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendComment(final String comment) {
        if (MyUtils.checkInternetConnection(context)) {
            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.sending));
            progressDialog.show();*/

            ProgressUtils.show(context);
            String replyToId = null;
            if (replyToComment != null) {
                replyToId = String.valueOf(replyToComment.getItemId());
            }

            MyApplication.apiManager.mediaCommentAdd(videoSelected.getMediaId(), replyToId, comment, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject data = response.body();
                    if (data != null) {
                        ReturnResult result = Webservices.parseJson(data.toString(), MediaComment.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                initReplyLayout(null);

                                MediaComment img = (MediaComment) result.getData();
                                if (img != null) {
//                                list_empty.setVisibility(View.GONE);

                                    //add vao trong list comment
                                    adapterComment.add(img);//mAdapter.getItemCount()
                                    //neu add vao cha thi scroll len dau
                                    /*if(img.getReplyToId()==0){
                                        rv.scrollToPosition(0);//mAdapter.getItemCount()-1
                                    }*/

                                    txtInput.setText("");

                                    //set lai so item, sau do goi nguoc ve cac man hinh
//                                    videoSelected.setCommentCount(adapterComment.getItemCount());


                                }
                            }
                        }
                    }
                    ProgressUtils.hide();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    ProgressUtils.hide();
                }
            });

        } else {
            MyUtils.showThongBao(context);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
//REPLY/////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.linearReply)
    LinearLayout linearReply;
    @BindView(R.id.txtReply1)
    TextView txtReply1;
    @BindView(R.id.txtReply2)
    TextView txtReply2;
    @BindView(R.id.imgReplyClose)
    ImageView imgReplyClose;

    private MediaComment replyToComment;

    private void initReplyLayout(MediaComment comment) {
        replyToComment = comment;
        if (comment != null) {
            linearReply.setVisibility(View.VISIBLE);
            imgReplyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearReply.setVisibility(View.GONE);
                    replyToComment = null;
                }
            });

            //set hinh, ten, description
            if (comment.getUserName() != null) {
                String name = comment.getUserName();
                txtReply1.setText(name);
            }

            txtReply2.setText(comment.getComment());
            txtInput.requestFocus();
            MyUtils.showKeyboard(context);


        } else {
            linearReply.setVisibility(View.GONE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //EVENT BUS/////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyUtils.hideKeyboard(context, txtInput);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMediaComment event) {
        if (event != null && event.getComment() != null) {
            initReplyLayout(event.getComment());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setLikeMedia(long mediaId, boolean isLiked) {
        if (isLiked) {
            MyApplication.apiManager.mediaLike(mediaId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null)
                        parseJson(obj.toString(), mediaId, isLiked);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        } else {
            MyApplication.apiManager.mediaUnlike(mediaId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null)
                        parseJson(obj.toString(), mediaId, isLiked);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }

    }


    private void parseJson(String json, long commentId, boolean isLiked) {
        if (json != null) {
            ReturnResult result = Webservices.parseJson(json, Boolean.class, false);

            if (result != null) {
                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                    MyUtils.showToastDebug(context, "Liked...");
                    if (media != null) {
                        media.setLiked(isLiked);
                        //refresh skyshop
                        int count = isLiked ? (media.getLikeCount() + 1) : (media.getLikeCount() - 1);
                        media.setLikeCount(count);

                        //set lai giao dien so luong like tang hoac giam
                        setUIVideo();

                        //cap nhat giao dien shop (liked + countLiked)
                        Intent intent = new Intent(Fragment_Shop.ACTION_UPDATE_MEDIA);
                        intent.putExtra(Media.MEDIA, media);
                        sendBroadcast(intent);
                    }

                } else {
                    MyUtils.showToastDebug(context, result.getErrorMessage());
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchMetion(String keyword) {
        MyApplication.apiManager.searchUserForMention(
                keyword,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<SearchMention>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<SearchMention> list = (ArrayList<SearchMention>) result.getData();
                                    if (list.size() > 0) {
                                        setAdapterMention(list);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchHashtag(String keyword) {
        MyApplication.apiManager.searchHashtag(
                keyword,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<SearchHashtag>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<SearchHashtag> list = (ArrayList<SearchHashtag>) result.getData();
                                    if (list.size() > 0) {
                                        setAdapterHashtag(list);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapterHashtag(ArrayList<SearchHashtag> list) {
        if (list != null && list.size() > 0) {
            ArrayList<Hashtag> data = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SearchHashtag item = list.get(i);
                data.add(new Hashtag(item.getHashtag(), item.getPostCount()));
            }
            ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<Hashtag>(context);
            hashtagAdapter.addAll(data);
            txtInput.setHashtagAdapter(hashtagAdapter);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapterMention(ArrayList<SearchMention> list) {
        if (list != null && list.size() > 0) {

            MentionArrayAdapter<Mention> mentionAdapter = new MentionArrayAdapter<Mention>(context);
            for (int i = 0; i < list.size(); i++) {
                SearchMention item = list.get(i);
                mentionAdapter.add(new Mention(item.getUserName(), item.getFullName(), item.getAvatarSmall()));
            }

            txtInput.setMentionAdapter(mentionAdapter);
        }
    }


    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////


    @BindView(R.id.videoPlayer)
    MyJzvdStd videoPlayer;

    private void initPlayer() {
        if (videoSelected != null) {
            String url = videoSelected.getFileUrl();
            if (MyUtils.isYoutubeUrl(url)) {
                imgBack.setVisibility(View.GONE);
                videoPlayer.setVisibility(View.GONE);
                youtubePlayerView.setVisibility(View.VISIBLE);
                playYoutube();
            } else {
                if (youTubePlayer != null) {
                    youTubePlayer.pause();
                }
                imgBack.setVisibility(View.VISIBLE);
                videoPlayer.setVisibility(View.VISIBLE);
                youtubePlayerView.setVisibility(View.GONE);
                videoPlayer.setUp(videoSelected.getFileUrl(), "", Jzvd.SCREEN_NORMAL, JZMediaExo.class);
                if (!TextUtils.isEmpty(videoSelected.getThumbnailUrl())) {
                    Glide.with(this)
                            .load(videoSelected.getThumbnailUrl())
                            .override(screenWidth, 0)
                            .centerCrop()
                            .into(videoPlayer.thumbImageView);
                }
                videoPlayer.startButton.performClick();

            }

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
        Jzvd.clearSavedProgress(this, null);
        //home back
        Jzvd.goOnPlayOnPause();


    }


    Jzvd.JZAutoFullscreenListener mSensorEventListener;
    SensorManager mSensorManager;

    private void initAutoFullscreen() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new Jzvd.JZAutoFullscreenListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //home back
        Jzvd.goOnPlayOnResume();
    }


    ///Youtube
//    public static final String YOUTUBE_VIDEO_ID = "Evfe8GEn33w";
//    public static final String YOUTUBE_PLAYLIST = "UCU3jy5C8MB-JvSw_86SFV2w";
    @BindView(R.id.youtubePlayer)
    YouTubePlayerView youtubePlayerView;
    com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer;

    private void initYoutubePlayer() {
//        youtubePlayerView.initialize(getString(R.string.GOOGLE_MAPS_ANDROID_API_KEY), this);
        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer player) {
                super.onReady(player);
//                String videoId = "S0Q4gqBUs7c";
//                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer = player;
                if (isNeedPlayFirst) {
                    playYoutube();
                }

            }
        });
        youtubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        youtubePlayerView.exitFullScreen();
                        VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
                        VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        youtubePlayerView.requestLayout();
                    }
                });


            }
        });
        //youtubePlayerView.getPlayerUiController().showMenuButton(false);

    }

    private boolean isPortrait = true;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false;
            linearInputComment.setVisibility(View.GONE);
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isPortrait = true;
            linearInputComment.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        if(youtubePlayerView.isFullScreen()){
            youtubePlayerView.exitFullScreen();
            return;
        }
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*pausePlayer();
        releasePlayer();*/
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        if(youtubePlayerView!=null){
            youtubePlayerView.release();
        }
    }

    private boolean isNeedPlayFirst = false;

    private void playYoutube() {
        if (videoSelected != null) {
            String url = videoSelected.getFileUrl();
            if (youTubePlayer != null) {
                String id = getYoutubeId(url);
                if (!TextUtils.isEmpty(id)) {
                    Jzvd.goOnPlayOnPause();
                    youTubePlayer.loadVideo(id, 0);
                }
            } else {
                isNeedPlayFirst = true;
            }
        }
    }

    public static String getYoutubeId(String url) {
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }/*from w  w  w.  j a  va  2 s .c om*/
        return null;
    }
}
