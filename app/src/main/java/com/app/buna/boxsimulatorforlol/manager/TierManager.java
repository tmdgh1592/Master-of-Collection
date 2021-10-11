package com.app.buna.boxsimulatorforlol.manager;

import android.content.Context;

import com.app.buna.boxsimulatorforlol.db.DBHelper;

public class TierManager {

    private Context context;

    public TierManager(Context context) {
        this.context = context;
    }

    public String getMyTier(double maxCount){
        DBHelper dbHelper = new DBHelper(context);
        double totalCount = dbHelper.getChampCount() + dbHelper.getSkinCount();
        //double totalCount = 1008;
        String myTier = null;

        /* 총 티어 종류 : 24개 */
        /* TIER            PERCENTAGE     */
        /* iron       0.01 0.02 0.03 0.04 */
        /* bronze     0.05 0.06 0.07 0.09 */
        /* silver     0.11 0.13 0.15 0.18 */
        /* gold       0.23 0.26 0.29 0.32 */
        /* platinum   0.36 0.40 0.44 0.48 */
        /* diamond    0.54 0.60 0.66 0.75 */
        /* master     0.82 */
        /* GMaster    0.89 */
        /* Challenger 0.97 */

        double percentage = totalCount / maxCount;

        if(percentage < 0.01){
            myTier = "Unranked";
        }else if(0.01 <= percentage && percentage < 0.02){
            myTier = "Iron 4";
        }
        else if(0.02 <= percentage && percentage < 0.03){
            myTier = "Iron 3";
        }
        else if(0.03 <= percentage && percentage < 0.04){
            myTier = "Iron 2";
        }
        else if(0.04 <= percentage && percentage < 0.05){
            myTier = "Iron 1";
        }
        else if(0.05 <= percentage && percentage < 0.06){
            myTier = "Bronze 4";
        }
        else if(0.06 <= percentage && percentage < 0.07){
            myTier = "Bronze 3";
        }
        else if(0.07 <= percentage && percentage < 0.09){
            myTier = "Bronze 2";
        }
        else if(0.09 <= percentage && percentage < 0.11){
            myTier = "Bronze 1";
        }
        else if(0.11 <= percentage && percentage < 0.13){
            myTier = "Silver 4";
        }
        else if(0.13 <= percentage && percentage < 0.15){
            myTier = "Silver 3";
        }
        else if(0.15 <= percentage && percentage < 0.18){
            myTier = "Silver 2";
        }
        else if(0.18 <= percentage && percentage < 0.23){
            myTier = "Silver 1";
        }
        else if(0.23 <= percentage && percentage < 0.26){
            myTier = "Gold 4";
        }
        else if(0.26 <= percentage && percentage < 0.29){
            myTier = "Gold 3";
        }
        else if(0.29 <= percentage && percentage < 0.32){
            myTier = "Gold 2";
        }
        else if(0.32 <= percentage && percentage < 0.36){
            myTier = "Gold 1";
        }
        else if(0.36 <= percentage && percentage < 0.40){
            myTier = "Platinum 4";
        }
        else if(0.40 <= percentage && percentage < 0.44){
            myTier = "Platinum 3";
        }
        else if(0.44 <= percentage && percentage < 0.48){
            myTier = "Platinum 2";
        }
        else if(0.48 <= percentage && percentage < 0.54){
            myTier = "Platinum 1";
        }
        else if(0.54 <= percentage && percentage < 0.60){
            myTier = "Diamond 4";
        }
        else if(0.60 <= percentage && percentage < 0.66){
            myTier = "Diamond 3";
        }
        else if(0.66 <= percentage && percentage < 0.75){
            myTier = "Diamond 2";
        }
        else if(0.75 <= percentage && percentage < 0.82){
            myTier = "Diamond 1";
        }
        else if(0.82 <= percentage && percentage < 0.89){
            myTier = "Master 1";
        }
        else if(0.89 <= percentage && percentage < 0.97){
            myTier = "Grandmaster 1";
        }
        else if(percentage >= 0.97){
            myTier = "Challenger 1";
        }

        dbHelper.close();
        return myTier;
    }

}
