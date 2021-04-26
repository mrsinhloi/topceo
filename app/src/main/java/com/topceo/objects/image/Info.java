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

    protected Info(Parcel in) {
        Icon = in.readString();
        Text = in.readString();
        Link = in.readString();
        Target = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Icon);
        dest.writeString(Text);
        dest.writeString(Link);
        dest.writeString(Target);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

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



    public Info() {
    }


}
