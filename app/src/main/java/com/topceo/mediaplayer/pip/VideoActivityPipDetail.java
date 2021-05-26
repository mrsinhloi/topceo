/*
 * Created on 2017/09/24.
 * Copyright © 2017–2020 刘振林. All rights reserved.
 */

package com.topceo.mediaplayer.pip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Rational;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Synthetic;
import com.google.android.material.snackbar.Snackbar;
import com.liuzhenlin.common.observer.OnOrientationChangeListener;
import com.liuzhenlin.common.observer.RotationObserver;
import com.liuzhenlin.common.observer.ScreenNotchSwitchObserver;
import com.liuzhenlin.common.utils.DisplayCutoutUtils;
import com.liuzhenlin.common.utils.FileUtils;
import com.liuzhenlin.common.utils.OSHelper;
import com.liuzhenlin.common.utils.SystemBarUtils;
import com.liuzhenlin.common.utils.UiUtils;
import com.liuzhenlin.common.utils.Utils;
import com.liuzhenlin.swipeback.SwipeBackActivity;
import com.liuzhenlin.swipeback.SwipeBackLayout;
import com.liuzhenlin.texturevideoview.ExoVideoPlayer;
import com.liuzhenlin.texturevideoview.IVideoPlayer;
import com.liuzhenlin.texturevideoview.IjkVideoPlayer;
import com.liuzhenlin.texturevideoview.TextureVideoView;
import com.liuzhenlin.texturevideoview.VideoPlayer;
import com.topceo.BuildConfig;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.chat.ChatUtils;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.eventbus.EventImageComment;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.group.GroupDetailActivity;
import com.topceo.mediaplayer.pip.bean.Video;
import com.topceo.mediaplayer.pip.presenter.IVideoPresenter;
import com.topceo.mediaplayer.pip.presenter.IVideoView;
import com.topceo.mediaplayer.pip.utils.VideoUtils2;
import com.topceo.objects.db.ImageItemDB;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserShort;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.utils.DateFormat;
import com.topceo.utils.MyUtils;
import com.topceo.viewholders.HolderUtils;
import com.topceo.views.ShowMoreTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
import static com.liuzhenlin.texturevideoview.utils.Utils.canUseExoPlayer;

/**
 * @author 刘振林
 */
public class VideoActivityPipDetail extends SwipeBackActivity implements IVideoView {
    private static WeakReference<VideoActivityPipDetail> sActivityInPiP;

    private View mStatusBarView;
    @Synthetic
    TextureVideoView mVideoView;
    private ImageView mLockUnlockOrientationButton;

    @Synthetic
    IVideoPlayer mVideoPlayer;
    @Synthetic
    final IVideoPresenter mPresenter = IVideoPresenter.newInstance();

    @Synthetic
    int mVideoWidth;
    @Synthetic
    int mVideoHeight;

    @Synthetic
    int mPrivateFlags;

    private static final int PFLAG_SCREEN_NOTCH_SUPPORT = 1;
    private static final int PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI = 1 << 1;
    private static final int PFLAG_SCREEN_NOTCH_SUPPORT_ON_MIUI = 1 << 2;
    private static final int PFLAG_SCREEN_NOTCH_HIDDEN = 1 << 3;

    private static final int PFLAG_DEVICE_SCREEN_ROTATION_ENABLED = 1 << 4;
    private static final int PFLAG_SCREEN_ORIENTATION_LOCKED = 1 << 5;
    private static final int PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE = 1 << 6;

    private static final int PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN = 1 << 7;

    private static final int PFLAG_STOPPED = 1 << 8;

    private static int sStatusHeight;
    private static int sStatusHeightInLandscapeOfNotchSupportDevice;
    private int mNotchHeight;
    @Nullable
    private ScreenNotchSwitchObserver mNotchSwitchObserver;

    private RotationObserver mRotationObserver;
    @Synthetic
    OnOrientationChangeListener mOnOrientationChangeListener;
    @Synthetic
    int mDeviceOrientation = SCREEN_ORIENTATION_PORTRAIT;
    @Synthetic
    int mScreenOrientation = SCREEN_ORIENTATION_PORTRAIT;

    @Synthetic
    Handler mHandler;

    private final Runnable mHideLockUnlockOrientationButtonRunnable =
            () -> showLockUnlockOrientationButton(false);
    private static final int DELAY_TIME_HIDE_LOCK_UNLOCK_ORIENTATION_BUTTON = 2500;

    /**
     * The arguments to be used for Picture-in-Picture mode.
     */
    @Synthetic
    PictureInPictureParams.Builder mPipParamsBuilder;

    /**
     * A {@link BroadcastReceiver} to receive action item events from Picture-in-Picture mode.
     */
    private BroadcastReceiver mReceiver;

    /**
     * Intent action for media controls from Picture-in-Picture mode.
     */
    private static final String ACTION_MEDIA_CONTROL = "media_control";

    /**
     * Intent extra for media controls from Picture-in-Picture mode.
     */
    private static final String EXTRA_PIP_ACTION = "PiP_action";

    /**
     * The intent extra value for play action.
     */
    private static final int PIP_ACTION_PLAY = 1;
    /**
     * The intent extra value for pause action.
     */
    private static final int PIP_ACTION_PAUSE = 1 << 1;
    /**
     * The intent extra value for pause action.
     */
    private static final int PIP_ACTION_FAST_FORWARD = 1 << 2;
    /**
     * The intent extra value for pause action.
     */
    private static final int PIP_ACTION_FAST_REWIND = 1 << 3;

    /**
     * The request code for play action PendingIntent.
     */
    private static final int REQUEST_PLAY = 1;
    /**
     * The request code for pause action PendingIntent.
     */
    private static final int REQUEST_PAUSE = 2;
    /**
     * The request code for fast forward action PendingIntent.
     */
    private static final int REQUEST_FAST_FORWARD = 3;
    /**
     * The request code for fast rewind action PendingIntent.
     */
    private static final int REQUEST_FAST_REWIND = 4;

    private String mPlay;
    private String mPause;
    private String mFastForward;
    private String mFastRewind;
    private String mLockOrientation;
    private String mUnlockOrientation;

    private View.OnLayoutChangeListener mOnPipLayoutChangeListener;

    @Synthetic
    ProgressBar mVideoProgressInPiP;

    @RequiresApi(Build.VERSION_CODES.O)
    @Synthetic
    RefreshVideoProgressInPiPTask mRefreshVideoProgressInPiPTask;

    @RequiresApi(Build.VERSION_CODES.O)
    private final class RefreshVideoProgressInPiPTask {
        RefreshVideoProgressInPiPTask() {
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final int progress = mVideoPlayer.getVideoProgress();
                if (mVideoPlayer.isPlaying()) {
                    mHandler.postDelayed(this, 1000 - progress % 1000);
                }
                mVideoProgressInPiP.setSecondaryProgress(mVideoPlayer.getVideoBufferProgress());
                mVideoProgressInPiP.setProgress(progress);
            }
        };

        void execute() {
            cancel();
            runnable.run();
        }

        void cancel() {
            mHandler.removeCallbacks(runnable);
        }
    }

    @Nullable
    @Override
    public MH01_MainActivity getPreviousActivity() {
        return MH01_MainActivity.mh01_mainActivity;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        mLockOrientation = getString(R.string.lockScreenOrientation);
        mUnlockOrientation = getString(R.string.unlockScreenOrientation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPlay = getString(R.string.play);
            mPause = getString(R.string.pause);
            mFastForward = getString(R.string.fastForward);
            mFastRewind = getString(R.string.fastRewind);
        }
        if (sStatusHeight == 0) {
            sStatusHeight = MyApplication.getInstance(newBase).getStatusHeightInPortrait();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.attachToView(this);
        if (mPresenter.initPlaylist(savedInstanceState, getIntent())) {
            setRequestedOrientation(mScreenOrientation);
            setContentView(R.layout.pip_activity_video_detail);
            ButterKnife.bind(this);
            initViews(savedInstanceState);
        } else {
            Activity preactivity = getPreviousActivity();
            if (preactivity == null) {
                showToast(this, R.string.cannotPlayThisVideo, Toast.LENGTH_LONG);
            } else {
                UiUtils.showUserCancelableSnackbar(preactivity.getWindow().getDecorView(),
                        R.string.cannotPlayThisVideo, Snackbar.LENGTH_LONG);
            }
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        initData();
        if (mPresenter.initPlaylistAndRecordCurrentVideoProgress(null, intent)) {
            super.onNewIntent(intent);
            setIntent(intent);

            final boolean needPlaylist = mPresenter.getPlaylistSize() > 1;
            //noinspection rawtypes
            TextureVideoView.PlayListAdapter adapter = mVideoView.getPlayListAdapter();
            if (needPlaylist && adapter != null) {
                adapter.notifyDataSetChanged();
            }
            //noinspection unchecked
            mVideoView.setPlayListAdapter(needPlaylist
                    ? adapter == null ? mPresenter.newPlaylistAdapter() : adapter
                    : null);
            mVideoView.setCanSkipToPrevious(needPlaylist);
            mVideoView.setCanSkipToNext(needPlaylist);

            mPresenter.playCurrentVideo();
        }
    }

    private void initViews(Bundle savedInstanceState) {
        initData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mStatusBarView = findViewById(R.id.view_statusBar);
            ViewGroup.LayoutParams lp = mStatusBarView.getLayoutParams();
            if (lp.height != sStatusHeight) {
                lp.height = sStatusHeight;
                mStatusBarView.setLayoutParams(lp);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mVideoProgressInPiP = findViewById(R.id.pbInPiP_videoProgress);
            }
        }

        mVideoView = findViewById(R.id.videoview);
        VideoPlayer videoPlayer = canUseExoPlayer()
                ? new ExoVideoPlayer(this) : new IjkVideoPlayer(this);
        mVideoPlayer = videoPlayer;
        videoPlayer.setVideoView(mVideoView);
        mVideoView.setVideoPlayer(videoPlayer);

        mLockUnlockOrientationButton = findViewById(R.id.btn_lockUnlockOrientation);
        mLockUnlockOrientationButton.setOnClickListener(v ->
                setScreenOrientationLocked((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_LOCKED) == 0));

        if (mPresenter.getPlaylistSize() > 1) {
            mVideoView.setPlayListAdapter(mPresenter.newPlaylistAdapter());
            mVideoView.setCanSkipToPrevious(true);
            mVideoView.setCanSkipToNext(true);
        }
        // Ensures the list scrolls to the position of the video to be played
        final int position = mPresenter.getCurrentVideoPositionInList();
        if (savedInstanceState == null && position != 0) {
            notifyPlaylistSelectionChanged(0, position, true);
        }
        mPresenter.playCurrentVideo();
        videoPlayer.addVideoListener(new IVideoPlayer.VideoListener() {
            @Override
            public void onVideoStarted() {
                mPresenter.onCurrentVideoStarted();

                if (mVideoWidth == 0 && mVideoHeight == 0) {
                    mVideoWidth = mVideoPlayer.getVideoWidth();
                    mVideoHeight = mVideoPlayer.getVideoHeight();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInPictureInPictureMode()) {
                    // We are playing the video now. In PiP mode, we want to show several
                    // action items to fast rewind, pause and fast forward the video.
                    updatePictureInPictureActions(PIP_ACTION_FAST_REWIND
                            | PIP_ACTION_PAUSE | PIP_ACTION_FAST_FORWARD);

                    if (mRefreshVideoProgressInPiPTask != null) {
                        mRefreshVideoProgressInPiPTask.execute();
                    }
                }
            }

            @Override
            public void onVideoStopped() {
                // The video stopped or reached the playlist end. In PiP mode, we want to show some
                // action items to fast rewind, play and fast forward the video.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInPictureInPictureMode()) {
                    int actions = PIP_ACTION_FAST_REWIND | PIP_ACTION_PLAY;
                    if (!(mVideoPlayer.getPlaybackState() == IVideoPlayer.PLAYBACK_STATE_COMPLETED
                            && !mVideoView.canSkipToNext())) {
                        actions |= PIP_ACTION_FAST_FORWARD;
                    }
                    updatePictureInPictureActions(actions);
                }
            }

            @Override
            public void onVideoDurationChanged(int duration) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (mVideoProgressInPiP.getMax() != duration) {
                        mVideoProgressInPiP.setMax(duration);
                    }
                }
            }

            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onVideoSizeChanged(int width, int height) {
                mVideoWidth = width;
                mVideoHeight = height;

                if (width == 0 && height == 0) return;
                if (width > height) {
                    mPrivateFlags &= ~PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE;
                    if (mScreenOrientation == SCREEN_ORIENTATION_PORTRAIT
                            && mVideoView.isInFullscreenMode()) {
                        mScreenOrientation = mDeviceOrientation == SCREEN_ORIENTATION_PORTRAIT
                                ? SCREEN_ORIENTATION_LANDSCAPE : mDeviceOrientation;
                        setRequestedOrientation(mScreenOrientation);
                        setFullscreenMode(true);
                    }
                } else {
                    mPrivateFlags |= PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE;
                    if (mScreenOrientation != SCREEN_ORIENTATION_PORTRAIT) {
                        mScreenOrientation = SCREEN_ORIENTATION_PORTRAIT;
                        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
                        setFullscreenMode(true);
                    }
                }
            }
        });
        videoPlayer.setOnSkipPrevNextListener(new VideoPlayer.OnSkipPrevNextListener() {
            @Override
            public void onSkipToPrevious() {
                mPresenter.skipToPreviousVideo();
            }

            @Override
            public void onSkipToNext() {
                mPresenter.skipToNextVideo();
            }
        });
        mVideoView.setEventListener(new TextureVideoView.EventListener() {

            @Override
            public void onPlayerChange(@Nullable VideoPlayer videoPlayer) {
                mVideoPlayer = videoPlayer;
            }

            @Override
            public void onReturnClicked() {
                if (mVideoView.isInFullscreenMode()) {
                    setFullscreenModeManually(false);
                } else {
                    finish();
                }
            }

            @Override
            public void onBackgroundPlaybackControllerClose() {
                finish();
            }

            public void onViewModeChange(int oldMode, int newMode, boolean layoutMatches) {
                isPipMode = false;
                switch (newMode) {
                    case TextureVideoView.VIEW_MODE_MINIMUM:
                        isPipMode = true;
                        showHideLayout(false);
                        if (!layoutMatches
                                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                && mVideoWidth != 0 && mVideoHeight != 0) {
                            if (mPipParamsBuilder == null) {
                                mPipParamsBuilder = new PictureInPictureParams.Builder();
                            }
                            enterPictureInPictureMode(mPipParamsBuilder
                                    .setAspectRatio(new Rational(mVideoWidth, mVideoHeight))
                                    .build());
                        }
                        break;
                    case TextureVideoView.VIEW_MODE_DEFAULT:
                        showHideLayout(true);
                        setFullscreenModeManually(false);
                        break;
                    case TextureVideoView.VIEW_MODE_LOCKED_FULLSCREEN:
                    case TextureVideoView.VIEW_MODE_VIDEO_STRETCHED_LOCKED_FULLSCREEN:
                        showHideLayout(false);
                        showLockUnlockOrientationButton(false);
                    case TextureVideoView.VIEW_MODE_VIDEO_STRETCHED_FULLSCREEN:
                    case TextureVideoView.VIEW_MODE_FULLSCREEN:
                        showHideLayout(false);
                        setFullscreenModeManually(true);
                        break;
                }
            }

            @Override
            public void onShareVideo() {
                Context context = VideoActivityPipDetail.this;
                mPresenter.shareCurrentVideo(context);
            }

            @Override
            public void onShareCapturedVideoPhoto(@NonNull File photo) {
                Context context = VideoActivityPipDetail.this;
                mPresenter.shareCapturedVideoPhoto(context, photo);
            }
        });
        mVideoView.setOpCallback(new TextureVideoView.OpCallback() {
            @NonNull
            @Override
            public Window getWindow() {
                return VideoActivityPipDetail.this.getWindow();
            }

            @NonNull
            @Override
            public Class<? extends Activity> getHostActivityClass() {
                return VideoActivityPipDetail.this.getClass();
            }

            @NonNull
            @Override
            public String getAppExternalFilesDir() {
                return MyApplication.getAppExternalFilesDir().getPath();
            }
        });
    }

    @Override
    public void setVideoToPlay(@NonNull Video video) {
        mVideoPlayer.setVideoPath(video.getPath());
        mVideoView.setTitle(FileUtils.getFileTitleFromFileName(video.getName()));
    }

    @Override
    public void seekPositionOnVideoStarted(int position) {
        // Restores the playback position saved at the last time
        mVideoPlayer.seekTo(position, false);
    }

    @Override
    public int getPlayingVideoProgress() {
        return mVideoPlayer.getVideoProgress();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sActivityInPiP != null) {
            VideoActivityPipDetail activity = sActivityInPiP.get();
            if (activity != this) {
                sActivityInPiP.clear();
                sActivityInPiP = null;
                if (activity != null) {
                    // We need to unregister the receiver registered for it when it entered pip mode
                    // first, since its onPictureInPictureModeChanged() method will not be called
                    // (we are still in picture-in-picture mode).
                    activity.unregisterReceiver(activity.mReceiver);
                    activity.finish();
                }
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPrivateFlags &= ~PFLAG_STOPPED;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !isInPictureInPictureMode()) {
            if (mNotchSwitchObserver != null) {
                mNotchSwitchObserver.startObserver();
            }
            setAutoRotationEnabled(true);
        }

        mVideoPlayer.openVideo();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DisplayCutout dc = decorView.getRootWindowInsets().getDisplayCutout();
            if (dc != null) {
                mPrivateFlags |= PFLAG_SCREEN_NOTCH_SUPPORT;
                if (OSHelper.isEMUI()) {
                    mPrivateFlags |= PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI;
                } else if (OSHelper.isMIUI()) {
                    mPrivateFlags |= PFLAG_SCREEN_NOTCH_SUPPORT_ON_MIUI;
                }
                mNotchHeight = dc.getSafeInsetTop();
                DisplayCutoutUtils.setLayoutInDisplayCutoutSinceP(window, true);
            }
        } else if (OSHelper.isEMUI()) {
            if (DisplayCutoutUtils.hasNotchInScreenForEMUI(this)) {
                mPrivateFlags |= PFLAG_SCREEN_NOTCH_SUPPORT | PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI;
                mNotchHeight = DisplayCutoutUtils.getNotchSizeForEMUI(this)[1];
                DisplayCutoutUtils.setLayoutInDisplayCutoutForEMUI(window, true);
            }
        } else if (OSHelper.isColorOS()) {
            if (DisplayCutoutUtils.hasNotchInScreenForColorOS(this)) {
                mPrivateFlags |= PFLAG_SCREEN_NOTCH_SUPPORT;
                mNotchHeight = DisplayCutoutUtils.getNotchSizeForColorOS()[1];
            }
        } else if (OSHelper.isFuntouchOS()) {
            if (DisplayCutoutUtils.hasNotchInScreenForFuntouchOS(this)) {
                mPrivateFlags |= PFLAG_SCREEN_NOTCH_SUPPORT;
                mNotchHeight = DisplayCutoutUtils.getNotchHeightForFuntouchOS(this);
            }
        } else if (OSHelper.isMIUI()) {
            if (DisplayCutoutUtils.hasNotchInScreenForMIUI()) {
                mPrivateFlags |= PFLAG_SCREEN_NOTCH_SUPPORT | PFLAG_SCREEN_NOTCH_SUPPORT_ON_MIUI;
                mNotchHeight = DisplayCutoutUtils.getNotchHeightForMIUI(this);
                DisplayCutoutUtils.setLayoutInDisplayCutoutForMIUI(window, true);
            }
        }

        if (Utils.isLayoutRtl(decorView)) {
            getSwipeBackLayout().setEnabledEdges(SwipeBackLayout.EDGE_RIGHT);
        }
        setFullscreenMode(mVideoView.isInFullscreenMode());

        mHandler = decorView.getHandler();

        final boolean notchSupportOnEMUI = (mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI) != 0;
        final boolean notchSupportOnMIUI = (mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT_ON_MIUI) != 0;
        if (notchSupportOnEMUI || notchSupportOnMIUI) {
            mNotchSwitchObserver = new ScreenNotchSwitchObserver(mHandler, this,
                    notchSupportOnEMUI, notchSupportOnMIUI) {
                @Override
                public void onNotchChange(boolean selfChange, boolean hidden) {
                    //noinspection DoubleNegation
                    if (hidden != ((mPrivateFlags & PFLAG_SCREEN_NOTCH_HIDDEN) != 0)) {
                        mPrivateFlags ^= PFLAG_SCREEN_NOTCH_HIDDEN;
                        resizeVideoView();
                    }
                }
            };
            mNotchSwitchObserver.startObserver();
        }

        mRotationObserver = new RotationObserver(mHandler, this) {
            @Override
            public void onRotationChange(boolean selfChange, boolean enabled) {
                //noinspection DoubleNegation
                if (enabled != ((mPrivateFlags & PFLAG_DEVICE_SCREEN_ROTATION_ENABLED) != 0)) {
                    mPrivateFlags ^= PFLAG_DEVICE_SCREEN_ROTATION_ENABLED;
                }
            }
        };
        mOnOrientationChangeListener = new OnOrientationChangeListener(this, mDeviceOrientation) {
            @Override
            public void onOrientationChange(int orientation) {
                if (orientation != SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                    if (mVideoWidth == 0 && mVideoHeight == 0) {
                        mOnOrientationChangeListener.setOrientation(mDeviceOrientation);
                        return;
                    }
                    mDeviceOrientation = orientation;
                    changeScreenOrientationIfNeeded(orientation);
                }
            }
        };
        setAutoRotationEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPrivateFlags |= PFLAG_STOPPED;

        // Saves the video progress when current Activity is put into background
        if (!isFinishing()) {
            mPresenter.recordCurrVideoProgress();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || !isInPictureInPictureMode()) {
            if (mNotchSwitchObserver != null) {
                mNotchSwitchObserver.stopObserver();
            }
            setAutoRotationEnabled(false);
        }

        mVideoPlayer.closeVideo();


        MyUtils.hideKeyboard(context, txtInput);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        mPresenter.recordCurrVideoProgressAndSetResult();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // finish() does not remove the activity in PIP mode from the recents stack.
            // Only finishAndRemoveTask() does this.
            finishAndRemoveTask();
//            super.finish();
        } else {
            super.finish();
        }
        if (!isPipMode) {
            reOpenPreviousActivity();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private boolean isPipMode = false;

    private void reOpenPreviousActivity() {
//        MH01_MainActivity activity = getPreviousActivity();

        //neu man hinh Group ko ton tai thi moi mo lai man hinh Main
        if(!GroupDetailActivity.isExists) {
            Intent intent = new Intent(context, MH01_MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachFromView(this);
        if (sActivityInPiP != null && sActivityInPiP.get() == this) {
            sActivityInPiP.clear();
            sActivityInPiP = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mHideLockUnlockOrientationButtonRunnable, null);
        }

        ////
        if (receiver != null) unregisterReceiver(receiver);
        MyUtils.hideKeyboard(VideoActivityPipDetail.this);
        if (realm != null) {
            realm.close();
        }
        MyApplication.imgItem = null;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                && sStatusHeightInLandscapeOfNotchSupportDevice == 0) {
            sStatusHeightInLandscapeOfNotchSupportDevice = SystemBarUtils.getStatusHeight(this);
            if (mVideoView.isInFullscreenMode()) {
                mVideoView.setFullscreenMode(true, sStatusHeightInLandscapeOfNotchSupportDevice);
            }
        }
    }

    private void setScreenOrientationLocked(boolean locked) {
        if (locked) {
            mPrivateFlags |= PFLAG_SCREEN_ORIENTATION_LOCKED;
            mLockUnlockOrientationButton.setImageResource(R.drawable.ic_unlock_white_24dp);
            mLockUnlockOrientationButton.setContentDescription(mUnlockOrientation);
        } else {
            mPrivateFlags &= ~PFLAG_SCREEN_ORIENTATION_LOCKED;
            mLockUnlockOrientationButton.setImageResource(R.drawable.ic_lock_white_24dp);
            mLockUnlockOrientationButton.setContentDescription(mLockOrientation);
            // Unlock to set the screen orientation to the current device orientation
            changeScreenOrientationIfNeeded(mDeviceOrientation);
        }
    }

    @Synthetic
    void showLockUnlockOrientationButton(boolean show) {
        mLockUnlockOrientationButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setAutoRotationEnabled(boolean enabled) {
        if (enabled) {
            mRotationObserver.startObserver();
            mOnOrientationChangeListener.setEnabled(true);
        } else {
            mOnOrientationChangeListener.setEnabled(false);
            mRotationObserver.stopObserver();
        }
    }

    @Synthetic
    void changeScreenOrientationIfNeeded(int orientation) {
        switch (mScreenOrientation) {
            case SCREEN_ORIENTATION_PORTRAIT:
                if ((mPrivateFlags & PFLAG_DEVICE_SCREEN_ROTATION_ENABLED) != 0
                        && !mVideoView.isLocked()) {
                    if ((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_LOCKED) != 0) {
                        final boolean fullscreen = orientation != SCREEN_ORIENTATION_PORTRAIT;
                        if (fullscreen == mVideoView.isInFullscreenMode()) {
                            return;
                        }
                        break;
                    }

                    if ((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE) != 0) {
                        final boolean fullscreen = orientation != SCREEN_ORIENTATION_PORTRAIT;
                        if (fullscreen == mVideoView.isInFullscreenMode()) {
                            return;
                        }
                        setFullscreenMode(fullscreen);
                    } else {
                        if (orientation == SCREEN_ORIENTATION_PORTRAIT) {
                            return;
                        }
                        mScreenOrientation = orientation;
                        setRequestedOrientation(orientation);
                        setFullscreenMode(true);
                    }
                    break;
                }
                return;
            case SCREEN_ORIENTATION_LANDSCAPE:
            case SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                if (mScreenOrientation == orientation) {
                    return;
                }
                if (mVideoView.isLocked()) {
                    if (orientation == SCREEN_ORIENTATION_PORTRAIT) {
                        return;
                    }
                } else if ((mPrivateFlags & PFLAG_DEVICE_SCREEN_ROTATION_ENABLED) != 0) {
                    if ((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_LOCKED) != 0
                            && orientation == SCREEN_ORIENTATION_PORTRAIT) {
                        break;
                    }
                } else if (orientation == SCREEN_ORIENTATION_PORTRAIT) {
                    return;
                }

                mScreenOrientation = orientation;
                setRequestedOrientation(orientation);

                final boolean fullscreen = orientation != SCREEN_ORIENTATION_PORTRAIT;
                if (fullscreen == mVideoView.isInFullscreenMode()) {
                    //@formatter:off
                    if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) == 0
                            || (mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI) != 0
                            && (mPrivateFlags & PFLAG_SCREEN_NOTCH_HIDDEN) != 0) {
                        //@formatter:on
                        if (mVideoView.isControlsShowing()) {
                            mVideoView.showControls(true, false);
                        }
                        return;
                    }
                    setFullscreenMode(fullscreen);
                    return;
                }
                setFullscreenMode(fullscreen);
                break;
        }

        showLockUnlockOrientationButton(true);
        mHandler.removeCallbacks(mHideLockUnlockOrientationButtonRunnable);
        mHandler.postDelayed(mHideLockUnlockOrientationButtonRunnable,
                DELAY_TIME_HIDE_LOCK_UNLOCK_ORIENTATION_BUTTON);
    }

    @Synthetic
    void setFullscreenMode(boolean fullscreen) {
        // Disable 'swipe back' in full screen mode
        getSwipeBackLayout().setGestureEnabled(!fullscreen);

        showSystemBars(!fullscreen);
        //@formatter:off
        mVideoView.setFullscreenMode(fullscreen,
                fullscreen && (
                        (mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) == 0
                                || (mPrivateFlags & PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE) == 0
                ) ? ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) == 0) ?
                        sStatusHeight : sStatusHeightInLandscapeOfNotchSupportDevice
                        : 0);
        //@formatter:on
        if (mVideoView.isControlsShowing()) {
            mVideoView.showControls(true, false);
        }
        resizeVideoView();

        mPrivateFlags = mPrivateFlags & ~PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN
                | (fullscreen ? PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN : 0);
    }

    @Synthetic
    void setFullscreenModeManually(boolean fullscreen) {
        if (mVideoView.isInFullscreenMode() == fullscreen) {
            return;
        }
        if ((mPrivateFlags & PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE) == 0) {
            mScreenOrientation = fullscreen ?
                    mDeviceOrientation == SCREEN_ORIENTATION_PORTRAIT
                            ? SCREEN_ORIENTATION_LANDSCAPE : mDeviceOrientation
                    : SCREEN_ORIENTATION_PORTRAIT;
            setRequestedOrientation(mScreenOrientation);
        }
        setFullscreenMode(fullscreen);
    }

    @Synthetic
    void resizeVideoView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInPictureInPictureMode()) {
            setVideoViewSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            return;
        }

        switch (mScreenOrientation) {
            case SCREEN_ORIENTATION_PORTRAIT:
                final boolean layoutIsFullscreen = mVideoView.isInFullscreenMode();
                final boolean lastLayoutIsFullscreen =
                        (mPrivateFlags & PFLAG_LAST_VIDEO_LAYOUT_IS_FULLSCREEN) != 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                        && (mPrivateFlags & PFLAG_SCREEN_ORIENTATION_PORTRAIT_IMMUTABLE) != 0
                        && layoutIsFullscreen != lastLayoutIsFullscreen) {
                    TransitionManager.beginDelayedTransition(mVideoView, new ChangeBounds());
                }

                if (layoutIsFullscreen) {
                    setVideoViewSize(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) != 0) {
                        /*
                         * setPadding () has no effect on DrawerLayout, such as:
                         *
                         * mVideoView.setPadding(0, mNotchHeight, 0, 0);
                         */
                        for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                            UiUtils.setViewMargins(mVideoView.getChildAt(i), 0, mNotchHeight, 0, 0);
                        }
                    }
                } else {
                    final int screenWidth = MyApplication.getInstance(this).getRealScreenWidthIgnoreOrientation();
                    // portrait w : h = 16 : 9
                    final int minLayoutHeight = Utils.roundFloat((float) screenWidth / 16f * 9f);

                    setVideoViewSize(screenWidth, minLayoutHeight);
                    if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) != 0) {
//                        mVideoView.setPadding(0, 0, 0, 0);
                        for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                            UiUtils.setViewMargins(mVideoView.getChildAt(i), 0, 0, 0, 0);
                        }
                    }
                }
                break;
            case SCREEN_ORIENTATION_LANDSCAPE:
                setVideoViewSize(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI) != 0
                        && (mPrivateFlags & PFLAG_SCREEN_NOTCH_HIDDEN) != 0) {
//                    mVideoView.setPadding(0, 0, 0, 0);
                    for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                        UiUtils.setViewMargins(mVideoView.getChildAt(i), 0, 0, 0, 0);
                    }
                } else if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) != 0) {
//                    mVideoView.setPadding(mNotchHeight, 0, 0, 0);
                    for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                        //noinspection SuspiciousNameCombination
                        UiUtils.setViewMargins(mVideoView.getChildAt(i), mNotchHeight, 0, 0, 0);
                    }
                }
                break;
            case SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                setVideoViewSize(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT_ON_EMUI) != 0
                        && (mPrivateFlags & PFLAG_SCREEN_NOTCH_HIDDEN) != 0) {
//                    mVideoView.setPadding(0, 0, 0, 0);
                    for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                        UiUtils.setViewMargins(mVideoView.getChildAt(i), 0, 0, 0, 0);
                    }
                } else if ((mPrivateFlags & PFLAG_SCREEN_NOTCH_SUPPORT) != 0) {
//                    mVideoView.setPadding(0, 0, mNotchHeight, 0);
                    for (int i = 0, childCount = mVideoView.getChildCount(); i < childCount; i++) {
                        //noinspection SuspiciousNameCombination
                        UiUtils.setViewMargins(mVideoView.getChildAt(i), 0, 0, mNotchHeight, 0);
                    }
                }
                break;
        }
    }

    private void setVideoViewSize(int layoutWidth, int layoutHeight) {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        lp.width = layoutWidth;
        lp.height = layoutHeight;
        mVideoView.setLayoutParams(lp);
    }

    private void showSystemBars(boolean show) {
        Window window = getWindow();
        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mStatusBarView.setVisibility(View.VISIBLE);

                View decorView = window.getDecorView();
                decorView.setOnSystemUiVisibilityChangeListener(null);
                // Status bar is set to transparent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    SystemBarUtils.setStatusBackgroundColor(window, Color.TRANSPARENT);
                } else {
                    SystemBarUtils.setTranslucentStatus(window, true);
                }
                decorView.setSystemUiVisibility(
                        (decorView.getSystemUiVisibility()
                                // Makes the content view appear under the status bar
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                // Status bar and navigation bar appear
                        ) & ~(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY));
            } else {
                SystemBarUtils.showSystemBars(window, true);
            }
        } else {
            if (mStatusBarView != null) {
                mStatusBarView.setVisibility(View.GONE);
            }

            SystemBarUtils.showSystemBars(window, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                final View decorView = window.getDecorView();
                final int visibility = decorView.getSystemUiVisibility();
                decorView.setOnSystemUiVisibilityChangeListener(newVisibility -> {
                    if (newVisibility != visibility) {
                        decorView.setSystemUiVisibility(visibility);
                    }
                });
            }
        }
    }

    /**
     * Update the action items in Picture-in-Picture mode.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @Synthetic
    void updatePictureInPictureActions(int pipActions) {
        List<RemoteAction> actions = new LinkedList<>();
        if ((pipActions & PIP_ACTION_FAST_REWIND) != 0) {
            actions.add(createPipAction(R.drawable.ic_fast_rewind_white_24dp,
                    mFastRewind, PIP_ACTION_FAST_REWIND, REQUEST_FAST_REWIND));
        }
        if ((pipActions & PIP_ACTION_PLAY) != 0) {
            actions.add(createPipAction(R.drawable.ic_play_white_24dp,
                    mPlay, PIP_ACTION_PLAY, REQUEST_PLAY));

        } else if ((pipActions & PIP_ACTION_PAUSE) != 0) {
            actions.add(createPipAction(R.drawable.ic_pause_white_24dp,
                    mPause, PIP_ACTION_PAUSE, REQUEST_PAUSE));
        }
        if ((pipActions & PIP_ACTION_FAST_FORWARD) != 0) {
            actions.add(createPipAction(R.drawable.ic_fast_forward_white_24dp,
                    mFastForward, PIP_ACTION_FAST_FORWARD, REQUEST_FAST_FORWARD));
        } else {
            RemoteAction action = createPipAction(R.drawable.ic_fast_forward_lightgray_24dp,
                    mFastForward, PIP_ACTION_FAST_FORWARD, REQUEST_FAST_FORWARD);
            action.setEnabled(false);
            actions.add(action);
        }
        mPipParamsBuilder.setActions(actions);

        // This is how you can update action items (or aspect ratio) for Picture-in-Picture mode.
        setPictureInPictureParams(mPipParamsBuilder.build());
    }

    /**
     * Create an pip action item in Picture-in-Picture mode.
     *
     * @param iconId      The icon to be used.
     * @param title       The title text.
     * @param pipAction   The type for the pip action. May be {@link #PIP_ACTION_PLAY},
     *                    {@link #PIP_ACTION_PAUSE},
     *                    {@link #PIP_ACTION_FAST_FORWARD},
     *                    or {@link #PIP_ACTION_FAST_REWIND}.
     * @param requestCode The request code for the {@link PendingIntent}.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private RemoteAction createPipAction(
            @DrawableRes int iconId, String title, int pipAction, int requestCode) {
        // This is the PendingIntent that is invoked when a user clicks on the action item.
        // You need to use distinct request codes for play, pause, fast forward, and fast rewind,
        // or the PendingIntent won't be properly updated.
        PendingIntent intent = PendingIntent.getBroadcast(
                this,
                requestCode,
                new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_PIP_ACTION, pipAction),
                0);
        Icon icon = IconCompat.createWithResource(this, iconId).toIcon(this);
        return new RemoteAction(icon, title, title, intent);
    }

    @SuppressLint("SwitchIntDef")
    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);

        mVideoView.onMinimizationModeChange(isInPictureInPictureMode);

        if (isInPictureInPictureMode) {

            int actions = PIP_ACTION_FAST_REWIND;
            final int playbackState = mVideoPlayer.getPlaybackState();
            switch (playbackState) {
                case IVideoPlayer.PLAYBACK_STATE_PLAYING:
                    actions |= PIP_ACTION_PAUSE | PIP_ACTION_FAST_FORWARD;
                    break;
                case IVideoPlayer.PLAYBACK_STATE_COMPLETED:
                    actions |= PIP_ACTION_PLAY
                            | (mVideoView.canSkipToNext() ? PIP_ACTION_FAST_FORWARD : 0);
                    break;
                default:
                    actions |= PIP_ACTION_PLAY | PIP_ACTION_FAST_FORWARD;
                    break;
            }
            updatePictureInPictureActions(actions);

            // Starts receiving events from action items in PiP mode.
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || !ACTION_MEDIA_CONTROL.equals(intent.getAction())) {
                        return;
                    }

                    // This is where we are called back from Picture-in-Picture action items.
                    final int action = intent.getIntExtra(EXTRA_PIP_ACTION, 0);
                    switch (action) {
                        case PIP_ACTION_PLAY:
                            mVideoPlayer.play(true);
                            break;
                        case PIP_ACTION_PAUSE:
                            mVideoPlayer.pause(true);
                            break;
                        case PIP_ACTION_FAST_REWIND: {
                            mVideoPlayer.fastRewind(true);
                        }
                        break;
                        case PIP_ACTION_FAST_FORWARD: {
                            mVideoPlayer.fastForward(true);
                        }
                        break;
                    }
                }
            };
            registerReceiver(mReceiver, new IntentFilter(ACTION_MEDIA_CONTROL));

            sActivityInPiP = new WeakReference<>(this);

            if (mNotchSwitchObserver != null) {
                mNotchSwitchObserver.stopObserver();
            }
            setAutoRotationEnabled(false);

            mStatusBarView.setVisibility(View.GONE);
            showLockUnlockOrientationButton(false);
            mVideoProgressInPiP.setVisibility(View.VISIBLE);
            mRefreshVideoProgressInPiPTask = new RefreshVideoProgressInPiPTask();
            mRefreshVideoProgressInPiPTask.execute();

            mVideoView.showControls(false, false);
            mVideoView.setClipViewBounds(true);
            resizeVideoView();

            mOnPipLayoutChangeListener = new View.OnLayoutChangeListener() {
                static final float RATIO_TOLERANCE_PIP_LAYOUT_SIZE = 5.0f / 3.0f;

                final float ratioOfProgressHeightToVideoSize;
                final int progressMinHeight;
                final int progressMaxHeight;

                float cachedVideoAspectRatio;
                int cachedSize = -1;

                static final String TAG = "VideoActivityInPIP";

                /* anonymous class initializer */ {
                    // 1dp -> 2.75px (5.5inch  w * h = 1080 * 1920)
                    final float dp = getResources().getDisplayMetrics().density;
                    ratioOfProgressHeightToVideoSize = 1.0f / (12121.2f * dp); // 1 : 33333.3 (px)
                    progressMinHeight = Utils.roundFloat(dp * 1.8f); // 5.45px -> 5px
                    progressMaxHeight = Utils.roundFloat(dp * 2.5f); // 7.375px -> 7px
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "ratioOfProgressHeightToVideoSize = " + ratioOfProgressHeightToVideoSize
                                + "    " + "progressMinHeight = " + progressMinHeight
                                + "    " + "progressMaxHeight = " + progressMaxHeight);
                    }
                }

                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (mVideoWidth == 0 || mVideoHeight == 0) return;

                    final float videoAspectRatio = (float) mVideoWidth / mVideoHeight;
                    int width = right - left;
                    int height = Utils.roundFloat(width / videoAspectRatio);
                    MyUtils.log("width video = " + width + " and mVideoWidth = " + mVideoWidth);
                    if (width > mVideoWidth) {
                        width = mVideoWidth;
                        height = Utils.roundFloat(width / videoAspectRatio);
//                        setVideoViewSize(width, height);
                    }

                    final int size = width * height;
                    final float sizeRatio = (float) size / cachedSize;

                    if (videoAspectRatio != cachedVideoAspectRatio
                            || sizeRatio > RATIO_TOLERANCE_PIP_LAYOUT_SIZE
                            || sizeRatio < 1.0f / RATIO_TOLERANCE_PIP_LAYOUT_SIZE) {
                        final int progressHeight = Math.max(
                                progressMinHeight,
                                Math.min(progressMaxHeight,
                                        Utils.roundFloat(size * ratioOfProgressHeightToVideoSize)));
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "sizeRatio = " + sizeRatio
                                    + "    " + "progressHeight = " + progressHeight
                                    + "    " + "size / 1.8dp = " + size / progressMinHeight
                                    + "    " + "size / 2.5dp = " + size / progressMaxHeight);
                        }

                        mPipParamsBuilder.setAspectRatio(
                                new Rational(width, height + progressHeight));
                        setPictureInPictureParams(mPipParamsBuilder.build());

                        cachedVideoAspectRatio = videoAspectRatio;
                        cachedSize = size;
                    }
                }
            };
            mVideoView.addOnLayoutChangeListener(mOnPipLayoutChangeListener);
        } else {

            // We are out of PiP mode. We can stop receiving events from it.
            unregisterReceiver(mReceiver);
            mReceiver = null;

            sActivityInPiP.clear();
            sActivityInPiP = null;

            mRefreshVideoProgressInPiPTask.cancel();
            mRefreshVideoProgressInPiPTask = null;

            mVideoView.removeOnLayoutChangeListener(mOnPipLayoutChangeListener);
            mOnPipLayoutChangeListener = null;

            // We closed the picture-in-picture window by clicking the 'close' button
            if ((mPrivateFlags & PFLAG_STOPPED) != 0) {
                finish();
                return;
            }

            if (mNotchSwitchObserver != null) {
                mNotchSwitchObserver.startObserver();
            }
            setAutoRotationEnabled(true);

            mStatusBarView.setVisibility(View.VISIBLE);
            mVideoProgressInPiP.setVisibility(View.GONE);

            mVideoView.showControls(true);
            mVideoView.setClipViewBounds(false);
            resizeVideoView();
        }
    }

    @Override
    public void showUserCancelableSnackbar(@NonNull CharSequence text, int duration) {
        UiUtils.showUserCancelableSnackbar(mVideoView, text, duration);
    }

    @Override
    public void notifyPlaylistSelectionChanged(int oldPosition, int position, boolean checkNewItemVisibility) {
        RecyclerView playlist = mVideoView.findViewById(R.id.rv_playlist);
        //noinspection rawtypes
        RecyclerView.Adapter adapter = playlist.getAdapter();
        //noinspection ConstantConditions
        adapter.notifyItemChanged(oldPosition,
                IVideoPresenter.PLAYLIST_ADAPTER_PAYLOAD_VIDEO_PROGRESS_CHANGED
                        | IVideoPresenter.PLAYLIST_ADAPTER_PAYLOAD_HIGHLIGHT_ITEM_IF_SELECTED);
        adapter.notifyItemChanged(position,
                IVideoPresenter.PLAYLIST_ADAPTER_PAYLOAD_VIDEO_PROGRESS_CHANGED
                        | IVideoPresenter.PLAYLIST_ADAPTER_PAYLOAD_HIGHLIGHT_ITEM_IF_SELECTED);
        if (checkNewItemVisibility) {
            RecyclerView.LayoutManager lm = playlist.getLayoutManager();
            if (lm instanceof LinearLayoutManager) {
                LinearLayoutManager llm = (LinearLayoutManager) lm;
                if (llm.findFirstCompletelyVisibleItemPosition() > position
                        || llm.findLastCompletelyVisibleItemPosition() < position) {
                    llm.scrollToPosition(position);
                }
            } else if (lm instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) lm;

                int minFirstCompletelyVisiblePosition = 0;
                for (int i : sglm.findFirstCompletelyVisibleItemPositions(null)) {
                    minFirstCompletelyVisiblePosition = Math.min(minFirstCompletelyVisiblePosition, i);
                }
                if (minFirstCompletelyVisiblePosition > position) {
                    sglm.scrollToPosition(position);
                    return;
                }

                int maxLastCompletelyVisiblePosition = 0;
                for (int i : sglm.findLastCompletelyVisibleItemPositions(null)) {
                    maxLastCompletelyVisiblePosition = Math.max(maxLastCompletelyVisiblePosition, i);
                }
                if (maxLastCompletelyVisiblePosition < position) {
                    sglm.scrollToPosition(position);
                }
            }
        }
    }

    @NonNull
    @Override
    public IVideoView.PlaylistViewHolder newPlaylistViewHolder(@NonNull ViewGroup parent) {
        return new PlaylistViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_video_play_list, parent, false));
    }

    private static final class PlaylistViewHolder extends IVideoView.PlaylistViewHolder {
        final ImageView videoImage;
        final TextView videoNameText;
        final TextView videoProgressDurationText;

        PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            videoImage = itemView.findViewById(R.id.image_video);
            videoNameText = itemView.findViewById(R.id.text_videoName);
            videoProgressDurationText = itemView.findViewById(R.id.text_videoProgressAndDuration);
            VideoUtils2.adjustVideoThumbView(videoImage);
        }

        @Override
        public void loadVideoThumb(@NonNull Video video) {
            VideoUtils2.loadVideoThumbIntoImageView(videoImage, video);

        }

        @Override
        public void cancelLoadingVideoThumb() {
            Glide.with(videoImage.getContext()).clear(videoImage);
        }

        @Override
        public void setVideoTitle(@Nullable String text) {
            videoNameText.setText(text);
        }

        @Override
        public void setVideoProgressAndDurationText(@Nullable String text) {
            videoProgressDurationText.setText(text);
        }

        @Override
        public void highlightItemIfSelected(boolean selected) {
            itemView.setSelected(selected);
            videoNameText.setTextColor(selected ? Consts.COLOR_ACCENT : Color.WHITE);
            videoProgressDurationText.setTextColor(
                    selected ? Consts.COLOR_ACCENT : 0x80FFFFFF);
        }
    }

    // --------------- Saved Instance State ------------------------

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.saveData(outState);


        outState.putParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST, binding.getmAdapter().getAllItem());
        outState.putParcelable(ImageItem.IMAGE_ITEM, item);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private Activity context = this;
    private int avatarSize = 0;
    private int widthImage = 0, heightImage = 0;
    private boolean isMyPost = false;
    private Realm realm;
    private ImageItem item;
    private User user;
    private DetailBinding binding;
    private BroadcastReceiver receiver;

    private void initUI() {
        realm = Realm.getDefaultInstance();
        widthImage = MyUtils.getScreenWidth(context);// - roundCorner * 2;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_small);
        img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));

        TinyDB db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }

        txtViewAllComment.setVisibility(View.GONE);
        linearCommentPreview.setVisibility(View.GONE);

        item = MyApplication.imgItem;
        if (item != null) {

            if (user != null && item.getOwner() != null) {
                isMyPost = user.getUserId() == item.getOwner().getUserId();
            }

            heightImage = item.getNeedHeightImage(widthImage);
//            if (img2 != null)
//                img2.setLayoutParams(new FrameLayout.LayoutParams(widthImage, heightImage));

            if (user.getUserId() == item.getOwner().getUserId()) {
                btnFollow.setVisibility(View.GONE);
            } else {
                btnFollow.setVisibility(View.VISIBLE);
            }

            binding = new DetailBinding(context,
                    user,
                    item,
                    realm,
                    isMyPost,
                    txt3,
                    imgLike,
                    imgSave,
                    txtInput,
                    rv,
                    linearReply);
            setTitleBar();

            setUI();
        }
    }

    ///////////////////////////////////////////////////
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgShop)
    ImageView imgShop;
    @BindView(R.id.relativeChat)
    RelativeLayout relativeChat;
    @BindView(R.id.txtNumber)
    TextView txtNumber;

    private void setTitleBar() {
        binding.setTitleBar(imgBack, imgShop, relativeChat, txtNumber, context);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMMENT
    @BindView(R.id.recyclerView1)
    RecyclerView rv;
    @BindView(R.id.editText1)
    SocialAutoCompleteTextView txtInput;
    @BindView(R.id.ripple1)
    LinearLayout rippleSend;

    private void initComment() {
        initAdapter();

        //todo scroll
        /*if (scrollView != null) {
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    MyUtils.hideKeyboard(context);
                }
            });
        }*/

    }

    private void initAdapter() {
        binding.initAdapter(rippleSend);
        binding.initHastag();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) {
            binding.getComments(item.getImageItemId(), 0, 0);

        } else {

            MyUtils.initCookie(context);
            item = savedInstanceState.getParcelable(ImageItem.IMAGE_ITEM);
            ArrayList<ImageComment> list = savedInstanceState.getParcelableArrayList(ImageComment.IMAGE_COMMENT_ARRAY_LIST);
            if (list != null && list.size() > 0) {
                binding.getmAdapter().clear();
                binding.getmAdapter().addListItems(list);
//                list_empty.setVisibility(View.GONE);
            } else {
//                list_empty.setVisibility(View.VISIBLE);
            }

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String ACTION_UPDATE_ITEM = "ACTION_UPDATE_ITEM_2";
    public static final String ACTION_UPDATE_STATE_FOLLOW = "ACTION_UPDATE_STATE_FOLLOW_2";
    public static final String ACTION_POST_DELETED = "ACTION_POST_DELETED_";
    public static final String ACTION_COMMENT_DELETED = "ACTION_COMMENT_DELETED_";
    public static final String ACTION_FINISH = "ACTION_FINISH_PIP_VIDEO_DETAIL";

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_ITEM)) {
                    ImageItem image = MyApplication.itemReturn;
                    if (image != null) {
                        item = image;
                        setUI();
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_UPDATE_STATE_FOLLOW)) {
                    binding.setButtonUI(btnFollow);
                } else if (intent.getAction().equalsIgnoreCase(ACTION_POST_DELETED)) {
                    Bundle b = intent.getExtras();
                    if (b != null) {
                        long imageItemId = b.getLong(ImageItem.IMAGE_ITEM_ID);
                        if (item != null && imageItemId > 0) {
                            if (item.getImageItemId() == imageItemId) {
                                finish();
                            }
                        }
                    }
                } else if (intent.getAction().equalsIgnoreCase(ACTION_COMMENT_DELETED)) {
                    binding.updateCommentCount();
                } else if (intent.getAction().equalsIgnoreCase(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD)) {
                    ChatUtils.setChatUnreadNumber(txtNumber);
                } else if (intent.getAction().equalsIgnoreCase(ACTION_FINISH)) {
                    finish();
                }


            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MH01_MainActivity.ACTION_SET_NUMBER_CHAT_UNREAD);
        filter.addAction(ACTION_UPDATE_ITEM);
        filter.addAction(ACTION_UPDATE_STATE_FOLLOW);
        filter.addAction(ACTION_POST_DELETED);
        filter.addAction(ACTION_COMMENT_DELETED);
        filter.addAction(ACTION_FINISH);

        registerReceiver(receiver, filter);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // each data imageItem is just a string in this case
    @BindView(R.id.textView1)
    TextView txt1;
    @BindView(R.id.textView2)
    TextView txt2;
    @BindView(R.id.textView3)
    TextView txt3;
    @BindView(R.id.textView5)
    ShowMoreTextView txt5;
    @BindView(R.id.textView6)
    TextView txt6;

    @BindView(R.id.imageView1)
    ImageView img1;
    /*@BindView(R.id.imageView2)
    ImageView img2;*/
    @BindView(R.id.imgLike)
    CheckBox imgLike;
    @BindView(R.id.imgMenu2)
    ImageView imgMenu;


    @BindView(R.id.linearLayout3)
    LinearLayout linear3;

    @BindView(R.id.vBgLike)
    View vBgLike;
    @BindView(R.id.ivLike)
    ImageView ivLike;

    @BindView(R.id.linearLike)
    LinearLayout linearLike;
    @BindView(R.id.linearComment)
    LinearLayout linearComment;
    @BindView(R.id.linearShare)
    LinearLayout linearShare;

    public @BindView(R.id.linearSave)
    LinearLayout linearSave;
    public @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.button1)
    AppCompatButton btnFollow;

    //    @BindView(R.id.scrollViewDetail)
//    NestedScrollView scrollView;
    @BindView(R.id.imgOwner)
    ImageView imgOwner;

    //comment preview
    @BindView(R.id.txtViewAllComment)
    TextView txtViewAllComment;
    @BindView(R.id.linearCommentPreview)
    LinearLayout linearCommentPreview;

    @BindView(R.id.imgVip)
    ImageView imgVip;

    /////////////////////////////////////////////////////////////////////////////////////////////
    public void setUI() {
        if (item != null) {
            binding.setUserVip(avatarSize, img1, imgVip);
            if (ImageItem.ITEM_TYPE_FACEBOOK.equals(item.getItemType())) {
                //neu chi co 1 video thi hien thi theo instagram video
                if (item.getItemContent().size() == 1 && item.isVideo()) {
                    item.setItemType(ImageItem.ITEM_TYPE_INSTAGRAM);
                    initTelegramPost();
                } else {
//                    initFacebookPost();
                }

            } else {//instagram
                initTelegramPost();
            }

            //neu like,comment,share deu = 0 thi an
            binding.setLikeShareComment();
            binding.setContent(txt1, txt2, txt3, txt6, imgMenu, linearLike, imgLike, linearSave, null, linearComment, linearShare, img1, ivLike);
            /////////////////////////////////////////////////////////////////////////
        }

        binding.setButtonUI(btnFollow);
    }

    private void initTelegramPost() {
        //neu description
        linear3.setVisibility(View.VISIBLE);
        HolderUtils.setDescription(item.getDescription(), txt5, context);

        /*if (item.isVideo()) {
            initVideoPost();
        } else {
            initImagePost();
        }*/
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //EVENT BUS/////////////////////////////////////////////////////////////////////////////////////
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventImageComment event) {
        if (event != null && event.getComment() != null) {
            initReplyLayout(event.getComment());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //REPLY/////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.linearReply)
    LinearLayout linearReply;
    @BindView(R.id.txtReply1)
    TextView txtReply1;
    @BindView(R.id.txtReply2)
    TextView txtReply2;
    @BindView(R.id.imgReplyClose)
    ImageView imgReplyClose;

    private void initReplyLayout(ImageComment comment) {
        binding.initReplyLayout(comment, linearReply, imgReplyClose, txtReply1, txtReply2);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////
    //FROM USER//////////////////////////////////////////////////////////
    private void initData() {
        initUI();
        initComment();
        registerReceiver();
    }


    @BindView(R.id.relative1)
    LinearLayout relative1;//header of row
    @BindView(R.id.linearTop)
    LinearLayout linearTop;//toolbar
    @BindView(R.id.linearInputComment)
    LinearLayout linearInputComment;//toolbar
    @BindView(R.id.linearFooterRow)
    LinearLayout linearFooterRow;//toolbar

    private void showHideLayout(boolean isShow) {
        if (isShow) {
            if (binding.replyToComment != null) {
                MyUtils.show(linearTop, linearComment, linear3, rv, linearReply, linearInputComment, relative1, linearFooterRow);
            } else {
                MyUtils.show(linearTop, linearComment, linear3, rv/*, linearReply*/, linearInputComment, relative1, linearFooterRow);
            }
        } else {
            MyUtils.hide(linearTop, linearComment, linear3, rv, linearReply, linearInputComment, relative1, linearFooterRow);
        }
    }


}