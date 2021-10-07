package com.app.buna.boxsimulatorforlol.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.Adapter.AdapterSpinner;
import com.app.buna.boxsimulatorforlol.DB.DBHelper;
import com.app.buna.boxsimulatorforlol.DTO.CollectData;
import com.app.buna.boxsimulatorforlol.DTO.ItemFragment;
import com.app.buna.boxsimulatorforlol.DTO.TransferData;
import com.app.buna.boxsimulatorforlol.DTO.UserAccount;
import com.app.buna.boxsimulatorforlol.Manager.GoldManager;
import com.app.buna.boxsimulatorforlol.Manager.ItemManager;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.Util.GameToast;
import com.app.buna.boxsimulatorforlol.Util.LangUtil;
import com.app.buna.boxsimulatorforlol.Util.LoginTextWatcher;
import com.app.buna.boxsimulatorforlol.Util.LoginToast;
import com.app.buna.boxsimulatorforlol.Util.Network;
import com.app.buna.boxsimulatorforlol.Util.Version;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP10ProgressBar;
import ir.alirezabdn.wp7progress.WP7ProgressBar;

public class LoginActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, TextView.OnEditorActionListener {

    private TextView registerTextView, findAccountTextView, versionTextView;
    private TextInputEditText idEditText, pwEditText;
    private CheckBox loginContCheckBox;
    private LinearLayout loginButton;
    private FirebaseAuth mAuth;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("account").child("userinfo");

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    private WP10ProgressBar progressBar;
    private Intent loginIntent;

    private ItemManager itemManager;
    private GoldManager goldManager;

    Spinner spinner;

    //Adapter
    AdapterSpinner adapterSpinner;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_login);

        itemManager = new ItemManager(this);
        goldManager = new GoldManager(this);
        mAuth = FirebaseAuth.getInstance();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        settingView();
        setGoogleLogin();
        setVersion();
        autoLogin();
        setLangAdapter();

        /*firebase google login*/
        /*로그인 되어있을 경우엔 바로 다음화면으로 이동*/
        firebaseAutoLogin();
    }



    private void setLangAdapter() {
        //데이터
        List<String> data = new ArrayList<>();
        data.add(getString(R.string.language_kr_text));
        data.add(getString(R.string.language_en_text));

        //UI생성
        spinner = (Spinner)findViewById(R.id.lang_spinner);

        //Adapter
        adapterSpinner = new AdapterSpinner(this, this, data);

        //Adapter 적용
        spinner.setAdapter(adapterSpinner);
    }

    private void settingView(){
        signInButton = findViewById(R.id.signInButton);
        registerTextView = findViewById(R.id.register_text_view);
        findAccountTextView = findViewById(R.id.find_id_text_view);
        versionTextView = findViewById(R.id.app_version_text);
        idEditText = findViewById(R.id.id_edit_text);
        pwEditText = findViewById(R.id.password_edit_text);
        loginContCheckBox = findViewById(R.id.login_continue_check_box);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.login_progress_bar);

        registerTextView.setOnTouchListener(this);
        registerTextView.setOnClickListener(this);
        findAccountTextView.setOnTouchListener(this);
        findAccountTextView.setOnClickListener(this);
        loginContCheckBox.setOnTouchListener(this);
        loginButton.setEnabled(false);
        pwEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pwEditText.setOnEditorActionListener(this);
        idEditText.addTextChangedListener(new LoginTextWatcher(this, idEditText, pwEditText, loginButton));
        pwEditText.addTextChangedListener(new LoginTextWatcher(this, idEditText, pwEditText, loginButton));
        loginButton.setOnClickListener(this);
    }

    private void setGoogleLogin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void setVersion(){
        String version = new Version().getVersionInfo(this);
        versionTextView.setText("v"+version);
    }

    private void autoLogin(){
        if(setting.getBoolean("isLoginStateContinue", false)){
            loginContCheckBox.setChecked(true);
            loginIntent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void firebaseAutoLogin(){
        if (setting.getBoolean("isLoginStateContinue", false) && Network.state(this) && mAuth.getCurrentUser() != null) {
            Log.d("ddd", "ddddd");
            loginContCheckBox.setChecked(true);
            loginIntent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void signIn() {
        if(Network.state(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else{
            new LoginToast(this, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginWithFirebase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                        }

                        // ...
                    }
                });
    }

    private void loginWithFirebase(FirebaseUser user){
        final String savedEmail = setting.getString("email", "tempEmail");
        final String myEmail = user.getEmail();
        final String nickname = user.getDisplayName();
        //final String AID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String emailNotContainDot = myEmail.replace("\u002E", "");
        //accountRef.child("userinfo").child(emailNotContainDot).setValue(userAccount);

        FirebaseDatabase.getInstance().getReference().child("account").child("nickname").child(emailNotContainDot).setValue(nickname);
        editor.putBoolean("isLoginStateContinue", true);
        editor.putString("nickname", nickname);
        editor.commit();

                            /*1. 처음 로그인 하는 경우 => initData();
                              2. 다른 아이디로 로그인 하는 경우 => initData()후에, 서버에서 데이터를 가져옴
                              */
        final String emailNoDot = myEmail.replace(".", "");

        if(savedEmail.equals("tempEmail") || !myEmail.equals(savedEmail)) {
            initData();
            dbRef.child(emailNoDot).addListenerForSingleValueEvent(new ValueEventListener() {

                /*입력한 email에 대한 저장정보가 서버에 있는 경우*/
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TransferData data = dataSnapshot.getValue(TransferData.class);
                    try {
                        if(data != null) {
                            setData(data);
                        }
                        // 저장된 데이터가 없는 경우 skip함
                    }catch (NullPointerException e){
                        initData();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                }
            });
        }

        loginIntent = new Intent(LoginActivity.this, SplashActivity.class);
        editor.putBoolean("isLoginStateContinue", loginContCheckBox.isChecked());
        editor.putString("email", myEmail);
        editor.commit();

        finish();
        startActivity(loginIntent);
        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        progressBar.hideProgressBar();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("", "Google sign in failed", e);
                // ...
            }
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.register_text_view:
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    registerTextView.setTextColor(getResources().getColor(R.color.black));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    registerTextView.setTextColor(getResources().getColor(R.color.login_activity_text_color));
                }
                break;
            case R.id.find_id_text_view:
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    findAccountTextView.setTextColor(getResources().getColor(R.color.black));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    findAccountTextView.setTextColor(getResources().getColor(R.color.login_activity_text_color));
                }
                break;
            case R.id.login_continue_check_box:
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    loginContCheckBox.setTextColor(getResources().getColor(R.color.black));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    loginContCheckBox.setTextColor(getResources().getColor(R.color.login_activity_text_color));
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                login(idEditText.getText().toString(), pwEditText.getText().toString());
                break;
            case R.id.register_text_view:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
                break;
            case R.id.find_id_text_view:
                Intent findIntent = new Intent(this, FindAccountActivity.class);
                startActivity(findIntent);
                finish();
                break;
        }
    }

    private void login(@NotNull final String email, String password){
        if(email.isEmpty() || password.isEmpty()){
            new LoginToast(this, getString(R.string.login_condition_text1), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            return;
        }else if(!Network.state(LoginActivity.this)) {
            new LoginToast(this, getString(R.string.nick_condition_error), Gravity.BOTTOM, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginActivity", "signInWithEmail:success");
                            final String savedEmail = setting.getString("email", "tempEmail");
                            //final String AID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            setNickname(idEditText.getText().toString());

                            /*1. 처음 로그인 하는 경우 => initData();
                              2. 다른 아이디로 로그인 하는 경우 => initData()후에, 서버에서 데이터를 가져옴
                              */
                            final String emailNoDot = email.replace(".", "");

                            if(savedEmail.equals("tempEmail") || !email.equals(savedEmail)) {
                                initData();
                                dbRef.child(emailNoDot).addListenerForSingleValueEvent(new ValueEventListener() {

                                    /*입력한 email에 대한 저장정보가 서버에 있는 경우*/
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        TransferData data = dataSnapshot.getValue(TransferData.class);
                                        try {
                                            if(data != null) {
                                                setData(data);
                                            }
                                            // 저장된 데이터가 없는 경우 skip함
                                        }catch (NullPointerException e){
                                            initData();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(LoginActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            loginIntent = new Intent(LoginActivity.this, SplashActivity.class);
                            editor.putBoolean("isLoginStateContinue", loginContCheckBox.isChecked());
                            editor.putString("email", email);
                            editor.commit();

                            finish();
                            startActivity(loginIntent);
                            overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
                            progressBar.hideProgressBar();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                            new LoginToast(LoginActivity.this, getString(R.string.login_condition_text1), Gravity.BOTTOM, Toast.LENGTH_LONG).show();
                            progressBar.hideProgressBar();
                        }

                    }
                });
    }

    /*firebase에서 nickname찾아서 저장*/
    private void setNickname(@NotNull String email){

        DatabaseReference emailRef = FirebaseDatabase.getInstance().getReference().child("account").child("nickname").child(email.replace("\u002E", ""));
        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    editor.putString("nickname", dataSnapshot.getValue().toString());
                    editor.commit();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                
            }
        });

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            switch (textView.getId()){
                case R.id.password_edit_text:
                    login(idEditText.getText().toString(), pwEditText.getText().toString());
                    break;
            }

        }
        return false;
    }

    private void setData(@NotNull TransferData data) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        goldManager.setGold(data.getGold());
        goldManager.setGoldPerClick(data.getGoldPerClick());
        goldManager.setGoldPerSec(data.getGoldPerSec());
        itemManager.setBlueGemCount(data.getBlueGem());
        itemManager.setYellowGemCount(data.getYellowGem());
        itemManager.setBoxCount(data.getBox());
        itemManager.setKeyCount(data.getKey());
        itemManager.setRewardItemCount(data.getRewardItem());
        editor.putInt("boughtBoxCount", data.getBoxBoughtCount());
        editor.putInt("boughtKeyCount", data.getKeyBoughtCount());

        /*불러올 데이터를 추가하기 전에 현재 데이터 모두 삭제*/
        dbHelper.deleteAllData(db);

        Map<String, ItemFragment> fragMap = data.getChampFragData();
        int i=0;

        /*get champ fragment*/
        if(fragMap != null) {
            while (i < fragMap.size()) {
                ItemFragment item = fragMap.get(i + "_key");
                db.execSQL("INSERT INTO champ_frag_table (champName, engChampName, buyBlueGem, sellBlueGem, count) VALUES(?,?,?,?,?)",
                        new String[]{item.getChampName(), item.getChampNameAndNum(), String.valueOf(item.getBuyBlueGem()), String.valueOf(item.getSellBlueGem()), String.valueOf(item.getItemCount())});
                i++;
            }
        }

        /*get skin fragment*/

        fragMap = data.getSkinFragData();
        if(fragMap != null) {
            i=0;
            while (i < fragMap.size()) {
                ItemFragment item = fragMap.get(i + "_key");
                db.execSQL("INSERT INTO skin_frag_table (champNameAndNum, skinName, buyYellowGem, sellYellowGem, count) VALUES(?,?,?,?,?)",
                        new String[]{item.getChampNameAndNum(), item.getSkinName(), String.valueOf(item.getBuyYellowGem()), String.valueOf(item.getSellYellowGem()), String.valueOf(item.getItemCount())});
                i++;
            }
        }

        /*get champ collection*/
        Map<String, CollectData> dataMap = data.getChampData();
        if(dataMap != null) {
            i=0;
            while (i < dataMap.size()) {
                CollectData item = dataMap.get(i + "_key");
                db.execSQL("INSERT INTO champ_table (champName, imgFileName, imgUrl) VALUES(?,?,?)",
                        new String[]{item.getName(), item.getImgFileName(), item.getUrl()});
                i++;
            }
        }

        /*get skin collection*/
        dataMap = data.getSkinData();
        if(dataMap != null) {
            i = 0;
            while (i < dataMap.size()) {
                CollectData item = dataMap.get(i + "_key");
                db.execSQL("INSERT INTO skin_table (skinName, imgFileName, skinUrl) VALUES(?,?,?)",
                        new String[]{item.getName(), item.getImgFileName(), item.getUrl()});
                i++;
            }
        }

        /* set skill level */
        Map<String, Integer> levelMap = data.getSkillLevel();
        String[] itemName = getResources().getStringArray(R.array.pref_upgrade_item_name);
        int[] upgradeInitPrice = getResources().getIntArray(R.array.upgrade_init_price);
        TypedArray howMuchUpgradeEffect = getResources().obtainTypedArray(R.array.how_much_upgrade_effect);
        TypedArray howMuchUpgradePrice = getResources().obtainTypedArray(R.array.how_much_upgrade_price);

        i=0;
        while(i < itemName.length) {
            int level = levelMap.get(itemName[i]+"_key");
            int upgradePrice = upgradeInitPrice[i];

            /*upgrade price 구하기*/
            for(int j=0; j<level; j++){
                upgradePrice *= howMuchUpgradePrice.getFloat(i, 0);
                upgradePrice += upgradeInitPrice[i];
            }

            upgradePrice -= upgradeInitPrice[i];

            editor.putInt(itemName[i]+"NowSkillLevel", level);
            editor.putFloat(itemName[i]+"SkillEffect", (level * howMuchUpgradeEffect.getFloat(i, 0)));
            editor.putInt(itemName[i]+"UpgradePrice", upgradePrice);
            i++;
        }

        itemManager.setBlueGemChance(data.getBluegemChance());
        itemManager.setYellowGemChance(data.getYellowgemChance());

        editor.commit();
        db.close();
    }


    private void initData() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        goldManager.setGoldPerClick(1);
        goldManager.setGoldPerSec(0);
        goldManager.setGold(0);
        itemManager.setBlueGemCount(0);
        itemManager.setYellowGemCount(0);
        itemManager.setBlueGemChance(0);
        itemManager.setYellowGemChance(0);
        itemManager.setBoxCount(0);
        itemManager.setKeyCount(0);
        itemManager.setRewardItemCount(0);

        editor.putInt("boughtBoxCount", 0);
        editor.putInt("boughtKeyCount", 0);
        editor.putString("myTier", "Unranked");

        int i=0;
        String[] itemName = getResources().getStringArray(R.array.pref_upgrade_item_name);

        while(i < itemName.length) {
            editor.putInt(itemName[i]+"NowSkillLevel", 0);
            editor.putFloat(itemName[i]+"SkillEffect", 0);
            editor.putInt(itemName[i]+"UpgradePrice", 0);
            i++;
        }

        dbHelper.deleteAllData(db);

        editor.commit();
        db.close();
    }

}
