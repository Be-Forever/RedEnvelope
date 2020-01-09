package com.example.accessabilitylearn.service.utils;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.accessabilitylearn.Constants;
import com.example.accessabilitylearn.activities.MainActivity;
import com.example.accessabilitylearn.utils.AccessibilityUtil;
import com.example.accessabilitylearn.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class SendMessage {
    public static String NAME;
    public static String CONTENT;
    private static AccessibilityEvent CurrentEvent;
    private static AccessibilityService CurrentService;
    private static SendMessage mInstance;

    private SendMessage(){
    }

    public static SendMessage getInstance(){
        if(mInstance == null){
            synchronized (SendMessage.class){
                if(mInstance == null){
                    mInstance = new SendMessage();
                }
            }
        }
        return mInstance;
    }

    public void startSedMessage(AccessibilityService service, AccessibilityEvent event){
        CurrentService = service;
        CurrentEvent = event;
        String currentActivity = event.getClassName().toString();
        Log.i(Constants.TAG, "startSedMessage: " + currentActivity);
        switch (currentActivity){
            case Constants.WeChatInfo.WECHAT_LAUNCHER_UI:
                headToContactUI();
                break;
            case "android.widget.LinearLayout":
            case Constants.WeChatInfo.TALKING_UI:
                sendMsg();
                break;
            case Constants.WeChatInfo.WECHAT_CLASS_CONTACT_INFO_UI:
                headToChattingUi();
                break;
            default:
                AppUtil.makeToast("请回到主界面");
        }
    }

    private void headToContactUI(){
        if(!AccessibilityUtil.findTextAndClick(CurrentService,"通讯录")){
            String talker = AccessibilityUtil.findTextById(CurrentService, Constants.WeChatInfo.ID_CHATTING_NAME);
            if(talker.equals(NAME)){
                sendMsg();
            }else {
                AccessibilityUtil.globalGoBack(CurrentService);
                freshContactUI();
            }
        }else {
            freshContactUI();
        }
    }

    private void freshContactUI(){
        AccessibilityUtil.findTextAndClick(CurrentService,"通讯录");
        SystemClock.sleep(500);
        AccessibilityUtil.findTextAndClick(CurrentService,"通讯录");
        AccessibilityNodeInfo node = findTalker();
        if(node != null){
            AccessibilityUtil.performClick(node);
        }
    }

    private void headToChattingUi(){
        AccessibilityUtil.findTextAndClick(CurrentService, "发消息");
    }

    private AccessibilityNodeInfo findTalker(){
        AccessibilityNodeInfo nodeInfo = CurrentService.getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(Constants.WeChatInfo.ID_CONTACT_LIST);
        if(list != null && !list.isEmpty()){
            List<String> userList = new ArrayList<>();
            while (true){
                List<AccessibilityNodeInfo> users = nodeInfo.findAccessibilityNodeInfosByViewId(Constants.WeChatInfo.ID_CONTACTOR_TEXT);
                if(users != null && !users.isEmpty()){
                    System.out.println(users.size());
                    for(int i = 0; i < users.size(); i++){
                        AccessibilityNodeInfo node = users.get(i);
                        String name = node.getText().toString();
                        if(name.equals(NAME)) return node;
                        if(!userList.contains(name)){
                            userList.add(name);
                        }else{
                            if(i == users.size() - 1){
                                AppUtil.makeToast("未找到联系人");
                                return null;
                            }
                        }
                    }
                    list.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    SystemClock.sleep(500);
                }
            }

        }
        return null;
    }

    private void sendMsg(){
        String talker = AccessibilityUtil.findTextById(CurrentService, Constants.WeChatInfo.ID_CHATTING_NAME);
        if(talker != null && talker.equals(NAME)){
            if(!AccessibilityUtil.findIdAndWrite(CurrentService, Constants.WeChatInfo.ID_EDIT_VIEW, CONTENT)){
                AccessibilityUtil.findAndClickById(CurrentService, Constants.WeChatInfo.ID_VOICE_BTN);
                AccessibilityUtil.findIdAndWrite(CurrentService, Constants.WeChatInfo.ID_EDIT_VIEW, CONTENT);
            }
            AccessibilityUtil.findAndClickById(CurrentService, Constants.WeChatInfo.ID_SEND_BTN);
            resetApp();
            Constants.CurrentTask = -1;
            Constants.IsOver = true;
        }else {
            AccessibilityUtil.globalGoBack(CurrentService);
            freshContactUI();
        }
    }

    public void resetApp(){
        ActivityManager manager = (ActivityManager) CurrentService.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(3);
        System.out.println(taskInfos.size());
        for(ActivityManager.RunningTaskInfo info : taskInfos){
            if(CurrentService.getPackageName().equals(info.topActivity.getPackageName())){
                manager.moveTaskToFront(info.id, ActivityManager.MOVE_TASK_WITH_HOME);
            }
        }
    }

}
