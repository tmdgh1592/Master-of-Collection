package com.app.buna.boxsimulatorforlol.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.app.buna.boxsimulatorforlol.R;

public class SoundManager {



    private Context context;

    private SoundPool mSoundPool;
    private int coinSoundId;

    private boolean isLoaded = false;

    public static final String TYPE_COIN = "TYPE_COIN";
    public static final String TYPE_TAB_OPEN = "TYPE_TAB_OPEN";
    public static final String TYPE_TAB_CLOSE = "TYPE_TAB_CLOSE";
    public static final String TYPE_SHOP_OPEN = "TYPE_SHOP_OPEN";
    public static final String TYPE_SHOP_CLOSE = "TYPE_SHOP_CLOSE";
    public static final String TYPE_BUY_ITEM = "TYPE_BUY_ITEM";
    public static final String TYPE_GET_RESULT = "TYPE_GET_RESULT";

    private int TYPE_ERROR = -1;
    private int MAX_STREAM = 8;


    public SoundManager(Context context){
        this.context = context;
    }

    public void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(MAX_STREAM)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_STREAM, AudioManager.STREAM_MUSIC, 0);
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                isLoaded = true;
            }
        });
    }

    public int loadSound(String SOUND_TYPE){
        switch (SOUND_TYPE) {
            case TYPE_COIN:
                return mSoundPool.load(context, R.raw.hit_sound, 0);
            case TYPE_SHOP_OPEN:
            case TYPE_TAB_OPEN:
                return mSoundPool.load(context, R.raw.tab_open_sound, 0);
            case TYPE_TAB_CLOSE:
                return mSoundPool.load(context, R.raw.tab_close_sound, 0);
            case TYPE_SHOP_CLOSE:
                return mSoundPool.load(context, R.raw.tab_close_sound, 0);
            case TYPE_BUY_ITEM:
                return mSoundPool.load(context, R.raw.item_buy_sound, 0);
            case TYPE_GET_RESULT:
                return mSoundPool.load(context, R.raw.item_buy_sound, 0);
            default:
                return TYPE_ERROR;
        }
    }

    public void play(final int soundId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSoundPool.play(soundId, 0.55f, 0.55f, 0, 0, 1f);
            }
        }).start();

    }

    public boolean isLoaded(){
        return isLoaded;
    }
}
