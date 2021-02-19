package com.topceo.ads;

import java.io.Serializable;

/**
 * Created by MrPhuong on 2016-12-27.
 */

public class AdsAppModel implements Serializable {

    private int AppId;
    private String AppName;
    /// Cấu hình quảng cáo Banner
    private AdConfigModel Banner;
    /// cấu hình quảng cáo màn hình trung gian (full)
    private AdConfigModel Interstitial;
    /// cấu hình quảng cáo Native
    private AdConfigModel Native;


    public int getAppId() {
        return AppId;
    }

    public void setAppId(int appId) {
        AppId = appId;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public AdConfigModel getBanner() {
        return Banner;
    }

    public void setBanner(AdConfigModel banner) {
        Banner = banner;
    }

    public AdConfigModel getInterstitial() {
        return Interstitial;
    }

    public void setInterstitial(AdConfigModel interstitial) {
        Interstitial = interstitial;
    }

    public AdConfigModel getNative() {
        return Native;
    }

    public void setNative(AdConfigModel aNative) {
        Native = aNative;
    }
}
