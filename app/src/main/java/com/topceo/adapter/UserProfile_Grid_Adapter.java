/**
 * Author Mateusz Mlodawski mmlodawski@gmail.com
 */
package com.topceo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.MyItemData;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

public class UserProfile_Grid_Adapter extends RecyclerView.Adapter<UserProfile_Grid_Adapter.ViewHolder> {

    private final Context context;
    public ArrayList<ImageItem> mDataset = new ArrayList<>();
    private ViewGroup parent;

    private void initSize() {
        //
        widthScreen = MyUtils.getScreenWidth(context);
        int cols = 3;//context.getResources().getInteger(R.integer.grid_span);
        if (cols > 0) {
            int space = context.getResources().getDimensionPixelOffset(R.dimen.grid_cell_offset);
//            int pxSpace=space*(cols-1);

            imageWidth = widthScreen / cols - space;
        }
    }

    private ColorGenerator generator;

    public UserProfile_Grid_Adapter(Context context) {
        this.context = context;
        this.mDataset = new ArrayList<>();
        initSize();
        generator = ColorGenerator.MATERIAL;
    }

    private int widthScreen = 0;
    private static int imageWidth = 0;

    public UserProfile_Grid_Adapter(Context context, ArrayList<ImageItem> imageModels) {
        this.context = context;
        this.mDataset = imageModels;
        initSize();

    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserProfile_Grid_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_gallery_recycler_view_row, parent, false);
        this.parent = parent;
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImageItem item = mDataset.get(position);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         *//*holder.ripple.setOnClickListener(
                    new OnPictureClickListener((Activity) parent.getContext(), PictureActivity.class, imageModel.getUrl(), holder.image)
            );*//*
        } else {
            holder.ripple.setOnClickListener(
                    new OnPictureClickListener((Activity) parent.getContext(), PictureActivity.class, imageModel.getUrl())
            );
        }*/

        holder.ripple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when click
                MyUtils.gotoDetailImage(context, item);
            }
        });

        //reset
        holder.iconVideo.setVisibility(View.GONE);
        holder.iconVideo.setImageResource(R.drawable.ic_videocam_white_24dp);

        //change
        if (ImageItem.ITEM_TYPE_FACEBOOK.equals(item.getItemType())) {
            String url = item.getImageSmall();
            if (MyUtils.isImageUrl(url)) {
                Glide.with(context)
                        .load(url)
                        .override(imageWidth, imageWidth)
                        .centerCrop()
                        .placeholder(R.color.grey_400)
                        .error(R.color.grey_400)
                        .into(holder.image);

                if (item.getItemContent().size() > 0) {
                    holder.iconVideo.setVisibility(View.VISIBLE);
                    holder.iconVideo.setImageResource(R.drawable.ic_baseline_photo_library_24);
                } else {
                    holder.iconVideo.setVisibility(View.GONE);
                }
            } else {
                try {
                    String text = "";
                    MyItemData data = item.getItemData();
                    if (data != null && data.getLinkPreview() != null) {
                        text = "link";
                    } else {
                        text = "message";
                    }

                    int color = generator.getColor(text);
                    TextDrawable drawable = TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(Typeface.DEFAULT)
                            .fontSize(50) /* size in px */
                            .bold()
                            .toUpperCase()
                            .endConfig()
                            .buildRect(text.toUpperCase(), color);
                    holder.image.setImageDrawable(drawable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {//instagram
            String url = item.getImageSmall();
            Glide.with(context)
                    .load(url)
                    .override(imageWidth, imageWidth)
                    .centerCrop()
                    .placeholder(R.color.grey_400)
                    .error(R.color.grey_400)
                    .into(holder.image);

            if (item.isVideo()) {
                holder.iconVideo.setVisibility(View.VISIBLE);
                holder.iconVideo.setImageResource(R.drawable.ic_videocam_white_24dp);
            } else {
                holder.iconVideo.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Clears the adapter and sets new dataset
     *
     * @param imageModels items to set
     */
    public void setItems(ArrayList<ImageItem> imageModels) {
        this.mDataset.clear();
        this.mDataset.addAll(imageModels);
        notifyDataSetChanged();
    }

    /**
     * Adds items to current dataset
     *
     * @param imageModels items to add
     */
    public void addItems(ArrayList<ImageItem> imageModels) {
        this.mDataset.addAll(imageModels);
        notifyDataSetChanged();
    }

    /**
     * Provides a reference to the views for each data item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialRippleLayout ripple;
        final ImageView image;
        private ImageView iconVideo;


        public ViewHolder(View v) {
            super(v);
            ripple = (MaterialRippleLayout) v.findViewById(R.id.ripple);
            image = (ImageView) v.findViewById(R.id.grid_view_row_image);
            iconVideo = (ImageView) v.findViewById(R.id.imgVideo);

            ripple.setLayoutParams(new FrameLayout.LayoutParams(imageWidth, imageWidth));
            image.setLayoutParams(new FrameLayout.LayoutParams(imageWidth, imageWidth));
        }
    }

    //clear all items
    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    //add list items
    public void addAll(ArrayList<ImageItem> list) {
        mDataset.addAll(list);
        notifyDataSetChanged();
    }

    public ImageItem getItem(int position) {
        if (getItemCount() == 0) {
            return null;
        } else {
            return mDataset.get(position);
        }
    }

    public void removeItem(long imageItemId) {

        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getImageItemId() == imageItemId) {
                mDataset.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }

    }

    /**
     * because .set method will replace your data to particular position.
     *
     * @param item
     */
    public void replaceItem(ImageItem item) {

        int position = -1;
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getImageItemId() == item.getImageItemId()) {
                /*mDataset.remove(i);
                notifyItemRemoved(i);
                mDataset.add(i, item);
                notifyItemInserted(i);*/
                position = i;

                break;
            }
        }

        if (position >= 0) {
            mDataset.set(position, item);
            notifyItemChanged(position);
        }

    }


}
