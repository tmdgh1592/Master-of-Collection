package com.app.buna.boxsimulatorforlol.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.boxsimulatorforlol.DTO.RankData;
import com.app.buna.boxsimulatorforlol.R;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankViewHolder>{

    private Context context;
    private List<RankData> data;

    public RankingAdapter(Context context, List<RankData> data){
        this.context = context;
        this.data = data;
    }

    public static class RankViewHolder extends RecyclerView.ViewHolder{

        private TextView rankText, rankNickText, rankChampCount, rankSkinCount;
        private RelativeLayout rankLayout;

        public RankViewHolder(@NonNull View view) {
            super(view);

            rankLayout = view.findViewById(R.id.rank_item_layout);
            rankText = view.findViewById(R.id.rank_text);
            rankNickText = view.findViewById(R.id.rank_nickname_text);
            rankChampCount = view.findViewById(R.id.rank_champ_count_text);
            rankSkinCount = view.findViewById(R.id.rank_skin_count_text);
        }
    }

    @NonNull
    @Override
    public RankingAdapter.RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_item_layout, null);
        RankingAdapter.RankViewHolder holder =  new RankingAdapter.RankViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.RankViewHolder holder, int position) {
        holder.rankText.setText(""+(position+1));
        holder.rankNickText.setText(data.get(position).getNickname());
        holder.rankChampCount.setText(""+data.get(position).getChampCount());
        holder.rankSkinCount.setText(""+data.get(position).getSkinCount());

        if(position == 0){
            holder.rankLayout.getBackground().setColorFilter(context.getResources().getColor(R.color.first_ranking_text_color), PorterDuff.Mode.ADD);
            holder.rankText.setTextColor(context.getResources().getColor(R.color.first_ranking_text_color));
        }else if(position == 1){
            holder.rankLayout.getBackground().setColorFilter(context.getResources().getColor(R.color.second_ranking_text_color), PorterDuff.Mode.ADD);
            holder.rankText.setTextColor(context.getResources().getColor(R.color.second_ranking_text_color));
        }else if(position == 2){
            holder.rankLayout.getBackground().setColorFilter(context.getResources().getColor(R.color.third_ranking_text_color), PorterDuff.Mode.ADD);
            holder.rankText.setTextColor(context.getResources().getColor(R.color.third_ranking_text_color));
        }else{
            holder.rankLayout.getBackground().setColorFilter(context.getResources().getColor(R.color.rank_view_background_color), PorterDuff.Mode.SRC_IN);
            holder.rankText.setTextColor(context.getResources().getColor(R.color.rank_data_count_color));
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
