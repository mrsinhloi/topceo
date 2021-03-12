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
import com.topceo.R;
import com.topceo.autoplayvideo.CameraAnimation;
import com.topceo.autoplayvideo.Video;
import com.topceo.autoplayvideo.VideoView;
import com.topceo.objects.image.ImageItem;
import com.topceo.utils.MyUtils;

public class ViewHolder6_Instagram_Video extends MyVideoHolder {
    int widthImage;

    public ViewHolder6_Instagram_Video(View v, int avatarSize, int widthImage) {
        super(v, avatarSize);
        this.widthImage = widthImage;
        initInstagramPost(v);
    }

    //IMAGE
    FrameLayout frameLayoutVideo;
    VideoView vvInfo;
    ImageView ivInfo;
    ImageView imgSound;
    CameraAnimation ivCameraAnimation;

    private void initInstagramPost(View view) {
        if (view != null) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.fragment_1_row_video, null);
            frameLayoutVideo = (FrameLayout) v.findViewById(R.id.frameLayoutVideo);
            vvInfo = (VideoView) v.findViewById(R.id.vvInfo);
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

            //init basic view
            initViewBasic(item, position, adapter);

            //an description bottom
            linear3.setVisibility(View.VISIBLE);
            HolderUtils.setDescription(item.getDescription(), txt5, context);

            if (adapter != null && adapter.isActivityStop) {
                stopVideo();
            }

            //neu la video thi: large chua link video, (medium+small) chua cover
            isNeedPlay = true;
            vvInfo.setVideo(new Video(item.getImageLarge(), 0));


            imgSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isMuted = !isMuted;
                    setMute(isMuted);
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
            setMute(isMuted);
            frameLayoutVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearComment.performClick();
                }
            });
            txt5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearComment.performClick();
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


    public boolean isNeedPlay = false;
    public boolean isMuted = true;
    public void setMute(boolean isMuted) {
        this.isMuted = isMuted;
        if (vvInfo != null && vvInfo.getMediaPlayer() != null) {
            if (isMuted) {
                vvInfo.getMediaPlayer().setVolume(0, 0);
                imgSound.setImageResource(R.drawable.ic_volume_off_white_24dp);
            } else {
                vvInfo.getMediaPlayer().setVolume(1, 1);
                imgSound.setImageResource(R.drawable.ic_volume_up_white_24dp);
            }

        }
    }

    @Override
    public View getVideoLayout() {
        return vvInfo;
    }
    @Override
    public void playVideo() {

        ivInfo.setVisibility(View.VISIBLE);
        ivCameraAnimation.start();

        vvInfo.play(new VideoView.OnPreparedListener() {
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
        });
    }

    @Override
    public void stopVideo() {
        vvInfo.stop();
        ivInfo.setVisibility(View.VISIBLE);
        ivCameraAnimation.stop();
    }


}
