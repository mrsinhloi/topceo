package com.topceo.shopping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.topceo.R;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Shop_Adapter extends RecyclerView.Adapter<Fragment_Shop_Adapter.MediaViewHolder> {
    private ArrayList<Media> dataSet = new ArrayList<>();
    private Context context;
    private int imgWidth, imgHeight;
    private int imgWidthVideo, imgHeightVideo;

    private int space;
    private int radius;
    private User user;
    private TinyDB db;

    private RelativeLayout.LayoutParams params;
    private RelativeLayout.LayoutParams paramsVideo;
    private ViewGroup.LayoutParams paramsRoot;

    public Fragment_Shop_Adapter(Context context, ArrayList<Media> list) {
        this.context = context;
        this.dataSet = list;

        db = new TinyDB(context);
        try {
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ////
        radius = context.getResources().getDimensionPixelOffset(R.dimen.radius_round);
        int screenWidth = MyUtils.getScreenWidth(context);
        space = context.getResources().getDimensionPixelOffset(R.dimen.margin_4dp);
        imgWidth = screenWidth / 3 - space * 2;
        imgHeight = imgWidth;//(int) (imgWidth * 1.2f);
        params = new RelativeLayout.LayoutParams(imgWidth, imgHeight);

        ////
//        float ratioVideo = (float) 16 / 9;//1.7777
        imgWidthVideo = screenWidth / 2 - space * 2;
        imgHeightVideo = imgWidthVideo * 9 / 16;
        paramsVideo = new RelativeLayout.LayoutParams(imgWidthVideo, imgHeightVideo);


    }

    public void addMore(ArrayList<Media> list) {
        int size = dataSet.size();
        dataSet.addAll(list);
        notifyItemMoved(size, list.size());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void clear() {
        dataSet.clear();
        notifyDataSetChanged();
    }

    public void replaceItem(Media media) {
        if (media != null) {
            int position = findItem(media.getMediaId());
            if (position > -1) {
                dataSet.set(position, media);
                notifyItemChanged(position);
            }
        }
    }

    private int findItem(long mediaId) {
        int position = -1;
        for (int i = 0; i < dataSet.size(); i++) {
            Media item = dataSet.get(i);
            if (item.getMediaId() == mediaId) {
                position = i;
                break;
            }
        }
        return position;
    }

    public Media getLastItem() {
        if (dataSet != null && dataSet.size() > 0) {
            return dataSet.get(dataSet.size() - 1);
        }
        return null;
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img1)
        ImageView img1;
        @BindView(R.id.txt1)
        TextView txt1;
        @BindView(R.id.txt2)
        TextView txt2;
        @BindView(R.id.txt3)
        TextView txt3;

        @BindView(R.id.imgComment)
        ImageView imgComment;
        @BindView(R.id.txt4)
        TextView txt4;
        @BindView(R.id.txt5)
        TextView txt5;


        @BindView(R.id.imgLike)
        ImageView imgLike;
        @BindView(R.id.linearRoot)
        LinearLayout linearRoot;
        @BindView(R.id.linearBottom)
        LinearLayout linearBottom;


        public MediaViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            img1.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgHeight));

            //an gia va free
            txt2.setVisibility(View.GONE);
            txt3.setVisibility(View.GONE);
            txt4.setVisibility(View.GONE);
            txt5.setVisibility(View.GONE);


            imgLike.setVisibility(View.GONE);
            paramsRoot = linearRoot.getLayoutParams();
        }
    }

    /*@Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getMediaTypeInt();
    }*/

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shopping_adapter_item, viewGroup, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Media item = dataSet.get(position);

        //set thong tin co ban
        holder.txt1.setText(item.getTitle());
        if (item.isFree()) {
            holder.txt2.setText(R.string.free);
        } else {
            //co phi, chi xet user la vip hoac khong, ko quan tam priceMobileCard
            if (user.isVip()) {
                String money = MyUtils.getMoneyFormat(item.getVipPrice()) + " VNĐ";
                holder.txt2.setText(money);
            } else {
                String money = MyUtils.getMoneyFormat(item.getPrice()) + " VNĐ";
                holder.txt2.setText(money);
            }

        }


        //comment
        int commentCount = item.getCommentCount();
        if (commentCount > 0) {
            holder.imgComment.setVisibility(View.VISIBLE);
            holder.txt4.setVisibility(View.VISIBLE);
            holder.txt4.setText(String.valueOf(commentCount));
        } else {
//            holder.txt4.setVisibility(View.GONE);
//            holder.imgComment.setVisibility(View.GONE);

            holder.imgComment.setVisibility(View.VISIBLE);
            holder.txt4.setVisibility(View.VISIBLE);
            holder.txt4.setText(String.valueOf(0));
        }

        //SHOW LIKE
        int count = item.getLikeCount();
        if (count > 0) {
            holder.txt3.setVisibility(View.VISIBLE);
            holder.txt3.setText(String.valueOf(count));
        } else {
//            holder.txt3.setVisibility(View.GONE);

            holder.txt3.setVisibility(View.VISIBLE);
            holder.txt3.setText(String.valueOf(0));
        }

        //show lock
        if (item.isFree()) {
            holder.txt5.setVisibility(View.GONE);
        } else {
            holder.txt5.setVisibility(View.VISIBLE);
        }


        //SHOW ICON FAVORITE
        if (item.isLiked()) {
            holder.imgLike.setVisibility(View.VISIBLE);
        } else {
            holder.imgLike.setVisibility(View.GONE);
        }

        if (item.getMediaType().equalsIgnoreCase(MediaType.VIDEO)) {
            holder.img1.setLayoutParams(paramsVideo);
            //ty le 16:9
            /*Glide.with(context)
                    .load(item.getThumbnail())
                    .placeholder(R.drawable.no_media_small)
                    .override(imgWidthVideo, imgHeightVideo)
                    .into(holder.img1);*/
            holder.img1.setImageBitmap(null);
            Glide.with(context)
                    .asBitmap()
                    .load(item.getThumbnail())
                    .override(imgWidthVideo, imgHeightVideo)
                    .placeholder(R.drawable.no_media_small)
                    .error(R.drawable.no_media_small)
                    .transform(new RoundedCorners(radius))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // creating the image that maintain aspect ratio with width of image is set to screenwidth.
                            int width = imgWidthVideo;
                            int diw = resource.getWidth();
                            if (diw > 0) {
                                int height = 0;
                                height = width * resource.getHeight() / diw;
                                resource = Bitmap.createScaledBitmap(resource, width, height, false);
                                imgHeightVideo = height;
                                paramsVideo = new RelativeLayout.LayoutParams(imgWidthVideo, imgHeightVideo);
                                holder.img1.setLayoutParams(paramsVideo);
                            }
                            holder.img1.setImageBitmap(resource);

                            /*holder.img1.post(new Runnable() {
                                @Override
                                public void run() {
                                    int heightText = holder.txt1.getHeight();
                                    int heightBottom = holder.linearBottom.getHeight();
                                    paramsRoot.height = imgHeightVideo + heightText + heightBottom;
                                    holder.linearRoot.setLayoutParams(paramsRoot);

                                }
                            });*/

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Glide.with(context)
                                    .load(R.drawable.no_media_small)
                                    .override(imgWidthVideo, imgHeightVideo)
                                    .into( holder.img1);
                            holder.img1.setLayoutParams(new RelativeLayout.LayoutParams(imgWidthVideo, imgHeightVideo));
                        }
                    });
        } else {
            holder.img1.setLayoutParams(params);
            holder.img1.setImageBitmap(null);
            //image
            /*Glide.with(context)
                    .load(item.getThumbnail())
                    .placeholder(R.drawable.no_media_small)
                    .override(imgWidth, imgHeight)
                    .into(holder.img1);*/

            Glide.with(context)
                    .asBitmap()
                    .load(item.getThumbnail())
                    .override(imgWidth, imgHeight)
                    .placeholder(R.drawable.no_media_small)
                    .error(R.drawable.no_media_small)
                    .transform(new RoundedCorners(radius))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // creating the image that maintain aspect ratio with width of image is set to screenwidth.
                            int width = imgWidth;
                            int diw = resource.getWidth();
                            if (diw > 0) {
                                int height = 0;
                                height = width * resource.getHeight() / diw;
                                resource = Bitmap.createScaledBitmap(resource, width, height, false);
                                imgHeight = height;
                                params = new RelativeLayout.LayoutParams(imgWidth, imgHeight);
                                holder.img1.setLayoutParams(params);
                            }
                            holder.img1.setImageBitmap(resource);

                           /* holder.img1.post(new Runnable() {
                                @Override
                                public void run() {
                                    int heightText = holder.txt1.getHeight();
                                    int heightBottom = holder.linearBottom.getHeight();
                                    paramsRoot.height = imgHeight + heightText + heightBottom;
                                    holder.linearRoot.setLayoutParams(paramsRoot);

                                }
                            });*/
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
//                            holder.img1.setImageDrawable(errorDrawable);
                            loadEmptyImage(holder.img1);
                        }
                    });
        }


        //dieu khien khi click vao
        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShoppingActivity.ACTION_PLAY_ALBUM);
                intent.putExtra(Media.MEDIA, item);
                context.sendBroadcast(intent);

                /*switch (item.getMediaTypeInt()) {
                    case MediaType.AUDIO_INT:
                        if(item.isFree()){//play
//                            MyUtils.showToast(context, "Playing developing...");
                            Intent intent = new Intent(ShoppingActivity.ACTION_PLAY_ALBUM);
                            intent.putExtra(Media.MEDIA, item);
                            context.sendBroadcast(intent);

                        }else{//buy
                            Intent intent = new Intent(context, PaymentActivity.class);
                            intent.putExtra(Media.MEDIA_ID, item.getMediaId());
                            context.startActivity(intent);
                        }
                        break;
                    case MediaType.VIDEO_INT:
                        if(item.isFree()){//play
//                            MyUtils.showToast(context, "Playing developing...");
                            Intent intent = new Intent(ShoppingActivity.ACTION_PLAY_ALBUM);
                            intent.putExtra(Media.MEDIA, item);
                            context.sendBroadcast(intent);
                        }else{//buy
                            Intent intent = new Intent(context, PaymentActivity.class);
                            intent.putExtra(Media.MEDIA_ID, item.getMediaId());
                            context.startActivity(intent);
                        }
                        break;
                    case MediaType.PHOTO_INT:
                        if(item.isFree()){//play
                            MyUtils.showToast(context, "Playing developing...");
                        }else{//buy
                            Intent intent = new Intent(context, PaymentActivity.class);
                            intent.putExtra(Media.MEDIA_ID, item.getMediaId());
                            context.startActivity(intent);
                        }
                        break;
                    case MediaType.PDF_INT:
                        if(item.isFree()){//play
                            MyUtils.showToast(context, "Playing developing...");
                        }else{//buy
                            Intent intent = new Intent(context, PaymentActivity.class);
                            intent.putExtra(Media.MEDIA_ID, item.getMediaId());
                            context.startActivity(intent);
                        }
                        break;
                    case MediaType.EPUB_INT:
                        if(item.isFree()){//play
                            MyUtils.showToast(context, "Playing developing...");
                        }else{//buy
                            Intent intent = new Intent(context, PaymentActivity.class);
                            intent.putExtra(Media.MEDIA_ID, item.getMediaId());
                            context.startActivity(intent);
                        }
                        break;
                }*/
            }
        });
    }

    private void loadEmptyImage(ImageView img){
        Glide.with(context)
                .load(R.drawable.no_media_small)
                .override(imgWidth, imgWidth)
                .into(img);
        img.setLayoutParams(new RelativeLayout.LayoutParams(imgWidth, imgWidth));
    }
}
