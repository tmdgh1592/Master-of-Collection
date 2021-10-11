package com.app.buna.boxsimulatorforlol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.LangUtil;
import com.app.buna.boxsimulatorforlol.ads.AppOpenManager;

public class SplashActivity extends AppCompatActivity {

    private int term = 2000;
    private AppOpenManager openManager;
    private boolean isAdShown = false;
    private boolean isAdDismissed = false;
    private boolean isLoadCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_splash);

        if(!getIntent().getBooleanExtra("autoLogin", false)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, 3000);
        }

        //openManager = ((MyApplication) getApplication()).getAppOpenManager();

        //loadResources();
        /*openManager.showAdIfAvailable(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                isAdShown = true;
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                isAdDismissed = true;

                if(isLoadCompleted) {
                    launchMainScreen();
                } else {
                    Log.d("TAG", "Waiting resources to be loaded...");
                }
            }
        });*/
    }

    private void loadResources() {
        // Wait for 5 seconds
        CountDownTimer timer = new CountDownTimer(5000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing
            }

            @Override
            public void onFinish() {
                isLoadCompleted = true;

                // Check whether App Open ad was shown or not.
                if (isAdShown) {
                    // Check App Open ad was dismissed or not.
                    if (isAdDismissed) {
                        launchMainScreen();
                    } else {
                        Log.d("TAG", "Waiting for ad to be dismissed...");
                    }
                } else {
                    launchMainScreen();
                }
            }
        };
        timer.start();
    }

    public void launchMainScreen() {
        ActivityCompat.finishAffinity(SplashActivity.this);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
    }

    @Override
    public void onBackPressed() {
        // 로딩창에서 갑작스런 뒤로가기 눌렀을 때를 방지
        //super.onBackPressed();
    }
}
