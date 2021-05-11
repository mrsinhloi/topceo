package com.topceo.comments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.eventbus.EventMediaComment;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.mediaplayer.audio.MediaPlayerActivity;
import com.topceo.mediaplayer.video.VideoActivity;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.MediaComment;
import com.topceo.socialview.core.widget.SocialTextView;
import com.topceo.socialview.core.widget.SocialView;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.workchat.core.plan.Comment;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MrPhuong on 2016-10-31.
 * http://android-pratap.blogspot.com/2015/12/sectioned-recyclerview-in-android_1.html
 * https://github.com/afollestad/sectioned-recyclerview
 */

public class CommentAdapterSectionParent_MediaComment extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private ArrayList<MediaComment> listItems = new ArrayList<>();

    public void setListItems(ArrayList<MediaComment> list) {
        this.listItems = list;
    }

    public void addListItems(ArrayList<MediaComment> list) {
        int sum = listItems.size();
        this.listItems.addAll(list);
        notifyItemRangeChanged(sum, list.size());
    }

    public void addItem(MediaComment item, int position) {
        listItems.add(position, item);
        notifyDataSetChanged();
        notifyItemChanged(position);
    }

    /**
     * Thay the mot phan tu
     *
     * @param obj
     */
    public void updateItem(MediaComment obj) {
        for (int i = 0; i < listItems.size(); i++) {
            if (listItems.get(i).getItemId() == obj.getItemId()) {
                listItems.set(i, obj);
                notifyItemChanged(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public MediaComment getLastestItem() {
        if (listItems != null && listItems.size() > 0) {
            return listItems.get(listItems.size() - 1);
        }
        return null;
    }

    public ArrayList<MediaComment> getAllItem() {
        return listItems;
    }

    public void clear() {
        if (listItems != null && listItems.size() > 0) {
            listItems.clear();
            notifyDataSetChanged();
        }
    }

    public void add(int position, MediaComment comment) {
        if (listItems != null) {
            listItems.add(position, comment);
            notifyItemInserted(position);
        }
    }

    private int findPostionParent(long id) {
        int position = -1;
        if (listItems != null && listItems.size() > 0) {
            for (int i = 0; i < listItems.size(); i++) {
                MediaComment c = listItems.get(i);
                if (c.getItemId() == id) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public void add(MediaComment comment) {
        if (listItems != null) {
            if (comment.getReplyToId() > 0) {//child
                //tim cha
                int position = findPostionParent(comment.getReplyToId());

                //add vao list cua cha
                if (position >= 0) {
                    listItems.get(position).getListChild().add(0, comment);
//                    notifyItemChanged(position);
                    notifyDataSetChanged();
                }
            } else {//parent add vao vi tri 0
                listItems.add(0, comment);
                notifyItemInserted(0);
            }


        }
    }

    public class MyViewHolderItem extends RecyclerView.ViewHolder {

        @BindView(R.id.img1)
        ImageView img1;
        @BindView(R.id.txt1)
        TextView txt1;
        @BindView(R.id.txt2)
        SocialTextView txt2;
        @BindView(R.id.txt3)
        TextView txt3;
        @BindView(R.id.txt5)
        TextView txt5;
        @BindView(R.id.linear6)
        LinearLayout linearDelete;


        public MyViewHolderItem(View view) {
            super(view);
            ButterKnife.bind(this, view);
            linearDelete.setVisibility(View.GONE);
        }
    }

    public class MyViewHolderSection extends RecyclerView.ViewHolder {

        @BindView(R.id.img1)
        ImageView img1;
        @BindView(R.id.txt1)
        TextView txt1;
        @BindView(R.id.txt2)
        SocialTextView txt2;
        @BindView(R.id.txt3)
        TextView txt3;
        @BindView(R.id.txt4)
        TextView txt4;
        @BindView(R.id.txt5)
        TextView txt5;

        @BindView(R.id.linear1)
        LinearLayout linear1;
        @BindView(R.id.linear2)
        LinearLayout linear2;
        @BindView(R.id.linear3)
        LinearLayout linear3;
        @BindView(R.id.linear5)
        LinearLayout linear5;

        @BindView(R.id.linear6)
        LinearLayout linearDelete;

        @BindView(R.id.linearReplyMore)
        LinearLayout linearReplyMore;
        @BindView(R.id.txtReplyMoreCount)
        TextView txtReplyMoreCount;

        @BindView(R.id.btnLike)
        ShineButton btnLike;

        public MyViewHolderSection(View view) {
            super(view);
            ButterKnife.bind(this, view);
            linearDelete.setVisibility(View.GONE);
            /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            img1.setLayoutParams(params);
            img1.setScaleType(ImageView.ScaleType.CENTER_CROP);*/

        }
    }

    private int width = 0, widthSmall = 0;
    private Context context;
    private User user;

    public CommentAdapterSectionParent_MediaComment(ArrayList<MediaComment> horizontalList, Context context) {
        this.listItems = horizontalList;
        this.context = context;
        user = MyApplication.getUser();
        width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);
        widthSmall = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_small);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(viewType == VIEW_TYPE_HEADER ? R.layout.activity_comment_row_1 : R.layout.activity_comment_row_1_child, parent, false);

        if (viewType == VIEW_TYPE_HEADER) {
            return new MyViewHolderSection(itemView);
        } else {
            return new MyViewHolderItem(itemView);
        }
    }

    ////////////////////////////////////////////////////////////////////

    @Override
    public int getSectionCount() {
        return listItems.size();
    }

    @Override
    public int getItemCount(int section) {
        return listItems.get(section).getListChild().size();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder1, int section) {
        MediaComment item = listItems.get(section);

        MyViewHolderSection holder = (MyViewHolderSection) holder1;
        //set image
        if (!TextUtils.isEmpty(item.getAvatarSmall())) {

            Glide.with(context)
                    .load(item.getAvatarSmall())
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(width, width)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);
        }

        holder.txt1.setText(item.getUserName());
        holder.txt2.setText(MyUtils.fromHtml(item.getComment()));
        MyUtils.whenClickComment(context, holder.txt2);


        holder.txt3.setText(MyUtils.getTimeAgo(item.getCreateDate() * 1000, context));

        holder.txt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMediaComment(item));
            }
        });

        //txt4 Thich
        if (item.getLikeCount() > 0) {
            holder.linear5.setVisibility(View.VISIBLE);
            String value = item.getLikeCount() + " " + context.getString(R.string.like);
            holder.txt4.setText(value);
            holder.txt4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CommentUserLikedActivity.class);
                    intent.putExtra(MediaComment.COMMENT_ID, item.getItemId());
                    intent.putExtra(MediaComment.IS_MEDIA_COMMENT, true);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.linear5.setVisibility(View.GONE);
            holder.txt4.setText(R.string.like);
        }

        holder.btnLike.setChecked(item.isLiked());
        holder.btnLike.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                setLikeComment(item.getItemId(), checked);
            }
        });

        //so luong child
        int count = item.getReplyCount();
        if (count > 0) {

            if (item.isShowCommentChild()) {
                holder.txtReplyMoreCount.setText(R.string.hide_reply_number);
            } else {
                holder.txtReplyMoreCount.setText(context.getString(R.string.view_reply_number, count));
            }

            holder.linearReplyMore.setVisibility(View.VISIBLE);
            holder.linearReplyMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //set text la an / hien
                    item.setShowCommentChild(!item.isShowCommentChild());
//                    listItems.get(section).setShowCommentChild(!item.isShowCommentChild());


                    if (item.isShowCommentChild()) {//xoa list dang co, load lai tu dau, set text hide
                        try {
                            //clear de ko bi load trung lap
                            listItems.get(section).getListChild().clear();
                            //load childs
                            getComments(item.getImageItemId(), item.getItemId(), 0);
                            //set text la an
                            holder.txtReplyMoreCount.setText(R.string.hide_reply_number);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            //an cau tra loi la xoa danh sach tra loi con
                            //clear
                            listItems.get(section).getListChild().clear();
                            //refresh lai giao dien
//                        notifyItemChanged(section);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        } else {
            holder.linearReplyMore.setVisibility(View.GONE);
        }

        //neu la chu thi cho phep xoa
        if (user.getUserId() == item.getUserId()) {
            holder.linearDelete.setVisibility(View.VISIBLE);
            holder.linearDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                    dialog.setMessage(R.string.confirm_delete_comment);
//                    dialog.setTitle(title);
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            deleteComment(item.getItemId(), section, -1);
                        }
                    });
                    dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    android.app.AlertDialog alertDialog = dialog.create();
                    alertDialog.show();

                    Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    btn.setTextColor(Color.BLACK);
                }
            });
        } else {
            holder.linearDelete.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int section, int relativePosition, int absolutePosition) {
        holder1.setIsRecyclable(false);
        if (listItems.get(section).getListChild().size() > 0) {
            MediaComment item = listItems.get(section).getListChild().get(relativePosition);

            MyViewHolderItem holder = (MyViewHolderItem) holder1;
            //set image
            if (!TextUtils.isEmpty(item.getAvatarSmall())) {
                Glide.with(context)
                        .load(item.getAvatarSmall())
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(width, width)
                        .transform(new GlideCircleTransform(context))
                        .into(holder.img1);
            }

            holder.txt1.setText(item.getUserName());
            holder.txt2.setText(item.getComment());
            holder.txt2.setOnHashtagClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                    MyUtils.showToastDebug(context, text.toString());
                    MyUtils.gotoHashtag(text.toString(), context);
                }
            });
            holder.txt2.setOnMentionClickListener(new SocialView.OnClickListener() {
                @Override
                public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
//                    MyUtils.showToastDebug(context, text.toString());
                    MyUtils.gotoProfile(text.toString(), context);
                }
            });

            //ngay
            holder.txt3.setText(MyUtils.getTimeAgo(item.getCreateDate() * 1000, context));
            //tra loi
            holder.txt5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaComment item = listItems.get(section);
                    EventBus.getDefault().post(new EventMediaComment(item));

                }
            });

            //neu la chu thi cho phep xoa
            if (user.getUserId() == item.getUserId()) {
                holder.linearDelete.setVisibility(View.VISIBLE);
                holder.linearDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
                        dialog.setMessage(R.string.confirm_delete_comment);
//                    dialog.setTitle(title);
                        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                deleteComment(item.getItemId(), section, relativePosition);
                            }
                        });
                        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        android.app.AlertDialog alertDialog = dialog.create();
                        alertDialog.show();

                        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        btn.setTextColor(Color.BLACK);
                    }
                });
            } else {
                holder.linearDelete.setVisibility(View.GONE);
            }


        }
    }

    private void getComments(long mediaId, long replyToId, long lastId) {

        MyApplication.apiManager.mediaCommentReplyList(
                mediaId,
                replyToId,
                (lastId == 0) ? null : String.valueOf(lastId),
                VideoActivity.ITEM_COUNT,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<MediaComment>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<MediaComment> comments = (ArrayList<MediaComment>) result.getData();
                                    if (comments.size() > 0) {

                                        //tim vi tri cha
                                        int position = findPostionParent(replyToId);
                                        if (position >= 0) {
                                            if (lastId == 0) {//page 1
                                                listItems.get(position).getListChild().addAll(comments);
                                                notifyDataSetChanged();
                                            } else {//load more
                                                listItems.get(position).getListChild().addAll(comments);
                                                notifyDataSetChanged();
                                            }

                                            //load tiep den khi het
                                            MediaComment last = comments.get(comments.size() - 1);
                                            if (last != null) {
                                                getComments(mediaId, replyToId, last.getItemId());
                                            }
                                        }

                                    } else {
                                        //ko con load more
                                    }

                                    comments.clear();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        MyUtils.log("error");
                    }
                });

    }


    public void setLikeComment(long commentId, boolean isLiked) {
        if (isLiked) {
            MyApplication.apiManager.mediaCommentLike(commentId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null) {
                        parseJson(obj.toString(), commentId, isLiked);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        } else {
            MyApplication.apiManager.mediaCommentUnlike(commentId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null) {
                        parseJson(obj.toString(), commentId, isLiked);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }

    }


    private void parseJson(String json, long commentId, boolean isLiked) {
        if (json != null) {
            ReturnResult result = Webservices.parseJson(json, Boolean.class, false);

            if (result != null) {
                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                    MyUtils.showToastDebug(context, "Liked...");
                    //tim vi tri va show lai trang thai
                    int position = findPostionParent(commentId);
                    if (position >= 0) {
                        listItems.get(position).setLiked(isLiked);
                        int likeCount = listItems.get(position).getLikeCount();
                        if (isLiked) {
                            likeCount += 1;
                        } else {
                            likeCount -= 1;
                            if (likeCount < 0) {
                                likeCount = 0;
                            }
                        }
                        listItems.get(position).setLikeCount(likeCount);
                        notifyItemChanged(position);
                    }
                } else {
                    MyUtils.showToastDebug(context, result.getErrorMessage());
                }
            }
        }
    }


    public void deleteComment(long commentId, int section, int positionChild) {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.mediaCommentDelete(commentId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null) {
                        ReturnResult result = Webservices.parseJson(obj.toString(), Boolean.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                MyUtils.showToast(context, R.string.delete_success);

                                int numberRemove = 1;
                                if (positionChild == -1) {
                                    if(getItemCount(section)>0){
                                        numberRemove = getItemCount(section) + 1;
                                    }
                                    //xoa cha theo section
                                    listItems.remove(section);
                                    notifyDataSetChanged();

                                } else {
                                    //xoa con
                                    listItems.get(section).getListChild().remove(positionChild);
                                    notifyDataSetChanged();
                                    numberRemove = 1;
                                }

                                Intent intent = new Intent(MediaPlayerActivity.ACTION_REMOVE_COMMENT);
                                intent.putExtra(Comment.NUMBER_COMMENT, numberRemove);
                                context.sendBroadcast(intent);
                            } else {
                                MyUtils.showToastDebug(context, result.getErrorMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        } else {
            MyUtils.showThongBao(context);
        }

    }
}
