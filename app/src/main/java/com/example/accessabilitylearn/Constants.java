package com.example.accessabilitylearn;


import com.example.accessabilitylearn.utils.AppUtil;

import java.util.List;

public class Constants {
    public static String Version = "";
    public static boolean IsOver = false;
    public static int CurrentTask = -1;
    public static final int RedEnvelopeType = 1000;
    public static final int AutoSendMsgType = 1001;
    public static final int InitFriendList = 1002;
    public static List<String> FriendList;
    public static final String TAG = "Accessibility";

    public static class WeChatInfo{
        public static final String WECHAT_PACKAGE = "com.tencent.mm";
        //首页
        public static final String WECHAT_LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI";
        //红包界面
        public static final String LUCKY_MONEY_UI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";
        //聊天界面
        public static final String TALKING_UI = "com.tencent.mm.ui.chatting.ChattingUI";
        //微信联系人页面
        public static final String WECHAT_CLASS_CONTACT_INFO_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";

        /**
         * 聊天界面
         */
        //聊天框左上角名字id
        public static String ID_CHATTING_NAME;
        //聊天语音切换按钮id
        public static String ID_VOICE_BTN;
        //聊天界面输入框id
        public static String ID_EDIT_VIEW;
        //发送按钮
        public static String ID_SEND_BTN;

        /**
         * 联系人界面
         */
        //通讯录listViewId
        public static String ID_CONTACT_LIST;
        //联系人textViewID
        public static String ID_CONTACTOR_TEXT;


    }

    public static void setArgs(String version){
        switch (version){
            case "7.0.6":
                WeChatInfo.ID_CHATTING_NAME = "com.tencent.mm:id/l5";
                WeChatInfo.ID_VOICE_BTN = "com.tencent.mm:id/aok";
                WeChatInfo.ID_EDIT_VIEW = "com.tencent.mm:id/aom";
                WeChatInfo.ID_CONTACT_LIST = "com.tencent.mm:id/nn";
                WeChatInfo.ID_CONTACTOR_TEXT = "com.tencent.mm:id/ol";
                WeChatInfo.ID_SEND_BTN = "com.tencent.mm:id/aot";
                break;
            case "7.0.10":
                WeChatInfo.ID_CHATTING_NAME = "com.tencent.mm:id/lt";
                WeChatInfo.ID_VOICE_BTN = "com.tencent.mm:id/aqc";
                WeChatInfo.ID_EDIT_VIEW = "com.tencent.mm:id/aqe";
                WeChatInfo.ID_CONTACT_LIST = "com.tencent.mm:id/oc";
                WeChatInfo.ID_CONTACTOR_TEXT = "com.tencent.mm:id/pa";
                WeChatInfo.ID_SEND_BTN = "com.tencent.mm:id/aql";
                break;
            default:
                AppUtil.makeToast( "初始化版本失败");
        }
    }

    public static void init(String version){
        switch (version){
            case "7.0.10":

        }
    }
}
