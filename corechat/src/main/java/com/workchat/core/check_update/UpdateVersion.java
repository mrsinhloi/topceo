package com.workchat.core.check_update;

import java.io.Serializable;

/**
 * Created by Administrator on 9/15/2015.
 */
public class UpdateVersion implements Serializable {
    //{"errorCode":0,"error":null,"data":{"OS":"AndroidWorkChat","Version":43,"ForceUpdate":false,"ForceUpdateVersion":35,"AppLink":null}}
    private String OS;
    private int Version;
    private boolean ForceUpdate;//true/false theo Version
    private int ForceUpdateVersion;//nhỏ hơn version này thì bắt buộc cập nhật (bất kể ForceUpdate)
    private String AppLink;

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public boolean isForceUpdate() {
        return ForceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        ForceUpdate = forceUpdate;
    }

    public String getAppLink() {
        return AppLink;
    }

    public void setAppLink(String appLink) {
        AppLink = appLink;
    }

    public int getForceUpdateVersion() {
        return ForceUpdateVersion;
    }

    public void setForceUpdateVersion(int forceUpdateVersion) {
        ForceUpdateVersion = forceUpdateVersion;
    }
}
