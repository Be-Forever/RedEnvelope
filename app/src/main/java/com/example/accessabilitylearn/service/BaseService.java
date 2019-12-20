package com.example.accessabilitylearn.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


public class BaseService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    public void globalGoBack(){
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    public void globalGoHome(){
        performGlobalAction(GLOBAL_ACTION_HOME);
    }


    //输入x, y坐标模拟点击事件
    @TargetApi(android.os.Build.VERSION_CODES.N)
    public void fakeClick(float x, float y){
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1));
        GestureDescription gestureDescription = builder.build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
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
    public void goClick(AccessibilityNodeInfo nodeInfo){
        if(nodeInfo == null) return;
        while (nodeInfo != null){
            if(nodeInfo.isClickable()){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }
}
