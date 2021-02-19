package com.topceo.mediaplayer.preview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desmond.squarecamera.myproject.APIConstants;
import com.topceo.R;
import com.topceo.activity.MH03_PostActivity;
import com.topceo.crop.utils.AppAnalyze;
import com.topceo.utils.MyUtils;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoSelectThumbnailActivity extends AppCompatActivity {

    public static final int NUMBER_COLUMNS = 4;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.linearNext)
    LinearLayout linearNext;
    @BindView(R.id.recyclerView)
    RecyclerView rv;


    private String videoPath = "";
    private int screenWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_select_thumbnail);
        ButterKnife.bind(this);

        screenWidth = MyUtils.getScreenWidth(this);
        imgPreview.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth));


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
            videoPath = b.getString(APIConstants.VIDEO_URL);
            if (!TextUtils.isEmpty(videoPath)) {
                //thumbnail
                position = 0;
//                listImage = extractThumbnail(videoPath);
                /*if (listImage != null && listImage.size() > 0) {
                    loadImage(listImage.get(position));
                }*/


                //log thong tin
                File f = new File(videoPath);
                long size = f.length() / 1024;

                MyUtils.log("video size " + size + " Kb");
            }


            linearNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadVideo();
                }
            });

        }

        /*actualResolution.setTitleAndMessage("Size", result.getSize() + "");
        isSnapshot.setTitleAndMessage("Snapshot", result.isSnapshot() + "");
        rotation.setTitleAndMessage("Rotation", result.getRotation() + "");
        audio.setTitleAndMessage("Audio", result.getAudio().name());
        audioBitRate.setTitleAndMessage("Audio bit rate", result.getAudioBitRate() + " bits per sec.");
        videoCodec.setTitleAndMessage("VideoCodec", result.getVideoCodec().name());
        videoBitRate.setTitleAndMessage("Video bit rate", result.getVideoBitRate() + " bits per sec.");
        videoFrameRate.setTitleAndMessage("Video frame rate", result.getVideoFrameRate() + " fps");*/


//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        GridLayoutManager layoutManager = new GridLayoutManager(this, NUMBER_COLUMNS);
        rv.setLayoutManager(layoutManager);
        ThumbnailAdapter adapter = new ThumbnailAdapter(this, videoPath);
        rv.setAdapter(adapter);
        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
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

        //set phan tu dau tien
        if (adapter != null && adapter.getItemCount() > 0) {
            loadImage(adapter.getBitmapAt(position));
            adapter.setPositionSelected(position);

        }

    }

    private void loadImage(Bitmap b) {
        bitmapSelected = b;
        int size = screenWidth;//getResources().getDimensionPixelSize(R.dimen.dimen_120dp);
        Glide.with(getApplicationContext())
                .load(b)
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
