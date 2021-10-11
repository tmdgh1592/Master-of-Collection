package com.app.buna.boxsimulatorforlol.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.buna.boxsimulatorforlol.activity.LoginActivity;
import com.app.buna.boxsimulatorforlol.R;
import com.app.buna.boxsimulatorforlol.util.LangUtil;

import java.util.List;

public class AdapterSpinner extends BaseAdapter {


    Context context;
    List<String> data;
    LayoutInflater inflater;
    LangUtil langUtil;
    LoginActivity activity;
    SharedPreferences setting;
    SharedPreferences.Editor editor;


    public AdapterSpinner(LoginActivity activity, Context context, List<String> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        langUtil = new LangUtil(context);
        this.activity = activity;
        setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = setting.edit();
    }



    @Override
    public int getCount() {
        if(data!=null) return data.size();
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.spinner_spinner1_normal, parent, false);
        }

        if(data!=null){
            //데이터세팅
            String text = context.getString(R.string.language_select_text);
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.spinner_spinner1_dropdown, parent, false);
        }

        //데이터세팅
        final String text = data.get(position);
        ((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);

        if(text.equals(context.getString(R.string.language_kr_text))) {
            ((ImageView) convertView.findViewById(R.id.language_image_view)).setImageResource(R.drawable.korean_lang_icon);
        }else if(text.equals(context.getString(R.string.language_en_text))) {
            ((ImageView) convertView.findViewById(R.id.language_image_view)).setImageResource(R.drawable.english_lang_icon);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == 0){
                    langUtil.changeLang(LangUtil.KR);
                    editor.putInt("lang", LangUtil.KR);
                }else if(position == 1) {
                    langUtil.changeLang(LangUtil.EN);
                    editor.putInt("lang", LangUtil.EN);
                }
                editor.commit();
                activity.finish();
                activity.startActivity(new Intent(activity, LoginActivity.class));
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}