package com.app.buna.boxsimulatorforlol.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.boxsimulatorforlol.adapter.ShopRecyclerAdapter;
import com.app.buna.boxsimulatorforlol.adapter.UpgradeRecyclerAdapter;
import com.app.buna.boxsimulatorforlol.broadcast.BgmRestartBroadcast;
import com.app.buna.boxsimulatorforlol.broadcast.OnOffBroadcast;
import com.app.buna.boxsimulatorforlol.db.DBHelper;
import com.app.buna.boxsimulatorforlol.dto.ShopItem;
import com.app.buna.boxsimulatorforlol.dto.UpgradeData;
import com.app.buna.boxsimulatorforlol.manager.GoldEffectManager;
import com.app.buna.boxsimulatorforlol.manager.GoldManager;
import com.app.buna.boxsimulatorforlol.manager.ItemManager;
import com.app.buna.boxsimulatorforlol.manager.SoundManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.service.BGMService;
import com.app.buna.boxsimulatorforlol.service.GoldPerSecService;
import com.app.buna.boxsimulatorforlol.util.Cache;
import com.app.buna.boxsimulatorforlol.util.GameToast;
import com.app.buna.boxsimulatorforlol.util.LangUtil;
import com.app.buna.boxsimulatorforlol.util.Network;
import com.app.buna.boxsimulatorforlol.util.ScreenProportion;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import ir.alirezabdn.wp7progress.WP10ProgressBar;

import static com.app.buna.boxsimulatorforlol.vo.ItemType.TITLE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static ArrayList<Activity> actList = new ArrayList<>();

    private RewardedAd mRewardedAd;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView upgradeRecyclerView;
    private UpgradeRecyclerAdapter upgradeRecyclerAdapter;
    private TextView goldPerTextView;

    public static BGMService bgmService;

    private ArrayList<UpgradeData> upgradeList;

    private BgmRestartBroadcast restartService;

    private OnOffBroadcast onOffService;

    private DrawerLayout drawerLayout;
    private View drawerView;

    private RelativeLayout mainLayout;

    private ImageView monster;
    private TextView goldTextView;
    private TextView blueGemTextView;
    private TextView yellowGemTextView;

    private ImageView leftOptionIcon1, configBtn;
    private RelativeLayout openUpgradeBtn, openInfoBtn, rankBtn, rewardBtn;


    private GoldEffectManager goldEffectManager;

    private LinearLayout collectionTab, factoryTab, shopTab;

    RecyclerView shopRecyclerView;
    ShopRecyclerAdapter shopRecyclerAdapter;
    GridLayoutManager gridLayoutManager;


    SharedPreferences setting;
    SharedPreferences.Editor editor;

    private Intent activityTransIntent;

    private WP10ProgressBar loadingBar;

    private SoundManager soundManager;
    private int coinSoundId, tabClickSoundId, tabCloseSoundId,
            shopOpenSoundId, shopCloseSoundId;

    private GoldManager goldManager;
    private float goldPerClick;

    private float blueStealChance;
    private float yellowStealChance;
    private Random stealRandom;

    private ItemManager itemManager;

    private RelativeLayout shopLayout;

    private Intent bgmIntent;

    private String nickname;
    private String loginText;

    private AdView banner;

    private Long regTime=0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_main);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        startOnOffCheckService();

        stealRandom = new Random(); // 스틸 확률을 위한 랜덤값
        mainLayout = findViewById(R.id.main_relative_layout);

        //deleteUnnecessaryData();
        setting();
        refreshUIPerSec();
        showLoginMessage();

        Cache.clearApplicationData(this);
        editor.putBoolean("isRestart", false).commit();
    }


    private void showLoginMessage() {
        nickname = setting.getString("nickname", getString(R.string.temp_user_name));
        loginText = getString(R.string.hello1, nickname) + getString(R.string.hello2);

        new GameToast(this, loginText, Gravity.TOP, Toast.LENGTH_LONG).show();
    }

    private void setting() {
        setAds();
        settingView();
        setDrawer();
        setSoundManager();
        setTabLayout();
        setLeftOptions();
        setResource();
        setMonster();
        setGoldEffect();
        setShop();
        prefUpdate();
    }

    private void setAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadAdsRequest();
            }
        });
        banner = findViewById(R.id.banner);
        banner.loadAd(new AdRequest.Builder().build());
    }

    private void loadAdsRequest() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mRewardedAd.load(this, "ca-app-pub-6856965594532028/8365559420", adRequest, new RewardedAdLoadCallback() {
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
                        loadAdsRequest();
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

    private void settingView() {
        loadingBar = findViewById(R.id.loading_progress_bar);
        openInfoBtn = findViewById(R.id.my_info_button);
        openInfoBtn.setOnClickListener(this);
        rankBtn = findViewById(R.id.ranking_button);
        rankBtn.setOnClickListener(this);
        rewardBtn = findViewById(R.id.reward_button);
        rewardBtn.setOnClickListener(this);
        configBtn = findViewById(R.id.config_btn);
        configBtn.setOnClickListener(this);
        findViewById(R.id.my_game_button).setOnClickListener(this);
    }

    private void setDrawer() {
        goldManager = new GoldManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer_view);
        findViewById(R.id.upgrade_layout).getLayoutParams().width = new ScreenProportion(this).getItemWidthSize((float) 1.2);

        drawerLayout.addDrawerListener(drawerListener);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        goldPerTextView = drawerView.findViewById(R.id.gold_per_text_view);

        setUpgradeAdapter();
        ImageView drawerUpgradeButton = findViewById(R.id.drawer_left_option_icon);
        drawerUpgradeButton.setOnClickListener(this);
        drawerUpgradeButton.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_main_left_option));

        updateGoldPerTextView();
    }

    private void updateGoldPerTextView() {
        goldPerTextView.setText(new BigDecimal(goldManager.getGoldPerClick()).toString() + " Gold/Click    " + String.format("%.1f", goldManager.getGoldPerSec()) + " Gold/Sec");
    }

    private void setLeftOptions() {
        leftOptionIcon1 = findViewById(R.id.left_option_icon1);
        openUpgradeBtn = findViewById(R.id.upgrade_button_layout1);
        openUpgradeBtn.setOnClickListener(this);

        leftOptionIcon1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_main_left_option));
    }


    private void setShop() {
        LangUtil.setLang(this);
        shopLayout = findViewById(R.id.shop_layout);
        setShopAdapter();
    }

    private void openShop() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                shopLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void closeShop() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                shopLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setTabLayout() {
        collectionTab = findViewById(R.id.collection_tab);
        factoryTab = findViewById(R.id.factory_tab);
        shopTab = findViewById(R.id.shop_tab);

        collectionTab.setOnClickListener(this);
        factoryTab.setOnClickListener(this);
        shopTab.setOnClickListener(this);
    }


    private void setSoundManager() {

        /* effect sound */
        soundManager = new SoundManager(MainActivity.this);
        soundManager.init();

        coinSoundId = soundManager.loadSound(SoundManager.TYPE_COIN);
        tabClickSoundId = soundManager.loadSound(SoundManager.TYPE_TAB_OPEN);
        tabCloseSoundId = soundManager.loadSound(SoundManager.TYPE_TAB_CLOSE);
        shopOpenSoundId = soundManager.loadSound(SoundManager.TYPE_SHOP_OPEN);
        shopCloseSoundId = soundManager.loadSound(SoundManager.TYPE_SHOP_CLOSE);

        /* bgm */
        setBgmService();
    }

    public void setBgmService() {
        if (setting.getBoolean("BgmState", true)) {
            bgmIntent = new Intent(MainActivity.this, BGMService.class);
            bindService(bgmIntent, conn, BIND_AUTO_CREATE);
        }
    }


    private void startGoldPerSecondService() {
        //리스타트 서비스 생성
        restartService = new BgmRestartBroadcast();
        Intent bgIntent;
        bgIntent = new Intent(MainActivity.this, GoldPerSecService.class);

        // 서비스 시작
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(bgIntent);
        } else {
            startService(bgIntent);
        }
    }

    private void startOnOffCheckService() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        onOffService = new OnOffBroadcast();
        registerReceiver(onOffService, filter);
    }

    private void setGoldPerClick() {
        goldPerClick = goldManager.getGoldPerClick();
    }

    private void setBlueStealChance() {
        blueStealChance = itemManager.getBlueGemChance();
    }

    private void setYellowStealChance() {
        yellowStealChance = itemManager.getYellowGemChance();
    }

    public void refreshResource() {
        goldTextView.setText(String.format("%.0f", goldManager.getGold()));
        blueGemTextView.setText(Integer.toString(itemManager.getBlueGemCount()));
        yellowGemTextView.setText(Integer.toString(itemManager.getYellowGemCount()));
    }

    public void updateShopPrice() {
        LangUtil.setLang(this);
        shopRecyclerAdapter.updateReceiptsList(getShopItem());
    }


    private void setResource() {
        goldTextView = findViewById(R.id.gold_text_view);
        blueGemTextView = findViewById(R.id.blue_gem_text_view);
        yellowGemTextView = findViewById(R.id.yellow_gem_text_view);

        itemManager = new ItemManager(MainActivity.this);

        setGoldPerClick();
        refreshResource();
    }

    private void setMonster() {
        monster = findViewById(R.id.monster_image_view);
        monster.setOnClickListener(this);
    }


    private void setGoldEffect() {
        goldEffectManager = new GoldEffectManager(MainActivity.this);
        goldEffectManager.setMonster(monster)
                .setParentLayout(mainLayout)
                .build();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_info_button:
                if (soundManager.isLoaded()) {
                    soundManager.play(tabClickSoundId);
                }
                activityTransIntent = new Intent(MainActivity.this, MyInfoActivity.class);
                activityTransIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activityTransIntent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(activityTransIntent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeShop();
                    }
                }, 500);
                break;
            case R.id.reward_button:
                /*광고 시청 여부 묻는 dialog*/
                if (Network.state(MainActivity.this)) {
                    LangUtil.setLang(MainActivity.this);
                    AlertDialog askAdDialog = new AlertDialog.Builder(this, R.style.DialogMainColorBackground)
                            .setView(getLayoutInflater().inflate(R.layout.main_ad_ask_layout, null))
                            .setCancelable(true)
                            .setTitle(getString(R.string.main_ad_title))
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    soundManager.play(tabCloseSoundId);
                                }
                            })
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(mRewardedAd != null) {
                                        mRewardedAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                                            @Override
                                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                Random random = new Random();
                                                int rewardCount = random.nextInt(3) + 2;
                                                itemManager.addRewardItem(rewardCount);
                                                new GameToast(MainActivity.this, getString(R.string.main_ad_reward_message, rewardCount), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        loadAdsRequest();
                                        new GameToast(MainActivity.this, getString(R.string.main_ad_request_error), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    soundManager.play(tabClickSoundId);
                    askAdDialog.getWindow().setBackgroundDrawableResource(R.drawable.major_background);

                    if (!askAdDialog.isShowing()) {
                        askAdDialog.show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.nick_condition_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.monster_image_view:
                if (setting.getBoolean("EffectSoundState", true)) {
                    soundManager.play(coinSoundId);
                }
                goldManager.addGold(goldPerClick);
                goldTextView.setText(Integer.toString((int) goldManager.getGold()));
                if (blueStealChance != 0 && stealRandom.nextInt(100) < blueStealChance) {
                    itemManager.setBlueGemCount(itemManager.getBlueGemCount() + 1);
                    blueGemTextView.setText(Integer.toString(itemManager.getBlueGemCount()));
                }
                if (yellowStealChance != 0 && stealRandom.nextInt(100) < yellowStealChance) {
                    itemManager.setYellowGemCount(itemManager.getYellowGemCount() + 1);
                    yellowGemTextView.setText(Integer.toString(itemManager.getYellowGemCount()));
                }
                goldEffectManager.showGoldEffect();
                break;
            case R.id.collection_tab:
                if (soundManager.isLoaded()) {
                    soundManager.play(tabClickSoundId);
                }
                activityTransIntent = new Intent(MainActivity.this, CollectionActivity.class);
                activityTransIntent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);  // onUserLeaveHint 발생하지 않도록 설정 (bgm꺼지지 않게 하기 위함.)
                startActivity(activityTransIntent);
                //finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeShop();
                    }
                }, 500);
                break;
            case R.id.shop_tab:
                if (soundManager.isLoaded()) {
                    soundManager.play(shopOpenSoundId);
                }
                openShop();
                break;
            case R.id.factory_tab:
                if (soundManager.isLoaded()) {
                    soundManager.play(tabClickSoundId);
                }

                activityTransIntent = new Intent(MainActivity.this, FactoryActivity.class);
                activityTransIntent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);  // onUserLeaveHint 발생하지 않도록 설정 (bgm꺼지지 않게 하기 위함.)
                startActivity(activityTransIntent);
                //finish();
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeShop();
                    }
                }, 500);
                break;
            case R.id.upgrade_button_layout1:   //open
                if (!drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.openDrawer(drawerView);
                }
                break;
            case R.id.drawer_left_option_icon:  // close
                if (drawerLayout.isDrawerOpen(drawerView)) {
                    drawerLayout.closeDrawer(drawerView);
                }
                break;
            case R.id.ranking_button:
                if (Network.state(MainActivity.this)) {
                    rankBtn.setClickable(false);
                    loadingBar.showProgressBar();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activityTransIntent = new Intent(MainActivity.this, RankActivity.class);
                            activityTransIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activityTransIntent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);  // onUserLeaveHint 발생하지 않도록 설정 (bgm꺼지지 않게 하기 위함.)
                            startActivity(activityTransIntent);

                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
                            loadingBar.hideProgressBar();
                            rankBtn.setClickable(true);

                        }
                    }, 700);

                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.nick_condition_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.config_btn:
                activityTransIntent = new Intent(this, SettingsActivity.class);
                activityTransIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                actList.add(this);
                startActivityForResult(activityTransIntent, 1592);
                break;
            case R.id.my_game_button:
                startActivity(new Intent(this, GamePickerActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            default:
                break;
        }
    }


    private ArrayList<ShopItem> getShopItem() {
        ArrayList<ShopItem> dataList = new ArrayList<>();
        dataList.clear();

        String[] itemName = getResources().getStringArray(R.array.shop_item_name);
        String[] itemDesc = getResources().getStringArray(R.array.shop_item_desc);
        String[] boughtCountKey = getResources().getStringArray(R.array.shop_item_bought_count_key);
        int[] itemPrice = getResources().getIntArray(R.array.shop_item_price);
        int[] itemType = getResources().getIntArray(R.array.shop_item_type);
        int[] itemCode = getResources().getIntArray(R.array.shop_item_code);
        TypedArray itemImage = getResources().obtainTypedArray(R.array.shop_item_image);

        int i = 0;
        while (i < itemName.length) {
            ShopItem data = new ShopItem(itemCode[i], itemName[i], itemDesc[i], itemPrice[i], itemType[i], itemImage.getResourceId(i, 0), setting.getInt(boughtCountKey[i], 0));
            dataList.add(data);
            i++;
        }

        return dataList;
    }


    private void setShopAdapter() {
        final ArrayList<ShopItem> dataList = getShopItem();
        shopRecyclerView = findViewById(R.id.shop_recycler_view);
        shopRecyclerAdapter = new ShopRecyclerAdapter(this, this, dataList);
        shopRecyclerAdapter.setItemSize(new ScreenProportion(this).getItemWidthSize((float) 2.8));
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (dataList.get(position).getItemType() == TITLE) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });

        shopRecyclerView.setAdapter(shopRecyclerAdapter);
        shopRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private ArrayList<UpgradeData> getUpgrades() {
        ArrayList<UpgradeData> dataList = new ArrayList<>();

        String[] itemName = getResources().getStringArray(R.array.upgrade_item_name);
        String[] prefItemName = getResources().getStringArray(R.array.pref_upgrade_item_name);
        String[] itemDesc = getResources().getStringArray(R.array.upgrade_shop_desc);
        int[] upgradeType = getResources().getIntArray(R.array.upgrade_type);
        int[] maxSkillLevel = getResources().getIntArray(R.array.max_skill_level);
        int[] upgradeInitPrice = getResources().getIntArray(R.array.upgrade_init_price);
        TypedArray howMuchUpgradePrice = getResources().obtainTypedArray(R.array.how_much_upgrade_price);
        TypedArray howMuchUpgradeEffect = getResources().obtainTypedArray(R.array.how_much_upgrade_effect);
        int[] tierCondition = getResources().getIntArray(R.array.upgrade_tier_condition);
        TypedArray itemImage = getResources().obtainTypedArray(R.array.upgrade_item_image);

        int i = 0;
        while (i < itemName.length) {
            int nowSkillLevel = setting.getInt(prefItemName[i] + "NowSkillLevel", 0);
            float effectValue = setting.getFloat(prefItemName[i] + "SkillEffect", 0);
            int upgradePrice = upgradeInitPrice[i] + setting.getInt(prefItemName[i] + "UpgradePrice", 0);

            UpgradeData data = new UpgradeData(upgradeType[i], prefItemName[i], itemName[i], itemDesc[i], nowSkillLevel, maxSkillLevel[i],
                    itemImage.getResourceId(i, 0), effectValue, upgradePrice, tierCondition[i], howMuchUpgradePrice.getFloat(i, 0), howMuchUpgradeEffect.getFloat(i, 0));
            dataList.add(data);
            i++;
        }

        return dataList;
    }

    private void deleteUnnecessaryData() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("DELETE FROM champ_frag_table WHERE count=0");
        db.execSQL("DELETE FROM skin_frag_table WHERE count=0");

        db.close();
    }

    private void setUpgradeAdapter() {
        upgradeList = getUpgrades();
        upgradeRecyclerView = findViewById(R.id.upgrade_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setItemPrefetchEnabled(true);
        upgradeRecyclerAdapter = new UpgradeRecyclerAdapter(this, this, upgradeList);

        upgradeRecyclerView.setAdapter(upgradeRecyclerAdapter);
        upgradeRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() { // 화면이 onResume 되었을 때 restart
        super.onResume();

        // mRewardedVideoAd.resume(this);
        if (bgmService != null && setting.getBoolean("BgmState", true) == true) {
            bgmService.restartBgm();
        }
        refreshResource();
        /* 자원 뷰 새로고침 */
    }

    @Override
    protected void onPause() {
        // mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        startGoldPerSecondService();    // goldPerSecond Service 실행
    }

    @Override
    protected void onDestroy() {
        if (mRewardedAd != null) {
            mRewardedAd = null;
        }
        super.onDestroy();
        //mRewardedVideoAd.destroy(this);
        unregisterReceiver(onOffService);
        android.os.Process.killProcess(Process.myPid());
    }

    public void prefUpdate() {
        LangUtil.setLang(this);
        setGoldPerClick();      // 골드 퍼 클릭
        setBlueStealChance();   // 파랑 정수 획득 확률
        setYellowStealChance(); // 노랑 정수 획득 확률
        updateGoldPerTextView();
        upgradeRecyclerAdapter.updateReceiptsList(getUpgrades());   //recyclerView
        refreshResource();
    }


    @Override
    protected void onUserLeaveHint() {  // 홈버튼 누르거나 액티비티 전환할 때
        if (bgmService != null) {
            bgmService.pauseBgm();
        }
        super.onUserLeaveHint();
    }


    @Override
    public void onBackPressed() {



        if (drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.closeDrawer(drawerView);
            soundManager.play(tabCloseSoundId);
            return;
        }
        if (shopLayout.getVisibility() == View.VISIBLE) {
            if (soundManager.isLoaded()) {
                soundManager.play(shopCloseSoundId);
            }
            closeShop();
            return;
        }

        Long curTime = System.currentTimeMillis();
        if ((curTime - regTime) > 2000) {
            Toast.makeText(this, getString(R.string.back_press), Toast.LENGTH_SHORT).show();
            regTime = curTime;
            return;
        }

        super.onBackPressed();
    }


    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            openUpgradeBtn.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_hide_right_to_left));
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
            openUpgradeBtn.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_show_right_from_left));
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };


    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            BGMService.LocalBinder mb = (BGMService.LocalBinder) service;
            bgmService = mb.getService(); // 서비스가 제공하는 메소드 호출하여 서비스쪽 객체를 전달받을수 있슴
        }

        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 callback 메서드

        }
    };


    private void refreshUIPerSec() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goldTextView.setText(String.format("%.0f", goldManager.getGold()));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


}
