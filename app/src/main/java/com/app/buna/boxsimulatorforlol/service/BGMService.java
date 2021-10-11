package com.app.buna.boxsimulatorforlol.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.app.buna.boxsimulatorforlol.R;

public class BGMService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;

    public class LocalBinder extends Binder {
        public BGMService getService() {
            return BGMService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        startBgm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public void startBgm(){
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void restartBgm(){
        if(mediaPlayer != null){
            mediaPlayer.start();
            Log.d("", "restartBgm: 1");
        }else{
            mediaPlayer = MediaPlayer.create(this, R.raw.background_bgm);
            Log.d("", "restartBgm: 2");
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.background_bgm);
    }

    public void pauseBgm() {
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
    }

    public void stopBgm(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

}
