package com.example.accessabilitylearn.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import com.example.accessabilitylearn.Constants;
import com.example.accessabilitylearn.service.utils.AutoRedEnvelope;
import com.example.accessabilitylearn.service.utils.InitFriendList;
import com.example.accessabilitylearn.service.utils.SendMessage;
import com.example.accessabilitylearn.utils.AccessibilityUtil;
import com.example.accessabilitylearn.utils.AppUtil;



public class RedEnvelopeService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        AppUtil.makeToast("初始化成功");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event == null || event.getPackageName() == null){
            return;
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED){
            switch (Constants.CurrentTask){
                case Constants.RedEnvelopeType:
                    AutoRedEnvelope.getInstance(this).startEnvelope(event);
                    break;
                case Constants.AutoSendMsgType:
                    SendMessage.getInstance(this).startSedMessage(event);
                    break;
                case Constants.InitFriendList:
                    InitFriendList.getInstance(this).initList(event);
                    break;
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(Constants.TAG, "onInterrupt");
    }


}
