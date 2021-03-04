package com.topceo.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.config.MyApplication;
import com.topceo.fragments.GlideCircleTransform;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.HashTag;
import com.topceo.objects.other.Mention;
import com.topceo.post.PostUtils;
import com.topceo.post.UpdateCompletListener;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.socialview.commons.socialview.Hashtag;
import com.topceo.socialview.commons.widget.HashtagArrayAdapter;
import com.topceo.socialview.commons.widget.MentionArrayAdapter;
import com.topceo.socialview.commons.widget.SocialAutoCompleteTextView;
import com.topceo.socialview.objects.SearchHashtag;
import com.topceo.socialview.objects.SearchMention;
import com.topceo.utils.KeyboardStatusDetector;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MH04_FeedEditActivity extends AppCompatActivity {
    private Context context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imageView1)
    ImageView img1;
    @BindView(R.id.imageView2)
    ImageView img2;
    @BindView(R.id.textView1)
    TextView txt1;
    @BindView(R.id.textView2)
    TextView txt2;

    @BindView(R.id.etDescription)
    SocialAutoCompleteTextView txtInput;
    @BindView(R.id.scrollView1)
    ScrollView sv;


    private ImageItem item;
    private int avatarSize = 0;
    private int widthScreen = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkChat));
        }
    }

    private PostUtils postUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_edit);
        ButterKnife.bind(this);

        postUtils = new PostUtils(this);
        widthScreen = MyUtils.getScreenWidth(context);
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size_medium);
        img1.setLayoutParams(new RelativeLayout.LayoutParams(avatarSize, avatarSize));
        img2.setLayoutParams(new LinearLayout.LayoutParams(widthScreen, widthScreen));

        /*setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        updateStatusBarColor();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            item = (ImageItem) b.getParcelable(ImageItem.IMAGE_ITEM);

            if (item != null) {
                Glide.with(context)
                        .load(item.getOwner().getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                        .placeholder(R.drawable.ic_no_avatar)
                        .override(avatarSize, avatarSize)
                        .transform(new GlideCircleTransform(context))
                        .into(img1);

                Glide.with(context)
                        .load(item.getImageLarge())
//                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)//R.drawable.progress_animation
                        .override(widthScreen, widthScreen)
                        .into(img2);

                txtInput.setText(MyUtils.fromHtml(item.getDescription()));
                txt1.setText(item.getOwner().getFullName());
                txt2.setText(item.getLocation());

                int positon = 0;
                positon = txtInput.getText().length();
                if (positon < 0) positon = 0;
                txtInput.setSelection(positon);

            }

        }

        //init suggestion
        initAutoCompletTextView();

        //detect keyboard open/close
        registerKeyboardShowHide();
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        if (menu.getItemId() == R.id.action_publish) {
            //update
            updateDescription();
            return true;
        } else {
            return super.onOptionsItemSelected(menu);
        }
    }

    private void updateDescription() {
        final String txt = txtInput.getText().toString().trim();
        this.item.setDescription(txt);
        postUtils.updateDescription(item, txt, txtInput.getHashtags(), txtInput.getMentions(), new UpdateCompletListener() {
            @Override
            public void onUpdateSuccess() {
                MyUtils.hideKeyboard(MH04_FeedEditActivity.this);
                //finish
                finish();
            }
        });

    }

    /////////////////////////////////////////////////////////////////////////////////

    private void initAutoCompletTextView() {
        /*txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeString=charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(hashtagAdapter!=null){
                    hashtagAdapter.getFilter().filter(charSequence);
                }
                cursorPosition=i;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                afterString=editable.toString();
                textString=editable.toString();

                //neu ky tu cuoi cung la " " thi ko query, vi ho sap nhap tu moi
                if(!TextUtils.isEmpty(textString) && textString.length()>1 && !textString.substring(textString.length()-1).equals(" ")){
                    if(!mTyping){
                        mTyping=true;
                    }
                    mTypingHandler.removeCallbacks(onTypingTimeout);
                    mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
                }
            }
        });*/

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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*private void searchHashtag(final String keyword){
        if(!TextUtils.isEmpty(keyword)&& keyword.length()>1) {
            AndroidNetworking.post(Webservices.URL + "image/searchHashtag")
                    .addQueryParameter("Keyword", keyword)
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Type collectionType = new TypeToken<List<HashTag>>() {}.getType();
                            ReturnResult result = Webservices.parseJson(response, collectionType, true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    if(result.getData()!=null){
                                        ArrayList<HashTag> tags = (ArrayList<HashTag>) result.getData();
//                                        MyUtils.showToast(context, "tag size " + tags.size());
                                        initHashtagAdapter(tags, keyword);

                                    }

                                } else {
                                    MyUtils.showToast(context, R.string.welcome_register);

                                }
                            }
                            mTyping=false;
                        }

                        @Override
                        public void onError(ANError ANError) {
                            MyUtils.log(ANError.getMessage());
                            mTyping=false;
                        }
                    });
        }
    }

    private void searchMention(final String keyword){
        if(!TextUtils.isEmpty(keyword)&& keyword.length()>1){
            AndroidNetworking.post(Webservices.URL + "image/searchUserForMention")
                    .addQueryParameter("Keyword", keyword)
                    .setOkHttpClient(MyApplication.getClient())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Type collectionType = new TypeToken<List<Mention>>(){}.getType();
                            ReturnResult result = Webservices.parseJson(response, collectionType,true);

                            if (result != null) {
                                if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                    if(result.getData()!=null) {
                                        ArrayList<Mention> tags = (ArrayList<Mention>) result.getData();
//                                        MyUtils.showToast(context, "tag size " + tags.size());
                                        initMentionAdapter(tags, keyword);
                                    }
                                } else {
                                    MyUtils.showToast(context, R.string.welcome_register);

                                }
                            }

                            mTyping=false;
                        }

                        @Override
                        public void onError(ANError ANError) {
                            MyUtils.log(ANError.getMessage());
                            mTyping=false;
                        }
                    });
        }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String textString = "";
    private String textReplace = "";
    private String beforeString = "";
    private String afterString = "";

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

        LayoutInflater inflater;

        public MentionAdapter() {
            inflater = getLayoutInflater();
            getFilter();
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
    private int cursorPosition=0;

    private Handler mTypingHandler = new Handler();
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            //dang stop thi search
            String text=textString;//txtInput.getText().toString();


            String gold=MyUtils.getStringEditting(text, cursorPosition);
            textReplace=gold;
//            MyUtils.log("search hashtag..."+gold);
            if (gold.contains("@")){
                String tag = gold.substring(gold.lastIndexOf("@")+1, gold.length());//chi lay keyword
                // search for tag...
                MyUtils.log("search mention..."+tag);
                searchMention(tag);
            }

            if (gold.contains("#")){
                String tag = gold.substring(gold.lastIndexOf("#")+1, gold.length());//chi lay keyword
                // search for tag...
                MyUtils.log("search hashtag..."+tag);
                searchHashtag(tag);
            }

//            Log.e("test", "typing: false");
        }
    };*/
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void scrollDown() {
        //scroll to bottom
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private void registerKeyboardShowHide() {
        KeyboardStatusDetector key = new KeyboardStatusDetector();
        key.registerActivity(MH04_FeedEditActivity.this);  //or register to an activity
        key.setVisibilityListener(new KeyboardStatusDetector.KeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean keyboardVisible) {
                if (keyboardVisible) {
                    //Do stuff for keyboard visible
//                    MyUtils.showToast(context, "show");
//                    if(fMenu.isShown()){
//                        fMenu.hideMenuButton(true);
                    scrollDown();
//                    }
                } else {
                    //Do stuff for keyboard hidden
//                    MyUtils.showToast(context, "hide");
//                    if(fMenu.isShown()==false){

//                    }

                }
            }
        });
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

}
