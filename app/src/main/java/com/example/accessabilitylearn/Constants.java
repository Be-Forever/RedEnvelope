package com.example.accessabilitylearn;

import android.content.Context;
import android.widget.Toast;

import com.example.accessabilitylearn.Utils.AppUtil;

public class Constants {
    public static String Version = "";
    public static String Package = "com.tencent.mm";
    public static Context BaseContext;

    public static void setArgs(String version){
        switch (version){
//            case "7.0.6":
//                break;

            default:
                AppUtil.makeToast( "初始化失败");
        }
    }
}
