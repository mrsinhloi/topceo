package com.topceo.mediaplayer.preview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

/**
 * Created by MrPhuong on 2016-08-05.
 */
public class ThumbnailAdapter extends
        RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {


    public ArrayList<Bitmap> getData() {
        return data;
    }

    public Bitmap getBitmapAt(int position) {
        if (position >= 0 && position < data.size()) {
            return data.get(position);
        }
        return null;
    }

    // Store a member variable for the contacts
    private ArrayList<Bitmap> data = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;
    private int size = 100;
    private int screenWidth;

    // Pass in the contact array into the constructor
    public ThumbnailAdapter(Context context, ArrayList<Bitmap> data) {
        mContext = context;
        screenWidth = MyUtils.getScreenWidth(mContext);
//        size = mContext.getResources().getDimensionPixelSize(R.dimen.dimen_120dp);
        size = screenWidth / VideoSelectThumbnailActivity.NUMBER_COLUMNS;
        this.data = data;

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
        View contactView = inflater.inflate(R.layout.thumnail_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        viewHolder.img.setLayoutParams(new FrameLayout.LayoutParams(size, size));
        return viewHolder;
    }

    public int getPositionSelected() {
        return positionSelected;
    }

    public void setPositionSelected(int positionSelected) {
        this.positionSelected = positionSelected;
        notifyDataSetChanged();
    }

    private int positionSelected = 0;

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Bitmap item = data.get(position);
        Glide.with(mContext)
                .load(item)
                .override(size, size)
                .into(holder.img);

        if (positionSelected == position) {
//            holder.img.setBackgroundResource(R.drawable.bg_rectangle_3_not_fill);
            holder.imgCheck.setVisibility(View.VISIBLE);
        } else {
            holder.imgCheck.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView img;
        public ImageView imgCheck;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder1 instance.
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.img1);

            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);

        }
    }


}
