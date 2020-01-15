package com.example.accessabilitylearn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.accessabilitylearn.Constants;
import com.example.accessabilitylearn.R;
import com.example.accessabilitylearn.service.utils.SendMessage;
import com.example.accessabilitylearn.service.RedEnvelopeService;
import com.example.accessabilitylearn.utils.AppUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static Context AppContext;
    public static Context WeChatContext;
    public static int Width;
    public static int Height;

    private Switch OpenService;
    private Switch AutoWeChat;
    private Switch AutoSendMsg;
    private LinearLayout SendMsgDetail;
    private TextView VersionView;

    private EditText ToWho;
    private EditText WhatMsg;
    private Button SendMsgBtn;

    private Button InitFriend;
    private Spinner FriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initWeChatVersion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        freshOpenServiceSwitch(RedEnvelopeService.class, OpenService);
        initList();
    }

    private void initWeChatVersion(){
        Context weChat = AppUtil.getContext(this, Constants.WeChatInfo.WECHAT_PACKAGE);
        WeChatContext = weChat;
        if(weChat != null){
            String weChatVersion = AppUtil.getAppVersion(weChat);
            System.out.println(weChatVersion);
            Constants.setArgs(weChatVersion);
        }
    }


    private void init(){
        AppContext = this;
        initScreen();
        initVersion();
        SendMsgDetail = findViewById(R.id.send_msg_detail);
        SendMsgDetail.clearAnimation();
        OpenService = findViewById(R.id.open_accessibility);
        setOpenServiceListener(RedEnvelopeService.class, OpenService);
        AutoWeChat = findViewById(R.id.open_we_chat);
        setSwitchListener(AutoWeChat);
        AutoSendMsg = findViewById(R.id.auto_send_msg);
        setSwitchListener(AutoSendMsg);
        //freshOpenServiceSwitch(RedEnvelopeService.class, OpenService);

        ToWho = findViewById(R.id.send_name);
        WhatMsg = findViewById(R.id.send_msg);
        SendMsgBtn = findViewById(R.id.send_WeChat);
        SendMsgBtn.setOnClickListener(this);

        InitFriend = findViewById(R.id.init_friend_btn);
        InitFriend.setOnClickListener(this);
        FriendList = findViewById(R.id.friend_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_WeChat:
                System.out.println("click");
                SendMessage.NAME = ToWho.getText().toString();
                SendMessage.CONTENT = WhatMsg.getText().toString();
                Constants.CurrentTask = Constants.AutoSendMsgType;
                gotoWeChat();
                break;
            case R.id.init_friend_btn:
                Constants.CurrentTask = Constants.InitFriendList;
                gotoWeChat();
                break;
        }
    }

    private void gotoWeChat(){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(Constants.WeChatInfo.WECHAT_PACKAGE, Constants.WeChatInfo.WECHAT_LAUNCHER_UI);
        startActivity(intent);
    }

    private void initList(){
        if(Constants.FriendList == null) return;
        System.out.println(Constants.FriendList);
        FriendList.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_item, Constants.FriendList));
        FriendList.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(Constants.FriendList.get(position));
        ToWho.setText(Constants.FriendList.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setOpenServiceListener(final Class clazz, Switch s){
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(Constants.TAG, "onCheckedChanged: " + isChecked);
                if(isChecked){
                    if(!isAccessibilitySettingOn(clazz.getCanonicalName(), AppContext)){
                        openAccessibility();
                    }
                }else {
                    if(isAccessibilitySettingOn(clazz.getCanonicalName(), AppContext)){
                        openAccessibility();
                    }
                }
            }
        });
    }

    private void setSwitchListener(final Switch s){
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    freshSwitch(s);
                }else {
                    freshSwitch(s);
                }
            }
        });
    }

    private void initVersion(){
        VersionView = findViewById(R.id.app_version);
        String version = AppUtil.getAppVersion(this);
        VersionView.setText(version);
    }

    private void freshOpenServiceSwitch(Class clazz, Switch s){
        if(isAccessibilitySettingOn(clazz.getCanonicalName(), MainActivity.this)){
            s.setChecked(true);
            switch (s.getId()){
                case R.id.open_accessibility:
                    freshSwitch(AutoWeChat);
                    freshSwitch(AutoSendMsg);
            }
        }else {
            s.setChecked(false);
            switch (s.getId()){
                case R.id.open_accessibility:
                    AutoSendMsg.setChecked(false);
                    AutoWeChat.setChecked(false);
            }
        }
        SendMsgDetail.setVisibility(View.GONE);
    }

    private void freshSwitch(Switch s){
        if(OpenService.isChecked()) {
            if(Constants.CurrentTask == Constants.InitFriendList){
                Constants.CurrentTask = -1;
            }
            switch (s.getId()){
                case R.id.open_we_chat:
                    if(s.isChecked()){
                        if(Constants.IsOver){
                            s.setChecked(false);
                            Constants.IsOver = false;
                            break;
                        }else if(AutoSendMsg.isChecked()){
                            AutoSendMsg.setChecked(false);
                            SendMsgDetail.setVisibility(View.GONE);
                        }
                        Constants.CurrentTask = Constants.RedEnvelopeType;
                    }else {
                        Constants.CurrentTask = -1;
                    }
                    break;
                case R.id.auto_send_msg:
                    if(s.isChecked()){
                        if(Constants.IsOver){
                            s.setChecked(false);
                            Constants.IsOver = false;
                            break;
                        }else if(AutoWeChat.isChecked()){
                            AutoWeChat.setChecked(false);
                        }
                        if(s.isChecked()) {
                            SendMsgDetail.setVisibility(View.VISIBLE);
                        }
                    } else {
                        SendMsgDetail.setVisibility(View.GONE);
                        Constants.CurrentTask = -1;
                    }
                    break;
                default:
                    break;
            }
        }else {
            AppUtil.makeToast("请先打开辅助功能");
            s.setChecked(false);
        }
    }

    private boolean isAccessibilitySettingOn(String accessibilityServiceName, Context context){
        int enable = 0;
        String serviceName = context.getPackageName() + "/" + accessibilityServiceName;
        Log.i(Constants.TAG, "isAccessibilitySettingOn: " + serviceName);
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
                freshOpenServiceSwitch(RedEnvelopeService.class, OpenService);
            }
        };
        DialogInterface.OnCancelListener cancel = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                freshOpenServiceSwitch(RedEnvelopeService.class, OpenService);
            }
        };
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.tips)
                .setOnCancelListener(cancel)
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
