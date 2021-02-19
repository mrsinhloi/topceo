package com.workchat.core.plan;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Place implements Parcelable {
    public static final String PLACE_MODEL = "PLACE_MODEL";


    private String lat;
    private String lng;
    private String address;

    public Place(String lat, String lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
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

    public JSONObject getPlaceAsJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("lat", lat);
            json.put("lng", lng);
            json.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
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
    }

    protected Place(Parcel in) {
        this.lat = in.readString();
        this.lng = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
