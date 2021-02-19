package com.topceo.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.topceo.R;
import com.topceo.group.models.GroupInfo;
import com.topceo.objects.other.User;
import com.topceo.post.PostLikeFacebookActivity;

public class ViewHolder7 extends RecyclerView.ViewHolder {

    public LinearLayout linearAddPost;
    public ImageView imgAvatar;
    long groupId = 0;
    public ViewHolder7(View v, long groupId) {
        super(v);
        this.groupId = groupId;
        linearAddPost = (LinearLayout) v.findViewById(R.id.linearAddPost);
        imgAvatar = (ImageView) v.findViewById(R.id.imgAvatar);
    }

    public void bind(Context context, int avatarSize, User owner) {
        if (context != null) {
            linearAddPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostLikeFacebookActivity.class);
                    intent.putExtra(GroupInfo.GROUP_ID, groupId);//group admin
                    context.startActivity(intent);
                }
            });

            if (owner != null) {
                int size = context.getResources().getDimensionPixelSize(R.dimen.ic_size_36);
                Glide.with(context)
                        .load(owner.getAvatarSmall())
                        .override(size, size)
                        .transform(new CenterCrop(), new RoundedCorners(size / 2))
                        .into(imgAvatar);
            }
        }
    }
}
