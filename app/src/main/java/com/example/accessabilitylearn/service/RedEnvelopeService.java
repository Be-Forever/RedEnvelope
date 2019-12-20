package com.example.accessabilitylearn.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.example.accessabilitylearn.Constants;
import com.example.accessabilitylearn.MainActivity;
import com.example.accessabilitylearn.utils.AppUtil;


import java.util.List;

import static com.example.accessabilitylearn.utils.AppUtil.weakUpScreen;


//WeChat自动抢红包
public class RedEnvelopeService extends BaseService {
    private static String MainUi = "com.tencent.mm.ui.LauncherUI";

    private static String LuckMoneyUi = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";

    private static String CurrentPackage;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(Constants.TAG, "onServiceConnected");
        AppUtil.makeToast("初始化成功");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event == null || event.getPackageName() == null){
            return;
        }
        CurrentPackage = event.getPackageName().toString();
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.i(Constants.TAG, "CurrentPackage: " + CurrentPackage);
                List<CharSequence> texts = event.getText();
                for(CharSequence ch : texts){
                    Log.i(Constants.TAG, "NotificationContent: " + ch.toString());
                    AppUtil.makeToast("Msg: " + ch.toString());
                    if(ch.toString().contains("[微信红包]")){
                        weakUpScreen();
                        gotoApp(event);
                        Log.i(Constants.TAG, "Get Red Envelope!");
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED :
                String className = event.getClassName().toString();
                if(className.equals(MainUi)){
                    AccessibilityNodeInfo nodeInfo = event.getSource();
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("微信红包");
                    if(list != null && list.size() > 0){
                        //getRealEnvelope(list);
                        goClick(list.get(list.size() - 1));
                    }

                }else if(className.equals(LuckMoneyUi)){
                    openRedEnvelope(event.getSource());
                }
                break;
            default:
                //globalGoHome();
                break;
        }

    }

    @Override
    public void onInterrupt() {
        Log.i(Constants.TAG, "onInterrupt");
    }

    private void getRealEnvelope(List<AccessibilityNodeInfo> list){
        int count = list.size();
        AppUtil.makeToast("Red Envelope Count: " + count);
        for(int i = 0; i < count; i++){
            AccessibilityNodeInfo child = list.get(i).getParent().getParent();
            if(null != child){
                boolean flag = true;
                List<AccessibilityNodeInfo> l1 = child.findAccessibilityNodeInfosByText("已被领完");
                List<AccessibilityNodeInfo> l2 = child.findAccessibilityNodeInfosByText("已领取");
                if((l1 != null && l1.size() > 0) || (l2 != null && l2.size() > 0)){
                    flag = false;
                }
                if(flag){
                    goClick(child);
                }
            }
        }
    }

    //开启红包
    private void openRedEnvelope(AccessibilityNodeInfo node){
        if(node == null) {
            Log.i(Constants.TAG, "node = null");
        }
        SystemClock.sleep(50);
        Log.i(Constants.TAG, "openRedEnvelope: fakeClick");
        float x = MainActivity.Width / 2.0F;
        float y = MainActivity.Height * 2 / 3.0F;
        for(int i = 0; i < 10; i++){
            SystemClock.sleep(17);
            fakeClick(x, y);
        }
        SystemClock.sleep(500);
        globalGoHome();
    }
}
