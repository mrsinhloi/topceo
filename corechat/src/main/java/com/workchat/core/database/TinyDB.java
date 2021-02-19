package com.workchat.core.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.workchat.core.models.realm.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * https://github.com/mukeshsolanki/Android-Shared-Preferences-TinyDB-
 * Created by MrPhuong on 2016-07-27.
 * //Create a new instance of TinyDB
 * TinyDB tinyDB=new TinyDB(appContext);
 * <p>
 * //use that instance to save data
 * <p>
 * tinyDB.putString(key,value); //Save's a string value in your preferences
 * tinyDB.putInt(key,value); //Save's a int value in your preferences
 * <p>
 * <p>
 * //use that instance to retrieve data
 * tinyDB.getString(key); //retrives the data from preferences or default values if it does not exists
 * tinyDB.getBoolean(key); //retrives the data from preferences or default values if it does not exists
 */
public class TinyDB {

    public static final String CHAT_ADAPTER_POSITION = "CHAT_ADAPTER_POSITION";
    public static final String DATE_SYNC_LASTEST = "DATE_SYNC_LASTEST";//lan dong bo gan nhat
    public static final String LAST_TIME_DEEP_LINK = "LAST_TIME_DEEP_LINK";
    public static final String IS_ALLOW_CHAT_BUBBLE = "IS_ALLOW_CHAT_BUBBLE";



    private SharedPreferences preferences;

    private Gson gson;
    Context context;

    public TinyDB(Context appContext) {
        preferences = appContext.getSharedPreferences("MY_XML", Context.MODE_PRIVATE);
        gson = new Gson();
        this.context = appContext;
    }

    /**
     * Get int value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key SharedPreferences key
     * @return int value at 'key' or 'defaultValue' if key not found
     */
    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Get double value from SharedPreferences at 'key'. If exception thrown, return 'defaultValue'
     *
     * @param key          SharedPreferences key
     * @param defaultValue double value returned if exception is thrown
     * @return double value at 'key' or 'defaultValue' if exception is thrown
     */
    public double getDouble(String key, double defaultValue) {
        String number = getString(key);

        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    /**
     * Get String value from SharedPreferences at 'key'. If key not found, return ""
     *
     * @param key SharedPreferences key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    public String getString(String key) {
        return preferences.getString(key, "");
    }
    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    /**
     * Get parsed ArrayList of String from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of String
     */
    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(
                Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }

    /**
     * Get boolean value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key SharedPreferences key
     * @return boolean value at 'key' or 'defaultValue' if key not found
     */
    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaul) {
        return preferences.getBoolean(key, defaul);
    }

    public Object getObject(String key, Class<?> classOfT) {

        String json = getString(key);
        Object value = null;
        try {
            value = new Gson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (value == null) throw new NullPointerException();
        return value;
    }



    /**
     * Put int value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value int value to be added
     */
    public void putInt(String key, int value) {
        checkForNullKey(key);
        preferences.edit().putInt(key, value).apply();
    }

    /**
     * Put double value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value double value to be added
     */
    public void putDouble(String key, double value) {
        checkForNullKey(key);
        putString(key, String.valueOf(value));
    }

    /**
     * Put String value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value String value to be added
     */
    public void putString(String key, String value) {
        checkForNullKey(key);
        checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Put ArrayList of String into SharedPreferences with 'key' and save
     *
     * @param key        SharedPreferences key
     * @param stringList ArrayList of String to be added
     */
    public void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    /**
     * Put boolean value into SharedPreferences with 'key' and save
     *
     * @param key   SharedPreferences key
     * @param value boolean value to be added
     */
    public void putBoolean(String key, boolean value) {
        checkForNullKey(key);
        preferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Put ObJect any type into SharedPrefrences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param obj is the Object you want to put
     */
    public void putObject(String key, Object obj) {
        checkForNullKey(key);
//        Gson gson = new Gson();
        putString(key, gson.toJson(obj));
    }

    /**
     * Remove SharedPreferences item with 'key'
     *
     * @param key SharedPreferences key
     */
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    /**
     * Clear SharedPreferences (remove everything)
     */
    public void clear() {
        preferences.edit().clear().apply();
    }

    /**
     * Retrieve all values from SharedPreferences. Do not modify collection return by method
     *
     * @return a Map representing a list of key/value pairs from SharedPreferences
     */
    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive
     * measure
     *
     * @param key the pref key
     */
    public void checkForNullKey(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive
     * measure
     *
     * @param value the pref value
     */
    public void checkForNullValue(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }


    /////// Register /////////////////////////////
    public static final String TOKEN_USER = "TOKEN_USER";
    //public static final String TOKEN_GCM = "TOKEN_GCM";
    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    public void putListRoom(String key, ArrayList<Room> objArray) {
        checkForNullKey(key);
//        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (Room obj : objArray) {
            objStrings.add(gson.toJson(obj));
        }
        putListString(key, objStrings);
    }

    public ArrayList<Room> getListRoom(String key) {


        ArrayList<String> objStrings = getListString(key);
        ArrayList<Room> objects = new ArrayList<Room>();

        for (String jObjString : objStrings) {
            Room value = gson.fromJson(jObjString, Room.class);
            objects.add(value);
        }
        return objects;
    }

}
