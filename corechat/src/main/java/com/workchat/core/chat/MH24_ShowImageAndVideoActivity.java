package com.workchat.core.chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.workchat.core.utils.MyUtils;
import com.workchat.core.widgets.TouchImageView;
import com.workchat.corechat.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class MH24_ShowImageAndVideoActivity extends AppCompatActivity {
    public static final String IMAGE_URLS = "IMAGE_URLS";
    public static final String IMAGE_URLS_POSITION_SELECTED = "IMAGE_URLS_POSITION_SELECTED";

    private TextView txtTitle;
    private ImageView img1, img2, img3;
    private String name = "";
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private Context context = this;
    private int measuredWidth = 200;
    private int measuredHeight = 200;


    private ArrayList<String> urls = new ArrayList<String>();

    private void setTitlePage(int number) {
        if (urls.size() == 0) number = 0;
        txtTitle.setText(number + "/" + urls.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh24_show_image_and_video_activity);
        inflater = getLayoutInflater();

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        img1 = (ImageView) findViewById(R.id.imageView1);
        img2 = (ImageView) findViewById(R.id.imageView2);
        img3 = (ImageView) findViewById(R.id.imageView3);//save
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);

        img1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        Point size = new Point();
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            w.getDefaultDisplay().getSize(size);

            measuredWidth = size.x;
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredHeight = d.getHeight();
        }

       /* double density = getResources().getDisplayMetrics().density;
        measuredWidth = (int) (measuredWidth * density);
        measuredHeight = (int) (measuredHeight * density);*/

        Bundle b = getIntent().getExtras();
        if (b != null) {
            urls = (ArrayList<String>) b.getSerializable(IMAGE_URLS);
            if (urls == null) urls = new ArrayList<String>();
            int positionInit = b.getInt(IMAGE_URLS_POSITION_SELECTED, -1);


            adapter = new GalleryAdapter();
            viewPager.setAdapter(adapter);

            if (positionInit >= 0) {
                setTitlePage(positionInit + 1);//vd:1/9
                viewPager.setCurrentItem(positionInit);

            }

        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitlePage(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        img2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //share image
                String url = urls.get(viewPager.getCurrentItem());
                if(MyUtils.isImageUrl(url)) {
                    try {
                        Uri imageUri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");

                        intent.putExtra(Intent.EXTRA_SUBJECT, "Image");
                        intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(imageUri));
                        startActivity(Intent.createChooser(intent, "Share"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    //share video
                    try {
                        Uri imageUri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");

                        intent.putExtra(Intent.EXTRA_SUBJECT, "Video");
                        intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(imageUri));
                        startActivity(Intent.createChooser(intent, "Share"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        //download image
        img3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isTrue = checkPermission();
                if(isTrue) {
                    if (MyUtils.checkInternetConnection(context)) {
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage(R.string.download_confirm);
                        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                int position = viewPager.getCurrentItem();
                                if (position >= 0 && position < urls.size()) {
                                    String path = urls.get(position);
                                    if (!TextUtils.isEmpty(path)) {
                                        if (MyUtils.isImageUrl(path)) {
                                            String name = MyUtils.getFileNameFromUrl(path);
                                            downloadImage(path, name);
                                        } else {
                                            String name = MyUtils.getFileNameFromUrl(path);
                                            downloadVideo(path, name);
                                        }
                                    }
                                }
                            }
                        });
                        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        MyUtils.showThongBao(context);
                    }
                }else{
                    requestPermission(PERMISSION_REQUEST_CAMERA_AND_STORAGE_1);
                }


            }
        });

    }

    GalleryAdapter adapter;

    private class GalleryAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return urls.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            String path = urls.get(position);
            if (!TextUtils.isEmpty(path)) {

                //image
                if(MyUtils.isImageUrl(path)){
                    View v = inflater.inflate(R.layout.mh24_show_image_and_video_activity_item_image, container, false);
                    final TouchImageView touchImage = (TouchImageView) v.findViewById(R.id.touchImageView1);
                    final ProgressBar progressBar1 = (ProgressBar) v.findViewById(R.id.progressBar1);
                    container.addView(v);

                    path = path.replace("\\", "/");
                    name = path.substring(path.lastIndexOf("/") + 1);


                    if (!TextUtils.isEmpty(path)) {
                        /*Picasso.get()
                                .load(path)
//                                .resize(measuredWidth, 0)
                                .placeholder(R.drawable.no_media)
                                .into(touchImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar1.setVisibility(View.GONE);
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        progressBar1.setVisibility(View.GONE);
                                    }
                                });*/
                        //load hinh
                        progressBar1.setVisibility(View.GONE);
                        Glide.with(context)
                                .load(path)
//                                .transform(new RoundedCorners(radius))
                                .placeholder(R.drawable.no_media)
                                .into(touchImage);
                    }
                    return v;

                }else{//video
                    View v = inflater.inflate(R.layout.mh24_show_image_and_video_activity_item_video, container, false);
                    final VideoView vv = (VideoView)v.findViewById(R.id.player);
                    container.addView(v);

                    path = path.replace("\\", "/");
                    name = path.substring(path.lastIndexOf("/") + 1);


                    if (TextUtils.isEmpty(path) == false) {
                        vv.setVideoURI(Uri.parse(path));
                        vv.setOnPreparedListener(new OnPreparedListener() {
                            @Override
                            public void onPrepared() {
                                vv.start();
                            }
                        });
                    }
                    return v;
                }


            }

            return new View(context);

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    String fileLocal = "";

    private void rescanFile(String file) {
        fileLocal = file;
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(file);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);


    }

    private void saveImage(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + getString(R.string.app_name));
        if (!file.exists())
            file.mkdirs();
        else if (!file.isDirectory() && file.canWrite()) {
            file.delete();
            file.mkdirs();
        } else {
            //you can't access there with write permission.
            //Try other way.
        }

        file = new File(file, name);
        if (file.exists() == false) {
            try {
                //file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.flush();
                ostream.close();

                //refresh media
                rescanFile(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    //http://www.codexpedia.com/android/android-downloadmanager-example/
    public void downloadImage(String url, String outputFileName) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(outputFileName)) {
            try {
                //replace blank space
                url = url.replaceAll(" ", "%20");
                outputFileName = outputFileName.replaceAll(" ", "_");


                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Download image to device");
                request.setTitle("Download " + outputFileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                request.setDestinationInExternalPublicDir(getString(R.string.app_name), outputFileName);
                request.setVisibleInDownloadsUi(true);
                request.setMimeType("image/*");

                //Log.d("MainActivity: ", "download folder>>>>" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                MyUtils.showAlertDialog(context, R.string.file_downloading_in_app_bar);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    public void downloadVideo(String url, String outputFileName) {

        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(outputFileName)) {
            try {
                //replace blank space
                url = url.replaceAll(" ", "%20");
                outputFileName = outputFileName.replaceAll(" ", "_");


                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Download video to device");
                request.setTitle("Download " + outputFileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                request.setDestinationInExternalPublicDir(getString(R.string.app_name), outputFileName);
                request.setVisibleInDownloadsUi(true);
                request.setMimeType("media/video");

                //Log.d("MainActivity: ", "download folder>>>>" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                MyUtils.showAlertDialog(context, R.string.file_downloading_in_app_bar);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }


    private final static int PERMISSION_REQUEST_CAMERA_AND_STORAGE_1 = 108;
    String[] arrPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private boolean checkPermission() {

        boolean result = true;
        for (int i = 0; i < arrPermissions.length; i++) {
            int grant = ContextCompat.checkSelfPermission(getApplicationContext(), arrPermissions[i]);
            if (grant == PackageManager.PERMISSION_DENIED) {
                result = false;
                break;
            }
        }

        return result;
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA_AND_STORAGE_1:
                img3.performClick();
                break;
        }

    }

    private void requestPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrPermissions, requestCode);
        }
    }
}
