package com.app.buna.boxsimulatorforlol.Util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenProportion {

    Context context;

    public ScreenProportion(Context context){
        this.context = context;
    }

    public int getItemSize(float proportion){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);

        return (int)(dm.widthPixels / proportion);
    }

}
