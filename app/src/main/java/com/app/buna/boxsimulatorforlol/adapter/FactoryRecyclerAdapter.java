package com.app.buna.boxsimulatorforlol.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.boxsimulatorforlol.activity.FactoryActivity;
import com.app.buna.boxsimulatorforlol.db.DBHelper;
import com.app.buna.boxsimulatorforlol.dto.BoxResultData;
import com.app.buna.boxsimulatorforlol.dto.ItemFragment;
import com.app.buna.boxsimulatorforlol.manager.GoldManager;
import com.app.buna.boxsimulatorforlol.manager.ItemManager;
import com.app.buna.boxsimulatorforlol.manager.SoundManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.JsonUtil;
import com.app.buna.boxsimulatorforlol.vo.FragType;
import com.app.buna.boxsimulatorforlol.vo.ItemCode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.cy.dialog.BaseDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static android.view.View.GONE;

public class FactoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ItemFragment> itemData;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private View boxOpenView, boxResultView, exchangeView, forgeView;
    private BaseDialog boxOpenDialog;
    public BaseDialog forgeDialog;
    private LinearLayout boxOpenButton, boxAllOpenButton, boxResultGetButton;

    private AlertDialog boxResultDialog, exchangeDialog;
    private ImageView boxResultImage, boxBorderImage;
    private TextView resultNameText, resultTypeText;

    private FactoryActivity factoryActivity;

    private ArrayList<BoxResultData> resultData;
    private ArrayList<BoxResultData> openResultData;
    public ArrayList<ImageView> forgeImages;
    public ArrayList<ImageView> forgeBorders;
    private int resultSize;

    private TextView exchangeNameText, exchangeTypeText, disenchantPriceTextView, upgradePriceTextView, upgradeTextView, upgradeDescTextView, isHavingTextView;
    private ImageView exchangePriceImage, upgradePriceImage, closeButton, boxOpenCloseButton;
    private RelativeLayout forgeLayout, disenchantLayout, upgradeLayout;
    private LinearLayout isHavingLayout;

    TextView openTextView, openAllTextView, getResultTextView;

    ItemManager itemManager;
    GoldManager goldManager;

    SoundManager soundManager;
    int boxDialogOpenSoundId, boxDialogCloseSoundId, getResultSoundId;

    public ImageView forgeButton, forgeCloseButton;
    public LinearLayout clearButton;
    private ImageView firstForgeImage, secondForgeImage, thirdForgeImage;
    private ImageView firstForgeBorderImage, secondForgeBorderImage, thirdForgeBorderImage;

    int count = 0;  // 상자 한번에 뽑기 카운트를 위한 변수

    public boolean isForging = false;
    public int selectedCount = 0, selectedFragType;
    public ArrayList<ItemFragment> selectedDatas;
    public ArrayList<ImageView> forgingImageList;

    int itemSize;

    private boolean isDelayed = true;

    private DrawableImageViewTarget gifImage;

    public FactoryRecyclerAdapter(Context context, ArrayList<ItemFragment> itemData, FactoryActivity factoryActivity){
        this.context = context;
        this.itemData = itemData;
        soundManager = new SoundManager(context);
        soundManager.init();
        boxDialogOpenSoundId = soundManager.loadSound(SoundManager.TYPE_SHOP_OPEN);
        boxDialogCloseSoundId = soundManager.loadSound(SoundManager.TYPE_SHOP_CLOSE);
        getResultSoundId = soundManager.loadSound(SoundManager.TYPE_GET_RESULT);

        itemManager = new ItemManager(context);
        goldManager = new GoldManager(context);
        this.factoryActivity = factoryActivity;


        setResultData();

        setBoxOpenDialog();
        setBoxResultDialog();
        setExchangeDialog();
        setForgeDialog();
    }

    public void setItemSize(int itemSize){
        this.itemSize = itemSize;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        if(viewType == TYPE_HEADER){
            view = LayoutInflater.from(context).inflate(R.layout.factory_recycler_header, parent, false);
            holder = new HeaderViewHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.factory_recycler_item, parent, false);
            holder = new ItemViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.onBind(itemData.get(position));
        } else if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.onBind(context, itemData.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return itemData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(itemData.get(position).isItem() == false){
            return TYPE_HEADER;
        }else{
            return TYPE_ITEM;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void settingDialog() {
        boxOpenDialog = new BaseDialog(context);
        boxOpenDialog.contentView(boxOpenView)
                .layoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                .dimAmount(0.5f)
                .animType(BaseDialog.AnimInType.BOTTOM)
                .canceledOnTouchOutside(false)
                .setCancelable(false);

        boxOpenDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                soundManager.play(boxDialogOpenSoundId);
            }
        });


        boxOpenButton = boxOpenView.findViewById(R.id.box_open_button);
        boxAllOpenButton = boxOpenView.findViewById(R.id.box_all_open_button);
        boxOpenCloseButton = boxOpenView.findViewById(R.id.box_open_cancel_image);

        openTextView = boxOpenView.findViewById(R.id.open_text_view);
        openAllTextView = boxOpenView.findViewById(R.id.open_all_text_view);
        boxOpenView.findViewById(R.id.box_open_cancel_image).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(boxOpenDialog.isShowing()){
                            boxOpenDialog.dismiss();
                        }
                    }
                });

        if(itemManager.getBoxCount() < 1 && itemManager.getKeyCount() < 1){
            openTextView.setTextColor(context.getResources().getColor(R.color.button_disabled_text_color));
            boxOpenButton.setEnabled(false);
        }else{
            openTextView.setTextColor(context.getResources().getColor(R.color.item_price_color));
            boxOpenButton.setEnabled(true);
        }

        if(itemManager.getBoxCount() <= 1 && itemManager.getKeyCount() <= 1){
            boxAllOpenButton.setVisibility(GONE);
        }else{
            boxAllOpenButton.setVisibility(View.VISIBLE);
            openAllTextView.setTextColor(context.getResources().getColor(R.color.item_price_color));
        }
    }

    private void settingForgeDialog(){
        forgeDialog = new BaseDialog(context);
        forgeDialog.contentView(forgeView)
                .layoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                .gravity(Gravity.CENTER)
                .dimAmount(0.5f)
                .animType(BaseDialog.AnimInType.BOTTOM)
                .canceledOnTouchOutside(false)
                .setCancelable(false);
        forgeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                soundManager.play(boxDialogCloseSoundId);
            }
        });

        forgeButton = forgeView.findViewById(R.id.forge_button);
        clearButton = forgeView.findViewById(R.id.forge_clear_button);
        forgeCloseButton = forgeView.findViewById(R.id.forge_close_button);
        firstForgeImage = forgeView.findViewById(R.id.first_forge_image_view);
        secondForgeImage = forgeView.findViewById(R.id.second_forge_image_view);
        thirdForgeImage = forgeView.findViewById(R.id.third_forge_image_view);
        firstForgeBorderImage = forgeView.findViewById(R.id.first_forge_border_image_view);
        secondForgeBorderImage = forgeView.findViewById(R.id.second_forge_border_image_view);
        thirdForgeBorderImage = forgeView.findViewById(R.id.third_forge_border_image_view);

        forgeCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(forgeDialog.isShowing()){
                    forgeDialog.dismiss();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
             @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
             @Override
             public void onClick(View view) {
                 soundManager.play(getResultSoundId);
                 if(isForging) {
                     factoryActivity.cancelForge(forgeImages, forgeBorders, false);
                     isForging = true;
                 }
             }
        });

        forgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCount == 3 && isDelayed) {
                    //합성

                    isDelayed = false; // 조합버튼 마구잡이로 클릭하는 것 방지

                    forgeCloseButton.setVisibility(GONE);
                    clearButton.setVisibility(GONE);
                    factoryActivity.forgeCycleImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_hide_cycle));
                    factoryActivity.forgeElePreView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_hide_pre_view));
                    forgeView.findViewById(R.id.first_forge_close_image_view).setVisibility(View.VISIBLE);
                    forgeView.findViewById(R.id.first_forge_close_image_view).startAnimation(AnimationUtils.loadAnimation(context, R.anim.forge_first_anim));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            forgeView.findViewById(R.id.second_forge_close_image_view).setVisibility(View.VISIBLE);
                            forgeView.findViewById(R.id.second_forge_close_image_view).startAnimation(AnimationUtils.loadAnimation(context, R.anim.forge_second_anim));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    forgeView.findViewById(R.id.third_forge_close_image_view).setVisibility(View.VISIBLE);
                                    forgeView.findViewById(R.id.third_forge_close_image_view).startAnimation(AnimationUtils.loadAnimation(context, R.anim.forge_third_anim));
                                }
                            }, 500);
                        }
                    }, 500);

                    forgeButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.more_faster_rotation_anim));
                    new Handler().postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            count = 1;
                            openResultData.clear();
                            getForgeResultToDB(selectedFragType);    // 데이터에 저장돼있는 결과 랜덤으로 가져와서 DB에 저장하고 dialog view에 띄움
                            deleteFromDB(selectedFragType);
                            forgeDialog.dismiss();
                            boxResultDialog.show();

                            forgeImages.add(firstForgeImage);
                            forgeImages.add(secondForgeImage);
                            forgeImages.add(thirdForgeImage);
                            forgeBorders.add(firstForgeBorderImage);
                            forgeBorders.add(secondForgeBorderImage);
                            forgeBorders.add(thirdForgeBorderImage);

                            forgeView.findViewById(R.id.first_forge_close_image_view).setVisibility(View.GONE);
                            forgeView.findViewById(R.id.second_forge_close_image_view).setVisibility(View.GONE);
                            forgeView.findViewById(R.id.third_forge_close_image_view).setVisibility(View.GONE);

                            factoryActivity.cancelForge(forgeImages, forgeBorders, true);
                            isDelayed = true;
                        }
                    }, 4000);
                }
            }
        });
    }

    public void initForgeImages(){
        Glide.with(context).load(R.drawable.empty_forge_box).into(firstForgeImage);
        Glide.with(context).load(R.drawable.empty_forge_box).into(secondForgeImage);
        Glide.with(context).load(R.drawable.empty_forge_box).into(thirdForgeImage);
        firstForgeBorderImage.setBackground(null);
        secondForgeBorderImage.setBackground(null);
        thirdForgeBorderImage.setBackground(null);
        selectedCount = 0;
    }

    private void deleteFromDB(int fragType){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try{
            if(fragType == FragType.FRAG_CHAMPION){
                db.execSQL("UPDATE champ_frag_table SET count=count-1 WHERE champName=?", new String[]{selectedDatas.get(0).getChampName()});
                db.execSQL("UPDATE champ_frag_table SET count=count-1 WHERE champName=?", new String[]{selectedDatas.get(1).getChampName()});
                db.execSQL("UPDATE champ_frag_table SET count=count-1 WHERE champName=?", new String[]{selectedDatas.get(2).getChampName()});
                db.execSQL("DELETE FROM champ_frag_table WHERE count<1");
            }else if(fragType == FragType.FRAG_SKIN){
                db.execSQL("UPDATE skin_frag_table SET count=count-1 WHERE skinName=?", new String[]{selectedDatas.get(0).getSkinName()});
                db.execSQL("UPDATE skin_frag_table SET count=count-1 WHERE skinName=?", new String[]{selectedDatas.get(1).getSkinName()});
                db.execSQL("UPDATE skin_frag_table SET count=count-1 WHERE skinName=?", new String[]{selectedDatas.get(2).getSkinName()});
                db.execSQL("DELETE FROM skin_frag_table WHERE count<1");
            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        dbHelper.close();
        db.close();
    }

    private void settingResultDialog() {
        boxResultDialog = new AlertDialog.Builder(context, R.style.DialogBlackBackground)
                .setView(boxResultView)
                .setCancelable(false)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        soundManager.play(boxDialogOpenSoundId);
                    }
                }).create();

        boxResultImage = boxResultView.findViewById(R.id.box_result_image_view);
        boxBorderImage = boxResultView.findViewById(R.id.box_result_border_image_view);
        resultTypeText = boxResultView.findViewById(R.id.result_frag_type_text_view);
        resultNameText = boxResultView.findViewById(R.id.result_name_text_view);
        getResultTextView = boxResultView.findViewById(R.id.get_text_view);

        boxResultDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                --count;
                if (count != 0) {
                    BoxResultData boxResultData = openResultData.get(count-1);

                    boxResultImage.setImageResource(context.getResources().getIdentifier
                            (boxResultData.getEngChampName().toLowerCase() + "_" + boxResultData.getNum(), "drawable", context.getPackageName()));

                    if (boxResultData.getNum() == 0) {
                        resultTypeText.setText(context.getString(R.string.champ_shard_text));
                        resultNameText.setText(boxResultData.getChampName());
                        Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_champ_background)).into(boxBorderImage);

                    } else {
                        resultTypeText.setText(context.getString(R.string.skin_shard_text));
                        resultNameText.setText(boxResultData.getSkinName());
                        Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_skin_background)).into(boxBorderImage);
                    }
                    boxResultDialog.show();

                }
            }
        });

        boxResultGetButton = boxResultView.findViewById(R.id.box_result_get_button);
        boxResultGetButton.setOnClickListener(new View.OnClickListener() {  /*영구 획득하기 버튼 click listener*/
            @Override
            public void onClick(View view) {
                soundManager.play(getResultSoundId);
                boxResultDialog.dismiss();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        factoryActivity.refreshRecyclerView();
                    }
                });

            }
        });
    }

    private void settingExchangeDialog(){
        exchangeNameText = exchangeView.findViewById(R.id.exchange_name_text);
        exchangeTypeText = exchangeView.findViewById(R.id.exchange_type_text);;
        disenchantPriceTextView = exchangeView.findViewById(R.id.disenchant_price_text_view);
        upgradePriceTextView = exchangeView.findViewById(R.id.upgrade_price_text_view);
        upgradeTextView = exchangeView.findViewById(R.id.upgrade_text_view);
        upgradeDescTextView = exchangeView.findViewById(R.id.upgrade_desc_text_view);
        isHavingLayout = exchangeView.findViewById(R.id.is_having_layout);
        isHavingTextView = exchangeView.findViewById(R.id.is_having_text_view);
        exchangePriceImage = exchangeView.findViewById(R.id.exchange_price_type_image_view);
        upgradePriceImage = exchangeView.findViewById(R.id.upgrade_price_type_image_view);
        closeButton = exchangeView.findViewById(R.id.exchange_close_button);

        forgeLayout = exchangeView.findViewById(R.id.forge_layout);
        disenchantLayout = exchangeView.findViewById(R.id.disenchant_layout);
        upgradeLayout = exchangeView.findViewById(R.id.frag_upgrade_layout);

        closeButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(exchangeDialog.isShowing()){
                     exchangeDialog.cancel();
                 }
             }
        });

        exchangeDialog = new AlertDialog.Builder(context, R.style.DialogBlackBackground)
                .setView(exchangeView)
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        soundManager.play(boxDialogCloseSoundId);
                    }
                }).create();
    }

    private void getOpenRewardItem(){
        getResultTextView.setText(context.getString(R.string.reward_result_text));

        int itemProbability = new Random().nextInt(7);
        Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_champ_background)).into(boxBorderImage);
        int rewardCount;

        switch (itemProbability){
            case 0:
            case 1:
            case 2:
                rewardCount = new Random().nextInt(4) + 2;

                boxResultImage.setImageResource(R.drawable.item_box_and_key);
                resultTypeText.setText(context.getString(R.string.ad_reward_type, rewardCount));
                resultNameText.setText(context.getString(R.string.ad_reward_0));
                itemManager.addBoxCount(rewardCount);
                itemManager.addKeyCount(rewardCount);
                break;
            case 3:
                rewardCount = new Random().nextInt(2) + 1;

                boxResultImage.setImageResource(R.drawable.reward_item_icon);
                resultTypeText.setText(context.getString(R.string.ad_reward_type, rewardCount));
                resultNameText.setText(context.getString(R.string.ad_reward_1));
                itemManager.addRewardItem(rewardCount);
                break;
            case 4:
                rewardCount = new Random().nextInt(300) + 150;

                boxResultImage.setImageResource(R.drawable.item_blue_gem);
                resultTypeText.setText(context.getString(R.string.ad_reward_type, rewardCount));
                resultNameText.setText(context.getString(R.string.ad_reward_2));
                itemManager.addBlueGemCount(rewardCount);
                break;
            case 5:
                rewardCount = new Random().nextInt(300) + 150;

                boxResultImage.setImageResource(R.drawable.item_yellow_gem);
                resultTypeText.setText(context.getString(R.string.ad_reward_type, rewardCount));
                resultNameText.setText(context.getString(R.string.ad_reward_3));
                itemManager.addYellowGemCount(rewardCount);
                break;
            case 6:
                rewardCount = new Random().nextInt(450) + 300;

                boxResultImage.setImageResource(R.drawable.gold_reward_icon);
                resultTypeText.setText(context.getString(R.string.ad_reward_type, rewardCount));
                resultNameText.setText(context.getString(R.string.ad_reward_4));
                goldManager.addGold(rewardCount);
                break;
        }
    }

    private void getOpenResultToDB(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                getResultTextView.setText(context.getString(R.string.box_get_result_text));

                BoxResultData boxResultData = resultData.get(new Random().nextInt(resultSize));
                openResultData.add(boxResultData);

                boxResultImage.setImageResource(context.getResources().getIdentifier
                        (boxResultData.getEngChampName().toLowerCase() + "_" + boxResultData.getNum(), "drawable", context.getPackageName()));

                if (boxResultData.getNum() == 0) {
                    resultTypeText.setText(context.getString(R.string.champ_shard_text));
                    resultNameText.setText(boxResultData.getChampName());
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_champ_background)).into(boxBorderImage);

                    Cursor cursor = db.rawQuery("SELECT count FROM champ_frag_table WHERE champName=?", new String[]{boxResultData.getChampName()});
                    if (cursor.getCount() == 0) {
                        db.execSQL("INSERT INTO champ_frag_table (champName, engChampName, buyBlueGem, sellBlueGem, count) VALUES (?, ?, 720, 350, 1)",
                                new String[]{boxResultData.getChampName(), boxResultData.getEngChampName()});
                    } else if (cursor.getCount() > 0) {
                        db.execSQL("UPDATE champ_frag_table SET count=count+1 WHERE champName=?", new String[]{boxResultData.getChampName()});
                    }
                } else {
                    resultTypeText.setText(context.getString(R.string.skin_shard_text));
                    resultNameText.setText(boxResultData.getSkinName());
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_skin_background)).into(boxBorderImage);

                    Cursor cursor = db.rawQuery("SELECT count FROM skin_frag_table WHERE skinName=?", new String[]{boxResultData.getSkinName()});
                    if (cursor.getCount() == 0) {
                        db.execSQL("INSERT INTO skin_frag_table (champNameAndNum, skinName, buyYellowGem, sellYellowGem, count) VALUES (?, ?, 720, 350, 1)",
                                new String[]{boxResultData.getEngChampName() + "_" + boxResultData.getNum(), boxResultData.getSkinName()});
                    } else if (cursor.getCount() > 0) {
                        db.execSQL("UPDATE skin_frag_table SET count=count+1 WHERE skinName=?", new String[]{boxResultData.getSkinName()});
                    }
                }

                db.close();
            }
        });

    }


    private void getForgeResultToDB(final int fragType){

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                getResultTextView.setText(context.getString(R.string.box_upgrade_result_text));
                BoxResultData boxResultData = resultData.get(new Random().nextInt(resultSize));

                if(fragType == FragType.FRAG_SKIN) {
                    while (boxResultData.getNum() == 0) {
                        boxResultData = resultData.get(new Random().nextInt(resultSize));
                    }
                }else if(fragType == FragType.FRAG_CHAMPION){
                    while(boxResultData.getNum() != 0){
                        boxResultData = resultData.get(new Random().nextInt(resultSize));
                    }
                }

                openResultData.add(boxResultData);

                boxResultImage.setImageResource(context.getResources().getIdentifier
                        (boxResultData.getEngChampName().toLowerCase() + "_" + boxResultData.getNum(), "drawable", context.getPackageName()));

                if (boxResultData.getNum() == 0) {
                    resultNameText.setText(boxResultData.getChampName());
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_champ_background)).into(boxBorderImage);

                    Cursor champCursor = db.rawQuery("SELECT * FROM champ_table WHERE champName=?", new String[]{boxResultData.getChampName()});

                    if(champCursor.getCount() == 0){
                        resultTypeText.setText(context.getString(R.string.whole_champ_text));
                        String engName = boxResultData.getEngChampName();
                        db.execSQL("INSERT INTO champ_table (champName, imgFileName, imgUrl) VALUES (?,?,?)",
                        new String[]{boxResultData.getChampName(), engName.toLowerCase()+"_0", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"
                                + engName.replace(engName.charAt(0), engName.toUpperCase().charAt(0)) + "_0.jpg"});
                    }else if(champCursor.getCount() > 0) {
                        resultTypeText.setText(context.getString(R.string.champ_shard_text));
                        Cursor cursor = db.rawQuery("SELECT count FROM champ_frag_table WHERE champName=?", new String[]{boxResultData.getChampName()});
                        if (cursor.getCount() == 0) {
                            db.execSQL("INSERT INTO champ_frag_table (champName, engChampName, buyBlueGem, sellBlueGem, count) VALUES (?, ?, 720, 350, 1)",
                                    new String[]{boxResultData.getChampName(), boxResultData.getEngChampName()});
                        } else if (cursor.getCount() > 0) {
                            db.execSQL("UPDATE champ_frag_table SET count=count+1 WHERE champName=?", new String[]{boxResultData.getChampName()});
                        }
                    }
                } else {
                    resultTypeText.setText(context.getString(R.string.skin_shard_text));
                    resultNameText.setText(boxResultData.getSkinName());
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_skin_background)).into(boxBorderImage);
                    Cursor skinCursor = db.rawQuery("SELECT * FROM skin_table WHERE skinName=?", new String[]{boxResultData.getSkinName()});

                    if (skinCursor.getCount() == 0) {
                        resultTypeText.setText(context.getString(R.string.whole_skin_text));
                        String engName = boxResultData.getEngChampName();
                        db.execSQL("INSERT INTO skin_table (skinName, imgFileName, skinUrl) VALUES (?,?,?)",
                                new String[]{boxResultData.getSkinName(), engName.toLowerCase() + "_" +boxResultData.getNum(), "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"
                                        + engName.replace(engName.charAt(0), engName.toUpperCase().charAt(0)) +"_"+boxResultData.getNum()+ ".jpg"});
                    } else if (skinCursor.getCount() > 0) {
                        resultTypeText.setText(context.getString(R.string.skin_shard_text));
                        Cursor cursor = db.rawQuery("SELECT count FROM skin_frag_table WHERE skinName=?", new String[]{boxResultData.getSkinName()});
                        if (cursor.getCount() == 0) {
                            db.execSQL("INSERT INTO skin_frag_table (champNameAndNum, skinName, buyYellowGem, sellYellowGem, count) VALUES (?, ?, 720, 350, 1)",
                                    new String[]{boxResultData.getEngChampName() + "_" + boxResultData.getNum(), boxResultData.getSkinName()});
                        } else if (cursor.getCount() > 0) {
                            db.execSQL("UPDATE skin_frag_table SET count=count+1 WHERE skinName=?", new String[]{boxResultData.getSkinName()});
                        }
                    }
                }
                dbHelper.close();
                db.close();
            }
        });

    }

    private void loadMaterialImage(int itemCode){
        final ImageView boxImageView = boxOpenView.findViewById(R.id.box_image_view);

        /* 상자 or 열쇠 클릭시 */
        if(itemCode == ItemCode.BOX || itemCode == ItemCode.KEY) {
            boxOpenButton.setVisibility(View.VISIBLE);
            boxOpenCloseButton.setVisibility(View.VISIBLE);
            boxAllOpenButton.setVisibility(GONE);
            boxOpenButton.setEnabled(false);
            boxAllOpenButton.setEnabled(false);
            openTextView.setTextColor(context.getResources().getColor(R.color.button_disabled_text_color));

            boxOpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Glide.with(context).load(R.raw.box_open_ani3).into(gifImage);
                    soundManager.play(boxDialogOpenSoundId);
                    boxOpenButton.setVisibility(GONE);
                    boxAllOpenButton.setVisibility(GONE);
                    boxOpenCloseButton.setVisibility(GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boxOpenDialog.cancel();

                            itemManager.setBoxCount(itemManager.getBoxCount()-1);
                            itemManager.setKeyCount(itemManager.getKeyCount()-1);

                            count = 1;
                            openResultData.clear();
                            getOpenResultToDB();    // 데이터에 저장돼있는 결과 랜덤으로 가져와서 DB에 저장하고 dialog view에 띄움

                            boxResultDialog.show();
                        }
                    }, 3000);
                }
            });
            boxAllOpenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Glide.with(context).load(R.raw.box_open_ani3).into(gifImage);
                    soundManager.play(boxDialogOpenSoundId);
                    boxOpenButton.setVisibility(GONE);
                    boxAllOpenButton.setVisibility(GONE);
                    boxOpenCloseButton.setVisibility(GONE);

                    openResultData.clear();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boxOpenDialog.cancel();

                            count = (itemManager.getBoxCount() >= itemManager.getKeyCount()) ?
                                    itemManager.getKeyCount() : itemManager.getBoxCount();

                            itemManager.setBoxCount(itemManager.getBoxCount()-count);
                            itemManager.setKeyCount(itemManager.getKeyCount()-count);

                            for(int i=0; i<count; i++) {
                                getOpenResultToDB();    // 데이터에 저장돼있는 결과 랜덤으로 가져와서 DB에 저장하고 dialog view에 띄움
                            }
                            soundManager.play(getResultSoundId);
                            boxResultDialog.show();

                        }
                    }, 3000);
                }
            });

            if (itemManager.getKeyCount() > 1 && itemManager.getBoxCount() > 1) {
                boxAllOpenButton.setVisibility(View.VISIBLE);
                boxAllOpenButton.setEnabled(false);
                openAllTextView.setTextColor(context.getResources().getColor(R.color.button_disabled_text_color));
                /*전체 열기 text설정*/
                openAllTextView.setText(((itemManager.getBoxCount() >= itemManager.getKeyCount()) ?
                        itemManager.getKeyCount() : itemManager.getBoxCount()) + context.getString(R.string.open_all_text));
            }

            gifImage = new DrawableImageViewTarget(boxImageView);
            Glide.with(context).load(R.raw.box_open_ani1).into(gifImage);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load(R.raw.box_open_ani_loop2).into(gifImage);
                    if (itemManager.getKeyCount() > 0 && itemManager.getBoxCount() > 0) {
                        boxOpenButton.setEnabled(true);
                        openTextView.setTextColor(context.getResources().getColor(R.color.item_price_color));
                        boxAllOpenButton.setEnabled(true);
                        openAllTextView.setTextColor(context.getResources().getColor(R.color.item_price_color));
                    }
                }
            }, 1450);
        }
        /* 스폐셜 구 클릭시 */
        else if(itemCode == ItemCode.REWARD_ITEM){
            Glide.with(context).load(R.drawable.reward_item_icon).into(boxImageView);
            boxImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_factory_reward));

            boxOpenButton.setVisibility(View.VISIBLE);
            boxOpenCloseButton.setVisibility(View.VISIBLE);
            boxAllOpenButton.setVisibility(GONE);
            boxOpenButton.setEnabled(false);
            openTextView.setTextColor(context.getResources().getColor(R.color.button_disabled_text_color));

            if(itemManager.getRewardItemCount() > 0){
                boxOpenButton.setEnabled(true);
                openTextView.setTextColor(context.getResources().getColor(R.color.item_price_color));

                boxOpenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boxImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_factory_open_reward));
                        soundManager.play(boxDialogOpenSoundId);
                        boxOpenButton.setVisibility(GONE);
                        boxAllOpenButton.setVisibility(GONE);
                        boxOpenCloseButton.setVisibility(GONE);

                        count = 1;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boxOpenDialog.cancel();
                                itemManager.setRewardItemCount(itemManager.getRewardItemCount()-1);
                                getOpenRewardItem();
                                boxResultDialog.show();
                            }
                        }, 2850);
                    }
                });
            }
        }
    }


    private void loadExchangeDialogData(int fragType, final ItemFragment data, final ImageView fragItemImage){

        // 파편 재조합
        forgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isForging = true;
                selectedDatas.clear();
                selectedFragType = data.getFragType();
                selectedCount = 0;
                factoryActivity.forgeCycleImageView.setVisibility(View.VISIBLE);
                factoryActivity.forgeCycleImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_show_cycle));
                factoryActivity.forgeElePreView.setVisibility(View.VISIBLE);
                factoryActivity.forgeElePreView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_show_pre_view));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        factoryActivity.forgeCycleImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.loop_rotation_anim));
                    }
                }, 700);

                transToForgingView(fragItemImage, data);
                if(exchangeDialog.isShowing()) {
                    exchangeDialog.dismiss();
                }
            }
        });

        if(fragType == FragType.FRAG_CHAMPION){
            int nowBlueGem = itemManager.getBlueGemCount();
            if(isHaving(fragType, data.getChampName())) {
                upgradeDescTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeLayout.setClickable(false);
                upgradeLayout.setEnabled(false);
                isHavingLayout.setBackgroundResource(R.drawable.is_having_background);
                isHavingTextView.setText(R.string.is_having_text);
            }else if(!isHaving(fragType, data.getChampName())){
                upgradeDescTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_text_color));
                upgradeTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_text_color));
                upgradeLayout.setClickable(true);
                upgradeLayout.setEnabled(true);
                isHavingLayout.setBackgroundResource(R.drawable.is_not_having_drawable);
                isHavingTextView.setText(R.string.is_not_having_text);
            }
            if(data.getBuyBlueGem() > nowBlueGem) {
                upgradeDescTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeLayout.setClickable(false);
                upgradeLayout.setEnabled(false);
            }

            disenchantLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(exchangeDialog.isShowing()) {
                        exchangeDialog.dismiss();
                    }
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            upgradeFrag(FragType.FRAG_CHAMPION, data, false);
                            itemManager.addBlueGemCount(data.getSellBlueGem());
                            factoryActivity.refreshRecyclerView();
                        }
                    });
                }
            });
            upgradeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(exchangeDialog.isShowing()) {
                        exchangeDialog.dismiss();
                    }
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            count = 1;
                            upgradeFrag(FragType.FRAG_CHAMPION, data, true);
                            itemManager.addBlueGemCount(-data.getBuyBlueGem());
                            factoryActivity.refreshRecyclerView();
                        }
                    });
                }
            });

            exchangeNameText.setText(data.getChampName());
            exchangeTypeText.setText(context.getString(R.string.champ_shard_text));
            disenchantPriceTextView.setText("+"+data.getSellBlueGem());
            upgradePriceTextView.setText("-"+data.getBuyBlueGem());
            Glide.with(context).load(R.drawable.icon_be).into(upgradePriceImage);
            Glide.with(context).load(R.drawable.icon_be).into(exchangePriceImage);

        }else if(fragType == FragType.FRAG_SKIN){
            int nowYellowGem = itemManager.getYellowGemCount();
            if(isHaving(fragType, data.getSkinName())) {
                upgradeDescTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeLayout.setClickable(false);
                upgradeLayout.setEnabled(false);
                isHavingLayout.setBackgroundResource(R.drawable.is_having_background);
                isHavingTextView.setText(R.string.is_having_text);
            }else if(!isHaving(fragType, data.getSkinName())){
                upgradeDescTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_text_color));
                upgradeTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_text_color));
                upgradeLayout.setClickable(true);
                upgradeLayout.setEnabled(true);
                isHavingLayout.setBackgroundResource(R.drawable.is_not_having_drawable);
                isHavingTextView.setText(R.string.is_not_having_text);
            }
            if(data.getBuyYellowGem() > nowYellowGem) {
                upgradeDescTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeTextView.setTextColor(context.getResources().getColor(R.color.frag_upgrade_disabled_text_color));
                upgradeLayout.setClickable(false);
                upgradeLayout.setEnabled(false);
            }

            disenchantLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (exchangeDialog.isShowing()) {
                        exchangeDialog.dismiss();
                    }
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            upgradeFrag(FragType.FRAG_SKIN, data, false);
                            itemManager.addYellowGemCount(data.getSellYellowGem());
                            factoryActivity.refreshRecyclerView();
                        }
                    });
                }
            });
            upgradeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(exchangeDialog.isShowing()) {
                        exchangeDialog.dismiss();
                    }
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            count = 1;
                            upgradeFrag(FragType.FRAG_SKIN, data, true);
                            itemManager.addYellowGemCount(-data.getBuyYellowGem());
                            factoryActivity.refreshRecyclerView();
                        }
                    });
                }
            });

            exchangeNameText.setText(data.getSkinName());
            exchangeTypeText.setText(context.getString(R.string.skin_shard_text));
            disenchantPriceTextView.setText("+"+data.getSellYellowGem());
            upgradePriceTextView.setText("-"+data.getBuyYellowGem());
            Glide.with(context).load(R.drawable.icon_oe).into(upgradePriceImage);
            Glide.with(context).load(R.drawable.icon_oe).into(exchangePriceImage);
        }


    }


    private void upgradeFrag(int fragType, ItemFragment data, boolean isUpgrade){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        soundManager.play(getResultSoundId);
        if(fragType == FragType.FRAG_CHAMPION){
            db.execSQL("UPDATE champ_frag_table SET count=count-1 WHERE champName=?", new String[]{data.getChampName()});
            db.execSQL("DELETE FROM champ_frag_table WHERE count<1");
            if(isUpgrade) {
                String champNameAndNum = data.getChampNameAndNum();
                db.execSQL("INSERT INTO champ_table (champName, imgFileName, imgUrl) VALUES(?,?,?)",
                        new String[]{data.getChampName(), champNameAndNum.toLowerCase()+"_0", "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"
                                + champNameAndNum.replace(champNameAndNum.charAt(0), champNameAndNum.toUpperCase().charAt(0)) + "_0.jpg"});
            }
        }else if(fragType == FragType.FRAG_SKIN){
            db.execSQL("UPDATE skin_frag_table SET count=count-1 WHERE skinName=?", new String[]{data.getSkinName()});
            db.execSQL("DELETE FROM skin_frag_table WHERE count<1");
            if(isUpgrade) {
                String champNameAndNum = data.getChampNameAndNum();
                db.execSQL("INSERT INTO skin_table (skinName, imgFileName, skinUrl) VALUES(?,?,?)",
                        new String[]{data.getSkinName(), champNameAndNum.toLowerCase(), "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"
                                + champNameAndNum.replace(champNameAndNum.charAt(0), champNameAndNum.toUpperCase().charAt(0)) + ".jpg"});
            }
        }
        dbHelper.close();
        db.close();

        if(isUpgrade) {
            getResultTextView.setText(context.getString(R.string.box_upgrade_result_text));

            if (data.getFragType() == FragType.FRAG_CHAMPION) {
                resultTypeText.setText(context.getString(R.string.whole_champ_text));
                resultNameText.setText(data.getChampName());
                boxResultImage.setImageResource(context.getResources().getIdentifier
                        (data.getChampNameAndNum().toLowerCase()+"_0", "drawable", context.getPackageName()));
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_champ_background)).into(boxBorderImage);
            } else {
                resultTypeText.setText(context.getString(R.string.whole_skin_text));
                resultNameText.setText(data.getSkinName());
                boxResultImage.setImageResource(context.getResources().getIdentifier
                        (data.getChampNameAndNum().toLowerCase(), "drawable", context.getPackageName()));
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.fragment_skin_background)).into(boxBorderImage);
            }
            boxResultDialog.show();
        }
    }

    private boolean isHaving(int fragType, String name){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;

        if(fragType == FragType.FRAG_CHAMPION){
            cursor = db.rawQuery("SELECT * FROM champ_table WHERE champName=?", new String[]{name});
        }else if(fragType == FragType.FRAG_SKIN){
            cursor = db.rawQuery("SELECT * FROM skin_table WHERE skinName=?", new String[]{name});
        }

        boolean isHaving = (cursor.getCount() > 0)? true : false;
        dbHelper.close();
        db.close();
        return isHaving;
    }

    /*아이템 합성할 조각 추가하기*/
    @SuppressLint("ResourceType")
    private void transToForgingView(ImageView fragItemImage, ItemFragment selectedDatum){
        soundManager.play(getResultSoundId);
        if(selectedCount == 0){
            selectedFragType = selectedDatum.getFragType();
        }
        if(!(selectedDatas.contains(selectedDatum)) && (selectedFragType == selectedDatum.getFragType()) && (selectedCount < 3)) {
            selectedDatas.add(selectedDatum);
            forgingImageList.add(fragItemImage);
            ++selectedCount;
            if(selectedCount == 1){
                if(selectedDatum.getFragType() == FragType.FRAG_CHAMPION) {
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase() + "_0", "drawable", context.getPackageName())).into(firstForgeImage);
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase() + "_0", "drawable", context.getPackageName())).into(factoryActivity.firstForgePreImage);
                }else{
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase(), "drawable", context.getPackageName())).into(firstForgeImage);
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase(), "drawable", context.getPackageName())).into(factoryActivity.firstForgePreImage);
                }
                firstForgeBorderImage.setBackgroundResource(R.color.forge_item_stroke_color);
            }else if(selectedCount == 2){
                if(selectedDatum.getFragType() == FragType.FRAG_CHAMPION) {
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase() + "_0", "drawable", context.getPackageName())).into(secondForgeImage);
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase() + "_0", "drawable", context.getPackageName())).into(factoryActivity.secondForgePreImage);
                }else{
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase(), "drawable", context.getPackageName())).into(secondForgeImage);
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase(), "drawable", context.getPackageName())).into(factoryActivity.secondForgePreImage);
                }
                secondForgeBorderImage.setBackgroundResource(R.color.forge_item_stroke_color);
            }else if(selectedCount == 3){
                if(selectedDatum.getFragType() == FragType.FRAG_CHAMPION) {
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase() + "_0", "drawable", context.getPackageName())).into(thirdForgeImage);
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase() + "_0", "drawable", context.getPackageName())).into(factoryActivity.thirdForgePreImage);
                }else{
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase(), "drawable", context.getPackageName())).into(thirdForgeImage);
                    Glide.with(context).load(context.getResources().getIdentifier(selectedDatum.getChampNameAndNum().toLowerCase(), "drawable", context.getPackageName())).into(factoryActivity.thirdForgePreImage);
                }
                thirdForgeBorderImage.setBackgroundResource(R.color.forge_item_stroke_color);
            }
            if(selectedCount == 3){
                forgeCloseButton.setVisibility(View.VISIBLE);
                clearButton.setVisibility(View.VISIBLE);
                forgeButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.loop_rotation_anim));
                forgeDialog.show();
            }
        }
    }

    private void setBoxOpenDialog() {
        boxOpenView = LayoutInflater.from(context).inflate(R.layout.box_open_view, null);
        settingDialog();
    }

    private void setBoxResultDialog() {
        boxResultView = LayoutInflater.from(context).inflate(R.layout.box_result_layout, null);
        settingResultDialog();
    }

    private void setForgeDialog() {
        forgeView = LayoutInflater.from(context).inflate(R.layout.forge_layout, null);
        forgeImages = new ArrayList<>();
        forgeBorders = new ArrayList<>();
        settingForgeDialog();
    }

    private void setExchangeDialog(){
        exchangeView = LayoutInflater.from(context).inflate(R.layout.exchange_layout, null);
        selectedDatas = new ArrayList<>();
        forgingImageList = new ArrayList<>();
        settingExchangeDialog();
    }

    private void setResultData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                resultData = new ArrayList<>();
                openResultData = new ArrayList<>();

                String json = new JsonUtil(context).getJsonString("champion.json");

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONObject dataObject = jsonObject.getJSONObject("data");

                    Iterator iterator = dataObject.keys();

                    while(iterator.hasNext()) {
                        String engChampName = iterator.next().toString();

                        JSONObject champObject = dataObject.getJSONObject(engChampName);
                        JSONArray skinArray = champObject.getJSONArray("skins");

                        for(int j=0; j<skinArray.length(); j++) {
                            BoxResultData boxResultData = new BoxResultData();
                            boxResultData.setEngChampName(engChampName);
                            boxResultData.setChampName(champObject.get("name").toString());
                            JSONObject skinObject = skinArray.getJSONObject(j);
                            boxResultData.setNum(skinObject.getInt("num"));
                            boxResultData.setSkinName(skinObject.getString("name"));

                            resultData.add(boxResultData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                resultSize = resultData.size();
            }
        }).start();

    }


    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView titleText;

        HeaderViewHolder(View headerView) {
            super(headerView);
            titleText = headerView.findViewById(R.id.frag_title);
        }

        void onBind(ItemFragment data) {
            titleText.setText(data.getItemName());
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout fragLayout;
        private TextView itemCount;
        private ImageView fragItemImage;

        ItemViewHolder(View itemView) {
            super(itemView);

            fragLayout = itemView.findViewById(R.id.item_backgorund_layout);
            itemCount = itemView.findViewById(R.id.frag_count_text_view);
            fragItemImage = itemView.findViewById(R.id.frag_image_view);

        }

        void onBind(Context context, final ItemFragment data) {
            itemCount.setText(""+data.getItemCount());
            fragLayout.getLayoutParams().width = itemSize;
            fragLayout.getLayoutParams().height = itemSize;
            fragLayout.requestLayout();
            /* box or key*/
            if((data.getItemCode() == ItemCode.KEY || data.getItemCode() == ItemCode.BOX)) {
                Glide.with(context).load(data.getItemResId()).into(fragItemImage);
                fragLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!boxOpenDialog.isShowing() && !isForging){
                            soundManager.play(boxDialogOpenSoundId);
                            loadMaterialImage(data.getItemCode());
                            boxOpenDialog.show();
                        }
                    }
                });
                /* reward item */
            }else if(data.getItemCode() == ItemCode.REWARD_ITEM) {
                Glide.with(context).load(data.getItemResId()).into(fragItemImage);
                fragLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!boxOpenDialog.isShowing() && !isForging){
                            soundManager.play(boxDialogOpenSoundId);
                            loadMaterialImage(data.getItemCode());
                            boxOpenDialog.show();
                        }
                    }
                });
            }
            else if(data.getFragType() == FragType.FRAG_CHAMPION){
                Glide.with(context).load(
                        context.getResources().getIdentifier((data.getChampNameAndNum().toLowerCase())+"_0", "drawable", context.getPackageName()))
                        .into(fragItemImage);
                fragLayout.setBackgroundResource(R.drawable.fragment_champ_background);
                fragLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isForging){
                            transToForgingView(fragItemImage, data);
                        }else if(!exchangeDialog.isShowing() && !boxResultDialog.isShowing()){
                            soundManager.play(boxDialogOpenSoundId);
                            loadExchangeDialogData(FragType.FRAG_CHAMPION, data, fragItemImage);
                            exchangeDialog.show();
                        }
                    }
                });
            }else if(data.getFragType() == FragType.FRAG_SKIN){
                Glide.with(context).load(
                        context.getResources().getIdentifier((data.getChampNameAndNum()).toLowerCase(), "drawable", context.getPackageName()))
                        .into(fragItemImage);
                fragLayout.setBackgroundResource(R.drawable.fragment_skin_background);
                fragLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isForging){
                            transToForgingView(fragItemImage, data);
                        }else if(!exchangeDialog.isShowing() && !boxResultDialog.isShowing()){
                            soundManager.play(boxDialogOpenSoundId);
                            loadExchangeDialogData(FragType.FRAG_SKIN, data, fragItemImage);
                            exchangeDialog.show();
                        }
                    }
                });
            }
        }
    }


    public void updateReceiptsList(ArrayList<ItemFragment> newlist) {
        itemData = newlist;
        this.notifyDataSetChanged();
    }

}

