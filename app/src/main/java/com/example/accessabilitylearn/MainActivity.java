package com.example.accessabilitylearn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Accessibility";

    public static Context AppContext;
    public static int Width;
    public static int Height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext = this;
        initScreen();
        setContentView(R.layout.activity_main);
        try {
            Constants.BaseContext = this.createPackageContext(Constants.Package, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        openAccessibility(ClickService.class.getCanonicalName(), this);
    }

    private boolean isAccessibilitySettingOn(String accessibilityServiceName, Context context){
        int enable = 0;
        String serviceName = context.getPackageName() + "/" + accessibilityServiceName;
        try {
            enable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(enable == 1){
            TextUtils.SimpleStringSplitter stringSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingVal = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if(settingVal != null){
                stringSplitter.setString(settingVal);
                while(stringSplitter.hasNext()){
                    String accessibilityService = stringSplitter.next();
                    if(accessibilityService.equals(serviceName)){
                        Log.i(TAG, "serviceName: " + serviceName + "isAccessibilitySettingOn->true");
                        return true;
                    }
                }
            }
        }else {
            Log.i(TAG, "serviceName: " + serviceName + "---isAccessibilitySettingOn->false");
        }
        return false;
    }

    private void openAccessibility(String name, Context context){
        if(!isAccessibilitySettingOn(name, context)){
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
            };
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.tips)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", listener)
                    .create();
            dialog.show();
        }
    }

    private void initScreen(){
        WindowManager manager = this.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        Width = metrics.widthPixels;
        Height = metrics.heightPixels;
    }

    //获取正确的屏幕高度
    public static int getRealHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenHeight = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics();
            display.getRealMetrics(dm);
            screenHeight = dm.heightPixels;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
                DisplayMetrics dm = new DisplayMetrics();
                display.getMetrics(dm);
                screenHeight = dm.heightPixels;
            }
        }
        return screenHeight;
    }
}
