package com.topceo.socialview.commons.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.socialview.commons.socialview.Mentionable;

/**
 * Default adapter for displaying mention in {@link SocialAutoCompleteTextView}.
 * Note that this adapter is completely optional, any adapter extending
 * {@link android.widget.ArrayAdapter} can be attached to {@link SocialAutoCompleteTextView}.
 */
public class MentionArrayAdapter<T extends Mentionable> extends SocialArrayAdapter<T> {

    private final int defaultAvatar;
    private int avatarSize = 100;
    public MentionArrayAdapter(@NonNull Context context) {
        this(context, R.drawable.socialview_ic_mention_placeholder);
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium_smaller);
    }

    public MentionArrayAdapter(@NonNull Context context, @DrawableRes int defaultAvatar) {
        super(context, R.layout.socialview_layout_mention, R.id.socialview_mention_username);
        this.defaultAvatar = defaultAvatar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.socialview_layout_mention, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final T item = getItem(position);
        if (item != null) {
            holder.usernameView.setText(item.getUsername());

            final CharSequence displayname = item.getDisplayname();
            if (!TextUtils.isEmpty(displayname)) {
                holder.displaynameView.setText(displayname);
                holder.displaynameView.setVisibility(View.VISIBLE);
            } else {
                holder.displaynameView.setVisibility(View.GONE);
            }

             Object obj = item.getAvatar();
            if(obj!=null){
                String avatar = (String)obj;
                if(!TextUtils.isEmpty(avatar)){
                    Glide.with(getContext())
                            .load(avatar)//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                            .placeholder(R.drawable.socialview_ic_mention_placeholder)
                            .override(avatarSize, avatarSize)
                            .transform(new GlideCircleTransform(getContext()))
                            .into(holder.avatarView);
                }
            }

            /*final Object avatar = item.getAvatar();
            final RequestCreator request;
            if (avatar == null) {
                request = Picasso.get().load(defaultAvatar);
            } else if (avatar instanceof Integer) {
                request = Picasso.get().load((int) avatar);
            } else if (avatar instanceof String) {
                request = Picasso.get().load((String) avatar);
            } else if (avatar instanceof Uri) {
                request = Picasso.get().load((Uri) avatar);
            } else if (avatar instanceof File) {
                request = Picasso.get().load((File) avatar);
            } else {
                throw new UnsupportedOperationException("Unknown avatar type");
            }
            request.error(defaultAvatar)
                .transform(new CropCircleTransformation())
                .into(holder.avatarView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.loadingView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.loadingView.setVisibility(View.GONE);
                    }
                });*/
        }
        return convertView;
    }

    private static class ViewHolder {
        private final ImageView avatarView;
        private final ProgressBar loadingView;
        private final TextView usernameView;
        private final TextView displaynameView;

        ViewHolder(View itemView) {
            avatarView = itemView.findViewById(R.id.socialview_mention_avatar);
            loadingView = itemView.findViewById(R.id.socialview_mention_loading);
            usernameView = itemView.findViewById(R.id.socialview_mention_username);
            displaynameView = itemView.findViewById(R.id.socialview_mention_displayname);
        }
    }

}