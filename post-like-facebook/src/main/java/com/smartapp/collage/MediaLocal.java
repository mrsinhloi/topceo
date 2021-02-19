package com.smartapp.collage;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.smartapp.sizes.ISize;
import com.smartapp.sizes.SizeFromImage;
import com.smartapp.sizes.SizeFromVideoFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class MediaLocal implements Parcelable {

    private String path = "";
    private int width = 0;
    private int height = 0;
    private int sizeInKb = 0;
    private boolean isVideo = false;
    private Uri uri;//ko co uri co nghia la ko phai path local, ma load tu server

    public MediaLocal() {
    }

    public MediaLocal(String path, int width, int height, boolean isVideo, Uri uri) {
        this.path = path;
        this.width = width;
        this.height = height;
        this.isVideo = isVideo;
        this.uri = uri;
    }


    protected MediaLocal(Parcel in) {
        path = in.readString();
        width = in.readInt();
        height = in.readInt();
        sizeInKb = in.readInt();
        isVideo = in.readByte() != 0;
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(sizeInKb);
        dest.writeByte((byte) (isVideo ? 1 : 0));
        dest.writeParcelable(uri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaLocal> CREATOR = new Creator<MediaLocal>() {
        @Override
        public MediaLocal createFromParcel(Parcel in) {
            return new MediaLocal(in);
        }

        @Override
        public MediaLocal[] newArray(int size) {
            return new MediaLocal[size];
        }
    };

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSizeInKb() {
        return sizeInKb;
    }

    public void setSizeInKb(int sizeInKb) {
        this.sizeInKb = sizeInKb;
    }

    public void setSizeInKb(long sizeInKb) {
        this.sizeInKb = (int) sizeInKb;
    }


    public boolean isSquare() {
        return (height == width) ||
                (height > width && height <= width * 1.15) ||
                (height < width && height * 1.15 >= width);
    }


    public boolean isVertical() {
        return height > width;
    }

    public boolean isHorizontal() {
        return height < width;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @NotNull
    public static ArrayList<MediaLocal> getListMediaLocal(List<Uri> uriList, Context context) {
        ArrayList<MediaLocal> list = new ArrayList<>();
        if (uriList != null && uriList.size() > 0) {
            for (int i = 0; i < uriList.size(); i++) {
                Uri uri = uriList.get(i);
                String path = uri.getPath();
                if(uri.toString().contains("content://")){
                    Uri uriImage = FileUtils.parseUriImage(uri, context);
                    path = uriImage.getPath();
                }
                boolean isVideo = UtilsKt.isVideoFile(path);

                ISize size = null;
                if (isVideo) {
                    size = new SizeFromVideoFile(path);
                } else {
                    size = new SizeFromImage(path);
                }

                int width = size.width();
                int height = size.height();
                MediaLocal media = new MediaLocal(path, width, height, isVideo, uri);
                list.add(media);
            }
        }
        return list;
    }

    public static ArrayList<MediaLocal> getListMediaLocal(@Nullable String[] imageList) {
        ArrayList<MediaLocal> list = new ArrayList<>();
        if (imageList != null && imageList.length > 0) {
            for (int i = 0; i < imageList.length; i++) {
                String path = imageList[i];
                boolean isVideo = UtilsKt.isVideoFile(path);

                ISize size = null;
                if (isVideo) {
                    size = new SizeFromVideoFile(path);
                } else {
                    size = new SizeFromImage(path);
                }

                int width = size.width();
                int height = size.height();
                MediaLocal media = new MediaLocal(path, width, height, isVideo, null);
                list.add(media);
            }
        }
        return list;
    }

    @NotNull
    public static ArrayList<String> getListPath(@NotNull ArrayList<MediaLocal> items) {
        ArrayList<String> list = new ArrayList<String>();
        if (items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                MediaLocal item = items.get(i);
                list.add(item.getPath());
            }
        }
        return list;
    }

    public static ArrayList<Uri> getListUri(@NotNull ArrayList<MediaLocal> items) {
        ArrayList<Uri> list = new ArrayList<Uri>();
        if (items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                MediaLocal item = items.get(i);
                Uri uri = item.getUri();
                if(uri!=null){
                    list.add(uri);
                }else {
                    list.add(Uri.parse(item.getPath()));
                }
            }
        }
        return list;
    }

}
