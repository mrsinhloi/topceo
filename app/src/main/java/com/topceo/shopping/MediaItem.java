package com.topceo.shopping;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MediaItem implements Parcelable {
    public static final String ITEM_ID = "ITEM_ID";
    public static final String LIST = "LIST";
    public static final String LYRIC = "LYRIC";
    public static final String COVER = "COVER";
    public static final String POSITION = "POSITION";
    public static final String MEDIA_ITEM = "MEDIA_ITEM";
    public static final String MEDIA_POSITION = "MEDIA_POSITION";
    private long ItemId;//
    private String ItemGUID;//(50)		GUID để lưu path file
    private long MediaId;//		Reference tới Media
    private String ItemType;//(10)	VIDEO / AUDIO / PHOTO	dạng media
    private String Title;//(1000)		Tiêu đề
    private String Description;//(MAX)		Mô tả
    private int SortId;//		Id để sắp xếp
    private String Author;//(500)		Ca sĩ / Tác giả
    private String ReleaseYear;//(50)		Năm phát hành
    private String FileUrl;//(MAX)		Link đến file
    private long FileSizeInKb;//		Kích thước file tính bằng Kb
    private int Width;//		Chiều rộng
    private int Height;//		Chiều cao
    private String FileHDUrl;//(MAX)		Link đến file HD
    private long FileHDSizeInKb;//		Kích thước file HD tính bằng Kb
    private int HDWidth;//		Chiều rộng HD
    private int HDHeight;//		Chiều cao HD
    private long LengthInSecond;//		Độ dài media tính bằng giây
    private String CoverUrl;//(MAX)		Link đến hình Cover
    private int CoverWidth;//		Chiều rộng Cover
    private int CoverHeight;//		Chiều cao Cover
    private String ThumbnailUrl;//(MAX)		Link đến hình Thumbnail Cover
    private int ThumbWidth;//		Chiều rộng Thumb
    private int ThumbHeight;//		Chiều cao Thumb
    private String LyricOrSubtitle;//(MAX)		Lời bài hát / Phụ đề
    private long CreateDate;//	unix timestamp		Ngày tạo
    private long LastModifyDate;//	unix timestamp		Ngày cập nhật cuối
    private boolean IsPublish;//	bit		Đã phát hành
    private int Version;//		Version File

    public long getItemId() {
        return ItemId;
    }

    public void setItemId(long itemId) {
        ItemId = itemId;
    }

    public String getItemGUID() {
        return ItemGUID;
    }

    public void setItemGUID(String itemGUID) {
        ItemGUID = itemGUID;
    }

    public long getMediaId() {
        return MediaId;
    }

    public void setMediaId(long mediaId) {
        MediaId = mediaId;
    }

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public String getTitle() {
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

    public int getSortId() {
        return SortId;
    }

    public void setSortId(int sortId) {
        SortId = sortId;
    }

    public String getAuthor() {
        if (Author == null) Author = "";
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

    public String getFileUrl() {
        if(FileUrl==null)FileUrl = "";
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public long getFileSizeInKb() {
        return FileSizeInKb;
    }

    public void setFileSizeInKb(long fileSizeInKb) {
        FileSizeInKb = fileSizeInKb;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public String getFileHDUrl() {
        return FileHDUrl;
    }

    public void setFileHDUrl(String fileHDUrl) {
        FileHDUrl = fileHDUrl;
    }

    public long getFileHDSizeInKb() {
        return FileHDSizeInKb;
    }

    public void setFileHDSizeInKb(long fileHDSizeInKb) {
        FileHDSizeInKb = fileHDSizeInKb;
    }

    public int getHDWidth() {
        return HDWidth;
    }

    public void setHDWidth(int HDWidth) {
        this.HDWidth = HDWidth;
    }

    public int getHDHeight() {
        return HDHeight;
    }

    public void setHDHeight(int HDHeight) {
        this.HDHeight = HDHeight;
    }

    public long getLengthInSecond() {
        return LengthInSecond;
    }

    public void setLengthInSecond(long lengthInSecond) {
        LengthInSecond = lengthInSecond;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        CoverUrl = coverUrl;
    }

    public int getCoverWidth() {
        return CoverWidth;
    }

    public void setCoverWidth(int coverWidth) {
        CoverWidth = coverWidth;
    }

    public int getCoverHeight() {
        return CoverHeight;
    }

    public void setCoverHeight(int coverHeight) {
        CoverHeight = coverHeight;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public int getThumbWidth() {
        return ThumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        ThumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return ThumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        ThumbHeight = thumbHeight;
    }

    public String getLyricOrSubtitle() {
        return LyricOrSubtitle;
    }

    public void setLyricOrSubtitle(String lyricOrSubtitle) {
        LyricOrSubtitle = lyricOrSubtitle;
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

    public boolean isPublish() {
        return IsPublish;
    }

    public void setPublish(boolean publish) {
        IsPublish = publish;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ItemId);
        dest.writeString(this.ItemGUID);
        dest.writeLong(this.MediaId);
        dest.writeString(this.ItemType);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeInt(this.SortId);
        dest.writeString(this.Author);
        dest.writeString(this.ReleaseYear);
        dest.writeString(this.FileUrl);
        dest.writeLong(this.FileSizeInKb);
        dest.writeInt(this.Width);
        dest.writeInt(this.Height);
        dest.writeString(this.FileHDUrl);
        dest.writeLong(this.FileHDSizeInKb);
        dest.writeInt(this.HDWidth);
        dest.writeInt(this.HDHeight);
        dest.writeLong(this.LengthInSecond);
        dest.writeString(this.CoverUrl);
        dest.writeInt(this.CoverWidth);
        dest.writeInt(this.CoverHeight);
        dest.writeString(this.ThumbnailUrl);
        dest.writeInt(this.ThumbWidth);
        dest.writeInt(this.ThumbHeight);
        dest.writeString(this.LyricOrSubtitle);
        dest.writeLong(this.CreateDate);
        dest.writeLong(this.LastModifyDate);
        dest.writeByte(this.IsPublish ? (byte) 1 : (byte) 0);
        dest.writeInt(this.Version);
    }

    public MediaItem() {
    }

    protected MediaItem(Parcel in) {
        this.ItemId = in.readLong();
        this.ItemGUID = in.readString();
        this.MediaId = in.readLong();
        this.ItemType = in.readString();
        this.Title = in.readString();
        this.Description = in.readString();
        this.SortId = in.readInt();
        this.Author = in.readString();
        this.ReleaseYear = in.readString();
        this.FileUrl = in.readString();
        this.FileSizeInKb = in.readLong();
        this.Width = in.readInt();
        this.Height = in.readInt();
        this.FileHDUrl = in.readString();
        this.FileHDSizeInKb = in.readLong();
        this.HDWidth = in.readInt();
        this.HDHeight = in.readInt();
        this.LengthInSecond = in.readLong();
        this.CoverUrl = in.readString();
        this.CoverWidth = in.readInt();
        this.CoverHeight = in.readInt();
        this.ThumbnailUrl = in.readString();
        this.ThumbWidth = in.readInt();
        this.ThumbHeight = in.readInt();
        this.LyricOrSubtitle = in.readString();
        this.CreateDate = in.readLong();
        this.LastModifyDate = in.readLong();
        this.IsPublish = in.readByte() != 0;
        this.Version = in.readInt();
    }

    public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel source) {
            return new MediaItem(source);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    public static String[] getUrls(ArrayList<MediaItem> list){
        if(list!=null && list.size()>0){
            String[] arr = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i).getFileUrl();
            }
            return arr;
        }
        return null;
    }
}
