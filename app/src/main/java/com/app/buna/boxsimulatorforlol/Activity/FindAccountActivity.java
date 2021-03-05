package com.app.buna.boxsimulatorforlol.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.FindEmailTextWatcher;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.app.buna.boxsimulatorforlol.Util.LoginToast;
import com.app.buna.boxsimulatorforlol.Util.Network;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ir.alirezabdn.wp7progress.WP10ProgressBar;

public class FindAccountActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailEditText;
    TextView registerTextView;
    RelativeLayout findButton;
    FirebaseAuth mAuth;
    WP10ProgressBar findProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_find_account);
        mAuth = FirebaseAuth.getInstance();
        settingView();
    }

    private void settingView(){
        findProgressBar = findViewById(R.id.find_password_progress_bar);
        emailEditText = findViewById(R.id.find_email_edit_text);
        registerTextView = findViewById(R.id.register_text_view);
        findButton = findViewById(R.id.find_button);

        registerTextView.setOnClickListener(this);
        findButton.setOnClickListener(this);
        findButton.setEnabled(false);
        emailEditText.addTextChangedListener(new FindEmailTextWatcher(this, emailEditText, findButton));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_text_view:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.find_button:
                findEmail(emailEditText.getText().toString());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void findEmail(String email){
        if(Network.state(this)) {
            findProgressBar.showProgressBar();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                new LoginToast(FindAccountActivity.this, getString(R.string.prompt_sended_toast_text), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(FindAccountActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new LoginToast(FindAccountActivity.this, getString(R.string.prompt_sended_failed_toast_text), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                    findProgressBar.hideProgressBar();
                }
            });
        }else{
            new LoginToast(FindAccountActivity.this, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
        }
    }
}
