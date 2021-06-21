package com.topceo.mediaplayer.audio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.comments.CommentAdapterSectionParent_MediaComment;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.eventbus.EventMediaComment;
import com.topceo.fragments.GlideCircleTransform;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.workchat.core.plan.Comment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity context = this;
    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.indicator)
    WormDotsIndicator indicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.txtTitleComment)
    TextView txtTitleComment;

    private void setTitleComment(Media media) {
        if (media != null) {
            int like = media.getLikeCount();
            int comment = media.getCommentCount();

            String sLike = "";
            String sComment = getString(R.string.comment);

            if (like > 0) {
                sLike = getString(R.string.x_like, like);
            }
            if (comment > 0) {
                sComment = getString(R.string.x_comment, comment);
            }

            if (!TextUtils.isEmpty(sLike)) {
                if (comment > 0) {
                    txtTitleComment.setText(sLike.concat(getString(R.string.icon_dot)).concat(sComment));
                } else {
                    txtTitleComment.setText(sLike);
                }
            } else {
                //chi co comment
                txtTitleComment.setText(sComment);
            }
        }
    }


    private TinyDB db;
    private boolean isHideKeyboard = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        db = new TinyDB(this);


        //player
        setViews();
        initializeSeekBar();
        //comment
        initInputComment();
        initBottomSheetComment();

        //init
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MH01_MainActivity.isExist) {
                    startActivity(new Intent(context, MH01_MainActivity.class));
                }
                finish();
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });

        registerReceiver();

    }

    private void initUI() {

        //viewpager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //adapter
        if (mPlayerAdapter != null && mPlayerAdapter.getCurrentSong() != null) {
            setupViewPager();
        }
    }

    private class MyFragmentAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyFragmentAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /*public void clear(){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            for (Fragment fragment : mFragmentList) {
                transaction.remove(fragment);
            }

            mFragmentList.clear();
            transaction.commitAllowingStateLoss();

            mFragmentTitleList.clear();

            notifyDataSetChanged();
        }*/

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // Add Fragments to Tabs
    private void setupViewPager() {

        //tab and viewpager
//        tabs.setupWithViewPager(viewPager);

        //load array
//        String[] titles = getResources().getStringArray(R.array.arr_tab_1);

        //clear cache fragment
//        viewPager.setSaveFromParentEnabled(false);
        //create fragment
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager());

        if (mMusicService.getListAlbum() != null)
            adapter.addFragment(Fragment_Player_Playlist.newInstance(mMusicService.getListAlbum(), mPlayerAdapter.getCurrentPosition()), "list");

        if (mMusicService.getMedia() != null)
            adapter.addFragment(Fragment_Player_Cover.newInstance(mPlayerAdapter.getCurrentSong().getCoverUrl()), "player");

        String lyric = mPlayerAdapter.getCurrentSong().getLyricOrSubtitle();
        if (!TextUtils.isEmpty(lyric) && lyric.length() > 10) {
            adapter.addFragment(Fragment_Player_Lyric.newInstance(lyric), "lyric");
        }

        viewPager.setAdapter(adapter);
        //indicator
        indicator.setViewPager(viewPager);

        viewPager.setCurrentItem(1);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.song_position)
    TextView songPosition;
    @BindView(R.id.song_duration)
    TextView songDuration;
    @BindView(R.id.buttonPlayPause)
    ImageButton playPause;
    @BindView(R.id.buttonNext)
    ImageButton next;
    @BindView(R.id.buttonPrevious)
    ImageButton previous;
    @BindView(R.id.txt1)
    TextView songTitle;
    @BindView(R.id.txt2)
    TextView songSinger;
    @BindView(R.id.btnLike)
    ShineButton btnLike;

    @BindView(R.id.relativeMedia)
    RelativeLayout relativeMedia;

    @BindView(R.id.buttonShuffle)
    ImageButton buttonShuffle;
    @BindView(R.id.buttonRepeat)
    ImageButton buttonRepeat;

    private void setUIButtonShuffleRepeat() {
        boolean isShuffle = db.getBoolean(TinyDB.IS_SHUFFLE);
        boolean isRepeat = db.getBoolean(TinyDB.IS_REPEAT);

        buttonShuffle.setImageResource(isShuffle ? R.drawable.ic_shuffle_selected : R.drawable.ic_shuffle);
        buttonRepeat.setImageResource(isRepeat ? R.drawable.ic_replay_selected : R.drawable.ic_replay);
    }

    private void clickShuffle() {
        boolean isShuffle = !db.getBoolean(TinyDB.IS_SHUFFLE);
        db.putBoolean(TinyDB.IS_SHUFFLE, isShuffle);
        buttonShuffle.setImageResource(isShuffle ? R.drawable.ic_shuffle_selected : R.drawable.ic_shuffle);
    }

    private void clickRepeat() {
        boolean isRepeat = !db.getBoolean(TinyDB.IS_REPEAT);
        db.putBoolean(TinyDB.IS_REPEAT, isRepeat);
        buttonRepeat.setImageResource(isRepeat ? R.drawable.ic_replay_selected : R.drawable.ic_replay);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setViews() {

//        relativeMedia.setVisibility(View.GONE);

        /*playPause = findViewById(R.id.buttonPlayPause);
        next = findViewById(R.id.buttonNext);
        previous = findViewById(R.id.buttonPrevious);
        seekBar = findViewById(R.id.seekBar);
        songTitle = findViewById(R.id.songTitle);*/
        //To listen to clicks
        playPause.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        buttonShuffle.setOnClickListener(this);
        buttonRepeat.setOnClickListener(this);
        setUIButtonShuffleRepeat();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeSeekBar() {
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        if (fromUser) {
                            userSelectedPosition = progress;

                        }

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        if (mUserIsSeeking) {

                        }
                        mUserIsSeeking = false;
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.buttonPlayPause): {
                resumeOrPause();
                break;
            }
            case (R.id.buttonNext): {
                skipNext();
                break;
            }
            case (R.id.buttonPrevious): {
                skipPrev();
                break;
            }
            case R.id.buttonShuffle:
                clickShuffle();
                break;
            case R.id.buttonRepeat:
                clickRepeat();
                break;

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void skipPrev() {
        if (checkIsPlayer()) {
            mPlayerAdapter.instantReset();
        }
    }

    public void resumeOrPause() {
        if (checkIsPlayer()) {
            mPlayerAdapter.resumeOrPause();
        }
    }

    public void skipNext() {
        if (checkIsPlayer()) {
            mPlayerAdapter.skip(true);
        }
    }

    private boolean checkIsPlayer() {

        boolean isPlayer = false;
        if (mPlayerAdapter != null) {
            isPlayer = mPlayerAdapter.isMediaPlayer();
        }
        if (!isPlayer) {
            MyUtils.showToastDebug(context, "Play song first");
        }
        return isPlayer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private PlayerService mMusicService;
    private boolean isBound;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;
    private PlaybackListener mPlaybackListener;
    private MusicNotificationManager mMusicNotificationManager;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            MyUtils.log("ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            PlayerService.MyBinder binder = (PlayerService.MyBinder) iBinder;
            mMusicService = binder.getService();
            mPlayerAdapter = mMusicService.getMediaPlayerHolder();
            mMusicNotificationManager = mMusicService.getMusicNotificationManager();
            if (mPlaybackListener == null) {
                mPlaybackListener = new PlaybackListener();
                mPlayerAdapter.setPlaybackInfoListener(mPlaybackListener);
            }

            isBound = true;

            if (mPlayerAdapter != null && mPlayerAdapter.getCurrentSong() != null) {
                restorePlayerStatus();
            }


            //sau khi bound thi lay thong tin bai hat tu service va hien thi len giao dien
            initUI();

            //lay comment
            initRecyclerViewComment();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            MyUtils.log("ServiceConnection: disconnected from service.");
            isBound = false;
        }
    };


    private void bindService() {
        Intent intent = new Intent(context, PlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void startMediaPlayer() {
//        Intent intent = new Intent(context, PlayerService.class);
//        startService(intent);
        //neu dang co service chay thi hien thi player
        if (MyUtils.isMyServiceRunning(context, PlayerService.class)) {
            //sau khi start service thi moi duoc bind service de lay thong tin
            bindService();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startMediaPlayer();
        if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {
            restorePlayerStatus();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        if (mPlayerAdapter != null && mPlayerAdapter.isMediaPlayer()) {
            mPlayerAdapter.onPauseActivity();
        }

        MyUtils.hideKeyboard(context, txtInput);
        EventBus.getDefault().unregister(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onPositionChanged(long position) {
            if (!mUserIsSeeking) {
                seekBar.setProgress((int) position);
            }
        }

        @Override
        public void onStateChanged(@State int state) {

            updatePlayingStatus();
            if (mPlayerAdapter.getState() != State.RESUMED && mPlayerAdapter.getState() != State.PAUSED) {
                updatePlayingInfo(false, true);
            }

            //update fragments
            updateAllFragment();
        }

        @Override
        public void onPlaybackCompleted() {
            //After playback is complete
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void restorePlayerStatus() {
        seekBar.setEnabled(mPlayerAdapter.isMediaPlayer());

        //if we are playing and the activity was restarted
        //update the controls panel
        if (mPlayerAdapter != null && mPlayerAdapter.getCurrentSong() != null) {

            mPlayerAdapter.onResumeActivity();
            updatePlayingInfo(true, false);
        }
    }

    private Media media;

    private void updatePlayingInfo(boolean restore, boolean startPlay) {

        relativeMedia.setVisibility(View.VISIBLE);

        if (startPlay) {
            mPlayerAdapter.getMediaPlayer().setPlayWhenReady(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMusicService.startForeground(MusicNotificationManager.NOTIFICATION_ID,
                            mMusicNotificationManager.createNotification());
                }
            }, 250);
        }

        media = mPlayerAdapter.getAlbum();
        if (media != null) {
            btnLike.setChecked(media.isLiked());
            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLikeMedia(media.getMediaId(), !media.isLiked());
                }
            });

            setTitleComment(media);
        }

        final MediaItem selectedSong = mPlayerAdapter.getCurrentSong();

        songTitle.setText(selectedSong.getTitle());
        songSinger.setText(selectedSong.getAuthor());


        if (restore) {
            seekBar.setProgress((int) mPlayerAdapter.getPlayerPosition());
            updatePlayingStatus();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //stop foreground if coming from pause state
                    if (mMusicService.isRestoredFromPause()) {
                        mMusicService.stopForeground(false);
                        mMusicService.getMusicNotificationManager().getNotificationManager()
                                .notify(MusicNotificationManager.NOTIFICATION_ID,
                                        mMusicService.getMusicNotificationManager().getNotificationBuilder().build());
                        mMusicService.setRestoredFromPause(false);
                    }
                }
            }, 250);
        }


        //set duration
        //duration
        try {
            int dur = 0;
            if (mPlayerAdapter != null) dur = (int) mPlayerAdapter.getPlayerDuration();
            int durMns = (dur / 60000) % 60000;
            int durScs = dur % 60000 / 1000;
            String songDur = String.format("%02d:%02d", durMns, durScs);
            songDuration.setText(songDur);
            seekBar.setMax(dur);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set position
        //setup handler that will keep seekBar and playTime in sync
        final Handler handler = new Handler();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //position
                try {
                    int pos = 0;
                    if (mPlayerAdapter != null) pos = (int) mPlayerAdapter.getPlayerPosition();
                    int posMns = (pos / 60000) % 60000;
                    int posScs = pos % 60000 / 1000;
                    String songPos = String.format("%02d:%02d", posMns, posScs);
                    songPosition.setText(songPos);

                    seekBar.setProgress(pos);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, 1000);
            }
        });
    }


    private void updatePlayingStatus() {
        final int drawable = mPlayerAdapter.getState() != PlaybackInfoListener.State.PAUSED ?
                R.drawable.ic_pause_large : R.drawable.ic_play_large;
        playPause.post(new Runnable() {
            @Override
            public void run() {
                playPause.setImageResource(drawable);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_SELECT_SONG = "ACTION_SELECT_SONG";
    public static final String ACTION_REMOVE_COMMENT = "ACTION_REMOVE_COMMENT";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                switch (intent.getAction()) {
                    case ACTION_SELECT_SONG:
                        long mediaId = b.getLong(MediaItem.ITEM_ID, 0);
                        if (mediaId > 0) {
                            //play bai moi
                            if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {
                                mPlayerAdapter.setCurrentSong(mediaId);
                            }

                            //giao dien player theo bai hat
                            onResume();

                            //update fragments
                            updateAllFragment();

                        }
                        break;
                    case ACTION_REMOVE_COMMENT:
                        int number = b.getInt(Comment.NUMBER_COMMENT);
                        if (number > 0) {
                            if (media != null) {
                                media.setCommentCount(media.getCommentCount() - number);
                                setTitleComment(media);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_SELECT_SONG));
        registerReceiver(receiver, new IntentFilter(ACTION_REMOVE_COMMENT));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!MH01_MainActivity.isExist) {
            startActivity(new Intent(this, MH01_MainActivity.class));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void updateAllFragment() {
        //goi broadcast den tung fragment
        if (mPlayerAdapter.getCurrentSong() != null) {

            //POSITION
            int position = mPlayerAdapter.getCurrentPosition();
            Intent intent1 = new Intent(Fragment_Player_Playlist.ACTION_CHANGE_POSITION);
            intent1.putExtra(MediaItem.POSITION, position);
            sendBroadcast(intent1);

            //COVER
            String cover = mPlayerAdapter.getCurrentSong().getCoverUrl();
            Intent intent2 = new Intent(Fragment_Player_Cover.ACTION_CHANGE_COVER);
            intent2.putExtra(MediaItem.COVER, cover);
            sendBroadcast(intent2);

            //LYRIC
            String lyric = mPlayerAdapter.getCurrentSong().getLyricOrSubtitle();
            Intent intent3 = new Intent(Fragment_Player_Lyric.ACTION_CHANGE_LYRIC);
            intent3.putExtra(MediaItem.LYRIC, lyric);
            sendBroadcast(intent3);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setLikeMedia(long mediaId, boolean isLiked) {
        if (isLiked) {
            MyApplication.apiManager.mediaLike(mediaId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null) {
                        parseJson(obj.toString(), mediaId, isLiked);
                    }
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
                    if (obj != null) {
                        parseJson(obj.toString(), mediaId, isLiked);
                    }
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
                        setTitleComment(media);


                        if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {
                            mPlayerAdapter.getAlbum().setLiked(isLiked);
                            mPlayerAdapter.getAlbum().setLikeCount(count);
                        }

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


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


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
            if (media != null) {
                /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getText(R.string.sending));
                progressDialog.show();*/

                ProgressUtils.show(context);

                String replyToId = null;
                if (replyToComment != null) {
                    replyToId = String.valueOf(replyToComment.getItemId());
                }

                MyApplication.apiManager.mediaCommentAdd(media.getMediaId(), replyToId, comment, new Callback<JsonObject>() {
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

                                        media.setCommentCount(media.getCommentCount() + 1);
                                        setTitleComment(media);

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
            }

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

            // set hinh, ten, description
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

   /* @Override
    public void onStop() {
        super.onStop();
        MyUtils.hideKeyboard(context, txtInput);
        EventBus.getDefault().unregister(this);
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMediaComment event) {
        if (event != null && event.getComment() != null) {
            initReplyLayout(event.getComment());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*@BindView(R.id.nestedScrollView)
    NestedScrollView scrollView;*/
    @BindView(R.id.recyclerView2)
    RecyclerView rv2;
    CommentAdapterSectionParent_MediaComment adapterComment;

    private void initRecyclerViewComment() {

        /*scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!isHideKeyboard){
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        MyUtils.hideKeyboard(context);
                        isHideKeyboard = true;
                        return true;
                    }
                }

                return false;
            }
        });*/

        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        llm2.setOrientation(RecyclerView.VERTICAL);
        rv2.setLayoutManager(llm2);
        rv2.setNestedScrollingEnabled(false);
        rv2.setHasFixedSize(false);
        rv2.setItemAnimator(new DefaultItemAnimator());

        rv2.setNestedScrollingEnabled(true);
        ViewCompat.setNestedScrollingEnabled(rv2, true);


        //list comment
        //adapterVideo comment
        adapterComment = new CommentAdapterSectionParent_MediaComment(new ArrayList<MediaComment>(), context);
        adapterComment.shouldShowHeadersForEmptySections(true);
        rv2.setAdapter(adapterComment);
        rv2.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                MyUtils.showToastDebug(context, "Load more...");
                MediaComment comment = adapterComment.getLastestItem();
                if (comment != null && media != null) {
                    getComments(media.getMediaId(), comment.getItemId());
                }
            }
        });

        if (media != null) {
            getComments(media.getMediaId(), 0);
        }


    }

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
    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    @BindView(R.id.imgExpand)
    ImageView imgExpand;

    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            imgExpand.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_500_24dp);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            imgExpand.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_500_24dp);
        }
    }

    BottomSheetBehavior sheetBehavior;

    private void initBottomSheetComment() {
        imgExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBottomSheet();
            }
        });
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setText("Close Sheet");
                        imgExpand.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_500_24dp);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        btnBottomSheet.setText("Expand Sheet");
                        imgExpand.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_500_24dp);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }


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

}
