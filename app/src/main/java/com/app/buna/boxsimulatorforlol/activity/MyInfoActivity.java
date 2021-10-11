package com.app.buna.boxsimulatorforlol.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;
import com.app.buna.boxsimulatorforlol.adapter.MyInfoSkillRecyclerAdapter;
import com.app.buna.boxsimulatorforlol.db.DBHelper;
import com.app.buna.boxsimulatorforlol.dto.UpgradeData;
import com.app.buna.boxsimulatorforlol.manager.GoldManager;
import com.app.buna.boxsimulatorforlol.manager.ItemManager;
import com.app.buna.boxsimulatorforlol.manager.TierManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.JsonUtil;
import com.app.buna.boxsimulatorforlol.util.LangUtil;
import com.app.buna.boxsimulatorforlol.util.MyInfoSkillDecoration;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextRoundCornerProgressBar champProgress, skinProgress;
    private ImageView backButton, tierImageView;
    private TextView myNameText, tierText, collectionExpText, goldPerSecText, goldPerClickText;
    private TextView goldText, bluegemText, yellowGemText;

    private GoldManager goldManager;
    private ItemManager itemManager;

    private RecyclerView skillInfoRecyclerView;
    private MyInfoSkillRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<UpgradeData> upgradeList;

    private SharedPreferences setting;

    private int myChampCount = 0;
    private int mySkinCount = 0;

    private int totalChampCount = 0;
    private int totalSkinCount = 0;

    private String myTier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_my_info);


        setting = getSharedPreferences("setting", MODE_PRIVATE);

        goldManager = new GoldManager(this);
        itemManager = new ItemManager(this);

        settingView();
    }

    private void settingView(){
        champProgress = findViewById(R.id.champ_progress);
        skinProgress = findViewById(R.id.skin_progress);
        backButton = findViewById(R.id.my_info_back_image_view);
        goldPerClickText = findViewById(R.id.gold_per_click_text);
        goldPerSecText = findViewById(R.id.gold_per_sec_text);
        myNameText = findViewById(R.id.my_name_text_view);
        tierImageView = findViewById(R.id.my_tier);
        tierText = findViewById(R.id.my_tier_text);
        collectionExpText = findViewById(R.id.required_level_up_text_view);
        goldText = findViewById(R.id.my_gold_text);
        bluegemText = findViewById(R.id.my_blue_gem_text);
        yellowGemText = findViewById(R.id.my_yellow_gem_text);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setRecyclerView();
            }
        });

        jsonParsing(new JsonUtil(MyInfoActivity.this).getJsonString("champion.json"));

        goldText.setText(getString(R.string.gold_text) +" : " + String.format("%.0f", goldManager.getGold()));
        bluegemText.setText(getString(R.string.blue_gem_text) +" : " + itemManager.getBlueGemCount());
        yellowGemText.setText(getString(R.string.yellow_gem_text) +" : " + itemManager.getYellowGemCount());

        setChampProgress();
        setSkinProgress();
        collectionExpText.setText(getString(R.string.collection_exp_text) + " : "
                + (myChampCount+mySkinCount) + " / " + (totalChampCount+totalSkinCount));

        setTierImage(totalChampCount+totalSkinCount);
        setTierText();
        backButton.setOnClickListener(this);
        goldPerClickText.setText("Gold Per Click : " + new BigDecimal(goldManager.getGoldPerClick()).toString());
        goldPerSecText.setText("Gold Per Second : " + String.format("%.1f",goldManager.getGoldPerSec()));
        myNameText.setText(setting.getString("nickname", getString(R.string.temp_user_name)));

    }

    private void setChampProgress(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT champName FROM champ_table", null);
        myChampCount = cursor.getCount();
        champProgress.setProgressText(myChampCount + "/" + totalChampCount);
        champProgress.setProgress(myChampCount);
        champProgress.setMax(totalChampCount);
        dbHelper.close();
        db.close();
    }

    private void setSkinProgress(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT skinName FROM skin_table", null);
        mySkinCount = cursor.getCount();
        skinProgress.setProgressText(mySkinCount + "/" + totalSkinCount);
        skinProgress.setProgress(mySkinCount);
        skinProgress.setMax(totalSkinCount);
        dbHelper.close();
        db.close();
    }

    private void setTierImage(double maxCount){
        myTier = new TierManager(this).getMyTier(maxCount);
        switch (myTier){
            case "Unranked":
                Glide.with(this).load(R.drawable.tier_unrank).into(tierImageView);
                break;
            case "Iron 4":
                Glide.with(this).load(R.drawable.tier_iron4).into(tierImageView);
                break;
            case "Iron 3":
                Glide.with(this).load(R.drawable.tier_iron3).into(tierImageView);
                break;
            case "Iron 2":
                Glide.with(this).load(R.drawable.tier_iron2).into(tierImageView);
                break;
            case "Iron 1":
                Glide.with(this).load(R.drawable.tier_iron1).into(tierImageView);
                break;
            case "Bronze 4":
                Glide.with(this).load(R.drawable.tier_bronze4).into(tierImageView);
                break;
            case "Bronze 3":
                Glide.with(this).load(R.drawable.tier_bronze3).into(tierImageView);
                break;
            case "Bronze 2":
                Glide.with(this).load(R.drawable.tier_bronze2).into(tierImageView);
                break;
            case "Bronze 1":
                Glide.with(this).load(R.drawable.tier_bronze1).into(tierImageView);
                break;
            case "Silver 4":
                Glide.with(this).load(R.drawable.tier_silver4).into(tierImageView);
                break;
            case "Silver 3":
                Glide.with(this).load(R.drawable.tier_silver3).into(tierImageView);
                break;
            case "Silver 2":
                Glide.with(this).load(R.drawable.tier_silver2).into(tierImageView);
                break;
            case "Silver 1":
                Glide.with(this).load(R.drawable.tier_silver1).into(tierImageView);
                break;
            case "Gold 4":
                Glide.with(this).load(R.drawable.tier_gold4).into(tierImageView);
                break;
            case "Gold 3":
                Glide.with(this).load(R.drawable.tier_gold3).into(tierImageView);
                break;
            case "Gold 2":
                Glide.with(this).load(R.drawable.tier_gold2).into(tierImageView);
                break;
            case "Gold 1":
                Glide.with(this).load(R.drawable.tier_gold1).into(tierImageView);
                break;
            case "Platinum 4":
                Glide.with(this).load(R.drawable.tier_platinum4).into(tierImageView);
                break;
            case "Platinum 3":
                Glide.with(this).load(R.drawable.tier_platinum3).into(tierImageView);
                break;
            case "Platinum 2":
                Glide.with(this).load(R.drawable.tier_platinum2).into(tierImageView);
                break;
            case "Platinum 1":
                Glide.with(this).load(R.drawable.tier_platinum1).into(tierImageView);
                break;
            case "Diamond 4":
                Glide.with(this).load(R.drawable.tier_diamond4).into(tierImageView);
                break;
            case "Diamond 3":
                Glide.with(this).load(R.drawable.tier_diamond3).into(tierImageView);
                break;
            case "Diamond 2":
                Glide.with(this).load(R.drawable.tier_diamond2).into(tierImageView);
                break;
            case "Diamond 1":
                Glide.with(this).load(R.drawable.tier_diamond1).into(tierImageView);
                break;
            case "Master 1":
                Glide.with(this).load(R.drawable.tier_master1).into(tierImageView);
                break;
            case "Grandmaster 1":
                Glide.with(this).load(R.drawable.tier_grandmaster1).into(tierImageView);
                break;
            case "Challenger 1":
                Glide.with(this).load(R.drawable.tier_challenger1).into(tierImageView);
                break;
        }
    }

    private void setTierText(){
        tierText.setText(myTier);
        if(myTier.equals("Unranked")){
            tierText.setTextColor(getResources().getColor(R.color.my_info_unrank_text_color));
        }
    }

    private void setRecyclerView(){
        upgradeList = getData();

        skillInfoRecyclerView = findViewById(R.id.my_info_skill_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new MyInfoSkillRecyclerAdapter(this, upgradeList);
        skillInfoRecyclerView.addItemDecoration(new MyInfoSkillDecoration(25));

        skillInfoRecyclerView.setLayoutManager(linearLayoutManager);
        skillInfoRecyclerView.setAdapter(adapter);
    }

    private ArrayList<UpgradeData> getData() {
        ArrayList<UpgradeData> dataList = new ArrayList<>();

        String[] itemName = getResources().getStringArray(R.array.upgrade_item_name);
        String[] prefItemName = getResources().getStringArray(R.array.pref_upgrade_item_name);
        String[] itemDesc = getResources().getStringArray(R.array.upgrade_shop_desc);
        int[] upgradeType = getResources().getIntArray(R.array.upgrade_type);
        int[] maxSkillLevel = getResources().getIntArray(R.array.max_skill_level);
        TypedArray itemImage = getResources().obtainTypedArray(R.array.upgrade_item_image);
        int[] tierCondition = getResources().getIntArray((R.array.upgrade_tier_condition));

        int i = 0;
        while(i < itemName.length) {
            int nowSkillLevel = setting.getInt(prefItemName[i]+"NowSkillLevel", 0);
            float effectValue = setting.getFloat(prefItemName[i]+"SkillEffect", 0);

            UpgradeData data = new UpgradeData(upgradeType[i], itemName[i], itemDesc[i], nowSkillLevel, maxSkillLevel[i],
                    itemImage.getResourceId(i, 0), effectValue, tierCondition[i]);
            dataList.add(data);
            i++;
        }
        return dataList;
    }

    private void jsonParsing(String json){
        JSONObject jsonObject = null;
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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.my_info_back_image_view:
                finish();
                break;
        }
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
        finish();
    }
}
