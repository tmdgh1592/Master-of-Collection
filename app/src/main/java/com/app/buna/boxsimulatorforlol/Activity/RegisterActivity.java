package com.app.buna.boxsimulatorforlol.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.DTO.UserAccount;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.ByteLengthFilter;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.app.buna.boxsimulatorforlol.Util.LoginToast;
import com.app.buna.boxsimulatorforlol.Util.Network;
import com.app.buna.boxsimulatorforlol.Util.RegisterTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.regex.Pattern;

import ir.alirezabdn.wp7progress.WP10ProgressBar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView pwConditionTextView, idConditionTextView, nickDuplicateTextView;
    private EditText pwEditTextView, rePwEditTextView, idEditTextView, nickEditTextView;
    private ImageView idCheckImageView, pwCheckImageView, pwReCheckImageView;
    private RegisterTextWatcher pwTextWatcher;
    private LinearLayout duplicateCheckButton, registerButton, cancelButton;

    private String nickPattern = "^(?=.*[A-Za-z가-힣0-9])[가-힣A-Za-z[0-9]]{2,8}$";   //영어, 숫자, 한글 포함
    private String nickPattern2 = "^(?=.*[A-Za-z0-9])[A-Za-z[0-9]]{4,16}$";   //영어, 숫자, 한글 포함

    private boolean nickCheck = false;

    private WP10ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("account");

    AlertDialog.Builder nickDialogBuilder;
    AlertDialog nickDialog;


    private ByteLengthFilter byteLengthFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_register);
        byteLengthFilter = new ByteLengthFilter(14, "KSC5601");
        setFirebaseDB();
        settingView();
    }


    private void settingView(){
        pwConditionTextView = findViewById(R.id.pw_contion_text_view);
        pwEditTextView = findViewById(R.id.register_pw_edit_text);
        rePwEditTextView = findViewById(R.id.register_re_pw_edit_text);
        idCheckImageView = findViewById(R.id.id_check_image_view);
        pwCheckImageView = findViewById(R.id.password_check_image_view);
        pwReCheckImageView = findViewById(R.id.password_recheck_image_view);
        idEditTextView = findViewById(R.id.register_id_edit_text);
        nickEditTextView = findViewById(R.id.register_nickname_edit_text);
        idConditionTextView = findViewById(R.id.id_contion_text_view);
        duplicateCheckButton = findViewById(R.id.nickname_duplicate_check_button);
        registerButton = findViewById(R.id.register_button);
        cancelButton = findViewById(R.id.register_cancel_button);
        nickDuplicateTextView = findViewById(R.id.nickname_duplicate_text_view);
        progressBar = findViewById(R.id.register_progress_bar);

        pwTextWatcher = new RegisterTextWatcher(this, pwEditTextView, rePwEditTextView, pwCheckImageView, pwReCheckImageView, pwConditionTextView, idEditTextView, nickEditTextView, idCheckImageView, idConditionTextView);
        pwEditTextView.addTextChangedListener(pwTextWatcher);
        rePwEditTextView.addTextChangedListener(pwTextWatcher);
        idEditTextView.addTextChangedListener(pwTextWatcher);
        nickEditTextView.addTextChangedListener(pwTextWatcher);
        nickEditTextView.setFilters(new InputFilter[] {byteLengthFilter});
        duplicateCheckButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void setFirebaseDB(){
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nickname_duplicate_check_button:
                if (nickCheck) {
                    nickDuplicateTextView.setText(getString(R.string.check_duplication));
                    nickCheck = false;
                    nickEditTextView.setEnabled(true);
                    nickEditTextView.setTextColor(getResources().getColor(R.color.login_activity_text_color));
                    nickEditTextView.getText().clear();

                    nickEditTextView.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    return;
                }
                if(Network.state(this)) {
                    final String nickString = nickEditTextView.getText().toString();

                    //test용 코드
                /*accountRef.child("userinfo").child("test@gmailcom").setValue(new UserAccount(idEditTextView.getText().toString(), pwEditTextView.getText().toString(), nickString));
                accountRef.child("nickname").child("test@gmailcom").setValue("테스트");*/

                    if (nickString.isEmpty()) {
                        new LoginToast(RegisterActivity.this, getString(R.string.nick_condition_text3), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                        return;
                    } else if (byteLengthFilter.getByteLength(nickString) < 4) {
                        new LoginToast(RegisterActivity.this, getString(R.string.nick_condition_text6), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                        return;
                    }/* else if (!Pattern.matches(nickPattern, nickEditTextView.getText().toString())
                            && !Pattern.matches(nickPattern2, nickEditTextView.getText().toString())) {
                        new LoginToast(RegisterActivity.this, getString(R.string.nick_condition_text4), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                        return;
                    }*/

                    duplicateCheckButton.setClickable(false);
                    accountRef.child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.exists()) {
                                Map<String, String> nickMap = (Map<String, String>) dataSnapshot.getValue();
                                if (nickMap.values().contains(nickString)) {  // 닉네임 사용 불가능
                                    new LoginToast(RegisterActivity.this, getString(R.string.nick_condition_text2), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                                    duplicateCheckButton.setClickable(true);
                                    return;
                                } else {    // 닉네임 사용
                                    String message = getString(R.string.nick_condition_text1, nickString);

                                    nickDialogBuilder = new AlertDialog.Builder(RegisterActivity.this)
                                            .setTitle(getString(R.string.check_duplication))
                                            .setMessage(message)
                                            .setCancelable(false)
                                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    nickCheck = true;
                                                    nickDuplicateTextView.setText(getString(R.string.reset_nick));
                                                    nickEditTextView.setTextColor(getResources().getColor(R.color.disabled_login_activity_text_color));
                                                    nickEditTextView.setEnabled(false);
                                                    nickDialog.dismiss();
                                                }
                                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    nickCheck = false;
                                                    nickDialog.dismiss();
                                                }
                                            });
                                    nickDialog = nickDialogBuilder.create();
                                    nickDialog.show();
                                    duplicateCheckButton.setClickable(true);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new LoginToast(RegisterActivity.this, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                            duplicateCheckButton.setClickable(true);
                        }
                    });
                }else{
                    new LoginToast(this, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                    progressBar.hideProgressBar();
                }
                break;
            case R.id.register_button:
                progressBar.showProgressBar();
                if(pwTextWatcher.getState() && nickCheck){
                    createUser(idEditTextView.getText().toString(), pwEditTextView.getText().toString(), nickEditTextView.getText().toString());
                }else if(nickCheck == false){
                    new LoginToast(RegisterActivity.this, getString(R.string.register_condition_text1), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                    progressBar.hideProgressBar();
                }else{
                    new LoginToast(RegisterActivity.this, getString(R.string.register_condition_text2), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                    progressBar.hideProgressBar();
                }
                break;
            case R.id.register_cancel_button:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if(nickDialog != null && nickDialog.isShowing()){
            nickDialog.dismiss();
        }
        super.onDestroy();
    }

    private void createUser(final String email, final String password, final String nickName){
        if(Network.state(this)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("RegisterActivity", "createUserWithEmail:success");
                                new LoginToast(RegisterActivity.this, getString(R.string.nick_condition_text5, nickName), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                addValueToDB(email, nickName);
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                                progressBar.hideProgressBar();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("RegisterActivity", "createUserWithEmail:failure", task.getException());
                                new LoginToast(RegisterActivity.this, getString(R.string.register_condition_text3), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
                                progressBar.hideProgressBar();
                            }
                        }
                    });
        }else{
            new LoginToast(this, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            progressBar.hideProgressBar();
        }
    }

    private void addValueToDB(String email, String nickname){
        //UserAccount userAccount = new UserAccount(email, password, nickname);

        // firebase의 database path는 . 을 포함할 수 없기때문에 .을 모두 공백으로 바꿔줌.
        String emailNotContainDot = email.replace("\u002E", "");
        //accountRef.child("userinfo").child(emailNotContainDot).setValue(userAccount);
        accountRef.child("nickname").child(emailNotContainDot).setValue(nickname);
    }

}
