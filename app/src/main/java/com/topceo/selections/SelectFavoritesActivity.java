package com.topceo.selections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.topceo.R;
import com.topceo.activity.MH01_MainActivity;
import com.topceo.bottom_navigation.NoSwipePager;
import com.topceo.config.MyApplication;
import com.topceo.db.TinyDB;
import com.topceo.objects.db.ImageItemDB;
import com.topceo.objects.image.ImageItem;
import com.topceo.objects.other.User;
import com.topceo.selections.hashtags.HashtagCategory;
import com.topceo.services.ReturnResult;
import com.topceo.services.Webservices;
import com.topceo.utils.MyUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectFavoritesActivity extends AppCompatActivity {
    private Activity context = this;

    @BindView(R.id.noSwipePager)
    NoSwipePager viewPager;

    private User user;
    private TinyDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_favorites);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        db = new TinyDB(this);

        getHashtagCategory();
        registerReceiver();


        try {
            Object obj = db.getObject(User.USER, User.class);
            if (obj != null) {
                user = (User) obj;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //lay thong tin pages va tao cac man hinh dong theo so luong page
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getHashtagCategory() {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.getHashtagCategory(
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                Type collectionType = new TypeToken<ArrayList<HashtagCategory>>() {
                                }.getType();
                                ReturnResult result = Webservices.parseJson(data.toString(), collectionType, true);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                        ArrayList<HashtagCategory> list = (ArrayList<HashtagCategory>) result.getData();
                                        if (list != null && list.size() > 0) {
//                                        MyUtils.showAlertDialog(context, "size = "+list.size());
                                            buildFragments(list);
                                        }
                                    }
                                }
                            } else {
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            MyUtils.log("error");
                            finish();
                        }
                    });
        } else {
            MyUtils.showThongBao(context);
        }

    }

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private void buildFragments(ArrayList<HashtagCategory> list) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list != null && list.size() > 0) {

                    //load array
                    fragments.clear();

                    //Tab Home
                    for (int i = 0; i < list.size(); i++) {
                        HashtagCategory item = list.get(i);
                        if (item.isHashtag()) {//chon topics
                            fragments.add(Fragment_3_Select_Favorite.newInstance(item));
                        } else {//update user info
                            fragments.add(Fragment_4_Update_User_Info.newInstance(item));
                        }
                    }


                    //first lauch
                    viewPager.setPagingEnabled(false);
                    viewPager.setOffscreenPageLimit(fragments.size());
                    adapter = new MyFragmentAdapter(getSupportFragmentManager());
                    viewPager.setAdapter(adapter);


                }
            }
        });
    }


    private void switchFragment(int position) {
        if (adapter != null) {
            if (position >= 0 && position < adapter.getCount()) {
                viewPager.setCurrentItem(position, false);
            } else {
//                MyUtils.showAlertDialog(context, "The end");
                updateSuggestComplete();
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private MyFragmentAdapter adapter;

    private class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        /*@Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }*/
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void onNext() {
        if (adapter != null) {
            int currentPos = viewPager.getCurrentItem();
            currentPos++;
            switchFragment(currentPos);
        }
    }

    private void onPrevious() {
        if (adapter != null) {
            int currentPos = viewPager.getCurrentItem();
            if (currentPos > 0) {
                currentPos--;
                switchFragment(currentPos);
            }
        }
    }


    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";

    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_NEXT)) {
                    onNext();
                } else if (intent.getAction().equals(ACTION_PREVIOUS)) {
                    onPrevious();
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(ACTION_NEXT));
        registerReceiver(receiver, new IntentFilter(ACTION_PREVIOUS));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) realm.close();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }


    private int numberError = 0;

    private void updateSuggestComplete() {
        if (MyUtils.checkInternetConnection(context)) {
            MyApplication.apiManager.hashtagSelected(
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject data = response.body();
                            if (data != null) {
                                ReturnResult result = Webservices.parseJson(data.toString(), String.class, false);

                                if (result != null) {
                                    if (result.getErrorCode() == ReturnResult.SUCCESS) {//da ton tai thi vao
                                        gotoMain();
                                    } else {

                                        //nếu lỗi 3 lần thì cho qua
                                        numberError++;
                                        if (numberError == TinyDB.NUMBER_ERROR_MAX) {
                                            gotoMain();
                                        } else {
                                            String message = result.getErrorMessage();
                                            if (!TextUtils.isEmpty(message)) {
                                                MyUtils.showAlertDialog(context, message);
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            MyUtils.log("error");
                        }
                    });
        } else {
            MyUtils.showThongBao(context);
        }

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////

    private void getHomePageSonTungAndInsertDB() {
        Webservices.getNewsFeedPageFirst(true)
                .continueWith(new Continuation<Object, Void>() {
                    @Override
                    public Void then(Task<Object> task) throws Exception {
                        setResult(task);
                        return null;
                    }
                });
    }

    private void saveDate() {
        //lan dau lay thi cung ghi nhan lai
        db.putLong(TinyDB.LAST_SYN_DATE, System.currentTimeMillis());
    }

    private void setResult(Task<Object> task) {
        if (task.getError() == null) {//ko co exception
            ArrayList<ImageItem> list = (ArrayList<ImageItem>) task.getResult();
            if (list != null && list.size() > 0) {
                //lan dau lay thi cung ghi nhan lai
                saveDate();
                //neu la tab sontung thi luu lai
                insertListImage(list);

            }
        }
    }

    private Realm realm;

    private void insertListImage(ArrayList<ImageItem> list) {
        if (list != null && list.size() > 0) {
            ArrayList<ImageItemDB> list2 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                ImageItem item = list.get(i);
                ImageItemDB itemDB = item.copy();
                list2.add(itemDB);
            }

            if (list2.size() > 0) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insertOrUpdate(list2);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        MyUtils.showToastDebug(context, "save success list " + list.size());
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (user != null) {
            //neu chua chon so thich xong thi khi thoat khoi man hinh nay se thoat khoi man hinh main
            if (!user.isHashtagSuggested()) {
                sendBroadcast(new Intent(MH01_MainActivity.ACTION_FINISH));
                finish();
            }
        }
    }

    private void gotoMain() {
        setResult(RESULT_OK);
        finish();
    }
}
