package com.app.buna.boxsimulatorforlol.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LangUtil {

    Context context;
    public static final int KR = 0;
    public static final int EN = 1;
    private static SharedPreferences setting;

    public LangUtil(Context context){
        this.context = context;
    }

    public static void setLang(Context context){
        setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        if(setting.getInt("lang", LangUtil.KR) == LangUtil.KR){
            new LangUtil(context).changeLang(LangUtil.KR);
        }else if(setting.getInt("lang", LangUtil.EN) == LangUtil.EN){
            new LangUtil(context).changeLang(LangUtil.EN);
        }
    }

    public static int getLang(Context context){
        Configuration config = context.getResources().getConfiguration();
        Locale locale = config.locale;

        if(locale.toString().equals(Locale.KOREA.toString())){ //ko_KR
            return KR;
        }else if(locale.toString().equals(Locale.US.toString())){ //en_US
            return EN;
        }
        return -1;
    }

    public void changeLang(int langType){
        Locale locale;
        Configuration config;

        switch (langType){
            case KR:
                locale = Locale.KOREA;
                Locale.setDefault(locale);
                config = context.getResources().getConfiguration();
                config.setLocale(locale);
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
                break;
            case EN:
                locale = Locale.US;
                Locale.setDefault(locale);
                config = context.getResources().getConfiguration();
                config.setLocale(locale);
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
                break;
            default:
                break;
        }
    }

}
