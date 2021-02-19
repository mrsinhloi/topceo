package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by MrPhuong on 2017-08-29.
 */

public class Location implements Parcelable {

    private String lat;
    private String lng;
    private String address;
    private boolean checkin;

    public LatLng getLatLng() {
        LatLng latLng = null;
        try {
            latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location() {
    }

    public boolean isCheckin() {
        return checkin;
    }

    public void setCheckin(boolean checkin) {
        this.checkin = checkin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.address);
        dest.writeByte(this.checkin ? (byte) 1 : (byte) 0);
    }

    protected Location(Parcel in) {
        this.lat = in.readString();
        this.lng = in.readString();
        this.address = in.readString();
        this.checkin = in.readByte() != 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
