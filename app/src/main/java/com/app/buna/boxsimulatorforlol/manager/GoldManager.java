package com.app.buna.boxsimulatorforlol.manager;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class GoldManager {

    private Context context;
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public GoldManager(Context context) {
        this.context = context;
        setting = context.getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
    }

    public float getGoldPerClick() {
        //return setting.getFloat("goldPerClick", 1000000); // 운영자 테스트용
        return setting.getFloat("goldPerClick", 1);
    }

    public void setGoldPerClick(float goldPerClick) {
        editor.putFloat("goldPerClick", goldPerClick);
        editor.commit();
    }

    public float getGoldPerSec() {
        return setting.getFloat("goldPerSec", 0);
    }

    public void setGoldPerSec(float goldPerSec) {
        editor.putFloat("goldPerSec", goldPerSec);
        editor.commit();
    }

    public void setGold(float gold){
        editor.putFloat("gold", gold);
        editor.commit();
    }

    public float getGold(){
        return setting.getFloat("gold", 0);
    }

    public void addGold(float gold) {
        float totalGold = getGold() + gold;
        editor.putFloat("gold", totalGold);
        editor.commit();
    }


}
