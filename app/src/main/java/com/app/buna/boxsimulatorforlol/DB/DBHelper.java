package com.app.buna.boxsimulatorforlol.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DBName = "AppDB";

    public static final String champFragSql = "create table if not exists champ_frag_table (" +
            "champName text not null," +                                // 0
            "engChampName text not null," +                             // 1    소문자가 아닌 원본으로 저장, 사용할 때 toLowerCase
            "buyBlueGem Integer not null,"+                             // 2
            "sellBlueGem Integer not null," +                           // 3
            "count Integer not null default 0)";                        // 4

    public static final String skinFragSql = "create table if not exists skin_frag_table (" +
            "champNameAndNum text not null," +                          // 0    소문자가 아닌 원본으로 저장, 사용할 때 toLowerCase
            "skinName text not null," +                                 // 1
            "buyYellowGem Integer not null,"+                           // 2
            "sellYellowGem Integer not null," +                         // 3
            "count Integer not null default 0)";                        // 4

    public static final String champSql = "create table if not exists champ_table (" +
            "champName text not null," +                                // 0
            "imgFileName text not null," +                              // 1
            "imgUrl text not null)";                                    // 2

    public static final String skinSql = "create table if not exists skin_table (" +
            "skinName text not null," +                                 // 0
            "imgFileName text not null," +                              // 1
            "skinUrl text not null)";                                   // 2


    public DBHelper(Context context){
        super(context, DBName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //변경 시 DATABASE_VERSION up.
        db.execSQL(champFragSql);
        db.execSQL(skinFragSql);
        db.execSQL(champSql);
        db.execSQL(skinSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전이 변경될 때마다 실행, db table upgrade해줄 때
        if(newVersion == DATABASE_VERSION) {
            dropAllData(db);
            onCreate(db);
        }
    }

    private void dropAllData(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS champ_frag_table");
        db.execSQL("DROP TABLE IF EXISTS skin_frag_table");
        db.execSQL("DROP TABLE IF EXISTS champ_table");
        db.execSQL("DROP TABLE IF EXISTS skin_table");
    }

    public void deleteAllData(SQLiteDatabase db){
        db.execSQL("DELETE FROM champ_frag_table");
        db.execSQL("DELETE FROM skin_frag_table");
        db.execSQL("DELETE FROM champ_table");
        db.execSQL("DELETE FROM skin_table");
    }

    public int getChampCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT champName FROM champ_table", null);
        int count = cursor.getCount();
        close();
        db.close();
        return count;
    }

    public int getSkinCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT skinName FROM skin_table", null);
        int count = cursor.getCount();
        close();
        db.close();
        return count;
    }
}
