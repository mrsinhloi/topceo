package com.topceo.services;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class SignupError implements Serializable {
    private ArrayList<String> Invalid;
    private ArrayList<String> Existed;
    private ArrayList<String> AtleastOne;//ko can xet vi app da xet du dieu kien truoc khi goi len

    public ArrayList<String> getInvalid() {
        return Invalid;
    }

    public void setInvalid(ArrayList<String> invalid) {
        Invalid = invalid;
    }

    public ArrayList<String> getExisted() {
        return Existed;
    }

    public void setExisted(ArrayList<String> existed) {
        Existed = existed;
    }

    public ArrayList<String> getAtleastOne() {
        return AtleastOne;
    }

    public void setAtleastOne(ArrayList<String> atleastOne) {
        AtleastOne = atleastOne;
    }

    public String getParams(ArrayList<String> list) {
        String params = null;
        if (list != null && list.size() > 0) {
            params = TextUtils.join(",", list);
        }
        return params;
    }
}
