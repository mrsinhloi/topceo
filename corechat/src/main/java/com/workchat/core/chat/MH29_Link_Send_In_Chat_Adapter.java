package com.workchat.core.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.workchat.core.models.chat.ContentType;
import com.workchat.core.models.chat.Link;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.utils.DateFormat;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH29_Link_Send_In_Chat_Adapter extends RecyclerView.Adapter<MH29_Link_Send_In_Chat_Adapter.MyViewHolder> {

    private List<RoomLog> data = new ArrayList<RoomLog>();
    int size = 100;
    Context context;
    public MH29_Link_Send_In_Chat_Adapter(Context context, List<RoomLog> list) {
        this.context = context;
        size = context.getResources().getDimensionPixelSize(R.dimen.avatar_size);
        data = list;
    }

    public void addMore(List<RoomLog> list) {
        int size = data.size();
        data.addAll(list);
        notifyItemRangeInserted(size, list.size());
    }

    public String getLastLogId() {
        if(data!=null && data.size()>0){
            return data.get(data.size()-1).get_id();
        }
        return null;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.linearRoot)
        LinearLayout linearRoot;

        @BindView(R2.id.imageView1)
        ImageView img1;
        @BindView(R2.id.textView1)
        AppCompatTextView txt1;
        @BindView(R2.id.textView2)
        AppCompatTextView txt2;
        @BindView(R2.id.textView3)
        AppCompatTextView txt3;


        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mh29_link_send_in_chat_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final RoomLog item = data.get(position);
        if (item != null) {

            if (item.getType().equalsIgnoreCase(ContentType.LINK)) {
                JsonObject json = item.getContent();
                if (json != null) {
                    try {
                        final Link link = new Gson().fromJson(json.toString(), Link.class);
                        holder.txt1.setText(link.getText());

                        holder.txt2.setText(item.getAuthorInfo().getName());
                        holder.txt3.setText(MyUtils.getDate(item.getCreateDate(), DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM));

                        if(item.getAuthorInfo()!=null && !TextUtils.isEmpty(item.getAuthorInfo().getAvatar())) {
                            RequestOptions options = new RequestOptions();
                            options.centerCrop();
                            options.transform(new RoundedCorners(size/2));

                            Glide.with(context)
                                    .load(item.getAuthorInfo().getAvatar())
                                    .override(size, size)
                                    .apply(options)
                                    .placeholder(R.drawable.icon_no_image)
                                    .into(holder.img1);
                        }

                        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (MyUtils.checkInternetConnection(context)) {
                                    String url = link.getLink();

                                    MyUtils.goToWeb(context, url);

                                } else {
                                    MyUtils.showThongBao(context);
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }







        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
