package com.app.buna.boxsimulatorforlol.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.boxsimulatorforlol.dto.UpgradeData;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.GameToast;
import com.app.buna.boxsimulatorforlol.vo.TierCondition;
import com.app.buna.boxsimulatorforlol.vo.UgrType;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyInfoSkillRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<UpgradeData> dataList;

    public MyInfoSkillRecyclerAdapter(Context context, ArrayList<UpgradeData> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_info_skill_view, null, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView iconView;
        private TextView nameView, descView, itemEffValue, upgradeValuePerMax;

        public MyViewHolder(@NonNull View view) {
            super(view);
            iconView = view.findViewById(R.id.my_info_upgrade_icon);
            nameView = view.findViewById(R.id.my_info_upgrade_item_name);
            descView = view.findViewById(R.id.my_info_upgrade_item_desc);
            itemEffValue = view.findViewById(R.id.my_info_upgrade_effect_item_value);
            upgradeValuePerMax = view.findViewById(R.id.my_info_upgrade_value);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;

        final UpgradeData mData = dataList.get(position);

        Glide.with(context).load(mData.getIconResId()).into(holder.iconView);
        holder.nameView.setText(mData.getName());
        holder.descView.setText(mData.getItemDesc());
        holder.upgradeValuePerMax.setText(mData.getNowSkillLevel() + "/" + mData.getMaxSkillLevel());

        if(mData.getUpgradeType() == UgrType.GOLD_PER_CLICK){
            holder.itemEffValue.setText(mData.getSkillEffect() +" Gold/Click");
        }else if(mData.getUpgradeType() == UgrType.GOLD_PER_SECOND){
            holder.itemEffValue.setText(mData.getSkillEffect() +" Gold/Sec");
        }else if(mData.getUpgradeType() == UgrType.GOLD_PER_BLUE_STEAL || mData.getUpgradeType() == UgrType.GOLD_PER_YELLOW_STEAL){
            holder.itemEffValue.setText(mData.getSkillEffect() + "% Chance");
        }else if(mData.getUpgradeType() == UgrType.GOLD_PER_TIER){
            switch (mData.getTierCondition()){
                case TierCondition.IRON:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.BRONZE:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.SILVER:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.GOLD:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.PLATINUM:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.DIAMOND:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.MASTER:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.GRANDMASTER:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
                case TierCondition.CHALLENGER:
                    holder.itemEffValue.setText(mData.getSkillEffect() + "Tier");
                    break;
            }
        }else{
            new GameToast(context, context.getString(R.string.unknown_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
