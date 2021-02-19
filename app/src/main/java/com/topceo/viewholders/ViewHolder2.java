package com.topceo.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.activity.MH08_SuggestActivity;
import com.topceo.adapter.SuggestAdapter;
import com.topceo.objects.other.User;
import com.topceo.utils.SimpleItemDecorator;

import java.util.ArrayList;

public class ViewHolder2 extends RecyclerView.ViewHolder {
    // each data item is just a string in this case

    public LinearLayout linearSuggestRoot;
    public TextView txtSeeMore;
    public LinearLayout linearSeeMore;
    public RecyclerView rvHorizontal;

    public SuggestAdapter suggestAdapter;

    public ViewHolder2(View v, Context context) {
        super(v);

        //
        txtSeeMore = (TextView) v.findViewById(R.id.txtSeeMore);
        linearSeeMore = (LinearLayout) v.findViewById(R.id.linearSeeMore);
        rvHorizontal = (RecyclerView) v.findViewById(R.id.rvHorizontal);
        linearSuggestRoot = (LinearLayout) v.findViewById(R.id.linearSuggestRoot);

        //suggestion
        int space = context.getResources().getDimensionPixelOffset(R.dimen.margin_10dp);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvHorizontal.setLayoutManager(mLayoutManager2);
        rvHorizontal.setHasFixedSize(true);
        rvHorizontal.addItemDecoration(new SimpleItemDecorator(space));

        //suggest
        suggestAdapter = new SuggestAdapter(new ArrayList<User>(), context);
        rvHorizontal.setAdapter(suggestAdapter);
        txtSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MH08_SuggestActivity.class);
                context.startActivity(intent);
            }
        });

    }
}
