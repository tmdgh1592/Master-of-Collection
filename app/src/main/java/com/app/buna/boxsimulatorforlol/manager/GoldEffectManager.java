package com.app.buna.boxsimulatorforlol.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.buna.boxsimulatorforlol.R;

import java.math.BigDecimal;
import java.util.Random;

public class GoldEffectManager {

    private Activity activity;
    private ImageView monster;
    private RelativeLayout mainLayout;
    private SharedPreferences setting;

    private float goldPerClick;

    private GoldManager goldManager;

    public GoldEffectManager(Activity activity){
        this.activity = activity;
        this.goldManager = new GoldManager(activity.getApplicationContext());
    }

    public GoldEffectManager setMonster(ImageView monster) {
        this.monster = monster;
        return this;
    }

    public GoldEffectManager setParentLayout(RelativeLayout mainLayout) {
        this.mainLayout = mainLayout;
        return this;
    }

    public void build() {
        setting = activity.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }


    public void showGoldEffect(){
        final View goldEffectView = activity.getLayoutInflater().inflate(R.layout.gold_effect_layout, null);
        TextView goldTextView = goldEffectView.findViewById(R.id.gold_per_click_text_view);

        goldTextView.setText((new BigDecimal(goldManager.getGoldPerClick()).toString() + "G"));
        Random random = new Random();
        int randomX = (int) (monster.getX() + random.nextInt(monster.getWidth()));  // 0 ~ width-1
        int randomY = (int) (monster.getY() + random.nextInt(monster.getHeight()));  // 0 ~ height-1

        /*set effect location*/
        mainLayout.addView(goldEffectView);
        goldEffectView.setX(randomX);
        goldEffectView.setY(randomY);

        /*hide gold effect*/
        hideGoldEffect(goldEffectView);
    }

    private void hideGoldEffect(final View goldEffectView) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.hide_gold_effect);
                animation.setAnimationListener(new Animation.AnimationListener() {
                   @Override
                   public void onAnimationStart(Animation animation) {

                   }

                   @Override
                   public void onAnimationEnd(Animation animation) {    //애니메이션이 끝나면 실제 view도 삭제
                       new Handler().post(new Runnable() {
                           @Override
                           public void run() {
                               mainLayout.removeView(goldEffectView);
                           }
                       });
                   }

                   @Override
                   public void onAnimationRepeat(Animation animation) {

                   }
               });

               goldEffectView.startAnimation(animation);
            }
        }).start();
    }
}
