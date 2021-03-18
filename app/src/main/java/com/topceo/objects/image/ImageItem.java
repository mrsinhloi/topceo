package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.db.ImageItemDB;
import com.topceo.objects.db.ItemDB;
import com.topceo.objects.db.UserShortDB;
import com.topceo.objects.other.UserShort;
import com.smartapp.collage.MediaLocal;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * https://github.com/mcharmas/android-parcelable-intellij-plugin
 * Created by MrPhuong on 2016-07-27.
 */
public class ImageItem implements Parcelable {
    public static final String IMAGE_ITEM = "IMAGE_ITEM";
    public static final String IMAGE_ITEM_NUMBER_COMMENT = "IMAGE_ITEM_NUMBER_COMMENT";
    public static final String IMAGE_ITEM_ID = "IMAGE_ITEM_ID";
    public static final String IMAGE_ARRAY_LIST = "IMAGE_ARRAY_LIST";


    //
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
    private ArrayList<Item> ItemContent = new ArrayList<>();
    private UserShort Owner;

    //bo sung 27/7/2020
    public static final String ITEM_TYPE_INSTAGRAM = "INSTA";
    public static final String ITEM_TYPE_FACEBOOK = "FB";
    private String ItemType = ITEM_TYPE_INSTAGRAM;//INSTA / FB
    private ItemData ItemData = null;

    //bo sung cho pending post
    private long ItemId;
    private long GroupId;


    protected ImageItem(Parcel in) {
        ImageItemId = in.readLong();
        ItemGUID = in.readString();
        Description = in.readString();
        CreateDate = in.readLong();
        CreateOS = in.readString();
        LastModifyDate = in.readLong();
        LastModifyOS = in.readString();
        Location = in.readString();
        Lat = in.readDouble();
        Long = in.readDouble();
        IsPrivate = in.readByte() != 0;
        LikeCount = in.readInt();
        CommentCount = in.readInt();
        ShareCount = in.readInt();
        IsLiked = in.readByte() != 0;
        IsShared = in.readByte() != 0;
        IsOwner = in.readByte() != 0;
        WebLink = in.readString();
        IsSaved = in.readByte() != 0;
        SavedDate = in.readLong();
        ItemContent = in.createTypedArrayList(Item.CREATOR);
        Owner = in.readParcelable(UserShort.class.getClassLoader());
        ItemType = in.readString();
        ItemId = in.readLong();
        GroupId = in.readLong();
        Comments = in.createTypedArrayList(ImageComment.CREATOR);
        Likes = in.createTypedArrayList(ImageLike.CREATOR);
        Shares = in.createTypedArrayList(ImageShare.CREATOR);
        typeView = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ImageItemId);
        dest.writeString(ItemGUID);
        dest.writeString(Description);
        dest.writeLong(CreateDate);
        dest.writeString(CreateOS);
        dest.writeLong(LastModifyDate);
        dest.writeString(LastModifyOS);
        dest.writeString(Location);
        dest.writeDouble(Lat);
        dest.writeDouble(Long);
        dest.writeByte((byte) (IsPrivate ? 1 : 0));
        dest.writeInt(LikeCount);
        dest.writeInt(CommentCount);
        dest.writeInt(ShareCount);
        dest.writeByte((byte) (IsLiked ? 1 : 0));
        dest.writeByte((byte) (IsShared ? 1 : 0));
        dest.writeByte((byte) (IsOwner ? 1 : 0));
        dest.writeString(WebLink);
        dest.writeByte((byte) (IsSaved ? 1 : 0));
        dest.writeLong(SavedDate);
        dest.writeTypedList(ItemContent);
        dest.writeParcelable(Owner, flags);
        dest.writeString(ItemType);
        dest.writeLong(ItemId);
        dest.writeLong(GroupId);
        dest.writeTypedList(Comments);
        dest.writeTypedList(Likes);
        dest.writeTypedList(Shares);
        dest.writeInt(typeView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public ImageItemDB copy() {
        ImageItemDB item = new ImageItemDB(ImageItemId,
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
                getItemData()
        );
        return item;
    }

    public ImageItem(long ImageItemId,
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
                     RealmList<ItemDB> ItemContent,
                     UserShortDB Owner,
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
        this.ItemContent = new ArrayList<>();
        if (Owner != null) {
            this.Owner = Owner.copy();
        }
        this.ItemType = ItemType;
        this.ItemData = ItemData;

        if (ItemContent != null && ItemContent.size() > 0) {
            for (int i = 0; i < ItemContent.size(); i++) {
                Item item = ItemContent.get(i).copy();
                this.ItemContent.add(item);
            }
        }
    }

    public ImageItem() {}

    //bo sung comment preview
    private ArrayList<ImageComment> Comments = new ArrayList<>();//danh sach comment
    private ArrayList<ImageLike> Likes = new ArrayList<>();//danh sach da like
    private ArrayList<ImageShare> Shares = new ArrayList<>();//danh sach da share


    //Bổ sung để load gợi ý kết bạn, quảng cáo, và view
    public static final int TYPE_SUGGEST = 0;
    public static final int TYPE_ADS = 1;
    public static final int TYPE_VIEW_ITEM = 2;
    private int typeView = TYPE_VIEW_ITEM;

    //bo sung local phan ra tu TYPE_VIEW_TIEM -> INSTA/FB
    public static final int TYPE_FACEBOOK = 3;
    public static final int TYPE_INSTAGRAM_IMAGE = 4;
    public static final int TYPE_INSTAGRAM_VIDEO = 5;
    public static final int TYPE_GROUP = 6;
    public static final int TYPE_ADD_POST = 7;


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
        if (Description == null) {
            Description = "";
        } else {
            Description = Description.replace("\n", "<br/>");
        }

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

    public ArrayList<Item> getItemContent() {
        return ItemContent;
    }

    public void setItemContent(ArrayList<Item> itemContent) {
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
        //bo share, share truc tiep tu intent system nen ko ghi nhan nua
        ShareCount = 0;
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

    public UserShort getOwner() {
        return Owner;
    }

    public void setOwner(UserShort owner) {
        Owner = owner;
    }

    public ArrayList<ImageComment> getComments() {
        return Comments;
    }

    public void setComments(ArrayList<ImageComment> comments) {
        Comments = comments;
    }

    public ArrayList<ImageLike> getLikes() {
        return Likes;
    }

    public void setLikes(ArrayList<ImageLike> likes) {
        Likes = likes;
    }

    public ArrayList<ImageShare> getShares() {
        return Shares;
    }

    public void setShares(ArrayList<ImageShare> shares) {
        Shares = shares;
    }

    public int getTypeView() {
        return typeView;
    }

    public void setTypeView(int typeView) {
        this.typeView = typeView;
    }


    ///////////////////////////////////
    public String getImageSmall() {
        String url = "";
        if (ItemContent != null && ItemContent.size() > 0) {
            MediaObject item = ItemContent.get(0).getSmall();
            if (item != null) {
                url = item.getLink();
            }
        }

        //neu ko co thi lay medium
        /*if (TextUtils.isEmpty(url)) {
            url = getImageMedium();
        }
        //neu ko co thi lay large
        if (TextUtils.isEmpty(url)) {
            url = getImageLarge();
        }*/

        return url;
    }

    public String getImageMedium() {
        String url = "";
        if (ItemContent != null && ItemContent.size() > 0) {
            MediaObject item = ItemContent.get(0).getMedium();
            if (item != null) {
                url = item.getLink();
            }
        }

        //neu ko co thi lay medium
        /*if (TextUtils.isEmpty(url)) {
            url = getImageLarge();
        }
        //neu ko co thi lay small
        if (TextUtils.isEmpty(url)) {
            url = getImageSmall();
        }*/
        return url;
    }

    public String getImageLarge() {
        String url = "";
        if (ItemContent != null && ItemContent.size() > 0) {
            MediaObject item = ItemContent.get(0).getLarge();
            if (item != null) {
                url = item.getLink();
            }
        }

        //neu ko co thi lay medium
        /*if (TextUtils.isEmpty(url)) {
            url = getImageMedium();
        }
        //neu ko co thi lay small
        if (TextUtils.isEmpty(url)) {
            url = getImageSmall();
        }*/
        return url;
    }

    public float getImageRatio() {
        float ratio = 1;
        if (ItemContent != null && ItemContent.size() > 0) {
            MediaObject item = ItemContent.get(0).getLarge();
            if (item != null) {
                float w = item.getWidth();
                float h = item.getHeight();
                ratio = w / h;
            }
        }
        return ratio;
    }


    public String getItemContentType() {
        String url = "";
        if (ItemContent != null && ItemContent.size() > 0) {
            Item item = ItemContent.get(0);
            return (item.getItemType() == null) ? Item.TYPE_IMAGE : item.getItemType();
        }
        return url;
    }

    public boolean isVideo() {
        boolean video = false;
        String itemType = getItemContentType();
        switch (itemType) {
            case Item.TYPE_VIDEO:
                video = true;
                break;
        }
        return video;
    }

    /**
     * Ratio = width/height
     *
     * @param widthImage
     * @return
     */
    public int getNeedHeightImage(int widthImage) {
        int height = widthImage;
        float ratio = getImageRatio();
        height = (int) ((float) widthImage / ratio);
        return height;
    }

    public Info getInfo() {
        if (ItemContent != null && ItemContent.size() > 0) {
            Item item = ItemContent.get(0);
            if (item.getInfo() != null) {
                return item.getInfo();
            }
        }
        return null;
    }

    public String getWebLink() {
        return WebLink;
    }

    public void setWebLink(String webLink) {
        WebLink = webLink;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    public String getItemType() {
        return ItemType;
    }


    public com.topceo.objects.image.ItemData getItemData() {
        /*if(ItemData !=null && ItemData instanceof ItemData){
            return (ItemData)ItemData;
        }*/
        return ItemData;
    }

    public void setItemData(com.topceo.objects.image.ItemData itemData) {
        ItemData = itemData;
    }



    @Nullable
    public List<MediaLocal> getListMediaLocal() {
        List<MediaLocal> listUri = new ArrayList<>();
        if (ItemContent != null && ItemContent.size() > 0) {
            for (int i = 0; i < ItemContent.size(); i++) {
                Item item = ItemContent.get(i);
                int width = item.getMedium().getWidth();
                int height = item.getMedium().getHeight();
                boolean isVideo = item.getItemType() == Item.TYPE_VIDEO;

                MediaLocal media = new MediaLocal(item.getMedium().getLink(), width, height, isVideo, null);
                listUri.add(media);
            }
        }
        return listUri;
    }

    public long getItemId() {
        return ItemId;
    }

    public void setItemId(long itemId) {
        ItemId = itemId;
    }

    public long getGroupId() {
        return GroupId;
    }

    public void setGroupId(long groupId) {
        GroupId = groupId;
    }
}
