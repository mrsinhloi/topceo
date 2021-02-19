package com.workchat.core.channel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
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

public class MH03_AdminUserAdapter extends RecyclerView.Adapter<MH03_AdminUserAdapter.MyViewHolder> {
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
        @BindView(R2.id.btn1)
        AppCompatButton btn1;
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

        boolean isExist = false;
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).getUserId().equals(item.getUserId())) {
                listItems.set(i, item);
                isExist = true;
                break;
            }
        }
        if (isExist == false) {
            listItems.add(item);
        }

        notifyDataSetChanged();

    }

    private int width = 0;
    private Activity context;
    private String roomId;

    public MH03_AdminUserAdapter(List<Member> list, Activity context, String roomId) {
        this.context = context;
        width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);
        this.roomId = roomId;

        listItems.clear();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Member item = list.get(i);
                if ((item.isAdmin() || item.isOwner())) {
                    listItems.add(item);
                }
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_03_admin_user_row, parent, false);

        return new MH03_AdminUserAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MH03_AdminUserAdapter.MyViewHolder holder, final int position) {
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


            if (item.isAdmin()) {
                holder.txt2.setText(R.string.admin);
            }

            if (item.isOwner()) {
                holder.txt2.setText(R.string.owner);
            }

            if (item.isOwner()) {
                holder.btn1.setVisibility(View.GONE);
            } else {
                holder.btn1.setVisibility(View.VISIBLE);
            }
            holder.btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(R.string.confirm_delete_admin_user);
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

                            Intent intent = new Intent(MH03_AdminUserActivity.ACTION_REMOVE_ADMIN);
                            intent.putExtra(Member.MEMBER, item);
                            intent.putExtra(Room.ROOM_ID, roomId);
                            context.sendBroadcast(intent);
                        }
                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

        }


        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //vao edit quyen
                //chuyen qua man hinh set quyen admin cho user
                Intent intent = new Intent(context, MH04_AdminDetailActivity.class);
                intent.putExtra(Member.MEMBER, item);
                intent.putExtra(Room.ROOM_ID, roomId);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


}
