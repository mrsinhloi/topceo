package com.topceo.shopping;

import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Parcelable {
    public static final String MEDIA_ID = "MEDIA_ID";
    public static final String MEDIA = "MEDIA";

    private long MediaId;//	int		Media có thể là 1 album hay single nhạc hoặc video, là 1 file PDF, file EPUB
    private String MediaGUID;//	string(50)		GUID để lưu path file
    private String MediaType;//	string(10)	PHOTO / AUDIO / VIDEO / PDF / EPUB	Dạng Media, kiểu string in hoa để dễ đọc thay vì lưu số
    private String Title;//	string(1000)		Tiêu đề
    private String Description;//	string(MAX)		Mô tả
    private String Link;// string(MAX)		Link tới file nội dung nếu có (Type: PDF, EPUB)
    private String Cover;//	string(MAX)		Link tới hình cover
    private String Thumbnail;//	string(MAX)		Link tới hình thumbnail cover
    private int SortId;//	int		Id dùng để sắp xếp
    private boolean IsPublish;//	bit		Đã phát hành
    private long PublishAfter;//	unix timestamp		Phát hành sau ngày, dù thuộc tính IsPublish là True nhưng trước ngày đang cấu hình thì vẫn ko hiện
    private boolean IsFree;//	bit		Nội dung miễn phí, không cần xem các thuộc tính bán khác
    private int Price;//	int		Giá bán
    private int VipPrice;//	int		Giá bán dành cho thành viên VIP
    private boolean EnableMobileCardPaid;//	bit		Cho phép bán qua thẻ cào
    private int MobileCardPrice;//	int		Giá bán qua thẻ cào
    private boolean EnableIOSInAppPaid;//	bit		Cho phép bán trên iOS In-app
    private String IOSInAppTier;//	string(50)		Mức giá iOS In-app
    private int IOSInAppPrice;//	int		Giá bán iOS In App
    private int IOSInAppProceeds;//	int		Doanh thu thực từ giá bán iOS In app
    private boolean VipNeedBuy;//	bit		Nội dung cần phải mua để truy cập, kể cả đã là thành viên VIP
    private int Version;//	int		Version biên tập để dưới app tải lại nếu cần
    private String Author;//	string(500)		Ca sĩ / Tác giả
    private String ReleaseYear;//	string(50)		Năm phát hành
    private long CreateDate;//	unix timestamp		Ngày tạo
    private long LastModifyDate;//	unix timestamp		Ngày cập nhật cuối
    private int LikeCount;//	int		Số lượng Like
    private int CommentCount;//	int		Số lượng Comment
    private boolean IsLiked;//	bit		Đã like media chưa
    private boolean IsLock;

    public boolean isCommentingOff() {
        return CommentingOff;
    }

    public void setCommentingOff(boolean commentingOff) {
        CommentingOff = commentingOff;
    }

    private boolean CommentingOff;//cau hinh cho phep comment tren media


    //local
    private int mediaTypeInt;

    public int getMediaTypeInt() {
        switch (getMediaType()) {
            case com.topceo.shopping.MediaType.AUDIO:
                return com.topceo.shopping.MediaType.AUDIO_INT;

            case com.topceo.shopping.MediaType.VIDEO:
                return com.topceo.shopping.MediaType.VIDEO_INT;

            case com.topceo.shopping.MediaType.PHOTO:
                return com.topceo.shopping.MediaType.PHOTO_INT;

            case com.topceo.shopping.MediaType.PDF:
                return com.topceo.shopping.MediaType.PDF_INT;

            case com.topceo.shopping.MediaType.EPUB:
                return com.topceo.shopping.MediaType.EPUB_INT;
            default:
                return com.topceo.shopping.MediaType.ALL_INT;

        }
    }

    public void setMediaTypeInt(int mediaTypeInt) {
        this.mediaTypeInt = mediaTypeInt;
    }

    public long getMediaId() {
        return MediaId;
    }

    public void setMediaId(long mediaId) {
        MediaId = mediaId;
    }

    public String getMediaGUID() {
        return MediaGUID;
    }

    public void setMediaGUID(String mediaGUID) {
        MediaGUID = mediaGUID;
    }

    public String getMediaType() {
        return MediaType;
    }

    public void setMediaType(String mediaType) {
        MediaType = mediaType;
    }

    public String getTitle() {
        if (Title == null) Title = "";
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getCover() {
        if(Cover == null) Cover = "";
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public int getSortId() {
        return SortId;
    }

    public void setSortId(int sortId) {
        SortId = sortId;
    }

    public boolean isPublish() {
        return IsPublish;
    }

    public void setPublish(boolean publish) {
        IsPublish = publish;
    }

    public long getPublishAfter() {
        return PublishAfter;
    }

    public void setPublishAfter(long publishAfter) {
        PublishAfter = publishAfter;
    }

    public boolean isFree() {
        return IsFree;
    }

    public void setFree(boolean free) {
        IsFree = free;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getVipPrice() {
        return VipPrice;
    }

    public void setVipPrice(int vipPrice) {
        VipPrice = vipPrice;
    }

    public boolean isEnableMobileCardPaid() {
        return EnableMobileCardPaid;
    }

    public void setEnableMobileCardPaid(boolean enableMobileCardPaid) {
        EnableMobileCardPaid = enableMobileCardPaid;
    }

    public int getMobileCardPrice() {
        return MobileCardPrice;
    }

    public void setMobileCardPrice(int mobileCardPrice) {
        MobileCardPrice = mobileCardPrice;
    }

    public boolean isEnableIOSInAppPaid() {
        return EnableIOSInAppPaid;
    }

    public void setEnableIOSInAppPaid(boolean enableIOSInAppPaid) {
        EnableIOSInAppPaid = enableIOSInAppPaid;
    }

    public String getIOSInAppTier() {
        return IOSInAppTier;
    }

    public void setIOSInAppTier(String IOSInAppTier) {
        this.IOSInAppTier = IOSInAppTier;
    }

    public int getIOSInAppPrice() {
        return IOSInAppPrice;
    }

    public void setIOSInAppPrice(int IOSInAppPrice) {
        this.IOSInAppPrice = IOSInAppPrice;
    }

    public int getIOSInAppProceeds() {
        return IOSInAppProceeds;
    }

    public void setIOSInAppProceeds(int IOSInAppProceeds) {
        this.IOSInAppProceeds = IOSInAppProceeds;
    }

    public boolean isVipNeedBuy() {
        return VipNeedBuy;
    }

    public void setVipNeedBuy(boolean vipNeedBuy) {
        VipNeedBuy = vipNeedBuy;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getReleaseYear() {
        return ReleaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        ReleaseYear = releaseYear;
    }

    public long getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(long createDate) {
        CreateDate = createDate;
    }

    public long getLastModifyDate() {
        return LastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        LastModifyDate = lastModifyDate;
    }

    public int getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(int likeCount) {
        LikeCount = likeCount;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int commentCount) {
        CommentCount = commentCount;
    }

    public boolean isLiked() {
        return IsLiked;
    }

    public void setLiked(boolean liked) {
        IsLiked = liked;
    }

    public Media() {
    }

    /**
     * Get all
     * @return
     */
    public boolean isLock() {
        return IsLock;
    }

    public void setLock(boolean lock) {
        IsLock = lock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.MediaId);
        dest.writeString(this.MediaGUID);
        dest.writeString(this.MediaType);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeString(this.Link);
        dest.writeString(this.Cover);
        dest.writeString(this.Thumbnail);
        dest.writeInt(this.SortId);
        dest.writeByte(this.IsPublish ? (byte) 1 : (byte) 0);
        dest.writeLong(this.PublishAfter);
        dest.writeByte(this.IsFree ? (byte) 1 : (byte) 0);
        dest.writeInt(this.Price);
        dest.writeInt(this.VipPrice);
        dest.writeByte(this.EnableMobileCardPaid ? (byte) 1 : (byte) 0);
        dest.writeInt(this.MobileCardPrice);
        dest.writeByte(this.EnableIOSInAppPaid ? (byte) 1 : (byte) 0);
        dest.writeString(this.IOSInAppTier);
        dest.writeInt(this.IOSInAppPrice);
        dest.writeInt(this.IOSInAppProceeds);
        dest.writeByte(this.VipNeedBuy ? (byte) 1 : (byte) 0);
        dest.writeInt(this.Version);
        dest.writeString(this.Author);
        dest.writeString(this.ReleaseYear);
        dest.writeLong(this.CreateDate);
        dest.writeLong(this.LastModifyDate);
        dest.writeInt(this.LikeCount);
        dest.writeInt(this.CommentCount);
        dest.writeByte(this.IsLiked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.IsLock ? (byte) 1 : (byte) 0);
        dest.writeByte(this.CommentingOff ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mediaTypeInt);
    }

    protected Media(Parcel in) {
        this.MediaId = in.readLong();
        this.MediaGUID = in.readString();
        this.MediaType = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
        this.Link = in.readString();
        this.Cover = in.readString();
        this.Thumbnail = in.readString();
        this.SortId = in.readInt();
        this.IsPublish = in.readByte() != 0;
        this.PublishAfter = in.readLong();
        this.IsFree = in.readByte() != 0;
        this.Price = in.readInt();
        this.VipPrice = in.readInt();
        this.EnableMobileCardPaid = in.readByte() != 0;
        this.MobileCardPrice = in.readInt();
        this.EnableIOSInAppPaid = in.readByte() != 0;
        this.IOSInAppTier = in.readString();
        this.IOSInAppPrice = in.readInt();
        this.IOSInAppProceeds = in.readInt();
        this.VipNeedBuy = in.readByte() != 0;
        this.Version = in.readInt();
        this.Author = in.readString();
        this.ReleaseYear = in.readString();
        this.CreateDate = in.readLong();
        this.LastModifyDate = in.readLong();
        this.LikeCount = in.readInt();
        this.CommentCount = in.readInt();
        this.IsLiked = in.readByte() != 0;
        this.IsLock = in.readByte() != 0;
        this.CommentingOff = in.readByte() != 0;
        this.mediaTypeInt = in.readInt();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
