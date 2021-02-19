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
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

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
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private ArrayList<User> mDataset = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.textView1)
        TextView txt1;
        @BindView(R.id.textView2)
        TextView txt2;
        @BindView(R.id.button1)
        ToggleButton btn;

        @BindView(R.id.imageView1)
        ImageView imgAvatar;
        @BindView(R.id.linear1)
        LinearLayout linear1;

        //expand
        @BindView(R.id.linearImages)
        LinearLayout linearImages;
        @BindView(R.id.img1)
        ImageView img1;
        @BindView(R.id.img2)
        ImageView img2;
        @BindView(R.id.img3)
        ImageView img3;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            imgAvatar.setLayoutParams(new LinearLayout.LayoutParams(avatarSize, avatarSize));
        }

        private int columns = 3;
        private int imageWidth = widthScreen / columns;
        private int imageHeight = widthScreen / columns;

        private void loadImage(ImageView img, String url) {
            if (!TextUtils.isEmpty(url)) {
                Glide.with(context)
                        .load(url)//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                        .placeholder(R.drawable.no_media_small)
                        .override(imageWidth, imageHeight)
                        .centerCrop()
                        .into(img);
            }
        }

        private void setImageCount(ArrayList<ImageItem> list) {
            //loai bo phan ti ko co hinh
            if (list != null && list.size() > 0) {
                //loai bo phan tu ko co anh
                for (int i = list.size() - 1; i >= 0; i--) {
                    ImageItem item = list.get(i);
                    if(TextUtils.isEmpty(item.getImageSmall())){
                        //ko co hinh thi xoa bo
                        list.remove(i);
                    }
                }
            }

            //set image
            if (list != null && list.size() > 0) {
                int count = list.size();
                if (count > 0 && count <= 3) {
                    switch (count) {
                        case 0:
                            linearImages.setVisibility(View.GONE);
                            break;

                        case 1:
                            linearImages.setVisibility(View.VISIBLE);
                            img2.setVisibility(View.GONE);
                            img3.setVisibility(View.GONE);

                            imageWidth = widthScreen / count;
                            img1.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));

                            String url1 = list.get(0).getImageSmall();
                            loadImage(img1, url1);
                            break;
                        case 2:
                            linearImages.setVisibility(View.VISIBLE);
                            img3.setVisibility(View.GONE);

                            imageWidth = widthScreen / count;
                            img1.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
                            img2.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));

                            url1 = list.get(0).getImageSmall();
                            loadImage(img1, url1);

                            String url2 = list.get(1).getImageSmall();
                            loadImage(img2, url2);
                            break;
                        case 3:
                            linearImages.setVisibility(View.VISIBLE);
                            img1.setVisibility(View.VISIBLE);
                            img2.setVisibility(View.VISIBLE);
                            img3.setVisibility(View.VISIBLE);

                            imageWidth = widthScreen / count;
                            img1.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
                            img2.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
                            img3.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));

                            url1 = list.get(0).getImageSmall();
                            loadImage(img1, url1);

                            url2 = list.get(1).getImageSmall();
                            loadImage(img2, url2);

                            String url3 = list.get(2).getImageSmall();
                            loadImage(img3, url3);


                            break;
                    }
                } else {
                    linearImages.setVisibility(View.GONE);
                }
            } else {
                linearImages.setVisibility(View.GONE);
            }
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
    private long ownerId = -1;

    public FollowAdapter(ArrayList<User> myDataset, Context context, long ownerId) {
        mDataset = myDataset;
        this.context = context;
        this.ownerId = ownerId;
        widthScreen = MyUtils.getScreenWidth(context);
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_list_item);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FollowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_like_row_expand, parent, false);
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

        if (item.getImageItems() != null && item.getImageItems().size() > 0) {
            holder.setImageCount(item.getImageItems());
        } else {
            holder.setImageCount(null);
        }

        Glide.with(context)
                .load(item.getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                .placeholder(R.drawable.ic_no_avatar)
                .override(avatarSize, avatarSize)
                .transform(new GlideCircleTransform(context))
                .into(holder.imgAvatar);


        holder.txt1.setText(item.getUserName());

        if (!TextUtils.isEmpty(item.getFullName())) {
            holder.txt2.setText(item.getFullName());
            holder.txt2.setVisibility(View.VISIBLE);
        } else {
            holder.txt2.setVisibility(View.GONE);
        }

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.gotoProfile(item.getUserName(), context);
            }
        });
        holder.linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imgAvatar.performClick();
            }
        });


        //thong tin user da like
        final User uLike = item;
        holder.btn.setOnCheckedChangeListener(null);
        setStateButton(uLike, holder.btn);
        holder.btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*if (b) {//on following
                    holder.btn.setTextColor(context.getResources().getColor(R.color.black));
                } else {//off follow
                    holder.btn.setTextColor(context.getResources().getColor(R.color.white));
                }*/


                //update server
                Webservices.updateUserFollowingState(uLike.getUserId(), b).continueWith(new Continuation<Object, Void>() {
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
        /*holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
                    //set khong quan tam
                    //bo follow
                    Webservices.updateUserFollowingState(uLike.getUserId(), false).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if (task.getError() == null) {
                                if (task.getResult() != null) {
                                    boolean isOK = (boolean) task.getResult();
                                    if (isOK) {
//                                            MyUtils.showToast(context, R.string.update_success);

                                        //refresh giao dien
                                        notifyItemChanged(position);

                                    }
                                }
                            } else {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }

                            return null;
                        }
                    });
                } else {//set da quan tam
                    //set follow
                    Webservices.updateUserFollowingState(uLike.getUserId(), true).continueWith(new Continuation<Object, Void>() {
                        @Override
                        public Void then(Task<Object> task) throws Exception {
                            if (task.getError() == null) {
                                if (task.getResult() != null) {
                                    boolean isOK = (boolean) task.getResult();
                                    if (isOK) {
//                                            MyUtils.showToast(context, R.string.update_success);

                                        //refresh giao dien
                                        notifyItemChanged(position);

                                    }
                                }
                            } else {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }

                            return null;
                        }
                    });
                }
            }
        });*/

    }



    /**
     * So voi owner la co dang theo doi nguoi nay hay khong
     *
     * @param uLike
     * @param btn
     */
    private void setStateButton(User uLike, ToggleButton btn) {
        //neu user like = owner thi an button
        if (uLike.getUserId() == ownerId) {
            btn.setVisibility(View.INVISIBLE);
        } else {
            btn.setVisibility(View.VISIBLE);
            if (uLike != null) {
                if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
//                    btn.setText(context.getText(R.string.following_title));
//                    btn.setBackgroundResource(R.drawable.bg_rectangle_2_fill);
                    btn.setChecked(true);
//                    btn.setTextColor(context.getResources().getColor(R.color.black));
                } else {
//                    btn.setText(context.getText(R.string.follow_title));
//                    btn.setBackgroundResource(R.drawable.bg_rectangle_1_fill);
                    btn.setChecked(false);
//                    btn.setTextColor(context.getResources().getColor(R.color.white));
                }
            }
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


    public void addAllCheckDuplicate(ArrayList<User> list) {
        if (mDataset != null) {
            if (mDataset.size() == 0) {
                mDataset.addAll(list);
                notifyDataSetChanged();
            } else {

                //tim ptu ko trung trong list
                ArrayList<User> temp = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    User child = list.get(i);
                    if (!isExist(child)) {
                        temp.add(child);
                    }
                }

                if (temp.size() > 0) {
                    int size = mDataset.size();
                    mDataset.addAll(temp);
                    notifyItemRangeInserted(size, temp.size());
                }
            }
        }

    }

    public boolean isExist(User item) {
        boolean result = false;
        for (int i = 0; i < mDataset.size(); i++) {
            if (item.getUserId() == mDataset.get(i).getUserId()) {
                result = true;
                break;
            }
        }
        return result;
    }

}
