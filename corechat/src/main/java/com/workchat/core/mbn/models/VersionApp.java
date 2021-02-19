package com.workchat.core.mbn.models;

/**
 * Created by Hung on 1/14/16.
 */
public class VersionApp {

    String versionName = "";
    String versionCode = "";
    String link = "";
    String feature = "";

    public VersionApp() {
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }
}
