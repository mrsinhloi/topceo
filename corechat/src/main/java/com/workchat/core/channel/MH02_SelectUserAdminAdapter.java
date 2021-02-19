package com.workchat.core.channel;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.realm.Room;
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

public class MH02_SelectUserAdminAdapter extends RecyclerView.Adapter<MH02_SelectUserAdminAdapter.MyViewHolder> {
    private List<Member> listItems = new ArrayList<>();

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
        @BindView(R2.id.txt2)
        AppCompatTextView txt2;
        @BindView(R2.id.linearRoot)
        LinearLayout linearRoot;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
//            img1.setLayoutParams(params);
            /*img1.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

        }
    }

    public void setListItems(ArrayList<Member> list) {
        this.listItems = list;
    }

    public void addListItems(ArrayList<Member> list) {
        int sum = listItems.size();
        this.listItems.addAll(list);
        notifyItemRangeChanged(sum, list.size());
    }

    public void addItem(Member item, int position) {
        try {
            listItems.set(position, item);
            notifyItemChanged(position);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addItem(Member item) {
        listItems.add(item);
        notifyDataSetChanged();

    }

    public void replaceItem(Member item) {
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).getUserId().equals(item.getUserId())) {
                listItems.set(i, item);
                break;
            }
        }

        notifyDataSetChanged();

    }

    private int width = 0;
    private Activity context;
    private String roomId;

    public MH02_SelectUserAdminAdapter(List<Member> list, Activity context, String id) {
        this.listItems = list;
        this.context = context;
        this.roomId = id;
        width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_02_select_user_row, parent, false);

        return new MH02_SelectUserAdminAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MH02_SelectUserAdminAdapter.MyViewHolder holder, final int position) {
        final Member item = listItems.get(position);

        //set image
        if (item.getUserInfo() != null) {
            if (!TextUtils.isEmpty(item.getUserInfo().getAvatar())) {
                Picasso.get()
                        .load(item.getUserInfo().getAvatar())
                        .centerCrop()
                        .resize(width, width)
                        .transform(new CircleTransform(context))
                        .placeholder(R.drawable.icon_no_image)
                        .into(holder.img1);
            }
            holder.txt1.setText(item.getUserInfo().getName());
        }

        if (item.isOwner()) {
            holder.txt2.setText(R.string.owner);
        } else {
            if (item.isAdmin()) {
                holder.txt2.setText(R.string.admin);
            } else {
                holder.txt2.setText(R.string.user);
            }
        }

        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(R.string.confirm_ban_user);
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
                        intent.putExtra(com.muabannhanh.chat.model.UserChat.IS_BANNED_USER, true);
                        intent.putExtra(com.muabannhanh.chat.model.UserChat.USER_ID, Long.parseLong(item.getUserId()+""));
                        context.sendBroadcast(intent);
                        context.finish();
                    }
                });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();*/

                //chuyen qua man hinh set quyen admin cho user
                Intent intent = new Intent(context, MH04_AdminDetailActivity.class);
                intent.putExtra(Member.MEMBER, item);
                intent.putExtra(Room.ROOM_ID, roomId);
                intent.putExtra(Member.IS_ADD_NEW_ADMIN, true);
                context.startActivity(intent);
                context.finish();


            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


}
