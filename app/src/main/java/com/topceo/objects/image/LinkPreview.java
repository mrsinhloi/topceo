package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;

import io.realm.RealmObject;

public class LinkPreview extends RealmObject implements Parcelable {

    private String Link;
    private String Title;
    private String Caption;
    private String Image;
    private String SiteName;

    public LinkPreview(){}
    public LinkPreview(
            String Link,
            String Title,
            String Caption,
            String Image,
            String SiteName){
        this.Link = Link;
        this.Title = Title;
        this.Caption = Caption;
        this.Image = Image;
        this.SiteName = SiteName;
    }


    protected LinkPreview(Parcel in) {
        Link = in.readString();
        Title = in.readString();
        Caption = in.readString();
        Image = in.readString();
        SiteName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Link);
        dest.writeString(Title);
        dest.writeString(Caption);
        dest.writeString(Image);
        dest.writeString(SiteName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LinkPreview> CREATOR = new Creator<LinkPreview>() {
        @Override
        public LinkPreview createFromParcel(Parcel in) {
            return new LinkPreview(in);
        }

        @Override
        public LinkPreview[] newArray(int size) {
            return new LinkPreview[size];
        }
    };

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public static MyItemData getMyItemData(Object ItemData) {
        if (ItemData != null && !TextUtils.isEmpty(ItemData.toString())) {
            if (ItemData instanceof LinkedTreeMap) {
                LinkedTreeMap<Object, Object> t = (LinkedTreeMap) ItemData;
                Object preview = t.get("LinkPreview");
                if (preview instanceof LinkedTreeMap) {
                    LinkedTreeMap<Object, Object> p = (LinkedTreeMap) preview;
                    String link = p.get("Link").toString();
                    String caption = p.get("Caption").toString();
                    String title = p.get("Title").toString();
                    String image = p.get("Image").toString();
                    String sitename = p.get("SiteName").toString();
                    LinkPreview pre = new LinkPreview(link, title, caption, image, sitename);

                    MyItemData data = new MyItemData();
                    data.setLinkPreview(pre);
                    return data;
                }
            }else if (ItemData instanceof MyItemData) {
                return (MyItemData) ItemData;
            }
        }
        return null;
    }

}
