package com.app.buna.boxsimulatorforlol.dto;

public class UpgradeData {

    private String name, prefName;
    private String itemDesc;
    private int nowSkillLevel, maxSkillLevel;
    private int iconResId;
    private float skillEffect;
    private int tierCondition;
    private int upgradePrice;
    private int upgradeType;
    private float howMuchUpgradePrice;
    private float howMuchUpgradeEffect;


    public UpgradeData(int upgradeType, String prefName, String name, String itemDesc, int nowSkillLevel, int maxSkillLevel, int iconResId,
                       float skillEffect, int upgradePrice, int tierCondition, float howMuchUpgradePrice, float howMuchUpgradeEffect) {
        this.upgradeType = upgradeType;
        this.name = name;
        this.prefName = prefName;
        this.itemDesc = itemDesc;
        this.nowSkillLevel = nowSkillLevel;
        this.maxSkillLevel = maxSkillLevel;
        this.iconResId = iconResId;
        this.skillEffect = skillEffect;
        this.upgradePrice = upgradePrice;
        this.tierCondition = tierCondition;
        this.howMuchUpgradePrice = howMuchUpgradePrice;
        this.howMuchUpgradeEffect = howMuchUpgradeEffect;
    }

    public UpgradeData(int upgradeType, String name, String itemDesc, int nowSkillLevel, int maxSkillLevel, int iconResId,
                       float skillEffect, int tierCondition) {
        this.upgradeType = upgradeType;
        this.name = name;
        this.itemDesc = itemDesc;
        this.nowSkillLevel = nowSkillLevel;
        this.maxSkillLevel = maxSkillLevel;
        this.iconResId = iconResId;
        this.skillEffect = skillEffect;
        this.tierCondition = tierCondition;
    }


    public String getPrefName() {
        return prefName;
    }

    public void setPrefName(String prefName) {
        this.prefName = prefName;
    }

    public int getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(int upgradeType) {
        this.upgradeType = upgradeType;
    }

    public int getUpgradePrice() {
        return upgradePrice;
    }

    public void setUpgradePrice(int upgradePrice) {
        this.upgradePrice = upgradePrice;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public int getTierCondition() {
        return tierCondition;
    }

    public void setTierCondition(int tierCondition) {
        this.tierCondition = tierCondition;
    }

    public int getNowSkillLevel() {
        return nowSkillLevel;
    }

    public void setNowSkillLevel(int nowSkillLevel) {
        this.nowSkillLevel = nowSkillLevel;
    }

    public int getMaxSkillLevel() {
        return maxSkillLevel;
    }

    public void setMaxSkillLevel(int maxSkillLevel) {
        this.maxSkillLevel = maxSkillLevel;
    }

    public float getSkillEffect() {
        return skillEffect;
    }

    public void setSkillEffect(float skillEffect) {
        this.skillEffect = skillEffect;
    }

    public float getHowMuchUpgradePrice() {
        return howMuchUpgradePrice;
    }

    public void setHowMuchUpgradePrice(float howMuchUpgradePrice) {
        this.howMuchUpgradePrice = howMuchUpgradePrice;
    }

    public float getHowMuchUpgradeEffect() {
        return howMuchUpgradeEffect;
    }

    public void setHowMuchUpgradeEffect(float howMuchUpgradeEffect) {
        this.howMuchUpgradeEffect = howMuchUpgradeEffect;
    }
}
