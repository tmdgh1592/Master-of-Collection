package com.app.buna.boxsimulatorforlol.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {

    Context context;

    public JsonUtil(Context context){
        this.context = context;
    }

    public String getJsonString(String jsonFileName){
        String json = "";
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

}
