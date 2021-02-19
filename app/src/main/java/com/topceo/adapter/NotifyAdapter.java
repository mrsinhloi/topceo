package com.topceo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.objects.other.NotifySetting;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrPhuong on 2016-08-05.
 */
public class NotifyAdapter extends
        RecyclerView.Adapter<NotifyAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private ArrayList<NotifySetting> data = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    private boolean isModified = false;

    // Pass in the contact array into the constructor
    public NotifyAdapter(Context context, ArrayList<NotifySetting> addresses) {
        data = addresses;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.activity_setting_notify_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NotifySetting item = data.get(position);

        holder.cb.setOnCheckedChangeListener(null);
        holder.txt.setText(item.getNotifyString());
        holder.cb.setChecked(item.isChecked());
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                data.get(position).setChecked(isChecked);
                setModified(true);
            }
        });
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb.performClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<NotifySetting> getList() {
        return data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt1)TextView txt;
        @BindView(R.id.cb1)
        CheckBox cb;
        @BindView(R.id.relative1)
        RelativeLayout root;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
