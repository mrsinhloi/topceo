package com.topceo.mediaplayer.preview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.desmond.squarecamera.myproject.APIConstants;
import com.topceo.R;
import com.topceo.activity.MH03_PostActivity;
import com.topceo.crop.utils.AppAnalyze;
import com.topceo.post.PostLikeFacebookActivity;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoSelectThumbnailActivity extends AppCompatActivity {

    public static final int NUMBER_COLUMNS = 4;
    private Context context = this;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.linearNext)
    LinearLayout linearNext;
    @BindView(R.id.recyclerView)
    RecyclerView rv;
    @BindView(R.id.txtInfo)
    TextView txtInfo;


    private String videoPath = "";
    private int screenWidth;
    ThumbnailAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressUtils.show(context);
        setContentView(R.layout.activity_video_select_thumbnail);
        ButterKnife.bind(this);

        screenWidth = MyUtils.getScreenWidth(this);
        imgPreview.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth));


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            videoPath = b.getString(APIConstants.VIDEO_URL);
            if (!TextUtils.isEmpty(videoPath)) {
                //thumbnail
                position = 0;

                //log thong tin
                MyUtils.setTextFileInfo(context, txtInfo, videoPath);
            }


            linearNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    uploadVideo();

                    //luu tam hinh thumbnail
                    File file = MyUtils.createImageFile(context);
                    MyUtils.saveBitmap(bitmapSelected, file.getAbsolutePath());

                    //tra ve man hinh post
                    Intent intent = new Intent();
                    intent.putExtra(PostLikeFacebookActivity.THUMB_PATH, file.getAbsolutePath());
                    setResult(RESULT_OK, intent);

                    finish();

                }
            });

        }


        GridLayoutManager layoutManager = new GridLayoutManager(this, NUMBER_COLUMNS);
        rv.setLayoutManager(layoutManager);
        rv.addOnItemTouchListener(new RecyclerItemClickListener(context, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (adapter != null && adapter.getItemCount() > 0) {
                    loadImage(adapter.getBitmapAt(position));
                    adapter.setPositionSelected(position);

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Bitmap> data = MyUtils.extractThumbnail(context, videoPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ThumbnailAdapter(context, data);
                        rv.setAdapter(adapter);

                        //set phan tu dau tien
                        if (adapter.getItemCount() > 0) {
                            loadImage(adapter.getBitmapAt(position));
                            adapter.setPositionSelected(position);
                        }

                        //
                        ProgressUtils.hide();
                    }
                });
            }
        }).start();


    }

    private void loadImage(Bitmap b) {
        bitmapSelected = b;
        int size = screenWidth;//getResources().getDimensionPixelSize(R.dimen.dimen_120dp);
        Glide.with(getApplicationContext())
                .load(b)
                .transition(withCrossFade())
                .override(size, size)
                .into(imgPreview);
    }

    private int position = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    int second = 1000;
//    ArrayList<Bitmap> listImage = new ArrayList<>();

    /*private ArrayList<Bitmap> extractThumbnail(String videoPath) {
        ArrayList<Bitmap> list = new ArrayList<>();

        if (!TextUtils.isEmpty(videoPath)) {
            MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
            mRetriever.setDataSource(videoPath);


//            Bitmap b = mRetriever.getFrameAtTime(second, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            //don vi tinh bang nano second, 3s lay 1 tam
            long second = 3000 * 1000;
            long duration = APIConstants.TIME_VIDEO_RECORD_SECOND * second;
            for (long i = second; i <= duration; i += second) // for incrementing 1s use 1000
            {
                list.add(mRetriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
            }
        }
        return list;
    }*/


    public Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(second, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Bitmap bitmapSelected;

    private void uploadVideo() {

        //luu tam hinh thumbnail
        File file = MyUtils.createImageFile(this);
        MyUtils.saveBitmap(bitmapSelected, file.getAbsolutePath());

        //luu thong tin thumbnail va video path
        AppAnalyze appAnalyze = AppAnalyze.getInstance();
        appAnalyze.setVideoUri(Uri.fromFile(new File(videoPath)));
        appAnalyze.setImageUri(Uri.fromFile(file));


        //qua man hinh upload video
        Intent intent = new Intent(this, MH03_PostActivity.class);
        startActivity(intent);

        finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////


}
