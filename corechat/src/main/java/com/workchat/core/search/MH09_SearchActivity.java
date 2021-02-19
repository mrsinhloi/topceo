package com.workchat.core.search;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.workchat.core.chat.RecentChatType;
import com.workchat.core.chat.RecentChat_Fragment;
import com.workchat.core.contacts.Contact_Fragment_1_Chat;
import com.workchat.core.contacts.Contact_Fragment_2_Online;
import com.workchat.core.contacts.Contact_Fragment_3_All;
import com.workchat.core.models.chat.RoomLog;
import com.workchat.core.models.realm.Room;
import com.workchat.core.models.realm.UserInfo;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Default search 3 tab full
 */

public class MH09_SearchActivity extends AppCompatActivity {
    public static final String SHOW_TAB_ALL = "SHOW_TAB_ALL";
    public static final String SHOW_TAB_CONTACT = "SHOW_TAB_CONTACT";
    public static final String SHOW_TAB_RECENT = "SHOW_TAB_RECENT";
    private boolean showTabAll = true;
    private boolean showTabContact = true;
    private boolean showTabRecent = true;
    private int numberTab = 0;


    private Activity context = this;

    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    @BindView(R2.id.appbarLayout)
    AppBarLayout appbarLayout;
    @BindView(R2.id.tabs)
    TabLayout tabs;
    @BindView(R2.id.viewPager)
    ViewPager viewPager;

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_green_500_24dp);
        toolbar.getNavigationIcon().setTint(ContextCompat.getColor(context, R.color.colorPrimaryChat));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    boolean isForward = false;
    boolean isSharing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_09_search);
        ButterKnife.bind(this);

        initToolbar();

        Bundle b = getIntent().getExtras();
        if (b != null) {

            //hien thi search
            showTabAll = b.getBoolean(SHOW_TAB_ALL, true);
            showTabContact = b.getBoolean(SHOW_TAB_CONTACT, true);
            showTabRecent = b.getBoolean(SHOW_TAB_RECENT, true);


            isSharing = b.getBoolean(RoomLog.IS_SHARING, false);
            isForward = b.getBoolean(RoomLog.IS_FORWARD, false);
            if (isForward) {
                txtSearch.setHint(R.string.forward_to);
            }
        }

        //setup tab and viewpager
        setupViewPager();
        initSearch();

        registerReceiver();
        txtSearch.requestFocus();

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Handler handler = new Handler();
                if (!hasFocus) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!txtSearch.hasFocus()) {
                                MyUtils.hideKeyboard(context);
                            }
                        }
                    }, 200);
                }
            }
        });

    }


    //TABS/////////////////////////////////////////////////////////////////////////////////////////////
    private ViewPagerAdapter adapter;
    private int positionSelected = 0;

    private void setupViewPager() {

        mFragmentList.clear();
        mFragmentTitleList.clear();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //tat ca
        if (showTabAll) {
            Search_Fragment_All f1 = Search_Fragment_All.newInstance(isForward, isSharing);
            f1.setCanLongClick(false);
            adapter.addFrag(f1, getString(R.string.all));
            numberTab += 1;
        }

        //danh ba
        if (showTabContact) {
            Contact_Fragment_3_All f2 = Contact_Fragment_3_All.newInstance(isForward, isSharing, true);
            f2.showHeader(false);
            f2.setCanSearch(true);
            adapter.addFrag(f2, getString(R.string.contacts));
            numberTab += 1;
        }

        //chat gan day
        if (showTabRecent) {
            RecentChat_Fragment f3 = RecentChat_Fragment.newInstance(RecentChatType.ALL, isForward, isSharing);
            f3.setCanSearch(true);
            f3.setCanLongClick(false);
            adapter.addFrag(f3, getString(R.string.chat_recent));
            numberTab += 1;
        }

        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        if (numberTab == 1) {
            tabs.setVisibility(View.GONE);
        }

        //mac dinh la chat gan day
        if (adapter.getCount() > 0) {
            viewPager.setCurrentItem(adapter.getCount() - 1);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                positionSelected = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        sendBroadcast(new Intent(Contact_Fragment_3_All.ACTION_GO_TO_TOP_CONTACT_TAB_1));
                        break;
                    case 1:
                        sendBroadcast(new Intent(Contact_Fragment_2_Online.ACTION_GO_TO_TOP_CONTACT_TAB_2));
                        break;
                    case 2:
                        sendBroadcast(new Intent(Contact_Fragment_1_Chat.ACTION_GO_TO_TOP_CONTACT_TAB_3));
                        break;
                }
            }
        });

        //search thong tin dau tien
//        search("");

    }

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return mFragmentTitleList.get(position);
        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.editText1)
    EditText txtSearch;
    @BindView(R2.id.imgClear)
    ImageView imgClear;


    private void initSearch() {
        imgClear.setVisibility(View.INVISIBLE);
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    txtSearch.clearFocus();
                    MyUtils.hideKeyboard(context, txtSearch);
                }

                return true;
            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.INVISIBLE);
                }

                if (!mTyping) {
                    mTyping = true;
                }
                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");
                MyUtils.hideKeyboard(context, txtSearch);
            }
        });


    }

    ////////////////////////////////////////////////////////////////////////////////////
    private static final int TYPING_TIMER_LENGTH = 500;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            //dang stop thi search
            String keyword = txtSearch.getText().toString().trim();
            search(keyword);

        }
    };

    private void search(String keyword) {
        //goi sang cac fragment con de search
        Intent intent = new Intent(Contact_Fragment_3_All.ACTION_SEARCH_ACCOUNT_LOCAL_1);
        intent.putExtra(UserInfo.KEY_SEARCH, keyword);
        context.sendBroadcast(intent);

        //search man chat gan day
        intent = new Intent(RecentChat_Fragment.ACTION_SEARCH_TEXT);
        intent.putExtra(RecentChat_Fragment.SEARCH_TEXT, keyword);
        intent.putExtra(Room.CHAT_TYPE, 0);
        context.sendBroadcast(intent);

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String SEARCH_NUMBER_FOUND = "SEARCH_NUMBER_FOUND";
    public static final String ACTION_GO_TO_POSITION_SEARCH = "ACTION_GO_TO_POSITION_SEARCH";
    public static final String ACTION_SET_TAB_TITLE_SEARCH = "ACTION_SET_TAB_TITLE_SEARCH";
    public static final String ACTION_FINISH = "ACTION_FINISH_MH09_SearchActivity";
    BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();

                switch (intent.getAction()) {
                    case ACTION_GO_TO_POSITION_SEARCH:
                        if (b != null) {
                            int position = b.getInt(ACTION_GO_TO_POSITION_SEARCH, 0);
                            if (position < viewPager.getChildCount()) {
                                viewPager.setCurrentItem(position, true);
                            }
                        }
                        break;
                    case ACTION_SET_TAB_TITLE_SEARCH:
                        if (b != null) {
                            int position = b.getInt(ACTION_GO_TO_POSITION_SEARCH, 0);
                            int number = b.getInt(SEARCH_NUMBER_FOUND, 0);
                            setTabTitle(position, number);
                        }
                        break;
                    case ACTION_FINISH:
                        finish();
                        break;
                }


            }
        };
        context.registerReceiver(receiver, new IntentFilter(ACTION_GO_TO_POSITION_SEARCH));
        context.registerReceiver(receiver, new IntentFilter(ACTION_SET_TAB_TITLE_SEARCH));
        context.registerReceiver(receiver, new IntentFilter(ACTION_FINISH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (context != null && receiver != null) {
            context.unregisterReceiver(receiver);
        }

        MyUtils.hideKeyboard(context);

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onStop() {
        super.onStop();
        MyUtils.hideKeyboard(context, txtSearch);
    }

    private void setTabTitle(int position, int numberItems) {
        if (tabs != null) {
            if (position < tabs.getTabCount() && adapter != null) {
                TabLayout.Tab tab = tabs.getTabAt(position);

                CharSequence title = adapter.getPageTitle(position);
                if (numberItems > 0) {
                    title = title + " (" + numberItems + ")";
                }
                tab.setText(title);
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*private boolean isFirst = true;
    @Override
    protected void onResume() {
        super.onResume();
        if(isFirst){
            isFirst = false;
        }

        if(!isFirst){
            MyUtils.hideKeyboard(context);
        }
    }*/
}
