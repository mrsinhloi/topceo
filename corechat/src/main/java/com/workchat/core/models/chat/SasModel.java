package com.workchat.core.models.chat;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.workchat.core.utils.MyUtils;

import org.json.JSONObject;

/**
 * fileType(image, video, file), sasUrl, link, sasThumbUrl (nếu là image, video), thumbLink
 * Created by MrPhuong on 2017-09-08.
 */

public class SasModel implements Parcelable {

    private String sasUrl;
    private String link;
    private String sasThumbUrl;
    private String thumbLink;
    private String fileType;

    //local
    private String fileName;

    public String getSasUrl() {
        return sasUrl;
    }

    public void setSasUrl(String sasUrl) {
        this.sasUrl = sasUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSasThumbUrl() {
        return sasThumbUrl;
    }

    public void setSasThumbUrl(String sasThumbUrl) {
        this.sasThumbUrl = sasThumbUrl;
    }

    public String getThumbLink() {
        return thumbLink;
    }

    public void setThumbLink(String thumbLink) {
        this.thumbLink = thumbLink;
    }


    public static SasModel parseSasModel(Context context, Object... args) {
        SasModel item = null;
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    if (json.has("data")) {
                        String data = json.getString("data");
                        item = new Gson().fromJson(data, SasModel.class);
                    }
                } else {
                    String errMessage = json.getString("error");
                    MyUtils.log("parseSasModel" + json.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public static boolean isSuccess(Context context, Object... args) {
        boolean isSuccess = false;
        try {
            JSONObject json = (JSONObject) args[0];
            if (json.has("errorCode")) {
                int code = json.getInt("errorCode");
                if (code == 0) {
                    isSuccess = true;
                } else {
                    String errMessage = json.getString("error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sasUrl);
        dest.writeString(this.link);
        dest.writeString(this.sasThumbUrl);
        dest.writeString(this.thumbLink);
        dest.writeString(this.fileType);
    }

    public SasModel() {
    }

    protected SasModel(Parcel in) {
        this.sasUrl = in.readString();
        this.link = in.readString();
        this.sasThumbUrl = in.readString();
        this.thumbLink = in.readString();
        this.fileType = in.readString();
    }

    public static final Parcelable.Creator<SasModel> CREATOR = new Parcelable.Creator<SasModel>() {
        @Override
        public SasModel createFromParcel(Parcel source) {
            return new SasModel(source);
        }

        @Override
        public SasModel[] newArray(int size) {
            return new SasModel[size];
        }
    };

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
