package com.atguigu.my_mobileplay.app;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by Administrator on 2017/5/22.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //xUtils初始化
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在“ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5928e7f4");
    }
}
