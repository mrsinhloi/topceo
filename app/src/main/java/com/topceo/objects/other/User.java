package com.topceo.objects.other;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.topceo.objects.image.ImageItem;
import com.workchat.core.mbn.models.UserChatCore;

import java.util.ArrayList;

/**
 * https://github.com/mcharmas/android-parcelable-intellij-plugin
 * Created by MrPhuong on 2016-07-27.
 */
public class User implements Parcelable {

    public static final int ADMIN_ROLE_ID = 1;
    public static final String USER = "USER";
    public static final String USER_NAME = "USER_NAME";
    public static final java.lang.String USER_ID = "USER_ID";
    public static final String ARRAY_LIST = "ARRAY_LIST";
    public static final String PHONE = "PHONE";
    public static final String AUTHORIZATION_CODE = "AUTHORIZATION_CODE";
    public static final String IS_RESET_PASSWORD = "IS_RESET_PASSWORD";
    public static final String IS_SIGN_UP = "IS_SIGN_UP";
    public static final String PASSWORD = "PASSWORD";
    public static final String IS_OPEN_MY_STORE = "IS_OPEN_MY_STORE";
    public static final String IS_LOAD_FAVORITE = "IS_LOAD_FAVORITE";

    //Nếu đã gởi email xác thực thì bên dưới mỗi lần resume
    public static final String IS_SEND_VERIFY_EMAIL = "IS_SEND_VERIFY_EMAIL";
    public static final String IS_SHOW_ADMIN_PAGE = "IS_SHOW_ADMIN_PAGE";
    public static final String REFERAL_LINK = "REFERAL_LINK";

    private long UserId;
    private String ChatUserId;
    private String UserName;
    private String UserGUID;
    private String Email;

    private String Phone;
    private String FullName;

    private String AvatarOriginal;
    private String AvatarSmall;
    private String AvatarMedium;
    private int ImageCount=0;
    private int FollowingCount=0;
    private int FollowerCount=0;
    private boolean IsVip;
    private long VipStartDate;
    private long VipEndDate;
    private int GroupCount;


    public static final int VIP_LEVEL_SKY = -1;
    public static final int VIP_LEVEL_VIP = 0;
    public static final int VIP_LEVEL_VVIP = 1;

    /*VipLevelId tương ứng với VipLevel
    -1: SKY (ko vip)
    0: VIP
    1: VVIP*/
    private int VipLevelId = VIP_LEVEL_SKY;
    private String VipLevel;


    //Đã thực hiện chọn trên màn hình hashtag suggest của hệ thống
    private int Gender;
    private String Birthday;
    private String MaritalStatus;
    private boolean HashtagSuggested;
    private int RoleId;//admin = 1
    private boolean EmailVerified;//": null,
    private boolean PhoneVerified;//": null,
    private boolean CanPost;//
    private long ReleaseDate;
    private String ReleaseMessage;

    private String Job;
    private String Address;
    private String Favorite;
    private String Biography;
    private int CountryID;
    private String CountryName;
    private long CreateDate;
    private String CreateOS;
    private long LastLoginDate;
    private String LastLoginOS;
    private String Token;

    public String getCoreChatCustomToken() {
        return CoreChatCustomToken;
    }

    public void setCoreChatCustomToken(String coreChatCustomToken) {
        CoreChatCustomToken = coreChatCustomToken;
    }

    private String CoreChatCustomToken;

    public long getJoinDate() {
        return JoinDate;
    }

    public void setJoinDate(long joinDate) {
        JoinDate = joinDate;
    }

    //group
    private long JoinDate;

    //bo sung local
    private boolean isBanned;


    public String getMaritalStatus() {
        return MaritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        MaritalStatus = maritalStatus;
    }

    private ArrayList<ImageItem> ImageItems;
    private ArrayList<User> Followings;
    private ArrayList<User> Followers;

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    public String getUserGUID() {
        return UserGUID;
    }

    public void setUserGUID(String userGUID) {
        UserGUID = userGUID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getAvatarOriginal() {
        return AvatarOriginal;
    }

    public void setAvatarOriginal(String avatarOriginal) {
        AvatarOriginal = avatarOriginal;
    }

    public String getAvatarSmall() {
        return AvatarSmall;
    }

    public void setAvatarSmall(String avatarSmall) {
        AvatarSmall = avatarSmall;
    }

    public String getAvatarMedium() {
        return AvatarMedium;
    }

    public void setAvatarMedium(String avatarMedium) {
        AvatarMedium = avatarMedium;
    }

    public String getJob() {
        return Job;
    }

    public void setJob(String job) {
        Job = job;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getFavorite() {
        if(TextUtils.isEmpty(Favorite)){
            Favorite = "";
        }
        return Favorite;
    }

    public void setFavorite(String favorite) {
        Favorite = favorite;
    }

    public String getBiography() {
        return Biography;
    }

    public void setBiography(String biography) {
        Biography = biography;
    }

    public int getCountryID() {
        return CountryID;
    }

    public void setCountryID(int countryID) {
        CountryID = countryID;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public String getCreateOS() {
        return CreateOS;
    }

    public void setCreateOS(String createOS) {
        CreateOS = createOS;
    }

    public long getLastLoginDate() {
        return LastLoginDate;
    }

    public void setLastLoginDate(long lastLoginDate) {
        LastLoginDate = lastLoginDate;
    }

    public String getLastLoginOS() {
        return LastLoginOS;
    }

    public void setLastLoginOS(String lastLoginOS) {
        LastLoginOS = lastLoginOS;
    }

    public int getImageCount() {
//        if (ImageCount < 0) ImageCount = 0;
        return ImageCount;
    }

    public void setImageCount(int imageCount) {
        ImageCount = imageCount;
    }

    public int getFollowingCount() {
//        if (FollowingCount < 0) FollowingCount = 0;
        return FollowingCount;
    }

    public void setFollowingCount(int followingCount) {
        FollowingCount = followingCount;
    }

    public int getFollowerCount() {
//        if (FollowerCount < 0) FollowerCount = 0;
        return FollowerCount;
    }

    public void setFollowerCount(int followerCount) {
        FollowerCount = followerCount;
    }

    public ArrayList<ImageItem> getImageItems() {
        return ImageItems;
    }

    public void setImageItems(ArrayList<ImageItem> imageItems) {
        ImageItems = imageItems;
    }

    public ArrayList<User> getFollowings() {
        return Followings;
    }

    public void setFollowings(ArrayList<User> followings) {
        Followings = followings;
    }

    public ArrayList<User> getFollowers() {
        return Followers;
    }

    public void setFollowers(ArrayList<User> followers) {
        Followers = followers;
    }


    public User() {
    }

    public boolean isVip() {
        return IsVip;
    }

    public void setVip(boolean vip) {
        IsVip = vip;
    }

    public long getVipStartDate() {
        return VipStartDate;
    }

    public void setVipStartDate(long vipStartDate) {
        VipStartDate = vipStartDate;
    }

    public long getVipEndDate() {
        return VipEndDate;
    }

    public void setVipEndDate(long vipEndDate) {
        VipEndDate = vipEndDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.UserId);
        dest.writeString(this.UserName);
        dest.writeString(this.UserGUID);
        dest.writeString(this.AvatarOriginal);
        dest.writeString(this.AvatarSmall);
        dest.writeString(this.AvatarMedium);
        dest.writeString(this.ChatUserId);

        dest.writeString(this.VipLevel);
        dest.writeInt(this.RoleId);
        dest.writeInt(this.ImageCount);
        dest.writeInt(this.FollowingCount);
        dest.writeInt(this.FollowerCount);
        dest.writeInt(this.VipLevelId);
        dest.writeByte(this.IsVip ? (byte) 1 : (byte) 0);


        dest.writeString(this.Email);
        dest.writeString(this.Phone);
        dest.writeString(this.FullName);
        dest.writeInt(this.Gender);
        dest.writeString(this.Birthday);
        dest.writeInt(this.GroupCount);




        dest.writeString(this.Job);
        dest.writeString(this.Address);
        dest.writeString(this.Favorite);
        dest.writeString(this.Biography);
        dest.writeInt(this.CountryID);
        dest.writeString(this.CountryName);
        dest.writeLong(this.CreateDate);
        dest.writeString(this.CreateOS);
        dest.writeLong(this.LastLoginDate);
        dest.writeString(this.LastLoginOS);

        dest.writeByte(this.HashtagSuggested ? (byte) 1 : (byte) 0);
        dest.writeByte(this.EmailVerified ? (byte) 1 : (byte) 0);
        dest.writeByte(this.PhoneVerified ? (byte) 1 : (byte) 0);
        dest.writeByte(this.CanPost ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isBanned ? (byte) 1 : (byte) 0);


        dest.writeString(this.MaritalStatus);
        dest.writeString(this.Token);
        dest.writeString(this.CoreChatCustomToken);


        dest.writeLong(this.VipStartDate);
        dest.writeLong(this.VipEndDate);

        dest.writeList(this.ImageItems);
        dest.writeList(this.Followings);
        dest.writeList(this.Followers);

    }

    protected User(Parcel in) {
        this.UserId = in.readLong();
        this.UserName = in.readString();
        this.UserGUID = in.readString();
        this.AvatarOriginal = in.readString();
        this.AvatarSmall = in.readString();
        this.AvatarMedium = in.readString();
        this.ChatUserId = in.readString();

        this.VipLevel = in.readString();
        this.ImageCount = in.readInt();
        this.FollowingCount = in.readInt();
        this.FollowerCount = in.readInt();
        this.VipLevelId = in.readInt();
        this.IsVip = in.readByte() != 0;
        this.RoleId = in.readInt();

        this.Email = in.readString();
        this.Phone = in.readString();
        this.FullName = in.readString();
        this.Gender = in.readInt();
        this.Birthday = in.readString();
        this.GroupCount = in.readInt();



        this.Job = in.readString();
        this.Address = in.readString();
        this.Favorite = in.readString();
        this.Biography = in.readString();
        this.CountryID = in.readInt();
        this.CountryName = in.readString();
        this.CreateDate = in.readLong();
        this.CreateOS = in.readString();
        this.LastLoginDate = in.readLong();
        this.LastLoginOS = in.readString();

        this.HashtagSuggested = in.readByte() != 0;
        this.EmailVerified = in.readByte() != 0;
        this.PhoneVerified = in.readByte() != 0;
        this.CanPost = in.readByte() != 0;
        this.isBanned = in.readByte() != 0;


        this.MaritalStatus = in.readString();
        this.Token = in.readString();
        this.CoreChatCustomToken = in.readString();



        this.VipStartDate = in.readLong();
        this.VipEndDate = in.readLong();

        this.ImageItems = new ArrayList<>();
        in.readList(this.ImageItems, ImageItem.class.getClassLoader());
//        this.ImageItems = in.createTypedArrayList(ImageItem.CREATOR);

        this.Followings = new ArrayList<>();
        in.readList(this.Followings, User.class.getClassLoader());
//        this.Followings = in.createTypedArrayList(User.CREATOR);

        this.Followers = new ArrayList<>();
        in.readList(this.Followers, User.class.getClassLoader());
//        this.Followers = in.createTypedArrayList(User.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public boolean isHashtagSuggested() {
        return HashtagSuggested;
    }

    public void setHashtagSuggested(boolean hashtagSuggested) {
        HashtagSuggested = hashtagSuggested;
    }

    public int getRoleId() {
        return RoleId;
    }

    public void setRoleId(int roleId) {
        RoleId = roleId;
    }

    public boolean isEmailVerified() {
        return EmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        EmailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return PhoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        PhoneVerified = phoneVerified;
    }

    public boolean isCanPost() {
        return CanPost;
    }

    public void setCanPost(boolean canPost) {
        CanPost = canPost;
    }

    public long getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        ReleaseDate = releaseDate;
    }

    public String getReleaseMessage() {
        return ReleaseMessage;
    }

    public void setReleaseMessage(String releaseMessage) {
        ReleaseMessage = releaseMessage;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
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

    public UserShort getUserShort() {
        UserShort user = new UserShort();
        user.setVip(isVip());
        user.setAvatarSmall(getAvatarSmall());
        user.setUserName(getUserName());
        user.setUserId(getUserId());
        user.setGroupCount(getGroupCount());
        user.setChatUserId(getChatUserId());

        return user;
    }

    public UserMedium getUserMedium(){
        UserMedium user = new UserMedium();
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

        return user;
    }

    public UserChatCore getUserChatCore(){
        UserChatCore user = new UserChatCore();
        user.set_id(getChatUserId());
        user.setName(getUserName());
        user.setNameMBN(getFullName());
        user.setEmail(getEmail());
        user.setAvatar(getAvatarMedium());
//        data.setToken(getToken());

        user.setAvatar(getAvatarSmall());
        user.setPhone(getPhone());

        return user;
    }

    public int getGroupCount() {
        return GroupCount;
    }

    public void setGroupCount(int groupCount) {
        GroupCount = groupCount;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getChatUserId() {
        return ChatUserId;
    }

    public void setChatUserId(String chatUserId) {
        ChatUserId = chatUserId;
    }


}
