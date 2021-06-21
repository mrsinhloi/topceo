package com.topceo.mediaplayer.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.eventbus.EventMediaComment;
import com.topceo.objects.other.User;
import com.topceo.shopping.Media;
import com.topceo.shopping.MediaComment;
import com.topceo.shopping.MediaItem;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {
    public static final int ITEM_COUNT = 20;
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

    private ShoppingBinding binding;

    private void initBinding() {
        binding = new ShoppingBinding(context, media, list, videoSelected, txtInput, rippleSend, txtItems, linearInputComment, linearReply, rv1, rv2, txt1, txt2, txt3, txtLikeCount, txtCommentCount);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            list = b.getParcelableArrayList(MediaItem.LIST);
            media = b.getParcelable(Media.MEDIA);

            if (list != null && list.size() > 0) {
                videoSelected = list.get(0);
                initBinding();
                setUIVideo();
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
        initUI();
        registerReceiver();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;
    @BindView(R.id.recyclerView1)
    RecyclerView rv1;
    @BindView(R.id.recyclerView2)
    RecyclerView rv2;

    private void initUI() {
        binding.initUI(btnLike);
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
        binding.setUIVideo(videoSelected);
        //todo play item selected

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

    private void initReplyLayout(MediaComment comment) {
        binding.initReplyLayout(comment, imgReplyClose, txtReply1, txtReply2);
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



    ////

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

}
