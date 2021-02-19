package com.topceo.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by MrPhuong on 2016-07-27.
 */
public class CookieStore implements CookieJar {
    private final Set<Cookie> cookieStore = new HashSet<>();

    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        /**
         *Saves cookies from HTTP response
         * If the response includes a trailer this method is called second time
         */
        //Save cookies to the store
        cookieStore.addAll(cookies);
    }

    public List<Cookie> getValidCookies() {
        return validCookies;
    }

    List<Cookie> validCookies = new ArrayList<>();

    @Override
    synchronized public List<Cookie> loadForRequest(HttpUrl url) {
        /**
         * Load cookies from the jar for an HTTP request.
         * This method returns cookies that have not yet expired
         */
        List<Cookie> temps = new ArrayList<>(cookieStore);
        validCookies.clear();
        for (Cookie cookie : temps) {
            LogCookie(cookie);
            if (cookie.expiresAt() < System.currentTimeMillis()) {
                // invalid cookies
            } else {
                setHttpOnly(cookie, false);
                validCookies.add(cookie);

            }
        }

        /*Iterator<Cookie> iterator = cookieStore.iterator();
        while (iterator.hasNext()){
            Cookie cookie = iterator.next();
            LogCookie(cookie);
            if (cookie.expiresAt() < System.currentTimeMillis()) {
                // invalid cookies
            } else {
                validCookies.add(cookie);

            }
        }*/


        return validCookies;
    }

    //Print the values of cookies - Useful for testing
    private void LogCookie(Cookie cookie) {
        /*System.out.println("String: " + cookie.toString());
        System.out.println("Expires: " + cookie.expiresAt());
        System.out.println("Hash: " + cookie.hashCode());
        System.out.println("Path: " + cookie.path());
        System.out.println("Domain: " + cookie.domain());
        System.out.println("Name: " + cookie.name());
        System.out.println("Value: " + cookie.value());*/
    }

    public String getCookie() {
        if(validCookies!=null && validCookies.size()>0){
            for (int i = 0; i < validCookies.size(); i++) {
                return validCookies.get(i).toString();
            }
        }
        return null;
    }

    // Workaround httpOnly (getter)
    private boolean getHttpOnly(Cookie cookie) {
        try {
            Field fieldHttpOnly = cookie.getClass().getDeclaredField("httpOnly");
            fieldHttpOnly.setAccessible(true);

            return (boolean) fieldHttpOnly.get(cookie);
        } catch (Exception e) {
            // NoSuchFieldException || IllegalAccessException ||
            // IllegalArgumentException
//            Log.w(TAG, e);
        }
        return false;
    }

    // Workaround httpOnly (setter)
    private void setHttpOnly(Cookie cookie, boolean httpOnly) {
        try {
            Field fieldHttpOnly = cookie.getClass().getDeclaredField("httpOnly");
            fieldHttpOnly.setAccessible(true);

            fieldHttpOnly.set(cookie, httpOnly);
        } catch (Exception e) {
            // NoSuchFieldException || IllegalAccessException ||
            // IllegalArgumentException
//            Log.w(TAG, e);
        }
    }

}
