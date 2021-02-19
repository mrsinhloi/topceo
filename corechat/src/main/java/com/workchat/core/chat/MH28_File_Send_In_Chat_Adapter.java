package com.workchat.core.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.workchat.core.models.chat.ContentType;
import com.workchat.core.models.chat.File;
import com.workchat.core.models.chat.Image;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.utils.DateFormat;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH28_File_Send_In_Chat_Adapter extends RecyclerView.Adapter<MH28_File_Send_In_Chat_Adapter.MyViewHolder> {

    private List<RoomLog> data = new ArrayList<RoomLog>();
    int size = 100;
    Context context;
    public MH28_File_Send_In_Chat_Adapter(Context context, List<RoomLog> list) {
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
                .inflate(R.layout.mh28_file_send_in_chat_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final RoomLog item = data.get(position);
        if (item != null) {

            if (item.getType().equalsIgnoreCase(ContentType.IMAGE)) {
                JsonObject json = item.getContent();
                if (json != null) {
                    try {
                        final Image image = new Gson().fromJson(json.toString(), Image.class);
                        holder.txt1.setText(image.getName());
                        Glide.with(context)
                                .load(image.getThumbLink())
                                .override(size, size)
                                .centerCrop()
                                .placeholder(R.drawable.no_media_small)
                                .into(holder.img1);

                        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (MyUtils.checkInternetConnection(context)) {
                                    String url = image.getLink();

                                    //neu chon image thi mo nguyen list
                                    if (MyUtils.isImageUrl(url)) {
                                        //add all duong dan hinh
                                        ArrayList<String> urls = MyUtils.getAllImageLink(data);

                                        //tim lai position
                                        int pos = 0;
                                        for (int i = 0; i < urls.size(); i++) {
                                            if (urls.get(i).equalsIgnoreCase(url)) {
                                                pos = i;
                                                break;
                                            }
                                        }

                                        MyUtils.openFileImages(context, urls, pos);

                                    } else {//neu chon document thi mo file
                                        MyUtils.openFile(context, url);

                                    }

                                } else {
                                    MyUtils.showThongBao(context);
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                if (item.getType().equalsIgnoreCase(ContentType.FILE)) {
                    JsonObject json = item.getContent();
                    if (json != null) {
                        try {
                            final File image = new Gson().fromJson(json.toString(), File.class);
                            holder.txt1.setText(image.getName());
                            Glide.with(context)
                                    .load(R.drawable.ic_folder_download)
                                    .override(size, size)
                                    .centerCrop()
                                    .into(holder.img1);

                            holder.linearRoot.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (MyUtils.checkInternetConnection(context)) {
                                        String url = image.getLink();

                                        //neu chon image thi mo nguyen list
                                        if (MyUtils.isImageUrl(url)) {
                                            //add all duong dan hinh
                                            ArrayList<String> urls = MyUtils.getAllImageLink(data);

                                            //tim lai position
                                            int pos = 0;
                                            for (int i = 0; i < urls.size(); i++) {
                                                if (urls.get(i).equalsIgnoreCase(url)) {
                                                    pos = i;
                                                    break;
                                                }
                                            }

                                            MyUtils.openFileImages(context, urls, pos);

                                        } else {//neu chon document thi mo file
                                            MyUtils.openFile(context, url);

                                        }

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

            holder.txt2.setText(item.getAuthorInfo().getName());
            holder.txt3.setText(MyUtils.getDate(item.getCreateDate(), DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM));





        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
