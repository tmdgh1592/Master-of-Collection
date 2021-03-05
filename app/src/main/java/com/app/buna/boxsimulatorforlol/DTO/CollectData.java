package com.app.buna.boxsimulatorforlol.DTO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.buna.boxsimulatorforlol.DB.DBHelper;

import java.util.HashMap;
import java.util.Map;

public class CollectData {

    private String name, imgFileName, url;

    public CollectData(){

    }

    public CollectData(String name, String imgFileName, String url){
        this.name = name;
        this.imgFileName = imgFileName;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgFileName() {
        return imgFileName;
    }

    public void setImgFileName(String imgFileName) {
        this.imgFileName = imgFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public static Map<String, CollectData> getChampCollectData(Context context){
        Map<String, CollectData> data = new HashMap<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM champ_table", null);
        int i=0;
        while(cursor.moveToNext()){
            CollectData collectData = new CollectData(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            data.put(Integer.toString(i++)+"_key", collectData);
        }
        db.close();
        return data;
    }

    public static Map<String, CollectData> getSkinCollectData(Context context){
        Map<String, CollectData> data = new HashMap<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM skin_table", null);
        int i=0;
        while(cursor.moveToNext()){
            CollectData collectData = new CollectData(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            data.put(Integer.toString(i++)+"_key", collectData);
        }
        db.close();
        return data;
    }
}
