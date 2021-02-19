package com.topceo.group.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PendingGroup implements Parcelable {
    int GroupId;
    String GroupName;
    String CoverUrl;
    long CreateDate;
    long CreateUserId;

    String UserName;
    String AvatarSmall;
    String FullName;

    public int getGroupId() {
        return GroupId;
    }

    public void setGroupId(int groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        CoverUrl = coverUrl;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public long getCreateUserId() {
        return CreateUserId;
    }

    public void setCreateUserId(long createUserId) {
        CreateUserId = createUserId;
    }

    public PendingGroup() {
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getAvatarSmall() {
        return AvatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        AvatarSmall = avatarSmall;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.GroupId);
        dest.writeString(this.GroupName);
        dest.writeString(this.CoverUrl);
        dest.writeLong(this.CreateDate);
        dest.writeLong(this.CreateUserId);
        dest.writeString(this.UserName);
        dest.writeString(this.AvatarSmall);
        dest.writeString(this.FullName);
    }

    protected PendingGroup(Parcel in) {
        this.GroupId = in.readInt();
        this.GroupName = in.readString();
        this.CoverUrl = in.readString();
        this.CreateDate = in.readLong();
        this.CreateUserId = in.readLong();
        this.UserName = in.readString();
        this.AvatarSmall = in.readString();
        this.FullName = in.readString();
    }

    public static final Creator<PendingGroup> CREATOR = new Creator<PendingGroup>() {
        @Override
        public PendingGroup createFromParcel(Parcel source) {
            return new PendingGroup(source);
        }

        @Override
        public PendingGroup[] newArray(int size) {
            return new PendingGroup[size];
        }
    };
}
