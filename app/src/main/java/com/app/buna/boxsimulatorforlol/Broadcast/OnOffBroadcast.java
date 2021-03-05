package com.app.buna.boxsimulatorforlol.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.app.buna.boxsimulatorforlol.Activity.MainActivity;

public class OnOffBroadcast extends BroadcastReceiver {



    public static final String ScreenOff = "android.intent.action.SCREEN_OFF";
    public static final String ScreenOn = "android.intent.action.SCREEN_ON";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ScreenOff))
        {
            MainActivity.bgmService.pauseBgm();
            Log.e("OnOffBroadcast", "Screen Off");
        }
        else if (intent.getAction().equals(ScreenOn))
        {
            MainActivity.bgmService.restartBgm();
            Log.e("OnOffBroadcast", "Screen On");
        }
    }
}
