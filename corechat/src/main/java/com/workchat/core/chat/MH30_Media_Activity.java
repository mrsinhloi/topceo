package com.workchat.core.chat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.workchat.core.models.realm.Room;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Phuong Pham on 7/31/2015.
 */
public class MH30_Media_Activity extends AppCompatActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Activity context = this;
    private String chatRoomId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mh30_media_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_green_500_24dp);
        toolbar.getNavigationIcon().setTint(ContextCompat.getColor(context, R.color.colorPrimaryChat));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        if(b!=null){
            chatRoomId = b.getString(Room.ROOM_ID, "");
        }

        initTab();

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R2.id.viewPager)
    ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView tvTitle;
    public static int currentTab;

    private void initTab(){
        setViewPager();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // On third tab we have pass intent to call SecondActivity.
                currentTab = tab.getPosition();
                /*Log.e("tab ","onTabSelected ===>"+tab.getPosition());

                if(tab.getPosition()==2) {
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                }*/
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.e("tab ","onTabUnselected ===>"+tab.getPosition());
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.e("tab ","onTabReselected ===>"+tab.getPosition());
            }
        });


    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void setViewPager() {
        // set Adaapter.
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(MH28_File_Send_In_Chat.newInstance(chatRoomId), getString(R.string.files));
        adapter.addFrag(MH29_Link_Send_In_Chat.newInstance(chatRoomId), getString(R.string.links));

        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //che tab chatnhanh lai
//        tabLayout.setVisibility(View.GONE);

        //set custom tab
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
        }

//        setTabIcon();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
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


        public View getTabView(int position) {
            View tab = LayoutInflater.from(context).inflate(R.layout.tab_item_text, null);

            TextView txt1 = (TextView) tab.findViewById(R.id.txt1);
            txt1.setText(mFragmentTitleList.get(position));

            //mac dinh vao la gone
            TextView tabText = (TextView) tab.findViewById(R.id.txt2);
            tabText.setVisibility(View.GONE);

            return tab;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
