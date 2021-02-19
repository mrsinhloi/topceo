package com.topceo.objects.image;

import android.os.Parcel;
import android.os.Parcelable;

import com.topceo.objects.db.InfoDB;

public class Info implements Parcelable {

    public static final String ICON_YOUTUBE = "YOUTUBE";
    public static final String ICON_DOWNLOAD = "DOWNLOAD";
    public static final String ICON_WEBVIEW = "WEBVIEW";


    /* "Info: {
     Icon, (Icon để app hiện lên ở góc dưới)
     Text, (Text gợi ý )
     Link, (Link tới Youtube hay link nào đó)
     Target (Mở popup hay webview ...)
 }"*/
    private String Icon;
    private String Text;
    private String Link;
    private String Target;

    public InfoDB copy() {
        InfoDB item = new InfoDB();
        item.setIcon(Icon);
        item.setText(Text);
        item.setLink(Link);
        item.setTarget(Target);
        return item;
    }


    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getTarget() {
        return Target;
    }

    public void setTarget(String target) {
        Target = target;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Icon);
        dest.writeString(this.Text);
        dest.writeString(this.Link);
        dest.writeString(this.Target);
    }

    public Info() {
    }

    protected Info(Parcel in) {
        this.Icon = in.readString();
        this.Text = in.readString();
        this.Link = in.readString();
        this.Target = in.readString();
    }

    public static final Parcelable.Creator<Info> CREATOR = new Parcelable.Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel source) {
            return new Info(source);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
}
