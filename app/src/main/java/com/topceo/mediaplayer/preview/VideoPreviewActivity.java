package com.topceo.mediaplayer.preview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.desmond.squarecamera.myproject.APIConstants;
import com.topceo.R;
import com.topceo.activity.MH03_PostActivity;
import com.topceo.crop.utils.AppAnalyze;
import com.topceo.utils.MyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPreviewActivity extends AppCompatActivity {

    @BindView(R.id.video)
    VideoView videoView;
    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.btnOK)
    Button btnOK;
    @BindView(R.id.recyclerView)
    RecyclerView rv;


    private String videoPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squarecamera_activity_video_preview);
        ButterKnife.bind(this);


        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });


        Bundle b = getIntent().getExtras();
        if (b != null) {
            videoPath = b.getString(APIConstants.VIDEO_URL);
            if (!TextUtils.isEmpty(videoPath)) {
                MediaController controller = new MediaController(this);
                controller.setAnchorView(videoView);
                controller.setMediaPlayer(videoView);
                videoView.setMediaController(controller);
                videoView.setVideoURI(Uri.fromFile(new File(videoPath)));
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                        float videoWidth = mp.getVideoWidth();
                        float videoHeight = mp.getVideoHeight();
                        float viewWidth = videoView.getWidth();
                        lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                        videoView.setLayoutParams(lp);
                        playVideo();
                    }
                });
            }


            //thumbnail
            position = 0;
            listImage = extractThumbnail(videoPath);
            if (listImage != null && listImage.size() > 0) {
                loadImage(listImage.get(position));
            }


            btnOK.setOnClickListener(new View.OnClickListener() {
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


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        ThumbnailAdapter adapter = new ThumbnailAdapter(this, videoPath);
        rv.setAdapter(adapter);
        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listImage != null && listImage.size() > 0 && position < listImage.size()) {
                    loadImage(listImage.get(position));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


    }

    private void loadImage(Bitmap b){
        bitmapSelected = b;
        int size = getResources().getDimensionPixelSize(R.dimen.dimen_120dp);
        Glide.with(getApplicationContext())
                .load(b)
                .override(size, size)
                .into(imgPreview);
    }

    private int position = 0;

    void playVideo() {
        if (videoView.isPlaying()) return;
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    int second = 1000;
    ArrayList<Bitmap> listImage = new ArrayList<>();

    private ArrayList<Bitmap> extractThumbnail(String videoPath) {
        ArrayList<Bitmap> list = new ArrayList<>();

        if (!TextUtils.isEmpty(videoPath)) {
            MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
            mRetriever.setDataSource(videoPath);


//            Bitmap b = mRetriever.getFrameAtTime(second, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            long second = 1000 * 1000;
            long duration = 10 * second;
            for (long i = second; i <= duration; i += second) // for incrementing 1s use 1000
            {
                list.add(mRetriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
            }
        }
        return list;
    }


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
