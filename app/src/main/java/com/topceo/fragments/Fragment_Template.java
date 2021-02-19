package com.topceo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.topceo.R;
import com.topceo.selections.hashtags.HashtagCategory;

import butterknife.ButterKnife;

public class Fragment_Template extends Fragment {


    public static Fragment_Template newInstance(HashtagCategory category){
        Fragment_Template f = new Fragment_Template();
        Bundle b = new Bundle();
        b.putParcelable(HashtagCategory.HASH_TAG_CATEGORY, category);
        f.setArguments(b);
        return f;
    }
    HashtagCategory category;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            category = getArguments().getParcelable(HashtagCategory.HASH_TAG_CATEGORY);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, v);

        return v;
    }
}
