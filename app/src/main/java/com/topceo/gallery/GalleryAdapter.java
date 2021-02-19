/**
 * Author Mateusz Mlodawski mmlodawski@gmail.com
 */
package com.topceo.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.topceo.R;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private final List<ParcelableImageModel> imageModels;
    private ViewGroup parent;

    public GalleryAdapter(Context context) {
        this.context = context;
        this.imageModels = new ArrayList<>();
    }

    private int widthScreen = 0;
    private static int imageWidth = 0;

    public GalleryAdapter(Context context, List<ParcelableImageModel> imageModels) {
        this.context = context;
        this.imageModels = imageModels;

        //
        widthScreen = MyUtils.getScreenWidth(context);
        int cols = context.getResources().getInteger(R.integer.grid_span);
        if (cols > 0) {
            int space = context.getResources().getDimensionPixelOffset(R.dimen.grid_cell_offset);
//            int pxSpace=space*(cols-1);

            imageWidth = widthScreen / cols - space;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_gallery_recycler_view_row, parent, false);
        this.parent = parent;
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ParcelableImageModel imageModel = imageModels.get(position);


        /*Glide.with(context)
                .load(imageModel.getUrl())
                .override(imageWidth, imageWidth)
                .placeholder(R.color.grey_200)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        Bitmap bitmap = null;

                        try {
                            bitmap = ((BitmapDrawable)resource).getBitmap();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(bitmap!=null){
                            new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    // Get colors from Palette
                                    Palette.Swatch darkVibrantSwatch = palette.getDarkVibrantSwatch();
                                    Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();

                                    int color = 0;
                                    // Pick darkVibrant if generated, if not try darkMuted
                                    if (darkVibrantSwatch != null) {
                                        color = darkVibrantSwatch.getRgb();
                                        holder.ripple.setRippleColor(color);
                                    } else if (darkMutedSwatch != null) {
                                        color = darkMutedSwatch.getRgb();
                                        holder.ripple.setRippleColor(color);
                                    }

                                    if (color != 0) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            // We can do Activity Transitions with Lollipop and higher

                                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            // We can't do Activity transitions. Pass only color.

                                        }
                                    }
                                }
                            });
                        }


                        holder.image.setImageDrawable(resource);
                        return true;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);*/

        Glide.with(context)
                .load(imageModel.getUrl())
                .override(imageWidth, imageWidth)
                .placeholder(R.color.grey_200)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);

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
                GalleryFragment.setImageFull(imageModel.getUrl(), imageModel.isVideo());
            }
        });

        if (imageModel.isVideo()) {
            holder.imgVideo.setVisibility(View.VISIBLE);
        } else {
            holder.imgVideo.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    /**
     * Clears the adapter and sets new dataset
     *
     * @param imageModels items to set
     */
    public void setItems(List<ParcelableImageModel> imageModels) {
        this.imageModels.clear();
        this.imageModels.addAll(imageModels);
        notifyDataSetChanged();
    }

    /**
     * Adds items to current dataset
     *
     * @param imageModels items to add
     */
    public void addItems(List<ParcelableImageModel> imageModels) {
        this.imageModels.addAll(imageModels);
        notifyDataSetChanged();
    }

    /**
     * Provides a reference to the views for each data item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialRippleLayout ripple;
        final ImageView image;
        final ImageView imgVideo;

        public ViewHolder(View v) {
            super(v);
            ripple = (MaterialRippleLayout) v.findViewById(R.id.ripple);
            image = (ImageView) v.findViewById(R.id.grid_view_row_image);
            imgVideo = (ImageView) v.findViewById(R.id.imgVideo);


            ripple.setLayoutParams(new FrameLayout.LayoutParams(imageWidth, imageWidth));
            image.setLayoutParams(new FrameLayout.LayoutParams(imageWidth, imageWidth));
        }
    }
}
