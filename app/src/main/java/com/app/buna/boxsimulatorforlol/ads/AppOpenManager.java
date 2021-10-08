package com.app.buna.boxsimulatorforlol.ads;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.app.buna.boxsimulatorforlol.Activity.MainActivity;
import com.app.buna.boxsimulatorforlol.Util.Network;
import com.app.buna.boxsimulatorforlol.application.MyApplication;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static androidx.lifecycle.Lifecycle.Event.ON_START;

public class AppOpenManager extends AppOpenAd.AppOpenAdLoadCallback implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static final String LOG_TAG = "AppOpenManager";
    //private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294"; // 테스트 ID
    private static final String AD_UNIT_ID = "ca-app-pub-6856965594532028/4618334148";
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;
    public static boolean isOpened = false;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    private long loadTime = 0;
    private final MyApplication myApplication;
    private boolean isShowedAd = false;
    public static boolean isShowingAd = false;

    /**
     * Constructor
     */
    public AppOpenManager(MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        Log.d(LOG_TAG, "onStart");
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        /*fetchAd();*/

        if (!isOpened && !isShowingAd && isAdAvailable() && !isShowedAd) {
            Log.d(LOG_TAG, "Will show ad.");
            isShowedAd = true;
            isOpened = true;

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();

                            if (myApplication.getBaseContext().getSharedPreferences("setting", MODE_PRIVATE).getBoolean("isLoginStateContinue", false) && Network.state(myApplication.getBaseContext()) && FirebaseAuth.getInstance().getCurrentUser() != null) {
                                ActivityCompat.finishAffinity(currentActivity);
                                currentActivity.startActivity(new Intent(currentActivity, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                        }



                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
        }

    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        // Have unused ad, no need to fetch another.
        Log.d("TAG", "fetch");
        if (isAdAvailable()) {
            return;
        }

        /*loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {
                    *//**
         * Called when an app open ad has loaded.
         *
         * @param ad the loaded app open ad.
         *//*
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        AppOpenManager.this.appOpenAd = ad;
                        AppOpenManager.this.loadTime = (new Date()).getTime();
                    }

                    *//**
         * Called when an app open ad has failed to load.
         *
         * @param loadAdError the error.
         *//*
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle the error.
                    }

                };*/
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, this);
    }

    @Override
    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
        //super.onAdLoaded(appOpenAd);
        Log.d("TAG", "Load success");
        AppOpenManager.this.loadTime = (new Date()).getTime();
        this.appOpenAd = appOpenAd;
        showAdIfAvailable();
    }

    @Override
    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
        //super.onAdFailedToLoad(loadAdError);
        Log.d("TAG", "Failed to load an ad: " + loadAdError.getMessage());
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        //return appOpenAd != null;
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }


}