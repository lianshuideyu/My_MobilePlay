package com.atguigu.my_mobileplay.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/5/24.
 */

public class TestService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
        //这里需要返回一个IBinder的对象，但是IBinder为接口，接口不可创建对象
        //所以这里返回一个IBinder的实现类(new Binder() )也是可以的
    }
}
