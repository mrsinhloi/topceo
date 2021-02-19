package com.topceo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.error.ANError;
import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.analytics.Engagement;
import com.topceo.config.MyApplication;
import com.topceo.fragments.Fragment_1_Home_User;
import com.topceo.fragments.Fragment_2_Explorer;
import com.topceo.hashtag.HashTagActivity;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.topceo.utils.ShareTo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MH06_FeedShareActivity extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imageView1)
    ImageView img1;
    @BindView(R.id.linearLayout1)
    LinearLayout linear1;

    @BindView(R.id.linearLayout2)
    LinearLayout linear2;
    @BindView(R.id.checkBox1)
    CheckBox cb1;
    @BindView(R.id.checkBox2)
    CheckBox cb2;
    @BindView(R.id.checkBox3)
    CheckBox cb3;
    @BindView(R.id.checkBox4)
    CheckBox cb4;
    @BindView(R.id.button1)
    AppCompatButton btnShare;
    @BindView(R.id.editText1)
    EditText txt;


    private ImageItem item;
    private int widthScreen = 0, heightScreen = 0, marginTop = 0;
    private boolean isOwner = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkChat));
        }
    }

    //    private TwitterAuthClient mTwitterAuthClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_share);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_svg_16_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        updateStatusBarColor();

        ///////////////////////////////////////////////////////////
//        mTwitterAuthClient= new TwitterAuthClient();
        callbackManager = CallbackManager.Factory.create();
        widthScreen = MyUtils.getScreenWidth(context);
        heightScreen = MyUtils.getScreenHeight(context);

        linear1.post(new Runnable() {
            @Override
            public void run() {
                //tinh layout margin top
                /*int toolbarHeight=toolbar.getHeight();
                int linearHeight=linear1.getHeight();
                marginTop=heightScreen-linearHeight-toolbarHeight;
                //dung marginTop lam chieu cao cua hinh
                img1.setLayoutParams(new FrameLayout.LayoutParams(widthScreen,marginTop));

                FrameLayout.LayoutParams layout=(FrameLayout.LayoutParams)linear1.getLayoutParams();
                layout.setMargins(0,marginTop,0,0);
                linear1.setLayoutParams(layout);*/

                Bundle b = getIntent().getExtras();
                if (b != null) {
                    item = b.getParcelable(ImageItem.IMAGE_ITEM);

                    if (item != null) {
                        Glide.with(context)
                                .load(item.getImageLarge())
//                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)//R.drawable.progress_animation
                                .centerCrop()
                                .override(widthScreen, widthScreen)
                                .into(img1);

                        //Nếu khg khong phai tin cua minh thi co thêm share ve profile của mình
                        if (isOwner = item.isOwner()) {
                            linear2.setVisibility(View.GONE);
                        }

                    }

                }
            }
        });

        //init listener checkbox
        listenerCheckbox();


    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {

            //share
//            MyUtils.share(context);
            MyUtils.share(context, this.item.getWebLink());

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private void listenerCheckbox() {

        //init facebook
        initFacebookPermissions();

        //chup hinh dep
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                }
            }
        });

        //facebook
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("email", "public_profile"));
                }
            }
        });


        //zalo
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                }
            }
        });

        //logout: Twitter.getSessionManager().clearActiveSession();
        //twitter
        cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
//                    loginTwitter();

                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //chup hinh dep, share len tuong
                if (cb1.isChecked()) {
                    shareToWall();
                }

                //facebook
                if (cb2.isChecked()) {
                    postToFacebook(item.getWebLink(), txt.getText().toString().trim());
                }

                //zalo
                if (cb3.isChecked()) {

                }

                //twitter
                if (cb4.isChecked()) {
//                    postTweet();

                }

                //thong ke
                MyApplication.getInstance().insertMap(item.getImageItemId(), Engagement.TYPE_SHARED);

            }
        });


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean isProcessing = false;

    private void shareToWall() {
        isProcessing = true;
        Webservices.ReshareImageItem(item.getImageItemId(), ShareTo.WALL, item.getShareCount())
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {

                        if (task.getError() == null) {//ko co exception
                            int shareCount = (int) task.getResult();
                            if (shareCount > 0) {
                                MyUtils.showToast(context, R.string.reshare_success);

                                item.setShareCount(shareCount);
                                //update home list
                                Intent intent = new Intent(Fragment_1_Home_User.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);


                                //update man hinh chi tiet MH02_PhotoDetailActivity
                                intent = new Intent(MH02_PhotoDetailActivity.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                //update grid user
                                intent = new Intent(Fragment_5_User_Profile_Grid.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                //update grid image by hashtag
                                intent = new Intent(HashTagActivity.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                //update grid explorer
                                intent = new Intent(Fragment_2_Explorer.ACTION_UPDATE_ITEM);
                                intent.putExtra(ImageItem.IMAGE_ITEM, item);
                                sendBroadcast(intent);

                                finish();
                            } else {
                                if (shareCount == -1) {
                                    //hinh da bi xoa
                                    //update ui cac man hinh home, profile
                                    MyUtils.afterDeletePost(context, item.getImageItemId());
                                    //null tuc la hinh da bi xoa, xoa trong list
                                    new MaterialDialog.Builder(context)
                                            .content(R.string.post_is_deleted)
                                            .positiveText(R.string.ok)
                                            .cancelable(false)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .show();
                                }
                            }
                        } else {//co exception
                            boolean isLostCookie = MyApplication.controlException((ANError) task.getError());
                            MyUtils.log("Fragment_1_Home_User - getNewFeed() - Exception = " + ((ANError) task.getError()).getErrorCode());

                            if (isLostCookie) {
                                MyApplication.initCookie(context).continueWith(new Continuation<Object, Void>() {
                                    @Override
                                    public Void then(Task<Object> task) throws Exception {
                                        if (task.getResult() != null) {
                                            User kq = (User) task.getResult();
                                            if (kq != null) {
                                                isProcessing = false;
                                                shareToWall();
                                            }
                                        }
                                        return null;
                                    }
                                });
                            } else {
                                if (!TextUtils.isEmpty(((ANError) task.getError()).getErrorDetail())) {
                                    MyUtils.showAlertDialog(context, ((ANError) task.getError()).getErrorDetail());
                                }
                            }
                        }

                        isProcessing = false;

                        return null;
                    }
                });


    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    CallbackManager callbackManager;

    private boolean isLogined() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private String facebookId = "";
    private String emailFacebook = "";
    /*@Override
    protected void onResume() {
        super.onResume();
        if(cb1.isChecked()){
            if (isLogined()) {
                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                MyUtils.log("user = " + object.toString());

                                try {
                                    if (!object.isNull("id")) {//http://graph.facebook.com/1048094951948232/picture?type=large
                                        String id = object.getString("id");
                                        facebookId = id;
                                        String avatar = "http://graph.facebook.com/" + id + "/picture?type=large";
                                        MyUtils.log("avatar=" + avatar);
                                    }
                                    if (!object.isNull("name")) {
                                        String name = object.getString("name");
//                                    txtFacebook.setText(String.format(getText(R.string.login_with_alias).toString(), name));
                                    }
                                    if (!object.isNull("email")) {
                                        String email = object.getString("email");
                                    }

                                    //request publish post
                                    if(isAllowPostFacebook==false){
                                        getPublishPermissions();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            } else {
//            txtFacebook.setText(R.string.sign_up_by_facebook);
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //twitter
//        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }


    public void initFacebookPermissions() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // User succesfully login with all permissions
                        // After this with these json and ParseUser , you can save your user to Parse
                        MyUtils.log(object.toString());
                        getPublishPermissions();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                MyUtils.log("facebook cancel publish_actions");
                cb2.setChecked(false);
                isAllowPostFacebook = false;
            }

            @Override
            public void onError(FacebookException facebookException) {
                MyUtils.log("facebook error publish_actions");
                cb2.setChecked(false);
                isAllowPostFacebook = false;
            }
        });

    }

    private boolean isAllowPostFacebook = false;

    public void getPublishPermissions() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // User succesfully login with all permissions
                        // After this with these json and ParseUser , you can save your user to Parse
                        MyUtils.log(object.toString());
                        isAllowPostFacebook = true;
                        MyUtils.showToast(context, R.string.facebook_ready);

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                MyUtils.log("facebook cancel publish_actions");
                cb2.setChecked(false);
                isAllowPostFacebook = false;
            }

            @Override
            public void onError(FacebookException facebookException) {
                MyUtils.log("facebook error publish_actions");
                cb2.setChecked(false);
                isAllowPostFacebook = false;
            }
        });

        LoginManager.getInstance().logInWithPublishPermissions(context, Arrays.asList("publish_actions"));
    }

    private void postToFacebook(String link, String description) {

        if (isLogined() && isAllowPostFacebook) {
            if (!TextUtils.isEmpty(link)) {

                final ProgressDialog dialog = ProgressDialog.show(context, "", "Posting...");
                Bundle bundle = new Bundle();
                bundle.putString("url", link);
                bundle.putString("message", description);
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "me/photos",
                        bundle,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                //{Response:  responseCode: 200, graphObject: {"id":"1048094951948232_1094758590615201"}, error: null}
                                MyUtils.log("user = " + response.toString());
//                                isAllowPostFacebook=false;
                                if (dialog != null) dialog.dismiss();
                                MyUtils.showToast(context, R.string.post_facebook_success);
                                finish();
                            }

                        }
                ).executeAsync();
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        //goi thong tin ve server
        MyApplication.getInstance().sendAnalyticToServer();
    }
}
