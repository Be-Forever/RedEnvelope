package com.example.accessabilitylearn;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.example.accessabilitylearn.Utils.AppUtil;


import java.util.List;

public class ClickService extends AccessibilityService {
    private final String TAG = "Accessibility";

    private static String MainUi = "com.tencent.mm.ui.LauncherUI";

    private static String LuckMoneyUi = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";

    private static String CurrentPackage;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected");
//        Constants.Version = AppUtil.getVersion(Constants.BaseContext, Constants.Package);
//        Log.i(TAG, "WeCharVersion: " + Constants.Version);
//        Constants.setArgs(Constants.Version);
    }


    int i = 0;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event == null || event.getPackageName() == null){
            return;
        }
        Log.i(TAG, "onAccessibilityEvent: " + event.getClassName());
        CurrentPackage = event.getPackageName().toString();
        switch (event.getEventType()){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Log.i(TAG, "CurrentPackage: " + CurrentPackage);
                List<CharSequence> texts = event.getText();
                for(CharSequence ch : texts){
                    Log.i(TAG, "NotificationContent: " + ch.toString());
                    AppUtil.makeToast("Msg: " + ch.toString());
                    if(ch.toString().contains("[微信红包]")){
                        gotoWeChat(event);
                        Log.i(TAG, "Get Red Envelope!");
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
        Log.i(TAG, "onInterrupt");
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
            Log.i(TAG, "node = null");
        }
        SystemClock.sleep(50);
        Log.i(TAG, "openRedEnvelope: fakeClick");
        for(int i = 0; i < 20; i++){
            SystemClock.sleep(17);
            fakeClick();
        }
        SystemClock.sleep(500);
        globalGoHome();
    }

    private void goClick(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null) return;
        while (nodeInfo != null){
            if(nodeInfo.isClickable()){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }


    private void gotoWeChat(AccessibilityEvent event){
        Parcelable data = event.getParcelableData();
        if(data != null && data instanceof Notification){
            Notification notification = (Notification) data;
            PendingIntent intent = notification.contentIntent;
            try {
                intent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    private void globalGoBack(){
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    private void globalGoHome(){
        performGlobalAction(GLOBAL_ACTION_HOME);
    }


    @TargetApi(android.os.Build.VERSION_CODES.N)
    private void fakeClick(){
        float x = MainActivity.Width / 2.0F;
        float y = MainActivity.Height * 2 / 3.0F;
        Log.i(TAG, "x, y = " + x + ", " + y);
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1));
        GestureDescription gestureDescription = builder.build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.i(TAG, "onCompleted: completed");
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.i(TAG, "onCancelled: cancelled");
            }
        }, null);
    }

}
