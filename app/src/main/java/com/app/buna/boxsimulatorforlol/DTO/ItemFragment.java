package com.app.buna.boxsimulatorforlol.DTO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.buna.boxsimulatorforlol.DB.DBHelper;
import com.app.buna.boxsimulatorforlol.VO.FragType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemFragment {

    private String itemName, champName, skinName;
    private String champNameAndNum; // only skin identifier
    private int fragType, itemCode, itemCount, itemResId;
    private int buyBlueGem, sellBlueGem, buyYellowGem, sellYellowGem;
    private boolean isItem;


    /*title*/
    public ItemFragment(boolean isItem, String itemName){
        this.isItem = isItem;
        this.itemName = itemName;
    }

    /*box and key*/
    public ItemFragment(boolean isItem, int fragType, int itemCode, String itemName, int itemCount, int itemResId) {
        this.fragType = fragType;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemCount = itemCount;
        this.itemResId = itemResId;
        this.isItem = isItem;
    }

    /*champion fragment*/
    public ItemFragment(boolean isItem, int fragType, String champName, int buyBlueGem, int sellBlueGem, int itemCount){
        this.isItem = isItem;
        this.fragType = fragType;
        this.champName = champName;
        this.buyBlueGem = buyBlueGem;
        this.sellBlueGem = sellBlueGem;
        this.itemCount = itemCount;
    }

    /*skin fragment*/
    public ItemFragment(boolean isItem, int fragType, String champNameAndNum, String skinName, int buyYellowGem, int sellYellowGem, int itemCount){
        this.isItem = isItem;
        this.fragType = fragType;
        this.champNameAndNum = champNameAndNum;
        this.skinName = skinName;
        this.buyYellowGem = buyYellowGem;
        this.sellYellowGem = sellYellowGem;
        this.itemCount = itemCount;
    }

    public static Map<String, ItemFragment> getChampFragData(Context context){
        Map<String, ItemFragment> dataList = new HashMap<>();

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM champ_frag_table WHERE count>?", new String[]{"0"});
        int i=0;
        while(cursor.moveToNext()){
            ItemFragment itemFragment = new ItemFragment();

            itemFragment.setItem(true);
            itemFragment.setFragType(FragType.FRAG_CHAMPION);
            itemFragment.setChampName(cursor.getString(0));
            itemFragment.setChampNameAndNum(cursor.getString(1));
            itemFragment.setBuyBlueGem(cursor.getInt(2));
            itemFragment.setSellBlueGem(cursor.getInt(3));
            itemFragment.setItemCount(cursor.getInt(4));

            dataList.put(Integer.toString(i++)+"_key", itemFragment);
        }
        dbHelper.close();
        db.close();
        return dataList;
    }

    public static Map<String, ItemFragment> getSkinFragData(Context context){
        Map<String, ItemFragment> dataList = new HashMap<>();

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM skin_frag_table WHERE count>0", null);
        int i=0;
        while(cursor.moveToNext()){
            ItemFragment itemFragment = new ItemFragment(
                    true,
                    FragType.FRAG_SKIN,
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4)
            );

            dataList.put(Integer.toString(i++)+"_key", itemFragment);
        }
        dbHelper.close();
        db.close();
        return dataList;
    }

    public ItemFragment() {

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getChampName() {
        return champName;
    }

    public void setChampName(String champName) {
        this.champName = champName;
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public String getChampNameAndNum() {
        return champNameAndNum;
    }

    public void setChampNameAndNum(String champNameAndNum) {
        this.champNameAndNum = champNameAndNum;
    }

    public int getFragType() {
        return fragType;
    }

    public void setFragType(int fragType) {
        this.fragType = fragType;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getItemResId() {
        return itemResId;
    }

    public void setItemResId(int itemResId) {
        this.itemResId = itemResId;
    }

    public int getBuyBlueGem() {
        return buyBlueGem;
    }

    public void setBuyBlueGem(int buyBlueGem) {
        this.buyBlueGem = buyBlueGem;
    }

    public int getSellBlueGem() {
        return sellBlueGem;
    }

    public void setSellBlueGem(int sellBlueGem) {
        this.sellBlueGem = sellBlueGem;
    }

    public int getBuyYellowGem() {
        return buyYellowGem;
    }

    public void setBuyYellowGem(int buyYellowGem) {
        this.buyYellowGem = buyYellowGem;
    }

    public int getSellYellowGem() {
        return sellYellowGem;
    }

    public void setSellYellowGem(int sellYellowGem) {
        this.sellYellowGem = sellYellowGem;
    }

    public boolean isItem() {
        return isItem;
    }

    public void setItem(boolean item) {
        isItem = item;
    }
}
