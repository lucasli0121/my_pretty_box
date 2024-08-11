package com.effectsar.labcv.common.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class LocalParamHelper extends SQLiteOpenHelper {

    public String TABLE_NAME;

    public static final String CATEGORY_COMPOSER_NODE = "composer_node";
    public static final String CATEGORY_FILTER = "filter";
    public static final String CATEGORY_STICKER = "sticker";
    public static final String CATEGORY_MSG = "msg";


    public LocalParamHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized void saveComposerNode(SQLiteDatabase db, String path, String key, float intensity, int range, int selectColorIndex, boolean selected, boolean effect) {
        ContentValues values = new ContentValues();
        values.put("category", CATEGORY_COMPOSER_NODE);
        values.put("path", path);
        values.put("key", key);
        values.put("intensity", intensity);
        values.put("arg0", range);
        values.put("arg1", selectColorIndex);
        values.put("selected", selected ? 1 : 0 );
        values.put("effect", effect ? 1 : 0 );

        Cursor cursor = db.query(TABLE_NAME, null, "category=? and path=? and key=?", new String[]{CATEGORY_COMPOSER_NODE, path, key}, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            values.put("id", id);
            db.replace(TABLE_NAME, null, values);
        } else {
            db.replaceOrThrow(TABLE_NAME, null, values);
        }
    }

    public synchronized void updateComposerNode(SQLiteDatabase db, String path, String key, float intensity, int range, int selectColorIndex, boolean selected, boolean effect) {
            ContentValues values = new ContentValues();
            values.put("category", CATEGORY_COMPOSER_NODE);
            values.put("path", path);
            values.put("key", key);
            values.put("intensity", intensity);
            values.put("arg0", range);
            values.put("arg1", selectColorIndex);
            values.put("selected", selected ? 1 : 0 );
            values.put("effect", effect ? 1 : 0 );
            db.update(TABLE_NAME, values, "category=? and path=? and key=?", new String[]{CATEGORY_COMPOSER_NODE, path, key});
    }

    public synchronized void updateFilter(SQLiteDatabase db, String path, float intensity, boolean selected, boolean effect) {
        ContentValues values = new ContentValues();
        values.put("category", CATEGORY_FILTER);
        values.put("path", path);
        values.put("intensity", intensity);
        values.put("selected", selected ? 1 : 0 );
        values.put("effect", effect ? 1 : 0 );

        Cursor cursor = db.query(TABLE_NAME, null, "category=? and path=?", new String[]{CATEGORY_FILTER, path}, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            values.put("id", id);
            db.replace(TABLE_NAME, null, values);
        } else {
            db.replaceOrThrow(TABLE_NAME, null, values);
        }
    }


    public ArrayList<LocalParam> queryLocalParam(SQLiteDatabase db, String path, String key){
        ArrayList<LocalParam> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, "path=? and key=?", new String[]{path, key}, null, null, null);
            while (cursor.moveToNext()) {
                list.add(new LocalParam(
                        cursor.getString(cursor.getColumnIndex("category")),
                        cursor.getString(cursor.getColumnIndex("path")),
                        cursor.getString(cursor.getColumnIndex("key")),
                        cursor.getFloat(cursor.getColumnIndex("intensity")),
                        cursor.getInt(cursor.getColumnIndex("arg0")),
                        cursor.getLong(cursor.getColumnIndex("arg1")),
                        cursor.getLong(cursor.getColumnIndex("arg2")),
                        cursor.getString(cursor.getColumnIndex("arg3")),
                        ( cursor.getInt(cursor.getColumnIndex("selected")) > 0 ),
                        ( cursor.getInt(cursor.getColumnIndex("effect")) > 0 )
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<LocalParam> queryLocalParam(SQLiteDatabase db, String path){
        ArrayList<LocalParam> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, "path=?", new String[]{path}, null, null, null);
            while (cursor.moveToNext()) {
                list.add(new LocalParam(
                        cursor.getString(cursor.getColumnIndex("category")),
                        cursor.getString(cursor.getColumnIndex("path")),
                        cursor.getString(cursor.getColumnIndex("key")),
                        cursor.getFloat(cursor.getColumnIndex("intensity")),
                        cursor.getInt(cursor.getColumnIndex("arg0")),
                        cursor.getLong(cursor.getColumnIndex("arg1")),
                        cursor.getLong(cursor.getColumnIndex("arg2")),
                        cursor.getString(cursor.getColumnIndex("arg3")),
                        ( cursor.getInt(cursor.getColumnIndex("selected")) > 0 ),
                        ( cursor.getInt(cursor.getColumnIndex("effect")) > 0 )
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }

    public ArrayList<LocalParam> queryAll(SQLiteDatabase db){
        ArrayList<LocalParam> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                list.add(new LocalParam(
                        cursor.getString(cursor.getColumnIndex("category")),
                        cursor.getString(cursor.getColumnIndex("path")),
                        cursor.getString(cursor.getColumnIndex("key")),
                        cursor.getFloat(cursor.getColumnIndex("intensity")),
                        cursor.getInt(cursor.getColumnIndex("arg0")),
                        cursor.getLong(cursor.getColumnIndex("arg1")),
                        cursor.getLong(cursor.getColumnIndex("arg2")),
                        cursor.getString(cursor.getColumnIndex("arg3")),
                        ( cursor.getInt(cursor.getColumnIndex("selected")) > 0 ),
                        ( cursor.getInt(cursor.getColumnIndex("effect")) > 0 )
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (list.size() > 0) {
            return list;
        } else {
            return null;
        }

    }


    public float queryComposerNodeIntensity(SQLiteDatabase db, String path, String key) {
        Cursor cursor = db.query(TABLE_NAME, null, "category=? and path=? and key=?", new String[]{CATEGORY_COMPOSER_NODE, path,key}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getFloat(cursor.getColumnIndex("intensity"));
        } else {
            return 0.0f;
        }
    }

    public float queryFilterIntensity(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, "category=?", new String[]{CATEGORY_FILTER}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getFloat(cursor.getColumnIndex("intensity"));
        } else {
            return 0.0f;
        }
    }

    public String queryFilterPath(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, "category=?", new String[]{CATEGORY_FILTER}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("path"));
        } else {
            return "";
        }
    }

    public synchronized void deleteItem(SQLiteDatabase db, String path) {
        db.delete(TABLE_NAME, "path=?", new String[]{path});
    }

    public synchronized void removeAll(SQLiteDatabase db) {
        db.execSQL("update sqlite_sequence set seq=0 where name=(?)", new Object[]{TABLE_NAME});
        db.execSQL("delete from "+TABLE_NAME);
    }

    public class LocalParam{
        public String category;
        public String path;
        public String key;
        public float intensity;
        public int arg0;
        public long arg1;
        public long arg2;
        public String arg3;
        public boolean selected;
        public boolean effect;

        public LocalParam(
                String category,
                String path,
                String key,
                float intensity,
                int arg0,
                long arg1,
                long arg2,
                String arg3,
                boolean selected,
                boolean effect
        ){
            this.category = category;
            this.path = path;
            this.key = key;
            this.intensity = intensity;
            this.arg0 = arg0;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.selected = selected;
            this.effect = effect;
        }

    }

    public void setTableName(SQLiteDatabase db, String tableName){
        TABLE_NAME = tableName;
        db.execSQL("create table if not exists " + TABLE_NAME + " ("
                + "id integer primary key autoincrement, "
                + "category text, "
                + "path text, "
                + "`key` text, "
                + "intensity real, "
                + "arg0 integer, "
                + "arg1 integer, "
                + "arg2 integer, "
                + "arg3 text, "
                + "selected integer, "
                + "effect integer)");
    }
}
