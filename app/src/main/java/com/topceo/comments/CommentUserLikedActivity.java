package com.topceo.comments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.mediaplayer.video.VideoActivity;
import com.topceo.objects.image.ImageComment;
import com.topceo.objects.image.ImageCommentLike;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.shopping.MediaComment;
import com.topceo.utils.EndlessRecyclerOnScrollListener;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentUserLikedActivity extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView1)
    RecyclerView rv;

    private boolean isMediaComment = false;
    private long commentId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_user_liked);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            isMediaComment = b.getBoolean(MediaComment.IS_MEDIA_COMMENT, false);
            commentId = b.getLong(ImageComment.COMMENT_ID, 0);
            if (commentId > 0) {
                init();
            }
        }


    }

    private void init() {
        initAdapter();
        if(isMediaComment){
            getUserCommentLikeMedia(commentId, 0);
        }else{
            getUserCommentLikeImage(commentId, 0);
        }

    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<ImageCommentLike> listComments = new ArrayList<>();
    private CommentUserLikedAdapter mAdapter;

    private void initAdapter() {
        // use a linear layout manager
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        rv.setNestedScrollingEnabled(false);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                MyUtils.showToastDebug(context, "Load more...");
                ImageCommentLike comment = mAdapter.getLastestItem();
                if (comment != null) {
                    if(isMediaComment){
                        getUserCommentLikeMedia(commentId, comment.getCommentId());
                    }else{
                        getUserCommentLikeImage(commentId, comment.getCommentId());
                    }
                }
            }
        });

        // specify an adapter (see also next example)
        mAdapter = new CommentUserLikedAdapter(listComments, context);
        rv.setAdapter(mAdapter);
    }


////////////////////////////////////////////////////////////////////////////////////////////



    public void getUserCommentLikeImage(long commentId, long lastId) {
        String service = "image/comment/getLikeList";
        ANRequest.PostRequestBuilder param = AndroidNetworking.post(Webservices.URL + service)
                .addQueryParameter("CommentId", String.valueOf(commentId))
                .addQueryParameter("ItemCount", "30")
                .setOkHttpClient(MyApplication.getClient());

        if (lastId > 0) {
            param.addQueryParameter("LastId", String.valueOf(lastId));
        }
        ANRequest request = param.build();

        request.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                Type type = new TypeToken<ArrayList<ImageCommentLike>>(){}.getType();
                ReturnResult result = Webservices.parseJson(response, type, true);

                if (result != null) {
                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                        if(result.getData()!=null){
                            ArrayList<ImageCommentLike> list = (ArrayList<ImageCommentLike>) result.getData();
                            if(list!=null && list.size()>0){
                                if(lastId==0){
                                    //moi tao
                                    mAdapter = new CommentUserLikedAdapter(list, context);
                                    rv.setAdapter(mAdapter);
                                }else{
                                    //add more
                                    mAdapter.add(list);
                                }
                            }
                        }

                    } else {
                        MyUtils.showToastDebug(context, result.getErrorMessage());
                    }
                }

            }

            @Override
            public void onError(ANError ANError) {
                MyUtils.log(ANError.getMessage());
            }
        });
    }

    public void getUserCommentLikeMedia(long commentId, long lastId) {
        MyApplication.apiManager.mediaCommentLikeList(
                commentId,
                (lastId==0)?null:String.valueOf(lastId),
                VideoActivity.ITEM_COUNT,
                new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject json = response.body();
                if (json != null) {
                    Type type = new TypeToken<ArrayList<ImageCommentLike>>(){}.getType();
                    ReturnResult result = Webservices.parseJson(json.toString(), type, true);

                    if (result != null) {
                        if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                            if(result.getData()!=null){
                                ArrayList<ImageCommentLike> list = (ArrayList<ImageCommentLike>) result.getData();
                                if(list!=null && list.size()>0){
                                    if(lastId==0){
                                        //moi tao
                                        mAdapter = new CommentUserLikedAdapter(list, context);
                                        rv.setAdapter(mAdapter);
                                    }else{
                                        //add more
                                        mAdapter.add(list);
                                    }
                                }
                            }
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
    }

}
