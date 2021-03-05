package com.app.buna.boxsimulatorforlol.Util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.R;

public class LoginToast extends Toast {


    public LoginToast(Context context) {
        super(context);
    }

    public LoginToast(Context context, String message, int position, int period) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.login_toast_view, null);

        TextView messageTextView = view.findViewById(R.id.toast_text_view);
        messageTextView.setText(message);

        if(position == Gravity.BOTTOM){
            setGravity(position, 0, 100);
        }else if(position == Gravity.TOP){
            setGravity(position, 0, 100);
        }else {
            setGravity(position, 0, 0);
        }
        setDuration(period);
        setView(view);
    }
}
