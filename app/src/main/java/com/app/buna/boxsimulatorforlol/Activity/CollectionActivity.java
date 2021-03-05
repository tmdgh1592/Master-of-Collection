package com.app.buna.boxsimulatorforlol.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.app.buna.boxsimulatorforlol.Adapter.CollectionPagerAdapter;
import com.app.buna.boxsimulatorforlol.Service.BGMService;
import com.app.buna.boxsimulatorforlol.Manager.SoundManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.google.android.material.tabs.TabLayout;

public class CollectionActivity extends AppCompatActivity {

    private SoundManager soundManager;
    private int tabCloseSoundId;

    private TabLayout mTabLayout;
    private ViewPager viewPager;

    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_collection);
        setting = getSharedPreferences("setting", MODE_PRIVATE);

        setting();
    }


    private void setting(){
        setSoundManager();
        setViewPager();
    }

    private void setViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.collection_tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.champion_text)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.skin_text)));

        viewPager = findViewById(R.id.collection_view_pager);
        viewPager.setAdapter(new CollectionPagerAdapter(getSupportFragmentManager(), 2));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void setSoundManager(){
        soundManager = new SoundManager(CollectionActivity.this);
        soundManager.init();

        tabCloseSoundId = soundManager.loadSound(SoundManager.TYPE_TAB_CLOSE);
    }

    @Override
    protected void onResume() { // 화면이 onResume 되었을 때 restart
        super.onResume();
        if(MainActivity.bgmService != null && setting.getBoolean("BgmState", true) == true) {
            MainActivity.bgmService.restartBgm();
        }
    }

    @Override
    protected void onUserLeaveHint() {  // 홈버튼 누르거나 액티비티 전환할 때
        if(MainActivity.bgmService != null) {
            MainActivity.bgmService.pauseBgm();      // 노래 멈춤
        }
        super.onUserLeaveHint();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(soundManager.isLoaded()) {
            soundManager.play(tabCloseSoundId);
        }
        //startActivity(new Intent(CollectionActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
