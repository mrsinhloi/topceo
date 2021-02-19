package com.workchat.core.models.chat;

import android.os.Parcel;
import android.os.Parcelable;

//link	string
//voiceLength	int	tính bằng giây
//size	int	tính bằng byte
public class Voice implements Parcelable {
    private String link;
    private int voiceLength;
    private long size;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(int voiceLength) {
        this.voiceLength = voiceLength;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.link);
        dest.writeInt(this.voiceLength);
        dest.writeLong(this.size);
    }

    public Voice() {
    }

    protected Voice(Parcel in) {
        this.link = in.readString();
        this.voiceLength = in.readInt();
        this.size = in.readLong();
    }

    public static final Parcelable.Creator<Voice> CREATOR = new Parcelable.Creator<Voice>() {
        @Override
        public Voice createFromParcel(Parcel source) {
            return new Voice(source);
        }

        @Override
        public Voice[] newArray(int size) {
            return new Voice[size];
        }
    };
}
