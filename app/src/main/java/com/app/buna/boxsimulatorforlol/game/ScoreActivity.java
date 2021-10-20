package com.app.buna.boxsimulatorforlol.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.activity.MyInfoActivity;
import com.app.buna.boxsimulatorforlol.db.DBHelper;
import com.app.buna.boxsimulatorforlol.manager.GoldManager;
import com.app.buna.boxsimulatorforlol.manager.ItemManager;
import com.app.buna.boxsimulatorforlol.manager.TierManager;
import com.app.buna.boxsimulatorforlol.util.GameToast;
import com.app.buna.boxsimulatorforlol.util.JsonUtil;
import com.app.buna.boxsimulatorforlol.util.LangUtil;
import com.app.buna.boxsimulatorforlol.util.Network;
import com.bumptech.glide.Glide;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;

public class ScoreActivity extends AppCompatActivity {

    final String video_reward_id = "ca-app-pub-6856965594532028/1564427832";
    private RewardedAd mRewardedAd;
    private int score = 0;
    private int bonus = 0;
    private boolean isGetReward = false;
    private String type = "card";
    private int reward = 0;

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
                type = "card";
                bonus = (int) (Math.random() * 100 + 1);
                int mistake = i.getIntExtra("mistake", 0);
                int number_of_card = i.getIntExtra("numberOfCard", 1);

                score = ((number_of_card * 20) - mistake);
                characterImage.setBackgroundResource(R.drawable.anim_twisted_pate);

                tv.setText("Card : " + number_of_card + "\nScore : " + score +"\nGame Bonus : " + bonus + "\nTier Bonus : " + getTierPoint()
                        + "\nBonus Coin :  " + bonus + "\n" + "Reward : " + ((int)((score)*getTierPoint())+bonus));

                reward = (int)(score *getTierPoint())+bonus;

                AnimationDrawable animationDrawable = (AnimationDrawable) characterImage.getBackground();
                animationDrawable.start();
                break;
            case "jump":
                type = "jump";
                bonus = (int) (Math.random() * 50 + 1);
                score = i.getIntExtra("score", 0)/5;
                tv.setText("Score: " + score + "\n" +"Game Bonus : " + bonus + "\n" +"Tier Bonus : " + getTierPoint()+ "\nReward : " + (int)(Math.floor(score / 5) * getTierPoint() + bonus));
                characterImage.setBackgroundResource(R.drawable.game_master_e);

                reward = (int) (((Math.floor(score / 5)) + bonus)*getTierPoint());

                /* image width setting */
                ViewGroup.LayoutParams params = characterImage.getLayoutParams();
                params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
                break;
            case "hangman":
                type = "hangman";
                bonus = (int) (Math.random() * 100 + 1);
                score = (i.getIntExtra("score", 0));

                characterImage.setBackgroundResource(R.drawable.anim_twisted_pate);

                tv.setText("Score: " + score + "\n" +"Game Bonus : " + bonus +"\nTier Bonus : " + getTierPoint() + "\n" + "Reward : " + (int)(((bonus + score)*getTierPoint())));

                reward = (int) (score *getTierPoint() + bonus);

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
                                                getReward(2, reward, type);
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
                getReward(1, reward, type);
                finish();
            }
        });
    }


    private void getReward(int multiple, int reward, String type) {
        new GoldManager(this).addGold((int)Math.floor(reward * multiple));
        new GameToast(ScoreActivity.this, getString(R.string.game_ad_reward_message, (int)Math.floor(reward * multiple)), Gravity.BOTTOM, Toast.LENGTH_LONG).show();

        if (type != "hangman" && new Random().nextInt(10) < 4 && score > 200) { // 30% 확률로 score를 특정 점수 넘기면 스폐셜 보상 지급
            int specialItemCount = new Random().nextInt(2) + 1;
            new GameToast(ScoreActivity.this, getString(R.string.game_ad_extra_reward_message, (int)Math.floor(reward * multiple), specialItemCount), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
            new ItemManager(this).addGameRewardItem(multiple*specialItemCount); // 게임 보상 1~2개 랜덤보상
        }
    }

    private float getTierPoint() {
        float tierPoint = 1;

        switch (getTier()) {
            case "Unranked":
                tierPoint = 1.0f;
                break;
            case "Iron 4":
                tierPoint = 1.2f;
                break;
            case "Iron 3":
                tierPoint = 1.3f;
                break;
            case "Iron 2":
                tierPoint = 1.4f;
                break;
            case "Iron 1":
                tierPoint = 1.5f;
                break;
            case "Bronze 4":
                tierPoint = 1.6f;
                break;
            case "Bronze 3":
                tierPoint = 1.7f;
                break;
            case "Bronze 2":
                tierPoint = 1.8f;
                break;
            case "Bronze 1":
                tierPoint = 1.9f;
                break;
            case "Silver 4":
                tierPoint = 2.0f;
                break;
            case "Silver 3":
                tierPoint = 2.1f;
                break;
            case "Silver 2":
                tierPoint = 2.2f;
                break;
            case "Silver 1":
                tierPoint = 2.3f;
                break;
            case "Gold 4":
                tierPoint = 2.4f;
                break;
            case "Gold 3":
                tierPoint = 2.5f;
                break;
            case "Gold 2":
                tierPoint = 2.6f;
                break;
            case "Gold 1":
                tierPoint = 2.7f;
                break;
            case "Platinum 4":
                tierPoint = 2.8f;
                break;
            case "Platinum 3":
                tierPoint = 2.9f;
                break;
            case "Platinum 2":
                tierPoint = 3.0f;
                break;
            case "Platinum 1":
                tierPoint = 3.1f;
                break;
            case "Diamond 4":
                tierPoint = 3.2f;
                break;
            case "Diamond 3":
                tierPoint = 3.3f;
                break;
            case "Diamond 2":
                tierPoint = 3.4f;
                break;
            case "Diamond 1":
                tierPoint = 3.5f;
                break;
            case "Master 1":
                tierPoint = 4.0f;
                break;
            case "Grandmaster 1":
                tierPoint = 4.5f;
                break;
            case "Challenger 1":
                tierPoint = 5.0f;
                break;
        }
        return tierPoint;
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
                        if (isGetReward) {
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

    private int getTotalSkinChamp() {
        int totalChampCount = 0;
        int totalSkinCount = 0;

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new JsonUtil(this).getJsonString("champion.json"));
            JSONObject dataObject = jsonObject.getJSONObject("data");
            Iterator iterator = dataObject.keys();

            while (iterator.hasNext()) {
                totalChampCount++;
                String champName = iterator.next().toString();
                JSONObject champData = dataObject.getJSONObject(champName);
                JSONArray skinArray = champData.getJSONArray("skins");
                totalSkinCount += (skinArray.length() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return totalChampCount + totalSkinCount;
    }

    private String getTier() {
        return new TierManager(this).getMyTier(getTotalSkinChamp());
    }
}