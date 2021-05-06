package com.topceo.viewholders;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.master.exoplayer.ExoPlayerHelper;
import com.master.exoplayer.MasterExoPlayer;
import com.topceo.R;
import com.topceo.adapter.FeedAdapter;
import com.topceo.autoplayvideo.CameraAnimation;
import com.topceo.autoplayvideo.Video;
import com.topceo.autoplayvideo.VideoView;
import com.topceo.config.VideoListConfig;
import com.topceo.objects.image.ImageItem;
import com.topceo.utils.MyUtils;

import org.jetbrains.annotations.Nullable;

public class ViewHolder6_Instagram_Video extends MyVideoHolder {
    int widthImage;

    public ViewHolder6_Instagram_Video(View v, int avatarSize, int widthImage) {
        super(v, avatarSize);
        this.widthImage = widthImage;
        initInstagramPost(v);
    }

    //IMAGE
    FrameLayout frameLayoutVideo;
    public MasterExoPlayer vvInfo;
    ImageView ivInfo;
    ImageView imgSound;
    CameraAnimation ivCameraAnimation;

    private void initInstagramPost(View view) {
        if (view != null) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.fragment_1_row_video, null);
            frameLayoutVideo = (FrameLayout) v.findViewById(R.id.frameLayoutVideo);
            vvInfo = (MasterExoPlayer) v.findViewById(R.id.vvInfo);
            ivInfo = (ImageView) v.findViewById(R.id.ivInfo);
            imgSound = (ImageView) v.findViewById(R.id.imgSound);
            ivCameraAnimation = (CameraAnimation) v.findViewById(R.id.ivCameraAnimation);

//            ivInfo.setLayoutParams(new FrameLayout.LayoutParams(widthImage, widthImage));

            //add to parent container
            linearContainer.addView(v);
        }
    }

    public void bindData(ImageItem item, int position, FeedAdapter adapter) {
        if (item != null) {

            int heightImage = item.getNeedHeightImage(widthImage);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthImage, heightImage);
//                params.setMargins(roundCorner, 0, roundCorner, roundCorner);
            vvInfo.setLayoutParams(params);
            ivInfo.setLayoutParams(params);

            //reset row line
            txt5.reset();

            //init basic view
            initViewBasic(item, position, adapter);

            //an description bottom
            linear3.setVisibility(View.VISIBLE);
            HolderUtils.setDescription(item.getDescription(), txt5, context);

            //neu la video thi: large chua link video, (medium+small) chua cover
//            vvInfo.setVideo(new Video(item.getImageLarge(), 0));
            vvInfo.setUrl(item.getImageLarge());
            VideoListConfig.Companion.configListener(vvInfo, imgSound, ivCameraAnimation, ivInfo);


            imgSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vvInfo.setMute(!vvInfo.isMute());
                    VideoListConfig.Companion.setMute(vvInfo, imgSound);
                }
            });


            //set hinh
            String img = item.getImageMedium();
            if (!TextUtils.isEmpty(img)) {
                Glide.with(context)
                        .load(img)//images[position%images.length]
//                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(roundCorner)))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .priority(Priority.NORMAL)
                        .override(widthImage, heightImage)
                        .into(ivInfo);
            } else {
                Glide.with(context)
                        .load(R.drawable.no_media)//images[position%images.length]
                        .override(widthImage, heightImage)
                        .into(ivInfo)
                ;
            }

            //icon mute
            frameLayoutVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearComment.performClick();
                }
            });
            txt5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!txt5.isClickMoreLess()) {
                        linearComment.performClick();
                    } else {
                        //reset
                        txt5.setClickMoreLess(false);
                    }

                }
            });


            //
            linearComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //neu la video thi dung phat
                    stopVideo();
                    MyUtils.gotoDetailImage(context, item);
                }
            });
//            playVideo();

        }
    }

    /*private void configListener(MasterExoPlayer vvInfo, ImageView imgSound, CameraAnimation ivCameraAnimation, ImageView ivInfo) {
        vvInfo.setListener(new ExoPlayerHelper.Listener() {
            @Override
            public void onPlayerReady() {
                MyUtils.log("masterplayer onPlayerReady");
                imgSound.setVisibility(View.VISIBLE);
                VideoListConfig.Companion.setMute(vvInfo, imgSound);
            }

            @Override
            public void onStart() {
                MyUtils.log("masterplayer onStart ");
                ivCameraAnimation.setVisibility(View.GONE);
                ivCameraAnimation.stop();
                ivInfo.setVisibility(View.GONE);
            }

            @Override
            public void onStop() {
                MyUtils.log("masterplayer onStop ");
                ivInfo.setVisibility(View.VISIBLE);
                imgSound.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(long l) {
                MyUtils.log("masterplayer progress " + l);
            }

            @Override
            public void onError(@Nullable ExoPlaybackException e) {
                MyUtils.log("masterplayer onError ");
            }

            @Override
            public void onBuffering(boolean b) {
                ivCameraAnimation.setVisibility(View.VISIBLE);
                ivCameraAnimation.start();
                MyUtils.log("masterplayer onBuffering ");
            }

            @Override
            public void onToggleControllerVisible(boolean b) {
                MyUtils.log("masterplayer onToggleControllerVisible ");
            }
        });
    }*/


    /*public void setMute(MasterExoPlayer vvInfo, ImageView imgSound) {
        if (vvInfo != null) {
            if (vvInfo.isMute()) {
                imgSound.setImageResource(R.drawable.ic_volume_off_white_24dp);
            } else {
                imgSound.setImageResource(R.drawable.ic_volume_up_white_24dp);
            }
        }
    }*/

    @Override
    public View getVideoLayout() {
        return vvInfo;
    }

    @Override
    public void playVideo() {

        ivInfo.setVisibility(View.VISIBLE);
        ivCameraAnimation.start();

        //todo mute
        /*vvInfo.play(new VideoView.OnPreparedListener() {
            @Override
            public void onPrepared() {
                ivInfo.setVisibility(View.GONE);
                ivCameraAnimation.stop();

                //refresh giao dien
                if (isMuted) {
                    imgSound.setImageResource(R.drawable.ic_volume_off_white_24dp);
                    vvInfo.getMediaPlayer().setVolume(0, 0);
                } else {
                    imgSound.setImageResource(R.drawable.ic_volume_up_white_24dp);
                    vvInfo.getMediaPlayer().setVolume(1, 1);
                }

                if (!isNeedPlay) {
                    stopVideo();
                }
            }
        });*/
    }

    @Override
    public void stopVideo() {
//        vvInfo.getPlayerView().getPlayer().stop();
        ivInfo.setVisibility(View.VISIBLE);
        ivCameraAnimation.stop();
    }


}
