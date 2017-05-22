package com.atguigu.my_mobileplay.app;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by Administrator on 2017/5/22.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //xUtils初始化
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
