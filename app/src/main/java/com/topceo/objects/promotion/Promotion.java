package com.topceo.objects.promotion;

import android.os.Parcel;
import android.os.Parcelable;

public class Promotion implements Parcelable {

    private long PromotionId;
    private String Title;
    private String Description;
    private String Banner;
    private boolean AutoApply;

    private String Screen;
    /**
     * "Kiểu hiển thị: (trước mắt thì chỉ có POPUP)
     * POPUP: kiểu popup
     * POST: kiểu post lồng trong các post khác"
     */
    private String ShowType;
    /**
     * "Kiểu hành động:
     * ACCEPT: show 2 button Accept & Refuse để User nhận Promo, nếu accept thì gọi setPromotion, refuse thì gọi refusePromotion
     * DEEPLINK: Deeplink đến màn hình mong muốn trong app, dựa vào ActionLink để chuyển đến màn hình, gọi accept hoặc refuse cũng đc để lần sau ko hiện nữa
     * LINK: link web bên ngoài theo giá trị ActionLink, gọi accept hoặc refuse cũng đc để lần sau ko hiện nữa"
     */
    private String ActionType;
    private String ActionLink;



    public long getPromotionId() {
        return PromotionId;
    }

    public void setPromotionId(long promotionId) {
        PromotionId = promotionId;
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

    public String getBanner() {
        return Banner;
    }

    public void setBanner(String banner) {
        Banner = banner;
    }

    public boolean isAutoApply() {
        return AutoApply;
    }

    public void setAutoApply(boolean autoApply) {
        AutoApply = autoApply;
    }


    public Promotion() {
    }

    public String getScreen() {
        return Screen;
    }

    public void setScreen(String screen) {
        Screen = screen;
    }

    public String getShowType() {
        return ShowType;
    }

    public void setShowType(String showType) {
        ShowType = showType;
    }

    public String getActionType() {
        return ActionType;
    }

    public void setActionType(String actionType) {
        ActionType = actionType;
    }

    public String getActionLink() {
        return ActionLink;
    }

    public void setActionLink(String actionLink) {
        ActionLink = actionLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.PromotionId);
        dest.writeString(this.Title);
        dest.writeString(this.Description);
        dest.writeString(this.Banner);
        dest.writeByte(this.AutoApply ? (byte) 1 : (byte) 0);
        dest.writeString(this.Screen);
        dest.writeString(this.ShowType);
        dest.writeString(this.ActionType);
        dest.writeString(this.ActionLink);
    }

    protected Promotion(Parcel in) {
        this.PromotionId = in.readLong();
        this.Title = in.readString();
        this.Description = in.readString();
        this.Banner = in.readString();
        this.AutoApply = in.readByte() != 0;
        this.Screen = in.readString();
        this.ShowType = in.readString();
        this.ActionType = in.readString();
        this.ActionLink = in.readString();
    }

    public static final Creator<Promotion> CREATOR = new Creator<Promotion>() {
        @Override
        public Promotion createFromParcel(Parcel source) {
            return new Promotion(source);
        }

        @Override
        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };
}
