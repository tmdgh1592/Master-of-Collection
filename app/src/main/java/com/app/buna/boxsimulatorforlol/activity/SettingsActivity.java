package com.app.buna.boxsimulatorforlol.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.app.buna.boxsimulatorforlol.db.DBHelper;
import com.app.buna.boxsimulatorforlol.dto.CollectData;
import com.app.buna.boxsimulatorforlol.dto.ItemFragment;
import com.app.buna.boxsimulatorforlol.dto.TransferData;
import com.app.buna.boxsimulatorforlol.manager.GoldManager;
import com.app.buna.boxsimulatorforlol.manager.ItemManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.AppVersion;
import com.app.buna.boxsimulatorforlol.util.GameToast;
import com.app.buna.boxsimulatorforlol.util.LangUtil;
import com.app.buna.boxsimulatorforlol.util.Network;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    //private RewardedVideoAd mRewardedVideoAd;loadData();
    private InterstitialAd mInterstitialSaveAd;
    private InterstitialAd mInterstitialLoadAd;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("account").child("userinfo");
    private DatabaseReference svRef = FirebaseDatabase.getInstance().getReference().child("account");
    private DatabaseReference rankRef = FirebaseDatabase.getInstance().getReference().child("rank");

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private Context context;

    private ItemManager itemManager;
    private GoldManager goldManager;

    private Preference nickPref, emailPref, logoutPref, versionPref, savePref, loadPref;// initPref;
    private ListPreference changeLangPref;
    private SwitchPreference bgmSwitch, effectSwitch;

    AlertDialog msgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        addPreferencesFromResource(R.xml.root_preferences);

        context = this;
        setAds();

        itemManager = new ItemManager(this);
        goldManager = new GoldManager(this);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        settingView();
        setViewFunc();
    }

    private void setAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {
            }
        });
        loadSaveAd();
        loadLoadAd();
    }

    private void loadSaveAd() {
        InterstitialAd.load(this, "ca-app-pub-6856965594532028/6385709364", new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialSaveAd = interstitialAd;
                        mInterstitialSaveAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                saveData();
                                saveLank();
                                new GameToast(context, getString(R.string.preference_ad_save_success_message), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                                loadSaveAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialSaveAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });

                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialSaveAd = null;
                    }
                });
    }


    private void loadLoadAd() {
        InterstitialAd.load(this, "ca-app-pub-6856965594532028/4681184379", new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialLoadAd = interstitialAd;
                        mInterstitialLoadAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                loadData();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialLoadAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });

                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialLoadAd = null;
                    }
                });
    }



    /*private void setAd() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
            }
            @Override
            public void onRewardedVideoAdOpened() {
            }
            @Override
            public void onRewardedVideoStarted() {
                new GameToast(context, getString(R.string.preference_ad_message), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onRewardedVideoAdClosed() {
                loadSaveRewardedVideoAd();
                loadLoadRewardedVideoAd();
            }
            @Override
            public void onRewarded(RewardItem rewardItem) {
                if(rewardItem.getType().equals("save")) {
                    saveData();
                    saveLank();
                    new GameToast(context, getString(R.string.preference_ad_save_success_message), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                }else if(rewardItem.getType().equals("load")){
                    loadData();
                    new GameToast(context, getString(R.string.preference_ad_load_success_message), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                    restartApp("LOAD_DATA");
                }else{
                    new GameToast(context, getString(R.string.unknown_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onRewardedVideoAdLeftApplication() {
            }
            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
            }
            @Override
            public void onRewardedVideoCompleted() {
            }
        });
        loadSaveRewardedVideoAd();
        loadLoadRewardedVideoAd();
    }*/


    private void restartApp(String type){
        Intent i;
        getSharedPreferences("setting",MODE_PRIVATE).edit().putBoolean("isRestart", true).commit();
        if(type.equals("LOGOUT")) {
            i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 이미 loginActivity가 Task에 있을 경우 이미 있는 것을 가져옴
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            MainActivity.actList.get(0).finish();
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
        }else{
            i = new Intent(this, SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            MainActivity.actList.get(0).finish();
            startActivity(i);
        }


    }

    private void settingView(){
        nickPref = getPreferenceScreen().findPreference("nickname_pref");
        emailPref = getPreferenceScreen().findPreference("email_pref");
        logoutPref = getPreferenceScreen().findPreference("logout_pref");
        versionPref = getPreferenceScreen().findPreference("version_pref");
        savePref = getPreferenceScreen().findPreference("save_data_pref");
        loadPref = getPreferenceScreen().findPreference("load_data_pref");
        //     initPref = getPreferenceScreen().findPreference("init_data_pref");
        changeLangPref = (ListPreference) getPreferenceScreen().findPreference("change_lang_pref");
        bgmSwitch = (SwitchPreference) getPreferenceScreen().findPreference("bgm_sound_switch");
        effectSwitch = (SwitchPreference) getPreferenceScreen().findPreference("effect_sound_switch");
    }

    private void setViewFunc(){
        updateSummary();
        logoutPref.setOnPreferenceClickListener(this);
        savePref.setOnPreferenceClickListener(this);
        loadPref.setOnPreferenceClickListener(this);
//        initPref.setOnPreferenceClickListener(this);
        changeLangPref.setOnPreferenceChangeListener(this);
        versionPref.setOnPreferenceClickListener(this);
        bgmSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((boolean)newValue == true){
                    if(MainActivity.bgmService == null){
                        editor.putBoolean("BgmState", true);
                        editor.commit();
                        ((MainActivity)MainActivity.actList.get(0)).setBgmService();
                    }else {
                        MainActivity.bgmService.restartBgm();
                    }
                    editor.putBoolean("BgmState", true);
                }else{
                    MainActivity.bgmService.pauseBgm();
                    editor.putBoolean("BgmState", false);
                }
                editor.commit();
                return true;
            }
        });

        effectSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((boolean)newValue == true){
                    editor.putBoolean("EffectSoundState", true);
                }else{
                    editor.putBoolean("EffectSoundState", false);
                }
                editor.commit();
                return true;
            }
        });
    }


    private void updateSummary(){
        nickPref.setSummary(setting.getString("nickname", getString(R.string.temp_user_name)));
        emailPref.setSummary(setting.getString("email", ""));
        versionPref.setTitle(getString(R.string.pref_now_version) + " : " + AppVersion.getVersionInfo(context));
        versionPref.setSummary(getString(R.string.pref_now_version_summ));

        if(LangUtil.getLang(this) == LangUtil.KR){
            changeLangPref.setValueIndex(0);
            changeLangPref.setSummary(getString(R.string.language_kr_text));
        }else if(LangUtil.getLang(this) == LangUtil.EN){
            changeLangPref.setValueIndex(1);
            changeLangPref.setSummary(getString(R.string.language_en_text));
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        LangUtil.setLang(this);
        String notUnderlineMessage;
        String message;
        SpannableStringBuilder ssBuilder;
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.tabIconPressedColor));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        switch(preference.getKey()){
            case "save_data_pref":
                notUnderlineMessage = getString(R.string.save_alert_not_underline_message);
                message = getString(R.string.save_alert_message);
                ssBuilder = new SpannableStringBuilder(message);
                ssBuilder.setSpan(foregroundColorSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssBuilder.setSpan(underlineSpan, notUnderlineMessage.length(), message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                msgDialog = new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.save_alert_title))
                        .setMessage(ssBuilder)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Network.state(context)){
                                    if(mInterstitialSaveAd != null) {
                                        mInterstitialSaveAd.show(SettingsActivity.this);
                                    }
                                    //mRewardedVideoAd.show();
                                }else{
                                    new GameToast(context, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton(getString(R.string.cancel), null).create();
                if(!msgDialog.isShowing()) {
                    msgDialog.show();
                }
                break;
            case "load_data_pref":
                notUnderlineMessage = getString(R.string.load_alert_not_underline_message);

                message = getString(R.string.load_alert_message);
                ssBuilder = new SpannableStringBuilder(message);
                ssBuilder.setSpan(foregroundColorSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssBuilder.setSpan(underlineSpan, notUnderlineMessage.length(), message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                msgDialog = new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.load_alert_title))
                        .setMessage(ssBuilder)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Network.state(context)){
                                    if(mInterstitialLoadAd != null) {
                                        mInterstitialLoadAd.show(SettingsActivity.this);
                                    }
                                }else{
                                    new GameToast(context, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                msgDialog.dismiss();
                            }
                        }).create();
                if(!msgDialog.isShowing()) {
                    msgDialog.show();
                }
                break;
            /*case "init_data_pref": // 초기화는 문제가 있어 일단 기능 중지
                message = getString(R.string.clear_alert_message);
                ssBuilder = new SpannableStringBuilder(message);
                ssBuilder.setSpan(foregroundColorSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                msgDialog = new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.clear_alert_title))
                        .setMessage(ssBuilder)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                initData();
                                new GameToast(context, getString(R.string.preference_init_success_message), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                restartApp();
                            }
                        }).setNegativeButton(getString(R.string.cancel), null).create();
                if(!msgDialog.isShowing()) {
                    msgDialog.show();
                }
                break;*/
            case "logout_pref":
                if(Network.state(context)) {
                    notUnderlineMessage = getString(R.string.logout_alert_not_underline_message);

                    message = getString(R.string.logout_alert_message);
                    ssBuilder = new SpannableStringBuilder(message);
                    ssBuilder.setSpan(foregroundColorSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssBuilder.setSpan(underlineSpan, notUnderlineMessage.length(), message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    msgDialog = new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.logout_alert_title))
                            .setMessage(ssBuilder)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 로그아웃을 할 경우 서버 데이터를 업데이트 하고 기기에 있는 데이터는 초기화한다.
                                    saveData();

                                    editor.putBoolean("isLoginStateContinue", false);
                                    editor.commit();

                                    /*firebase logout*/
                                    FirebaseAuth.getInstance().signOut();
                                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(getString(R.string.default_web_client_id))
                                            .requestEmail()
                                            .build();
                                    GoogleSignIn.getClient(SettingsActivity.this, gso).signOut()
                                            .addOnCompleteListener(SettingsActivity.this,
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            setResult(1);
                                                        }
                                                    });


                                    restartApp("LOGOUT");
                                }
                            }).setNegativeButton(getString(R.string.cancel), null)
                            .create();

                    if (!msgDialog.isShowing()) {
                        msgDialog.show();
                    }
                }else{
                    new GameToast(context, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                }
                break;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(msgDialog != null) {
            msgDialog.dismiss();
        }
    }

    private void saveData() {
        if(Network.state(this)) {
            //String uid = null;
            String emailNoDot = setting.getString("email", "").replace(".", "");

            /*if (user != null) {
                uid = user.getUid();
            }*/

            /*save user game data*/
            TransferData postData = new TransferData(goldManager.getGold(), goldManager.getGoldPerClick(), goldManager.getGoldPerSec(),
                    itemManager.getBlueGemCount(), itemManager.getYellowGemCount(), itemManager.getBoxCount(), itemManager.getKeyCount(), itemManager.getRewardItemCount(),
                    setting.getInt("boughtBoxCount", 0), setting.getInt("boughtKeyCount", 0),
                    itemManager.getBlueGemChance(), itemManager.getYellowGemChance(),
                    ItemFragment.getChampFragData(this), ItemFragment.getSkinFragData(context),
                    CollectData.getChampCollectData(this), CollectData.getSkinCollectData(this),
                    getSkillsLevel(), itemManager.getGameRewardItemCount());

            Map<String, Object> postDataHash = postData.toMap();
            Map<String, Object> childUpdate = new HashMap<>();
            childUpdate.put("/userinfo/"+emailNoDot, postDataHash); // 직접 경로(/userinfo/) 입력시 해당 데이터에서 기존 데이터를 지우지 않고 추가할 수 있음

            svRef.updateChildren(childUpdate);

        }else{
            new GameToast(context, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
        }
    }

    private void saveLank(){
        /*save ranking data*/
        String emailNoDot = setting.getString("email", "").replace(".", "");

        DBHelper dbHelper = new DBHelper(this);
        String nickname = setting.getString("nickname", "");
        int champCount = dbHelper.getChampCount();
        int skinCount = dbHelper.getSkinCount();
        int totalCount = champCount + skinCount;

        rankRef.child(emailNoDot).child("nickname").setValue(nickname);
        rankRef.child(emailNoDot).child("champCount").setValue(champCount);
        rankRef.child(emailNoDot).child("skinCount").setValue(skinCount);
        rankRef.child(emailNoDot).child("totalCount").setValue(totalCount);
        rankRef.child(emailNoDot).child("created").setValue(System.currentTimeMillis());

    }


    private void loadData() {
        if(Network.state(this)) {
            String emailNoDot = setting.getString("email", "").replace(".", "");

            dbRef.child(emailNoDot).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null) {
                        TransferData data = dataSnapshot.getValue(TransferData.class);
                        if(data == null){
                            new GameToast(context, getString(R.string.preference_ad_load_failed_message), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                            return;
                        }
                        setData(data);
                        new GameToast(context, getString(R.string.preference_ad_load_success_message), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                        restartApp("LOAD_DATA");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    new GameToast(context, getString(R.string.unknown_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            new GameToast(context, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        goldManager.setGoldPerClick(1);
        goldManager.setGoldPerSec(0);
        goldManager.setGold(0);
        itemManager.setBlueGemCount(0);
        itemManager.setYellowGemCount(0);
        itemManager.setBlueGemChance(0);
        itemManager.setYellowGemChance(0);
        itemManager.setBoxCount(0);
        itemManager.setKeyCount(0);
        itemManager.setRewardItemCount(0);

        editor.putInt("boughtBoxCount", 0);
        editor.putInt("boughtKeyCount", 0);
        editor.putString("myTier", "Unranked");

        int i=0;
        String[] itemName = getResources().getStringArray(R.array.pref_upgrade_item_name);

        while(i < itemName.length) {
            editor.putInt(itemName[i]+"NowSkillLevel", 0);
            editor.putFloat(itemName[i]+"SkillEffect", 0);
            editor.putInt(itemName[i]+"UpgradePrice", 0);
            i++;
        }

        dbHelper.deleteAllData(db);

        editor.commit();
        db.close();
    }


    private Map<String, Integer> getSkillsLevel() {
        String[] itemName = getResources().getStringArray(R.array.pref_upgrade_item_name);
        Map<String, Integer> skillLevelArr = new HashMap<>();

        int i=0;
        while(i < itemName.length) {
            int nowSkillLevel = setting.getInt(itemName[i]+"NowSkillLevel", 0);
            skillLevelArr.put(itemName[i]+"_key" , nowSkillLevel);
            i++;
        }
        return skillLevelArr;
    }

    private void setData(TransferData data) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        goldManager.setGold(data.getGold());
        goldManager.setGoldPerClick(data.getGoldPerClick());
        goldManager.setGoldPerSec(data.getGoldPerSec());
        itemManager.setBlueGemCount(data.getBlueGem());
        itemManager.setYellowGemCount(data.getYellowGem());
        itemManager.setBoxCount(data.getBox());
        itemManager.setKeyCount(data.getKey());
        itemManager.setRewardItemCount(data.getRewardItem());
        itemManager.setGameRewardItemCount(data.getGameRewardItem());
        editor.putInt("boughtBoxCount", data.getBoxBoughtCount());
        editor.putInt("boughtKeyCount", data.getKeyBoughtCount());

        /*불러올 데이터를 추가하기 전에 현재 데이터 모두 삭제*/
        dbHelper.deleteAllData(db);

        Map<String, ItemFragment> fragMap = data.getChampFragData();
        int i=0;

        /*get champ fragment*/
        if(fragMap != null) {
            while (i < fragMap.size()) {
                ItemFragment item = fragMap.get(i + "_key");
                db.execSQL("INSERT INTO champ_frag_table (champName, engChampName, buyBlueGem, sellBlueGem, count) VALUES(?,?,?,?,?)",
                        new String[]{item.getChampName(), item.getChampNameAndNum(), String.valueOf(item.getBuyBlueGem()), String.valueOf(item.getSellBlueGem()), String.valueOf(item.getItemCount())});
                i++;
            }
        }

        /*get skin fragment*/

        fragMap = data.getSkinFragData();
        if(fragMap != null) {
            i=0;
            while (i < fragMap.size()) {
                ItemFragment item = fragMap.get(i + "_key");
                db.execSQL("INSERT INTO skin_frag_table (champNameAndNum, skinName, buyYellowGem, sellYellowGem, count) VALUES(?,?,?,?,?)",
                        new String[]{item.getChampNameAndNum(), item.getSkinName(), String.valueOf(item.getBuyYellowGem()), String.valueOf(item.getSellYellowGem()), String.valueOf(item.getItemCount())});
                i++;
            }
        }

        /*get champ collection*/
        Map<String, CollectData> dataMap = data.getChampData();
        if(dataMap != null) {
            i=0;
            while (i < dataMap.size()) {
                CollectData item = dataMap.get(i + "_key");
                db.execSQL("INSERT INTO champ_table (champName, imgFileName, imgUrl) VALUES(?,?,?)",
                        new String[]{item.getName(), item.getImgFileName(), item.getUrl()});
                i++;
            }
        }

        /*get skin collection*/
        dataMap = data.getSkinData();
        if(dataMap != null) {
            i = 0;
            while (i < dataMap.size()) {
                CollectData item = dataMap.get(i + "_key");
                db.execSQL("INSERT INTO skin_table (skinName, imgFileName, skinUrl) VALUES(?,?,?)",
                        new String[]{item.getName(), item.getImgFileName(), item.getUrl()});
                i++;
            }
        }

        /* set skill level */
        Map<String, Integer> levelMap = data.getSkillLevel();
        String[] itemName = getResources().getStringArray(R.array.pref_upgrade_item_name);
        int[] upgradeInitPrice = getResources().getIntArray(R.array.upgrade_init_price);
        TypedArray howMuchUpgradeEffect = getResources().obtainTypedArray(R.array.how_much_upgrade_effect);
        TypedArray howMuchUpgradePrice = getResources().obtainTypedArray(R.array.how_much_upgrade_price);

        i=0;
        while(i < itemName.length) {
            int level = levelMap.get(itemName[i]+"_key");
            int upgradePrice = upgradeInitPrice[i];

            /*upgrade price 구하기*/
            for(int j=0; j<level; j++){
                upgradePrice *= howMuchUpgradePrice.getFloat(i, 0);
                upgradePrice += upgradeInitPrice[i];
            }

            upgradePrice -= upgradeInitPrice[i];

            editor.putInt(itemName[i]+"NowSkillLevel", level);
            editor.putFloat(itemName[i]+"SkillEffect", (level * howMuchUpgradeEffect.getFloat(i, 0)));
            editor.putInt(itemName[i]+"UpgradePrice", upgradePrice);
            i++;
        }

        itemManager.setBlueGemChance(data.getBluegemChance());
        itemManager.setYellowGemChance(data.getYellowgemChance());

        editor.commit();
        dbHelper.close();
        db.close();
    }


    /*private void loadSaveRewardedVideoAd(){
        mRewardedVideoAd.loadAd(getString(R.string.save_video_ad_id), new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
    }
    private void loadLoadRewardedVideoAd(){
        mRewardedVideoAd.loadAd(getString(R.string.load_video_ad_id), new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
    }
*/

    @Override
    protected void onUserLeaveHint() {  // 홈버튼 누르거나 액티비티 전환할 때
        if(MainActivity.bgmService != null) {
            MainActivity.bgmService.pauseBgm();      // 노래 멈춤
        }
        super.onUserLeaveHint();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.bgmService != null && setting.getBoolean("BgmState", true) == true) {
            MainActivity.bgmService.restartBgm();
        }
    }

    private int getTotalCount(String json){
        JSONObject jsonObject = null;
        int totalChampCount = 0;
        int totalSkinCount = 0;
        try {
            jsonObject = new JSONObject(json);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            Iterator iterator = dataObject.keys();

            while(iterator.hasNext()){
                totalChampCount++;
                String champName = iterator.next().toString();
                JSONObject champData = dataObject.getJSONObject(champName);
                JSONArray skinArray = champData.getJSONArray("skins");
                totalSkinCount+=(skinArray.length()-1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return (totalChampCount + totalSkinCount);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference.getKey().equals("change_lang_pref")){
            if(newValue.toString().equals("kr")){
                new LangUtil(context).changeLang(LangUtil.KR);
//                preference.setSummary(getString(R.string.language_kr_text));
            }else if(newValue.toString().equals("en")) {
                new LangUtil(context).changeLang(LangUtil.EN);
//                preference.setSummary(getString(R.string.language_en_text));
            }
            editor.putInt("lang", LangUtil.getLang(context));
            editor.commit();

            // 언어 변경후 재시작
            restartApp("CHANGE_LANG");
        }
        return true;
    }
}