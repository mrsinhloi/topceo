package com.topceo.mediaplayer.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.mediaplayer.pip.presenter.VideoListItemOpsKt;
import com.topceo.shopping.MediaItem;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrPhuong on 2016-08-05.
 */
public class Video_Adapter extends
        RecyclerView.Adapter<Video_Adapter.ViewHolder> {

    // Store a member variable for the contacts
    private ArrayList<MediaItem> data = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;

    private int positionSelected = 0;

    private int imgWidth = 100, imgHeight = 70;
    private int radius = 4;
    private static RelativeLayout.LayoutParams params;

    // Pass in the contact array into the constructor
    public Video_Adapter(Context context, ArrayList<MediaItem> addresses, int position) {
        data = addresses;
        mContext = context;
        positionSelected = position;

        radius = mContext.getResources().getDimensionPixelOffset(R.dimen.radius_round);
        int screenWidth = MyUtils.getScreenWidth(context);
        imgWidth = (int) (screenWidth * 1.5f / 5);
        imgHeight = (int) (imgWidth * 9f / 16);
        params = new RelativeLayout.LayoutParams(imgWidth, imgHeight);

    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.activity_video_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MediaItem item = data.get(position);
        holder.txt1.setText(item.getTitle());
        holder.txt2.setText(item.getAuthor());

        if(item.getLengthInSecond()>0) {
            holder.txt3.setText(MyUtils.formatDuration(item.getLengthInSecond()));
            holder.txt3.setVisibility(View.VISIBLE);
        }else{
            holder.txt3.setVisibility(View.GONE);
        }

        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //khac bai thi moi play
                if (position != positionSelected) {
                    //set player old
                    Intent intent = new Intent(VideoActivity.ACTION_PLAY_VIDEO);
                    intent.putExtra(MediaItem.MEDIA_ITEM, item);
                    intent.putExtra(MediaItem.MEDIA_POSITION, position);
                    getContext().sendBroadcast(intent);
                    positionSelected = position;
                    notifyDataSetChanged();

                }
            }
        });

        if (position == positionSelected) {
            holder.txt1.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));
        } else {
            holder.txt1.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

       /* Glide.with(mContext)
                .load(item.getThumbnailUrl())
                .override(imgWidth, imgHeight)
                .centerCrop()
                .transform(new RoundedCorners(radius))
                .placeholder(R.drawable.no_media_small)
                .into(holder.img1);*/
        Glide.with(mContext).asBitmap().load(item.getThumbnailUrl()).transform(new RoundedCorners(radius)).into(new SimpleTarget<Bitmap>() {
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
            }

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setPosition(int position) {
        if (position < data.size()) {
            positionSelected = position;
            notifyDataSetChanged();
        }
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @BindView(R.id.txt1)
        TextView txt1;
        @BindView(R.id.txt2)
        TextView txt2;
        @BindView(R.id.txt3)
        TextView txt3;

        @BindView(R.id.img1)
        ImageView img1;
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.linearRoot)
        LinearLayout linearRoot;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder1 instance.
            super(itemView);

            ButterKnife.bind(this, itemView);
            img1.setLayoutParams(params);

        }


    }


}
