package com.app.buna.boxsimulatorforlol.application;

import android.app.Application;
import android.util.Log;

import com.app.buna.boxsimulatorforlol.StaticData;
import com.app.buna.boxsimulatorforlol.ads.AppOpenManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MyApplication extends Application {

    private AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!getSharedPreferences("setting", MODE_PRIVATE).getBoolean("isRestart", false)) {
            MobileAds.initialize(
                    this,
                    new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                        }
                    });
            appOpenManager = new AppOpenManager(this);
        }
    }


    public AppOpenManager getAppOpenManager() {
        return appOpenManager;
    }
}