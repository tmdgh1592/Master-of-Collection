package com.app.buna.boxsimulatorforlol.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.Activity.MainActivity;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.GameToast;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.app.buna.boxsimulatorforlol.VO.TierCondition;
import com.app.buna.boxsimulatorforlol.VO.UgrType;

import java.math.BigDecimal;


public class UpgradeManager {

    private Context context;
    private GoldManager goldManager;
    private ItemManager itemManager;
    private SoundManager soundManager;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private MainActivity mainActivity;

    private int ugrSuccessSoundId, ugrFailedSoundId;

    public UpgradeManager(Context context, MainActivity mainActivity) {
        LangUtil.setLang(context);
        this.context = context;
        this.mainActivity = mainActivity;
        goldManager = new GoldManager(context);
        itemManager = new ItemManager(context);
        soundManager = new SoundManager(context);
        soundManager.init();
        ugrSuccessSoundId = soundManager.loadSound(SoundManager.TYPE_BUY_ITEM);
        ugrFailedSoundId = soundManager.loadSound(SoundManager.TYPE_BUY_ITEM);  // 실패했을 때 사운드로 바꿔야함
        setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = setting.edit();
    }

    public void upgrade(String upgradeName, int type, int nowUpgradePrice, float nowSkillEffect,
                        int nowLevel, int maxLevel, float howMuchPrice, float howMuchEffect, String beforeUpgradeName, String beforeUpgradePrefName, int beforePosition) {

        float myGold = goldManager.getGold();

        if(!isBeforeSkillMaxLevelByCode(beforeUpgradePrefName, beforePosition)){
            new GameToast(context, "("+beforeUpgradeName +")" + context.getString(R.string.warning_disable_upgrade_bc_before_skill), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            soundManager.play(ugrFailedSoundId);
            return;
        }else if(myGold < nowUpgradePrice) {
            new GameToast(context, context.getString(R.string.warning_disable_upgrade_bc_money), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            soundManager.play(ugrFailedSoundId);
            return;
        }else if(nowLevel >= maxLevel){
            new GameToast(context, context.getString(R.string.warning_disable_upgrade_bc_max_upgrade), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            soundManager.play(ugrFailedSoundId);
        }else{
            /* 업그레이드 스킬 효과 레벨업 */
            soundManager.play(ugrSuccessSoundId);
            goldManager.setGold(goldManager.getGold() - nowUpgradePrice);
            editor.putFloat(upgradeName+"SkillEffect", getNextSkillEffect(nowSkillEffect, howMuchEffect));
            editor.putInt(upgradeName+"UpgradePrice", getNextUpgradePrice(nowUpgradePrice, howMuchPrice));
            editor.putInt(upgradeName+"NowSkillLevel", getNextSkillLevel(nowLevel));
            if(type == UgrType.GOLD_PER_CLICK){
                goldManager.setGoldPerClick(goldManager.getGoldPerClick() + howMuchEffect);
            }else if(type == UgrType.GOLD_PER_SECOND){
                goldManager.setGoldPerSec(goldManager.getGoldPerSec() + howMuchEffect);
            }else if(type == UgrType.GOLD_PER_BLUE_STEAL){
                itemManager.setBlueGemChance(itemManager.getBlueGemChance() + howMuchEffect);
            }else if(type == UgrType.GOLD_PER_YELLOW_STEAL){
                itemManager.setYellowGemChance(itemManager.getYellowGemChance() + howMuchEffect);
            }
            editor.commit();
            mainActivity.prefUpdate();
        }
    }


    /*스킬 효과 밸런스 잘 조정하기*/
    private float getNextSkillEffect(float nowEffectValue, float howMuchEffect){
        /*오차를 없애기 위해 BigDecimal 사용*/
        BigDecimal a = new BigDecimal(String.valueOf(nowEffectValue));
        BigDecimal b = new BigDecimal(String.valueOf(howMuchEffect));
        return a.add(b).floatValue();
    }

    // +1만 해주면 됌
    private int getNextSkillLevel(int nowSkillLevel) {
        return nowSkillLevel + 1;
    }

    /*스킬 가격 밸런스 잘 조정하기*/
    private int getNextUpgradePrice(int nowUpgradePrice, float howMuchPrice) {
        return (int)((float)nowUpgradePrice * howMuchPrice);
    }

    private boolean isBeforeSkillMaxLevelByCode(String beforeUpgradeName, int beforePosition){
        // 첫번째 스킬이 아니면서 이전 스킬을 모두 완료한 경우 (첫번째 스킬 제외하기 위함.)
        if(beforePosition == -1 || beforePosition == 2 || beforePosition == 5 || beforePosition == 6 || beforePosition == 7){   // beforeUpgradeName이 첫번째 스킬인 경우
            return true;
        }

        int beforeSkillNowLevel = setting.getInt(beforeUpgradeName+"NowSkillLevel", 0);
        int beforeSkillMaxLevel = context.getResources().getIntArray(R.array.max_skill_level)[beforePosition];

        if(beforeSkillNowLevel == beforeSkillMaxLevel){
            return true;
        }else{
            return false;
        }
    }

}
