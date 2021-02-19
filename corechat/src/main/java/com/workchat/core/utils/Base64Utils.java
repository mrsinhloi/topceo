package com.workchat.core.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Utils {

    public static String decode(String value){
        byte[] data = Base64.decode(value, Base64.DEFAULT);
        String key = new String(data, StandardCharsets.UTF_8);
        return key;
    }

    private String encode(String value){
        byte[] bytes = value.getBytes();
        String encodeString = new String(android.util.Base64.encode(bytes, Base64.DEFAULT));
        return encodeString;
    }
}
