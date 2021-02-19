package com.topceo.selections.hashtags;

import android.os.Parcel;
import android.os.Parcelable;

public class HashtagCategory implements Parcelable {
    public static final String HASH_TAG_CATEGORY = "HASH_TAG_CATEGORY";

    private long CategoryId;
    private String CategoryName;
    private String Title;
    private String TitleEN;
    private boolean IsHashtag;
    private int Order;
    private boolean CanSkip;
    private int MinPick;
    private int MaxPick;
    private boolean HasRelative;//": false,
    private boolean ForSuggest;//": false,
    private String Subtitle;//": "Hãy chọn dưới đây",
    private String SubtitleEN;//": "Hãy chọn dưới đây"

    public long getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(long categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getTitle() {
        if (Title == null) Title = "";
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitleEN() {
        if (TitleEN == null) TitleEN = "";
        return TitleEN;
    }

    public void setTitleEN(String titleEN) {
        TitleEN = titleEN;
    }

    public boolean isHashtag() {
        return IsHashtag;
    }

    public void setHashtag(boolean hashtag) {
        IsHashtag = hashtag;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public boolean isCanSkip() {
        return CanSkip;
    }

    public void setCanSkip(boolean canSkip) {
        CanSkip = canSkip;
    }

    public int getMinPick() {
        return MinPick;
    }

    public void setMinPick(int minPick) {
        MinPick = minPick;
    }

    public int getMaxPick() {
        return MaxPick;
    }

    public void setMaxPick(int maxPick) {
        MaxPick = maxPick;
    }


    public HashtagCategory() {
    }

    public boolean isHasRelative() {
        return HasRelative;
    }

    public void setHasRelative(boolean hasRelative) {
        HasRelative = hasRelative;
    }

    public boolean isForSuggest() {
        return ForSuggest;
    }

    public void setForSuggest(boolean forSuggest) {
        ForSuggest = forSuggest;
    }

    public String getSubtitle() {
        if (Subtitle == null) Subtitle = "";
        return Subtitle;
    }

    public void setSubtitle(String subtitle) {
        Subtitle = subtitle;
    }

    public String getSubtitleEN() {
        if (Subtitle == null) Subtitle = "";
        return SubtitleEN;
    }

    public void setSubtitleEN(String subtitleEN) {
        SubtitleEN = subtitleEN;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.CategoryId);
        dest.writeString(this.CategoryName);
        dest.writeString(this.Title);
        dest.writeString(this.TitleEN);
        dest.writeByte(this.IsHashtag ? (byte) 1 : (byte) 0);
        dest.writeInt(this.Order);
        dest.writeByte(this.CanSkip ? (byte) 1 : (byte) 0);
        dest.writeInt(this.MinPick);
        dest.writeInt(this.MaxPick);
        dest.writeByte(this.HasRelative ? (byte) 1 : (byte) 0);
        dest.writeByte(this.ForSuggest ? (byte) 1 : (byte) 0);
        dest.writeString(this.Subtitle);
        dest.writeString(this.SubtitleEN);
    }

    protected HashtagCategory(Parcel in) {
        this.CategoryId = in.readLong();
        this.CategoryName = in.readString();
        this.Title = in.readString();
        this.TitleEN = in.readString();
        this.IsHashtag = in.readByte() != 0;
        this.Order = in.readInt();
        this.CanSkip = in.readByte() != 0;
        this.MinPick = in.readInt();
        this.MaxPick = in.readInt();
        this.HasRelative = in.readByte() != 0;
        this.ForSuggest = in.readByte() != 0;
        this.Subtitle = in.readString();
        this.SubtitleEN = in.readString();
    }

    public static final Creator<HashtagCategory> CREATOR = new Creator<HashtagCategory>() {
        @Override
        public HashtagCategory createFromParcel(Parcel source) {
            return new HashtagCategory(source);
        }

        @Override
        public HashtagCategory[] newArray(int size) {
            return new HashtagCategory[size];
        }
    };
}
