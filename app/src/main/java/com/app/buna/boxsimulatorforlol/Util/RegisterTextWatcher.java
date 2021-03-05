package com.app.buna.boxsimulatorforlol.Util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.buna.boxsimulatorforlol.R;

import java.util.regex.Pattern;

public class RegisterTextWatcher implements TextWatcher {

    Boolean emailCheck = false;
    Boolean pwCheck = false;
    Boolean rePwCheck = false;


    Context context;
    EditText pwEditText, rePwEditText, idEditText, nickEditText;
    ImageView idCheckImageView, pwCheckImageView, pwReCheckImageView;
    TextView pwConditionTextView, idConditionTextView;
    // 대소문자 구분 숫자 특수문자  조합 6 ~ 12 자리
    String pwPattern1 = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{6,15}$"; //영어, 숫자, 특수문자 포함
    String pwPattern2 = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{6,15}$";   //영어, 숫자 포함
    String pwPattern3 = "^[가-힣]*$"; // 한글
    String emailPattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; // 이메일
    Boolean patternCheck1, patternCheck2, patternCheck3, emailPatternCheck;

    public RegisterTextWatcher(Context context, EditText pwEditText, EditText rePwEditText, ImageView pwCheckImageView, ImageView pwReCheckImageView,
                               TextView pwConditionTextView, EditText idEditText, EditText nickEditText, ImageView idCheckImageView, TextView idConditionTextView) {
        this.context = context;
        this.pwEditText = pwEditText;
        this.rePwEditText = rePwEditText;
        this.idCheckImageView = idCheckImageView;
        this.pwCheckImageView = pwCheckImageView;
        this.pwReCheckImageView = pwReCheckImageView;
        this.pwConditionTextView = pwConditionTextView;
        this.idEditText = idEditText;
        this.nickEditText = nickEditText;
        this.idConditionTextView = idConditionTextView;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        String nickString = nickEditText.getText().toString();
        String emailString = idEditText.getText().toString();
        String pwString = pwEditText.getText().toString();
        String rePwString = rePwEditText.getText().toString();


        patternCheck1 = Pattern.matches(pwPattern1, pwString);
        patternCheck2 = Pattern.matches(pwPattern2, pwString);
        patternCheck3 = Pattern.matches(pwPattern3, pwString);
        emailPatternCheck = Pattern.matches(emailPattern, emailString);

        /* PW condition */
        if(pwString.contains(" ") || pwString.contains("<") || pwString.contains(">") || pwString.contains("(") || pwString.contains(")")
                || pwString.contains("#")  || pwString.contains("'") || pwString.contains("/") || pwString.contains("|")) {    // 특수문자 포함되지 않아야 할 것이 들어간 경우
            pwCheckImageView.setImageResource(R.drawable.password_not_ok_icon);
            pwConditionTextView.setText(context.getString(R.string.password_condtion1_text));
            pwCheck = false;
        }else if(!pwString.equals(idEditText.getText().toString()) && !pwString.contains(" ") && (6 <= pwString.length() && pwString.length() <= 15)){  //조건 성립
            pwCheckImageView.setImageResource(R.drawable.password_ok_icon);
            pwConditionTextView.setText(context.getString(R.string.password_condtion5_text));
            if(pwCheck == false) {
                pwCheckImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_check_condition_true));
                pwCheck = true;
            }
        }else if(!emailString.isEmpty() && pwString.equals(emailString)){    // 아이디와 동일할 시
            pwCheckImageView.setImageResource(R.drawable.password_not_ok_icon);
            pwConditionTextView.setText(context.getString(R.string.password_condtion2_text));
            pwCheck = false;
        }/*else if(!pwString.isEmpty() && !patternCheck2){ //영어, 숫자 미포함
            pwCheckImageView.setImageResource(R.drawable.password_not_ok_icon);
            pwConditionTextView.setText(context.getString(R.string.password_condtion4_text));
            pwCheck = false;
        }*/else if(!pwString.isEmpty()){ //한글
            pwCheckImageView.setImageResource(R.drawable.password_not_ok_icon);
            pwConditionTextView.setText(context.getString(R.string.password_condtion3_text));
            pwCheck = false;
        }else{
            pwCheckImageView.setImageResource(R.drawable.password_not_ok_icon);
            pwConditionTextView.setText("");
            pwCheck = false;
        }

        /* rePassword */
        if(pwCheck && rePwString.equals(pwString)){
            pwReCheckImageView.setImageResource(R.drawable.password_ok_icon);
            if(rePwCheck == false){
                pwReCheckImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_check_condition_true));
                rePwCheck = true;
            }
        }else{
            pwReCheckImageView.setImageResource(R.drawable.password_not_ok_icon);
            rePwCheck = false;
        }

        /*email*/
        if(!emailString.isEmpty() && emailPatternCheck){
            idCheckImageView.setImageResource(R.drawable.password_ok_icon);
            idConditionTextView.setText(context.getString(R.string.email_condition1_text));
            idConditionTextView.setTextColor(context.getResources().getColor(R.color.email_valid_text_color));
            if(emailCheck == false){
                idCheckImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_check_condition_true));
                emailCheck = true;
            }
        }else if(!emailString.isEmpty() && !emailPatternCheck){
            idCheckImageView.setImageResource(R.drawable.password_not_ok_icon);
            idConditionTextView.setText(context.getString(R.string.email_condition2_text));
            idConditionTextView.setTextColor(context.getResources().getColor(R.color.email_invalid_text_color));
            emailCheck = false;
        }else if(emailString.isEmpty()){
            idConditionTextView.setText("");
            emailCheck = false;
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public boolean getState(){
        if(emailCheck && pwCheck && rePwCheck){
            return true;
        }else{
            return false;
        }
    }
}
