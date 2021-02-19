package com.workchat.core.channel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.widgets.CircleTransform;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrPhuong on 2016-10-31.
 */

public class MH01_BanUserAdapter extends RecyclerView.Adapter<MH01_BanUserAdapter.MyViewHolder> {
    private List<ChannelUser> listItems = new ArrayList<>();

    public void removeUser(String userId) {
        if (listItems != null && listItems.size() > 0) {
            for (int i = 0; i < listItems.size(); i++) {
                if (listItems.get(i).getUserId().equals(userId)) {
                    listItems.remove(i);
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.img1)
        ImageView img1;
        @BindView(R2.id.txt1)
        AppCompatTextView txt1;
        @BindView(R2.id.btn1)
        AppCompatButton btn1;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
//            img1.setLayoutParams(params);
            /*img1.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

        }
    }

    public void setListItems(ArrayList<ChannelUser> list) {
        this.listItems = list;
    }

    public void addListItems(ArrayList<ChannelUser> list) {
        int sum = listItems.size();
        this.listItems.addAll(list);
        notifyItemRangeChanged(sum, list.size());
    }

    public void addItem(ChannelUser item, int position) {
        try {
            listItems.set(position, item);
            notifyItemChanged(position);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addItem(ChannelUser item) {
        listItems.add(item);
        notifyDataSetChanged();

    }

    private int width = 0;
    private Context context;

    public MH01_BanUserAdapter(List<ChannelUser> list, Context context) {
        if (list != null) {
            this.listItems = list;
        }
        this.context = context;
        width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_01_ban_user_row, parent, false);

        return new MH01_BanUserAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MH01_BanUserAdapter.MyViewHolder holder, final int position) {
        ChannelUser user = listItems.get(position);

        //set image
        final UserInfo item = user.getUserInfo();
        if (item != null && !TextUtils.isEmpty(item.getAvatar())) {
            Picasso.get()
                    .load(item.getAvatar())
                    .centerCrop()
                    .resize(width, width)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.icon_no_image)
                    .into(holder.img1);
        }


        holder.txt1.setText(item.getName());
        holder.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(R.string.confirm_un_ban_user);
                alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();

                        Intent intent = new Intent(MH01_BanUserActivity.ACTION_SET_BAN_USER);
                        intent.putExtra(UserChat.IS_BANNED_USER, false);
                        intent.putExtra(UserChat.USER_ID, item.get_id());
                        context.sendBroadcast(intent);
                    }
                });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


}
