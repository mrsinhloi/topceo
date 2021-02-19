package com.topceo.shopping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.topceo.R;
import com.topceo.views.TouchImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;



public class ShowAlbumActivity extends Activity {
    public static final String IMAGE_URLS = "IMAGE_URLS";
    public static final String IMAGE_URLS_POSITION_SELECTED = "IMAGE_URLS_POSITION_SELECTED";

    private TextView txtTitle;
    private ImageView img1;
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
        setContentView(R.layout.show_album_activity);
        inflater = getLayoutInflater();

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        img1 = (ImageView) findViewById(R.id.imageView1);
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


        Bundle b = getIntent().getExtras();
        if (b != null) {
            urls = (ArrayList<String>) b.getSerializable(IMAGE_URLS);
            if (urls == null) urls = new ArrayList<String>();
            int positionInit = b.getInt(IMAGE_URLS_POSITION_SELECTED, 0);


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

                View v = inflater.inflate(R.layout.show_album_activity_item_image, container, false);
                final TouchImageView touchImage = (TouchImageView) v.findViewById(R.id.touchImageView1);
                final ProgressBar progressBar1 = (ProgressBar) v.findViewById(R.id.progressBar1);
                container.addView(v);

                path = path.replace("\\", "/");
                name = path.substring(path.lastIndexOf("/") + 1);


                if (!TextUtils.isEmpty(path)) {
                    /*Picasso.get()
                            .load(path)
                            .resize(measuredWidth, 0)
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
                    Glide.with(context)
                            .load(path)
                            .override(measuredWidth, 0)
                            .placeholder(R.drawable.no_media)
                            .into(new Target<Drawable>() {
                                @Override
                                public void onLoadStarted(@Nullable Drawable placeholder) {

                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    progressBar1.setVisibility(View.GONE);
                                }

                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    touchImage.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }

                                @Override
                                public void getSize(@NonNull SizeReadyCallback cb) {

                                }

                                @Override
                                public void removeCallback(@NonNull SizeReadyCallback cb) {

                                }

                                @Override
                                public void setRequest(@Nullable Request request) {

                                }

                                @Nullable
                                @Override
                                public Request getRequest() {
                                    return null;
                                }

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onStop() {
                                    progressBar1.setVisibility(View.GONE);
                                }

                                @Override
                                public void onDestroy() {
                                }
                            });
                }
                return v;

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


}
