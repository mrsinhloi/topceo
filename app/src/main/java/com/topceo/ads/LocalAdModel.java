package com.topceo.ads;

import java.io.Serializable;

/**
 * Created by MrPhuong on 2016-12-27.
 */

public class LocalAdModel implements Serializable {
    public static final String LOCAL_MODEL="LOCAL_MODEL";
    public static final int AD_TYPE_IMAGE=1;
    public static final int AD_TYPE_VIDEO=2;
    public static final int AD_TYPE_TEXT=3;


    private int AdId;
    private String AdName;
    private int CampaignId;
    private int AdUnitId;
    private int AdTypeId;//1,2,3 ////"Image"=>1, "Video"=>2, "Text"=>3

    //All Ads Properties
    private String ImageUrl;
    private String LinkUrl;
    private String RedirectLink;

    //Native Ads Properties
    private String Content;
    private String Title;
    private String AdvertiserIcon;



    public int getAdId() {
        return AdId;
    }

    public void setAdId(int adId) {
        AdId = adId;
    }

    public String getAdName() {
        return AdName;
    }

    public void setAdName(String adName) {
        AdName = adName;
    }

    public int getCampaignId() {
        return CampaignId;
    }

    public void setCampaignId(int campaignId) {
        CampaignId = campaignId;
    }

    public int getAdUnitId() {
        return AdUnitId;
    }

    public void setAdUnitId(int adUnitId) {
        AdUnitId = adUnitId;
    }

    public int getAdTypeId() {
        return AdTypeId;
    }

    public void setAdTypeId(int adTypeId) {
        AdTypeId = adTypeId;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return LinkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        LinkUrl = linkUrl;
    }

    public String getRedirectLink() {
        return RedirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        RedirectLink = redirectLink;
    }

    /*public String getContent() {
        return Content;
    }*/

    public void setContent(String content) {
        Content = content;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAdvertiserIcon() {
        return AdvertiserIcon;
    }

    public void setAdvertiserIcon(String advertiserIcon) {
        AdvertiserIcon = advertiserIcon;
    }
}
