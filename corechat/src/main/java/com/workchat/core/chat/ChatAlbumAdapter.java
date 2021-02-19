package com.workchat.core.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.workchat.core.models.chat.AlbumItem;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrPhuong on 2016-10-31.
 */

public class ChatAlbumAdapter extends RecyclerView.Adapter<ChatAlbumAdapter.MyViewHolder> {
    private List<AlbumItem> listItems = new ArrayList<>();

    private int width = 0, radius = 16, margin = 10;
    private Context context;
    private int columns = 3;

    public ChatAlbumAdapter(List<AlbumItem> list, Context context, int defaultColumn) {
        if (list == null) list = new ArrayList<>();

        this.columns = defaultColumn;
        this.listItems = list;
        this.context = context;
        width = MyUtils.getScreenWidth(context) / (defaultColumn+2);//context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_larger);
        radius = context.getResources().getDimensionPixelOffset(R.dimen.radius_image);
        margin = radius;
        maps.clear();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_chat_row_album_item, parent, false);

        return new ChatAlbumAdapter.MyViewHolder(itemView);
    }

    private HashMap<Integer, MyViewHolder> maps = new HashMap<>();

    @Override
    public void onBindViewHolder(final ChatAlbumAdapter.MyViewHolder holder, final int position) {
        final AlbumItem item = listItems.get(position);
        //set image
        maps.put(position, holder);
        holder.setState(item, position);
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }


    private int findPosition(String path) {
        int position = -1;
        if (!TextUtils.isEmpty(path)) {
            for (int i = 0; i < listItems.size(); i++) {
                if (path.equalsIgnoreCase(listItems.get(i).getLocalPath())) {
                    position = i;
                }
            }
        }
        return position;
    }

    private int positionUploading = 0;
    public MyViewHolder holderUploading;

    public void setPathUploading(String path) {
        int pos = findPosition(path);
        if (pos >= 0) {
            positionUploading = pos;
//            notifyItemChanged(pos);
            holderUploading = maps.get(pos);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R2.id.imgThumb)
        ImageView imgThumb;
        @BindView(R2.id.imgPlay)
        ImageView imgPlay;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            imgThumb.setLayoutParams(params);
//            imgThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        //uploading holder
        @BindView(R2.id.relativeCir)
        RelativeLayout relativeCir;
        @BindView(R2.id.progressCir)
        ProgressBar progressCir;
        @BindView(R2.id.txtCir)
        TextView txtCir;
        @BindView(R2.id.imgCir)
        ImageView imgCir;

        public void setPercent(String path, int percent) {
            if (!TextUtils.isEmpty(path) && path.equalsIgnoreCase(item.getLocalPath())) {

                if (item != null) item.setPercent(percent);
                if (percent == 100) {
                    if (txtCir.getVisibility() != View.GONE) {
                        txtCir.setVisibility(View.GONE);
                    }
                    if (imgCir.getVisibility() != View.VISIBLE) {
                        imgCir.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (txtCir.getVisibility() != View.VISIBLE) {
                        txtCir.setVisibility(View.VISIBLE);
                    }
                    if (imgCir.getVisibility() != View.GONE) {
                        imgCir.setVisibility(View.GONE);
                    }
//                    txtCir.setText(percent + "%");
                    txtCir.setText("");
                }
                progressCir.setProgress(percent);

                //save
//                this.percent = percent;
//                itemPercentChangeListener.onPercentChange(path, percent);
            }
        }

        private AlbumItem item;

        private void setState(AlbumItem item, int position) {
            this.item = item;
            //set image theo trang thai
            if (item.isUploading()) {

                if (item.getPercent() == 100 && position == positionUploading) {
                    positionUploading += 1;
                }
                if (item.getPercent() < 100 && position == positionUploading) {
                    holderUploading = this;
                }

                if (relativeCir.getVisibility() != View.VISIBLE) {
                    relativeCir.setVisibility(View.VISIBLE);
                }
                //set hinh cache tam tu local path
                if (item != null) {

                    //percent
                    setPercent(item.getLocalPath(), item.getPercent());

                    //keep layout for image
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
                    imgThumb.setLayoutParams(params);
                    relativeCir.setLayoutParams(params);

                    //local image
                    if (!TextUtils.isEmpty(item.getLocalPath())) {
                        if(context!=null){
                            try {
                                Glide.with(context)
                                        .load(item.getLocalPath())
                                        .override(width, width)
                                        .transform(new CenterCrop(), new RoundedCorners(radius))
                                        .error(R.drawable.no_media_small)
                                        .into(imgThumb);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            } else {

                if (relativeCir.getVisibility() != View.GONE) {
                    relativeCir.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(item.getThumbLink())) {
                    if(context!=null){
                        try {
                            Glide.with(context)
                                    .load(item.getThumbLink())
                                    .override(width, width)
                                    .centerCrop()
                                    .transform(new CenterCrop(), new RoundedCorners(radius))
                                    .error(R.drawable.no_media_small)
                                    .into(imgThumb);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (item.isVideo()) {
                    imgPlay.setVisibility(View.VISIBLE);
                } else {
                    imgPlay.setVisibility(View.GONE);
                }
            }

        }


        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onItemClick(v, getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null) {
                return itemLongClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return false;
        }
    }

    ////
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    ////
    private ItemLongClickListener itemLongClickListener;

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public interface ItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }


    ////
    /*private ItemPercentChangeListener itemPercentChangeListener;
    public void setItemPercentChangeListener(ItemPercentChangeListener itemPercentChangeListener) {
        this.itemPercentChangeListener = itemPercentChangeListener;
    }
    public interface ItemPercentChangeListener {
        boolean onPercentChange(String path, int percent);
    }*/


}
