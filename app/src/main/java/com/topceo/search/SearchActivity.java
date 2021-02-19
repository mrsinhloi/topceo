package com.topceo.search;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.topceo.R;
import com.topceo.objects.other.SearchObject;
import com.topceo.utils.KeyboardUtils;
import com.topceo.utils.MyUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * http://joerichard.net/android/android-material-design-searchview-example/
 */
public class SearchActivity extends AppCompatActivity {
    private Activity context = this;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editText)
    EditText txtSearch;
    @BindView(R.id.imgClear)
    ImageView imgClear;
    @BindView(R.id.linearRoot)
    LinearLayout linearRoot;

    private boolean isKeyboardShow = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkChat));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sky);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_20);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ///
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSearch.setText("");
//                txtSearch.requestFocus();
//                MyUtils.showKeyboard(context);
            }
        });

        setupTabs();


        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString())) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.GONE);
                    MyUtils.showKeyboard(context, txtSearch);
                }

                if (!mTyping) {
                    mTyping = true;
                }
                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);

            }
        });
        /*txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = txtSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(keyword)) {
                        Intent intent = new Intent(Fragment_Explorer_Top.SEARCH_KEY_WORD);
                        intent.putExtra(SearchObject.KEY_WORD, keyword);
                        sendBroadcast(intent);
                    }
                }
                return true;
            }
        });*/

        KeyboardUtils.addKeyboardVisibilityListener(linearRoot, new KeyboardUtils.OnKeyboardVisibiltyListener() {
            @Override
            public void onVisibilityChange(boolean isVisible) {
                isKeyboardShow = isVisible;
            }
        });

        txtSearch.requestFocus();
//        MyUtils.showKeyboard(context);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtSearch, InputMethodManager.SHOW_IMPLICIT);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private void setupTabs() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    ViewPagerAdapter adapter;

    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Fragment_Explorer_Top(), getText(R.string.top).toString());
        adapter.addFrag(new Fragment_Explorer_People(), getText(R.string.people).toString());
        adapter.addFrag(new Fragment_Explorer_Tags(), getText(R.string.tags).toString());
//        adapter.notifyDataSetChanged();

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStop() {
        super.onStop();
        MyUtils.hideKeyboard(context);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////
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
            if (!TextUtils.isEmpty(keyword)) {
                Intent intent = new Intent(Fragment_Explorer_Top.SEARCH_KEY_WORD);
                intent.putExtra(SearchObject.KEY_WORD, keyword);
                sendBroadcast(intent);

//                MyUtils.hideKeyboard(context, txtSearch);
            }

        }
    };
}
