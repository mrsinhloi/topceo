package com.topceo.selections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.topceo.R;
import com.topceo.selections.hashtags.Hashtag;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_3_Select_Favorite_Adapter extends RecyclerView.Adapter<Fragment_3_Select_Favorite_Adapter.TopicViewHolder> {

    private final Context context;
    private static int cellSize;

    private final ArrayList<Hashtag> data;
    private int corner;

    private int min = 0, max = 0;
    private ItemSelectDeselectListener listener;

    private boolean isEnglish = false;

    public Fragment_3_Select_Favorite_Adapter(
            Context context,
            ArrayList<Hashtag> list,
            int spanRow,
            int spacing,
            int min,
            int max,
            boolean isEnglish,
            ItemSelectDeselectListener listener) {
        this.context = context;
        int spaceSize = (spanRow + 1) * spacing;//2 la padding left+right cua recyclerview
        this.cellSize = MyUtils.getScreenWidth(context) / spanRow - spaceSize / spanRow;
        this.data = list;
        corner = context.getResources().getDimensionPixelOffset(R.dimen.grid_round_corner);
        this.listener = listener;
        this.isEnglish = isEnglish;

        //trường hợp người dùng set sai: min=2,max=1 thì cho max bằng min, xem như chọn tối đa max
        if (min > 0 && max > 0) {
            if (max < min) {
                max = min;
            }
        }
        this.min = min;
        this.max = max;


    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.activity_select_favorites_row, parent, false);
        return new TopicViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TopicViewHolder holder, int position) {
        Hashtag tag = data.get(position);

        holder.txt1.setText(isEnglish ? tag.getNameEN() : tag.getName());
        if (tag.isSelected()) {
            holder.img2.setVisibility(View.VISIBLE);
        } else {
            holder.img2.setVisibility(View.GONE);
        }


        int with = cellSize;
        RequestOptions options = new RequestOptions()
                .override(with, with)
                .transform(new CenterCrop(), new RoundedCorners(corner))
                ;
        Glide.with(context)
                .load(tag.getImageUrl())
                .apply(options)
                .into(holder.img1)
        ;

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isSelected = !tag.isSelected();
                data.get(position).setSelected(isSelected);
                notifyItemChanged(position);


                int selectedSize = getItemSelected().size();
                if (max > 0 && selectedSize > max) {

                    //neu chi chon 1 thi chon luon
                    if (max == 1) {
                        deselectAll();
                        data.get(position).setSelected(isSelected);
                        notifyItemChanged(position);
                    } else {
                        //phai bo chon 1 ptu roi moi chon tiep
                        String thongBao = context.getString(R.string.select_max_x_topic, max);
                        MyUtils.showAlertDialog(context, thongBao);

                        //vuot qua, tra lai
                        data.get(position).setSelected(!isSelected);
                        notifyItemChanged(position);
                    }

                } else {
                    //trigger change select/deselect item
                    if (listener != null) {
                        listener.onSelectionChange(selectedSize);
                    }
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rootlayout)
        RelativeLayout rootlayout;
        @BindView(R.id.img1)
        ImageView img1;
        @BindView(R.id.txtBg)
        View txtBg;
        @BindView(R.id.img2)
        ImageView img2;
        @BindView(R.id.txt1)
        TextView txt1;

        public TopicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


            rootlayout.setLayoutParams(new LinearLayout.LayoutParams(cellSize, cellSize));
            img1.setLayoutParams(new RelativeLayout.LayoutParams(cellSize, cellSize));
//            txtBg.setLayoutParams(new RelativeLayout.LayoutParams(cellSize, cellSize));


        }
    }


    public ArrayList<Hashtag> getItemSelected() {
        ArrayList<Hashtag> list = new ArrayList<>();
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                Hashtag item = data.get(i);
                if (item.isSelected()) {
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void deselectAll() {
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isSelected()) {
                    data.get(i).setSelected(false);
                }
            }
            notifyDataSetChanged();
        }
    }


    /*public ArrayList<Hashtag> getItemUnSelected() {
        ArrayList<Hashtag> list = new ArrayList<>();
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                Hashtag item = data.get(i);
                if (!item.isSelected()) {
                    list.add(item);
                }
            }
        }
        return list;
    }*/

}
