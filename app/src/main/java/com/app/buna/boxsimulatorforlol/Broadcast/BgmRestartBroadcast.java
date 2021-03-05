package com.app.buna.boxsimulatorforlol.Broadcast;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.buna.boxsimulatorforlol.Service.GoldPerSecService;


public class BgmRestartBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("000 BgmRestartBroadcast" , "BgmRestartBroadcast called : " + intent.getAction());

        /**
         * 서비스 죽일때 알람으로 다시 서비스 등록
         */
        if(!isServiceRunning(context)) {
            if (intent.getAction().equals("ACTION.RESTART.GoldPerSecService")) {

                Log.i("000 BgmRestartBroadcast", "ACTION.RESTART.GoldPerSecService");

                Intent i = new Intent(context, GoldPerSecService.class);

                context.startService(i);
            }

            /**
             * 폰 재시작 할때 서비스 등록
             */
            else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

                Log.i("BgmRestartBroadcast", "ACTION_BOOT_COMPLETED");
                Intent i = new Intent(context, GoldPerSecService.class);
                context.startService(i);

            }
        }
    }

    public boolean isServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {
            if (GoldPerSecService.class.getName().equals(rsi.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
