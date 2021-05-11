package com.topceo.mediaplayer.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.topceo.R;
import com.topceo.comments.CommentAdapterSectionParent_MediaComment;
import com.topceo.config.MyApplication;
import com.topceo.objects.other.User;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.Fragment_Shop;
import com.topceo.shopping.Media;
import com.topceo.shopping.MediaComment;
import com.topceo.shopping.MediaItem;
import com.topceo.socialview.commons.socialview.Hashtag;
import com.topceo.socialview.commons.socialview.Mention;
import com.topceo.socialview.commons.widget.HashtagArrayAdapter;
import com.topceo.socialview.commons.widget.MentionArrayAdapter;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.socialview.objects.SearchHashtag;
import com.topceo.socialview.objects.SearchMention;
import com.topceo.utils.EndlessRecyclerOnScrollListener;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingBinding {

    Activity context;
    Media media;
    ArrayList<MediaItem> list;

    SocialAutoCompleteTextView txtInput;
    TextView txt1, txt2, txt3, txtLikeCount, txtCommentCount;
    RecyclerView rv1, rv2;
    LinearLayout rippleSend;
    LinearLayout linearReply;
    TextView txtItems;
    public ShoppingBinding(Activity context, Media media, ArrayList<MediaItem> list, MediaItem videoSelected, SocialAutoCompleteTextView txtInput, LinearLayout rippleSend, TextView txtItems, LinearLayout linearInputComment, LinearLayout linearReply, RecyclerView rv1, RecyclerView rv2, TextView txt1, TextView txt2, TextView txt3, TextView txtLikeCount, TextView txtCommentCount) {
        this.context = context;
        this.media = media;
        this.videoSelected = videoSelected;
        this.list = list;

        this.txtInput = txtInput;
        this.txt1 = txt1;
        this.txt2 = txt2;
        this.txt3 = txt3;
        this.txtLikeCount = txtLikeCount;
        this.txtCommentCount = txtCommentCount;

        this.rv1=rv1;
        this.rv2=rv2;

        this.rippleSend = rippleSend;
        this.linearReply=linearReply;
        this.txtItems = txtItems;
        setTextItem(list.size());

    }

    private void setTextItem(int number) {
        if (number > 0) {
            txtItems.setText(context.getString(R.string.list_x_items, number));
            txtItems.setVisibility(View.VISIBLE);
        } else {
            txtItems.setVisibility(View.GONE);
        }
    }
    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setLikeMedia(long mediaId, boolean isLiked) {
        if (isLiked) {
            MyApplication.apiManager.mediaLike(mediaId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null)
                        parseJson(obj.toString(), mediaId, isLiked);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        } else {
            MyApplication.apiManager.mediaUnlike(mediaId, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    if (obj != null)
                        parseJson(obj.toString(), mediaId, isLiked);
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
                    if (media != null) {
                        media.setLiked(isLiked);
                        //refresh skyshop
                        int count = isLiked ? (media.getLikeCount() + 1) : (media.getLikeCount() - 1);
                        media.setLikeCount(count);

                        //set lai giao dien so luong like tang hoac giam
                        setUIVideo(videoSelected);

                        //cap nhat giao dien shop (liked + countLiked)
                        Intent intent = new Intent(Fragment_Shop.ACTION_UPDATE_MEDIA);
                        intent.putExtra(Media.MEDIA, media);
                        context.sendBroadcast(intent);
                    }

                } else {
                    MyUtils.showToastDebug(context, result.getErrorMessage());
                }
            }
        }
    }

    private void initInputComment() {
        /*TinyDB db = new TinyDB(this);
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
            if (user != null) {
                int width = context.getResources().getDimensionPixelOffset(R.dimen.avatar_size_medium);
                //set avatar
                Glide.with(context)
                        .load(user.getAvatarSmall())
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(width, width)
                        .transform(new GlideCircleTransform(context))
                        .into(imgOwner);
            }
        }*/

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
        txtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isHideKeyboard = false;
                }
            }
        });

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
    private boolean isHideKeyboard = false;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void send() {
        String text = txtInput.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            MyUtils.hideKeyboard(context);
            sendComment(text);
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendComment(final String comment) {
        if (MyUtils.checkInternetConnection(context)) {
            /*final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.sending));
            progressDialog.show();*/

            ProgressUtils.show(context);
            String replyToId = null;
            if (replyToComment != null) {
                replyToId = String.valueOf(replyToComment.getItemId());
            }

            MyApplication.apiManager.mediaCommentAdd(videoSelected.getMediaId(), replyToId, comment, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject data = response.body();
                    if (data != null) {
                        ReturnResult result = Webservices.parseJson(data.toString(), MediaComment.class, false);

                        if (result != null) {
                            if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao

                                replyToComment=null;
                                linearReply.setVisibility(View.GONE);

                                MediaComment img = (MediaComment) result.getData();
                                if (img != null) {
//                                list_empty.setVisibility(View.GONE);

                                    //add vao trong list comment
                                    adapterComment.add(img);//mAdapter.getItemCount()
                                    //neu add vao cha thi scroll len dau
                                    /*if(img.getReplyToId()==0){
                                        rv.scrollToPosition(0);//mAdapter.getItemCount()-1
                                    }*/

                                    txtInput.setText("");

                                    //set lai so item, sau do goi nguoc ve cac man hinh
//                                    videoSelected.setCommentCount(adapterComment.getItemCount());


                                }
                            }
                        }
                    }
                    ProgressUtils.hide();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    ProgressUtils.hide();
                }
            });

        } else {
            MyUtils.showThongBao(context);
        }
    }

    private MediaItem videoSelected;

    public void setUIVideo(MediaItem videoSelected) {
        if (videoSelected != null) {
            txt1.setText(videoSelected.getTitle());
            txt3.setText(MyUtils.getTimeAgo(videoSelected.getLastModifyDate() * 1000, context));
            if (!TextUtils.isEmpty(videoSelected.getAuthor())) {
                txt2.setText(videoSelected.getAuthor());
                txt2.setVisibility(View.VISIBLE);
            } else {
                txt2.setVisibility(View.GONE);
            }

        }

        //hien thi so luong like comment
        if (media != null) {
            if (media.getLikeCount() > 0) {
                txtLikeCount.setVisibility(View.VISIBLE);
                txtLikeCount.setText(String.valueOf(media.getLikeCount()));
            } else {
                txtLikeCount.setVisibility(View.GONE);
            }
            txtCommentCount.setText(String.valueOf(media.getCommentCount()));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int ITEM_COUNT = 20;
    private void getComments(long mediaId, long lastId) {
        MyApplication.apiManager.mediaCommentList(
                mediaId,
                (lastId == 0) ? null : String.valueOf(lastId),
                ITEM_COUNT,
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
                                    ArrayList<MediaComment> list = (ArrayList<MediaComment>) result.getData();
                                    if (list.size() > 0) {
                                        if (lastId == 0) {//page 1
                                            adapterComment.clear();
                                            adapterComment.addListItems(list);

                                        } else {//load more
                                            adapterComment.addListItems(list);
                                        }
                                    }

                                    //danh sach nho hon page thi dung load more
                                    if (list.size() < ITEM_COUNT) {
                                        //isLoadMore = false;
                                    }
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



    Video_Adapter adapterVideo;
    CommentAdapterSectionParent_MediaComment adapterComment;
    public void initUI(CheckBox btnLike) {

        if (media != null) {
            btnLike.setChecked(media.isLiked());
        }
        btnLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (media != null) {
                    setLikeMedia(media.getMediaId(), b);
                }
            }
        });

        initInputComment();

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv1.setLayoutManager(llm);
        rv1.setNestedScrollingEnabled(false);
        rv1.setHasFixedSize(true);
        rv1.setItemAnimator(new DefaultItemAnimator());
        //rv1.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        LinearLayoutManager llm2 = new LinearLayoutManager(context);
        llm2.setOrientation(RecyclerView.VERTICAL);
        rv2.setLayoutManager(llm2);
        rv2.setNestedScrollingEnabled(false);
        rv2.setHasFixedSize(false);
        rv2.setItemAnimator(new DefaultItemAnimator());

        ViewCompat.setNestedScrollingEnabled(rv1, false);
        ViewCompat.setNestedScrollingEnabled(rv2, false);


        //list video
        if (list != null && list.size() > 0) {

            /*for (int i = 0; i < 10; i++) {
                list.add(list.get(0));
            }*/

            //adapterVideo video
            adapterVideo = new Video_Adapter(context, list, 0);
            rv1.setAdapter(adapterVideo);


            //adapterVideo comment
            adapterComment = new CommentAdapterSectionParent_MediaComment(new ArrayList<MediaComment>(), context);
            adapterComment.shouldShowHeadersForEmptySections(true);
            rv2.setAdapter(adapterComment);
            getComments(videoSelected.getMediaId(), 0);
            rv2.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore() {
                    MyUtils.showToastDebug(context, "Load more...");
                    MediaComment comment = adapterComment.getLastestItem();
                    if (comment != null) {
                        getComments(videoSelected.getMediaId(), comment.getItemId());
                    }
                }
            });

        }
    }

    public MediaComment replyToComment;
    public void initReplyLayout(MediaComment comment, ImageView imgReplyClose, TextView txtReply1, TextView txtReply2) {
        replyToComment = comment;
        if (comment != null) {
            linearReply.setVisibility(View.VISIBLE);
            imgReplyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearReply.setVisibility(View.GONE);
                    replyToComment = null;
                }
            });

            //set hinh, ten, description
            if (comment.getUserName() != null) {
                String name = comment.getUserName();
                txtReply1.setText(name);
            }

            txtReply2.setText(comment.getComment());
            txtInput.requestFocus();
            MyUtils.showKeyboard(context);


        } else {
            linearReply.setVisibility(View.GONE);
        }
    }
}
