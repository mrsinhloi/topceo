package com.topceo.mediaplayer.pip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.topceo.R;
import com.topceo.activity.MH02_PhotoDetailActivity;
import com.topceo.activity.MH11_LikeActivity;
import com.topceo.chat.ChatUtils;
import com.topceo.chat.MainChatActivity;
import com.topceo.comments.CommentAdapterSectionParent_ImageComment;
import com.topceo.config.MyApplication;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.db.ImageItemDB;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.objects.other.UserShort;
import com.topceo.onesignal.NotifyObject;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.services.ReturnResult;
import com.topceo.services.UtilService;
import com.topceo.services.Webservices;
import com.topceo.shopping.ShoppingActivity;
import com.topceo.socialview.commons.socialview.Hashtag;
import com.topceo.socialview.commons.socialview.Mention;
import com.topceo.socialview.commons.widget.HashtagArrayAdapter;
import com.topceo.socialview.commons.widget.MentionArrayAdapter;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.socialview.objects.SearchHashtag;
import com.topceo.socialview.objects.SearchMention;
import com.topceo.utils.DateFormat;
import com.topceo.utils.EndlessRecyclerOnScrollListener;
import com.topceo.utils.MyUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBinding {
    Activity context;
    User user;
    ImageItem item;
    private Realm realm;
    private int avatarSize = 0;
    private int widthImage = 0, heightImage = 0;
    private boolean isMyPost = false;

    TextView txt3;
    ImageView imgLike;
    ImageView imgSave;
    SocialAutoCompleteTextView txtInput;
    RecyclerView rv;
    LinearLayout linearReply;
    NestedScrollView scrollView;

    public DetailBinding(Activity context, User user, ImageItem item, Realm realm, boolean isMyPost, TextView txt3, ImageView imgLike, ImageView imgSave, SocialAutoCompleteTextView txtInput, RecyclerView rv, LinearLayout linearReply, NestedScrollView scrollView) {
        this.context = context;
        this.user = user;
        this.item = item;
        this.realm = realm;
        this.isMyPost = isMyPost;

        this.txt3 = txt3;
        this.imgLike = imgLike;
        this.imgSave = imgSave;
        this.txtInput = txtInput;
        this.rv = rv;
        this.linearReply = linearReply;
        this.scrollView = scrollView;

    }

    public void setTitleBar(ImageView imgBack, ImageView imgShop, RelativeLayout relativeChat, TextView txtNumber, Activity context) {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.finish();
            }
        });
        imgShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ShoppingActivity.class));
            }
        });
        relativeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainChatActivity.class);
                context.startActivity(intent);
            }
        });
        ChatUtils.setChatUnreadNumber(txtNumber);
    }

    public void setVip(UserShort user, ImageView imgVip) {
        if (user.isVip() || user.getUserId() == 1) {
            imgVip.setVisibility(View.VISIBLE);
            imgVip.setImageResource(R.drawable.ic_svg_24);
        } else {
            imgVip.setVisibility(View.GONE);
        }
    }

    public void setUserVip(int avatarSize, ImageView img1, ImageView imgVip) {
        UserShort owner = item.getOwner();
        if (owner != null) {
            Glide.with(context)
                    .load(owner.getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(img1);
            setVip(owner, imgVip);
        }
    }

    public void setLikeShareComment() {
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

        setImageLikeState(imgLike, item.isLiked());
        if (item.isSaved()) {
            imgSave.setImageResource(R.drawable.ic_svg_23);
        } else {
            imgSave.setImageResource(R.drawable.ic_svg_22);
        }
    }

    public void setContent(TextView txt1,
                           TextView txt2,
                           TextView txt3,
                           TextView txt6,
                           ImageView imgMenu,
                           LinearLayout linearLike,
                           ImageView imgLike,
                           LinearLayout linearSave,
                           ImageView img2,
                           LinearLayout linearComment,
                           LinearLayout linearShare,
                           ImageView img1,
                           ImageView ivLike
    ) {
        txt1.setText(item.getOwner().getFullName());
        if (!TextUtils.isEmpty(item.getLocation())) {
            txt2.setText(item.getLocation());
            txt2.setVisibility(View.VISIBLE);
        } else {
            txt2.setVisibility(View.GONE);
        }
        txt6.setText(MyUtils.formatDate(item.getCreateDate(), DateFormat.DATE_FORMAT_VN_DDMMYYYY_HHMM, context));

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(imgMenu);
            }
        });

        ////
        linearLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update local
                item.setLiked(!item.isLiked());
                whenLike();
            }
        });

        item.setLiked(item.isLiked());
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setLiked(!item.isLiked());
                whenLike();
            }
        });

        linearSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost(item);
            }
        });

        if (img2 != null)
            img2.setOnTouchListener(new View.OnTouchListener() {
                long t1 = 0, t2 = 0;
                int count = 0;

                @Override
                public boolean onTouch(View view, MotionEvent e) {

                    int action = e.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:

                            if (count == 0) t1 = System.currentTimeMillis();
                            if (count == 1) t2 = System.currentTimeMillis();
                            count++;
                            if (count > 1) count = 0;
                            if (Math.abs(t2 - t1) < 300) {
                                t1 = 0;
                                t2 = 0;
                                count = 0;
                                //On double tap here. Do stuff
//                                MyUtils.showToast(context, "double tap");
                                if (!item.isLiked()) {
                                    linearLike.performClick();
                                }

                                animateHeart(ivLike);
                            }

                            break;

                    }

                    return true;
                }

            });
        ////
        linearComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtInput.requestFocus();
                MyUtils.showKeyboard(context);
            }
        });
        ////
        linearShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.share(context, item);
            }
        });

        //vao profile
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.gotoProfile(item.getOwner().getUserName(), context);
                context.finish();
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
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Showing popup menu when tapping on 3 dots
     */
    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view) {
        // inflate menu
        final PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        if (item.getOwner().getUserName().equals(user.getUserName())) {
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
                        savePost(item);
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
                        new MaterialDialog.Builder(context)
//                                    .title(R.string.delete_image)
                                .content(R.string.do_you_delete_image)
                                .positiveText(R.string.delete)
                                .negativeText(R.string.no)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        deletePost(item.getImageItemId());
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                        return true;
                    default:
                }
                return false;
            }
        });

        @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();

    }

    private void whenLike() {

        //set lai giao dien: nut like va so luong like----------------------
        int like = item.getLikeCount();
        if (item.isLiked()) {
            like += 1;
        } else {
            like -= 1;
        }
        item.setLikeCount(like);

        //like
        setLikeShareComment();

        //update server
        UtilService.updateLikeState(context, item.getImageItemId(), item.isLiked());

        //update db
        updateLiked(item.getImageItemId(), item.isLiked(), item.getLikeCount());

        //set lai UI


        //bao cac man hinh khac update
        MyUtils.updateImageItem(context, item, false);
    }

    private void setUI() {
        if (context instanceof MH02_PhotoDetailActivity) {
            ((MH02_PhotoDetailActivity) context).setUI();
        } else if (context instanceof VideoActivityPipDetail) {
//            ((VideoActivityPipDetail) context).setUI();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////
    private void deletePost(long imageItemId) {
        AndroidNetworking.post(Webservices.API_URL + "image/delete")
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

                                //update ui cac man hinh home, profile
                                MyUtils.afterDeletePost(context, item.getImageItemId());
                                //xoa va refresh lai danh sach
                                context.finish();


                            } else {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void savePost(ImageItem item) {
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
                                            item.setSaved(!item.isSaved());
                                            //reload item
                                            refreshItem();
                                            //grid cua user
                                            Intent intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST_SAVED);
                                            context.sendBroadcast(intent);


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
                                            item.setSaved(!item.isSaved());
                                            //reload item
                                            refreshItem();
                                            //grid cua user
                                            Intent intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST_SAVED);
                                            context.sendBroadcast(intent);
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

    public void animateHeart(ImageView ivLike) {
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Tab SonTung luu offline, cac tab khac thi ko dung
    private void updateLiked(long imageItemId, boolean isLiked, int likeCount) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ImageItemDB item = realm.where(ImageItemDB.class)
                        .equalTo("ImageItemId", imageItemId).findFirst();
                if (item != null) {
                    item.setLiked(isLiked);
                    item.setLikeCount(likeCount);
                }
            }
        });
    }

    public void refreshItem() {
        MyUtils.updateImageItem(context, item, false);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void updateLikeState() {
        long imageItemId = item.getImageItemId();
        boolean isLiked = item.isLiked();

        AndroidNetworking.post(Webservices.URL_GRAPHQL)
                .addQueryParameter("query", Webservices.UPDATE_IMAGE_ITEM_LIKED(imageItemId, isLiked))
                .setOkHttpClient(MyApplication.getClient())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //{"data":{"UpdateImageItemLiked":null}}
                        try {
                            JSONObject objP = response.getJSONObject("data");
                            if (!objP.isNull("UpdateImageItemLiked")) {
                                JSONObject obj = objP.getJSONObject("UpdateImageItemLiked");

                                double imageItemId = obj.getDouble("ImageItemId");
//                                int likeCount = obj.getInt("LikeCount");

                                if (imageItemId > 0) {
                                    //update grid user
                                    refreshItem();
                                }
                            } else {

                                //update ui cac man hinh home, profile
                                MyUtils.afterDeletePost(context, item.getImageItemId());
                                //null tuc la hinh da bi xoa, xoa trong list
                                new MaterialDialog.Builder(context)
                                        .content(R.string.post_is_deleted)
                                        .positiveText(R.string.ok)
                                        .cancelable(false)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                context.finish();
                                            }
                                        })
                                        .show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.getMessage());
                        if (ANError.getErrorCode() == ReturnResult.ERROR_CODE_UNAUTHORIZED) {
                        }
                    }
                })
        ;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    public void setButtonUI(AppCompatButton btnFollow) {
        //todo //kiem tra owner cua hinh co nam trong danh sach minh dang theo doi khong
        final UserShort uLike = item.getOwner();
        setStateButton(uLike, btnFollow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
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
//                                        MyUtils.showToast(context, R.string.update_success);

                                        //refresh giao dien
                                        setButtonUI(btnFollow);

                                        //refresh giao dien MH19_UserProfileActivity
                                        Intent intent = new Intent(Fragment_Profile_Owner.ACTION_UPDATE_STATE_FOLLOW);
                                        context.sendBroadcast(intent);
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
//                                        MyUtils.showToast(context, R.string.update_success);

                                        //refresh giao dien
                                        setButtonUI(btnFollow);

                                        //refresh giao dien MH19_UserProfileActivity
                                        Intent intent = new Intent(Fragment_Profile_Owner.ACTION_UPDATE_STATE_FOLLOW);
                                        context.sendBroadcast(intent);
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
        });
    }

    private void setStateButton(UserShort uLike, AppCompatButton btn) {
        if (uLike != null) {
            if (MyUtils.isFollowing(uLike.getUserId())) {//da quan tam
                btn.setText(context.getText(R.string.following_title));
                btn.setBackgroundResource(R.drawable.bg_sky_rectangle_border);
                btn.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                btn.setText(context.getText(R.string.follow_title));
                btn.setBackgroundResource(R.drawable.bg_sky_radian);
                btn.setTextColor(context.getResources().getColor(R.color.white));
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //comment
    private CommentAdapterSectionParent_ImageComment mAdapter;

    public CommentAdapterSectionParent_ImageComment getmAdapter() {
        return mAdapter;
    }

    private ArrayList<ImageComment> listComments = new ArrayList<>();

    public void initAdapter(LinearLayout rippleSend) {
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        rv.setNestedScrollingEnabled(false);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                MyUtils.showToastDebug(context, "Load more...");
                ImageComment comment = mAdapter.getLastestItem();
                if (comment != null) {
                    getComments(item.getImageItemId(), comment.getItemId(), comment.getCreateDate());
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new CommentAdapterSectionParent_ImageComment(listComments, context, isMyPost);
        mAdapter.shouldShowHeadersForEmptySections(true);
        rv.setAdapter(mAdapter);

        //getdata
        if (item != null) {
            getComments(item.getImageItemId(), 0, 0);
        }

        rippleSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });


        txtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    send();
                    if (TextUtils.isEmpty(txtInput.getText())) {
                        MyUtils.hideKeyboard(context, txtInput);
                    }
                    return true;
                }
                return false;
            }
        });


    }

    private void send() {
        String text = txtInput.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            MyUtils.hideKeyboard(context);
            sendComment(text);
        }

    }

    private void findCommentIdAndSrollTo() {
        if (mAdapter != null && rv != null) {
            ImageItem img = MyApplication.imgItem;
            if (img != null) {
                NotifyObject notify = img.getNotifyObject();
                if (notify != null) {
                    //scroll toi item
                    long commentId = notify.getReplyToId();
                    if (commentId <= 0) {
                        commentId = notify.getCommentId();
                    }

                    if (commentId > 0) {
                        int position = mAdapter.findPostionParent(commentId);
                        if (position >= 0) {
                            if (scrollView == null) {//video
                                rv.scrollToPosition(position);
                                MyUtils.showToast(context, "Scroll to postion = " + position);
                            } else {//image
                                float y = rv.getChildAt(position).getY();
                                scrollView.smoothScrollTo(0, (int) y);
                                MyUtils.showToast(context, "Scroll to postion = " + position);
                            }
                            //xoa bo
                            if (MyApplication.imgItem != null) {
                                MyApplication.imgItem.setNotifyObject(null);
                            }

                            //animation
                            mAdapter.animation(position);
                        }


                    }


                }
            }
        }
    }

    /**
     * {"data":{"ImageComment":{"Comments":
     * [{"ItemId":1062,"User":{"UserName":"jessicahua","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/87b2fef5-3b6a-4a05-ae09-90bcc8396ad4\/bbbf19b1-8ccb-4145-983a-cb6bee1ddf96.JPG"},"Comment":"Dep qua ah @ChupHinhDep","CreateDate":"2016-06-25 00:40:40"}
     * ,{"ItemId":1063,"User":{"UserName":"nhattien667","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/10973213-85c8-4195-9fd4-ab4f931d3331\/54074402-b36e-4635-b90b-414c167db5bc.JPG"},"Comment":"Bạn dùng app gi vậy","CreateDate":"2016-06-24 19:09:09"},
     * {"ItemId":1064,"User":{"UserName":"hieukeodeo2882","AvatarSmall":"http:\/\/service.winkjoy.vn\/Pictures\/7627e20c-604d-4145-805c-944eae61a3df\/c955e242-0fc9-4ead-b867-64390541e841.JPG"},"Comment":"Hay nhỉ��","CreateDate":"2016-06-24 18:43:18"}]
     * }}}
     *
     * @param imageItemId
     * @param lastItemId
     */
    public void getComments(final long imageItemId, final long lastItemId, final long lastCreateDate) {

        if (MyUtils.checkInternetConnection(context)) {
            Webservices.getComments(imageItemId, 0, lastItemId, lastCreateDate).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
                    if (task.getError() == null) {
                        if (task.getResult() != null) {
                            ArrayList<ImageComment> comments = (ArrayList<ImageComment>) task.getResult();
                            if (comments.size() > 0) {

                                if (lastItemId == 0) {//page 1
                                    mAdapter.clear();
                                    mAdapter.addListItems(comments);

                                    //scroll den comment tu notify neu co
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            findCommentIdAndSrollTo();
                                        }
                                    }, 1000);

                                } else {//load more
                                    mAdapter.addListItems(comments);
                                }

                            }

                            comments.clear();
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            getComments(imageItemId, lastItemId, lastCreateDate);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }
                        }
                    }

//                    setRefresh(false);
                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ImageComment replyToComment;

    public void sendComment(final String comment) {
        if (MyUtils.checkInternetConnection(context)) {
            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.sending));
            progressDialog.show();*/

            long replyToId = 0;
            if (replyToComment != null) {
                replyToId = replyToComment.getItemId();
            }

            Webservices.sendComment(item.getImageItemId(), comment, replyToId).continueWith(new Continuation<Object, Void>() {
                @Override
                public Void then(Task<Object> task) throws Exception {
//                    progressDialog.dismiss();
                    if (task.getError() == null) {
                        if (task.getResult() != null) {

//                            initReplyLayout(null);
                            replyToComment = null;
                            linearReply.setVisibility(View.GONE);

                            ImageComment img = (ImageComment) task.getResult();
                            if (img != null) {
//                                list_empty.setVisibility(View.GONE);

                                //add vao trong list comment
                                mAdapter.add(img);//mAdapter.getItemCount()
                                //neu add vao cha thi scroll len dau
                                if (img.getReplyToId() == 0) {
                                    rv.scrollToPosition(0);//mAdapter.getItemCount()-1
                                }

                                txtInput.setText("");
                                item.getComments().add(0, img);
                                updateCommentCount();

                            }
                        }
                    } else {
                        boolean isLostCookie = MyApplication.controlException((ANError) task.getError());

                        if (isLostCookie) {
                            MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                @Override
                                public Void then(Task<Object> task) throws Exception {
                                    if (task.getResult() != null) {
                                        User kq = (User) task.getResult();
                                        if (kq != null) {
                                            sendComment(comment);
                                        }
                                    }
                                    return null;
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(task.getError().getMessage())) {
                                MyUtils.showToast(context, task.getError().getMessage());
                            }
                        }
                    }

                    return null;
                }
            });
        } else {
            MyUtils.showThongBao(context);
        }
    }

    public void updateCommentCount() {
        if (mAdapter != null && item != null && context != null) {
            //set lai so imageItem, sau do goi nguoc ve cac man hinh
            item.setCommentCount(mAdapter.getParentAndChildCount());

            setUI();
            MyUtils.updateImageItem(context, item, false);
        }
    }

    public void initReplyLayout(ImageComment comment, LinearLayout linearReply, ImageView imgReplyClose, TextView txtReply1, TextView txtReply2) {
        replyToComment = comment;
        if (replyToComment != null) {
            linearReply.setVisibility(View.VISIBLE);
            imgReplyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearReply.setVisibility(View.GONE);
                    replyToComment = null;
                }
            });

            //todo set hinh, ten, description
            if (replyToComment.getUser() != null) {
                String name = replyToComment.getUser().getFullName();
                txtReply1.setText(name);
            }

            txtReply2.setText(MyUtils.fromHtml(replyToComment.getComment()));


        } else {
            linearReply.setVisibility(View.GONE);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchMetion(String keyword) {
        MyApplication.apiManager.searchUserForMention(
                keyword,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<SearchMention>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<SearchMention> list = (ArrayList<SearchMention>) result.getData();
                                    if (list.size() > 0) {
                                        setAdapterMention(list);
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

    }

    //hashtag
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

    public void initHastag() {
        //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    String string = txtInput.getText().toString();
                    if (!TextUtils.isEmpty(string)) {
                        String[] words = string.split(" ");
                        if (words.length > 0) {
                            String lastWord = words[words.length - 1];

                            //#h, @t
                            if (lastWord.length() >= 2) {
                                if (lastWord.charAt(0) == '#') {
                                    String keyword = lastWord.substring(1);
                                    searchHashtag(keyword);
                                } else if (lastWord.charAt(0) == '@') {
                                    String keyword = lastWord.substring(1);
                                    searchMetion(keyword);


                                }
                            }
                        }
                    }
                }
                return false;
            }
        });
        //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchHashtag(String keyword) {
        MyApplication.apiManager.searchHashtag(
                keyword,
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject data = response.body();
                        if (data != null) {
                            Type collectionType = new TypeToken<List<SearchHashtag>>() {
                            }.getType();
                            ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    ArrayList<SearchHashtag> list = (ArrayList<SearchHashtag>) result.getData();
                                    if (list.size() > 0) {
                                        setAdapterHashtag(list);
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapterHashtag(ArrayList<SearchHashtag> list) {
        if (list != null && list.size() > 0) {
            ArrayList<Hashtag> data = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SearchHashtag item = list.get(i);
                data.add(new Hashtag(item.getHashtag(), item.getPostCount()));
            }
            ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<Hashtag>(context);
            hashtagAdapter.addAll(data);
            txtInput.setHashtagAdapter(hashtagAdapter);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void setAdapterMention(ArrayList<SearchMention> list) {
        if (list != null && list.size() > 0) {

            MentionArrayAdapter<Mention> mentionAdapter = new MentionArrayAdapter<Mention>(context);
            for (int i = 0; i < list.size(); i++) {
                SearchMention item = list.get(i);
                mentionAdapter.add(new Mention(item.getUserName(), item.getFullName(), item.getAvatarSmall()));
            }

            txtInput.setMentionAdapter(mentionAdapter);
        }
    }


    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void setImageLikeState(ImageView imgLike, boolean isChecked) {
        if (imgLike != null) {
            if(isChecked){
                imgLike.setImageResource(R.drawable.ic_svg_20);
            }else{
                imgLike.setImageResource(R.drawable.ic_svg_19);
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
