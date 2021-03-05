package com.app.buna.boxsimulatorforlol.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.Activity.MainActivity;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.GameToast;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.app.buna.boxsimulatorforlol.VO.ItemCode;
import com.app.buna.boxsimulatorforlol.VO.ItemType;

public class ShopManager {

    private Context context;
    private MainActivity mainActivity;
    private GoldManager goldManager;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    ItemManager itemManager;

    public ShopManager(Context context, MainActivity mainActivity){
        LangUtil.setLang(context);
        this.context = context;
        this.mainActivity = mainActivity;
        goldManager = new GoldManager(context);
        itemManager = new ItemManager(context);
        setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = setting.edit();
    }

    public boolean buyItem(int itemCode, int itemType, int count, int itemPrice){
        if(itemType == ItemType.GOLD){
            if(goldManager.getGold() >= count * itemPrice) {
                goldManager.setGold(goldManager.getGold() - count * itemPrice);

                int totalCount = itemManager.getItemByCode(itemCode) * count;

                switch (itemCode){
                    /* when user buys box */
                    case ItemCode.BOX:
                        itemManager.addBoxCount(totalCount);
                        editor.putInt("boughtBoxCount", (setting.getInt("boughtBoxCount",0)+count));
                        editor.commit();
                        break;
                    case ItemCode.KEY:
                        itemManager.addKeyCount(totalCount);
                        editor.putInt("boughtKeyCount", (setting.getInt("boughtKeyCount",0)+count));
                        editor.commit();
                        break;
                    case ItemCode.BOX_AND_KEY_SET:
                        itemManager.addBoxCount(totalCount);
                        itemManager.addKeyCount(totalCount);
                        editor.putInt("boughtBoxAndKeyCount", (setting.getInt("boughtBoxAndKeyCount",0)+count));
                        editor.commit();
                        break;
                    default:
                        break;
                }
                mainActivity.refreshResource();
                mainActivity.updateShopPrice();
                new GameToast(context, context.getString(R.string.buy_comment, itemManager.getItemNameByCode(itemCode), Integer.toString(count)), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                return true;
            }else{
                new GameToast(context, context.getString(R.string.not_enough_gold), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if(itemType == ItemType.YELLOW_GEM){
            int yellowGemCount = itemManager.getYellowGemCount();

            if(yellowGemCount >= itemPrice * count) {
                itemManager.setYellowGemCount(yellowGemCount - itemPrice * count);

                int totalCount = itemManager.getItemByCode(itemCode) * count;

                itemManager.addBlueGemCount(totalCount);
                mainActivity.refreshResource();

                new GameToast(context, context.getString(R.string.buy_comment, itemManager.getItemNameByCode(itemCode), Integer.toString(count)), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                return true;
            }else{
                new GameToast(context, context.getString(R.string.not_enough_essence), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if(itemType == ItemType.BLUE_GEM){
            int blueGemCount = itemManager.getBlueGemCount();

            if(blueGemCount >= itemPrice * count) {
                itemManager.setBlueGemCount(blueGemCount - itemPrice * count);

                int totalCount = itemManager.getItemByCode(itemCode) * count;

                itemManager.addYellowGemCount(totalCount);
                mainActivity.refreshResource();
                new GameToast(context, context.getString(R.string.buy_comment, itemManager.getItemNameByCode(itemCode), Integer.toString(count)), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                return true;
            }else{
                new GameToast(context, context.getString(R.string.not_enough_essence), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            new GameToast(context, context.getString(R.string.unknown_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            return false;
        }
    }



}
