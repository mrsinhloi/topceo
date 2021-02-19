package com.topceo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.topceo.R;
import com.topceo.db.TinyDB;
import com.topceo.objects.other.User;
import com.topceo.profile.Fragment_5_User_Profile_Grid;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//change to MH19_UserProfileActivity
@Deprecated
public class Fragment_4_User_Profile extends Fragment {

    public Fragment_4_User_Profile() {
        // Required empty public constructor
    }

    private TinyDB db;
    private User user;

    @BindView(R.id.imageView1)
    ImageView avatar;
    @BindView(R.id.textView1)
    TextView txt1;
    @BindView(R.id.textView2)
    TextView txt2;
    @BindView(R.id.textView3)
    TextView txt3;


    private int avatarSize = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_4, container, false);
        ButterKnife.bind(this, view);

        db = new TinyDB(getContext());
        Object obj = db.getObject(User.USER, User.class);
        if (obj != null) {
            user = (User) obj;
        }
        avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        //set user//////////////////////////////////////////////////////
        Glide.with(getContext())
                .load(user.getAvatarSmall())//"http://d2i37wz5q98nd1.cloudfront.net/wp-content/uploads/2015/08/VCCircle_Sundar_Pichai.png")
                .placeholder(R.drawable.ic_no_avatar)
                .override(avatarSize, avatarSize)
                .transform(new GlideCircleTransform(getContext()))
                .into(avatar);
        txt1.setText(user.getImageCount() + "");
        txt2.setText(user.getFollowerCount() + "");
        txt3.setText(user.getFollowingCount() + "");

        ////////////////////////////////////////////////////////////////
        setupTabs();


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TinyDB db=new TinyDB(getActivity());
                db.putBoolean(TinyDB.IS_LOGINED, false);



                getActivity().startActivity(new Intent(getActivity(), MH15_SigninActivity.class));
                getActivity().finish();
            }
        });*/
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private void setupTabs() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    private int[] tabIcons = {
            R.drawable.ic_apps_grey_500_48dp,
            R.drawable.ic_list_bulleted_grey_500_48dp
//            R.drawable.ic_place_white_48dp,
//            R.drawable.ic_label_white_48dp
    };

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    ViewPagerAdapter adapter;

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new Fragment_5_User_Profile_Grid(), "One");
        adapter.addFrag(new Fragment_2_Explorer(), "Two");
//        adapter.addFrag(new Fragment_2_Explorer(), "Three");
//        adapter.addFrag(new Fragment_2_Explorer(), "Four");
        viewPager.setAdapter(adapter);
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
            return null;
        }


    }
    ///////////////////////////////////////////////////////////////////////////////////////////////


}
