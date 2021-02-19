package com.workchat.core.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Picasso;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.mbn.models.UserChatCore;
import com.workchat.core.models.chat.ChatView;
import com.workchat.core.models.chat.Member;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserChat;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by MrPhuong on 2017-09-21.
 */

public class ListAvatarAdapter extends RecyclerView.Adapter<ListAvatarAdapter.ItemHolder> {

    private int avatarSizeSmall = 40;
    private List<ChatView> data = new ArrayList<>();
    private UserChatCore owner;
    private String ownerId;
    private List<Member> members = new ArrayList<>();

    public ListAvatarAdapter(Context context, List<ChatView> list, Room room) {

        avatarSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_small);

        try {

            //loai bo owner ra khoi list member
            owner = ChatApplication.Companion.getUser();
            ownerId = owner.get_id();
            members.clear();
            members.addAll(room.getMembers());
            if (members != null && members.size() > 0) {
                for (int i = 0; i < members.size(); i++) {
                    if (members.get(i).getUserId().equals(ownerId)) {
                        members.remove(i);
                        break;
                    }
                }
            }

            //loai bo user chua xem, va loai bo chinh minh, hien thi user da xem
            for (int i = list.size() - 1; i >= 0; i--) {
                ChatView v = list.get(i);
                if (!v.isViewed() || v.getUserId().equals(ownerId)) {
                    list.remove(i);
                }
            }

            this.data = list;

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public ListAvatarAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_row_avatar, parent, false);
        return new ItemHolder(itemView);
    }

    private UserChat findByUserId(String userId) {
        UserChat user = null;
        if (members != null && members.size() > 0) {
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).getUserId().equals(userId)) {
                    user = members.get(i).getUserInfo();
                    break;
                }
            }
        }
        return user;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        try {

            //danh sach toan bo la viewed=true
            if (data != null && position < data.size()) {
                ChatView view = data.get(position);
                if (view.isViewed()) {
                    holder.img1.setVisibility(View.VISIBLE);
                    UserChat user = findByUserId(view.getUserId());

                    if(user != null){
                        if (!TextUtils.isEmpty(user.getAvatar())) {
                            Picasso.get()
                                    .load(user.getAvatar())
                                    .resize(avatarSizeSmall, avatarSizeSmall)
                                    .centerCrop()
                                    .transform(new CropCircleTransformation())
                                    .error(R.drawable.ic_user_2)
                                    .into(holder.img1);

                        }else{
                            ColorGenerator generator = ColorGenerator.MATERIAL;
                            int color = generator.getColor(user.getName());

                            TextDrawable drawable = TextDrawable.builder()
                                    .buildRound(user.getName().substring(0,1), color);
                            holder.img1.setImageDrawable(drawable);
                        }
                    }

                } else {
                    holder.img1.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.img1)
        ImageView img1;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
