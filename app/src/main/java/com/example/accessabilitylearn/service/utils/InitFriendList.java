package com.example.accessabilitylearn.service.utils;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.accessabilitylearn.Constants;
import com.example.accessabilitylearn.utils.AccessibilityUtil;
import com.example.accessabilitylearn.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class InitFriendList {

    private static AccessibilityService CurrentService;
    private static InitFriendList mInstance;

    private InitFriendList(AccessibilityService service){
        CurrentService = service;
    }

    public static InitFriendList getInstance(AccessibilityService service){
        if(mInstance == null){
            synchronized (InitFriendList.class){
                if(mInstance == null){
                    mInstance = new InitFriendList(service);
                }
            }
        }
        return mInstance;
    }

    public void initList(AccessibilityEvent event){
        String currentActivity = event.getClassName().toString();
        Log.i(Constants.TAG, "initList: " + currentActivity);
        switch (currentActivity){
            case Constants.WeChatInfo.WECHAT_LAUNCHER_UI:
                headToContactUI();
                break;
            default:
                AppUtil.makeToast("请回到主界面");
        }
    }

    private void headToContactUI(){
        if(!AccessibilityUtil.findTextAndClick(CurrentService,"通讯录")) {
            AccessibilityUtil.globalGoBack(CurrentService);
        }
        freshContactUI();
        getList();
        resetApp();
    }

    private void freshContactUI(){
        AccessibilityUtil.findTextAndClick(CurrentService,"通讯录");
        SystemClock.sleep(500);
        AccessibilityUtil.findTextAndClick(CurrentService,"通讯录");
    }

    private void getList(){
        AccessibilityNodeInfo nodeInfo = CurrentService.getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(Constants.WeChatInfo.ID_CONTACT_LIST);
        if(list != null && !list.isEmpty()){
            List<String> userList = new ArrayList<>();
            while (true){
                List<AccessibilityNodeInfo> users = nodeInfo.findAccessibilityNodeInfosByViewId(Constants.WeChatInfo.ID_CONTACTOR_TEXT);
                if(users != null && !users.isEmpty()){
                    for(int i = 0; i < users.size(); i++){
                        AccessibilityNodeInfo node = users.get(i);
                        String name = node.getText().toString();
                        if(!userList.contains(name)){
                            userList.add(name);
                        }else{
                            if(i == users.size() - 1){
                                AppUtil.makeToast("扫描完毕");
                                Constants.FriendList = userList;
                                return;
                            }
                        }
                    }
                    list.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    SystemClock.sleep(500);
                }
            }

        }
    }


    public void resetApp(){
        ActivityManager manager = (ActivityManager) CurrentService.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(3);
        for(ActivityManager.RunningTaskInfo info : taskInfos){
            if(CurrentService.getPackageName().equals(info.topActivity.getPackageName())){
                manager.moveTaskToFront(info.id, ActivityManager.MOVE_TASK_WITH_HOME);
            }
        }
    }

}
