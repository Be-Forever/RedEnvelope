package com.example.accessabilitylearn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.accessabilitylearn.service.RedEnvelopeService;

public class MainActivity extends AppCompatActivity {
    public static Context AppContext;
    public static int Width;
    public static int Height;
    private Switch AutoWeChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext = this;
        initScreen();
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        freshSwitch();-
    }


    private void init(){
        AutoWeChat = findViewById(R.id.open_we_chat);
        freshSwitch();
        AutoWeChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(Constants.TAG, "onCheckedChanged: " + isChecked);
                if(isChecked){
                    if(!isAccessibilitySettingOn(RedEnvelopeService.class.getCanonicalName(), AppContext)){
                        openAccessibility();
                    }
                }else {
                    if(isAccessibilitySettingOn(RedEnvelopeService.class.getCanonicalName(), AppContext)){
                        openAccessibility();
                    }
                }
            }
        });
    }

    private void freshSwitch(){
        if(isAccessibilitySettingOn(RedEnvelopeService.class.getCanonicalName(), MainActivity.this)){
            AutoWeChat.setChecked(true);
        }else {
            AutoWeChat.setChecked(false);
        }
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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void openAccessibility(){
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                freshSwitch();
            }
        };
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.tips)
                .setNegativeButton("取消", cancelListener)
                .setPositiveButton("确定", clickListener)
                .create();
        dialog.show();
    }

    private void initScreen(){
        WindowManager manager = this.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        Width = metrics.widthPixels;
        Height = metrics.heightPixels;
    }
}
