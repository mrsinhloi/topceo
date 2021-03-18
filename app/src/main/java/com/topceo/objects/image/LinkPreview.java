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

    public LinkPreview(){}

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


}
