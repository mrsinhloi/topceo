package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;

public class UserMedium extends UserShort implements Parcelable {

    public static final String USER = "USER";
    /* PARENT
        private long UserId;//": 1,
        private String UserName;//": "sontungmtp",
        private String AvatarSmall;//": "https://cdn.ehubstar.com/avatar/baf0e3e1-3895-46a5-9039-811b2209c838/a10fd31c-77b1-47b0-b6ec-a8a5b6bbb2d3/s.jpg",
        private boolean IsVip;//": true
        private String FullName;
        */
    private String Email;
    private String AvatarMedium;
    private String AvatarOriginal;

    private int ImageCount = 0;
    private int FollowingCount = 0;
    private int FollowerCount = 0;
    private int VipLevelId = User.VIP_LEVEL_SKY;
    private String VipLevel;
    private String ChatUserId;

    private String Job;
    private String Company;
    private String Address;


    protected UserMedium(Parcel in) {
        super(in);
        Email = in.readString();
        AvatarMedium = in.readString();
        AvatarOriginal = in.readString();
        ImageCount = in.readInt();
        FollowingCount = in.readInt();
        FollowerCount = in.readInt();
        VipLevelId = in.readInt();
        VipLevel = in.readString();
        ChatUserId = in.readString();
        Job = in.readString();
        Company = in.readString();
        Address = in.readString();
        Favorite = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(Email);
        dest.writeString(AvatarMedium);
        dest.writeString(AvatarOriginal);
        dest.writeInt(ImageCount);
        dest.writeInt(FollowingCount);
        dest.writeInt(FollowerCount);
        dest.writeInt(VipLevelId);
        dest.writeString(VipLevel);
        dest.writeString(ChatUserId);
        dest.writeString(Job);
        dest.writeString(Company);
        dest.writeString(Address);
        dest.writeString(Favorite);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserMedium> CREATOR = new Creator<UserMedium>() {
        @Override
        public UserMedium createFromParcel(Parcel in) {
            return new UserMedium(in);
        }

        @Override
        public UserMedium[] newArray(int size) {
            return new UserMedium[size];
        }
    };

    public String getFavorite() {
        return Favorite;
    }

    public void setFavorite(String favorite) {
        Favorite = favorite;
    }

    private String Favorite;

    public User getUser() {
        User user = new User();
        user.setUserId(getUserId());
        user.setUserName(getUserName());
        user.setAvatarSmall(getAvatarSmall());
        user.setVip(isVip());

        user.setEmail(getEmail());
        user.setFullName(getFullName());
        user.setAvatarMedium(getAvatarMedium());
        user.setAvatarOriginal(getAvatarOriginal());

        user.setImageCount(getImageCount());
        user.setFollowingCount(getFollowingCount());
        user.setFollowerCount(getFollowerCount());
        user.setVipLevelId(getVipLevelId());
        user.setVipLevel(getVipLevel());
        user.setGroupCount(getGroupCount());
        user.setChatUserId(getChatUserId());
        user.setFavorite(getFavorite());

        user.setJob(getJob());
        user.setCompany(getCompany());
        user.setAddress(getAddress());

        return user;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAvatarMedium() {
        return AvatarMedium;
    }

    public void setAvatarMedium(String avatarMedium) {
        AvatarMedium = avatarMedium;
    }

    public String getAvatarOriginal() {
        return AvatarOriginal;
    }

    public void setAvatarOriginal(String avatarOriginal) {
        AvatarOriginal = avatarOriginal;
    }

    public int getImageCount() {
        return ImageCount;
    }

    public void setImageCount(int imageCount) {
        ImageCount = imageCount;
    }

    public int getFollowingCount() {
        return FollowingCount;
    }

    public void setFollowingCount(int followingCount) {
        FollowingCount = followingCount;
    }

    public int getFollowerCount() {
        return FollowerCount;
    }

    public void setFollowerCount(int followerCount) {
        FollowerCount = followerCount;
    }

    public int getVipLevelId() {
        return VipLevelId;
    }

    public void setVipLevelId(int vipLevelId) {
        VipLevelId = vipLevelId;
    }

    public String getVipLevel() {
        return VipLevel;
    }

    public void setVipLevel(String vipLevel) {
        VipLevel = vipLevel;
    }


    public UserMedium() {
    }


    public String getChatUserId() {
        return ChatUserId;
    }

    public void setChatUserId(String chatUserId) {
        ChatUserId = chatUserId;
    }


    public String getJob() {
        return Job;
    }

    public void setJob(String job) {
        Job = job;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
