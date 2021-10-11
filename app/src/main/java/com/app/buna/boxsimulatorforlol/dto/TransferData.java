package com.app.buna.boxsimulatorforlol.dto;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class TransferData {

    private float gold, goldPerClick, goldPerSec;
    private int blueGem, yellowGem;
    private int box, key, rewardItem;
    private int boxBoughtCount, keyBoughtCount;
    private float bluegemChance, yellowgemChance;
    private Map<String, ItemFragment> champFragData, skinFragData;
    private Map<String, CollectData> champData, skinData;
    private Map<String, Integer> skillLevel;
    //private String AID;


    public TransferData(){

    }

    public TransferData(float gold, float goldPerClick, float goldPerSec
            , int blueGem, int yellowGem, int box, int key, int rewardItem
            , int boxBoughtCount, int keyBoughtCount, float bluegemChance, float yellowgemChance
            , Map<String, ItemFragment> champFragData, Map<String, ItemFragment> skinFragData
            , Map<String, CollectData> champData, Map<String, CollectData> skinData
            , Map<String, Integer> skillLevel){
        this.gold = gold;
        this.goldPerClick = goldPerClick;
        this.goldPerSec = goldPerSec;
        this.blueGem = blueGem;
        this.yellowGem = yellowGem;
        this.box = box;
        this.key = key;
        this.rewardItem = rewardItem;
        this.boxBoughtCount = boxBoughtCount;
        this.keyBoughtCount = keyBoughtCount;
        this.yellowgemChance = yellowgemChance;
        this.bluegemChance = bluegemChance;
        this.champFragData = champFragData;
        this.skinFragData = skinFragData;
        this.champData = champData;
        this.skinData = skinData;
        this.skillLevel = skillLevel;
    }

    /*public TransferData(float gold, float goldPerClick, float goldPerSec
    , int blueGem, int yellowGem, int box, int key, int rewardItem
    , int boxBoughtCount, int keyBoughtCount, float bluegemChance, float yellowgemChance
    , Map<String, ItemFragment> champFragData, Map<String, ItemFragment> skinFragData
    , Map<String, CollectData> champData, Map<String, CollectData> skinData
    , Map<String, Integer> skillLevel, String AID){
        this.gold = gold;
        this.goldPerClick = goldPerClick;
        this.goldPerSec = goldPerSec;
        this.blueGem = blueGem;
        this.yellowGem = yellowGem;
        this.box = box;
        this.key = key;
        this.rewardItem = rewardItem;
        this.boxBoughtCount = boxBoughtCount;
        this.keyBoughtCount = keyBoughtCount;
        this.yellowgemChance = yellowgemChance;
        this.bluegemChance = bluegemChance;
        this.champFragData = champFragData;
        this.skinFragData = skinFragData;
        this.champData = champData;
        this.skinData = skinData;
        this.skillLevel = skillLevel;
        this.AID = AID;
    }*/


    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("gold", gold);
        result.put("goldPerClick", goldPerClick);
        result.put("goldPerSec", goldPerSec);
        result.put("blueGem", blueGem);
        result.put("yellowGem", yellowGem);
        result.put("box", box);
        result.put("key", key);
        result.put("rewardItem", rewardItem);
        result.put("boxBoughtCount", boxBoughtCount);
        result.put("keyBoughtCount", keyBoughtCount);
        result.put("champFragData", champFragData);
        result.put("skinFragData", skinFragData);
        result.put("champData", champData);
        result.put("skinData", skinData);
        result.put("skillLevel", skillLevel);
    //    result.put("AID", AID);

        return result;
    }

    public float getGold() {
        return gold;
    }

    public void setGold(float gold) {
        this.gold = gold;
    }

    public float getGoldPerClick() {
        return goldPerClick;
    }

    public void setGoldPerClick(float goldPerClick) {
        this.goldPerClick = goldPerClick;
    }

    public float getGoldPerSec() {
        return goldPerSec;
    }

    public void setGoldPerSec(float goldPerSec) {
        this.goldPerSec = goldPerSec;
    }

    public int getBlueGem() {
        return blueGem;
    }

    public void setBlueGem(int blueGem) {
        this.blueGem = blueGem;
    }

    public int getYellowGem() {
        return yellowGem;
    }

    public void setYellowGem(int yellowGem) {
        this.yellowGem = yellowGem;
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getRewardItem() {
        return rewardItem;
    }

    public void setRewardItem(int rewardItem) {
        this.rewardItem = rewardItem;
    }

    public int getBoxBoughtCount() {
        return boxBoughtCount;
    }

    public void setBoxBoughtCount(int boxBoughtCount) {
        this.boxBoughtCount = boxBoughtCount;
    }

    public int getKeyBoughtCount() {
        return keyBoughtCount;
    }

    public void setKeyBoughtCount(int keyBoughtCount) {
        this.keyBoughtCount = keyBoughtCount;
    }

    public float getBluegemChance() {
        return bluegemChance;
    }

    public void setBluegemChance(float bluegemChance) {
        this.bluegemChance = bluegemChance;
    }

    public float getYellowgemChance() {
        return yellowgemChance;
    }

    public void setYellowgemChance(float yellowgemChance) {
        this.yellowgemChance = yellowgemChance;
    }

    public Map<String, ItemFragment> getChampFragData() {
        return champFragData;
    }

    public void setChampFragData(Map<String, ItemFragment> champFragData) {
        this.champFragData = champFragData;
    }

    public Map<String, ItemFragment> getSkinFragData() {
        return skinFragData;
    }

    public void setSkinFragData(Map<String, ItemFragment> skinFragData) {
        this.skinFragData = skinFragData;
    }

    public Map<String, CollectData> getChampData() {
        return champData;
    }

    public void setChampData(Map<String, CollectData> champData) {
        this.champData = champData;
    }

    public Map<String, CollectData> getSkinData() {
        return skinData;
    }

    public void setSkinData(Map<String, CollectData> skinData) {
        this.skinData = skinData;
    }

    public Map<String, Integer> getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Map<String, Integer> skillLevel) {
        this.skillLevel = skillLevel;
    }


    /*public String getAID() {
        return AID;
    }

    public void setAID(String AID) {
        this.AID = AID;
    }*/
}
