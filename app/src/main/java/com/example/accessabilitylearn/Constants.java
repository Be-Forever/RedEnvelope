package com.example.accessabilitylearn;


import com.example.accessabilitylearn.utils.AppUtil;

public class Constants {
    public static String Version = "";
    public static String Package = "com.tencent.mm";
    public static String TAG = "Accessibility";

    public static void setArgs(String version){
        switch (version){
//            case "7.0.6":
//                break;

            default:
                AppUtil.makeToast( "初始化失败");
        }
    }
}
