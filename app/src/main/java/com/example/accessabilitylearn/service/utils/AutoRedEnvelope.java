package com.example.accessabilitylearn.service.utils;

import android.accessibilityservice.AccessibilityService;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.accessabilitylearn.Constants;
import com.example.accessabilitylearn.activities.MainActivity;
import com.example.accessabilitylearn.utils.AccessibilityUtil;
import com.example.accessabilitylearn.utils.AppUtil;

import java.util.List;

import static com.example.accessabilitylearn.utils.AppUtil.weakUpScreen;

public class AutoRedEnvelope {
    private static AutoRedEnvelope autoRedEnvelope;
    private static AccessibilityService Service;

    private AutoRedEnvelope(AccessibilityService service){
        Service = service;
    }

    public static AutoRedEnvelope getInstance(AccessibilityService service){
        if(autoRedEnvelope == null){
            synchronized (AutoRedEnvelope.class){
                if(autoRedEnvelope == null){
                    autoRedEnvelope = new AutoRedEnvelope(service);
                }
            }
        }
        return autoRedEnvelope;
    }

    public void startEnvelope(AccessibilityEvent event){
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.i(Constants.TAG, "CurrentPackage: " + event.getPackageName().toString());
                List<CharSequence> texts = event.getText();
                for(CharSequence ch : texts){
                    Log.i(Constants.TAG, "NotificationContent: " + ch.toString());
                    AppUtil.makeToast("Msg: " + ch.toString());
                    if(ch.toString().contains("[微信红包]")){
                        weakUpScreen();
                        AccessibilityUtil.gotoApp(event);
                        Log.i(Constants.TAG, "Get Red Envelope!");
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED :
                String className = event.getClassName().toString();
                if(className.equals(Constants.WeChatInfo.WECHAT_LAUNCHER_UI)){
                    AccessibilityNodeInfo nodeInfo = event.getSource();
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("微信红包");
                    if(list != null && list.size() > 0){
                        //getRealEnvelope(list);
                        AccessibilityUtil.performClick(list.get(list.size() - 1));
                    }

                }else if(className.equals(Constants.WeChatInfo.LUCKY_MONEY_UI)){
                    openRedEnvelope(event.getSource());
                }
                break;
            default:
                break;
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
            AccessibilityUtil.performXYClick(Service, x, y);
        }
        SystemClock.sleep(500);
        AccessibilityUtil.globalGoHome(Service);
    }
}
