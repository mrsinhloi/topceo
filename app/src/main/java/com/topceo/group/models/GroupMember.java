package com.topceo.group.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.other.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GroupMember implements Parcelable {

    public static final String ROLE_ADMIN   = "ADMIN";
    public static final String ROLE_MOD     = "MOD";
    public static final String ROLE_MEMBER  = "MEMBER";


    long GroupId;
    long UserId;
    String RoleId;
    boolean IsAccept;
    long CreateUserId;
    long CreateDate;
    String UserName;
    String FullName;
    String AvatarSmall;

    //bo sung local
    boolean isHeader;
    boolean isInvited;

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {
        GroupId = groupId;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getRoleId() {
        return RoleId;
    }

    public void setRoleId(String roleId) {
        RoleId = roleId;
    }

    public boolean isAccept() {
        return IsAccept;
    }

    public void setAccept(boolean accept) {
        IsAccept = accept;
    }

    public long getCreateUserId() {
        return CreateUserId;
    }

    public void setCreateUserId(long createUserId) {
        CreateUserId = createUserId;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
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


    public GroupMember() {
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }
    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }

    @NotNull
    public static ArrayList<GroupMember> parseList(@NotNull ArrayList<User> users) {
        ArrayList<GroupMember> members = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User item = users.get(i);
            GroupMember member = new GroupMember();
            member.setAvatarSmall(item.getAvatarSmall());
            member.setUserId(item.getUserId());
            member.setUserName(item.getUserName());
            member.setFullName(item.getFullName());
            members.add(member);
        }

        return members;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.GroupId);
        dest.writeLong(this.UserId);
        dest.writeString(this.RoleId);
        dest.writeByte(this.IsAccept ? (byte) 1 : (byte) 0);
        dest.writeLong(this.CreateUserId);
        dest.writeLong(this.CreateDate);
        dest.writeString(this.UserName);
        dest.writeString(this.FullName);
        dest.writeString(this.AvatarSmall);
        dest.writeByte(this.isHeader ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isInvited ? (byte) 1 : (byte) 0);
    }

    protected GroupMember(Parcel in) {
        this.GroupId = in.readLong();
        this.UserId = in.readLong();
        this.RoleId = in.readString();
        this.IsAccept = in.readByte() != 0;
        this.CreateUserId = in.readLong();
        this.CreateDate = in.readLong();
        this.UserName = in.readString();
        this.FullName = in.readString();
        this.AvatarSmall = in.readString();
        this.isHeader = in.readByte() != 0;
        this.isInvited = in.readByte() != 0;
    }

    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel source) {
            return new GroupMember(source);
        }

        @Override
        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };
}
