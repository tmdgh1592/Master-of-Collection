package com.app.buna.boxsimulatorforlol.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.boxsimulatorforlol.activity.MainActivity;
import com.app.buna.boxsimulatorforlol.dto.UpgradeData;
import com.app.buna.boxsimulatorforlol.manager.UpgradeManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.GameToast;
import com.app.buna.boxsimulatorforlol.vo.TierCondition;
import com.app.buna.boxsimulatorforlol.vo.UgrType;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpgradeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<UpgradeData> dataList;
    private Context context;
    private UpgradeManager upgradeManager;
    private SharedPreferences setting;

    public UpgradeRecyclerAdapter(Context context, MainActivity mainActivity, ArrayList<UpgradeData> dataList) {
        this.context = context;
        this.dataList = dataList;
        upgradeManager = new UpgradeManager(context, mainActivity);
        setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout upgradeLayout;
        private CircleImageView iconView;
        private TextView nameView, descView, itemEffValue, upgradeValuePerMax, upgradePrice;

        public MyViewHolder(@NonNull View view) {
            super(view);
            upgradeLayout = view.findViewById(R.id.upgrade_item_layout);
            iconView = view.findViewById(R.id.upgrade_icon);
            nameView = view.findViewById(R.id.upgrade_item_name);
            descView = view.findViewById(R.id.upgrade_item_desc);
            itemEffValue = view.findViewById(R.id.upgrade_effect_item_value);
            upgradeValuePerMax = view.findViewById(R.id.upgrade_value);
            upgradePrice = view.findViewById(R.id.upgrade_price);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upgrade_item_view, null, false);
        return (new MyViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        final UpgradeData mData = dataList.get(position);

        Glide.with(context).load(mData.getIconResId()).into(viewHolder.iconView);
        viewHolder.nameView.setText(mData.getName());
        viewHolder.descView.setText(mData.getItemDesc());
        viewHolder.upgradeValuePerMax.setText(mData.getNowSkillLevel() + "/" + mData.getMaxSkillLevel());
        if(mData.getNowSkillLevel() == mData.getMaxSkillLevel()){   // 스킬 만렙시
            viewHolder.upgradePrice.setText("-  ");
        }else{
            viewHolder.upgradePrice.setText(mData.getUpgradePrice()+"");
        }

        if(mData.getUpgradeType() == UgrType.GOLD_PER_CLICK){
            viewHolder.itemEffValue.setText(mData.getSkillEffect() +" Gold/Click");
        }else if(mData.getUpgradeType() == UgrType.GOLD_PER_SECOND){
            viewHolder.itemEffValue.setText(mData.getSkillEffect() +" Gold/Sec");
        }else if(mData.getUpgradeType() == UgrType.GOLD_PER_BLUE_STEAL || mData.getUpgradeType() == UgrType.GOLD_PER_YELLOW_STEAL){
            viewHolder.itemEffValue.setText(mData.getSkillEffect() + "% Chance");
        }else if(mData.getUpgradeType() == UgrType.GOLD_PER_TIER){
            switch (mData.getTierCondition()){
                case TierCondition.IRON:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.BRONZE:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.SILVER:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.GOLD:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.PLATINUM:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.DIAMOND:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.MASTER:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.GRANDMASTER:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
                case TierCondition.CHALLENGER:
                    viewHolder.itemEffValue.setText((mData.getSkillEffect() + " Level").replace(".0", ""));
                    break;
            }
        }else{
            new GameToast(context, context.getString(R.string.unknown_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
        }


        /* 업그레이드 버튼 */
        viewHolder.upgradeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 업그레이드 function

                if(position == 0){
                    upgradeManager.upgrade(mData.getPrefName(), mData.getUpgradeType(), mData.getUpgradePrice(), mData.getSkillEffect(),
                            mData.getNowSkillLevel(), mData.getMaxSkillLevel(), mData.getHowMuchUpgradePrice(), mData.getHowMuchUpgradeEffect(),
                            dataList.get(0).getName(), dataList.get(0).getPrefName(), position-1);
                }
                else if(0 < position && position < 8){   // 치명적 상처 ~ 노랑 정수 약탈
                    upgradeManager.upgrade(mData.getPrefName(), mData.getUpgradeType(), mData.getUpgradePrice(), mData.getSkillEffect(),
                            mData.getNowSkillLevel(), mData.getMaxSkillLevel(), mData.getHowMuchUpgradePrice(), mData.getHowMuchUpgradeEffect(),
                            dataList.get(position-1).getName(), dataList.get(position-1).getPrefName(), position-1);
                }else if(8 <= position){ // 티어별 스킬
                    // if satisfiedTierCode가 현재 setting.getInt("myTier") 보다 낮은 Toast("안됌")
                    // else upgrade gogo

                    int satisfiedTierCode = mData.getTierCondition();
                    int myTierCode = setting.getInt("myTier", TierCondition.UNRANKED);

                    if(satisfiedTierCode <= myTierCode){
                        upgradeManager.upgrade(mData.getPrefName(), mData.getUpgradeType(), mData.getUpgradePrice(), mData.getSkillEffect(),
                                mData.getNowSkillLevel(), mData.getMaxSkillLevel(), mData.getHowMuchUpgradePrice(), mData.getHowMuchUpgradeEffect(),
                                dataList.get(position-1).getName(), dataList.get(position-1).getPrefName(), position-1);
                    }else{
                        new GameToast(context, context.getString(R.string.warning_disable_upgrade_bc_tier), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void updateReceiptsList(ArrayList<UpgradeData> newlist) {
        dataList = newlist;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
