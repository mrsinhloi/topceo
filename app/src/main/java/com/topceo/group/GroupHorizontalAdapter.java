package com.topceo.group;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.topceo.R;
import com.topceo.db.TinyDB;
import com.topceo.group.models.GroupInfo;
import com.topceo.objects.other.User;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GroupHorizontalAdapter extends RecyclerView.Adapter<GroupHorizontalAdapter.ViewHolder> {
    public ArrayList<GroupInfo> getmDataset() {
        return mDataset;
    }

    private ArrayList<GroupInfo> mDataset = new ArrayList<>();

    int distance = 0;

    public void showTotal(int total) {
//        total += 20;
        if (total > 0) {
            int view = getItemCount();
            distance = total - view;
            if (distance > 0) {
                notifyItemChanged(getItemCount() - 1);
            }
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        @BindView(R.id.constraintLayout)
        ConstraintLayout cardView;
        @BindView(R.id.imageView1)
        ImageView img1;
        @BindView(R.id.textView1)
        TextView txt1;
        @BindView(R.id.txtNumber)
        TextView txtNumber;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
        }
    }


    public void add(int position, GroupInfo item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void remove(GroupInfo item) {
        int position = -1;//= mDataset.indexOf(item);
        for (int i = 0; i < mDataset.size(); i++) {
            if (item.getGroupId() == mDataset.get(i).getGroupId()) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void remove(int position) {
        if (position >= 0) {
            mDataset.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    private int corners = 8;
    private int avatarSize = 0;
    private int widthScreen = 0;
    private Context context;

    private User user;
    private TinyDB db;

    public GroupHorizontalAdapter(ArrayList<GroupInfo> myDataset, Context context) {
        this.context = context;
        mDataset = myDataset;
        widthScreen = MyUtils.getScreenWidth(context);
        avatarSize = widthScreen / 5;
        corners = context.getResources().getDimensionPixelOffset(R.dimen.corners_group_image);

        //////////////////
        db = new TinyDB(context);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupHorizontalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_horizontal, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final GroupInfo item = mDataset.get(position);

        if (!TextUtils.isEmpty(item.getCoverUrl())) {
            Glide.with(context)
                    .load(item.getCoverUrl())
                    .placeholder(R.drawable.no_media_small)
                    .override(avatarSize, avatarSize)
                    .transform(new CenterCrop(), new RoundedCorners(corners))
                    .into(holder.img1);
        } else {
            Glide.with(context)
                    .load(R.drawable.no_media_small)
                    .override(avatarSize, avatarSize)
                    .transform(new CenterCrop(), new RoundedCorners(corners))
                    .into(holder.img1);
        }


        holder.txt1.setText(item.getGroupName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == getItemCount() - 1) {
                    //all groups
                    AllGroupActivity.Companion.openActivity(context,0);
                } else {
                    GroupDetailActivity.Companion.openActivity(context, item);
                }
            }
        });

        holder.txtNumber.setVisibility(View.GONE);
        if (position == getItemCount() - 1) {
            //neu la phan tu cuoi cung thi hien thi so luong group
            holder.txtNumber.setVisibility(View.VISIBLE);
            holder.txtNumber.setText(R.string.seemore);
        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //add by mr.pham
    //clear all items
    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    //add list items
    public void addAll(ArrayList<GroupInfo> list) {
        mDataset.addAll(list);
        notifyDataSetChanged();
    }

    public void update(ArrayList<GroupInfo> list) {
        mDataset = list;
        notifyDataSetChanged();
    }

    public GroupInfo getLastestItem() {
        GroupInfo item = null;
        if (getItemCount() > 0) {
            item = mDataset.get(getItemCount() - 1);
        }
        return item;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////


}
