package com.topceo.objects.db;

import com.topceo.objects.image.ImageItem;
import com.topceo.objects.image.Item;
import com.topceo.objects.image.ItemData;
import com.topceo.objects.other.UserShort;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * https://github.com/mcharmas/android-parcelable-intellij-plugin
 * Created by MrPhuong on 2016-07-27.
 */
public class ImageItemDB extends RealmObject {

    //
    @PrimaryKey
    private long ImageItemId;
    private String ItemGUID;
    private String Description;
    private long CreateDate;//second
    private String CreateOS;

    private long LastModifyDate;
    private String LastModifyOS;
    private String Location;
    private double Lat;
    private double Long;

    private boolean IsPrivate;
    private int LikeCount;
    private int CommentCount;
    private int ShareCount;
    private boolean IsLiked;
    private boolean IsShared;
    private boolean IsOwner;

    private String WebLink;
    private boolean IsSaved;
    private long SavedDate;
    private RealmList<ItemDB> ItemContent;
    private UserShortDB Owner;
    private String ItemType = ImageItem.ITEM_TYPE_INSTAGRAM;//INSTA / FB
    private ItemData ItemData;

    //bo sung comment preview


    public ImageItem copy() {
        ImageItem item = new ImageItem(ImageItemId,
                ItemGUID,
                Description,
                CreateDate,//second
                CreateOS,
                LastModifyDate,
                LastModifyOS,
                Location,
                Lat,
                Long,
                IsPrivate,
                LikeCount,
                CommentCount,
                ShareCount,
                IsLiked,
                IsShared,
                IsOwner,
                WebLink,
                IsSaved,
                SavedDate,
                ItemContent,
                Owner,
                ItemType,
                ItemData
                );
        return item;
    }

    public ImageItemDB() {
    }

    public ImageItemDB(long ImageItemId,
                       String ItemGUID,
                       String Description,
                       long CreateDate,//second
                       String CreateOS,
                       long LastModifyDate,
                       String LastModifyOS,
                       String Location,
                       double Lat,
                       double Long,
                       boolean IsPrivate,
                       int LikeCount,
                       int CommentCount,
                       int ShareCount,
                       boolean IsLiked,
                       boolean IsShared,
                       boolean IsOwner,
                       String WebLink,
                       boolean IsSaved,
                       long SavedDate,
                       ArrayList<Item> ItemContent,
                       UserShort Owner,
                       String ItemType,
                       ItemData ItemData
    ) {

        this.ImageItemId = ImageItemId;
        this.ItemGUID = ItemGUID;
        this.Description = Description;
        this.CreateDate = CreateDate;
        this.CreateOS = CreateOS;
        this.LastModifyDate = LastModifyDate;
        this.LastModifyOS = LastModifyOS;
        this.LastModifyOS = LastModifyOS;
        this.Lat = Lat;
        this.Long = Long;
        this.IsPrivate = IsPrivate;
        this.LikeCount = LikeCount;
        this.CommentCount = CommentCount;
        this.ShareCount = ShareCount;
        this.IsLiked = IsLiked;
        this.IsShared = IsShared;
        this.IsOwner = IsOwner;
        this.WebLink = WebLink;
        this.IsSaved = IsSaved;
        this.SavedDate = SavedDate;
        this.ItemContent = new RealmList<>();
        if (Owner != null) {
            this.Owner = Owner.copy();
        }
        this.ItemType = ItemType;
        this.ItemData = ItemData;


        if (ItemContent != null && ItemContent.size() > 0) {
            for (int i = 0; i < ItemContent.size(); i++) {
                ItemDB item = ItemContent.get(i).copy();
                this.ItemContent.add(item);
            }
        }
    }

    public static ArrayList<ImageItem> copy(RealmResults<ImageItemDB> list) {
        ArrayList<ImageItem> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ImageItem item = list.get(i).copy();
                result.add(item);
            }
        }
        return result;
    }

   /* @Ignore
    private RealmList<ImageComment> Comments;//danh sach comment
    @Ignore
    private RealmList<ImageLike> Likes;//danh sach da like
    @Ignore
    private RealmList<ImageShare> Shares;//danh sach da share*/


    //Bổ sung để load gợi ý kết bạn, quảng cáo, và view
    public static final int TYPE_SUGGEST = 0;
    public static final int TYPE_ADS = 1;
    public static final int TYPE_VIEW_ITEM = 2;
    private int typeView = TYPE_VIEW_ITEM;
    //Bổ sung để load gợi ý kết bạn, quảng cáo, và view


    public boolean isSaved() {
        return IsSaved;
    }

    public void setSaved(boolean saved) {
        IsSaved = saved;
    }

    public long getSavedDate() {
        return SavedDate;
    }

    public void setSavedDate(long savedDate) {
        SavedDate = savedDate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public long getImageItemId() {
        return ImageItemId;
    }

    public void setImageItemId(long imageItemId) {
        ImageItemId = imageItemId;
    }

    public String getItemGUID() {
        return ItemGUID;
    }

    public void setItemGUID(String itemGUID) {
        ItemGUID = itemGUID;
    }

    public RealmList<ItemDB> getItemContent() {
        return ItemContent;
    }

    public void setItemContent(RealmList<ItemDB> itemContent) {
        ItemContent = itemContent;
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

    public long getLastModifyDate() {
        return LastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        LastModifyDate = lastModifyDate;
    }

    public String getLastModifyOS() {
        return LastModifyOS;
    }

    public void setLastModifyOS(String lastModifyOS) {
        LastModifyOS = lastModifyOS;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public boolean isPrivate() {
        return IsPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        IsPrivate = aPrivate;
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

    public int getShareCount() {
        return ShareCount;
    }

    public void setShareCount(int shareCount) {
        ShareCount = shareCount;
    }

    public boolean isLiked() {
        return IsLiked;
    }

    public void setLiked(boolean liked) {
        IsLiked = liked;
    }

    public boolean isShared() {
        return IsShared;
    }

    public void setShared(boolean shared) {
        IsShared = shared;
    }

    public boolean isOwner() {
        return IsOwner;
    }

    public void setOwner(boolean owner) {
        IsOwner = owner;
    }

    public UserShortDB getOwner() {
        return Owner;
    }

    public void setOwner(UserShortDB owner) {
        Owner = owner;
    }


    public int getTypeView() {
        return typeView;
    }

    public void setTypeView(int typeView) {
        this.typeView = typeView;
    }


    public String getWebLink() {
        return WebLink;
    }

    public void setWebLink(String webLink) {
        WebLink = webLink;
    }


    /*public RealmList<ImageComment> getComments() {
        return Comments;
    }

    public void setComments(RealmList<ImageComment> comments) {
        Comments = comments;
    }

    public RealmList<ImageLike> getLikes() {
        return Likes;
    }

    public void setLikes(RealmList<ImageLike> likes) {
        Likes = likes;
    }

    public RealmList<ImageShare> getShares() {
        return Shares;
    }

    public void setShares(RealmList<ImageShare> shares) {
        Shares = shares;
    }*/

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public com.topceo.objects.image.ItemData getItemData() {
        return ItemData;
    }

    public void setItemData(com.topceo.objects.image.ItemData itemData) {
        ItemData = itemData;
    }
}
