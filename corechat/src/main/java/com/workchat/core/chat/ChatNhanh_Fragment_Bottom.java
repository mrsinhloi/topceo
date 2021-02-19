package com.workchat.core.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.workchat.core.channel.MH03_ChannelActivity;
import com.workchat.core.config.ChatApplication;
import com.workchat.core.models.realm.Room;
import com.workchat.core.search.MH09_SearchActivity;
import com.workchat.core.utils.MyUtils;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatNhanh_Fragment_Bottom extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TITLE = "title_fragment";

    private String fragment_title;


    public ChatNhanh_Fragment_Bottom() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @return A new instance of fragment Temp_ParentFragment.
     */
    public static ChatNhanh_Fragment_Bottom newInstance(String title) {
        ChatNhanh_Fragment_Bottom fragment = new ChatNhanh_Fragment_Bottom();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        if (getArguments() != null) {
            fragment_title = getArguments().getString(ARG_TITLE);
        }
    }

    private Context context;

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.appbarLayout)
    AppBarLayout appbarLayout;
    @BindView(R2.id.contactNavFrag_tabs)
    TabLayout tabs;
    @BindView(R2.id.contactNavFrag_viewPager)
    ViewPager viewPager;

    private void initToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        activity.getSupportActionBar().setTitle("");
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_nav, container, false);
        ButterKnife.bind(this, v);
        initToolbar();
        initMenu();

        ////////////////////////////////////////////////////////////////////////////////////

//        user = (UserMBN) db.getObject(UserMBN.USER_MODEL, UserMBN.class);
        //setup tab and viewpager
        setupViewPager();
        registerReceiver();
        initSearch();



        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    //TABS/////////////////////////////////////////////////////////////////////////////////////////////
    ViewPagerAdapter adapter;
    private int positionSelected = 0;
    private boolean isForward = false;

    private void setupViewPager() {

        mFragmentList.clear();
        mFragmentTitleList.clear();

        adapter = new ViewPagerAdapter(getChildFragmentManager());

        RecentChat_Fragment f1 = RecentChat_Fragment.newInstance(RecentChatType.ALL, isForward, false);
        f1.setCanSearch(false);
        adapter.addFrag(f1, getString(R.string.chat_recent));

        RecentChat_Fragment f2 = RecentChat_Fragment.newInstance(RecentChatType.GROUP, isForward, false);
        f2.setCanSearch(false);
        adapter.addFrag(f2, getString(R.string.chat_recent_room));

        RecentChat_Fragment f3 = RecentChat_Fragment.newInstance(RecentChatType.CHANNEL, isForward, false);
        f3.setCanSearch(false);
        adapter.addFrag(f3, getString(R.string.chat_recent_channel));

        viewPager.setOffscreenPageLimit(adapter.getCount());//
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);



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
                //switch (tab.getPosition()){
                //vi tri tuong ung voi constant
                gotoTopOfFragment(tab.getPosition());
            }
        });
    }

    private void gotoTopOfFragment(int position) {
        Intent intent = new Intent(RecentChat_Fragment.ACTION_GO_TO_TOP_CHAT);
        intent.putExtra(Room.CHAT_TYPE, position);
        getContext().sendBroadcast(intent);
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
    public static final String ACTION_SEARCH_TEXT_FOCUS = "ACTION_SEARCH_TEXT_FOCUS";
    public static final String ACTION_SEARCH_TEXT_CLEAR_FOCUS = "ACTION_SEARCH_TEXT_CLEAR_FOCUS";
    public static final String ACTION_EXPAND_TOOLBAR = "ACTION_EXPAND_TOOLBAR";
    public static final String ACTION_SHOW_AS_FIRST_TAB_CHAT = "ACTION_SHOW_AS_FIRST_TAB_CHAT";
    private BroadcastReceiver receiver;

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_SEARCH_TEXT_FOCUS)) {
//                    searchView.setQuery("", false);
                    txtSearch.requestFocus();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyUtils.showKeyboard(getActivity());
                        }
                    }, 10);
                } else if (intent.getAction().equals(ACTION_SEARCH_TEXT_CLEAR_FOCUS)) {
                    txtSearch.clearFocus();
                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyUtils.showKeyboard(MH02_Recent_Chat_Activity_New.this);
                        }
                    }, 1000);*/
                } else if (intent.getAction().equals(ACTION_EXPAND_TOOLBAR)) {
                    appbarLayout.setExpanded(true);
                } else if (intent.getAction().equals(ACTION_SHOW_AS_FIRST_TAB_CHAT)) {
                    //chuyen ve tab 0
                    viewPager.setCurrentItem(RecentChatType.ALL);
                    //tab 0 scroll ve dau
                    gotoTopOfFragment(RecentChatType.ALL);
                }


            }
        };
        context.registerReceiver(receiver, new IntentFilter(ACTION_SEARCH_TEXT_FOCUS));
        context.registerReceiver(receiver, new IntentFilter(ACTION_SEARCH_TEXT_CLEAR_FOCUS));
        context.registerReceiver(receiver, new IntentFilter(ACTION_EXPAND_TOOLBAR));
        context.registerReceiver(receiver, new IntentFilter(ACTION_SHOW_AS_FIRST_TAB_CHAT));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {

        txtSearch.clearFocus();
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }

        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.editText1)
    EditText txtSearch;
    @BindView(R2.id.imgClear)
    ImageView imgClear;

    private void initSearch() {
        //click vao thi mo man hinh search
        txtSearch.clearFocus();
        txtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    txtSearch.clearFocus();
                    startActivity(new Intent(getContext(), MH09_SearchActivity.class));
                    return true;
                }
                return false;
            }
        });


        imgClear.setVisibility(View.INVISIBLE);
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    txtSearch.clearFocus();
                    MyUtils.hideKeyboard(getActivity());
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
                MyUtils.hideKeyboard(getActivity());
            }
        });

    }

    private void search(String query) {
//        positionSelected tướng với các type RecentChatType.ALL
        Intent intent = new Intent(RecentChat_Fragment.ACTION_SEARCH_TEXT);
        intent.putExtra(RecentChat_Fragment.SEARCH_TEXT, query);
        intent.putExtra(Room.CHAT_TYPE, positionSelected);
        context.sendBroadcast(intent);
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

    ////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.imgMenu)
    ImageView imgMenu;

    private void initMenu() {
        imgMenu.setVisibility(View.VISIBLE);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, imgMenu);
                try {
                    Field[] fields = popupMenu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popupMenu);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.action_1) {//add group
                            Intent intent = new Intent(context, SearchUserChatFromRestApiActivity.class);
                            startActivity(intent);
                        } else if (itemId == R.id.action_2) {//add channel
                            Intent intent;
                            intent = new Intent(context, MH03_ChannelActivity.class);
                            startActivity(intent);
                        } else if (itemId == R.id.action_3) {//them lien he
                            ChatApplication.Companion.openAddContactActivity();
                        }

                        return true;
                    }


                });
                popupMenu.inflate(R.menu.menu_recent_chat_room);
                popupMenu.show();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
