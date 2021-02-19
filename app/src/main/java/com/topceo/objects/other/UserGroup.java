package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

public class UserGroup implements Parcelable {


    private long UserId;
    private long ItemId;
    private long HashtagId;
    private String Hashtag;
    private String Name;
    private String NameEN;
    private String ImageUrl;
    private int Order;
    private int MemberCount;
    private long CategoryId;
    private long CreateDate;



    //local
    private boolean isSelected;


    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public long getItemId() {
        return ItemId;
    }

    public void setItemId(long itemId) {
        ItemId = itemId;
    }

    public long getHashtagId() {
        return HashtagId;
    }

    public void setHashtagId(long hashtagId) {
        HashtagId = hashtagId;
    }

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Hashtag = hashtag;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNameEN() {
        return NameEN;
    }

    public void setNameEN(String nameEN) {
        NameEN = nameEN;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public int getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(int memberCount) {
        MemberCount = memberCount;
    }

    public long getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(long categoryId) {
        CategoryId = categoryId;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.UserId);
        dest.writeLong(this.ItemId);
        dest.writeLong(this.HashtagId);
        dest.writeString(this.Hashtag);
        dest.writeString(this.Name);
        dest.writeString(this.NameEN);
        dest.writeString(this.ImageUrl);
        dest.writeInt(this.Order);
        dest.writeInt(this.MemberCount);
        dest.writeLong(this.CategoryId);
        dest.writeLong(this.CreateDate);
    }

    public UserGroup() {
    }

    protected UserGroup(Parcel in) {
        this.UserId = in.readLong();
        this.ItemId = in.readLong();
        this.HashtagId = in.readLong();
        this.Hashtag = in.readString();
        this.Name = in.readString();
        this.NameEN = in.readString();
        this.ImageUrl = in.readString();
        this.Order = in.readInt();
        this.MemberCount = in.readInt();
        this.CategoryId = in.readLong();
        this.CreateDate = in.readLong();
    }

    public static final Parcelable.Creator<UserGroup> CREATOR = new Parcelable.Creator<UserGroup>() {
        @Override
        public UserGroup createFromParcel(Parcel source) {
            return new UserGroup(source);
        }

        @Override
        public UserGroup[] newArray(int size) {
            return new UserGroup[size];
        }
    };
}
