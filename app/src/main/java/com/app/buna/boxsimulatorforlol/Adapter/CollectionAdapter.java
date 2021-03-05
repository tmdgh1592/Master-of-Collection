package com.app.buna.boxsimulatorforlol.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.boxsimulatorforlol.Activity.WebSplashActivity;
import com.app.buna.boxsimulatorforlol.DTO.CollectData;
import com.app.buna.boxsimulatorforlol.Manager.SoundManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.GameToast;
import com.app.buna.boxsimulatorforlol.Util.Network;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.app.buna.boxsimulatorforlol.VO.CollectionType.COLLECTION_CHAMP_ITEM;
import static com.app.buna.boxsimulatorforlol.VO.CollectionType.COLLECTION_SKIN_ITEM;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectViewHolder> {

    private Context context;
    private ArrayList<CollectData> data;
    int type;

    private SoundManager soundManager;
    private int dialogOpenSoundId, dialogCloseSoundId;

    private AlertDialog itemDataDialog;
    private View dataView;
    private ImageView collectionItemImage, collectionDataFrame, collectionDataCloseButton;
    private TextView collectionDataNameText, collectionDataType;
    private RelativeLayout wallpaperLayout;


    public CollectionAdapter(Context context, ArrayList<CollectData> data, int type){
        this.context = context;
        this.data = data;
        this.type = type;
        soundManager = new SoundManager(context);
        soundManager.init();
        dialogOpenSoundId = soundManager.loadSound(SoundManager.TYPE_TAB_OPEN);
        dialogCloseSoundId = soundManager.loadSound(SoundManager.TYPE_TAB_CLOSE);
        setDialog();
    }

    @NonNull
    @Override
    public CollectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.collection_item, null);
        CollectViewHolder viewHolder = new CollectViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CollectViewHolder holder, final int position) {
        String imgName = data.get(position).getImgFileName();
        Glide.with(context).load(context.getResources().getIdentifier(imgName, "drawable", context.getPackageName())).into(holder.itemImageView);
        if(type == COLLECTION_CHAMP_ITEM){
            Glide.with(context).load(R.drawable.collection_champ_frame).into(holder.frameImageView);
        }else if(type == COLLECTION_SKIN_ITEM){
            Glide.with(context).load(R.drawable.collection_skin_frame).into(holder.frameImageView);
        }
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.play(dialogOpenSoundId);
                if(!itemDataDialog.isShowing()) {
                    loadDataView(position);
                    itemDataDialog.show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return data.size();
    }


    private void setDialog(){
        dataView = LayoutInflater.from(context).inflate(R.layout.collection_data_view, null);

        collectionItemImage = dataView.findViewById(R.id.collection_data_item_image);
        collectionDataFrame = dataView.findViewById(R.id.collection_data_frame_image);
        collectionDataCloseButton = dataView.findViewById(R.id.collection_view_close_button);
        collectionDataNameText = dataView.findViewById(R.id.collection_data_name_text);
        collectionDataType = dataView.findViewById(R.id.collection_data_type_text);
        wallpaperLayout = dataView.findViewById(R.id.wallpaper_layout);

        itemDataDialog = new AlertDialog.Builder(context)
                .setView(dataView)
                .setCancelable(true)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        soundManager.play(dialogCloseSoundId);
                    }
                }).create();

        collectionDataCloseButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(itemDataDialog.isShowing()){
                     itemDataDialog.dismiss();
                 }
             }
         });
    }

    private void loadDataView(final int position){

        String imgName = data.get(position).getImgFileName();
        collectionDataNameText.setText(data.get(position).getName());
        Glide.with(context).load(context.getResources().getIdentifier(imgName, "drawable", context.getPackageName())).into(collectionItemImage);
        if(type == COLLECTION_CHAMP_ITEM){
            Glide.with(context).load(R.drawable.collection_champ_frame).into(collectionDataFrame);
            collectionDataType.setText(context.getString(R.string.champion_text));
        }else if(type == COLLECTION_SKIN_ITEM){
            Glide.with(context).load(R.drawable.collection_skin_frame).into(collectionDataFrame);
            collectionDataType.setText(context.getString(R.string.skin_text));
        }

        wallpaperLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Network.state(context)) {
                    Intent intent = new Intent(context, WebSplashActivity.class);
                    intent.putExtra("splashImgUrl", data.get(position).getUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    if(itemDataDialog.isShowing()){
                        itemDataDialog.dismiss();
                    }
                }else{
                    new GameToast(context, context.getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class CollectViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout itemLayout;
        private ImageView itemImageView;
        private ImageView frameImageView;

        public CollectViewHolder(@NonNull View itemView) {
            super(itemView);

            itemLayout = itemView.findViewById(R.id.collection_layout);
            itemImageView = itemView.findViewById(R.id.collection_item_image);
            frameImageView = itemView.findViewById(R.id.collection_frame_image);
        }
    }

    public void onDataSetChanged(ArrayList<CollectData> newData) {
        data = newData;
        this.notifyDataSetChanged();
    }


}
