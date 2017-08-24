package com.millet.myapplication;

import android.app.Application;
import android.content.Intent;

import com.millet.myapplication.service.MyService1;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //启动服务1和2
        initService();
    }

    private void initService() {
        Intent intent1 = new Intent(this, MyService1.class);
        this.startService(intent1);
        Intent intent2 = new Intent(this, MyService1.class);
        this.startService(intent2);
    }

}
