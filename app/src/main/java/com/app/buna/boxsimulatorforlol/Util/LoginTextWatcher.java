package com.app.buna.boxsimulatorforlol.Util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class LoginTextWatcher implements TextWatcher {

    Context context;
    TextInputEditText emailEditText, pwEditText;
    LinearLayout loginButton;

    public LoginTextWatcher(Context context, TextInputEditText emailEditText, TextInputEditText pwEditText, LinearLayout loginButton){
        this.context = context;
        this.emailEditText = emailEditText;
        this.pwEditText = pwEditText;
        this.loginButton = loginButton;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if(emailEditText.getText().length() <= 2 && pwEditText.getText().length() <= 1){
            loginButton.setEnabled(false);
        }else{
            loginButton.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
