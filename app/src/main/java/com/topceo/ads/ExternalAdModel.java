package com.topceo.ads;

import java.io.Serializable;

/**
 * Created by MrPhuong on 2016-12-27.
 */

public class ExternalAdModel implements Serializable {

    private int ItemId;
    /// AdUnit của Admob hoặc PlacementId của Facebook
    private String AdUnitId;


    public int getItemId() {
        return ItemId;
    }

    public void setItemId(int itemId) {
        ItemId = itemId;
    }

    public String getAdUnitId() {
        return AdUnitId;
    }

    public void setAdUnitId(String adUnitId) {
        AdUnitId = adUnitId;
    }
}
