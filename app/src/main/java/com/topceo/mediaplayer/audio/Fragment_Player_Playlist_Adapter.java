package com.topceo.mediaplayer.audio;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.shopping.MediaItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MrPhuong on 2016-08-05.
 */
public class Fragment_Player_Playlist_Adapter extends
        RecyclerView.Adapter<Fragment_Player_Playlist_Adapter.ViewHolder> {

    // Store a member variable for the contacts
    private ArrayList<MediaItem> data = new ArrayList<>();
    // Store the context for easy access
    private Context mContext;

    private int positionSelected = 0;

    // Pass in the contact array into the constructor
    public Fragment_Player_Playlist_Adapter(Context context, ArrayList<MediaItem> addresses, int position) {
        data = addresses;
        mContext = context;
        positionSelected = position;
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
        View contactView = inflater.inflate(R.layout.media_fragment_playlist_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MediaItem item = data.get(position);
        holder.txt1.setText(item.getTitle());
        holder.txt2.setText(item.getAuthor());
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //khac bai thi moi play
                if (position != positionSelected) {
                    //set player
                    Intent intent = new Intent(MediaPlayerActivity.ACTION_SELECT_SONG);
                    intent.putExtra(MediaItem.ITEM_ID, item.getItemId());
                    getContext().sendBroadcast(intent);

                    positionSelected = position;
                    notifyDataSetChanged();
                }

            }
        });

        if (position == positionSelected) {
            holder.txt1.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));
        } else {
            holder.txt1.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setPosition(int position) {
        if (position < data.size()) {
            positionSelected = position;
            notifyDataSetChanged();
        }
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @BindView(R.id.txt1)
        TextView txt1;
        @BindView(R.id.txt2)
        TextView txt2;
        @BindView(R.id.imgBtn)
        ImageButton imgMenu;
        @BindView(R.id.linearRoot)
        LinearLayout linearRoot;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder1 instance.
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
