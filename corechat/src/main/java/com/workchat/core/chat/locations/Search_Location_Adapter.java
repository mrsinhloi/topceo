

package com.workchat.core.chat.locations;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.workchat.corechat.R;
import com.workchat.corechat.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Search_Location_Adapter
        extends RecyclerView.Adapter<Search_Location_Adapter.MyViewHolder>{


    private static final String TAG = "PlaceAdapter";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    private ArrayList<MyLocation> mResultList = new ArrayList<>();



    private LatLngBounds mBounds;
    private Context context;



    public Search_Location_Adapter(Context context) {
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public MyLocation getItem(int position) {
        return mResultList.get(position);
    }

    public void setData(List<MyLocation> list, MyLocation currentLocation){
        positionSelected = 0;
        mResultList.clear();
        mResultList.addAll(list);
        if(currentLocation!=null){
            mResultList.add(0,currentLocation);
        }
        notifyDataSetChanged();
    }

    public void setData2(List<PlaceLikelihood> list, MyLocation currentLocation) {
        if(list!=null){
            List<MyLocation> locations = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                PlaceLikelihood item = list.get(i);
                Place place = item.getPlace();
                LatLng latLng = place.getLatLng();

                MyLocation l = new MyLocation();
                l.setName(place.getName());
                l.setAddress(place.getAddress());
                if(latLng!=null){
                    l.setLat(latLng.latitude);
                    l.setLon(latLng.longitude);
                }
                locations.add(l);
            }
            setData(locations, currentLocation);
        }
    }

    public void setData3(List<AutocompletePrediction> list, MyLocation currentLocation) {
        if(list!=null){
            List<MyLocation> locations = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                AutocompletePrediction item = list.get(i);

                MyLocation l = new MyLocation();
                l.setName(item.getPrimaryText(STYLE_BOLD).toString());
                l.setAddress(item.getSecondaryText(STYLE_BOLD).toString());
                l.setPlaceId(item.getPlaceId());

                locations.add(l);
            }
            setData(locations, currentLocation);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mh02_select_location_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MyLocation item = getItem(position);
//        holder.text1.setText(item.getPrimaryText(STYLE_BOLD));
//        holder.text2.setText(item.getSecondaryText(STYLE_BOLD));
        holder.text1.setText(item.getName());
        holder.text2.setText(item.getAddress());
        if(item.isCurrentLocation()){
            holder.img1.setImageResource(R.drawable.ic_my_location_red_a700_48dp);
            holder.text1.setTextColor(ContextCompat.getColor(context, R.color.red_A700));
        }else{
            holder.img1.setImageResource(R.drawable.ic_location_on_light_blue_a700_48dp);
            holder.text1.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        if(position==positionSelected){
            holder.relative1.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue_50));
        }else{
            holder.relative1.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

    }



    private int positionSelected = 0;//vi tri dau tien

    public void updateItem(MyLocation location) {
        if(location!=null){
            int position = -1;
            for (int i = 0; i < mResultList.size(); i++) {
                MyLocation item = mResultList.get(i);
                if(item.getPlaceId()!=null && item.getPlaceId().equalsIgnoreCase(location.getPlaceId())){
                    //neu chua co thong tin toa do thi moi set lai
                    if(item.getLat()==0){
                        position = i;
                    }
                }
            }
            if(position>=0){
                mResultList.set(position, location);
                //ko can update giao dien
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R2.id.relative1)
        RelativeLayout relative1;
        @BindView(R2.id.img1)
        ImageView img1;
        @BindView(R2.id.text1)
        TextView text1;
        @BindView(R2.id.text2)
        TextView text2;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //sau do moi set vi tri chon moi
            positionSelected = getAdapterPosition();
            notifyDataSetChanged();
            if(itemClickListener!=null)itemClickListener.onItemClick(v, positionSelected);
        }
    }

    private ItemClickListener itemClickListener;
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }


    public MyLocation getLocationSelected(){
        return mResultList.get(positionSelected);
    }





}
