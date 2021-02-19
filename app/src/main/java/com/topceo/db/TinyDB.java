package com.topceo.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.desmond.squarecamera.myproject.ImageUtils;
import com.topceo.gallery.ParcelableImageModel;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * https://github.com/mukeshsolanki/Android-Shared-Preferences-TinyDB-
 * Created by MrPhuong on 2016-07-27.
 *  //Create a new instance of TinyDB
 TinyDB tinyDB=new TinyDB(appContext);

 //use that instance to save data

 tinyDB.putString(key,value); //Save's a string value in your preferences
 tinyDB.putInt(key,value); //Save's a int value in your preferences


 //use that instance to retrieve data
 tinyDB.getString(key); //retrives the data from preferences or default values if it does not exists
 tinyDB.getBoolean(key); //retrives the data from preferences or default values if it does not exists
 */
public class TinyDB {

    public static final int NUMBER_ERROR_MAX = 2;
    public static final String IS_SHUFFLE = "IS_SHUFFLE";
    public static final String IS_REPEAT = "IS_REPEAT";
    public static final String LAST_SYN_DATE = "LAST_SYN_DATE";
    public static final String IS_SHOWCASE = "IS_SHOWCASE";
    public static final String IS_REGISTER_ONESIGNAL_LOCAL_SERVER = "IS_REGISTER_ONESIGNAL_LOCAL_SERVER";

    private SharedPreferences preferences;
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";

    public TinyDB(Context appContext) {
//        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        preferences=appContext.getSharedPreferences("MY_XML", Context.MODE_PRIVATE);
    }

    /**
     * Decodes the Bitmap from 'path' and returns it
     *
     * @param path image path
     * @return the Bitmap from 'path'
     */
    public Bitmap getImage(String path) {
        Bitmap bitmapFromPath = null;
        try {
            bitmapFromPath = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return bitmapFromPath;
    }

    /**
     * Returns the String path of the last saved image
     *
     * @return string path of the last saved image
     */
    public String getSavedImagePath() {
        return lastImagePath;
    }

    /**
     * Saves 'theBitmap' into folder 'theFolder' with the name 'theImageName'
     *
     * @param theFolder the folder path dir you want to save it to e.g "DropBox/WorkImages"
     * @param theImageName the name you want to assign to the image file e.g "MeAtLunch.png"
     * @param theBitmap the image you want to save as a Bitmap
     * @return returns the full path(file system address) of the saved image
     */
    public String putImage(String theFolder, String theImageName, Bitmap theBitmap) {
        if (theFolder == null || theImageName == null || theBitmap == null) return null;

        this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
        String mFullPath = setupFullPath(theImageName);

        if (!mFullPath.equals("")) {
            lastImagePath = mFullPath;
            saveBitmap(mFullPath, theBitmap);
        }

        return mFullPath;
    }

    /**
     * Saves 'theBitmap' into 'fullPath'
     *
     * @param fullPath full path of the image file e.g. "Images/MeAtLunch.png"
     * @param theBitmap the image you want to save as a Bitmap
     * @return true if image was saved, false otherwise
     */
    public boolean putImageWithFullPath(String fullPath, Bitmap theBitmap) {
        return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap);
    }

    /**
     * Creates the path for the image with name 'imageName' in DEFAULT_APP.. directory
     *
     * @param imageName name of the image
     * @return the full path of the image. If it failed to create directory, return empty string
     */
    private String setupFullPath(String imageName) {
        File mFolder =
                new File(Environment.getExternalStorageDirectory(), DEFAULT_APP_IMAGEDATA_DIRECTORY);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists()) {
            if (!mFolder.mkdirs()) {
                Log.e("ERROR", "Failed to setup folder");
                return "";
            }
        }

        return mFolder.getPath() + '/' + imageName;
    }

    /**
     * Saves the Bitmap as a PNG file at path 'fullPath'
     *
     * @param fullPath path of the image file
     * @param bitmap the image as a Bitmap
     * @return true if it successfully saved, false otherwise
     */
    private boolean saveBitmap(String fullPath, Bitmap bitmap) {
        if (fullPath == null || bitmap == null) return false;

        boolean fileCreated = false;
        boolean bitmapCompressed = false;
        boolean streamClosed = false;

        File imageFile = new File(fullPath);

        if (imageFile.exists()) if (!imageFile.delete()) return false;

        try {
            fileCreated = imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            bitmapCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, ImageUtils.IMAGE_QUALITY, out);
        } catch (Exception e) {
            e.printStackTrace();
            bitmapCompressed = false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                    streamClosed = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    streamClosed = false;
                }
            }
        }

        return (fileCreated && bitmapCompressed && streamClosed);
    }

    // Getters

    /**
     * Get int value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key SharedPreferences key
     * @return int value at 'key' or 'defaultValue' if key not found
     */
    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    /**
     * Get parsed ArrayList of Integers from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of Integers
     */
    public ArrayList<Integer> getListInt(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Integer> newList = new ArrayList<Integer>();

        for (String item : arrayToList)
            newList.add(Integer.parseInt(item));

        return newList;
    }

    /**
     * Get long value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key SharedPreferences key
     * @param defaultValue long value returned if key was not found
     * @return long value at 'key' or 'defaultValue' if key not found
     */
    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    /**
     * Get float value from SharedPreferences at 'key'. If key not found, return 'defaultValue'
     *
     * @param key SharedPreferences key
     * @return float value at 'key' or 'defaultValue' if key not found
     */
    public float getFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    /**
     * Get double value from SharedPreferences at 'key'. If exception thrown, return 'defaultValue'
     *
     * @param key SharedPreferences key
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
     * Get parsed ArrayList of Double from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of Double
     */
    public ArrayList<Double> getListDouble(String key) {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Double> newList = new ArrayList<Double>();

        for (String item : arrayToList)
            newList.add(Double.parseDouble(item));

        return newList;
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

    public String getString(String key, String defaultString) {
        return preferences.getString(key, defaultString);
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

    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    /**
     * Get parsed ArrayList of Boolean from SharedPreferences at 'key'
     *
     * @param key SharedPreferences key
     * @return ArrayList of Boolean
     */
    public ArrayList<Boolean> getListBoolean(String key) {
        ArrayList<String> myList = getListString(key);
        ArrayList<Boolean> newList = new ArrayList<Boolean>();

        for (String item : myList) {
            if (item.equals("true")) {
                newList.add(true);
            } else {
                newList.add(false);
            }
        }

        return newList;
    }

    public ArrayList<Object> getListObject(String key, Class<?> mClass) {
        Gson gson = new Gson();

        ArrayList<String> objStrings = getListString(key);
        ArrayList<Object> objects = new ArrayList<Object>();

        for (String jObjString : objStrings) {
            Object value = gson.fromJson(jObjString, mClass);
            objects.add(value);
        }
        return objects;
    }

    public Object getObject(String key, Class<?> classOfT) {

        String json = getString(key);
        Object value = new Gson().fromJson(json, classOfT);
//        if (value == null) throw new NullPointerException();
        return value;
    }

    // Put methods

    /**
     * Put int value into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param value int value to be added
     */
    public void putInt(String key, int value) {
        checkForNullKey(key);
        preferences.edit().putInt(key, value).apply();
    }

    /**
     * Put ArrayList of Integer into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param intList ArrayList of Integer to be added
     */
    public void putListInt(String key, ArrayList<Integer> intList) {
        checkForNullKey(key);
        Integer[] myIntList = intList.toArray(new Integer[intList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply();
    }

    /**
     * Put long value into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param value long value to be added
     */
    public void putLong(String key, long value) {
        checkForNullKey(key);
        preferences.edit().putLong(key, value).apply();
    }

    /**
     * Put float value into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param value float value to be added
     */
    public void putFloat(String key, float value) {
        checkForNullKey(key);
        preferences.edit().putFloat(key, value).apply();
    }

    /**
     * Put double value into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param value double value to be added
     */
    public void putDouble(String key, double value) {
        checkForNullKey(key);
        putString(key, String.valueOf(value));
    }

    /**
     * Put ArrayList of Double into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param doubleList ArrayList of Double to be added
     */
    public void putListDouble(String key, ArrayList<Double> doubleList) {
        checkForNullKey(key);
        Double[] myDoubleList = doubleList.toArray(new Double[doubleList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myDoubleList)).apply();
    }

    /**
     * Put String value into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
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
     * @param key SharedPreferences key
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
     * @param key SharedPreferences key
     * @param value boolean value to be added
     */
    public void putBoolean(String key, boolean value) {
        checkForNullKey(key);
        preferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Put ArrayList of Boolean into SharedPreferences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param boolList ArrayList of Boolean to be added
     */
    public void putListBoolean(String key, ArrayList<Boolean> boolList) {
        checkForNullKey(key);
        ArrayList<String> newList = new ArrayList<String>();

        for (Boolean item : boolList) {
            if (item) {
                newList.add("true");
            } else {
                newList.add("false");
            }
        }

        putListString(key, newList);
    }

    /**
     * Put ObJect any type into SharedPrefrences with 'key' and save
     *
     * @param key SharedPreferences key
     * @param obj is the Object you want to put
     */
    public void putObject(String key, Object obj) {
        checkForNullKey(key);
        Gson gson = new Gson();
        putString(key, gson.toJson(obj));
    }

    public void putListImageGallery(String key, ArrayList<ParcelableImageModel> objArray) {
        checkForNullKey(key);
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (Object obj : objArray) {
            objStrings.add(gson.toJson(obj));
        }
        putListString(key, objStrings);
    }

    public ArrayList<ParcelableImageModel> getListParcelableImageModel(String key) {
        Gson gson = new Gson();

        ArrayList<String> objStrings = getListString(key);
        ArrayList<ParcelableImageModel> objects = new ArrayList<ParcelableImageModel>();

        for (String jObjString : objStrings) {
            ParcelableImageModel value = gson.fromJson(jObjString, ParcelableImageModel.class);
            objects.add(value);
        }
        return objects;
    }

    public void putListObject(String key, ArrayList<Object> objArray) {
        checkForNullKey(key);
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (Object obj : objArray) {
            objStrings.add(gson.toJson(obj));
        }
        putListString(key, objStrings);
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
     * Delete image file at 'path'
     *
     * @param path path of image file
     * @return true if it successfully deleted, false otherwise
     */
    public boolean deleteImage(String path) {
        return new File(path).delete();
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
     * Register SharedPreferences change listener
     *
     * @param listener listener object of OnSharedPreferenceChangeListener
     */
    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregister SharedPreferences change listener
     *
     * @param listener listener object of OnSharedPreferenceChangeListener to be unregistered
     */
    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {

        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Check if external storage is writable or not
     *
     * @return true if writable, false otherwise
     */
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Check if external storage is readable or not
     *
     * @return true if readable, false otherwise
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(
                state);
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

    ///////////////////////////////////////////////////////////////////////////////////////////
    public static final String IS_LOGIN_BY_FACEBOOK="IS_LOGIN_BY_FACEBOOK";//login bang email hoac facebook
    public static final String USER_NAME="USER_NAME";
    public static final String USER_PASSWORD="USER_PASSWORD";
    public static final String USER_PHONE = "USER_PHONE";

    public static final String USER_EMAIL="USER_EMAIL";
    public static final String FACEBOOK_ID="FACEBOOK_ID";
    public static final String FACEBOOK_PASSWORD="FACEBOOK_PASSWORD";
    public static final String IS_LOGINED="IS_LOGINED";
}
