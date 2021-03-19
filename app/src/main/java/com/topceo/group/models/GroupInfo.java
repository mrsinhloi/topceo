package com.topceo.group.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupInfo implements Parcelable {
    public static final long GROUP_ADMIN = 1;
    public static final long GROUP_PERSONAL = -1;//KO TRUYEN GROUP ID PARAM

    public static final String GROUP_INFO = "GROUP_INFO";
    public static final String GROUP_ID = "GROUP_ID";
    public static final String IS_INVITE = "IS_INVITE";
    public static final String COVER_MIN_HEIGHT = "COVER_MIN_HEIGHT";

    private long GroupId;
    private String GroupGUID;
    private boolean IsPrivate;
    private boolean IsHide;
    private String GroupName;
    private String Description="";
    private String CoverUrl;
    private String CoverSmallUrl;
    private String GroupType;//string (default: General)
    private String Color;
    private String Location;
    private int TotalMember = 1; //chi tra trong /group/info
    private long LastPostDate;

    public static final int MEMBER_APPROVE_AUTO = 0;
    public static final int MEMBER_APPROVE_EVERYONE = 1;
    public static final int MEMBER_APPROVE_ADMIN_MOD = 2;
    /**
     * "Ai được phép duyệt member tham gia vào group
     * 0: tự động
     * 1: tất cả mọi người
     * 2: Admin / Mod"
     */
    private int MemberApprove;
    private boolean MemberCanPost;//ko quan tam
    private boolean PostNeedApprove;
    private long CreateDate;
    private long CreateUserId;

    private String StorageProvider;
    /**
     * "Đã join group hay chưa: Client dựa vào trạng thái tương ứng để show button và gọi API
     * 1: Đã join
     * 0: Chưa join
     * -1: đã gửi yêu cầu join nhưng chưa đc duyệt (Client hiển thị nút Cancel để huỷ yêu cầu tham gia tương ứng với client gọi API: /group/memberRequest/cancel)
     * -2: đã đc mời tham gia nhưng chưa đồng ý (Client hiển thị nút đồng ý hoặc từ chối tương ứng gọi API: /group/member/accept và /group/member/refuse)"
     */
    private int IsJoin;
    public static final int UN_JOINNED = 0;
    public static final int JOINNED = 1;
    public static final int REQUESTED_JOIN = -1;
    public static final int INVITED_AND_WAITING_ACCEPT = -2;




    private long JoinDate;
    private boolean ICanPost;//dua vao thuoc tinh nay de biet user co dc post bai trong group hay khong

    //bo sung local
    private boolean isHeader;

    protected GroupInfo(Parcel in) {
        GroupId = in.readLong();
        GroupGUID = in.readString();
        IsPrivate = in.readByte() != 0;
        IsHide = in.readByte() != 0;
        GroupName = in.readString();
        Description = in.readString();
        CoverUrl = in.readString();
        CoverSmallUrl = in.readString();
        GroupType = in.readString();
        Color = in.readString();
        Location = in.readString();
        TotalMember = in.readInt();
        LastPostDate = in.readLong();
        MemberApprove = in.readInt();
        MemberCanPost = in.readByte() != 0;
        PostNeedApprove = in.readByte() != 0;
        CreateDate = in.readLong();
        CreateUserId = in.readLong();
        StorageProvider = in.readString();
        IsJoin = in.readInt();
        JoinDate = in.readLong();
        ICanPost = in.readByte() != 0;
        isHeader = in.readByte() != 0;
        isAdmin = in.readByte() != 0;
        isMod = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(GroupId);
        dest.writeString(GroupGUID);
        dest.writeByte((byte) (IsPrivate ? 1 : 0));
        dest.writeByte((byte) (IsHide ? 1 : 0));
        dest.writeString(GroupName);
        dest.writeString(Description);
        dest.writeString(CoverUrl);
        dest.writeString(CoverSmallUrl);
        dest.writeString(GroupType);
        dest.writeString(Color);
        dest.writeString(Location);
        dest.writeInt(TotalMember);
        dest.writeLong(LastPostDate);
        dest.writeInt(MemberApprove);
        dest.writeByte((byte) (MemberCanPost ? 1 : 0));
        dest.writeByte((byte) (PostNeedApprove ? 1 : 0));
        dest.writeLong(CreateDate);
        dest.writeLong(CreateUserId);
        dest.writeString(StorageProvider);
        dest.writeInt(IsJoin);
        dest.writeLong(JoinDate);
        dest.writeByte((byte) (ICanPost ? 1 : 0));
        dest.writeByte((byte) (isHeader ? 1 : 0));
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeByte((byte) (isMod ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel in) {
            return new GroupInfo(in);
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    private boolean isAdmin;
    private boolean isMod;

    public boolean isMod() {
        return isMod;
    }

    public void setMod(boolean mod) {
        isMod = mod;
    }

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {
        GroupId = groupId;
    }

    public String getGroupGUID() {
        return GroupGUID;
    }

    public void setGroupGUID(String groupGUID) {
        GroupGUID = groupGUID;
    }

    public boolean isPrivate() {
        return IsPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        IsPrivate = aPrivate;
    }

    public boolean isHide() {
        return IsHide;
    }

    public void setHide(boolean hide) {
        IsHide = hide;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        CoverUrl = coverUrl;
    }

    public String getCoverSmallUrl() {
        return CoverSmallUrl;
    }

    public void setCoverSmallUrl(String coverSmallUrl) {
        CoverSmallUrl = coverSmallUrl;
    }

    public String getGroupType() {
        return GroupType;
    }

    public void setGroupType(String groupType) {
        GroupType = groupType;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getMemberApprove() {
        return MemberApprove;
    }

    public void setMemberApprove(int memberApprove) {
        MemberApprove = memberApprove;
    }

    public boolean isMemberCanPost() {
        return MemberCanPost;
    }

    public void setMemberCanPost(boolean memberCanPost) {
        MemberCanPost = memberCanPost;
    }

    public boolean isPostNeedApprove() {
        return PostNeedApprove;
    }

    public void setPostNeedApprove(boolean postNeedApprove) {
        PostNeedApprove = postNeedApprove;
    }



    public long getCreateUserId() {
        return CreateUserId;
    }

    public void setCreateUserId(long createUserId) {
        CreateUserId = createUserId;
    }

    public String getStorageProvider() {
        return StorageProvider;
    }

    public void setStorageProvider(String storageProvider) {
        StorageProvider = storageProvider;
    }



    public GroupInfo() {
    }

    public boolean isICanPost() {
        return ICanPost;
    }

    public void setICanPost(boolean ICanPost) {
        this.ICanPost = ICanPost;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public long getJoinDate() {
        return JoinDate;
    }

    public void setJoinDate(long joinDate) {
        JoinDate = joinDate;
    }

    public int getTotalMember() {
        return TotalMember;
    }

    public void setTotalMember(int totalMember) {
        TotalMember = totalMember;
    }

    public long getLastPostDate() {
        return LastPostDate;
    }

    public void setLastPostDate(long lastPostDate) {
        LastPostDate = lastPostDate;
    }

    public int isJoin() {
        return IsJoin;
    }

    public void setJoin(int join) {
        IsJoin = join;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

}