package com.example.accessabilitylearn.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.example.accessabilitylearn.MainActivity;

public class AppUtil {


    public static String getVersion(Context context, String packageName){
        try {
           return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void makeToast(String text){
        Toast.makeText(MainActivity.AppContext, text, Toast.LENGTH_SHORT).show();
    }
}
