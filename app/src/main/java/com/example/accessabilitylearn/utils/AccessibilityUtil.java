package com.example.accessabilitylearn.utils;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Path;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.accessabilitylearn.activities.MainActivity;

import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;
import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME;

public class AccessibilityUtil {


    //点击文字所属按钮事件
    public static boolean findTextAndClick(AccessibilityService service, String text){
        AccessibilityNodeInfo nodeInfo = service.getRootInActiveWindow();
        if(nodeInfo == null) return false;
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(nodeInfoList != null && !nodeInfoList.isEmpty()){
            for(AccessibilityNodeInfo info : nodeInfoList){
                if(info != null && (text.equals(info.getText())) || text.equals(info.getContentDescription())){
                    performClick(info);
                    return true;
                }
            }
        }
        return false;
    }

    //点击id所属按钮文字
    public static void findAndClickById(AccessibilityService service, String id){
        AccessibilityNodeInfo nodeInfo = service.getRootInActiveWindow();
        if(nodeInfo == null) return;
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if(nodeInfoList != null && nodeInfoList.size() > 0){
            performClick(nodeInfoList.get(0));
        }
    }

    //获取id所属按钮文字
    public static String findTextById(AccessibilityService service, String id){
        AccessibilityNodeInfo nodeInfo = service.getRootInActiveWindow();
        if(nodeInfo == null) return null;
        List<AccessibilityNodeInfo> nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if(nodeInfoList != null && nodeInfoList.size() > 0){
            return nodeInfoList.get(0).getText().toString();
        }
        return null;
    }

    //编辑EditView
    public static boolean findIdAndWrite(AccessibilityService service, String id, String text){
        AccessibilityNodeInfo nodeInfo = service.getRootInActiveWindow();
        List<AccessibilityNodeInfo> edit = nodeInfo.findAccessibilityNodeInfosByViewId(id);
        if(edit != null && !edit.isEmpty()){
            AccessibilityNodeInfo node = edit.get(0);
            ClipboardManager clipboardManager = (ClipboardManager) MainActivity.AppContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", text);
            clipboardManager.setPrimaryClip(clipData);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            return true;
        }
        return false;
    }

    //输入x, y坐标模拟点击事件
    @TargetApi(android.os.Build.VERSION_CODES.N)
    public static void performXYClick(AccessibilityService service, float x, float y){
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1));
        GestureDescription gestureDescription = builder.build();
        service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                //Log.i(Constants.TAG, "onCompleted: completed");
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                //Log.i(Constants.TAG, "onCancelled: cancelled");
            }
        }, null);
    }

    //对某个节点进行点击
    public static void performClick(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null) return;
        while (nodeInfo != null){
            if(nodeInfo.isClickable()){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }


    //通知栏事件进入应用
    public static void gotoApp(AccessibilityEvent event){
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

    public static void globalGoBack(AccessibilityService service){
        service.performGlobalAction(GLOBAL_ACTION_BACK);
    }

    public static void globalGoHome(AccessibilityService service){
        service.performGlobalAction(GLOBAL_ACTION_HOME);
    }
}
