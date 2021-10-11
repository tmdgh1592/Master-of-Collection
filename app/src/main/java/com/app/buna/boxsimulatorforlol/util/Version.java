package com.app.buna.boxsimulatorforlol.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.app.buna.boxsimulatorforlol.R;

public class Version {

    public String getVersionInfo(Context context) {
        String version = context.getString(R.string.app_version);
        PackageInfo packageInfo;

        if (context == null) {
            return version;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Error", "getVersionInfo :" + e.getMessage());
        }
        return version;
    }

}
