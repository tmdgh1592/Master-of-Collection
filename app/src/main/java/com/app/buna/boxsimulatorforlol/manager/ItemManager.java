package com.app.buna.boxsimulatorforlol.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.vo.ItemCode;

public class ItemManager {

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private Context context;

    public ItemManager(Context context){
        this.context = context;
        setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = setting.edit();
    }

    public int getGameRewardItemCount() {
        return setting.getInt("gameRewardItem", 0);
    }

    public void addGameRewardItem(int addRewardItemCount){
        editor.putInt("gameRewardItem", getGameRewardItemCount()+addRewardItemCount);
        editor.commit();
    }

    public void setGameRewardItemCount(int gameRewardItemCount) {
        editor.putInt("gameRewardItem", gameRewardItemCount);
        editor.commit();
    }

    public int getRewardItemCount(){
        return setting.getInt("rewardItem", 0);
    }

    public void addRewardItem(int addRewardItemCount){
        editor.putInt("rewardItem", getRewardItemCount()+addRewardItemCount);
        editor.commit();
    }

    public void setRewardItemCount(int rewardItemCount) {
        editor.putInt("rewardItem", rewardItemCount);
        editor.commit();
    }


    public int getBoxCount() {
        return setting.getInt("box", 0);
    }

    public void setBoxCount(int boxCount) {
        editor.putInt("box", boxCount);
        editor.commit();
    }

    public void addBoxCount(int addBoxCount) {
        editor.putInt("box", getBoxCount() + addBoxCount);
        editor.commit();
    }



    public int getKeyCount() {
        return setting.getInt("key", 0);
    }

    public void setKeyCount(int keyCount) {
        editor.putInt("key", keyCount);
        editor.commit();
    }

    public void addKeyCount(int addKeyCount) {
        editor.putInt("key", getKeyCount() + addKeyCount);
        editor.commit();
    }






    public int getYellowGemCount() {
        return setting.getInt("yellowGem", 0);
    }

    public void setYellowGemCount(int yellowGemCount) {
        editor.putInt("yellowGem", yellowGemCount);
        editor.commit();
    }

    public float getYellowGemChance() {
        return setting.getFloat("yellowGemChance", 0);
    }

    public void setYellowGemChance(float yellowGemChance) {
        editor.putFloat("yellowGemChance", yellowGemChance);
        editor.commit();
    }

    public void addYellowGemCount(int addYellowGemCount) {
        editor.putInt("yellowGem", getYellowGemCount() + addYellowGemCount);
        editor.commit();
    }




    public int getBlueGemCount() {
        return setting.getInt("blueGem", 0);
    }

    public void setBlueGemCount(int blueGemCount) {
        editor.putInt("blueGem", blueGemCount);
        editor.commit();
    }

    public float getBlueGemChance() {
        return setting.getFloat("blueGemChance", 0.0f);
    }

    public void setBlueGemChance(float blueGemChance) {
        editor.putFloat("blueGemChance", blueGemChance);
        editor.commit();
    }

    public void addBlueGemCount(int addBlueGemCount) {
        editor.putInt("blueGem", getBlueGemCount() + addBlueGemCount);
        editor.commit();
    }





    public int getItemByCode(int itemCode){
        switch (itemCode) {
            case ItemCode.BOX:
            case ItemCode.KEY:
            case ItemCode.GAME_SPEICAL_ITEM:
            case ItemCode.BOX_AND_KEY_SET:
                return 1;
            case ItemCode.BLUE_GEM:
            case ItemCode.YELLOW_GEM:
                return 10;
            default:
                return 0;
        }
    }

    /* 아이템 코드로 아이템명 찾기 */
    public String getItemNameByCode(int itemCode) {
        int idx=0;
        String[] itemNames = context.getResources().getStringArray(R.array.shop_item_name);
        int[] itemCodes = context.getResources().getIntArray(R.array.shop_item_code);

        for(; idx<itemCodes.length; idx++){
            if(itemCodes[idx] == itemCode){
                return itemNames[idx];
            }
        }

        return "(null)";

    }

}
