package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class LinkPreview extends RealmObject implements Parcelable {

    private String Link;
    private String Title;
    private String Caption;
    private String Image;
    private String SiteName;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Link);
        dest.writeString(this.Title);
        dest.writeString(this.Caption);
        dest.writeString(this.Image);
        dest.writeString(this.SiteName);
    }

    public LinkPreview() {
    }

    protected LinkPreview(Parcel in) {
        this.Link = in.readString();
        this.Title = in.readString();
        this.Caption = in.readString();
        this.Image = in.readString();
        this.SiteName = in.readString();
    }

    public static final Parcelable.Creator<LinkPreview> CREATOR = new Parcelable.Creator<LinkPreview>() {
        @Override
        public LinkPreview createFromParcel(Parcel source) {
            return new LinkPreview(source);
        }

        @Override
        public LinkPreview[] newArray(int size) {
            return new LinkPreview[size];
        }
    };
}
