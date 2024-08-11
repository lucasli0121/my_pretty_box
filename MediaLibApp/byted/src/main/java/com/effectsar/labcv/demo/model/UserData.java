package com.effectsar.labcv.demo.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserData {
    public static final String NAME = "user";
    public static final String VERSION = "versionCode";
    public static final String RESOURCE_READY = "resource";
    public static final String MODEL_DOWNLOADED = "modelDownloaded";
    public static final String IS_BOE = "isBoe";

    private static volatile UserData sInstance;
    private final SharedPreferences mSp;

    private UserData(Context context) {
        mSp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static UserData getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UserData.class) {
                if (sInstance == null) {
                    sInstance = new UserData(context);
                }
            }
        }
        return sInstance;
    }

    public int getVersion() {
        return mSp.getInt(VERSION, 0);
    }

    public void setVersion(int version) {
        mSp.edit().putInt(VERSION, version).apply();
    }

    public boolean isResourceReady() {
        return mSp.getBoolean(RESOURCE_READY, false);
    }

    public void setResourceReady(boolean ready) {
        mSp.edit().putBoolean(RESOURCE_READY, ready).apply();
    }

    public boolean isBoe() {
        return mSp.getBoolean(IS_BOE, false);
    }

    public void setBoe(boolean isBoe) {
        mSp.edit().putBoolean(IS_BOE, isBoe).apply();
    }

    public boolean hasModelDownloaded(){
        return mSp.getBoolean(MODEL_DOWNLOADED, false);
    }

    public void setModelDownloaded(boolean modelDownloaded){
        mSp.edit().putBoolean(MODEL_DOWNLOADED, modelDownloaded).apply();
    }
}
