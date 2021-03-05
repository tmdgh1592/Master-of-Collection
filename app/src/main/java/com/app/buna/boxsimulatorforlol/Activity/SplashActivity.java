package com.app.buna.boxsimulatorforlol.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;

public class SplashActivity extends AppCompatActivity {

    private int term = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        }, term);
    }


    @Override
    public void onBackPressed() {
        // 로딩창에서 갑작스런 뒤로가기 눌렀을 때를 방지
        //super.onBackPressed();
    }
}
