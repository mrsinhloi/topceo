package com.topceo.adapter;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.topceo.R;
import com.topceo.activity.MH03_PostActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrPhuong on 2016-08-05.
 */
public class AddressAdapter  extends
        RecyclerView.Adapter<AddressAdapter.ViewHolder>  {

    // Store a member variable for the contacts
    private List<Address> mAddresses=new ArrayList<>();
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public AddressAdapter(Context context, List<Address> addresses) {
        mAddresses = addresses;
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
        View contactView = inflater.inflate(R.layout.activity_publish_image_rv_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Address add=mAddresses.get(position);
        StringBuilder strReturnedAddress = new StringBuilder();//context.getText(R.string.address) + ": "
        /*for (int i = 0; i < add.getMaxAddressLineIndex(); i++) {
            if (i == add.getMaxAddressLineIndex() - 1) {
                strReturnedAddress.append(add.getAddressLine(i));
            } else {
                strReturnedAddress.append(add.getAddressLine(i)).append(", ");
            }
        }*/
        strReturnedAddress.append(add.getAddressLine(0));

        /*if(add.getMaxAddressLineIndex()>0) {
            strReturnedAddress.append(add.getAddressLine(0));
            holder.txt.setVisibility(View.VISIBLE);
        }else{
            holder.txt.setVisibility(View.GONE);
        }*/

        holder.txt.setText(strReturnedAddress.toString());
        holder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder strReturnedAddress = new StringBuilder();//context.getText(R.string.address) + ": "
                /*for (int i = 0; i < add.getMaxAddressLineIndex(); i++) {
                    if (i == add.getMaxAddressLineIndex() - 1) {
                        strReturnedAddress.append(add.getAddressLine(i));
                    } else {
                        strReturnedAddress.append(add.getAddressLine(i)).append(", ");
                    }
                }*/
                strReturnedAddress.append(add.getAddressLine(0));
                MH03_PostActivity.setTextAddress(strReturnedAddress.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAddresses.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView txt;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder1 instance.
            super(itemView);

            txt = (TextView) itemView.findViewById(R.id.tagView);
        }
    }
}
