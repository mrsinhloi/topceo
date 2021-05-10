package com.topceo.shopping;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.fragments.Fragment_WebView;
import com.topceo.mediaplayer.audio.MediaPlayerActivity;
import com.topceo.mediaplayer.audio.MusicNotificationManager;
import com.topceo.mediaplayer.audio.PlaybackInfoListener;
import com.topceo.mediaplayer.audio.PlayerAdapter;
import com.topceo.mediaplayer.audio.PlayerService;
import com.topceo.mediaplayer.extractor.ExtractorException;
import com.topceo.mediaplayer.extractor.YoutubeStreamExtractor;
import com.topceo.mediaplayer.extractor.model.YTMedia;
import com.topceo.mediaplayer.extractor.model.YTSubtitles;
import com.topceo.mediaplayer.extractor.model.YoutubeMeta;
import com.topceo.mediaplayer.pip.presenter.VideoListItemOpsKt;
import com.topceo.objects.menu.MenuShop;
import com.topceo.objects.menu.MenuType;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.DownloadUtils;
import com.topceo.utils.MyUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingActivity extends AppCompatActivity implements View.OnClickListener {
    public static boolean isExist = false;
    private Activity context = this;
    private TinyDB db;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    private boolean isOpenMyStore = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isExist = true;
        setContentView(R.layout.shopping_activity);
        ButterKnife.bind(this);
        db = new TinyDB(this);

        setTitleBar();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            isOpenMyStore = b.getBoolean(User.IS_OPEN_MY_STORE, false);
        }


        registerReceiver();

        setViews();
//        initializeSeekBar();

        getListMenu();
        setupViewPager();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    ViewPagerAdapter adapter;

    /*private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.ALL), "1");
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.AUDIO), "2");
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.VIDEO), "3");
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.GIFT), "4");
        adapter.addFragment(Fragment_MyStore.newInstance(MediaType.ALL), "5");


        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        setIcon();

        if (isOpenMyStore) {
            gotoMyStore();
        }

    }

    private void gotoMyStore() {
        if (viewPager != null && adapter != null) {
            viewPager.setCurrentItem(adapter.getCount() - 1);
        }
    }

    private int[] icons = {
            R.drawable.ic_1,
            R.drawable.ic_2,
            R.drawable.ic_3,
            R.drawable.ic_4,
            R.drawable.ic_5

    };

    private void setIcon() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            tabs.getTabAt(i).setIcon(icons[i]);
        }
    }*/


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        /*@Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }*/
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    private void gotoPayment(long mediaId) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(Media.MEDIA_ID, mediaId);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        isExist = false;
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }

        dismissDialog();
//        AndroidNetworking.cancel(DOWNLOAD_EPUB_TAG);
        if (downloadId > 0) {
            PRDownloader.cancelAll();
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkCanPlay(final Media item) {
        if (item != null) {

            MyApplication.apiManager.checkCanPlay(
                    item.getMediaId(),
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {

//                                {"ErrorCode":0,"Message":"","Data":{"CanPlay":0}}
                                JsonObject dataObj = data.getAsJsonObject("Data");
                                boolean canPlay = dataObj.get("CanPlay").getAsBoolean();

                                if (canPlay) {//da ton tai thi vao player
                                    switch (item.getMediaTypeInt()) {
                                        case MediaType.AUDIO_INT:
                                            getAlbumAudio(item);
                                            break;
                                        case MediaType.VIDEO_INT:
                                            getAlbumVideo(item);
                                            break;
                                        case MediaType.PHOTO_INT:
//                                                MyUtils.showToast(context, "Developing...");
                                            getAlbumImage(item);
                                            break;
                                        case MediaType.PDF_INT:
//                                            MyUtils.showToast(context, "Developing...");
                                            break;
                                        case MediaType.EPUB_INT:
//                                            MyUtils.showToast(context, "Developing...");
                                            break;
                                    }
                                } else {
                                    //Mua
                                    gotoPayment(item.getMediaId());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////


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
            }
            mPlayerAdapter.setPlaybackInfoListener(mPlaybackListener);
            if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {
                restorePlayerStatus();
            }

            isBound = true;

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

        if (mPlayerAdapter != null && mPlayerAdapter.getCurrentSong() != null) {
            if (mPlaybackListener == null) {
                mPlaybackListener = new PlaybackListener();
            }
            mPlayerAdapter.setPlaybackInfoListener(mPlaybackListener);
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isPlayingThisMedia(long mediaId) {
        boolean isThisMedia = false;
        if (isBound && mMusicService != null && mMusicService.getMedia() != null) {
            if (mediaId == mMusicService.getMedia().getMediaId()) {
                isThisMedia = true;
            }
        }
        return isThisMedia;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onPositionChanged(long position) {
            if (!mUserIsSeeking) {
//                seekBar.setProgress((int)position);
            }
        }

        @Override
        public void onStateChanged(@State int state) {

            updatePlayingStatus();
            if (mPlayerAdapter.getState() != State.RESUMED && mPlayerAdapter.getState() != State.PAUSED) {
                updatePlayingInfo(false, true);
            }
        }

        @Override
        public void onPlaybackCompleted() {
            //After playback is complete
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void restorePlayerStatus() {
//        seekBar.setEnabled(mPlayerAdapter.isMediaPlayer());

        //if we are playing and the activity was restarted
        //update the controls panel
        if (mPlayerAdapter != null && mPlayerAdapter.getCurrentSong() != null) {

            mPlayerAdapter.onResumeActivity();
            updatePlayingInfo(true, false);
        }
    }

    private void updatePlayingInfo(boolean restore, boolean startPlay) {

        if (!isFinishing() && context != null) {
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

            final MediaItem selectedSong = mPlayerAdapter.getCurrentSong();

            songTitle.setText(selectedSong.getTitle());
            songTitle.setSelected(true);
            songSinger.setText(selectedSong.getAuthor());
//        int size = getActionBarHeight();
            int size = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
            if (!TextUtils.isEmpty(selectedSong.getThumbnailUrl())) {
                Glide.with(context)
                        .load(selectedSong.getThumbnailUrl())
                        .override(size, size)
                        .centerCrop()
                        .placeholder(R.drawable.no_media_small)
                        .into(img1);
            }

            if (restore) {
//            seekBar.setProgress((int)mPlayerAdapter.getPlayerPosition());
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
        }


        //set duration
        //duration
        /*try {
            int dur = 0;
            if(mPlayerAdapter != null) dur = (int)mPlayerAdapter.getPlayerDuration();
            int durMns = (dur / 60000) % 60000;
            int durScs = dur % 60000 / 1000;
            String songDur = String.format("%02d:%02d",  durMns, durScs);
            songDuration.setText(songDur);
            seekBar.setMax(dur);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //set position
        //setup handler that will keep seekBar and playTime in sync
        /*final Handler handler = new Handler();
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                //position
                try {
                    int pos = 0;
                    if(mPlayerAdapter != null) pos = (int)mPlayerAdapter.getPlayerPosition();
                    int posMns = (pos / 60000) % 60000;
                    int posScs = pos % 60000 / 1000;
                    String songPos = String.format("%02d:%02d",  posMns, posScs);
                    songPosition.setText(songPos);

                    seekBar.setProgress(pos);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, 1000);
            }
        });*/
    }


    private void updatePlayingStatus() {
        final int drawable = mPlayerAdapter.getState() != PlaybackInfoListener.State.PAUSED ?
                R.drawable.ic_pause : R.drawable.ic_play;
        playPause.post(new Runnable() {
            @Override
            public void run() {
                playPause.setImageResource(drawable);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.img1)
    ImageView img1;
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
    @BindView(R.id.relativeMedia)
    RelativeLayout relativeMedia;

    private void setViews() {

        relativeMedia.setVisibility(View.GONE);

        /*playPause = findViewById(R.id.buttonPlayPause);
        next = findViewById(R.id.buttonNext);
        previous = findViewById(R.id.buttonPrevious);
        songTitle = findViewById(R.id.songTitle);*/
        //To listen to clicks
        playPause.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        relativeMedia.setOnClickListener(this);


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
            case R.id.relativeMedia:
                startActivity(new Intent(context, MediaPlayerActivity.class));
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

        boolean isPlayer = mPlayerAdapter.isMediaPlayer();
        if (!isPlayer) {
            MyUtils.showToastDebug(context, "Play song first");
        }
        return isPlayer;
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
    public int getActionBarHeight() {
        int height;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            height = getActionBar().getHeight();
        } else {
            height = getSupportActionBar().getHeight();

        }
        return height;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private int pageIndex = 1;
    private int itemCount = 200;

    private void getAlbumAudio(Media media) {
        if (media != null) {

            boolean isPlayingThisMedia = isPlayingThisMedia(media.getMediaId());
            //todo Nếu đang playing media này thì chỉ hiển thị media lên, ngược lại thì lấy mới và phát
            if (isPlayingThisMedia) {
                //media nay dang playing
                //mo man hinh media player
                relativeMedia.performClick();
            } else {

                if (media.isLock()) {//get all
                    MyApplication.apiManager.getListAudioAll(
                            pageIndex,
                            itemCount,
                            new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    parseListAudio(response.body(), media);
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {

                                }
                            });
                } else {
                    MyApplication.apiManager.getListItemOfMedia(
                            media.getMediaId(),
                            new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    parseListAudio(response.body(), media);
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {

                                }
                            });
                }


            }

        }
    }

    private void parseListAudio(JsonObject body, Media media) {
        JsonObject data = body;
        if (data != null) {
            Type collectionType = new TypeToken<List<MediaItem>>() {
            }.getType();
            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

            if (result != null) {
                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                    ArrayList<MediaItem> list = (ArrayList<MediaItem>) result.getData();
                    MyUtils.showToastDebug(context, "List size " + list.size());

                    //neu co bai hat thi moi play
                    if (list.size() > 0) {
                        //Goi xuong service play [media+listItem]
                        Bundle b = new Bundle();
                        b.putParcelable(Media.MEDIA, media);
                        b.putParcelableArrayList(MediaItem.LIST, list);
                        MyUtils.startPlayerService(context, PlayerService.class, b);

                        //bind vao service
                        onResume();

                        //mo man hinh media player
                        relativeMedia.performClick();
                    } else {
                        MyUtils.showToast(context, R.string.no_song);
                    }
                }
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getAlbumVideo(final Media media) {
        if (media != null) {

            if (media.isLock()) {
                MyApplication.apiManager.getListVideoAll(
                        pageIndex,
                        itemCount,
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                parseVideo(response.body(), media);
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
            } else {
                MyApplication.apiManager.getListItemOfMedia(
                        media.getMediaId(),
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                parseVideo(response.body(), media);
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
            }


        }
    }

    private void parseVideo(JsonObject body, Media media) {
        JsonObject data = body;
        if (data != null) {
            Type collectionType = new TypeToken<List<MediaItem>>() {
            }.getType();
            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

            if (result != null) {
                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                    ArrayList<MediaItem> list = (ArrayList<MediaItem>) result.getData();
                    MyUtils.showToastDebug(context, "List size " + list.size());

                    //neu co bai hat thi moi play
                    if (list.size() > 0) {

                        //tat nhac, pause thoi
                        if (mPlayerAdapter != null && mPlayerAdapter.isPlaying()) {
//                            stopService(new Intent(context, PlayerService.class));
                            if (checkIsPlayer()) {
                                mPlayerAdapter.pause();
                            }
                        }

                        //mo video
                        /*Intent intent = new Intent(context, VideoActivity.class);
                        intent.putExtra(Media.MEDIA, media);
                        intent.putParcelableArrayListExtra(MediaItem.LIST, list);
                        startActivity(intent);*/

                        String[] arr = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            arr[i] = list.get(i).getFileUrl();
                        }

                        String url = arr[0];
                        if (MyUtils.isYoutubeUrl(url)) {
                            String videoId = MyUtils.getYoutubeId(url);
                            new YoutubeStreamExtractor(new YoutubeStreamExtractor.ExtractorListner() {
                                @Override
                                public void onExtractionDone(List<YTMedia> adativeStream, final List<YTMedia> muxedStream, List<YTSubtitles> subtitles, YoutubeMeta meta) {
                                    //url to get subtitle
//                                    String subUrl = subtitles.get(0).getBaseUrl();
                                    if(muxedStream!=null && muxedStream.size()>0){
                                        String subUrl = muxedStream.get(muxedStream.size()-1).getUrl();
                                        VideoListItemOpsKt.playVideo(context, subUrl);
                                    }

                                    /*for (YTMedia media:adativeStream) {
                                        if(media.isVideo()){
                                            //is video

                                        }else{
                                            //is audio
                                        }
                                    }*/
                                }

                                @Override
                                public void onExtractionGoesWrong(final ExtractorException e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }).useDefaultLogin().Extract(url);
                        } else {
                            VideoListItemOpsKt.playVideoList(context, arr, 0);
                        }

                    } else {
                        MyUtils.showToast(context, R.string.no_video);
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getAlbumImage(final Media media) {
        if (media != null) {

            MyApplication.apiManager.getListItemOfMedia(
                    media.getMediaId(),
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            parseImages(response.body(), media);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });

        }
    }

    private void parseImages(JsonObject body, Media media) {
        JsonObject data = body;
        if (data != null) {
            Type collectionType = new TypeToken<List<MediaItem>>() {
            }.getType();
            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

            if (result != null) {
                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                    ArrayList<MediaItem> list = (ArrayList<MediaItem>) result.getData();
                    MyUtils.showToastDebug(context, "List size " + list.size());

                    //neu co bai hat thi moi play
                    if (list.size() > 0) {

                        //mo view album hinh
                        /*Intent intent = new Intent(context, VideoActivity.class);
                        intent.putExtra(Media.MEDIA, media);
                        intent.putParcelableArrayListExtra(MediaItem.LIST, list);
                        startActivity(intent);*/

                        ArrayList<String> listString = new ArrayList<String>();
                        for (int i = 0; i < list.size(); i++) {
                            String item = list.get(i).getFileUrl();
                            if (!TextUtils.isEmpty(item)) {
                                listString.add(item);
                            }
                        }
                        Intent intent = new Intent(context, ShowAlbumActivity.class);
                        intent.putExtra(ShowAlbumActivity.IMAGE_URLS, listString);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } else {
                        MyUtils.showToast(context, R.string.no_video);
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getListMenu() {
        MyApplication.apiManager.getListMenu(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<MenuShop>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<MenuShop> list = (ArrayList<MenuShop>) result.getData();
                                    if (list != null && list.size() > 0) {


                                        //tao icon cho tab
                                        adapter = new ViewPagerAdapter(getSupportFragmentManager());
                                        viewPager.setOffscreenPageLimit(list.size());


                                        for (int i = 0; i < list.size(); i++) {
                                            MenuShop item = list.get(i);
                                            switch (item.getTargetType()) {
                                                case MenuType.MEDIA:
                                                    adapter.addFragment(Fragment_Shop.newInstance(item), String.valueOf(item.getPosition()));
                                                    break;
                                                case MenuType.WEBVIEW:
                                                    String link = item.getTargetContent().get("Link").getAsString();
                                                    adapter.addFragment(Fragment_WebView.newInstance(link), String.valueOf(item.getPosition()));
                                                    break;
                                            }

                                        }


                                        viewPager.setAdapter(adapter);
                                        tabs.setupWithViewPager(viewPager);

                                        //set icon
                                        for (int i = 0; i < tabs.getTabCount(); i++) {
                                            MenuShop item = list.get(i);
                                            //get drawable
                                            try {
//                                                tabs.getTabAt(i).setCustomView(createTabItemView(item.getMenuIcon()));
                                                tabs.getTabAt(i).setText(item.getMenuName());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        if (isOpenMyStore) {
                                            gotoMyStore();
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

    private void setupViewPager() {
        /*adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.ALL), "1");
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.AUDIO), "2");
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.VIDEO), "3");
        adapter.addFragment(Fragment_Shop.newInstance(MediaType.GIFT), "4");
        adapter.addFragment(Fragment_MyStore.newInstance(MediaType.ALL), "5");


        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        setIcon();

        if (isOpenMyStore) {
            gotoMyStore();
        }*/

    }

    private void gotoMyStore() {
        if (viewPager != null && adapter != null && adapter.getCount() > 0) {
            viewPager.setCurrentItem(adapter.getCount() - 1);
        }
    }


    private View createTabItemView(String imgUri) {
        ImageView imageView = new ImageView(this);
        int size = getResources().getDimensionPixelSize(R.dimen.margin_24dp);
        TabLayout.LayoutParams params = new TabLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        int margin = getResources().getDimensionPixelOffset(R.dimen.margin_5dp);
//        params.setMargins(margin,margin,margin,margin);
        imageView.setLayoutParams(params);
//        imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)));
//

        Glide.with(context)
                .load(imgUri)
                .override(size, size)
                .into(imageView);

        return imageView;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_PLAY_ALBUM = "ACTION_PLAY_ALBUM";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                switch (intent.getAction()) {
                    case ACTION_PLAY_ALBUM:

                        Media item = b.getParcelable(Media.MEDIA);

                        switch (item.getMediaTypeInt()) {
                            case MediaType.AUDIO_INT:
                                if (item.isFree()) {//play
                                    getAlbumAudio(item);
                                } else {//buy
                                    checkCanPlay(item);
                                }
                                break;
                            case MediaType.VIDEO_INT:
                                if (item.isFree()) {//play
                                    getAlbumVideo(item);
                                } else {//buy
                                    gotoPayment(item.getMediaId());
                                }
                                break;
                            case MediaType.PHOTO_INT:
                                if (item.isFree()) {//play
//                                    MyUtils.showToast(context, "Playing developing...");
                                    getAlbumImage(item);
                                } else {//buy
                                    gotoPayment(item.getMediaId());
                                }
                                break;
                            case MediaType.PDF_INT:
                                if (item.isFree()) {//play
                                    MyUtils.showToast(context, "PDF developing...");
                                } else {//buy
                                    gotoPayment(item.getMediaId());
                                }
                                break;
                            case MediaType.EPUB_INT:
                                if (item.isFree()) {//play
//                                    MyUtils.showToast(context, "EPUB developing...");
                                    String link = item.getLink();

                                    //Kiem tra quyen ghi file truoc khi download
                                    checkStoragePermission(link, item.getMediaId());
                                } else {//buy
                                    gotoPayment(item.getMediaId());
                                }
                                break;
                        }

                        break;


                    default:
                        break;
                }

                if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);
        filter.addAction(ACTION_PLAY_ALBUM);

        registerReceiver(receiver, filter);


    }

    ///////////////////////////////////////////////////
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;

    private void setTitleBar() {
        /*toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0,0);*/
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
        ChatUtils.setChatUnreadNumber(txtNumber);
    }

    private int downloadId = 0;
    public static final String DOWNLOAD_EPUB_TAG = "downloadEpub";

    private void downloadEpub(String url, long mediaId) {
        if (!TextUtils.isEmpty(url)) {
            String path = DownloadUtils.getRootDirPath(context);
            //do co the trung ten file nen gan them id phia truoc de phan biet
            String fileName = mediaId + "_" + com.workchat.core.utils.MyUtils.getFileNameFromUrl(url);
            File file = new File(path, fileName);
            if (!file.exists()) {
                downloadId = PRDownloader.download(url, path, fileName)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                initProgressDialog();
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                                dismissDialog();
                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                dismissDialog();
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
//                                MyUtils.log(progress.currentBytes + "/" + progress.totalBytes);
                                if (dialog != null) {
                                    int percent = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                    if (percent >= 0 && percent <= 100) {
                                        dialog.setProgress(percent);
                                    }
                                }
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
//                                MyUtils.showAlertDialog(context, "complete");
                                dismissDialog();
                                readEpub(file);
                            }

                            @Override
                            public void onError(Error error) {
//                                MyUtils.showAlertDialog(context, "error");
                                dismissDialog();
                            }

                        });
            } else {
                readEpub(file);
            }


        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    private ProgressDialog dialog;

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void initProgressDialog() {
        dismissDialog();
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.downloading));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setProgress(0);
        dialog.setMax(100);
        if (!isFinishing()) {
            dialog.show();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////
    private void readEpub(File file) {
        if (file != null) {
            Config config = new Config()
                    .setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)
                    .setDirection(Config.Direction.HORIZONTAL)
                    .setFont(Constants.FONT_LORA)
                    .setFontSize(2)
                    .setNightMode(false)
                    .setThemeColorRes(R.color.colorPrimary)
                    .setShowTts(true);

            FolioReader folioReader = FolioReader.get();
            folioReader.setConfig(config, false);
            folioReader.openBook(file.getAbsolutePath());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    private void checkStoragePermission(String link, long mediaId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            downloadEpub(link, mediaId);
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
    }

    public static final int PERMISSION_REQUEST_STORAGE = 10;

    private void runRequest() {
        ActivityCompat.requestPermissions(context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_STORAGE);
    }

    private void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(viewPager, R.string.storage_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    runRequest();
                }
            }).show();

        } else {
            Snackbar.make(viewPager, R.string.storage_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            runRequest();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////


}
