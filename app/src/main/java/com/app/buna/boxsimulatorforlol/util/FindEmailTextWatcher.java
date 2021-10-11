package com.app.buna.boxsimulatorforlol.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.regex.Pattern;

public class FindEmailTextWatcher implements TextWatcher {

    Context context;
    EditText emailEditText;
    RelativeLayout findButton;
    String emailPattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; // 이메일
    Boolean isEmail = false;

    public FindEmailTextWatcher(Context context, EditText emailEditText, RelativeLayout findButton){
        this.context = context;
        this.emailEditText = emailEditText;
        this.findButton = findButton;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        isEmail = Pattern.matches(emailPattern, emailEditText.getText().toString());

        if(!isEmail){
            findButton.setEnabled(false);
        }else{
            findButton.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
