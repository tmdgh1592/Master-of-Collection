package com.app.buna.boxsimulatorforlol.game.cardgame;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.ScreenProportion;

public class Card extends AppCompatButton {

    boolean isOpen = false;     //open->face, close->back sides of card
    boolean isSpin = true;
    static boolean isMix = true;

    int backSideOfCardID;
    int faceSideOfCardID;
    int card_number;
    Drawable face;
    Drawable back;


    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        /*params.width = 230;
        params.height = 230;*/
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.width = new ScreenProportion(getContext()).getItemWidthSize((float)4);
        param.height = new ScreenProportion(getContext()).getItemHeightSize((float)7.5);
        param.setMargins(0, 20,-20,20);
        super.setLayoutParams(param);
    }



    static int[] icons_champion = new int[]{R.drawable.aatrox_0, R.drawable.amumu_0, R.drawable.ahri_0, R.drawable.bard_0,
    R.drawable.zac_0, R.drawable.akali_0, R.drawable.blitzcrank_0, R.drawable.tahmkench_0,
    R.drawable.leesin_0, R.drawable.teemo_0};

    public Card(@NonNull Context context, int id, int number_of_Cards) {
        super(context);

        setId(id);      //button id

        int index;      //for cards --> If number of cards are 12, index (mode number of cards / 2)
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Mix();
            }
        });     //thread is used to shred high works to threads

        thread.start();

        try {
            thread.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        card_number = number_of_Cards/2;        //unique card number

        index = (id - 1) % card_number;
        faceSideOfCardID = icons_champion[index];
        backSideOfCardID = R.drawable.backside;

        back = ContextCompat.getDrawable(context,backSideOfCardID);
        face = ContextCompat.getDrawable(context,faceSideOfCardID);
        setBackground(back);

    }

    public void Spin() {
        if(!isSpin)       //eşleşmeden sonra kullanıcı tekrar döndürmesin kartları
            return;

        if(!isOpen) {   //arkası cevriliyse
            Animation anim = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
            anim.setAnimationListener(new Animation.AnimationListener() {
                                          @Override
                                          public void onAnimationStart(Animation animation) {

                                          }

                                          @Override
                                          public void onAnimationEnd(Animation animation) {
                                              setBackground(face);
                                              isOpen=true;
                                          }

                                          @Override
                                          public void onAnimationRepeat(Animation animation) {

                                          }
                                      });
            startAnimation(anim);
        }
        else {
            setBackground(back);
            isOpen=false;
        }
    }

    public static void Mix() {
        if (isMix) {
            for (int j = 0; j < icons_champion.length; j++) {
                int random = (int) (Math.random() * icons_champion.length);
                int k = icons_champion[random];
                icons_champion[random] = icons_champion[j];
                icons_champion[j] = k;
            }
        }
    }
}
