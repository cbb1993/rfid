package com.app.rfidmaster.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.rfidmaster.App;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesUtils {
    private static SharedPreferences mSharedPreferences;

    /**
     * @param key   本地数据对应的key
     * @param value 本地数据对应的value
     */
    public static void addData(String key, String value) {
        init();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void addData(String key, int value) {
        init();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void addData(String key, boolean value) {
        init();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void addStringSetData(String key, Set<String> value) {
        init();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    private static void init() {
        if (mSharedPreferences == null) {
            mSharedPreferences = App.getContext().getSharedPreferences(App.getContext().getPackageName(), Context.MODE_PRIVATE);
        }
    }

    public static Set<String> readStringSerData(String key) {
        init();
        return mSharedPreferences.getStringSet(key, new HashSet<String>());
    }

    public static int readIntData(String key) {
        init();
        return mSharedPreferences.getInt(key, 1);
    }

    public static String readData(String key) {
        return readData(key, "");
    }

    public static boolean readBooleanData(String key, Boolean defaultStr) {
        init();

        return mSharedPreferences.getBoolean(key, defaultStr);
    }

    public static String readData(String key, String defaultStr) {
        init();

        return mSharedPreferences.getString(key, defaultStr);
    }

    public static SharedPreferences getInstance() {
        init();

        return mSharedPreferences;
    }

    public static void clearShared() {
        init();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
