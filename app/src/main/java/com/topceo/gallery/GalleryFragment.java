package com.topceo.gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.desmond.squarecamera.myproject.APIConstants;
import com.topceo.R;
import com.topceo.crop.ImageActivity;
import com.topceo.crop.bitmap.BitmapLoader;
import com.topceo.db.TinyDB;
import com.topceo.mediaplayer.preview.TrimmerActivity;
import com.topceo.mediaplayer.preview.VideoSelectThumbnailActivity;
import com.topceo.objects.image.ImageSize;
import com.topceo.utils.MyUtils;
import com.topceo.views.GradientTextView;
import com.mianamiana.view.CopyType;
import com.mianamiana.view.ICropView;
import com.mianamiana.view.MianaCropView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GalleryFragment extends Fragment {

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @BindView(R.id.toolbar)
    LinearLayout toolbar;
    @BindView(R.id.relativeImage)
    RelativeLayout relativeImage;


    @BindView(R.id.rvGallery)
    RecyclerView rvGallery;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txtNext)
    GradientTextView txtNext;
    @BindView(R.id.imgFill)
    ImageView imgFill;


    @BindView(R.id.relativeVideo)
    RelativeLayout relativeVideo;
    @BindView(R.id.videoView)
    VideoView videoView;

    static RelativeLayout relativeImageStatic;
    static RelativeLayout relativeVideoStatic;
    static VideoView videoViewStatic;


    //    static AppBarLayout appBarLayout;
    static MianaCropView cropView;


    private static int imageWidth = 0;
    private static Context context;
    private TinyDB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        getContext().getTheme().applyStyle(R.style.AppTheme_NoActionBar_Fullscreen2, true);

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, view);
        context = getContext();
        db = new TinyDB(getContext());


        relativeImageStatic = relativeImage;
        relativeVideoStatic = relativeVideo;
        videoViewStatic = videoView;

//        appBarLayout=(AppBarLayout)view.findViewById(R.id.appBarLayout1);
        cropView = (MianaCropView) view.findViewById(R.id.cropView);

        //giao dien thi set screenwidth, khi tao bitmap thi lay theo size yeu cau tu server (origin_width)
        imageWidth = MyUtils.getScreenWidth(getActivity());
        relativeImage.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
        relativeVideo.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

        cropView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, imageWidth));


        cropView.setCropType(CopyType.SQUARE);
        cropView.setOnFillableChangeListener(new ICropView.OnFillableChangeListener() {
            @Override
            public void onFillableChange(boolean isFillable) {
                //co fill ko

            }
        });

        //set truoc thi moi xai dc chuc nang nay
        cropView.setIsAdvancedMode(true);
        imgFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropView.setFillMode(!cropView.isFillMode());
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVideo) {
                    if(!TextUtils.isEmpty(localPathSelected)){
                        //lay duration
                        long duration = MyUtils.getDurationOfVideo(getContext(), localPathSelected);

                        //neu >1 phut thi vao man hinh trim video
                        long second = duration / 1000;
                        if (second > APIConstants.TIME_VIDEO_RECORD_SECOND) {
                            Intent intent = new Intent(getContext(), TrimmerActivity.class);
                            intent.putExtra(TrimmerActivity.EXTRA_VIDEO_PATH, localPathSelected);
                            startActivity(intent);
                        } else {//else vao man hinh chon cover
                            Intent intent = new Intent(context, VideoSelectThumbnailActivity.class);
                            intent.putExtra(APIConstants.VIDEO_URL, localPathSelected);
                            startActivity(intent);
                        }

                        //dong man hinh
                        getActivity().finish();
                    }
                } else {
                    //crop hinh
                    Bitmap b = cropView.getCroppedBitmap();

                    //luu hinh
                    MyUtils.saveBitmapTemp(b);

                    //chuyen qua edit hinh
                    Intent intent = new Intent(context, ImageActivity.class);
                    context.startActivity(intent);

                    //thoat
                    b.recycle();
                    getActivity().finish();
                }
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disableScroll();
        initAdapter();

    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    private GalleryAdapter mAdapter;

    private void initAdapter() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvGallery.setHasFixedSize(true);

        // use a linear layout manager
        int spansCount = getContext().getResources().getInteger(R.integer.grid_span);
        final GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), spansCount);
//        mLayoutManager.offsetChildrenHorizontal(getResources().getDimensionPixelOffset(R.dimen.grid_cell_offset));
//        mLayoutManager.offsetChildrenVertical(getResources().getDimensionPixelOffset(R.dimen.grid_cell_offset));


        rvGallery.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.grid_cell_offset);
        rvGallery.addItemDecoration(itemDecoration);


//        if(getActivity()!=null && !getActivity().isFinishing()){
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }

        task = new TaskLoadGallery();
        task.execute();
//        }

        //lay data
       /* getListPhotoFirst();
        if (feeds.size() > 0) {
            GalleryFragment.setImageFull(feeds.get(0).getUrl());
        }

        // specify an adapter (see also next example)
        mAdapter = new GalleryAdapter(getActivity(), feeds);
        rvGallery.setAdapter(mAdapter);*/

    }


    private TaskLoadGallery task;

    private class TaskLoadGallery extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAdapter = new GalleryAdapter(getActivity(), new ArrayList<ParcelableImageModel>());
            rvGallery.setAdapter(mAdapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //lay data
            getListPhotoFirst();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // specify an adapter (see also next example)
//            mAdapter = new GalleryAdapter(getActivity(), feeds);
//            rvGallery.setAdapter(mAdapter);
            if(feeds!=null && feeds.size()>0){
                setAdapter();
            }else{
                //doc len
                readMemory();
                setAdapter();
            }


        }
    }

    private void setAdapter(){
        if(feeds!=null && feeds.size()>0) {
            mAdapter.addItems(feeds);
            if (feeds.size() > 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GalleryFragment.setImageFull(feeds.get(0).getUrl(), feeds.get(0).isVideo());
                    }
                }, 30);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }

        if (videoView != null && videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    private ArrayList<ParcelableImageModel> feeds = new ArrayList<>();

    private void getListPhotoFirst() {
        long start = SystemClock.elapsedRealtime();
        feeds = db.getListParcelableImageModel(ParcelableImageModel.LIST_IMAGE_MODEL);//getGalleryPhotos();
        MyUtils.howLong(start, "read gallery");

    }

    private void readMemory(){
        if(feeds==null || feeds.size()==0){
            /*try {

                // Get relevant columns for use later.
                String[] projection = {
//                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DATA
//                        MediaStore.Files.FileColumns.DATE_ADDED,
//                        MediaStore.Files.FileColumns.MEDIA_TYPE,
//                        MediaStore.Files.FileColumns.MIME_TYPE,
//                        MediaStore.Files.FileColumns.TITLE
                };

                // Return only video and image metadata.
                String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

                Uri queryUri = MediaStore.Files.getContentUri("external");

                CursorLoader cursorLoader = new CursorLoader(
                        context,
                        queryUri,
                        projection,
                        selection,
                        null, // Selection args (none).
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
                );

                Cursor cursor = cursorLoader.loadInBackground();
                if (cursor != null && cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        ParcelableImageModel item = new ParcelableImageModel();

                        int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                        String url = cursor.getString(dataColumnIndex);
//                            MyUtils.log("url = " + url);

                        //chi nhan : mp4, jpg, png
                        if (MyUtils.isMp4(url)) {
                        } else if (MyUtils.isJpg_Png(url)) {
                            item.setUrl(url);
                            item.setVideo(false);
                            feeds.add(item);
                        }

                    }

                    //luu xuong bo nho
                    if (feeds != null && feeds.size() > 0) {
                        db.putListImageGallery(ParcelableImageModel.LIST_IMAGE_MODEL, feeds);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/


            try {

                Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.MediaColumns.DATA};
                // Return only video and image metadata.
                String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                Cursor cursor = getActivity().getContentResolver().query(
                        uri, projection, null, null,
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
                if (cursor != null && cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        ParcelableImageModel item = new ParcelableImageModel();

                        int dataColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                        String url = cursor.getString(dataColumnIndex);

                        item.setUrl(url);
                        item.setVideo(false);
                        feeds.add(item);
                    }

                    //luu xuong bo nho
                    if (feeds != null && feeds.size() > 0) {
                        db.putListImageGallery(ParcelableImageModel.LIST_IMAGE_MODEL, feeds);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*private ArrayList<ParcelableImageModel> getGalleryPhotos() {
        ArrayList<ParcelableImageModel> galleryList = new ArrayList<ParcelableImageModel>();
        long start = SystemClock.elapsedRealtime();
        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = getActivity().managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    ParcelableImageModel item = new ParcelableImageModel();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.setUrl(imagecursor.getString(dataColumnIndex));

                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        MyUtils.howLong(start, "get gallery photo");
        return galleryList;
    }*/


    private void disableScroll() {
        //http://stackoverflow.com/questions/30771156/how-to-set-applayout-scrollflags-for-toolbar-programmatically
        //http://stackoverflow.com/questions/30779123/need-to-disable-expand-on-collapsingtoolbarlayout-for-certain-fragments

        /*AppBarLayout.LayoutParams appBarParams=(AppBarLayout.LayoutParams)toolbar.getLayoutParams();
        appBarParams.setScrollFlags(0);


        appBarParams=(AppBarLayout.LayoutParams)linearImage.getLayoutParams();
        appBarParams.setScrollFlags(0);
        appBarParams.setScrollInterpolator(null);

        rvGallery.setNestedScrollingEnabled(false);*/

        PickImageActivity.viewPager.setPagingEnabled(false);
    }


    private static String localPathSelected = "";
    private static boolean isVideo = false;

    public static void setImageFull(String localPath, boolean isVideo1) {
        if (!TextUtils.isEmpty(localPath)) {
            localPathSelected = localPath;
            isVideo = isVideo1;
            MyUtils.log(localPath);

            if (isVideo) {
                relativeImageStatic.setVisibility(View.GONE);
                relativeVideoStatic.setVisibility(View.VISIBLE);



                if (!TextUtils.isEmpty(localPath)) {
                    File f = new File(localPath);
                    if(f.exists()){
                        MediaController controller = new MediaController(context);
                        controller.setAnchorView(videoViewStatic);
                        controller.setMediaPlayer(videoViewStatic);
                        videoViewStatic.setMediaController(controller);
                        videoViewStatic.setVideoURI(Uri.fromFile(new File(localPath)));
                        videoViewStatic.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                //video view
                                ViewGroup.LayoutParams lp = videoViewStatic.getLayoutParams();
                                float videoWidth = mp.getVideoWidth();
                                float videoHeight = mp.getVideoHeight();

                                float ratio = videoHeight / videoWidth;

                                //container ratio = 1 do la hinh vuong
                                videoHeight = imageWidth;
                                videoWidth = videoHeight / ratio;


                                lp.width = (int) videoWidth;
                                lp.height = (int) videoHeight;


                                videoViewStatic.setLayoutParams(lp);

                                playVideo();
                            }

                        });
                        videoViewStatic.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                showAlertDialog(context, R.string.file_not_found);
                                localPathSelected = null;
                                return true;
                            }
                        });
                    }else{
                        MyUtils.showAlertDialog(context, R.string.file_not_found);
                    }

                }
            } else {

                relativeImageStatic.setVisibility(View.VISIBLE);
                relativeVideoStatic.setVisibility(View.GONE);

                Bitmap bmtmp = BitmapLoader.load(context, new int[]{ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_WIDTH}, localPath);
                cropView.setImageBitmap(bmtmp);
            }


        }
    }

    static void playVideo() {
        if (videoViewStatic.isPlaying()) return;
        videoViewStatic.start();
    }


    static android.app.AlertDialog alertDialog;
    private static void showAlertDialog(Context context, int message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
//        alertDialogBuilder.setIcon(R.drawable.fail);
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setTextColor(Color.BLACK);
    }

}
