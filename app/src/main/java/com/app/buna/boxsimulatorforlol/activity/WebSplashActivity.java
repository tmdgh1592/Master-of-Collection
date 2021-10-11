package com.app.buna.boxsimulatorforlol.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.ImageDownloader;
import com.app.buna.boxsimulatorforlol.util.LangUtil;
import com.app.buna.boxsimulatorforlol.util.Network;

public class WebSplashActivity extends AppCompatActivity {

    private Intent receiveIntent;
    private String imgUrl;
    private WebView webView;
    private Context context;
    private LinearLayout imgDownBtn;
    private final int REQ_EXTERNAL_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LangUtil.setLang(this);
        setContentView(R.layout.activity_web_splash);

        context = WebSplashActivity.this;

        imgDownBtn = findViewById(R.id.img_download_button);
        ((TextView)findViewById(R.id.download_text)).setText(getString(R.string.wallpaper_download));

        webView = findViewById(R.id.splash_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        receiveIntent = getIntent();
        imgUrl = receiveIntent.getStringExtra("splashImgUrl");

        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(imgUrl);

        imgDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Network.state(WebSplashActivity.this)){
                    if(requestPermission()){
                        ImageDownloader downloader = new ImageDownloader(WebSplashActivity.this);
                        downloader.execute(imgUrl);
                        finish();
                    }
                }else{
                    Toast.makeText(context, getString(R.string.nick_condition_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean requestPermission(){
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //권한 없는 경우, 요청한다.
        if(permission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_EXTERNAL_PERMISSION);
            return false;
        }else{  //권한 있는 경우
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQ_EXTERNAL_PERMISSION:
                // 권한을 허용한 경우
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ImageDownloader downloader = new ImageDownloader(WebSplashActivity.this);
                    downloader.execute(imgUrl);
                }else{
                    Toast.makeText(context, getString(R.string.permission_denined_text), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
