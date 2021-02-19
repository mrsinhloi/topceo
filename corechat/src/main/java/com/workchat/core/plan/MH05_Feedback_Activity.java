package com.workchat.core.plan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.workchat.core.models.realm.Room;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MH05_Feedback_Activity extends AppCompatActivity {

    private Activity context = this;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.title)
    TextView title;


    private PlanModel plan;
    private Room room;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mh05_feedback);
        ButterKnife.bind(this);



        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_green_500_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initUI();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            plan = b.getParcelable(PlanModel.PLAN_MODEL);
            room =  b.getParcelable(Room.ROOM);
            if (plan != null && room != null) {
                ArrayList<Comment> list = plan.getComments();
                adapter = new MH05_Feedback_Adapter(plan.getResult(), room, context);
                rv.setAdapter(adapter);
                if(adapter.getItemCount()>1){
                    rv.scrollToPosition(adapter.getItemCount()-1);
                }
            }

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @BindView(R2.id.recyclerView)
    RecyclerView rv;
    private MH05_Feedback_Adapter adapter;

    private void initUI() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(mLayoutManager);
//        DividerItemDecoration itemDecor = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
//        rv.addItemDecoration(itemDecor);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////



}
