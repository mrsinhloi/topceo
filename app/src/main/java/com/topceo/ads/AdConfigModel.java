package com.topceo.ads;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MrPhuong on 2016-12-27.
 */

public class AdConfigModel implements Serializable {
    public static final int AD_NETWORK_NONE=0;
    public static final int AD_NETWORK_LOCAL=1;
    public static final int AD_NETWORK_ADMOB=2;
    public static final int AD_NETWORK_FACEBOOK=3;

    /// <summary>
/// 0: không quảng cáo
/// 1: Local dùng danh sách LocalAds
/// 2: Admob dùng ExternalAd
/// 3: Facebook dùng ExternalAd
/// </summary>
    private int AdNetworkId;
    private ArrayList<LocalAdModel> LocalAds;//			Nếu AdNetworkId = 1 thì dùng ds trả về này
    private ExternalAdModel ExternalAd;//			Nếu AdNetworkId = 2 & 3 thì dùng thuộc tính này


    public int getAdNetworkId() {
        return AdNetworkId;
    }

    public void setAdNetworkId(int adNetworkId) {
        AdNetworkId = adNetworkId;
    }

    public ArrayList<LocalAdModel> getLocalAds() {
        if(LocalAds==null)LocalAds=new ArrayList<>();
        return LocalAds;
    }

    public void setLocalAds(ArrayList<LocalAdModel> localAds) {
        LocalAds = localAds;
    }

    public ExternalAdModel getExternalAd() {
        return ExternalAd;
    }

    public void setExternalAd(ExternalAdModel externalAd) {
        ExternalAd = externalAd;
    }
}
