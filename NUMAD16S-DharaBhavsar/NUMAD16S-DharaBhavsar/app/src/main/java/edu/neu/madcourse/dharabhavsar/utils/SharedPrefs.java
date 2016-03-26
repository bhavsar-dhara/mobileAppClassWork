package edu.neu.madcourse.dharabhavsar.utils;

import android.util.Log;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author dharam
 */
public class SharedPrefs {

    private static final String TAG = "SharedPrefs";
    private static final String SharedPrefsName = "<YOUR_SHARED_PREFS_NAME>";

    public static void setInt(String key, int intValue, Context mContext) {

        Log.i(TAG, "Saving Int - Key - " + key + " - Value - " + intValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putInt(key, intValue);
        mEditor.apply();
    }

    public static int getInt(String key, int defaultValue, Context mContext) {
        Log.i(TAG, "Getting Int - Key - " + key + " - DefaultValue - " + defaultValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName,
                        Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void setString(String key, String stringValue,
                                 Context mContext) {
        Log.i(TAG, "Saving String - Key - " + key + " - Value - " + stringValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putString(key, stringValue);
        mEditor.apply();
    }

    public static String getString(String key, String defaultValue,
                                   Context mContext) {
        Log.i(TAG, "Getting String - Key - " + key + " - DefaultValue - " + defaultValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName,
                        Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void setBoolean(String key, Boolean boolValue,
                                  Context mContext) {
        Log.i(TAG, "Saving Boolean - Key - " + key + " - Value - " + boolValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putBoolean(key, boolValue);
        mEditor.apply();
    }

    public static Boolean getBoolean(String key, Boolean defaultValue,
                                     Context mContext) {
        Log.i(TAG, "Getting Boolean - Key - " + key + " - DefaultValue - " + defaultValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName,
                        Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void setLong(String key, long longValue, Context mContext) {
        Log.i(TAG, "Saving Long - Key - " + key + " - Value - " + longValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.putLong(key, longValue);
        mEditor.apply();
    }

    public static long getLong(String key, long defaultValue, Context mContext) {
        Log.i(TAG, "Getting Boolean - Key - " + key + " - DefaultValue - " + defaultValue);
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName,
                        Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void remove(String key, Context mContext) {

        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        mEditor.remove(key);
        mEditor.apply();
    }

    public static void clearAll(Context mContext) {
        SharedPreferences sharedPreferences = mContext
                .getSharedPreferences(SharedPrefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = sharedPreferences.edit();
        Map<String, ?> allSharedPrefs = sharedPreferences.getAll();
        Set<String> allKeys = allSharedPrefs.keySet();
        Iterator<String> iterator = allKeys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            System.out.println("Removing Key = " + key);
            mEditor.remove(key);
        }
        mEditor.apply();
    }
}