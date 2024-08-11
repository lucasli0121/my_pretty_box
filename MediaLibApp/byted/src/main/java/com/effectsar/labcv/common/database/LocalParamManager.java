package com.effectsar.labcv.common.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class LocalParamManager {
    private static Context mContext = null;
    private final LocalParamHelper mDatabaseHelper;
    SQLiteDatabase db;

    private volatile static LocalParamManager mDatabaseManager;

    private int mFrameRate = 30;

    private LocalParamManager(Context context) {
        mDatabaseHelper = new LocalParamHelper(context.getApplicationContext(), "EffectResConfig.db", null, 1);
        db = mDatabaseHelper.getWritableDatabase();
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static LocalParamManager getInstance() {
        if (mDatabaseManager == null) {
            if (mContext == null) {
                throw new IllegalStateException("must call init(Context) first");
            }
            synchronized (LocalParamManager.class) {
                if (mDatabaseManager == null) {
                    mDatabaseManager = new LocalParamManager(mContext);
                    mContext = null;
                }
            }
        }
        return mDatabaseManager;
    }

    public void setFrameRate(int mFrameRate) {
        this.mFrameRate = mFrameRate;
    }

    public int getFrameRate() {
        return mFrameRate;
    }

    public void saveComposerNode(String path, String key, float intensity, int range, int selectColorIndex, boolean selected, boolean effect){
        mDatabaseHelper.saveComposerNode(db, path, key, intensity, range, selectColorIndex, selected, effect);
    }

    public void removeComposerNode(String path) {
        mDatabaseHelper.deleteItem(db, path);
    }

    public void updateComposerNode(String path, String key, float intensity, int range, int selectColorIndex, boolean selected, boolean effect){
        mDatabaseHelper.updateComposerNode(db, path, key, intensity, range, selectColorIndex, selected, effect);
    }


    public void updateFilter(String path, float intensity, boolean selected, boolean effect){
        mDatabaseHelper.updateFilter(db, path, intensity, selected, effect);
    }
    public void reset() {
        mDatabaseHelper.removeAll(db);
    }

    public ArrayList<LocalParamHelper.LocalParam> queryLocalParam(String path, String key){
        return mDatabaseHelper.queryLocalParam(db, path, key);
    }

    public ArrayList<LocalParamHelper.LocalParam> queryLocalParam(String path){
        return mDatabaseHelper.queryLocalParam(db, path);
    }

    public ArrayList<LocalParamHelper.LocalParam> queryAll(){
        return mDatabaseHelper.queryAll(db);
    }

    public void setTableName(String tableName){
        mDatabaseHelper.setTableName(db, tableName);
    }

}
