package com.topceo.crop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.squarecamera.myproject.APIConstants;
import com.desmond.squarecamera.myproject.ImageSize;
import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.activity.MH03_PostActivity;
import com.topceo.crop.bitmap.BitmapLoader;
import com.topceo.crop.utils.AppAnalyze;
import com.topceo.crop.utils.NoneFilter;
import com.topceo.crop.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLookupFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter;

public class ImageActivity extends Activity {

    private Context context = this;

    // Bitmap
    public static Bitmap original;
    public static Bitmap effectBitmap;
    private int type = 0;
    private ImageView imgNext;
    private static ImageView imgBack;
    private TextView title_display;
    private TextView title_resetall;
    private GPUImageView mGPUImageView;
    private ArrayList<String> listFilters;
    private List<String> listIndicator;
    private List<String> listColor;
    private ImageView imgSquare;

    public static Dialog progressDialog;
    private AppAnalyze appAnalyze;
    private com.devsmart.android.ui.HorizontalListView rootLayout;
    private RelativeLayout rlmain;
    private int widtImage;
    private Typeface tfSyoB;
    private Typeface tfRotoB;

    private ImageView ivchangefilter;
    private ImageView ivchangeexposure;
    private ImageView ivchangecontrast;
    private ImageView ivchangesharpen;
    private ImageView ivchangetemperature;
    private ImageView ivchangehighlight;
    private ImageView ivchangeshadow;
    private ImageView ivchangevignette;

    private RelativeLayout rlslider;
    private SeekBar sbslider;
    private TextView tvslider;
    private int dis;
    private int center;
    private int wthumb;

    private int filter = 0;
    private int exposure = 6;
    private int contrast = 0;
    private int highlight = 0;
    private int shadow = 0;
    private int sharpen = 0;
    private int temperature = 6;
    private int vignette = 0;

    private SharedPreferences mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
        } catch (Exception e) {
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Set this APK no title
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.effect_screen);
        init();
        registerReceiver();
    }

    public void init() {
        try {
            this.mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Util.updateLanguage(mPrefs, getApplicationContext());
            this.appAnalyze = AppAnalyze.getInstance();
            if (appAnalyze.getImageUri() == null) return;

            this.mGPUImageView = (GPUImageView) this.findViewById(R.id.llimage);
            this.imgNext = (ImageView) findViewById(R.id.imageViewNext);
            this.imgBack = (ImageView) findViewById(R.id.imageViewBack);
            this.title_display = (TextView) findViewById(R.id.title_display);
            this.title_resetall = (TextView) findViewById(R.id.title_resetall);

            this.ivchangefilter = (ImageView) findViewById(R.id.ivchangefilter);
            this.ivchangeexposure = (ImageView) findViewById(R.id.ivchangeexposure);
            this.ivchangecontrast = (ImageView) findViewById(R.id.ivchangecontrast);
            this.ivchangesharpen = (ImageView) findViewById(R.id.ivchangesharpen);
            this.ivchangetemperature = (ImageView) findViewById(R.id.ivchangetemperature);
            this.ivchangehighlight = (ImageView) findViewById(R.id.ivchangehighlight);
            this.ivchangeshadow = (ImageView) findViewById(R.id.ivchangeshadow);
            this.ivchangevignette = (ImageView) findViewById(R.id.ivchangevignette);

            this.rlslider = (RelativeLayout) findViewById(R.id.rlslider);
            this.sbslider = (SeekBar) findViewById(R.id.sbslider);
            this.tvslider = (TextView) findViewById(R.id.tvslider);

            this.rlslider.setVisibility(View.GONE);
            this.rlmain = (RelativeLayout) findViewById(R.id.rlmain);
            this.rootLayout = (com.devsmart.android.ui.HorizontalListView) findViewById(R.id.rootlayout);
            rootLayout.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    try {
                        filter = position;
                        if (imgSquare.getParent() != null) {
                            ((RelativeLayout) imgSquare.getParent()).removeView(imgSquare);
                        }

                        ((RelativeLayout) view).addView(imgSquare);
                        gpuEffect();
                    } catch (Exception e) {
                    }
                }
            });

            widtImage = Util.convertDpToPixel(context, 70);

            LoadPointforSlider();

            this.imgNext.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SaveImage().execute();
                }
            });
            this.imgBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (effectBitmap != null) {
                            effectBitmap.recycle();
                            effectBitmap = null;
                        }
                        if (original != null) {
                            original.recycle();
                            original = null;
                        }

                    } catch (Exception e) {
                    }

                    sendBroadcast(new Intent(MH01_MainActivity.ACTION_PICK_IMAGE));

                    deleteCacheImage();

                    finish();
                }
            });

            this.ivchangefilter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 0;
                    rlslider.setVisibility(View.GONE);
                    rootLayout.setVisibility(View.VISIBLE);
                    setChange();
                }
            });

            this.ivchangeexposure.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 1;
                    rlslider.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.GONE);
                    setTextSlider(exposure);
                    setChange();
                }
            });

            this.ivchangecontrast.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 2;
                    rlslider.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.GONE);
                    setTextSlider(contrast);
                    setChange();
                }
            });

            this.ivchangesharpen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 3;
                    rlslider.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.GONE);
                    setTextSlider(sharpen);
                    setChange();
                }
            });

            this.ivchangehighlight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 4;
                    rlslider.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.GONE);
                    setTextSlider(highlight);
                    setChange();
                }
            });

            this.ivchangeshadow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 5;
                    rlslider.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.GONE);
                    setTextSlider(shadow);
                    setChange();
                }
            });

            this.ivchangetemperature.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 6;
                    rlslider.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.GONE);
                    setTextSlider(temperature);
                    setChange();
                }
            });

            this.ivchangevignette.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    type = 7;
                    rlslider.setVisibility(View.VISIBLE);
                    rootLayout.setVisibility(View.GONE);
                    setTextSlider(vignette);
                    setChange();
                }
            });

            this.title_resetall.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        filter = 0;
                        exposure = 6;
                        contrast = 0;
                        highlight = 0;
                        shadow = 0;
                        sharpen = 0;
                        temperature = 6;
                        vignette = 0;

                        if (type == 0) {
                            if (imgSquare.getParent() != null) {
                                ((RelativeLayout) imgSquare.getParent()).removeView(imgSquare);
                            }
                        } else if (type == 1) {
                            setTextSlider(exposure);
                        } else if (type == 2) {
                            setTextSlider(contrast);
                        } else if (type == 3) {
                            setTextSlider(sharpen);
                        } else if (type == 4) {
                            setTextSlider(highlight);
                        } else if (type == 5) {
                            setTextSlider(shadow);
                        } else if (type == 6) {
                            setTextSlider(temperature);
                        } else if (type == 7) {
                            setTextSlider(vignette);
                        }

                        mGPUImageView.setFilter(new NoneFilter());
                        mGPUImageView.requestRender();
                    } catch (Exception e) {
                    }
                }
            });

            this.appAnalyze.setIsCameraFont(false);
        } catch (Exception e) {
            Toast.makeText(context, getResources().getString(R.string.toast_errorsystem), Toast.LENGTH_LONG).show();
        }
        listColor = new ArrayList<String>(Arrays.asList("#1ea340", "#a3951e", "#c00b98", "#3f6ec3", "#8f08c8", "#949494", "#e46f20", "#ba0bc0", "#3fa3c3", "#3fc3b2", "#3fc390", "#d2434d", "#7fa31e", "#c82970", "#e49820", "#3f97e9", "#e4c120", "#5508c8"));
        listIndicator = new ArrayList<String>();
        loadListEffects();
        ImageActivity.original = BitmapLoader.load(this, new int[]{ImageSize.ORIGINAL_WIDTH, ImageSize.ORIGINAL_HEIGHT}, appAnalyze.getImageUri().getPath());

        int widthScreen = Util.getScreenWidth(context);
        int width = widthScreen;
        int height = widthScreen;
        if (ImageActivity.original != null) {
            width = ImageActivity.original.getWidth();
            height = ImageActivity.original.getHeight();

            if (width > widthScreen) {//tinh theo width screen
                //ratio cua hinh
                float ratio = (float) height / width;

                //tao layout theo ratio hinh
                width = widthScreen;
                height = (int) (width * ratio);

            }
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.setMargins(0,
                (Util.getScreenHeight(this) -
                        Util.convertDpToPixel(this, 190) -
                        Util.getScreenWidth(this)) / 2 +
                        Util.convertDpToPixel(this, 50), 0, 0);

        this.mGPUImageView.setLayoutParams(lp);
        this.mGPUImageView.setImage(ImageActivity.original);
        this.mGPUImageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mGPUImageView.setFilter(new NoneFilter());
                        mGPUImageView.requestRender();
                        break;
                    case MotionEvent.ACTION_UP:
                        gpuEffect();
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        tfSyoB = Typeface.createFromAsset(this.getAssets(), APIConstants.font_Roboto_Light);
        tfRotoB = Typeface.createFromAsset(this.getAssets(), APIConstants.font_Roboto_Bold);
        Util.overrideFonts(this, rlmain, tfSyoB);
        Util.overrideFonts(this, title_display, tfRotoB);
        Util.overrideFonts(this, title_resetall, tfRotoB);
        Util.overrideFonts(this, tvslider, tfRotoB);

        LoadEffect();
    }


    private void setTextSlider(int progress) {
        try {
            sbslider.setProgress(progress);
            if (type == 1) {
                tvslider.setText(String.valueOf(progress - 6));
            } else if (type == 2) {
                tvslider.setText(String.valueOf(progress));
            } else if (type == 3) {
                tvslider.setText(String.valueOf(progress));
            } else if (type == 4) {
                tvslider.setText(String.valueOf(progress));
            } else if (type == 5) {
                tvslider.setText(String.valueOf(progress));
            } else if (type == 6) {
                tvslider.setText(String.valueOf(progress - 6));
            } else if (type == 7) {
                tvslider.setText(String.valueOf(progress));
            }
            RelativeLayout.LayoutParams lpram = (RelativeLayout.LayoutParams) tvslider.getLayoutParams();
            lpram.setMargins(center - wthumb + ((progress - 6) * dis), Util.convertDpToPixel(context, 5), 0, 0);
            tvslider.setLayoutParams(lpram);
        } catch (Exception e) {
        }
    }

    private void LoadPointforSlider() {
        try {
            Bitmap bmpoint = BitmapFactory.decodeResource(getResources(), R.drawable.slidersmall);

            wthumb = Util.convertDpToPixel(context, 30);
            Bitmap bmResize = Util.resizeImage(bmpoint, wthumb, wthumb, false);
            sbslider.setThumb(new BitmapDrawable(getResources(), bmResize));

            int wpoint = wthumb / 3;
            int top = wthumb + wpoint;
            center = Util.getScreenWidth(context) / 2;
            dis = (Util.getScreenWidth(context) - wthumb * 2) / 12;
            int start = wthumb - wpoint / 2;
            for (int i = 0; i < 13; i++) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(wpoint, wpoint);
                lp.setMargins(start, top, 0, 0);

                ImageView iv = new ImageView(this);
                iv.setLayoutParams(lp);
                iv.setImageBitmap(bmpoint);

                rlslider.addView(iv);
                start = start + dis;
            }

            sbslider.bringToFront();
            sbslider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (type == 1) {
                        exposure = seekBar.getProgress();
                    } else if (type == 2) {
                        contrast = seekBar.getProgress();
                    } else if (type == 3) {
                        sharpen = seekBar.getProgress();
                    } else if (type == 4) {
                        highlight = seekBar.getProgress();
                    } else if (type == 5) {
                        shadow = seekBar.getProgress();
                    } else if (type == 6) {
                        temperature = seekBar.getProgress();
                    } else if (type == 7) {
                        vignette = seekBar.getProgress();
                    }
                    setTextSlider(seekBar.getProgress());
                    gpuEffect();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        setTextSlider(progress);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private void LoadEffect() {

        /*try {
            AssetManager am = getAssets();
            String listFile[] = null;
            try {
                listFile = am.list("effect");
            } catch (IOException e) {
            }

            if (listFile == null)
                return;
            ImageAdapterEffect myImageAdapter = new ImageAdapterEffect(this);
            rootLayout.setAdapter(myImageAdapter);

            for (String file : listFile) {
                myImageAdapter.add("effect/" + file);
            }
        } catch (Exception e) {
        }*/

        ImageAdapterEffect myImageAdapter = new ImageAdapterEffect(this);
        rootLayout.setAdapter(myImageAdapter);

        //co 46 effect
        for (int i = 0; i < 46; i++) {
            myImageAdapter.add("effect/" + i);
        }
    }

    public class ImageAdapterEffect extends android.widget.BaseAdapter {
        Context mContext;
        public static final int ACTIVITY_CREATE = 10;

        public ImageAdapterEffect(Context c) {
            mContext = c;

        }

        void add(String path) {
            listIndicator.add(path);
        }

        @Override
        public int getCount() {
            return listIndicator.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.effectitem, null);

//            Bitmap bm = BitmapLoader.loadFromAsset(mContext, new int[]{widtImage, widtImage}, listIndicator.get(position));

//            Bitmap bm = BitmapLoader.loadFromAsset(mContext, new int[]{widtImage, widtImage}, appAnalyze.getImageUri().getPath());

            new AsyncTask<Integer, Void, Bitmap>() {

                GPUImageView imageView;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    imageView = (GPUImageView) retval.findViewById(R.id.image);
                }

                int position = 0;

                @Override
                protected Bitmap doInBackground(Integer... integers) {
                    position = integers[0];
                    return BitmapLoader.load(mContext, new int[]{widtImage, widtImage}, appAnalyze.getImageUri().getPath());
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    if (bitmap != null) {
                        imageView.setImage(bitmap);
                        gpuEffect(imageView, position);
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);


            TextView text = (TextView) retval.findViewById(R.id.text);
            if (position == 0) {
                text.setText("N0");

                if (imgSquare == null) {
                    imgSquare = new ImageView(context);
                    RelativeLayout.LayoutParams lpsquared = new RelativeLayout.LayoutParams(widtImage, widtImage);
                    lpsquared.setMargins(Util.convertDpToPixel(context, 5), 0, 0, 0);
                    imgSquare.setLayoutParams(lpsquared);
                    imgSquare.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_squared));
                    ((RelativeLayout) retval).addView(imgSquare);
                }

            } else {
                text.setText("F" + position);
            }
            Util.overrideFonts(context, text, tfRotoB);

            RelativeLayout rlicontext = (RelativeLayout) retval.findViewById(R.id.rlicontext);
            rlicontext.setBackgroundColor(Color.parseColor(listColor.get(position % listColor.size())));

            return retval;
        }

        @Override
        public Object getItem(int arg0) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (effectBitmap != null) {
                    effectBitmap.recycle();
                    effectBitmap = null;
                }
                if (original != null) {
                    original.recycle();
                    original = null;
                }

            } catch (Exception e) {
            }
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("SimpleDateFormat")
    public String saveImage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime = sdf.format(new Date());
        String currentSeconds = String.valueOf(System.currentTimeMillis());
        String filename = "CHD" + currentDateandTime + currentSeconds + ".jpg";
        File root = new File(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + File.separator);
        if (!root.exists()) {
            root.mkdirs();
        }

        FileOutputStream fo = null;
        try {
            effectBitmap = mGPUImageView.getGPUImage().getBitmapWithFilterApplied();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //hinh da xu ly thi luu xuong chat luong 100%
            effectBitmap.compress(APIConstants.mOutputFormat, 100, bytes);

            // you can create a new file name "test.jpg" in sdcard folder.
            File f = new File(root.getAbsolutePath() + File.separator + filename);
            f.createNewFile();
            // write the bytes in file
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

            // remember close de FileOutput
            fo.flush();

            try {
                addImageGallery(root.getAbsolutePath() + File.separator + filename);
            } catch (Exception e) {
            }

        } catch (Exception e) {
        } finally {

            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                } finally {
                    fo = null;
                }
            }
        }

        return root.getAbsolutePath() + File.separator + filename;
    }

    private void addImageGallery(String url) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, url);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    class SaveImage extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            try {
                if (progressDialog != null)
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
            } catch (Exception ex) {
            }
            try {
                progressDialog = new Dialog(context);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.dialog_loading);
                progressDialog.show();
                progressDialog.setCancelable(false);
                APIConstants.timerDelayRemoveDialog(APIConstants.TIMEOUT_DIALOG, progressDialog);
            } catch (Exception ex) {
            }
        }

        @Override
        protected String doInBackground(Void... dataArr) {
            String savePath = saveImage();
            return savePath;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (progressDialog != null)
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
            } catch (Exception ex) {
            }

            appAnalyze.setVideoUri(null);
            appAnalyze.setImageUri(getImageUri(result));
            Intent intent = new Intent(context, MH03_PostActivity.class);
            startActivity(intent);
        }
    }

    private void loadListEffects() {
        listFilters = new ArrayList<String>();
        AssetManager am = getAssets();
        String listFile[] = null;
        try {
            listFile = am.list("filters");
        } catch (IOException e) {
        }

        if (listFile == null)
            return;

        for (String file : listFile) {
            listFilters.add("filters/" + file);
        }
    }

    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private void setChange() {
        try {
            ivchangefilter.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_filter));
            ivchangeexposure.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_lux));
            ivchangecontrast.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_contrast));
            ivchangesharpen.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_shapren));
            ivchangehighlight.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_highlights));
            ivchangeshadow.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_shadow));
            ivchangetemperature.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_warmth));
            ivchangevignette.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_vignette));

            if (type == 0) {
                title_display.setText(getResources().getString(R.string.editfilter));
                ivchangefilter.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_filtered));
            } else if (type == 1) {
                title_display.setText(getResources().getString(R.string.editexposure));
                ivchangeexposure.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_luxed));
            } else if (type == 2) {
                title_display.setText(getResources().getString(R.string.editcontrast));
                ivchangecontrast.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_contrasted));
            } else if (type == 3) {
                title_display.setText(getResources().getString(R.string.editsharpen));
                ivchangesharpen.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_shaprened));
            } else if (type == 4) {
                title_display.setText(getResources().getString(R.string.edithightlightsave));
                ivchangehighlight.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_highlightsed));
            } else if (type == 5) {
                title_display.setText(getResources().getString(R.string.editshadowsave));
                ivchangeshadow.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_shadowed));
            } else if (type == 6) {
                title_display.setText(getResources().getString(R.string.edittemperature));
                ivchangetemperature.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_warmthed));
            } else if (type == 7) {
                title_display.setText(getResources().getString(R.string.editvignette));
                ivchangevignette.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_edit_vignetteed));
            }
        } catch (NotFoundException e) {
        } catch (Exception e) {
        } catch (OutOfMemoryError e) {
        }
    }

    private void gpuEffect() {
        try {
            Boolean isChange = false;
            GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

            if (exposure != 6) {
                isChange = true;
                float tmp = 2.0f - (((exposure - 6) * 0.1f) + 1);
                GPUImageGammaFilter gammafilter = new GPUImageGammaFilter();
                gammafilter.setGamma(tmp);
                filterGroup.addFilter(gammafilter);
            }

            if (contrast != 0) {
                isChange = true;
                float tmp = (contrast * 0.1f) + 1;
                GPUImageContrastFilter contrastfilter = new GPUImageContrastFilter();
                contrastfilter.setContrast(tmp);
                filterGroup.addFilter(contrastfilter);
            }

            if (sharpen != 0) {
                isChange = true;
                float tmp = sharpen * 0.1f;
                GPUImageSharpenFilter sharpenfilter = new GPUImageSharpenFilter();
                sharpenfilter.setSharpness(tmp);
                filterGroup.addFilter(sharpenfilter);
            }

            if (highlight != 0 || shadow != 0) {
                isChange = true;
                float tmphl = 1.0f - (highlight * 0.08f);
                float tmpsd = shadow * 0.08f;
                GPUImageHighlightShadowFilter hsdfilter = new GPUImageHighlightShadowFilter();
                hsdfilter.setHighlights(tmphl);
                hsdfilter.setShadows(tmpsd);
                filterGroup.addFilter(hsdfilter);
            }

            if (temperature != 6) {
                isChange = true;
                int change = 400;
                if (temperature < 6) {
                    change = 200;
                }
                float tmp = 5000.0f + (temperature - 6) * change;
                GPUImageWhiteBalanceFilter whiteblancefilter = new GPUImageWhiteBalanceFilter();
                whiteblancefilter.setTemperature(tmp);
                filterGroup.addFilter(whiteblancefilter);
            }

            if (vignette != 0) {
                isChange = true;
                PointF centerPoint = new PointF();
                centerPoint.x = 0.5f;
                centerPoint.y = 0.5f;
                float tmp = 1.0f - (vignette * 0.01f);

                GPUImageVignetteFilter vignettefilter = new GPUImageVignetteFilter(centerPoint, new float[]{0.0f, 0.0f, 0.0f}, 0.3f, tmp);
                filterGroup.addFilter(vignettefilter);
            }

            if (filter != 0) {
                isChange = true;
                GPUImageLookupFilter lookupfilter = new GPUImageLookupFilter();
                lookupfilter.setBitmap(BitmapLoader.loadFromAsset(this, new int[]{512, 512}, listFilters.get(filter)));
                filterGroup.addFilter(lookupfilter);
            }

            if (isChange) {
                mGPUImageView.setFilter(filterGroup);
                mGPUImageView.requestRender();
            } else {
                mGPUImageView.setFilter(new NoneFilter());
                mGPUImageView.requestRender();
            }
        } catch (Exception e) {
        }
    }


    private void gpuEffect(GPUImageView gpuImageView, int filter) {
        try {
            Boolean isChange = false;
            GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();


            if (filter != 0) {
                isChange = true;
                GPUImageLookupFilter lookupfilter = new GPUImageLookupFilter();
                lookupfilter.setBitmap(BitmapLoader.loadFromAsset(this, new int[]{512, 512}, listFilters.get(filter)));
                filterGroup.addFilter(lookupfilter);
            }

            if (isChange) {
                gpuImageView.setFilter(filterGroup);
                gpuImageView.requestRender();
            } else {
                gpuImageView.setFilter(new NoneFilter());
                gpuImageView.requestRender();
            }


        } catch (Exception e) {
        }
    }


    public static final String ACTION_FINISH = "ACTION_FINISH_" + ImageActivity.class.getSimpleName();
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(ACTION_FINISH)) {
                    finish();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_FINISH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
    }

    //khi back lai thi xoa hinh cache


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteCacheImage();
    }

    private void deleteCacheImage(){
        String path = appAnalyze.getImageUri().getPath();
        if(!TextUtils.isEmpty(path)){
            File f = new File(path);
            if(f.exists()){
                f.delete();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
            }
        }
    }
}