package com.app.buna.boxsimulatorforlol.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.buna.boxsimulatorforlol.R;

public class ShopTextWatcher implements TextWatcher {

    Context context;
    LinearLayout button;
    EditText editText;
    TextView textView;

    public ShopTextWatcher(Context context, EditText editText, LinearLayout linearLayout, TextView textView) {
        this.context = context;
        this.button = linearLayout;
        this.editText = editText;
        this.textView = textView;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if(editText.getText().toString().contains(".")) {
            editText.setText(editText.getText().toString().replaceAll(".", ""));
        }else if(editText.getText().toString().contains(",")) {
            editText.setText(editText.getText().toString().replaceAll(",", ""));
        }else if(editText.getText().length() == 0 || Integer.parseInt(editText.getText().toString()) == 0){
            button.setEnabled(false);
            textView.setTextColor(context.getResources().getColor(R.color.button_disabled_stroke));
        }else{
            button.setEnabled(true);
            textView.setTextColor(context.getResources().getColor(R.color.item_price_color));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
