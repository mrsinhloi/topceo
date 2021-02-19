package com.topceo.gallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.topceo.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrPhuong on 2016-08-01.
 */
public class PickImageActivity extends AppCompatActivity {

    private Activity context=this;
    public static CustomViewPager viewPager;
    @BindView(R.id.tabs)TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pick_image);
        ButterKnife.bind(this);

        viewPager=(CustomViewPager)findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        //tabs
        setupTabs();
        registerReceiver();

        //init
//        initLocation();
    }

    public static final String ACTION_FINISH ="ACTION_FINISH_"+PickImageActivity.class.getSimpleName();
    private BroadcastReceiver receiver;
    private void registerReceiver(){
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equalsIgnoreCase(ACTION_FINISH)){
                    finish();
                }
            }
        };
        registerReceiver(receiver,new IntentFilter(ACTION_FINISH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null)unregisterReceiver(receiver);
    }

    //TABS/////////////////////////////////////////////////////////////////////////////////////////////
    private void setupTabs(){
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }


    ViewPagerAdapter adapter;
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new GalleryFragment(), "Gallery");
        adapter.addFrag(new com.desmond.squarecamera.CameraFragment(), "Camera");
        adapter.addFrag(new com.desmond.squarecamera.VideoFragment(), "Video");
        viewPager.setAdapter(adapter);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addFrag(new com.desmond.squarecamera.CameraFragment(), "Camera");
                adapter.notifyDataSetChanged();
            }
        }, 150);*/

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

        public void replaceFragmentCamera(Fragment fragment){
            mFragmentList.remove(1);
            mFragmentList.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return mFragmentTitleList.get(position);
        }


    }
    ////////////////////////////////////////////////////////////////////////////////////////////////




    //location////////////////////
    /*private double lat = 0, lon = 0;
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
//                showLocation();
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
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode== MH01_MainActivity.REQUEST_CAMERA){
                MyUtils.showToast(context, "REQUEST_CAMERA 2");
            }
        }
    }

    // override and call airLocation object's method by the same name
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/
}
