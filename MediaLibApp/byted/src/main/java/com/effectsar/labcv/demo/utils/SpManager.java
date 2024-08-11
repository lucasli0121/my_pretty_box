package com.effectsar.labcv.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpManager {

    private final Context context;
    private final String spName;

    private SpManager(Context context, String spName) {
        this.context = context;
        this.spName = spName;
    }

    public static SpManager of(Context context, String name) {
        return new SpManager(context, name);
    }
    public void putString(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, def);
    }

    public void putInt(String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int def) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, def);
    }

}
