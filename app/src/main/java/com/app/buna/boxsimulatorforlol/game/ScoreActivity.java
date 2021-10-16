package com.app.buna.boxsimulatorforlol.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.app.buna.boxsimulatorforlol.manager.GoldManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.GameToast;
import com.app.buna.boxsimulatorforlol.util.LangUtil;
import com.app.buna.boxsimulatorforlol.util.Network;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.text.DecimalFormat;

public class ScoreActivity extends AppCompatActivity {

    final String video_reward_id = "ca-app-pub-6856965594532028/1564427832";
    private RewardedAd mRewardedAd;
    private int score = 0;
    private int bonus = 0;
    private boolean isGetReward = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_score);

        setAds();

        TextView tv = (TextView) findViewById(R.id.reward_result_text_view);
        ImageView characterImage = findViewById(R.id.character_image_view);


        Intent i = getIntent();

        switch (i.getStringExtra("from")) {
            case "card":
                bonus = (int) (Math.random() * 100 + 1);
                int mistake = i.getIntExtra("mistake", 0);
                int number_of_card = i.getIntExtra("numberOfCard", 1);

                score = (number_of_card * 20) - mistake;
                characterImage.setBackgroundResource(R.drawable.anim_twisted_pate);

                tv.setText("\tCard" + " :  " + number_of_card + "\n\t" + "Score: " + score
                        + "\n\tBonus Coin :  " + bonus + "\n\t" + "Reward : " + (bonus + score));

                AnimationDrawable animationDrawable = (AnimationDrawable) characterImage.getBackground();
                animationDrawable.start();
                break;
            case "jump":
                bonus = (int) (Math.random() * 50 + 1);
                score = i.getIntExtra("score", 0);
                tv.setText("Score: " + score + "\n" + "Reward : " + new DecimalFormat("#.##").format(Math.floor(score / 5) + bonus));
                characterImage.setBackgroundResource(R.drawable.game_master_e);

                score = (int) (Math.floor(score / 5));

                /* image width setting */
                ViewGroup.LayoutParams params = characterImage.getLayoutParams();
                params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
                break;
            case "hangman":
                bonus = (int) (Math.random() * 100 + 1);
                score = i.getIntExtra("score", 0);

                characterImage.setBackgroundResource(R.drawable.anim_twisted_pate);

                tv.setText("Score: " + score + "\n" + "Reward : " + (score + bonus));

                params = characterImage.getLayoutParams();
                params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());
                characterImage.setBackgroundResource(R.drawable.anim_thresh);
                animationDrawable = (AnimationDrawable) characterImage.getBackground();
                animationDrawable.start();
                break;
        }

        CardView rewardBoosterBtn = findViewById(R.id.reward_booster_btn);
        CardView rewardBtn = findViewById(R.id.reward_btn);


        /* get reward x2 */
        rewardBoosterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*광고 시청 여부 묻는 dialog*/
                if (Network.state(ScoreActivity.this)) {
                    LangUtil.setLang(ScoreActivity.this);
                    AlertDialog askAdDialog = new AlertDialog.Builder(ScoreActivity.this, R.style.DialogMainColorBackground)
                            .setView(getLayoutInflater().inflate(R.layout.game_ad_ask_layout, null))
                            .setCancelable(true)
                            .setTitle(getString(R.string.game_ad_title))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (mRewardedAd != null) {
                                        mRewardedAd.show(ScoreActivity.this, new OnUserEarnedRewardListener() {
                                            @Override
                                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                isGetReward = true;
                                                getReward(2, (score + bonus));
                                            }
                                        });
                                    } else {
                                        loadAdsRequest();
                                        new GameToast(ScoreActivity.this, getString(R.string.game_ad_request_error), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    askAdDialog.getWindow().setBackgroundDrawableResource(R.drawable.major_background);

                    if (!askAdDialog.isShowing()) {
                        askAdDialog.show();
                    }
                } else {
                    Toast.makeText(ScoreActivity.this, getString(R.string.nick_condition_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* just get reward */
        rewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getReward(1, score+bonus);
                finish();
            }
        });
    }


    private void getReward(int multiple, int score) {
        new GoldManager(this).addGold(score * multiple);
        new GameToast(ScoreActivity.this, getString(R.string.game_ad_reward_message, score), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
    }

    private void setAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadAdsRequest();
            }
        });
    }

    private void loadAdsRequest() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mRewardedAd.load(this, video_reward_id, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                //super.onAdLoaded(rewardedAd);
                mRewardedAd = rewardedAd;

                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        //super.onAdShowedFullScreenContent();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        //super.onAdDismissedFullScreenContent();
                        if(isGetReward) {
                            loadAdsRequest();
                            finish();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mRewardedAd = null;
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}