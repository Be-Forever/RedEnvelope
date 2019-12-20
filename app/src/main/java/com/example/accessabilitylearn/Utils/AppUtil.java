package com.example.accessabilitylearn.Utils;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.PowerManager;
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

    @SuppressLint("InvalidWakeLockTag")
    public static void weakUpScreen(){
        PowerManager pm = (PowerManager)MainActivity.AppContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "WakeLock");
        wakeLock.acquire();
        wakeLock.release();

        KeyguardManager km = (KeyguardManager) MainActivity.AppContext.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = km.newKeyguardLock("unlock");
        lock.disableKeyguard();

    }

    public static void makeToast(String text){
        Toast.makeText(MainActivity.AppContext, text, Toast.LENGTH_SHORT).show();
    }
}
