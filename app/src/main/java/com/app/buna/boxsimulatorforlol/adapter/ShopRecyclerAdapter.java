package com.app.buna.boxsimulatorforlol.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.boxsimulatorforlol.activity.MainActivity;
import com.app.buna.boxsimulatorforlol.dto.ShopItem;
import com.app.buna.boxsimulatorforlol.util.ShopTextWatcher;
import com.app.buna.boxsimulatorforlol.manager.ShopManager;
import com.app.buna.boxsimulatorforlol.manager.SoundManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.vo.ItemType;
import com.bumptech.glide.Glide;
import com.cy.dialog.BaseDialog;

import java.util.ArrayList;

public class ShopRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    ArrayList<ShopItem> data;
    Activity activity;
    BaseDialog purchaseDialog;
    SoundManager soundManager;
    int shopOpenSoundId, shopCloseSoundId, buyItemSoundId;
    View confirmView;
    private ShopManager shopManager;
    ShopTextWatcher shopTextWatcher;
    private int percent = 5;    // 아이템 구매할 때마다 증가하는 돈 액수
    private int itemSize;

    public ShopRecyclerAdapter(Context context, MainActivity activity, ArrayList<ShopItem> data) {
        super();
        this.context = context;
        this.data = data;
        this.activity = activity;
        soundManager = new SoundManager(context);
        soundManager.init();
        shopOpenSoundId = soundManager.loadSound(SoundManager.TYPE_SHOP_OPEN);
        shopCloseSoundId = soundManager.loadSound(SoundManager.TYPE_SHOP_CLOSE);
        buyItemSoundId = soundManager.loadSound(SoundManager.TYPE_BUY_ITEM);

        shopManager = new ShopManager(context, activity);
    }

    public void setItemSize(int itemSize){
        this.itemSize = itemSize;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ShopHeaderViewHolder){
            ShopHeaderViewHolder viewHolder = (ShopHeaderViewHolder) holder;
            viewHolder.onBind(data.get(position));
        }else{
            final String itemPriceText = Integer.toString(data.get(position).getItemPrice() + data.get(position).getItemPrice() * data.get(position).getBoughtCount() / percent);
            ShopItemViewHolder viewHolder = (ShopItemViewHolder) holder;
            viewHolder.itemImageView.setImageResource(data.get(position).getItemImage());
            viewHolder.itemName.setText(data.get(position).getItemName());
            viewHolder.itemPrice.setText(itemPriceText);
            viewHolder.itemLayout.getLayoutParams().width = itemSize;
            viewHolder.itemLayout.getLayoutParams().height = itemSize;
            viewHolder.itemLayout.requestLayout();
            viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {   // 구매 확인 창 listener
                @Override
                public void onClick(View view) {

                if(soundManager.isLoaded()){
                    soundManager.play(shopOpenSoundId);
                }

                confirmView = activity.getLayoutInflater().inflate(R.layout.item_purchase_confirm_layout, null);
                ImageView confirmItemImage = confirmView.findViewById(R.id.confirm_item_image);
                TextView confirmItemName = confirmView.findViewById(R.id.confirm_item_name);
                ImageView confirmItemType = confirmView.findViewById(R.id.confirm_item_type);
                TextView confirmItemDesc = confirmView.findViewById(R.id.confirm_item_desc);
                final TextView confirmItemPrice = confirmView.findViewById(R.id.confirm_item_price);
                LinearLayout purchaseButton = confirmView.findViewById(R.id.confirm_item_purchase_button);
                ImageView cancelButton = confirmView.findViewById(R.id.confirm_cancel_image);
                final EditText buyCount = confirmView.findViewById(R.id.buy_count_edit_text);


                Glide.with(context).load(data.get(position).getItemImage()).into(confirmItemImage);
                confirmItemName.setText(data.get(position).getItemName());
                confirmItemDesc.setText(data.get(position).getItemDesc());
                confirmItemPrice.setText(itemPriceText);

                if(data.get(position).getItemType() == ItemType.GOLD){  // 골드
                    Glide.with(context).load(R.drawable.icon_gold).into(confirmItemType);
                }else if(data.get(position).getItemType() == ItemType.BLUE_GEM) {  // 파랑 정수
                    Glide.with(context).load(R.drawable.icon_be).into(confirmItemType);
                }else if(data.get(position).getItemType() == ItemType.YELLOW_GEM) { // 노랑 정수
                    Glide.with(context).load(R.drawable.icon_oe).into(confirmItemType);
                }

                cancelButton.setOnClickListener(new View.OnClickListener() {    // 구매창 취소
                    @Override
                    public void onClick(View view) {
                        closeDialog(0);
                    }
                });

                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buyCount.getWindowToken(), 0);

                shopTextWatcher = new ShopTextWatcher(context, buyCount, purchaseButton, (TextView)confirmView.findViewById(R.id.purchase_text_view));
                buyCount.addTextChangedListener(shopTextWatcher);

                purchaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {    // 구매 버튼
                        int itemCount = Integer.parseInt(buyCount.getText().toString());
                        boolean isBought = shopManager.buyItem(data.get(position).getItemCode(), data.get(position).getItemType(), itemCount, Integer.parseInt(confirmItemPrice.getText().toString()));
                        if(isBought) {
                            closeDialog(1);
                        }
                    }
                });

                settingDialog();
                purchaseDialog.show();

                }
            });

            /*** 0 : 골드(gold), 1 : 파랑정수, 2 : 노랑정수 ***/
            if(data.get(position).getItemType() == 0){  // 골드
                Glide.with(context).load(R.drawable.icon_gold).into(viewHolder.itemType);
            }else if(data.get(position).getItemType() == 1) {  // 정수
                Glide.with(context).load(R.drawable.icon_be).into(viewHolder.itemType);
            }else if(data.get(position).getItemType() == 2) {
                Glide.with(context).load(R.drawable.icon_oe).into(viewHolder.itemType);
            }

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.TITLE) {
            View view = LayoutInflater.from(context).inflate(R.layout.shop_recycler_header, parent, false);
            return new ShopHeaderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.shop_item_view, parent, false);
            return new ShopItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).getItemType() == ItemType.TITLE){
            return ItemType.TITLE;
        }else{
            return data.get(position).getItemType();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateReceiptsList(ArrayList<ShopItem> newlist) {
        data = newlist;
        this.notifyDataSetChanged();
    }


    private void settingDialog() {
        purchaseDialog = new BaseDialog(context);
        purchaseDialog.contentView(confirmView)
                .layoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                .dimAmount(0.5f)
                .animType(BaseDialog.AnimInType.CENTER)
                .canceledOnTouchOutside(false);

        purchaseDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                soundManager.play(shopCloseSoundId);
            }
        });
    }

    public void closeDialog(int closeType) {
        if (purchaseDialog.isShowing()) {
            if (soundManager.isLoaded()) {
                if(closeType == 1){
                    soundManager.play(buyItemSoundId);
                }else{
                    soundManager.play(shopCloseSoundId);
                }
            }
            purchaseDialog.dismiss();
        }
    }
}

class ShopHeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView titleText;

    ShopHeaderViewHolder(View headerView) {
        super(headerView);
        titleText = headerView.findViewById(R.id.shop_title);
    }

    void onBind(ShopItem data) {
        titleText.setText(data.getItemName());
    }
}

class ShopItemViewHolder extends RecyclerView.ViewHolder{

    RelativeLayout itemLayout;
    ImageView itemImageView, itemType;
    TextView itemName, itemPrice;

    ShopItemViewHolder(View view) {
        super(view);
        itemLayout = view.findViewById(R.id.item_backgorund_layout);
        itemImageView = view.findViewById(R.id.item_image_view);
        itemType = view.findViewById(R.id.item_type);
        itemName = view.findViewById(R.id.item_name_text_view);
        itemPrice = view.findViewById(R.id.item_price_text_view);
    }
}

