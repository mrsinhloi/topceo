package com.topceo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.topceo.R;
import com.topceo.adapter.AddressAdapter;
import com.topceo.config.MyApplication;
import com.topceo.crop.ImageActivity;
import com.topceo.crop.utils.AppAnalyze;
import com.topceo.crop.utils.Util;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.Item;
import com.topceo.objects.other.HashTag;
import com.topceo.objects.other.Mention;
import com.topceo.post.PostUtils;
import com.topceo.post.UploadImageListener;
import com.topceo.post.UploadVideoListener;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.profile.Fragment_Profile_Owner;
import com.topceo.retrofit.PostImageParam;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.socialspost.common.DialogUtils;
import com.topceo.socialspost.facebook.Page;
import com.topceo.socialspost.facebook.PermissionRequest;
import com.topceo.socialview.commons.socialview.Hashtag;
import com.topceo.socialview.commons.widget.HashtagArrayAdapter;
import com.topceo.socialview.commons.widget.MentionArrayAdapter;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.socialview.objects.SearchHashtag;
import com.topceo.socialview.objects.SearchMention;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ProgressUtils;
import com.topceo.video_compressor.video.MediaController;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mumayank.com.airlocationlibrary.AirLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MrPhuong on 2016-08-04.
 */
public class MH03_PostActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private Activity context = this;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.etDescription)
    SocialAutoCompleteTextView txtInput;
    @BindView(R.id.switch1)
    Switch switch1;
    @BindView(R.id.txtLocation)
    TextView txtLocation;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txtTitle)
    TextView txtTitle;


    @BindView(R.id.recyclerView1)
    RecyclerView rv;
    static TextView txtAddLocation;
    static ImageView imgClose;
    static LinearLayout linearLocation;
    static LinearLayout rippleLocation;

    private AppAnalyze appAnalyze;
    private int photoSize = 0;
    private boolean isUploadVideo = false;
    @BindView(R.id.txtProcessing)
    TextView txtProcessing;
    @BindView(R.id.txtShare)
    TextView txtShare;


    @BindView(R.id.switchFacebook)
    SwitchMaterial switchFacebook;
    @BindView(R.id.imgFacebookConfig)
    ImageView imgFacebookConfig;


    private PostUtils postUtils;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_publish_image);
        ButterKnife.bind(this);
        txtAddLocation = (TextView) findViewById(R.id.textView);
        imgClose = (ImageView) findViewById(R.id.imageView1);
        linearLocation = (LinearLayout) findViewById(R.id.linearLocation);
        rippleLocation = (LinearLayout) findViewById(R.id.ripple1);
        linearLocation.setVisibility(View.GONE);
        postUtils = new PostUtils(this);
        //init
        initLocation();
        initFacebookShare();

        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        updateStatusBarColor();

        //init
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);
        if (savedInstanceState == null) {
            appAnalyze = AppAnalyze.getInstance();
            photoUri = appAnalyze.getImageUri();
            videoUri = appAnalyze.getVideoUri();

        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
            videoUri = savedInstanceState.getParcelable(ARG_TAKEN_VIDEO_URI);

        }


        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    showLocation();
                } else {
                    txtLocation.setVisibility(View.GONE);
                    txtLocation.setText("");
                }
            }
        });


        //checbox facebook
        switchFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    loginIfNot();
                }
            }
        });

        showLocation();


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

        compressVideo();
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUploadVideo) {
                    uploadVideo();
                    /*Intent intent = new Intent(context, VideoPreviewActivity.class);
                    intent.putExtra(APIConstants.VIDEO_URL, compressVideoPath);
                    startActivity(intent);*/
                } else {
                    uploadImage();
                }
            }
        });

    }


    private float getSizeFile(String path) {
        File f = new File(path);

        long bytes = f.length();
        float kb = bytes / 1024;
        float mb = kb / 1024;

        return mb;
    }


    private void showLocation() {
        setTextLocation();
    }

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";
    public static final String ARG_TAKEN_VIDEO_URI = "arg_taken_video_uri";

    private Uri photoUri;
    private Uri videoUri;
    private String compressVideoPath;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
        outState.putParcelable(ARG_TAKEN_VIDEO_URI, videoUri);

    }
    /////////////////////////////////
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);

        Glide.with(context)
                .load(photoUri)//images[position%images.length]
                .centerCrop()
                .override(photoSize, photoSize)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                        ivPhoto.setImageDrawable(resource);
                        return true;
                    }
                })
                .into(ivPhoto);
    }


    private static String description = "", locationString = "";
    private void uploadImage() {
        description = txtInput.getText().toString().trim();
//        uploadImageToServer(photoUri.getPath());
        ArrayList<Uri> list = new ArrayList<>();
        list.add(photoUri);
        postUtils.uploadImageToServer(0, list, imageListener);
    }

    private void uploadVideo() {
        description = txtInput.getText().toString().trim();
//        uploadVideoToServer(videoUri.getPath());
        postUtils.uploadVideoToServer(videoUri.getPath(), photoUri.getPath(), videoListener);
    }

    /*public void sharePhoto(String url, String caption) {
        SharePhoto photo = new SharePhoto.Builder()
                .setImageUrl(Uri.parse(url))
                .setCaption(caption)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo).build();
        ShareDialog.show(this, content);
    }*/

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadSuccess(boolean isFinish) {
        try {
            //refresh onefragment
            sendBroadcast(new Intent(Fragment_1_Home_User.ACTION_REFRESH));
            sendBroadcast(new Intent(Fragment_5_User_Profile_Grid.ACTION_REFRESH_LIST));

            //Neu post moi thi tang so luong post trong profile
            sendBroadcast(new Intent(Fragment_Profile_Owner.ACTION_WHEN_HAVE_POST));


            //dong man hinh edit image
            sendBroadcast(new Intent(ImageActivity.ACTION_FINISH));


            if (ImageActivity.effectBitmap != null) {
                ImageActivity.effectBitmap.recycle();
                ImageActivity.effectBitmap = null;
            }
            if (ImageActivity.original != null) {
                ImageActivity.original.recycle();
                ImageActivity.original = null;
            }


        } catch (Exception e) {
        }

        if (isFinish) {
            finish();
        }
    }

    ////////////////////////////////////////////////////////////
    private void setTextLocation() {
        initListAddress();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    List<Address> addresses;
    AddressAdapter addressAdapter;

    //http://androidmastermind.blogspot.co.ke/2016/06/android-google-maps-with-nearyby-places.html
    private void initListAddress() {
        if (lat > 0 && lon > 0) {
            addresses = Util.getListAddress(context, lat, lon, 3);
            addressAdapter = new AddressAdapter(context, addresses);
            rv.setAdapter(addressAdapter);

            rv.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(layoutManager);

            if (addresses.size() > 0) {
                //lay dia chi dau tien
                final Address add = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();//context.getText(R.string.address) + ": "
                /*for (int i = 0; i < add.getMaxAddressLineIndex(); i++) {
                    if (i == add.getMaxAddressLineIndex() - 1) {
                        strReturnedAddress.append(add.getAddressLine(i));
                    } else {
                        strReturnedAddress.append(add.getAddressLine(i)).append(", ");
                    }
                }*/
                strReturnedAddress.append(add.getAddressLine(0));
                MH03_PostActivity.setTextAddress(strReturnedAddress.toString());
            }
//            rv2.setVisibility(View.GONE);

//            loadNearByPlaces(MainActivity.lat,MainActivity.lon);
        } else {
//            MyUtils.showToast(context, R.string.toast_locationerror_find);

        }


    }

    public static void setTextAddress(String address) {
        txtAddLocation.setText(address);
        locationString = address;

        //an ripple
        rippleLocation.setVisibility(View.GONE);
        linearLocation.setVisibility(View.VISIBLE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleLocation.setVisibility(View.VISIBLE);
                linearLocation.setVisibility(View.GONE);

                txtAddLocation.setText("");
                locationString = "";
            }
        });


    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    public static final int PROXIMITY_RADIUS = 5000;

    /*private void loadNearByPlaces(String latitude, String longitude) {
        //YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
        String type = "grocery_or_supermarket";
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
//        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.goolge_map_key));


        AndroidNetworking.get(googlePlacesUrl.toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        MyUtils.log("onResponse: Result= " + response.toString());
                    }

                    @Override
                    public void onError(ANError ANError) {
                        MyUtils.log(ANError.toString());
                    }
                });
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String beforeString = "";
    private String afterString = "";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String textString = "";
    private String textReplace = "";

    private void replaceWord(String tagMention) {
        String text = textString;//txtInput.getText().toString();//vi no la mot item Object nen ko tra ve dc string
        //dua vao cursorPosition de biet dang edit o word nao
        String gold = textReplace;


        //replace word dang edit bang tagMention
        text = text.replace(gold, tagMention);
        MyUtils.log(text);

        txtInput.setText(text);
        txtInput.setSelection(txtInput.length());
    }

    private void initHashtagAdapter(ArrayList<HashTag> tags, String keyword) {
        hashtagsRoot.clear();
        hashtags.clear();

        hashtagsRoot.addAll(tags);
        hashtags.addAll(tags);

        hashtagAdapter = new HashtagAdapter();
        txtInput.setAdapter(hashtagAdapter);
        hashtagAdapter.getFilter().filter(keyword);
        txtInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyUtils.log("before string=" + beforeString + ", afterString=" + afterString);
                //replace text cho txtInput, vi no bi thay the boi Mention@21efbf83
                txtInput.setText(beforeString);
                //dua vao cursorPosition de biet dang edit o word nao
                replaceWord("#" + hashtags.get(i).getHashtag());

            }

        });
    }

    private ArrayList<HashTag> hashtagsRoot = new ArrayList<>();
    private ArrayList<HashTag> hashtags = new ArrayList<>();
    private HashtagAdapter hashtagAdapter;

    private class HashtagAdapter extends BaseAdapter implements Filterable {

        LayoutInflater inflater;

        public HashtagAdapter() {
            inflater = getLayoutInflater();
            getFilter();
        }

        @Override
        public int getCount() {
            return hashtags.size();
        }

        @Override
        public HashTag getItem(int position) {
            return hashtags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class MyHolder {
            TextView txt1;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            MyHolder holder = null;
            if (v == null) {
                holder = new MyHolder();
                v = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
                holder.txt1 = (TextView) v.findViewById(android.R.id.text1);
                holder.txt1.setText("");

                v.setTag(holder);
            } else {
                holder = (MyHolder) v.getTag();
            }

            HashTag item = hashtags.get(position);
            holder.txt1.setText("#" + item.getHashtag());


            return v;
        }


        private ValueFilter valueFilter;


        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {


            //Invoked in a worker thread to filter the data according to the constraint.
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {

                    ArrayList<HashTag> filters = new ArrayList<HashTag>();

                    for (int i = 0; i < hashtagsRoot.size(); i++) {

                        HashTag item = hashtagsRoot.get(i);
                        if (item != null) {
                            if (item.getHashtag().toLowerCase()
                                    .contains(constraint.toString().toLowerCase())) {
                                filters.add(item);
                            }

                        }

                    }

                    results.count = filters.size();
                    results.values = filters;

                } else {


                    results.count = hashtagsRoot.size();
                    results.values = hashtagsRoot;

                }

                return results;
            }


            //Invoked in the UI thread to publish the filtering results in the user interface.
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                hashtags = (ArrayList<HashTag>) results.values;
                //sort alphabet
                Collections.sort(hashtags, comp);

                notifyDataSetChanged();


            }

            private Comparator<HashTag> comp = new Comparator<HashTag>() {
                public int compare(HashTag e1, HashTag e2) {
                    return e1.getHashtag().toString().compareTo(e2.getHashtag().toString());
                }
            };
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void initMentionAdapter(ArrayList<Mention> tags, String keyword) {
        mentionsRoot.clear();
        mentions.clear();

        mentionsRoot.addAll(tags);
        mentions.addAll(tags);

        mentionAdapter = new MentionAdapter();
        txtInput.setAdapter(mentionAdapter);
        mentionAdapter.getFilter().filter(keyword);
        txtInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MyUtils.log("before string=" + beforeString + ", afterString=" + afterString);
                //replace text cho txtInput, vi no bi thay the boi Mention@21efbf83
                txtInput.setText(beforeString);
                //dua vao cursorPosition de biet dang edit o word nao
                replaceWord("@" + mentions.get(i).getUserName());
            }
        });
    }

    private ArrayList<Mention> mentionsRoot = new ArrayList<>();
    private ArrayList<Mention> mentions = new ArrayList<>();
    private MentionAdapter mentionAdapter;

    private class MentionAdapter extends BaseAdapter implements Filterable {
        private int avatarSize = 0;
        LayoutInflater inflater;

        public MentionAdapter() {
            inflater = getLayoutInflater();
            getFilter();
            avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        }

        @Override
        public int getCount() {
            return mentions.size();
        }

        @Override
        public Mention getItem(int position) {
            return mentions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class MyHolder {
            TextView txt1, txt2;
            ImageView img1;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            MyHolder holder = null;
            if (v == null) {
                holder = new MyHolder();
                v = inflater.inflate(R.layout.activity_publish_image_mention_row, null);
                holder.txt1 = (TextView) v.findViewById(R.id.text1);
                holder.txt2 = (TextView) v.findViewById(R.id.text2);
                holder.img1 = (ImageView) v.findViewById(R.id.imageView1);

                v.setTag(holder);
            } else {
                holder = (MyHolder) v.getTag();
            }

            Mention item = mentions.get(position);

            /*if(TextUtils.isEmpty(item.getFullName())){
                holder.txt1.setVisibility(View.GONE);
            }else{
                holder.txt1.setText(item.getFullName());
                holder.txt1.setVisibility(View.VISIBLE);
            }*/

            holder.txt1.setText(item.getFullName());


            holder.txt2.setText("@" + item.getUserName());

            Glide.with(context)
                    .load(item.getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                    .placeholder(R.drawable.ic_no_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.img1);


            return v;
        }


        private ValueFilter valueFilter;


        @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {


            //Invoked in a worker thread to filter the data according to the constraint.
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {

                    ArrayList<Mention> filters = new ArrayList<Mention>();

                    for (int i = 0; i < mentionsRoot.size(); i++) {

                        Mention item = mentionsRoot.get(i);
                        if (item != null) {
                            if (item.getUserName().toLowerCase()
                                    .contains(constraint.toString().toLowerCase())) {
                                filters.add(item);
                            }

                        }

                    }

                    results.count = filters.size();
                    results.values = filters;

                } else {


                    results.count = mentionsRoot.size();
                    results.values = mentionsRoot;

                }

                return results;
            }


            //Invoked in the UI thread to publish the filtering results in the user interface.
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                mentions = (ArrayList<Mention>) results.values;
                //sort alphabet
                Collections.sort(mentions, new Comparator<Mention>() {
                    @Override
                    public int compare(Mention e1, Mention e2) {
                        return e1.getUserName().toString().compareToIgnoreCase(e2.getUserName().toString());
                    }
                });
                notifyDataSetChanged();

            }


        }


    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /*private static final int TYPING_TIMER_LENGTH = 500;
    private boolean mTyping = false;
    private int cursorPosition = 0;

    private Handler mTypingHandler = new Handler();
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            //dang stop thi search
            String text = textString;//txtInput.getText().toString();


            String gold = MyUtils.getStringEditting(text, cursorPosition);
            textReplace = gold;
//            MyUtils.log("search hashtag..."+gold);
            if (gold.contains("@")) {
                String tag = gold.substring(gold.lastIndexOf("@") + 1, gold.length());//chi lay keyword
                // search for tag...
                MyUtils.log("search mention..." + tag);
                searchMention(tag);
            }

            if (gold.contains("#")) {
                String tag = gold.substring(gold.lastIndexOf("#") + 1, gold.length());//chi lay keyword
                // search for tag...
                MyUtils.log("search hashtag..." + tag);
                searchHashtag(tag);
            }

//            Log.e("test", "typing: false");
        }
    };*/


    ///////////////////////////////////////////////////////////////////////////////////////////////


    //location////////////////////
    private double lat = 0, lon = 0;
    private AirLocation airLocation;

    private void initLocation() {
        // Fetch location simply like this whenever you need
        airLocation = new AirLocation(this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location location) {
                // do something
                lat = location.getLatitude();
                lon = location.getLongitude();
                MyUtils.log("location = " + lat + ", " + lon);
                showLocation();
            }

            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
                // do something
            }

        });
    }

    // override and call airLocation object's method by the same name
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    // override and call airLocation object's method by the same name
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //
        /*if (Build.VERSION.SDK_INT >= 23) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {
                    case MyPermission.MY_PERMISSIONS_REQUEST_READ_LOCATION:
                        setTextLocation();
                        break;
                }

            }

        }*/

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

            MentionArrayAdapter<com.topceo.socialview.commons.socialview.Mention> mentionAdapter = new MentionArrayAdapter<com.topceo.socialview.commons.socialview.Mention>(context);
            for (int i = 0; i < list.size(); i++) {
                SearchMention item = list.get(i);
                mentionAdapter.add(new com.topceo.socialview.commons.socialview.Mention(item.getUserName(), item.getFullName(), item.getAvatarSmall()));
            }

            txtInput.setMentionAdapter(mentionAdapter);
        }
    }


    //HASHTAG MENTION//////////////////////////////////////////////////////////////////////////////////////////////
    private void deleteFile(File f) {
        if (f.exists()) {
            f.delete();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
        }
    }

    private void deleteCacheImage() {

        //xoa hinh
        if (photoUri != null && !TextUtils.isEmpty(photoUri.getPath())) {
            File f = new File(photoUri.getPath());
            deleteFile(f);
        }
        if (appAnalyze != null && appAnalyze.getImageUri() != null && !TextUtils.isEmpty(appAnalyze.getImageUri().getPath())) {
            File f = new File(appAnalyze.getImageUri().getPath());
            deleteFile(f);
        }


        //xoa video
        if (videoUri != null && !TextUtils.isEmpty(videoUri.getPath())) {
            File f = new File(videoUri.getPath());
            deleteFile(f);
        }
        if (appAnalyze != null && appAnalyze.getVideoUri() != null && !TextUtils.isEmpty(appAnalyze.getVideoUri().getPath())) {
            File f = new File(appAnalyze.getVideoUri().getPath());
            deleteFile(f);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////




    private void parseJsonVideo(String response) {
        if (!TextUtils.isEmpty(response)) {
            ReturnResult result = Webservices.parseJson(response, ImageItem.class, false);
            if (result != null) {
                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                    //upload thanh cong
                    MyUtils.showToast(context, R.string.toast_upload_success);
                    appAnalyze.setIsupload(true);


                    //kiem tra co share facebook, twitter, zalo
                    //facebook
                    if (switchFacebook.isChecked()) {
                        uploadSuccess(false);

                        //todo post len facebook
                        String message = txtInput.getText().toString().trim();
                        /*if (videoPath != null) {
                            shareVideo(videoPath, message);
                        }*/
                        if (result.getData() != null) {
                            ImageItem item = (ImageItem) result.getData();
                            if (item != null) {
                                String url = item.getImageLarge();
//                                String message = item.getDescription();
                                postVideo(message, url);
                            }
                        }

                    } else {
                        uploadSuccess(true);
                    }
                }
            }


        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////
    private void printFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(filePath);
                String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                File f = new File(filePath);
                long fileSize = f.length();
                String size = Formatter.formatFileSize(context, fileSize);
                MyUtils.log("video size " + size);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private void compressVideo() {
        if (videoUri != null) {
            isUploadVideo = true;
            compressVideoPath = MyUtils.createVideoFile(context).getAbsolutePath();
            //copy file tam
            tempFile = new File(videoUri.getPath());//FileUtils.saveTempFile("TempVideo.mp4", this, videoUri);

            //nen video
            compressVideoPath = tempFile.getPath();
            new VideoCompressor().execute();


        }
    }

    long start = 0;
    //////////////////////////////////////////////////////////////////////////////////////////////
    private File tempFile;

    class VideoCompressor extends AsyncTask<Void, Void, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyUtils.log("compress begin");
            start = SystemClock.elapsedRealtime();
            //log video size
            printFileSize(compressVideoPath);

            txtProcessing.setVisibility(View.VISIBLE);
            txtShare.setVisibility(View.GONE);
        }

        @Override
        protected File doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(compressVideoPath);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file != null) {
                //file sau khi nen
                printFileSize(file.getAbsolutePath());

                compressVideoPath = file.getAbsolutePath();
                videoUri = Uri.fromFile(new File(compressVideoPath));

                MyUtils.howLong(start, "compress video");
                txtProcessing.setVisibility(View.GONE);
                txtShare.setVisibility(View.VISIBLE);
            } else {
                MyUtils.log("Compress video fail");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //xoa hinh tam
        deleteCacheImage();

    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private void initFacebookShare() {

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                finish();
            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                finish();
            }
        });
    }


    /**
     * @param b
     * @param caption: facebook ko cho phép forward caption khi share
     */
    public void sharePhoto(Bitmap b, String caption) {

        if (b != null) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(b)
                        .setCaption(caption)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            }
        }

    }


    public void shareVideo(String videoPath, String caption) {

        if (!TextUtils.isEmpty(videoPath)) {
            if (ShareDialog.canShow(ShareLinkContent.class)) {

                Uri videoFileUri = Uri.fromFile(new File(videoPath));
                ShareVideo video = new ShareVideo.Builder()
                        .setLocalUrl(videoFileUri)
                        .build();
                ShareVideoContent content = new ShareVideoContent.Builder()
                        .setVideo(video)
                        .build();
                shareDialog.show(content);
            }
        }

    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void loginIfNot() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (!isLoggedIn) {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            AccessToken token = loginResult.getAccessToken();
                            getProfile();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            switchFacebook.setChecked(false);
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            switchFacebook.setChecked(false);
                        }
                    });
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(PermissionRequest.PERMISSION_PAGE_AND_USER_PROFILE));

        } else {
            getProfile();
        }
    }

    private void getProfile() {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            MyApplication.facebookApi.setUserId(profile.getId());
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            MyApplication.facebookApi.setAccessToken(accessToken);

            getPages();
        } else {
            //Do lay asyn nen chua tra ve thong tin profile, track de don thong tin tra ve
            ProfileTracker profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    this.stopTracking();
                    Profile.setCurrentProfile(currentProfile);
                    getProfile();
                }
            };
            profileTracker.startTracking();

        }

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////


    private ArrayList<Page> selectedPages = new ArrayList<>();

    private void getPages() {
        MyApplication.facebookApi.getPages(response -> {
            if (response.getError() == null) {
                /* handle the result */
                ArrayList<Page> list = MyApplication.facebookApi.parseListPage(response.getJSONObject());
                if (list.size() > 0) {
                    //show dialog user chon 1 page, tam thoi lay page dau tien post len
                    selectedPages = Page.Companion.getPageSelected(list, context);
                    //neu chua co page nao chon thi hien thi chon page
                    if (selectedPages.size() == 0) {
                        showPageConfig(list);
                    }

                    imgFacebookConfig.setVisibility(View.VISIBLE);
                    imgFacebookConfig.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Chon page nao muon dang
                            showPageConfig(list);
                        }
                    });

                } else {
                    logoutFacebook();
                    MyUtils.showAlertDialog(context, R.string.user_has_not_page);
                }
            }
        });
    }

    private void confirmLogoutFacebook() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.confirm_logout);
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                //revoke toan bo quyen roi logout
                ProgressUtils.show(context, getString(R.string.logout));
                PermissionRequest.makeRevokePermissionPage(new PermissionRequest.CallbackRevokePermission() {
                    @Override
                    public void completed() {
                        logoutFacebook();
                        ProgressUtils.hide();
                    }
                });

            }
        });
        alertDialogBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void logoutFacebook() {

        LoginManager.getInstance().logOut();

        //clear cache
        switchFacebook.setChecked(false);
        Page.Companion.saveNameSelected(null, null, context);
        selectedPages.clear();
        imgFacebookConfig.setVisibility(View.GONE);
    }

    private void showPageConfig(ArrayList<Page> list) {
        String[] arrayName = Page.Companion.getArrayName(list);
        boolean[] checkedItems = Page.Companion.getCheckedItems(list, context);
        DialogUtils.Companion.selectMultipleItemDialogWithLogoutFacebook(context, R.string.facebook_select_page_title,
                arrayName, checkedItems,
                new DialogUtils.SelectMultipleCallbackFacebook() {

                    @Override
                    public void onOK(@NotNull boolean[] checkedItems) {
                        Page.Companion.saveNameSelected(list, checkedItems, context);
                        //lay lai danh sach da chon
                        selectedPages = Page.Companion.getPageSelected(list, context);
                        //khong chon cong dong nao thi off
                        switchFacebook.setChecked(selectedPages.size() > 0);
                    }

                    @Override
                    public void onLogout() {
                        confirmLogoutFacebook();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private UploadImageListener imageListener = new UploadImageListener() {
        @Override
        public void onUploadImageSuccess(String GUID, ArrayList<Item> itemContent) {
            //upload hinh xong thi upload cac thong tin con lai
            PostImageParam post = new PostImageParam(GUID, false, description,
                    locationString, lat, lon, itemContent, txtInput.getHashtags(), txtInput.getMentions(),
                    ImageItem.ITEM_TYPE_INSTAGRAM, null);
            MyApplication.apiManager.addImageItem(post, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    parseJson(obj.toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////
    private void parseJson(String response) {
        if (!TextUtils.isEmpty(response)) {
            ReturnResult result = Webservices.parseJson(response, ImageItem.class, false);
            if (result != null) {
                if (result.getErrorCode() == ReturnResult.SUCCESS) {
                    //upload thanh cong
                    MyUtils.showToast(context, R.string.toast_upload_success);
                    appAnalyze.setIsupload(true);

                    //kiem tra co share facebook, twitter, zalo
                    //facebook
                    if (switchFacebook.isChecked()) {
                        uploadSuccess(false);
                        if (result.getData() != null) {
                            ImageItem item = (ImageItem) result.getData();
                            if (item != null) {
                                String url = item.getImageLarge();
//                                String message = item.getDescription();
                                postPhotoUrl(description, url);
                            }
                        }
                    } else {
                        uploadSuccess(true);
                    }
                }
            }
        }
    }

    //facebook
    //////////////////////////////////////////////////////////////////////////////////////////////
    private void postPhotoUrl(String message, String url) {
//        String url = "https://photo2.tinhte.vn/data/attachment-files/2020/07/5069141_09B406D8-CA87-4A41-9BE8-853AB83C5879.jpeg";
        Page currentPage = null;
        if (selectedPages != null && selectedPages.size() > 0) {
            currentPage = selectedPages.get(0);
            selectedPages.remove(0);
        }

        if (currentPage != null) {
            ProgressUtils.show(context, getString(R.string.facebook_posting));
            MyApplication.facebookApi.postPhotoUrl(message, url, currentPage.getId(), currentPage.getAccess_token(), true, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response.getError() == null) {
                        ProgressUtils.hide();
                        postPhotoUrl(message, url);
                    }
                }
            });
        } else {
            MyUtils.showToast(context, R.string.post_facebook_success);
            finish();
        }

    }

    private void postVideo(String message, String url) {
        Page currentPage = null;
        if (selectedPages != null && selectedPages.size() > 0) {
            currentPage = selectedPages.get(0);
            selectedPages.remove(0);
        }

        if (currentPage != null) {
            ProgressUtils.show(context, getString(R.string.facebook_posting));
            MyApplication.facebookApi.postVideoUrl(message, url, currentPage.getId(), currentPage.getAccess_token(), new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    if (response.getError() == null) {
                        ProgressUtils.hide();
                        postPhotoUrl(message, url);
                    }
                }
            });
        } else {
            MyUtils.showToast(context, R.string.post_facebook_success);
            finish();
        }

    }

    private UploadVideoListener videoListener = new UploadVideoListener() {
        @Override
        public void onUploadVideoSuccess(String GUID, ArrayList<Item> itemContent) {
            //up file video xong thi upload ket qua cuoi cung len server
            PostImageParam post = new PostImageParam(GUID, false, description,
                    locationString, lat, lon, itemContent, txtInput.getHashtags(), txtInput.getMentions(),
                    ImageItem.ITEM_TYPE_INSTAGRAM, null);
            MyApplication.apiManager.addImageItem(post, new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject obj = response.body();
                    parseJsonVideo(obj.toString());
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }
    };


}
