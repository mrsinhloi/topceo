package com.topceo.viewholders;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.topceo.R;
import com.topceo.adapter.FeedAdapter;
import com.topceo.objects.image.ImageItem;
import com.topceo.utils.MyUtils;

public class ViewHolder6_Instagram_Image extends ViewHolderBasic {
    int widthImage;
    public ViewHolder6_Instagram_Image(View v, int avatarSize, int widthImage) {
        super(v, avatarSize);
        this.widthImage = widthImage;
        initInstagramPost(v);
    }

    //IMAGE
    FrameLayout frameLayoutImage;
    ImageView img2;

    private void initInstagramPost(View view) {
        if (view != null) {
            View v = LayoutInflater.from(view.getContext()).inflate(R.layout.fragment_1_row_image, null);
            frameLayoutImage = (FrameLayout) v.findViewById(R.id.frameLayoutImage);
            img2 = (ImageView) v.findViewById(R.id.imageView2);
            img2.setLayoutParams(new FrameLayout.LayoutParams(widthImage, widthImage));

            //add to parent container
            linearContainer.addView(v);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void bindData(ImageItem item, int position, FeedAdapter adapter) {
        if (item != null) {

            //init basic view
            initViewBasic(item, position, adapter);

            //an description bottom
            linear3.setVisibility(View.VISIBLE);
            HolderUtils.setDescription(item.getDescription(), txt5, context);

            int heightImage = item.getNeedHeightImage(widthImage);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(widthImage, heightImage);
//                params.setMargins(roundCorner, 0, roundCorner, roundCorner);
            img2.setLayoutParams(params);

            String img = item.getImageMedium();
            if (!TextUtils.isEmpty(img)) {
                Glide.with(context)
                        .load(img)//images[position%images.length]
                        .thumbnail(
                                Glide.with(context)
                                        .load(item.getImageSmall())
                        )
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .override(widthImage, heightImage)
//                                .placeholder(R.drawable.img_loading)
                        .into(img2);
            } else {
                Glide.with(context)
                        .load(R.drawable.no_media)//images[position%images.length]
                        .override(widthImage, widthImage)
                        .into(img2);
            }

            img2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent e) {
                    gestureDetector.onTouchEvent(e);
                    return true;
                }

                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
//                        Log.d("TEST", "onDoubleTap");
                        if (!item.isLiked()) {
                            imgLike.performClick();
                        }
                        animateHeart();
                        return super.onDoubleTap(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        MyUtils.gotoDetailImage(context, item);
                        return super.onSingleTapConfirmed(e);
                    }
                });

            });

        }
    }


}
