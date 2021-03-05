package com.app.buna.boxsimulatorforlol.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.Adapter.FactoryRecyclerAdapter;
import com.app.buna.boxsimulatorforlol.DB.DBHelper;
import com.app.buna.boxsimulatorforlol.DTO.ItemFragment;
import com.app.buna.boxsimulatorforlol.Manager.ItemManager;
import com.app.buna.boxsimulatorforlol.Manager.SoundManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.app.buna.boxsimulatorforlol.Util.ScreenProportion;
import com.app.buna.boxsimulatorforlol.VO.FragType;
import com.app.buna.boxsimulatorforlol.VO.ItemCode;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FactoryActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    private TextView boxTextView;
    private TextView keyTextView;
    private TextView blueGemTextView;
    private TextView yellowGemTextView;

    private SoundManager soundManager;
    private int tabCloseSoundId, openBoxViewSoundId;

    public ImageView forgeCycleImageView;

    private ItemManager itemManager;

    private RecyclerView factoryRecyclerView;
    private FactoryRecyclerAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private ArrayList<ItemFragment> itemList;
    private ArrayList<ItemFragment> tempList = new ArrayList<>();
    private ProgressBar itemLoadBar;
    private int loadMoreSize = 21;
    private boolean isLoading;


    public ImageView firstForgePreImage, secondForgePreImage, thirdForgePreImage;
    public LinearLayout forgeElePreView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_factory);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        itemManager = new ItemManager(FactoryActivity.this);

        setting();
    }



    private void setting(){
        forgeCycleImageView = findViewById(R.id.forge_cycle_image_view);
        forgeCycleImageView.setOnClickListener(this);
        forgeElePreView = findViewById(R.id.forge_element_pre_view);
        firstForgePreImage = findViewById(R.id.first_forge_pre_image_view);
        secondForgePreImage = findViewById(R.id.second_forge_pre_image_view);
        thirdForgePreImage = findViewById(R.id.third_forge_pre_image_view);

        itemLoadBar = findViewById(R.id.item_more_progressBar);

        setSoundManager();
        setRecycler();
        setResource();
    }

    public void setRecycler() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                factoryRecyclerView = findViewById(R.id.factory_recycler_view);

                itemList = getItemFragments();
                setFirstData();

                adapter = new FactoryRecyclerAdapter(FactoryActivity.this, tempList, FactoryActivity.this);
                adapter.setHasStableIds(true);
                adapter.setItemSize(new ScreenProportion(FactoryActivity.this).getItemSize((float)5.2));
                gridLayoutManager = new GridLayoutManager(FactoryActivity.this, 4);
                gridLayoutManager.setItemPrefetchEnabled(true);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (itemList.get(position).isItem()) {
                            return 1;
                        }else{
                            return 4;
                        }
                    }
                });

                factoryRecyclerView.setAdapter(adapter);
                factoryRecyclerView.setLayoutManager(gridLayoutManager);
                initScrollListener();
                itemLoadBar.setVisibility(View.GONE);

            }
        });

    }

    private void setFirstData() {
        int defaultSize = itemList.size();
        int rotationSize;

        /*loadMoreSize보다 적게 있을 경우엔 있는 것만 가져옴*/
        if(defaultSize < loadMoreSize){
            rotationSize = defaultSize;
        }else {
            rotationSize = loadMoreSize;
        }
        for(int i=0; i<rotationSize; i++){
            tempList.add(itemList.get(i));
        }
    }

    private void dataMore(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentSize = tempList.size();  // 현재 보여지는 데이터 개수
                int totalSize = itemList.size();    // 총 불러와야 할 전체 데이터 개수
                int nextLimit = currentSize + loadMoreSize-1;   // 다음에 불러올 데이터 개수

                for(int i=currentSize; i<nextLimit; i++) {
                    if(i == totalSize) {    // 불러오는 중에 전체 크기만큼 다 불러와 더 불러올 데이터가 없으면 return해버림
                        return;
                    }
                    tempList.add(itemList.get(i));  // 아닐 경우 계속해서 가져옴
                }

                adapter.notifyDataSetChanged(); // adapter에게 데이터가 변경되었음을 알림
                isLoading = false;  // loading 종료
            }
        }, 1200);
    }

    private void initScrollListener() {
        factoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                if (isLoading == false) {
                    // 화면의 마지막까지 스크롤 했을 때 데이터 더 불러옴, 로딩바 보여줌
                    if (gridLayoutManager != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() == tempList.size() -1) {
                        dataMore();
                        isLoading = true;
                        itemLoadBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                itemLoadBar.setVisibility(View.GONE);
                            }
                        }, 1200);
                    }
                }
            }
        });
    }


    private ArrayList<ItemFragment> getItemFragments() {
        ArrayList<ItemFragment> dataList = new ArrayList<>();

        /*example*/

        dataList.add(new ItemFragment(false, getString(R.string.material)));
        dataList.add(new ItemFragment(true, FragType.FRAG_MATERIAL, ItemCode.BOX, getString(R.string.box_text), itemManager.getBoxCount(), R.drawable.item_box));
        dataList.add(new ItemFragment(true, FragType.FRAG_MATERIAL, ItemCode.KEY, getString(R.string.key_text), itemManager.getKeyCount(), R.drawable.item_key));
        dataList.add(new ItemFragment(true, FragType.FRAG_MATERIAL, ItemCode.REWARD_ITEM, getString(R.string.rewarded_item_text), itemManager.getRewardItemCount(), R.drawable.reward_item_icon));

        addChampionFragData(dataList);
        addSkinFragData(dataList);
        return dataList;
    }

    private void addChampionFragData(ArrayList<ItemFragment> dataList){
        dataList.add(new ItemFragment(false, getString(R.string.champions_text)));

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM champ_frag_table WHERE count>?", new String[]{"0"});
        while(cursor.moveToNext()){
            ItemFragment itemFragment = new ItemFragment();

            itemFragment.setItem(true);
            itemFragment.setFragType(FragType.FRAG_CHAMPION);
            itemFragment.setChampName(cursor.getString(0));
            itemFragment.setChampNameAndNum(cursor.getString(1));
            itemFragment.setBuyBlueGem(cursor.getInt(2));
            itemFragment.setSellBlueGem(cursor.getInt(3));
            itemFragment.setItemCount(cursor.getInt(4));

            dataList.add(itemFragment);
        }
        db.close();
    }

    private void addSkinFragData(ArrayList<ItemFragment> dataList){
        dataList.add(new ItemFragment(false, getString(R.string.skins_text)));

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM skin_frag_table WHERE count>0", null);
        while(cursor.moveToNext()){
            ItemFragment itemFragment = new ItemFragment(
                    true,
                    FragType.FRAG_SKIN,
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4)
            );

            dataList.add(itemFragment);
        }
        db.close();
    }


    private void setSoundManager(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                soundManager = new SoundManager(FactoryActivity.this);
                soundManager.init();

                tabCloseSoundId = soundManager.loadSound(SoundManager.TYPE_TAB_CLOSE);
                openBoxViewSoundId = soundManager.loadSound(SoundManager.TYPE_SHOP_OPEN);
            }
        }).start();
    }



    private void setResource() {
        keyTextView = findViewById(R.id.key_text_view);
        boxTextView = findViewById(R.id.box_text_view);
        blueGemTextView = findViewById(R.id.blue_gem_text_view);
        yellowGemTextView = findViewById(R.id.yellow_gem_text_view);


        refreshResource();
    }



    public void refreshResource(){
        boxTextView.setText(Integer.toString(itemManager.getBoxCount()));
        keyTextView.setText(Integer.toString(itemManager.getKeyCount()));
        blueGemTextView.setText(Integer.toString(itemManager.getBlueGemCount()));
        yellowGemTextView.setText(Integer.toString(itemManager.getYellowGemCount()));
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelForge(ArrayList<ImageView> forgeImages, ArrayList<ImageView> forgeBorders, boolean isDelay){
        adapter.isForging = false;

        for(ImageView selectedImageView : forgeImages){
            Glide.with(this).load(R.drawable.empty_forge_box).into(selectedImageView);
        }
        for(ImageView selectedImageView : forgeBorders){
            selectedImageView.setBackground(null);
        }
        for(ImageView selectedImageView : adapter.forgingImageList){
            selectedImageView.setColorFilter(null);
        }
        if(isDelay) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(FactoryActivity.this).load(R.drawable.empty_forge_box).into(firstForgePreImage);
                    Glide.with(FactoryActivity.this).load(R.drawable.empty_forge_box).into(secondForgePreImage);
                    Glide.with(FactoryActivity.this).load(R.drawable.empty_forge_box).into(thirdForgePreImage);
                    adapter.initForgeImages();
                }
            }, 700);
        }else{
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(FactoryActivity.this).load(R.drawable.empty_forge_box).into(firstForgePreImage);
                    Glide.with(FactoryActivity.this).load(R.drawable.empty_forge_box).into(secondForgePreImage);
                    Glide.with(FactoryActivity.this).load(R.drawable.empty_forge_box).into(thirdForgePreImage);
                    adapter.initForgeImages();
                }
            });
        }
        forgeImages.clear();
        forgeBorders.clear();
        adapter.selectedDatas.clear();
        adapter.forgingImageList.clear();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(adapter.isForging){
            forgeCycleImageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_hide_cycle));
            forgeElePreView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_hide_pre_view));
            cancelForge(adapter.forgeImages, adapter.forgeBorders, true);
            return;
        }
        if(soundManager.isLoaded()) {
            soundManager.play(tabCloseSoundId);
        }
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.bgmService != null && setting.getBoolean("BgmState", true) == true) {
            MainActivity.bgmService.restartBgm();
        }
        refreshResource();
    }

    public void refreshRecyclerView(){
        refreshResource();
        final ArrayList<ItemFragment> itemList = getItemFragments();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (itemList.get(position).isItem()) {
                    return 1;
                }else{
                    return 4;
                }
            }
        });
        adapter.updateReceiptsList(itemList);
    }


    @Override
    protected void onUserLeaveHint() {  // 홈버튼 누르거나 액티비티 전환할 때
        if(MainActivity.bgmService != null) {
            MainActivity.bgmService.pauseBgm();      // 노래 멈춤
        }
        super.onUserLeaveHint();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forge_cycle_image_view:
                if(adapter.forgeDialog != null && adapter.forgeDialog.isShowing()){
                    adapter.forgeDialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            forgeCycleImageView.startAnimation(AnimationUtils.loadAnimation(FactoryActivity.this, R.anim.anim_hide_cycle));
                        }
                    }, 700);
                    forgeCycleImageView.setVisibility(View.GONE);
                }else if(adapter.isForging){
                    adapter.forgeCloseButton.setVisibility(View.VISIBLE);
                    adapter.clearButton.setVisibility(View.VISIBLE);
                    forgeCycleImageView.setVisibility(View.VISIBLE);
                    adapter.forgeButton.startAnimation(AnimationUtils.loadAnimation(FactoryActivity.this, R.anim.loop_rotation_anim));
                    adapter.forgeDialog.show();
                }
                break;
        }
    }
}


