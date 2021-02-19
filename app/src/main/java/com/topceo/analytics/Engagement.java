package com.topceo.analytics;

import android.os.Parcel;
import android.os.Parcelable;

public class Engagement implements Parcelable {

    public static final String TYPE_VIEW = "VIEW";
    public static final String TYPE_SHARED = "SHARED";


    private long PostId;
    private String EngageType;
    private long CreateDate;//second

    public long getPostId() {
        return PostId;
    }

    public void setPostId(long postId) {
        PostId = postId;
    }

    public String getEngageType() {
        return EngageType;
    }

    public void setEngageType(String engageType) {
        EngageType = engageType;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.PostId);
        dest.writeString(this.EngageType);
        dest.writeLong(this.CreateDate);
    }

    public Engagement() {
    }

    protected Engagement(Parcel in) {
        this.PostId = in.readLong();
        this.EngageType = in.readString();
        this.CreateDate = in.readLong();
    }

    public static final Parcelable.Creator<Engagement> CREATOR = new Parcelable.Creator<Engagement>() {
        @Override
        public Engagement createFromParcel(Parcel source) {
            return new Engagement(source);
        }

        @Override
        public Engagement[] newArray(int size) {
            return new Engagement[size];
        }
    };
}
