package com.workchat.core.plan;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Locale;

public class PlanModelLocal implements Parcelable {

    public static final String PLAN_MODEL = "PLAN_MODEL";
    private String title;
    private long timestamp;//giay
    private int duration;//phut
    private Place place;// { lat, lng, address } như gửi location
    private String note;

    //local
    private int year;
    private int month;
    private int day;
    private int hour;


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    private int minute;

    public PlanModelLocal(String title, long timestamp, int duration, Place place, String note){
        this.title = title;
        this.timestamp = timestamp;
        this.duration = duration;
        this.place = place;
        this.note = note;

        //create year,month,day,hour,minute
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timestamp*1000);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    public PlanModelLocal(String title, long timestamp, int duration, Place place, String note,
                          int year, int month, int day, int hour, int minute){
        this.title = title;
        this.timestamp = timestamp;
        this.duration = duration;
        this.place = place;
        this.note = note;

        this.year=year;
        this.month=month;
        this.day=day;
        this.hour=hour;
        this.minute=minute;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.timestamp);
        dest.writeInt(this.duration);
        dest.writeParcelable(this.place, flags);
        dest.writeString(this.note);
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.hour);
        dest.writeInt(this.minute);
    }

    protected PlanModelLocal(Parcel in) {
        this.title = in.readString();
        this.timestamp = in.readLong();
        this.duration = in.readInt();
        this.place = in.readParcelable(Place.class.getClassLoader());
        this.note = in.readString();
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.hour = in.readInt();
        this.minute = in.readInt();
    }

    public static final Creator<PlanModelLocal> CREATOR = new Creator<PlanModelLocal>() {
        @Override
        public PlanModelLocal createFromParcel(Parcel source) {
            return new PlanModelLocal(source);
        }

        @Override
        public PlanModelLocal[] newArray(int size) {
            return new PlanModelLocal[size];
        }
    };
}
