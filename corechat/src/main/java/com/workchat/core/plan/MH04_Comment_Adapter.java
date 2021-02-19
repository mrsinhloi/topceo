package com.workchat.core.plan;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.widgets.CircleTransform;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrPhuong on 2016-10-31.
 */

public class MH04_Comment_Adapter extends RecyclerView.Adapter<MH04_Comment_Adapter.MyViewHolder> {

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mh04_comment_row, parent, false);

        return new MH04_Comment_Adapter.MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.img1)
        ImageView img1;
        @BindView(R2.id.txt1)
        TextView txt1;
        @BindView(R2.id.txt2)
        TextView txt2;
        @BindView(R2.id.txt3)
        TextView txt3;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private ArrayList<Comment> items = new ArrayList<>();

    public void setList(ArrayList<Comment> list) {
        this.items = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void addMore(ArrayList<Comment> list) {
        if (list != null && list.size() > 0) {
            int sum = items.size();
            this.items.addAll(sum, list);
            notifyItemRangeInserted(sum, list.size());
        }
    }

    public void addItem(Comment item, int position) {
        try {
            items.set(position, item);
            notifyItemInserted(position);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addItem(Comment item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void replaceItem(Comment item) {

        boolean isExist = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUserId() == item.getUserId()) {
                items.set(i, item);
                isExist = true;
                break;
            }
        }
        if (isExist == false) {
            items.add(item);
        }

        notifyDataSetChanged();

    }

    private int findPosition(String id) {
        int position = -1;
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getUserId()==id) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public void removeItem(String id) {

        int position = findPosition(id);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void addItemOnTop(Comment userInfo) {
        if (userInfo != null) {
            int position = findPosition(userInfo.getUserId());
            if (position > -1) {//co thi thay the
                items.set(position, userInfo);
                notifyItemChanged(position);
            } else {//ko co thi them vao dau
                items.add(0, userInfo);
                notifyItemInserted(0);
            }
        }

    }


    private int width = 100;
    private Context context;
    private UserChatCore user;
    private Room room;

    public MH04_Comment_Adapter(ArrayList<Comment> list, Room room, Context context) {
        this.context = context;
        width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);
        user = ChatApplication.Companion.getUser();
        items = list;
        this.room = room;

    }


    public ArrayList<Comment> getItems() {
        return items;
    }

    @Override
    public void onBindViewHolder(final MH04_Comment_Adapter.MyViewHolder holder, final int position) {

        final Comment item = items.get(position);
        holder.txt2.setText(item.getMessage());
        //set image

        String avatar = "";
        Member member = getMember(item.getUserId());
        if (member != null) {
            if (member.getUserInfo() != null) {
                avatar = member.getUserInfo().getAvatar();
                holder.txt1.setText(member.getUserInfo().getName());
            }
        }
        if (!TextUtils.isEmpty(avatar)) {
            Picasso.get()
                    .load(avatar)
                    .centerCrop()
                    .resize(width, width)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.icon_no_image)
                    .into(holder.img1);
        } else {
            holder.img1.setImageResource(R.drawable.icon_no_image);
        }

        holder.txt3.setText(MyUtils.getDateNotify(item.getCreateDate()));


    }

    private Member getMember(String userId) {
        Member member = null;
        if (room != null && room.getMembers() != null && room.getMembers().size() > 0) {
            for (int i = 0; i < room.getMembers().size(); i++) {
                Member m = room.getMembers().get(i);
                if (m.getUserId().equals(userId)) {
                    member = m;
                    break;
                }
            }
        }
        if (member == null && user != null) {//la tin cua minh
            member = new Member();
            member.setUserId(user.get_id());
            UserChat info = new UserChat();
            info.setUserId(user.get_id());
            info.setName(user.getName());
            info.setAvatar(user.getAvatar());
            member.setUserInfo(info);

        }
        return member;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////


}
