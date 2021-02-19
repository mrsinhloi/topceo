package com.topceo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.db.TinyDB;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;

/**
 * Created by MrPhuong on 2017-02-02.
 * /**
 * https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
 * https://github.com/wasabeef/recyclerview-animators
 * <p/>
 * notifyItemChanged(int pos)	Notify that item at position has changed.
 * notifyItemInserted(int pos)	Notify that item reflected at position has been newly inserted.
 * notifyItemRemoved(int pos)	Notify that items previously located at position has been removed from the data set.
 * notifyDataSetChanged()	    Notify that the dataset has changed. Use only as last resort.
 * rvContacts.scrollToPosition(0);   // index 0 position
 *
 */

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {
    public ArrayList<User> getmDataset() {
        return mDataset;
    }

    private ArrayList<User> mDataset = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txt1, txt2;
        public ImageView img1;
        public ToggleButton btn;

        public ViewHolder(View v) {
            super(v);
            txt1 = (TextView) v.findViewById(R.id.textView1);
            txt2 = (TextView) v.findViewById(R.id.textView2);

            img1 = (ImageView) v.findViewById(R.id.imageView1);

            img1.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
            btn = (ToggleButton) v.findViewById(R.id.button1);

            // set interpolators for both expanding and collapsing animations
//                txt5.setInterpolator(new OvershootInterpolator());


        }
    }


    public void add(int position, User item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void remove(User item) {
        int position = -1;//= mDataset.indexOf(item);
        for (int i = 0; i < mDataset.size(); i++) {
            if (item.getUserId() == mDataset.get(i).getUserId()) {
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
    private int avatarSize = 0;
    private int widthScreen = 0;
    private Context context;

    private User user;
    private TinyDB db;
    public SuggestAdapter(ArrayList<User> myDataset, Context context) {
        this.context=context;
        mDataset = myDataset;
        widthScreen = MyUtils.getScreenWidth(context);
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_suggest);

        //////////////////
        realm=Realm.getDefaultInstance();
        db=new TinyDB(context);
        Object obj=db.getObject(User.USER, User.class);
        if(obj!=null) {
            user = (User)obj;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SuggestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_like_row_horizontal, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final User item = mDataset.get(position);


        Glide.with(context)
                .load(item.getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                .placeholder(R.drawable.ic_no_avatar)
                .override(avatarSize, avatarSize)
                .transform(new GlideCircleTransform(context))
                .into(holder.img1);


        holder.txt1.setText(item.getUserName());

        if (!TextUtils.isEmpty(item.getFullName())) {
            holder.txt2.setText(item.getFullName());
            holder.txt2.setVisibility(View.VISIBLE);
        } else {
            holder.txt2.setVisibility(View.INVISIBLE);
        }

        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.gotoProfile(item.getUserId(), context);
            }
        });
        holder.txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.img1.performClick();
            }
        });
        holder.txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.img1.performClick();
            }
        });

        //////////////////////////////////////////////////////////////////////


        holder.btn.setOnCheckedChangeListener(null);
        setStateButton(item, holder.btn);
        holder.btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*if (b) {//on following
                    holder.btn.setTextColor(context.getResources().getColor(R.color.black));
                } else {//off follow
                    holder.btn.setTextColor(context.getResources().getColor(R.color.white));
                }*/


                //update server
                Webservices.updateUserFollowingState(item.getUserId(), b).continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {
                        if (task.getError() == null) {
                            if (task.getResult() != null) {
                                boolean isOK = (boolean) task.getResult();
                                if (isOK) {
//                                            MyUtils.showToast(context, R.string.update_success);

                                    //refresh giao dien
//                                    notifyItemChanged(position);

                                }
                            }
                        } else {
                            MyUtils.showToast(context, task.getError().getMessage());
                        }

                        return null;
                    }
                });


            }
        });


    }

    private void setStateButton(User uLike, ToggleButton btn) {

        //neu user like = owner thi an button
        if (uLike.getUserId() == user.getUserId()) {
            btn.setVisibility(View.INVISIBLE);
        } else {
            btn.setVisibility(View.VISIBLE);
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
    public void addAll(ArrayList<User> list) {
        mDataset.addAll(list);
        notifyDataSetChanged();
    }

    public User getLastestItem() {
        User item = null;
        if (getItemCount() > 0) {
            item = mDataset.get(getItemCount() - 1);
        }
        return item;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private Realm realm;




}
