package com.workchat.core.chat.locations;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class MyLocation implements Parcelable {


    public static final String MY_LOCATION = "MY_LOCATION";
    private String name;
    private String address;
    private double lat;
    private double lon;
    private String placeId;

    public boolean isCurrentLocation() {
        return isCurrentLocation;
    }

    public void setCurrentLocation(boolean currentLocation) {
        isCurrentLocation = currentLocation;
    }

    private boolean isCurrentLocation;


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        if (name == null) {
            name = "";
            if(!TextUtils.isEmpty(address)){
                name = address;
            }
        }

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        if (address == null) address = "";
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


    public MyLocation() {
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.placeId);
        dest.writeByte(this.isCurrentLocation ? (byte) 1 : (byte) 0);
    }

    protected MyLocation(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.placeId = in.readString();
        this.isCurrentLocation = in.readByte() != 0;
    }

    public static final Creator<MyLocation> CREATOR = new Creator<MyLocation>() {
        @Override
        public MyLocation createFromParcel(Parcel source) {
            return new MyLocation(source);
        }

        @Override
        public MyLocation[] newArray(int size) {
            return new MyLocation[size];
        }
    };
}
