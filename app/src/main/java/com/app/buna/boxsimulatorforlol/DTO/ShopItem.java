package com.app.buna.boxsimulatorforlol.DTO;

public class ShopItem {

    private int itemCode;
    private String itemName, itemDesc;
    private int itemPrice;
    private int itemType;   /*** 0 : 골드(gold), 1 : 파랑정수, 2 : 노랑정수,      -1 : title ***/
    private int itemImage;
    private int boughtCount;


    public ShopItem(int itemCode, String itemName, String itemDesc, int itemPrice, int itemType, int itemImage, int boughtCount){
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemPrice = itemPrice;
        this.itemType = itemType;
        this.itemImage = itemImage;
        this.boughtCount = boughtCount;
    }

    public int getBoughtCount() {
        return boughtCount;
    }

    public void setBoughtCount(int boughtCount) {
        this.boughtCount = boughtCount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getItemImage() {
        return itemImage;
    }

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }
}
