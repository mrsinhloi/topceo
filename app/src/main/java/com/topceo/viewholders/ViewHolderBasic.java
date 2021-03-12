package com.topceo.viewholders;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.topceo.R;
import com.topceo.activity.MH11_LikeActivity;
import com.topceo.config.MyApplication;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.Info;
import com.topceo.objects.other.UserShort;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.services.ReturnResult;
import com.topceo.services.UtilService;
import com.topceo.services.Webservices;
import com.topceo.socialview.core.widget.SocialTextView;
import com.topceo.utils.DateFormat;
import com.topceo.utils.MyUtils;
import com.topceo.views.ExpandableTextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.topceo.views.ShowMoreTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewHolderBasic extends RecyclerView.ViewHolder {
    public @BindView(R.id.imgVip)
    ImageView imgVip;

    public void setVip(UserShort user) {
        if (user.isVip()) {
            imgVip.setVisibility(View.VISIBLE);
            imgVip.setImageResource(R.drawable.ic_svg_24);
        } else {
            imgVip.setVisibility(View.GONE);
        }
    }

    //IMAGE
    public @BindView(R.id.textView1)
    TextView txt1;
    public @BindView(R.id.textView2)
    TextView txt2;
    public @BindView(R.id.textView3)
    TextView txt3;
    public @BindView(R.id.textView5)
    ShowMoreTextView txt5;
    public @BindView(R.id.textView6)
    TextView txt6;


    public @BindView(R.id.imageView1)
    ImageView img1;
    public @BindView(R.id.imgLike)
    CheckBox imgLike;
    public @BindView(R.id.imgMenu2)
    ImageView imgMenu;

    public @BindView(R.id.linearLayout3)
    LinearLayout linear3;

    public @BindView(R.id.vBgLike)
    View vBgLike;
    public @BindView(R.id.ivLike)
    ImageView ivLike;

    public @BindView(R.id.linearLike)
    LinearLayout linearLike;
    public @BindView(R.id.linearComment)
    LinearLayout linearComment;
    public @BindView(R.id.linearShare)
    LinearLayout linearShare;

    public @BindView(R.id.linearSave)
    LinearLayout linearSave;
    public @BindView(R.id.imgSave)
    ImageView imgSave;


    @BindView(R.id.button1)
    Button btnFollow;


    //Ads
    public @BindView(R.id.linearAds)
    LinearLayout linearAds;
    public @BindView(R.id.imgAds)
    ImageView imgAds;
    public @BindView(R.id.txtAds)
    TextView txtAds;

    //comment preview
    @BindView(R.id.txtViewAllComment)
    TextView txtViewAllComment;
    @BindView(R.id.linearCommentPreview)
    LinearLayout linearCommentPreview;
    @BindView(R.id.txtName1)
    TextView txtName1;
    @BindView(R.id.txtName2)
    TextView txtName2;

    @BindView(R.id.txtDes1)
    SocialTextView txtDes1;
    @BindView(R.id.txtDes2)
    SocialTextView txtDes2;
    @BindView(R.id.btnLike1)
    ShineButton btnLike1;
    @BindView(R.id.btnLike2)
    ShineButton btnLike2;
    @BindView(R.id.relativePreview1)
    RelativeLayout relativePreview1;
    @BindView(R.id.relativePreview2)
    RelativeLayout relativePreview2;
    @BindView(R.id.separator)
    View separator;
    @BindView(R.id.linearContainer)
    LinearLayout linearContainer;

    //Duyet post pending
    public @BindView(R.id.linearAcceptRefuse)
    LinearLayout linearAcceptRefuse;
    public @BindView(R.id.btnAccept)
    Button btnAccept;
    public @BindView(R.id.btnRefuse)
    Button btnRefuse;

    public Context context;
    public View view;
    private int avatarSize;

    public ViewHolderBasic(View v, int avatarSize) {
        super(v);
        ButterKnife.bind(this, v);
        context = v.getContext();
        view = v;
        this.avatarSize = avatarSize;

    }


    /////////////////////////////////////////////////////////////////////////////
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimationsMap = new HashMap<>();
    Map<RecyclerView.ViewHolder, AnimatorSet> heartAnimationsMap = new HashMap<>();
    //////////////////////////////////////////////////////////////////////////////////////
    float scale = 1f;

    public void animatePhotoLike(final int position, final ImageItem item) {
        vBgLike.setVisibility(View.VISIBLE);
        ivLike.setVisibility(View.VISIBLE);

        vBgLike.setScaleY(0.1f);
        vBgLike.setScaleX(0.1f);
        vBgLike.setAlpha(1f);
        ivLike.setScaleY(0.1f);
        ivLike.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(vBgLike, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(vBgLike, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(vBgLike, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //animation xong thi thuc hien
                //update server
                UtilService.updateLikeState(context, item.getImageItemId(), item.isLiked());

                likeAnimationsMap.remove(ViewHolderBasic.this);
                resetLikeAnimationState(ViewHolderBasic.this);
                dispatchChangeFinishedIfAllAnimationsEnded(ViewHolderBasic.this);


            }
        });
        animatorSet.start();

        likeAnimationsMap.put(ViewHolderBasic.this, animatorSet);
    }

    private void dispatchChangeFinishedIfAllAnimationsEnded(ViewHolderBasic holder) {
        if (likeAnimationsMap.containsKey(holder) || heartAnimationsMap.containsKey(holder)) {
            return;
        }

//        rv.dispatchAnimationFinished(holder);
    }

    private void resetLikeAnimationState(ViewHolderBasic holder) {
        holder.vBgLike.setVisibility(View.INVISIBLE);
        holder.ivLike.setVisibility(View.INVISIBLE);
    }


    public void animateHeart() {
        /*ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 0.3f, 0.0f, 0.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(700);
        animation.setFillAfter(true);

        ivLike.startAnimation(animation);*/
        ivLike.setVisibility(View.VISIBLE);
        Animatable animatable = (Animatable) ivLike.getDrawable();
        if (animatable.isRunning())
            animatable.stop();
        else
            animatable.start();


    }

    private Animation prepareAnimation(Animation animation) {
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    FeedAdapter adapter;
    ArrayList<ImageItem> mDataset = new ArrayList<>();

    public void initViewBasic(ImageItem item, int position, FeedAdapter adapter) {
        if (item != null) {
            this.adapter = adapter;
            if (adapter != null) {
                this.mDataset = adapter.mDataset;
            }

            //avatar
            String avatar = "";
            if (item.getOwner() != null && !TextUtils.isEmpty(item.getOwner().getAvatarSmall())) {
                avatar = item.getOwner().getAvatarSmall();
            }
            Glide.with(context)
                    .load(!TextUtils.isEmpty(avatar) ? avatar : R.drawable.ic_no_avatar)//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new CenterCrop(), new RoundedCorners(avatarSize / 2))//new GlideCircleTransform(context)
                    .into(img1);

            //SET THONG TIN
            if (item.getOwner() != null) {
                txt1.setText(item.getOwner().getFullName());
                setVip(item.getOwner());
            }

            if (!TextUtils.isEmpty(item.getLocation())) {
                txt2.setText(item.getLocation());
                txt2.setVisibility(View.VISIBLE);
            } else {
                txt2.setVisibility(View.GONE);
            }
            txt6.setText(MyUtils.formatDate(item.getCreateDate(), DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM, context));

            //like,share
            setLikeShareComment(item);

            //cac menu, button
            imgMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(imgMenu, item, position, imgSave);
                }
            });

            ////
            linearLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgLike.setChecked(!imgLike.isChecked());
                }
            });
            imgLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //update local
                    item.setLiked(isChecked);
//                    item.setLiked(item.isLiked());

                    if (position < mDataset.size()) {
                        mDataset.get(position).setLiked(item.isLiked());
                    }

                    //set lai giao dien: nut like va so luong like----------------------
                    int like = item.getLikeCount();
                    if (item.isLiked()) {
                        like += 1;
                    } else {
                        like -= 1;
                    }
                    item.setLikeCount(like);
                    if (position < mDataset.size()) {
                        mDataset.get(position).setLikeCount(like);
                    }

                    //set lai giao dien: nut like va so luong like----------------------
                    setLikeShareComment(item);

                    //update server
                    UtilService.updateLikeState(context, item.getImageItemId(), item.isLiked());

                    //update db tab SonTung
                    HolderUtils.updateLiked(item.getImageItemId(), item.isLiked(), item.getLikeCount());

                    //update cac fragment khac
//                MyUtils.updateImageItem(context, item);
                }
            });
            ////
            linearComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //neu la video thi dung phat
                    /*String itemType = item.getItemContentType();
                    if (itemType.equals(Item.TYPE_VIDEO)) {
                        stopVideo();
                    }*/

                    MyUtils.gotoDetailImage(context, item);
                }
            });
            /*txt5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearComment.performClick();
                }
            });*/
            ////
            linearShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.share(context, item);
                }
            });

            linearSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePost(item, position, imgSave);
                }
            });

            //vao profile
            txt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item != null && item.getOwner() != null) {
                        MyUtils.gotoProfile(item.getOwner().getUserName(), context);
                    }
                }
            });
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txt1.performClick();
                }
            });
            txt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    txt1.performClick();
                }
            });

            /////////////////////////////////////////////////////////////////////////
            //click vao so luong like thi mo danh sach nguoi da like
            txt3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    MyUtils.showToastDebug(context, "Number likes");
                    Intent intent = new Intent(context, MH11_LikeActivity.class);
                    intent.putExtra(ImageItem.IMAGE_ITEM, item);
                    context.startActivity(intent);

                }
            });


            //ads neu co
            Info info = item.getInfo();
            if (info != null) {
                linearAds.setVisibility(View.VISIBLE);
                txtAds.setText(info.getText());
                if (!TextUtils.isEmpty(info.getIcon())) {
                    imgAds.setVisibility(View.VISIBLE);
                    switch (info.getIcon()) {
                        case Info.ICON_YOUTUBE:
                            imgAds.setImageResource(R.drawable.ic_youtube);
                            break;
                        case Info.ICON_DOWNLOAD:
                            imgAds.setImageResource(R.drawable.ic_file_download_white_24dp);
                            break;
                        default:
//                            imgAds.setImageResource(R.drawable.ic_link_white_24dp);
                            imgAds.setVisibility(View.GONE);
                            break;
                    }
                } else {//default
//                    imgAds.setImageResource(R.drawable.ic_link_white_24dp);
                    imgAds.setVisibility(View.GONE);
                }

                linearAds.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(info.getLink())) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getLink()));
                            context.startActivity(intent);
                        } else {

                        }
                    }
                });
            } else {
                linearAds.setVisibility(View.GONE);
            }

            //comments
            initComment(item, position);

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void setLikeShareComment(ImageItem item) {
        int like = item.getLikeCount();

        //cai nao ko co thi an
        //likes
        if (like > 0) {
            String s = String.format(context.getString(R.string.number_likes), like);
            txt3.setText(s);
            txt3.setVisibility(View.VISIBLE);
        } else {
            txt3.setVisibility(View.GONE);
            txt3.setText("");
        }


        //control image button Thich
        imgLike.setOnCheckedChangeListener(null);
        imgLike.setChecked(item.isLiked());

        if (item.isSaved()) {
            imgSave.setImageResource(R.drawable.ic_svg_23);
        } else {
            imgSave.setImageResource(R.drawable.ic_svg_22);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Showing popup menu when tapping on 3 dots
     */
    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view, final ImageItem item, final int position, ImageView imgSave) {
        // inflate menu
        final PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        if (adapter != null && adapter.owner != null && item.getOwner().getUserName().equals(adapter.owner.getUserName())) {
            inflater.inflate(R.menu.menu_item_newsfeed_owner, popup.getMenu());
        } else {
            Menu menu = null;
            inflater.inflate(R.menu.menu_item_newsfeed, menu = popup.getMenu());

            MenuItem menuSave = menu.findItem(R.id.action_save);
            if (item.isSaved()) {
                menuSave.setTitle(R.string.not_save);
                menuSave.setIcon(R.drawable.ic_svg_23_24dp);
            } else {
                menuSave.setTitle(R.string.save_post);
                menuSave.setIcon(R.drawable.ic_svg_22_24dp);
            }
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_save:
                        savePost(item, position, imgSave);
                        break;
                    case R.id.action_report:
//                            Toast.makeText(context, "Report", Toast.LENGTH_SHORT).show();
                        MyUtils.showDialogReportPost(context, item.getImageItemId());
                        return true;
                    case R.id.action_edit:
                        //Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
                        MyUtils.editPost(item, context);

                        return true;
                    case R.id.action_delete:
//                            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                        //hoi truoc khi xoa
                        new MaterialAlertDialogBuilder(context)
                                .setMessage(R.string.do_you_delete_image)
                                .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                                    deletePost(item.getImageItemId());
                                    dialogInterface.dismiss();
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();

                        return true;
                    default:
                }
                return false;
            }


        });
//        popup.show();
        @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }


    private void savePost(ImageItem item, int position, ImageView imgSave) {
        if (item != null) {

            if (item.isSaved()) {
                imgSave.setImageResource(R.drawable.ic_svg_22);
                MyApplication.apiManager.unSavePost(
                        item.getImageItemId(),
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject data = response.body();
                                if (data != null) {

                                    ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);
                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
//                                            MyUtils.showToast(context, R.string.update_success);
                                            mDataset.get(position).setSaved(!item.isSaved());
                                            context.sendBroadcast(new Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST_SAVED));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
            } else {
                imgSave.setImageResource(R.drawable.ic_svg_23);
                MyApplication.apiManager.savePost(
                        item.getImageItemId(),
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                JsonObject data = response.body();
                                if (data != null) {

                                    ReturnResult result = Webservices.parseJson(data.toString(), JsonObject.class, false);
                                    if (result != null) {
                                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
//                                            MyUtils.showToast(context, R.string.save_success);
                                            mDataset.get(position).setSaved(!item.isSaved());
                                            context.sendBroadcast(new Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST_SAVED));

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {

                            }
                        });
            }


        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void deletePost(long imageItemId) {
        AndroidNetworking.post(Webservices.URL + "image/delete")
                .addQueryParameter("ImageItemId", String.valueOf(imageItemId))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        ReturnResult result = Webservices.parseJson(response, String.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                MyUtils.showToast(context, R.string.delete_success);
                                MyUtils.afterDeletePost(context, imageItemId);

                                //xoa va refresh lai danh sach
                                if (adapter != null) {
                                    adapter.remove(imageItemId);
                                }
                            } else {
//                                String message = result.getErrorMessage();
                                MyUtils.showToast(context, R.string.delete_not_success);

                            }


                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////
    //#region COMMENT BINDING
    private void initComment(ImageItem item, int position) {
        //#1 title
        if (item.getComments().size() > 0) {
            txtViewAllComment.setVisibility(View.VISIBLE);
            linearCommentPreview.setVisibility(View.VISIBLE);
            linearCommentPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearComment.performClick();
                }
            });

            //#2 content
            if (item.getComments().size() >= 2) {
                txtViewAllComment.setText(context.getString(R.string.view_all_number_comment, item.getCommentCount()));
                //LAY 2 COMMENT DAU TIEN
                relativePreview1.setVisibility(View.VISIBLE);
                relativePreview2.setVisibility(View.VISIBLE);
                setUpComment(txtName1, txtDes1, btnLike1, item.getComments().get(0), position, 0);
                setUpComment(txtName2, txtDes2, btnLike2, item.getComments().get(1), position, 1);


            } else {
                txtViewAllComment.setText(R.string.view_comment);
                //CHI CO 1 COMMENT
                relativePreview1.setVisibility(View.VISIBLE);
                relativePreview2.setVisibility(View.GONE);
                separator.setVisibility(View.GONE);
                setUpComment(txtName1, txtDes1, btnLike1, item.getComments().get(0), position, 0);
            }
        } else {
            txtViewAllComment.setVisibility(View.GONE);
            linearCommentPreview.setVisibility(View.GONE);
        }
        txtViewAllComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearComment.performClick();
            }
        });
    }

    private void setUpComment(TextView txtName, SocialTextView txtDes, ShineButton btnLike, ImageComment comment, int positionImage, int positionComment) {
        //avatar
        String url = null;
        UserShort user = comment.getUser();
        if (user != null) {
            url = user.getAvatarSmall();
            txtName.setText(user.getFullName());
            //description
//            String s = context.getString(R.string.username_and_comment, "<b>"+user.getUserName()+"</b>", comment.getComment());
            txtDes.setText(MyUtils.fromHtml(comment.getComment()));
            MyUtils.whenClickComment(context, txtDes);
        }
        /*Glide.with(context)
                .load(!TextUtils.isEmpty(url) ? url : R.drawable.ic_no_avatar)//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                .placeholder(R.drawable.ic_no_avatar)
                .override(avatarSize, avatarSize)
                .transform(new CenterCrop(), new RoundedCorners(avatarSize / 2))//new GlideCircleTransform(context)
                .into(img1);*/

        //btn liked
        btnLike.setChecked(comment.isLiked());
        btnLike.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                UtilService.setLikeComment(comment.getItemId(), checked);
                if (positionImage < mDataset.size()) {
                    if (mDataset.get(positionImage).getComments().size() > 0) {
                        mDataset.get(positionImage).getComments().get(positionComment).setLiked(checked);
                    }

                }
            }
        });


    }
    //#endregion comment

    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////


}
