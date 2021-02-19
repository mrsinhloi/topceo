package com.topceo.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.image.ImageCommentLike;

import java.util.ArrayList;


public class CommentUserLikedAdapter extends RecyclerView.Adapter<CommentUserLikedAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private ArrayList<ImageCommentLike> data = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;

    private int avatarSize = 48;

    // Pass in the contact array into the constructor
    public CommentUserLikedAdapter(ArrayList<ImageCommentLike> list, Context context) {
        data = list;
        mContext = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);

    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public CommentUserLikedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.activity_comment_user_liked_row, parent, false);

        // Return a new holder instance
        CommentUserLikedAdapter.ViewHolder viewHolder = new CommentUserLikedAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CommentUserLikedAdapter.ViewHolder holder, int position) {
        final ImageCommentLike item = data.get(position);
        holder.txt.setText(item.getUserName());
        Glide.with(mContext)
                .load(item.getAvatarSmall())
                .placeholder(R.drawable.ic_no_avatar)
                .override(avatarSize, avatarSize)
                .transform(new GlideCircleTransform(mContext))
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ImageCommentLike getLastestItem() {
        if(data!=null && data.size()>0){
            return data.get(data.size()-1);
        }
        return null;
    }

    public void add(ArrayList<ImageCommentLike> list) {
        int size = data.size();
        data.addAll(list);
        notifyItemRangeInserted(size, list.size());
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView txt;
        public ImageView img;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder1 instance.
            super(itemView);

            txt = (TextView) itemView.findViewById(R.id.textView1);
            img = (ImageView) itemView.findViewById(R.id.imageView1);
        }
    }
}
